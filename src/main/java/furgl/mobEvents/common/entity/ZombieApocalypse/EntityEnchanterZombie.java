package furgl.mobEvents.common.entity.ZombieApocalypse;

import java.util.ArrayList;

import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityEnchanterZombie extends EntityEventZombie
{
	public EntityEnchanterZombie(World world) 
	{
		super(world);
		this.progressOnDeath = 5;
	}

	@Override
	protected void addRandomDrop()
	{
		ArrayList<Item> list = new ArrayList<Item>();
		list.add(Items.iron_ingot);list.add(Items.iron_ingot);list.add(Items.iron_ingot);
		list.add(Items.gold_ingot);list.add(Items.gold_ingot);list.add(Items.gold_ingot);
		list.add(Items.golden_apple);list.add(Items.golden_apple);list.add(Items.golden_apple);
		list.add(Items.emerald);
		list.add(Items.diamond);
		this.dropItem(list.get(rand.nextInt(list.size())), 1);
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) 
	{ 
		if (this.rand.nextFloat() < (this.worldObj.getDifficulty() == EnumDifficulty.HARD ? 0.9F : 0.5F))
		{
			int i = this.rand.nextInt(4);
			if (i == 0)
				this.setCurrentItemOrArmor(0, new ItemStack(Items.wooden_pickaxe));
			else if (i == 1)
				this.setCurrentItemOrArmor(0, new ItemStack(Items.wooden_axe));
			else if (i == 2)
				this.setCurrentItemOrArmor(0, new ItemStack(Items.stick));
		}
		this.setCurrentItemOrArmor(1, new ItemStack(Items.leather_boots));
		this.setCurrentItemOrArmor(2, new ItemStack(Items.leather_leggings));
		this.setCurrentItemOrArmor(3, new ItemStack(Items.leather_chestplate));
		this.setCurrentItemOrArmor(4, new ItemStack(Items.leather_helmet));
		for (int i=0; i<5; i++)
			this.setEquipmentDropChance(i, 0.01f);
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata)
	{
		super.onInitialSpawn(difficulty, livingdata);
		this.setCustomNameTag("Zombie Enchanter");
		return livingdata;
	}
}
