package furgl.mobEvents.packets;

import furgl.mobEvents.common.Events.ChaoticTurmoil;
import furgl.mobEvents.common.Events.Event;
import furgl.mobEvents.common.entity.IEventMob;
import furgl.mobEvents.common.entity.boss.spawner.EntityBossSpawner;
import furgl.mobEvents.common.world.WorldData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
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
					WorldData data = WorldData.get(ctx.getServerHandler().playerEntity.worldObj);
					EntityPlayerMP player = ctx.getServerHandler().playerEntity;
					try 
					{//north -z = 2, west -x = 1, south +z = 0, east +x = 3
						int d = MathHelper.floor_double((double) (player.rotationYaw * 4.0F / 360) + 0.50) & 3;
						double x = player.posX;
						double z = player.posZ;
						switch (d)
						{
						case 0:
							z += 4;
							break;
						case 1:
							x -= 4;
							break;
						case 2:
							z -= 4;
							break;
						case 3:
							x += 4;
							break;
						}
						EntityLiving mob = (EntityLiving) Event.allEvents.get(packet.event).mobs.get(packet.mob).getClass().getDeclaredConstructor(World.class).newInstance(player.worldObj);
						if (mob instanceof EntityBossSpawner || ((IEventMob) mob).getEvent().getClass() == data.currentEvent.getClass() || data.currentEvent.getClass() == ChaoticTurmoil.class) {
							mob.setLocationAndAngles(x, player.posY, z, 0, 0);//needs to be done before initial spawn for boss spawners
							mob.onInitialSpawn(null, null);//needs to be done to get name
						}
						if (((IEventMob) mob).getEvent().getClass() == data.currentEvent.getClass() || data.currentEvent.getClass() == ChaoticTurmoil.class) {
							player.worldObj.spawnEntityInWorld(mob);
							player.addChatMessage(new TextComponentTranslation("Summoned "+mob.getName()+".").setStyle(new Style().setItalic(true).setColor(TextFormatting.DARK_PURPLE)));
						}
						else 
							player.addChatMessage(new TextComponentTranslation(mob.getName()+" must be summoned during "+((IEventMob) mob).getEvent()+".").setStyle(new Style().setItalic(true).setColor(TextFormatting.DARK_PURPLE)));
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