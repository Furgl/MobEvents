package furgl.mobEvents.common.entity.ZombieApocalypse;

import java.util.ArrayList;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.Events.ChaoticTurmoil;
import furgl.mobEvents.common.Events.Event;
import furgl.mobEvents.common.entity.IEventMob;
import furgl.mobEvents.common.entity.bosses.EntityBossZombie;
import furgl.mobEvents.common.item.ModItems;
import furgl.mobEvents.common.sound.ModSoundEvents;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

public class EntityEventZombie extends EntityZombie implements IEventMob
{
	protected static final DataParameter<Byte> SUMMONED = EntityDataManager.<Byte>createKey(EntityEventZombie.class, DataSerializers.BYTE);
	/**Bosses have 100 progressOnDeath*/
	protected int progressOnDeath;
	public int armorColor = -1;
	public int maxSpawnedInChunk = 1;
	private int bardsBoost;
	/**Is color increasing or decreasing*/
	private boolean increasing; 
	public boolean summoned;
	protected String bookDescription;
	protected ArrayList<ItemStack> bookDrops;
	/**Wave that mob thinks it is currently*/
	public int currentWave;

	public EntityEventZombie(World world) 
	{
		super(world);
		this.setEquipmentBasedOnDifficulty(null);
		this.setBookDescription();
	}

	public void setBookDescription()
	{
		this.bookDescription = "";
		this.bookDrops = new ArrayList<ItemStack>();
	}

	@Override
	protected void applyEntityAI()
	{
		this.tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0D, false));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[] {EntityPigZombie.class}));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
	}

	@Override
	public int getMaxSpawnedInChunk()
	{
		return this.maxSpawnedInChunk;
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.getDataManager().register(SUMMONED, Byte.valueOf((byte)0)); //summoned
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand p_184645_2_, ItemStack stack)
	{
		return false;
	}

	@Override
	public void setNoAI(boolean disable)
	{
		super.setNoAI(disable);
		this.onInitialSpawn(null, null);
	}

	@Override
	public void onUpdate()
	{
		//play living sound on wave change
		if (this.currentWave != MobEvents.proxy.getWorldData().currentWave)
		{
			this.currentWave = MobEvents.proxy.getWorldData().currentWave;
			this.playLivingSound();
		}

		//set summoned from datawatcher
		if (this.ticksExisted == 1 && this.worldObj.isRemote && this.getDataManager().get(SUMMONED) == 1)
		{
			this.summoned = true;
			this.worldObj.playSound(this.posX, this.posY, this.posZ, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.HOSTILE, 1f, rand.nextFloat(), false);
		}

		//summoned particles
		if (summoned && this.ticksExisted < 30 && this.worldObj.isRemote)
		{
			this.worldObj.spawnParticle(EnumParticleTypes.SPELL_INSTANT, this.posX+rand.nextDouble()-0.5D, this.posY+rand.nextDouble()-0.5D, this.posZ+rand.nextDouble()-0.5D, rand.nextDouble()-0.5D, rand.nextDouble()-0.5D, rand.nextDouble()-0.5D, 0);
		}
		if (summoned && this.worldObj.isRemote)
		{
			this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX+rand.nextDouble()-0.5D, this.posY+rand.nextDouble()-0.5D, this.posZ+rand.nextDouble()-0.5D, 0, 0, 0, 0);
			this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX+rand.nextDouble()-0.5D, this.posY+rand.nextDouble()-0.5D, this.posZ+rand.nextDouble()-0.5D, 0, 0, 0, 0);
		}

		//damage outside of event
		if (MobEvents.proxy.getWorldData().currentEvent.getClass() != this.getEvent().getClass() && MobEvents.proxy.getWorldData().currentEvent.getClass() != ChaoticTurmoil.class)
		{
			if (this.worldObj.isRemote)
				this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX+rand.nextDouble()-0.5D, this.posY+rand.nextDouble(), this.posZ+rand.nextDouble()-0.5D, 0, 0, 0, 0);
			else
				this.setFire(100);
			if (this.ticksExisted % 20 == 0)
			{
				if (this.worldObj.isRemote)
					this.performHurtAnimation();
				else
				{
					this.damageEntity(DamageSource.outOfWorld, this.getMaxHealth()/3);
					if (this.getHealth() > 0)
						this.playSound(this.getHurtSound(), this.getSoundVolume(), this.getSoundPitch());
					else
					{
						if (this instanceof EntityZombieBard)
							MobEvents.proxy.stopSounds();
						else if (this instanceof EntityZombieThief)
							this.onDeath(DamageSource.outOfWorld);
						this.playSound(this.getDeathSound(), this.getSoundVolume(), this.getSoundPitch());
					}
				}
			}
		}

		//bardsBoost effects
		if (this.bardsBoost > 0 && this.worldObj.isRemote)
		{
			for (int i=0; i<EntityEquipmentSlot.values().length; i++)
			{
				if (this.getItemStackFromSlot(EntityEquipmentSlot.values()[i]) != null && this.getItemStackFromSlot(EntityEquipmentSlot.values()[i]).getItem() instanceof ItemArmor && 
						((ItemArmor)this.getItemStackFromSlot(EntityEquipmentSlot.values()[i]).getItem()).getArmorMaterial() == ItemArmor.ArmorMaterial.LEATHER && 
						((ItemArmor)this.getItemStackFromSlot(EntityEquipmentSlot.values()[i]).getItem()).getColor(this.getItemStackFromSlot(EntityEquipmentSlot.values()[i])) != -1)
				{
					int color = ((ItemArmor)this.getItemStackFromSlot(EntityEquipmentSlot.values()[i]).getItem()).getColor(this.getItemStackFromSlot(EntityEquipmentSlot.values()[i]));
					int max = 16776960;
					int min = 0;
					int increment = 500;
					if (increasing)
						color += increment;
					else
						color -= increment;
					if (color > max)
					{
						color = max;
						increasing = false;
					}
					else if (color < min)
					{
						color = min;
						increasing = true;
					}
					if (color == -1)
						color += this.rand.nextBoolean() ? 1 : -1;
					((ItemArmor)this.getItemStackFromSlot(EntityEquipmentSlot.values()[i]).getItem()).setColor(this.getItemStackFromSlot(EntityEquipmentSlot.values()[i]), color);
				}
			}
			if (rand.nextInt(4) == 0)
				this.worldObj.spawnParticle(EnumParticleTypes.NOTE, this.posX-2.5D+rand.nextDouble()*4, this.posY+1.0D+rand.nextDouble()*2, this.posZ-2.5D+rand.nextDouble()*4, rand.nextDouble(), 0, 0, null);
		}

		//stop bardsBoost effects
		if (this.bardsBoost == 1 && this.worldObj.isRemote)
		{
			for (int i=0; i<EntityEquipmentSlot.values().length; i++)
				if (this.armorColor == -1)
				{
					if (this.getItemStackFromSlot(EntityEquipmentSlot.values()[i]) != null && this.getItemStackFromSlot(EntityEquipmentSlot.values()[i]).getItem() instanceof ItemArmor && 
							((ItemArmor)this.getItemStackFromSlot(EntityEquipmentSlot.values()[i]).getItem()).getArmorMaterial() == ItemArmor.ArmorMaterial.LEATHER &&
							((ItemArmor)this.getItemStackFromSlot(EntityEquipmentSlot.values()[i]).getItem()).getColor(this.getItemStackFromSlot(EntityEquipmentSlot.values()[i])) != -1)
						((ItemArmor)this.getItemStackFromSlot(EntityEquipmentSlot.values()[i]).getItem()).removeColor(this.getItemStackFromSlot(EntityEquipmentSlot.values()[i]));
				}
				else
				{
					if (this.getItemStackFromSlot(EntityEquipmentSlot.values()[i]) != null && this.getItemStackFromSlot(EntityEquipmentSlot.values()[i]).getItem() instanceof ItemArmor && 
							((ItemArmor)this.getItemStackFromSlot(EntityEquipmentSlot.values()[i]).getItem()).getArmorMaterial() == ItemArmor.ArmorMaterial.LEATHER &&
							((ItemArmor)this.getItemStackFromSlot(EntityEquipmentSlot.values()[i]).getItem()).getColor(this.getItemStackFromSlot(EntityEquipmentSlot.values()[i])) != -1)
						((ItemArmor)this.getItemStackFromSlot(EntityEquipmentSlot.values()[i]).getItem()).setColor(this.getItemStackFromSlot(EntityEquipmentSlot.values()[i]), this.armorColor);
				}
		}

		if (this.bardsBoost > 0)
			this.bardsBoost--;

		super.onUpdate();
	}

	@Override //prevents fire damage in sun
	public float getBrightness(float partialTicks)
	{
		return super.getBrightness(partialTicks);//0.0f;
	}

	public void applyBardsBoost()
	{
		if (this instanceof EntityBossZombie)
			return;
		this.bardsBoost = 150;
		this.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 200));
		this.addPotionEffect(new PotionEffect(MobEffects.SPEED, 200));
		this.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 200));
		this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200));
		this.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 200, 1));
	}

	@Override
	protected float getSoundPitch()
	{
		return this.isChild() ? (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.2F : (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F;
	}

	@Override
	protected SoundEvent getAmbientSound()
	{
		return (this.bardsBoost > 0 || this instanceof EntityZombieBard) ? SoundEvents.BLOCK_NOTE_SNARE : ModSoundEvents.mob_event_zombie_say;
	}

	@Override
	protected SoundEvent getHurtSound()
	{
		return (this.bardsBoost > 0 || this instanceof EntityZombieBard) ? SoundEvents.BLOCK_NOTE_PLING : ModSoundEvents.mob_event_zombie_hurt;
	}

	@Override
	protected SoundEvent getDeathSound()
	{
		return (this.bardsBoost > 0 || this instanceof EntityZombieBard) ? SoundEvents.BLOCK_NOTE_SNARE : ModSoundEvents.mob_event_zombie_death;
	}

	@Override
	protected void playStepSound(BlockPos pos, Block blockIn)
	{
		if (this.bardsBoost > 0 || this instanceof EntityZombieBard)
			this.playSound(SoundEvents.BLOCK_NOTE_HARP, 1.0F, rand.nextFloat()+1F);
		else
			this.playSound(SoundEvents.ENTITY_ZOMBIE_STEP, 0.15F, 1.0F);
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(60.0D);
	}

	@Override
	public ItemStack getPickedResult(RayTraceResult target)
	{
		return ModItems.getSpawnEgg(this);
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) 
	{ 
		for (int i=0; i<5; i++)
			this.setDropChance(EntityEquipmentSlot.values()[i], 0.01f);
		if (this.armorColor > -1)
			for (int i=0; i<EntityEquipmentSlot.values().length; i++)
				if (this.getItemStackFromSlot(EntityEquipmentSlot.values()[i]) != null && this.getItemStackFromSlot(EntityEquipmentSlot.values()[i]).getItem() instanceof ItemArmor && ((ItemArmor)this.getItemStackFromSlot(EntityEquipmentSlot.values()[i]).getItem()).getArmorMaterial() == ItemArmor.ArmorMaterial.LEATHER)
					((ItemArmor)this.getItemStackFromSlot(EntityEquipmentSlot.values()[i]).getItem()).setColor(this.getItemStackFromSlot(EntityEquipmentSlot.values()[i]), this.armorColor);
	}

	private void addRandomDrop()
	{
		if (bookDrops != null && bookDrops.size() > 0)
			this.entityDropItem(bookDrops.get(rand.nextInt(bookDrops.size())), 0);
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata)
	{
		if (MobEvents.proxy.world != null)
			this.currentWave = MobEvents.proxy.getWorldData().currentWave;
		this.setChild(false);
		this.setVillagerType(null);
		this.setEquipmentBasedOnDifficulty(difficulty);
		return livingdata;
	}

	@Override
	public void onDeath(DamageSource cause)
	{
		if (!this.worldObj.isRemote && cause.getEntity() instanceof EntityPlayer)
		{
			MobEvents.proxy.getWorldData().currentEvent.increaseProgress(progressOnDeath);
			//drop random drop
			int i = EnchantmentHelper.getLootingModifier((EntityLivingBase)cause.getEntity());
			if (this.recentlyHit > 0 && this.rand.nextInt(100) < (30 + i * 5))
				this.addRandomDrop();
			//drop event book and unlock entity
			if (!(cause.getEntity() instanceof FakePlayer))
			{
				this.unlockEntityInBook((EntityPlayer) cause.getEntity());
				if (!(((EntityPlayer) cause.getEntity()).inventory.hasItemStack(new ItemStack(ModItems.creativeEventBook)) || ((EntityPlayer) cause.getEntity()).inventory.hasItemStack(new ItemStack(ModItems.eventBook))))
					this.dropItem(ModItems.eventBook, 1);
			}
		}

		super.onDeath(cause);
	}

	private void unlockEntityInBook(EntityPlayer player)
	{ 
		int index = MobEvents.proxy.getWorldData().getPlayerIndex(player.getDisplayNameString());
		if (!this.isNonBoss())
		{
			if (!Event.bossDefeated)
				return;
			for (String entity : MobEvents.proxy.getWorldData().unlockedEntities.get(index))
				if (entity.equals("Zombie Boss"))
					return;
			MobEvents.proxy.getWorldData().unlockedEntities.get(index).add("Zombie Boss");
			MobEvents.proxy.getWorldData().markDirty();
			if (player.worldObj.isRemote)
				Event.displayUnlockMessage(player, "Unlocked information about the Zombie Boss in the Event Book");
		}
		else 
		{
			for (String entity : MobEvents.proxy.getWorldData().unlockedEntities.get(index))
				if (entity.equals(this.getName()))
					return;
			MobEvents.proxy.getWorldData().unlockedEntities.get(index).add(this.getName());
			MobEvents.proxy.getWorldData().markDirty();
			if (player.worldObj.isRemote)
				Event.displayUnlockMessage(player, "Unlocked information about the "+this.getName()+" in the Event Book");
		}
	}

	@Override
	protected boolean canDropLoot()
	{
		if (MobEvents.proxy.getWorldData().currentEvent.getClass() != this.getEvent().getClass() && MobEvents.proxy.getWorldData().currentEvent.getClass() != ChaoticTurmoil.class)
			return false;
		else
			return true;
	}

	protected void addDrops(Item item, int amount)
	{
		if (this.bookDrops == null)
			this.bookDrops = new ArrayList<ItemStack>();
		for (int i=0; i<amount; i++)
			this.bookDrops.add(new ItemStack(item));
	}

	protected void addDrops(ItemStack stack, int amount)
	{
		if (this.bookDrops == null)
			this.bookDrops = new ArrayList<ItemStack>();
		for (int i=0; i<amount; i++)
			this.bookDrops.add(stack);
	}

	@Override
	public String getBookDescription() 
	{
		return this.bookDescription;
	}

	@Override
	public ArrayList<ItemStack> getBookDrops() 
	{
		return this.bookDrops;
	}

	@Override
	public int getProgressOnDeath() 
	{
		return this.progressOnDeath;
	}

	@Override
	public void doSpecialRender(int displayTicks) { }

	@Override
	public Event getEvent() {
		return Event.ZOMBIE_APOCALYPSE;
	}
}
