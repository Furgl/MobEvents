package furgl.mobEvents.client;

import furgl.mobEvents.client.gui.book.GuiEventBook;
import furgl.mobEvents.client.gui.progressBar.GuiEventProgress;
import furgl.mobEvents.client.render.entity.RenderEventBossSummoner;
import furgl.mobEvents.client.render.entity.RenderEventSkeleton;
import furgl.mobEvents.client.render.entity.RenderEventZombie;
import furgl.mobEvents.client.render.entity.TileEntityBossLootRenderer;
import furgl.mobEvents.common.CommonProxy;
import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.block.ModBlocks;
import furgl.mobEvents.common.entity.EntityGuiPlayer;
import furgl.mobEvents.common.entity.SkeletalUprising.EntitySkeletonBard;
import furgl.mobEvents.common.entity.SkeletalUprising.EntitySkeletonClone;
import furgl.mobEvents.common.entity.SkeletalUprising.EntitySkeletonHorse;
import furgl.mobEvents.common.entity.SkeletalUprising.EntitySkeletonPyromaniac;
import furgl.mobEvents.common.entity.SkeletalUprising.EntitySkeletonRider;
import furgl.mobEvents.common.entity.SkeletalUprising.EntitySkeletonSoldier;
import furgl.mobEvents.common.entity.SkeletalUprising.EntitySkeletonThief;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityZombieBard;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityZombieClone;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityZombieHorse;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityZombieJumper;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityZombieMinion;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityZombiePyromaniac;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityZombieRider;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityZombieRunt;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityZombieSummoner;
import furgl.mobEvents.common.entity.ZombieApocalypse.EntityZombieThief;
import furgl.mobEvents.common.entity.boss.EntityBossZombie;
import furgl.mobEvents.common.entity.boss.spawner.EntityZombieBossSpawner;
import furgl.mobEvents.common.entity.projectile.EntityPyromaniacsArrow;
import furgl.mobEvents.common.event.EventFogEvent;
import furgl.mobEvents.common.event.RenderThievesMaskEvent;
import furgl.mobEvents.common.item.ModItems;
import furgl.mobEvents.common.item.drops.ItemDoubleJumpBoots;
import furgl.mobEvents.common.item.drops.ItemThievesMask;
import furgl.mobEvents.common.sound.SoundLoopedRecord;
import furgl.mobEvents.common.tileentity.TileEntityBossLoot;
import net.minecraft.block.Block;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelHorse;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.entity.RenderHorse;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RenderTippedArrow;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class ClientProxy extends CommonProxy {
	private SoundLoopedRecord bossSound;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		this.registerModelsAndVariants();
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		this.registerRenders();
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}

	private void registerRenders() {	
		registerEntityRenders();
		registerBlockRenders();
		ModItems.registerRenders();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBossLoot.class, new TileEntityBossLootRenderer());
		MinecraftForge.EVENT_BUS.register(new GuiEventProgress(Minecraft.getMinecraft()));
		MinecraftForge.EVENT_BUS.register(new EventFogEvent());
		MinecraftForge.EVENT_BUS.register(new RenderThievesMaskEvent());
	}

	@SuppressWarnings("deprecation")
	private void registerEntityRenders() {
		RenderingRegistry.registerEntityRenderingHandler(EntityGuiPlayer.class, new RenderPlayer(Minecraft.getMinecraft().getRenderManager()));
		//Zombie Apocalypse
		RenderingRegistry.registerEntityRenderingHandler(EntityZombieRunt.class, new RenderEventZombie(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityZombiePyromaniac.class, new RenderEventZombie(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityZombieRider.class, new RenderEventZombie(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityZombieHorse.class, new RenderHorse(Minecraft.getMinecraft().getRenderManager(), new ModelHorse(), 0.75f));
		RenderingRegistry.registerEntityRenderingHandler(EntityZombieBard.class, new RenderEventZombie(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityZombieClone.class, new RenderEventZombie(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityZombieSummoner.class, new RenderEventZombie(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityZombieMinion.class, new RenderEventZombie(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityZombieJumper.class, new RenderEventZombie(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityZombieThief.class, new RenderEventZombie(Minecraft.getMinecraft().getRenderManager()/*, new ModelZombieThief()*/));
		RenderingRegistry.registerEntityRenderingHandler(EntityBossZombie.class, new RenderEventZombie(Minecraft.getMinecraft().getRenderManager()));
		//Skeletal Uprising
		RenderingRegistry.registerEntityRenderingHandler(EntitySkeletonSoldier.class, new RenderEventSkeleton(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntitySkeletonBard.class, new RenderEventSkeleton(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntitySkeletonClone.class, new RenderEventSkeleton(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntitySkeletonPyromaniac.class, new RenderEventSkeleton(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntitySkeletonRider.class, new RenderEventSkeleton(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntitySkeletonHorse.class, new RenderHorse(Minecraft.getMinecraft().getRenderManager(), new ModelHorse(), 0.75f));
		RenderingRegistry.registerEntityRenderingHandler(EntitySkeletonThief.class, new RenderEventSkeleton(Minecraft.getMinecraft().getRenderManager()/*, new ModelSkeletonThief()*/));
		//Bosses
		RenderingRegistry.registerEntityRenderingHandler(EntityZombieBossSpawner.class, new RenderEventBossSummoner(Minecraft.getMinecraft().getRenderManager(), null, 0));
		//other
		RenderingRegistry.registerEntityRenderingHandler(EntityPyromaniacsArrow.class, new RenderTippedArrow(Minecraft.getMinecraft().getRenderManager()));
	}

	private void registerModelsAndVariants() {
		ModelLoader.setCustomStateMapper(ModBlocks.bardsJukebox, (new StateMap.Builder()).ignore(new IProperty[] {BlockJukebox.HAS_RECORD}).build());
		ModelBakery.registerItemVariants(Item.getItemFromBlock(ModBlocks.upgradedAnvil), new ResourceLocation(MobEvents.MODID+":upgraded_anvil"), new ResourceLocation(MobEvents.MODID+":upgraded_anvil_slightly_damaged"), new ResourceLocation(MobEvents.MODID+":upgraded_anvil_very_damaged"));
	}

	private void registerBlockRenders() {
		registerRender(ModBlocks.summonersHelm);
		registerRender(ModBlocks.litSummonersHelm);
		registerRender(ModBlocks.bardsJukebox);
		registerRender(ModBlocks.upgradedAnvil);
		registerRender(ModBlocks.upgradedAnvil, 1, ModBlocks.upgradedAnvil.getUnlocalizedName().substring(5)+"_slightly_damaged");
		registerRender(ModBlocks.upgradedAnvil, 2, ModBlocks.upgradedAnvil.getUnlocalizedName().substring(5)+"_very_damaged");

		registerRender(ModBlocks.bossLoot);
	}

	private static void registerRender(Block block) {	
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), 0, new ModelResourceLocation(MobEvents.MODID+":" + block.getUnlocalizedName().substring(5), "inventory"));
	}

	private static void registerRender(Block block, int meta, String unlocalizedName) {	
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), meta, new ModelResourceLocation(MobEvents.MODID+":" + unlocalizedName, "inventory"));
	}

	@Override
	public void openBookGui(EntityPlayer player, boolean creative) { 
		Minecraft.getMinecraft().displayGuiScreen(new GuiEventBook(player, creative));
	}

	@Override
	public void playSoundJukebox(SoundEvent sound, World world, BlockPos pos, float volume) { 
		Minecraft.getMinecraft().getSoundHandler().playSound(new SoundLoopedRecord(sound, world, pos, volume));
	}

	@Override
	public void playSoundEntity(SoundEvent sound, Entity entity, float volume) { 
		Minecraft.getMinecraft().getSoundHandler().playSound(new SoundLoopedRecord(sound, entity, volume, false));
	}

	@Override
	public void startBossRecord(SoundEvent sound, Entity entity, float volume) { 
		this.bossSound = new SoundLoopedRecord(sound, entity, volume, false);
		Minecraft.getMinecraft().getSoundHandler().playSound(this.bossSound);
	}

	@Override
	public void stopBossRecord() { 
		if (this.bossSound != null)
		{
			this.bossSound.donePlaying = true;
			this.bossSound = null;
		}
	}

	@Override
	public void stopSounds() {
		Minecraft.getMinecraft().getSoundHandler().stopSounds();
	}

	@Override
	public void doubleJumpBootsTick(EntityPlayer player, ItemDoubleJumpBoots boots) { 
		if (!(player instanceof EntityPlayerSP))
			return;
		if (!boots.jumped && !player.isOnLadder() && !player.onGround && ((EntityPlayerSP)player).movementInput.jump && ((int)ReflectionHelper.getPrivateValue(EntityLivingBase.class, player, 63)) == 0) { //jumpTicks
			player.jump();
			boots.jumped = true;
			boots.jumpEffects(player);
		}
		if (player.onGround)
			boots.jumped = false;
	}

	@Override
	public void thievesMaskTick(EntityPlayer player, ItemThievesMask mask) { 
		if (!(player instanceof EntityGuiPlayer))
			return;
		if (((EntityGuiPlayer) player).book.displayTicks % 90 == 0)
			mask.isSneaking = !mask.isSneaking;
		if (mask.isSneaking)
			player.setSneaking(true);
		else
			player.setSneaking(false);
	}
}