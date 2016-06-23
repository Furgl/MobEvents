package furgl.mobEvents.common.tileentity;

import furgl.mobEvents.client.renderer.entity.TileEntityBossLootRenderer;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModTileEntities 
{
	public static void init()
	{
		GameRegistry.registerTileEntity(TileEntitySummonersHelm.class, "tile_summoners_helm");
		
		GameRegistry.registerTileEntity(TileEntityBossLoot.class, "tile_boss_loot");
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBossLoot.class, new TileEntityBossLootRenderer());
	}
}
