package furgl.mobEvents.common.entity.ZombieApocalypse;

import java.util.ArrayList;

import furgl.mobEvents.common.Events.Event;
import furgl.mobEvents.common.Events.ZombieApocalypse;
import furgl.mobEvents.common.block.BlockBossLoot;
import furgl.mobEvents.common.block.BlockDisappearingFire;
import furgl.mobEvents.common.block.ModBlocks;
import furgl.mobEvents.common.item.ModItems;
import furgl.mobEvents.common.tileentity.TileEntityBossLoot;
import net.minecraft.block.Block;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityBossZombieSpawner extends EntityEventZombie
{
	private ArrayList<Block> blocks;
	private Vec3 position;
	private int celebrateTime;

	public EntityBossZombieSpawner(World world) {
		super(world);
		this.setSilent(true);
		this.setNoAI(true);
		this.setSize(5f, 3f);
		this.multiplySize(1f);
	}

	public void spawnBlocks()
	{
		//kill if can't see sky
		if (!this.worldObj.canSeeSky(new BlockPos(this.posX, this.posY, this.posZ)))
			this.setDead();
		if (!this.isDead)
		{
			//stop more spawning
			Event.currentEvent.removeCustomSpawns();
			//add blocks
			this.addBlocks(Blocks.coal_ore, 18);
			this.addBlocks(Blocks.iron_ore, 18);
			this.addBlocks(Blocks.lapis_ore, 12);
			this.addBlocks(Blocks.gold_ore, 12);
			this.addBlocks(Blocks.redstone_ore, 12);
			this.addBlocks(Blocks.iron_block, 9);
			this.addBlocks(Blocks.redstone_block, 7);
			this.addBlocks(Blocks.lapis_block, 7);
			this.addBlocks(Blocks.gold_block, 7);
			this.addBlocks(Blocks.diamond_ore, 6);
			this.addBlocks(Blocks.emerald_ore, 4);
			this.addBlocks(Blocks.diamond_block, 1);
			this.addBlocks(Blocks.emerald_block, 1);
			//bottom
			for (int x=-2; x<=2; x++)
				for (int z=-2; z<=2; z++)
					this.worldObj.setBlockState(new BlockPos(this.posX+x, this.posY, this.posZ+z), this.blocks.get(worldObj.rand.nextInt(this.blocks.size())).getDefaultState());
			//top
			for (int x=-1; x<=1; x++)
				for (int z=-1; z<=1; z++)
					this.worldObj.setBlockState(new BlockPos(this.posX+x, this.posY+1, this.posZ+z), this.blocks.get(worldObj.rand.nextInt(this.blocks.size())).getDefaultState());
			//glowstone
			this.worldObj.setBlockState(new BlockPos(this.posX, this.posY+1, this.posZ), Blocks.glowstone.getDefaultState());
			//boss loot
			this.worldObj.setBlockState(new BlockPos(this.posX, this.posY+2, this.posZ), ModBlocks.boss_loot.getDefaultState());
			TileEntityBossLoot chest = (TileEntityBossLoot) this.worldObj.getTileEntity(new BlockPos(this.posX, this.posY+2, this.posZ));
			chest.setInventorySlotContents(0, new ItemStack(ModItems.record1));
			MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation("A beacon appears in the distance...").setChatStyle(new ChatStyle().setColor(Event.currentEvent.enumColor).setItalic(true)));
			System.out.println("Spawning: "+new BlockPos(this.posX, this.posY, this.posZ));
		}
	}

	public void destroyBlocks()
	{
		int blocks = 0;
		for (int x=-2; x<=2; x++)
			for (int z=-2; z<=2; z++)
				for (int y=0; y<=2; y++)
					if (!this.worldObj.isAirBlock(new BlockPos(this.posX+x, this.posY+y, this.posZ+z)))
					{
						if (this.worldObj.isRemote && this.worldObj.rand.nextInt(500+blocks*3) == 0)
						{
							this.worldObj.addWeatherEffect(new EntityLightningBolt(worldObj, this.posX+x, this.posY+y, this.posZ+z));
							this.worldObj.playSound(this.posX, this.posY, this.posZ, "ambient.weather.thunder", 10000.0F, 0.8F + this.rand.nextFloat() * 0.2F, true);
							this.worldObj.playSound(this.posX, this.posY, this.posZ, "random.explode", 2.0F, 0.5F + this.rand.nextFloat() * 0.2F, true);
						}
						if (this.worldObj.rand.nextInt(2) == 0)
							this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX+x+worldObj.rand.nextDouble()-0.5D, this.posY+y+worldObj.rand.nextDouble(), this.posZ+z+worldObj.rand.nextDouble()-0.5D, 0, 0.0f, 0, 0);
						blocks++;
					}
		if (!this.worldObj.isRemote) {
			for (int x=-2; x<=2; x++)
				for (int z=-2; z<=2; z++)
					for (int y=0; y<=2; y++)
					{
						BlockPos pos = new BlockPos(this.posX+x, this.posY+y, this.posZ+z);
						if (this.worldObj.isAirBlock(pos.up()) && !this.worldObj.isAirBlock(pos) && this.worldObj.rand.nextInt(40+blocks) == 0)
							this.worldObj.setBlockState(pos.up(), ModBlocks.disappearingFire.getDefaultState());
						else if ((this.worldObj.getBlockState(pos.up()).getBlock() instanceof BlockDisappearingFire || !this.worldObj.getBlockState(pos.up()).getBlock().isOpaqueCube()) && this.worldObj.rand.nextInt(40+blocks) == 0)
						{
							if (this.worldObj.getBlockState(pos).getBlock() instanceof BlockBossLoot)
								((TileEntityBossLoot) this.worldObj.getTileEntity(pos)).clear();
							this.worldObj.setBlockToAir(pos);
						}
					}
			if (blocks == 0)
				this.setDead();
		}
	}

	@Override
	public void onUpdate()
	{
		//destroy blocks
		if (Event.currentEvent.getClass() != ZombieApocalypse.class && !this.isDead && !Event.bossDefeated)
			this.destroyBlocks();
		else
		{
			//celebrate and kill once boss defeated
			if (!this.isDead && Event.bossDefeated && !this.worldObj.isRemote)
			{
				if (celebrateTime++ < 150)
				{
					if (celebrateTime == 1)
						Event.currentEvent.stopEvent();
					if  (worldObj.rand.nextInt(15) == 0 || celebrateTime == 1)
					{
						ItemStack stack = new ItemStack(Items.fireworks);

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
				}
				else
				{
					Event.bossDefeated = true;
					this.setDead();
				}
			}
			//set position if moved
			if (!this.worldObj.isRemote && !this.isDead)
			{
				if (this.worldObj.getBlockState(new BlockPos(this.posX, this.posY+2, this.posZ)).getBlock() instanceof BlockBossLoot && this.position == null)
					this.position = new Vec3(this.posX, this.posY, this.posZ);
				else
				{
					if (this.position != null)
						this.setPosition(this.position.xCoord, this.position.yCoord, this.position.zCoord);
					if (!(this.worldObj.getBlockState(new BlockPos(this.posX, this.posY+2, this.posZ)).getBlock() instanceof BlockBossLoot) && this.ticksExisted > 1)
						this.setDead();
				}
			}
			//set/check currentEvent's boss
			if (!this.worldObj.isRemote && !this.isDead)
			{
				if (Event.currentEvent.boss == null)
					Event.currentEvent.boss = this;
				else if (this.position == null || Event.currentEvent.boss == null || Event.currentEvent.boss.position.xCoord != this.position.xCoord || Event.currentEvent.boss.position.yCoord != this.position.yCoord || Event.currentEvent.boss.position.zCoord != this.position.zCoord) {
					System.out.println("Existing boss: "+Event.currentEvent.boss.position);
					Event.currentEvent.removeCustomSpawns();
					this.setDead();
				}
			}
			//spawn blocks
			if (this.ticksExisted == 1 && !this.worldObj.isRemote && !(this.worldObj.getBlockState(new BlockPos(this.posX, this.posY+2, this.posZ)).getBlock() instanceof BlockBossLoot) && !this.isDead)
				this.spawnBlocks();
			//particles
			if (this.worldObj.isRemote)
				for (int i=0; i<1; i++)
					this.worldObj.spawnParticle(Event.bossDefeated ? EnumParticleTypes.FIREWORKS_SPARK : EnumParticleTypes.SPELL_MOB_AMBIENT, this.posX+this.worldObj.rand.nextInt(9)+this.worldObj.rand.nextDouble()-4, this.posY+this.worldObj.rand.nextInt(6)+this.worldObj.rand.nextDouble(), this.posZ+this.worldObj.rand.nextInt(9)+this.worldObj.rand.nextDouble()-4, 0, 0, 0, 0);
		}

		super.onUpdate();
	}

	@Override
	public void setDead()
	{
		if (!this.worldObj.isRemote && this.worldObj.getBlockState(new BlockPos(this.posX, this.posY+2, this.posZ)).getBlock() instanceof BlockBossLoot)
		{
			if (this.position == null || Event.currentEvent.boss == null || Event.currentEvent.boss.position.xCoord != this.position.xCoord || Event.currentEvent.boss.position.yCoord != this.position.yCoord || Event.currentEvent.boss.position.zCoord != this.position.zCoord)
				Event.currentEvent.boss = null;
			System.out.println("Killing: "+new BlockPos(this.posX, this.posY, this.posZ));
			if (!Event.bossDefeated)
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

		super.setDead();Event.bossDefeated = false;//TODO remove
	}
	
	@Override
	public boolean interact(EntityPlayer player)
    {
		Event.bossDefeated = true;//TODO remove
		return true;
    }

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
}
