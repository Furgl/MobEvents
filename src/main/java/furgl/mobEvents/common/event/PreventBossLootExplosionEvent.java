package furgl.mobEvents.common.event;

import furgl.mobEvents.common.entity.ZombieApocalypse.EntityBossZombieSpawner;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PreventBossLootExplosionEvent 
{
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(ExplosionEvent.Start event)
	{
		if (!event.world.getEntitiesWithinAABB(EntityBossZombieSpawner.class, new AxisAlignedBB(new BlockPos(event.explosion.getPosition().subtract(5, 5, 5)), new BlockPos(event.explosion.getPosition()).add(5, 5, 5))).isEmpty())
			event.setCanceled(true);
	}
}
