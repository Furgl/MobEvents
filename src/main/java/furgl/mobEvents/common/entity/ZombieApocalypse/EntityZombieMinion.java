package furgl.mobEvents.common.entity.ZombieApocalypse;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityZombieMinion extends EntityEventZombie
{
	public EntityZombieMinion(World world) 
	{
		super(world);
		this.progressOnDeath = 1;
	}

	@Override
	public void setBookDescription()
	{
		this.bookDescription = "A Zombie Summoner's tiny minion.";
		this.addDrops(Items.GOLD_INGOT, 3);
		this.addDrops(Items.GOLDEN_APPLE, 3);
		this.addDrops(Items.GLOWSTONE_DUST, 2);
		this.addDrops(Item.getItemFromBlock(Blocks.GOLD_ORE), 1);
		this.addDrops(Item.getItemFromBlock(Blocks.GLOWSTONE), 1);
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		//swap pumpkin head
		if (!this.worldObj.isRemote)
			this.doSpecialRender(this.ticksExisted % 50);
	}

	@Override
	public void doSpecialRender(int displayTicks) 
	{ 
		//swap pumpkin head
		if (displayTicks % 90 == 0)
		{
			if (this.getItemStackFromSlot(EntityEquipmentSlot.HEAD) != null && this.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == Item.getItemFromBlock(Blocks.PUMPKIN))
			{
				this.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Item.getItemFromBlock(Blocks.LIT_PUMPKIN)));
				this.worldObj.playSound(this.posX, this.posY, this.posZ, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.HOSTILE, 1.0F, rand.nextFloat()+0.8F, true);
			}
			else if (this.getItemStackFromSlot(EntityEquipmentSlot.HEAD) != null && this.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == Item.getItemFromBlock(Blocks.LIT_PUMPKIN))
			{
				this.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Item.getItemFromBlock(Blocks.PUMPKIN)));
				this.worldObj.playSound(this.posX, this.posY, this.posZ, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.HOSTILE, 1.0F, rand.nextFloat()/2+1.6F, true);
			}
		}
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(4.0D);
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) 
	{ 
		ItemStack stack = new ItemStack(Items.GOLDEN_BOOTS);
		EnchantmentHelper.addRandomEnchantment(rand, stack, 10, true);
		this.setItemStackToSlot(EntityEquipmentSlot.FEET, stack);
		stack = new ItemStack(Items.GOLDEN_LEGGINGS);
		EnchantmentHelper.addRandomEnchantment(rand, stack, 10, true);
		this.setItemStackToSlot(EntityEquipmentSlot.LEGS, stack);
		stack = new ItemStack(Items.GOLDEN_CHESTPLATE);
		EnchantmentHelper.addRandomEnchantment(rand, stack, 10, true);
		this.setItemStackToSlot(EntityEquipmentSlot.CHEST, stack);
		stack = new ItemStack(Item.getItemFromBlock(Blocks.PUMPKIN));
		this.setItemStackToSlot(EntityEquipmentSlot.HEAD, stack);
		super.setEquipmentBasedOnDifficulty(difficulty);
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata)
	{
		super.onInitialSpawn(difficulty, livingdata);
		this.setChild(true);
		this.setCustomNameTag("Zombie Minion");
		return livingdata;
	}
}
