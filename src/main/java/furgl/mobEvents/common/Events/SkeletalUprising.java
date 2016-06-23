package furgl.mobEvents.common.Events;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

public class SkeletalUprising extends Event
{
	public SkeletalUprising() 
	{	
		this.color = 0xcccccc;
		this.red = 0.9f;
		this.green = 0.9f;
		this.blue = 0.9f;
		this.enumColor = EnumChatFormatting.GRAY;
		this.setBookDescription();
	}

	@Override
	public void setBookDescription()
	{
		this.bookJokes = new ArrayList<String>();
		this.bookJokes.add("Man, these jokes aren't even that humerus.");	
		this.bookJokes.add("A dog stole a skeleton's left leg and left arm the other day. But it's cool he's ALL RIGHT now!");
		this.bookJokes.add("What's a skeletons favorite weapon? A bow and MARROW!");
		this.bookJokes.add("What do skeletons say when they're in danger? \"WE'RE BONED!\"");
		this.bookJokes.add("How do French skeletons greet each other? BONE-jour!");
		this.bookJokes.add("What do skeletons call their homies? Vertebruhs. Because they always have their backs.");
		this.bookOccurs = "Night";
		this.bookWaves = "3 + Boss";
	}

	public void onUpdate()
	{
		if (rand.nextInt(500) == 0)
		{
			this.updatePlayers();
			//this.playSound(sounds); bc haven't done setSounds()
		}
		super.onUpdate();
	}
	
	@Override
	public void startWave(int wave) {
		super.startWave(wave);

		switch (wave)
		{
		case 1:
			this.updatePlayers();
			for (EntityPlayer player : players)
				Event.world.playSoundAtEntity(player, "mob.skeleton.say", 0.5f, 1.5f); //change to custom say
			break;
		case 2:
			break;
		case 3:
			break;
		case 4:
			break;
		}
	}


	public void increaseProgress(int amount)
	{
		if (progress > 10 && progress + amount >= 10)
			System.out.println("10 Kills!");
		Event.progress += amount;
	}

	public void startEvent() 
	{ 
		Event.currentEvent = new SkeletalUprising();
		super.startEvent();
		if (MinecraftServer.getServer().getConfigurationManager() != null)
			MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation("My bones are rattling.").setChatStyle(new ChatStyle().setBold(true).setColor(this.enumColor).setItalic(true)));
		this.updatePlayers();
		for (EntityPlayer player : players)
			Event.world.playSoundAtEntity(player, "mob.zombie.infect", 10f, 0f);
	}

	public void stopEvent() 
	{
		super.stopEvent();
		if (MinecraftServer.getServer().getConfigurationManager() != null)
			MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation(this.toString() + " has ended.").setChatStyle(new ChatStyle().setBold(true).setColor(this.enumColor)));
		this.updatePlayers();
		for (EntityPlayer player : players)
			Event.world.playSoundAtEntity(player, "mob.zombie.remedy", 0.2f, 2f);
	}

	public String toString()
	{
		return "Skeletal Uprising";
	}
}
