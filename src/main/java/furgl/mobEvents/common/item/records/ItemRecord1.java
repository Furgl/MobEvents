package furgl.mobEvents.common.item.records;


import furgl.mobEvents.common.MobEvents;
import net.minecraft.item.ItemRecord;

public class ItemRecord1 extends ItemRecord
{
	public ItemRecord1(String name) {
		super(name);
	}
	
	public net.minecraft.util.ResourceLocation getRecordResource(String name)
    {
        return new net.minecraft.util.ResourceLocation(MobEvents.MODID+":"+name);
    }
}
