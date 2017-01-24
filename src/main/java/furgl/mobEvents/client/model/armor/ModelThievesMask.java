package furgl.mobEvents.client.model.armor;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class ModelThievesMask extends ModelBiped
{
	ModelRenderer frontbandana;
	ModelRenderer rightbandana;
	ModelRenderer leftbandana;
	ModelRenderer hat;
	ModelRenderer backbandana;
	ModelRenderer hatrim;

	public ModelThievesMask()
	{
		super(1F);
		this.textureHeight = 64;
		frontbandana = new ModelRenderer(this, 41, 0);
		frontbandana.addBox(-5F, -5F, -5F, 10, 3, 1);
		frontbandana.setRotationPoint(0F, 0F, 0F);
		frontbandana.setTextureSize(64, 32);
		frontbandana.mirror = true;
		setRotationAngles(frontbandana, 0F, 0F, 0F);
		rightbandana = new ModelRenderer(this, 0, 0);
		rightbandana.addBox(-5F, -5F, -4F, 1, 2, 9);
		rightbandana.setRotationPoint(0F, 0F, 0F);
		rightbandana.setTextureSize(64, 32);
		rightbandana.mirror = true;
		setRotationAngles(rightbandana, 0F, 0F, 0F);
		leftbandana = new ModelRenderer(this, 0, 0);
		leftbandana.addBox(4F, -5F, -4F, 1, 2, 9);
		leftbandana.setRotationPoint(0F, 0F, 0F);
		leftbandana.setTextureSize(64, 32);
		leftbandana.mirror = true;
		setRotationAngles(leftbandana, 0F, 0F, 0F);
		hat = new ModelRenderer(this, 0, 0);
		hat.addBox(-5F, -10F, -5F, 10, 5, 10);
		hat.setRotationPoint(0F, 0F, 0F);
		hat.setTextureSize(64, 32);
		hat.mirror = true;
		setRotationAngles(hat, -0.122173F, 0F, 0F);
		backbandana = new ModelRenderer(this, 0, 0);
		backbandana.addBox(-5F, -5F, 5F, 10, 2, 1);
		backbandana.setRotationPoint(0F, 0F, 0F);
		backbandana.setTextureSize(64, 32);
		backbandana.mirror = true;
		setRotationAngles(backbandana, 0F, 0F, 0F);
		hatrim = new ModelRenderer(this, 0, 19);
		hatrim.addBox(-5.5F, -7F, -5.5F, 11, 2, 11);
		hatrim.setRotationPoint(0F, 0F, 0F);
		hatrim.setTextureSize(64, 32);
		hatrim.mirror = true;
		setRotationAngles(hatrim, -0.122173F, 0F, 0F);

		this.frontbandana.addChild(backbandana);
		this.frontbandana.addChild(leftbandana);
		this.frontbandana.addChild(rightbandana);
		this.frontbandana.addChild(hat);
		this.frontbandana.addChild(hatrim);
	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float scale)
	{
		this.setRotationAngles(f, f1, f2, f3, f4, scale, entity);
		GlStateManager.pushMatrix();

		if (entity.isSneaking())
		{
			GlStateManager.translate(0.0F, 0.25F, 0.0F);
			GlStateManager.enableNormalize();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(770, 771);
			GlStateManager.color(0.5F, 0.5F, 0.5F, 0.3F);
		}

		this.bipedBody.render(scale);
		this.bipedRightArm.render(scale);
		this.bipedLeftArm.render(scale);
		this.bipedRightLeg.render(scale);
		this.bipedLeftLeg.render(scale);
		this.frontbandana.render(scale);
		
		if (entity.isSneaking())
		{
			GlStateManager.disableBlend();
			GlStateManager.disableNormalize();
		}
		GlStateManager.popMatrix();
	}

	private void setRotationAngles(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
	{
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		ModelBase.copyModelAngles(this.bipedHead, this.frontbandana);
	}
}