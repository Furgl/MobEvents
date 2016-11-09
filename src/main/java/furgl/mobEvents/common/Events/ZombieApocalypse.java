package furgl.mobEvents.common.Events;

import java.util.ArrayList;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.entity.IEventMob;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityZombieBard;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityZombieClone;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityZombieJumper;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityZombieMinion;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityZombiePyromaniac;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityZombieRider;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityZombieRunt;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityZombieSummoner;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityZombieThief;
import furgl.mobEvents.common.entity.bosses.spawner.EntityZombieBossSpawner;
import furgl.mobEvents.common.sound.ModSoundEvents;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class ZombieApocalypse extends Event
{
	public ZombieApocalypse(World world) 
	{ 
		super(world);
		this.occurs = Occurs.NIGHT;
		this.color = 1572663;
		this.red = 0.23f;
		this.green = 0.43f;
		this.blue = 0.23f;
		this.enumColor = TextFormatting.DARK_GREEN;
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
		this.bookWaves = "3 + Boss";
	}

	@Override
	public void setSounds()
	{
		sounds = new ArrayList<SoundEvent>();
		sounds.add(ModSoundEvents.ambience_zombie_ambience);
	}

	@Override
	public void setMobs()
	{
		mobs = new ArrayList<IEventMob>();
		ArrayList<IEventMob> tmp = new ArrayList<IEventMob>();
		tmp.add(new EntityZombieRunt(MobEvents.proxy.world));
		tmp.add(new EntityZombieBard(MobEvents.proxy.world));
		tmp.add(new EntityZombieClone(MobEvents.proxy.world));
		tmp.add(new EntityZombieMinion(MobEvents.proxy.world));
		tmp.add(new EntityZombiePyromaniac(MobEvents.proxy.world));
		tmp.add(new EntityZombieRider(MobEvents.proxy.world));
		tmp.add(new EntityZombieSummoner(MobEvents.proxy.world));
		tmp.add(new EntityZombieJumper(MobEvents.proxy.world));
		tmp.add(new EntityZombieThief(MobEvents.proxy.world));
		tmp.add(new EntityZombieBossSpawner(MobEvents.proxy.world));
		for (int i=0; i<tmp.size(); i++)
		{
			int progressOnDeath = 1000;
			int indexToAdd = 0;
			for (int j=0; j<tmp.size(); j++)
			{
				if (tmp.get(j).getProgressOnDeath() < progressOnDeath && !mobs.contains(tmp.get(j)))
				{
					progressOnDeath = tmp.get(j).getProgressOnDeath();
					indexToAdd = j;
				}
			}
			mobs.add(tmp.get(indexToAdd));
		}
	}

	@Override
	public void onUpdate()
	{
		if (rand.nextInt(200) == 0)
			this.playSound(sounds);
		super.onUpdate();
	}

	@Override
	protected void playStartSound()
	{
		Event.playServerSound(ModSoundEvents.mob_event_zombie_say, 0.4f, 1.5f);
	}

	@Override
	public void startWave(int wave) {
		super.startWave(wave);

		int weightedProb = 600 + wave*100;	
		int progressDeduction = 80;
		switch (wave)
		{
		case 1:
			this.playStartSound();
			EntityRegistry.addSpawn(EntityZombieRunt.class, 3000, 4, 4, EnumCreatureType.MONSTER, Event.biomes);
			EntityRegistry.addSpawn(EntityZombieJumper.class, weightedProb -= progressDeduction, 4, 4, EnumCreatureType.MONSTER, Event.biomes);
			EntityRegistry.addSpawn(EntityZombiePyromaniac.class, weightedProb -= progressDeduction, 4, 4, EnumCreatureType.MONSTER, Event.biomes);
			EntityRegistry.addSpawn(EntityZombieBard.class, weightedProb -= progressDeduction, 4, 4, EnumCreatureType.MONSTER, Event.biomes);
			EntityRegistry.addSpawn(EntityZombieThief.class, weightedProb -= progressDeduction, 4, 4, EnumCreatureType.MONSTER, Event.biomes);
			EntityRegistry.addSpawn(EntityZombieRider.class, weightedProb -= progressDeduction, 4, 4, EnumCreatureType.MONSTER, Event.biomes);
			EntityRegistry.addSpawn(EntityZombieSummoner.class, weightedProb -= progressDeduction, 4, 4, EnumCreatureType.MONSTER, Event.biomes);
			EntityRegistry.addSpawn(EntityZombieClone.class, weightedProb -= progressDeduction, 4, 4, EnumCreatureType.MONSTER, Event.biomes);
			break;
		case 2:
			EntityRegistry.addSpawn(EntityZombieRunt.class, 3000, 4, 4, EnumCreatureType.MONSTER, Event.biomes);
			EntityRegistry.addSpawn(EntityZombieJumper.class, weightedProb -= progressDeduction, 4, 4, EnumCreatureType.MONSTER, Event.biomes);
			EntityRegistry.addSpawn(EntityZombiePyromaniac.class, weightedProb -= progressDeduction, 4, 4, EnumCreatureType.MONSTER, Event.biomes);
			EntityRegistry.addSpawn(EntityZombieBard.class, weightedProb -= progressDeduction, 4, 4, EnumCreatureType.MONSTER, Event.biomes);
			EntityRegistry.addSpawn(EntityZombieThief.class, weightedProb -= progressDeduction, 4, 4, EnumCreatureType.MONSTER, Event.biomes);
			EntityRegistry.addSpawn(EntityZombieRider.class, weightedProb -= progressDeduction, 4, 4, EnumCreatureType.MONSTER, Event.biomes);
			EntityRegistry.addSpawn(EntityZombieSummoner.class, weightedProb -= progressDeduction, 4, 4, EnumCreatureType.MONSTER, Event.biomes);
			EntityRegistry.addSpawn(EntityZombieClone.class, weightedProb -= progressDeduction, 4, 4, EnumCreatureType.MONSTER, Event.biomes);
			break;
		case 3:
			EntityRegistry.addSpawn(EntityZombieRunt.class, 3000, 4, 4, EnumCreatureType.MONSTER, Event.biomes);
			EntityRegistry.addSpawn(EntityZombieJumper.class, weightedProb -= progressDeduction, 4, 4, EnumCreatureType.MONSTER, Event.biomes);
			EntityRegistry.addSpawn(EntityZombiePyromaniac.class, weightedProb -= progressDeduction, 4, 4, EnumCreatureType.MONSTER, Event.biomes);
			EntityRegistry.addSpawn(EntityZombieBard.class, weightedProb -= progressDeduction, 4, 4, EnumCreatureType.MONSTER, Event.biomes);
			EntityRegistry.addSpawn(EntityZombieThief.class, weightedProb -= progressDeduction, 4, 4, EnumCreatureType.MONSTER, Event.biomes);
			EntityRegistry.addSpawn(EntityZombieRider.class, weightedProb -= progressDeduction, 4, 4, EnumCreatureType.MONSTER, Event.biomes);
			EntityRegistry.addSpawn(EntityZombieSummoner.class, weightedProb -= progressDeduction, 4, 4, EnumCreatureType.MONSTER, Event.biomes);
			EntityRegistry.addSpawn(EntityZombieClone.class, weightedProb -= progressDeduction, 4, 4, EnumCreatureType.MONSTER, Event.biomes);
			break;
		case 4:
			EntityRegistry.addSpawn(EntityZombieBossSpawner.class, 10000, 1, 1, EnumCreatureType.MONSTER, Event.biomes);
			break;
		}
	}

	@Override
	public void startEvent() 
	{ 
		MobEvents.proxy.getWorldData().currentEvent = Event.ZOMBIE_APOCALYPSE;
		super.startEvent();
		if (FMLCommonHandler.instance().getSide() == Side.SERVER) {
			Event.sendServerMessage(new TextComponentString("Did I hear something?").setStyle(new Style().setBold(true).setColor(this.enumColor).setItalic(true)));
			Event.playServerSound(SoundEvents.ENTITY_ZOMBIE_INFECT, 10f, 0f);	
		}
	}

	@Override
	public void stopEvent() 
	{
		super.stopEvent();
		if (FMLCommonHandler.instance().getSide() == Side.SERVER) {
			Event.sendServerMessage(new TextComponentTranslation(this.toString() + " has ended.").setStyle(new Style().setBold(true).setColor(this.enumColor)));
			Event.playServerSound(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, 0.2f, 2f);	
		}
	}

	@Override
	public String toString()
	{
		return "Zombie Apocalypse";
	}
}
