package com.github.commoble.dungeonfist.world;

import com.github.commoble.dungeonfist.world.feature.DungeonRoomDoorwayFeature;
import com.github.commoble.dungeonfist.world.feature.DungeonRoomSubdivisionFeature;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

public class DungeonBiome extends Biome
{
	public DungeonBiome()
	{
		super((new Biome.Builder()).surfaceBuilder(SurfaceBuilder.NOPE, SurfaceBuilder.AIR_CONFIG).precipitation(Biome.RainType.NONE).category(Biome.Category.NONE).depth(0.1F).scale(0.2F).temperature(1.0F).downfall(0.0F).waterColor(4159204).waterFogColor(329011).parent((String)null));
		// add structures
		// add carver
		// add features
//		this.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION,
//				Biome.createDecoratedFeature(
//						new TorchesFeature(NoFeatureConfig::deserialize),
//						IFeatureConfig.NO_FEATURE_CONFIG,
//						new RoomPerimeterPlacement(RoomPerimeterPlacementConfig::deserialize),
//						new RoomPerimeterPlacementConfig(1,4)));
		this.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION,
				Biome.createDecoratedFeature(
						new DungeonRoomSubdivisionFeature(NoFeatureConfig::deserialize),
						IFeatureConfig.NO_FEATURE_CONFIG,
						Placement.NOPE,
						IPlacementConfig.NO_PLACEMENT_CONFIG));
		this.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION,
				Biome.createDecoratedFeature(
						new DungeonRoomDoorwayFeature(NoFeatureConfig::deserialize),
						IFeatureConfig.NO_FEATURE_CONFIG,
						Placement.NOPE,
						IPlacementConfig.NO_PLACEMENT_CONFIG));
		// add mob spawns
	}

}
