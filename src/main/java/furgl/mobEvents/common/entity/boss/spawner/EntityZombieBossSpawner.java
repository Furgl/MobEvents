package furgl.mobEvents.common.entity.boss.spawner;

import furgl.mobEvents.common.Events.Event;
import furgl.mobEvents.common.entity.boss.EntityBossZombie;
import furgl.mobEvents.common.item.ModItems;
import furgl.mobEvents.common.item.drops.IEventItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityZombieBossSpawner extends EntityBossSpawner
{
	public EntityZombieBossSpawner(World world) 
	{
		super(world);
		this.event = Event.ZOMBIE_APOCALYPSE;
		for (int i=1; i<7; i++)
		{
			EntityBossZombie boss = new EntityBossZombie(world, i);
			boss.onInitialSpawn(null, null);
			this.bossesToSummon.add(boss);
		}
		this.record = (ItemRecord) ModItems.recordZombieApocalypse;
		this.setBookDescription();
	}

	@Override
	public void setBookDescription()
	{
		super.setBookDescription();
		this.bookDescription = "Six giant villager zombies; each with its own special abilities.";
		for (IEventItem drop : ModItems.drops)
			if (drop.droppedBy().get(0).contains("Zombie"))
				this.addDrops(drop.getItemStack(), 5);
	}

	@Override
    public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, ItemStack stack, EnumHand hand)
	{
		if (super.applyPlayerInteraction(player, vec, stack, hand) == EnumActionResult.FAIL)
			return EnumActionResult.FAIL;
		if (!this.worldObj.isRemote)
		{
			for (int i=1; i<7; i++)
			{
				EntityBossZombie boss = new EntityBossZombie(worldObj, i);
				boss.setBeaconPosition();
				boss.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, i*40+0, 0, true, false));
				boss.onInitialSpawn(null, null);
				boss.setAttackTarget(player);
				boss.setSilent(true);
				this.worldObj.spawnEntityInWorld(boss);
			}
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.FAIL;
	}

	@Override
	public String getName() {
		return "Zombie Boss";
	}
	
	@Override
	public Event getEvent() {
		return Event.ZOMBIE_APOCALYPSE;
	}
}
