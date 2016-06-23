package furgl.mobEvents.common.item.drops;

import java.util.ArrayList;
import java.util.List;

import furgl.mobEvents.client.model.item.ModelThievesMask;
import furgl.mobEvents.common.config.Config;
import furgl.mobEvents.common.entity.EntityGuiPlayer;
import net.minecraft.creativetab.CreativeTabs;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemThievesMask extends ItemArmor implements IEventItem
{	
	private boolean isSneaking;
	
	public ItemThievesMask(ItemArmor.ArmorMaterial material, int renderIndex, int armorType) {
		super(material, renderIndex, armorType);
		this.maxStackSize = 1;
		this.setMaxDamage(400);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
	{
		tooltip.set(0, EnumChatFormatting.AQUA+tooltip.get(0));
		Config.syncFromConfig(player);
		if (Config.unlockedItems.contains(this.getName()) || player.capabilities.isCreativeMode) {
			tooltip.add(EnumChatFormatting.ITALIC+""+EnumChatFormatting.GOLD+"Provides boosts when sneaking");
			tooltip.add("Invisibility");
			tooltip.add("Speed");
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
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
	{
		subItems.add(this.getItemStack());
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public net.minecraft.client.model.ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot)
	{
		return new ModelThievesMask();
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
		if (player instanceof EntityGuiPlayer && world.getTotalWorldTime() % 30 == 0)
			this.isSneaking = !this.isSneaking;
		if (isSneaking)
			player.setSneaking(true);
		if (player.isSneaking())
		{
			player.addPotionEffect(new PotionEffect(Potion.invisibility.id, 2, 0, false, false));
			player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 1, 15, false, false));
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
	public boolean isValidArmor(ItemStack stack, int armorType, Entity entity) {
		if (armorType == 0)
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
}
