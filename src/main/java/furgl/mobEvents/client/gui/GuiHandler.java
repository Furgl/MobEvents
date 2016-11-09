package furgl.mobEvents.client.gui;

import furgl.mobEvents.client.gui.inventory.ContainerUpgradedRepair;
import furgl.mobEvents.client.gui.inventory.GuiBossLoot;
import furgl.mobEvents.common.inventory.ContainerBossLoot;
import furgl.mobEvents.common.tileentity.TileEntityUpgradedAnvil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
	@Override
	/**ID 0 = ContainerBossLoot, ID 1 = ContainerRepair*/
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) 
	{
		TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
		switch(ID)
		{
		case 0:
			return new ContainerBossLoot(player.inventory, (IInventory)tileEntity, player);
		case 1:
			return new ContainerUpgradedRepair(player.inventory, player.worldObj, new BlockPos(x, y, z), player);
		}
		return null;
	}

	@Override
	/**ID 0 = GuiBossLoot, ID 1 = GuiUpgradedRepair*/
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) 
	{
		TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
		switch(ID)
		{
		case 0:
			return new GuiBossLoot(player.inventory, (IInventory)tileEntity);
		case 1:
			return new GuiUpgradedRepair(player.inventory, world, (TileEntityUpgradedAnvil) tileEntity);
		}
		return null;
	}

}
