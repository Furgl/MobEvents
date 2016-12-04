package furgl.mobEvents.common.Events;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.achievements.Achievements;
import furgl.mobEvents.common.entity.IEventMob;
import furgl.mobEvents.common.entity.bosses.spawner.EntityBossSpawner;
import furgl.mobEvents.common.event.EventSetupEvent;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.EntityRegistry;
/**
 * Rule of Thumb: 
 *    Never do ANYTHING on client side unless server tells you to (via readFromNBT in MobEventsWorldSavedData).
 * */
public class Event 
{	
	/**List of all mobs in event sorted by progressOnDeath*/
	public ArrayList<IEventMob> mobs = new ArrayList<IEventMob>();
	/**Sounds that play randomly during event*/
	public ArrayList<SoundEvent> sounds = new ArrayList<SoundEvent>();
	/**Jokes in first page of book*/
	public ArrayList<String> bookJokes = new ArrayList<String>();
	/**Number of waves; in first page of book*/
	public String bookWaves;
	/**Color of text displayed on progress bar*/
	public int color;
	/**Colors for fog*/
	public float red = 1.0f;
	public float green = 1.0f;
	public float blue = 1.0f;
	/**Color for chat messages*/
	public TextFormatting enumColor;
	public EntityBossSpawner boss;

	public static boolean bossDefeated;
	public static ArrayList<String> playerDeaths = new ArrayList<String>();
	public static Random rand = new Random();
	public static ArrayList<EntityPlayer> players;
	public static Biome[] biomes;
	static 
	{
		ArrayList<Biome> list = new ArrayList<Biome>();
		Iterator<Biome> itr = Biome.REGISTRY.iterator();
		while (itr.hasNext()) {
			Biome biome = itr.next();
			if (biome != Biomes.HELL && biome != Biomes.VOID)
				list.add(biome);
		}
		biomes = new Biome[list.size()];
		biomes = list.toArray(biomes);
	}
	//referenced manually - do NOT change order
	public static Event EVENT;
	public static ZombieApocalypse ZOMBIE_APOCALYPSE;
	public static SkeletalUprising SKELETAL_UPRISING;
	public static ChaoticTurmoil CHAOTIC_TURMOIL;
	public static ArrayList<Event> allEvents;
	public enum Occurs {
		NEVER, CREATIVE, DAY, NIGHT
	}
	public Occurs occurs = Occurs.NEVER;
	/**Used to delay updating event/wave until after world data is created*/
	private Event newEvent;
	/**Used to delay updating event/wave until after world data is created*/
	private int newWave;
	/**Used to delay updating event/wave until after world data is created*/
	private int newProgress;
	public static final int TIME_TILL_WAVE_1 = 200;

	public Event(World world)
	{
		this.setSounds();
		this.setMobs();
		this.setBookDescription();
	}

	public void setBookDescription() { }
	public void setSounds() { }
	public void setMobs() { }

	public void removeCustomSpawns() { 
		for (Event event : Event.allEvents)
			if (event.getClass() != ChaoticTurmoil.class)
				for (IEventMob mob : event.mobs)
					EntityRegistry.removeSpawn((Class<? extends EntityLiving>) mob.getClass(), EnumCreatureType.MONSTER, Event.biomes);		
	}

	public void onUpdate() {
		if (newEvent != null) {
			if (MobEvents.proxy.getWorldData().currentEvent != newEvent) {
				if (MobEvents.proxy.getWorldData().currentEvent != Event.EVENT)
					MobEvents.proxy.getWorldData().currentEvent.stopEvent();
				if (newEvent != Event.EVENT)
					newEvent.startEvent();
			}
			if (MobEvents.proxy.getWorldData().currentWave != newWave)
				MobEvents.proxy.getWorldData().currentEvent.startWave(newWave);
			MobEvents.proxy.getWorldData().progress = newProgress;
			newEvent = null;
		}
		if (MobEvents.proxy.world.getGameRules().getBoolean("doDaylightCycle")) {
			//Short event 
			if (MobEvents.proxy.getWorldData().currentEvent.getClass() != Event.class && MobEvents.proxy.getWorldData().eventLength == 0 && MobEvents.proxy.world.getTotalWorldTime() % 3 == 0)
				MobEvents.proxy.world.setWorldTime(MobEvents.proxy.world.getWorldTime() + 1);
			//Long event
			else if (MobEvents.proxy.getWorldData().currentEvent.getClass() != Event.class && MobEvents.proxy.getWorldData().eventLength == 2 && MobEvents.proxy.world.getTotalWorldTime() % 3 == 0)
				MobEvents.proxy.world.setWorldTime(MobEvents.proxy.world.getWorldTime() - 1);
		}
		if (!MobEvents.proxy.world.isRemote && MobEvents.proxy.getWorldData().currentEvent.boss != null && MobEvents.proxy.getWorldData().currentEvent.boss.isDead) {
			if (MobEvents.DEBUG)
				System.out.println("Event detected boss is dead and cleared boss variable");
			MobEvents.proxy.getWorldData().currentEvent.boss = null;
		}
		if (!MobEvents.proxy.world.isRemote && MobEvents.proxy.getWorldData().currentEvent.getClass() != Event.class && MobEvents.proxy.getWorldData().progressNeededForBoss == 0)
		{
			Event.updatePlayers();
			if (players.size() > 0)
			{
				if (MobEvents.DEBUG)
					System.out.println("Event detected progressNeededForBoss is 0 and recalculated it");
				MobEvents.proxy.getWorldData().progressNeededForBoss = 100 * players.size();
				this.startWave(MobEvents.proxy.getWorldData().currentWave); //prints current wave when server restarted
			}
		}
	}

	public void startWave(int wave)
	{
		if (MobEvents.DEBUG)
			System.out.println("current wave: "+MobEvents.proxy.getWorldData().currentWave+", starting wave: "+wave);
		if (MobEvents.proxy.getWorldData().currentEvent.getClass() == Event.class)
			return;
		EventSetupEvent.timeTillWave1 = wave == 0 ? TIME_TILL_WAVE_1 : 0;
		if (MobEvents.proxy.getWorldData().currentEvent == this)
		{
			this.removeCustomSpawns();
			if (wave < 4 && wave > 0)
				Event.sendServerMessage(new TextComponentTranslation("Wave "+wave).setStyle(new Style().setBold(true).setColor(this.enumColor).setItalic(true)));
			else if (wave == 4)
				Event.sendServerMessage(new TextComponentTranslation("Boss Wave").setStyle(new Style().setBold(true).setColor(TextFormatting.DARK_PURPLE).setItalic(true)));
		}
		switch (wave) {
		case 0:
			MobEvents.proxy.getWorldData().progress = 0;
			break;
		case 1:
			MobEvents.proxy.getWorldData().progress = 0;
			break;
		case 2:
			MobEvents.proxy.getWorldData().progress = MobEvents.proxy.getWorldData().progressNeededForBoss/3;
			break;
		case 3:
			MobEvents.proxy.getWorldData().progress = MobEvents.proxy.getWorldData().progressNeededForBoss/3*2;
			break;
		case 4:
			MobEvents.proxy.getWorldData().progress = MobEvents.proxy.getWorldData().progressNeededForBoss;
			break;
		}
		MobEvents.proxy.getWorldData().currentWave = wave;
		MobEvents.proxy.getWorldData().markDirty();
	}

	public static Event stringToEvent(String string) 
	{
		for (int i=0; i<Event.allEvents.size(); i++)
			if (allEvents.get(i).toString().equals(string))
				return allEvents.get(i);
		return Event.EVENT;
	}

	public void increaseProgress(int amount)
	{
		if (MobEvents.proxy.getWorldData().currentWave == 1 && MobEvents.proxy.getWorldData().progress + amount >= MobEvents.proxy.getWorldData().progressNeededForBoss/3)
			this.startWave(2);
		else if (MobEvents.proxy.getWorldData().currentWave == 2 && MobEvents.proxy.getWorldData().progress + amount >= (MobEvents.proxy.getWorldData().progressNeededForBoss/3*2))
			this.startWave(3);
		else if (MobEvents.proxy.getWorldData().currentWave == 3 && MobEvents.proxy.getWorldData().progress + amount >= MobEvents.proxy.getWorldData().progressNeededForBoss)
		{
			this.startWave(4);
			MobEvents.proxy.getWorldData().progress = MobEvents.proxy.getWorldData().progressNeededForBoss;
		}
		else if (MobEvents.proxy.getWorldData().currentWave != 4)
			MobEvents.proxy.getWorldData().progress += amount;
	}

	/**
	 * Updates list of online players
	 */
	public static void updatePlayers()
	{
		players = new ArrayList<EntityPlayer>();
		if (MobEvents.proxy.world.isRemote)
			return;
		for(int i = 0; i<MobEvents.proxy.world.playerEntities.size(); i++) 
			players.addAll(MobEvents.proxy.world.playerEntities);
	}

	/**
	 * Plays random sound near random player
	 * @param sounds 
	 */
	protected void playSound(ArrayList<SoundEvent> sounds)
	{
		Event.updatePlayers();
		if (players.size() > 0 && MobEvents.proxy.getWorldData().currentWave != 4) {
			EntityPlayer targetPlayer = players.get(rand.nextInt(players.size()));
			int distance = 10;
			if (sounds.size() > 0)
				MobEvents.proxy.world.playSound(targetPlayer.posX+rand.nextDouble()*distance, targetPlayer.posY+rand.nextDouble()*distance, targetPlayer.posZ+rand.nextDouble()*distance, sounds.get(rand.nextInt(sounds.size())), SoundCategory.AMBIENT, Event.rand.nextFloat(), Event.rand.nextFloat()+0.5F, true);
		}
	}

	/**
	 * Plays sound at start of event
	 */
	protected void playStartSound() { }

	public static void sendServerMessage(ITextComponent component) {
		updatePlayers();
		for (EntityPlayer player : players) 
			player.addChatMessage(component);
	}

	public static void playServerSound(SoundEvent sound, float volume, float pitch) {
		updatePlayers();
		for (EntityPlayer player : players) 
			player.playSound(sound, volume, pitch);
	}

	public String toString()
	{
		return "None";
	}

	/**Set conditions for event; i.e. day/night or weather*/
	public void setEventConditions()
	{
		if (MobEvents.proxy.world == null)
			return;
		for (Event event : Event.allEvents)
			if (event.occurs == Occurs.DAY && MobEvents.proxy.getWorldData().currentEvent.getClass() == event.getClass()/* && !MobEvents.proxy.world.isDaytime()*/)
				MobEvents.proxy.world.setWorldTime(23460);
		for (Event event : Event.allEvents)
			if (event.occurs == Occurs.NIGHT && MobEvents.proxy.getWorldData().currentEvent.getClass() == event.getClass()/* && MobEvents.proxy.world.isDaytime()*/)
				MobEvents.proxy.world.setWorldTime(12542);
	}

	public void startEvent() 
	{ 
		System.out.println("current event: "+MobEvents.proxy.getWorldData().currentEvent+", starting event: "+this);
		this.setEventConditions();
		Event.bossDefeated = false;
		Event.updatePlayers();
		//check if event should be unlocked
		for (EntityPlayer player : Event.players)
		{
			int index = MobEvents.proxy.getWorldData().getPlayerIndex(player.getDisplayNameString());
			if (MobEvents.proxy.getWorldData().currentEvent.getClass() != Event.class && !MobEvents.proxy.getWorldData().unlockedTabs.get(index).contains(MobEvents.proxy.getWorldData().currentEvent.toString()))
			{
				MobEvents.proxy.getWorldData().unlockedTabs.get(index).add(MobEvents.proxy.getWorldData().currentEvent.toString());
				MobEvents.proxy.getWorldData().currentPages.set(index, 0);
				for (int i=0; i<Event.allEvents.size(); i++) //iterate through events
				{ 
					if (Event.allEvents.get(i).toString().equals(MobEvents.proxy.getWorldData().currentEvent.toString()))
					{
						MobEvents.proxy.getWorldData().currentTabs.set(index, i+1);
						break;
					}
				}
				Event.displayUnlockMessage(player, "Unlocked information about the "+MobEvents.proxy.getWorldData().currentEvent.toString()+" event in the Event Book");
			}
		}
		MobEvents.proxy.getWorldData().markDirty();
		MobEvents.proxy.getWorldData().progressNeededForBoss = 90 * players.size();
		playerDeaths = new ArrayList<String>();
		this.startWave(0);
	}

	public void stopEvent() 
	{ 
		System.out.println("current event: "+MobEvents.proxy.getWorldData().currentEvent+", stopping event: "+this);
		if (MobEvents.proxy.getWorldData().currentEvent.getClass() == Event.class)
			return;
		this.boss = null;
		EventSetupEvent.timeTillWave1 = 0;
		Event.updatePlayers();
		if (bossDefeated)
		{
			for (EntityPlayer player : players)
			{
				if (!playerDeaths.contains(player.getDisplayNameString()))
					player.addStat(Achievements.achievementExpert);
				player.addStat(Achievements.achievementThatWasEasy);
			}
		}
		else
		{
			for (EntityPlayer player : players)
			{
				if (!playerDeaths.contains(player.getDisplayNameString()))
					player.addStat(Achievements.achievementISurvived);
				player.addStat(Achievements.achievementItsFinallyOver);
			}
		}
		MobEvents.proxy.getWorldData().currentEvent = Event.EVENT;
		MobEvents.proxy.getWorldData().currentWave = 0;
		this.removeCustomSpawns();
		MobEvents.proxy.getWorldData().markDirty();
	}

	public static void displayUnlockMessage(EntityPlayer player, String message) {
		player.addChatMessage(new TextComponentTranslation(message).setStyle(new Style().setItalic(true).setColor(TextFormatting.DARK_GRAY)));
	}

	/**Used to delay updating event/wave until after world data is created
	 * @param progress */
	public void markForUpdate(Event newEvent, int newWave, int newProgress) {
		this.newEvent = newEvent;
		this.newWave = newWave;
		this.newProgress = newProgress;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Event && ((Event)obj).getClass() == this.getClass();
	}
}
