package furgl.mobEvents.common.event;

import org.lwjgl.opengl.GL11;

import furgl.mobEvents.common.Events.Event;
import furgl.mobEvents.common.Events.ZombieApocalypse;
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
			event.density = 0;
			event.setCanceled(true);
			GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);
		}
		else if (event.entity.worldObj.provider.getDimensionId() == 0 && Event.currentEvent.getClass() == ZombieApocalypse.class)
		{
			event.setCanceled(true);
			event.density = 0.01f * Event.currentWave;
			GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);
		}
	}

	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void viewRenderEvent(EntityViewRenderEvent.FogColors event)
	{
		if (event.entity.worldObj.provider.getDimensionId() == 0 && Event.currentEvent.getClass() == ZombieApocalypse.class)
		{
			event.red = 0.23f;
			event.green = 0.43f;
			event.blue = 0.23f;
		}
	}
}
