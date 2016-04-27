package furgl.mobEvents.client.gui.creativeTab;

import furgl.mobEvents.common.item.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class MobEventsCreativeTab extends CreativeTabs 
{
	public MobEventsCreativeTab(String label) 
	{
		super(label);
	}

	@Override
	public Item getTabIconItem() {
		return ModItems.eventBook;
	}
	
}

