package furgl.mobEvents.common.item;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.Events.Event;
import furgl.mobEvents.common.entity.IEventMob;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemEventMobSpawnEgg extends Item 
{
	public String entityName;

	public ItemEventMobSpawnEgg(String entityName)
	{
		this.entityName = entityName;
	}

	public Event getEvent() {
		return ((IEventMob)EntityList.createEntityByName(entityName, MobEvents.proxy.world)).getEvent();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasEffect(ItemStack stack)
	{
		return this.entityName.contains("Boss");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
	{
		tooltip.add(TextFormatting.BLUE+"Only spawns during "+this.getEvent());
		if (this.entityName.contains("Boss"))
			tooltip.add(TextFormatting.GOLD+"Takes up 5x5x3 area");

		super.addInformation(stack, player, tooltip, advanced);
	}

	@Override
	//mostly copied from ItemMonsterPlacer
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (worldIn.isRemote)
		{
            return EnumActionResult.SUCCESS;
		}
		else if (!playerIn.canPlayerEdit(pos.offset(side), side, stack) || MobEvents.proxy.getWorldData().currentEvent.getClass() != this.getEvent().getClass())
		{
            return EnumActionResult.FAIL;
		}
		else
		{
			IBlockState iblockstate = worldIn.getBlockState(pos);

			if (iblockstate.getBlock() == Blocks.MOB_SPAWNER)
			{
				TileEntity tileentity = worldIn.getTileEntity(pos);

				if (tileentity instanceof TileEntityMobSpawner)
				{
					MobSpawnerBaseLogic mobspawnerbaselogic = ((TileEntityMobSpawner)tileentity).getSpawnerBaseLogic();
					mobspawnerbaselogic.setEntityName(this.entityName);
					tileentity.markDirty();
					worldIn.notifyBlockUpdate(pos, iblockstate, iblockstate, 3);

					if (!playerIn.capabilities.isCreativeMode)
					{
						--stack.stackSize;
					}

                    return EnumActionResult.SUCCESS;
				}
			}

			pos = pos.offset(side);
			double d0 = 0.0D;

			if (side == EnumFacing.UP && iblockstate instanceof BlockFence)
			{
				d0 = 0.5D;
			}

			Entity entity = spawnCreature(worldIn, this.entityName, (double)pos.getX() + 0.5D, (double)pos.getY() + d0, (double)pos.getZ() + 0.5D);

			if (entity != null)
			{
				if (entity instanceof EntityLivingBase && stack.hasDisplayName())
				{
					entity.setCustomNameTag(stack.getDisplayName());
				}

				if (!playerIn.capabilities.isCreativeMode)
				{
					--stack.stackSize;
				}
			}

            return EnumActionResult.SUCCESS;
		}
	}

	@Override
	//100% copied from ItemMonsterPlacer
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
        if (worldIn.isRemote)
        {
            return new ActionResult(EnumActionResult.PASS, itemStackIn);
        }
        else
        {
            RayTraceResult raytraceresult = this.rayTrace(worldIn, playerIn, true);

            if (raytraceresult != null && raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK)
            {
                BlockPos blockpos = raytraceresult.getBlockPos();

                if (!(worldIn.getBlockState(blockpos).getBlock() instanceof BlockLiquid))
                {
                    return new ActionResult(EnumActionResult.PASS, itemStackIn);
                }
                else if (worldIn.isBlockModifiable(playerIn, blockpos) && playerIn.canPlayerEdit(blockpos, raytraceresult.sideHit, itemStackIn))
                {
                    Entity entity = spawnCreature(worldIn, getEntityIdFromItem(itemStackIn), (double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.5D, (double)blockpos.getZ() + 0.5D);

                    if (entity == null)
                    {
                        return new ActionResult(EnumActionResult.PASS, itemStackIn);
                    }
                    else
                    {
                        if (entity instanceof EntityLivingBase && itemStackIn.hasDisplayName())
                        {
                            entity.setCustomNameTag(itemStackIn.getDisplayName());
                        }

                        applyItemEntityDataToEntity(worldIn, playerIn, itemStackIn, entity);

                        if (!playerIn.capabilities.isCreativeMode)
                        {
                            --itemStackIn.stackSize;
                        }

                        playerIn.addStat(StatList.getObjectUseStats(this));
                        return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
                    }
                }
                else
                {
                    return new ActionResult(EnumActionResult.FAIL, itemStackIn);
                }
            }
            else
            {
                return new ActionResult(EnumActionResult.PASS, itemStackIn);
            }
        }
    }
	
	//100% copied from ItemMonsterPlacer
	public static void applyItemEntityDataToEntity(World entityWorld, @Nullable EntityPlayer player, ItemStack stack, @Nullable Entity targetEntity)
    {
        MinecraftServer minecraftserver = entityWorld.getMinecraftServer();

        if (minecraftserver != null && targetEntity != null)
        {
            NBTTagCompound nbttagcompound = stack.getTagCompound();

            if (nbttagcompound != null && nbttagcompound.hasKey("EntityTag", 10))
            {
                if (!entityWorld.isRemote && targetEntity.ignoreItemEntityData() && (player == null || !minecraftserver.getPlayerList().canSendCommands(player.getGameProfile())))
                {
                    return;
                }

                NBTTagCompound nbttagcompound1 = targetEntity.writeToNBT(new NBTTagCompound());
                UUID uuid = targetEntity.getUniqueID();
                nbttagcompound1.merge(nbttagcompound.getCompoundTag("EntityTag"));
                targetEntity.setUniqueId(uuid);
                targetEntity.readFromNBT(nbttagcompound1);
            }
        }
    }
	
	//100% copied from ItemMonsterPlacer
	public static String getEntityIdFromItem(ItemStack stack)
    {
        NBTTagCompound nbttagcompound = stack.getTagCompound();

        if (nbttagcompound == null)
        {
            return null;
        }
        else if (!nbttagcompound.hasKey("EntityTag", 10))
        {
            return null;
        }
        else
        {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("EntityTag");
            return !nbttagcompound1.hasKey("id", 8) ? null : nbttagcompound1.getString("id");
        }
    }

	/**
	 * Spawns the creature specified by the egg's type in the location specified by the last three parameters.
	 * Parameters: world, entityID, x, y, z.
	 */
	public static Entity spawnCreature(World worldIn, String name, double x, double y, double z)
	{
		if (name != null && !EntityList.isStringValidEntityName(name))
			return null;
		else
		{
			Entity entity = null;

			for (int j = 0; j < 1; ++j)
			{
				entity = EntityList.createEntityByName(name, worldIn);

                if (entity instanceof EntityLivingBase)
                {
                    EntityLiving entityliving = (EntityLiving)entity;
                    entity.setLocationAndAngles(x, y, z, MathHelper.wrapDegrees(worldIn.rand.nextFloat() * 360.0F), 0.0F);
                    entityliving.rotationYawHead = entityliving.rotationYaw;
                    entityliving.renderYawOffset = entityliving.rotationYaw;
                    entityliving.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(entityliving)), (IEntityLivingData)null);
                    worldIn.spawnEntityInWorld(entity);
                    entityliving.playLivingSound();
                }
			}
			return entity;
		}
	}
}
