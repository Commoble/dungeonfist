package net.commoble.dungeonfist.datagen;

import java.util.stream.Collectors;

import net.commoble.dungeonfist.DungeonFistBiomes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.BiomeBuilder;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;

public class DungeonFistBiomeDataGen
{	
	public static void gatherData(BootstrapContext<Biome> bootstrap)
	{
		bootstrap.register(DungeonFistBiomes.DUNGEON, new BiomeBuilder()
			.downfall(0)
			.temperature(0)
			.hasPrecipitation(true)
			.generationSettings(baseFeatures(bootstrap).build())
			.mobSpawnSettings(baseMobs().build())
			.specialEffects(baseEffects().build())
			.build());
	}
	
	public static BiomeSpecialEffects.Builder baseEffects()
	{
		return new BiomeSpecialEffects.Builder()
			// same as plains
			.waterColor(0x3f76e4);
	}
	
	public static BiomeGenerationSettings.Builder baseFeatures(BootstrapContext<Biome> bootstrap)
	{
		var placedFeatures = bootstrap.lookup(Registries.PLACED_FEATURE);
		var carvers = bootstrap.lookup(Registries.CARVER);
		var builder = new BiomeGenerationSettings.Builder(placedFeatures,carvers);

        BiomeDefaultFeatures.addDefaultCarversAndLakes(builder);
        BiomeDefaultFeatures.addDefaultCrystalFormations(builder);
        BiomeDefaultFeatures.addDefaultMonsterRoom(builder);
        BiomeDefaultFeatures.addDefaultUndergroundVariety(builder);
        BiomeDefaultFeatures.addDefaultSprings(builder);
		
		return builder;
	}
	
	public static MobSpawnSettings.Builder baseMobs()
	{
		var builder = new VisibleMobSpawnSettingsBuilder();
		BiomeDefaultFeatures.farmAnimals(builder);
		BiomeDefaultFeatures.commonSpawns(builder);
//		for (EntityType<?> type : builder.getSpawns()
//			.values()
//			.stream()
//			.flatMap(b -> b.getList().stream())
//			.map(w -> w.value().type())
//			.filter(type -> type.getCategory() == MobCategory.MONSTER)
//			.collect(Collectors.toSet()))
//		{
//			builder.addMobSpawnCost(type, 0.7D, 1D);
//		}
		return builder;
	}
}
