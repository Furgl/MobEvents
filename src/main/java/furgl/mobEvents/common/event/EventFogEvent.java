package furgl.mobEvents.common.event;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.Events.ChaoticTurmoil;
import furgl.mobEvents.common.Events.Event;
import furgl.mobEvents.common.item.drops.ItemButchersCleaver0;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.FogMode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.world.DimensionType;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventFogEvent 
{
	public static float currentDensity = -1;
	public static float[] currentColors = new float[] {-1, -1, -1};

	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(EntityViewRenderEvent.FogDensity event)
	{
		//only affect overworld
		if (event.getEntity().worldObj.provider.getDimensionType() != DimensionType.OVERWORLD)
			return;
		//if blindness effect, don't change
		if (Minecraft.getMinecraft().thePlayer.getActivePotionEffect(MobEffects.BLINDNESS) != null && Minecraft.getMinecraft().thePlayer.getActivePotionEffect(MobEffects.BLINDNESS).getDuration() > 10)
			return;
		//if in water/lava, don't change
		if (event.getState().getBlock().getBlockLayer() == BlockRenderLayer.TRANSLUCENT)
			return;
		//amount to increment color per render tick
		float increment = 0.00001f;
		//initialize currentDensity and densityToChangeTo
		if (currentDensity == -1)
			currentDensity = event.getDensity()/20;
		float densityToChangeTo = event.getDensity()/20;
		//apply event density
		if (MobEvents.proxy.getWorldData().currentEvent.getClass() != Event.class)
			densityToChangeTo = 0.01f * (MobEvents.proxy.getWorldData().currentWave > 0 ? MobEvents.proxy.getWorldData().currentWave : 0.5f);
		//apply cleaver density
		boolean hasCleaver = event.getEntity() instanceof EntityPlayer && 
				((EntityPlayer)event.getEntity()).getHeldItemMainhand() != null && 
				((EntityPlayer)event.getEntity()).getHeldItemMainhand().getItem() instanceof ItemButchersCleaver0;
		if (hasCleaver) {
			ItemStack stack = ((EntityPlayer)event.getEntity()).getHeldItemMainhand();
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			int blood = stack.getTagCompound().hasKey("blood") ? stack.getTagCompound().getInteger("blood") : 0;
			densityToChangeTo += 0.02f * blood/ItemButchersCleaver0.MAX_BLOOD;
		}
		//increment currentDensity
		if (currentDensity >= densityToChangeTo-increment && currentDensity <= densityToChangeTo+increment)
			currentDensity = densityToChangeTo;
		else if (currentDensity > densityToChangeTo)
			currentDensity -= increment;
		else if (currentDensity < densityToChangeTo)
			currentDensity += increment;
		//set density
		if (currentDensity != event.getDensity()/20) {
			event.setCanceled(true);
			event.setDensity(currentDensity);
			GlStateManager.setFog(FogMode.EXP);
		}
	}

	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(EntityViewRenderEvent.FogColors event)
	{
		//only affect overworld
		if (event.getEntity().worldObj.provider.getDimensionType() != DimensionType.OVERWORLD)
			return;
		//don't copy colors before they're set properly (when first starting mc)
		if (event.getRed() == 0 && event.getGreen() == 0 && event.getBlue() == 0)
			return;
		//if blindness effect, don't change
		if (Minecraft.getMinecraft().thePlayer.getActivePotionEffect(MobEffects.BLINDNESS) != null && Minecraft.getMinecraft().thePlayer.getActivePotionEffect(MobEffects.BLINDNESS).getDuration() > 10)
			return;
		//amount to increment color per render tick
		float increment = 0.001f;
		//initialize currentColors and colorsToChangeTo
		if (currentColors[0] == -1 || currentColors[1] == -1 || currentColors[2] == -1)
			currentColors = new float[] {event.getRed(), event.getGreen(), event.getBlue()};
		float[] colorsToChangeTo = new float[] {event.getRed(), event.getGreen(), event.getBlue()};
		//apply event colors
		if (MobEvents.proxy.getWorldData().currentEvent.getClass() != Event.class)
		{
			Event currentEvent = MobEvents.proxy.getWorldData().currentEvent;
			colorsToChangeTo[0] += currentEvent.red;
			colorsToChangeTo[1] += currentEvent.green;
			colorsToChangeTo[2] += currentEvent.blue;
			if (currentEvent.getClass() == ChaoticTurmoil.class)
				increment = Math.max(increment, 0.003f);
		}
		//apply cleaver colors
		boolean hasCleaver = event.getEntity() instanceof EntityPlayer && 
				((EntityPlayer)event.getEntity()).getHeldItemMainhand() != null && 
				((EntityPlayer)event.getEntity()).getHeldItemMainhand().getItem() instanceof ItemButchersCleaver0;
		if (hasCleaver) {
			ItemStack stack = ((EntityPlayer)event.getEntity()).getHeldItemMainhand();
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			int blood = stack.getTagCompound().hasKey("blood") ? stack.getTagCompound().getInteger("blood") : 0;
			float[] fullColor = new float[] {0.5f, 0.1f, 0.1f};//color for 100% blood
			float percentage = (float) (blood/ItemButchersCleaver0.MAX_BLOOD);//weighted by amount of current blood
			float roundedPercentage = ((int)percentage/10)*10+0.5f;
			double oscillation = Math.sin(event.getEntity().ticksExisted/(1.8D+3D*(1D-roundedPercentage)))/20D*percentage;
			for (int i=0; i<colorsToChangeTo.length; i++) {
				colorsToChangeTo[i] += (fullColor[i] - colorsToChangeTo[i]) * percentage; //average between full and original by percentage
				colorsToChangeTo[i] += oscillation;
				increment = Math.max(increment, 0.004f);
			}
			if (event.getEntity().ticksExisted % (11+Math.floor(10D*(1D-percentage))) == 0 && percentage > 0 && !Minecraft.getMinecraft().isGamePaused()) 
				event.getEntity().playSound(SoundEvents.BLOCK_ANVIL_FALL, 0.6f*percentage, 0f);
		}
		//increment currentcolor
		for (int i=0; i<currentColors.length; i++) 
			if (currentColors[i] >= colorsToChangeTo[i]-increment && currentColors[i] <= colorsToChangeTo[i]+increment)
				currentColors[i] = colorsToChangeTo[i];
			else if (currentColors[i] > colorsToChangeTo[i])
				currentColors[i] -= increment;
			else if (currentColors[i] < colorsToChangeTo[i])
				currentColors[i] += increment;

		//set colors
		if (event.getRed() != currentColors[0] || event.getGreen() != currentColors[1] || event.getBlue() != currentColors[2]) {
			//System.out.println(currentColors[0]+", "+currentColors[1]+", "+currentColors[2]);
			event.setRed(currentColors[0]);
			event.setGreen(currentColors[1]);
			event.setBlue(currentColors[2]);
		}
	}
}