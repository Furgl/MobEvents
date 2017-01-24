package furgl.mobEvents.client.render.entity;

import furgl.mobEvents.common.entity.boss.EntityBossZombie;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.entity.monster.EntityZombie;

public class RenderEventZombie extends RenderZombie {
	public RenderEventZombie(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	protected void preRenderCallback(EntityZombie entity, float partialTickTime) {
		if (this.mainModel instanceof ModelBiped) {
			//sneaking
			((ModelBiped)this.mainModel).isSneak = entity.isSneaking();	
			//put arms down when sneaking
			if (entity.isSneaking())
				entity.setArmsRaised(false);
		}
		//boss scale
		if (entity instanceof EntityBossZombie)	{
			float scale = 3.0F;
			GlStateManager.scale(scale, scale, scale);
			this.shadowSize = 0.4f;
		}
	}

	@Override
	protected void renderLayers(EntityZombie entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scaleIn) {
		if (entity instanceof EntityBossZombie && entity.isInvisible())
			return;
		super.renderLayers(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scaleIn);
	}
}