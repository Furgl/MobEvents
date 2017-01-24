package furgl.mobEvents.common.entity;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.entity.SkeletalUprising.EntitySkeletonBard;
import furgl.mobEvents.common.entity.SkeletalUprising.EntitySkeletonClone;
import furgl.mobEvents.common.entity.SkeletalUprising.EntitySkeletonHorse;
import furgl.mobEvents.common.entity.SkeletalUprising.EntitySkeletonPyromaniac;
import furgl.mobEvents.common.entity.SkeletalUprising.EntitySkeletonRider;
import furgl.mobEvents.common.entity.SkeletalUprising.EntitySkeletonSoldier;
import furgl.mobEvents.common.entity.SkeletalUprising.EntitySkeletonThief;
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
import furgl.mobEvents.common.entity.boss.EntityBossZombie;
import furgl.mobEvents.common.entity.boss.spawner.EntityZombieBossSpawner;
import furgl.mobEvents.common.entity.projectile.EntityPyromaniacsArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ModEntities 
{
	private static int id;
	
	public static void registerEntities() {
		//Zombie Apocalypse
		registerEntityWithSpawnEgg(EntityZombieRunt.class, 44975, 7969893);
		registerEntityWithSpawnEgg(EntityZombiePyromaniac.class, 44975, 7969893);
		registerEntityWithSpawnEgg(EntityZombieRider.class, 44975, 7969893);
		registerEntity(EntityZombieHorse.class);
		registerEntityWithSpawnEgg(EntityZombieBard.class, 44975, 7969893);
		registerEntityWithSpawnEgg(EntityZombieClone.class, 44975, 7969893);
		registerEntityWithSpawnEgg(EntityZombieSummoner.class, 44975, 7969893);
		registerEntityWithSpawnEgg(EntityZombieMinion.class, 44975, 7969893);
		registerEntityWithSpawnEgg(EntityZombieJumper.class, 44975, 7969893);
		registerEntityWithSpawnEgg(EntityZombieThief.class, 44975, 7969893);
		registerEntity(EntityBossZombie.class);
		registerEntityWithSpawnEgg(EntityZombieBossSpawner.class, 44975, 7969893);
		//Skeletal Uprising
		registerEntityWithSpawnEgg(EntitySkeletonSoldier.class, 12698049, 4802889);
		registerEntityWithSpawnEgg(EntitySkeletonBard.class, 12698049, 4802889);
		registerEntityWithSpawnEgg(EntitySkeletonClone.class, 12698049, 4802889);
		registerEntityWithSpawnEgg(EntitySkeletonPyromaniac.class, 12698049, 4802889);
		registerEntityWithSpawnEgg(EntitySkeletonRider.class, 12698049, 4802889);
		registerEntity(EntitySkeletonHorse.class);
		registerEntityWithSpawnEgg(EntitySkeletonThief.class, 12698049, 4802889);
		//other
		registerEntity(EntityPyromaniacsArrow.class);
	}
	
	/**Registers entity to unlocalizedName based on entity class (i.e. EntityZombieRunt = zombieRunt)*/
    private static void registerEntity(Class clazz) {
    	String unlocalizedName = clazz.getSimpleName().replace("Entity", ""); 
    	unlocalizedName = unlocalizedName.substring(0, 1).toLowerCase()+unlocalizedName.substring(1);
        EntityRegistry.registerModEntity(clazz, unlocalizedName, id++, MobEvents.instance, 64, 3, true);
    }
    
    /**Registers entity to unlocalizedName based on entity class (i.e. EntityZombieRunt = zombieRunt) and gives it a spawn egg*/
    private static void registerEntityWithSpawnEgg(Class clazz, int primary, int secondary) {
    	String unlocalizedName = clazz.getSimpleName().replace("Entity", "");   
    	unlocalizedName = unlocalizedName.substring(0, 1).toLowerCase()+unlocalizedName.substring(1);
    	EntityRegistry.registerModEntity(clazz, unlocalizedName, id++, MobEvents.instance, 64, 3, true, primary, secondary);
    	MobEvents.mobsTab.orderedMobs.add(getSpawnEgg(clazz));
    }

    /**Get spawn egg for given mob entity class*/
	public static ItemStack getSpawnEgg(Class clazz) {
		ItemStack stack = new ItemStack(Items.SPAWN_EGG);
		NBTTagCompound nbt = new NBTTagCompound();    	
		String unlocalizedName = clazz.getSimpleName().replace("Entity", "");  
    	unlocalizedName = unlocalizedName.substring(0, 1).toLowerCase()+unlocalizedName.substring(1);
		nbt.setString("id", MobEvents.MODID+"."+unlocalizedName);
		NBTTagCompound nbt2 = new NBTTagCompound();
		nbt2.setTag("EntityTag", nbt);
		stack.setTagCompound(nbt2);
		return stack;
	}
}