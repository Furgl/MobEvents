package furgl.mobEvents.common.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class EntityPyromaniacsArrow extends EntityTippedArrow {
	public EntityPyromaniacsArrow(World worldIn) {
		super(worldIn);
		this.setFire(99999);
	}

	public EntityPyromaniacsArrow(World worldIn, EntityLivingBase shooter) {
		super(worldIn, shooter);
		this.setFire(99999);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (!this.worldObj.isRemote && this.worldObj.isAirBlock(this.getPosition()) && this.timeInGround <= 0) 
			this.worldObj.setBlockState(this.getPosition(), Blocks.FIRE.getDefaultState(), 11);
	}
}