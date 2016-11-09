package furgl.mobEvents.client.gui.inventory;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import furgl.mobEvents.common.block.ModBlocks;
import furgl.mobEvents.common.item.drops.ItemAnvilUpgrade;
import furgl.mobEvents.common.tileentity.TileEntityUpgradedAnvil;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerUpgradedRepair extends ContainerRepair
{
	/** Here comes out item you merged and/or renamed. */
	private IInventory outputSlot;
	/** The 2slots where you put your items in that you want to merge and/or rename. */
	private IInventory inputSlots;
	private World theWorld;
	/** The maximum cost of repairing/renaming in the anvil. */
	public int maximumCost;
	/** determined by damage of input item and stackSize of repair materials */
	public int materialCost;
	private String repairedItemName;
	/** The player that has this container open. */
	private final EntityPlayer thePlayer;
	private BlockPos pos;

	public ContainerUpgradedRepair(InventoryPlayer playerInventory, final World worldIn, final BlockPos blockPosIn, EntityPlayer player)
	{
		super(playerInventory, worldIn, blockPosIn, player);
		this.inventorySlots.clear();

		this.pos = blockPosIn;
		this.outputSlot = new InventoryCraftResult();
		this.inputSlots = new InventoryBasic("Repair", true, 2)
		{
			/**
			 * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't
			 * think it hasn't changed and skip it.
			 */
			public void markDirty()
			{
				super.markDirty();
				ContainerUpgradedRepair.this.onCraftMatrixChanged(this);
			}
		};
		this.theWorld = worldIn;
		this.thePlayer = player;
		this.addSlotToContainer(new Slot(this.inputSlots, 0, 27, 47));
		this.addSlotToContainer(new Slot(this.inputSlots, 1, 76, 47));
		this.addSlotToContainer(new Slot(this.outputSlot, 2, 134, 47)
		{
			/**
			 * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
			 */
			public boolean isItemValid(ItemStack stack)
			{
				return false;
			}
			/**
			 * Return whether this slot's stack can be taken from this slot.
			 */
			public boolean canTakeStack(EntityPlayer playerIn)
			{
				return (playerIn.capabilities.isCreativeMode || playerIn.experienceLevel >= ContainerUpgradedRepair.this.maximumCost)/* && ContainerUpgradedRepair.this.maximumCost > 0*/ && this.getHasStack();
			}
			public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack)
			{
				if (ContainerUpgradedRepair.this.pos == BlockPos.ORIGIN)
					return;
				if (!playerIn.capabilities.isCreativeMode && !playerIn.worldObj.isRemote)
					playerIn.addExperienceLevel(-ContainerUpgradedRepair.this.maximumCost);

				float breakChance = net.minecraftforge.common.ForgeHooks.onAnvilRepair(playerIn, stack, ContainerUpgradedRepair.this.inputSlots.getStackInSlot(0), ContainerUpgradedRepair.this.inputSlots.getStackInSlot(1));

				ContainerUpgradedRepair.this.inputSlots.setInventorySlotContents(0, (ItemStack)null);

				if (ContainerUpgradedRepair.this.materialCost > 0)
				{
					ItemStack itemstack = ContainerUpgradedRepair.this.inputSlots.getStackInSlot(1);

					if (itemstack != null && itemstack.stackSize > ContainerUpgradedRepair.this.materialCost)
					{
						itemstack.stackSize -= ContainerUpgradedRepair.this.materialCost;
						ContainerUpgradedRepair.this.inputSlots.setInventorySlotContents(1, itemstack);
					}
					else
						ContainerUpgradedRepair.this.inputSlots.setInventorySlotContents(1, (ItemStack)null);
				}
				else
					ContainerUpgradedRepair.this.inputSlots.setInventorySlotContents(1, (ItemStack)null);

				ContainerUpgradedRepair.this.maximumCost = 0;
				IBlockState iblockstate = worldIn.getBlockState(blockPosIn);

				if (!playerIn.capabilities.isCreativeMode && !worldIn.isRemote && iblockstate.getBlock() == ModBlocks.upgradedAnvil && playerIn.getRNG().nextFloat() < breakChance)
				{
					NBTTagCompound tileNbt = new NBTTagCompound();//added
					worldIn.getTileEntity(blockPosIn).writeToNBT(tileNbt);//added

					int l = ((Integer)iblockstate.getValue(BlockAnvil.DAMAGE)).intValue();
					++l;

					if (l > 2)
					{
						worldIn.setBlockToAir(blockPosIn);
						worldIn.playEvent(1029, blockPosIn, 0);
					}
					else
					{
						worldIn.setBlockState(blockPosIn, iblockstate.withProperty(BlockAnvil.DAMAGE, Integer.valueOf(l)), 2);
						worldIn.getTileEntity(blockPosIn).readFromNBT(tileNbt);//added
						worldIn.playEvent(1030, blockPosIn, 0);
					}
				}
				else if (!worldIn.isRemote)
				{
					worldIn.playEvent(1030, blockPosIn, 0);
				}
			}
		});

		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
				this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
		for (int k = 0; k < 9; ++k)
			this.addSlotToContainer(new Slot(playerInventory, k, 8 + k * 18, 142));
	}

	/**
	 * Callback for when the crafting matrix is changed.
	 */
	@Override
	public void onCraftMatrixChanged(IInventory inventoryIn)
	{
		super.onCraftMatrixChanged(inventoryIn);

		if (inventoryIn == this.inputSlots)
			this.updateRepairOutput();
	}

	/**
	 * called when the Anvil Input Slot changes, calculates the new result and puts it in the output slot
	 */
	@Override
	public void updateRepairOutput()
	{				
		int durabilityRepair = 0;//added
		int durabilityEach = 0;//added

		ItemStack itemstack = this.inputSlots.getStackInSlot(0);
		this.maximumCost = 1;
		int l1 = 0;
		int i2 = 0;
		int j2 = 0;

		if (itemstack == null)
		{
			this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
			this.maximumCost = 0;
		}
		else
		{
			ItemStack itemstack1 = itemstack.copy();
			ItemStack itemstack2 = this.inputSlots.getStackInSlot(1);
			Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemstack1);
			boolean flag = false;
			i2 = i2 + itemstack.getRepairCost() + (itemstack2 == null ? 0 : itemstack2.getRepairCost());
			this.materialCost = 0;

			if (itemstack2 != null)
			{
				//if (!net.minecraftforge.common.ForgeHooks.onAnvilChange(this, itemstack, itemstack2, outputSlot, repairedItemName, i2)) return;
				flag = itemstack2.getItem() == Items.ENCHANTED_BOOK && Items.ENCHANTED_BOOK.getEnchantments(itemstack2).tagCount() > 0;

				//TODO repair with upgrades
				if (this.theWorld.getTileEntity(this.pos) instanceof TileEntityUpgradedAnvil)
				{
					NBTTagCompound tileNbt = new NBTTagCompound();
					this.theWorld.getTileEntity(this.pos).writeToNBT(tileNbt);
					int[] upgradeIds = tileNbt.getIntArray("upgrades");
					for (int id : upgradeIds)
						for (int i =0; i<((ItemAnvilUpgrade) Item.getItemById(id)).repairableItems.size(); i++)
							if (((ItemAnvilUpgrade) Item.getItemById(id)).repairableItems.get(i) == itemstack2.getItem())
							{
								durabilityEach = ((ItemAnvilUpgrade) Item.getItemById(id)).repairableItemDurability.get(i);
								durabilityRepair = durabilityEach*itemstack2.stackSize;
							}
				}

				if (durabilityRepair > 0 || (itemstack1.isItemStackDamageable() && itemstack1.getItem().getIsRepairable(itemstack, itemstack2)))
				{
					int j4 = Math.min(itemstack1.getItemDamage(), itemstack1.getMaxDamage() / 4);

					if (j4 <= 0)
					{
						this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
						this.maximumCost = 0;
						return;
					}

					int l4;

					for (l4 = 0; j4 > 0 && l4 < itemstack2.stackSize; ++l4)
					{
						int j5 = itemstack1.getItemDamage() - j4;
						itemstack1.setItemDamage(j5);
						++l1;
						j4 = Math.min(itemstack1.getItemDamage(), itemstack1.getMaxDamage() / 4);
					}

					this.materialCost = l4;
				}
				else
				{
					if (!flag && (itemstack1.getItem() != itemstack2.getItem() || !itemstack1.isItemStackDamageable()))
					{
						this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
						this.maximumCost = 0;
						return;
					}

					if (itemstack1.isItemStackDamageable() && !flag)
					{
						int k2 = itemstack.getMaxDamage() - itemstack.getItemDamage();
						int l2 = itemstack2.getMaxDamage() - itemstack2.getItemDamage();
						int i3 = l2 + itemstack1.getMaxDamage() * 12 / 100;
						int j3 = k2 + i3;
						int k3 = itemstack1.getMaxDamage() - j3;

						if (k3 < 0)
						{
							k3 = 0;
						}

						if (k3 < itemstack1.getMetadata())
						{
							itemstack1.setItemDamage(k3);
							l1 += 2;
						}
					}

					Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(itemstack2);

                    for (Enchantment enchantment1 : map1.keySet())
                    {
                        if (enchantment1 != null)
                        {
                            int i3 = map.containsKey(enchantment1) ? ((Integer)map.get(enchantment1)).intValue() : 0;
                            int j3 = ((Integer)map1.get(enchantment1)).intValue();
                            j3 = i3 == j3 ? j3 + 1 : Math.max(j3, i3);
                            boolean flag1 = enchantment1.canApply(itemstack);

                            if (this.thePlayer.capabilities.isCreativeMode || itemstack.getItem() == Items.ENCHANTED_BOOK)
                            {
                                flag1 = true;
                            }

                            for (Enchantment enchantment : map.keySet())
                            {
                                if (enchantment != enchantment1 && !(enchantment1.canApplyTogether(enchantment) && enchantment.canApplyTogether(enchantment1)))  //Forge BugFix: Let Both enchantments veto being together
                                {
                                    flag1 = false;
                                    ++l1;
                                }
                            }

                            if (flag1)
                            {
                                if (j3 > enchantment1.getMaxLevel())
                                {
                                    j3 = enchantment1.getMaxLevel();
                                }

                                map.put(enchantment1, Integer.valueOf(j3));
                                int k3 = 0;

                                switch (enchantment1.getRarity())
                                {
                                    case COMMON:
                                        k3 = 1;
                                        break;
                                    case UNCOMMON:
                                        k3 = 2;
                                        break;
                                    case RARE:
                                        k3 = 4;
                                        break;
                                    case VERY_RARE:
                                        k3 = 8;
                                }

                                if (flag)
                                {
                                    k3 = Math.max(1, k3 / 2);
                                }

                                l1 += k3 * j3;
                            }
                        }
                    }
                }
            }

			if (flag && !itemstack1.getItem().isBookEnchantable(itemstack1, itemstack2)) itemstack1 = null;

			if (durabilityRepair > 0)//added - remove xp cost before calculating renaming price
			{
				this.maximumCost = 0;
				i2 = 0;
			}

			if (StringUtils.isBlank(this.repairedItemName))
			{
				if (itemstack.hasDisplayName())
				{
					j2 = 1;
					l1 += j2;
					itemstack1.clearCustomName();
				}
			}
			else if (!this.repairedItemName.equals(itemstack.getDisplayName()))
			{
				j2 = 1;
				l1 += j2;
				itemstack1.setStackDisplayName(this.repairedItemName);
			}

			this.maximumCost = i2 + l1;

			if (durabilityRepair > 0 && this.maximumCost == 1)//added - remove xp cost before calculating renaming price
				this.maximumCost = 0;

			if (l1 <= 0)
			{
				itemstack1 = null;
			}

			if (j2 == l1 && j2 > 0 && this.maximumCost >= 40)
			{
				this.maximumCost = 39;
			}

			if (this.maximumCost >= 40 && !this.thePlayer.capabilities.isCreativeMode)
			{
				itemstack1 = null;
			}

			if (itemstack1 != null)
			{
				int k4 = itemstack1.getRepairCost();

				if (itemstack2 != null && k4 < itemstack2.getRepairCost())
				{
					k4 = itemstack2.getRepairCost();
				}

				k4 = k4 * 2 + 1;
				if (durabilityRepair == 0)//added - don't increase repair cost if doing upgrade repairs
					itemstack1.setRepairCost(k4);
				else
				{
					//TODO
					this.materialCost = itemstack2.stackSize + (itemstack.getItemDamage()-durabilityRepair)/durabilityEach;
					itemstack1.setItemDamage(Math.max(itemstack.getItemDamage()-durabilityRepair, 0));	
				}
				EnchantmentHelper.setEnchantments(map, itemstack1);
			}

			this.outputSlot.setInventorySlotContents(0, itemstack1);
			this.detectAndSendChanges();
		}
	}

	@Override
	public void detectAndSendChanges()
	{
		for (int i = 0; i < this.inventorySlots.size(); ++i)
		{
			ItemStack itemstack = ((Slot)this.inventorySlots.get(i)).getStack();
			ItemStack itemstack1 = (ItemStack)this.inventoryItemStacks.get(i);

			if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)/* || i == 2*/) //changed bc glitch with updating output
			{
				itemstack1 = itemstack == null ? null : itemstack.copy();
				this.inventoryItemStacks.set(i, itemstack1);

				for (int j = 0; j < this.listeners.size(); ++j)
				{
                    ((IContainerListener)this.listeners.get(j)).sendSlotContents(this, i, itemstack1);
				}
			}
		}
	}

	@Override
	public void addListener(IContainerListener listener)
	{
		super.addListener(listener);
		listener.sendProgressBarUpdate(this, 0, this.maximumCost);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int id, int data)
	{
		if (id == 0)
		{
			this.maximumCost = data;
		}
	}

	/**
	 * Called when the container is closed.
	 */
	@Override
	public void onContainerClosed(EntityPlayer playerIn)
	{
		super.onContainerClosed(playerIn);

		if (!this.theWorld.isRemote)
		{
			for (int i = 0; i < this.inputSlots.getSizeInventory(); ++i)
			{
				ItemStack itemstack = this.inputSlots.removeStackFromSlot(i);

				if (itemstack != null)
				{
                    playerIn.dropItem(itemstack, false);
				}
			}
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		return true;//changed
	}

	/**
	 * Take a stack from the specified inventory slot.
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
	{
		ItemStack itemstack = null;
		Slot slot = (Slot)this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (index == 2)
			{
				if (!this.mergeItemStack(itemstack1, 3, 39, true))
				{
					return null;
				}

				slot.onSlotChange(itemstack1, itemstack);
			}
			else if (index != 0 && index != 1)
			{
				if (index >= 3 && index < 39 && !this.mergeItemStack(itemstack1, 0, 2, false))
				{
					return null;
				}
			}
			else if (!this.mergeItemStack(itemstack1, 3, 39, false))
			{
				return null;
			}

			if (itemstack1.stackSize == 0)
			{
				slot.putStack((ItemStack)null);
			}
			else
			{
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize)
			{
				return null;
			}

			slot.onPickupFromSlot(playerIn, itemstack1);
		}

		return itemstack;
	}

	/**
	 * used by the Anvil GUI to update the Item Name being typed by the player
	 */
	@Override
	public void updateItemName(String newName)
	{
		this.repairedItemName = newName;

		if (this.getSlot(2).getHasStack())
		{
			ItemStack itemstack = this.getSlot(2).getStack();

			if (StringUtils.isBlank(newName))
			{
				itemstack.clearCustomName();
			}
			else
			{
				itemstack.setStackDisplayName(this.repairedItemName);
			}
		}

		this.updateRepairOutput();
	}
}
