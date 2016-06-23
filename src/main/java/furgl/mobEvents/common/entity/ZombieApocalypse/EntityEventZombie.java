package furgl.mobEvents.common.entity.ZombieApocalypse;

import java.util.ArrayList;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.Events.Event;
import furgl.mobEvents.common.Events.ZombieApocalypse;
import furgl.mobEvents.common.config.Config;
import furgl.mobEvents.common.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

public class EntityEventZombie extends EntityZombie implements IEventMob
{
	public static final Event event = new ZombieApocalypse();
	/**Bosses have 100 progressOnDeath*/
	public int progressOnDeath;
	public int armorColor = -1;
	public int maxSpawnedInChunk = 4;
	private int bardsBoost;
	public boolean summoned;
	public String bookDescription;
	public ArrayList<ItemStack> bookDrops;
	/**Wave that mob thinks it is currently*/
	public int currentWave;

	public EntityEventZombie(World world) 
	{
		super(world);
		if (world == null)
			this.setEquipmentBasedOnDifficulty(null);
		this.currentWave = Event.currentWave;
	}

	public void setBookDescription()
	{
		this.bookDescription = "";
		this.bookDrops = new ArrayList<ItemStack>();
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
		this.getDataWatcher().addObject(20, Byte.valueOf((byte)0)); //summoned
	}
	
	@Override
	public boolean interact(EntityPlayer player)
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
		if (this.currentWave != Event.currentWave)
		{
			this.currentWave = Event.currentWave;
			this.playLivingSound();
		}

		//set summoned from datawatcher
		if (this.ticksExisted == 1 && this.worldObj.isRemote && this.getDataWatcher().getWatchableObjectByte(20) == 1)
		{
			this.summoned = true;
			this.worldObj.playSound(this.posX, this.posY, this.posZ, "random.fizz", 1f, rand.nextFloat(), false);
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
		if (this.ticksExisted % 20 == 0 && Event.currentEvent.getClass() != ZombieApocalypse.class)
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
					if (this instanceof EntityBardZombie)
						Minecraft.getMinecraft().getSoundHandler().stopSounds();
					else if (this instanceof EntityThiefZombie)
						this.onDeath(DamageSource.outOfWorld);
					this.playSound(this.getDeathSound(), this.getSoundVolume(), this.getSoundPitch());
				}
			}
		}

		//bardsBoost effects
		if (this.bardsBoost > 0 && this.worldObj.isRemote)
		{
			for (int i=0; i<4; i++)
			{
				if (this.getCurrentArmor(i) != null && this.getCurrentArmor(i).getItem() instanceof ItemArmor && ((ItemArmor)this.getCurrentArmor(i).getItem()).getColor(this.getCurrentArmor(i)) != -1)
				{
					int color = ((ItemArmor)this.getCurrentArmor(i).getItem()).getColor(this.getCurrentArmor(i));
					color = color + 5 >= 16777215 ? 0 : color + 5;
					((ItemArmor)this.getCurrentArmor(i).getItem()).setColor(this.getCurrentArmor(i), color);
				}
			}
			if (rand.nextInt(4) == 0)
				this.worldObj.spawnParticle(EnumParticleTypes.NOTE, this.posX-2.5D+rand.nextDouble()*4, this.posY+1.0D+rand.nextDouble()*2, this.posZ-2.5D+rand.nextDouble()*4, rand.nextDouble(), 0, 0, null);
		}

		//stop bardsBoost effects
		if (this.bardsBoost == 1 && this.worldObj.isRemote)
		{
			for (int i=0; i<4; i++)
				if (this.armorColor == -1)
				{
					if (this.getCurrentArmor(i) != null && this.getCurrentArmor(i).getItem() instanceof ItemArmor && ((ItemArmor)this.getCurrentArmor(i).getItem()).getColor(this.getCurrentArmor(i)) != -1)
						((ItemArmor)this.getCurrentArmor(i).getItem()).removeColor(this.getCurrentArmor(i));
				}
				else
				{
					if (this.getCurrentArmor(i) != null && this.getCurrentArmor(i).getItem() instanceof ItemArmor && ((ItemArmor)this.getCurrentArmor(i).getItem()).getColor(this.getCurrentArmor(i)) != -1)
						((ItemArmor)this.getCurrentArmor(i).getItem()).setColor(this.getCurrentArmor(i), this.armorColor);
				}
		}

		if (this.bardsBoost > 0)
			this.bardsBoost--;

		super.onUpdate();
	}

	public void applyBardsBoost()
	{
		this.bardsBoost = 150;
		this.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 200));
		this.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 200));
		this.addPotionEffect(new PotionEffect(Potion.resistance.id, 200));
		this.addPotionEffect(new PotionEffect(Potion.regeneration.id, 200));
		this.addPotionEffect(new PotionEffect(Potion.jump.id, 200, 1));
	}

	@Override
	protected float getSoundPitch()
	{
		return this.isChild() ? (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.2F : (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F;
	}

	@Override
	protected String getLivingSound()
	{
		return this.bardsBoost > 0 ? "note.snare" : MobEvents.MODID+":mob.event_zombie.say";
	}

	@Override
	protected String getHurtSound()
	{
		return this.bardsBoost > 0 ? "note.pling" : MobEvents.MODID+":mob.event_zombie.hurt";
	}

	@Override
	protected String getDeathSound()
	{
		return this.bardsBoost > 0 ? "note.snare" : MobEvents.MODID+":mob.event_zombie.death";
	}

	@Override
	protected void playStepSound(BlockPos pos, Block blockIn)
	{
		if (this.bardsBoost > 0)
			this.playSound("note.harp", 1.0F, rand.nextFloat()+1F);
		else
			this.playSound("mob.zombie.step", 0.15F, 1.0F);
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(60.0D);
	}

	@Override
	public ItemStack getPickedResult(MovingObjectPosition target)
	{
		return ModItems.getSpawnEgg(this);
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) 
	{ 
		for (int i=0; i<5; i++)
			this.setEquipmentDropChance(i, 0.01f);
		if (this.armorColor > -1)
			for (int i=0; i<4; i++)
				if (this.getCurrentArmor(i) != null && this.getCurrentArmor(i).getItem() instanceof ItemArmor && ((ItemArmor)this.getCurrentArmor(i).getItem()).getArmorMaterial() == ItemArmor.ArmorMaterial.LEATHER)
					((ItemArmor)this.getCurrentArmor(i).getItem()).setColor(this.getCurrentArmor(i), this.armorColor);
	}

	@Override
	protected void addRandomDrop()
	{
		if (bookDrops != null && bookDrops.size() > 0)
			this.entityDropItem(bookDrops.get(rand.nextInt(bookDrops.size())), 0);
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata)
	{
		this.setChild(false);
		this.setVillager(false);
		this.setEquipmentBasedOnDifficulty(difficulty);
		return livingdata;
	}

	@Override
	public void onDeath(DamageSource cause)
	{
		if (!this.worldObj.isRemote && cause.getEntity() instanceof EntityPlayer)
		{
			int i = EnchantmentHelper.getLootingModifier((EntityLivingBase)cause.getEntity());
			if (this.recentlyHit > 0 && this.rand.nextInt(100) < (20 + i * 5))
				this.addRandomDrop();
		}

		if (cause.getEntity() instanceof EntityPlayer && !(cause.getEntity() instanceof FakePlayer))
		{
			Event.currentEvent.increaseProgress(progressOnDeath);
			this.unlockEntityInBook((EntityPlayer) cause.getEntity());
			if (!(((EntityPlayer) cause.getEntity()).inventory.hasItem(ModItems.creativeEventBook) || ((EntityPlayer) cause.getEntity()).inventory.hasItem(ModItems.eventBook)))
				this.dropItem(ModItems.eventBook, 1);
		}

		super.onDeath(cause);
	}

	private void unlockEntityInBook(EntityPlayer player)
	{ 
		Config.syncFromConfig(player);
		for (String entity : Config.unlockedEntities)
			if (entity.equals(this.getName()))
				return;
		Config.unlockedEntities.add(this.getName());
		Config.syncToConfig(player);
		if (!player.worldObj.isRemote)
			MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation("Unlocked information about "+(this instanceof EntityThiefZombie ? "Zombie Thieve" : this.getName())+"s in the Event Book").setChatStyle(new ChatStyle().setItalic(true).setColor(EnumChatFormatting.DARK_GRAY)));
	}

	@Override
	protected boolean canDropLoot()
	{
		if (Event.currentEvent.getClass() != ZombieApocalypse.class)
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
	public void doSpecialRender() { }
}
