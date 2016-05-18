package furgl.mobEvents.common.Events;

import java.util.ArrayList;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityBardZombie;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityCloneZombie;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityEventZombie;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityMinionZombie;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityPyromaniacZombie;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityRiderZombie;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityRuntZombie;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntitySummonerZombie;
import furgl.mobEvents.common.entity.ZombieApocalypse.IEventMob;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ZombieApocalypse extends Event
{
	public ZombieApocalypse() 
	{ 
		this.color = 1572663;
		this.red = 0.23f;
		this.green = 0.43f;
		this.blue = 0.23f;
		this.enumColor = EnumChatFormatting.DARK_GREEN;
		this.setSounds();
		this.setMobs();
		this.setBookDescription();
	}
	
	@Override
	public void setBookDescription()
	{
		this.bookJokes = new ArrayList<String>();
		this.bookJokes.add("What did the zombie say to his date? I just love a woman with BRAAAINS!");		
		this.bookJokes.add("Where is the safest place in your home from a zombie? The LIVING room!");
		this.bookJokes.add("Do zombies eat dinner with their family? No, their family IS the dinner!");
		this.bookJokes.add("What is a zombie's favorite shampoo? Head & Shoulders!");
		this.bookJokes.add("Do zombies eat candy with their fingers? No, they eat the fingers separately.");
		this.bookJokes.add("What does it take to become a zombie? DEADication!");
		this.bookJokes.add("What do you do if you see a zombie? Hope it's Halloween!");
		this.bookJokes.add("What has a dog's head, a cat's tail & brains all over its face? A zombie leaving the pet store!");
		this.bookOccurs = "Night";
		this.bookWaves = "3 + Boss";
	}

	@Override
	public void setSounds()
	{
		sounds = new ArrayList<String>();
		sounds.add(MobEvents.MODID+":ambience.zombie_ambience");
		/*sounds.add("mob.zombie.hurt");
		sounds.add("mob.zombie.say");
		sounds.add("mob.zombie.step");
		sounds.add("mob.zombie.wood");*/
	}

	@Override
	public void setMobs()
	{
		mobs = new ArrayList<IEventMob>();
		ArrayList<EntityEventZombie> tmp = new ArrayList();
		tmp.add(new EntityRuntZombie(null));
		tmp.add(new EntityBardZombie(null));
		tmp.add(new EntityCloneZombie(null));
		tmp.add(new EntityMinionZombie(null));
		tmp.add(new EntityPyromaniacZombie(null));
		tmp.add(new EntityRiderZombie(null));
		tmp.add(new EntitySummonerZombie(null));
		for (int i=0; i<tmp.size(); i++)
		{
			int progressOnDeath = 1000;
			int indexToAdd = 0;
			for (int j=0; j<tmp.size(); j++)
			{
				if (tmp.get(j).progressOnDeath < progressOnDeath && !mobs.contains(tmp.get(j)))
				{
					progressOnDeath = tmp.get(j).progressOnDeath;
					indexToAdd = j;
				}
			}
			mobs.add(tmp.get(indexToAdd));
		}
	}

	public void onUpdate()
	{
		if (rand.nextInt(200) == 0)
		{
			this.updatePlayers();
			this.playSound(sounds);
		}
	}

	public void removeCustomSpawns()
	{
		for (IEventMob zombie : this.mobs)
			EntityRegistry.removeSpawn((Class<? extends EntityLiving>) zombie.getClass(), EnumCreatureType.MONSTER, Event.biomes);
	}

	public void wave1() 
	{
		super.wave1();
		this.updatePlayers();
		for (EntityPlayer player : players)
			Event.world.playSoundAtEntity(player, MobEvents.MODID+":mob.event_zombie.say", 0.4f, 1.5f);
		EntityRegistry.addSpawn(EntityRuntZombie.class, 2000, 4, 4, EnumCreatureType.MONSTER, Event.biomes);
		EntityRegistry.addSpawn(EntityPyromaniacZombie.class, 90, 1, 1, EnumCreatureType.MONSTER, Event.biomes);
		EntityRegistry.addSpawn(EntityBardZombie.class, 90, 1, 1, EnumCreatureType.MONSTER, Event.biomes);
		EntityRegistry.addSpawn(EntityRiderZombie.class, 30, 1, 1, EnumCreatureType.MONSTER, Event.biomes);
		EntityRegistry.addSpawn(EntitySummonerZombie.class, 30, 1, 1, EnumCreatureType.MONSTER, Event.biomes);
		EntityRegistry.addSpawn(EntityCloneZombie.class, 30, 1, 1, EnumCreatureType.MONSTER, Event.biomes);
	}

	public void wave2() 
	{
		super.wave2();
		this.removeCustomSpawns();
		EntityRegistry.addSpawn(EntityRuntZombie.class, 2000, 4, 4, EnumCreatureType.MONSTER, Event.biomes);
		EntityRegistry.addSpawn(EntityPyromaniacZombie.class, 130, 1, 1, EnumCreatureType.MONSTER, Event.biomes);
		EntityRegistry.addSpawn(EntityBardZombie.class, 130, 1, 1, EnumCreatureType.MONSTER, Event.biomes);
		EntityRegistry.addSpawn(EntityRiderZombie.class, 70, 1, 1, EnumCreatureType.MONSTER, Event.biomes);
		EntityRegistry.addSpawn(EntitySummonerZombie.class, 70, 1, 1, EnumCreatureType.MONSTER, Event.biomes);
		EntityRegistry.addSpawn(EntityCloneZombie.class, 70, 1, 1, EnumCreatureType.MONSTER, Event.biomes);
	}

	public void wave3() 
	{
		super.wave3();
		this.removeCustomSpawns();
		EntityRegistry.addSpawn(EntityRuntZombie.class, 2000, 4, 4, EnumCreatureType.MONSTER, Event.biomes);
		EntityRegistry.addSpawn(EntityPyromaniacZombie.class, 150, 1, 1, EnumCreatureType.MONSTER, Event.biomes);
		EntityRegistry.addSpawn(EntityBardZombie.class, 150, 1, 1, EnumCreatureType.MONSTER, Event.biomes);
		EntityRegistry.addSpawn(EntityRiderZombie.class, 90, 2, 2, EnumCreatureType.MONSTER, Event.biomes);
		EntityRegistry.addSpawn(EntitySummonerZombie.class, 90, 1, 1, EnumCreatureType.MONSTER, Event.biomes);
		EntityRegistry.addSpawn(EntityCloneZombie.class, 90, 1, 1, EnumCreatureType.MONSTER, Event.biomes);
	}

	public void bossWave()
	{
		super.bossWave();
		this.removeCustomSpawns();
	}

	public void startEvent() 
	{ 
		Event.currentEvent = new ZombieApocalypse();
		super.startEvent();
		MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation("Did I hear something?").setChatStyle(new ChatStyle().setBold(true).setColor(this.enumColor).setItalic(true)));
		this.updatePlayers();
		for (EntityPlayer player : players)
			Event.world.playSoundAtEntity(player, "mob.zombie.infect", 10f, 0f);
	}

	public void stopEvent() 
	{
		super.stopEvent();
		MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation(this.toString() + " has ended.").setChatStyle(new ChatStyle().setBold(true).setColor(this.enumColor)));
		this.updatePlayers();
		for (EntityPlayer player : players)
			Event.world.playSoundAtEntity(player, "mob.zombie.remedy", 0.2f, 2f);
		this.removeCustomSpawns();
	}

	public String toString()
	{
		return "Zombie Apocalypse";
	}
}
