package furgl.mobEvents.common.sound;

import furgl.mobEvents.common.block.BlockBardsJukebox;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SoundLoopedRecord extends MovingSound implements ITickableSound
{
	public boolean donePlaying;
	private Entity entity;
	private World jukeboxWorld;
	private boolean preload; //true stop playing instantly

	public SoundLoopedRecord(SoundEvent sound, World jukeboxWorld, BlockPos pos, float volume)	{
		this(sound, pos.getX(), pos.getY(), pos.getZ(), volume);
		this.jukeboxWorld = jukeboxWorld;
	}
	
	public SoundLoopedRecord(SoundEvent sound, Entity entity, float volume, boolean preload) {
		this(sound, (float) entity.posX, (float) entity.posY, (float) entity.posZ, volume);
		this.entity = entity;
		this.preload = preload;
	}
	
	public SoundLoopedRecord(SoundEvent sound, float x, float y, float z, float volume) {
		super(sound, SoundCategory.RECORDS);
		this.xPosF = x;
		this.yPosF = y;
		this.zPosF = z;
		this.repeat = true;
		this.donePlaying = false;
		this.volume = volume;
	}
	
	@Override
	public void update() 
	{
		//preload and stop
		if (preload) {
			this.volume = 0;
			this.repeat = false;
		}
			//this.donePlaying = true;
		//bound to entity
		if (entity != null)
		{
			if (entity.isDead)
				this.donePlaying = true;
			this.xPosF = (float) entity.posX;
			this.yPosF = (float) entity.posY;
			this.zPosF = (float) entity.posZ;
		}
		//bound to jukebox
		else if (jukeboxWorld != null)
		{
			IBlockState state = jukeboxWorld.getBlockState(new BlockPos(this.xPosF, this.yPosF, this.zPosF));
			if (!(state.getBlock() instanceof BlockBardsJukebox) || !state.getValue(BlockJukebox.HAS_RECORD).booleanValue())
				this.donePlaying = true;
			if (jukeboxWorld.rand.nextInt(5) == 0)
				Minecraft.getMinecraft().theWorld.spawnParticle(EnumParticleTypes.NOTE, true, this.xPosF-1.5f+jukeboxWorld.rand.nextFloat()*4, this.yPosF+0.0f+jukeboxWorld.rand.nextFloat()*2, this.zPosF-1.5f+jukeboxWorld.rand.nextFloat()*4, jukeboxWorld.rand.nextFloat(), 0, 0);
		}
	}

	@Override
	public boolean isDonePlaying() 
	{
		return donePlaying;
	}
}
