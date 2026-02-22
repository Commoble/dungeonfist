package net.commoble.dungeonfist;

import java.util.OptionalLong;

import net.commoble.infiniverse.api.InfiniverseAPI;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;

public final class DimensionHelper
{
	private DimensionHelper() {}
	
	public static ServerLevel getOrCreateDungeonLevel(MinecraftServer server, ResourceKey<Level> newLevelKey, long seed)
	{
		RegistryAccess registries = server.registryAccess();
		return InfiniverseAPI.get().getOrCreateLevel(server, newLevelKey, () -> new LevelStem(
			registries.lookupOrThrow(Registries.DIMENSION_TYPE).getOrThrow(DungeonFistDimensionTypes.DUNGEON),
			new NoiseBasedChunkGenerator(
				new FixedBiomeSource(registries.lookupOrThrow(Registries.BIOME).getOrThrow(DungeonFistBiomes.DUNGEON)),
				registries.lookupOrThrow(Registries.NOISE_SETTINGS).getOrThrow(DungeonFistNoiseSettings.DUNGEON_CAVES)
			),
			OptionalLong.of(seed)
		));
	}
}
