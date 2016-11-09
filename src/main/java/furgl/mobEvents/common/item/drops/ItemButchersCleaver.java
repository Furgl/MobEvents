package furgl.mobEvents.common.item.drops;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.Events.Event;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemButchersCleaver extends ItemSword implements IEventItem
{	
	private double attackDamage;
/*	private final ArrayList<Item> meatDrops = new ArrayList<Item>() {{
		add(Items.BEEF);
		add(Items.CHICKEN);
		add(Items.COOKED_BEEF);
		add(Items.COOKED_CHICKEN);
		add(Items.COOKED_FISH);
		add(Items.COOKED_MUTTON);
		add(Items.COOKED_PORKCHOP);
		add(Items.COOKED_RABBIT);
		add(Items.FISH);
		add(Items.MUTTON);
		add(Items.PORKCHOP);
		add(Items.RABBIT);
		add(Items.ROTTEN_FLESH);
	}};*/

	public ItemButchersCleaver(Item.ToolMaterial material) {
		super(material);
		this.maxStackSize = 1;
		this.setMaxDamage(1000);
		this.attackDamage = 6.5D;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
	{
		tooltip.set(0, TextFormatting.AQUA+tooltip.get(0));
		int index = MobEvents.proxy.getWorldData().getPlayerIndex(player.getDisplayNameString());
		if (MobEvents.proxy.getWorldData().unlockedItems.get(index).contains(this.getName()) || player.capabilities.isCreativeMode) {
			tooltip.add(TextFormatting.ITALIC+""+TextFormatting.GOLD+"Slices meaty monsters");
		}
		else 
			tooltip.add(TextFormatting.ITALIC+""+TextFormatting.GOLD+"???");
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)
	{
		/*if (!attacker.worldObj.isRemote && target instanceof EntityLiving)
		{
			ResourceLocation resourcelocation = ((EntityLiving) target).getLootTable();

			if (resourcelocation != null)
			{
				LootTable loottable = this.worldObj.getLootTableManager().getLootTableFromLocation(resourcelocation);
				this.deathLootTable = null;
				LootContext.Builder lootcontext$builder = (new LootContext.Builder((WorldServer)this.worldObj)).withLootedEntity(this).withDamageSource(source);

				if (p_184610_1_ && this.attackingPlayer != null)
				{
					lootcontext$builder = lootcontext$builder.withPlayer(this.attackingPlayer).withLuck(this.attackingPlayer.getLuck());
				}

				for (ItemStack itemstack : loottable.generateLootForPools(this.deathLootTableSeed == 0L ? this.rand : new Random(this.deathLootTableSeed), lootcontext$builder.build()))
				{
					this.entityDropItem(itemstack, 0.0F);
				}

				this.dropEquipment(p_184610_1_, p_184610_2_);
			}
			try
			{LootTableLoadEvent
				Method method = ReflectionHelper.findMethod(EntityLiving.class, (EntityLiving)target, new String[] {"getLootTable", "func_184276_b"}, boolean.class, int.class);
				method.setAccessible(true);
				ResourceLocation resourceLocation = (ResourceLocation) method.invoke(target);
				if (resourceLocation != null) {
					LootTable loot = target.worldObj.getLootTableManager().getLootTableFromLocation(resourcelocation);
					for (Item meat : this.meatDrops)
						for (EntityItem drop : loot.)
							if (drop.getEntityItem().getItem() == meat)
							{
								target.entityDropItem(new ItemStack(meat), 1);
								float damage = this.getDamageVsEntity()+EnchantmentHelper.getModifierForCreature(stack, target.getCreatureAttribute());
								target.attackEntityFrom(DamageSource.causeMobDamage(attacker), damage*1.5f);
								target.worldObj.playSound(target.posX, target.posY, target.posZ, SoundEvents.entity_wither_shoot, SoundCategory.PLAYERS, 0.5F, target.worldObj.rand.nextFloat()+1.2F, true);//mob.wither.shoot mob.zombie.woodbreak tile.piston.out
								for (int i=0; i<10; i++)
									attacker.worldObj.spawnParticle(EnumParticleTypes.REDSTONE, true, (float)target.posX+(target.worldObj.rand.nextFloat()-0.5f)*1f, (float)target.posY+(target.worldObj.rand.nextFloat()+target.height/2)*1f, (float)target.posZ+(target.worldObj.rand.nextFloat()-0.5f)*1f, 0, 0, 0, 0, 1);
								break;
							}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}*/

		return super.hitEntity(stack, target, attacker);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
	{
		subItems.add(this.getItemStack());
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack)
	{
		Multimap<String, AttributeModifier> multimap = HashMultimap.<String, AttributeModifier>create();
		if (slot == EntityEquipmentSlot.MAINHAND)
		{
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double)this.attackDamage, 0));
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getAttributeUnlocalizedName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -2.4000000953674316D, 0));
		}
		return multimap;
	}

	@Override
	public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot equipmentSlotIn, Entity entity){
		if (equipmentSlotIn == EntityEquipmentSlot.MAINHAND || equipmentSlotIn == EntityEquipmentSlot.OFFHAND)
			return true;
		return false;
	}

	@Override
	public ItemStack getItemStack() {
		ItemStack stack = new ItemStack(this);
		return stack;
	}

	@Override
	public String getName() {
		return this.getItemStackDisplayName(new ItemStack(this));
	}

	@Override
	public int getColor() {
		return 0xb30000;
	}

	@Override
	public float getRed() {
		return 0.8f;
	}

	@Override
	public float getGreen() {
		return 0.1f;
	}

	@Override
	public float getBlue() {
		return 0.1f;
	}

	@Override
	public ArrayList<String> droppedBy() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Boss Zombie Butcher");
		return list;
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (!worldIn.isRemote && entityIn instanceof EntityPlayer && !(entityIn instanceof FakePlayer)) {
			int index = MobEvents.proxy.getWorldData().getPlayerIndex(entityIn.getName());
			if (!MobEvents.proxy.getWorldData().unlockedItems.get(index).contains(this.getName()))
			{
				MobEvents.proxy.getWorldData().unlockedItems.get(index).add(this.getName());
				Event.displayUnlockMessage((EntityPlayer) entityIn, "Unlocked information about the "+stack.getDisplayName()+" item in the Event Book");
				MobEvents.proxy.getWorldData().markDirty();
			}
		}
	}
}
