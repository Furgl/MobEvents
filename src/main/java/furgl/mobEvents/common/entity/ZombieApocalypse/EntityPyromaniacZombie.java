package furgl.mobEvents.common.entity.ZombieApocalypse;

import furgl.mobEvents.common.block.ModBlocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityPyromaniacZombie extends EntityEventZombie
{
	public EntityPyromaniacZombie(World world) 
	{
		super(world);
		this.setBookDescription();
		this.progressOnDeath = 2;
		this.armorColor = 10027008;
		this.maxSpawnedInChunk = 2;
	}
	
	@Override
	public void setBookDescription()
	{
		this.bookDescription = "Likes lighting players on fire.";
		ItemStack torches = new ItemStack(Item.getItemFromBlock(Blocks.torch), rand.nextInt(3)+1);
		ItemStack coal = new ItemStack(Items.coal, rand.nextInt(3)+1);
		this.addDrops(Items.flint_and_steel, 3);
		this.addDrops(torches, 3);
		this.addDrops(coal, 3);
		this.addDrops(Item.getItemFromBlock(Blocks.coal_block), 1);
	}
	
	@Override
	public boolean attackEntityAsMob(Entity entity)
	{
		if (!this.worldObj.isRemote && this.worldObj.isAirBlock(entity.getPosition()) && World.doesBlockHaveSolidTopSurface(this.worldObj, entity.getPosition().down()))
		{
			this.worldObj.playSoundEffect((double)entity.getPosition().getX() + 0.5D, (double)entity.getPosition().getY() + 0.5D, (double)entity.getPosition().getZ() + 0.5D, "fire.ignite", 1.0F, rand.nextFloat() * 0.4F + 0.8F);
			this.worldObj.setBlockState(entity.getPosition(), ModBlocks.disappearingFire.getDefaultState());
			entity.setFire(3);
		}

		return super.attackEntityAsMob(entity);
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.0D);
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) 
	{ 
		this.setCurrentItemOrArmor(0, new ItemStack(Items.flint_and_steel));
		this.setCurrentItemOrArmor(1, new ItemStack(Items.leather_boots));
		this.setCurrentItemOrArmor(2, new ItemStack(Items.leather_leggings));
		this.setCurrentItemOrArmor(3, new ItemStack(Items.leather_chestplate));
		this.setCurrentItemOrArmor(4, new ItemStack(Items.leather_helmet));
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
