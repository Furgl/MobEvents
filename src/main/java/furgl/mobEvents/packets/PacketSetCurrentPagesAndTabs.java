package furgl.mobEvents.packets;

import furgl.mobEvents.common.world.WorldData;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSetCurrentPagesAndTabs implements IMessage
{
	protected int currentPage;
	protected int currentTab;
	protected int currentCreativePage;
	protected int currentCreativeTab;

	public PacketSetCurrentPagesAndTabs() 
	{

	}

	public PacketSetCurrentPagesAndTabs(int currentPage, int currentTab, int currentCreativePage, int currentCreativeTab) 
	{
		this.currentPage = currentPage;
		this.currentTab = currentTab;
		this.currentCreativePage = currentCreativePage;
		this.currentCreativeTab = currentCreativeTab;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.currentPage = buf.readInt();
		this.currentTab = buf.readInt();
		this.currentCreativePage = buf.readInt();
		this.currentCreativeTab = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(this.currentPage);
		buf.writeInt(this.currentTab);
		buf.writeInt(this.currentCreativePage);
		buf.writeInt(this.currentCreativeTab);
	}

	public static class Handler implements IMessageHandler<PacketSetCurrentPagesAndTabs, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSetCurrentPagesAndTabs packet, final MessageContext ctx) 
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runnable() 
			{
				@Override
				public void run() 
				{
					WorldData data = WorldData.get(ctx.getServerHandler().playerEntity.worldObj);
					int index = data.getPlayerIndex(ctx.getServerHandler().playerEntity.getName());
					data.currentPages.set(index, packet.currentPage);
					data.currentTabs.set(index, packet.currentTab);
					data.currentCreativePages.set(index, packet.currentCreativePage);
					data.currentCreativeTabs.set(index, packet.currentCreativeTab);
					data.markDirty();
				}
			});
			return null;
		}
	}
}