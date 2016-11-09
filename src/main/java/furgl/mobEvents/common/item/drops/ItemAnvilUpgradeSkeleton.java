package furgl.mobEvents.common.item.drops;

import java.util.ArrayList;

import net.minecraft.init.Items;

public class ItemAnvilUpgradeSkeleton extends ItemAnvilUpgrade
{	
	public ItemAnvilUpgradeSkeleton() {
		super();
		this.repairableItems.add(Items.BONE);
		this.repairableItems.add(Items.ARROW);
		this.repairableItemDurability.add(1);
		this.repairableItemDurability.add(2);
	}

	@Override
	public String getName() {
		return "Anvil Upgrade (Sk)";
	}

	@Override
	public int getColor() {
		return 0x8c8c8c;
	}

	@Override
	public float getRed() {
		return 0.5f;
	}

	@Override
	public float getGreen() {
		return 0.5f;
	}

	@Override
	public float getBlue() {
		return 0.5f;
	}

	@Override
	public ArrayList<String> droppedBy() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Skeleton Boss");
		return list;
	}
}
