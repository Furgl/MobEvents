package furgl.mobEvents.common.item.drops;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.Events.Event;
import furgl.mobEvents.common.entity.EntityGuiPlayer;
import furgl.mobEvents.common.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("deprecation")
public class ItemButchersCleaver0 extends ItemSword implements IEventItem
{	
	/**Normal attackDamage is 1D*/
	protected double attackDamage;
	/**Normal attackSpeed is 4D*/
	protected double attackSpeed;
	public static final double MAX_BLOOD = 200;
	protected static final int MAX_COOLDOWN = 100;

	public ItemButchersCleaver0(Item.ToolMaterial material) {
		super(material);
		this.maxStackSize = 1;
		this.setMaxDamage(1000);
		this.attackDamage = 7.0D;
		this.attackSpeed = -2.6D;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack)
	{
		return ItemButchersCleaver0.getTier(stack.getItem()) == 3 || super.hasEffect(stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
	{
		int blood = (stack.hasTagCompound() && stack.getTagCompound().hasKey("blood")) ? stack.getTagCompound().getInteger("blood") : 0;

		//book render
		if (player instanceof EntityGuiPlayer) {
			blood = (int) (((EntityGuiPlayer)player).renderTicks/3 % ItemButchersCleaver0.MAX_BLOOD);
			if (blood == 0 && player.worldObj.rand.nextInt(2) != 0)  //stay at 0 blood for a while
				((EntityGuiPlayer)player).renderTicks = 0;
			if (blood == 0 && this.getClass() != ItemButchersCleaver0.class) 
				stack.setItem(ModItems.butchersCleaver0);
			else if (blood > 0 && blood <= ItemButchersCleaver0.getMaxBloodForTier(1) && this.getClass() != ItemButchersCleaver1.class) 
				stack.setItem(ModItems.butchersCleaver1);
			else if (blood > ItemButchersCleaver0.getMaxBloodForTier(1) && blood <= ItemButchersCleaver0.getMaxBloodForTier(2) && this.getClass() != ItemButchersCleaver2.class) 
				stack.setItem(ModItems.butchersCleaver2);
			else if (blood > ItemButchersCleaver0.getMaxBloodForTier(2) && this.getClass() != ItemButchersCleaver3.class) 
				stack.setItem(ModItems.butchersCleaver3);
			player.setHeldItem(EnumHand.MAIN_HAND, stack);
			((EntityGuiPlayer)player).book.stack = stack;
		}

		tooltip.set(0, TextFormatting.AQUA+tooltip.get(0));
		int index = MobEvents.proxy.getWorldData().getPlayerIndex(player.getDisplayNameString());
		if (MobEvents.proxy.getWorldData().unlockedItems.get(index).contains("Butcher's Cleaver") || player.capabilities.isCreativeMode) {
			tooltip.add(TextFormatting.DARK_RED+""+TextFormatting.ITALIC+"Blood Capacity: "+(int)((blood/MAX_BLOOD)*100D)+"%");
			tooltip.add(TextFormatting.GOLD+"Derives strength from");
			tooltip.add(TextFormatting.GOLD+"its enemies' blood");
		}
		else 
			tooltip.add(TextFormatting.ITALIC+""+TextFormatting.GOLD+"???");
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack)
	{
		String name = ("" + I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name")).trim();
		switch (ItemButchersCleaver0.getTier(stack.getItem())) {
		case 1:
			name = TextFormatting.RED+name;
			break;
		case 2:
			name = TextFormatting.DARK_RED+name;
			break;
		case 3: 
			name = TextFormatting.DARK_RED+""+TextFormatting.BOLD+name;
			break;
		}
		return name;
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack)
	{
		Multimap<String, AttributeModifier> multimap = HashMultimap.<String, AttributeModifier>create();
		if (slot == EntityEquipmentSlot.MAINHAND)
		{
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", this.attackDamage, 0));
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getAttributeUnlocalizedName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", this.attackSpeed, 0));
		}
		return multimap;
	}

	@Override
	public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot equipmentSlotIn, Entity entity){
		if (equipmentSlotIn == EntityEquipmentSlot.MAINHAND || equipmentSlotIn == EntityEquipmentSlot.OFFHAND)
			return true;
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
	{
		ItemStack stack = new ItemStack(itemIn);
		NBTTagCompound nbt = new NBTTagCompound();
		if (itemIn instanceof ItemButchersCleaver0) {
			nbt.setInteger("blood", ItemButchersCleaver0.getMaxBloodForTier(ItemButchersCleaver0.getTier(itemIn)));
			nbt.setInteger("cooldown", MAX_COOLDOWN);
		}
		stack.setTagCompound(nbt);
		subItems.add(stack);
	}

	@Override
	public ItemStack getItemStack() {
		ItemStack stack = new ItemStack(this);
		return stack;
	}

	@Override
	public String getName() {
		return this.getItemStackDisplayName(new ItemStack(this));
	}

	@Override
	public int getColor() {
		return 0xb30000;
	}

	@Override
	public float getRed() {
		return 0.8f;
	}

	@Override
	public float getGreen() {
		return 0.1f;
	}

	@Override
	public float getBlue() {
		return 0.1f;
	}

	@Override
	public ArrayList<String> droppedBy() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Boss Zombie Butcher");
		return list;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return oldStack.getItem() != newStack.getItem();
	}

	public static int getTier(Item item) {
		return Integer.parseInt(item.getClass().toString().subSequence(item.getClass().toString().length()-1, item.getClass().toString().length()).toString());
	}

	public static int getMaxBloodForTier(int tier) {
		switch (tier) {
		default:
			return 0;
		case 1:
			return (int) (MAX_BLOOD/2);
		case 2:
			return (int) (MAX_BLOOD/5*4);
		case 3: 
			return (int) MAX_BLOOD;
		}
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		//unlock
		if (this.getClass() == ItemButchersCleaver0.class && !worldIn.isRemote && entityIn instanceof EntityPlayer && !(entityIn instanceof FakePlayer)) {
			int index = MobEvents.proxy.getWorldData().getPlayerIndex(entityIn.getName());
			if (!MobEvents.proxy.getWorldData().unlockedItems.get(index).contains(this.getName()))
			{
				MobEvents.proxy.getWorldData().unlockedItems.get(index).add(this.getName());
				Event.displayUnlockMessage((EntityPlayer) entityIn, "Unlocked information about the "+stack.getDisplayName()+" item in the Event Book");
				MobEvents.proxy.getWorldData().markDirty();
			}
		}

		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		int blood = stack.getTagCompound().hasKey("blood") ? stack.getTagCompound().getInteger("blood") : 0;
		int cooldown = stack.getTagCompound().hasKey("cooldown") ? stack.getTagCompound().getInteger("cooldown") : 0;
		//auto attack
		if (worldIn.isRemote && entityIn instanceof EntityPlayer && isSelected && !((EntityPlayer)entityIn).isSwingInProgress) {
			if (worldIn.rand.nextInt((int) (MAX_BLOOD-blood + 20)) == 0) {
				RayTraceResult ray = Minecraft.getMinecraft().objectMouseOver;
				if (ray != null && ray.entityHit != null)
					Minecraft.getMinecraft().playerController.attackEntity((EntityPlayer) entityIn, ray.entityHit);
			}
		}
		//no client work behind this point
		if (worldIn.isRemote)
			return;
		//decrease blood if cooldown is <= 0
		if (!isSelected)
			cooldown -= 3;
		if (cooldown-- <= 0 && blood > 0 && entityIn.ticksExisted % (isSelected ? 5 : 3) == 0)
			blood--;
		stack.getTagCompound().setInteger("blood", blood);
		stack.getTagCompound().setInteger("cooldown", cooldown);
		//change item depending on blood level
		int before = ItemButchersCleaver0.getTier(stack.getItem());
		if (blood == 0 && this.getClass() != ItemButchersCleaver0.class) 
			stack.setItem(ModItems.butchersCleaver0);
		else if (blood > 0 && blood <= ItemButchersCleaver0.getMaxBloodForTier(1) && this.getClass() != ItemButchersCleaver1.class) 
			stack.setItem(ModItems.butchersCleaver1);
		else if (blood > ItemButchersCleaver0.getMaxBloodForTier(1) && blood <= ItemButchersCleaver0.getMaxBloodForTier(2) && this.getClass() != ItemButchersCleaver2.class) 
			stack.setItem(ModItems.butchersCleaver2);
		else if (blood > ItemButchersCleaver0.getMaxBloodForTier(2) && this.getClass() != ItemButchersCleaver3.class) 
			stack.setItem(ModItems.butchersCleaver3);
		int after = ItemButchersCleaver0.getTier(stack.getItem());
		int difference = after - before;
		//play sound based on calculated difference
		if (difference > 0)
			worldIn.playSound(null, entityIn.getPosition(), SoundEvents.ENTITY_WOLF_GROWL, SoundCategory.PLAYERS, 0.4f*after, 0.9f+(0.2f*after));
		else if (difference < 0)
			worldIn.playSound(null, entityIn.getPosition(), SoundEvents.ENTITY_WOLF_WHINE, SoundCategory.PLAYERS, 0.1f*(after+1), 0.5f+(0.2f*after));
	}

	/**Copied from Entity to make accessible on server*/
	public RayTraceResult rayTrace(Entity entity, double blockReachDistance, float partialTicks)
	{
		Vec3d vec3d = this.getPositionEyes(entity, partialTicks);
		Vec3d vec3d1 = entity.getLook(partialTicks);
		Vec3d vec3d2 = vec3d.addVector(vec3d1.xCoord * blockReachDistance, vec3d1.yCoord * blockReachDistance, vec3d1.zCoord * blockReachDistance);
		return entity.worldObj.rayTraceBlocks(vec3d, vec3d2, false, false, true);
	}

	/**Copied from Entity to make accessible on server*/
	public Vec3d getPositionEyes(Entity entity, float partialTicks)
	{
		if (partialTicks == 1.0F)
		{
			return new Vec3d(entity.posX, entity.posY + (double)entity.getEyeHeight(), entity.posZ);
		}
		else
		{
			double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double)partialTicks;
			double d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double)partialTicks + (double)entity.getEyeHeight();
			double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double)partialTicks;
			return new Vec3d(d0, d1, d2);
		}
	}

	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(LivingAttackEvent event)
	{
		if (event.getSource().getEntity() instanceof EntityLivingBase &&
				!event.getSource().getEntity().worldObj.isRemote && 
				((EntityLivingBase) event.getSource().getEntity()).getHeldItemMainhand() != null && 
				((EntityLivingBase) event.getSource().getEntity()).getHeldItemMainhand().getItem() instanceof ItemButchersCleaver0) {
			ItemStack stack = ((EntityLivingBase) event.getSource().getEntity()).getHeldItemMainhand();
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			//add blood and cooldown
			int blood = (stack.getTagCompound().hasKey("blood")) ? stack.getTagCompound().getInteger("blood") : 0;
			blood = Math.min((((int) event.getAmount()) + blood), (int) MAX_BLOOD);
			stack.getTagCompound().setInteger("blood", blood);
			stack.getTagCompound().setInteger("cooldown", MAX_COOLDOWN);
			//play sound and spawn particles
			event.getSource().getEntity().worldObj.playSound(null, event.getSource().getEntity().getPosition(), SoundEvents.ENTITY_ENDERDRAGON_SHOOT, SoundCategory.PLAYERS, (float) (0.0f+(0.1f*((blood/MAX_BLOOD)*100f))), 2);
			for (int i=0; i<Math.max(event.getAmount(), 20); i++)
				((WorldServer)event.getEntity().worldObj).spawnParticle(EnumParticleTypes.REDSTONE, (float)event.getEntity().posX+(event.getEntity().worldObj.rand.nextFloat()-0.5f)*1f, (float)event.getEntity().posY+(event.getEntity().worldObj.rand.nextFloat()+0.0f)*2f, (float)event.getEntity().posZ+(event.getEntity().worldObj.rand.nextFloat()-0.5f)*1f, 1, 0, 0, 0, 0, new int[0]);
		}
	}
}
