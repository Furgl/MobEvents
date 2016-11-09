package furgl.mobEvents.common.item.drops;

import java.util.ArrayList;
import java.util.List;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.Events.Event;
import furgl.mobEvents.common.block.ModBlocks;
import furgl.mobEvents.common.entity.EntityGuiPlayer;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemSummonersHelm extends ItemArmor implements IEventItem
{	
	public Block block;
	public boolean isLit;

	public ItemSummonersHelm(ItemArmor.ArmorMaterial material, int renderIndex, EntityEquipmentSlot equipmentSlotIn) {
		super(material, renderIndex, equipmentSlotIn);
		this.maxStackSize = 1;
		this.setMaxDamage(400);
		block = ModBlocks.summonersHelm;
	}

	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
	{
		tooltip.set(0, TextFormatting.AQUA+tooltip.get(0));
		int index = MobEvents.proxy.getWorldData().getPlayerIndex(player.getDisplayNameString());
		if (MobEvents.proxy.getWorldData().unlockedItems.get(index).contains(this.getName()) || player.capabilities.isCreativeMode)
			tooltip.add(TextFormatting.ITALIC+""+TextFormatting.GOLD+"Provides immunity to burning");
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
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (this.block == null)
			this.block = ModBlocks.summonersHelm;
		IBlockState iblockstate = worldIn.getBlockState(pos);
		Block block = iblockstate.getBlock();
		if (!block.isReplaceable(worldIn, pos))
			pos = pos.offset(side);
		if (stack.stackSize == 0)
			return EnumActionResult.FAIL;
		else if (!playerIn.canPlayerEdit(pos, side, stack))
			return EnumActionResult.FAIL;
		else if (this.block != null && worldIn.canBlockBePlaced(this.block, pos, false, side, (Entity)null, stack))
		{
			int i = this.getMetadata(stack.getMetadata());
			IBlockState iblockstate1 = this.block.onBlockPlaced(worldIn, pos, side, hitX, hitY, hitZ, i, playerIn);
			if (placeBlockAt(stack, playerIn, worldIn, pos, side, hitX, hitY, hitZ, iblockstate1))
			{
				SoundType soundtype = this.block.getSoundType(iblockstate1, worldIn, pos, playerIn);
				worldIn.playSound(playerIn, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);				--stack.stackSize;
			}
			return EnumActionResult.SUCCESS;
		}
		else
			return EnumActionResult.FAIL;
	}

	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState)
	{
		if (this.block == null)
			this.block = ModBlocks.summonersHelm;
		if (!world.setBlockState(pos, newState, 3)) 
			return false;
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() == this.block)
			this.block.onBlockPlacedBy(world, pos, state, player, stack);
		return true;
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack){
		if (!world.isRemote)
			player.extinguish();
		if (world.isRemote && this.isLit && !player.isWet())
			player.setFire(1);
		if (world.isRemote && world.getTotalWorldTime() % 30 == 0)
		{
			isLit = !isLit;
			if (!isLit)
				player.extinguish();
		}
		if (world.isRemote && player instanceof EntityGuiPlayer && ((EntityGuiPlayer)player).book.displayTicks % 90 == 0)
		{
			isLit = !isLit;
			if (!isLit)
				player.extinguish();
		}
	}

	@Override
	public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot slot, Entity entity){
		if (slot == EntityEquipmentSlot.HEAD)
			return true;
		return false;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type)
	{
		if (isLit)
			return MobEvents.MODID+":textures/models/armor/summoners_helm_on_layer_1.png";
		else
			return MobEvents.MODID+":textures/models/armor/summoners_helm_off_layer_1.png";
	}

	@Override
	public ItemStack getItemStack() {
		ItemStack stack = new ItemStack(this);
		stack.addEnchantment(Enchantments.FIRE_PROTECTION, 5);
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

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (!stack.isItemEnchanted())
			stack.addEnchantment(Enchantments.FIRE_PROTECTION, 5);
		
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
