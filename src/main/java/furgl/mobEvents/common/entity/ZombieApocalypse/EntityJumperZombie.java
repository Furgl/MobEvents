package furgl.mobEvents.common.entity.ZombieApocalypse;

import furgl.mobEvents.common.item.ModItems;
import furgl.mobEvents.common.item.drops.ItemDoubleJumpBoots;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityJumperZombie extends EntityEventZombie
{
	/**Ticks when it can't jump*/
	private int jumpTick;
	private int attackTime;

	public EntityJumperZombie(World world) 
	{
		super(world);
		this.setBookDescription();
		this.progressOnDeath = 3;
		this.armorColor = 0x0060A0;
	}

	@Override
	public void setBookDescription()
	{
		this.bookDescription = "A bouncy Zombie warrior.";
		this.addDrops(Items.feather, 10);
		this.addDrops(Item.getItemFromBlock(Blocks.slime_block), 10);
		this.addDrops(Items.enchanted_book.getEnchantedItemStack(new EnchantmentData(Enchantment.featherFalling, 1)), 10);
		this.addDrops(new ItemStack(Items.potionitem, 1, 8203), 3);
		this.addDrops(((ItemDoubleJumpBoots) ModItems.doubleJumpBoots).getItemStack(), 1);
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(12.0D);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.33D);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(2.0D);
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.getDataWatcher().addObject(21, Byte.valueOf((byte)0)); //target
	}
	
	@Override
	protected void collideWithEntity(Entity entity)
	{
		super.collideWithEntity(entity);
		if (this.attackTime == 0 && this.getHealth() > 0 && this.getAttackTarget() == entity && !this.worldObj.isRemote)
		{
			this.attackTime = 20;
			this.swingItem();
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
		
		if (this.getActivePotionEffect(Potion.jump) == null || this.getActivePotionEffect(Potion.jump).getDuration() < 10 || this.getActivePotionEffect(Potion.jump).getIsShowParticles())
			this.addPotionEffect(new PotionEffect(Potion.jump.id, 99999, 3, false, false));

		//update datawatcher
		if (!this.worldObj.isRemote)
		{
			if (this.dataWatcher.getWatchableObjectByte(21) == 0 && this.shouldAttack(this.getAttackTarget()))
				this.dataWatcher.updateObject(21, (byte)1);
			else if (this.dataWatcher.getWatchableObjectByte(21) == 1 && !this.shouldAttack(this.getAttackTarget()))
				this.dataWatcher.updateObject(21, (byte)0);
		}

		//jump
		if ((!this.worldObj.isRemote && this.shouldAttack(this.getAttackTarget()) && this.jumpTick == 0) || 
				(this.worldObj.isRemote && this.dataWatcher.getWatchableObjectByte(21) == 1 && this.jumpTick == 0))
		{
			if (this.isEntityAlive() && this.getCurrentArmor(0) != null && this.getCurrentArmor(0).getItem() instanceof ItemDoubleJumpBoots)
			{
				if (this.isAirBorne)
					((ItemDoubleJumpBoots) this.getCurrentArmor(0).getItem()).jumpEffects(this);
				this.jump();
				this.jumpTick = 17;
				if (this.getAttackTarget() != null)
					this.faceEntity(this.getAttackTarget(), 360f, 360f);
				this.moveFlying(0, 0.5F, 1F);
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
	protected String getFallSoundString(int damageValue)
    {
        return damageValue > 4 ? "mob.slime.big" : "mob.slime.small";
    }

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) 
	{ 
		ItemStack boots = ((ItemDoubleJumpBoots) ModItems.doubleJumpBoots).getItemStack();
		boots.addEnchantment(Enchantment.featherFalling, 5);
		this.setCurrentItemOrArmor(1, boots);
		this.setCurrentItemOrArmor(2, new ItemStack(Items.leather_leggings));
		this.setCurrentItemOrArmor(3, new ItemStack(Items.leather_chestplate));
		this.setCurrentItemOrArmor(4, new ItemStack(Item.getItemFromBlock(Blocks.slime_block)));
		this.setEquipmentDropChance(1, 0.0f);
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
