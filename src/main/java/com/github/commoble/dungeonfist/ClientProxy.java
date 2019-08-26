package com.github.commoble.dungeonfist;

import com.github.commoble.dungeonfist.client.ParticleTypeRegistrar;
import com.github.commoble.dungeonfist.client.particle.DungeonPortalParticleData;

import net.minecraft.world.World;

public class ClientProxy
{
	public void spawnDungeonPortalParticle(World world, double x, double y, double z, double vx, double vy, double vz, float red, float green, float blue)
	{
		//Particle part = new ParticleDungeonPortal(world, x, y, z, vx, vy, vz, red, green, blue);
		//Minecraft.getInstance().particles.addEffect(part);
		world.addParticle(new DungeonPortalParticleData(ParticleTypeRegistrar.DUNGEON_PORTAL, red, green, blue), x, y, z, vx, vy, vz);
	}
}
