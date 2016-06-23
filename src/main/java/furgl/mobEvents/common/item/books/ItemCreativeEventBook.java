package furgl.mobEvents.common.item.books;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.Events.Event;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCreativeEventBook extends Item 
{
	public ItemCreativeEventBook()
	{
		this.setMaxStackSize(1);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasEffect(ItemStack stack)
	{
		return Event.currentEvent.getClass() != Event.class;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (player.worldObj.isRemote) {
			if (player.capabilities.isCreativeMode)
				MobEvents.proxy.openBookGui(player, true);
			else
				player.addChatMessage(new ChatComponentTranslation("You must be in creative mode to use the Creative Event Book.").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED).setItalic(true)));
		}
		return stack;
	}
}
