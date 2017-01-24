package furgl.mobEvents.client.gui.progressBar;

import furgl.mobEvents.common.Events.ChaoticTurmoil;
import furgl.mobEvents.common.Events.Event;
import furgl.mobEvents.common.config.Config;
import furgl.mobEvents.common.event.EventFogEvent;
import furgl.mobEvents.common.world.WorldData;
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
	private final ResourceLocation enchantTexture = new ResourceLocation("textures/misc/enchanted_item_glint.png");

	private static final ResourceLocation texture = new ResourceLocation("mobEvents", "textures/gui/event_progress.png");

	public GuiEventProgress(Minecraft mc) {
		super();
		this.mc = mc;
	}

	@SubscribeEvent(priority=EventPriority.NORMAL)
	public void onRenderExperienceBar(RenderGameOverlayEvent.Post event) 
	{
		if (event.getType() != ElementType.EXPERIENCE || WorldData.get(Minecraft.getMinecraft().theWorld).currentEvent.getClass() == Event.class || Config.eventProgressGuiLocation.equals(Config.eventProgressGuiLocations[Config.NONE])) 
			return;

		ScaledResolution sr = new ScaledResolution(this.mc);
		int xPos;
		int yPos;
		//if left
		if (Config.eventProgressGuiLocation.equalsIgnoreCase(Config.eventProgressGuiLocations[Config.TOP_LEFT]) || Config.eventProgressGuiLocation.equalsIgnoreCase(Config.eventProgressGuiLocations[Config.BOTTOM_LEFT]))
			xPos = 2;
		else//if right
			xPos = sr.getScaledWidth() - 125;
		//if top
		if (Config.eventProgressGuiLocation.equalsIgnoreCase(Config.eventProgressGuiLocations[Config.TOP_LEFT]) || Config.eventProgressGuiLocation.equalsIgnoreCase(Config.eventProgressGuiLocations[Config.TOP_RIGHT]))
			yPos = 2;
		else//if bottom
			yPos = sr.getScaledHeight() - 42;
		this.mc.getTextureManager().bindTexture(texture);

		GlStateManager.pushAttrib();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		// alpha test and blend needed due to vanilla or Forge rendering bug
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		drawTexturedModalRect(xPos, yPos, 0, 0, 123, 40);
		// You can keep drawing without changing anything
		int progressBarWidth = (int)(((double)WorldData.get(Minecraft.getMinecraft().theWorld).progress / (double)WorldData.get(Minecraft.getMinecraft().theWorld).progressNeededForBoss) * 104);
		drawTexturedModalRect(xPos + 9, yPos + 29, 0, 40, progressBarWidth, 3);
		String s = WorldData.get(Minecraft.getMinecraft().theWorld).currentEvent.getClass() == Event.CHAOTIC_TURMOIL.getClass() ? ((ChaoticTurmoil)WorldData.get(Minecraft.getMinecraft().theWorld).currentEvent).changingName : WorldData.get(Minecraft.getMinecraft().theWorld).currentEvent.toString();
		yPos += 6;
		xPos += 60 - this.mc.fontRendererObj.getStringWidth(s)/2;
		this.mc.fontRendererObj.drawString(s, xPos + 1, yPos, 0);
		this.mc.fontRendererObj.drawString(s, xPos - 1, yPos, 0);
		this.mc.fontRendererObj.drawString(s, xPos, yPos + 1, 0);
		this.mc.fontRendererObj.drawString(s, xPos, yPos - 1, 0);
		this.mc.fontRendererObj.drawString(s, xPos, yPos, WorldData.get(Minecraft.getMinecraft().theWorld).currentEvent.color);
		yPos += 11;
		xPos += this.mc.fontRendererObj.getStringWidth(s)/2;
		if (WorldData.get(Minecraft.getMinecraft().theWorld).currentWave == 0)
			s = "";
		else
			s = WorldData.get(Minecraft.getMinecraft().theWorld).currentWave < 4 ? "Wave " + WorldData.get(Minecraft.getMinecraft().theWorld).currentWave : "Boss Wave";
			xPos -= this.mc.fontRendererObj.getStringWidth(s)/2;
			this.mc.fontRendererObj.drawString(s, xPos + 1, yPos, 0);
			this.mc.fontRendererObj.drawString(s, xPos - 1, yPos, 0);
			this.mc.fontRendererObj.drawString(s, xPos, yPos + 1, 0);
			this.mc.fontRendererObj.drawString(s, xPos, yPos - 1, 0);
			this.mc.fontRendererObj.drawString(s, xPos, yPos, WorldData.get(Minecraft.getMinecraft().theWorld).currentWave == 4 ? 9961727 : 13036742);

			//if left
			if (Config.eventProgressGuiLocation.equalsIgnoreCase(Config.eventProgressGuiLocations[Config.TOP_LEFT]) || Config.eventProgressGuiLocation.equalsIgnoreCase(Config.eventProgressGuiLocations[Config.BOTTOM_LEFT]))
				xPos = 2;
			else//if right
				xPos = sr.getScaledWidth() - 125;
			//if top
			if (Config.eventProgressGuiLocation.equalsIgnoreCase(Config.eventProgressGuiLocations[Config.TOP_LEFT]) || Config.eventProgressGuiLocation.equalsIgnoreCase(Config.eventProgressGuiLocations[Config.TOP_RIGHT]))
				yPos = 2;
			else//if bottom
				yPos = sr.getScaledHeight() - 42;

			//creative effect
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			GlStateManager.blendFunc(770, 771);

			GlStateManager.color(EventFogEvent.currentColors[0], EventFogEvent.currentColors[1], EventFogEvent.currentColors[2]);				
			GlStateManager.depthMask(false);
			GlStateManager.depthFunc(514);
			GlStateManager.disableLighting();
			GlStateManager.blendFunc(768, 1);
			mc.getTextureManager().bindTexture(enchantTexture);
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.scale(1.0F, 1.0F, 1.0F);
			float f = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F / 1.0F;
			GlStateManager.translate(f, 0.0F, 0.0F);
			GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
			drawTexturedModalRect(xPos, yPos, 0, 0, 123, 40);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scale(1.0F, 1.0F, 1.0F);
			float f1 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F / 1.0F;
			GlStateManager.translate(-f1, 0.0F, 0.0F);
			GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
			drawTexturedModalRect(xPos, yPos, 0, 0, 123, 40);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
			GlStateManager.blendFunc(770, 771);
			GlStateManager.enableLighting();
			GlStateManager.depthFunc(515);
			GlStateManager.depthMask(true);
			GlStateManager.popMatrix();
			GlStateManager.popAttrib();
	}
}