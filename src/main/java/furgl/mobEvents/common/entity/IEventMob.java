package furgl.mobEvents.common.entity;

import java.util.ArrayList;

import furgl.mobEvents.common.Events.Event;
import net.minecraft.item.ItemStack;

public interface IEventMob 
{
	public Event getEvent();
	public int getProgressOnDeath();
	public String getBookDescription();
	public ArrayList<ItemStack> getBookDrops();
	public void doSpecialRender(int displayTicks);
}
