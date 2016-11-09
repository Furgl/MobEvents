package furgl.mobEvents.common.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.Events.Event;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class CommandMobEvents implements ICommand 
{
	private final static TextFormatting goodColor = TextFormatting.GREEN;
	private final static TextFormatting badColor = TextFormatting.RED;
	static ArrayList<String> commands = new ArrayList<String>();
	static
	{
		commands.add("setEvent");
		commands.add("setWave");
		commands.add("setChance");
		commands.add("setLength");
		commands.add("setKeepInventory");
	}
	static ArrayList<String> events = new ArrayList<String>();
	static
	{
		for (int i=0; i<Event.allEvents.size(); i++)
			events.add(Event.allEvents.get(i).toString().replace(" ", "_"));
		events.add("None");
	}
	static ArrayList<String> waves = new ArrayList<String>();
	static
	{
		for (int i=0; i<5; i++)
			waves.add(Integer.toString(i));
	}
	static ArrayList<String> chances = new ArrayList<String>();
	static
	{
		chances.add("0-100");
	}
	static ArrayList<String> lengths = new ArrayList<String>();
	static
	{
		lengths.add("Short");
		lengths.add("Normal");
		lengths.add("Long");
	}
	static ArrayList<String> keepInventory = new ArrayList<String>();
	static
	{
		keepInventory.add("Never");
		keepInventory.add("DuringBoss");
		keepInventory.add("DuringEvent");
	}

	@Override
	public int compareTo(ICommand o) 
	{
		return 0;
	}

	@Override
	public String getCommandName() 
	{
		return "MobEvents";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) 
	{
		return "";//TODO?
	}

	@Override
	public List<String> getCommandAliases() 
	{
		return new ArrayList<String>();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length == 2 && args[0].equalsIgnoreCase("setEvent"))
		{	
			Event event = Event.stringToEvent(args[1].replace("_", " "));
			if (event.getClass() != Event.class)
			{
				MobEvents.proxy.getWorldData().currentEvent.stopEvent();
				event.startEvent();
			}
			else if (args[1].equalsIgnoreCase("none"))
				MobEvents.proxy.getWorldData().currentEvent.stopEvent();				
		}
		else if (args.length == 2 && args[0].equalsIgnoreCase("setWave"))
		{
			if (NumberUtils.isNumber(args[1]) && Integer.valueOf(args[1]) >= 0 && Integer.valueOf(args[1]) <= 4 && MobEvents.proxy.getWorldData().currentEvent.getClass() != Event.class)
				MobEvents.proxy.getWorldData().currentEvent.startWave(Integer.valueOf(args[1]));
			else
				sender.addChatMessage(new TextComponentTranslation("Wave must be between 1 and 4 and can only be changed during an event.").setStyle(new Style().setColor(badColor)));
		}
		else if (args.length == 2 && args[0].equalsIgnoreCase("setChance"))
		{
			if (NumberUtils.isNumber(args[1]) && Integer.valueOf(args[1]) >= 0 && Integer.valueOf(args[1]) <= 100)
			{
				MobEvents.proxy.getWorldData().eventChance = Integer.valueOf(args[1]);
				MobEvents.proxy.getWorldData().markDirty();
				sender.addChatMessage(new TextComponentTranslation("Chance of Mob Events occurring now set to: "+Integer.valueOf(args[1])).setStyle(new Style().setColor(goodColor)));
			}
			else
				sender.addChatMessage(new TextComponentTranslation("Chance must be between 0 and 100.").setStyle(new Style().setColor(badColor)));
		}
		else if (args.length == 2 && args[0].equalsIgnoreCase("setLength"))
		{
			for (int i=0; i<lengths.size(); i++) {
				if (args[1].equalsIgnoreCase(lengths.get(i))) {
					MobEvents.proxy.getWorldData().eventLength = i;
					MobEvents.proxy.getWorldData().markDirty();
					sender.addChatMessage(new TextComponentTranslation("Length of Mob Events now set to: "+lengths.get(i)).setStyle(new Style().setColor(goodColor)));
				}
			}
		}
		else if (args.length == 2 && args[0].equalsIgnoreCase("setKeepInventory"))
		{
			for (int i=0; i<lengths.size(); i++) {
				if (args[1].equalsIgnoreCase(keepInventory.get(i))) {
					MobEvents.proxy.getWorldData().keepInventory = i;
					MobEvents.proxy.getWorldData().markDirty();
					sender.addChatMessage(new TextComponentTranslation("Keep Inventory Gamerule will be set to true: "+keepInventory.get(i)).setStyle(new Style().setColor(goodColor)));
				}
			}
		}
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) 
	{
		if (sender instanceof EntityPlayer)
			return server.getPlayerList().canSendCommands(((EntityPlayer) sender).getGameProfile());
		return false;
	}

	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) 
	{
		if (args.length == 1)
			return commands;
		else if (args.length == 2 && args[0].equalsIgnoreCase("setEvent"))
			return events;
		else if (args.length == 2 && args[0].equalsIgnoreCase("setWave"))
			return waves;
		else if (args.length == 2 && args[0].equalsIgnoreCase("setChance"))
			return chances;
		else if (args.length == 2 && args[0].equalsIgnoreCase("setLength"))
			return lengths;
		else if (args.length == 2 && args[0].equalsIgnoreCase("setKeepInventory"))
			return keepInventory;
		else
			return new ArrayList<String>();
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) 
	{
		return false;
	}
}
