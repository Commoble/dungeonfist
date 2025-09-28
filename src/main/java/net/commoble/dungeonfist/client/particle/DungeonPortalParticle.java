package net.commoble.dungeonfist.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;

public class DungeonPortalParticle extends TextureSheetParticle
{
	public DungeonPortalParticle(ClientLevel world, double x, double y, double z, double xVel, double yVel, double zVel,
			float red, float green, float blue, SpriteSet spriteSet)
	{

		super(world, x, y, z, 0.0D, 0.0D, 0.0D);
		this.rCol = red;
		this.gCol = green;
		this.bCol = blue;
		this.xd = xVel;
		this.yd = yVel;
		this.zd = zVel;

		this.lifetime = (int) (20.0D / (Math.random() * 0.8D + 0.6D));
		this.pickSprite(spriteSet);
	}

	@Override
	public void tick()
	{
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

		if (this.age++ >= this.lifetime)
		{
			this.remove();
		}

		this.move(this.xd, this.yd, this.zd);
	}

	public static class DungeonPortalParticleProvider implements ParticleProvider<DungeonPortalParticleOptions>
	{
		private final SpriteSet spriteSet;

		public DungeonPortalParticleProvider(SpriteSet spriteSet)
		{
			this.spriteSet = spriteSet;
		}

		@Override
		public Particle createParticle(DungeonPortalParticleOptions data, ClientLevel worldIn, double x, double y, double z,
				double xSpeed, double ySpeed, double zSpeed)
		{
			return new DungeonPortalParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, data.red(), data.green(), data.blue(), this.spriteSet);
		}
	}

	@Override
	public ParticleRenderType getRenderType()
	{
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}
}
