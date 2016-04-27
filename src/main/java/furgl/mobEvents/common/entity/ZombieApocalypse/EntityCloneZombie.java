package furgl.mobEvents.common.entity.ZombieApocalypse;

import java.lang.reflect.Field;
import java.util.ArrayList;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.DamageSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import scala.actors.threadpool.Arrays;

public class EntityCloneZombie extends EntityEventZombie
{
	public EntityCloneZombie(World world) 
	{
		super(world);
		this.setBookDescription();
		this.progressOnDeath = 7;
	}
	
	@Override
	public void setBookDescription()
	{
		this.bookDescription = "Copies a nearby player's best armor and weapon.";
		this.bookDrops = new ArrayList<Item>();
		bookDrops.add(Items.iron_ingot);bookDrops.add(Items.iron_ingot);
		bookDrops.add(Items.gold_ingot);bookDrops.add(Items.gold_ingot);
		bookDrops.add(Items.golden_apple);bookDrops.add(Items.golden_apple);
		bookDrops.add(Items.emerald);
		bookDrops.add(Items.diamond);
	}
	
	@Override
	protected void addRandomDrop()
	{
		this.dropItem(bookDrops.get(rand.nextInt(bookDrops.size())), 1);
	}
	
	@Override
	public void onUpdate()
	{
		if (this.getCurrentArmor(3) == null)
			this.setEquipmentBasedOnDifficulty(null);
		
		super.onUpdate();
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) 
	{ 
		if (this.worldObj == null)
			return;
		EntityPlayer player = this.worldObj.getClosestPlayerToEntity(this, -1);
		if (player != null)
		{
			ItemStack head = new ItemStack(((ItemSkull)Items.skull), 1, 3);
			head.setTagCompound(new NBTTagCompound());
			head.getTagCompound().setTag("SkullOwner", new NBTTagString(player.getDisplayNameString()));
			this.setCurrentItemOrArmor(4, head);
			ArrayList<ItemStack> stacks = new ArrayList<ItemStack> ();
			stacks.addAll(Arrays.asList(player.inventory.mainInventory));
			stacks.addAll(Arrays.asList(player.inventory.armorInventory));
			copyBestEquipment(stacks);
			for (int i=0; i<this.equipmentDropChances.length; i++)
				this.setEquipmentDropChance(i, 0.25f / this.equipmentDropChances.length);
			this.setAttackTarget(player);
		}
	}

	public void copyBestEquipment(ArrayList<ItemStack> stacks)
	{
		float currentWeaponDamage = 0;
		float currentChestplateProtection = 0;
		float currentLeggingsProtection = 0;
		float currentBootsProtection = 0;
		for (ItemStack stack : stacks)
		{
			if (stack != null)
			{
				if (stack.getItem() instanceof ItemTool)
				{
					try 
					{
						Field field = ItemTool.class.getDeclaredField("damageVsEntity");
						field.setAccessible(true);
						float newWeaponDamage = field.getFloat(stack.getItem()) + EnchantmentHelper.func_152377_a(stack, this.getCreatureAttribute());
						if (newWeaponDamage > currentWeaponDamage)
						{
							this.setCurrentItemOrArmor(0, stack.copy());
							currentWeaponDamage = newWeaponDamage;
						}
					} 
					catch (Exception e) { 
						e.printStackTrace();
					}
				}
				else if (stack.getItem() instanceof ItemSword)
				{
					try 
					{
						Field field = ItemSword.class.getDeclaredField("attackDamage");
						field.setAccessible(true);
						float newWeaponDamage = field.getFloat(stack.getItem()) + EnchantmentHelper.func_152377_a(stack, this.getCreatureAttribute());
						if (newWeaponDamage > currentWeaponDamage)
						{
							this.setCurrentItemOrArmor(0, stack.copy());
							currentWeaponDamage = newWeaponDamage;
						}
					} 
					catch (Exception e) { 
						e.printStackTrace();
					}
				}
				else if (stack.getItem() instanceof ItemArmor)
				{
					int newProtection = ((ItemArmor)stack.getItem()).damageReduceAmount;
					newProtection += EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[] {stack}, DamageSource.causePlayerDamage(this.worldObj.getClosestPlayerToEntity(this, -1)));
					if (((ItemArmor)stack.getItem()).armorType == 1) //chestplate
					{
						if (newProtection > currentChestplateProtection)
						{
							currentChestplateProtection = newProtection;
							this.setCurrentItemOrArmor(3, stack.copy());
						}
					}
					else if (((ItemArmor)stack.getItem()).armorType == 2) //leggings
					{
						if (newProtection > currentLeggingsProtection)
						{
							currentLeggingsProtection = newProtection;
							this.setCurrentItemOrArmor(2, stack.copy());
						}
					}
					else if (((ItemArmor)stack.getItem()).armorType == 3) //boots
					{
						if (newProtection > currentBootsProtection)
						{
							currentBootsProtection = newProtection;
							this.setCurrentItemOrArmor(1, stack.copy());
						}
					}
				}
				else if (this.getEquipmentInSlot(0) == null)
					this.setCurrentItemOrArmor(0, stack.copy());
			}
		}
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata)
	{
		super.onInitialSpawn(difficulty, livingdata);
		this.setCustomNameTag("Zombie Clone");
		return livingdata;
	}
}
