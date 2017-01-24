package furgl.mobEvents.common.item.drops;

import java.util.ArrayList;
import java.util.List;

import furgl.mobEvents.common.Events.Event;
import furgl.mobEvents.common.entity.EntityGuiPlayer;
import furgl.mobEvents.common.world.WorldData;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBookOfHealing extends Item implements IEventItem
{	
	/**Player doesn't have enough xp*/
	private boolean noXp;
	private int cooldown;

	public ItemBookOfHealing() {
		this.maxStackSize = 1;
		this.setMaxDamage(300);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
	{
		tooltip.set(0, TextFormatting.AQUA+tooltip.get(0));
		int index = WorldData.get(player.worldObj).getPlayerIndex(player.getDisplayNameString());
		if (WorldData.get(player.worldObj).unlockedItems.get(index).contains(this.getName()) || player.capabilities.isCreativeMode) {
			tooltip.add(TextFormatting.GOLD+"Uses experience to heal when held");
			if (!GuiScreen.isShiftKeyDown())
				tooltip.add(TextFormatting.GRAY+"Hold "+TextFormatting.AQUA+"SHIFT"+TextFormatting.GRAY+" for details");
			else
			{
				tooltip.add(TextFormatting.BLUE+""+"Shift + Right Click: "+ (this.isActive(stack) ? "Deactivate" : "Activate"));
				tooltip.add(TextFormatting.DARK_AQUA+""+"Gives Regen when low health");
				tooltip.add("");				
				tooltip.add(TextFormatting.BLUE+""+"Hold Right Click: Quick Heal");
				tooltip.add(TextFormatting.DARK_AQUA+""+"Gives health and Regen III");
			}
			tooltip.add("Regeneration");
		}
		else 
			tooltip.add(TextFormatting.ITALIC+""+TextFormatting.GOLD+"???");
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack)
	{
		return super.getItemStackDisplayName(stack) + (stack.getTagCompound() != null && !stack.getTagCompound().hasKey("Active") ? "" : (this.isActive(stack) ? " (Active)" : " (Inactive)"));
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack)
	{
		return EnumAction.BOW;
	}

	public void setActive(ItemStack stack, boolean active)
	{
		if(!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());

		NBTTagCompound nbt = stack.getTagCompound();
		nbt.setBoolean("Active", active);
	}

	private boolean isActive(ItemStack stack) 
	{
		if(!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());

		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt.hasKey("Active"))
			return nbt.getBoolean("Active");
		else
			return false;
	}

	private boolean giveRegen(EntityLivingBase entity, int level, int time)
	{
		//if player
		if (entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) entity;
			//subtract xp
			if (!player.capabilities.isCreativeMode)
			{
				int amount = level * 5;
				//calculate total xp based on level and xp bar progress
				if (player.experienceLevel < 17)
					player.experienceTotal = (int) (Math.pow(player.experienceLevel, 2)+6*player.experienceLevel);
				else if (player.experienceLevel < 32)
					player.experienceTotal = (int) (2.5D*Math.pow(player.experienceLevel, 2)-40.5D*player.experienceLevel+360);
				else
					player.experienceTotal = (int) (4.5D*Math.pow(player.experienceLevel, 2)-162.5D*player.experienceLevel+2220);
				player.experienceTotal += player.experience * player.xpBarCap();
				//cancel if total xp too low
				if (player.experienceTotal - amount < 0)
					return false;
				else if (entity.getActivePotionEffect(MobEffects.REGENERATION) != null && entity.getActivePotionEffect(MobEffects.REGENERATION).getDuration() > 0 && entity.getActivePotionEffect(MobEffects.REGENERATION).getAmplifier() >= level)
					return true;
				//calculate new level and xp bar progress
				int j = Integer.MIN_VALUE + player.experienceTotal;
				if (amount < j)
					amount = j;
				//subtract amount
				player.experienceTotal -= amount;
				double exactLevel;
				if (player.experienceLevel < 17)
					exactLevel = Math.sqrt(player.experienceTotal+9)-3;
				else if (player.experienceLevel < 32)
					exactLevel = 0.1D*(Math.sqrt(40*player.experienceTotal-7839)+81);
				else
					exactLevel = 1D/18D*(Math.sqrt(72*player.experienceTotal-54215)+325);
				player.experienceLevel = (int) Math.floor(exactLevel);
				player.experience = (float) (exactLevel - player.experienceLevel);
			}
			player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, time, level-1));
			return true;
		}
		//if mob
		else if (!(entity instanceof FakePlayer))
		{
			entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, time, level-1));
			return true;
		}
		return false;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack)
	{
		return 72000;
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase entity, int count) //count starts at maxItemUseDuration and counts down
	{
		if (!entity.isSneaking() && cooldown <= 0)
		{
			float max = this.getMaxItemUseDuration(stack)-40f;
			if (count <= max)
			{
				if (!entity.worldObj.isRemote && this.giveRegen(entity, 3, 100))
				{
					stack.attemptDamageItem(2, entity.worldObj.rand);
					entity.addPotionEffect(new PotionEffect(MobEffects.INSTANT_HEALTH, 1));
					for (int i=0; i<5; i++)
						entity.worldObj.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.PLAYERS, 1.0f, entity.worldObj.rand.nextFloat()+0.5f);
					for (int i=0; i<100; i++)
						entity.worldObj.spawnParticle(EnumParticleTypes.SPELL_INSTANT, entity.posX+entity.worldObj.rand.nextDouble()-0.5D, entity.posY+entity.worldObj.rand.nextDouble()*1.5D, entity.posZ+entity.worldObj.rand.nextDouble()-0.5D, entity.worldObj.rand.nextDouble()-0.5D, (entity.worldObj.rand.nextDouble()), entity.worldObj.rand.nextDouble()-0.5D, 0);
					if (!entity.worldObj.isRemote)
						this.cooldown = 200;
				}
				else
					entity.worldObj.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 1.0f, 1.0f);
				entity.stopActiveHand();
			}
			//build-up sound
			if (count % 5 == 0 && count < this.getMaxItemUseDuration(stack)-8)
				entity.worldObj.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_BOBBER_SPLASH, SoundCategory.PLAYERS, (float)(this.getMaxItemUseDuration(stack)-count)/(this.getMaxItemUseDuration(stack)-max)*0.5f, (float)(this.getMaxItemUseDuration(stack)-count)/(this.getMaxItemUseDuration(stack)-max));
			//particles
			if (entity.worldObj.isRemote)
				for (int i=0; i<(this.getMaxItemUseDuration(stack)-count)/3; i++)
					entity.worldObj.spawnParticle(EnumParticleTypes.SPELL_WITCH, entity.posX+entity.worldObj.rand.nextDouble()-0.5D, entity.posY+entity.worldObj.rand.nextDouble(), entity.posZ+entity.worldObj.rand.nextDouble()-0.5D, 0, 0, 0, 0);
		}
		else {
			entity.stopActiveHand();
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
	{
		if (player.isSneaking())
		{
			this.setActive(stack, !this.isActive(stack));
			if (this.isActive(stack) && world.isRemote)
				player.playSound(SoundEvents.ENTITY_ENDERDRAGON_GROWL, 0.4f, world.rand.nextFloat()+1.5f);
			else if (world.isRemote)
				player.playSound(SoundEvents.ENTITY_ENDERDRAGON_HURT, 1f, world.rand.nextFloat()-0.5f);
		}
		if (player.worldObj.isRemote)
			return new ActionResult(EnumActionResult.PASS, stack);
		player.setActiveHand(hand);
		return new ActionResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
	{
		subItems.add(this.getItemStack());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasEffect(ItemStack stack)
	{
		return isActive(stack);
	}

	@Override
	public ItemStack getItemStack() {
		ItemStack stack = new ItemStack(this);
		return stack;
	}

	@Override
	public String getName() {
		return this.getItemStackDisplayName(new ItemStack(this)).replace(" (Inactive)", "");
	}

	@Override
	public int getColor() {
		return 0xffbb33;
	}

	@Override
	public float getRed() {
		return 1.0f;
	}

	@Override
	public float getGreen() {
		return 0.7f;
	}

	@Override
	public float getBlue() {
		return 0.2f;
	}

	@Override
	public ArrayList<String> droppedBy() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Boss Zombie Priest");
		return list;
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		cooldown--;
		if (entityIn.worldObj.isRemote && entityIn instanceof EntityGuiPlayer)
		{
			if (((EntityGuiPlayer)entityIn).book.displayTicks % 180 == 0)
			{
				entityIn.setSneaking(true);
				this.setActive(stack, !this.isActive(stack));
			}
			else
			{
				boolean shouldSneak = false;
				for (int i=0; i<10; i++)
					if ((((EntityGuiPlayer)entityIn).book.displayTicks-i) % 60 == 0)
						shouldSneak = true;
				if (!shouldSneak)
					entityIn.setSneaking(false);
			}
		}
		//regen when activated - not if currently in use (so it doesn't interrupt)
		if (!entityIn.worldObj.isRemote && entityIn instanceof EntityLivingBase && ((EntityLivingBase) entityIn).getActiveItemStack() != stack && (((EntityLivingBase) entityIn).getHeldItemMainhand() == stack || ((EntityLivingBase) entityIn).getHeldItemOffhand() == stack))
			if (this.isActive(stack) && ((EntityLivingBase) entityIn).getHealth() < ((EntityLivingBase) entityIn).getMaxHealth() && entityIn.worldObj.getTotalWorldTime() % 80 == 0)
				if (this.giveRegen((EntityLivingBase) entityIn, 1, 100))
				{
					entityIn.worldObj.playSound(null, entityIn.posX, entityIn.posY, entityIn.posZ, SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.PLAYERS, 0.5f, worldIn.rand.nextFloat()+0.8f);
					this.noXp = false;
					stack.attemptDamageItem(1, worldIn.rand);
				}
				else if (!this.noXp)
				{
					entityIn.worldObj.playSound(null, entityIn.posX, entityIn.posY, entityIn.posZ, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 1.0f, 1.0f);
					this.noXp = true;
				}
		
		if (!worldIn.isRemote && entityIn instanceof EntityPlayer && !(entityIn instanceof FakePlayer)) {
			int index = WorldData.get(worldIn).getPlayerIndex(entityIn.getName());
			if (!WorldData.get(worldIn).unlockedItems.get(index).contains(this.getName()))
			{
				WorldData.get(worldIn).unlockedItems.get(index).add(this.getName());
				Event.displayUnlockMessage((EntityPlayer) entityIn, "Unlocked information about the "+stack.getDisplayName()+" item in the Event Book");
				WorldData.get(worldIn).markDirty();
			}
		}
	}
}
