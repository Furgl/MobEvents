package furgl.mobEvents.common;

import furgl.mobEvents.client.gui.GuiHandler;
import furgl.mobEvents.common.achievements.Achievements;
import furgl.mobEvents.common.block.ModBlocks;
import furgl.mobEvents.common.config.Config;
import furgl.mobEvents.common.entity.ModEntities;
import furgl.mobEvents.common.event.CancelFireOverlayEvent;
import furgl.mobEvents.common.event.DebugEvent;
import furgl.mobEvents.common.event.EventSetupEvent;
import furgl.mobEvents.common.event.KeepInvDuringBossEvent;
import furgl.mobEvents.common.event.LibrarianChatEvent;
import furgl.mobEvents.common.event.PlayerJoinedEvent;
import furgl.mobEvents.common.event.PreventBossLootExplosionEvent;
import furgl.mobEvents.common.event.PreventOtherMobsDuringEvent;
import furgl.mobEvents.common.item.ModItems;
import furgl.mobEvents.common.item.drops.ItemButchersCleaver0;
import furgl.mobEvents.common.item.drops.ItemDoubleJumpBoots;
import furgl.mobEvents.common.item.drops.ItemThievesMask;
import furgl.mobEvents.common.sound.ModSoundEvents;
import furgl.mobEvents.common.tileentity.ModTileEntities;
import furgl.mobEvents.packets.PacketGiveItem;
import furgl.mobEvents.packets.PacketSetCurrentPagesAndTabs;
import furgl.mobEvents.packets.PacketSetEvent;
import furgl.mobEvents.packets.PacketSetWave;
import furgl.mobEvents.packets.PacketSummonMob;
import furgl.mobEvents.packets.PacketWorldDataToClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.Achievement;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy {
	public void preInit(FMLPreInitializationEvent event) {
		this.registerPackets();
		ModSoundEvents.registerSounds();
		ModEntities.registerEntities();
		ModTileEntities.init();
		ModBlocks.init();
		ModItems.init();
		Achievements.init();
		Config.init(event.getSuggestedConfigurationFile());	
	}
	
	public void init(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(MobEvents.instance, new GuiHandler());
		this.registerAchievements();
		this.registerEventListeners();
	}
	
	public void postInit(FMLPostInitializationEvent event) {}
	
	private void registerEventListeners() {
		if (MobEvents.DEBUG)
			MinecraftForge.EVENT_BUS.register(new DebugEvent());
		MinecraftForge.EVENT_BUS.register(new Config());
		MinecraftForge.EVENT_BUS.register(new PreventOtherMobsDuringEvent());
		MinecraftForge.EVENT_BUS.register(new LibrarianChatEvent());
		MinecraftForge.EVENT_BUS.register(new EventSetupEvent());
		MinecraftForge.EVENT_BUS.register(new PlayerJoinedEvent());
		MinecraftForge.EVENT_BUS.register(new CancelFireOverlayEvent());
		MinecraftForge.EVENT_BUS.register(new PreventBossLootExplosionEvent());
		MinecraftForge.EVENT_BUS.register(new KeepInvDuringBossEvent());
		MinecraftForge.EVENT_BUS.register(new ItemButchersCleaver0(ModItems.butchersCleaverMaterial));
	}

	private void registerPackets() {
		int id = 0;
		MobEvents.network.registerMessage(PacketSetCurrentPagesAndTabs.Handler.class, PacketSetCurrentPagesAndTabs.class, id++, Side.SERVER);
		MobEvents.network.registerMessage(PacketSetEvent.Handler.class, PacketSetEvent.class, id++, Side.SERVER);
		MobEvents.network.registerMessage(PacketSetWave.Handler.class, PacketSetWave.class, id++, Side.SERVER);
		MobEvents.network.registerMessage(PacketSummonMob.Handler.class, PacketSummonMob.class, id++, Side.SERVER);
		MobEvents.network.registerMessage(PacketGiveItem.Handler.class, PacketGiveItem.class, id++, Side.SERVER);

		MobEvents.network.registerMessage(PacketWorldDataToClient.Handler.class, PacketWorldDataToClient.class, id++, Side.CLIENT);
	}

	private void registerAchievements() {
		AchievementPage.registerAchievementPage(new AchievementPage("Mob Events", (Achievement[]) Achievements.achievements.toArray(new Achievement[Achievements.achievements.size()])));

		for (int i=0; i<Achievements.achievements.size(); i++)
			Achievements.achievements.get(i).registerStat();
	}

	public void openBookGui(EntityPlayer player, boolean creative) {}

	public void playSoundJukebox(SoundEvent sound, World world, BlockPos pos, float volume) {}
	
	public void playSoundEntity(SoundEvent sound, Entity entity, float volume) {}

	public void startBossRecord(SoundEvent sound, Entity entity, float volume) {}
	
	public void stopBossRecord() {}
	
	public void stopSounds() {}

	public void doubleJumpBootsTick(EntityPlayer player, ItemDoubleJumpBoots boots) {}

	public void thievesMaskTick(EntityPlayer player, ItemThievesMask mask) {}
}