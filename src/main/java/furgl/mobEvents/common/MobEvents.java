package furgl.mobEvents.common;

import furgl.mobEvents.client.commands.CommandMobEvents;
import furgl.mobEvents.client.gui.achievements.Achievements;
import furgl.mobEvents.client.gui.creativeTab.MobEventsCreativeTab;
import furgl.mobEvents.client.gui.progressBar.GuiEventProgress;
import furgl.mobEvents.common.block.ModBlocks;
import furgl.mobEvents.common.config.Config;
import furgl.mobEvents.common.entity.ModEntities;
import furgl.mobEvents.common.event.EventFogEvent;
import furgl.mobEvents.common.event.EventSetupEvent;
import furgl.mobEvents.common.event.FireExtinguishEvent;
import furgl.mobEvents.common.event.FirstJoinEvent;
import furgl.mobEvents.common.event.ParticleUpdateEvent;
import furgl.mobEvents.common.item.ModItems;
import furgl.mobEvents.packets.PacketSummonMob;
import net.minecraft.client.Minecraft;
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
	public static final String MODID = "mobEvents";
	public static final String MODNAME = "Mob Events";
	public static final String VERSION = "1.0";
	public static final MobEventsCreativeTab tab = new MobEventsCreativeTab("tabMobEvents");
	@Mod.Instance("mobEvents")
	public static MobEvents instance;
	@SidedProxy(clientSide = "furgl.mobEvents.client.ClientProxy", serverSide = "furgl.mobEvents.common.CommonProxy")
	public static CommonProxy proxy;
	public static SimpleNetworkWrapper network;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		network = NetworkRegistry.INSTANCE.newSimpleChannel("mobEventsChannel");
		network.registerMessage(PacketSummonMob.Handler.class, PacketSummonMob.class, 0, Side.SERVER);
		ModEntities.registerEntities();
		ModBlocks.init();
		ModItems.init();
		Achievements.init();
		Config.init(event.getSuggestedConfigurationFile());		
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
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
	
	public void registerEventListeners() 
	{
		//MinecraftForge.EVENT_BUS.register(new MobEvents());
		MinecraftForge.EVENT_BUS.register(new GuiEventProgress(Minecraft.getMinecraft()));
		MinecraftForge.EVENT_BUS.register(new EventFogEvent());
		MinecraftForge.EVENT_BUS.register(new EventSetupEvent());
		MinecraftForge.EVENT_BUS.register(new ParticleUpdateEvent());
		MinecraftForge.EVENT_BUS.register(new FireExtinguishEvent());
		MinecraftForge.EVENT_BUS.register(new FirstJoinEvent());
	}
}
