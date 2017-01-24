package furgl.mobEvents.common.item.drops;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import furgl.mobEvents.common.Events.Event;
import furgl.mobEvents.common.entity.projectile.EntityPyromaniacsArrow;
import furgl.mobEvents.common.world.WorldData;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPyromaniacsBow extends ItemBow implements IEventItem
{	
	public ItemPyromaniacsBow() {
		super();
		this.setMaxDamage(400);
		this.addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter() {
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
				if (entity == null)
					return 0.0F;
				else {
					ItemStack itemstack = entity.getActiveItemStack();
					return itemstack != null && itemstack.getItem() instanceof ItemBow ? 
							(float)(stack.getMaxItemUseDuration() - entity.getItemInUseCount()) / 20.0F : 0.0F;
				}
			}
		});
	}

	//Copied from ItemBow onPlayerStoppedUsing
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft)
	{
		if (entityLiving instanceof EntityPlayer)
		{
			EntityPlayer entityplayer = (EntityPlayer)entityLiving;
			boolean flag = entityplayer.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
			ItemStack itemstack = this.findAmmo(entityplayer);

			int i = this.getMaxItemUseDuration(stack) - timeLeft;
			i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, worldIn, (EntityPlayer)entityLiving, i, itemstack != null || flag);
			if (i < 0) return;

			if (itemstack != null || flag)
			{
				if (itemstack == null)
				{
					itemstack = new ItemStack(Items.ARROW);
				}

				float f = getArrowVelocity(i);

				if ((double)f >= 0.1D)
				{
					boolean flag1 = entityplayer.capabilities.isCreativeMode || (itemstack.getItem() instanceof ItemArrow ? ((ItemArrow)itemstack.getItem()).isInfinite(itemstack, stack, entityplayer) : false);

					if (!worldIn.isRemote)
					{
						//ItemArrow itemarrow = (ItemArrow)((ItemArrow)(itemstack.getItem() instanceof ItemArrow ? itemstack.getItem() : Items.ARROW));
						//EntityArrow entityarrow = itemarrow.createArrow(worldIn, itemstack, entityplayer);
						EntityPyromaniacsArrow entityarrow = new EntityPyromaniacsArrow(worldIn, entityplayer);
						entityarrow.setPotionEffect(itemstack);

						entityarrow.setAim(entityplayer, entityplayer.rotationPitch, entityplayer.rotationYaw, 0.0F, f * 3.0F, 1.0F);

						if (f == 1.0F)
						{
							entityarrow.setIsCritical(true);
						}

						int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);

						if (j > 0)
						{
							entityarrow.setDamage(entityarrow.getDamage() + (double)j * 0.5D + 0.5D);
						}

						int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);

						if (k > 0)
						{
							entityarrow.setKnockbackStrength(k);
						}

						if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0)
						{
							entityarrow.setFire(100);
						}

						stack.damageItem(1, entityplayer);

						if (flag1)
						{
							entityarrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
						}

						worldIn.spawnEntityInWorld(entityarrow);
					}

					worldIn.playSound((EntityPlayer)null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

					if (!flag1)
					{
						--itemstack.stackSize;

						if (itemstack.stackSize == 0)
						{
							entityplayer.inventory.deleteStack(itemstack);
						}
					}

					entityplayer.addStat(StatList.getObjectUseStats(this));
				}
			}
		}
	}

	//Copied from ItemBow bc private
	private ItemStack findAmmo(EntityPlayer player)
	{
		if (this.isArrow(player.getHeldItem(EnumHand.OFF_HAND)))
		{
			return player.getHeldItem(EnumHand.OFF_HAND);
		}
		else if (this.isArrow(player.getHeldItem(EnumHand.MAIN_HAND)))
		{
			return player.getHeldItem(EnumHand.MAIN_HAND);
		}
		else
		{
			for (int i = 0; i < player.inventory.getSizeInventory(); ++i)
			{
				ItemStack itemstack = player.inventory.getStackInSlot(i);

				if (this.isArrow(itemstack))
				{
					return itemstack;
				}
			}

			return null;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		tooltip.set(0, TextFormatting.AQUA+tooltip.get(0));
		int index = WorldData.get(player.worldObj).getPlayerIndex(player.getDisplayNameString());
		if (WorldData.get(player.worldObj).unlockedItems.get(index).contains(this.getName()) || player.capabilities.isCreativeMode) {
			tooltip.add(TextFormatting.ITALIC+""+TextFormatting.GOLD+"Ignites arrows and causes them");
			tooltip.add(TextFormatting.ITALIC+""+TextFormatting.GOLD+"to light nearby blocks on fire");
		}
		else 
			tooltip.add(TextFormatting.ITALIC+""+TextFormatting.GOLD+"???");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		ItemStack stack = this.getItemStack();
		stack.addEnchantment(Enchantments.FLAME, 2);
		subItems.add(this.getItemStack());
	}

	@Override
	public ItemStack getItemStack() {
		ItemStack stack = new ItemStack(this);
		stack.addEnchantment(Enchantments.FLAME, 2);
		return stack;
	}

	@Override
	public String getName() {
		return this.getItemStackDisplayName(new ItemStack(this));
	}

	@Override
	public int getColor() {
		return 0xc96514;
	}

	@Override
	public float getRed() {
		return 0.8f;
	}

	@Override
	public float getGreen() {
		return 0.35f;
	}

	@Override
	public float getBlue() {
		return 0.1f;
	}

	@Override
	public ArrayList<String> droppedBy() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Skeleton Pyromaniac");
		return list;
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		//unlock
		if (entityIn instanceof EntityPlayer && !(entityIn instanceof FakePlayer)) {
			int index = WorldData.get(worldIn).getPlayerIndex(entityIn.getName());
			if (!worldIn.isRemote && !WorldData.get(worldIn).unlockedItems.get(index).contains(this.getName())) {
				WorldData.get(worldIn).unlockedItems.get(index).add(this.getName());
				Event.displayUnlockMessage((EntityPlayer) entityIn, "Unlocked information about the "+stack.getDisplayName()+" item in the Event Book");
				WorldData.get(worldIn).markDirty();
			}
		}
		//enchantment
		if (!stack.isItemEnchanted())
			stack.addEnchantment(Enchantments.FLAME, 2);
	}
}
