package furgl.mobEvents.common.event;

import furgl.mobEvents.common.entity.EntityGuiPlayer;
import furgl.mobEvents.common.item.drops.ItemThievesMask;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RenderThievesMaskEvent 
{
	/**Renders Thief mobs transparent when sneaking*/
	@SubscribeEvent(receiveCanceled=true)
	public void onEvent(final RenderLivingEvent.Pre event) {
		if (event.getEntity().getItemStackFromSlot(EntityEquipmentSlot.HEAD) != null &&	event.getEntity().isSneaking() &&
				event.getEntity().getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof ItemThievesMask) {
			if (event.getEntity().isInvisible())
				event.getEntity().setInvisible(false);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(770, 771);
			GlStateManager.color(0.5F, 0.5F, 0.5F, 0.3F);
		}
	}

	/**Renders Thief mobs transparent when sneaking*/
	@SubscribeEvent(receiveCanceled=true)
	public void onEvent(RenderLivingEvent.Post event) {
		if (event.getEntity().getItemStackFromSlot(EntityEquipmentSlot.HEAD) != null &&	event.getEntity().isSneaking() &&
				event.getEntity().getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof ItemThievesMask)
			GlStateManager.disableBlend();
	}

	//Not sure if this is needed, but no harm in keeping it
	@SubscribeEvent(receiveCanceled=true)
	public void onEvent(RenderLivingEvent.Specials.Pre event) {
		if (event.getEntity() instanceof EntityGuiPlayer)
			event.setCanceled(true);
		
		if (event.getEntity().getItemStackFromSlot(EntityEquipmentSlot.HEAD) != null &&	event.getEntity().isSneaking() &&
				event.getEntity().getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof ItemThievesMask) {
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(770, 771);
			GlStateManager.color(0.5F, 0.5F, 0.5F, 0.3F);
		}
	}

	//Not sure if this is needed, but no harm in keeping it
	@SubscribeEvent(receiveCanceled=true)
	public void onEvent(RenderLivingEvent.Specials.Post event) {
		if (event.getEntity().getItemStackFromSlot(EntityEquipmentSlot.HEAD) != null &&	event.getEntity().isSneaking() &&
				event.getEntity().getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof ItemThievesMask) 
			GlStateManager.disableBlend();
	}

	@SubscribeEvent(receiveCanceled=true)
	public void onEvent(LivingEvent.LivingJumpEvent event) {
		if (event.getEntityLiving() instanceof EntityPlayer && event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.HEAD) != null && event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof ItemThievesMask && event.getEntity().isSneaking())
			event.getEntityLiving().moveRelative(0f, event.getEntityLiving().moveForward*1.3f, 1f);
	}

	@SubscribeEvent(receiveCanceled=true)
	public void onEvent(FOVUpdateEvent event) {
		if (event.getEntity().getItemStackFromSlot(EntityEquipmentSlot.HEAD) != null &&	event.getEntity().isSneaking() &&
				event.getEntity().getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof ItemThievesMask) 
			event.setNewfov(1.0F);
	}
}