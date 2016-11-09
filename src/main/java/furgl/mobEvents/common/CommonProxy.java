package furgl.mobEvents.common;

import furgl.mobEvents.common.achievements.Achievements;
import furgl.mobEvents.common.item.drops.ItemDoubleJumpBoots;
import furgl.mobEvents.common.item.drops.ItemThievesMask;
import furgl.mobEvents.common.world.MobEventsWorldSavedData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.Achievement;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.AchievementPage;

public class CommonProxy 
{
	public World world;
	
	public void registerRenders() { }
	
	public void registerBlockRenders() { }
	
	public void registerBlockModels() { }

	public void registerAchievements()
	{
		AchievementPage.registerAchievementPage(new AchievementPage("Mob Events", (Achievement[]) Achievements.achievements.toArray(new Achievement[Achievements.achievements.size()])));

		for (int i=0; i<Achievements.achievements.size(); i++)
			Achievements.achievements.get(i).registerStat();
	}
	
	public MobEventsWorldSavedData getWorldData() {
		return MobEventsWorldSavedData.get(world);
	}

	public void openBookGui(EntityPlayer player, boolean creative) { }

	public void playSoundJukebox(SoundEvent sound, World world, BlockPos pos, float volume) { }
	
	public void playSoundEntity(SoundEvent sound, Entity entity, float volume) { }

	public void startBossRecord(SoundEvent sound, Entity entity, float volume) { }
	
	public void stopBossRecord() { }
	
	public void stopSounds() { }

	public void doubleJumpBootsTick(EntityPlayer player, ItemDoubleJumpBoots boots) { }

	public void thievesMaskTick(EntityPlayer player, ItemThievesMask mask) { }
}
