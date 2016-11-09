package furgl.mobEvents.common.entity.ZombieApocalypse;

import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityZombieRunt extends EntityEventZombie
{
	public EntityZombieRunt(World world) 
	{
		super(world);
		this.progressOnDeath = 1;
		this.maxSpawnedInChunk = 4;
	}
	
	@Override
	public void setBookDescription()
	{
		this.bookDescription = "A common Zombie warrior.";
		this.addDrops(Items.IRON_INGOT, 3);
		this.addDrops(Items.GOLD_INGOT, 3);
		this.addDrops(Items.GOLDEN_APPLE, 3);
		this.addDrops(Items.EMERALD, 1);
		this.addDrops(Items.DIAMOND, 1);
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) 
	{ 
		if (this.worldObj != null && this.rand.nextFloat() < (this.worldObj.getDifficulty() == EnumDifficulty.HARD ? 0.9F : 0.5F))
		{
			int i = this.rand.nextInt(4);
			if (i == 0)
				this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.WOODEN_PICKAXE));
			else if (i == 1)
				this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.WOODEN_AXE));
			else if (i == 2)
				this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.STICK));
		}
		this.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(Items.LEATHER_BOOTS));
		this.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(Items.LEATHER_LEGGINGS));
		this.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Items.LEATHER_CHESTPLATE));
		this.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));
		super.setEquipmentBasedOnDifficulty(difficulty);
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata)
	{
		super.onInitialSpawn(difficulty, livingdata);
		this.setCustomNameTag("Zombie Runt");
		return livingdata;
	}
}
