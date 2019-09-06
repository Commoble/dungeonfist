package com.github.commoble.dungeonfist.util;

import net.minecraft.util.math.ChunkPos;

public class RoomKey
{
	public Vec2i superChunkCoords;
	public Vec2i regionSize;
	public int yLayer;
	public long worldSeed;
	
	public RoomKey(Vec2i superChunkCoords, Vec2i regionSize, int yLayer, long worldSeed)
	{
		this.superChunkCoords = superChunkCoords;
		this.regionSize = regionSize;
		this.yLayer = yLayer;
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
		return superChunkCoords.hashCode() * yLayer + (int)(worldSeed >> 32) + (int)(worldSeed % Integer.MAX_VALUE);
	}
	
	public boolean equals(Object other)
	{
		if (other instanceof RoomKey)
		{
			RoomKey key = (RoomKey)other;
			return this.superChunkCoords.equals(key.superChunkCoords) &&
					this.regionSize.equals(key.regionSize) &&
					this.yLayer == key.yLayer &&
					this.worldSeed == key.worldSeed;
		}
		else return false;
	}
}
