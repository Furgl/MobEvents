package furgl.mobEvents.common.block;

import java.util.List;
import java.util.Random;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.item.drops.ItemAnvilUpgrade;
import furgl.mobEvents.common.tileentity.TileEntityUpgradedAnvil;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockUpgradedAnvil extends BlockAnvil implements ITileEntityProvider
{	
	public BlockUpgradedAnvil() {
		super();
		this.blockSoundType = SoundType.ANVIL;
	}
	
	@Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (!worldIn.isRemote)
			playerIn.openGui(MobEvents.instance, 1, worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}
	
	@Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
		//custom to give nbt to item given
		ItemStack item = super.getPickBlock(state, target, world, pos, player);
		NBTTagCompound stackNbt = new NBTTagCompound();
		item.writeToNBT(stackNbt);
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagCompound tileNbt = new NBTTagCompound();
		world.getTileEntity(pos).writeToNBT(tileNbt);
		nbt.setTag("BlockEntityTag", tileNbt);
		stackNbt.setTag("tag", nbt);
		item.readFromNBT(stackNbt);
		
        return item;
    }
	
	@Override
	 public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) { }
	
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) { }
	
	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack1)
	{
		player.addStat(StatList.getBlockStats(this));
		player.addExhaustion(0.025F);

        if (this.canSilkHarvest(worldIn, pos, state, player) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack1) > 0)
		{
			java.util.ArrayList<ItemStack> items = new java.util.ArrayList<ItemStack>();
			ItemStack itemstack = this.createStackedBlock(state);
			if (itemstack != null)
				items.add(itemstack);

			net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(items, worldIn, pos, worldIn.getBlockState(pos), 0, 1.0f, true, player);
			for (ItemStack stack : items)
				spawnAsEntity(worldIn, pos, stack);
		}
		else
		{
			harvesters.set(player);
			
			//everything copied except this
			if (!worldIn.isRemote && !worldIn.restoringBlockSnapshots) // do not drop items while restoring blockstates, prevents item dupe
			{
				java.util.List<ItemStack> items = getDrops(worldIn, pos, state, 0);
				
				for (ItemStack item : items)
				{
					//custom to give nbt to item dropped
					NBTTagCompound stackNbt = new NBTTagCompound();
					item.writeToNBT(stackNbt);
					NBTTagCompound nbt = new NBTTagCompound();
					NBTTagCompound tileNbt = new NBTTagCompound();
					te.writeToNBT(tileNbt);
					nbt.setTag("BlockEntityTag", tileNbt);
					stackNbt.setTag("tag", nbt);
					item.readFromNBT(stackNbt);
					
					spawnAsEntity(worldIn, pos, item);
				}
			}
			
			harvesters.set(null);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list)
	{
		for (int i=0; i<3; i++)
		{
			ItemStack stack = new ItemStack(itemIn, 1, i);
			NBTTagCompound stackNbt = new NBTTagCompound();
			stack.writeToNBT(stackNbt);
			NBTTagCompound nbt = new NBTTagCompound();
			NBTTagCompound tileNbt = new NBTTagCompound();
			TileEntityUpgradedAnvil te = (TileEntityUpgradedAnvil) this.createNewTileEntity(null, 0);
			te.upgrades.addAll(ItemAnvilUpgrade.allUpgrades);
			te.writeToNBT(tileNbt);
			nbt.setTag("BlockEntityTag", tileNbt);
			stackNbt.setTag("tag", nbt);
			stack.readFromNBT(stackNbt);
			list.add(stack);
		}
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) 
	{
		return new TileEntityUpgradedAnvil();
	}
}
