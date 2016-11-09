package furgl.mobEvents.common.entity.ZombieApocalypse;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.item.ModItems;
import furgl.mobEvents.common.item.drops.ItemThievesMask;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

public class EntityZombieThief extends EntityEventZombie
{
	private EntityAINearestAttackableTarget attackAI;
	private EntityPlayer playerStolenFrom;
	private NBTTagCompound tagToRead;

	public EntityZombieThief(World world) 
	{
		super(world);
		this.progressOnDeath = 4;
		this.armorColor = 0;
	}

	@Override
	protected void applyEntityAI()
	{
		//this.tasks.addTask(4, new EntityAIAttackOnCollide(this, EntityVillager.class, 1.0D, true));
		//this.tasks.addTask(4, new EntityAIAttackOnCollide(this, EntityIronGolem.class, 1.0D, true));
		this.tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0D, false));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[] {EntityPigZombie.class}));
		if (this.getHeldItem(EnumHand.MAIN_HAND) == null)
			this.targetTasks.addTask(2, attackAI = new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
		else
			this.targetTasks.addTask(1, new EntityAIAvoidEntity(this, EntityPlayer.class, 20.0F, 1.1D, 1.5D));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityVillager.class, false));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityIronGolem.class, true));
	}

	@Override
	public void setBookDescription()
	{
		this.bookDescription = "Likes to 'borrow' your items.";
		this.addDrops(Items.GOLD_INGOT, 3);
		this.addDrops(Item.getItemFromBlock(Blocks.TNT), 3);
		this.addDrops(Items.EMERALD, 2);
		this.addDrops(Items.DIAMOND, 2);
		this.addDrops(Item.getItemFromBlock(Blocks.GOLD_BLOCK), 2);
		this.addDrops(((ItemThievesMask) ModItems.thievesMask).getItemStack(), 1);
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(18.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.33D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
	}

	@Override
	protected boolean canDespawn()
	{
		return !this.isConverting() && this.playerStolenFrom == null;
	}

	@Override
	public void onDeath(DamageSource cause)
	{
		if (!this.worldObj.isRemote && this.playerStolenFrom != null) {
			playerStolenFrom.addChatMessage(new TextComponentTranslation("Your "+this.getHeldItemMainhand().getDisplayName()+" has been returned.").setStyle(new Style().setItalic(true).setColor(TextFormatting.DARK_GREEN)));
			if (!this.playerStolenFrom.inventory.addItemStackToInventory(this.getHeldItemMainhand()))
				this.playerStolenFrom.entityDropItem(this.getHeldItemMainhand(), 0);
		}
		super.onDeath(cause);
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
    {
		if (!this.worldObj.isRemote && !source.isCreativePlayer() && source.getEntity() instanceof EntityPlayer && !(source.getEntity() instanceof FakePlayer))
			this.setAttackTarget((EntityLivingBase) source.getEntity());
		return super.attackEntityFrom(source, amount);
    }

	@Override
	public boolean attackEntityAsMob(Entity entityIn)
	{
		boolean rt = super.attackEntityAsMob(entityIn);

		if (this.getHeldItemMainhand() == null && entityIn instanceof EntityPlayer && !(entityIn instanceof FakePlayer))
		{
			EntityPlayer player = (EntityPlayer) entityIn;
			ArrayList<Integer> itemSlots = new ArrayList<Integer>();
			for (int i = player.inventory.getSizeInventory()-1; i >= 0; i--)
				if (player.inventory.getStackInSlot(i) != null)
					itemSlots.add(i);

			if (itemSlots.size() > 0) {
				Random random = new Random();
				this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, player.inventory.removeStackFromSlot(itemSlots.get(random.nextInt(itemSlots.size()))));
				this.playerStolenFrom = player;
				this.targetTasks.addTask(1, new EntityAIAvoidEntity(this, EntityPlayer.class, 20.0F, 1.1D, 1.5D));
				this.targetTasks.removeTask(attackAI);
				attackAI = null;
				player.addChatMessage(new TextComponentTranslation("A Zombie Thief has stolen your "+this.getHeldItemMainhand().getDisplayName()+"!").setStyle(new Style().setItalic(true).setColor(TextFormatting.DARK_RED)));
			}
		}
		return rt;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		if (this.playerStolenFrom != null)
			tag.setString(MobEvents.MODID+": playerStolenFrom", this.playerStolenFrom.getPersistentID().toString());

		return super.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		this.tagToRead = tag;

		super.readFromNBT(tag);
	}

	@Override
	public void onUpdate() 
	{
		//change AI if loaded in with item
		if (this.getHeldItemMainhand() != null && this.attackAI != null) {
			this.targetTasks.addTask(1, new EntityAIAvoidEntity(this, EntityPlayer.class, 20.0F, 1.1D, 1.5D));
			this.targetTasks.removeTask(attackAI);
			attackAI = null;
		}
		//load playerStolenFrom
		if (!this.worldObj.isRemote && this.getHeldItemMainhand() != null && this.playerStolenFrom == null && this.tagToRead != null) {
			if (!this.tagToRead.getString(MobEvents.MODID+": playerStolenFrom").isEmpty()) 
				this.playerStolenFrom = this.worldObj.getMinecraftServer().getPlayerList().getPlayerByUUID(UUID.fromString(this.tagToRead.getString(MobEvents.MODID+": playerStolenFrom")));
			if (this.playerStolenFrom != null)
				this.tagToRead = null;
		}
		//no target when stealing
		if (!this.worldObj.isRemote && this.getHeldItemMainhand() != null && this.getAttackTarget() != null)
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
	public void doSpecialRender(int displayTicks)
	{
		if (displayTicks % 90 == 0)
			this.setSneaking(!this.isSneaking());
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) 
	{ 
		this.setItemStackToSlot(EntityEquipmentSlot.HEAD, ((ItemThievesMask) ModItems.thievesMask).getItemStack());
		this.setDropChance(EntityEquipmentSlot.HEAD, 0f);
		this.setDropChance(EntityEquipmentSlot.MAINHAND, 0f);
		super.setEquipmentBasedOnDifficulty(difficulty);
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata)
	{
		super.onInitialSpawn(difficulty, livingdata);
		this.setCustomNameTag("Zombie Thief");
		return livingdata;
	}
}
