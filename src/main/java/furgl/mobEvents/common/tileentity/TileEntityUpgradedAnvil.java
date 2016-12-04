package furgl.mobEvents.common.tileentity;

import java.util.ArrayList;

import furgl.mobEvents.common.item.drops.ItemAnvilUpgrade;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityUpgradedAnvil extends TileEntity
{
	public ArrayList<ItemAnvilUpgrade> upgrades;

	public TileEntityUpgradedAnvil()
	{
		upgrades = new ArrayList<ItemAnvilUpgrade>();
	}
	
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() 
	{
		NBTTagCompound nbtTag = new NBTTagCompound();
		writeToNBT(nbtTag);
		return new SPacketUpdateTileEntity(this.pos, 0, nbtTag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) 
	{
		readFromNBT(packet.getNbtCompound());
	}

	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		
		upgrades = new ArrayList<ItemAnvilUpgrade>();
		int[] upgradeIds = compound.getIntArray("upgrades");
		for (int id : upgradeIds)
			upgrades.add((ItemAnvilUpgrade) Item.getItemById(id));
	}

	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);

		int[] upgradeIds = new int[upgrades.size()];
		for (int i=0; i<upgrades.size(); i++)
			upgradeIds[i] = Item.getIdFromItem(upgrades.get(i));
		compound.setIntArray("upgrades", upgradeIds);
		return compound;
	}
}
