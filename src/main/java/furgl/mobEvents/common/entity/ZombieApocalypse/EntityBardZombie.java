package furgl.mobEvents.common.entity.ZombieApocalypse;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityBardZombie extends EntityEventZombie
{
	/**Number of ticks the bard will play for*/
	private int isPlaying;
	private Item[] records = {Items.record_cat, Items.record_blocks, Items.record_chirp, 
			Items.record_mellohi, Items.record_stal, Items.record_ward, Items.record_wait};

	public EntityBardZombie(World world) 
	{
		super(world);
		this.setBookDescription();
		this.progressOnDeath = 4;
		this.maxSpawnedInChunk = 1;
	}

	@Override
	public void setBookDescription()
	{
		this.bookDescription = "Plays music to strengthen nearby Zombies.";
		this.addDrops(Item.getItemFromBlock(Blocks.noteblock), 2);
		this.addDrops(Item.getItemFromBlock(Blocks.jukebox), 1);
		this.addDrops(Items.record_11, 1);
	}

	@Override
	protected void addRandomDrop()
	{
		bookDrops.remove(3); //remove record
		super.addRandomDrop();
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(25.0D);
	}

	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();
		
		this.worldObj.getBiomeGenForCoords(this.getPosition()).setColor(1);

		//Armor color and notes
		this.doSpecialRender();

		//Bard's boost
		if (!this.isAIDisabled() && this.isPlaying > 0 && this.ticksExisted % 100 == 0)
		{
			List<EntityEventZombie> zombies = new ArrayList<EntityEventZombie> ();
			zombies = this.worldObj.getEntitiesWithinAABB(EntityEventZombie.class, new AxisAlignedBB(this.posX+20, this.posY+20, this.posZ+20, this.posX-20, this.posY-20, this.posZ-20));
			for (EntityEventZombie zombie : zombies)
				if (!(zombie instanceof EntityBardZombie))
					zombie.applyBardsBoost();
		}

		//Play record
		if (!this.isAIDisabled() && this.isPlaying == 0 && this.worldObj.isAnyPlayerWithinRangeAt(this.posX, this.posY, this.posZ, 20D))
		{
			this.isPlaying = 2400;
			if (this.worldObj.isRemote)
			{
				Minecraft.getMinecraft().getSoundHandler().stopSounds();
				this.worldObj.playRecord(this.getPosition(), "records."+((ItemRecord)records[rand.nextInt(records.length)]).recordName);
			}
		}

		if (this.isPlaying > 0)
			this.isPlaying--;
	}

	@Override
	public void doSpecialRender() 
	{ 
		if (this.worldObj.isRemote)
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

			if (rand.nextInt(2) == 0)
				this.worldObj.spawnParticle(EnumParticleTypes.NOTE, this.posX-2.5D+rand.nextDouble()*4, this.posY+1.0D+rand.nextDouble()*2, this.posZ-2.5D+rand.nextDouble()*4, rand.nextDouble(), 0, 0, null);
		}
	}

	@Override
	public void onDeath(DamageSource cause)
	{
		Minecraft.getMinecraft().getSoundHandler().stopSounds();

		super.onDeath(cause);
	}

	@Override
	protected String getLivingSound()
	{
		return "note.snare";
	}

	@Override
	protected String getHurtSound()
	{
		return "note.pling";
	}

	@Override
	protected String getDeathSound()
	{
		return "note.snare";
	}

	@Override
	protected void playStepSound(BlockPos pos, Block blockIn)
	{
		this.playSound("note.harp", 1.0F, rand.nextFloat()+1F);
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) 
	{ 
		ArrayList<Item> list = new ArrayList<Item>() {{add(Items.record_11);add(Items.record_13);
		add(Items.record_blocks);add(Items.record_cat);add(Items.record_chirp);add(Items.record_far);add(Items.record_mall);
		add(Items.record_mellohi);add(Items.record_stal);add(Items.record_strad);add(Items.record_wait);add(Items.record_ward);}};
		this.setCurrentItemOrArmor(0, new ItemStack(list.get(rand.nextInt(12))));
		this.setCurrentItemOrArmor(1, new ItemStack(Items.leather_boots));
		this.setCurrentItemOrArmor(2, new ItemStack(Items.leather_leggings));
		this.setCurrentItemOrArmor(3, new ItemStack(Items.leather_chestplate));
		this.setCurrentItemOrArmor(4, new ItemStack(Item.getItemFromBlock(Blocks.jukebox)));
		super.setEquipmentBasedOnDifficulty(difficulty);
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata)
	{
		super.onInitialSpawn(difficulty, livingdata);
		this.setCustomNameTag("Zombie Bard");
		return livingdata;
	}
}
