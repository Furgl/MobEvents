package furgl.mobEvents.common.event;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.Events.Event;
import furgl.mobEvents.common.config.Config;
import furgl.mobEvents.common.item.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class PlayerJoinedEvent 
{
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(PlayerLoggedInEvent event) //only server side
	{
		//check if event should be unlocked
		int index = MobEvents.proxy.getWorldData().getPlayerIndex(event.player.getDisplayNameString());
		if (MobEvents.proxy.getWorldData().currentEvent.getClass() != Event.class && !MobEvents.proxy.getWorldData().unlockedTabs.get(index).contains(MobEvents.proxy.getWorldData().currentEvent.toString()))
		{
			MobEvents.proxy.getWorldData().unlockedTabs.get(index).add(MobEvents.proxy.getWorldData().currentEvent.toString());
			Event.displayUnlockMessage(event.player, "Unlocked information about the "+MobEvents.proxy.getWorldData().currentEvent.toString()+" event in the Event Book");
		}

		NBTTagCompound entityData = event.player.getEntityData();
		if(!entityData.getBoolean("MobEvents.firstJoin") && Config.giveBookOnFirstJoin) 
		{
			entityData.setBoolean("MobEvents.firstJoin", true);
			event.player.inventory.addItemStackToInventory(new ItemStack(ModItems.eventBook));
		}

		/*if (MobEvents.DEBUG) {
			System.out.println("First join debug:");
			MobEvents.proxy.getWorldData().printDebug();
		}*/
		//sync server to client
		MobEvents.proxy.getWorldData().markDirty();
	}
}
