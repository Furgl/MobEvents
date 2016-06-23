package furgl.mobEvents.common.item.drops;

import java.util.ArrayList;
import java.util.List;

import furgl.mobEvents.client.model.item.ModelDoubleJumpBoots;
import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.config.Config;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDoubleJumpBoots extends ItemArmor implements IEventItem
{	
	public boolean jumped;

	public ItemDoubleJumpBoots(ItemArmor.ArmorMaterial material, int renderIndex, int armorType) {
		super(material, renderIndex, armorType);
		this.maxStackSize = 1;
		this.setMaxDamage(400);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
	{
		ItemStack stack = new ItemStack(this);
		stack.addEnchantment(Enchantment.featherFalling, 1);
		subItems.add(stack);
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack)
	{
		player.addPotionEffect(new PotionEffect(Potion.jump.id, 4, 1, false, false));
		if (player instanceof EntityPlayerSP)
		{
			if (!this.jumped && !player.isOnLadder() && !player.onGround && ((EntityPlayerSP)player).movementInput.jump && ((int)ReflectionHelper.getPrivateValue(EntityLivingBase.class, player, 55)) == 0) { //55 39
				player.jump();
				this.jumped = true;
				this.jumpEffects(player);
			}
			if (player.onGround)
				this.jumped = false;
		}
	}

	public void jumpEffects(Entity entity) 
	{
		entity.worldObj.playSound(entity.posX, entity.posY, entity.posZ, "mob.bat.takeoff", 0.3F, 1.2F, true);
		for (int i=0; i<10; i++)
			entity.worldObj.spawnParticle(EnumParticleTypes.CLOUD, entity.posX+Item.itemRand.nextDouble()-0.5D, entity.posY, entity.posZ+Item.itemRand.nextDouble()-0.5D, 0, -0.05D, 0, 0);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		if (!stack.isItemEnchanted())
			stack.addEnchantment(Enchantment.featherFalling, 1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
	{
		tooltip.set(0, EnumChatFormatting.AQUA+tooltip.get(0));
		Config.syncFromConfig(player);
		if (Config.unlockedItems.contains(this.getName()) || player.capabilities.isCreativeMode) {
			tooltip.add(EnumChatFormatting.ITALIC+""+EnumChatFormatting.GOLD+"Allows double jumping");
			tooltip.add("Jump Boost II");
		}
		else if (!Config.unlockedItems.contains(this.getName()) && player.inventory.hasItemStack(stack))
		{
			Config.unlockedItems.add(this.getName());
			player.addChatMessage(new ChatComponentTranslation("Unlocked information about the "+stack.getDisplayName()+" item in the Event Book").setChatStyle(new ChatStyle().setItalic(true).setColor(EnumChatFormatting.DARK_GRAY)));
			Config.syncToConfig(player);
		}
		else 
			tooltip.add(EnumChatFormatting.ITALIC+""+EnumChatFormatting.GOLD+"???");
	}

	@Override
	public boolean isValidArmor(ItemStack stack, int armorType, Entity entity){
		if (armorType == 3)
			return true;
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public net.minecraft.client.model.ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot)
	{
		return new ModelDoubleJumpBoots();
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String layer)
	{
		return MobEvents.MODID+":textures/models/armor/double_jump_boots_layer_1.png";
	}

	@Override
	public ItemStack getItemStack() {
		ItemStack stack = new ItemStack(this);
		stack.addEnchantment(Enchantment.featherFalling, 1);
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
}
