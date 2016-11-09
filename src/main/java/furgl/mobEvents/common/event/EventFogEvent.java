package furgl.mobEvents.common.event;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.Events.ChaoticTurmoil;
import furgl.mobEvents.common.Events.Event;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.FogMode;
import net.minecraft.init.MobEffects;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.world.DimensionType;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EventFogEvent 
{
	public static boolean resetFogDensity;
	public static boolean resetFogColor;

	public static float densityToChangeTo = 1;
	public static float currentDensity = 1;

	public static float redToChangeTo = 1;
	public static float greenToChangeTo = 1;
	public static float blueToChangeTo = 1;
	public static float currentRed = 1;
	public static float currentGreen = 1;
	public static float currentBlue = 1;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(EntityViewRenderEvent.FogDensity event)
	{
		//if blindness effect, don't change
		if (Minecraft.getMinecraft().thePlayer.getActivePotionEffect(MobEffects.BLINDNESS) != null && Minecraft.getMinecraft().thePlayer.getActivePotionEffect(MobEffects.BLINDNESS).getDuration() > 10)
			return;
		//if in water/lava, don't change
		if (event.getState().getBlock().getBlockLayer() == BlockRenderLayer.TRANSLUCENT)
			return;
		float increment = 0.00001f;
		//becomes unused after fog set
		if (event.getDensity() == currentDensity)
			currentDensity = 1;
		//reset density after event
		if (resetFogDensity)
		{
			if (currentDensity == 0.005f)
			{
				resetFogDensity = false;
				currentDensity = 1;
				densityToChangeTo = 1;
			}
			else
				densityToChangeTo = 0.005f;
		}
		//set density during event
		else if (event.getEntity().worldObj.provider.getDimensionType() == DimensionType.OVERWORLD && MobEvents.proxy.getWorldData().currentEvent.getClass() != Event.class)
		{
			densityToChangeTo = 0.01f * (MobEvents.proxy.getWorldData().currentWave > 0 ? MobEvents.proxy.getWorldData().currentWave : 0.5f);
			if (currentDensity == 1)
				currentDensity = 0.0f;
		}
		//increment currentDensity
		if (densityToChangeTo != 1)
		{
			if (currentDensity >= densityToChangeTo-increment && currentDensity <= densityToChangeTo+increment)
			{
				currentDensity = densityToChangeTo;
				densityToChangeTo = 1;
			}
			else if (currentDensity > densityToChangeTo)
				currentDensity -= increment;
			else if (currentDensity < densityToChangeTo)
				currentDensity += increment;
		}
		//actually set density
		if (currentDensity != 1)
		{
			event.setCanceled(true);
			event.setDensity(currentDensity);
			GlStateManager.setFog(FogMode.EXP);
		}
	}

	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(EntityViewRenderEvent.FogColors event)
	{
		//if blindness effect, don't change
		if (Minecraft.getMinecraft().thePlayer.getActivePotionEffect(MobEffects.BLINDNESS) != null && Minecraft.getMinecraft().thePlayer.getActivePotionEffect(MobEffects.BLINDNESS).getDuration() > 10)
			return;
		float increment = 0.001f;

		if (MobEvents.proxy.getWorldData().currentEvent.getClass() == ChaoticTurmoil.class)
			increment = 0.003f;

		//reset density after event
		if (resetFogColor)
		{
			if (currentRed == event.getRed() && currentGreen == event.getGreen() && currentBlue == event.getBlue())
			{
				resetFogColor = false;
				currentRed = 1;
				currentGreen = 1;
				currentBlue = 1;
				redToChangeTo = 1;
				greenToChangeTo = 1;
				blueToChangeTo = 1;
			}
			else
			{
				redToChangeTo = event.getRed();
				greenToChangeTo = event.getGreen();
				blueToChangeTo = event.getBlue();
			}

		}
		//set color during event
		if (event.getEntity().worldObj.provider.getDimensionType() == DimensionType.OVERWORLD && MobEvents.proxy.getWorldData().currentEvent.getClass() != Event.class)
		{
			redToChangeTo = MobEvents.proxy.getWorldData().currentEvent.red;
			greenToChangeTo = MobEvents.proxy.getWorldData().currentEvent.green;
			blueToChangeTo = MobEvents.proxy.getWorldData().currentEvent.blue;
			if (currentRed == 1)
			{
				currentRed = event.getRed();
				currentGreen = event.getGreen();
				currentBlue = event.getBlue();
			}
		}
		//increment currentcolor
		if (redToChangeTo != 1)
		{
			if (currentRed >= redToChangeTo-increment && currentRed <= redToChangeTo+increment)
			{
				currentRed = redToChangeTo;
				redToChangeTo = 1;
			}
			else if (currentRed > redToChangeTo)
				currentRed -= increment;
			else if (currentRed < redToChangeTo)
				currentRed += increment;
		}
		if (greenToChangeTo != 1)
		{
			if (currentGreen >= greenToChangeTo-increment && currentGreen <= greenToChangeTo+increment)
			{
				currentGreen = greenToChangeTo;
				greenToChangeTo = 1;
			}
			else if (currentGreen > greenToChangeTo)
				currentGreen -= increment;
			else if (currentGreen < greenToChangeTo)
				currentGreen += increment;
		}
		if (blueToChangeTo != 1)
		{
			if (currentBlue >= blueToChangeTo-increment && currentBlue <= blueToChangeTo+increment)
			{
				currentBlue = blueToChangeTo;
				blueToChangeTo = 1;
			}
			else if (currentBlue > blueToChangeTo)
				currentBlue -= increment;
			else if (currentBlue < blueToChangeTo)
				currentBlue += increment;
		}
		//actually set color
		if (currentRed != 1)
		{
			event.setRed(currentRed);
			event.setGreen(currentGreen);
			event.setBlue(currentBlue);
		}
	}
}
