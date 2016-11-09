package furgl.mobEvents.common.event;

import furgl.mobEvents.common.MobEvents;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DebugEvent {

	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(ClientChatReceivedEvent event)
	{
		if (event.getMessage().getUnformattedText().contains("debug")) 
			MobEvents.proxy.getWorldData().printDebug();
	}

	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(ServerChatEvent event)
	{
		if (event.getMessage().contains("debug")) 
			MobEvents.proxy.getWorldData().printDebug();
	}
}
