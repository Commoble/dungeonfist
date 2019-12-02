package com.github.commoble.dungeonfist.util;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.IChunk;

public class RoomKey
{
	public Vec2i superChunkCoords;
	public Vec2i regionSize;
	public int y;
	public long worldSeed;
	
	public static int MIN_TIER = 0;
	public static int MAX_TIER = 4;
	
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
	
	public RoomKey(BlockPos pos, int yLayer, long worldSeed)
	{
		this(pos.getX() >> 5, pos.getZ() >> 5, yLayer, worldSeed);
	}
	
	public RoomKey(Vec2i pos, int yLayer, long worldSeed)
	{
		this(pos.X >> 5, pos.Y >> 5, yLayer, worldSeed);
	}
	
	/**
	 * Returns the base ylevel of this room in world space
	 */
	
	public int getWorldspaceBaseY()
	{
		return getWorldYFromDungeonTier(this.y);
	}
	
	public static int getWorldYFromDungeonTier(int yTier)
	{
		return yTier*50 + 10;
	}
	
	public static int getDungeonTierFromWorldSpace(int worldY)
	{
		return (worldY-10)/50;
	}
	
	public Set<ChunkPos> getOverworldChunks()
	{
		Set<ChunkPos> set = new HashSet<>();
		for (int x = 0; x < this.regionSize.X; x++)
		{
			for (int z = 0; z < this.regionSize.Y; z++)
			{
				int regionX = this.superChunkCoords.X + x;
				int regionZ = this.superChunkCoords.Y + z;
				
				int overworldChunkX = regionX >> 3;
				int overworldChunkZ = regionZ >> 3;
				
				set.add(new ChunkPos(overworldChunkX, overworldChunkZ));
			}
		}
		return set;
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
	
	@Override
	public int hashCode()
	{
		return this.superChunkCoords.hashCode()+31469 * this.y+31469 + (int)(this.worldSeed >> 32) + (int)(this.worldSeed % Integer.MAX_VALUE);
	}
	
	@Override
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
