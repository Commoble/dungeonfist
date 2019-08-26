package com.github.commoble.dungeonfist.client;

import com.github.commoble.dungeonfist.client.particle.DungeonPortalParticle;

import net.minecraft.client.Minecraft;

public class ParticleRegistrar
{
	public static void registerParticles()
	{
		Minecraft.getInstance().particles.registerFactory(ParticleTypeRegistrar.DUNGEON_PORTAL, DungeonPortalParticle.Factory::new);
	}
}
