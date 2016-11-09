package furgl.mobEvents.packets;

import furgl.mobEvents.common.item.ModItems;
import furgl.mobEvents.common.item.drops.IEventItem;
import furgl.mobEvents.common.item.drops.ItemSummonersHelm;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
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
						stack.addEnchantment(Enchantments.FIRE_PROTECTION, 5);
					if (!player.inventory.addItemStackToInventory(stack))
						player.entityDropItem(stack, 0);
					player.addChatMessage(new TextComponentTranslation("Gave "+player.getName()+" one "+((IEventItem) stack.getItem()).getName()+".").setStyle(new Style().setItalic(true).setColor(TextFormatting.DARK_PURPLE)));
				}
			});
			return null;
		}
	}
}