package furgl.mobEvents.common.event;

import furgl.mobEvents.common.entity.boss.spawner.EntityBossSpawner;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PreventBossLootExplosionEvent 
{
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(ExplosionEvent.Start event)
	{
		if (!event.getWorld().getEntitiesWithinAABB(EntityBossSpawner.class, new AxisAlignedBB(
				new BlockPos(event.getExplosion().getPosition().subtract(5, 5, 5)), 
				new BlockPos(event.getExplosion().getPosition()).add(5, 5, 5))).isEmpty())
			event.setCanceled(true);
	}
}
