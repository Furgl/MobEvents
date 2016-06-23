package furgl.mobEvents.common.inventory;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerBossLoot extends Container
{
	private int numRows;
	private IInventory lowerChestInventory;

	public ContainerBossLoot(IInventory playerInventory, IInventory chestInventory, EntityPlayer player) 
	{
        this.lowerChestInventory = chestInventory;
		this.numRows = 1;//chestInventory.getSizeInventory() / 9;
		chestInventory.openInventory(player);
		inventoryItemStacks = Lists.<ItemStack>newArrayList();
		inventorySlots = Lists.<Slot>newArrayList();
		for (int j = 0; j < this.numRows; ++j)
			for (int k = 0; k < 9; ++k)
				this.addSlotToContainer(new SlotBossLoot(chestInventory, k + j * 9, 8 + k * 18, 18 + j * 18));
		for (int l = 0; l < 3; ++l)
			for (int j1 = 0; j1 < 9; ++j1)
				this.addSlotToContainer(new Slot(playerInventory, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + (this.numRows - 4) * 18));
		for (int i1 = 0; i1 < 9; ++i1)
			this.addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 161 + (this.numRows - 4) * 18));
	}

	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
	{
		ItemStack itemstack = null;
		Slot slot = (Slot)this.inventorySlots.get(index);
		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (index < this.numRows * 9)
			{
				if (!this.mergeItemStack(itemstack1, this.numRows * 9, this.inventorySlots.size(), true))
					return null;
			}
			else if (!this.mergeItemStack(itemstack1, 0, this.numRows * 9, false))
				return null;
			if (itemstack1.stackSize == 0)
				slot.putStack((ItemStack)null);
			else
				slot.onSlotChanged();
		}
		return itemstack;
	}
	
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);
        this.lowerChestInventory.closeInventory(playerIn);
    }

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) 
	{
		return true;
	}
}
