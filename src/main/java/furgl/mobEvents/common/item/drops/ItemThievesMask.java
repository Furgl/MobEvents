package furgl.mobEvents.common.item.drops;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Multimap;

import furgl.mobEvents.client.model.item.ModelThievesMask;
import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.Events.Event;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemThievesMask extends ItemArmor implements IEventItem
{	
	public boolean isSneaking;
	private static final UUID MOVEMENT_SPEED_UUID = UUID.fromString("308e48ee-a300-2846-9b56-01e53e35eb8f");

	public ItemThievesMask(ItemArmor.ArmorMaterial material, int renderIndex, EntityEquipmentSlot equipmentSlotIn) {
		super(material, renderIndex, equipmentSlotIn);
		this.maxStackSize = 1;
		this.setMaxDamage(400);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
	{
		tooltip.set(0, TextFormatting.AQUA+tooltip.get(0));
		int index = MobEvents.proxy.getWorldData().getPlayerIndex(player.getDisplayNameString());
		if (MobEvents.proxy.getWorldData().unlockedItems.get(index).contains(this.getName()) || player.capabilities.isCreativeMode) {
			tooltip.add(TextFormatting.ITALIC+""+TextFormatting.GOLD+"Provides boosts when sneaking");
			tooltip.add("Invisibility");
			tooltip.add("Speed");
		}
		else 
			tooltip.add(TextFormatting.ITALIC+""+TextFormatting.GOLD+"???");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
	{
		subItems.add(this.getItemStack());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public net.minecraft.client.model.ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, net.minecraft.client.model.ModelBiped _default)
	{
		return new ModelThievesMask();
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack)
	{
		Multimap<String, AttributeModifier> map =  this.getItemAttributeModifiers(slot);
		if (slot == EntityEquipmentSlot.HEAD)
			map.put(SharedMonsterAttributes.MOVEMENT_SPEED.getAttributeUnlocalizedName(), new AttributeModifier(MOVEMENT_SPEED_UUID, "Speed Boost", 0.05d, 0));
		return map;
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
		if (world.isRemote)
			MobEvents.proxy.thievesMaskTick(player, this);
		else if (player.isSneaking())
		{
			if (!player.isPotionActive(MobEffects.INVISIBILITY) || player.getActivePotionEffect(MobEffects.INVISIBILITY) != null &&  player.getActivePotionEffect(MobEffects.INVISIBILITY).getDuration() == 0)
				player.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 2, 0, false, false));
			if (!player.isPotionActive(MobEffects.SPEED) || player.getActivePotionEffect(MobEffects.SPEED) != null &&  player.getActivePotionEffect(MobEffects.SPEED).getDuration() == 0)
				player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 1, 15, false, false));
			world.spawnParticle(EnumParticleTypes.TOWN_AURA, player.posX+world.rand.nextDouble()-0.5D, player.posY+world.rand.nextDouble()*1.5D, player.posZ+world.rand.nextDouble()-0.5D, 0, 0, 0, 0);
			world.spawnParticle(EnumParticleTypes.TOWN_AURA, player.posX+world.rand.nextDouble()-0.5D, player.posY+world.rand.nextDouble()*1.5D, player.posZ+world.rand.nextDouble()-0.5D, 0, 0, 0, 0);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasEffect(ItemStack stack)
	{
		return true;
	}

	@Override
	public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot equipmentSlotIn, Entity entity) {
		if (equipmentSlotIn == EntityEquipmentSlot.HEAD)
			return true;
		return false;
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
		return 0x8c8c8c;
	}

	@Override
	public float getRed() {
		return 0.5f;
	}

	@Override
	public float getGreen() {
		return 0.5f;
	}

	@Override
	public float getBlue() {
		return 0.5f;
	}

	@Override
	public ArrayList<String> droppedBy() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Zombie Thief");
		return list;
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		//unlock
		if (entityIn instanceof EntityPlayer && !(entityIn instanceof FakePlayer)) {
			int index = MobEvents.proxy.getWorldData().getPlayerIndex(entityIn.getName());
			if (!worldIn.isRemote && !MobEvents.proxy.getWorldData().unlockedItems.get(index).contains(this.getName()))
			{
				MobEvents.proxy.getWorldData().unlockedItems.get(index).add(this.getName());
				Event.displayUnlockMessage((EntityPlayer) entityIn, "Unlocked information about the "+stack.getDisplayName()+" item in the Event Book");
				MobEvents.proxy.getWorldData().markDirty();
			}
		}
	}
}
