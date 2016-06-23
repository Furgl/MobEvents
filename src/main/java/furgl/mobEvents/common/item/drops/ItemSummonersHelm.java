package furgl.mobEvents.common.item.drops;

import java.util.ArrayList;
import java.util.List;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.block.ModBlocks;
import furgl.mobEvents.common.config.Config;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemSummonersHelm extends ItemArmor implements IEventItem
{	
	public Block block;
	public boolean isLit;

	public ItemSummonersHelm(ItemArmor.ArmorMaterial material, int renderIndex, int armorType) {
		super(material, renderIndex, armorType);
		this.maxStackSize = 1;
		this.setMaxDamage(400);
		block = ModBlocks.summoners_helm;
	}

	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
	{
		tooltip.set(0, EnumChatFormatting.AQUA+tooltip.get(0));
		Config.syncFromConfig(player);
		if (Config.unlockedItems.contains(this.getName()) || player.capabilities.isCreativeMode)
			tooltip.add(EnumChatFormatting.ITALIC+""+EnumChatFormatting.GOLD+"Provides immunity to burning");
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
	public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		IBlockState iblockstate = worldIn.getBlockState(pos);
		Block block = iblockstate.getBlock();
		if (!block.isReplaceable(worldIn, pos))
			pos = pos.offset(side);
		if (stack.stackSize == 0)
			return false;
		else if (!playerIn.canPlayerEdit(pos, side, stack))
			return false;
		else if (worldIn.canBlockBePlaced(this.block, pos, false, side, (Entity)null, stack))
		{
			int i = this.getMetadata(stack.getMetadata());
			IBlockState iblockstate1 = this.block.onBlockPlaced(worldIn, pos, side, hitX, hitY, hitZ, i, playerIn);
			if (placeBlockAt(stack, playerIn, worldIn, pos, side, hitX, hitY, hitZ, iblockstate1))
			{
				worldIn.playSoundEffect((double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F), this.block.stepSound.getPlaceSound(), (this.block.stepSound.getVolume() + 1.0F) / 2.0F, this.block.stepSound.getFrequency() * 0.8F);
				--stack.stackSize;
			}
			return true;
		}
		else
			return false;
	}

	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState)
	{
		if (!world.setBlockState(pos, newState, 3)) 
			return false;
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() == this.block)
			this.block.onBlockPlacedBy(world, pos, state, player, stack);
		return true;
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		if (!stack.isItemEnchanted())
			stack.addEnchantment(Enchantment.fireProtection, 5);
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack){
		if (!world.isRemote)
			player.extinguish();
		if (world.isRemote && this.isLit && !player.isWet())
			player.setFire(1);
		if (world.isRemote && world.getTotalWorldTime() % 30 == 0)
			isLit = !isLit;
	}

	@Override
	public boolean isValidArmor(ItemStack stack, int armorType, Entity entity){
		if (armorType == 0)
			return true;
		return false;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type)
	{
		if (isLit)
			return MobEvents.MODID+":textures/models/armor/summoners_helm_on_layer_1.png";
		else
			return MobEvents.MODID+":textures/models/armor/summoners_helm_off_layer_1.png";
	}

	@Override
	public ItemStack getItemStack() {
		ItemStack stack = new ItemStack(this);
		stack.addEnchantment(Enchantment.fireProtection, 5);
		return stack;
	}

	@Override
	public String getName() {
		return this.getItemStackDisplayName(new ItemStack(this));
	}

	@Override
	public int getColor() {
		return 0xff5c33;
	}

	@Override
	public float getRed() {
		return 1.0f;
	}

	@Override
	public float getGreen() {
		return 0.4f;
	}

	@Override
	public float getBlue() {
		return 0.2f;
	}

	@Override
	public ArrayList<String> droppedBy() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Zombie Summoner");
		return list;
	}
}
