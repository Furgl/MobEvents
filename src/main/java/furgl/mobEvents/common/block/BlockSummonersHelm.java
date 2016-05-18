package furgl.mobEvents.common.block;

import java.util.List;

import furgl.mobEvents.common.item.ModItems;
import furgl.mobEvents.common.tileentity.TileEntitySummonersHelm;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.block.state.pattern.BlockStateHelper;
import net.minecraft.block.state.pattern.FactoryBlockPattern;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSummonersHelm extends BlockPumpkin implements ITileEntityProvider
{
	public boolean isLit;

	public BlockSummonersHelm(boolean isLit)
	{
		super();
		this.isLit = isLit;
		this.setTickRandomly(true);
		if (isLit)
			this.setLightLevel(1f);
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos)
	{
		ItemStack stack = new ItemStack(ModItems.summonersHelm);
		stack.addEnchantment(Enchantment.fireProtection, 5);
		return stack;
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		List<ItemStack> ret = new java.util.ArrayList<ItemStack>();
		ItemStack stack = new ItemStack(ModItems.summonersHelm, 1, this.damageDropped(state));
		stack.addEnchantment(Enchantment.fireProtection, 5);
		ret.add(stack);
		return ret;
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
	{
		super.onBlockAdded(worldIn, pos, state);
		this.trySpawnGolem(worldIn, pos);
	}

	private void trySpawnGolem(World worldIn, BlockPos pos)
	{
		BlockPattern.PatternHelper blockpattern$patternhelper;
		BlockPattern pattern = FactoryBlockPattern.start().aisle(new String[] {"^", "#", "#"}).where('^', BlockWorldState.hasState(BlockStateHelper.forBlock(ModBlocks.summoners_helm))).where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.snow))).build();
		if ((blockpattern$patternhelper = pattern.match(worldIn, pos)) != null)
		{
			for (int i = 0; i < pattern.getThumbLength(); ++i)
			{
				BlockWorldState blockworldstate = blockpattern$patternhelper.translateOffset(0, i, 0);
				worldIn.setBlockState(blockworldstate.getPos(), Blocks.air.getDefaultState(), 2);
			}

			EntitySnowman entitysnowman = new EntitySnowman(worldIn);
			BlockPos blockpos1 = blockpattern$patternhelper.translateOffset(0, 2, 0).getPos();
			entitysnowman.setLocationAndAngles((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.05D, (double)blockpos1.getZ() + 0.5D, 0.0F, 0.0F);
			//entitysnowman.setCurrentItemOrArmor(0, new ItemStack(ModItems.summonersHelm)); //added
			worldIn.spawnEntityInWorld(entitysnowman);

			for (int j = 0; j < 120; ++j)
			{
				worldIn.spawnParticle(EnumParticleTypes.SNOW_SHOVEL, (double)blockpos1.getX() + worldIn.rand.nextDouble(), (double)blockpos1.getY() + worldIn.rand.nextDouble() * 2.5D, (double)blockpos1.getZ() + worldIn.rand.nextDouble(), 0.0D, 0.0D, 0.0D, new int[0]);
			}

			for (int i1 = 0; i1 < pattern.getThumbLength(); ++i1)
			{
				BlockWorldState blockworldstate1 = blockpattern$patternhelper.translateOffset(0, i1, 0);
				worldIn.notifyNeighborsRespectDebug(blockworldstate1.getPos(), Blocks.air);
			}
		}
		pattern = FactoryBlockPattern.start().aisle(new String[] {"~^~", "###", "~#~"}).where('^', BlockWorldState.hasState(BlockStateHelper.forBlock(ModBlocks.summoners_helm))).where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.iron_block))).where('~', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.air))).build();
		if ((blockpattern$patternhelper = pattern.match(worldIn, pos)) != null)
		{
			for (int k = 0; k < pattern.getPalmLength(); ++k)
			{
				for (int l = 0; l < pattern.getThumbLength(); ++l)
				{
					worldIn.setBlockState(blockpattern$patternhelper.translateOffset(k, l, 0).getPos(), Blocks.air.getDefaultState(), 2);
				}
			}

			BlockPos blockpos = blockpattern$patternhelper.translateOffset(1, 2, 0).getPos();
			EntityIronGolem entityirongolem = new EntityIronGolem(worldIn);
			entityirongolem.setPlayerCreated(true);
			entityirongolem.setLocationAndAngles((double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.05D, (double)blockpos.getZ() + 0.5D, 0.0F, 0.0F);
			entityirongolem.setCurrentItemOrArmor(0, new ItemStack(Item.getItemFromBlock(ModBlocks.summoners_helm))); //added
			worldIn.spawnEntityInWorld(entityirongolem);

			for (int j1 = 0; j1 < 120; ++j1)
			{
				worldIn.spawnParticle(EnumParticleTypes.SNOWBALL, (double)blockpos.getX() + worldIn.rand.nextDouble(), (double)blockpos.getY() + worldIn.rand.nextDouble() * 3.9D, (double)blockpos.getZ() + worldIn.rand.nextDouble(), 0.0D, 0.0D, 0.0D, new int[0]);
			}

			for (int k1 = 0; k1 < pattern.getPalmLength(); ++k1)
			{
				for (int l1 = 0; l1 < pattern.getThumbLength(); ++l1)
				{
					BlockWorldState blockworldstate2 = blockpattern$patternhelper.translateOffset(k1, l1, 0);
					worldIn.notifyNeighborsRespectDebug(blockworldstate2.getPos(), Blocks.air);
				}
			}
		}
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntitySummonersHelm(this);
	}
}
