package furgl.mobEvents.common.Events;

import java.util.ArrayList;
import java.util.Random;

import furgl.mobEvents.client.gui.achievements.Achievements;
import furgl.mobEvents.common.config.Config;
import furgl.mobEvents.common.entity.ZombieApocalypse.IEventMob;
import furgl.mobEvents.common.event.EventFogEvent;
import furgl.mobEvents.common.event.EventSetupEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class Event 
{	
	/**List of all mobs in event sorted by progressOnDeath*/
	public ArrayList<IEventMob> mobs;
	/**Sounds that play randomly during event*/
	public ArrayList<String> sounds;
	/**Jokes in first page of book*/
	public ArrayList<String> bookJokes;
	/**Occurs during day/night; in first page of book*/
	public String bookOccurs;
	/**Number of waves; in first page of book*/
	public String bookWaves;
	/**Color of text displayed on progress bar*/
	public int color;
	/**Colors for fog*/
	public float red;
	public float green;
	public float blue;
	/**Color for chat messages*/
	public EnumChatFormatting enumColor;

	public static int progress;
	public static int progressNeededForBoss;
	public static int currentWave;
	protected static boolean bossDefeated;
	public static ArrayList<String> playerDeaths = new ArrayList<String>();
	public static World world;	//only server side
	public static Event currentEvent = new Event();
	public static Random rand = new Random();
	public static ArrayList<EntityPlayer> players;
	public static BiomeGenBase[] biomes;
	static 
	{
		ArrayList<BiomeGenBase> list = new ArrayList<BiomeGenBase>();
		for (BiomeGenBase biome : BiomeGenBase.getBiomeGenArray())
		{
			if (biome != null && !biome.isEqualTo(BiomeGenBase.hell) && !biome.isEqualTo(BiomeGenBase.sky))
				list.add(biome);
		}
		biomes = new BiomeGenBase[list.size()];
		biomes = list.toArray(biomes);
	}
	public static final Event EVENTS[] = new Event[] {
			new ZombieApocalypse(),
			new SkeletalUprising()
	};
	public static final Event NIGHTEVENTS[] = new Event[] {
			new ZombieApocalypse(),
			new SkeletalUprising()
	};
	public static final Event DAYEVENTS[] = new Event[] {

	};

	public void setBookDescription() { }
	public void setSounds() { }
	public void setMobs() { }
	public void onUpdate() { }
	
	public void wave1() { 
		Event.currentWave = 1;
		MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation("Wave "+Event.currentWave).setChatStyle(new ChatStyle().setBold(true).setColor(this.enumColor).setItalic(true)));
	}
	
	public void wave2() { 
		Event.currentWave = 2;
		MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation("Wave "+Event.currentWave).setChatStyle(new ChatStyle().setBold(true).setColor(this.enumColor).setItalic(true)));
	}
	
	public void wave3() { 
		Event.currentWave = 3;
		MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation("Wave "+Event.currentWave).setChatStyle(new ChatStyle().setBold(true).setColor(this.enumColor).setItalic(true)));
	}
	public void bossWave() { 
		Event.currentWave = 4;
		MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation("Boss Wave").setChatStyle(new ChatStyle().setBold(true).setColor(this.enumColor).setItalic(true)));
	}

	public static Event stringToEvent(String string) 
	{
		for (int i=0; i<Event.EVENTS.length; i++)
			if (EVENTS[i].toString().equals(string))
				return EVENTS[i];
		return new Event();
	}

	public void increaseProgress(int amount)
	{
		if (currentWave == 1 && progress + amount >= progressNeededForBoss/3)
			wave2();
		else if (currentWave == 2 && progress + amount >= (progressNeededForBoss/3*2))
			wave3();
		else if (currentWave == 3 && progress + amount >= progressNeededForBoss)
		{
			bossWave();
			progress = progressNeededForBoss;
		}
		else if (currentWave != 4)
			progress += amount;
	}

	/**
	 * Updates list of online players
	 */
	protected void updatePlayers()
	{
		players = new ArrayList<EntityPlayer>();
		for(int i = 0; i<MinecraftServer.getServer().worldServers.length; i++) 
			players.addAll(MinecraftServer.getServer().worldServers[i].playerEntities);
	}

	/**
	 * Plays random sound near random player
	 * @param sounds 
	 */
	protected void playSound(ArrayList<String> sounds)
	{
		EntityPlayer targetPlayer = players.get(rand.nextInt(players.size()));
		int distance = 10;
		if (sounds.size() > 0)
			Event.world.playSoundEffect(targetPlayer.posX+rand.nextDouble()*distance, targetPlayer.posY+rand.nextDouble()*distance, targetPlayer.posZ+rand.nextDouble()*distance, sounds.get(rand.nextInt(sounds.size())), Event.rand.nextFloat(), Event.rand.nextFloat()+0.5F);
	}

	public String toString()
	{
		return "None";
	}

	public void startEvent() 
	{ 
		EventSetupEvent.timeTillWave1 = 200;
		this.updatePlayers();
		//check if event should be unlocked
		for (EntityPlayer player : Event.players)
		{
			Config.syncFromConfig(player);
			if (Event.currentEvent.getClass() != Event.class && !Config.unlockedTabs.contains(Event.currentEvent.toString()))
			{
				Config.unlockedTabs.add(Event.currentEvent.toString());
				Config.currentPage = 0;
				for (int i=0; i<Event.EVENTS.length; i++) //iterate through events
				{ 
					if (Event.EVENTS[i].toString().equals(Event.currentEvent.toString()))
					{
						Config.currentTab = i+1;
						break;
					}
				}
				Config.syncToConfig(player);
				player.addChatMessage(new ChatComponentTranslation("Unlocked information about the "+Event.currentEvent.toString()+" event in the Event Book").setChatStyle(new ChatStyle().setItalic(true).setColor(EnumChatFormatting.DARK_GRAY)));
			}
		}
		Event.progressNeededForBoss = 100 * players.size();
		playerDeaths = new ArrayList<String>();
		Event.progress = 0;
		Event.currentWave = 0;
	}

	public void stopEvent() 
	{
		EventSetupEvent.timeTillWave1 = 0;
		EventFogEvent.resetFogDensity = true;
		this.updatePlayers();
		if (bossDefeated)
		{
			for (EntityPlayer player : players)
			{
				if (!playerDeaths.contains(player.getDisplayNameString()))
					player.triggerAchievement(Achievements.achievementExpert);
				player.triggerAchievement(Achievements.achievementThatWasEasy);
			}
		}
		else
		{
			for (EntityPlayer player : players)
			{
				if (!playerDeaths.contains(player.getDisplayNameString()))
					player.triggerAchievement(Achievements.achievementISurvived);
				player.triggerAchievement(Achievements.achievementItsFinallyOver);
			}
		}
		bossDefeated = false;
		Event.currentEvent = new Event();
	}
}
