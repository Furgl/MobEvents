package furgl.mobEvents.common.event;

import furgl.mobEvents.common.Events.Event;
import furgl.mobEvents.common.config.Config;
import furgl.mobEvents.common.item.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class FirstJoinEvent 
{
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void firstJoin(PlayerLoggedInEvent event) 
	{
		//check if event should be unlocked
		Config.syncFromConfig(event.player);
		if (Event.currentEvent.getClass() != Event.class && !Config.unlockedTabs.contains(Event.currentEvent.toString()))
		{
			Config.unlockedTabs.add(Event.currentEvent.toString());
			Config.currentPage = 0;
			for (int i=0; i<Event.EVENTS.length; i++) //iterate through events
			{ 
				if (Event.EVENTS[i].toString().equals(Event.currentEvent.toString()))
				{
					Config.syncToConfig(event.player);
					break;
				}
			}
			Config.syncToConfig(event.player);
			event.player.addChatMessage(new ChatComponentTranslation("Unlocked information about the "+Event.currentEvent.toString()+" event in the Event Book").setChatStyle(new ChatStyle().setItalic(true).setColor(EnumChatFormatting.DARK_GRAY)));
		}
		
		NBTTagCompound entityData = event.player.getEntityData();
		if(!entityData.getBoolean("MobEvents.firstJoin")) 
		{
			entityData.setBoolean("MobEvents.firstJoin", true);
			event.player.inventory.addItemStackToInventory(new ItemStack(ModItems.eventBook));
		}
	}
}
