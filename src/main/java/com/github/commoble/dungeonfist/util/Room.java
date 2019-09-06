package com.github.commoble.dungeonfist.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class Room
{
	public final Vec2i SUPERCHUNK_COORD;	// dominant superchunk (minimal x and z coord) of the room
	public final int AREA_YLEVEL;
	public final Vec2i REGION_SIZE;	// the width and length of the set of superchunks that enclose this room (in global blockpos scale)
	public final Vec2i ROOM_SIZE;
	// these dimensions and positions are in blockpos scale, and local to the dominant (minimal x/z) chunk of the room
	public final Vec2i OFFSET;
	public final int ROOM_HALLWAY_WIDTH;
	public final Vec2i ROOM_HALLWAY_START;
	public final Vec2i ROOM_HALLWAY_END;
	public final Vec2i ROOM_START;
	public final Vec2i ROOM_END;
	public final Vec2i WALL_MIN;
	public final Vec2i WALL_MAX;
	// inter-room hallways are defined on +x (east) and +z (south) sides
	// north and west hallways are determined by checking the superchunks to the west and north
	// this means that we need to generate inter-room hallways in a manner that can be done without
	// generating the rest of the room to avoid infinite loops
	// solution: create a separate cacheloader for hallway sets using roomkeys
	// each hallway set needs to know a region of superchunks and hallways (1 or 0?) protruding from each superchunk
	// we don't need to care if the hallway exits line up with the actual rooms or not, we can deal with everything
	// within our own superchunks
	public final List<BlockPos> hallwayFloorPositions = new ArrayList<>();
	
	/**
	 */
	public Room(RoomKey key)
	{
		
		this.SUPERCHUNK_COORD = key.superChunkCoords;
		this.AREA_YLEVEL = key.yLayer;
		this.REGION_SIZE = new Vec2i(key.regionSize.X << 5, key.regionSize.Y << 5);
		Random rand = new Random(key.hashCode());
		ROOM_HALLWAY_WIDTH = rand.nextInt(4)+1;	// 1,2,3,4
		
		// maximum possible width/length of the room, within the walls
		int maxXsize = (REGION_SIZE.X) - (ROOM_HALLWAY_WIDTH << 1) - 2;
		int maxZsize = (REGION_SIZE.Y) - (ROOM_HALLWAY_WIDTH << 1) - 2;
		int minSize = 4;
		int roomXsize = rand.nextInt(maxXsize-minSize) + minSize;
		int roomZsize = rand.nextInt(maxZsize-minSize) + minSize;
		int maxXoffset = REGION_SIZE.X - (roomXsize + 2 + ROOM_HALLWAY_WIDTH);
		int maxZoffset = REGION_SIZE.Y - (roomZsize + 2 + ROOM_HALLWAY_WIDTH);
		int roomXoffset = rand.nextInt(maxXoffset);
		int roomZoffset = rand.nextInt(maxZoffset);
		this.ROOM_SIZE = new Vec2i(roomXsize, roomZsize);
		this.OFFSET = new Vec2i(roomXoffset, roomZoffset);
		this.ROOM_HALLWAY_START = OFFSET;
		this.WALL_MIN = new Vec2i(ROOM_HALLWAY_WIDTH + OFFSET.X, ROOM_HALLWAY_WIDTH + OFFSET.Y);
		this.ROOM_START = new Vec2i(WALL_MIN.X + 1, WALL_MIN.Y + 1);
		this.ROOM_END = new Vec2i(ROOM_START.X + ROOM_SIZE.X - 1, ROOM_START.Y + ROOM_SIZE.Y -1);
		this.WALL_MAX = new Vec2i(ROOM_END.X + 1, ROOM_END.Y + 1);
		this.ROOM_HALLWAY_END = new Vec2i(WALL_MAX.X + ROOM_HALLWAY_WIDTH, WALL_MAX.Y + ROOM_HALLWAY_WIDTH);
		
		RegionSideExits exits = RoomCaches.EXITLOADER.getUnchecked(key);
		exits.getExitBlockPositions().forEach(null);
	}
	
	
	// returns a stream of coords with x,z in range[0,15]
	public Stream<Vec2i> getWallPositionsWithinChunk(ChunkPos pos)
	{
		// determine the coords of the walls relative to the given chunk
		int relativeDominantChunkX = ((this.SUPERCHUNK_COORD.X << 1) - pos.x) << 4; // in world coordinates relative to the given chunk
		int relativeDominantChunkZ = ((this.SUPERCHUNK_COORD.Y << 1) - pos.z) << 4;
		int relativeWallStartX = relativeDominantChunkX + this.WALL_MIN.X;
		int relativeWallStartZ = relativeDominantChunkZ + this.WALL_MIN.Y;
		int relativeWallEndX = relativeDominantChunkX + this.WALL_MAX.X;
		int relativeWallEndZ = relativeDominantChunkZ + this.WALL_MAX.Y;
		Set<Vec2i> wallCoords = new HashSet<>();

		int xStart = relativeWallStartX < 0 ? 0 : relativeWallStartX;
		int xEnd = relativeWallEndX > 15 ? 15 : relativeWallEndX;
		int zStart = relativeWallStartZ < 0 ? 0 : relativeWallStartZ;
		int zEnd = relativeWallEndZ > 15 ? 15 : relativeWallEndZ;
		
		//minX segment of wall
		if (relativeWallStartX >= 0 && relativeWallStartX < 16 && !(relativeWallEndZ < 0 || relativeWallStartZ > 15))
		{	// portion of wall is within the chunk's x-domain
			IntStream.range(zStart, zEnd+1).forEach(z -> wallCoords.add(new Vec2i(relativeWallStartX, z)));
		}
		// maxX segment of wall
		if (relativeWallEndX >= 0 && relativeWallEndX < 16 && !(relativeWallEndZ < 0 || relativeWallStartZ > 15))
		{
			IntStream.range(zStart, zEnd+1).forEach(z -> wallCoords.add(new Vec2i(relativeWallEndX, z)));
		}
		// minZ segment of wall
		if (relativeWallStartZ >= 0 && relativeWallStartZ < 16 && !(relativeWallEndX < 0 || relativeWallStartX > 15))
		{
			IntStream.range(xStart, xEnd+1).forEach(x -> wallCoords.add(new Vec2i(x, relativeWallStartZ)));
		}
		if (relativeWallEndZ >= 0 && relativeWallEndZ < 16 && !(relativeWallEndX < 0 || relativeWallStartX > 15))
		{
			IntStream.range(xStart, xEnd+1).forEach(x -> wallCoords.add(new Vec2i(x, relativeWallEndZ)));
		}
		
		return wallCoords.stream();
	}
	
	public Rect getFloorRectWithinChunk(ChunkPos pos)
	{
		int relativeDominantChunkX = ((this.SUPERCHUNK_COORD.X << 1) - pos.x) << 4; // in world coordinates relative to the given chunk
		int relativeDominantChunkZ = ((this.SUPERCHUNK_COORD.Y << 1) - pos.z) << 4;
		int relativeFloorStartX = relativeDominantChunkX + this.ROOM_HALLWAY_START.X;
		int relativeFloorStartZ = relativeDominantChunkZ + this.ROOM_HALLWAY_START.Y;
		int floorSizeX = this.ROOM_HALLWAY_WIDTH*2 + 2 + this.ROOM_SIZE.X;
		int floorSizeY = this.ROOM_HALLWAY_WIDTH*2 + 2 + this.ROOM_SIZE.Y;
		Rect floorRect = new Rect(new Vec2i(relativeFloorStartX, relativeFloorStartZ), new Vec2i(floorSizeX, floorSizeY));
		return floorRect.intersection(Rect.CHUNK_RECT);
	}
}
