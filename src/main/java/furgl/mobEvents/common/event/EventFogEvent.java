package furgl.mobEvents.common.event;

import org.lwjgl.opengl.GL11;

import furgl.mobEvents.common.Events.Event;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EventFogEvent 
{
	public static boolean resetFogDensity;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void viewRenderEvent(EntityViewRenderEvent.FogDensity event)
	{
		if (resetFogDensity)
		{
			resetFogDensity = false;
			event.setCanceled(true);
			event.density = 0.000f;
			GlStateManager.setFog(GL11.GL_EXP);
		}
		else if (event.entity.worldObj.provider.getDimensionId() == 0 && Event.currentEvent.getClass() != Event.class)
		{
			event.setCanceled(true);
			event.density = 0.01f * (Event.currentWave > 0 ? Event.currentWave : 0.5f);
			GlStateManager.setFog(GL11.GL_EXP);
		}
	}

	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void viewRenderEvent(EntityViewRenderEvent.FogColors event)
	{
		if (event.entity.worldObj.provider.getDimensionId() == 0 && Event.currentEvent.getClass() != Event.class)
		{
			event.red = Event.currentEvent.red;
			event.green = Event.currentEvent.green;
			event.blue = Event.currentEvent.blue;
		}
	}
}
