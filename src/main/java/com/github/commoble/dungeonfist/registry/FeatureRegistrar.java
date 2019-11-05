package com.github.commoble.dungeonfist.registry;

import com.github.commoble.dungeonfist.DungeonFist;
import com.github.commoble.dungeonfist.Registrator;
import com.github.commoble.dungeonfist.world.feature.CarcelithFeature;

import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(DungeonFist.MODID)
public class FeatureRegistrar
{
	@ObjectHolder(DungeonFist.CARCELITH)
	public static final Feature<NoFeatureConfig> CARCELITH = null;
	
	public static void registerFeatures(Registrator<Feature<?>> reg)
	{
//		ConfiguredFeature<?> portalFeature = Biome.createDecoratedFeature(
//				new DungeonPortalFeature(NoFeatureConfig::deserialize),
//				IFeatureConfig.NO_FEATURE_CONFIG,
//				new DungeonPortalPlacement(NoPlacementConfig::deserialize),
//				IPlacementConfig.NO_PLACEMENT_CONFIG);
		
		reg.register(DungeonFist.CARCELITH, new CarcelithFeature(NoFeatureConfig::deserialize));
		
//		ForgeRegistries.BIOMES.forEach(biome -> biome.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, portalFeature));
	}
}
