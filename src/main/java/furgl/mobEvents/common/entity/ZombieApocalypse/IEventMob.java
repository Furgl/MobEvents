package furgl.mobEvents.common.entity.ZombieApocalypse;

import java.util.ArrayList;

import net.minecraft.item.Item;

public interface IEventMob 
{
	public int getProgressOnDeath();
	public String getBookDescription();
	public ArrayList<Item> getBookDrops();
	public int getBookArmor();
}
