package furgl.mobEvents.packets;

import furgl.mobEvents.common.Events.Event;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSummonMob implements IMessage
{
	protected int event;
	protected int mob;

	public PacketSummonMob() 
	{

	}

	/**
	 * @param event position in Event.EVENTS[event] of event containing mob to spawn
	 * @param mob position in Event.EVENTS[event].get(mob) of mob to spawn
	 */
	public PacketSummonMob(int event, int mob) 
	{
		this.event = event;
		this.mob = mob;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.event = buf.readInt();
		this.mob = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(event);
		buf.writeInt(mob);
	}

	public static class Handler implements IMessageHandler<PacketSummonMob, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSummonMob packet, final MessageContext ctx) 
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runnable() 
			{
				@Override
				public void run() 
				{
					EntityPlayerMP player = ctx.getServerHandler().playerEntity;
					try 
					{//north -z = 2, west -x = 1, south +z = 0, east +x = 3
						int d = MathHelper.floor_double((double) (player.rotationYaw * 4.0F / 360) + 0.50) & 3;
						double x = player.posX;
						double z = player.posZ;
						switch (d)
						{
						case 0:
							z += 2;
							break;
						case 1:
							x -= 2;
							break;
						case 2:
							z -= 2;
							break;
						case 3:
							x += 2;
							break;
						}
						EntityLiving mob = (EntityLiving) Event.EVENTS[packet.event].mobs.get(packet.mob).getClass().getDeclaredConstructor(World.class).newInstance(player.worldObj);
						mob.setLocationAndAngles(x, player.posY, z, 0, 0);
						mob.onInitialSpawn(null, null);
						player.worldObj.spawnEntityInWorld(mob);
						player.addChatMessage(new ChatComponentTranslation("Summoned "+mob.getName()+".").setChatStyle(new ChatStyle().setItalic(true).setColor(EnumChatFormatting.DARK_PURPLE)));

					} 
					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}
			});
			return null;
		}
	}
}