package furgl.mobEvents.packets;

import furgl.mobEvents.common.world.WorldData;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSetWave implements IMessage
{
	protected int wave;

	public PacketSetWave() 
	{

	}

	public PacketSetWave(int wave) 
	{
		this.wave = wave;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.wave = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(wave);
	}

	public static class Handler implements IMessageHandler<PacketSetWave, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSetWave packet, final MessageContext ctx) 
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runnable() 
			{
				@Override
				public void run() 
				{
					WorldData data = WorldData.get(ctx.getServerHandler().playerEntity.worldObj);
					data.currentEvent.startWave(packet.wave);
				}
			});
			return null;
		}
	}
}