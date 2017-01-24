package furgl.mobEvents.common.event;

import furgl.mobEvents.common.Events.Event;
import furgl.mobEvents.common.world.WorldData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
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
		if (event.getEntity() instanceof EntityPlayer && this.shouldKeepInventory(event.getEntity().worldObj))
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
		if (this.shouldKeepInventory(event.getEntity().worldObj))
		{
			event.getEntityPlayer().inventory.copyInventory(event.getOriginal().inventory);
			event.getEntityPlayer().experienceLevel = event.getOriginal().experienceLevel;
			event.getEntityPlayer().experienceTotal = event.getOriginal().experienceTotal;
			event.getEntityPlayer().experience = event.getOriginal().experience;
			event.getEntityPlayer().setScore(event.getOriginal().getScore());
		}
	}

	private boolean shouldKeepInventory(World world)
	{
		if (WorldData.get(world).keepInventory == 1 && WorldData.get(world).currentWave == 4 && WorldData.get(world).currentEvent.boss != null && WorldData.get(world).currentEvent.boss.isBossAlive())
			return true;
		else if (WorldData.get(world).keepInventory == 2 && WorldData.get(world).currentEvent.getClass() != Event.class)
			return true;
		else
			return false;
	}
}
