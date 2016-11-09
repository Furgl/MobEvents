package furgl.mobEvents.packets;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.Events.Event;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSetEvent implements IMessage
{
	protected String event;

	public PacketSetEvent() 
	{

	}

	public PacketSetEvent(String event) 
	{
		this.event = event;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.event = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		ByteBufUtils.writeUTF8String(buf, event);
	}

	public static class Handler implements IMessageHandler<PacketSetEvent, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSetEvent packet, final MessageContext ctx) 
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runnable() 
			{
				@Override
				public void run() 
				{
					if (MobEvents.proxy.getWorldData().currentEvent.getClass() != Event.class)
						MobEvents.proxy.getWorldData().currentEvent.stopEvent();
					if (Event.stringToEvent(packet.event).getClass() != Event.class)
						Event.stringToEvent(packet.event).startEvent();
				}
			});
			return null;
		}
	}
}