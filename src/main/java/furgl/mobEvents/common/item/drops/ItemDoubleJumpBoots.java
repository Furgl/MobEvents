package furgl.mobEvents.common.item.drops;

import java.util.ArrayList;
import java.util.List;

import furgl.mobEvents.client.model.armor.ModelDoubleJumpBoots;
import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.Events.Event;
import furgl.mobEvents.common.world.WorldData;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDoubleJumpBoots extends ItemArmor implements IEventItem
{	
	public boolean jumped;

	public ItemDoubleJumpBoots(ItemArmor.ArmorMaterial material, int renderIndex, EntityEquipmentSlot equipmentSlotIn) {
		super(material, renderIndex, equipmentSlotIn);
		this.maxStackSize = 1;
		this.setMaxDamage(400);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
	{
		subItems.add(this.getItemStack());
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack)
	{
		player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 4, 1, false, false));
		if (world.isRemote)
			MobEvents.proxy.doubleJumpBootsTick(player, this);
	}

	public void jumpEffects(Entity entity) 
	{
		entity.worldObj.playSound(entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_BAT_TAKEOFF, SoundCategory.HOSTILE, 0.3F, 1.2F, false);
		for (int i=0; i<10; i++)
			entity.worldObj.spawnParticle(EnumParticleTypes.CLOUD, entity.posX+Item.itemRand.nextDouble()-0.5D, entity.posY, entity.posZ+Item.itemRand.nextDouble()-0.5D, 0, -0.05D, 0, 0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
	{
		tooltip.set(0, TextFormatting.AQUA+tooltip.get(0));
		int index = WorldData.get(player.worldObj).getPlayerIndex(player.getDisplayNameString());
		if (WorldData.get(player.worldObj).unlockedItems.get(index).contains(this.getName()) || player.capabilities.isCreativeMode) {
			tooltip.add(TextFormatting.ITALIC+""+TextFormatting.GOLD+"Allows double jumping");
			tooltip.add("Jump Boost II");
		}
		else 
			tooltip.add(TextFormatting.ITALIC+""+TextFormatting.GOLD+"???");
	}

	@Override
	public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot equipmentSlotIn, Entity entity){
		if (equipmentSlotIn == EntityEquipmentSlot.FEET)
			return true;
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public net.minecraft.client.model.ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, net.minecraft.client.model.ModelBiped _default)
	{
		return new ModelDoubleJumpBoots();
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String layer)
	{
		return MobEvents.MODID+":textures/models/armor/double_jump_boots_layer_1.png";
	}

	@Override
	public ItemStack getItemStack() {
		ItemStack stack = new ItemStack(this);
		stack.addEnchantment(Enchantments.FEATHER_FALLING, 1);
		return stack;
	}

	@Override
	public String getName() {
		return this.getItemStackDisplayName(new ItemStack(this));
	}

	@Override
	public int getColor() {
		return 0x0059b3;
	}

	@Override
	public float getRed() {
		return 0.0f;
	}

	@Override
	public float getGreen() {
		return 0.5f;
	}

	@Override
	public float getBlue() {
		return 0.9f;
	}

	@Override
	public ArrayList<String> droppedBy() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Zombie Jumper");
		return list;
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (!stack.isItemEnchanted())
			stack.addEnchantment(Enchantments.FEATHER_FALLING, 1);

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
