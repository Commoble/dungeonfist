package com.github.commoble.dungeonfist.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.ChunkPos;

public class Room
{
	public final Vec2i SUPERCHUNK_COORD;	// dominant superchunk (minimal x and z coord) of the room
	public final int AREA_YLEVEL;	// 0, 1, 2, 3, 4
	public final int WORLD_YLEVEL;	// in global blockspace
	public final Vec2i REGION_SIZE;	// the width and length of the set of superchunks that enclose this room (in global blockpos scale)
	public final Vec2i ROOM_SIZE;
	// bounds of the superchunk region that contains this room
	public final int GLOBAL_WESTMOST_BLOCK;
	public final int GLOBAL_EASTMOST_BLOCK;
	public final int GLOBAL_NORTHMOST_BLOCK;
	public final int GLOBAL_SOUTHMOST_BLOCK;
	public final Vec2i OFFSET_OF_REGION_START;	// beginning of the room's outer hallway ring in global blockspace
	public final Vec2i OFFSET_OF_ROOM_START;
	public final Rect REGION_RECT_IN_GLOBAL_BLOCKSPACE;
	// these dimensions and positions are in blockpos scale, and local to the dominant (minimal x/z) chunk of the room
	public final Vec2i OFFSET_FROM_REGION_START;
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
	public final List<Rect> EXIT_RECTS = new ArrayList<Rect>();
	public final List<Rect> EXIT_HALLWAY_RECTS = new ArrayList<Rect>();
	public final List<Rect> SUBDIVIDED_INTERIOR;
	public final Rect ROOM_HALLWAY_RECT_IN_GLOBAL_SPACE;
	public final Rect ROOM_INTERIOR_RECT_IN_GLOBAL_SPACE;
	/**
	 */
	public Room(RoomKey key)
	{
		
		this.SUPERCHUNK_COORD = key.superChunkCoords;
		this.AREA_YLEVEL = key.y;
		this.WORLD_YLEVEL = this.AREA_YLEVEL*50 + 10;	// change later? needs to be the same as in DungeonChunkGenerator
		this.REGION_SIZE = new Vec2i(key.regionSize.X << 5, key.regionSize.Y << 5);
		this.GLOBAL_WESTMOST_BLOCK = (this.SUPERCHUNK_COORD.X << 5);
		this.GLOBAL_EASTMOST_BLOCK = this.GLOBAL_WESTMOST_BLOCK + this.REGION_SIZE.X - 1;
		this.GLOBAL_NORTHMOST_BLOCK = this.SUPERCHUNK_COORD.Y << 5;
		this.GLOBAL_SOUTHMOST_BLOCK = this.GLOBAL_NORTHMOST_BLOCK + this.REGION_SIZE.Y - 1;
		Random rand = new Random(key.hashCode());
		this.ROOM_HALLWAY_WIDTH = rand.nextInt(4)+1;	// 1,2,3,4
		
		// maximum possible width/length of the room, within the walls
		int maxXsize = (this.REGION_SIZE.X) - (this.ROOM_HALLWAY_WIDTH << 1) - 2;
		int maxZsize = (this.REGION_SIZE.Y) - (this.ROOM_HALLWAY_WIDTH << 1) - 2;
		int minSize = 4;
		int roomXsize = rand.nextInt(maxXsize-minSize) + minSize;
		int roomZsize = rand.nextInt(maxZsize-minSize) + minSize;
		int maxXoffset = this.REGION_SIZE.X - (roomXsize + 2 + this.ROOM_HALLWAY_WIDTH);
		int maxZoffset = this.REGION_SIZE.Y - (roomZsize + 2 + this.ROOM_HALLWAY_WIDTH);
		int roomXoffset = rand.nextInt(maxXoffset);
		int roomZoffset = rand.nextInt(maxZoffset);
		this.ROOM_SIZE = new Vec2i(roomXsize, roomZsize);
		this.OFFSET_FROM_REGION_START = new Vec2i(roomXoffset, roomZoffset);
		this.ROOM_HALLWAY_START = this.OFFSET_FROM_REGION_START;
		this.WALL_MIN = new Vec2i(this.ROOM_HALLWAY_WIDTH + this.OFFSET_FROM_REGION_START.X, this.ROOM_HALLWAY_WIDTH + this.OFFSET_FROM_REGION_START.Y);
		this.ROOM_START = new Vec2i(this.WALL_MIN.X + 1, this.WALL_MIN.Y + 1);
		this.ROOM_END = new Vec2i(this.ROOM_START.X + this.ROOM_SIZE.X - 1, this.ROOM_START.Y + this.ROOM_SIZE.Y -1);
		this.WALL_MAX = new Vec2i(this.ROOM_END.X + 1, this.ROOM_END.Y + 1);
		this.ROOM_HALLWAY_END = new Vec2i(this.WALL_MAX.X + this.ROOM_HALLWAY_WIDTH, this.WALL_MAX.Y + this.ROOM_HALLWAY_WIDTH);
		this.OFFSET_OF_REGION_START = new Vec2i(this.SUPERCHUNK_COORD.X << 5, this.SUPERCHUNK_COORD.Y << 5);
		this.REGION_RECT_IN_GLOBAL_BLOCKSPACE = new Rect(this.OFFSET_OF_REGION_START, this.REGION_SIZE);
		//////// find all exit rects
		// get all superchunks orthagonally adjacent to here northward and westward
		// convert to a Set of RoomKeys
		// each key -> RegionSideExits
		// each RSE -> get rect, adjust into this room
		// add this room's rect too
		Set<RoomKey> adjacentKeys = new HashSet<RoomKey>();	// set of all rooms that touch this room's north and west borders
		IntStream.range(0, key.regionSize.X).forEach(x -> adjacentKeys.add(new RoomKey(x + this.SUPERCHUNK_COORD.X, this.SUPERCHUNK_COORD.Y-1, key.y, key.worldSeed)));
		IntStream.range(0, key.regionSize.Y).forEach(z -> adjacentKeys.add(new RoomKey(this.SUPERCHUNK_COORD.X-1, z + this.SUPERCHUNK_COORD.Y, key.y, key.worldSeed)));
		// now we have all the rooms, so get the exits relevant to this room
		adjacentKeys.stream().map(adjacentKey -> RoomCaches.EXITLOADER.getUnchecked(adjacentKey))
		.map(exit -> exit.asRectInGlobalSpace.move(exit.isOnEastSide ? new Vec2i(1,0) : new Vec2i(0,1)))	// get the exit rects as rects on the other side of the region boundary
		.filter(rect -> !rect.intersection(this.REGION_RECT_IN_GLOBAL_BLOCKSPACE).equals(Rect.EMPTY_RECT))	// only keep the ones inside this region
		.forEach(exit -> this.EXIT_RECTS.add(exit));	// remember the relevant exits
		this.EXIT_RECTS.add(RoomCaches.EXITLOADER.getUnchecked(key).asRectInGlobalSpace);	// also use this room's own east/south exit
		this.OFFSET_OF_ROOM_START = this.OFFSET_FROM_REGION_START.add(this.OFFSET_OF_REGION_START);
		this.ROOM_HALLWAY_RECT_IN_GLOBAL_SPACE = new Rect(
				this.OFFSET_OF_ROOM_START,
				new Vec2i(this.ROOM_HALLWAY_END.X - this.ROOM_HALLWAY_START.X, this.ROOM_HALLWAY_END.Y - this.ROOM_HALLWAY_START.Y));
		
		// create hallways between room and exits
		for (Rect exit : this.EXIT_RECTS)
		{
			Pair<Rect, Rect> extensionResults = this.getExtensionFromExitToFloor(exit);
			Rect firstExtension = extensionResults.getLeft();
			Rect secondExtension = extensionResults.getRight();
			// will need exactly 0, 1, or 2 additional hallway rects per exit
			// case 0: exit is already adjacent to hallway -- do nothing with it
			// case 1: exit is orthagonally adjacent to hallway
			if (!firstExtension.equals(Rect.EMPTY_RECT))
			{
				this.EXIT_HALLWAY_RECTS.add(firstExtension);
			}

			if (!secondExtension.equals(Rect.EMPTY_RECT))
			{
				this.EXIT_HALLWAY_RECTS.add(secondExtension);
			}
		}
		
		this.ROOM_INTERIOR_RECT_IN_GLOBAL_SPACE = new Rect(this.ROOM_START, this.ROOM_SIZE).move(this.OFFSET_OF_REGION_START);
		this.SUBDIVIDED_INTERIOR = this.ROOM_INTERIOR_RECT_IN_GLOBAL_SPACE.asRandomSubdivisions(rand).collect(Collectors.toCollection(ArrayList<Rect>::new));
	}
	
	private Pair<Rect,Rect> getExtensionFromExitToFloor(Rect exit)
	{
		Direction first_dir;
		Direction second_dir;
		if (exit.START.X == this.GLOBAL_WESTMOST_BLOCK && exit.SIZE.X == 1)
		{
			first_dir = Direction.EAST;
			second_dir = (exit.START.Y + exit.SIZE.Y <= this.ROOM_HALLWAY_RECT_IN_GLOBAL_SPACE.START.Y) ? Direction.SOUTH : Direction.NORTH;
		}
		else if (exit.START.Y == this.GLOBAL_NORTHMOST_BLOCK && exit.SIZE.Y == 1)
		{
			first_dir = Direction.SOUTH;
			second_dir = (exit.START.X + exit.SIZE.X <= this.ROOM_HALLWAY_RECT_IN_GLOBAL_SPACE.START.X) ? Direction.EAST : Direction.WEST;
		}
		else if (exit.START.X == this.GLOBAL_EASTMOST_BLOCK && exit.SIZE.X == 1)
		{
			first_dir = Direction.WEST;
			second_dir = (exit.START.Y + exit.SIZE.Y <= this.ROOM_HALLWAY_RECT_IN_GLOBAL_SPACE.START.Y) ? Direction.SOUTH : Direction.NORTH;
		}
		else if (exit.START.Y == this.GLOBAL_SOUTHMOST_BLOCK && exit.SIZE.Y == 1)
		{
			first_dir = Direction.NORTH;
			second_dir = (exit.START.X + exit.SIZE.X <= this.ROOM_HALLWAY_RECT_IN_GLOBAL_SPACE.START.X) ? Direction.EAST : Direction.WEST;
		}
		else return Pair.of(Rect.EMPTY_RECT, Rect.EMPTY_RECT);
		
		Rect firstExtension = exit.extension(this.ROOM_HALLWAY_RECT_IN_GLOBAL_SPACE, first_dir);

		Rect rectToUseToFindSecondExtension = firstExtension.equals(Rect.EMPTY_RECT) ? exit : firstExtension;
		
		// we may want a second extension (but no more than two)

		// firstExtension is guaranteed(?) to be orthagonally reachable from floor
		// secondExtension will run along opposite axis of first extension
		// we don't want to give the second extension a random width at this stage,
		// but it ought to be based on something
		// and it shouldn't necessarily be the same width as the first extension
		int secondWidth = (((int)Math.pow(Math.max(rectToUseToFindSecondExtension.SIZE.X, rectToUseToFindSecondExtension.SIZE.Y), Math.min(rectToUseToFindSecondExtension.SIZE.X, rectToUseToFindSecondExtension.SIZE.Y))) % 4) + 1;
		int maxSecondExtensionSize = first_dir.getAxis() == Axis.X ? rectToUseToFindSecondExtension.SIZE.X : rectToUseToFindSecondExtension.SIZE.Y;
		secondWidth = Math.min(secondWidth, maxSecondExtensionSize);
		
		int dummyXstart, dummyYstart, dummyXsize, dummyYsize;
		// make a dummy rect and extend from that
		if (second_dir.getAxis() == Axis.X)
		{
			dummyXsize = 1;
			dummyYsize = secondWidth;
		}
		else
		{
			dummyXsize = secondWidth;
			dummyYsize = 1;
		}
		
		if (first_dir == Direction.NORTH)
		{
			dummyXstart = second_dir == Direction.WEST ? rectToUseToFindSecondExtension.START.X + rectToUseToFindSecondExtension.SIZE.X : rectToUseToFindSecondExtension.START.X - 1;
			dummyYstart = rectToUseToFindSecondExtension.START.Y - dummyYsize;
		}
		else if (first_dir == Direction.SOUTH)
		{
			dummyXstart = second_dir == Direction.WEST ? rectToUseToFindSecondExtension.START.X + rectToUseToFindSecondExtension.SIZE.X : rectToUseToFindSecondExtension.START.X - 1;
			dummyYstart = rectToUseToFindSecondExtension.START.Y + rectToUseToFindSecondExtension.SIZE.Y;
		}
		else if (first_dir == Direction.WEST)
		{
			dummyXstart = rectToUseToFindSecondExtension.START.X - dummyXsize;
			dummyYstart = second_dir == Direction.NORTH ? rectToUseToFindSecondExtension.START.Y + rectToUseToFindSecondExtension.SIZE.Y: rectToUseToFindSecondExtension.START.Y - 1;
		}
		else // first dir east
		{
			dummyXstart = rectToUseToFindSecondExtension.START.X + rectToUseToFindSecondExtension.SIZE.X;
			dummyYstart = second_dir == Direction.NORTH ? rectToUseToFindSecondExtension.START.Y + rectToUseToFindSecondExtension.SIZE.Y: rectToUseToFindSecondExtension.START.Y - 1;
		}
		
		Rect dummy = new Rect(new Vec2i(dummyXstart, dummyYstart), new Vec2i(dummyXsize, dummyYsize));
		Rect secondExtension = dummy.extension(this.ROOM_HALLWAY_RECT_IN_GLOBAL_SPACE, second_dir);
		return Pair.of(firstExtension, secondExtension);
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
	
	@Nonnull
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
	
	// get the room's exit rects that exist within this chunk, with positions relative to the chunk
	public Stream<Rect> getExitRectsWithinChunk(ChunkPos pos)
	{
		return Rect.getRectCollectionAsRectsWithinChunk(this.EXIT_RECTS.stream(), pos, true);
	}
	
	public Stream<Rect> getExitHallwayRectsWithinChunk(ChunkPos pos)
	{
		return Rect.getRectCollectionAsRectsWithinChunk(this.EXIT_HALLWAY_RECTS.stream(), pos, true);
	}
	
	public Stream<Rect> getRoomInteriorPerimeterWithinChunk(ChunkPos pos, boolean local)
	{
		return Rect.getRectCollectionAsRectsWithinChunk(this.ROOM_INTERIOR_RECT_IN_GLOBAL_SPACE.asPerimeterRects(), pos, local);
	}
}
