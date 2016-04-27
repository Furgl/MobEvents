package furgl.mobEvents.common.entity.ZombieApocalypse;

import java.util.ArrayList;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityRiderZombie extends EntityEventZombie
{
	public EntityRiderZombie(World world) 
	{
		super(world);
		this.setBookDescription();
		this.progressOnDeath = 5;
	}
	
	@Override
	public void setBookDescription()
	{
		this.bookDescription = "Rides a Zombie Horse into battle.";
		this.bookDrops = new ArrayList<Item>();
		bookDrops.add(Items.name_tag);bookDrops.add(Items.name_tag);bookDrops.add(Items.name_tag);bookDrops.add(Items.name_tag);
		bookDrops.add(Items.saddle);bookDrops.add(Items.saddle);bookDrops.add(Items.saddle);bookDrops.add(Items.saddle);
		bookDrops.add(Items.iron_horse_armor);bookDrops.add(Items.iron_horse_armor);bookDrops.add(Items.iron_horse_armor);
		bookDrops.add(Items.golden_horse_armor);bookDrops.add(Items.golden_horse_armor);
		bookDrops.add(Items.diamond_horse_armor);
	}
	
	@Override
	protected void addRandomDrop()
	{
		this.dropItem(bookDrops.get(rand.nextInt(bookDrops.size())), 1);
	}
	
	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(2.0D);
	}

	@Override
	public double getYOffset()
	{
		return super.getYOffset() + (this.isChild() ? 0.2D : -0.1D);
	}

	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) 
	{ 
		ItemStack sword = new ItemStack(Items.stone_sword);
		EnchantmentHelper.addRandomEnchantment(rand, sword, 1);
		this.setCurrentItemOrArmor(0, sword);
		this.setCurrentItemOrArmor(1, new ItemStack(Items.chainmail_boots));
		this.setCurrentItemOrArmor(2, new ItemStack(Items.chainmail_leggings));
		this.setCurrentItemOrArmor(3, new ItemStack(Items.chainmail_chestplate));
		this.setCurrentItemOrArmor(4, new ItemStack(Items.chainmail_helmet));
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
		this.mountEntity(horse);
		return livingdata;
	}
}
