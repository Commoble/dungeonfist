package com.github.commoble.dungeonfist.registry;

import com.github.commoble.dungeonfist.Registrator;
import com.github.commoble.dungeonfist.world.DungeonPortalFeature;
import com.github.commoble.dungeonfist.world.DungeonPortalPlacement;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraftforge.registries.ForgeRegistries;

public class FeatureRegistrar
{
	public static void registerFeatures(Registrator<Feature<?>> reg)
	{
		ConfiguredFeature<?> feature = Biome.createDecoratedFeature(
				new DungeonPortalFeature(NoFeatureConfig::deserialize),
				IFeatureConfig.NO_FEATURE_CONFIG,
				new DungeonPortalPlacement(NoPlacementConfig::deserialize),
				IPlacementConfig.NO_PLACEMENT_CONFIG);
		
		ForgeRegistries.BIOMES.forEach(biome -> biome.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, feature));
	}
}
