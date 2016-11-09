package furgl.mobEvents.common.entity.ZombieApocalypse;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityZombiePyromaniac extends EntityEventZombie
{
	public EntityZombiePyromaniac(World world) 
	{
		super(world);
		this.progressOnDeath = 2;
		this.armorColor = 10027008;
		this.maxSpawnedInChunk = 2;
	}
	
	@Override
	public void setBookDescription()
	{
		this.bookDescription = "Likes lighting players on fire.";
		ItemStack torches = new ItemStack(Item.getItemFromBlock(Blocks.TORCH), rand.nextInt(3)+1);
		ItemStack coal = new ItemStack(Items.COAL, rand.nextInt(3)+1);
		this.addDrops(Items.FLINT_AND_STEEL, 3);
		this.addDrops(torches, 3);
		this.addDrops(coal, 3);
		this.addDrops(Item.getItemFromBlock(Blocks.COAL_BLOCK), 1);
	}
	
	@Override
	public boolean attackEntityAsMob(Entity entity)
	{
		if (!this.worldObj.isRemote && this.worldObj.isAirBlock(entity.getPosition()))
		{
			this.worldObj.playSound(null, entity.getPosition(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, rand.nextFloat() * 0.4F + 0.8F);
			this.worldObj.setBlockState(entity.getPosition(), Blocks.FIRE.getDefaultState());
			entity.setFire(3);
		}

		return super.attackEntityAsMob(entity);
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) 
	{ 
		this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.FLINT_AND_STEEL));
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
		this.setChild(true);
		this.isImmuneToFire = true;
		this.setCustomNameTag("Zombie Pyromaniac");
		return livingdata;
	}
}
