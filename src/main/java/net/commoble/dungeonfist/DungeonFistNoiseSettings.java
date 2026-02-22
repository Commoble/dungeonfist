package net.commoble.dungeonfist;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public final class DungeonFistNoiseSettings
{
	private DungeonFistNoiseSettings() {}
	
	public static final ResourceKey<NoiseGeneratorSettings> DUNGEON_CAVES = ResourceKey.create(Registries.NOISE_SETTINGS, DungeonFist.id("dungeon_caves"));
}
