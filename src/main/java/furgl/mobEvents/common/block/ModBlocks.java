package furgl.mobEvents.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModBlocks 
{
	//public static Block disappearingWeb;
	public static Block disappearingFire;

	public static void init()
	{
		disappearingFire = registerBlockWithoutTab(new BlockDisappearingFire().setHardness(0.0F).setLightLevel(1.0F).setStepSound(new Block.SoundType("cloth", 1.0F, 1.0F)).setUnlocalizedName("fire")/*.disableStats()*/, "fire");
		ModelLoader.setCustomStateMapper(disappearingFire, (new StateMap.Builder()).ignore(new IProperty[] {BlockDisappearingFire.AGE}).build());
		//disappearingWeb = registerBlockWithoutTab(new BlockDisappearingWeb().setLightOpacity(1).setHardness(4.0F), "disappearingWeb");
	}

	public static void registerRenders() 
	{
		registerRender(disappearingFire);
	}

	/*public static Block registerBlockWithTab(final Block block, final String unlocalizedName) {
		block.setUnlocalizedName(unlocalizedName);
		block.setCreativeTab(BabyMobs.tab);
		GameRegistry.registerBlock(block, unlocalizedName);
		return block;
	}*/

	public static Block registerBlockWithoutTab(final Block block, final String unlocalizedName) {
		block.setUnlocalizedName(unlocalizedName);
		GameRegistry.registerBlock(block, null, unlocalizedName);
		return block;
	}

	public static void registerRender(Block block)
	{	
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register((ItemBlock)null, 0, new ModelResourceLocation("mobEvents:" + block.getUnlocalizedName().substring(5), "inventory"));
	}
}
