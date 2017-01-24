package furgl.mobEvents.common.event;

import furgl.mobEvents.common.Events.Event;
import furgl.mobEvents.common.entity.boss.EntityBossZombie;
import furgl.mobEvents.common.world.WorldData;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LibrarianChatEvent 
{
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(ServerChatEvent event)
	{
		if (WorldData.get(event.getPlayer().worldObj).currentEvent == Event.ZOMBIE_APOCALYPSE && WorldData.get(event.getPlayer().worldObj).currentEvent.boss != null && event.getPlayer() != null)
		{
			for (EntityBossZombie boss : event.getPlayer().worldObj.getEntitiesWithinAABB(EntityBossZombie.class, WorldData.get(event.getPlayer().worldObj).currentEvent.boss.getEntityBoundingBox().expand(65, 65, 65)))
			{
				boss.setAttackTarget(event.getPlayer());
				if (boss.type == 4 && !event.getPlayer().worldObj.isRemote)
				{
					WorldData.get(event.getPlayer().worldObj).currentEvent.sendServerMessage(new TextComponentTranslation(boss.getName()+": "+boss.librarianTaunts.get(boss.worldObj.rand.nextInt(boss.librarianTaunts.size()))).setStyle(new Style().setColor(boss.getChatColor()).setItalic(true)));
					for (int i=0; i<100; i++)
						event.getPlayer().worldObj.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, true, (float)event.getPlayer().posX+(boss.worldObj.rand.nextFloat()-0.5f)*3f, (float)event.getPlayer().posY+(boss.worldObj.rand.nextFloat()+0.0f)*3f, (float)event.getPlayer().posZ+(boss.worldObj.rand.nextFloat()-0.5f)*3f, 0, 0, 0, 0, 1);
					event.getPlayer().worldObj.playSound(event.getPlayer().posX, event.getPlayer().posY, event.getPlayer().posZ, SoundEvents.ENTITY_ENDERDRAGON_GROWL, SoundCategory.HOSTILE, 1.0f, 0.8f, true);
					event.getPlayer().addPotionEffect(new PotionEffect(MobEffects.WITHER, 100, 1));
					event.getPlayer().addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200, 1));
					event.getPlayer().addPotionEffect(new PotionEffect(MobEffects.HUNGER, 200, 0));
					event.getPlayer().addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 600, 0));
					event.getPlayer().addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100, 3));
					event.setComponent(new TextComponentTranslation("<"+event.getUsername()+"> "+TextFormatting.OBFUSCATED+event.getMessage()));
				}
			}
		}
	}
}
