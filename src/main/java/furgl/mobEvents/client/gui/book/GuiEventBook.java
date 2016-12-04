package furgl.mobEvents.client.gui.book;

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
import furgl.mobEvents.client.gui.book.buttons.GuiButtonWave;
import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.Events.Event;
import furgl.mobEvents.common.entity.EntityGuiPlayer;
import furgl.mobEvents.common.entity.IEventMob;
import furgl.mobEvents.common.entity.SkeletalUprising.EntitySkeletonClone;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityZombieClone;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityZombieRider;
import furgl.mobEvents.common.entity.bosses.IEventBoss;
import furgl.mobEvents.common.entity.bosses.spawner.EntityBossSpawner;
import furgl.mobEvents.common.entity.bosses.spawner.EntityZombieBossSpawner;
import furgl.mobEvents.common.event.EventFogEvent;
import furgl.mobEvents.common.item.ModItems;
import furgl.mobEvents.common.item.drops.IEventItem;
import furgl.mobEvents.packets.PacketGiveItem;
import furgl.mobEvents.packets.PacketSetCurrentPagesAndTabs;
import furgl.mobEvents.packets.PacketSetEvent;
import furgl.mobEvents.packets.PacketSetWave;
import furgl.mobEvents.packets.PacketSummonMob;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
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
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class GuiEventBook extends GuiScreen
{
	public final ResourceLocation bookPageTexture = new ResourceLocation(MobEvents.MODID+":textures/gui/event_book.png");
	protected final int bookImageWidth = 292/2;
	protected final int bookImageHeight = 180/2;
	public ArrayList<String> introPages = new ArrayList<String>() {{
		add("Events");
		add("Commands");
		add("Waves");
		add("Monsters");
		add("Bosses");
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
	private GuiButtonWave buttonWave;
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
	/**Used for rendering instead of worldTime (inconsistent)*/
	private float partialTicks;
	/**Used for rendering instead of worldTime (inconsistent)*/
	public int displayTicks;
	/**EntityGuiPlayer used in gui*/
	private EntityGuiPlayer guiPlayer;
	/**page that gui player is geared for*/
	private int guiPlayerPage;
	/**IEventMob used in gui*/
	private IEventMob guiMob;
	/**IEventBoss used in gui*/
	private IEventBoss guiBoss;
	/**Current boss being displayed in gui*/
	private int currentBoss;
	/**Player index in MobEvents.proxy.getWorldData()*/
	private int index;
	/**Current item being rendered*/
	public ItemStack stack;

	public GuiEventBook(EntityPlayer player, boolean creative)
	{
		index = MobEvents.proxy.getWorldData().getPlayerIndex(player.getDisplayNameString());
		if (creative)
		{
			this.introPages.add("Creative");
			this.numIntroPages++;
		}
		this.editingPlayer = player;
		this.creative = creative;
		this.currentPage = creative ? MobEvents.proxy.getWorldData().currentCreativePages.get(index) : MobEvents.proxy.getWorldData().currentPages.get(index);
		this.currentTab = creative ? MobEvents.proxy.getWorldData().currentCreativeTabs.get(index) : MobEvents.proxy.getWorldData().currentTabs.get(index);
		unlockedEntities = new ArrayList<ArrayList<IEventMob>>();
		unlockedItems = new ArrayList<IEventItem>();
		maxProgress = new ArrayList<Integer>();
		randomJoke = new ArrayList<Integer>();
		if (creative)
			this.unlockedItems = ModItems.drops;
		else
		{
			for (String itemName : MobEvents.proxy.getWorldData().unlockedItems.get(index))
			{
				for (IEventItem item : ModItems.drops)
					if (itemName.equalsIgnoreCase(item.getName()))
						this.unlockedItems.add(item);
			}
		}
		for (int i=0; i<Event.allEvents.size(); i++) //iterate through events
		{  
			this.randomJoke.add(i, this.editingPlayer.worldObj.rand.nextInt(Event.allEvents.get(i).bookJokes.size()));
			ArrayList<IEventMob> entities = new ArrayList<IEventMob>();
			if (Event.allEvents.get(i).mobs != null)
			{
				if (creative)
				{
					entities.addAll(Event.allEvents.get(i).mobs);
					this.maxProgress.add(0);
					for (IEventMob mob : Event.allEvents.get(i).mobs)
						if (this.maxProgress.get(i) < mob.getProgressOnDeath() && mob.getProgressOnDeath() != 100)
							this.maxProgress.set(i, mob.getProgressOnDeath());
				}
				else
				{
					for (IEventMob mob : Event.allEvents.get(i).mobs) //iterate through mobs in event
					{
						this.maxProgress.add(0);
						for (String entity : MobEvents.proxy.getWorldData().unlockedEntities.get(index)) //iterate through unlockedEntities in world data 
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
		buttonList.add(this.buttonStartEvent = new GuiButtonStartEvent(0, w-28, h+65, 80, 20, "", this));
		buttonList.add(this.buttonWave = new GuiButtonWave(0, w-28, h+88, 80, 20, ""));
		buttonList.add(this.buttonSummonMob = new GuiButtonSummonMob(0, w-28, h+79, 80, 20, "Summon Mob"));
		buttonList.add(this.buttonGiveItem = new GuiButtonGiveItem(0, w-28, h+79, 80, 20, "Give Item"));
		buttonTabs = new ArrayList<GuiButtonTab>();
		buttonMobPages = new ArrayList<ArrayList<GuiButtonMobPage>>();
		buttonIntroPages = new ArrayList<GuiButtonIntroPage>();
		for (int i=0; i<this.numIntroPages; i++)
		{
			buttonIntroPages.add(new GuiButtonIntroPage(i, 65, this.creative ? 21*i-63 : 23*i-55, 90, 20, this.introPages.get(i), this));
			buttonList.add(buttonIntroPages.get(i));
		}
		buttonItemPages = new ArrayList<GuiButtonItemPage>();
		for (int i=0; i<ModItems.drops.size(); i++)
		{
			buttonItemPages.add(new GuiButtonItemPage(i, 87, 21*i-73, 101, 20, ModItems.drops.get(i).getName(), this));
			buttonList.add(buttonItemPages.get(i));
		}
		for (int i=0; i<this.numNonEventTabs; i++)
		{
			buttonTabs.add(new GuiButtonTab(i, w-148, h+(20*i)-30, 100, 16, TextFormatting.BOLD+this.nonEventTabs.get(i), this));
			buttonList.add(buttonTabs.get(i));
		}
		for (int i=0; i<Event.allEvents.size(); i++)
		{
			ArrayList<GuiButtonMobPage> eventMobPages = new ArrayList<GuiButtonMobPage>();
			buttonTabs.add(new GuiButtonTab(i+numNonEventTabs, w-148, h+(20*(i+numNonEventTabs))-30, 100, 16, Event.allEvents.get(i).toString(), this));
			if (Event.allEvents.get(i).mobs != null)
			{
				for (int j=0; j<Event.allEvents.get(i).mobs.size(); j++) //iterate through mobs in event
				{
					eventMobPages.add(new GuiButtonMobPage(i, 118, 20*j-95, 120, 20, ((Entity) Event.allEvents.get(i).mobs.get(j)).getName(), this, Event.allEvents.get(i).mobs.get(j)));
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
			this.buttonItemPages.get(i).enabled = creative || MobEvents.proxy.getWorldData().unlockedItems.get(index).contains(ModItems.drops.get(i).getName());
		}
		if (currentTab == 0)
			buttonNextPage.visible = currentPage < this.numIntroPages;
		else if (currentTab == 1)
			buttonNextPage.visible = currentPage < this.unlockedItems.size();//MobEvents.proxy.getWorldData().unlockedItems.get(index).size();//ModItems.drops.size();
		else
			buttonNextPage.visible = currentPage < this.unlockedEntities.get(currentTab-numNonEventTabs).size() && this.currentTab != this.buttonTabs.size()-1;
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
				this.buttonMobPages.get(i).get(j).visible = this.currentTab >= numNonEventTabs && this.currentPage == 0 && i == this.currentTab-numNonEventTabs && this.currentTab != this.buttonTabs.size()-1; 
				this.buttonMobPages.get(i).get(j).enabled = this.unlockedEntities.get(i/*-numNonEventTabs*/).contains(this.buttonMobPages.get(i).get(j).mob);
			}
		}
		this.buttonStartEvent.visible = this.creative && this.currentTab > numNonEventTabs-1 && this.currentPage == 0;
		this.buttonWave.visible = this.creative && this.currentTab > numNonEventTabs-1 && this.currentPage == 0 && Event.allEvents.get(this.currentTab-this.numNonEventTabs).getClass() == MobEvents.proxy.getWorldData().currentEvent.getClass();
		this.buttonSummonMob.visible = this.creative && this.currentTab > numNonEventTabs-1 && this.currentPage > 0;
		this.buttonGiveItem.visible = this.creative && this.currentTab < numNonEventTabs && this.nonEventTabs.get(this.currentTab).equals("Items") && this.currentPage > 0;
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return true;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		this.displayTicks++;
		/**Used to draw buttons before drop tooltips*/
		boolean skipButtonDraw = false;
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
			//so mob does not refresh
			if (this.guiMob == null || this.guiMob.getClass() != this.unlockedEntities.get(currentTab-numNonEventTabs).get(currentPage-1).getClass())
				this.guiMob = this.unlockedEntities.get(currentTab-numNonEventTabs).get(currentPage-1);
			//portrait background
			if (this.guiMob.getProgressOnDeath() == 100)
				GlStateManager.color(0.7F, 0.0F, 0.7F, 1.0F);				
			else
			{
				float difficulty = (float)(this.guiMob.getProgressOnDeath()-1)/(this.maxProgress.get(currentTab-numNonEventTabs)-1);
				GlStateManager.color(255F, 1F-difficulty, 1F-difficulty, 1.0F);
			}
			GlStateManager.pushMatrix();
			scale = 1.8f;
			GlStateManager.translate(w - 100, h, 0);
			GlStateManager.scale(scale, scale, scale);
			this.drawTexturedModalRect(103, -17, 146, 6, 58, 77);
			GlStateManager.popMatrix();
			//mob portrait
			EntityLiving entity = (EntityLiving) this.guiMob;
			((IEventMob) entity).doSpecialRender(this.displayTicks);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.pushMatrix();
			scale = 40f;
			if (entity instanceof EntityBossSpawner)
			{
				//rescale
				if (entity instanceof EntityZombieBossSpawner)
				{
					scale = 21f;
					GlStateManager.translate(110, 60, 0);
				}
				//display different bosses
				if (this.displayTicks % 90 == 0)
				{
					if (++currentBoss >= ((EntityBossSpawner)entity).bossesToSummon.size())
						currentBoss = 0;
					this.guiBoss = (IEventBoss) ((EntityBossSpawner)entity).bossesToSummon.get(currentBoss);
				}
				else if (this.guiBoss == null)
					this.guiBoss = (IEventBoss) ((EntityBossSpawner)entity).bossesToSummon.get(0);
				entity = (EntityLiving) this.guiBoss;
			}
			GlStateManager.translate(w - 100, h, 0);
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.rotate(180F, 0F, 0F, 1F);
			GlStateManager.rotate(135.0F, 0.0F, 1, 0.0f);
			RenderHelper.enableStandardItemLighting();
			GlStateManager.rotate(-165.0F, 0.0F, 1, -0.0f);
			GlStateManager.rotate(-10.0F, -1F, 0F, 0.5f);
			entity.rotationYawHead = 0.0F;
			entity.renderYawOffset = 0.0F;
			entity.rotationYaw = 0.0F;
			entity.prevRotationYawHead = 0.0F;
			entity.prevRenderYawOffset = 0.0F;
			entity.prevRotationYaw = 0.0F;
			this.partialTicks += 0.3F;
			mc.getRenderManager().setPlayerViewY(-20f);
			if (entity.worldObj == null)
				entity.worldObj = this.editingPlayer.worldObj;
			if (entity instanceof EntityZombieRider) {
				entity.hurtTime = 0;
				((EntityLiving)entity.getRidingEntity()).rotationYawHead = 0.0F;
				((EntityLiving)entity.getRidingEntity()).renderYawOffset = 0.0F;
				((EntityLiving)entity.getRidingEntity()).rotationYaw = 0.0F;
				((EntityLiving)entity.getRidingEntity()).prevRotationYawHead = 0.0F;
				((EntityLiving)entity.getRidingEntity()).prevRenderYawOffset = 0.0F;
				((EntityLiving)entity.getRidingEntity()).prevRotationYaw = 0.0F;
				mc.getRenderManager().doRenderEntity(entity, -4D, -1.3D, 5.0D, 0.0F, this.partialTicks, true);
				mc.getRenderManager().doRenderEntity(entity.getRidingEntity(), -4D, -2D, 5.0D, 0.0F, this.partialTicks, true);
			}
			else
				mc.getRenderManager().doRenderEntity(entity, -4D, -1.5D, 5.0D, 0.0F, this.partialTicks, true);
			RenderHelper.disableStandardItemLighting();
			this.mc.entityRenderer.disableLightmap();
			GlStateManager.popMatrix();
			//Mob details
			int color = 0;
			if (guiMob.getProgressOnDeath() == 100)
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
				float difficulty = (float)(guiMob.getProgressOnDeath()-1)/(this.maxProgress.get(currentTab-numNonEventTabs)-1);
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
			String title = ((Entity) guiMob).getName();
			/**Full hearts*/
			double health = (((EntityLivingBase)guiMob).getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue())/2;
			int healthOffset = (int) h+11;
			/**Full armor*/
			double armor = getArmorValue((EntityLiving) guiMob);//((EntityLiving) guiMob).getTotalArmorValue()/2;
			int armorOffset = (int) (healthOffset + 11*(health > 20 ? 1 : Math.ceil(health/10d)));
			/**Hearts per hit on normal mode*/
			double damage = ((((EntityLivingBase)guiMob).getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue()+mobWeaponDamage(guiMob)))/2;
			int damageOffset = (int) (armorOffset + 11*(armor > 20 ? 1 : Math.ceil(armor/10d)));
			int dropsOffset = (int) (damageOffset + 11*(damage > 20 ? 1 : Math.ceil(damage/10d)));
			float x = w+3-this.fontRendererObj.getStringWidth(title)/2;
			float y = h-30;
			this.mc.fontRendererObj.drawString(TextFormatting.BOLD+title, x, y, color, true);
			GlStateManager.pushMatrix();
			scale = 0.781f;
			GlStateManager.translate(w - 100, h, 0);
			GlStateManager.scale(scale, scale, scale);
			//text
			this.mc.fontRendererObj.drawSplitString(TextFormatting.BOLD.toString()+TextFormatting.ITALIC+guiMob.getBookDescription(), 73, -26, 145, 0);
			if (guiMob instanceof EntityBossSpawner)
				dropsOffset = (int) (h+2);
			else 
			{
				this.mc.fontRendererObj.drawString(TextFormatting.BOLD+"Difficulty: "+guiMob.getProgressOnDeath(), 73, 2, 0, false);
				this.mc.fontRendererObj.drawString(TextFormatting.BOLD+"Health: "+(health>20 ? health+" x" : ""), 73, healthOffset-h+6, 0, false);
				this.mc.fontRendererObj.drawString(TextFormatting.BOLD+"Armor: "+(armor>20 ? armor+" x" : ""), 73, armorOffset/scale-h/scale+2, 0, false);
				this.mc.fontRendererObj.drawString(TextFormatting.BOLD+"Damage: "+(damage>20 ? damage+" x" : ""), 73, damageOffset/scale-h/scale+2, 0, false);
			}
			this.mc.fontRendererObj.drawString(TextFormatting.BOLD+"Drops: ", 73, dropsOffset/scale-h/scale+2, 0, false);
			RenderHelper.disableStandardItemLighting();
			GlStateManager.popMatrix();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			mc.getTextureManager().bindTexture(bookPageTexture);
			if (!(guiMob instanceof EntityBossSpawner))
			{
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
			}
			//draw buttons
			for (int k = 0; k < this.buttonList.size(); ++k)
				((GuiButton)this.buttonList.get(k)).drawButton(this.mc, mouseX, mouseY);
			for (int j = 0; j < this.labelList.size(); ++j)
				((GuiLabel)this.labelList.get(j)).drawLabel(this.mc, mouseX, mouseY);
			skipButtonDraw = true;
			GlStateManager.pushMatrix();
			scale = 0.781f;
			GlStateManager.translate(w - 100, h, 0);
			GlStateManager.scale(scale, scale, scale);
			//drops
			ArrayList<ItemStack> drops = new ArrayList<ItemStack>();//get rid of duplicates
			ArrayList<Double> dropChances = new ArrayList<Double>();//drop chances
			if (guiMob.getBookDrops() != null)
			{
				//get rid of duplicates
				for (ItemStack stack1 : guiMob.getBookDrops())
				{
					boolean add = true;
					for (ItemStack stack2 : drops)
					{
						if (stack1.getItem() == stack2.getItem() && (stack1.getItem() == Item.getItemFromBlock(Blocks.AIR) || stack1.getMetadata() == stack2.getMetadata()))
							add = false;
					}
					if (add) 
						drops.add(stack1.copy());
				}
				//calculate drop chances
				for (ItemStack stack1 : drops)
				{
					double chance = 0;
					for (ItemStack stack2 : guiMob.getBookDrops())
					{
						if (stack1.getItem() == stack2.getItem() && (stack1.getItem() == Item.getItemFromBlock(Blocks.AIR) || stack1.getMetadata() == stack2.getMetadata()))
							chance++;//# occurrences
					}
					chance /= guiMob.getBookDrops().size();
					int i = EnchantmentHelper.getLootingModifier(mc.thePlayer);
					if (guiMob instanceof EntityBossSpawner)
					{
						chance = 1-Math.pow(1-chance, ((EntityBossSpawner) guiMob).getNumberOfDrops());
						chance *= 100;
					}
					else
						chance *= (double)100/(20 + i * 5);
					chance = Math.round((chance / 10 * 10) * 10) / 10.0;
					dropChances.add(chance); 
				}
				//add cloned items
				if (guiMob instanceof EntityZombieClone || guiMob instanceof EntitySkeletonClone) {
					for (int i=0; i<EntityEquipmentSlot.values().length; i++) {
						if (((EntityLiving)guiMob).getItemStackFromSlot(EntityEquipmentSlot.values()[i]) != null)
						{
							drops.add(((EntityLiving)guiMob).getItemStackFromSlot(EntityEquipmentSlot.values()[i]));
							dropChances.add(5.0);
						}
					}
				}
				//sort by drop chance
				ArrayList<ItemStack> tmpDrops= drops;
				ArrayList<Double> tmpDropChances = dropChances;
				drops = new ArrayList<ItemStack>();
				dropChances = new ArrayList<Double>();
				while (!tmpDrops.isEmpty())
				{
					double highestChance = 0;
					for (double chance : tmpDropChances)
						if (chance > highestChance)
							highestChance = chance;
					int index = tmpDropChances.indexOf(highestChance);
					drops.add(tmpDrops.remove(index));
					dropChances.add(tmpDropChances.remove(index));
				}
				//remove air
				for (int i=0; i<drops.size(); i++)
				{
					if (drops.get(i).getItem() == Item.getItemFromBlock(Blocks.AIR))
					{
						drops.remove(i);
						dropChances.remove(i);
						break;
					}
				}
				int itemsPerRow = 6;
				for (int i=0; i<drops.size(); i++)//max items to display = 6                  
				{//min 110, max 200
					int row = i / itemsPerRow;
					int spaceBetween = 125/(Math.min(drops.size(), itemsPerRow)+1);
					int xPos = 96 + (i+1)*spaceBetween-row*102;
					int yPos = (int) (dropsOffset/scale-h/scale-2+row*16);
					RenderHelper.enableGUIStandardItemLighting();
					this.itemRender.renderItemAndEffectIntoGUI(drops.get(i), xPos, yPos);
					if (drops.get(i).stackSize > 1)
						this.itemRender.renderItemOverlayIntoGUI(fontRendererObj, drops.get(i), xPos, yPos, String.valueOf(drops.get(i).stackSize));
				}
				for (int i=0; i<drops.size(); i++)                  
				{
					int row = i / itemsPerRow;
					int spaceBetween = 125/(Math.min(drops.size(), itemsPerRow)+1);
					int xPos = 96 + (i+1)*spaceBetween-row*102;
					int yPos = (int) (dropsOffset/scale-h/scale-2+row*16);
					int mX = (int) ((mouseX-w+100)/scale);
					int mY = (int) ((mouseY-h)/scale);
					if (mX >= xPos && mY >= yPos && mX < xPos + 16 && mY < yPos + 16) {
						List<String> tooltip = drops.get(i).getTooltip(this.editingPlayer, false);
						tooltip.add(TextFormatting.DARK_PURPLE+""+TextFormatting.BOLD+dropChances.get(i)+"% drop chance");
						this.drawHoveringText(tooltip, mX, mY);
						RenderHelper.disableStandardItemLighting();
					}
				}
				GlStateManager.popMatrix();
			}
		}
		//Event text
		if (this.currentTab > numNonEventTabs-1 && this.currentPage == 0)
		{
			String occurs;
			switch(Event.allEvents.get(this.currentTab-numNonEventTabs).occurs) {
			case CREATIVE:
				occurs = "Creative";
				break;
			case DAY:
				occurs = "Day";
				break;
			case NEVER:
				occurs = "Never";
				break;
			case NIGHT:
				occurs = "Night";
				break;
			default:
				occurs = "???";
				break;
			}
			String title = Event.allEvents.get(this.currentTab-numNonEventTabs).toString();
			if (title.contains(Event.CHAOTIC_TURMOIL.toString()))
				title = Event.CHAOTIC_TURMOIL.changingName;
			float x = w+10-this.fontRendererObj.getStringWidth(title)/2;
			float y = h-30;
			this.mc.fontRendererObj.drawString(title, x+1, y, 0, true);
			this.mc.fontRendererObj.drawString(title, x-1, y, 0, true);
			this.mc.fontRendererObj.drawString(title, x, y+1, 0, true);
			this.mc.fontRendererObj.drawString(title, x, y-1, 0, true);
			this.mc.fontRendererObj.drawString(title, x, y, Event.allEvents.get(this.currentTab-numNonEventTabs).color, false);
			this.mc.fontRendererObj.drawSplitString(TextFormatting.ITALIC+Event.allEvents.get(this.currentTab-numNonEventTabs).bookJokes.get(this.randomJoke.get(this.currentTab-numNonEventTabs)), (int)w-38, (int)h-16, 106, 0);
			this.mc.fontRendererObj.drawString(TextFormatting.BOLD+"Occurs: "+TextFormatting.RESET+occurs, w-38, h+39, 0, false);
			this.mc.fontRendererObj.drawString(TextFormatting.BOLD+"Waves: "+TextFormatting.RESET+Event.allEvents.get(this.currentTab-numNonEventTabs).bookWaves, w-38, h+54, 0, false);
		}

		//TODO Introduction
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
				this.mc.fontRendererObj.drawString(TextFormatting.BOLD+title, x, y, 0x595959, true);
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
				String text1 = "Events typically begin and end with the day/night cycle.\n\n"
						+ "Events currently have a "+MobEvents.proxy.getWorldData().eventChance+"% chance of occurring at the start of each day/night cycle   (can be changed in config).";
				String text2 = "Events have a certain number of waves and then conclude with a boss.\n\n"
						+ "";
				float x = w+3-this.fontRendererObj.getStringWidth(title)/2;
				float y = h-30;
				this.mc.fontRendererObj.drawString(TextFormatting.BOLD+title, x, y, 0x595959, true);
				this.mc.fontRendererObj.drawSplitString(text1, (int)w-42, (int)h-20, 110, 0);
				this.mc.fontRendererObj.drawSplitString(text2, (int)w+85, (int)h-20, 110, 0);
			}
			else if (this.currentPage == 2)
			{
				String title = "Commands";
				String text1 = "All commands work with tab completion.\n\n"
						+ TextFormatting.BOLD+"/MobEvents moveGui <Position>"+TextFormatting.RESET+" sets Event gui position.\n\n";
				String text2 = TextFormatting.BOLD+"/MobEvents setEvent <Event>"+TextFormatting.RESET+" set the current event.\n\n"
						+ TextFormatting.BOLD+"/MobEvents setWave <0-4>"+TextFormatting.RESET+" sets the current wave.\n\n";
				float x = w+3-this.fontRendererObj.getStringWidth(title)/2;
				float y = h-30;
				this.mc.fontRendererObj.drawString(TextFormatting.BOLD+title, x, y, 0x595959, true);
				this.mc.fontRendererObj.drawSplitString(text1, (int)w-42, (int)h-20, 110, 0);
				this.mc.fontRendererObj.drawSplitString(text2, (int)w+85, (int)h-20, 110, 0);
			}
			else if (this.currentPage == 3)
			{
				String title = "Waves";
				String text1 = "Each Event has a certain number of waves that progress as monsters are killed.\n\n"
						+ "More difficult monsters spawn more frequently during later waves and advance the wave more when killed.";
				String text2 = "During the last wave of an Event, a special boss monster will spawn.\n\n"
						+ "Surviving though all of an Event's waves and boss can be very difficult, but rewarding.";
				float x = w+3-this.fontRendererObj.getStringWidth(title)/2;
				float y = h-30;
				this.mc.fontRendererObj.drawString(TextFormatting.BOLD+title, x, y, 0x595959, true);
				this.mc.fontRendererObj.drawSplitString(text1, (int)w-42, (int)h-20, 110, 0);
				this.mc.fontRendererObj.drawSplitString(text2, (int)w+85, (int)h-20, 110, 0);
			}
			else if (this.currentPage == 4)
			{
				String title = "Monsters";
				String text1 = "During each Event, many monsters will spawn; each with their own ability, armor, weapon, and drops.\n\n"
						+ "These monsters vary in difficulty, with more difficult ones spawning more often in later waves.\n\n";
				String text2 = "More difficult monsters progress the waves faster and drop better items.\n\n"
						+ "When the Event has ended, all of these monsters will disappear on their own.";
				float x = w+3-this.fontRendererObj.getStringWidth(title)/2;
				float y = h-30;
				this.mc.fontRendererObj.drawString(TextFormatting.BOLD+title, x, y, 0x595959, true);
				this.mc.fontRendererObj.drawSplitString(text1, (int)w-42, (int)h-20, 110, 0);
				this.mc.fontRendererObj.drawSplitString(text2, (int)w+85, (int)h-20, 110, 0);
			}
			else if (this.currentPage == 5)
			{
				String title = "Bosses";
				String text1 = "During the boss wave of an event, all event mobs stop spawning.\n\n"
						+ "A beacon will guide you to the boss and, if you defeat it before the event ends, a great reward.\n\n"
						+ "Each event boss has its own mechanics and rewards.\n\n";
				String text2 = "";
				float x = w+3-this.fontRendererObj.getStringWidth(title)/2;
				float y = h-30;
				this.mc.fontRendererObj.drawString(TextFormatting.BOLD+title, x, y, 0x595959, true);
				this.mc.fontRendererObj.drawSplitString(text1, (int)w-42, (int)h-20, 110, 0);
				this.mc.fontRendererObj.drawSplitString(text2, (int)w+85, (int)h-20, 110, 0);
				//beacon image
				mc.getTextureManager().bindTexture(bookPageTexture);
				if (MobEvents.proxy.getWorldData().currentEvent.getClass() != Event.class)
					GlStateManager.color(EventFogEvent.currentColors[0], EventFogEvent.currentColors[1], EventFogEvent.currentColors[2]);				
				else
					GlStateManager.color(1f, 1f, 1f);
				GlStateManager.pushMatrix();
				scale = 1.9f;
				GlStateManager.translate(w - 100, h, 0);
				GlStateManager.scale(scale, scale, scale);
				this.drawTexturedModalRect(96, -18, 50, 110, 56, 75);
				GlStateManager.popMatrix();
			}
			else if (this.currentPage == 6)
			{
				String title = "Creative";
				String text1 = "This Creative Event Book can only be obtained and used in creative mode.\n\n"
						+ "It has all tabs, items, and monsters unlocked.";
				String text2 = "This book has buttons that allow you to start and stop Events, change waves, summon monsters, and spawn in items.\n\n"
						+ "These buttons are only visible in this Creative Event Book and have a purple glowing effect.";
				float x = w+3-this.fontRendererObj.getStringWidth(title)/2;
				float y = h-30;
				this.mc.fontRendererObj.drawString(TextFormatting.BOLD+title, x, y, 0x990099, true);
				this.mc.fontRendererObj.drawSplitString(text1, (int)w-42, (int)h-20, 110, 0);
				this.mc.fontRendererObj.drawSplitString(text2, (int)w+85, (int)h-20, 110, 0);
			}
		}
		else if (currentTab == 1)//TODO Items
		{
			if (this.currentPage == 0)
			{
				String title = "Items";
				String text = "This tab contains information on all custom items dropped by Event monsters.\n\n"
						+ "Each item must be obtained for its page to be unlocked.";
				float x = w+3-this.fontRendererObj.getStringWidth(title)/2;
				float y = h-30;
				this.mc.fontRendererObj.drawString(TextFormatting.BOLD+title, x, y, 0x595959, true);
				this.mc.fontRendererObj.drawSplitString(text, (int)w-42, (int)h-20, 110, 0);
			}
			else
			{
				IEventItem item = this.unlockedItems.get(this.currentPage-1);
				stack = item.getItemStack();
				String title = item.getName();
				float x = w+3-this.fontRendererObj.getStringWidth(title)/2;
				float y = h-30;
				this.mc.fontRendererObj.drawString(TextFormatting.BOLD+title, x, y, item.getColor(), true);
				//dropped by
				this.mc.fontRendererObj.drawString(TextFormatting.BOLD+"Dropped by:", w-38, h+50, 0, false);
				if (item.droppedBy().size() == 1)
					this.mc.fontRendererObj.drawString(item.droppedBy().get(0), w-38, h+65, 0, false);
				//player portrait
				//if no player or player is geared for different page - reset guiPlayer
				if (this.guiPlayer == null || this.guiPlayerPage != this.currentPage)
				{
					guiPlayer = new EntityGuiPlayer(mc.theWorld, mc.thePlayer.getGameProfile(), mc.thePlayer, this);
					guiPlayerPage = this.currentPage;
					EntityEquipmentSlot slot = EntityEquipmentSlot.MAINHAND; 
					for (int i=0; i<EntityEquipmentSlot.values().length; i++)
						if (((Item)item).isValidArmor(stack, EntityEquipmentSlot.values()[i], guiPlayer)) {
							slot = EntityEquipmentSlot.values()[i];
							break;
						}
					guiPlayer.setItemStackToSlot(slot, stack);
				}
				guiPlayer.doSpecialRender();
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
				guiPlayer.rotationYawHead = 0.0F;
				guiPlayer.renderYawOffset = 0.0F;
				this.partialTicks += 0.3F;
				mc.getRenderManager().setPlayerViewY(-20f);
				mc.getRenderManager().doRenderEntity(guiPlayer, -4D, -1.5D, 5.0D, 0.0F, this.partialTicks, true);
				RenderHelper.disableStandardItemLighting();
				this.mc.entityRenderer.disableLightmap();
				GlStateManager.popMatrix();
				//render tooltip (must be before render item bc cleaver.getTooltip)
				GlStateManager.pushMatrix();
				scale = 0.75f;
				GlStateManager.translate(w - 100, h, 0);
				GlStateManager.scale(scale, scale, scale);
				int length = 0;
				for (String string : stack.getTooltip(this.guiPlayer, false))
					if (this.fontRendererObj.getStringWidth(string) > length)
						length = this.fontRendererObj.getStringWidth(string);
				this.drawHoveringText(stack.getTooltip(this.guiPlayer, false), 137-length/2, 32);
				GlStateManager.popMatrix();
				RenderHelper.disableStandardItemLighting();
				//render item
				RenderHelper.enableStandardItemLighting();
				GlStateManager.pushMatrix();
				scale = 2f;
				GlStateManager.translate(w - 100, h, 0);
				GlStateManager.scale(scale, scale, scale);
				this.itemRender.renderItemAndEffectIntoGUI(stack, 47, -10);
				GlStateManager.popMatrix();
				//portrait background
				mc.getTextureManager().bindTexture(bookPageTexture);
				GlStateManager.color(item.getRed(), item.getGreen(), item.getBlue(), 1.0F);				
				GlStateManager.pushMatrix();
				scale = 1.8f;
				GlStateManager.translate(w - 100, h, 0);
				GlStateManager.scale(scale, scale, scale);
				this.drawTexturedModalRect(103, -17, 146, 6, 58, 77);
				GlStateManager.popMatrix();
			}
		}
		if (!skipButtonDraw)
			super.drawScreen(mouseX, mouseY, partialTicks);
	}

	private static double getArmorValue(EntityLiving entity) {
		EntityGuiPlayer player = new EntityGuiPlayer(Minecraft.getMinecraft().theWorld, Minecraft.getMinecraft().thePlayer.getGameProfile(), null, null);
		for (int i=0; i<EntityEquipmentSlot.values().length; i++)
			player.setItemStackToSlot(EntityEquipmentSlot.values()[i], entity.getItemStackFromSlot(EntityEquipmentSlot.values()[i]));
		return Math.max(ForgeHooks.getTotalArmorValue(player) / 2D, 0.5D);
	}

	private float mobWeaponDamage(IEventMob mob)
	{
		ItemStack stack = ((EntityLiving) mob).getHeldItemMainhand();
		if (stack != null)
		{
			if (stack.getItem() instanceof ItemTool)
			{
				try 
				{
					return ReflectionHelper.getPrivateValue(ItemTool.class, (ItemTool)stack.getItem(), 2);//ItemTool.class.getDeclaredField("damageVsEntity");
				} 
				catch (Exception e) { 
					e.printStackTrace();
				}
			}
			else if (stack.getItem() instanceof ItemSword)
			{
				try 
				{
					float attackDamage = ReflectionHelper.getPrivateValue(ItemSword.class, (ItemSword)stack.getItem(), 0);//ItemSword.class.getDeclaredField("attackDamage");
					return attackDamage + (float)EnchantmentHelper.getModifierForCreature(stack, EnumCreatureAttribute.UNDEFINED);
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
			//safety check
			if (this.currentTab > this.numNonEventTabs-1 && this.currentPage >= this.unlockedEntities.get(currentTab-numNonEventTabs).size())
				return;
			if (creative)
				MobEvents.network.sendToServer(new PacketSetCurrentPagesAndTabs(MobEvents.proxy.getWorldData().currentPages.get(index), MobEvents.proxy.getWorldData().currentTabs.get(index), MobEvents.proxy.getWorldData().currentCreativePages.get(index)+1, MobEvents.proxy.getWorldData().currentCreativeTabs.get(index)));
			else
				MobEvents.network.sendToServer(new PacketSetCurrentPagesAndTabs(MobEvents.proxy.getWorldData().currentPages.get(index)+1, MobEvents.proxy.getWorldData().currentTabs.get(index), MobEvents.proxy.getWorldData().currentCreativePages.get(index), MobEvents.proxy.getWorldData().currentCreativeTabs.get(index)));
			this.currentPage++;
		}
		else if (button == this.buttonPreviousPage)
		{
			if (creative)
				MobEvents.network.sendToServer(new PacketSetCurrentPagesAndTabs(MobEvents.proxy.getWorldData().currentPages.get(index), MobEvents.proxy.getWorldData().currentTabs.get(index), MobEvents.proxy.getWorldData().currentCreativePages.get(index)-1, MobEvents.proxy.getWorldData().currentCreativeTabs.get(index)));
			else
				MobEvents.network.sendToServer(new PacketSetCurrentPagesAndTabs(MobEvents.proxy.getWorldData().currentPages.get(index)-1, MobEvents.proxy.getWorldData().currentTabs.get(index), MobEvents.proxy.getWorldData().currentCreativePages.get(index), MobEvents.proxy.getWorldData().currentCreativeTabs.get(index)));
			this.currentPage--;
		}
		else if (button instanceof GuiButtonTab)
		{
			for (int i=0; i<this.buttonTabs.size(); i++)
				if (this.buttonTabs.get(i) == button && (i < numNonEventTabs || MobEvents.proxy.getWorldData().unlockedTabs.get(index).contains(Event.allEvents.get(buttonTabs.get(i).id-numNonEventTabs).toString()) || creative))
				{
					if (creative)
						MobEvents.network.sendToServer(new PacketSetCurrentPagesAndTabs(MobEvents.proxy.getWorldData().currentPages.get(index), MobEvents.proxy.getWorldData().currentTabs.get(index), 0, i));
					else
						MobEvents.network.sendToServer(new PacketSetCurrentPagesAndTabs(0, i, MobEvents.proxy.getWorldData().currentCreativePages.get(index), MobEvents.proxy.getWorldData().currentCreativeTabs.get(index)));
					this.currentTab = i;
					this.currentPage = 0;
					break;
				}
		}
		else if (button instanceof GuiButtonMobPage)
		{
			if (((GuiButtonMobPage)button).enabled && !this.buttonNextPage.isMouseOver())
			{
				for (int i=0; i<this.unlockedEntities.get(currentTab-numNonEventTabs).size(); i++) //iterate through unlocked mobs
				{
					if (this.unlockedEntities.get(currentTab-numNonEventTabs).get(i).equals(((GuiButtonMobPage) button).mob))
					{
						if (creative)
							MobEvents.network.sendToServer(new PacketSetCurrentPagesAndTabs(MobEvents.proxy.getWorldData().currentPages.get(index), MobEvents.proxy.getWorldData().currentTabs.get(index), i+1, MobEvents.proxy.getWorldData().currentCreativeTabs.get(index)));
						else
							MobEvents.network.sendToServer(new PacketSetCurrentPagesAndTabs(i+1, MobEvents.proxy.getWorldData().currentTabs.get(index), MobEvents.proxy.getWorldData().currentCreativePages.get(index), MobEvents.proxy.getWorldData().currentCreativeTabs.get(index)));
						this.currentPage = i+1;
						break;
					}
				}
			}
		}
		else if (button instanceof GuiButtonStartEvent)
		{
			if (MobEvents.proxy.getWorldData().currentEvent.getClass() != Event.class)
			{
				this.editingPlayer.addChatMessage(new TextComponentTranslation("Stopped "+MobEvents.proxy.getWorldData().currentEvent.toString()+" Event.").setStyle(new Style().setItalic(true).setColor(TextFormatting.DARK_PURPLE)));
				MobEvents.network.sendToServer(new PacketSetEvent(Event.EVENT.toString()));
			}
			if (button.displayString.equals("Start Event"))
			{
				this.editingPlayer.addChatMessage(new TextComponentTranslation("Started "+Event.allEvents.get(this.currentTab-numNonEventTabs).toString()+" Event.").setStyle(new Style().setItalic(true).setColor(TextFormatting.DARK_PURPLE)));
				MobEvents.network.sendToServer(new PacketSetEvent(Event.allEvents.get(this.currentTab-numNonEventTabs).toString()));
			}
		}
		else if (button instanceof GuiButtonWave)
		{
			if (MobEvents.proxy.getWorldData().currentWave + 1 > 4) 
				MobEvents.network.sendToServer(new PacketSetWave(0));
			else 
				MobEvents.network.sendToServer(new PacketSetWave(MobEvents.proxy.getWorldData().currentWave+1));
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
						MobEvents.network.sendToServer(new PacketSetCurrentPagesAndTabs(MobEvents.proxy.getWorldData().currentPages.get(index), MobEvents.proxy.getWorldData().currentTabs.get(index), i+1, MobEvents.proxy.getWorldData().currentCreativeTabs.get(index)));
					else
						MobEvents.network.sendToServer(new PacketSetCurrentPagesAndTabs(i+1, MobEvents.proxy.getWorldData().currentTabs.get(index), MobEvents.proxy.getWorldData().currentCreativePages.get(index), MobEvents.proxy.getWorldData().currentCreativeTabs.get(index)));
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
						MobEvents.network.sendToServer(new PacketSetCurrentPagesAndTabs(MobEvents.proxy.getWorldData().currentPages.get(index), MobEvents.proxy.getWorldData().currentTabs.get(index), i+1, MobEvents.proxy.getWorldData().currentCreativeTabs.get(index)));
					else
						MobEvents.network.sendToServer(new PacketSetCurrentPagesAndTabs(i+1, MobEvents.proxy.getWorldData().currentTabs.get(index), MobEvents.proxy.getWorldData().currentCreativePages.get(index), MobEvents.proxy.getWorldData().currentCreativeTabs.get(index)));
					this.currentPage = i+1;
					break;
				}
			}
		}
	}
}
