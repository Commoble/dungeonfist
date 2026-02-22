package net.commoble.dungeonfist.structure_placement;

import java.util.Optional;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.commoble.dungeonfist.DungeonFist;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;

public class OriginStructurePlacement extends StructurePlacement
{
	public static final MapCodec<OriginStructurePlacement> CODEC = RecordCodecBuilder.mapCodec(builder -> 
		StructurePlacement.placementCodec(builder)
		.apply(builder, OriginStructurePlacement::new));
	
	protected OriginStructurePlacement(Vec3i locateOffset, FrequencyReductionMethod frequencyReductionMethod, float frequency, int salt, @SuppressWarnings("deprecation") Optional<ExclusionZone> exclusionZone)
	{
		super(locateOffset, frequencyReductionMethod, frequency, salt, exclusionZone);
	}

	@Override
	protected boolean isPlacementChunk(ChunkGeneratorStructureState state, int sourceX, int sourceZ)
	{
		return sourceX == 0 && sourceZ == 0;
	}

	@Override
	public StructurePlacementType<?> type()
	{
		return DungeonFist.ORIGIN_STRUCTURE_PLACEMENT_TYPE.get();
	}

}
