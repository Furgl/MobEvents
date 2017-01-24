package furgl.mobEvents.common.item.books;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.Events.Event;
import furgl.mobEvents.common.world.WorldData;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemEventBook extends Item 
{
	//used to ignore interact when book was just created
	public boolean justCreated;

	public ItemEventBook()
	{
		this.setMaxStackSize(1);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
    public boolean hasEffect(ItemStack stack)
    {
        return WorldData.get(Minecraft.getMinecraft().theWorld).currentEvent.getClass() != Event.class;
    }

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
	{
		if (this.justCreated)
			this.justCreated = false;
		else if (player.worldObj.isRemote)
			MobEvents.proxy.openBookGui(player, false);
		return new ActionResult(EnumActionResult.SUCCESS, stack);
	}
}
