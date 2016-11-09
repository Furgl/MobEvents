package furgl.mobEvents.client.gui.book.buttons;

import furgl.mobEvents.client.gui.book.GuiEventBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonNextPage extends GuiButton
{
	private GuiEventBook book;
	private boolean nextPage;

	public GuiButtonNextPage(int buttonId, int x, int y, int width, int height, String buttonText, boolean nextPage, GuiEventBook book)
	{
		super(buttonId, x, y, width, height, buttonText);
		this.nextPage = nextPage;
		this.book = book;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY)
	{
		if (this.visible)
		{
			this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
			mc.getTextureManager().bindTexture(book.bookPageTexture);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableLighting();
			GlStateManager.enableAlpha();
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			GlStateManager.blendFunc(770, 771);
			int i = 3;
			int j = 122;
			if (hovered)
				i += 23;
			if (this.nextPage)
				j -= 14;
			this.drawTexturedModalRect(this.xPosition, this.yPosition, i, j, this.width, this.height);
		}
	}
}
