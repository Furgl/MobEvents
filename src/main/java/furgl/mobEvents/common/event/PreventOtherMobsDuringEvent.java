package furgl.mobEvents.common.event;

import furgl.mobEvents.common.Events.Event;
import furgl.mobEvents.common.entity.IEventMob;
import furgl.mobEvents.common.entity.boss.IEventBoss;
import furgl.mobEvents.common.entity.boss.spawner.EntityBossSpawner;
import furgl.mobEvents.common.world.WorldData;
import net.minecraft.entity.EnumCreatureType;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PreventOtherMobsDuringEvent 
{
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(LivingSpawnEvent.CheckSpawn event)
	{
		if (!event.getWorld().isRemote && WorldData.get(event.getWorld()).currentEvent.getClass() != Event.class && 
				!(event.getEntity() instanceof EntityBossSpawner) && !(event.getEntity() instanceof IEventBoss) && 
				!(event.getEntity() instanceof IEventMob) && event.getEntity().isCreatureType(EnumCreatureType.MONSTER, false)) {
			event.setResult(Result.DENY);
		}
	}
}
