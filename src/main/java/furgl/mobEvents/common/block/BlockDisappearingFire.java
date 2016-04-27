package furgl.mobEvents.common.block;

import java.util.Random;

import net.minecraft.block.BlockFire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDisappearingFire extends BlockFire
{
	@Override
	public int tickRate(World worldIn)
	{
		return 300;
	}

	@Override
	public boolean isBurning(IBlockAccess world, BlockPos pos)
	{
		return true;
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
		worldIn.setBlockToAir(pos);
	}
}
