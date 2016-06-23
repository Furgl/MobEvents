package furgl.mobEvents.common.entity;

import com.mojang.authlib.GameProfile;

import furgl.mobEvents.common.item.drops.IEventItem;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityGuiPlayer extends AbstractClientPlayer
{
	private AbstractClientPlayer player;

	public EntityGuiPlayer(World worldIn, GameProfile playerProfile, AbstractClientPlayer thePlayer) 
	{
		super(worldIn, playerProfile);
		this.player = thePlayer;
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
		for (int i=0; i<4; i++)
			if (this.getCurrentArmor(i) != null && this.getCurrentArmor(i).getItem() instanceof IEventItem)
				((ItemArmor) this.getCurrentArmor(i).getItem()).onArmorTick(worldObj, this, ((IEventItem) this.getCurrentArmor(i).getItem()).getItemStack());
	}
}
