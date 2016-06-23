package furgl.mobEvents.common.item;

import java.util.ArrayList;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.item.books.ItemCreativeEventBook;
import furgl.mobEvents.common.item.books.ItemEventBook;
import furgl.mobEvents.common.item.drops.IEventItem;
import furgl.mobEvents.common.item.drops.ItemDoubleJumpBoots;
import furgl.mobEvents.common.item.drops.ItemSummonersHelm;
import furgl.mobEvents.common.item.drops.ItemThievesMask;
import furgl.mobEvents.common.item.records.ItemRecord1;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModItems 
{
	//books
	public static Item eventBook;
	public static Item creativeEventBook;
	//drops
	public static ArmorMaterial summonersHelmMaterial = EnumHelper.addArmorMaterial("NAME", MobEvents.MODID+":summoners_helm_off", -1, new int[]{2, 5, 3, 1} , 0);
	public static ArmorMaterial doubleJumpBootsMaterial = EnumHelper.addArmorMaterial("NAME", MobEvents.MODID+":double_jump_boots", -1, new int[]{2, 5, 3, 1} , 0);
	public static ArmorMaterial thievesMaskMaterial = EnumHelper.addArmorMaterial("NAME", MobEvents.MODID+":thieves_mask", -1, new int[]{2, 5, 3, 1} , 0);
	public static Item summonersHelm;
	public static Item doubleJumpBoots;
	public static Item thievesMask;
	public static ArrayList<IEventItem> drops;
	//records
	public static Item record1;
	public static Item record2;
	public static Item record3;
	public static Item record4;
	//eggs
	public static Item zombieBardEgg;
	public static Item zombieCloneEgg;
	public static Item zombieMinionEgg;
	public static Item zombiePyromaniacEgg;
	public static Item zombieRiderEgg;
	public static Item zombieRuntEgg;
	public static Item zombieSummonerEgg;
	public static Item zombieJumperEgg;
	public static Item zombieThiefEgg;
	//bosses
	public static Item zombieBossSpawnerEgg;
	
	private static ArrayList<ItemEventMobSpawnEgg> eggs;

	public static void init() 
	{
		drops = new ArrayList<IEventItem>();
		eggs = new ArrayList<ItemEventMobSpawnEgg>();
		
		eventBook = registerItemWithTab(new ItemEventBook(), "event_book");
		creativeEventBook = registerItemWithTab(new ItemCreativeEventBook(), "creative_event_book");
		
		record1 = registerItemWithTab(new ItemRecord1("record1"), "record1");
		record2 = registerItemWithTab(new ItemRecord1("record2"), "record2");
		record3 = registerItemWithTab(new ItemRecord1("record3"), "record3");
		record4 = registerItemWithTab(new ItemRecord1("record4"), "record4");
		
		summonersHelm = registerItemWithTab(new ItemSummonersHelm(summonersHelmMaterial, 0, 0), "summoners_helm");
		doubleJumpBoots = registerItemWithTab(new ItemDoubleJumpBoots(doubleJumpBootsMaterial, 0, 3), "double_jump_boots");
		thievesMask = registerItemWithTab(new ItemThievesMask(thievesMaskMaterial, 0, 0), "thieves_mask");

		zombieBardEgg = registerItemWithTab(new ItemEventMobSpawnEgg("mobEvents.bardZombie"), "zombie_bard");
		zombieCloneEgg = registerItemWithTab(new ItemEventMobSpawnEgg("mobEvents.cloneZombie"), "zombie_clone");
		zombieMinionEgg = registerItemWithTab(new ItemEventMobSpawnEgg("mobEvents.minionZombie"), "zombie_minion");
		zombiePyromaniacEgg = registerItemWithTab(new ItemEventMobSpawnEgg("mobEvents.pyromaniacZombie"), "zombie_pyromaniac");
		zombieRiderEgg = registerItemWithTab(new ItemEventMobSpawnEgg("mobEvents.riderZombie"), "zombie_rider");
		zombieRuntEgg = registerItemWithTab(new ItemEventMobSpawnEgg("mobEvents.runtZombie"), "zombie_runt");
		zombieSummonerEgg = registerItemWithTab(new ItemEventMobSpawnEgg("mobEvents.summonerZombie"), "zombie_summoner");
		zombieJumperEgg = registerItemWithTab(new ItemEventMobSpawnEgg("mobEvents.jumperZombie"), "zombie_jumper");
		zombieThiefEgg = registerItemWithTab(new ItemEventMobSpawnEgg("mobEvents.thiefZombie"), "zombie_thief");
		
		zombieBossSpawnerEgg = registerItemWithTab(new ItemEventMobSpawnEgg("mobEvents.bossZombieSpawner"), "zombie_boss_spawner");
	}

	public static void registerRenders()
	{
		registerRender(eventBook);
		registerRender(creativeEventBook);
		
		registerRender(record1);
		registerRender(record2);
		registerRender(record3);
		registerRender(record4);
		
		registerRender(summonersHelm);
		registerRender(doubleJumpBoots);
		registerRender(thievesMask);

		registerRender(zombieBardEgg);
		registerRender(zombieCloneEgg);
		registerRender(zombieMinionEgg);
		registerRender(zombiePyromaniacEgg);
		registerRender(zombieRiderEgg);
		registerRender(zombieRuntEgg);
		registerRender(zombieSummonerEgg);
		registerRender(zombieJumperEgg);
		registerRender(zombieThiefEgg);
		
		registerRender(zombieBossSpawnerEgg);
	}

	public static Item registerItemWithTab(final Item item, final String unlocalizedName) {
		item.setUnlocalizedName(unlocalizedName);
		item.setCreativeTab(MobEvents.tab);
		MobEvents.tab.orderedItems.add(item instanceof IEventItem ? ((IEventItem) item).getItemStack() : new ItemStack(item));
		GameRegistry.registerItem(item, unlocalizedName);
		if (item instanceof ItemEventMobSpawnEgg)
			eggs.add((ItemEventMobSpawnEgg) item);
		else if (item instanceof IEventItem)
			drops.add((IEventItem) item);
		return item;
	}

	public static Item registerItemWithoutTab(final Item item, final String unlocalizedName) {
		item.setUnlocalizedName(unlocalizedName);
		GameRegistry.registerItem(item, unlocalizedName);
		if (item instanceof ItemEventMobSpawnEgg)
			eggs.add((ItemEventMobSpawnEgg) item);
		else if (item instanceof IEventItem)
			drops.add((IEventItem) item);
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

