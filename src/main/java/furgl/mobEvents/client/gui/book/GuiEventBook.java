package furgl.mobEvents.client.gui.book;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import furgl.mobEvents.client.gui.book.buttons.GuiButtonGiveItem;
import furgl.mobEvents.client.gui.book.buttons.GuiButtonIntroPage;
import furgl.mobEvents.client.gui.book.buttons.GuiButtonItemPage;
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
import furgl.mobEvents.common.item.ModItems;
import furgl.mobEvents.common.item.drops.IEventItem;
import furgl.mobEvents.common.item.drops.ItemSummonersHelm;
import furgl.mobEvents.packets.PacketGiveItem;
import furgl.mobEvents.packets.PacketSummonMob;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.Enchantment;
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
	}};
	/**Number of pages in first tab (NOT including first page)*/
	public int numIntroPages = introPages.size();
	public final int numNonEventTabs = 2;
	public ArrayList<String> nonEventTabs = new ArrayList<String>() {{
		add("Introduction");
		add("Items");
	}};
	private EntityPlayer editingPlayer;
	public boolean creative;
	private ArrayList<GuiButtonTab> buttonTabs;
	/**list of mob page buttons inside list of Events*/
	private ArrayList<ArrayList<GuiButtonMobPage>> buttonMobPages;
	private ArrayList<GuiButtonIntroPage> buttonIntroPages;
	private ArrayList<GuiButtonItemPage> buttonItemPages;
	private GuiButtonNextPage buttonNextPage;
	private GuiButtonNextPage buttonPreviousPage;
	private GuiButtonStartEvent buttonStartEvent;
	private GuiButtonSummonMob buttonSummonMob;
	private GuiButtonGiveItem buttonGiveItem;
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
		buttonList.add(this.buttonSummonMob = new GuiButtonSummonMob(0, w-28, h+79, 80, 20, "Summon Mob"));
		buttonList.add(this.buttonGiveItem = new GuiButtonGiveItem(0, w-28, h+79, 80, 20, "Give Item"));
		buttonTabs = new ArrayList<GuiButtonTab>();
		buttonMobPages = new ArrayList<ArrayList<GuiButtonMobPage>>();
		buttonIntroPages = new ArrayList<GuiButtonIntroPage>();
		for (int i=0; i<this.numIntroPages; i++)
		{
			buttonIntroPages.add(new GuiButtonIntroPage(i, 65, this.creative ? 23*i-45 : 23*i-33, 90, 20, this.introPages.get(i), this));
			buttonList.add(buttonIntroPages.get(i));
		}
		buttonItemPages = new ArrayList<GuiButtonItemPage>();
		for (int i=0; i<ModItems.drops.size(); i++)
		{
			buttonItemPages.add(new GuiButtonItemPage(i, 65, this.creative ? 23*i-45 : 23*i-33, 90, 20, ModItems.drops.get(i).getName(), this));
			buttonList.add(buttonItemPages.get(i));
		}
		for (int i=0; i<this.numNonEventTabs; i++)
		{
			buttonTabs.add(new GuiButtonTab(i, w-148, h+(20*i)-30, 100, 16, EnumChatFormatting.BOLD+this.nonEventTabs.get(i), this));
			buttonList.add(buttonTabs.get(i));
		}
		for (int i=0; i<Event.EVENTS.length; i++)
		{
			ArrayList<GuiButtonMobPage> eventMobPages = new ArrayList<GuiButtonMobPage>();
			buttonTabs.add(new GuiButtonTab(i+numNonEventTabs, w-148, h+(20*(i+numNonEventTabs))-30, 100, 16, Event.EVENTS[i].toString(), this));
			if (Event.EVENTS[i].mobs != null)
			{
				for (int j=0; j<Event.EVENTS[i].mobs.size(); j++) //iterate through mobs in event
				{
					eventMobPages.add(new GuiButtonMobPage(i, 97, 23*j-80, 110, 20, ((Entity) Event.EVENTS[i].mobs.get(j)).getName(), this, Event.EVENTS[i].mobs.get(j)));
					buttonList.add(eventMobPages.get(j));
				}
			}
			buttonList.add(buttonTabs.get(i+numNonEventTabs));
			this.buttonMobPages.add(eventMobPages);
		}
		this.buttonList.add(this.buttonNextPage = new GuiButtonNextPage(0, w+168, h+97, 18, 12, "", true, this));
		this.buttonList.add(this.buttonPreviousPage = new GuiButtonNextPage(0, w-38, h+97, 18, 11, "", false, this));
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
		for (int i=0; i<ModItems.drops.size(); i++)
		{
			this.buttonItemPages.get(i).visible = this.currentPage == 0 && this.currentTab == 1;
			this.buttonItemPages.get(i).enabled = creative || Config.unlockedItems.contains(ModItems.drops.get(i).getName());
		}
		if (currentTab == 0)
			buttonNextPage.visible = currentPage < this.numIntroPages;
		else if (currentTab == 1)
			buttonNextPage.visible = currentPage < ModItems.drops.size();
		else
			buttonNextPage.visible = currentPage < this.unlockedEntities.get(currentTab-numNonEventTabs).size();
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
				this.buttonMobPages.get(i).get(j).visible = this.currentTab >= numNonEventTabs && this.currentPage == 0 && i == this.currentTab-numNonEventTabs; 
				this.buttonMobPages.get(i).get(j).enabled = this.unlockedEntities.get(i/*-numNonEventTabs*/).contains(this.buttonMobPages.get(i).get(j).mob);
			}
		}
		this.buttonStartEvent.visible = this.creative && this.currentTab > numNonEventTabs-1 && this.currentPage == 0;
		this.buttonSummonMob.visible = this.creative && this.currentTab > numNonEventTabs-1 && this.currentPage > 0;
		this.buttonGiveItem.visible = this.creative && this.currentTab < numNonEventTabs && this.nonEventTabs.get(this.currentTab).equals("Items") && this.currentPage > 0;
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

		if (currentPage > 0 && currentTab >= numNonEventTabs)
		{
			//portrait background
			if (this.unlockedEntities.get(currentTab-numNonEventTabs).get(currentPage-1).getProgressOnDeath() == 100)
				GlStateManager.color(0.7F, 0.0F, 0.7F, 1.0F);				
			else
			{
				float difficulty = (float)(this.unlockedEntities.get(currentTab-numNonEventTabs).get(currentPage-1).getProgressOnDeath()-1)/(this.maxProgress.get(currentTab-numNonEventTabs)-1);
				GlStateManager.color(255F, 1F-difficulty, 1F-difficulty, 1.0F);
			}
			GlStateManager.pushMatrix();
			scale = 1.8f;
			GlStateManager.translate(w - 100, h, 0);
			GlStateManager.scale(scale, scale, scale);
			this.drawTexturedModalRect(103, -17, 146, 6, 58, 77);
			GlStateManager.popMatrix();

			//mob portrait
			if (this.unlockedEntities.get(currentTab-numNonEventTabs).get(currentPage-1) instanceof EntitySummonerZombie || this.unlockedEntities.get(currentTab-numNonEventTabs).get(currentPage-1) instanceof EntityMinionZombie)
			{
				if (Minecraft.getSystemTime() >= this.startTime + 1500L)
				{
					startTime = Minecraft.getSystemTime();
					if (this.litPumpkin)
					{
						mc.getTextureManager().bindTexture(new ResourceLocation(MobEvents.MODID+":/textures/entity/"+((Entity) this.unlockedEntities.get(currentTab-numNonEventTabs).get(currentPage-1)).getName()+" Off"+".png"));
						this.litPumpkin = false;
					}
					else
					{
						mc.getTextureManager().bindTexture(new ResourceLocation(MobEvents.MODID+":/textures/entity/"+((Entity) this.unlockedEntities.get(currentTab-numNonEventTabs).get(currentPage-1)).getName()+" On"+".png"));
						this.litPumpkin = true;
					}
				}
				else if (this.litPumpkin)
					mc.getTextureManager().bindTexture(new ResourceLocation(MobEvents.MODID+":/textures/entity/"+((Entity) this.unlockedEntities.get(currentTab-numNonEventTabs).get(currentPage-1)).getName()+" On"+".png"));
				else 
					mc.getTextureManager().bindTexture(new ResourceLocation(MobEvents.MODID+":/textures/entity/"+((Entity) this.unlockedEntities.get(currentTab-numNonEventTabs).get(currentPage-1)).getName()+" Off"+".png"));
			}
			else
				mc.getTextureManager().bindTexture(new ResourceLocation(MobEvents.MODID+":/textures/entity/"+((Entity) this.unlockedEntities.get(currentTab-numNonEventTabs).get(currentPage-1)).getName()+".png"));
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.pushMatrix();
			scale = 0.25f;
			GlStateManager.translate(w - 100, h, 0);
			GlStateManager.scale(scale, scale, scale);
			Gui.drawModalRectWithCustomSizedTexture(609, -248, 0, 0, this.entityImageWidth, this.entityImageHeight, this.entityImageWidth, this.entityImageHeight);
			GlStateManager.popMatrix();
		}

		//Event text
		if (this.currentTab > numNonEventTabs-1 && this.currentPage == 0)
		{
			String title = Event.EVENTS[this.currentTab-numNonEventTabs].toString();
			float x = w+10-this.fontRendererObj.getStringWidth(title)/2;
			float y = h-30;
			this.mc.fontRendererObj.drawString(title, x+1, y, 0, true);
			this.mc.fontRendererObj.drawString(title, x-1, y, 0, true);
			this.mc.fontRendererObj.drawString(title, x, y+1, 0, true);
			this.mc.fontRendererObj.drawString(title, x, y-1, 0, true);
			this.mc.fontRendererObj.drawString(title, x, y, Event.EVENTS[this.currentTab-numNonEventTabs].color, false);
			this.mc.fontRendererObj.drawSplitString(EnumChatFormatting.ITALIC+Event.EVENTS[this.currentTab-numNonEventTabs].bookJokes.get(this.randomJoke.get(this.currentTab-numNonEventTabs)), (int)w-38, (int)h-16, 106, 0);
			this.mc.fontRendererObj.drawString(EnumChatFormatting.BOLD+"Occurs: "+EnumChatFormatting.RESET+Event.EVENTS[this.currentTab-numNonEventTabs].bookOccurs, w-38, h+45, 0, false);
			this.mc.fontRendererObj.drawString(EnumChatFormatting.BOLD+"Waves: "+EnumChatFormatting.RESET+Event.EVENTS[this.currentTab-numNonEventTabs].bookWaves, w-38, h+60, 0, false);
		}
		//Mob details
		else if (this.currentTab > numNonEventTabs-1 && this.currentPage > 0)
		{
			IEventMob mob = this.unlockedEntities.get(currentTab-numNonEventTabs).get(currentPage-1);
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
				float difficulty = (float)(mob.getProgressOnDeath()-1)/(this.maxProgress.get(currentTab-numNonEventTabs)-1);
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
				this.mc.fontRendererObj.drawString("and any cloned items", 110, +73, 0, false);

			RenderHelper.disableStandardItemLighting();
			GlStateManager.popMatrix();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
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

			GlStateManager.pushMatrix();
			scale = 0.781f;
			GlStateManager.translate(w - 100, h, 0);
			GlStateManager.scale(scale, scale, scale);
			//drops
			RenderHelper.enableStandardItemLighting();
			ArrayList<ItemStack> list = new ArrayList<ItemStack>();//get rid of duplicates
			if (mob.getBookDrops() != null)
			{
				for (ItemStack stack1 : mob.getBookDrops())
				{
					boolean add = true;
					for (ItemStack stack2 : list)
					{
						if (stack1.getItem() == stack2.getItem())
							add = false;
					}
					if (add)
						list.add(stack1.copy());
				}
				for (int i=0; i<list.size(); i++)//max items to display = 6                  
				{//min 110, max 200
					int spaceBetween = 130/(list.size()+1);
					int xPos = 94 + (i+1)*spaceBetween;
					int yPos = (int) (dropsOffset/scale-h/scale-2);
					this.itemRender.renderItemAndEffectIntoGUI(list.get(i), xPos, yPos);
				}
				for (int i=0; i<list.size(); i++)                  
				{
					int spaceBetween = 130/(list.size()+1);
					int xPos = 94 + (i+1)*spaceBetween;
					int yPos = (int) (dropsOffset/scale-h/scale-2);
					int mX = (int) ((mouseX-w+100)/scale);
					int mY = (int) ((mouseY-h)/scale);
					if (mX >= xPos && mY >= yPos && mX < xPos + 16 && mY < yPos + 16)
						this.drawHoveringText(list.get(i).getTooltip(this.editingPlayer, false), mX, mY);
				}
				RenderHelper.disableStandardItemLighting();
				GlStateManager.popMatrix();
			}
		}

		//Introduction
		if (this.currentTab == 0)
		{
			if (this.currentPage == 0)
			{
				String title = "Introduction";
				String text = "This tab explains the basics of the Mob Events mod.\n\n"
						+ "The colored tabs on the left represent Events and are unlocked when you first experience the Event.\n\n"
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
						+ "Surviving though all of an Event's waves and boss can be very difficult, but rewarding.";
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
						+ "These monsters vary in difficulty, with more difficult ones spawning more often in later waves.\n\n";
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
		else if (currentTab == 1)
		{
			if (this.currentPage == 0)
			{
				String title = "Items";
				String text = "This tab contains information on all custom items dropped by Event monsters.\n\n"
						+ "Each item must be obtained for its page to be unlocked.";
				float x = w+3-this.fontRendererObj.getStringWidth(title)/2;
				float y = h-30;
				this.mc.fontRendererObj.drawString(EnumChatFormatting.BOLD+title, x, y, 0x595959, true);
				this.mc.fontRendererObj.drawSplitString(text, (int)w-42, (int)h-20, 110, 0);
			}
			else
			{
				IEventItem item = ModItems.drops.get(this.currentPage-1);
				ItemStack stack = new ItemStack((Item) item);
				if (item instanceof ItemSummonersHelm)
					stack.addEnchantment(Enchantment.fireProtection, 5);
				String title = item.getName();
				float x = w+3-this.fontRendererObj.getStringWidth(title)/2;
				float y = h-30;
				this.mc.fontRendererObj.drawString(EnumChatFormatting.BOLD+title, x, y, item.getColor(), true);
				//dropped by
				this.mc.fontRendererObj.drawString(EnumChatFormatting.BOLD+"Dropped by:", w-38, h+45, 0, false);
				if (item.droppedBy().size() == 1)
					this.mc.fontRendererObj.drawString(item.droppedBy().get(0), w-30, h+60, 0, false);
				//render item
				RenderHelper.enableStandardItemLighting();
				GlStateManager.pushMatrix();
				scale = 2f;
				GlStateManager.translate(w - 100, h, 0);
				GlStateManager.scale(scale, scale, scale);
				this.itemRender.renderItemAndEffectIntoGUI(stack, 47, -10);
				GlStateManager.popMatrix();
				//render tooltip
				GlStateManager.pushMatrix();
				scale = 0.75f;
				GlStateManager.translate(w - 100, h, 0);
				GlStateManager.scale(scale, scale, scale);
				int length = 0;
				for (String string : stack.getTooltip(this.editingPlayer, false))
					if (this.fontRendererObj.getStringWidth(string) > length)
						length = this.fontRendererObj.getStringWidth(string);
				this.drawHoveringText(stack.getTooltip(this.editingPlayer, false), 137-length/2, 32);
				GlStateManager.popMatrix();
				RenderHelper.disableStandardItemLighting();
				//portrait background
				mc.getTextureManager().bindTexture(bookPageTexture);
				GlStateManager.color(item.getRed(), item.getGreen(), item.getBlue(), 1.0F);				
				GlStateManager.pushMatrix();
				scale = 1.8f;
				GlStateManager.translate(w - 100, h, 0);
				GlStateManager.scale(scale, scale, scale);
				this.drawTexturedModalRect(103, -17, 146, 6, 58, 77);
				GlStateManager.popMatrix();
				//player portrait
				if (item instanceof ItemSummonersHelm)
				{
					if (Minecraft.getSystemTime() >= this.startTime + 1500L)
					{
						startTime = Minecraft.getSystemTime();
						if (this.litPumpkin)
						{
							mc.getTextureManager().bindTexture(new ResourceLocation(MobEvents.MODID+":/textures/entity/"+item.getName()+" Off"+".png"));
							this.litPumpkin = false;
						}
						else
						{
							mc.getTextureManager().bindTexture(new ResourceLocation(MobEvents.MODID+":/textures/entity/"+item.getName()+" On"+".png"));
							this.litPumpkin = true;
						}
					}
					else if (this.litPumpkin)
						mc.getTextureManager().bindTexture(new ResourceLocation(MobEvents.MODID+":/textures/entity/"+item.getName()+" On"+".png"));
					else 
						mc.getTextureManager().bindTexture(new ResourceLocation(MobEvents.MODID+":/textures/entity/"+item.getName()+" Off"+".png"));
				}
				else
					mc.getTextureManager().bindTexture(new ResourceLocation(MobEvents.MODID+":/textures/entity/"+item.getName()+".png"));
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.pushMatrix();
				scale = 0.25f;
				GlStateManager.translate(w - 100, h, 0);
				GlStateManager.scale(scale, scale, scale);
				Gui.drawModalRectWithCustomSizedTexture(609, -248, 0, 0, this.entityImageWidth, this.entityImageHeight, this.entityImageWidth, this.entityImageHeight);
				GlStateManager.popMatrix();
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
					return field.getFloat(stack.getItem());
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
					return field.getFloat(stack.getItem());
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
				if (this.buttonTabs.get(i) == button && (i < numNonEventTabs || Config.unlockedTabs.contains(Event.EVENTS[buttonTabs.get(i).id-numNonEventTabs].toString()) || creative))
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
				for (int i=0; i<this.unlockedEntities.get(currentTab-numNonEventTabs).size(); i++) //iterate through unlocked mobs
				{
					if (this.unlockedEntities.get(currentTab-numNonEventTabs).get(i).equals(((GuiButtonMobPage) button).mob))
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
				Event.EVENTS[this.currentTab-numNonEventTabs].startEvent();
				this.editingPlayer.addChatMessage(new ChatComponentTranslation("Started "+Event.currentEvent.toString()+" Event.").setChatStyle(new ChatStyle().setItalic(true).setColor(EnumChatFormatting.DARK_PURPLE)));
			}
		}
		else if (button instanceof GuiButtonSummonMob)
		{
			MobEvents.network.sendToServer(new PacketSummonMob(this.currentTab-numNonEventTabs, this.currentPage-1));
		}
		else if (button instanceof GuiButtonGiveItem)
		{
			MobEvents.network.sendToServer(new PacketGiveItem(this.currentPage-1));
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
		else if (button instanceof GuiButtonItemPage)
		{
			for (int i=0; i<this.buttonItemPages.size(); i++) //iterate through Item pages
			{
				if (ModItems.drops.get(i).getName().equals(button.displayString))
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
