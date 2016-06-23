package furgl.mobEvents.common.event;

import furgl.mobEvents.common.config.Config;
import furgl.mobEvents.common.item.drops.IEventItem;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemPickupEvent;

public class UnlockItemEvent 
{
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(ItemPickupEvent event)
	{				
		if (!event.player.worldObj.isRemote && event.pickedUp.getEntityItem().getItem() instanceof IEventItem)
		{
			Config.syncFromConfig(event.player);
			if (!Config.unlockedItems.contains(((IEventItem) event.pickedUp.getEntityItem().getItem()).getName()))
			{
				Config.unlockedItems.add(((IEventItem) event.pickedUp.getEntityItem().getItem()).getName());
				event.player.addChatMessage(new ChatComponentTranslation("Unlocked information about the "+event.pickedUp.getEntityItem().getDisplayName()+" item in the Event Book").setChatStyle(new ChatStyle().setItalic(true).setColor(EnumChatFormatting.DARK_GRAY)));
				Config.syncToConfig(event.player);
			}
		}
	}
}
