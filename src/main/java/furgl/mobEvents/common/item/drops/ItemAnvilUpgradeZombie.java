package furgl.mobEvents.common.item.drops;

import java.util.ArrayList;

import net.minecraft.init.Items;

public class ItemAnvilUpgradeZombie extends ItemAnvilUpgrade
{	
	public ItemAnvilUpgradeZombie() {
		super();
		this.repairableItems.add(Items.ROTTEN_FLESH);
		this.repairableItemDurability.add(1);
	}

	@Override
	public String getName() {
		return "Anvil Upgrade (Z)";
	}

	@Override
	public int getColor() {
		return 0x336600;
	}

	@Override
	public float getRed() {
		return 0.3f;
	}

	@Override
	public float getGreen() {
		return 0.5f;
	}

	@Override
	public float getBlue() {
		return 0.2f;
	}

	@Override
	public ArrayList<String> droppedBy() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Zombie Boss");
		return list;
	}
}
