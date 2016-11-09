package furgl.mobEvents.client.gui.creativeTab;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MobsTab extends CreativeTabs 
{
	public ArrayList<ItemStack> orderedMobs;

	public MobsTab(String label) 
	{
		super(label);
		orderedMobs = new ArrayList<ItemStack>();
	}

	@Override
	public Item getTabIconItem() 
	{
		return Items.SPAWN_EGG;
	}

	@Override
	@SideOnly(Side.CLIENT)
    public void displayAllRelevantItems(List<ItemStack> itemStacks)
	{
		super.displayAllRelevantItems(itemStacks);
		itemStacks.clear();
		itemStacks.addAll(orderedMobs);
	}
}

