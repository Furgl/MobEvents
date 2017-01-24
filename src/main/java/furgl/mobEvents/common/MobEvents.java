package furgl.mobEvents.common;

import furgl.mobEvents.client.gui.creativeTab.ItemsTab;
import furgl.mobEvents.client.gui.creativeTab.MobsTab;
import furgl.mobEvents.common.commands.CommandMobEvents;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = MobEvents.MODID, name = MobEvents.MODNAME, version = MobEvents.VERSION, guiFactory = "furgl.mobEvents.client.gui.config.MobEventsGuiFactory")
public class MobEvents { //TODO add update.json support
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
	public void preInit(FMLPreInitializationEvent event) {
		network = NetworkRegistry.INSTANCE.newSimpleChannel("mobEventsChannel");
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandMobEvents());
	}
}