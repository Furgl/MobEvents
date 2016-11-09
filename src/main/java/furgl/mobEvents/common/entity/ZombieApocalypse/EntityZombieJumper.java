package furgl.mobEvents.common.entity.ZombieApocalypse;

import furgl.mobEvents.common.item.ModItems;
import furgl.mobEvents.common.item.drops.ItemDoubleJumpBoots;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityZombieJumper extends EntityEventZombie
{
    protected static final DataParameter<Byte> TARGET = EntityDataManager.<Byte>createKey(EntityZombieJumper.class, DataSerializers.BYTE);
	/**Ticks when it can't jump*/
	private int jumpTick;
	private int attackTime;

	public EntityZombieJumper(World world) 
	{
		super(world);
		this.progressOnDeath = 3;
		this.armorColor = 0x0060A0;
		this.maxSpawnedInChunk = 2;
	}

	@Override
	public void setBookDescription()
	{
		this.bookDescription = "A bouncy Zombie warrior.";
		this.addDrops(Items.FEATHER, 3);
		this.addDrops(Item.getItemFromBlock(Blocks.SLIME_BLOCK), 3);
		this.addDrops(Items.ENCHANTED_BOOK.getEnchantedItemStack(new EnchantmentData(Enchantments.FEATHER_FALLING, 1)), 2);
		this.addDrops(PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), PotionTypes.LEAPING), 2);
		this.addDrops(((ItemDoubleJumpBoots) ModItems.doubleJumpBoots).getItemStack(), 1);
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(12.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.33D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.getDataManager().register(TARGET, Byte.valueOf((byte)0)); //target
	}
	
	@Override
	protected void collideWithEntity(Entity entity)
	{
		super.collideWithEntity(entity);
		if (this.attackTime == 0 && this.getHealth() > 0 && this.getAttackTarget() == entity && !this.worldObj.isRemote)
		{
			this.attackTime = 20;
			this.swingArm(EnumHand.MAIN_HAND);
			this.attackEntityAsMob(this.getAttackTarget());
		}
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
    {
		if (source == DamageSource.fall)
			return false;
		return super.attackEntityFrom(source, amount);
    }

	@Override
	public void onUpdate()
	{	
		if (this.attackTime > 0)
			this.attackTime--;
		
		if (this.getActivePotionEffect(MobEffects.JUMP_BOOST) == null || this.getActivePotionEffect(MobEffects.JUMP_BOOST).getDuration() < 10 || this.getActivePotionEffect(MobEffects.JUMP_BOOST).doesShowParticles())
			this.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 99999, 3, false, false));

		//update datawatcher
		if (!this.worldObj.isRemote)
		{
			if (this.dataManager.get(TARGET) == 0 && this.shouldAttack(this.getAttackTarget()))
				this.dataManager.set(TARGET, (byte)1);
			else if (this.dataManager.get(TARGET) == 1 && !this.shouldAttack(this.getAttackTarget()))
				this.dataManager.set(TARGET, (byte)0);
		}

		//jump
		if ((!this.worldObj.isRemote && this.shouldAttack(this.getAttackTarget()) && this.jumpTick == 0) || 
				(this.worldObj.isRemote && this.dataManager.get(TARGET) == 1 && this.jumpTick == 0))
		{
			if (this.isEntityAlive() && this.getItemStackFromSlot(EntityEquipmentSlot.FEET) != null && this.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() instanceof ItemDoubleJumpBoots)
			{
				if (this.isAirBorne)
					((ItemDoubleJumpBoots) this.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem()).jumpEffects(this);
				this.jump();
				this.jumpTick = 17;
				if (this.getAttackTarget() != null)
					this.faceEntity(this.getAttackTarget(), 360f, 360f);
				this.moveRelative(0, 0.5F, 1F);
			}
		}
		if (this.jumpTick > 0)
			this.jumpTick--;

		super.onUpdate();
		//this.multiplySize(2);
	}

	private boolean shouldAttack(EntityLivingBase entity) 
	{
		if (entity instanceof EntityPlayer)
		{
			double d0 = this.posX - entity.posX;
			double d2 = this.posZ - entity.posZ;
			double d3 =  d0 * d0 + d2 * d2;
			return (entity.posY > this.posY+1.5D && d3 < 70D);
		}
		return false;
	}
	
	@Override
	protected SoundEvent getFallSound(int damageValue)
    {
        return damageValue > 4 ? SoundEvents.ENTITY_SLIME_SQUISH : SoundEvents.ENTITY_SMALL_SLIME_SQUISH;
    }

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) 
	{ 
		ItemStack boots = ((ItemDoubleJumpBoots) ModItems.doubleJumpBoots).getItemStack();
		boots.addEnchantment(Enchantments.FEATHER_FALLING, 5);
		this.setItemStackToSlot(EntityEquipmentSlot.FEET, boots);
		this.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(Items.LEATHER_LEGGINGS));
		this.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Items.LEATHER_CHESTPLATE));
		this.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Item.getItemFromBlock(Blocks.SLIME_BLOCK)));
		this.setDropChance(EntityEquipmentSlot.FEET, 0.0f);
		super.setEquipmentBasedOnDifficulty(difficulty);
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata)
	{
		super.onInitialSpawn(difficulty, livingdata);
		this.setCustomNameTag("Zombie Jumper");
		return livingdata;
	}
}
