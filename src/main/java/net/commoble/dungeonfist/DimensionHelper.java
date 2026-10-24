package net.commoble.dungeonfist;

import java.util.OptionalLong;
import java.util.function.Consumer;

import org.apache.commons.lang3.function.Consumers;
import org.jspecify.annotations.Nullable;

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
	
	public static @Nullable ServerLevel getOrCreateDungeonLevel(MinecraftServer server, ResourceKey<Level> newLevelKey, long seed)
	{
		return getOrRequestDungeonLevel(server, newLevelKey, seed, Consumers.nop());
	}
	
	public static @Nullable ServerLevel getOrRequestDungeonLevel(MinecraftServer server, ResourceKey<Level> newLevelKey, long seed, Consumer<ServerLevel> onCreated)
	{
		RegistryAccess registries = server.registryAccess();
		return InfiniverseAPI.get().getOrRequestLevel(server, newLevelKey, () -> new LevelStem(
			registries.lookupOrThrow(Registries.DIMENSION_TYPE).getOrThrow(DungeonFistDimensionTypes.DUNGEON),
			new NoiseBasedChunkGenerator(
				new FixedBiomeSource(registries.lookupOrThrow(Registries.BIOME).getOrThrow(DungeonFistBiomes.DUNGEON)),
				registries.lookupOrThrow(Registries.NOISE_SETTINGS).getOrThrow(DungeonFistNoiseSettings.DUNGEON_CAVES)
			),
			OptionalLong.of(seed)
		), onCreated);
	}
}
