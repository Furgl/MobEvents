package furgl.mobEvents.client.gui.progressBar;

import furgl.mobEvents.common.Events.Event;
import furgl.mobEvents.common.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiEventProgress extends Gui
{
	private Minecraft mc;

	private static final ResourceLocation texture = new ResourceLocation("mobEvents", "textures/gui/event_progress.png");

	public GuiEventProgress(Minecraft mc) {
		super();
		this.mc = mc;
	}

	@SubscribeEvent(priority=EventPriority.NORMAL)
	public void onRenderExperienceBar(RenderGameOverlayEvent.Post event) 
	{
		if (event.type != ElementType.EXPERIENCE || Event.currentEvent.getClass() == Event.class || Config.eventProgressGuiLocation.equals(Config.eventProgressGuiLocations[Config.NONE])) 
			return;

        ScaledResolution sr = new ScaledResolution(this.mc);
        int xPos;
        int yPos;
        //if left
        if (Config.eventProgressGuiLocation.equals(Config.eventProgressGuiLocations[Config.TOP_LEFT]) || Config.eventProgressGuiLocation.equals(Config.eventProgressGuiLocations[Config.BOTTOM_LEFT]))
        	xPos = 2;
        else
        	xPos = sr.getScaledWidth() - 125;
        //if right
        if (Config.eventProgressGuiLocation.equals(Config.eventProgressGuiLocations[Config.TOP_LEFT]) || Config.eventProgressGuiLocation.equals(Config.eventProgressGuiLocations[Config.TOP_RIGHT]))
        	yPos = 2;
        else
        	yPos = sr.getScaledHeight() - 42;
		this.mc.getTextureManager().bindTexture(texture);

		// Add this block of code before you draw the section of your texture containing transparency
		GlStateManager.pushAttrib();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		// alpha test and blend needed due to vanilla or Forge rendering bug
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		// Here we draw the background bar which contains a transparent section; note the new size
		drawTexturedModalRect(xPos, yPos, 0, 0, 123, 40);
		// You can keep drawing without changing anything
		int manabarwidth = (int)(((double)Event.progress / (double)Event.progressNeededForBoss) * 104);
		drawTexturedModalRect(xPos + 9, yPos + 29, 0, 40, manabarwidth, 3);
		String s = Event.currentEvent.toString();
		yPos += 6;
		xPos += 60 - this.mc.fontRendererObj.getStringWidth(s)/2;
		this.mc.fontRendererObj.drawString(s, xPos + 1, yPos, 0);
		this.mc.fontRendererObj.drawString(s, xPos - 1, yPos, 0);
		this.mc.fontRendererObj.drawString(s, xPos, yPos + 1, 0);
		this.mc.fontRendererObj.drawString(s, xPos, yPos - 1, 0);
		this.mc.fontRendererObj.drawString(s, xPos, yPos, 1572663);
		yPos += 11;
		xPos += this.mc.fontRendererObj.getStringWidth(s)/2;
		s = Event.progress < Event.progressNeededForBoss ? "Wave " + Event.currentWave : "Boss Wave";
		xPos -= this.mc.fontRendererObj.getStringWidth(s)/2;
		this.mc.fontRendererObj.drawString(s, xPos + 1, yPos, 0);
		this.mc.fontRendererObj.drawString(s, xPos - 1, yPos, 0);
		this.mc.fontRendererObj.drawString(s, xPos, yPos + 1, 0);
		this.mc.fontRendererObj.drawString(s, xPos, yPos - 1, 0);
		this.mc.fontRendererObj.drawString(s, xPos, yPos, Event.currentWave == 4 ? 9961727 : 13036742);
		GlStateManager.popAttrib();
	}
}