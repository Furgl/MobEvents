package furgl.mobEvents.common.entity.SkeletalUprising;

import furgl.mobEvents.common.Events.Event;
import furgl.mobEvents.common.Events.ZombieApocalypse;
import furgl.mobEvents.common.entity.ModEntities;
import furgl.mobEvents.common.world.WorldData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.HorseType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

public class EntitySkeletonHorse extends EntityHorse
{
	private int attackTime;

	public EntitySkeletonHorse(World world) {
		super(world);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(60.0D);
	}
	
	@Override
	public ItemStack getPickedResult(RayTraceResult target)	{
		return ModEntities.getSpawnEgg(this.getClass());
	}
	
	protected boolean canDespawn() {
		return this.isBeingRidden();
	}

	@Override
	public void onLivingUpdate() {
		if (this.getRidingEntity() != null && this.getRidingEntity() instanceof EntitySkeletonRider && ((EntitySkeletonRider) this.getRidingEntity()).getAttackTarget() != null)
		{
			if (this.worldObj.isRemote)
				((EntitySkeletonRider)this.getRidingEntity()).faceEntity(((EntitySkeletonRider)this.getRidingEntity()).getAttackTarget(), 360, 360);
			else
			{
				this.getNavigator().tryMoveToEntityLiving(((EntitySkeletonRider) this.getRidingEntity()).getAttackTarget(), 1.5D);
				if (this.motionX == 0 && this.motionZ == 0)
					this.moveEntityWithHeading(0, 0.2f);
			}
		}

		if (this.getRidingEntity() instanceof EntitySkeletonRider && this.ticksExisted % 20 == 0 && WorldData.get(worldObj).currentEvent.getClass() != ZombieApocalypse.class && !this.worldObj.isRemote && WorldData.get(worldObj).currentEvent.getClass() != Event.CHAOTIC_TURMOIL.getClass())
			this.dealFireDamage((int) (this.getMaxHealth()/3));

		if (this.attackTime > 0)
			this.attackTime--;

		super.onLivingUpdate();
	}

	@Override
	public void onDeath(DamageSource cause)	{
		super.onDeath(cause);
		if (this.getRidingEntity() instanceof EntitySkeletonRider)
			((EntitySkeletonRider)this.getRidingEntity()).attackEntityFrom(cause, 999);
	}

	@Override
	protected void collideWithEntity(Entity entity) {
		super.collideWithEntity(entity);
		if (this.attackTime == 0 && this.getHealth() > 0 && entity instanceof EntityPlayer && !(entity instanceof FakePlayer) && !this.worldObj.isRemote && this.getRidingEntity() != null && this.getRidingEntity() instanceof EntitySkeletonRider && ((EntitySkeletonRider) this.getRidingEntity()).getAttackTarget() != null)
		{
			this.attackTime = 20;
			((EntitySkeletonRider) this.getRidingEntity()).swingArm(EnumHand.MAIN_HAND);
			((EntitySkeletonRider) this.getRidingEntity()).attackEntityAsMob(((EntitySkeletonRider) this.getRidingEntity()).getAttackTarget());
		}
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {		
		super.onInitialSpawn(difficulty, livingdata);

		this.setType(HorseType.SKELETON);
		this.setHorseTamed(true);
		this.setGrowingAge(0);

		return livingdata;
	}
}