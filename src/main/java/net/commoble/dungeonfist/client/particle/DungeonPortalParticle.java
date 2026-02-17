package net.commoble.dungeonfist.client.particle;

import org.jspecify.annotations.NonNull;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.RandomSource;

public class DungeonPortalParticle extends SingleQuadParticle
{
	private final SpriteSet sprites;
	public DungeonPortalParticle(ClientLevel world, double x, double y, double z, double xVel, double yVel, double zVel,
			float red, float green, float blue, SpriteSet spriteSet)
	{

		super(world, x, y, z, 0.0D, 0.0D, 0.0D, spriteSet.first());
		this.rCol = red;
		this.gCol = green;
		this.bCol = blue;
		this.xd = xVel;
		this.yd = yVel;
		this.zd = zVel;
		this.sprites = spriteSet;

		this.lifetime = (int) (20.0D / (Math.random() * 0.8D + 0.6D));
		this.setSpriteFromAge(spriteSet);
	}

	@Override
	public void tick()
	{
//		super.tick();
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

		if (this.age++ >= this.lifetime)
		{
			this.remove();
		}

		this.move(this.xd, this.yd, this.zd);
        this.setSpriteFromAge(this.sprites);
	}

	public static class DungeonPortalParticleProvider implements ParticleProvider<@NonNull DungeonPortalParticleOptions>
	{
		private final SpriteSet spriteSet;

		public DungeonPortalParticleProvider(SpriteSet spriteSet)
		{
			this.spriteSet = spriteSet;
		}

		@Override
		public @NonNull Particle createParticle(DungeonPortalParticleOptions data, ClientLevel worldIn, double x, double y, double z,
				double xSpeed, double ySpeed, double zSpeed, RandomSource random)
		{
			return new DungeonPortalParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, data.red(), data.green(), data.blue(), this.spriteSet);
		}
	}

	@Override
	protected Layer getLayer()
	{
		return Layer.TRANSLUCENT;
	}
}
