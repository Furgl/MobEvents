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
import furgl.mobEvents.common.item.drops.ItemButchersCleaver0;
import furgl.mobEvents.common.item.drops.ItemButchersCleaver1;
import furgl.mobEvents.common.item.drops.ItemButchersCleaver2;
import furgl.mobEvents.common.item.drops.ItemButchersCleaver3;
import furgl.mobEvents.common.item.drops.ItemDoubleJumpBoots;
import furgl.mobEvents.common.item.drops.ItemPyromaniacsBow;
import furgl.mobEvents.common.item.drops.ItemSummonersHelm;
import furgl.mobEvents.common.item.drops.ItemThievesMask;
import furgl.mobEvents.common.item.records.ItemCustomRecord;
import furgl.mobEvents.common.sound.ModSoundEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
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

public class ModItems {
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
	public static Item butchersCleaver0;
	public static Item butchersCleaver1;
	public static Item butchersCleaver2;
	public static Item butchersCleaver3;
	public static Item bookOfHealing;
	public static Item pyromaniacsBow;
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

	public static void init() {
		allItems = new ArrayList<Item>();
		drops = new ArrayList<IEventItem>();
		records = new ArrayList<ItemRecord>();
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
		butchersCleaver0 = registerItem(new ItemButchersCleaver0(butchersCleaverMaterial), "butchers_cleaver_0", true);
		butchersCleaver1 = registerItem(new ItemButchersCleaver1(butchersCleaverMaterial), "butchers_cleaver_1", true);
		butchersCleaver2 = registerItem(new ItemButchersCleaver2(butchersCleaverMaterial), "butchers_cleaver_2", true);
		butchersCleaver3 = registerItem(new ItemButchersCleaver3(butchersCleaverMaterial), "butchers_cleaver_3", true);
		bookOfHealing = registerItem(new ItemBookOfHealing(), "book_of_healing", true);
		pyromaniacsBow = registerItem(new ItemPyromaniacsBow(), "pyromaniacs_bow", true);
		anvilUpgradeZombie = registerItem(new ItemAnvilUpgradeZombie(), "anvil_upgrade_zombie", true);
		anvilUpgradeSkeleton = registerItem(new ItemAnvilUpgradeSkeleton(), "anvil_upgrade_skeleton", true);
		//other
		upgradedAnvil = registerItem(new ItemUpgradedAnvil(ModBlocks.upgradedAnvil), "upgraded_anvil", true);
		bossLoot = registerItem(new ItemBossLoot(ModBlocks.bossLoot), "boss_loot", true);
	}

	public static void registerRenders() {
		for (Item item : allItems)
			registerRender(item);
	}

	private static Item registerItem(final Item item, final String unlocalizedName, boolean addToTab) {
		item.setUnlocalizedName(unlocalizedName);
		GameRegistry.register(item.setRegistryName(unlocalizedName));
		if (addToTab && FMLCommonHandler.instance().getEffectiveSide()==Side.CLIENT)
			addToTab(item);
		else if (item instanceof IEventItem && !(item instanceof ItemButchersCleaver1) && !(item instanceof ItemButchersCleaver2) && !(item instanceof ItemButchersCleaver3))
			drops.add((IEventItem) item);
		if (item instanceof ItemRecord)
			records.add((ItemRecord) item);
		allItems.add(item);
		return item;
	}

	private static void addToTab(Item item) {
		ArrayList<ItemStack> subItems = new ArrayList<ItemStack>();
		item.getSubItems(item, MobEvents.itemsTab, subItems);
		for (ItemStack stack : subItems) {
			item.setCreativeTab(MobEvents.itemsTab);
			MobEvents.itemsTab.orderedItems.add(stack);
		}
	}

	private static void registerRender(Item item) {	
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(MobEvents.MODID + ":" + item.getUnlocalizedName().substring(5), "inventory"));
	}
}