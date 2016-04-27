package furgl.mobEvents.common.entity.ZombieApocalypse;

import furgl.mobEvents.common.Events.Event;
import furgl.mobEvents.common.Events.ZombieApocalypse;
import furgl.mobEvents.common.item.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

public class EntityZombieHorse extends EntityHorse
{
	private int attackTime;

	public EntityZombieHorse(World world)
	{
		super(world);
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(60.0D);
	}
	
	@Override
	public ItemStack getPickedResult(MovingObjectPosition target)
	{
		return new ItemStack(ModItems.zombieRiderEgg);
	}

	protected boolean canDespawn()
	{
		return this.riddenByEntity != null;
	}

	@Override
	public void onLivingUpdate()
	{
		if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityRiderZombie && ((EntityRiderZombie) this.riddenByEntity).getAttackTarget() != null)
		{
			if (this.worldObj.isRemote)
				((EntityRiderZombie)this.riddenByEntity).faceEntity(((EntityRiderZombie)this.riddenByEntity).getAttackTarget(), 360, 360);
			else
			{
				this.getNavigator().tryMoveToEntityLiving(((EntityRiderZombie) this.riddenByEntity).getAttackTarget(), 1.5D);
				if (this.motionX == 0 && this.motionZ == 0)
					this.moveEntityWithHeading(0, 0.2f);
			}
		}

		if (this.riddenByEntity instanceof EntityRiderZombie && this.ticksExisted % 20 == 0 && Event.currentEvent.getClass() != ZombieApocalypse.class && !this.worldObj.isRemote)
			this.dealFireDamage((int) (this.getMaxHealth()/3));

		if (this.attackTime > 0)
			this.attackTime--;

		super.onLivingUpdate();
	}

	@Override
	public void onDeath(DamageSource cause)
	{
		super.onDeath(cause);
		if (this.riddenByEntity instanceof EntityRiderZombie)
			((EntityRiderZombie)this.riddenByEntity).attackEntityFrom(cause, 999);
	}

	@Override
	protected void collideWithEntity(Entity entity)
	{
		super.collideWithEntity(entity);
		if (this.attackTime == 0 && this.getHealth() > 0 && entity instanceof EntityPlayer && !(entity instanceof FakePlayer) && !this.worldObj.isRemote && this.riddenByEntity != null && this.riddenByEntity instanceof EntityRiderZombie && ((EntityRiderZombie) this.riddenByEntity).getAttackTarget() != null)
		{
			this.attackTime = 20;
			((EntityRiderZombie) this.riddenByEntity).swingItem();
			((EntityRiderZombie) this.riddenByEntity).attackEntityAsMob(((EntityRiderZombie) this.riddenByEntity).getAttackTarget());
		}
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata)
	{
		super.onInitialSpawn(difficulty, livingdata);

		this.setHorseType(3);
		this.setHorseTamed(true);
		this.setGrowingAge(0);

		return livingdata;
	}
}
