package furgl.mobEvents.client.model.entity;

import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelZombieThief extends ModelZombie
{
	public ModelZombieThief()
	{
		super();
	}

	public ModelZombieThief(float p_i1165_1_, float p_i1165_2_, boolean p_i1165_3_)
    {
		super(p_i1165_1_, p_i1165_3_);
    }

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float scale)
	{
		this.setRotationAngles(f, f1, f2, f3, f4, scale, entity);
		GlStateManager.pushMatrix();

		if (entity.isSneaking())
		{
			this.bipedBody.rotateAngleX = 0.4F;
	        this.bipedRightArm.rotateAngleX = MathHelper.cos(f * 0.6662F + (float)Math.PI) * 2.0F * f1 * 0.5F;
	        this.bipedLeftArm.rotateAngleX = MathHelper.cos(f * 0.6662F) * 2.0F * f1 * 0.5F;
		}
		if (entity.isSneaking())
			GlStateManager.translate(0.0F, 0.2F, 0.0F);
		this.bipedBody.render(scale);
		this.bipedRightArm.render(scale);
		this.bipedLeftArm.render(scale);
		if (entity.isSneaking())
			GlStateManager.translate(0.0F, 0.05F, 0.0F);
		this.bipedHead.render(scale);
		this.bipedHeadwear.render(scale);
		if (entity.isSneaking())
			GlStateManager.translate(0.0F, -0.2F, 0.2F);
		this.bipedRightLeg.render(scale);
		this.bipedLeftLeg.render(scale);

		GlStateManager.popMatrix();
	}
}
