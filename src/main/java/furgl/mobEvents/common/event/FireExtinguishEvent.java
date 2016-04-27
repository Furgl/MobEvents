package furgl.mobEvents.common.event;

import furgl.mobEvents.common.block.ModBlocks;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FireExtinguishEvent 
{
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(PlayerInteractEvent event)
	{
		if (!event.world.isRemote && event.world.getBlockState(event.pos.up()) != null && event.world.getBlockState(event.pos.up()).getBlock() == ModBlocks.disappearingFire)
		{
			event.world.playSoundAtEntity(event.entityPlayer, "random.fizz", 1, 2);
			event.world.setBlockToAir(event.pos.up());
		}
	}
}
