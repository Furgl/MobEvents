package furgl.mobEvents.client.renderer.entity;

import furgl.mobEvents.client.model.entity.ModelZombieThief;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityBossZombieSpawner;
import furgl.mobEvents.common.entity.layers.LayerZombieThiefArmor;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.model.ModelZombieVillager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class RenderEventZombie extends RenderZombie
{
	public RenderEventZombie(RenderManager renderManager) 
	{
		super(renderManager);
	}

	public RenderEventZombie(RenderManager renderManager, ModelZombieVillager zombie) 
	{
		super(renderManager);
		ReflectionHelper.setPrivateValue(RenderZombie.class, this, zombie, 3);//zombieVillagerModel
	}

	public RenderEventZombie(RenderManager renderManager, ModelZombie zombie) 
	{
		super(renderManager);
		ReflectionHelper.setPrivateValue(RenderZombie.class, this, zombie, 2);//field_82434_o
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
		}
	}
	
	@Override
	public void doRender(EntityZombie entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
		if (entity instanceof EntityBossZombieSpawner)
			return;
		else
			super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }
	
	@Override
	 public void doRenderShadowAndFire(Entity entityIn, double x, double y, double z, float yaw, float partialTicks)
    {
		if (entityIn instanceof EntityBossZombieSpawner)
			return;
		else
			super.doRenderShadowAndFire(entityIn, x, y, z, yaw, partialTicks);
    }
}
