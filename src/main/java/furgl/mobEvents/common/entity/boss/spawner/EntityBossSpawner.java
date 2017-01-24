package furgl.mobEvents.common.entity.boss.spawner;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Predicate;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.Events.Event;
import furgl.mobEvents.common.block.BlockBossLoot;
import furgl.mobEvents.common.block.ModBlocks;
import furgl.mobEvents.common.entity.IEventMob;
import furgl.mobEvents.common.entity.ModEntities;
import furgl.mobEvents.common.entity.boss.IEventBoss;
import furgl.mobEvents.common.tileentity.TileEntityBossLoot;
import furgl.mobEvents.common.world.WorldData;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityBossSpawner extends EntityMob implements IEventMob
{
	private static final DataParameter<Integer> STAGE = EntityDataManager.<Integer>createKey(EntityBossSpawner.class, DataSerializers.VARINT);
	protected String bookDescription;
	/**variable stacksize for display in book*/
	protected ArrayList<ItemStack> bookDrops;
	/**max possible stacksize for reference*/
	protected ArrayList<ItemStack> drops;
	private ArrayList<Block> blocks;
	private Vec3d position;
	private int celebrateTime;
	public Event event;
	/**List of bosses that this spawner spawns; used for book gui*/
	public ArrayList<EntityCreature> bossesToSummon = new ArrayList<EntityCreature>();
	protected ItemRecord record;
	private int tpCoolDown;
	/**1 = pre-boss, 2 = boss fight, 3 = post-boss, 4 = destroying blocks, 5 = dead*/
	public int stage;
	private boolean playedRecord;
	private final ArrayList<String> taunts = new ArrayList<String>() {{
		add("There is no escape.");
		add("You cannot run from me!");
		add("Do not run away from me!");
		add("Fight me like a true warrior!");
		add("No one gets away from me.");
		add("I'm not finished with you!");
		add("Do not run and hide like a coward!");
	}};
	//used during initialSpawn to stop errors from .setDead() before spawned
	private boolean killOnceSpawned;

	public EntityBossSpawner(World world) 
	{
		super(world);
		this.setSilent(true);
		this.setNoAI(true);
		this.setSize(5.001f, 3.001f);
		this.event = Event.EVENT;
	}

	public void setBookDescription() 
	{ 
		this.addDrops(Items.NETHER_STAR, 1);
		this.addDrops(new ItemStack(Items.GOLDEN_APPLE, 1, 1), 4);
		this.addDrops(Item.getItemFromBlock(Blocks.ANVIL), 6);
		this.addDrops(Item.getItemFromBlock(Blocks.ENDER_CHEST), 6);
		this.addDrops(Item.getItemFromBlock(Blocks.ENCHANTING_TABLE), 6);
		this.addDrops(Items.DIAMOND_CHESTPLATE, 7);
		this.addDrops(Items.DIAMOND_LEGGINGS, 7);
		this.addDrops(Items.DIAMOND_HELMET, 7);
		this.addDrops(Items.DIAMOND_BOOTS, 7);
		this.addDrops(Items.DIAMOND_SWORD, 7);
		this.addDrops(Items.DIAMOND_PICKAXE, 7);
		this.addDrops(Items.DIAMOND_AXE, 7);
		this.addDrops(Items.DIAMOND_SHOVEL, 7);
		this.addDrops(Items.DIAMOND_HOE, 7);
		this.addDrops(Items.NAME_TAG, 8);
		this.addDrops(Items.SADDLE, 8);
		this.addDrops(new ItemStack(Items.GHAST_TEAR, 5), 9);
		this.addDrops(new ItemStack(Items.BLAZE_ROD, 18), 9);
		this.addDrops(new ItemStack(Items.ENDER_PEARL, 16), 9);
		this.addDrops(new ItemStack(Items.LEATHER, 64), 9);
		this.addDrops(new ItemStack(Items.ARROW, 64), 9);
		this.addDrops(new ItemStack(Items.GOLDEN_APPLE, 9), 11);
		this.addDrops(new ItemStack(Item.getItemFromBlock(Blocks.OBSIDIAN), 32), 11);
		this.addDrops(Items.ENCHANTED_BOOK, 11);
		this.addDrops(new ItemStack(Items.COOKED_BEEF, 64), 15);
		this.addDrops(new ItemStack(Items.EXPERIENCE_BOTTLE, 64), 15);	
		this.addDrops(this.record, 25);
		this.addDrops(new ItemStack(Item.getItemFromBlock(Blocks.AIR)), 80); //33% chance
	}

	/**Number of items in loot chest*/
	public int getNumberOfDrops()
	{
		return 9;
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
		if (this.event == null) //if created for book
			return null;

		if (!this.worldObj.isRemote) {
			//position onto blocks
			this.posX = (int) this.posX + 0.5D;
			this.posY = (int) this.posY - 0.001D;
			this.posZ = (int) this.posZ + 0.5D;
			//kill if can't see sky - check posY+1 bc pos is lowered and it rounds down
			if (!this.worldObj.canSeeSky(new BlockPos(this.posX, this.posY+1, this.posZ)))
				killOnceSpawned = true;
			//validate existing boss
			EntityBossSpawner boss = WorldData.get(worldObj).currentEvent.boss;
			if (boss != null && boss.position != null && !worldObj.getEntitiesWithinAABB(boss.getClass(), new AxisAlignedBB(new BlockPos(boss.position), new BlockPos(boss.position))).isEmpty())
				WorldData.get(worldObj).currentEvent.boss = null;
			//set this to boss
			boss = WorldData.get(worldObj).currentEvent.boss;
			if (boss == null && !killOnceSpawned)
				WorldData.get(worldObj).currentEvent.boss = this;
			//existing boss - delete this and prevent more spawns
			else if (boss != this && boss != null) {
				WorldData.get(worldObj).currentEvent.removeCustomSpawns();
				killOnceSpawned = true;
			}
		}

		return super.onInitialSpawn(difficulty, livingdata);
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.getDataManager().register(STAGE, Integer.valueOf(0)); //stage
	}

	public void spawnBlocks()
	{
		if (!this.isDead)
		{
			//stop more spawning
			WorldData.get(worldObj).currentEvent.removeCustomSpawns();
			//add blocks
			this.addBlocks(Blocks.COAL_ORE, 18);
			this.addBlocks(Blocks.IRON_ORE, 18);
			this.addBlocks(Blocks.LAPIS_ORE, 12);
			this.addBlocks(Blocks.GOLD_ORE, 12);
			this.addBlocks(Blocks.REDSTONE_ORE, 12);
			this.addBlocks(Blocks.IRON_BLOCK, 9);
			this.addBlocks(Blocks.REDSTONE_BLOCK, 7);
			this.addBlocks(Blocks.LAPIS_BLOCK, 7);
			this.addBlocks(Blocks.GOLD_BLOCK, 7);
			this.addBlocks(Blocks.DIAMOND_ORE, 6);
			this.addBlocks(Blocks.EMERALD_ORE, 4);
			this.addBlocks(Blocks.DIAMOND_BLOCK, 1);
			this.addBlocks(Blocks.EMERALD_BLOCK, 1);
			//bottom
			for (int x=-2; x<=2; x++)
				for (int z=-2; z<=2; z++)
					this.worldObj.setBlockState(new BlockPos(this.posX+x, this.posY+1, this.posZ+z), this.blocks.get(worldObj.rand.nextInt(this.blocks.size())).getDefaultState());
			//top
			for (int x=-1; x<=1; x++)
				for (int z=-1; z<=1; z++)
					this.worldObj.setBlockState(new BlockPos(this.posX+x, this.posY+2, this.posZ+z), this.blocks.get(worldObj.rand.nextInt(this.blocks.size())).getDefaultState());
			//glowstone
			this.worldObj.setBlockState(new BlockPos(this.posX, this.posY+2, this.posZ), Blocks.GLOWSTONE.getDefaultState());
			//boss loot
			this.worldObj.setBlockState(new BlockPos(this.posX, this.posY+3, this.posZ), ModBlocks.bossLoot.getDefaultState());
			TileEntityBossLoot chest = (TileEntityBossLoot) this.worldObj.getTileEntity(new BlockPos(this.posX, this.posY+3, this.posZ));
			//chest loot
			for (int i=0; i<this.getNumberOfDrops(); i++)
			{
				ItemStack stack = this.bookDrops.get(this.rand.nextInt(this.bookDrops.size())).copy();
				if (stack.getItem() == Items.ENCHANTED_BOOK)
				{
					stack = new ItemStack(Items.BOOK);
					stack = EnchantmentHelper.addRandomEnchantment(rand, stack, 30, true);
				}
				stack.stackSize = this.rand.nextInt(stack.stackSize)+1;
				if (stack.getItem() != Item.getItemFromBlock(Blocks.AIR))
					chest.setInventorySlotContents(i, stack);
			}
			WorldData.get(worldObj).currentEvent.sendServerMessage(new TextComponentTranslation("A beacon appears in the distance...").setStyle(new Style().setColor(WorldData.get(worldObj).currentEvent.enumColor).setItalic(true)));
		}
	}

	public void destroyBlocks()
	{
		int blocks = 0; 
		if (!this.worldObj.isRemote) {
			for (int x=-2; x<=2; x++)
				for (int z=-2; z<=2; z++)
					for (int y=1; y<=3; y++)
					{
						BlockPos pos = new BlockPos(this.posX+x, this.posY+y, this.posZ+z);
						//spawn fire
						if (this.worldObj.isAirBlock(pos.up()) && !this.worldObj.isAirBlock(pos) && this.worldObj.rand.nextInt(15+blocks) == 0)
							this.worldObj.setBlockState(pos.up(), Blocks.FIRE.getDefaultState());
						else if ((this.worldObj.getBlockState(pos.up()).getBlock() == Blocks.FIRE/* || !this.worldObj.getBlockState(pos).getBlock().isOpaqueCube(this.worldObj.getBlockState(pos))*/) && this.worldObj.rand.nextInt(15+blocks) == 0)
						{
							if (this.worldObj.getBlockState(pos).getBlock() instanceof BlockBossLoot) {
								((TileEntityBossLoot) this.worldObj.getTileEntity(pos)).clear();
								this.worldObj.setBlockToAir(pos.down());
							}
							this.worldObj.setBlockToAir(pos);
						}
						if (!this.worldObj.isAirBlock(new BlockPos(this.posX+x, this.posY+y, this.posZ+z)))
						{
							if (this.worldObj.rand.nextInt(500+blocks*3) == 0 || this.worldObj.getBlockState(pos).getBlock() instanceof BlockBossLoot)
							{
								if (this.worldObj.getBlockState(pos).getBlock() instanceof BlockBossLoot) {
									((TileEntityBossLoot) this.worldObj.getTileEntity(pos)).clear();
									this.worldObj.setBlockToAir(pos.down());
								}
								this.worldObj.addWeatherEffect(new EntityLightningBolt(worldObj, this.posX+x, this.posY+y-1, this.posZ+z, true));
								this.worldObj.setBlockToAir(new BlockPos(this.posX+x, this.posY+y, this.posZ+z));
							}
							if (this.worldObj.rand.nextInt(2) == 0)
								this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX+x+worldObj.rand.nextDouble()-0.5D, this.posY+y+worldObj.rand.nextDouble(), this.posZ+z+worldObj.rand.nextDouble()-0.5D, 0, 0.0f, 0, 0);
							blocks++;
						}
					}
			if (blocks == 0)
				this.setDead();
		}
	}

	@Override
	public void onUpdate()
	{
		if (!this.worldObj.isRemote && killOnceSpawned)
			this.setDead();
		//set position if moved
		if (!this.worldObj.isRemote)
		{
			if (this.worldObj.getBlockState(new BlockPos(this.posX, this.posY+3, this.posZ)).getBlock() instanceof BlockBossLoot && this.position == null)
				this.position = new Vec3d(this.posX, this.posY, this.posZ);
			else
			{
				if (this.position != null)
					this.setPosition(this.position.xCoord, this.position.yCoord, this.position.zCoord);
				if (this.stage != 4 && !(this.worldObj.getBlockState(new BlockPos(this.posX, this.posY+3, this.posZ)).getBlock() instanceof BlockBossLoot) && this.ticksExisted > 1)
					this.setDead();
			}
		}
		//update stage
		if (this.worldObj.isRemote)
			this.stage = this.dataManager.get(STAGE);
		else
		{
			if (this.isDead)
				this.stage = 5;
			else if (WorldData.get(worldObj).currentEvent.getClass() != this.event.getClass() && !Event.bossDefeated && WorldData.get(worldObj).currentEvent.getClass() != Event.CHAOTIC_TURMOIL.getClass())
				this.stage = 4;
			else if (!Event.bossDefeated && this.worldObj.getEntitiesWithinAABB(this.bossesToSummon.get(0).getClass(), getEntityBoundingBox().expand(65, 65, 65)).isEmpty())
				this.stage = 1;
			else if (!Event.bossDefeated && !this.worldObj.getEntitiesWithinAABB(this.bossesToSummon.get(0).getClass(), getEntityBoundingBox().expand(65, 65, 65)).isEmpty())
				this.stage = 2;
			else if (Event.bossDefeated)
				this.stage = 3;
			this.dataManager.set(STAGE, this.stage);
		}
		//start record
		if (this.worldObj.isRemote && !this.playedRecord && this.stage == 2)
		{
			Minecraft.getMinecraft().getSoundHandler().stopSounds();
			MobEvents.proxy.startBossRecord(this.record.getSound(), this, 5f);
			this.playedRecord = true;
		}
		//stop record
		else if (this.worldObj.isRemote && this.playedRecord && this.stage != 2)
		{
			MobEvents.proxy.stopBossRecord();
			this.playedRecord = false;
		}
		//celebrate and kill once boss defeated
		else if (!this.worldObj.isRemote && this.stage == 3)
		{
			if (celebrateTime++ < 150)
			{
				if (celebrateTime == 1 && (WorldData.get(worldObj).currentEvent.getClass() == this.event.getClass() || WorldData.get(worldObj).currentEvent.getClass() == Event.CHAOTIC_TURMOIL.getClass()))
					WorldData.get(worldObj).currentEvent.stopEvent();
				if  (worldObj.rand.nextInt(15) == 0 || celebrateTime == 2)
					this.spawnFireworks();
			}
			else
				this.setDead();
		}
		//spawn blocks
		if (this.ticksExisted == 1 && !this.worldObj.isRemote && this.stage == 1 && !(this.worldObj.getBlockState(new BlockPos(this.posX, this.posY+2, this.posZ)).getBlock() instanceof BlockBossLoot) && !this.isDead)
			this.spawnBlocks();
		//destroy blocks
		if (this.stage == 4 && this.ticksExisted > 3)
			this.destroyBlocks();
		//particles
		if (this.worldObj.isRemote && this.stage >= 1 && this.stage <= 3)
			for (int i=0; i<1; i++)
				this.worldObj.spawnParticle(Event.bossDefeated ? EnumParticleTypes.FIREWORKS_SPARK : EnumParticleTypes.SPELL_MOB_AMBIENT, this.posX+this.worldObj.rand.nextInt(9)+this.worldObj.rand.nextDouble()-4, this.posY+this.worldObj.rand.nextInt(6)+this.worldObj.rand.nextDouble(), this.posZ+this.worldObj.rand.nextInt(9)+this.worldObj.rand.nextDouble()-4, 0, 0, 0, 0);
		//tp player/bosses if too far
		EntityPlayer player = this.worldObj.getClosestPlayerToEntity(this, -1);
		if (this.tpCoolDown-- <= 0 && this.stage == 2 && player != null && !player.capabilities.isCreativeMode && (this.getDistanceToEntity(player) > 60D || this.ticksExisted == 10))
			this.tpPlayerAndBoss(player);

		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;

		super.onUpdate();
	}

	@Override
	public void setDead()
	{
		this.stage = 5;
		if (!this.worldObj.isRemote && (this.worldObj.getBlockState(new BlockPos(this.posX, this.posY+2, this.posZ)).getBlock() instanceof BlockBossLoot || (WorldData.get(worldObj).currentEvent.getClass() != this.event.getClass() && WorldData.get(worldObj).currentEvent.getClass() != Event.CHAOTIC_TURMOIL.getClass())))
		{
			if (this.position == null || WorldData.get(worldObj).currentEvent.boss == null || WorldData.get(worldObj).currentEvent.boss.position.xCoord != this.position.xCoord || WorldData.get(worldObj).currentEvent.boss.position.yCoord != this.position.yCoord || WorldData.get(worldObj).currentEvent.boss.position.zCoord != this.position.zCoord)
				WorldData.get(worldObj).currentEvent.boss = null;
			if (!Event.bossDefeated && this.worldObj.getBlockState(new BlockPos(this.posX, this.posY+2, this.posZ)).getBlock() instanceof BlockBossLoot)
			{
				for (int x=-2; x<=2; x++)
					for (int z=-2; z<=2; z++)
						for (int y=0; y<=2; y++)
						{
							if (this.worldObj.getBlockState(new BlockPos(this.posX+x, this.posY+y, this.posZ+z)).getBlock() instanceof BlockBossLoot)
								((TileEntityBossLoot) this.worldObj.getTileEntity(new BlockPos(this.posX+x, this.posY+y, this.posZ+z))).clear();
							this.worldObj.setBlockToAir(new BlockPos(this.posX+x, this.posY+y, this.posZ+z));
						}
			}
		}

		Event.bossDefeated = false;
		super.setDead();
	}

	@Override
	public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, ItemStack stack, EnumHand hand)
	{
		if (this.stage == 1)
		{
			if (!this.worldObj.isRemote) //kill all nearby IEventMobs before spawning boss
			{
				for (Entity entity : this.worldObj.getEntitiesInAABBexcluding(this, getEntityBoundingBox().expand(50, 50, 50), new Predicate<Entity>(){
					public boolean apply(Entity entity)
					{
						return entity instanceof IEventMob;
					}
				}))
					((EntityLiving) entity).setHealth(0);	
			}
			return EnumActionResult.SUCCESS;
		}
		else
			return EnumActionResult.FAIL;
	}

	private void spawnFireworks()
	{
		ItemStack stack = new ItemStack(Items.FIREWORKS);

		NBTTagCompound nbttagcompound1 = new NBTTagCompound();
		NBTTagCompound nbttagcompound2 = new NBTTagCompound();
		NBTTagCompound nbttagcompound3 = new NBTTagCompound();
		NBTTagList nbttaglist = new NBTTagList();

		nbttagcompound3.setByte("Type", (byte) worldObj.rand.nextInt(5));
		nbttagcompound3.setIntArray("Colors", new int[] {worldObj.rand.nextInt(0xFFFFFF), worldObj.rand.nextInt(0xFFFFFF)});
		nbttagcompound3.setIntArray("FadeColors", new int[] {worldObj.rand.nextInt(0xFFFFFF), worldObj.rand.nextInt(0xFFFFFF)});
		nbttagcompound3.setBoolean("Trail", worldObj.rand.nextBoolean());
		nbttagcompound3.setBoolean("Flicker", worldObj.rand.nextBoolean());
		nbttaglist.appendTag(nbttagcompound3);

		nbttagcompound2.setTag("Explosions", nbttaglist);
		nbttagcompound2.setByte("Flight", (byte)0.5);
		nbttagcompound1.setTag("Fireworks", nbttagcompound2);

		stack.setTagCompound(nbttagcompound1);
		double x = this.posX+this.worldObj.rand.nextInt(7)+this.worldObj.rand.nextDouble()-3;
		double y = this.posY+this.worldObj.rand.nextInt(6)+this.worldObj.rand.nextDouble();
		double z = this.posZ+this.worldObj.rand.nextInt(7)+this.worldObj.rand.nextDouble()-3;
		if (this.worldObj.isAirBlock(new BlockPos(x, y, z)))
		{
			EntityFireworkRocket rocket = new EntityFireworkRocket(worldObj, x, y, z, stack);
			this.worldObj.spawnEntityInWorld(rocket);
		}
	}

	public void tpPlayerAndBoss(EntityPlayer player)
	{
		this.tpCoolDown = 200;
		if (!this.worldObj.isRemote && player != null)
		{
			player.setPositionAndUpdate(posX, posY+3, posZ);
			List<EntityCreature> list = this.worldObj.getEntitiesWithinAABB(this.bossesToSummon.get(0).getClass(), getEntityBoundingBox().expand(65, 65, 65));
			for (EntityCreature mob : list)
			{
				((IEventBoss) mob).setBeaconPosition();
				mob.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100, 2, true, false));
				mob.setAttackTarget(player);
			}
			this.worldObj.playSound(player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ENDERDRAGON_GROWL, SoundCategory.HOSTILE, 1.0F, 1.5F, true);
			if (!list.isEmpty())
				player.addChatMessage(new TextComponentTranslation(list.get(0).getName()+": "+this.taunts.get(rand.nextInt(taunts.size()))).setStyle(new Style().setColor(((IEventBoss)list.get(0)).getChatColor()).setItalic(true)));
			player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200, 5, true, false));
			player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 20, 100, true, false));
			player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 40, 0, true, false));
			player.motionX = 0;
			player.motionY = 0;
			player.motionZ = 0;
		}
	}

	public boolean isBossAlive() 
	{
		return !this.worldObj.getEntitiesWithinAABB(this.bossesToSummon.get(0).getClass(), getEntityBoundingBox().expand(65, 65, 65)).isEmpty();
	}

	@Override
	public ItemStack getPickedResult(RayTraceResult target)	{
		return ModEntities.getSpawnEgg(this.getClass());
	}

	@Override
	public boolean canBeCollidedWith()
	{
		return !this.isDead && this.stage != 3;
	}

	@Override
	protected void collideWithEntity(Entity entityIn) { }

	@Override
	public boolean canBePushed()
	{
		return false;
	}

	@Override
	public boolean isPushedByWater()
	{
		return false;
	}

	@Override
	protected boolean canDespawn()
	{
		return false;
	}

	@Override
	public void moveEntity(double x, double y, double z) { }

	@Override
	public void moveRelative(float strafe, float forward, float friction) { }

	@Override
	public boolean isEntityInvulnerable(DamageSource source)
	{
		return true;
	}

	private void addBlocks(Block block, int amount)
	{
		if (this.blocks == null)
			this.blocks = new ArrayList<Block>();
		for (int i=0; i<amount; i++)
			this.blocks.add(block);
	}

	@Override
	public int getProgressOnDeath() {
		return 100;
	}

	protected void addDrops(Item item, int amount)
	{
		//max stacksize for drops
		if (this.drops == null)
			this.drops = new ArrayList<ItemStack>();
		for (int i=0; i<amount; i++)
			this.drops.add(new ItemStack(item));
		//variable stacksize for bookDrops
		if (this.bookDrops == null)
			this.bookDrops = new ArrayList<ItemStack>();
		for (int i=0; i<amount; i++)
			this.bookDrops.add(new ItemStack(item));
	}

	protected void addDrops(ItemStack stack, int amount)
	{
		//max stacksize for drops
		if (this.drops == null)
			this.drops = new ArrayList<ItemStack>();
		for (int i=0; i<amount; i++)
			this.drops.add(stack.copy());
		//variable stacksize for bookDrops
		stack.stackSize = this.rand.nextInt(stack.stackSize)+1;
		if (this.bookDrops == null)
			this.bookDrops = new ArrayList<ItemStack>();
		for (int i=0; i<amount; i++)
			this.bookDrops.add(stack);
	}

	@Override
	public String getBookDescription() {
		return this.bookDescription;
	}

	@Override
	public ArrayList<ItemStack> getBookDrops() {
		return this.bookDrops;
	}

	@Override
	public void doSpecialRender(int displayTicks) 
	{	
		//vary stacksize in bookDrops
		if (displayTicks % 90 == 0)
		{
			for (int i=0; i<this.drops.size(); i++)
			{
				ItemStack stack = this.drops.get(i).copy();
				stack.stackSize = this.rand.nextInt(stack.stackSize)+1;
				this.bookDrops.set(i, stack);
			}
		}
	}

	@Override
	public Event getEvent() {
		return Event.EVENT;
	}
}
