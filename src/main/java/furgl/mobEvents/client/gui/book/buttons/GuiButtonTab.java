package furgl.mobEvents.client.gui.book.buttons;

import furgl.mobEvents.client.gui.book.GuiEventBook;
import furgl.mobEvents.common.Events.Event;
import furgl.mobEvents.common.world.WorldData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonTab extends GuiButton
{	
	private final ResourceLocation enchantTexture = new ResourceLocation("textures/misc/enchanted_item_glint.png");
	private GuiEventBook book;
	public int startXPos;
	public int startYPos;

	public GuiButtonTab(int buttonId, int x, int y, int width, int height, String buttonText, GuiEventBook book)
	{
		super(buttonId, x, y, width, height, buttonText);
		this.book = book;
		this.startXPos = x;
		this.startYPos = y;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY)
	{
		if (visible)
		{
			if (this.displayString.contains(Event.CHAOTIC_TURMOIL.toString()))
				this.displayString = Event.CHAOTIC_TURMOIL.changingName;
			
			this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height && book.currentTab != this.id;
			mc.getTextureManager().bindTexture(book.bookPageTexture);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableLighting();
			GlStateManager.enableAlpha();
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			GlStateManager.blendFunc(770, 771);
			drawTexturedModalRect(this.xPosition, hovered ? this.yPosition - 1 : this.yPosition, 0, 90, this.width, this.height);

			int x = (this.width-mc.fontRendererObj.getStringWidth(this.displayString))/2+this.xPosition+2;
			int y = this.yPosition+(hovered ? 3 : 4);
			int borderColor = 0xb3b3b3;	
			int mainColor = 0xe6e6e6; //faded color
			int index = WorldData.get(Minecraft.getMinecraft().theWorld).getPlayerIndex(mc.thePlayer.getDisplayNameString());
			GlStateManager.pushMatrix();
			if (id < book.numNonEventTabs) //introduction and items
			{
				mainColor = 0x0;
			}
			else if (WorldData.get(Minecraft.getMinecraft().theWorld).unlockedTabs.get(index).contains(Event.allEvents.get(id-book.numNonEventTabs).toString()) || book.creative)
			{
				borderColor = 0x0;
				mainColor = Event.allEvents.get(id-book.numNonEventTabs).color;
			}
			if (id >= book.numNonEventTabs)
			{
				mc.fontRendererObj.drawString(this.displayString, x + 1, y, borderColor);
				mc.fontRendererObj.drawString(this.displayString, x - 1, y, borderColor);
				mc.fontRendererObj.drawString(this.displayString, x, y + 1, borderColor);
				mc.fontRendererObj.drawString(this.displayString, x, y - 1, borderColor);
			}
			mc.fontRendererObj.drawString(this.displayString, x, y, mainColor);
			GlStateManager.popMatrix();
			if (this.displayString.equals(WorldData.get(Minecraft.getMinecraft().theWorld).currentEvent.toString()))
			{
				GlStateManager.color(0.7F, 0.7F, 0.7F, 0.2F);
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
				this.drawTexturedModalRect(this.xPosition+2, hovered ? this.yPosition - 1 : this.yPosition, 0, 0, this.width-2, this.height);
				GlStateManager.popMatrix();
				GlStateManager.pushMatrix();
				GlStateManager.scale(1.0F, 1.0F, 1.0F);
				float f1 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F / 1.0F;
				GlStateManager.translate(-f1, 0.0F, 0.0F);
				GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
				this.drawTexturedModalRect(this.xPosition+2, hovered ? this.yPosition - 1 : this.yPosition, 0, 0, this.width-2, this.height);
				GlStateManager.popMatrix();
				GlStateManager.matrixMode(5888);
				GlStateManager.blendFunc(770, 771);
				GlStateManager.enableLighting();
				GlStateManager.depthFunc(515);
				GlStateManager.depthMask(true);
			}
		}
	}
}
