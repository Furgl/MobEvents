package furgl.mobEvents.common.Events;

import java.util.ArrayList;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.entity.IEventMob;
import furgl.mobEvents.common.entity.SkeletalUprising.EntitySkeletonBard;
import furgl.mobEvents.common.entity.SkeletalUprising.EntitySkeletonClone;
import furgl.mobEvents.common.entity.SkeletalUprising.EntitySkeletonPyromaniac;
import furgl.mobEvents.common.entity.SkeletalUprising.EntitySkeletonSoldier;
import furgl.mobEvents.common.sound.ModSoundEvents;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class SkeletalUprising extends Event
{
	public SkeletalUprising(World world) 
	{	
		super(world);
		this.occurs = Occurs.NIGHT;
		this.color = 0xcccccc;
		this.red = 0.9f;
		this.green = 0.9f;
		this.blue = 0.9f;
		this.enumColor = TextFormatting.GRAY;
	}

	@Override
	public void setBookDescription()
	{
		this.bookJokes = new ArrayList<String>();
		this.bookJokes.add("Man, these jokes aren't even that humerus.");	
		this.bookJokes.add("A dog stole a skeleton's left leg and left arm the other day. But it's cool he's ALL RIGHT now!");
		this.bookJokes.add("What's a skeleton's favorite weapon? A bow and MARROW!");
		this.bookJokes.add("What do skeletons say when they're in danger? \"WE'RE BONED!\"");
		this.bookJokes.add("How do French skeletons greet each other? BONE-jour!");
		this.bookJokes.add("What do skeletons call their homies? Vertebruhs. Because they always have their backs.");
		this.bookWaves = "3 + Boss";
	}

	@Override
	public void setSounds()
	{
		sounds = new ArrayList<SoundEvent>();
		sounds.add(ModSoundEvents.ambience_zombie_ambience);//TODO
	}

	@Override
	public void setMobs()
	{
		mobs = new ArrayList<IEventMob>();
		ArrayList<IEventMob> tmp = new ArrayList<IEventMob>();
		tmp.add(new EntitySkeletonSoldier(MobEvents.proxy.world));//tmp.add(new EntityRuntZombie(null));
		tmp.add(new EntitySkeletonBard(MobEvents.proxy.world));//tmp.add(new EntityBardZombie(null));
		tmp.add(new EntitySkeletonClone(MobEvents.proxy.world));//tmp.add(new EntityCloneZombie(null));
		//tmp.add(new EntityMinionZombie(null));
		tmp.add(new EntitySkeletonPyromaniac(MobEvents.proxy.world));//tmp.add(new EntityPyromaniacZombie(null));
		//tmp.add(new EntityRiderZombie(null));
		//tmp.add(new EntitySummonerZombie(null));
		//tmp.add(new EntityJumperZombie(null));
		//tmp.add(new EntityThiefZombie(null));
		//tmp.add(new EntityZombieBossSpawner(null));
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

	public void onUpdate()
	{
		if (rand.nextInt(200) == 0)
			this.playSound(sounds);
		super.onUpdate();
	}

	@Override
	protected void playStartSound()
	{
		Event.playServerSound(SoundEvents.ENTITY_SKELETON_AMBIENT, 0.5f, 1.5f); //TODO change to custom say
	}

	@Override
	public void startWave(int wave) {
		super.startWave(wave);

		switch (wave)
		{
		case 1:
			this.playStartSound();
			EntityRegistry.addSpawn(EntitySkeletonSoldier.class, 3000, 4, 4, EnumCreatureType.MONSTER, Event.biomes);
			break;
		case 2:
			EntityRegistry.addSpawn(EntitySkeletonSoldier.class, 3000, 4, 4, EnumCreatureType.MONSTER, Event.biomes);
			break;
		case 3:
			EntityRegistry.addSpawn(EntitySkeletonSoldier.class, 3000, 4, 4, EnumCreatureType.MONSTER, Event.biomes);
			break;
		case 4:
			break;
		}
	}

	public void startEvent() 
	{ 
		MobEvents.proxy.getWorldData().currentEvent = Event.SKELETAL_UPRISING;
		super.startEvent();
		if (!MobEvents.proxy.world.isRemote) {
			Event.sendServerMessage(new TextComponentTranslation("My bones are rattling.").setStyle(new Style().setBold(true).setColor(this.enumColor).setItalic(true)));
			Event.playServerSound(SoundEvents.ENTITY_ZOMBIE_INFECT, 10f, 0f);	
		}
	}

	public void stopEvent() 
	{
		super.stopEvent();
		if (!MobEvents.proxy.world.isRemote) {
			Event.sendServerMessage(new TextComponentTranslation(this.toString() + " has ended.").setStyle(new Style().setBold(true).setColor(this.enumColor)));
			Event.playServerSound(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, 0.2f, 2f);	
		}
	}

	public String toString()
	{
		return "Skeletal Uprising";
	}
}
