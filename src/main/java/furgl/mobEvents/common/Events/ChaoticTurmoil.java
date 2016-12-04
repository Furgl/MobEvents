package furgl.mobEvents.common.Events;

import java.util.ArrayList;

import furgl.mobEvents.common.MobEvents;
import furgl.mobEvents.common.entity.IEventMob;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ChaoticTurmoil extends Event
{
	public String changingName;
	private Event mimicEvent;

	public ChaoticTurmoil(World world) 
	{ 
		super(world);
		this.occurs = Occurs.CREATIVE;
		this.enumColor = TextFormatting.DARK_RED;
		this.obfuscate();
	}

	@Override
	public void setBookDescription()
	{
		this.bookJokes = new ArrayList<String>();
		this.bookJokes.add(TextFormatting.DARK_RED+""+TextFormatting.BOLD+""+TextFormatting.ITALIC+"This event spawns monsters from all other events.");
		this.bookWaves = "3 + Boss";
	}

	@Override
	public void setSounds()
	{
		sounds = new ArrayList<SoundEvent>();
		for (Event event : Event.allEvents)
			if (event.getClass() != ChaoticTurmoil.class)
				sounds.addAll(event.sounds);
	}

	@Override
	public void setMobs()
	{
		mobs = new ArrayList<IEventMob>();
		for (Event event : Event.allEvents)
			if (event.getClass() != ChaoticTurmoil.class)
				mobs.addAll(event.mobs);
	}

	public void obfuscate()
	{
		this.changingName = TextFormatting.OBFUSCATED+"Chaotic Turmoil";
	}

	public void recolor()
	{
		Event tmp = this;
		while (tmp == this.mimicEvent || tmp.getClass() == this.getClass())
			tmp = Event.allEvents.get(Event.rand.nextInt(Event.allEvents.size()));
		this.mimicEvent = tmp;
		this.color = this.mimicEvent.color+Event.rand.nextInt(100)-50;
		this.red = this.mimicEvent.red+(Event.rand.nextFloat()-0.5F)*0.5F;
		this.green = this.mimicEvent.green+(Event.rand.nextFloat()-0.5F)*0.5F;
		this.blue = this.mimicEvent.blue+(Event.rand.nextFloat()-0.5F)*0.5F;
	}

	public void deobfuscate()
	{
		this.changingName = "Chaotic Turmoil";
	}

	@Override
	public void onUpdate()
	{		
		MobEvents.proxy.world.setWorldTime(MobEvents.proxy.world.getWorldTime()+(MobEvents.proxy.world.isDaytime() ? 30 : 10));
		if (rand.nextInt(200) == 0)
			this.playSound(sounds);
		super.onUpdate();
	}

	@Override
	public void startWave(int wave) 
	{
		super.startWave(wave);

		for (Event event : Event.allEvents)
			if (event.getClass() != ChaoticTurmoil.class)
				event.startWave(wave);
		//TODO boss spawning?!
	}

	@Override
	public void startEvent() 
	{ 
		MobEvents.proxy.getWorldData().currentEvent = Event.CHAOTIC_TURMOIL;
		super.startEvent();
		if (!MobEvents.proxy.world.isRemote) {
			Event.sendServerMessage(new TextComponentTranslation("You asked for it...").setStyle(new Style().setBold(true).setColor(this.enumColor).setItalic(true)));	
			Event.playServerSound(SoundEvents.ENTITY_ZOMBIE_INFECT, 10f, 0f);
		}
	}

	@Override
	public void stopEvent() 
	{
		super.stopEvent();
		if (!MobEvents.proxy.world.isRemote) {
			Event.sendServerMessage(new TextComponentTranslation(this.toString() + " has ended.").setStyle(new Style().setBold(true).setColor(this.enumColor)));
			Event.playServerSound(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE,  0.2f, 2f);
		}
	}

	@Override
	public String toString()
	{
		return "Chaotic Turmoil";
	}
}
