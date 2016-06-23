package furgl.mobEvents.common.block;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.tileentity.TileEntityBossLoot;
import net.minecraft.block.BlockChest;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;

public class BlockBossLoot extends BlockChest implements ITileEntityProvider
{
	public BlockBossLoot() 
	{
		super(0);
	}

	@Override
	public ILockableContainer getLockableContainer(World worldIn, BlockPos pos)
	{
		TileEntity tileentity = worldIn.getTileEntity(pos);

		if (!(tileentity instanceof TileEntityChest))
			return null;
		else
			return (TileEntityChest)tileentity;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileEntityBossLoot();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (!worldIn.isRemote)
			playerIn.openGui(MobEvents.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}
}
