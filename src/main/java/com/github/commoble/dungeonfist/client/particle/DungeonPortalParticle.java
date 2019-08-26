package com.github.commoble.dungeonfist.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DungeonPortalParticle extends SpriteTexturedParticle
{
	public DungeonPortalParticle(World world, double x, double y, double z, double xVel, double yVel, double zVel,
			float red, float green, float blue)
	{

		super(world, x, y, z, 0.0D, 0.0D, 0.0D);
		// this.motionX *= 0.10000000149011612D;
		// this.motionY *= 0.10000000149011612D;
		// this.motionZ *= 0.10000000149011612D;
		// this.motionX += xVel * 0.4D;
		// this.motionY += yVel * 0.4D;
		// this.motionZ += zVel * 0.4D;
		//float f = (float) (Math.random() * 0.1 + 0.9);
		this.particleRed = red;
		this.particleGreen = green;
		this.particleBlue = blue;
		this.motionX = xVel;
		this.motionY = yVel;
		this.motionZ = zVel;
		// this.particleScale *= 0.1F; //0.75F;

		this.maxAge = (int) (20.0D / (Math.random() * 0.8D + 0.6D));
		// this.particleMaxAge = (int)((float)this.particleMaxAge * scale);
		// this.setParticleTextureIndex(65);
		this.onUpdate();
	}

	public IParticleRenderType getRenderType()
	{
		return IParticleRenderType.PARTICLE_SHEET_LIT;
	}

	/**
	 * Renders the particle
	 */
	@Override
	public void renderParticle(BufferBuilder buffer, ActiveRenderInfo entityIn, float partialTicks, float rotationX,
			float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
	{
		super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
	}

	public void onUpdate()
	{
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		if (this.age++ >= this.maxAge)
		{
			this.setExpired();
		}

		// this.setParticleTextureIndex(this.baseSpellTextureIndex + (7 - this.age * 8 /
		// this.maxAge));

		this.move(this.motionX, this.motionY, this.motionZ);

		/**
		 * this.particleRed *= 0.98F; this.particleGreen *= 0.98F; this.particleBlue *=
		 * 0.98F;
		 **/

		// this.particleScale *= 0.98F;

		// this.particleGreen = (float)(this.particleGreen * 0.975F);
		// this.particleBlue = (float)(this.particleBlue * 0.95F);
		// this.motionX *= 0.9D;
		// if (this.motionY > 0D) this.motionY *= 0.7D;
		// this.motionZ *= 0.9D;
		// this.motionY -= 0.06D;

		/*
		 * if (this.onGround) { this.motionX *= 0.699999988079071D; this.motionZ *=
		 * 0.699999988079071D; }
		 */
	}

	@OnlyIn(Dist.CLIENT)
	public static class Factory implements IParticleFactory<DungeonPortalParticleData>
	{
		private final IAnimatedSprite spriteSet;

		public Factory(IAnimatedSprite p_i50587_1_)
		{
			this.spriteSet = p_i50587_1_;
		}

		public Particle makeParticle(DungeonPortalParticleData data, World worldIn, double x, double y, double z,
				double xSpeed, double ySpeed, double zSpeed)
		{
			DungeonPortalParticle particle = new DungeonPortalParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, data.red, data.green, data.blue);
			particle.selectSpriteRandomly(this.spriteSet);
			return particle;
		}
	}
}
