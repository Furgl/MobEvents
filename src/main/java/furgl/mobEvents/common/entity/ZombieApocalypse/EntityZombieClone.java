package furgl.mobEvents.common.entity.ZombieApocalypse;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
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
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class EntityZombieClone extends EntityEventZombie
{
	public EntityZombieClone(World world) 
	{
		super(world);
		this.progressOnDeath = 7;
	}
	
	@Override
	public void setBookDescription()
	{
		this.bookDescription = "Copies a nearby player's best armor and weapon.";
		this.addDrops(Items.EMERALD, 1);
		this.addDrops(Items.DIAMOND, 1);
	}
	
	@Override
	public void onUpdate()
	{
		if (this.getItemStackFromSlot(EntityEquipmentSlot.HEAD) == null)
			this.setEquipmentBasedOnDifficulty(null);
		
		super.onUpdate();
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) 
	{ 
		if (this.worldObj == null)
			return;
		this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, null);
		this.setItemStackToSlot(EntityEquipmentSlot.FEET, null);
		this.setItemStackToSlot(EntityEquipmentSlot.LEGS, null);
		this.setItemStackToSlot(EntityEquipmentSlot.CHEST, null);
		this.setItemStackToSlot(EntityEquipmentSlot.HEAD, null);
		EntityPlayer player = this.worldObj.getClosestPlayerToEntity(this, -1);
		if (player != null)
		{
			ItemStack head = new ItemStack(((ItemSkull)Items.SKULL), 1, 3);
			head.setTagCompound(new NBTTagCompound());
			head.getTagCompound().setTag("SkullOwner", new NBTTagString(player.getDisplayNameString()));
			this.setItemStackToSlot(EntityEquipmentSlot.HEAD, head);
			ArrayList<ItemStack> stacks = new ArrayList<ItemStack> ();
			stacks.addAll(Arrays.asList(player.inventory.mainInventory));
			stacks.addAll(Arrays.asList(player.inventory.armorInventory));
			copyBestEquipment(stacks);
			for (int i=0; i<EntityEquipmentSlot.values().length; i++)
				this.setDropChance(EntityEquipmentSlot.values()[i], 0.05f); //% chance matched in book (manually)
			this.setAttackTarget(player);
		}
		super.setEquipmentBasedOnDifficulty(difficulty);
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
						float damageVsEntity = ReflectionHelper.getPrivateValue(ItemTool.class, (ItemTool)stack.getItem(), 2);//ItemTool.class.getDeclaredField("damageVsEntity");
						float newWeaponDamage = damageVsEntity + EnchantmentHelper.getModifierForCreature(stack, this.getCreatureAttribute());
						if (newWeaponDamage > currentWeaponDamage)
						{
							this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack.copy());
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
						float attackDamage = ReflectionHelper.getPrivateValue(ItemSword.class, (ItemSword)stack.getItem(), 0);//ItemSword.class.getDeclaredField("attackDamage");
						float newWeaponDamage = attackDamage + EnchantmentHelper.getModifierForCreature(stack, this.getCreatureAttribute());
						if (newWeaponDamage > currentWeaponDamage)
						{
							this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack.copy());
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
					ArrayList<ItemStack> list = new ArrayList<ItemStack>();
					list.add(stack);
					newProtection += EnchantmentHelper.getEnchantmentModifierDamage(list, DamageSource.causePlayerDamage(this.worldObj.getClosestPlayerToEntity(this, -1)));
					if (((ItemArmor)stack.getItem()).armorType == EntityEquipmentSlot.CHEST) 
					{
						if (newProtection > currentChestplateProtection)
						{
							currentChestplateProtection = newProtection;
							this.setItemStackToSlot(EntityEquipmentSlot.CHEST, stack.copy());
						}
					}
					else if (((ItemArmor)stack.getItem()).armorType == EntityEquipmentSlot.LEGS) 
					{
						if (newProtection > currentLeggingsProtection)
						{
							currentLeggingsProtection = newProtection;
							this.setItemStackToSlot(EntityEquipmentSlot.LEGS, stack.copy());
						}
					}
					else if (((ItemArmor)stack.getItem()).armorType == EntityEquipmentSlot.FEET) 
					{
						if (newProtection > currentBootsProtection)
						{
							currentBootsProtection = newProtection;
							this.setItemStackToSlot(EntityEquipmentSlot.FEET, stack.copy());
						}
					}
				}
				else if (this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND) == null)
					this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack.copy());
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
