package furgl.mobEvents.client.gui.book.buttons;

import furgl.mobEvents.client.gui.book.GuiEventBook;
import furgl.mobEvents.common.entity.IEventMob;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonMobPage extends GuiButton
{	
	private GuiEventBook book;
	private float scale;
	public int startXPos;
	public IEventMob mob;

	public GuiButtonMobPage(int buttonId, int x, int y, int width, int height, String buttonText, GuiEventBook book, IEventMob mob)
	{
		super(buttonId, x, y, width, height, buttonText);
		this.book = book;
		this.startXPos = x;
		this.mob = mob;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY)
	{
		if (this.visible)
		{
			FontRenderer fontrenderer = mc.fontRendererObj;
			mc.getTextureManager().bindTexture(BUTTON_TEXTURES);

			if (this.enabled)
			{
				//custom color based on difficulty
				if (mob.getProgressOnDeath() == 100) 
					GlStateManager.color(0.7F, 0.0F, 0.7F, 1.0F);				
				else
				{
					float difficulty = (float)(mob.getProgressOnDeath()-1)/(book.maxProgress.get(book.currentTab-book.numNonEventTabs)-1);
					GlStateManager.color(255F, 1F-difficulty, 1F-difficulty, 1.0F);
				}
			}
			else
				GlStateManager.color(1F, 1F, 1F, 1.0F);

			GlStateManager.pushMatrix();
			scale = 0.7f;
			float w = (book.width - this.width) / 2;
			float h = (book.height - this.height) / 2;
			GlStateManager.translate(w, h, 0);
			GlStateManager.scale(scale, scale, scale);
			int tmp1 = this.xPosition;
			int tmp2 = this.yPosition;
			int tmp3 = this.width;
			int tmp4 = this.height;
			this.xPosition = (int) (this.xPosition*scale+w);
			this.yPosition = (int) (this.yPosition*scale+h);
			this.width *= scale;
			this.height *= scale;
			this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
			int i = this.getHoverState(this.hovered);
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			GlStateManager.blendFunc(770, 771);
			this.xPosition = tmp1;
			this.yPosition = tmp2;
			this.width = tmp3;
			this.height = tmp4;

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
			this.drawCenteredString(fontrenderer, TextFormatting.ITALIC+""+TextFormatting.BOLD+this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 7) / 2, j);

			GlStateManager.popMatrix();
		}
		//super.drawButton(mc, mouseX, mouseY);
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
	{
		float w = (book.width - this.width) / 2;
		float h = (book.height - this.height) / 2;
		int tmp1 = this.xPosition;
		int tmp2 = this.yPosition;
		int tmp3 = this.width;
		int tmp4 = this.height;
		this.xPosition = (int) (this.xPosition*scale+w);
		this.yPosition = (int) (this.yPosition*scale+h);
		this.width *= scale;
		this.height *= scale;
		boolean value = this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
		this.xPosition = tmp1;
		this.yPosition = tmp2;
		this.width = tmp3;
		this.height = tmp4;
		return value;
	}
}
