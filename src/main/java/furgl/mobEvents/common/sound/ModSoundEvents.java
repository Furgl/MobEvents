package furgl.mobEvents.common.sound;

import furgl.mobEvents.common.MobEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModSoundEvents {
	//mobs
	public static SoundEvent mob_event_zombie_say;
	public static SoundEvent mob_event_zombie_hurt;
	public static SoundEvent mob_event_zombie_death;
	//ambience
	public static SoundEvent ambience_zombie_ambience;
	//records
	public static SoundEvent record_zombie_apocalypse;
	public static SoundEvent record2;
	public static SoundEvent record3;
	public static SoundEvent record4;

	public static void registerSounds() {
		//mobs
		mob_event_zombie_say = registerSound("mob.event_zombie.say");
		mob_event_zombie_hurt = registerSound("mob.event_zombie.hurt");
		mob_event_zombie_death = registerSound("mob.event_zombie.death");
		//ambience
		ambience_zombie_ambience = registerSound("ambience.zombie_ambience");
		//records
		record_zombie_apocalypse = registerSound("record_zombie_apocalypse");
		record2 = registerSound("record2");
		record3 = registerSound("record3");
		record4 = registerSound("record4");
	}
	
	private static SoundEvent registerSound(String soundName) {
		ResourceLocation loc = new ResourceLocation(MobEvents.MODID, soundName);
		SoundEvent sound = new SoundEvent(loc);
		GameRegistry.register(sound, loc);
		return sound;
	}
}
