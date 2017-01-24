package furgl.mobEvents.common.entity.SkeletalUprising;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.item.ModItems;
import furgl.mobEvents.common.item.drops.ItemThievesMask;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

public class EntitySkeletonThief extends EntityEventSkeleton {
	private EntityAINearestAttackableTarget attackAI;
	private EntityAIHurtByTarget hurtByTargetAI;

	private UUID playerStolenFrom;

	public EntitySkeletonThief(World world) {
		super(world);
		this.progressOnDeath = 4;
		this.armorColor = 0;
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIRestrictSun(this));
        this.tasks.addTask(3, new EntityAIFleeSun(this, 1.0D));
        this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityWolf.class, 6.0F, 1.0D, 1.2D));
        this.tasks.addTask(5, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(6, new EntityAILookIdle(this));
		attackAI = new EntityAINearestAttackableTarget(this, EntityPlayer.class, true);
		hurtByTargetAI = new EntityAIHurtByTarget(this, true, new Class[] {EntityPigZombie.class});
		this.targetTasks.addTask(2, attackAI);
		this.targetTasks.addTask(1, hurtByTargetAI);
	}

	@Override
	public void setBookDescription() {
		this.bookDescription = "Likes to 'borrow' your items.";
		this.addDrops(Items.GOLD_INGOT, 3);
		this.addDrops(Item.getItemFromBlock(Blocks.TNT), 3);
		this.addDrops(Items.EMERALD, 2);
		this.addDrops(Items.DIAMOND, 2);
		this.addDrops(Item.getItemFromBlock(Blocks.GOLD_BLOCK), 2);
		this.addDrops(((ItemThievesMask) ModItems.thievesMask).getItemStack(), 1);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(18.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.33D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
	}

	@Override
	protected boolean canDespawn() {
		return this.playerStolenFrom == null;
	}

	@Override
	public void onDeath(DamageSource cause)	{
		if (!this.worldObj.isRemote && this.playerStolenFrom != null && this.getHeldItemMainhand() != null) {
			EntityPlayer player = this.worldObj.getPlayerEntityByUUID(playerStolenFrom);
			if (player != null) {
				player.addChatMessage(new TextComponentTranslation("Your "+this.getHeldItemMainhand().getDisplayName()+" has been returned.").setStyle(new Style().setItalic(true).setColor(TextFormatting.DARK_GREEN)));
				if (!player.inventory.addItemStackToInventory(this.getHeldItemMainhand()))
					player.entityDropItem(this.getHeldItemMainhand(), 0);
			}
			else
				this.entityDropItem(this.getHeldItemMainhand(), 0);
			this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, null);
		}
		super.onDeath(cause);
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		boolean rt = super.attackEntityAsMob(entityIn);

		if (this.getHeldItemMainhand() == null && entityIn instanceof EntityPlayer && !(entityIn instanceof FakePlayer)) {
			EntityPlayer player = (EntityPlayer) entityIn;
			ArrayList<Integer> itemSlots = new ArrayList<Integer>();
			for (int i = player.inventory.getSizeInventory()-1; i >= 0; i--)
				if (player.inventory.getStackInSlot(i) != null)
					itemSlots.add(i);

			if (itemSlots.size() > 0) {
				Random random = new Random();
				this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, player.inventory.removeStackFromSlot(itemSlots.get(random.nextInt(itemSlots.size()))));
				this.playerStolenFrom = player.getPersistentID();
				player.addChatMessage(new TextComponentTranslation("A Skeleton Thief has stolen your "+this.getHeldItemMainhand().getDisplayName()+"!").setStyle(new Style().setItalic(true).setColor(TextFormatting.DARK_RED)));
			}
		}
		return rt;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		if (this.playerStolenFrom != null)
			tag.setUniqueId(MobEvents.MODID+": playerStolenFrom", playerStolenFrom);

		return super.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)	{
		this.playerStolenFrom = tag.getUniqueId(MobEvents.MODID+": playerStolenFrom");

		super.readFromNBT(tag);
	}

	@Override
	public void onUpdate() {
		//remove attack ai when stolen item
		if (this.getHeldItemMainhand() != null && !this.worldObj.isRemote) {
			boolean hasAttackAI = false;
			for (EntityAITaskEntry entry : this.targetTasks.taskEntries)
				if (entry.action.equals(attackAI)) 
					hasAttackAI = true;
			if (hasAttackAI) {
				this.targetTasks.addTask(1, new EntityAIAvoidEntity(this, EntityPlayer.class, 20.0F, 1.1D, 1.5D));
				this.targetTasks.removeTask(attackAI);
				this.targetTasks.removeTask(hurtByTargetAI);
			}
		}
		//don't attack if holding item
		if (this.getHeldItemMainhand() != null && this.getAttackTarget() != null)
			this.setAttackTarget(null);
		//sneaking
		if (!this.worldObj.isRemote && this.getAttackTarget() instanceof EntityPlayer && !this.isSneaking())
			this.setSneaking(true);
		else if (!this.worldObj.isRemote && !(this.getAttackTarget() instanceof EntityPlayer) && this.isSneaking())
			this.setSneaking(false);
		//particles
		if (this.worldObj.isRemote && this.isSneaking()) {
			this.worldObj.spawnParticle(EnumParticleTypes.TOWN_AURA, this.posX+rand.nextDouble()-0.5D, this.posY+rand.nextDouble()*1.5D, this.posZ+rand.nextDouble()-0.5D, 0, 0, 0, 0);
			this.worldObj.spawnParticle(EnumParticleTypes.TOWN_AURA, this.posX+rand.nextDouble()-0.5D, this.posY+rand.nextDouble()*1.5D, this.posZ+rand.nextDouble()-0.5D, 0, 0, 0, 0);
		}

		super.onUpdate();
	}

	@Override
	public void doSpecialRender(int displayTicks) {
		if (displayTicks % 90 == 0)
			this.setSneaking(!this.isSneaking());
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) { 
		this.setItemStackToSlot(EntityEquipmentSlot.HEAD, ((ItemThievesMask) ModItems.thievesMask).getItemStack());
		this.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Items.LEATHER_CHESTPLATE));
		this.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(Items.LEATHER_LEGGINGS));
		this.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(Items.LEATHER_BOOTS));
		this.setDropChance(EntityEquipmentSlot.HEAD, 0f);
		this.setDropChance(EntityEquipmentSlot.MAINHAND, 0f);
		super.setEquipmentBasedOnDifficulty(difficulty);
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
		super.onInitialSpawn(difficulty, livingdata);
		this.setCustomNameTag("Skeleton Thief");
		return livingdata;
	}
}