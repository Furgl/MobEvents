package furgl.mobEvents.common.entity.ZombieApocalypse;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityMinionZombie extends EntityEventZombie
{
	public EntityMinionZombie(World world) 
	{
		super(world);
		this.setBookDescription();
		this.progressOnDeath = 1;
	}
	
	@Override
	public void setBookDescription()
	{
		this.bookDescription = "A tiny Zombie Summoner's minion.";
		this.addDrops(Items.gold_ingot, 1);
		this.addDrops(Items.golden_apple, 1);
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		//swap pumpkin head
		if (!this.worldObj.isRemote && this.ticksExisted % 30 == 0)
		{
			if (this.getCurrentArmor(3) != null && this.getCurrentArmor(3).getItem() == Item.getItemFromBlock(Blocks.pumpkin))
			{
				this.setCurrentItemOrArmor(4, new ItemStack(Item.getItemFromBlock(Blocks.lit_pumpkin)));
				this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "fire.ignite", 1.0F, rand.nextFloat()+0.8F);
			}
			else if (this.getCurrentArmor(3) != null && this.getCurrentArmor(3).getItem() == Item.getItemFromBlock(Blocks.lit_pumpkin))
			{
				this.setCurrentItemOrArmor(4, new ItemStack(Item.getItemFromBlock(Blocks.pumpkin)));
				this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "random.fizz", 1.0F, rand.nextFloat()/2+1.6F);
			}
		}
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(4.0D);
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) 
	{ 
		//TODO held item?
		ItemStack stack = new ItemStack(Items.golden_boots);
		EnchantmentHelper.addRandomEnchantment(rand, stack, 10);
		this.setCurrentItemOrArmor(1, stack);
		stack = new ItemStack(Items.golden_leggings);
		EnchantmentHelper.addRandomEnchantment(rand, stack, 10);
		this.setCurrentItemOrArmor(2, stack);
		stack = new ItemStack(Items.golden_chestplate);
		EnchantmentHelper.addRandomEnchantment(rand, stack, 10);
		this.setCurrentItemOrArmor(3, stack);
		stack = new ItemStack(Item.getItemFromBlock(Blocks.pumpkin));
		this.setCurrentItemOrArmor(4, stack);
		for (int i=0; i<this.equipmentDropChances.length; i++)
			this.setEquipmentDropChance(i, 0.02f);
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
