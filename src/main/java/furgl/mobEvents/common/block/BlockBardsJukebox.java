package furgl.mobEvents.common.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import furgl.mobEvents.common.MobEvents;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockBardsJukebox extends BlockJukebox
{
	/**Works for complete restarts, not exiting and rejoining world*/
	private boolean playedRecord;
	
	public BlockBardsJukebox()
	{
		super();
		this.blockSoundType = SoundType.STONE;
		this.needsRandomTick = true;
	}

	@Override
	public int tickRate(World worldIn)
	{
		return 1;
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
		if (((Boolean)state.getValue(HAS_RECORD)).booleanValue())
		{
			if (!worldIn.isRemote && !playedRecord) {
                BlockJukebox.TileEntityJukebox blockjukebox$tileentityjukebox = (BlockJukebox.TileEntityJukebox)worldIn.getTileEntity(pos);
                ItemStack stack = blockjukebox$tileentityjukebox.getRecord();
				MobEvents.proxy.playSoundJukebox(((ItemRecord)stack.getItem()).getSound(), worldIn, pos, 3f);
				playedRecord = true;
			}
			
			List<EntityPlayer> players = new ArrayList<EntityPlayer>();
			players = worldIn.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos.getX()-20, pos.getY()-20, pos.getZ()-20, pos.getX()+20, pos.getY()+20, pos.getZ()+20));
			for (EntityPlayer player : players)
			{
				int effect = worldIn.rand.nextInt(5);
				switch(effect)
				{
				case 0:
					player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 920, 0, true, true));
					break;
				case 1:
					player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 920, 0, true, true));
					break;
				case 2:
					player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 920, 0, true, true));
					break;
				case 3:
					player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 920, 0, true, true));
					break;
				case 4:
					player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 920, 1, true, true));
					break;
				}
			}
		}
	}

	@Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (((Boolean)state.getValue(HAS_RECORD)).booleanValue())
		{
			this.dropRecord(worldIn, pos, state);
			state = state.withProperty(HAS_RECORD, Boolean.valueOf(false));
			worldIn.setBlockState(pos, state, 2);
			return true;
		}
		else if (playerIn.getHeldItemMainhand() != null && playerIn.getHeldItemMainhand().getItem() instanceof ItemRecord)
		{
			if (worldIn.isRemote)
				return true;
			else
			{
				((BlockJukebox)Blocks.JUKEBOX).insertRecord(worldIn, pos, state, playerIn.getHeldItemMainhand());
				MobEvents.proxy.playSoundJukebox(((ItemRecord)playerIn.getHeldItemMainhand().getItem()).getSound(), worldIn, pos, 3f);
				//worldIn.playEventAtEntity((EntityPlayer)null, 1005, pos, Item.getIdFromItem(playerIn.getHeldItem().getItem()));
				playedRecord = true;
				if (!playerIn.capabilities.isCreativeMode)
					--playerIn.getHeldItemMainhand().stackSize;
				playerIn.addStat(StatList.RECORD_PLAYED);
				return true;
			}
		}
		else
			return false;
	}

	//copied directly bc private
	private void dropRecord(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isRemote)
        {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof BlockJukebox.TileEntityJukebox)
            {
                BlockJukebox.TileEntityJukebox blockjukebox$tileentityjukebox = (BlockJukebox.TileEntityJukebox)tileentity;
                ItemStack itemstack = blockjukebox$tileentityjukebox.getRecord();

                if (itemstack != null)
                {
                    worldIn.playEvent(1010, pos, 0);
                    worldIn.playRecord(pos, (SoundEvent)null);
                    blockjukebox$tileentityjukebox.setRecord((ItemStack)null);
                    float f = 0.7F;
                    double d0 = (double)(worldIn.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                    double d1 = (double)(worldIn.rand.nextFloat() * f) + (double)(1.0F - f) * 0.2D + 0.6D;
                    double d2 = (double)(worldIn.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                    ItemStack itemstack1 = itemstack.copy();
                    EntityItem entityitem = new EntityItem(worldIn, (double)pos.getX() + d0, (double)pos.getY() + d1, (double)pos.getZ() + d2, itemstack1);
                    entityitem.setDefaultPickupDelay();
                    worldIn.spawnEntityInWorld(entityitem);
                }
            }
        }
    }
}
