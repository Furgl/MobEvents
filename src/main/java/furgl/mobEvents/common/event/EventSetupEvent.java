package furgl.mobEvents.common.event;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.Events.Event;
import furgl.mobEvents.common.config.Config;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class EventSetupEvent 
{
	/**0 = not set, 1 = day, 2 = night*/
	private int lastTickTime;
	public static int timeTillWave1;

	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(LivingDeathEvent event)
	{
		if (event.entity instanceof EntityPlayer && !(event.entity instanceof FakePlayer) && !Event.playerDeaths.contains(((EntityPlayer)event.entity).getDisplayNameString()))
			Event.playerDeaths.add(((EntityPlayer)event.entity).getDisplayNameString());
	}

	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(final ConfigChangedEvent.OnConfigChangedEvent event) 
	{
		if (event.modID.equals(MobEvents.MODID)) 
			Config.syncFromConfig(null);
	}

	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(WorldEvent.Load event)
	{
		if (event.world.provider.getDimensionId() == 0 && !event.world.isRemote)
		{
			Event.world = event.world;
			//Event.currentEvent.stopEvent();
		}
	}

	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(TickEvent.WorldTickEvent event)
	{
		if (event.world.provider.getDimensionId() == 0 && event.phase == Phase.START)
		{
			if (Event.currentEvent.getClass() != Event.class)
			{
				if (Event.currentWave == 0 && EventSetupEvent.timeTillWave1 < 0)
					EventSetupEvent.timeTillWave1 = 200;
				if (--EventSetupEvent.timeTillWave1 == 0)
					Event.currentEvent.startWave(1);
				Event.currentEvent.onUpdate();
			}
			if (Event.world.isDaytime() && this.lastTickTime == 2) //just turned day
			{
				this.lastTickTime = 1;
				if (Event.currentEvent.getClass() == Event.class && Event.rand.nextInt(100) < Config.eventChance && Event.DAYEVENTS.length > 0)
					Event.DAYEVENTS[Event.rand.nextInt(Event.DAYEVENTS.length)].startEvent();
				else if (Event.currentEvent.getClass() != Event.class)
					Event.currentEvent.stopEvent();
			}
			else if (!Event.world.isDaytime() && this.lastTickTime == 1) //just turned night
			{
				this.lastTickTime = 2;
				if (Event.currentEvent.getClass() == Event.class && Event.rand.nextInt(100) < Config.eventChance && Event.NIGHTEVENTS.length > 0)
					Event.NIGHTEVENTS[Event.rand.nextInt(Event.NIGHTEVENTS.length)].startEvent();
				else if (Event.currentEvent.getClass() != Event.class)
					Event.currentEvent.stopEvent();
			}
			else if (this.lastTickTime == 0)
				this.lastTickTime = Event.world.isDaytime() ? 1 : 2;
		}
	}
}
