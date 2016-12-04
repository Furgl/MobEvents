package furgl.mobEvents.common.item.drops;

import java.util.ArrayList;
import java.util.List;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.Events.Event;
import furgl.mobEvents.common.block.ModBlocks;
import furgl.mobEvents.common.tileentity.TileEntityUpgradedAnvil;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemAnvilUpgrade extends Item implements IEventItem
{	
	public static ArrayList<ItemAnvilUpgrade> allUpgrades;
	public ArrayList<Item> repairableItems;
	public ArrayList<Integer> repairableItemDurability;

	public ItemAnvilUpgrade() {
		if (allUpgrades == null)
			allUpgrades = new ArrayList<ItemAnvilUpgrade>();
		if (this.getClass() != ItemAnvilUpgrade.class && !allUpgrades.contains(this))
			allUpgrades.add(this);
		this.repairableItems = new ArrayList<Item>();
		this.repairableItemDurability = new ArrayList<Integer>();
		this.maxStackSize = 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
	{
		tooltip.set(0, TextFormatting.AQUA+tooltip.get(0));
		int index = MobEvents.proxy.getWorldData().getPlayerIndex(player.getDisplayNameString());
		if (MobEvents.proxy.getWorldData().unlockedItems.get(index).contains(this.getName()) || player.capabilities.isCreativeMode) {
			tooltip.add(TextFormatting.GOLD+"Right click anvil to upgrade.");
			tooltip.add(TextFormatting.WHITE+"Allows items to be repaired by:");
			for (Item item : this.repairableItems)
				tooltip.add(TextFormatting.WHITE+"* "+item.getItemStackDisplayName(new ItemStack(item)));
		}
		else 
			tooltip.add(TextFormatting.ITALIC+""+TextFormatting.GOLD+"???");
	}

	@SuppressWarnings("deprecation")
	@Override
	public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
	{
		if (worldIn.isRemote)
			return EnumActionResult.PASS;
		else if (playerIn.capabilities.allowEdit)
		{
			if (worldIn.getBlockState(pos).getBlock() == Blocks.ANVIL)
				worldIn.setBlockState(pos, ModBlocks.upgradedAnvil.getStateFromMeta(worldIn.getBlockState(pos).getBlock().getMetaFromState(worldIn.getBlockState(pos))));//ModBlocks.upgradedAnvil.getExtendedState(worldIn.getBlockState(pos), worldIn, pos)
			else if (worldIn.getBlockState(pos).getBlock() == ModBlocks.upgradedAnvil)
			{
				if (((TileEntityUpgradedAnvil)worldIn.getTileEntity(pos)).upgrades.contains(this))
				{
					playerIn.addChatMessage(new TextComponentTranslation("The anvil already has this upgrade.").setStyle(new Style().setColor(TextFormatting.RED)));
					return EnumActionResult.SUCCESS;
				}
			}
			else
				return EnumActionResult.FAIL;
			((TileEntityUpgradedAnvil)worldIn.getTileEntity(pos)).upgrades.add(this);
			worldIn.notifyBlockUpdate(pos, worldIn.getBlockState(pos), worldIn.getBlockState(pos), 3);
			worldIn.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1.0f, 1.0f);
			for (int i=0; i<100; i++)
				worldIn.spawnParticle(EnumParticleTypes.REDSTONE, true, (float)pos.getX()+(itemRand.nextFloat()-0.5f)*1.2f+0.5f, (float)pos.getY()+(itemRand.nextFloat()+0.0f)*1f+0.4f, (float)pos.getZ()+(itemRand.nextFloat()-0.5f)*1.2f+0.5f, 0, 0, 0, 0, 2);
			if (!playerIn.capabilities.isCreativeMode)
				stack.stackSize--;
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.FAIL;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
	{
		subItems.add(this.getItemStack());
	}

	@Override
	public String getName() {
		return "Anvil Upgrade";
	}

	@Override
	public ItemStack getItemStack() {
		ItemStack stack = new ItemStack(this);
		return stack;
	}

	@Override
	public int getColor() {
		return 0x204d00;
	}

	@Override
	public float getRed() {
		return 0.3f;
	}

	@Override
	public float getGreen() {
		return 0.5f;
	}

	@Override
	public float getBlue() {
		return 0.2f;
	}

	@Override
	public ArrayList<String> droppedBy() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Skeleton Boss");
		return list;
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (!worldIn.isRemote && entityIn instanceof EntityPlayer && !(entityIn instanceof FakePlayer)) {
			int index = MobEvents.proxy.getWorldData().getPlayerIndex(entityIn.getName());
			if (!MobEvents.proxy.getWorldData().unlockedItems.get(index).contains(this.getName()))
			{
				MobEvents.proxy.getWorldData().unlockedItems.get(index).add(this.getName());
				Event.displayUnlockMessage((EntityPlayer) entityIn, "Unlocked information about the "+stack.getDisplayName()+" item in the Event Book");
				MobEvents.proxy.getWorldData().markDirty();
			}
		}
	}
}
