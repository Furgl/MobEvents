package furgl.mobEvents.common.block;

import furgl.mobEvents.common.MobEvents;
import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModBlocks 
{
	public static Block summonersHelm;
	public static Block litSummonersHelm;
	public static Block bardsJukebox;
	public static Block upgradedAnvil;

	public static Block bossLoot;

	public static void init()
	{
		summonersHelm = registerBlock(new BlockSummonersHelm(false).setHardness(1.0F), "summoners_helm", false);
		litSummonersHelm = registerBlock(new BlockSummonersHelm(true).setHardness(1.0F), "lit_summoners_helm", false);
		bardsJukebox = registerBlock(new BlockBardsJukebox().setHardness(2.0F).setResistance(10.0F), "bards_jukebox", true);
		upgradedAnvil = registerBlock(new BlockUpgradedAnvil().setHardness(5.0F).setResistance(2000.0F), "upgraded_anvil", true);
		
		bossLoot = registerBlock(new BlockBossLoot().setHardness(2.5F), "boss_loot", true);
	}	

	public static Block registerBlock(final Block block, final String unlocalizedName, boolean addToTab) {
		block.setUnlocalizedName(unlocalizedName);
		GameRegistry.register(block.setRegistryName(unlocalizedName));
		if (addToTab)
			block.setCreativeTab(MobEvents.itemsTab); 
		return block;
	}
}
