package furgl.mobEvents.common.entity.SkeletalUprising;

import java.util.ArrayList;
import java.util.List;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.block.ModBlocks;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntitySkeletonBard extends EntityEventSkeleton
{
	/**Is color increasing or decreasing*/
	private boolean increasing;
	private boolean playedRecord;
	
	public EntitySkeletonBard(World world) 
	{
		super(world);
		this.progressOnDeath = 4;
		this.maxSpawnedInChunk = 2;
	}

	@Override
	public void setBookDescription()
	{
		this.setEquipmentBasedOnDifficulty(null);
		this.bookDescription = "Plays music to strengthen nearby Skeletons.";
		this.addDrops(Item.getItemFromBlock(Blocks.NOTEBLOCK), 3);
		this.addDrops(Item.getItemFromBlock(Blocks.JUKEBOX), 2);
		this.addDrops(this.getHeldItemMainhand(), 2);
		this.addDrops(Item.getItemFromBlock(ModBlocks.bardsJukebox), 1);
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(25.0D);
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		//this.worldObj.getBiomeGenForCoords(this.getPosition()).setColor(1); TODO what does this do?

		//Armor color and particles
		this.doSpecialRender(this.ticksExisted);

		//Bard's boost
		if (this.ticksExisted % 100 == 0)
		{
			List<EntityEventSkeleton> skeletons = new ArrayList<EntityEventSkeleton> ();
			skeletons = this.worldObj.getEntitiesWithinAABB(EntityEventSkeleton.class, new AxisAlignedBB(this.posX+20, this.posY+20, this.posZ+20, this.posX-20, this.posY-20, this.posZ-20));
			for (EntityEventSkeleton skeleton : skeletons)
				if (!(skeleton instanceof EntitySkeletonBard))
					skeleton.applyBardsBoost();
		}

		//Play record
		if (this.worldObj.isRemote && !this.playedRecord && this.getHeldItemMainhand().getItem() instanceof ItemRecord && this.currentWave < 4 && this.worldObj.isAnyPlayerWithinRangeAt(this.posX, this.posY, this.posZ, 20D))
		{
			MobEvents.proxy.playSoundEntity(((ItemRecord)this.getHeldItemMainhand().getItem()).getSound(), this, 2f);
			this.playedRecord = true;
		}
	}

	@Override
	public void doSpecialRender(int displayTicks) 
	{ 
		if (this.worldObj.isRemote)
		{
			for (int i=0; i<EntityEquipmentSlot.values().length; i++)
			{
				if (this.getItemStackFromSlot(EntityEquipmentSlot.values()[i]) != null && this.getItemStackFromSlot(EntityEquipmentSlot.values()[i]).getItem() instanceof ItemArmor && ((ItemArmor)this.getItemStackFromSlot(EntityEquipmentSlot.values()[i]).getItem()).getColor(this.getItemStackFromSlot(EntityEquipmentSlot.values()[i])) != -1)
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

			if (rand.nextInt(2) == 0)
				this.worldObj.spawnParticle(EnumParticleTypes.NOTE, this.posX-2.5D+rand.nextDouble()*4, this.posY+1.0D+rand.nextDouble()*2, this.posZ-2.5D+rand.nextDouble()*4, rand.nextDouble(), 0, 0, null);
		}
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) 
	{ 
		ArrayList<Item> list = new ArrayList<Item>() {{add(Items.RECORD_11);add(Items.RECORD_13);
		add(Items.RECORD_BLOCKS);add(Items.RECORD_CAT);add(Items.RECORD_CHIRP);add(Items.RECORD_FAR);add(Items.RECORD_MALL);
		add(Items.RECORD_MELLOHI);add(Items.RECORD_STAL);add(Items.RECORD_STRAD);add(Items.RECORD_WAIT);add(Items.RECORD_WARD);}};
		this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(list.get(rand.nextInt(list.size()))));
		this.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(Items.LEATHER_BOOTS));
		this.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(Items.LEATHER_LEGGINGS));
		this.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Items.LEATHER_CHESTPLATE));
		this.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Item.getItemFromBlock(ModBlocks.bardsJukebox)));
		super.setEquipmentBasedOnDifficulty(difficulty);
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata)
	{
		super.onInitialSpawn(difficulty, livingdata);
		this.setCustomNameTag("Skeleton Bard");
		return livingdata;
	}
}
