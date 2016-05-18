package furgl.mobEvents.common.entity.ZombieApocalypse;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;

public interface IEventMob 
{
	public int getProgressOnDeath();
	public String getBookDescription();
	public ArrayList<ItemStack> getBookDrops();
	public int getBookArmor();
}
