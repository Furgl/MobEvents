package furgl.mobEvents.common.item;

import java.util.ArrayList;
import java.util.List;

import furgl.mobEvents.common.item.drops.ItemAnvilUpgrade;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAnvilBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemUpgradedAnvil extends ItemAnvilBlock 
{
	public ItemUpgradedAnvil(Block block)
	{
		super(block);
	}

	@Override
	public int getMetadata(int damage)
	{
		return damage << 2;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
	{
		tooltip.set(0, TextFormatting.AQUA+tooltip.get(0));

		NBTTagCompound nbt = new NBTTagCompound();
		stack.writeToNBT(nbt);
		if (!nbt.hasNoTags())
		{
			ArrayList<ItemAnvilUpgrade> upgrades = new ArrayList<ItemAnvilUpgrade>();
			nbt = nbt.getCompoundTag("tag");
			nbt = nbt.getCompoundTag("BlockEntityTag");
			int[] upgradeIds = nbt.getIntArray("upgrades");
			for (int id : upgradeIds)
				upgrades.add((ItemAnvilUpgrade) Item.getItemById(id));

			tooltip.add(TextFormatting.GOLD+"Upgrades:");
			for (ItemAnvilUpgrade upgrade : upgrades)
				tooltip.add(TextFormatting.GOLD+"* "+upgrade.getName());
		}
	}

	@Override
	public boolean updateItemStackNBT(NBTTagCompound nbt)
	{
		return true;
	}
}
