package furgl.mobEvents.common.entity.ZombieApocalypse;


import java.util.ArrayList;

import furgl.mobEvents.common.item.ModItems;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntitySummonerZombie extends EntityEventZombie
{
	private int summonCooldown;
	private ArrayList<EntityEventZombie> summons;

	public EntitySummonerZombie(World world) 
	{
		super(world);
		this.setBookDescription();
		this.progressOnDeath = 5;
		this.maxSpawnedInChunk = 2;
		if (this.worldObj != null)
		{
			int modifier = -1;
			if (this.worldObj.getDifficulty() == EnumDifficulty.HARD)
				modifier++;
			else if (this.worldObj.getDifficulty() == EnumDifficulty.EASY)
				modifier--;
			summons = new ArrayList<EntityEventZombie>();
			for (IEventMob zombie : EntityEventZombie.event.mobs)
				for (int i=this.progressOnDeath-((EntityEventZombie)zombie).progressOnDeath+modifier; i>0; i--)
					summons.add((EntityEventZombie)zombie);
		}
	}
	
	@Override
	public void setBookDescription()
	{
		this.bookDescription = "Summons Zombies to aid it.";
		this.addDrops(Items.blaze_rod, 3);
		this.addDrops(Items.gold_ingot, 3);
		this.addDrops(Items.golden_apple, 3);
		this.addDrops(Items.emerald, 2);
		this.addDrops(Items.diamond, 2);
		ItemStack stack = new ItemStack(ModItems.summonersHelm);
		stack.addEnchantment(Enchantment.fireProtection, 5);
		this.addDrops(stack, 2);
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if (this.summonCooldown == 0 && !this.worldObj.isRemote && this.worldObj.isAnyPlayerWithinRangeAt(this.posX, this.posY, this.posZ, 20D) && this.getAttackTarget() instanceof EntityPlayer)
		{
			this.summonCooldown = 800;
			for (int i=0; i < 4; i++)
			{
				EntityMinionZombie zombie = new EntityMinionZombie(this.worldObj);
				zombie.summoned = true;
				zombie.getDataWatcher().updateObject(20, (byte)1);
				zombie.onInitialSpawn(null, null);
				zombie.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
				if (i > 1)
				{
					zombie.motionX = i % 2 == 0 ? 0.2D : -0.2D;
					zombie.motionZ = i % 2 == 0 ? -0.2D : 0.2D;
				}
				else
				{
					zombie.motionX = i % 2 == 0 ? -0.2D : 0.2D;
					zombie.motionZ = i % 2 == 0 ? -0.2D : 0.2D;
				}
				zombie.motionY = 0.8D;
				zombie.setAttackTarget(this.getAttackTarget());
				this.worldObj.spawnEntityInWorld(zombie);
			}
		}
		else if (this.summonCooldown % 100 == 0 && this.rand.nextInt(2) == 0 && !this.worldObj.isRemote && this.worldObj.isAnyPlayerWithinRangeAt(this.posX, this.posY, this.posZ, 20D) && this.getAttackTarget() instanceof EntityPlayer)
		{
			try 
			{
				EntityEventZombie zombie = this.summons.get(this.rand.nextInt(this.summons.size())).getClass().getDeclaredConstructor(World.class).newInstance(this.worldObj);
				zombie.summoned = true;
				zombie.getDataWatcher().updateObject(20, (byte)1);
				zombie.onInitialSpawn(null, null);
				zombie.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
				zombie.motionX = rand.nextDouble()-0.5D;
				zombie.motionY = 0.8D;
				zombie.motionZ = rand.nextDouble()-0.5D;
				zombie.setAttackTarget(this.getAttackTarget());
				this.worldObj.spawnEntityInWorld(zombie);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}

		if (this.worldObj.isRemote && this.ticksExisted % 1 == 0)
			this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX+rand.nextDouble()-0.5D, this.posY+rand.nextDouble()-0.5D, this.posZ+rand.nextDouble()-0.5D, 0, 0, 0, 0);

		//swap pumpkin head
		if (!this.worldObj.isRemote && this.ticksExisted % 30 == 0)
		{
			if (this.getCurrentArmor(3) != null && this.getCurrentArmor(3).getItem() == Item.getItemFromBlock(Blocks.pumpkin))
			{
				this.setCurrentItemOrArmor(4, new ItemStack(Item.getItemFromBlock(Blocks.lit_pumpkin)));
				this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "fire.ignite", 1.0F, rand.nextFloat()+0.0F);
				this.setFire(30);
			}
			else if (this.getCurrentArmor(3) != null && this.getCurrentArmor(3).getItem() == Item.getItemFromBlock(Blocks.lit_pumpkin))
			{
				this.setCurrentItemOrArmor(4, new ItemStack(Item.getItemFromBlock(Blocks.pumpkin)));
				this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "random.fizz", 1.0F, rand.nextFloat()/2+1.1F);
				this.extinguish();
			}
		}

		if (this.summonCooldown > 0)
			this.summonCooldown--;
	}
	
	@Override
	protected String getLivingSound()
	{
        return "mob.blaze.breathe";
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(5.0D);
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(17.0D);
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) 
	{ 
		ItemStack stack = new ItemStack(Items.blaze_rod);
		EnchantmentHelper.addRandomEnchantment(rand, stack, 10);
		this.setCurrentItemOrArmor(0, stack);
		stack = new ItemStack(Items.golden_boots);
		EnchantmentHelper.addRandomEnchantment(rand, stack, 10);
		this.setCurrentItemOrArmor(1, stack);
		stack = new ItemStack(Items.golden_leggings);
		EnchantmentHelper.addRandomEnchantment(rand, stack, 10);
		this.setCurrentItemOrArmor(2, stack);
		stack = new ItemStack(Items.golden_chestplate);
		EnchantmentHelper.addRandomEnchantment(rand, stack, 10);
		this.setCurrentItemOrArmor(3, stack);
		stack = new ItemStack(Item.getItemFromBlock(Blocks.pumpkin));
		this.setCurrentItemOrArmor(4, stack);
		for (int i=0; i<this.equipmentDropChances.length; i++)
			this.setEquipmentDropChance(i, i == 4 ? 0f : 0.08f);
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata)
	{
		super.onInitialSpawn(difficulty, livingdata);
		this.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 99999, 1, false, false));
		this.setCustomNameTag("Zombie Summoner");
		return livingdata;
	}
}
