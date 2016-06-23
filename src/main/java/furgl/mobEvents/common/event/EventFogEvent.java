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
	public static boolean resetFogColor;

	public static float densityToChangeTo = -1;
	public static float currentDensity = -1;

	public static float redToChangeTo = -1;
	public static float greenToChangeTo = -1;
	public static float blueToChangeTo = -1;
	public static float currentRed = -1;
	public static float currentGreen = -1;
	public static float currentBlue = -1;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(EntityViewRenderEvent.FogDensity event)
	{
		float increment = 0.00001f;
		//becomes unused after fog set
		if (event.density == currentDensity)
			currentDensity = -1;
		//reset density after event
		if (resetFogDensity)
		{
			if (currentDensity == 0.005f)
			{
				resetFogDensity = false;
				currentDensity = -1;
				densityToChangeTo = -1;
			}
			else
				densityToChangeTo = 0.005f;
		}
		//set density during event
		else if (event.entity.worldObj.provider.getDimensionId() == 0 && Event.currentEvent.getClass() != Event.class)
		{
			densityToChangeTo = 0.01f * (Event.currentWave > 0 ? Event.currentWave : 0.5f);
			if (currentDensity == -1)
				currentDensity = 0.0f;
		}
		//increment currentDensity
		if (densityToChangeTo != -1)
		{
			if (currentDensity >= densityToChangeTo-increment && currentDensity <= densityToChangeTo+increment)
			{
				currentDensity = densityToChangeTo;
				densityToChangeTo = -1;
			}
			else if (currentDensity > densityToChangeTo)
				currentDensity -= increment;
			else if (currentDensity < densityToChangeTo)
				currentDensity += increment;
		}
		//actually set density
		if (currentDensity != -1)
		{
			event.setCanceled(true);
			event.density = currentDensity;
			GlStateManager.setFog(GL11.GL_EXP);
		}
	}

	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(EntityViewRenderEvent.FogColors event)
	{
		float increment = 0.001f;
		
		//reset density after event
		if (resetFogColor)
		{
			if (currentRed == event.red && currentGreen == event.green && currentBlue == event.blue)
			{
				resetFogColor = false;
				currentRed = -1;
				currentGreen = -1;
				currentBlue = -1;
				redToChangeTo = -1;
				greenToChangeTo = -1;
				blueToChangeTo = -1;
			}
			else
			{
				redToChangeTo = event.red;
				greenToChangeTo = event.green;
				blueToChangeTo = event.blue;
			}
			
		}
		//set color during event
		if (event.entity.worldObj.provider.getDimensionId() == 0 && Event.currentEvent.getClass() != Event.class)
		{
			redToChangeTo = Event.currentEvent.red;
			greenToChangeTo = Event.currentEvent.green;
			blueToChangeTo = Event.currentEvent.blue;
			if (currentRed == -1)
			{
				currentRed = event.red;
				currentGreen = event.green;
				currentBlue = event.blue;
			}
		}
		//increment currentcolor
		if (redToChangeTo != -1)
		{
			if (currentRed >= redToChangeTo-increment && currentRed <= redToChangeTo+increment)
			{
				currentRed = redToChangeTo;
				redToChangeTo = -1;
			}
			else if (currentRed > redToChangeTo)
				currentRed -= increment;
			else if (currentRed < redToChangeTo)
				currentRed += increment;
		}
		if (greenToChangeTo != -1)
		{
			if (currentGreen >= greenToChangeTo-increment && currentGreen <= greenToChangeTo+increment)
			{
				currentGreen = greenToChangeTo;
				greenToChangeTo = -1;
			}
			else if (currentGreen > greenToChangeTo)
				currentGreen -= increment;
			else if (currentGreen < greenToChangeTo)
				currentGreen += increment;
		}
		if (blueToChangeTo != -1)
		{
			if (currentBlue >= blueToChangeTo-increment && currentBlue <= blueToChangeTo+increment)
			{
				currentBlue = blueToChangeTo;
				blueToChangeTo = -1;
			}
			else if (currentBlue > blueToChangeTo)
				currentBlue -= increment;
			else if (currentBlue < blueToChangeTo)
				currentBlue += increment;
		}
		//actually set color
		if (currentRed != -1)
		{
			event.red = currentRed;
			event.green = currentGreen;
			event.blue = currentBlue;
		}
	}
}
