package furgl.mobEvents.common.item;

import java.util.ArrayList;

import furgl.mobEvents.common.MobEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModItems 
{
	public static Item eventBook;
	public static Item creativeEventBook;

	public static Item zombieBardEgg;
	public static Item zombieCloneEgg;
	//public static Item zombieEnchanterEgg;
	public static Item zombieMinionEgg;
	public static Item zombiePyromaniacEgg;
	public static Item zombieRiderEgg;
	public static Item zombieRuntEgg;
	public static Item zombieSummonerEgg;
	
	private static ArrayList<ItemEventMobSpawnEgg> eggs;

	public static void init() 
	{
		eggs = new ArrayList<ItemEventMobSpawnEgg>();
		
		eventBook = registerItemWithTab(new ItemEventBook(), "event_book");
		creativeEventBook = registerItemWithTab(new ItemCreativeEventBook(), "creative_event_book");

		zombieBardEgg = registerItemWithTab(new ItemEventMobSpawnEgg("mobEvents.bardZombie"), "zombie_bard");
		zombieCloneEgg = registerItemWithTab(new ItemEventMobSpawnEgg("mobEvents.cloneZombie"), "zombie_clone");
		//zombieEnchanterEgg = registerItemWithTab(new ItemEventMobSpawnEgg("mobEvents.enchanterZombie"), "zombie_enchanter");
		zombieMinionEgg = registerItemWithTab(new ItemEventMobSpawnEgg("mobEvents.minionZombie"), "zombie_minion");
		zombiePyromaniacEgg = registerItemWithTab(new ItemEventMobSpawnEgg("mobEvents.pyromaniacZombie"), "zombie_pyromaniac");
		zombieRiderEgg = registerItemWithTab(new ItemEventMobSpawnEgg("mobEvents.riderZombie"), "zombie_rider");
		zombieRuntEgg = registerItemWithTab(new ItemEventMobSpawnEgg("mobEvents.runtZombie"), "zombie_runt");
		zombieSummonerEgg = registerItemWithTab(new ItemEventMobSpawnEgg("mobEvents.summonerZombie"), "zombie_summoner");
	}

	public static void registerRenders()
	{
		registerRender(eventBook);
		registerRender(creativeEventBook);

		registerRender(zombieBardEgg);
		registerRender(zombieCloneEgg);
		//registerRender(zombieEnchanterEgg);
		registerRender(zombieMinionEgg);
		registerRender(zombiePyromaniacEgg);
		registerRender(zombieRiderEgg);
		registerRender(zombieRuntEgg);
		registerRender(zombieSummonerEgg);
	}

	public static Item registerItemWithTab(final Item item, final String unlocalizedName) {
		item.setUnlocalizedName(unlocalizedName);
		item.setCreativeTab(MobEvents.tab);
		GameRegistry.registerItem(item, unlocalizedName);
		if (item instanceof ItemEventMobSpawnEgg)
			eggs.add((ItemEventMobSpawnEgg) item);
		return item;
	}

	public static Item registerItemWithoutTab(final Item item, final String unlocalizedName) {
		item.setUnlocalizedName(unlocalizedName);
		GameRegistry.registerItem(item, unlocalizedName);
		if (item instanceof ItemEventMobSpawnEgg)
			eggs.add((ItemEventMobSpawnEgg) item);
		return item;
	}
	
	public static ItemStack getSpawnEgg(EntityLiving mob)
	{
		String name = "mobevents."+mob.getClass().getSimpleName().substring(6);
		for (ItemEventMobSpawnEgg egg : eggs)
			if (egg.entityName.equalsIgnoreCase(name))
				return new ItemStack(egg);
		return null;
	}

	public static void registerRender(Item item)
	{	
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation("mobEvents:" + item.getUnlocalizedName().substring(5), "inventory"));
	}
}

