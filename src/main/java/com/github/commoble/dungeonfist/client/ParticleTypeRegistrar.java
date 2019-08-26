package com.github.commoble.dungeonfist.client;

import com.github.commoble.dungeonfist.DungeonFist;
import com.github.commoble.dungeonfist.RegistryHelper;
import com.github.commoble.dungeonfist.client.particle.DungeonPortalParticleData;

import net.minecraft.particles.ParticleType;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(DungeonFist.MODID)
public class ParticleTypeRegistrar
{
	//@ObjectHolder(DungeonFist.DUNGEON_PORTAL)
	public static ParticleType<DungeonPortalParticleData> DUNGEON_PORTAL = null;
	
	public static void registerParticleTypes(RegistryHelper<ParticleType<?>> reg)
	{
		//reg.register(DungeonFist.DUNGEON_PORTAL, new BasicParticleType(false));
		DUNGEON_PORTAL = Registry.<ParticleType<DungeonPortalParticleData>>register(Registry.PARTICLE_TYPE, DungeonFist.MODID+":"+DungeonFist.DUNGEON_PORTAL, new ParticleType<>(false, DungeonPortalParticleData.DESERIALIZER));
	}
}
