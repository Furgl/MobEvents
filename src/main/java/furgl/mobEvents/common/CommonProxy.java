package furgl.mobEvents.common;

import furgl.mobEvents.common.entity.ZombieApocalypse.EntitySummonerZombie;
import furgl.mobEvents.util.EntitySpawner;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class CommonProxy 
{
	public void registerRenders() { }

	public void registerAchievements() { }

	public Class getEntityFXClass() {return null;}
	
	public void spawnEntitySpawner(Class entityClass, World world, double x, double y, double z, EntitySpawner spawner, int heightIterator, int entityIterator) { }

	public void spawnEntitySummonerZombieSmokeFX(EntitySummonerZombie entitySummonerZombie, Vec3 vec) { }

	public void openBookGui(EntityPlayer player, boolean creative) { }
}
