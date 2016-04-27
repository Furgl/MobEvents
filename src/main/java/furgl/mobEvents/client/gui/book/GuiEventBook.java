package furgl.mobEvents.client.gui.book;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import furgl.mobEvents.client.gui.book.buttons.GuiButtonIntroPage;
import furgl.mobEvents.client.gui.book.buttons.GuiButtonMobPage;
import furgl.mobEvents.client.gui.book.buttons.GuiButtonNextPage;
import furgl.mobEvents.client.gui.book.buttons.GuiButtonStartEvent;
import furgl.mobEvents.client.gui.book.buttons.GuiButtonSummonMob;
import furgl.mobEvents.client.gui.book.buttons.GuiButtonTab;
import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.Events.Event;
import furgl.mobEvents.common.config.Config;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityCloneZombie;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityMinionZombie;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntitySummonerZombie;
import furgl.mobEvents.common.entity.ZombieApocalypse.IEventMob;
import furgl.mobEvents.packets.PacketSummonMob;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class GuiEventBook extends GuiScreen
{
	public final ResourceLocation bookPageTexture = new ResourceLocation(MobEvents.MODID+":textures/gui/event_book.png");
	protected final int bookImageWidth = 292/2;
	protected final int bookImageHeight = 180/2;
	private final int entityImageWidth = 768;
	private final int entityImageHeight = 768;
	public ArrayList<String> introPages = new ArrayList<String>() {{
		add("Events");
		add("Waves");
		add("Monsters");
		add("Items");
	}};
	/**Number of pages in first tab (NOT including first page)*/
	public int numIntroPages = introPages.size();
	private EntityPlayer editingPlayer;
	public boolean creative;
	private ArrayList<GuiButtonTab> buttonTabs;
	/**list of mob page buttons inside list of Events*/
	private ArrayList<ArrayList<GuiButtonMobPage>> buttonMobPages;
	private ArrayList<GuiButtonIntroPage> buttonIntroPages;
	private GuiButtonNextPage buttonNextPage;
	private GuiButtonNextPage buttonPreviousPage;
	private GuiButtonStartEvent buttonStartEvent;
	private GuiButtonSummonMob buttonSummonMob;
	/**list of mobs unlocked inside list of Events*/
	public ArrayList<ArrayList<IEventMob>> unlockedEntities;
	/**list of max progressOnDeath ordered by Event*/
	public ArrayList<Integer> maxProgress;
	/**list of random numbers to pick a joke ordered by Event*/
	public ArrayList<Integer> randomJoke;
	public int currentPage;
	public int currentTab;
	/**Rendering Zombie Summoner/Minions' pumpkins as lit or not*/
	private boolean litPumpkin;
	/**Used to calculate when to switch Zombie Summoners/Minions' lit*/
	private long startTime;

	public GuiEventBook(EntityPlayer player, boolean creative)
	{
		if (creative)
		{
			this.introPages.add("Creative");
			this.numIntroPages++;
		}
		this.editingPlayer = player;
		this.creative = creative;
		this.currentPage = creative ? Config.currentCreativePage : Config.currentPage;
		this.currentTab = creative ? Config.currentCreativeTab : Config.currentTab;
		unlockedEntities = new ArrayList<ArrayList<IEventMob>>();
		maxProgress = new ArrayList<Integer>();
		randomJoke = new ArrayList<Integer>();
		Config.syncFromConfig(player);
		for (int i=0; i<Event.EVENTS.length; i++) //iterate through events
		{  
			this.randomJoke.add(i, this.editingPlayer.worldObj.rand.nextInt(Event.EVENTS[i].bookJokes.size()));
			ArrayList<IEventMob> entities = new ArrayList<IEventMob>();
			if (Event.EVENTS[i].mobs != null)
			{
				if (creative)
				{
					entities.addAll(Event.EVENTS[i].mobs);
					this.maxProgress.add(0);
					for (IEventMob mob : Event.EVENTS[i].mobs)
						if (this.maxProgress.get(i) < mob.getProgressOnDeath() && mob.getProgressOnDeath() != 100)
							this.maxProgress.set(i, mob.getProgressOnDeath());
				}
				else
				{
					for (IEventMob mob : Event.EVENTS[i].mobs) //iterate through mobs in event
					{
						this.maxProgress.add(0);
						for (String entity : Config.unlockedEntities) //iterate through unlockedEntities in Config 
						{
							if (entity.equalsIgnoreCase(((Entity) mob).getName()))
								entities.add(mob);
							if (this.maxProgress.get(i) < mob.getProgressOnDeath() && mob.getProgressOnDeath() != 100)
								this.maxProgress.set(i, mob.getProgressOnDeath());
						}
					}
				}
			}
			unlockedEntities.add(entities); 
		}
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@Override
	public void initGui() 
	{
		int w = (this.width - this.bookImageWidth) / 2;
		int h = (this.height - this.bookImageHeight) / 2;	
		Keyboard.enableRepeatEvents(true);
		buttonList.clear();
		buttonList.add(this.buttonStartEvent = new GuiButtonStartEvent(0, w-28, h+79, 80, 20, "", this));
		buttonList.add(this.buttonSummonMob = new GuiButtonSummonMob(0, w-28, h+79, 80, 20, "Summon"));
		buttonTabs = new ArrayList<GuiButtonTab>();
		buttonMobPages = new ArrayList<ArrayList<GuiButtonMobPage>>();
		buttonIntroPages = new ArrayList<GuiButtonIntroPage>();
		for (int i=0; i<this.numIntroPages; i++)
		{
			buttonIntroPages.add(new GuiButtonIntroPage(i, 65, 23*i-55, 90, 20, this.introPages.get(i), this));
			buttonList.add(buttonIntroPages.get(i));
		}
		for (int i=0; i<Event.EVENTS.length+1; i++)
		{
			ArrayList<GuiButtonMobPage> eventMobPages = new ArrayList<GuiButtonMobPage>();
			if (i == 0)
			{
				buttonTabs.add(new GuiButtonTab(i, w-148, h+(20*i)-30, 100, 16, EnumChatFormatting.BOLD+"Introduction", this));
			}
			else
			{
				buttonTabs.add(new GuiButtonTab(i, w-148, h+(20*i)-30, 100, 16, Event.EVENTS[i-1].toString(), this));
				if (Event.EVENTS[i-1].mobs != null)
				{
					for (int j=0; j<Event.EVENTS[i-1].mobs.size(); j++) //iterate through mobs in event
					{
						eventMobPages.add(new GuiButtonMobPage(i, 97, 23*j-80, 110, 20, ((Entity) Event.EVENTS[i-1].mobs.get(j)).getName(), this, Event.EVENTS[i-1].mobs.get(j)));
						buttonList.add(eventMobPages.get(j));
					}
				}
			}
			buttonList.add(buttonTabs.get(i));
			this.buttonTabs.get(i).visible = true;
			this.buttonMobPages.add(eventMobPages);
		}
		this.buttonList.add(this.buttonNextPage = new GuiButtonNextPage(0, w+168, h+97, 18, 12, "test", true, this));
		this.buttonList.add(this.buttonPreviousPage = new GuiButtonNextPage(0, w-38, h+97, 18, 11, "test", false, this));
		this.updateScreen();
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	@Override
	public void updateScreen() 
	{
		//this.initGui();
		for (int i=0; i<this.numIntroPages; i++)
		{
			this.buttonIntroPages.get(i).visible = this.currentPage == 0 && this.currentTab == 0;
		}
		if (currentTab == 0)
			buttonNextPage.visible = currentPage < this.numIntroPages;
		else
			buttonNextPage.visible = currentPage < this.unlockedEntities.get(currentTab-1).size();
		buttonPreviousPage.visible = currentPage > 0;
		for (int i=0; i<this.buttonTabs.size(); i++)
		{
			if (i == currentTab)
				this.buttonTabs.get(i).xPosition = this.buttonTabs.get(i).startXPos+4;
			else
				this.buttonTabs.get(i).xPosition = this.buttonTabs.get(i).startXPos;
		}
		for (int i=0; i<this.buttonMobPages.size(); i++) //interate through Events
		{
			for (int j=0; j<this.buttonMobPages.get(i).size(); j++) //iterate through mob pages in Event
			{
				this.buttonMobPages.get(i).get(j).visible = this.currentTab > 0 && this.currentPage == 0 && i == this.currentTab; 
				this.buttonMobPages.get(i).get(j).enabled = this.unlockedEntities.get(i-1).contains(this.buttonMobPages.get(i).get(j).mob);
			}
		}
		this.buttonStartEvent.visible = this.creative && this.currentTab > 0 && this.currentPage == 0;
		this.buttonSummonMob.visible = this.creative && this.currentTab > 0 && this.currentPage > 0;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.drawDefaultBackground();
		float w = (this.width - this.bookImageWidth) / 2;
		float h = (this.height - this.bookImageHeight) / 2;

		//book
		mc.getTextureManager().bindTexture(bookPageTexture);
		GlStateManager.pushMatrix();
		float scale = 1.8f;
		GlStateManager.translate(w - 100, h, 0);
		GlStateManager.scale(scale, scale, scale);
		this.drawTexturedModalRect(24, -23, 0, 0, this.bookImageWidth, this.bookImageHeight);
		GlStateManager.popMatrix();

		if (currentPage > 0 && currentTab > 0)
		{
			//portrait background
			if (this.unlockedEntities.get(currentTab-1).get(currentPage-1).getProgressOnDeath() == 100)
				GlStateManager.color(0.7F, 0.0F, 0.7F, 1.0F);				
			else
			{
				float difficulty = (float)(this.unlockedEntities.get(currentTab-1).get(currentPage-1).getProgressOnDeath()-1)/(this.maxProgress.get(currentTab-1)-1);
				GlStateManager.color(255F, 1F-difficulty, 1F-difficulty, 1.0F);
			}
			GlStateManager.pushMatrix();
			scale = 1.8f;
			GlStateManager.translate(w - 100, h, 0);
			GlStateManager.scale(scale, scale, scale);
			this.drawTexturedModalRect(103, -17, 146, 6, 58, 77);
			GlStateManager.popMatrix();

			//mob portrait
			if (this.unlockedEntities.get(currentTab-1).get(currentPage-1) instanceof EntitySummonerZombie || this.unlockedEntities.get(currentTab-1).get(currentPage-1) instanceof EntityMinionZombie)
			{
				if (Minecraft.getSystemTime() >= this.startTime + 1500L)
				{
					startTime = Minecraft.getSystemTime();
					if (this.litPumpkin)
					{
						mc.getTextureManager().bindTexture(new ResourceLocation(MobEvents.MODID+":/textures/entity/"+((Entity) this.unlockedEntities.get(currentTab-1).get(currentPage-1)).getName()+" Off"+".png"));
						this.litPumpkin = false;
					}
					else
					{
						mc.getTextureManager().bindTexture(new ResourceLocation(MobEvents.MODID+":/textures/entity/"+((Entity) this.unlockedEntities.get(currentTab-1).get(currentPage-1)).getName()+" On"+".png"));
						this.litPumpkin = true;
					}
				}
				else if (this.litPumpkin)
					mc.getTextureManager().bindTexture(new ResourceLocation(MobEvents.MODID+":/textures/entity/"+((Entity) this.unlockedEntities.get(currentTab-1).get(currentPage-1)).getName()+" On"+".png"));
				else 
					mc.getTextureManager().bindTexture(new ResourceLocation(MobEvents.MODID+":/textures/entity/"+((Entity) this.unlockedEntities.get(currentTab-1).get(currentPage-1)).getName()+" Off"+".png"));
			}
			else
				mc.getTextureManager().bindTexture(new ResourceLocation(MobEvents.MODID+":/textures/entity/"+((Entity) this.unlockedEntities.get(currentTab-1).get(currentPage-1)).getName()+".png"));
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.pushMatrix();
			scale = 0.25f;
			GlStateManager.translate(w - 100, h, 0);
			GlStateManager.scale(scale, scale, scale);
			Gui.drawModalRectWithCustomSizedTexture(609, -248, 0, 0, this.entityImageWidth, this.entityImageHeight, this.entityImageWidth, this.entityImageHeight);
			GlStateManager.popMatrix();
		}

		//Event text
		if (this.currentTab > 0 && this.currentPage == 0)
		{
			String title = Event.EVENTS[this.currentTab-1].toString();
			float x = w+10-this.fontRendererObj.getStringWidth(title)/2;
			float y = h-30;
			this.mc.fontRendererObj.drawString(title, x+1, y, 0, true);
			this.mc.fontRendererObj.drawString(title, x-1, y, 0, true);
			this.mc.fontRendererObj.drawString(title, x, y+1, 0, true);
			this.mc.fontRendererObj.drawString(title, x, y-1, 0, true);
			this.mc.fontRendererObj.drawString(title, x, y, Event.EVENTS[this.currentTab-1].color, false);
			this.mc.fontRendererObj.drawSplitString(EnumChatFormatting.ITALIC+Event.EVENTS[this.currentTab-1].bookJokes.get(this.randomJoke.get(this.currentTab-1)), (int)w-38, (int)h-16, 106, 0);
			this.mc.fontRendererObj.drawString(EnumChatFormatting.BOLD+"Occurs: "+EnumChatFormatting.RESET+Event.EVENTS[this.currentTab-1].bookOccurs, w-38, h+45, 0, false);
			this.mc.fontRendererObj.drawString(EnumChatFormatting.BOLD+"Waves: "+EnumChatFormatting.RESET+Event.EVENTS[this.currentTab-1].bookWaves, w-38, h+60, 0, false);
		}
		//Mob details
		else if (this.currentTab > 0 && this.currentPage > 0)
		{
			IEventMob mob = this.unlockedEntities.get(currentTab-1).get(currentPage-1);
			int color = 0;
			if (mob.getProgressOnDeath() == 100)
			{
				int red = (int)(0.7F*255);
				int green = 0;
				int blue = (int)(0.7F*255);
				color = red;
				color = (color << 8) + green;
				color = (color << 8) + blue;
			}
			else
			{
				float difficulty = (float)(mob.getProgressOnDeath()-1)/(this.maxProgress.get(currentTab-1)-1);
				int red = 255;
				int green = (int)((1-difficulty)*255);
				int blue = (int)((1-difficulty)*255);
				if (green == 255 && blue == 255)//so white is readable
				{
					red = 225;
					green = 225;
					blue = 225;
				}
				color = red;
				color = (color << 8) + green;
				color = (color << 8) + blue;
			}
			String title = ((Entity) mob).getName();
			/**Full hearts*/
			double health = (((EntityLivingBase)mob).getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue())/2;
			int healthOffset = (int) h+11;
			/**Full armor*/
			double armor = mob.getBookArmor()/2;
			int armorOffset = (int) (healthOffset + 11*(health > 20 ? 1 : Math.ceil(health/10d)));
			/**Hearts per hit on normal mode*/
			double damage = ((((EntityLivingBase)mob).getEntityAttribute(SharedMonsterAttributes.attackDamage).getBaseValue()+mobWeaponDamage(mob)))/2;
			int damageOffset = (int) (armorOffset + 11*(armor > 20 ? 1 : Math.ceil(armor/10d)));
			int dropsOffset = damageOffset + 11;
			float x = w+3-this.fontRendererObj.getStringWidth(title)/2;
			float y = h-30;
			this.mc.fontRendererObj.drawString(EnumChatFormatting.BOLD+title, x, y, color, true);
			GlStateManager.pushMatrix();
			scale = 0.781f;
			GlStateManager.translate(w - 100, h, 0);
			GlStateManager.scale(scale, scale, scale);
			//text
			this.mc.fontRendererObj.drawSplitString(EnumChatFormatting.BOLD.toString()+EnumChatFormatting.ITALIC+mob.getBookDescription(), 73, -26, 145, 0);
			this.mc.fontRendererObj.drawString(EnumChatFormatting.BOLD+"Difficulty: "+mob.getProgressOnDeath(), 73, 2, 0, false);
			this.mc.fontRendererObj.drawString(EnumChatFormatting.BOLD+"Health: "+(health>20 ? health+" x" : ""), 73, healthOffset-h+6, 0, false);
			this.mc.fontRendererObj.drawString(EnumChatFormatting.BOLD+"Armor: "+(mob instanceof EntityCloneZombie ? EnumChatFormatting.RESET+"varies" : (armor>20 ? armor+" x" : "")), 73, armorOffset/scale-h/scale+2, 0, false);
			this.mc.fontRendererObj.drawString(EnumChatFormatting.BOLD+"Damage: "+(mob instanceof EntityCloneZombie ? EnumChatFormatting.RESET+"varies" : ""), 73, damageOffset/scale-h/scale+2, 0, false);
			this.mc.fontRendererObj.drawString(EnumChatFormatting.BOLD+"Drops: ", 73, dropsOffset/scale-h/scale+2, 0, false);
			if (mob instanceof EntityCloneZombie)
				this.mc.fontRendererObj.drawString("and any cloned item", 91, +73, 0, false);
			//drops
			RenderHelper.enableStandardItemLighting();
			if (mob.getBookDrops() != null)
			{
				ArrayList<Item> list = new ArrayList<Item>();//get rid of duplicates
				for (Item item : mob.getBookDrops())
					if (!list.contains(item))
						list.add(item);
				for (int i=0; i<list.size(); i++)//max items to display = 6                  
				{//min 110, max 200
					int spaceBetween = 130/(list.size()+1);
					this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(list.get(i)), 94 + (i+1)*spaceBetween, (int) (dropsOffset/scale-h/scale-2));
				}
			}
			RenderHelper.disableStandardItemLighting();
			GlStateManager.popMatrix();
			mc.getTextureManager().bindTexture(bookPageTexture);
			//health
			if (health > 20)
				this.drawTexturedModalRect(w+26, healthOffset+1, 0, 133, 9, 9);
			else
			{
				for (int r=0; r<health/10; r++)//rows
					for (int c=0; c<Math.min(health-r*10, 10); c++)//columns
						this.drawTexturedModalRect(c*8+w-12, r*11+healthOffset, 0, 133, r == (int)health/10 && c == (int)Math.min(health-r*10, 10) && health % health == 0 ? 5 : 9, 9);
			}
			//armor
			if (!(mob instanceof EntityCloneZombie))
			{
				if (armor > 20)
					this.drawTexturedModalRect(w+24, armorOffset+1, 0, 143, 9, 8);
				else
				{
					for (int r=0; r<armor/10; r++)//rows
						for (int c=0; c<Math.min(armor-r*10, 10); c++)//columns
							this.drawTexturedModalRect(c*8+w-12, r*11+armorOffset, 0, 143, r == (int)armor/10 && c == (int)Math.min(armor-r*10, 10) && armor % armor == 0 ? 5 : 9, 8);
				}
			}
			//damage
			if (!(mob instanceof EntityCloneZombie))
			{
				for (int i=0; i<damage; i++)
					this.drawTexturedModalRect(i*8+w-8, damageOffset, 0, 152, i == (int)damage && damage % damage == 0 ? 5 : 9, 8);
			}
		}

		//Introduction
		if (this.currentTab == 0)
		{
			if (this.currentPage == 0)
			{
				String title = "Introduction";
				String text = "This tab explains the basics of the Mob Events mod.\n\n"
						+ "The tabs on the left represent Events and are unlocked when you first experience the Event.\n\n"
						+ "They contain information on monsters that spawn during that Event.\n\n"
						+ "Each monster must be killed at least once for its page to be unlocked.";
				float x = w+3-this.fontRendererObj.getStringWidth(title)/2;
				float y = h-30;
				this.mc.fontRendererObj.drawString(EnumChatFormatting.BOLD+title, x, y, 0x595959, true);
				GlStateManager.pushMatrix();
				scale = 0.8f;
				GlStateManager.translate(w - 100, h, 0);
				GlStateManager.scale(scale, scale, scale);
				this.mc.fontRendererObj.drawSplitString(text, 75, -25, 137, 0);
				GlStateManager.popMatrix();
			}
			else if (this.currentPage == 1)
			{
				String title = "Events";
				String text1 = "Events begin and end with the day/night cycle.\n\n"
						+ "Events currently have a "+Config.eventChance+"% chance of occurring at the start of each day/night cycle   (can be changed in config).";
				String text2 = "";
				float x = w+3-this.fontRendererObj.getStringWidth(title)/2;
				float y = h-30;
				this.mc.fontRendererObj.drawString(EnumChatFormatting.BOLD+title, x, y, 0x595959, true);
				this.mc.fontRendererObj.drawSplitString(text1, (int)w-42, (int)h-20, 110, 0);
				this.mc.fontRendererObj.drawSplitString(text2, (int)w+85, (int)h-20, 110, 0);
			}
			else if (this.currentPage == 2)
			{
				String title = "Waves";
				String text1 = "Each Event has a certain number of waves that progress as monsters are killed.\n\n"
						+ "More difficult monsters spawn more frequently during later waves and advance the wave more when killed.";
				String text2 = "During the last wave of an Event, a special boss monster will spawn.\n\n"
						+ "Surviving though all of an Event's waves and boss can be very difficult.";
				float x = w+3-this.fontRendererObj.getStringWidth(title)/2;
				float y = h-30;
				this.mc.fontRendererObj.drawString(EnumChatFormatting.BOLD+title, x, y, 0x595959, true);
				this.mc.fontRendererObj.drawSplitString(text1, (int)w-42, (int)h-20, 110, 0);
				this.mc.fontRendererObj.drawSplitString(text2, (int)w+85, (int)h-20, 110, 0);
			}
			else if (this.currentPage == 3)
			{
				String title = "Monsters";
				String text1 = "During each Event, many monsters will spawn; each with their own ability, armor, weapon, and drops.\n\n"
						+ "These monsters vary in difficulty, with more difficult ones spawning more in later waves.\n\n";
				String text2 = "More difficult monsters progress the waves faster and drop better items.\n\n"
						+ "When the Event has ended, all of these monsters will disappear on their own.";
				float x = w+3-this.fontRendererObj.getStringWidth(title)/2;
				float y = h-30;
				this.mc.fontRendererObj.drawString(EnumChatFormatting.BOLD+title, x, y, 0x595959, true);
				this.mc.fontRendererObj.drawSplitString(text1, (int)w-42, (int)h-20, 110, 0);
				this.mc.fontRendererObj.drawSplitString(text2, (int)w+85, (int)h-20, 110, 0);
			}
			else if (this.currentPage == 4)
			{
				String title = "Items";
				String text1 = "Event monsters also have a chance of dropping some unique items.";
				String text2 = "";
				float x = w+3-this.fontRendererObj.getStringWidth(title)/2;
				float y = h-30;
				this.mc.fontRendererObj.drawString(EnumChatFormatting.BOLD+title, x, y, 0x595959, true);
				this.mc.fontRendererObj.drawSplitString(text1, (int)w-42, (int)h-20, 110, 0);
				this.mc.fontRendererObj.drawSplitString(text2, (int)w+85, (int)h-20, 110, 0);
			}
			else if (this.currentPage == 5)
			{
				String title = "Creative";
				String text1 = "This Creative Event Book can only be obtained in creative mode.\n\n"
						+ "It has all tabs and monsters unlocked.";
				String text2 = "This book has buttons that allow you to start and stop Events, summon monsters, and spawn in items.\n\n"
						+ "These buttons are only visible in this Creative Event Book and have a purple glowing effect.";
				float x = w+3-this.fontRendererObj.getStringWidth(title)/2;
				float y = h-30;
				this.mc.fontRendererObj.drawString(EnumChatFormatting.BOLD+title, x, y, 0x990099, true);
				this.mc.fontRendererObj.drawSplitString(text1, (int)w-42, (int)h-20, 110, 0);
				this.mc.fontRendererObj.drawSplitString(text2, (int)w+85, (int)h-20, 110, 0);
			}
		}
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	private float mobWeaponDamage(IEventMob mob)
	{
		ItemStack stack = ((EntityLiving) mob).getHeldItem();
		if (stack != null)
		{
			if (stack.getItem() instanceof ItemTool)
			{
				try 
				{
					Field field = ItemTool.class.getDeclaredField("damageVsEntity");
					field.setAccessible(true);
					return field.getFloat(stack.getItem());// + EnchantmentHelper.func_152377_a(stack, ((EntityLiving) mob).getCreatureAttribute());
				} 
				catch (Exception e) { 
					e.printStackTrace();
				}
			}
			else if (stack.getItem() instanceof ItemSword)
			{
				try 
				{
					Field field = ItemSword.class.getDeclaredField("attackDamage");
					field.setAccessible(true);
					return field.getFloat(stack.getItem());// + EnchantmentHelper.func_152377_a(stack, ((EntityLiving) mob).getCreatureAttribute());
				} 
				catch (Exception e) { 
					e.printStackTrace();
				}
			}
		}
		return 0;
	}

	@Override
	protected void actionPerformed(GuiButton button) 
	{
		if (button == this.buttonNextPage)
		{
			if (creative)
				Config.currentCreativePage++;
			else
				Config.currentPage++;
			this.currentPage++;
			Config.syncToConfig(this.editingPlayer);
		}
		else if (button == this.buttonPreviousPage)
		{
			if (creative)
				Config.currentCreativePage--;
			else
				Config.currentPage--;
			this.currentPage--;
			Config.syncToConfig(this.editingPlayer);
		}
		else if (button instanceof GuiButtonTab)
		{
			for (int i=0; i<this.buttonTabs.size(); i++)
				if (this.buttonTabs.get(i) == button && (i == 0 || i > 0 && Config.unlockedTabs.contains(Event.EVENTS[buttonTabs.get(i).id-1].toString()) || creative))
				{
					if (creative)
					{
						Config.currentCreativeTab = i;
						Config.currentCreativePage = 0;
					}
					else
					{
						Config.currentTab = i;
						Config.currentPage = 0;
					}
					this.currentTab = i;
					this.currentPage = 0;
					Config.syncToConfig(this.editingPlayer);
					break;
				}
		}
		else if (button instanceof GuiButtonMobPage)
		{
			if (((GuiButtonMobPage)button).enabled)
			{
				for (int i=0; i<this.unlockedEntities.get(currentTab-1).size(); i++) //iterate through unlocked mobs
				{
					if (this.unlockedEntities.get(currentTab-1).get(i).equals(((GuiButtonMobPage) button).mob))
					{
						if (creative)
							Config.currentCreativePage = i+1;
						else
							Config.currentPage = i+1;
						Config.syncToConfig(this.editingPlayer);
						this.currentPage = i+1;
						break;
					}
				}
			}
		}
		else if (button instanceof GuiButtonStartEvent)
		{
			if (Event.currentEvent.getClass() != Event.class)
			{
				this.editingPlayer.addChatMessage(new ChatComponentTranslation("Stopped "+Event.currentEvent.toString()+" Event.").setChatStyle(new ChatStyle().setItalic(true).setColor(EnumChatFormatting.DARK_PURPLE)));
				Event.currentEvent.stopEvent();
			}
			if (button.displayString.equals("Start Event"))
			{
				Event.EVENTS[this.currentTab-1].startEvent();
				this.editingPlayer.addChatMessage(new ChatComponentTranslation("Started "+Event.currentEvent.toString()+" Event.").setChatStyle(new ChatStyle().setItalic(true).setColor(EnumChatFormatting.DARK_PURPLE)));
			}
		}
		else if (button instanceof GuiButtonSummonMob)
		{
			MobEvents.network.sendToServer(new PacketSummonMob(this.currentTab-1, this.currentPage-1));
		}
		else if (button instanceof GuiButtonIntroPage)
		{
			for (int i=0; i<this.buttonIntroPages.size(); i++) //iterate through intro pages
			{
				if (this.introPages.get(i).equals(button.displayString))
				{
					if (creative)
						Config.currentCreativePage = i+1;
					else
						Config.currentPage = i+1;
					Config.syncToConfig(this.editingPlayer);
					this.currentPage = i+1;
					break;
				}
			}
		}
	}
}
