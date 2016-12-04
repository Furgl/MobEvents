package furgl.mobEvents.common;

import furgl.mobEvents.client.gui.GuiHandler;
import furgl.mobEvents.client.gui.creativeTab.ItemsTab;
import furgl.mobEvents.client.gui.creativeTab.MobsTab;
import furgl.mobEvents.common.achievements.Achievements;
import furgl.mobEvents.common.block.ModBlocks;
import furgl.mobEvents.common.commands.CommandMobEvents;
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
import furgl.mobEvents.common.sound.ModSoundEvents;
import furgl.mobEvents.common.tileentity.ModTileEntities;
import furgl.mobEvents.packets.PacketGiveItem;
import furgl.mobEvents.packets.PacketSetCurrentPagesAndTabs;
import furgl.mobEvents.packets.PacketSetEvent;
import furgl.mobEvents.packets.PacketSetWave;
import furgl.mobEvents.packets.PacketSummonMob;
import furgl.mobEvents.packets.PacketWorldDataToClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = MobEvents.MODID, name = MobEvents.MODNAME, version = MobEvents.VERSION, guiFactory = "furgl.mobEvents.client.gui.config.MobEventsGuiFactory")
public class MobEvents
{
	public static final String MODID = "mobevents";
	public static final String MODNAME = "Mob Events";
	public static final String VERSION = "1.0";
	public static final boolean DEBUG = true;
	public static final ItemsTab itemsTab = new ItemsTab("tabMobEventItems");
	public static final MobsTab mobsTab = new MobsTab("tabMobEventMobs");
	@Mod.Instance(MODID)
	public static MobEvents instance;
	@SidedProxy(clientSide = "furgl.mobEvents.client.ClientProxy", serverSide = "furgl.mobEvents.common.CommonProxy")
	public static CommonProxy proxy;
	public static SimpleNetworkWrapper network;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		network = NetworkRegistry.INSTANCE.newSimpleChannel("mobEventsChannel");
		registerPackets();
		ModSoundEvents.registerSounds();
		ModEntities.registerEntities();
		ModTileEntities.init();
		ModBlocks.init();
		ModItems.init();
		proxy.registerModelsAndVariants();
		Achievements.init();
		Config.init(event.getSuggestedConfigurationFile());		
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
		proxy.registerRenders();
		proxy.registerAchievements();
		registerEventListeners();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) { }

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandMobEvents());
	}

	private void registerEventListeners() 
	{
		if (DEBUG)
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

	private void registerPackets()
	{
		int id = 0;
		network.registerMessage(PacketSetCurrentPagesAndTabs.Handler.class, PacketSetCurrentPagesAndTabs.class, id++, Side.SERVER);
		network.registerMessage(PacketSetEvent.Handler.class, PacketSetEvent.class, id++, Side.SERVER);
		network.registerMessage(PacketSetWave.Handler.class, PacketSetWave.class, id++, Side.SERVER);
		network.registerMessage(PacketSummonMob.Handler.class, PacketSummonMob.class, id++, Side.SERVER);
		network.registerMessage(PacketGiveItem.Handler.class, PacketGiveItem.class, id++, Side.SERVER);

		network.registerMessage(PacketWorldDataToClient.Handler.class, PacketWorldDataToClient.class, id++, Side.CLIENT);
	}
}
