package furgl.mobEvents.client.commands;

import java.util.ArrayList;
import java.util.List;

import furgl.mobEvents.common.Events.Event;
import furgl.mobEvents.common.config.Config;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

public class CommandMobEvents implements ICommand 
{
	static ArrayList<String> events = new ArrayList<String>();

	static
	{
		for (int i=0; i<Event.EVENTS.length; i++)
			events.add(Event.EVENTS[i].toString().replace(" ", "_"));
		events.add("None");
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
		return "/MobEvents setEvent <Event>";//TODO
	}

	@Override
	public List<String> getCommandAliases() 
	{
		return new ArrayList<String>();
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException 
	{
		if (args.length == 2 && args[0].equals("setEvent"))
		{	
			Event event = Event.stringToEvent(args[1].replace("_", " "));
			if (event.getClass() != Event.class)
			{
				Event.currentEvent.stopEvent();
				event.startEvent();
			}
			else if (args[1].equalsIgnoreCase("none"))
				Event.currentEvent.stopEvent();				
		}
		else if (args.length == 2 && args[0].equals("moveGui"))
		{
			for (String location : Config.eventProgressGuiLocations)
				if (args[1].equalsIgnoreCase(location.replace(" ", "_")))
				{
					Config.eventProgressGuiLocation = location;
					Config.syncToConfig(null);
				}
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) 
	{
		/*String[] ops = MinecraftServer.getServer().getEventurationManager().getOppedPlayerNames();
		for (int i=0; i<ops.length; i++)
			if (ops[i].equals(sender.getName()))
				return true;
		return false;*///TODO
		return true;
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) 
	{
		if (args.length == 1)
		{
			ArrayList<String> list = new ArrayList<String>();
			list.add("setEvent");
			list.add("moveGui");
			return list;
		}
		else if (args.length == 2 && args[0].equalsIgnoreCase("setEvent"))
			return CommandMobEvents.events;
		else if (args.length == 2 && args[0].equalsIgnoreCase("moveGui"))
		{
			ArrayList<String> list = new ArrayList<String>();
			for (String location : Config.eventProgressGuiLocations)
				list.add(location.replace(" ", "_"));
			return list;
		}
		else
			return null;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) 
	{
		return false;
	}

}
