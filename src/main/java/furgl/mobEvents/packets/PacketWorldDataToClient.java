package furgl.mobEvents.packets;

import furgl.mobEvents.common.world.WorldData;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketWorldDataToClient implements IMessage
{
	protected NBTTagCompound nbt;

	public PacketWorldDataToClient() 
	{

	}

	public PacketWorldDataToClient(NBTTagCompound nbt) 
	{
		this.nbt = nbt;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		ByteBufUtils.writeTag(buf, nbt);
	}

	public static class Handler implements IMessageHandler<PacketWorldDataToClient, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketWorldDataToClient packet, final MessageContext ctx) 
		{
			IThreadListener mainThread = Minecraft.getMinecraft();
			mainThread.addScheduledTask(new Runnable() 
			{
				@Override
				public void run() 
				{
					WorldData data = WorldData.get(Minecraft.getMinecraft().theWorld);
					data.readFromNBT(packet.nbt);
				}
			});
			return null;
		}
	}
}