package net.commoble.dungeonfist.structure_placement;

import com.mojang.serialization.MapCodec;

import net.commoble.dungeonfist.DungeonFist;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

public enum OriginStructurePlacement implements StructurePlacement
{
	INSTANCE;
	
	public static final MapCodec<OriginStructurePlacement> CODEC = MapCodec.unit(INSTANCE);

	@Override
	public boolean isStructureChunk(ChunkGeneratorStructureState state, int sourceX, int sourceZ)
	{
		return sourceX == 0 && sourceZ == 0;
	}

	@Override
	public MapCodec<? extends StructurePlacement> codec()
	{
		return DungeonFist.ORIGIN_STRUCTURE_PLACEMENT_TYPE.get();
	}

}
