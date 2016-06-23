package furgl.mobEvents.common.entity.layers;

import furgl.mobEvents.client.model.entity.ModelZombieThief;
import furgl.mobEvents.common.item.ModItems;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerZombieThiefArmor extends LayerBipedArmor
{
	private RendererLivingEntity<?> renderer;

	public LayerZombieThiefArmor(RendererLivingEntity<?> rendererIn)
	{
		super(rendererIn);
		this.renderer = rendererIn;
	}

	@Override
	protected void initArmor()
	{
		this.field_177189_c = new ModelZombieThief(0.5F, 0.0F, true);
		this.field_177186_d = new ModelZombieThief(1.0F, 0.0F, true);
	}

	@Override
	public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale)
	{
		this.renderLayer(entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale, 4);

		if (entitylivingbaseIn.isSneaking())
		{
			GlStateManager.pushMatrix();
			GlStateManager.enableNormalize();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(770, 771);
			GlStateManager.color(0.5F, 0.5F, 0.5F, 0.3F);
		}

		this.renderLayer(entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale, 3);
		this.renderLayer(entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale, 2);
		this.renderLayer(entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale, 1);

		if (entitylivingbaseIn.isSneaking())
		{
			GlStateManager.disableBlend();
			GlStateManager.disableNormalize();
			GlStateManager.popMatrix();
		}
	}

	private void renderLayer(EntityLivingBase entitylivingbaseIn, float p_177182_2_, float p_177182_3_, float p_177182_4_, float p_177182_5_, float p_177182_6_, float p_177182_7_, float p_177182_8_, int armorSlot)
	{
		ItemStack itemstack;
		if (armorSlot == 4)
			itemstack = new ItemStack(ModItems.thievesMask);
		else {
			itemstack = new ItemStack(Items.leather_chestplate);
			((ItemArmor)itemstack.getItem()).setColor(itemstack, 0);
		}
		float alpha = 0.3F;
		float colorR = 1.0F;
		float colorG = 1.0F;
		float colorB = 1.0F;

		if (itemstack != null && itemstack.getItem() instanceof ItemArmor)
		{
			ItemArmor itemarmor = (ItemArmor)itemstack.getItem();
			ModelBiped t = this.func_177175_a(armorSlot);
			t.setModelAttributes(this.renderer.getMainModel());
			t.setLivingAnimations(entitylivingbaseIn, p_177182_2_, p_177182_3_, p_177182_4_);
			t = getArmorModelHook(entitylivingbaseIn, itemstack, armorSlot, t);
			this.func_177179_a(t, armorSlot);
			boolean flag = armorSlot == 2;
			this.renderer.bindTexture(this.getArmorResource(entitylivingbaseIn, itemstack, flag ? 2 : 1, null));

			int i = itemarmor.getColor(itemstack);
			{
				if (i != -1) // Allow this for anything, not only cloth.
				{
					float f = (float)(i >> 16 & 255) / 255.0F;
					float f1 = (float)(i >> 8 & 255) / 255.0F;
					float f2 = (float)(i & 255) / 255.0F;
					GlStateManager.color(colorR * f, colorG * f1, colorB * f2, alpha);
					t.render(entitylivingbaseIn, p_177182_2_, p_177182_3_, p_177182_5_, p_177182_6_, p_177182_7_, p_177182_8_);
					this.renderer.bindTexture(this.getArmorResource(entitylivingbaseIn, itemstack, flag ? 2 : 1, "overlay"));
				}
				{ // Non-colored
					GlStateManager.color(colorR, colorG, colorB, alpha);
					t.render(entitylivingbaseIn, p_177182_2_, p_177182_3_, p_177182_5_, p_177182_6_, p_177182_7_, p_177182_8_);
				}
				// Default
				if (/*!this.field_177193_i && */itemstack.hasEffect())
				{
					this.func_177183_a(entitylivingbaseIn, t, p_177182_2_, p_177182_3_, p_177182_4_, p_177182_5_, p_177182_6_, p_177182_7_, p_177182_8_);
				}
			}
		}
	}
	
	private void func_177183_a(EntityLivingBase entitylivingbaseIn, ModelBiped modelbaseIn, float p_177183_3_, float p_177183_4_, float p_177183_5_, float p_177183_6_, float p_177183_7_, float p_177183_8_, float p_177183_9_)
    {
        float f = (float)entitylivingbaseIn.ticksExisted + p_177183_5_;
        this.renderer.bindTexture(ENCHANTED_ITEM_GLINT_RES);
        GlStateManager.enableBlend();
        GlStateManager.depthFunc(514);
        GlStateManager.depthMask(false);
        float f1 = 0.5F;
        GlStateManager.color(f1, f1, f1, 1.0F);

        for (int i = 0; i < 2; ++i)
        {
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(768, 1);
            float f2 = 0.76F;
            GlStateManager.color(0.5F * f2, 0.25F * f2, 0.8F * f2, 1.0F);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            float f3 = 0.33333334F;
            GlStateManager.scale(f3, f3, f3);
            GlStateManager.rotate(30.0F - (float)i * 60.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(0.0F, f * (0.001F + (float)i * 0.003F) * 20.0F, 0.0F);
            GlStateManager.matrixMode(5888);
            modelbaseIn.render(entitylivingbaseIn, p_177183_3_, p_177183_4_, p_177183_6_, p_177183_7_, p_177183_8_, p_177183_9_);
        }

        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(515);
        GlStateManager.disableBlend();
    }
}