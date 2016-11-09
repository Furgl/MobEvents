package furgl.mobEvents.common.event;

import furgl.mobEvents.common.item.ModItems;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CancelFireOverlayEvent 
{
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(RenderBlockOverlayEvent event)
	{
		if (event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.FIRE && 
				event.getPlayer().getItemStackFromSlot(EntityEquipmentSlot.HEAD) != null && 
				event.getPlayer().getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == ModItems.summonersHelm
				&& event.getPlayer().worldObj.getBlockState(new BlockPos(event.getPlayer().posX, event.getPlayer().posY, event.getPlayer().posZ)).getBlock() != Blocks.FIRE
				&& event.getPlayer().worldObj.getBlockState(new BlockPos(event.getPlayer().posX, event.getPlayer().posY, event.getPlayer().posZ)).getBlock() != Blocks.LAVA)
			event.setCanceled(true);
	}
}
