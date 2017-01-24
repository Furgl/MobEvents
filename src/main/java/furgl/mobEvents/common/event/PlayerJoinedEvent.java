package furgl.mobEvents.common.event;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.Events.Event;
import furgl.mobEvents.common.config.Config;
import furgl.mobEvents.common.item.ModItems;
import furgl.mobEvents.common.world.WorldData;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class PlayerJoinedEvent {
	@SubscribeEvent(receiveCanceled=true)
	public void onEvent(PlayerLoggedInEvent event) //only server side
	{
		//check if event should be unlocked
		WorldData data = WorldData.get(event.player.worldObj);
		int index = data.getPlayerIndex(event.player.getDisplayNameString());
		if (data.currentEvent.getClass() != Event.class && 
				!data.unlockedTabs.get(index).contains(data.currentEvent.toString())) {
			data.unlockedTabs.get(index).add(data.currentEvent.toString());
			Event.displayUnlockMessage(event.player, "Unlocked information about the "+data.currentEvent.toString()+" event in the Event Book");
		}

		NBTTagCompound entityData = event.player.getEntityData();
		if(!entityData.getBoolean(MobEvents.MODID+":firstJoin") && Config.giveBookOnFirstJoin) {
			entityData.setBoolean(MobEvents.MODID+":firstJoin", true);
			event.player.inventory.addItemStackToInventory(new ItemStack(ModItems.eventBook));
		}

		//sync server to client
		data.markDirty();
	}
}