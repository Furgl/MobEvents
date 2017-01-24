package furgl.mobEvents.common.event;

import java.util.ArrayList;

import furgl.mobEvents.common.Events.ChaoticTurmoil;
import furgl.mobEvents.common.Events.Event;
import furgl.mobEvents.common.Events.SkeletalUprising;
import furgl.mobEvents.common.Events.ZombieApocalypse;
import furgl.mobEvents.common.world.WorldData;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class EventSetupEvent {
	/**0 = not set, 1 = day, 2 = night*/
	private int lastTickTime;
	public static int timeTillWave1;
	private int ticks;

	@SubscribeEvent(receiveCanceled=true)
	public void onEvent(LivingDeathEvent event)	{
		if (event.getEntity() instanceof EntityPlayer && !(event.getEntity() instanceof FakePlayer) && 
				!Event.playerDeaths.contains(((EntityPlayer)event.getEntity()).getDisplayNameString()))
			Event.playerDeaths.add(((EntityPlayer)event.getEntity()).getDisplayNameString());
	}

	@SubscribeEvent(receiveCanceled=true)
	public void onEvent(WorldEvent.Load event) { //server first, then client
		if (event.getWorld().provider.getDimensionType() == DimensionType.OVERWORLD) {
			//initialize events
			if (Event.EVENT == null) { //SP: only runs server and shares, MP: runs both
				Event.EVENT = new Event(event.getWorld());
				Event.allEvents = new ArrayList<Event>();
				Event.ZOMBIE_APOCALYPSE = new ZombieApocalypse(event.getWorld());
				Event.allEvents.add(Event.ZOMBIE_APOCALYPSE);
				Event.SKELETAL_UPRISING = new SkeletalUprising(event.getWorld());
				Event.allEvents.add(Event.SKELETAL_UPRISING);
				Event.CHAOTIC_TURMOIL = new ChaoticTurmoil(event.getWorld());
				Event.allEvents.add(Event.CHAOTIC_TURMOIL);
			}
			/*//initialize proxy world
			if (!event.getWorld().isRemote)  //SP: only runs server and shares, MP: runs both
				MobEvents.proxy.world = event.getWorld();
			else if (world == null) 
				MobEvents.proxy.world = event.getWorld();*/
		}
	}

	@SubscribeEvent(receiveCanceled=true)
	public void onEvent(TickEvent.ClientTickEvent event) {
		if (Minecraft.getMinecraft().theWorld != null && event.phase == Phase.START &&
				Minecraft.getMinecraft().theWorld.provider.getDimensionType() == DimensionType.OVERWORLD) {
			WorldData.get(Minecraft.getMinecraft().theWorld).currentEvent.onUpdate(); //client onUpdate
			//Accelerate time during chaotic
			if (!Minecraft.getMinecraft().isGamePaused() && 
					WorldData.get(Minecraft.getMinecraft().theWorld).currentEvent.getClass() == Event.CHAOTIC_TURMOIL.getClass())
				Minecraft.getMinecraft().theWorld.setWorldTime(Minecraft.getMinecraft().theWorld.getWorldTime()+(Minecraft.getMinecraft().theWorld.getWorldTime() % 24000 < 12575 ? 30 : 10)); //isDayTime() doesn't work on client
			if (Minecraft.getMinecraft().theWorld.getGameRules().getBoolean("doDaylightCycle")) {//doesn't work on client
				//Short event 
				if (!Minecraft.getMinecraft().isGamePaused() && 
						WorldData.get(Minecraft.getMinecraft().theWorld).currentEvent.getClass() != Event.class && 
						WorldData.get(Minecraft.getMinecraft().theWorld).eventLength == 0 && 
						Minecraft.getMinecraft().theWorld.getTotalWorldTime() % 3 == 0)
					Minecraft.getMinecraft().theWorld.setWorldTime(Minecraft.getMinecraft().theWorld.getWorldTime() + 1);
				//Long event
				else if (!Minecraft.getMinecraft().isGamePaused() && 
						WorldData.get(Minecraft.getMinecraft().theWorld).currentEvent.getClass() != Event.class && 
						WorldData.get(Minecraft.getMinecraft().theWorld).eventLength == 2 && 
						Minecraft.getMinecraft().theWorld.getTotalWorldTime() % 3 == 0)
					Minecraft.getMinecraft().theWorld.setWorldTime(Minecraft.getMinecraft().theWorld.getWorldTime() - 1);
			}
			//change chaotic turmoil color/obfuscated name
			if (ticks % 100 == 0)
				Event.CHAOTIC_TURMOIL.obfuscate();
			else if ((ticks-10) % 100 == 0)
				Event.CHAOTIC_TURMOIL.recolor();
			else if ((ticks-20) % 100 == 0)
				Event.CHAOTIC_TURMOIL.deobfuscate();
			ticks++;
		}
	}

	@SubscribeEvent(receiveCanceled=true)
	public void onEvent(TickEvent.WorldTickEvent event) {//only runs on server
		if (event.world.provider.getDimensionType() == DimensionType.OVERWORLD && event.phase == Phase.START) {		
			WorldData.get(event.world).currentEvent.onUpdate(); //server onUpdate

			if (WorldData.get(event.world).currentEvent.getClass() != Event.class) {
				if (WorldData.get(event.world).currentWave == 0 && EventSetupEvent.timeTillWave1 < 0)
					EventSetupEvent.timeTillWave1 = Event.TIME_TILL_WAVE_1;
				if (--EventSetupEvent.timeTillWave1 == 0)
					WorldData.get(event.world).currentEvent.startWave(1);
			}
			if (event.world.isDaytime() && this.lastTickTime == 2) {//just turned day (time = 23460)
				this.lastTickTime = 1;
				//cancel current event if doesn't match time
				if (WorldData.get(event.world).currentEvent.getClass() != Event.class) {
					boolean found = false;
					for (Event event2 : Event.allEvents)
						if (event2.occurs == Event.Occurs.DAY && WorldData.get(event.world).currentEvent == event2)
							found = true;
					if (!found && WorldData.get(event.world).currentEvent.getClass() != ChaoticTurmoil.class)
						WorldData.get(event.world).currentEvent.stopEvent();
				}
				else if (WorldData.get(event.world).currentEvent.getClass() == Event.class && 
						Event.rand.nextInt(100) < WorldData.get(event.world).eventChance) {
					ArrayList<Event> events = new ArrayList<Event>();
					for (Event event2 : Event.allEvents)
						if (event2.occurs == Event.Occurs.DAY)
							events.add(event2);
					if (!events.isEmpty())
						events.get(Event.rand.nextInt(events.size())).startEvent();
				}
			}
			else if (!event.world.isDaytime() && this.lastTickTime == 1) {//just turned night (time = 12542)
				this.lastTickTime = 2;
				//cancel current event if doesn't match time
				if (WorldData.get(event.world).currentEvent.getClass() != Event.class) {
					boolean found = false;
					for (Event event2 : Event.allEvents)
						if (event2.occurs == Event.Occurs.NIGHT && WorldData.get(event.world).currentEvent == event2)
							found = true;
					if (!found && WorldData.get(event.world).currentEvent.getClass() != ChaoticTurmoil.class)
						WorldData.get(event.world).currentEvent.stopEvent();
				}
				else if (WorldData.get(event.world).currentEvent.getClass() == Event.class && 
						Event.rand.nextInt(100) < WorldData.get(event.world).eventChance) {
					ArrayList<Event> events = new ArrayList<Event>();
					for (Event event2 : Event.allEvents)
						if (event2.occurs == Event.Occurs.NIGHT)
							events.add(event2);
					if (!events.isEmpty())
						events.get(Event.rand.nextInt(events.size())).startEvent();
				}
			}
			else if (this.lastTickTime == 0)
				this.lastTickTime = event.world.isDaytime() ? 1 : 2;
		}
	}
}