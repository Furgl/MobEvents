package furgl.mobEvents.common.item;

import java.util.ArrayList;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.block.ModBlocks;
import furgl.mobEvents.common.item.books.ItemCreativeEventBook;
import furgl.mobEvents.common.item.books.ItemEventBook;
import furgl.mobEvents.common.item.drops.IEventItem;
import furgl.mobEvents.common.item.drops.ItemAnvilUpgradeSkeleton;
import furgl.mobEvents.common.item.drops.ItemAnvilUpgradeZombie;
import furgl.mobEvents.common.item.drops.ItemBardsJukebox;
import furgl.mobEvents.common.item.drops.ItemBookOfHealing;
import furgl.mobEvents.common.item.drops.ItemButchersCleaver;
import furgl.mobEvents.common.item.drops.ItemDoubleJumpBoots;
import furgl.mobEvents.common.item.drops.ItemFireArrow;
import furgl.mobEvents.common.item.drops.ItemSummonersHelm;
import furgl.mobEvents.common.item.drops.ItemThievesMask;
import furgl.mobEvents.common.item.records.ItemCustomRecord;
import furgl.mobEvents.common.sound.ModSoundEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class ModItems 
{
	public static ArrayList<Item> allItems;
	//books
	public static Item eventBook;
	public static Item creativeEventBook;
	//drops
	public static ArrayList<IEventItem> drops;
	public static ArmorMaterial summonersHelmMaterial = EnumHelper.addArmorMaterial("NAME", MobEvents.MODID+":summoners_helm_off", -1, new int[]{2, 5, 3, 1}, 10, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 2);
	public static ArmorMaterial doubleJumpBootsMaterial = EnumHelper.addArmorMaterial("NAME", MobEvents.MODID+":double_jump_boots", -1, new int[]{2, 5, 3, 1}, 10, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 2);
	public static ArmorMaterial thievesMaskMaterial = EnumHelper.addArmorMaterial("NAME", MobEvents.MODID+":thieves_mask", -1, new int[]{2, 5, 3, 1}, 10, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 2);
	public static ToolMaterial butchersCleaverMaterial = EnumHelper.addToolMaterial("NAME", 3, 400, 8.0F, 6.5F, 10);
	public static Item summonersHelm;
	public static Item bardsJukebox;
	public static Item doubleJumpBoots;
	public static Item thievesMask;
	public static Item butchersCleaver;
	public static Item bookOfHealing;
	public static Item fireArrow;
	public static Item anvilUpgradeZombie;
	public static Item anvilUpgradeSkeleton;
	//records
	public static ArrayList<ItemRecord> records;
	public static Item recordZombieApocalypse;
	public static Item record2;
	public static Item record3;
	public static Item record4;
	//other
	public static Item upgradedAnvil;
	public static Item bossLoot;
	//spawn eggs
	private static ArrayList<ItemEventMobSpawnEgg> eggs;
	//Zombie Apocalypse
	public static Item zombieBardEgg;
	public static Item zombieCloneEgg;
	public static Item zombieMinionEgg;
	public static Item zombiePyromaniacEgg;
	public static Item zombieRiderEgg;
	public static Item zombieRuntEgg;
	public static Item zombieSummonerEgg;
	public static Item zombieJumperEgg;
	public static Item zombieThiefEgg;
	//Skeletal Uprising
	public static Item skeletonSoldierEgg;
	public static Item skeletonBardEgg;
	public static Item skeletonCloneEgg;
	public static Item skeletonPyromaniacEgg;
	//bosses
	public static Item zombieBossSpawnerEgg;

	public static void init() 
	{
		allItems = new ArrayList<Item>();
		drops = new ArrayList<IEventItem>();
		records = new ArrayList<ItemRecord>();
		eggs = new ArrayList<ItemEventMobSpawnEgg>();
		//books
		eventBook = registerItem(new ItemEventBook(), "event_book", true);
		creativeEventBook = registerItem(new ItemCreativeEventBook(), "creative_event_book", true);
		//records
		recordZombieApocalypse = registerItem(new ItemCustomRecord("record_zombie_apocalypse", ModSoundEvents.record_zombie_apocalypse), "record_zombie_apocalypse", true);
		record2 = registerItem(new ItemCustomRecord("record2", ModSoundEvents.record2), "record2", true);
		record3 = registerItem(new ItemCustomRecord("record3", ModSoundEvents.record3), "record3", true);
		record4 = registerItem(new ItemCustomRecord("record4", ModSoundEvents.record4), "record4", true);
		//drops
		summonersHelm = registerItem(new ItemSummonersHelm(summonersHelmMaterial, 0, EntityEquipmentSlot.HEAD), "summoners_helm", true);
		bardsJukebox = registerItem(new ItemBardsJukebox(ModBlocks.bardsJukebox), "bards_jukebox", true);
		doubleJumpBoots = registerItem(new ItemDoubleJumpBoots(doubleJumpBootsMaterial, 0, EntityEquipmentSlot.FEET), "double_jump_boots", true);
		thievesMask = registerItem(new ItemThievesMask(thievesMaskMaterial, 0, EntityEquipmentSlot.HEAD), "thieves_mask", true);
		butchersCleaver = registerItem(new ItemButchersCleaver(butchersCleaverMaterial), "butchers_cleaver", true);
		bookOfHealing = registerItem(new ItemBookOfHealing(), "book_of_healing", true);
		fireArrow = registerItem(new ItemFireArrow(), "fire_arrow", true);
		anvilUpgradeZombie = registerItem(new ItemAnvilUpgradeZombie(), "anvil_upgrade_zombie", true);
		anvilUpgradeSkeleton = registerItem(new ItemAnvilUpgradeSkeleton(), "anvil_upgrade_skeleton", true);
		//other
		upgradedAnvil = registerItem(new ItemUpgradedAnvil(ModBlocks.upgradedAnvil), "upgraded_anvil", true);
		bossLoot = registerItem(new ItemBossLoot(ModBlocks.bossLoot), "boss_loot", true);
		//Zombie Apocalypse
		registerItem(new ItemEventMobSpawnEgg(null), "zombie_egg", false);
		zombieBardEgg = registerItem(new ItemEventMobSpawnEgg("mobEvents.zombieBard"), "zombie_bard", true);
		zombieCloneEgg = registerItem(new ItemEventMobSpawnEgg("mobEvents.zombieClone"), "zombie_clone", true);
		zombieMinionEgg = registerItem(new ItemEventMobSpawnEgg("mobEvents.zombieMinion"), "zombie_minion", true);
		zombiePyromaniacEgg = registerItem(new ItemEventMobSpawnEgg("mobEvents.zombiePyromaniac"), "zombie_pyromaniac", true);
		zombieRiderEgg = registerItem(new ItemEventMobSpawnEgg("mobEvents.zombieRider"), "zombie_rider", true);
		zombieRuntEgg = registerItem(new ItemEventMobSpawnEgg("mobEvents.zombieRunt"), "zombie_runt", true);
		zombieSummonerEgg = registerItem(new ItemEventMobSpawnEgg("mobEvents.zombieSummoner"), "zombie_summoner", true);
		zombieJumperEgg = registerItem(new ItemEventMobSpawnEgg("mobEvents.zombieJumper"), "zombie_jumper", true);
		zombieThiefEgg = registerItem(new ItemEventMobSpawnEgg("mobEvents.zombieThief"), "zombie_thief", true);
		//Skeletal Uprising
		registerItem(new ItemEventMobSpawnEgg(null), "skeleton_egg", false);
		skeletonSoldierEgg = registerItem(new ItemEventMobSpawnEgg("mobEvents.skeletonSoldier"), "skeleton_soldier", true);
		skeletonBardEgg = registerItem(new ItemEventMobSpawnEgg("mobEvents.skeletonBard"), "skeleton_bard", true);
		skeletonCloneEgg = registerItem(new ItemEventMobSpawnEgg("mobEvents.skeletonClone"), "skeleton_clone", true);
		skeletonPyromaniacEgg = registerItem(new ItemEventMobSpawnEgg("mobEvents.skeletonPyromaniac"), "skeleton_pyromaniac", true);
		//boss
		zombieBossSpawnerEgg = registerItem(new ItemEventMobSpawnEgg("mobEvents.zombieBossSpawner"), "zombie_boss_spawner", true);
	}

	public static void registerRenders()
	{
		for (Item item : allItems)
			registerRender(item);
	}

	public static Item registerItem(final Item item, final String unlocalizedName, boolean addToTab) {
		item.setUnlocalizedName(unlocalizedName);
		if (addToTab && FMLCommonHandler.instance().getEffectiveSide()==Side.CLIENT)
			addToTab(item);
		GameRegistry.register(item.setRegistryName(unlocalizedName));
		if (item instanceof ItemEventMobSpawnEgg)
			eggs.add((ItemEventMobSpawnEgg) item);
		else if (item instanceof IEventItem)
			drops.add((IEventItem) item);
		if (item instanceof ItemRecord)
			records.add((ItemRecord) item);
		allItems.add(item);
		return item;
	}

	public static void addToTab(Item item)
	{
		ArrayList<ItemStack> subItems = new ArrayList<ItemStack>();
		item.getSubItems(item, item instanceof ItemEventMobSpawnEgg ? MobEvents.mobsTab : MobEvents.itemsTab, subItems);
		for (ItemStack stack : subItems)
		{
			if (item instanceof ItemEventMobSpawnEgg)
			{
				item.setCreativeTab(MobEvents.mobsTab);
				MobEvents.mobsTab.orderedMobs.add(stack);
			}
			else
			{
				item.setCreativeTab(MobEvents.itemsTab);
				MobEvents.itemsTab.orderedItems.add(stack);
			}
		}
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
		if (item instanceof ItemEventMobSpawnEgg)
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(MobEvents.MODID + ":" + item.getUnlocalizedName().substring(5, item.getUnlocalizedName().indexOf("_"))+"_egg", "inventory"));
		else
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(MobEvents.MODID + ":" + item.getUnlocalizedName().substring(5), "inventory"));
	}
}

