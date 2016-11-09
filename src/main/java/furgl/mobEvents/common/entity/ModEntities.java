package furgl.mobEvents.common.entity;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.entity.SkeletalUprising.EntitySkeletonBard;
import furgl.mobEvents.common.entity.SkeletalUprising.EntitySkeletonClone;
import furgl.mobEvents.common.entity.SkeletalUprising.EntitySkeletonPyromaniac;
import furgl.mobEvents.common.entity.SkeletalUprising.EntitySkeletonSoldier;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityZombieBard;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityZombieClone;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityZombieHorse;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityZombieJumper;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityZombieMinion;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityZombiePyromaniac;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityZombieRider;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityZombieRunt;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityZombieSummoner;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityZombieThief;
import furgl.mobEvents.common.entity.bosses.EntityBossZombie;
import furgl.mobEvents.common.entity.bosses.spawner.EntityZombieBossSpawner;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ModEntities 
{
	public static void registerEntities() 
	{
		int ID = 0;    

		//Zombie Apocalypse
		EntityRegistry.registerModEntity(EntityZombieRunt.class, "zombieRunt", ID++, MobEvents.instance, 64, 3, true);
		EntityRegistry.registerModEntity(EntityZombiePyromaniac.class, "zombiePyromaniac", ID++, MobEvents.instance, 64, 3, true);
		EntityRegistry.registerModEntity(EntityZombieRider.class, "zombieRider", ID++, MobEvents.instance, 64, 3, true);
		EntityRegistry.registerModEntity(EntityZombieHorse.class, "zombieHorse", ID++, MobEvents.instance, 64, 3, true);
		EntityRegistry.registerModEntity(EntityZombieBard.class, "zombieBard", ID++, MobEvents.instance, 64, 3, true);
		EntityRegistry.registerModEntity(EntityZombieClone.class, "zombieClone", ID++, MobEvents.instance, 64, 3, true);
		EntityRegistry.registerModEntity(EntityZombieSummoner.class, "zombieSummoner", ID++, MobEvents.instance, 64, 3, true);
		EntityRegistry.registerModEntity(EntityZombieMinion.class, "zombieMinion", ID++, MobEvents.instance, 64, 3, true);
		EntityRegistry.registerModEntity(EntityZombieJumper.class, "zombieJumper", ID++, MobEvents.instance, 64, 3, true);
		EntityRegistry.registerModEntity(EntityZombieThief.class, "zombieThief", ID++, MobEvents.instance, 64, 3, true);
		EntityRegistry.registerModEntity(EntityBossZombie.class, "zombieBoss", ID++, MobEvents.instance, 64, 3, true);
		//Skeletal Uprising
		EntityRegistry.registerModEntity(EntitySkeletonSoldier.class, "skeletonSoldier", ID++, MobEvents.instance, 64, 3, true);
		EntityRegistry.registerModEntity(EntitySkeletonBard.class, "skeletonBard", ID++, MobEvents.instance, 64, 3, true);
		EntityRegistry.registerModEntity(EntitySkeletonClone.class, "skeletonClone", ID++, MobEvents.instance, 64, 3, true);
		EntityRegistry.registerModEntity(EntitySkeletonPyromaniac.class, "skeletonPyromaniac", ID++, MobEvents.instance, 64, 3, true);
		//boss spawners
		EntityRegistry.registerModEntity(EntityZombieBossSpawner.class, "zombieBossSpawner", ID++, MobEvents.instance, 64, 3, true);
		//other
		EntityRegistry.registerModEntity(EntityFireArrow.class, "fireArrow", ID++, MobEvents.instance, 64, 3, true);
	}
}
