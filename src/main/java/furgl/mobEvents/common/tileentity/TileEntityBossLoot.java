package furgl.mobEvents.common.tileentity;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.Events.Event;
import furgl.mobEvents.common.event.EventFogEvent;
import furgl.mobEvents.common.inventory.ContainerBossLoot;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityBossLoot extends TileEntityLockable implements ITickable, IInventory, IChunkLoader
{
	private final List<TileEntityBossLoot.BeamSegment> beamSegments = Lists.<TileEntityBossLoot.BeamSegment>newArrayList();
	@SideOnly(Side.CLIENT)
	private long beamRenderCounter;
	@SideOnly(Side.CLIENT)
	private float field_146014_j;
	private ItemStack[] chestContents = new ItemStack[27];
	/** The current angle of the lid (between 0 and 1) */
	public float lidAngle;
	/** The angle of the lid last tick */
	public float prevLidAngle;
	/** The number of players currently using this chest */
	public int numPlayersUsing;
	/** Server sync counter (once per 20 ticks) */
	private int ticksSinceSync;
	private BlockChest.Type cachedChestType;
	private String customName;

	public TileEntityBossLoot()
	{
		super();
	}

	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
	{
		return new ContainerBossLoot(playerInventory, this, playerIn);
	}

	@Override
	public void onChunkUnload()
	{
		/*if (!this.worldObj.isRemote && this.worldObj.getClosestPlayerToEntity(this, -1) != null)
		{
			if (MobEvents.DEBUG)
				System.out.println("Unloaded "+ pos);
			EntityPlayer player = this.worldObj.getClosestPlayerToEntity(pos.getX(), pos.getY(), pos.getZ(), -1);
			if (player != null && MobEvents.proxy.getWorldData().currentEvent.boss != null && this.getDistanceSq(player.posX, player.posY, player.posZ) > 60D && MobEvents.proxy.getWorldData().currentEvent.boss.getPosition().equals(this.getPos().down(2)))
			{
				if (MobEvents.proxy.getWorldData().currentEvent.boss != null && MobEvents.proxy.getWorldData().currentEvent.boss.stage == 2)
					MobEvents.proxy.getWorldData().currentEvent.boss.tpPlayerAndBoss(this.worldObj.getClosestPlayerToEntity(pos.getX(), pos.getY(), pos.getZ(), -1));
			}
		}*/
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack)
	{
		return false;
	}

	/**
	 * Returns the number of slots in the inventory.
	 */
	public int getSizeInventory()
	{
		return 9;
	}

	/**
	 * Returns the stack in the given slot.
	 */
	public ItemStack getStackInSlot(int index)
	{
		return this.chestContents[index];
	}

	/**
	 * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
	 */
	public ItemStack decrStackSize(int index, int count)
	{
		if (this.chestContents[index] != null)
		{
			if (this.chestContents[index].stackSize <= count)
			{
				ItemStack itemstack1 = this.chestContents[index];
				this.chestContents[index] = null;
				this.markDirty();
				return itemstack1;
			}
			else
			{
				ItemStack itemstack = this.chestContents[index].splitStack(count);

				if (this.chestContents[index].stackSize == 0)
				{
					this.chestContents[index] = null;
				}

				this.markDirty();
				return itemstack;
			}
		}
		else
		{
			return null;
		}
	}

	/**
	 * Removes a stack from the given slot and returns it.
	 */
	public ItemStack removeStackFromSlot(int index)
	{
		if (this.chestContents[index] != null)
		{
			ItemStack itemstack = this.chestContents[index];
			this.chestContents[index] = null;
			return itemstack;
		}
		else
		{
			return null;
		}
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
	 */
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		this.chestContents[index] = stack;

		if (stack != null && stack.stackSize > this.getInventoryStackLimit())
		{
			stack.stackSize = this.getInventoryStackLimit();
		}

		this.markDirty();
	}

	/**
	 * Get the name of this object. For players this returns their username
	 */
	public String getName()
	{
		return "Boss Loot";
	}

	/**
	 * Returns true if this thing is named
	 */
	public boolean hasCustomName()
	{
		return this.customName != null && this.customName.length() > 0;
	}

	public void setCustomName(String name)
	{
		this.customName = name;
	}

	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		NBTTagList nbttaglist = compound.getTagList("Items", 10);
		this.chestContents = new ItemStack[this.getSizeInventory()];

		if (compound.hasKey("CustomName", 8))
		{
			this.customName = compound.getString("CustomName");
		}

		for (int i = 0; i < nbttaglist.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;

			if (j >= 0 && j < this.chestContents.length)
			{
				this.chestContents[j] = ItemStack.loadItemStackFromNBT(nbttagcompound);
			}
		}
	}

	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < this.chestContents.length; ++i)
		{
			if (this.chestContents[i] != null)
			{
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte)i);
				this.chestContents[i].writeToNBT(nbttagcompound);
				nbttaglist.appendTag(nbttagcompound);
			}
		}

		compound.setTag("Items", nbttaglist);

		if (this.hasCustomName())
		{
			compound.setString("CustomName", this.customName);
		}
		return compound;
	}

	/**
	 * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
	 */
	public int getInventoryStackLimit()
	{
		return 64;
	}

	/**
	 * Do not make give this method the name canInteractWith because it clashes with Container
	 */
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return this.worldObj.getTileEntity(this.pos) != this ? false : player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
	}

	/**
	 * Like the old updateEntity(), except more generic.
	 */
	public void update()
	{
		//beam
		//if (this.worldObj.getTotalWorldTime() % 80L == 0L)
		this.updateSegmentColors();

		//added
		if (this.numPlayersUsing < 0)
			this.numPlayersUsing = 0;
		
		int i = this.pos.getX();
		int j = this.pos.getY();
		int k = this.pos.getZ();
		++this.ticksSinceSync;
		//delete if empty
		if (this.prevLidAngle > 0 && this.lidAngle <= 0)
		{
			boolean empty = true;
			for (int l=0; l<this.getSizeInventory(); l++)
				if (this.getStackInSlot(l) != null)
					empty = false;
			if (empty) 
			{
				if (this.worldObj.isRemote)
					for (int l=0; l<30; l++)
						this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.pos.getX()+0.5D+this.worldObj.rand.nextDouble()-0.5D, this.pos.getY()+1+this.worldObj.rand.nextDouble()-0.5D, this.pos.getZ()+0.5D+this.worldObj.rand.nextDouble()-0.5D, 0, 0, 0, 0);
				else {
					this.worldObj.playSound(null, i, j, k, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.5F);
					this.worldObj.setBlockToAir(this.pos);
				}
			}
		}

		if (!this.worldObj.isRemote && this.numPlayersUsing != 0 && (this.ticksSinceSync + i + j + k) % 200 == 0)
		{
			this.numPlayersUsing = 0;
			float f = 5.0F;

			for (EntityPlayer entityplayer : this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB((double)((float)i - f), (double)((float)j - f), (double)((float)k - f), (double)((float)(i + 1) + f), (double)((float)(j + 1) + f), (double)((float)(k + 1) + f))))
			{
				if (entityplayer.openContainer instanceof ContainerChest)
				{
					IInventory iinventory = ((ContainerChest)entityplayer.openContainer).getLowerChestInventory();

					if (iinventory == this || iinventory instanceof InventoryLargeChest && ((InventoryLargeChest)iinventory).isPartOfLargeChest(this))
					{
						++this.numPlayersUsing;
					}
				}
			}
		}

		this.prevLidAngle = this.lidAngle;
		float f1 = 0.1F;

		if (this.numPlayersUsing > 0 && this.lidAngle == 0.0F)
		{
			double d1 = (double)i + 0.5D;
			double d2 = (double)k + 0.5D;

            this.worldObj.playSound((EntityPlayer)null, d1, (double)j + 0.5D, d2, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
		}

		if (this.numPlayersUsing == 0 && this.lidAngle > 0.0F || this.numPlayersUsing > 0 && this.lidAngle < 1.0F)
		{
			float f2 = this.lidAngle;

			if (this.numPlayersUsing > 0)
				this.lidAngle += f1;
			else
				this.lidAngle -= f1;

			if (this.lidAngle > 1.0F)
				this.lidAngle = 1.0F;

			float f3 = 0.5F;

			if (this.lidAngle < f3 && f2 >= f3)
			{
				double d3 = (double)i + 0.5D;
				double d0 = (double)k + 0.5D;
				//delete if empty
				boolean empty = true;
				for (i=0; i<this.getSizeInventory(); i++)
					if (this.getStackInSlot(i) != null)
						empty = false;
				if (!empty)
	                this.worldObj.playSound((EntityPlayer)null, d3, (double)j + 0.5D, d0, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
			}

			if (this.lidAngle < 0.0F)
				this.lidAngle = 0.0F;
		}
	}

	public boolean receiveClientEvent(int id, int type)
	{
		if (id == 1)
		{
			this.numPlayersUsing = type;
			return true;
		}
		else
		{
			return super.receiveClientEvent(id, type);
		}
	}

	public void openInventory(EntityPlayer player)
	{
		if (!player.isSpectator())
		{
			if (this.numPlayersUsing < 0)
				this.numPlayersUsing = 0;

			++this.numPlayersUsing;
			this.worldObj.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
			this.worldObj.notifyNeighborsOfStateChange(this.pos, this.getBlockType());
			this.worldObj.notifyNeighborsOfStateChange(this.pos.down(), this.getBlockType());
		}
	}

	public void closeInventory(EntityPlayer player)
	{
		if (!player.isSpectator() && this.getBlockType() instanceof BlockChest)
		{
			--this.numPlayersUsing;
			this.worldObj.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
			this.worldObj.notifyNeighborsOfStateChange(this.pos, this.getBlockType());
			this.worldObj.notifyNeighborsOfStateChange(this.pos.down(), this.getBlockType());
		}
	}


	/**
	 * invalidates a tile entity
	 */
	public void invalidate()
	{
		super.invalidate();
		this.updateContainingBlockInfo();
	}

	public BlockChest.Type getChestType()
    {
        if (this.cachedChestType == null)
        {
            if (this.worldObj == null || !(this.getBlockType() instanceof BlockChest))
            {
                return BlockChest.Type.BASIC;
            }

            this.cachedChestType = ((BlockChest)this.getBlockType()).chestType;
        }

        return this.cachedChestType;
    }

	public String getGuiID()
	{
		return "minecraft:chest";
	}

	public int getField(int id)
	{
		return 0;
	}

	public void setField(int id, int value)
	{
	}

	public int getFieldCount()
	{
		return 0;
	}

	public void clear()
	{
		for (int i = 0; i < this.chestContents.length; ++i)
			this.chestContents[i] = null;
	}

	//TODO beam

	private void updateSegmentColors()
	{
		int j = this.pos.getX();
		int k = this.pos.getY();
		int l = this.pos.getZ();
		this.beamSegments.clear();
		//this.isComplete = true;
		TileEntityBossLoot.BeamSegment tileentitybeacon$beamsegment;
		if (MobEvents.proxy.getWorldData().currentEvent.getClass() != Event.class)
			tileentitybeacon$beamsegment = new TileEntityBossLoot.BeamSegment(EventFogEvent.currentColors/*EntitySheep.func_175513_a(EnumDyeColor.WHITE)*/);
		else
			tileentitybeacon$beamsegment = new TileEntityBossLoot.BeamSegment(new float[]{1, 1, 1});
		this.beamSegments.add(tileentitybeacon$beamsegment);
		boolean flag = true;
		BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

		for (int i1 = k + 1; i1 < 256; ++i1)
		{
			IBlockState iblockstate = this.worldObj.getBlockState(blockpos$mutableblockpos.setPos(j, i1, l));
			float[] afloat;

			if (iblockstate.getBlock() == Blocks.STAINED_GLASS)
			{
				afloat = EntitySheep.getDyeRgb((EnumDyeColor)iblockstate.getValue(BlockStainedGlass.COLOR));
			}
			else
			{
				if (iblockstate.getBlock() != Blocks.STAINED_GLASS)
				{
					if (iblockstate.getLightOpacity(this.worldObj, blockpos$mutableblockpos) >= 15 && iblockstate.getBlock() != Blocks.BEDROCK)
					{
						//this.isComplete = false;
						this.beamSegments.clear();
						break;
					}

					tileentitybeacon$beamsegment.incrementHeight();
					continue;
				}

				afloat = EntitySheep.getDyeRgb((EnumDyeColor)iblockstate.getValue(BlockStainedGlassPane.COLOR));
			}

			if (!flag)
			{
				afloat = new float[] {(tileentitybeacon$beamsegment.getColors()[0] + afloat[0]) / 2.0F, (tileentitybeacon$beamsegment.getColors()[1] + afloat[1]) / 2.0F, (tileentitybeacon$beamsegment.getColors()[2] + afloat[2]) / 2.0F};
			}

			if (Arrays.equals(afloat, tileentitybeacon$beamsegment.getColors()))
			{
				tileentitybeacon$beamsegment.incrementHeight();
			}
			else
			{
				tileentitybeacon$beamsegment = new TileEntityBossLoot.BeamSegment(afloat);
				this.beamSegments.add(tileentitybeacon$beamsegment);
			}

			flag = false;
		}

		if (true/*this.isComplete*/)
		{
			for (int l1 = 1; l1 <= 4; l1++)
			{
				int i2 = k - l1;

				if (i2 < 0)
				{
					break;
				}

				boolean flag1 = true;

				for (int j1 = j - l1; j1 <= j + l1 && flag1; ++j1)
				{
					for (int k1 = l - l1; k1 <= l + l1; ++k1)
					{
						Block block = this.worldObj.getBlockState(new BlockPos(j1, i2, k1)).getBlock();

						if (!block.isBeaconBase(this.worldObj, new BlockPos(j1, i2, k1), getPos()))
						{
							flag1 = false;
							break;
						}
					}
				}

				if (!flag1)
				{
					break;
				}
			}

			/*if (this.levels == 0)
			{
				this.isComplete = false;
			}*/
		}

		/*if (!this.worldObj.isRemote && this.levels == 4 && i < this.levels)
		{
			for (EntityPlayer entityplayer : this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, (new AxisAlignedBB((double)j, (double)k, (double)l, (double)j, (double)(k - 4), (double)l)).expand(10.0D, 5.0D, 10.0D)))
			{
				entityplayer.triggerAchievement(AchievementList.fullBeacon);
			}
		}*/
	}

	@SideOnly(Side.CLIENT)
	public List<TileEntityBossLoot.BeamSegment> getBeamSegments()
	{
		return this.beamSegments;
	}

	@SideOnly(Side.CLIENT)
	public float shouldBeamRender()
	{
		/*if (!this.isComplete)
		{
			return 0.0F;
		}
		else*/
		{
			int i = (int)(this.worldObj.getTotalWorldTime() - this.beamRenderCounter);
			this.beamRenderCounter = this.worldObj.getTotalWorldTime();

			if (i > 1)
			{
				this.field_146014_j -= (float)i / 40.0F;

				if (this.field_146014_j < 0.0F)
				{
					this.field_146014_j = 0.0F;
				}
			}

			this.field_146014_j += 0.025F;

			if (this.field_146014_j > 1.0F)
			{
				this.field_146014_j = 1.0F;
			}

			return this.field_146014_j;
		}
	}

	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared()
	{
		return 65536.0D;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		return super.getRenderBoundingBox().expand(100, 100, 100);
	}

	public static class BeamSegment
	{
		/** RGB (0 to 1.0) colors of this beam segment */
		private final float[] colors;
		private int height;

		public BeamSegment(float[] p_i45669_1_)
		{
			this.colors = p_i45669_1_;
			this.height = 1;
		}

		protected void incrementHeight()
		{
			++this.height;
		}

		/**
		 * Returns RGB (0 to 1.0) colors of this beam segment
		 */
		public float[] getColors()
		{
			return this.colors;
		}

		@SideOnly(Side.CLIENT)
		public int getHeight()
		{
			return this.height;
		}
	}

	@Override
	public Chunk loadChunk(World worldIn, int x, int z) throws IOException {
		return null;
	}

	@Override
	public void saveChunk(World worldIn, Chunk chunkIn) throws MinecraftException, IOException {

	}

	@Override
	public void saveExtraChunkData(World worldIn, Chunk chunkIn) throws IOException {

	}

	@Override
	public void chunkTick() {

	}

	@Override
	public void saveExtraData() {

	}
}
