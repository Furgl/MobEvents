package furgl.mobEvents.common.item.records;


import furgl.mobEvents.common.MobEvents;
import net.minecraft.item.ItemRecord;
import net.minecraft.util.SoundEvent;

public class ItemCustomRecord extends ItemRecord
{
	public ItemCustomRecord(String name, SoundEvent sound) {
		super(name, sound);
	}
	
	@Override
	public net.minecraft.util.ResourceLocation getRecordResource(String name)
    {
        return new net.minecraft.util.ResourceLocation(MobEvents.MODID+":"+name);
    }
}
