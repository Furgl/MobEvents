package furgl.mobEvents.client.render.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

public class RenderEventBossSummoner<T extends EntityLiving> extends RenderLiving<T>
{
	public RenderEventBossSummoner(RenderManager rendermanagerIn, ModelBase modelbaseIn, float shadowsizeIn) 
	{
		super(rendermanagerIn, modelbaseIn, shadowsizeIn);
	}
	
	@Override
	public void doRenderShadowAndFire(Entity entityIn, double x, double y, double z, float yaw, float partialTicks)
    {
		return;
    }
	
	@Override
	public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
		return;
    }

	@Override
	protected ResourceLocation getEntityTexture(T entity) 
	{
		return null;
	}

}
