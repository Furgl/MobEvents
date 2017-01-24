package furgl.mobEvents.client.render.entity;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSkeleton;
import net.minecraft.entity.monster.EntitySkeleton;

public class RenderEventSkeleton extends RenderSkeleton {
	public RenderEventSkeleton(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	protected void preRenderCallback(EntitySkeleton entity, float partialTickTime) {
		//sneaking
		if (this.mainModel instanceof ModelBiped)
			((ModelBiped)this.mainModel).isSneak = entity.isSneaking();	
	}
}