package furgl.mobEvents.common.entity;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityBardZombie;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityBossZombieSpawner;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityCloneZombie;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityJumperZombie;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityMinionZombie;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityPyromaniacZombie;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityRiderZombie;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityRuntZombie;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntitySummonerZombie;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityThiefZombie;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityZombieHorse;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ModEntities 
{
	public static void registerEntities() 
    {
        int ID = 0;    
               
        EntityRegistry.registerModEntity(EntityRuntZombie.class, "runtZombie", ID++, MobEvents.instance, 64, 3, true);
        EntityRegistry.registerModEntity(EntityPyromaniacZombie.class, "pyromaniacZombie", ID++, MobEvents.instance, 64, 3, true);
        EntityRegistry.registerModEntity(EntityRiderZombie.class, "riderZombie", ID++, MobEvents.instance, 64, 3, true);
        EntityRegistry.registerModEntity(EntityZombieHorse.class, "zombieHorse", ID++, MobEvents.instance, 64, 3, true);
        EntityRegistry.registerModEntity(EntityBardZombie.class, "bardZombie", ID++, MobEvents.instance, 64, 3, true);
        EntityRegistry.registerModEntity(EntityCloneZombie.class, "cloneZombie", ID++, MobEvents.instance, 64, 3, true);
        EntityRegistry.registerModEntity(EntitySummonerZombie.class, "summonerZombie", ID++, MobEvents.instance, 64, 3, true);
        EntityRegistry.registerModEntity(EntityMinionZombie.class, "minionZombie", ID++, MobEvents.instance, 64, 3, true);
        EntityRegistry.registerModEntity(EntityJumperZombie.class, "jumperZombie", ID++, MobEvents.instance, 64, 3, true);
        EntityRegistry.registerModEntity(EntityThiefZombie.class, "thiefZombie", ID++, MobEvents.instance, 64, 3, true);
        
        EntityRegistry.registerModEntity(EntityBossZombieSpawner.class, "bossZombieSpawner", ID++, MobEvents.instance, 64, 3, true);
    }
}
