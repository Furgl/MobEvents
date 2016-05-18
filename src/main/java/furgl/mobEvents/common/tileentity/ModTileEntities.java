package furgl.mobEvents.common.tileentity;

import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModTileEntities 
{
	public static void init()
	{
		GameRegistry.registerTileEntity(TileEntitySummonersHelm.class, "tile_summoners_helm");
	}
}
