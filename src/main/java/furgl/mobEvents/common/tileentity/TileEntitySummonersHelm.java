package furgl.mobEvents.common.tileentity;

import furgl.mobEvents.common.block.BlockSummonersHelm;
import furgl.mobEvents.common.block.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;

public class TileEntitySummonersHelm extends TileEntity implements ITickable
{
	private int ticksExisted;
	private BlockSummonersHelm block;
	
	public TileEntitySummonersHelm()
	{
		
	}
	
	public TileEntitySummonersHelm(BlockSummonersHelm block)
	{
		this.block = block;
		this.ticksExisted = 0;
	}
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
	{
	    return (oldState.getBlock() != newSate.getBlock());
	}

	@Override
	public void update() {
		if (this.ticksExisted % 30 == 0 && this.ticksExisted > 0 && block != null)
		{
			int meta = block.getMetaFromState(this.worldObj.getBlockState(pos));
			if (block.isLit)
				this.worldObj.setBlockState(pos, ModBlocks.summoners_helm.getStateFromMeta(meta));
			else
				this.worldObj.setBlockState(pos, ModBlocks.lit_summoners_helm.getStateFromMeta(meta));
		}
		this.ticksExisted++;
	}
}
