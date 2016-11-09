package furgl.mobEvents.common.entity.ZombieApocalypse;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityZombieRider extends EntityEventZombie
{
	public EntityZombieRider(World world) 
	{
		super(world);
		this.progressOnDeath = 6;
	}

	@Override
	public void setBookDescription()
	{
		this.bookDescription = "Rides a Zombie Horse into battle.";
		this.addDrops(Items.NAME_TAG, 4);
		this.addDrops(Items.SADDLE, 4);
		this.addDrops(Items.IRON_HORSE_ARMOR, 3);
		this.addDrops(Items.GOLDEN_HORSE_ARMOR, 2);
		this.addDrops(Items.DIAMOND_HORSE_ARMOR, 1);
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
	}

	@Override
	public double getYOffset()
	{
		return super.getYOffset() + (this.isChild() ? 0.2D : -0.1D);
	}

	@Override
	public void onUpdate()
	{
		if (!this.worldObj.isRemote && !this.isRiding())
			this.setDead();

		super.onUpdate();
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) 
	{ 
		ItemStack sword = new ItemStack(Items.STONE_SWORD);
		EnchantmentHelper.addRandomEnchantment(rand, sword, 1, true);
		this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, sword);
		this.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(Items.CHAINMAIL_BOOTS));
		this.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(Items.CHAINMAIL_LEGGINGS));
		this.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Items.CHAINMAIL_CHESTPLATE));
		this.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Items.CHAINMAIL_HELMET));
		super.setEquipmentBasedOnDifficulty(difficulty);
		for (int i=0; i<EntityEquipmentSlot.values().length; i++)
			this.setDropChance(EntityEquipmentSlot.values()[i], 0.08f);
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata)
	{
		super.onInitialSpawn(difficulty, livingdata);
		this.setCustomNameTag("Zombie Rider");
		EntityZombieHorse horse = new EntityZombieHorse(this.worldObj);
		horse.onInitialSpawn(this.worldObj.getDifficultyForLocation(this.getPosition()), (IEntityLivingData)null);
		if (this.rand.nextInt(20) == 0)
		{
			this.setChild(true);
			horse.setGrowingAge(-24000);
		}
		horse.copyLocationAndAnglesFrom(this);
		this.worldObj.spawnEntityInWorld(horse);
		this.startRiding(horse);
		return livingdata;
	}
}
