package furgl.mobEvents.client.gui.creativeTab;

import java.util.ArrayList;
import java.util.List;

import furgl.mobEvents.common.item.ItemUpgradedAnvil;
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
		//remove old upgraded anvils (bc upgrade ids change for some reason and crash) with new ones
		int index = 0;
		ArrayList<Integer> indiciesToRemove = new ArrayList<Integer>();
		for (int i=0; i<orderedItems.size(); i++) 
			if (orderedItems.get(i).getItem() instanceof ItemUpgradedAnvil) {
				indiciesToRemove.add(i);
				if (index == 0) 
					index = i;
			}
		for (int i=indiciesToRemove.size()-1; i>=0; i--)
			orderedItems.remove((int) indiciesToRemove.get(i));
		List<ItemStack> list = new ArrayList<ItemStack>();
		ModItems.upgradedAnvil.getSubItems(ModItems.upgradedAnvil, this, list);
		for (int i=list.size()-1; i>=0; i--)
			orderedItems.add(index, list.get(i));

		itemStacks.addAll(orderedItems);
	}
}

