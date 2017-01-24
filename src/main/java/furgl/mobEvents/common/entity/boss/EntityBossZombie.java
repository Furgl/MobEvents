package furgl.mobEvents.common.entity.boss;

import java.util.ArrayList;
import java.util.List;

import furgl.mobEvents.common.Events.Event;
import furgl.mobEvents.common.entity.IEventMob;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityEventZombie;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityZombiePyromaniac;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityZombieThief;
import furgl.mobEvents.common.entity.boss.spawner.EntityBossSpawner;
import furgl.mobEvents.common.item.ModItems;
import furgl.mobEvents.common.item.drops.ItemBookOfHealing;
import furgl.mobEvents.common.world.WorldData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.ZombieType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityBossZombie extends EntityEventZombie implements IEventBoss
{
	private final BossInfoServer bossInfo = (BossInfoServer)(new BossInfoServer(new TextComponentString(this.getCustomNameTag()), BossInfo.Color.GREEN, BossInfo.Overlay.PROGRESS)).setDarkenSky(true);
	private static final DataParameter<Integer> TYPE = EntityDataManager.<Integer>createKey(EntityBossZombie.class, DataSerializers.VARINT);
	private static final DataParameter<Byte> SMITH_BLOCK = EntityDataManager.<Byte>createKey(EntityBossZombie.class, DataSerializers.BYTE);
	/**1 = Villager, 2 = Butcher, 3 = Farmer, 4 = Librarian, 5 = Priest, 6 = Smith*/
	public int type;
	/**Time to force player to look at boss*/
	private int forceLook;
	/**Time that farmer should eat*/
	private int farmerEatingTime;
	/**Is farmer eating currently*/
	private boolean isFarmerEating;
	/**Should smith emit block particles and make sound*/
	private boolean smithBlockEffect;
	/**Villager cooldown after summon*/
	private int villagerSummonCooldown;
	/**List of random event zombies for villager to summon*/
	private ArrayList<EntityEventZombie> villagerSummons;
	/**Priest cooldown after effect*/
	private int priestCooldown;
	/**Target of priest's effect*/
	private EntityBossZombie priestTarget;
	/**Time that priest's effect is active*/
	private int priestEffectTime;
	private final ArrayList<ItemFood> food = new ArrayList<ItemFood>() {{
		add((ItemFood) Items.BAKED_POTATO);
		add((ItemFood) Items.CARROT);
		add((ItemFood) Items.BREAD);
		add((ItemFood) Items.PUMPKIN_PIE);
		add((ItemFood) Items.MELON);
		add((ItemFood) Items.APPLE);
		add((ItemFood) Items.COOKIE);
	}};
	private final ArrayList<String> farmerTaunts = new ArrayList<String>() {{
		add("Omnomnom");
		add("Mmm delicious");
		add("Scrumptious");
	}};
	private final ArrayList<String> smithTaunts = new ArrayList<String>() {{
		add("My armor is the strongest in the lands!");
		add("I'm indestructable!");
		add("You'll never get through my armor!");
	}};
	private final ArrayList<String> butcherTaunts = new ArrayList<String>() {{
		add("I'll chop you to bits!");
		add("That'll teach you to get in the way of my cleaver!");
		add("This'll hurt you way more than it'll hurt me!");
	}};
	public final ArrayList<String> librarianTaunts = new ArrayList<String>() {{
		add("Shhh!");
		add("Quiet, human!");
		add("I demand silence!");
		add("You won't be so talkative when I'm done with you!");
		add("Get the chatty one!");
	}};

	public EntityBossZombie(World world)
	{
		super(world);
	}

	public EntityBossZombie(World world, int type) 
	{
		super(world);
		this.type = type;
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(60.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50D);
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.getDataManager().register(TYPE, Integer.valueOf(0)); 
		this.getDataManager().register(SMITH_BLOCK, Byte.valueOf((byte) 0));
	}

	@Override
	public boolean isNonBoss()
	{
		return false;
	}

	@Override
	public void onUpdate()
	{	
		if (type == 6)
			this.setActiveHand(EnumHand.MAIN_HAND);
	    EntityPlayer player = this.worldObj.getClosestPlayerToEntity(this, -1);
		if (player == null)
			return;
		this.multiplySize(3);
		this.height += 0.5f;
		//find target
		if (!this.worldObj.isRemote && this.getAttackTarget() == null && player != null)
			this.setAttackTarget(player);
		//set type on client and speed on server
		if (this.type == 0 && this.worldObj.isRemote)
			this.type = this.dataManager.get(TYPE);
		else if (this.isInvisible() && this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue() > 0)
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0D);
		//handle spawn delay
		if (worldObj.isRemote && this.getActivePotionEffect(MobEffects.INVISIBILITY) != null && this.getActivePotionEffect(MobEffects.INVISIBILITY).getDuration() == 5)
		{
			this.setInvisible(false);
			this.forceLook = 40;
			for (int i=0; i<40; i++)
				this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE, posX+(rand.nextDouble()-0.5D)*3D, posY+(rand.nextDouble()+0.0D)*5D, posZ+(rand.nextDouble()-0.5D)*3D, 0, 0, 0, 0);
			player = this.worldObj.getClosestPlayerToEntity(this, -1);
			((WorldClient)this.worldObj).playSound(this.getPosition(), SoundEvents.ENTITY_GENERIC_EXPLODE, getSoundCategory(), 10.0F, rand.nextFloat(), false);
			((WorldClient)this.worldObj).playSound(player.getPosition(), this.getAmbientSound(), getSoundCategory(), 10.0F, rand.nextFloat()+0.4F, false);
		}
		//force player to look and stop moving
		if (this.forceLook-- > 0)
		{
			player = this.worldObj.getClosestPlayerToEntity(this, -1);
			player.setPositionAndRotation(player.posX, player.posY, player.posZ, this.getYawToFaceEntity(player), this.getPitchToFaceEntity(player));
			player.motionX = 0;
			player.motionZ = 0;
		}
		else if (this.isSilent() && !this.worldObj.isRemote)
		{
			player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 20, 9, false, false));
			player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 20, 9, false, false));
		}
		//allow movement when all bosses spawned in
		if (this.ticksExisted == 270 && this.isSilent())
		{
			this.setSilent(false);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
			this.worldObj.playSound(null, player.getPosition(), getAmbientSound(), getSoundCategory(), 1.0F, 1.0F);
		}
		//move out of water
		if ((this.isInWater() || this.isInLava()) && this.ticksExisted % 10 == 0 && !this.isSilent())
		{
			this.moveRelative(0, 0.2F, 1F);
			this.motionY += 0.5F;
		}
		//tp if too far away
		if (WorldData.get(worldObj).currentEvent.boss != null && this.getDistanceToEntity(WorldData.get(worldObj).currentEvent.boss) > 60D)
			this.setBeaconPosition();
		//typeOnUpdate
		this.typeOnUpdate(player);

		super.onUpdate();
	}

	@Override
	public boolean attackEntityAsMob(Entity entity)
	{
		if (this.type == 2 && !this.worldObj.isRemote && rand.nextInt(5) == 0)
		{//TODO add sound?
			entity.addChatMessage(new TextComponentTranslation(this.getName()+": "+this.butcherTaunts.get(rand.nextInt(butcherTaunts.size()))).setStyle(new Style().setColor(this.getChatColor()).setItalic(true)));
		}

		return super.attackEntityAsMob(entity);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		if (source == DamageSource.fall || source.getEntity() instanceof EntityBossZombie)
			return false;
		if (this.isSilent())
			return false;

		if (this.type == 6 && source.getEntity() instanceof EntityPlayer)
		{
			amount *= 2;
			if (this.worldObj.isRemote)
				this.smithBlockEffect = true;
			else if (rand.nextInt(5) == 0)
			{
				this.smithBlockEffect = true;
				this.dataManager.set(SMITH_BLOCK, (byte)1);
				this.setActiveHand(EnumHand.OFF_HAND);
				this.worldObj.playSound(null, this.getPosition(), SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.HOSTILE, rand.nextFloat(), rand.nextFloat()+0.5f);
				this.worldObj.playSound(null, source.getEntity().getPosition(), SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.HOSTILE, rand.nextFloat(), rand.nextFloat()+0.5f);
				source.getEntity().addChatMessage(new TextComponentTranslation(this.getName()+": "+this.smithTaunts.get(rand.nextInt(smithTaunts.size()))).setStyle(new Style().setColor(this.getChatColor()).setItalic(true)));
				return false;
			}
		}

		return super.attackEntityFrom(source, amount);
	}

	private void typeOnUpdate(EntityPlayer player)
	{
		//smith and butcher weakness
		if ((this.type == 2 || this.type == 6) && this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue() != 2.0D)
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
		//priest effects
		if (!this.worldObj.isRemote)
		{
			if (this.getActivePotionEffect(MobEffects.REGENERATION) != null && this.getActivePotionEffect(MobEffects.REGENERATION).getDuration() > 0) {
				for (int i=0; i<3*this.getActivePotionEffect(MobEffects.REGENERATION).getAmplifier()+1; i++)
					((WorldServer)this.worldObj).spawnParticle(EnumParticleTypes.HEART, (float)posX+(rand.nextFloat()-0.5f)*3f, (float)posY+(rand.nextFloat()+0.0f)*5f, (float)posZ+(rand.nextFloat()-0.5f)*3f, 1, 0, 0, 0, 0, new int[0]);
			}
			if (this.getActivePotionEffect(MobEffects.RESISTANCE) != null && this.getActivePotionEffect(MobEffects.RESISTANCE).getDuration() > 0) {
				for (int i=0; i<10; i++)
					((WorldServer)this.worldObj).spawnParticle(EnumParticleTypes.SMOKE_LARGE, (float)posX+(rand.nextFloat()-0.5f)*3f, (float)posY+(rand.nextFloat()+0.0f)*5f, (float)posZ+(rand.nextFloat()-0.5f)*3f, 1, 0, 0, 0, 0, new int[0]);
			}
			if (this.getActivePotionEffect(MobEffects.STRENGTH) != null && this.getActivePotionEffect(MobEffects.STRENGTH).getDuration() > 0) {
				for (int i=0; i<10; i++)
					((WorldServer)this.worldObj).spawnParticle(EnumParticleTypes.FLAME, (float)posX+(rand.nextFloat()-0.5f)*3f, (float)posY+(rand.nextFloat()+0.0f)*5f, (float)posZ+(rand.nextFloat()-0.5f)*3f, 1, 0, 0, 0, 0, new int[0]);
			}
			if (this.getActivePotionEffect(MobEffects.NAUSEA) != null && this.getActivePotionEffect(MobEffects.NAUSEA).getDuration() > 0) {
				for (int i=0; i<10; i++)
					((WorldServer)this.worldObj).spawnParticle(EnumParticleTypes.SPELL_WITCH, (float)posX+(rand.nextFloat()-0.5f)*3f, (float)posY+(rand.nextFloat()+0.0f)*5f, (float)posZ+(rand.nextFloat()-0.5f)*3f, 1, 0, 0, 0, 0, new int[0]);
				if (this.ticksExisted % 10 == 0)
					this.worldObj.playSound(null, this.getPosition(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.HOSTILE, 1.0F, rand.nextFloat());
			}
		}

		if (this.type == 1 && !this.worldObj.isRemote)
		{
			//populate list of possible summons
			if (this.villagerSummons == null)
			{
				int modifier = -1;
				if (this.worldObj.getDifficulty() == EnumDifficulty.HARD)
					modifier++;
				else if (this.worldObj.getDifficulty() == EnumDifficulty.EASY)
					modifier--;
				villagerSummons = new ArrayList<EntityEventZombie>();
				for (IEventMob zombie : this.getEvent().mobs)
					for (int i=10-((IEventMob)zombie).getProgressOnDeath()+modifier; i>0; i--)
						villagerSummons.add((EntityEventZombie)zombie);
			}
			else if (this.villagerSummonCooldown-- <= 0 && this.ticksExisted > 1000 && this.getAttackTarget() instanceof EntityPlayer && this.getDistanceToEntity(this.getAttackTarget()) < 50D)
			{
				this.villagerSummonCooldown = this.rand.nextInt(300)+900;
				int summonType = rand.nextInt(3);
				if (!this.worldObj.isRemote)
					switch(summonType)
					{
					case 0:
						WorldData.get(worldObj).currentEvent.sendServerMessage(new TextComponentTranslation(this.getName()+": Aid us in the fight, younglings!").setStyle(new Style().setColor(this.getChatColor()).setItalic(true)));
						break;
					case 1:
						WorldData.get(worldObj).currentEvent.sendServerMessage(new TextComponentTranslation(this.getName()+": Rob them blind!").setStyle(new Style().setColor(this.getChatColor()).setItalic(true)));
						break;
					case 2:
						WorldData.get(worldObj).currentEvent.sendServerMessage(new TextComponentTranslation(this.getName()+": Make them burn!").setStyle(new Style().setColor(this.getChatColor()).setItalic(true)));
						break;
					}
				for (int i=0; i < 4; i++)
				{
					try 
					{
						EntityEventZombie zombie = null;
						switch(summonType)
						{
						case 0:
							zombie = this.villagerSummons.get(this.rand.nextInt(this.villagerSummons.size())).getClass().getDeclaredConstructor(World.class).newInstance(this.worldObj);
							break;
						case 1:
							zombie = new EntityZombieThief(this.worldObj);
							break;
						case 2:
							zombie = new EntityZombiePyromaniac(this.worldObj);
							break;
						}
						zombie.summoned = true;
						zombie.getDataManager().set(SUMMONED, (byte)1);
						zombie.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
						zombie.onInitialSpawn(null, null);
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
					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}
			}
		}
		else if (this.type == 3)
		{
			if (this.getHeldItemOffhand() == null)
			{
				if (!this.worldObj.isRemote && this.rand.nextInt(40) == 0)
					this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(this.food.get(this.rand.nextInt(food.size()))));
				else
					this.isFarmerEating = false;
			}
			if (!this.worldObj.isRemote && this.getHeldItemOffhand() != null && this.getHealth() < this.getMaxHealth() && !this.isFarmerEating && this.rand.nextInt(40) == 0)
			{
				this.isFarmerEating = true;
				this.farmerEatingTime = 0;
				this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0D);

			}
			if (this.worldObj.isRemote && this.getHeldItemOffhand() != null && this.getHealth() < this.getMaxHealth() && !this.isFarmerEating && this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue() == 0)
				this.isFarmerEating = true;
			if (this.isFarmerEating && this.farmerEatingTime++ >= 40)
			{
				if (!this.worldObj.isRemote)
				{
					float saturation = ((ItemFood)this.getHeldItemOffhand().getItem()).getSaturationModifier(this.getHeldItemOffhand());
					float heal = ((ItemFood)this.getHeldItemOffhand().getItem()).getHealAmount(this.getHeldItemOffhand());
					this.heal((saturation+heal)*2);
					this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, null);
					this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
					if (rand.nextBoolean() && WorldData.get(worldObj).currentEvent == this.getEvent())
						WorldData.get(worldObj).currentEvent.sendServerMessage(new TextComponentTranslation(this.getName()+": "+this.farmerTaunts.get(rand.nextInt(farmerTaunts.size()))).setStyle(new Style().setColor(this.getChatColor()).setItalic(true)));
				}
				else
					this.farmerEat(16);
			}
			else if (this.isFarmerEating)
				this.farmerEat(5);
		}
		else if (this.type == 5)
		{
			if (this.getHeldItemMainhand() != null && this.getHeldItemMainhand().getItem() instanceof ItemBookOfHealing)
				((ItemBookOfHealing)this.getHeldItemMainhand().getItem()).onUpdate(this.getHeldItemMainhand(), this.worldObj, this, 0, true);
			if (!this.worldObj.isRemote && this.priestEffectTime-- > 0 && this.priestTarget != null)
			{
				this.getLookHelper().setLookPositionWithEntity(priestTarget, 200, 200);
				if (this.getDistanceToEntity(priestTarget) > 15D)
				{
					this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
					this.moveHelper.setMoveTo(priestTarget.posX, priestTarget.posY, priestTarget.posZ, this.getAIMoveSpeed());
				}
				else
					this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
				if (this.priestEffectTime == 0)
				{
					this.setAttackTarget(null);
					this.priestTarget = null;
					this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
				}
			}
			else if (!this.worldObj.isRemote && this.priestCooldown-- <= 0 && this.ticksExisted > 400)
			{
				this.priestCooldown = 300 + rand.nextInt(600);
				this.priestEffectTime = 150;
				this.priestTarget = this.worldObj.findNearestEntityWithinAABB(this.getClass(), this.getEntityBoundingBox().expand(65, 65, 65), this);
				if (this.priestTarget != null)
				{
					this.setAttackTarget(this.priestTarget);
					int effect = rand.nextInt(this.priestTarget.getHealth() < this.priestTarget.getMaxHealth() ? 3 : 2);
					if (this.priestTarget.getHealth() < this.priestTarget.getMaxHealth()/2)
						effect = 2;
					PotionEffect potionEffect;
					switch(effect)
					{
					case 0:
						potionEffect = new PotionEffect(MobEffects.RESISTANCE, 300, 2);
						this.priestTarget.addPotionEffect(potionEffect);
						WorldData.get(worldObj).currentEvent.sendServerMessage(new TextComponentTranslation(this.getName()+": Let my blessings protect you, brother " + this.priestTarget.getName().replace("Boss Zombie ", "")+".").setStyle(new Style().setColor(this.getChatColor()).setItalic(true)));
						break;
					case 1:
						potionEffect = new PotionEffect(MobEffects.STRENGTH, 300, 0);
						this.priestTarget.addPotionEffect(potionEffect);
						WorldData.get(worldObj).currentEvent.sendServerMessage(new TextComponentTranslation(this.getName()+": I give you my strength, brother " + this.priestTarget.getName().replace("Boss Zombie ", "")+".").setStyle(new Style().setColor(this.getChatColor()).setItalic(true)));
						break;
					case 2:
						potionEffect = new PotionEffect(MobEffects.REGENERATION, 300, 2);
						this.priestTarget.addPotionEffect(potionEffect);
						WorldData.get(worldObj).currentEvent.sendServerMessage(new TextComponentTranslation(this.getName()+": Allow me to heal your wounds, brother " + this.priestTarget.getName().replace("Boss Zombie ", "")+".").setStyle(new Style().setColor(this.getChatColor()).setItalic(true)));
						break;
					}
					//used to time particles/sounds for effect
					potionEffect = new PotionEffect(MobEffects.NAUSEA, this.priestEffectTime, 0);
					this.priestTarget.addPotionEffect(potionEffect);
					this.addPotionEffect(potionEffect);
				}
			}
		}
		else if (this.type == 6 && this.worldObj.isRemote && this.dataManager.get(SMITH_BLOCK) == 1 && this.smithBlockEffect)
		{
			this.smithBlockEffect = false;
			this.swingArm(EnumHand.MAIN_HAND);
			for (int i=0; i<100; i++)
				this.worldObj.spawnParticle(EnumParticleTypes.VILLAGER_ANGRY, posX+(rand.nextDouble()-0.5D)*3D, posY+(rand.nextDouble()+0.0D)*5D, posZ+(rand.nextDouble()-0.5D)*3D, 0, 0, 0, 0);
			double x = player.posX - this.posX;
			double z = player.posZ - this.posZ;
			double normalize = (Math.sqrt(Math.pow(x, 2)+Math.pow(z, 2))) * 0.5D;
			player.setVelocity(x/normalize, player.motionY+0.7f, z/normalize);
		}
		if (this.dataManager.get(SMITH_BLOCK) == 1)
		{
			if (this.smithBlockEffect)
				this.smithBlockEffect = false;
			else if (!this.worldObj.isRemote)
				this.dataManager.set(SMITH_BLOCK, (byte)0);
		}
	}

	private void farmerEat(int times)
	{
		for (int i = 0; i < times; ++i)
		{
			Vec3d vec3d = new Vec3d(((double)this.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
			vec3d = vec3d.rotatePitch(-this.rotationPitch * 0.017453292F);
			vec3d = vec3d.rotateYaw(-this.rotationYaw * 0.017453292F);
			double d0 = (double)(-this.rand.nextFloat()) * 0.6D - 0.3D;
			Vec3d vec3d1 = new Vec3d(((double)this.rand.nextFloat() - 0.5D) * 0.3D, d0, 0.6D);
			vec3d1 = vec3d1.rotatePitch(-this.rotationPitch * 0.017453292F);
			vec3d1 = vec3d1.rotateYaw(-this.rotationYaw * 0.017453292F);
			vec3d1 = vec3d1.addVector(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ);
			if (this.getHeldItemOffhand().getHasSubtypes())
				this.worldObj.spawnParticle(EnumParticleTypes.ITEM_CRACK, vec3d1.xCoord, vec3d1.yCoord, vec3d1.zCoord, vec3d.xCoord, vec3d.yCoord + 0.05D, vec3d.zCoord, new int[] {Item.getIdFromItem(this.getHeldItemOffhand().getItem()), this.getHeldItemOffhand().getMetadata()});
			else
				this.worldObj.spawnParticle(EnumParticleTypes.ITEM_CRACK, vec3d1.xCoord, vec3d1.yCoord, vec3d1.zCoord, vec3d.xCoord, vec3d.yCoord + 0.05D, vec3d.zCoord, new int[] {Item.getIdFromItem(this.getHeldItemOffhand().getItem())});
		}
		if (rand.nextBoolean())
			this.worldObj.playSound(null, this.getPosition(), SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.HOSTILE, 0.5F + 0.5F * (float)this.rand.nextInt(2), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
	}

	private float getPitchToFaceEntity(EntityPlayer player)
	{
		double d0 = this.posX - player.posX;
		double d2 = this.posZ - player.posZ;
		double d1;
		d1 = this.posY + (double)this.getEyeHeight() - (player.posY + (double)player.getEyeHeight());
		double d3 = (double)MathHelper.sqrt_double(d0 * d0 + d2 * d2);
		return (float)(-(MathHelper.atan2(d1, d3) * 180.0D / Math.PI));
	}

	private float getYawToFaceEntity(EntityPlayer player)
	{
		double d0 = this.posX - player.posX;
		double d2 = this.posZ - player.posZ;
		MathHelper.sqrt_double(d0 * d0 + d2 * d2);
		return (float)(MathHelper.atan2(d2, d0) * 180.0D / Math.PI) - 90.0F;
	}

	@Override
	public void setBeaconPosition()
	{
		if (WorldData.get(worldObj).currentEvent.boss != null)
		{
			Vec3d spawner = WorldData.get(worldObj).currentEvent.boss.getPositionVector();
			switch (this.type)
			{
			case 1:
				this.setPosition(spawner.xCoord+12D, spawner.yCoord, spawner.zCoord-12D);
				break;
			case 2:
				this.setPosition(spawner.xCoord+15, spawner.yCoord, spawner.zCoord);
				break;
			case 3:
				this.setPosition(spawner.xCoord+12D, spawner.yCoord, spawner.zCoord+12D);
				break;
			case 4:
				this.setPosition(spawner.xCoord-12D, spawner.yCoord, spawner.zCoord+12D);
				break;
			case 5:
				this.setPosition(spawner.xCoord-15, spawner.yCoord, spawner.zCoord);
				break;
			case 6:
				this.setPosition(spawner.xCoord-12D, spawner.yCoord, spawner.zCoord-12D);
				break;
			}
			while (!this.worldObj.canSeeSky(new BlockPos(this)))
			{
				this.setPosition(this.posX, this.posY+1, this.posZ);
				if (this.posY >= posY+30)
				{
					this.setPosition(this.posX, spawner.yCoord, this.posZ);
					break;
				}
			}
		}
	}

	@SuppressWarnings("deprecation")//bc can't figure out how to change villager via forge
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata)
	{
		super.onInitialSpawn(difficulty, livingdata);
		switch(type) {
		case 1:
			this.func_189778_a(ZombieType.VILLAGER_SMITH);
			break;
		case 2:
			this.func_189778_a(ZombieType.VILLAGER_BUTCHER);
			break;
		case 3:
			this.func_189778_a(ZombieType.VILLAGER_FARMER);
			break;
		case 4:
			this.func_189778_a(ZombieType.VILLAGER_LIBRARIAN);
			break;
		case 5:
			this.func_189778_a(ZombieType.VILLAGER_PRIEST);
			break;
		case 6:
			this.func_189778_a(ZombieType.VILLAGER_SMITH);
			break;
		}
		this.isImmuneToFire = true;
		this.dataManager.set(TYPE, type);
		switch (this.type)
		{
		case 1:
			this.setCustomNameTag(this.getChatColor()+""+TextFormatting.BOLD+"Boss Zombie Villager"+TextFormatting.RESET+""+this.getChatColor()+TextFormatting.ITALIC);
			this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
			this.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(Items.IRON_BOOTS));
			this.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(Items.IRON_LEGGINGS));
			this.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
			this.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
			break;
		case 2:
			this.setCustomNameTag(this.getChatColor()+""+TextFormatting.BOLD+"Boss Zombie Butcher"+TextFormatting.RESET+""+this.getChatColor()+TextFormatting.ITALIC);
			this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ModItems.butchersCleaver0));
			break;
		case 3:
			this.setCustomNameTag(this.getChatColor()+""+TextFormatting.BOLD+"Boss Zombie Farmer"+TextFormatting.RESET+""+this.getChatColor()+TextFormatting.ITALIC);
			this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND_HOE));
			this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(this.food.get(this.rand.nextInt(food.size()))));
			break;
		case 4:
			this.setCustomNameTag(this.getChatColor()+""+TextFormatting.BOLD+"Boss Zombie Librarian"+TextFormatting.RESET+""+this.getChatColor()+TextFormatting.ITALIC);
			this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.ENCHANTED_BOOK));
			this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(Items.EXPERIENCE_BOTTLE));
			break;
		case 5:
			this.setCustomNameTag(this.getChatColor()+""+TextFormatting.BOLD+"Boss Zombie Priest"+TextFormatting.RESET+""+this.getChatColor()+TextFormatting.ITALIC);
			this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ModItems.bookOfHealing));
			((ItemBookOfHealing)this.getHeldItemMainhand().getItem()).setActive(this.getHeldItemMainhand(), true);
			break;
		case 6:
			this.setCustomNameTag(this.getChatColor()+""+TextFormatting.BOLD+"Boss Zombie Smith"+TextFormatting.RESET+""+this.getChatColor()+TextFormatting.ITALIC);
			this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND_AXE));
			this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(Items.SHIELD));
			this.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(Items.DIAMOND_BOOTS));
			this.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(Items.DIAMOND_LEGGINGS));
			this.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Items.DIAMOND_CHESTPLATE));
			this.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
			break;
		}
		this.bossInfo.setName(new TextComponentString(this.getCustomNameTag()));
		return livingdata;
	}

	@Override
	public void onDeath(DamageSource cause)
	{
		boolean bossDefeated = true;
		List<EntityBossZombie> entities = this.worldObj.getEntitiesWithinAABB(this.getClass(), getEntityBoundingBox().expand(50, 50, 50));
		for (EntityBossZombie entity : entities)
			if (entity.isEntityAlive() && this != entity)
				bossDefeated = false;
		if (bossDefeated)
			Event.bossDefeated = true;

		super.onDeath(cause);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagCompund)
	{
		tagCompund.setInteger("Type", this.type);
		return super.writeToNBT(tagCompund);
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompund)
	{
		this.type = tagCompund.getInteger("Type");
		super.readFromNBT(tagCompund);
	}

	@Override
	protected float getJumpUpwardsMotion()
	{
		return 0.6F;
	}

	@Override
	protected boolean canDespawn()
	{
		return false;
	}

	@Override
	public void applyEntityCollision(Entity entityIn)
	{
		if (entityIn instanceof EntityBossSpawner)
			return;
		super.applyEntityCollision(entityIn);
	}

	@Override
	public float getEyeHeight()
	{
		return 5.0F;
	}

	@Override
	public TextFormatting getChatColor() {
		switch (this.type)
		{
		case 1:
			return TextFormatting.GRAY;
		case 2:
			return TextFormatting.DARK_RED;
		case 3:
			return TextFormatting.DARK_GREEN;
		case 4:
			return TextFormatting.WHITE;
		case 5:
			return TextFormatting.DARK_PURPLE;
		case 6:
			return TextFormatting.DARK_AQUA;
		}
		return null;
	}

	@Override
	protected boolean canDropLoot()
	{
		return false;
	}

	@Override
	public float getRenderSizeModifier()
	{
		return 3.0F;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean getAlwaysRenderNameTagForRender()
	{
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean canRenderOnFire()
	{
		return false;
	}

	@Override
	public boolean isPotionApplicable(PotionEffect potioneffectIn)
	{
		return !potioneffectIn.getPotion().isBadEffect() || potioneffectIn.getPotion() == MobEffects.NAUSEA;
	}

	protected void updateAITasks()
	{
		super.updateAITasks();
		this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
	}

	@Override
	public void addTrackingPlayer(EntityPlayerMP player)
	{
		super.addTrackingPlayer(player);
		this.bossInfo.addPlayer(player);
	}

	@Override
	public void removeTrackingPlayer(EntityPlayerMP player)
	{
		super.removeTrackingPlayer(player);
		this.bossInfo.removePlayer(player);
	}
}
