package furgl.mobEvents.client.gui.book.buttons;

import furgl.mobEvents.client.gui.book.GuiEventBook;
import furgl.mobEvents.common.Events.Event;
import furgl.mobEvents.common.world.WorldData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonStartEvent extends GuiButton
{	
    private final ResourceLocation enchantTexture = new ResourceLocation("textures/misc/enchanted_item_glint.png");
	private GuiEventBook book;

	public GuiButtonStartEvent(int buttonId, int x, int y, int width, int height, String buttonText, GuiEventBook book)
	{
		super(buttonId, x, y, width, height, buttonText);
		this.book = book;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY)
	{
		if (this.visible)
		{
			if (Event.allEvents.get(book.currentTab-book.numNonEventTabs).getClass() == WorldData.get(Minecraft.getMinecraft().theWorld).currentEvent.getClass())
				this.displayString = "Stop Event";
			else
				this.displayString = "Start Event";
			
			FontRenderer fontrenderer = mc.fontRendererObj;
			mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
			GlStateManager.color(1F, 1F, 1F, 1.0F);

			GlStateManager.pushMatrix();
			this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
			int i = this.getHoverState(this.hovered);
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			GlStateManager.blendFunc(770, 771);

			this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + i * 20, this.width / 2, this.height);
			this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);

			this.mouseDragged(mc, mouseX, mouseY);
			int j = 14737632;

			if (packedFGColour != 0)
			{
				j = packedFGColour;
			}
			else
				if (!this.enabled)
				{
					j = 10526880;
				}
				else if (this.hovered)
				{
					j = 16777120;
				}
			this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 7) / 2, j);
			//creative effect
			GlStateManager.color(0.8F, 0.5F, 0.8F, 0.2F);
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
			this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 0, this.width, this.height);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scale(1.0F, 1.0F, 1.0F);
			float f1 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F / 1.0F;
			GlStateManager.translate(-f1, 0.0F, 0.0F);
			GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
			this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 0, this.width, this.height);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
			GlStateManager.blendFunc(770, 771);
			GlStateManager.enableLighting();
			GlStateManager.depthFunc(515);
			GlStateManager.depthMask(true);
			GlStateManager.popMatrix();
		}
	}
}
