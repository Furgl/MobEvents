package furgl.mobEvents.client.gui.creativeTab;

import java.util.ArrayList;
import java.util.List;

import furgl.mobEvents.common.item.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemsTab extends CreativeTabs 
{
	public ArrayList<ItemStack> orderedItems;

	public ItemsTab(String label) 
	{
		super(label);
		orderedItems = new ArrayList<ItemStack>();
	}

	@Override
	public Item getTabIconItem() 
	{
		return ModItems.eventBook;
	}

	@Override
	@SideOnly(Side.CLIENT)
    public void displayAllRelevantItems(List<ItemStack> itemStacks)
	{
		super.displayAllRelevantItems(itemStacks);
		//itemStacks.clear();
		//itemStacks.addAll(orderedItems);
	}
}

