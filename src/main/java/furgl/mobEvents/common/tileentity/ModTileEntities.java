package furgl.mobEvents.common.tileentity;

import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModTileEntities 
{
	public static void init()
	{
		GameRegistry.registerTileEntity(TileEntitySummonersHelm.class, "tile_summoners_helm");
		GameRegistry.registerTileEntity(TileEntityBossLoot.class, "tile_boss_loot");
		//moved to proxy - ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBossLoot.class, new TileEntityBossLootRenderer());
		GameRegistry.registerTileEntity(TileEntityUpgradedAnvil.class, "tile_upgraded_anvil");
	}
}
