package com.github.commoble.dungeonfist.util;

import java.util.Random;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.IChunk;

public class RoomKey
{
	public Vec2i superChunkCoords;
	public Vec2i regionSize;
	public int y;
	public long worldSeed;
	
	public RoomKey(IChunk chunk, int y, long worldSeed)
	{
		ChunkPos chunkpos = chunk.getPos();

		int chunkX = chunkpos.x;
		int chunkZ = chunkpos.z;
		int superChunkX = chunkX >> 1;
		int superChunkZ = chunkZ >> 1;
		int areaX = MathBuddy.rescaleCoordinate(superChunkX,3);
		int areaZ = MathBuddy.rescaleCoordinate(superChunkZ,3);

		Random areaRand = new Random((areaZ*31 + y)*31+areaX);
		AreaGrid grid = AreaGrid.grids.get(areaRand.nextInt(AreaGrid.GRIDCOUNT));
		int gridX = MathBuddy.absoluteMod(superChunkX, 3);
		int gridZ = MathBuddy.absoluteMod(superChunkZ, 3);
		Vec2i gridOffsetHere = grid.offsets[gridX][gridZ];
		Vec2i dominantSuperChunkCoords = new Vec2i(areaX*3 + gridOffsetHere.X, areaZ*3 + gridOffsetHere.Y);
		this.superChunkCoords = dominantSuperChunkCoords;
		this.regionSize = grid.sizes[gridX][gridZ];
		this.y = y;
		this.worldSeed = worldSeed;
	}
	
	public RoomKey(int superChunkX, int superChunkZ, int y, long worldSeed)
	{
		int areaX = MathBuddy.rescaleCoordinate(superChunkX,3);
		int areaZ = MathBuddy.rescaleCoordinate(superChunkZ,3);

		Random areaRand = new Random((areaZ*31 + y)*31+areaX);
		AreaGrid grid = AreaGrid.grids.get(areaRand.nextInt(AreaGrid.GRIDCOUNT));
		int gridX = MathBuddy.absoluteMod(superChunkX, 3);
		int gridZ = MathBuddy.absoluteMod(superChunkZ, 3);
		Vec2i gridOffsetHere = grid.offsets[gridX][gridZ];
		Vec2i dominantSuperChunkCoords = new Vec2i(areaX*3 + gridOffsetHere.X, areaZ*3 + gridOffsetHere.Y);
		this.superChunkCoords = dominantSuperChunkCoords;
		this.regionSize = grid.sizes[gridX][gridZ];
		this.y = y;
		this.worldSeed = worldSeed;
	}
	
	public RoomKey(Vec2i superChunkCoords, Vec2i regionSize, int yLayer, long worldSeed)
	{
		this.superChunkCoords = superChunkCoords;
		this.regionSize = regionSize;
		this.y = yLayer;
		this.worldSeed = worldSeed;
	}
	
	/**
	 * returns a ChunkPos indicating the relative position of a chunkpos to this room's upper-left chunk
	 * e.g. the upper-left chunk is 0,0, the next chunk to the right is 1,0, etc
	 */
	public ChunkPos getLocalizedChunkPos(ChunkPos pos)
	{
		int superChunkXinChunkCoords = this.superChunkCoords.X << 1;
		int superChunkZinChunkCoords = this.superChunkCoords.Y << 1;
		return new ChunkPos(pos.x - superChunkXinChunkCoords, pos.z - superChunkZinChunkCoords);
	}
	
	public int hashCode()
	{
		return superChunkCoords.hashCode() * y + (int)(worldSeed >> 32) + (int)(worldSeed % Integer.MAX_VALUE);
	}
	
	public boolean equals(Object other)
	{
		if (other instanceof RoomKey)
		{
			RoomKey key = (RoomKey)other;
			return this.superChunkCoords.equals(key.superChunkCoords) &&
					this.regionSize.equals(key.regionSize) &&
					this.y == key.y &&
					this.worldSeed == key.worldSeed;
		}
		else return false;
	}
}
