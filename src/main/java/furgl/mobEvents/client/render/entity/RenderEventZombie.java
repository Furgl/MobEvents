package furgl.mobEvents.client.render.entity;

import furgl.mobEvents.client.model.entity.ModelZombieThief;
import furgl.mobEvents.common.entity.bosses.EntityBossZombie;
import furgl.mobEvents.common.entity.layers.LayerZombieThiefArmor;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class RenderEventZombie extends RenderZombie
{
	public RenderEventZombie(RenderManager renderManager) 
	{
		super(renderManager);
	}

	public RenderEventZombie(RenderManager renderManager, ModelZombie zombie) 
	{
		super(renderManager);
		ReflectionHelper.setPrivateValue(RenderZombie.class, this, zombie, 8);//defaultModel
		this.mainModel = zombie;
		this.modelBipedMain = zombie;

		if (zombie instanceof ModelZombieThief)
		{
			for (LayerRenderer layer : this.layerRenderers) {
				if (layer.getClass().getSimpleName().equals("LayerVillagerArmor"))
				{
					this.layerRenderers.remove(layer);
					break;
				}
			}
			LayerZombieThiefArmor layerbipedarmor = new LayerZombieThiefArmor(this);
			this.addLayer(layerbipedarmor);
			ReflectionHelper.setPrivateValue(RenderZombie.class, this, this.layerRenderers, 11); //defaultLayers
		}
	}

	@Override
	protected void preRenderCallback(EntityZombie entity, float partialTickTime)
	{
		if (entity instanceof EntityBossZombie)
		{
			float scale = 3.0F;
			GlStateManager.scale(scale, scale, scale);
			this.shadowSize = 0.4f;
		}
	}

	@Override
	public void doRender(EntityZombie entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
/*		if (entity instanceof EntityBossZombie)
			BossStatus.setBossStatus((EntityBossZombie) entity, true);
*/
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}
	
	@Override
	protected void renderLayers(EntityZombie entity, float p_177093_2_, float p_177093_3_, float partialTicks, float p_177093_5_, float p_177093_6_, float p_177093_7_, float p_177093_8_)
    {
		if (entity instanceof EntityBossZombie && entity.isInvisible())
			return;
		super.renderLayers(entity, p_177093_2_, p_177093_3_, partialTicks, p_177093_5_, p_177093_6_, p_177093_7_, p_177093_8_);
    }
}
