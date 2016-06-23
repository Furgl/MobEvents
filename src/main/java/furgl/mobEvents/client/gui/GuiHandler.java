package furgl.mobEvents.client.gui;

import furgl.mobEvents.client.gui.inventory.GuiBossLoot;
import furgl.mobEvents.common.inventory.ContainerBossLoot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) 
	{
		TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

		if (tileEntity != null)
		{
			if (ID == 0)
				return new ContainerBossLoot(player.inventory, (IInventory)tileEntity, player);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) 
	{
		TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

		if (tileEntity != null)
		{
			if (ID == 0)
				return new GuiBossLoot(player.inventory, (IInventory)tileEntity);
		}
		return null;
	}

}
