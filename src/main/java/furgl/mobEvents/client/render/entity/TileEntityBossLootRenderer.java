package furgl.mobEvents.client.render.entity;

import java.util.List;

import org.lwjgl.opengl.GL11;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.Events.Event;
import furgl.mobEvents.common.block.BlockBossLoot;
import furgl.mobEvents.common.block.ModBlocks;
import furgl.mobEvents.common.event.EventFogEvent;
import furgl.mobEvents.common.tileentity.TileEntityBossLoot;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class TileEntityBossLootRenderer extends TileEntitySpecialRenderer<TileEntityBossLoot>
{
	private static final ResourceLocation beaconBeam = new ResourceLocation("textures/entity/beacon_beam.png");
	private static final ResourceLocation textureNormal = new ResourceLocation(MobEvents.MODID+":textures/blocks/boss_loot.png");
	private ModelChest simpleChest = new ModelChest();

	public TileEntityBossLootRenderer()
	{
		super();
	}

	@Override
	public void renderTileEntityAt(TileEntityBossLoot te, double x, double y, double z, float partialTicks, int destroyStage)
	{
		x -= 1;
		GlStateManager.enableDepth();
		GlStateManager.depthFunc(515);
		GlStateManager.depthMask(true);
		int i;

		if (!te.hasWorldObj())
		{
			i = 0;
		}
		else
		{
			Block block = te.getBlockType();
			i = te.getBlockMetadata();

			if (block instanceof BlockChest && i == 0)
			{
				//((BlockChest)block).checkForSurroundingChests(te.getWorld(), te.getPos(), te.getWorld().getBlockState(te.getPos()));
				i = te.getBlockMetadata();
			}
		}

		if (true/*te.adjacentChestZNeg == null && te.adjacentChestXNeg == null*/)
		{
			ModelChest modelchest;

			/*if (te.adjacentChestXPos == null && te.adjacentChestZPos == null)
            {*/
			modelchest = this.simpleChest;

			if (destroyStage >= 0)
			{
				this.bindTexture(DESTROY_STAGES[destroyStage]);
				GlStateManager.matrixMode(5890);
				GlStateManager.pushMatrix();
				GlStateManager.scale(4.0F, 4.0F, 1.0F);
				GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
				GlStateManager.matrixMode(5888);
			}
			/*else if (this.isChristams)
                {
                    this.bindTexture(textureChristmas);
                }
                else if (te.getChestType() == 1)
                {
                    this.bindTexture(textureTrapped);
                }*/
			else
			{
				this.bindTexture(textureNormal);
			}
			/*}
            else
            {
                modelchest = this.largeChest;

                if (destroyStage >= 0)
                {
                    this.bindTexture(DESTROY_STAGES[destroyStage]);
                    GlStateManager.matrixMode(5890);
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(8.0F, 4.0F, 1.0F);
                    GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
                    GlStateManager.matrixMode(5888);
                }
                else if (this.isChristams)
                {
                    this.bindTexture(textureChristmasDouble);
                }
                else if (te.getChestType() == 1)
                {
                    this.bindTexture(textureTrappedDouble);
                }
                else
                {
                    this.bindTexture(textureNormalDouble);
                }
            }*/

			GlStateManager.pushMatrix();
			GlStateManager.enableRescaleNormal();

			if (destroyStage < 0)//TODO set color
				if (MobEvents.proxy.getWorldData().currentEvent.getClass() != Event.class)
					GlStateManager.color(EventFogEvent.currentColors[0], EventFogEvent.currentColors[1], EventFogEvent.currentColors[2]);				
				else
					GlStateManager.color(1, 1, 1);
			//GlStateManager.color(MobEvents.proxy.getWorldData().currentEvent.red-0.0f, MobEvents.proxy.getWorldData().currentEvent.green-0.0f, MobEvents.proxy.getWorldData().currentEvent.blue-0.0f, 1.0F);

			GlStateManager.translate((float)x, (float)y + 1.0F, (float)z + 1.0F);
			GlStateManager.scale(1.0F, -1.0F, -1.0F);
			GlStateManager.translate(0.5F, 0.5F, 0.5F);
			int j = 0;

			if (i == 2)
			{
				j = 180;
			}

			if (i == 3)
			{
				j = 0;
			}

			if (i == 4)
			{
				j = 90;
			}

			if (i == 5)
			{
				j = -90;
			}

			if (i == 2/* && te.adjacentChestXPos != null*/)
			{
				GlStateManager.translate(1.0F, 0.0F, 0.0F);
			}

			if (i == 5/* && te.adjacentChestZPos != null*/)
			{
				GlStateManager.translate(0.0F, 0.0F, -1.0F);
			}

			GlStateManager.rotate((float)j, 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(-0.5F, -0.5F, -0.5F);
			float f = te.prevLidAngle + (te.lidAngle - te.prevLidAngle) * partialTicks;

			/*if (te.adjacentChestZNeg != null)
            {
                float f1 = te.adjacentChestZNeg.prevLidAngle + (te.adjacentChestZNeg.lidAngle - te.adjacentChestZNeg.prevLidAngle) * partialTicks;

                if (f1 > f)
                {
                    f = f1;
                }
            }

            if (te.adjacentChestXNeg != null)
            {
                float f2 = te.adjacentChestXNeg.prevLidAngle + (te.adjacentChestXNeg.lidAngle - te.adjacentChestXNeg.prevLidAngle) * partialTicks;

                if (f2 > f)
                {
                    f = f2;
                }
            }
			 */
			if (te.getWorld().getBlockState(te.getPos()).getBlock() == ModBlocks.bossLoot)
			{
				switch (te.getWorld().getBlockState(te.getPos()).getValue(BlockBossLoot.FACING))
				{
				case EAST:
					GlStateManager.translate(1, 0, -1);
					break;
				case SOUTH:
					GlStateManager.translate(1, 0, 0);
					break;
				case WEST:
					GlStateManager.translate(0, 0, 1);
					break;
				default:
					break;
				}
			}

			f = 1.0F - f;
			f = 1.0F - f * f * f;
			modelchest.chestLid.rotateAngleX = -(f * (float)Math.PI / 2.0F);
			modelchest.renderAll();
			GlStateManager.disableRescaleNormal();
			GlStateManager.popMatrix();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

			if (destroyStage >= 0)
			{
				GlStateManager.matrixMode(5890);
				GlStateManager.popMatrix();
				GlStateManager.matrixMode(5888);
			}

		}

		//TODO beam
		y += 0.15D;
		x += 1;

		float f = ((TileEntityBossLoot) te).shouldBeamRender();
		GlStateManager.alphaFunc(516, 0.1F);

		if (f > 0.0F)
		{
			Tessellator tessellator = Tessellator.getInstance();
	        net.minecraft.client.renderer.VertexBuffer vertexbuffer = tessellator.getBuffer();
			GlStateManager.disableFog();
			List<TileEntityBossLoot.BeamSegment> list = ((TileEntityBossLoot) te).getBeamSegments();
			i = 0;

			for (int j = 0; j < list.size(); ++j)
			{
				TileEntityBossLoot.BeamSegment tileentitybeacon$beamsegment = (TileEntityBossLoot.BeamSegment)list.get(j);
				int k = i + tileentitybeacon$beamsegment.getHeight();
				this.bindTexture(beaconBeam);
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10497.0F);
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10497.0F);
				GlStateManager.disableLighting();
				GlStateManager.disableCull();
				GlStateManager.disableBlend();
				GlStateManager.depthMask(true);
				GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
				double d0 = (double)te.getWorld().getTotalWorldTime() + (double)partialTicks;
				double d1 = MathHelper.frac(-d0 * 0.2D - (double)MathHelper.floor_double(-d0 * 0.1D));
				float f1 = tileentitybeacon$beamsegment.getColors()[0];
				float f2 = tileentitybeacon$beamsegment.getColors()[1];
				float f3 = tileentitybeacon$beamsegment.getColors()[2];
				double d2 = d0 * 0.025D * -1.5D;
				double d4 = 0.5D + Math.cos(d2 + 2.356194490192345D) * 0.2D;
				double d5 = 0.5D + Math.sin(d2 + 2.356194490192345D) * 0.2D;
				double d6 = 0.5D + Math.cos(d2 + (Math.PI / 4D)) * 0.2D;
				double d7 = 0.5D + Math.sin(d2 + (Math.PI / 4D)) * 0.2D;
				double d8 = 0.5D + Math.cos(d2 + 3.9269908169872414D) * 0.2D;
				double d9 = 0.5D + Math.sin(d2 + 3.9269908169872414D) * 0.2D;
				double d10 = 0.5D + Math.cos(d2 + 5.497787143782138D) * 0.2D;
				double d11 = 0.5D + Math.sin(d2 + 5.497787143782138D) * 0.2D;
				double d12 = 0.0D;
				double d13 = 1.0D;
				double d14 = -1.0D + d1;
				double d15 = (double)((float)tileentitybeacon$beamsegment.getHeight() * f) * 2.5D + d14;
				vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
				vertexbuffer.pos(x + d4, y + (double)k, z + d5).tex(1.0D, d15).color(f1, f2, f3, 1.0F).endVertex();
				vertexbuffer.pos(x + d4, y + (double)i, z + d5).tex(1.0D, d14).color(f1, f2, f3, 1.0F).endVertex();
				vertexbuffer.pos(x + d6, y + (double)i, z + d7).tex(0.0D, d14).color(f1, f2, f3, 1.0F).endVertex();
				vertexbuffer.pos(x + d6, y + (double)k, z + d7).tex(0.0D, d15).color(f1, f2, f3, 1.0F).endVertex();
				vertexbuffer.pos(x + d10, y + (double)k, z + d11).tex(1.0D, d15).color(f1, f2, f3, 1.0F).endVertex();
				vertexbuffer.pos(x + d10, y + (double)i, z + d11).tex(1.0D, d14).color(f1, f2, f3, 1.0F).endVertex();
				vertexbuffer.pos(x + d8, y + (double)i, z + d9).tex(0.0D, d14).color(f1, f2, f3, 1.0F).endVertex();
				vertexbuffer.pos(x + d8, y + (double)k, z + d9).tex(0.0D, d15).color(f1, f2, f3, 1.0F).endVertex();
				vertexbuffer.pos(x + d6, y + (double)k, z + d7).tex(1.0D, d15).color(f1, f2, f3, 1.0F).endVertex();
				vertexbuffer.pos(x + d6, y + (double)i, z + d7).tex(1.0D, d14).color(f1, f2, f3, 1.0F).endVertex();
				vertexbuffer.pos(x + d10, y + (double)i, z + d11).tex(0.0D, d14).color(f1, f2, f3, 1.0F).endVertex();
				vertexbuffer.pos(x + d10, y + (double)k, z + d11).tex(0.0D, d15).color(f1, f2, f3, 1.0F).endVertex();
				vertexbuffer.pos(x + d8, y + (double)k, z + d9).tex(1.0D, d15).color(f1, f2, f3, 1.0F).endVertex();
				vertexbuffer.pos(x + d8, y + (double)i, z + d9).tex(1.0D, d14).color(f1, f2, f3, 1.0F).endVertex();
				vertexbuffer.pos(x + d4, y + (double)i, z + d5).tex(0.0D, d14).color(f1, f2, f3, 1.0F).endVertex();
				vertexbuffer.pos(x + d4, y + (double)k, z + d5).tex(0.0D, d15).color(f1, f2, f3, 1.0F).endVertex();
				tessellator.draw();
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.depthMask(false);
				d2 = 0.2D;
				d4 = 0.8D;
				d5 = 0.2D;
				d6 = 0.2D;
				d7 = 0.8D;
				d8 = 0.8D;
				d9 = 0.8D;
				d10 = 0.0D;
				d11 = 1.0D;
				d12 = -1.0D + d1;
				d13 = (double)((float)tileentitybeacon$beamsegment.getHeight() * f) + d12;
				vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
				vertexbuffer.pos(x + 0.2D, y + (double)k, z + 0.2D).tex(1.0D, d13).color(f1, f2, f3, 0.125F).endVertex();
				vertexbuffer.pos(x + 0.2D, y + (double)i, z + 0.2D).tex(1.0D, d12).color(f1, f2, f3, 0.125F).endVertex();
				vertexbuffer.pos(x + 0.8D, y + (double)i, z + 0.2D).tex(0.0D, d12).color(f1, f2, f3, 0.125F).endVertex();
				vertexbuffer.pos(x + 0.8D, y + (double)k, z + 0.2D).tex(0.0D, d13).color(f1, f2, f3, 0.125F).endVertex();
				vertexbuffer.pos(x + 0.8D, y + (double)k, z + 0.8D).tex(1.0D, d13).color(f1, f2, f3, 0.125F).endVertex();
				vertexbuffer.pos(x + 0.8D, y + (double)i, z + 0.8D).tex(1.0D, d12).color(f1, f2, f3, 0.125F).endVertex();
				vertexbuffer.pos(x + 0.2D, y + (double)i, z + 0.8D).tex(0.0D, d12).color(f1, f2, f3, 0.125F).endVertex();
				vertexbuffer.pos(x + 0.2D, y + (double)k, z + 0.8D).tex(0.0D, d13).color(f1, f2, f3, 0.125F).endVertex();
				vertexbuffer.pos(x + 0.8D, y + (double)k, z + 0.2D).tex(1.0D, d13).color(f1, f2, f3, 0.125F).endVertex();
				vertexbuffer.pos(x + 0.8D, y + (double)i, z + 0.2D).tex(1.0D, d12).color(f1, f2, f3, 0.125F).endVertex();
				vertexbuffer.pos(x + 0.8D, y + (double)i, z + 0.8D).tex(0.0D, d12).color(f1, f2, f3, 0.125F).endVertex();
				vertexbuffer.pos(x + 0.8D, y + (double)k, z + 0.8D).tex(0.0D, d13).color(f1, f2, f3, 0.125F).endVertex();
				vertexbuffer.pos(x + 0.2D, y + (double)k, z + 0.8D).tex(1.0D, d13).color(f1, f2, f3, 0.125F).endVertex();
				vertexbuffer.pos(x + 0.2D, y + (double)i, z + 0.8D).tex(1.0D, d12).color(f1, f2, f3, 0.125F).endVertex();
				vertexbuffer.pos(x + 0.2D, y + (double)i, z + 0.2D).tex(0.0D, d12).color(f1, f2, f3, 0.125F).endVertex();
				vertexbuffer.pos(x + 0.2D, y + (double)k, z + 0.2D).tex(0.0D, d13).color(f1, f2, f3, 0.125F).endVertex();
				tessellator.draw();
				GlStateManager.enableLighting();
				GlStateManager.enableTexture2D();
				GlStateManager.depthMask(true);
				i = k;
			}

			GlStateManager.enableFog();
		}
	}

	@Override
    public boolean isGlobalRenderer(TileEntityBossLoot p_188185_1_)
    {
        return true;
    }
}
