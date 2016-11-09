package furgl.mobEvents.client.render.entity;

import net.minecraft.client.model.ModelSkeleton;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSkeleton;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.util.ResourceLocation;

public class RenderEventSkeleton extends RenderSkeleton
{
	public RenderEventSkeleton(RenderManager renderManager) 
	{
		super(renderManager);
	}

/*	public RenderEventSkeleton(RenderManager renderManager, final ModelSkeleton zombie) 
	{
		super(renderManager);
		ReflectionHelper.setPrivateValue(RenderZombie.class, this, zombie, 3);//zombieVillagerModel
		if (zombie instanceof ModelZombieBoss)
		{
			for (LayerRenderer layer : this.layerRenderers) {
				if (layer.getClass().getSimpleName().equals("LayerVillagerArmor"))
				{
					this.layerRenderers.remove(layer);
					break;
				}
			}
			LayerVillagerArmor layerbipedarmor = new LayerZombieBossArmor(this);
			this.addLayer(layerbipedarmor);
			ReflectionHelper.setPrivateValue(RenderZombie.class, this, this.layerRenderers, 4); //field_177121_n
		}
	}*/

	public RenderEventSkeleton(RenderManager renderManager, ModelSkeleton skeleton) 
	{
		super(renderManager);
		/*ReflectionHelper.setPrivateValue(RenderZombie.class, this, zombie, 2);//field_82434_o
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
			ReflectionHelper.setPrivateValue(RenderZombie.class, this, this.layerRenderers, 5); //field_177122_o
		}*/
	}

	@Override
	protected void preRenderCallback(EntitySkeleton entity, float partialTickTime)
	{
/*		if (entity instanceof EntityBossZombie)
		{
			float scale = 3.0F;
			GlStateManager.scale(scale, scale, scale);
			this.shadowSize = 0.4f;
		}*/
	}

	@Override
	public void doRender(EntitySkeleton entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
/*		if (entity instanceof EntityBossZombie)
			BossStatus.setBossStatus((EntityBossZombie) entity, true);*/

		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySkeleton entity)
	{
		/*if (entity instanceof EntityBossZombie)
		{
			switch (((EntityBossZombie)entity).type)
			{
			case 1:
				return new ResourceLocation(MobEvents.MODID+":textures/entity/ZombieApocalypse/zombie_villager.png");
			case 2:
				return new ResourceLocation(MobEvents.MODID+":textures/entity/ZombieApocalypse/zombie_butcher.png");
			case 3:
				return new ResourceLocation(MobEvents.MODID+":textures/entity/ZombieApocalypse/zombie_farmer.png");
			case 4:
				return new ResourceLocation(MobEvents.MODID+":textures/entity/ZombieApocalypse/zombie_librarian.png");
			case 5:
				return new ResourceLocation(MobEvents.MODID+":textures/entity/ZombieApocalypse/zombie_priest.png");
			case 6: 
				return new ResourceLocation(MobEvents.MODID+":textures/entity/ZombieApocalypse/zombie_smith.png");
			}
		}*/
		return super.getEntityTexture(entity);
	}
	
	@Override
	protected void renderLayers(EntitySkeleton entity, float p_177093_2_, float p_177093_3_, float partialTicks, float p_177093_5_, float p_177093_6_, float p_177093_7_, float p_177093_8_)
    {
/*		if (entity instanceof EntityBossZombie && entity.isInvisible())
			return;*/
		super.renderLayers(entity, p_177093_2_, p_177093_3_, partialTicks, p_177093_5_, p_177093_6_, p_177093_7_, p_177093_8_);
    }
}
