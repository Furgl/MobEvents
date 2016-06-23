package furgl.mobEvents.common.event;

import furgl.mobEvents.common.item.ModItems;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CancelFireOverlayEvent 
{
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(RenderBlockOverlayEvent event)
	{
		if (event.overlayType == RenderBlockOverlayEvent.OverlayType.FIRE && 
				event.player.getEquipmentInSlot(4) != null && 
				event.player.getEquipmentInSlot(4).getItem() == ModItems.summonersHelm
				&& event.player.worldObj.getBlockState(new BlockPos(event.player.posX, event.player.posY, event.player.posZ)).getBlock() != Blocks.fire
				&& event.player.worldObj.getBlockState(new BlockPos(event.player.posX, event.player.posY, event.player.posZ)).getBlock() != Blocks.lava)
			event.setCanceled(true);
	}
}
