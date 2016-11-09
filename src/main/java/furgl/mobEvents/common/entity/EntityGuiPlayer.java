package furgl.mobEvents.common.entity;

import java.util.ArrayList;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;

import furgl.mobEvents.client.gui.book.GuiEventBook;
import furgl.mobEvents.common.item.drops.IEventItem;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityGuiPlayer extends AbstractClientPlayer
{
	private AbstractClientPlayer player;
	public GuiEventBook book;

	public EntityGuiPlayer(World worldIn, GameProfile playerProfile, AbstractClientPlayer thePlayer, GuiEventBook book) 
	{
		super(worldIn, playerProfile);
		this.player = thePlayer;
		this.book = book;
	}

	@Override
	public ResourceLocation getLocationSkin()
	{
		if (player != null) {
			NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
			return networkplayerinfo == null ? DefaultPlayerSkin.getDefaultSkin(this.getUniqueID()) : networkplayerinfo.getLocationSkin();
		}
		else 
			return super.getLocationSkin();
	}
	
	public void doSpecialRender()
	{
		ArrayList<ItemStack> armor = Lists.newArrayList(this.getArmorInventoryList());
		for (int i=0; i<4; i++)
			if (armor.get(i) != null && armor.get(i).getItem() instanceof IEventItem)
				((ItemArmor) armor.get(i).getItem()).onArmorTick(worldObj, this, ((IEventItem) armor.get(i).getItem()).getItemStack());
		if (this.getHeldItemMainhand() != null)
			this.getHeldItemMainhand().getItem().onUpdate(this.getHeldItemMainhand(), this.worldObj, this, 0, true);
		if (this.getHeldItemOffhand() != null)
			this.getHeldItemOffhand().getItem().onUpdate(this.getHeldItemOffhand(), this.worldObj, this, 0, true);
	}
}
