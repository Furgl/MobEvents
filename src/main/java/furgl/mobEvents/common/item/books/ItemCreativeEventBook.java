package furgl.mobEvents.common.item.books;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.Events.Event;
import net.minecraft.command.CommandGameMode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
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
		return MobEvents.proxy.getWorldData().currentEvent.getClass() != Event.class;
	}

	@Override
	public ActionResult onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
	{
		if (player.worldObj.isRemote) {
			if (player.capabilities.isCreativeMode && player.canCommandSenderUseCommand(2, new CommandGameMode().getCommandName()))
				MobEvents.proxy.openBookGui(player, true);
			else
				player.addChatMessage(new TextComponentTranslation("You must be in creative mode and opped to use the Creative Event Book.").setStyle(new Style().setColor(TextFormatting.RED).setItalic(true)));
		}
		return new ActionResult(EnumActionResult.SUCCESS, stack);
	}
}
