package furgl.mobEvents.client;

import furgl.mobEvents.client.gui.achievements.Achievements;
import furgl.mobEvents.client.gui.book.GuiEventBook;
import furgl.mobEvents.client.gui.progressBar.GuiEventProgress;
import furgl.mobEvents.client.particle.EntitySummonerZombieSmokeFX;
import furgl.mobEvents.client.renderer.entity.RenderEventZombie;
import furgl.mobEvents.common.CommonProxy;
import furgl.mobEvents.common.block.ModBlocks;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityBardZombie;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityCloneZombie;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityMinionZombie;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityPyromaniacZombie;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityRiderZombie;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityRuntZombie;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntitySummonerZombie;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityZombieHorse;
import furgl.mobEvents.common.item.ModItems;
import furgl.mobEvents.util.EntitySpawner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelHorse;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.entity.RenderHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.Achievement;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy
{
	GuiEventProgress gui;
	
	@Override
	public void registerRenders() 
	{	
		registerEntityRenders();
		ModItems.registerRenders();
		ModBlocks.registerRenders();
	}
	
	@SuppressWarnings("deprecation")
	private void registerEntityRenders() 
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityRuntZombie.class, new RenderEventZombie(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityPyromaniacZombie.class, new RenderEventZombie(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityRiderZombie.class, new RenderEventZombie(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityZombieHorse.class, new RenderHorse(Minecraft.getMinecraft().getRenderManager(), new ModelHorse(), 0.75f));
		RenderingRegistry.registerEntityRenderingHandler(EntityBardZombie.class, new RenderEventZombie(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityCloneZombie.class, new RenderEventZombie(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntitySummonerZombie.class, new RenderEventZombie(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityMinionZombie.class, new RenderEventZombie(Minecraft.getMinecraft().getRenderManager()));
	}
	
	@Override
	public void registerAchievements()
	{
		AchievementPage.registerAchievementPage(new AchievementPage("Mob Events", (Achievement[]) Achievements.achievements.toArray(new Achievement[Achievements.achievements.size()])));
		
		for (int i=0; i<Achievements.achievements.size(); i++)
			Achievements.achievements.get(i).registerStat();
	}
	
	@Override
	public Class getEntityFXClass()
	{
		return EntityFX.class;
	}
	
	@Override
	public void spawnEntitySpawner(Class entityClass, World world, double x, double y, double z, EntitySpawner spawner, int heightIterator, int entityIterator) 
	{ 
		try 
		{
			EntityFX particle = (EntityFX) entityClass.getConstructor(World.class, double.class, double.class, double.class, EntitySpawner.class, int.class, int.class).newInstance(world, x, y, z, spawner, heightIterator, entityIterator);
			Minecraft.getMinecraft().effectRenderer.addEffect(particle);
		} 
		catch (Exception e) 
		{
			System.out.println("ERROR: ENTITYFX " + entityClass + " MISSING SPAWNER CONSTRUCTOR");
			e.printStackTrace();
		}
	}
	
	@Override
	public void spawnEntitySummonerZombieSmokeFX(EntitySummonerZombie entity, Vec3 vec) 
	{ 
		EntitySpawner spawner = new EntitySpawner(EntitySummonerZombieSmokeFX.class, entity.worldObj, vec, 10);
		spawner.setMovementFollowShape(0.1D);
		spawner.setShapeCircle(1D);
		spawner.setRandVar(0.0D);
		spawner.setEntityToFollow(entity);
		spawner.run();
	}
	
	@Override
	public void openBookGui(EntityPlayer player, boolean creative) 
	{ 
		Minecraft.getMinecraft().displayGuiScreen(new GuiEventBook(player, creative));
	}
}
