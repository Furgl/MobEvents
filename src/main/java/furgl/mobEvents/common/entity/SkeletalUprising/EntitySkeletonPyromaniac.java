package furgl.mobEvents.common.entity.SkeletalUprising;

import furgl.mobEvents.common.item.ModItems;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntitySkeletonPyromaniac extends EntityEventSkeleton
{
	public EntitySkeletonPyromaniac(World world) 
	{
		super(world);
		this.progressOnDeath = 2;
		this.armorColor = 10027008;
		this.maxSpawnedInChunk = 2;
	}
	
	@Override
	public void setBookDescription()
	{
		this.bookDescription = "Likes lighting players on fire with fire arrows.";
		ItemStack torches = new ItemStack(Item.getItemFromBlock(Blocks.TORCH), rand.nextInt(3)+1);
		ItemStack coal = new ItemStack(Items.COAL, rand.nextInt(3)+1);
		this.addDrops(new ItemStack(ModItems.fireArrow, rand.nextInt(4)+1), 5);
		this.addDrops(torches, 3);
		this.addDrops(coal, 3);
		this.addDrops(Item.getItemFromBlock(Blocks.COAL_BLOCK), 1);
	}
	
	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase target, float p_82196_2_)//copied entirely - changed EntityArrow to EntityFireArrow
    {
        EntityArrow entityarrow = new EntityTippedArrow(this.worldObj, this);
        double d0 = target.posX - this.posX;
        double d1 = target.getEntityBoundingBox().minY + (double)(target.height / 3.0F) - entityarrow.posY;
        double d2 = target.posZ - this.posZ;
        double d3 = (double)MathHelper.sqrt_double(d0 * d0 + d2 * d2);
        entityarrow.setThrowableHeading(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, (float)(14 - this.worldObj.getDifficulty().getDifficultyId() * 4));
        int i = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.POWER, this);
        int j = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.PUNCH, this);
        entityarrow.setDamage((double)(p_82196_2_ * 2.0F) + this.rand.nextGaussian() * 0.25D + (double)((float)this.worldObj.getDifficulty().getDifficultyId() * 0.11F));

        if (i > 0)
        {
            entityarrow.setDamage(entityarrow.getDamage() + (double)i * 0.5D + 0.5D);
        }

        if (j > 0)
        {
            entityarrow.setKnockbackStrength(j);
        }

        if (EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FLAME, this) > 0)
        {
            entityarrow.setFire(100);
        }

        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.worldObj.spawnEntityInWorld(entityarrow);
    }
	
	@Override
	public boolean attackEntityAsMob(Entity entity)
	{
		if (!this.worldObj.isRemote && this.worldObj.isAirBlock(entity.getPosition()))
		{
			this.worldObj.playSound((double)entity.getPosition().getX() + 0.5D, (double)entity.getPosition().getY() + 0.5D, (double)entity.getPosition().getZ() + 0.5D, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, rand.nextFloat() * 0.4F + 0.8F, true);
			this.worldObj.setBlockState(entity.getPosition(), Blocks.FIRE.getDefaultState());
			entity.setFire(3);
		}

		return super.attackEntityAsMob(entity);
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) 
	{ 
		ItemStack stack = new ItemStack(Items.BOW);
		stack.addEnchantment(Enchantments.FLAME, 1);
		this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack);
		this.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(Items.LEATHER_BOOTS));
		this.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(Items.LEATHER_LEGGINGS));
		this.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Items.LEATHER_CHESTPLATE));
		this.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));
		super.setEquipmentBasedOnDifficulty(difficulty);
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata)
	{
		super.onInitialSpawn(difficulty, livingdata);
		this.isImmuneToFire = true;
		this.setCustomNameTag("Skeleton Pyromaniac");
		return livingdata;
	}
}
