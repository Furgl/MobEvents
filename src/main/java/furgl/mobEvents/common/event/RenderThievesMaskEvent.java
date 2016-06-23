package furgl.mobEvents.common.event;

import furgl.mobEvents.common.entity.EntityGuiPlayer;
import furgl.mobEvents.common.item.drops.ItemThievesMask;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RenderThievesMaskEvent 
{
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(final RenderLivingEvent.Pre event)
	{
		if (event.entity.getCurrentArmor(3) != null && event.entity.getCurrentArmor(3).getItem() instanceof ItemThievesMask && event.entity.isSneaking())
		{
			if (event.entity.isInvisible())
				event.entity.setInvisible(false);
			GlStateManager.enableNormalize();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(770, 771);
			GlStateManager.color(0.5F, 0.5F, 0.5F, 0.3F);
		}
	}

	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(RenderLivingEvent.Post event)
	{
		if (event.entity.getCurrentArmor(3) != null && event.entity.getCurrentArmor(3).getItem() instanceof ItemThievesMask && event.entity.isSneaking())
		{
			GlStateManager.disableBlend();
			GlStateManager.disableNormalize();
		}
	}

	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(RenderLivingEvent.Specials.Pre event)
	{
		if (event.entity instanceof EntityGuiPlayer)
			event.setCanceled(true);
		
		if (event.entity.getCurrentArmor(3) != null && event.entity.getCurrentArmor(3).getItem() instanceof ItemThievesMask && event.entity.isSneaking())
		{
			GlStateManager.enableNormalize();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(770, 771);
			GlStateManager.color(0.5F, 0.5F, 0.5F, 0.3F);
		}
	}

	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(RenderLivingEvent.Specials.Post event)
	{
		if (event.entity.getCurrentArmor(3) != null && event.entity.getCurrentArmor(3).getItem() instanceof ItemThievesMask && event.entity.isSneaking())
		{
			GlStateManager.disableBlend();
			GlStateManager.disableNormalize();
		}
	}

	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(LivingEvent.LivingJumpEvent event)
	{
		if (event.entityLiving instanceof EntityPlayer && event.entityLiving.getCurrentArmor(3) != null && event.entityLiving.getCurrentArmor(3).getItem() instanceof ItemThievesMask && event.entity.isSneaking())
			event.entityLiving.moveFlying(0f, 0.3f, 1f);
	}

	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(FOVUpdateEvent event)
	{
		if (event.entity.getCurrentArmor(3) != null && event.entity.getCurrentArmor(3).getItem() instanceof ItemThievesMask && event.entity.isSneaking())
			event.newfov = 1.0F;
	}
}
