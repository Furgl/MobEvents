package furgl.mobEvents.common.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityFireArrow extends EntityArrow {

	//private boolean inGround;

	public EntityFireArrow(World worldIn) {
		super(worldIn);
		this.setFire(99999);
	}

	public EntityFireArrow(World worldIn, EntityLivingBase shooter, EntityLivingBase p_i1755_3_, float p_i1755_4_, float p_i1755_5_) {
		super(worldIn, shooter);
		this.setFire(99999);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		//manually implement inGround to avoid constant reflection
		/*if (!inGround && !this.worldObj.isRemote) {
			Vec3 vec31 = new Vec3(this.posX, this.posY, this.posZ);
			Vec3 vec3 = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
			MovingObjectPosition movingobjectposition = this.worldObj.rayTraceBlocks(vec31, vec3, false, true, false);
			if (movingobjectposition != null) {
				BlockPos blockpos = movingobjectposition.getBlockPos();
				IBlockState iblockstate = this.worldObj.getBlockState(blockpos);
				Block block = iblockstate.getBlock();

				if (block.getMaterial() != Material.air)
				{
					block.setBlockBoundsBasedOnState(this.worldObj, blockpos);
					AxisAlignedBB axisalignedbb = block.getCollisionBoundingBox(this.worldObj, blockpos, iblockstate);

					if (axisalignedbb != null && axisalignedbb.isVecInside(new Vec3(this.posX, this.posY, this.posZ)))
					{
						this.inGround = true;
						this.worldObj.setBlockState(this.getPosition(), ModBlocks.disappearingFire.getDefaultState());
					}
				}
			}
		}*/
	}

	@Override
	protected ItemStack getArrowStack() {
		return null;
	}
}
