package furgl.mobEvents.common.entity.ZombieApocalypse;

import java.util.ArrayList;

import furgl.mobEvents.common.block.ModBlocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
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
	}
	
	@Override
	public void setBookDescription()
	{
		this.bookDescription = "Likes lighting players on fire.";
		this.bookDrops = new ArrayList<Item>();
		bookDrops.add(Items.flint_and_steel);bookDrops.add(Items.flint_and_steel);bookDrops.add(Items.flint_and_steel);
		bookDrops.add(Item.getItemFromBlock(Blocks.torch));bookDrops.add(Item.getItemFromBlock(Blocks.torch));bookDrops.add(Item.getItemFromBlock(Blocks.torch));
		bookDrops.add(Items.coal);bookDrops.add(Items.coal);bookDrops.add(Items.coal);
		bookDrops.add(Item.getItemFromBlock(Blocks.coal_block));
	}
	
	@Override
	protected void addRandomDrop()
	{
		Item item = bookDrops.get(rand.nextInt(bookDrops.size()));
		if (item == Items.coal || item == Item.getItemFromBlock(Blocks.torch))
			this.dropItem(item, rand.nextInt(3)+1);
		else
			this.dropItem(item, 1);
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
		for (int i=0; i<5; i++)
			this.setEquipmentDropChance(i, 0.01f);
		for (int i=0; i<4; i++)
			((ItemArmor)this.getCurrentArmor(i).getItem()).setColor(this.getCurrentArmor(i), this.armorColor);
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
