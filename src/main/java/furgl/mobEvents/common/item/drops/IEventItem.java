package furgl.mobEvents.common.item.drops;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;

public interface IEventItem 
{
	public String getName();
	public int getColor();
	public float getRed();
	public float getGreen();
	public float getBlue();
	public ArrayList<String> droppedBy();
	public ItemStack getItemStack();
}
