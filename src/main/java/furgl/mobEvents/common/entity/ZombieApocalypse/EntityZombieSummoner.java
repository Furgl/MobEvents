package furgl.mobEvents.common.entity.ZombieApocalypse;


import java.util.ArrayList;

import furgl.mobEvents.common.entity.IEventMob;
import furgl.mobEvents.common.item.ModItems;
import furgl.mobEvents.common.item.drops.ItemSummonersHelm;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityZombieSummoner extends EntityEventZombie
{
	private int summonCooldown;
	private ArrayList<EntityEventZombie> summons;

	public EntityZombieSummoner(World world) 
	{
		super(world);
		this.progressOnDeath = 5;
		this.maxSpawnedInChunk = 2;
	}

	@Override
	public void setBookDescription()
	{
		this.bookDescription = "Summons Zombies to aid it.";
		this.addDrops(Items.BLAZE_ROD, 2);
		this.addDrops(Items.GOLD_INGOT, 2);
		this.addDrops(Items.GOLDEN_APPLE, 2);
		this.addDrops(Items.EMERALD, 1);
		this.addDrops(Items.DIAMOND, 1);
		this.addDrops(((ItemSummonersHelm) ModItems.summonersHelm).getItemStack(), 1);
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if (this.summons == null && this.worldObj != null)
		{
			int modifier = -1;
			if (this.worldObj.getDifficulty() == EnumDifficulty.HARD)
				modifier++;
			else if (this.worldObj.getDifficulty() == EnumDifficulty.EASY)
				modifier--;
			summons = new ArrayList<EntityEventZombie>();
			for (IEventMob zombie : this.getEvent().mobs)
				for (int i=this.progressOnDeath-((IEventMob)zombie).getProgressOnDeath()+modifier; i>0; i--)
					summons.add((EntityEventZombie)zombie);
		}

		if (this.summonCooldown == 0 && !this.worldObj.isRemote && this.worldObj.isAnyPlayerWithinRangeAt(this.posX, this.posY, this.posZ, 20D) && this.getAttackTarget() instanceof EntityPlayer)
		{
			this.summonCooldown = 800;
			for (int i=0; i < 4; i++)
			{
				EntityZombieMinion zombie = new EntityZombieMinion(this.worldObj);
				zombie.summoned = true;
				zombie.getDataManager().set(SUMMONED, (byte)1);
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
				zombie.getDataManager().set(SUMMONED, (byte)1);
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
		if (!this.worldObj.isRemote)
			this.doSpecialRender(this.ticksExisted % 50);

		if (this.summonCooldown > 0)
			this.summonCooldown--;
	}

	@Override
	public void doSpecialRender(int displayTicks) 
	{ 
		//swap pumpkin head
		if (displayTicks % 90 == 0)
		{
			if (this.getItemStackFromSlot(EntityEquipmentSlot.HEAD) != null && this.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == Item.getItemFromBlock(Blocks.PUMPKIN))
			{
				this.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Item.getItemFromBlock(Blocks.LIT_PUMPKIN)));
				this.worldObj.playSound(null, this.getPosition(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.HOSTILE, 1.0F, this.worldObj.rand.nextFloat()+0.0F);
				this.setFire(30);
			}
			else if (this.getItemStackFromSlot(EntityEquipmentSlot.HEAD) != null && this.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == Item.getItemFromBlock(Blocks.LIT_PUMPKIN))
			{
				this.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Item.getItemFromBlock(Blocks.PUMPKIN)));
				this.worldObj.playSound(null, this.getPosition(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.HOSTILE, 1.0F, this.worldObj.rand.nextFloat()/2+1.1F);
				this.extinguish();
			}
		}
	}

	@Override
	protected SoundEvent getAmbientSound()
	{
		return SoundEvents.ENTITY_BLAZE_AMBIENT;
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(17.0D);
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) 
	{ 
		ItemStack stack = new ItemStack(Items.BLAZE_ROD);
		EnchantmentHelper.addRandomEnchantment(rand, stack, 10, true);
		this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack);
		stack = new ItemStack(Items.GOLDEN_BOOTS);
		EnchantmentHelper.addRandomEnchantment(rand, stack, 10, true);
		this.setItemStackToSlot(EntityEquipmentSlot.FEET, stack);
		stack = new ItemStack(Items.GOLDEN_LEGGINGS);
		EnchantmentHelper.addRandomEnchantment(rand, stack, 10, true);
		this.setItemStackToSlot(EntityEquipmentSlot.LEGS, stack);
		stack = new ItemStack(Items.GOLDEN_CHESTPLATE);
		EnchantmentHelper.addRandomEnchantment(rand, stack, 10, true);
		this.setItemStackToSlot(EntityEquipmentSlot.CHEST, stack);
		stack = new ItemStack(Item.getItemFromBlock(Blocks.PUMPKIN));
		this.setItemStackToSlot(EntityEquipmentSlot.HEAD, stack);
		super.setEquipmentBasedOnDifficulty(difficulty);
		for (int i=0; i<EntityEquipmentSlot.values().length; i++)
			this.setDropChance(EntityEquipmentSlot.values()[i], EntityEquipmentSlot.values()[i] == EntityEquipmentSlot.HEAD ? 0f : 0.08f);
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata)
	{
		super.onInitialSpawn(difficulty, livingdata);
		this.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 99999, 1, false, false));
		this.setCustomNameTag("Zombie Summoner");
		return livingdata;
	}
}
