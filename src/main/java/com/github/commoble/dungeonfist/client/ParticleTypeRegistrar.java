package com.github.commoble.dungeonfist.client;

import com.github.commoble.dungeonfist.DungeonFist;
import com.github.commoble.dungeonfist.client.particle.DungeonPortalParticleData;

import net.minecraft.particles.ParticleType;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(DungeonFist.MODID)
public class ParticleTypeRegistrar
{
	public static ParticleType<DungeonPortalParticleData> DUNGEON_PORTAL = null;
	
	public static void registerParticleTypes()
	{
		// forge registries don't seem to be implemented for this, so we have to use the vanilla registry
		DUNGEON_PORTAL = Registry.<ParticleType<DungeonPortalParticleData>>register(Registry.PARTICLE_TYPE, DungeonFist.MODID+":"+DungeonFist.DUNGEON_PORTAL, new ParticleType<>(false, DungeonPortalParticleData.DESERIALIZER));
	}
}
