package furgl.mobEvents.common.event;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.Events.Event;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class KeepInvDuringBossEvent 
{
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(LivingDeathEvent event)
	{
		//prevent items from being removed from inventory and dropped
		if (event.getEntity() instanceof EntityPlayer && this.shouldKeepInventory())
		{
			boolean keepInventory = event.getEntity().worldObj.getGameRules().getBoolean("keepInventory");
			if (!keepInventory)
			{
				event.getEntity().worldObj.getGameRules().setOrCreateGameRule("keepInventory", "true");
				((EntityPlayer)event.getEntity()).onDeath(event.getSource());
				event.getEntity().worldObj.getGameRules().setOrCreateGameRule("keepInventory", "false");
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(PlayerEvent.Clone event)
	{
		//copy items to mimic keep inventory
		if (this.shouldKeepInventory())
		{
			event.getEntityPlayer().inventory.copyInventory(event.getOriginal().inventory);
			event.getEntityPlayer().experienceLevel = event.getOriginal().experienceLevel;
			event.getEntityPlayer().experienceTotal = event.getOriginal().experienceTotal;
			event.getEntityPlayer().experience = event.getOriginal().experience;
			event.getEntityPlayer().setScore(event.getOriginal().getScore());
		}
	}

	public boolean shouldKeepInventory()
	{
		if (MobEvents.proxy.getWorldData().keepInventory == 1 && MobEvents.proxy.getWorldData().currentWave == 4 && MobEvents.proxy.getWorldData().currentEvent.boss != null && MobEvents.proxy.getWorldData().currentEvent.boss.isBossAlive())
			return true;
		else if (MobEvents.proxy.getWorldData().keepInventory == 2 && MobEvents.proxy.getWorldData().currentEvent.getClass() != Event.class)
			return true;
		else
			return false;
	}
}
