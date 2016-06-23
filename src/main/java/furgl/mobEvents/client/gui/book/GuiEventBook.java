package furgl.mobEvents.client.gui.book;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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
import furgl.mobEvents.common.entity.EntityGuiPlayer;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityCloneZombie;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityRiderZombie;
import furgl.mobEvents.common.entity.ZombieApocalypse.IEventMob;
import furgl.mobEvents.common.item.ModItems;
import furgl.mobEvents.common.item.drops.IEventItem;
import furgl.mobEvents.packets.PacketGiveItem;
import furgl.mobEvents.packets.PacketSummonMob;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
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
	/**list of items unlocked*/
	public ArrayList<IEventItem> unlockedItems;
	/**list of max progressOnDeath ordered by Event*/
	public ArrayList<Integer> maxProgress;
	/**list of random numbers to pick a joke ordered by Event*/
	public ArrayList<Integer> randomJoke;
	public int currentPage;
	public int currentTab;
	private float partialTicks;

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
		unlockedItems = new ArrayList<IEventItem>();
		maxProgress = new ArrayList<Integer>();
		randomJoke = new ArrayList<Integer>();
		Config.syncFromConfig(player);
		for (String itemName : Config.unlockedItems)
		{
			for (IEventItem item : ModItems.drops)
				if (itemName.equalsIgnoreCase(item.getName()))
					this.unlockedItems.add(item);
		}
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
		//set world and do onInitialSpawn (for mob portraits)
		for (ArrayList<IEventMob> list : this.unlockedEntities)
			for (IEventMob mob : list) {
				if (((EntityLiving)mob).worldObj == null) 
					((EntityLiving)mob).setWorld(mc.theWorld);
				((EntityLiving)mob).onInitialSpawn(null, null);
			}

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
			buttonItemPages.add(new GuiButtonItemPage(i, 65, this.creative ? 23*i-45 : 23*i-33, 98, 20, ModItems.drops.get(i).getName(), this));
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
					eventMobPages.add(new GuiButtonMobPage(i, 97, 20*j-85, 110, 20, ((Entity) Event.EVENTS[i].mobs.get(j)).getName(), this, Event.EVENTS[i].mobs.get(j)));
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
			buttonNextPage.visible = currentPage < Config.unlockedItems.size();//ModItems.drops.size();
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
	public boolean doesGuiPauseGame()
	{
		return false;
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
			EntityLiving entity = (EntityLiving) this.unlockedEntities.get(currentTab-numNonEventTabs).get(currentPage-1);
			((IEventMob) entity).doSpecialRender();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.pushMatrix();
			scale = 40f;
			GlStateManager.translate(w - 100, h, 0);
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.rotate(180F, 0F, 0F, 1F);
			GlStateManager.rotate(135.0F, 0.0F, 1, 0.0f);
			RenderHelper.enableStandardItemLighting();
			GlStateManager.rotate(-165.0F, 0.0F, 1, -0.0f);
			GlStateManager.rotate(-10.0F, -1F, 0F, 0.5f);
			entity.rotationYawHead = 0.0F;
			entity.renderYawOffset = 0.0F;
			this.partialTicks += 0.3F;
			mc.getRenderManager().setPlayerViewY(-20f);
			if (entity instanceof EntityRiderZombie) {
				entity.hurtTime = 0;
				((EntityLiving)entity.ridingEntity).rotationYawHead = 0.0F;
				((EntityLiving)entity.ridingEntity).renderYawOffset = 0.0F;
				mc.getRenderManager().renderEntityWithPosYaw(entity, -4D, -1.3D, 5.0D, 0.0F, this.partialTicks);
				mc.getRenderManager().renderEntityWithPosYaw(entity.ridingEntity, -4D, -2D, 5.0D, 0.0F, this.partialTicks);
			}
			else
				mc.getRenderManager().renderEntityWithPosYaw(entity, -4D, -1.5D, 5.0D, 0.0F, this.partialTicks);
			RenderHelper.disableStandardItemLighting();
			this.mc.entityRenderer.disableLightmap();
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
			double armor = ((EntityLivingBase) mob).getTotalArmorValue()/2;
			int armorOffset = (int) (healthOffset + 11*(health > 20 ? 1 : Math.ceil(health/10d)));
			/**Hearts per hit on normal mode*/
			double damage = ((((EntityLivingBase)mob).getEntityAttribute(SharedMonsterAttributes.attackDamage).getBaseValue()+mobWeaponDamage(mob)))/2;
			int damageOffset = (int) (armorOffset + 11*(armor > 20 ? 1 : Math.ceil(armor/10d)));
			int dropsOffset = (int) (damageOffset + 11*(damage > 20 ? 1 : Math.ceil(damage/10d)));
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
			this.mc.fontRendererObj.drawString(EnumChatFormatting.BOLD+"Armor: "+(armor>20 ? armor+" x" : ""), 73, armorOffset/scale-h/scale+2, 0, false);
			this.mc.fontRendererObj.drawString(EnumChatFormatting.BOLD+"Damage: "+(damage>20 ? damage+" x" : ""), 73, damageOffset/scale-h/scale+2, 0, false);
			this.mc.fontRendererObj.drawString(EnumChatFormatting.BOLD+"Drops: ", 73, dropsOffset/scale-h/scale+2, 0, false);

			RenderHelper.disableStandardItemLighting();
			GlStateManager.popMatrix();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			mc.getTextureManager().bindTexture(bookPageTexture);
			//health
			if (health > 20)
				this.drawTexturedModalRect(w+3+this.fontRendererObj.getStringWidth(String.valueOf(health)), healthOffset+1, 0, 133, 9, 9);
			else
			{
				for (int r=0; r<health/10; r++)//rows
					for (int c=0; c<Math.min(health-r*10, 10); c++)//columns
						this.drawTexturedModalRect(c*8+w-12, r*11+healthOffset, 0, 133, r == (int)health/10 && c == (int)Math.min(health-r*10, 10) && health % health == 0 ? 5 : 9, 9);
			}
			//armor
			if (armor > 20)
				this.drawTexturedModalRect(w+2+this.fontRendererObj.getStringWidth(String.valueOf(armor)), armorOffset+1, 0, 143, 9, 8);
			else
			{
				for (int r=0; r<armor/10; r++)//rows
					for (int c=0; c<Math.min(armor-r*10, 10); c++)//columns
						this.drawTexturedModalRect(c*8+w-12, r*11+armorOffset, 0, 143, r == (int)armor/10 && c == (int)Math.min(armor-r*10, 10) && armor % armor == 0 ? 5 : 9, 8);
			}
			//damage
			if (damage > 20) 
				this.drawTexturedModalRect(w+6+this.fontRendererObj.getStringWidth(String.valueOf(damage)), damageOffset, 0, 152, 9, 8);
			else
			{
				for (int r=0; r<damage/10; r++)//rows
					for (int c=0; c<Math.min(damage-r*10, 10); c++)//columns
						this.drawTexturedModalRect(c*8+w-8, r*11+damageOffset, 0, 152, r == (int)damage && damage % damage == 0 ? 5 : 9, 8);
			}
			GlStateManager.pushMatrix();
			scale = 0.781f;
			GlStateManager.translate(w - 100, h, 0);
			GlStateManager.scale(scale, scale, scale);
			//drops
			ArrayList<ItemStack> drops = new ArrayList<ItemStack>();//get rid of duplicates
			ArrayList<Double> dropChances = new ArrayList<Double>();//drop chances
			if (mob.getBookDrops() != null)
			{
				for (ItemStack stack1 : mob.getBookDrops())//get rid of duplicates
				{
					boolean add = true;
					for (ItemStack stack2 : drops)
					{
						if (stack1.getItem() == stack2.getItem())
							add = false;
					}
					if (add) 
						drops.add(stack1.copy());
				}
				for (ItemStack stack1 : drops)//calculate drop chances
				{
					double chance = 0;
					for (ItemStack stack2 : mob.getBookDrops())
					{
						if (stack1.getItem() == stack2.getItem())
							chance++;//# occurrences
					}
					chance /= mob.getBookDrops().size();
					int i = EnchantmentHelper.getLootingModifier(mc.thePlayer);
					chance *= (double)100/(20 + i * 5);
					chance = Math.round((chance / 10 * 10) * 10) / 10.0;
					dropChances.add(chance); 
				}
				if (mob instanceof EntityCloneZombie) {//add cloned items
					for (int i=0; i<4; i++) {
						if (((EntityLiving)mob).getCurrentArmor(i) != null)
						{
							drops.add(((EntityLiving)mob).getCurrentArmor(i));
							dropChances.add(5.0);
						}
					}
					if (((EntityLiving)mob).getHeldItem() != null)
					{
						drops.add(((EntityLiving)mob).getHeldItem());
						dropChances.add(5.0);
					}
				}
				for (int i=0; i<drops.size(); i++)//max items to display = 6                  
				{//min 110, max 200
					int spaceBetween = 130/(drops.size()+1);
					int xPos = 94 + (i+1)*spaceBetween;
					int yPos = (int) (dropsOffset/scale-h/scale-2);
					RenderHelper.enableGUIStandardItemLighting();
					this.itemRender.renderItemAndEffectIntoGUI(drops.get(i), xPos, yPos);
				}
				for (int i=0; i<drops.size(); i++)                  
				{
					int spaceBetween = 130/(drops.size()+1);
					int xPos = 94 + (i+1)*spaceBetween;
					int yPos = (int) (dropsOffset/scale-h/scale-2);
					int mX = (int) ((mouseX-w+100)/scale);
					int mY = (int) ((mouseY-h)/scale);
					if (mX >= xPos && mY >= yPos && mX < xPos + 16 && mY < yPos + 16) {
						List<String> tooltip = drops.get(i).getTooltip(this.editingPlayer, false);
						tooltip.add(EnumChatFormatting.DARK_PURPLE+""+EnumChatFormatting.BOLD+dropChances.get(i)+"% drop chance");
						this.drawHoveringText(tooltip, mX, mY);
					}
				}
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
						+ "It has all tabs, items, and monsters unlocked.";
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
				IEventItem item = this.unlockedItems.get(this.currentPage-1);//ModItems.drops.get(this.currentPage-1);
				ItemStack stack = item.getItemStack();
				String title = item.getName();
				float x = w+3-this.fontRendererObj.getStringWidth(title)/2;
				float y = h-30;
				this.mc.fontRendererObj.drawString(EnumChatFormatting.BOLD+title, x, y, item.getColor(), true);
				//dropped by
				this.mc.fontRendererObj.drawString(EnumChatFormatting.BOLD+"Dropped by:", w-38, h+50, 0, false);
				if (item.droppedBy().size() == 1)
					this.mc.fontRendererObj.drawString(item.droppedBy().get(0), w-38, h+65, 0, false);
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
				EntityGuiPlayer player = new EntityGuiPlayer(mc.theWorld, mc.thePlayer.getGameProfile(), mc.thePlayer);
				int slot = -1;
				for (int i=0; i<5; i++)
					if (((Item)item).isValidArmor(stack, i, player)) {
						slot = i;
						break;
					}
				switch(slot)
				{
				case 0:
					slot = 4;
					break;
				case 1:
					slot = 3;
					break;
				case 2: 
					slot = 2;
					break;
				case 3:
					slot = 1;
					break;
				}
				player.setCurrentItemOrArmor(slot, stack);
				player.doSpecialRender();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.pushMatrix();
				scale = 40f;
				GlStateManager.translate(w - 100, h, 0);
				GlStateManager.scale(scale, scale, scale);
				GlStateManager.rotate(180F, 0F, 0F, 1F);
				GlStateManager.rotate(135.0F, 0.0F, 1, 0.0f);
				RenderHelper.enableStandardItemLighting();
				GlStateManager.rotate(-165.0F, 0.0F, 1, -0.0f);
				GlStateManager.rotate(-10.0F, -1F, 0F, 0.5f);
				player.rotationYawHead = 0.0F;
				player.renderYawOffset = 0.0F;
				this.partialTicks += 0.3F;
				mc.getRenderManager().setPlayerViewY(-20f);
				mc.getRenderManager().renderEntityWithPosYaw(player, -4D, -1.5D, 5.0D, 0.0F, this.partialTicks);
				RenderHelper.disableStandardItemLighting();
				this.mc.entityRenderer.disableLightmap();
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
					return field.getFloat(stack.getItem()) + (float)EnchantmentHelper.func_152377_a(stack, EnumCreatureAttribute.UNDEFINED);
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
			for (int i=0; i<this.unlockedItems.size(); i++) //iterate through Item pages
			{
				if (this.unlockedItems.get(i).getName().equals(button.displayString))
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
