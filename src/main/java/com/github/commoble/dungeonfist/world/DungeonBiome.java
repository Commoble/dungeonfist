package com.github.commoble.dungeonfist.world;

import com.github.commoble.dungeonfist.world.feature.TorchesFeature;
import com.github.commoble.dungeonfist.world.placement.RoomPerimeterPlacement;
import com.github.commoble.dungeonfist.world.placement.RoomPerimeterPlacementConfig;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

public class DungeonBiome extends Biome
{
	public DungeonBiome()
	{
		super((new Biome.Builder()).surfaceBuilder(SurfaceBuilder.NOPE, SurfaceBuilder.AIR_CONFIG).precipitation(Biome.RainType.NONE).category(Biome.Category.NONE).depth(0.1F).scale(0.2F).temperature(1.0F).downfall(0.0F).waterColor(4159204).waterFogColor(329011).parent((String)null));
		// add structures
		// add carver
		// add features
		this.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION,
				Biome.createDecoratedFeature(
						new TorchesFeature(NoFeatureConfig::deserialize),
						IFeatureConfig.NO_FEATURE_CONFIG,
						new RoomPerimeterPlacement(RoomPerimeterPlacementConfig::deserialize),
						new RoomPerimeterPlacementConfig(1,4)));
		// add mob spawns
	}

}
