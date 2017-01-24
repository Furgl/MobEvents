package furgl.mobEvents.client.model.armor;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelDoubleJumpBoots extends ModelBiped
{
	ModelRenderer rightFoot; 
	ModelRenderer leftFoot;
	ModelRenderer rightWing1;
	ModelRenderer rightWing2;
	ModelRenderer rightWing3;
	ModelRenderer rightWing4;
	ModelRenderer rightWing5;
	ModelRenderer rightWing6;
	ModelRenderer leftWing1;
	ModelRenderer leftWing2;
	ModelRenderer leftWing3;
	ModelRenderer leftWing4;
	ModelRenderer leftWing5;
	ModelRenderer leftWing6;

	public ModelDoubleJumpBoots()
	{
		super(1F);
		float xRotationPoint = 4.2F;
		float yRotationPoint = 4F;
		float zRotationPoint = 2F;
		float zRotationAngle = 0.35F;
		rightWing1 = new ModelRenderer(this, 48, 0);
		rightWing1.addBox(0F, 0F, 0F, 1, 1, 7);
		rightWing1.setRotationPoint(-xRotationPoint, yRotationPoint, zRotationPoint);
		rightWing1.setTextureSize(64, 32);
		rightWing1.mirror = true;
		setRotationAngles(rightWing1, 0.296706F, -zRotationAngle, 0F);
		rightWing2 = new ModelRenderer(this, 50, 1);
		rightWing2.addBox(0F, 0F, 0F, 1, 1, 6);
		rightWing2.setRotationPoint(-xRotationPoint, yRotationPoint+1F, zRotationPoint);
		rightWing2.setTextureSize(64, 32);
		rightWing2.mirror = true;
		setRotationAngles(rightWing2, 0.2617994F, -zRotationAngle, 0F);
		rightWing3 = new ModelRenderer(this, 52, 2);
		rightWing3.addBox(0F, 0F, 0F, 1, 1, 5);
		rightWing3.setRotationPoint(-xRotationPoint, yRotationPoint+2F, zRotationPoint);
		rightWing3.setTextureSize(64, 32);
		rightWing3.mirror = true;
		setRotationAngles(rightWing3, 0.2268928F, -zRotationAngle, 0F);
		rightWing4 = new ModelRenderer(this, 54, 3);
		rightWing4.addBox(0F, 0F, 0F, 1, 1, 4);
		rightWing4.setRotationPoint(-xRotationPoint, yRotationPoint+3F, zRotationPoint);
		rightWing4.setTextureSize(64, 32);
		rightWing4.mirror = true;
		setRotationAngles(rightWing4, 0.1919862F, -zRotationAngle, 0F);
		rightWing5 = new ModelRenderer(this, 44, 0);
		rightWing5.addBox(0F, 0F, 0F, 1, 4, 1);
		rightWing5.setRotationPoint(-xRotationPoint, yRotationPoint, zRotationPoint);
		rightWing5.setTextureSize(64, 32);
		rightWing5.mirror = true;
		setRotationAngles(rightWing5, -0.1919862F, -zRotationAngle, 0F);
		rightWing6 = new ModelRenderer(this, 44, 0);
		rightWing6.addBox(0F, 0F, 0F, 1, 2, 1);
		rightWing6.setRotationPoint(-xRotationPoint, yRotationPoint+2.6F, zRotationPoint+0.1491525F);
		rightWing6.setTextureSize(64, 32);
		rightWing6.mirror = true;
		setRotationAngles(rightWing6, -0.6632251F, -zRotationAngle, 0F);
		leftWing1 = new ModelRenderer(this, 48, 0);
		leftWing1.addBox(0F, 0F, 0F, 1, 1, 7);
		leftWing1.setRotationPoint(xRotationPoint-1F, yRotationPoint, zRotationPoint);
		leftWing1.setTextureSize(64, 32);
		leftWing1.mirror = true;
		setRotationAngles(leftWing1, 0.296706F, zRotationAngle, 0F);
		leftWing2 = new ModelRenderer(this, 50, 1);
		leftWing2.addBox(0F, 0F, 0F, 1, 1, 6);
		leftWing2.setRotationPoint(xRotationPoint-1F, yRotationPoint+1F, zRotationPoint);
		leftWing2.setTextureSize(64, 32);
		leftWing2.mirror = true;
		setRotationAngles(leftWing2, 0.2617994F, zRotationAngle, 0F);
		leftWing3 = new ModelRenderer(this, 52, 2);
		leftWing3.addBox(0F, 0F, 0F, 1, 1, 5);
		leftWing3.setRotationPoint(xRotationPoint-1F, yRotationPoint+2F, zRotationPoint);
		leftWing3.setTextureSize(64, 32);
		leftWing3.mirror = true;
		setRotationAngles(leftWing3, 0.2268928F, zRotationAngle, 0F);
		leftWing4 = new ModelRenderer(this, 54, 3);
		leftWing4.addBox(0F, 0F, 0F, 1, 1, 4);
		leftWing4.setRotationPoint(xRotationPoint-1F, yRotationPoint+3F, zRotationPoint);
		leftWing4.setTextureSize(64, 32);
		leftWing4.mirror = true;
		setRotationAngles(leftWing4, 0.1919862F, zRotationAngle, 0F);
		leftWing5 = new ModelRenderer(this, 44, 0);
		leftWing5.addBox(0F, 0F, 0F, 1, 4, 1);
		leftWing5.setRotationPoint(xRotationPoint-1F, yRotationPoint, zRotationPoint);
		leftWing5.setTextureSize(64, 32);
		leftWing5.mirror = true;
		setRotationAngles(leftWing5, -0.1919862F, zRotationAngle, 0F);
		leftWing6 = new ModelRenderer(this, 44, 0);
		leftWing6.addBox(0F, 0F, 0F, 1, 2, 1);
		leftWing6.setRotationPoint(xRotationPoint-1F, yRotationPoint+2.6F, zRotationPoint+0.1F);
		leftWing6.setTextureSize(64, 32);
		leftWing6.mirror = true;
		setRotationAngles(leftWing6, -0.6632251F, zRotationAngle, 0F);

		this.bipedRightLeg.addChild(rightWing1);
		this.bipedRightLeg.addChild(rightWing2);
		this.bipedRightLeg.addChild(rightWing3);
		this.bipedRightLeg.addChild(rightWing4);
		this.bipedRightLeg.addChild(rightWing5);
		this.bipedRightLeg.addChild(rightWing6);
		this.bipedLeftLeg.addChild(leftWing1);
		this.bipedLeftLeg.addChild(leftWing2);
		this.bipedLeftLeg.addChild(leftWing3);
		this.bipedLeftLeg.addChild(leftWing4);
		this.bipedLeftLeg.addChild(leftWing5);
		this.bipedLeftLeg.addChild(leftWing6);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float scale)
	{
		this.setRotationAngles(f, f1, f2, f3, f4, scale, entity);
		GlStateManager.pushMatrix();

		if (entity.isSneaking())
		{
			GlStateManager.translate(0.0F, 0.2F, 0.0F);
		}

		this.bipedHead.render(scale);
		this.bipedBody.render(scale);
		this.bipedRightArm.render(scale);
		this.bipedLeftArm.render(scale);
		this.bipedRightLeg.render(scale);
		this.bipedLeftLeg.render(scale);
		this.bipedHeadwear.render(scale);

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
		
		//flapping
		float rotateAngleY = (MathHelper.cos(f2 * 0.2662F) * 0.2F /** f1*/)-0.3F;
		this.rightWing1.rotateAngleY = rotateAngleY;
		this.rightWing2.rotateAngleY = rotateAngleY;
		this.rightWing3.rotateAngleY = rotateAngleY;
		this.rightWing4.rotateAngleY = rotateAngleY;
		this.rightWing5.rotateAngleY = rotateAngleY;
		this.rightWing6.rotateAngleY = rotateAngleY;
		this.leftWing1.rotateAngleY = -rotateAngleY;
		this.leftWing2.rotateAngleY = -rotateAngleY;
		this.leftWing3.rotateAngleY = -rotateAngleY;
		this.leftWing4.rotateAngleY = -rotateAngleY;
		this.leftWing5.rotateAngleY = -rotateAngleY;
		this.leftWing6.rotateAngleY = -rotateAngleY;
		//stationary
		float rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.4F * f1;
		this.rightWing1.rotateAngleX -= rotateAngleX;
		this.rightWing2.rotateAngleX -= rotateAngleX;
		this.rightWing3.rotateAngleX -= rotateAngleX;
		this.rightWing4.rotateAngleX -= rotateAngleX;
		//this.rightWing5.rotateAngleX -= rotateAngleX;
		//this.rightWing6.rotateAngleX -= rotateAngleX;
		this.leftWing1.rotateAngleX -= -rotateAngleX;
		this.leftWing2.rotateAngleX -= -rotateAngleX;
		this.leftWing3.rotateAngleX -= -rotateAngleX;
		this.leftWing4.rotateAngleX -= -rotateAngleX;
		//this.leftWing5.rotateAngleX -= -rotateAngleX;
		//this.leftWing6.rotateAngleX -= -rotateAngleX;
		float rotateAngleZ = 0;
		this.rightWing1.rotateAngleZ = rotateAngleZ;
		this.rightWing2.rotateAngleZ = rotateAngleZ;
		this.rightWing3.rotateAngleZ = rotateAngleZ;
		this.rightWing4.rotateAngleZ = rotateAngleZ;
		//this.rightWing5.rotateAngleZ = rotateAngleZ;
		//this.rightWing6.rotateAngleZ = rotateAngleZ;
		this.leftWing1.rotateAngleZ = -rotateAngleZ;
		this.leftWing2.rotateAngleZ = -rotateAngleZ;
		this.leftWing3.rotateAngleZ = -rotateAngleZ;
		this.leftWing4.rotateAngleZ = -rotateAngleZ;
		//this.leftWing5.rotateAngleZ = -rotateAngleZ;
		//this.leftWing6.rotateAngleZ = -rotateAngleZ;
	}
}
