package net.commoble.dungeonfist;

import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;

public record OctreePieceState(PoolElementStructurePiece piece, SubtractiveOctree octree, int depth)
{
	
}
