package furgl.mobEvents.packets;

import furgl.mobEvents.common.item.ModItems;
import furgl.mobEvents.common.item.drops.ItemSummonersHelm;
import io.netty.buffer.ByteBuf;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketGiveItem implements IMessage
{
	protected int item;

	public PacketGiveItem() 
	{

	}

	/**
	 * @param item Position in ModItems.drops.get(item)
	 */
	public PacketGiveItem(int item) 
	{
		this.item = item;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.item = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(item);
	}

	public static class Handler implements IMessageHandler<PacketGiveItem, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketGiveItem packet, final MessageContext ctx) 
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runnable() 
			{
				@Override
				public void run() 
				{
					EntityPlayerMP player = ctx.getServerHandler().playerEntity;
					ItemStack stack = new ItemStack((Item) ModItems.drops.get(packet.item));
					if (stack.getItem() instanceof ItemSummonersHelm)
						stack.addEnchantment(Enchantment.fireProtection, 5);
					if (!player.inventory.addItemStackToInventory(stack))
						player.entityDropItem(stack, 0);
					player.addChatMessage(new ChatComponentTranslation("Gave "+player.getName()+" one "+stack.getDisplayName()+".").setChatStyle(new ChatStyle().setItalic(true).setColor(EnumChatFormatting.DARK_PURPLE)));
				}
			});
			return null;
		}
	}
}