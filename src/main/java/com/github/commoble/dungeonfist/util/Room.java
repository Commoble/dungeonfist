package com.github.commoble.dungeonfist.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import com.github.commoble.dungeonfist.world.dungature.Dungature;
import com.github.commoble.dungeonfist.world.dungature.DungatureContext;
import com.github.commoble.dungeonfist.world.dungature.DungatureTable;
import com.github.commoble.dungeonfist.world.dungature.StandardDungatures;
import com.github.commoble.dungeonfist.world.dungature.doorway.StandardDoorways;
import com.github.commoble.dungeonfist.world.dungature.interfloor.DeepwellDungature;

import net.minecraft.block.Block;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;

public class Room
{
	public static final int FILLER_SIZE = 12;	// minimum guaranteed space under the room's floor and above the room's ceiling (excluding floor, including ceiling)
	public static final int BEDROCK_CEILING_OFFSET = 45;	// height above the room's baseY to put a layer of bedrock
	
	public final int Y_LAYER;
	public final Vec2i SUPER_CHUNK_COORDS;
	public final int WORLD_FLOOR_YLEVEL; // in global blockspace
	public final int HEIGHT_SIZE;
	public final Vec2i REGION_SIZE_IN_BLOCKS; // the width and length of the set of superchunks that enclose this room
												// (in global blockpos scale)
	public final Vec2i INTERIOR_SIZE;
	// bounds of the superchunk region that contains this room
	public final int GLOBAL_WESTMOST_BLOCK;
	public final int GLOBAL_EASTMOST_BLOCK;
	public final int GLOBAL_NORTHMOST_BLOCK;
	public final int GLOBAL_SOUTHMOST_BLOCK;
	public final Vec2i GLOBAL_REGION_START; // beginning of the room's outer hallway ring in global blockspace
	public final Vec2i OFFSET_OF_ROOM_START;
	public final Rect GLOBAL_REGION_RECT_IN_BLOCKSPACE;
	// these dimensions and positions are in blockpos scale, and local to the
	// dominant (minimal x/z) chunk of the room
	public final Vec2i LOCAL_ROOM_AND_HALLWAY_START;
	public final int ROOM_HALLWAY_WIDTH;
	public final int WALL_THICKNESS;
	public final Vec2i LOCAL_ROOM_AND_HALLWAY_END;
	public final Vec2i LOCAL_INTERIOR_START;
	public final Vec2i LOCAL_INTERIOR_END;
	public final Vec2i LOCAL_WALL_START;
	public final Vec2i LOCAL_WALL_END;
	public final Vec2i TOTAL_ROOM_SIZE;
	// inter-room hallways are defined on +x (east) and +z (south) sides
	// north and west hallways are determined by checking the superchunks to the
	// west and north
	// this means that we need to generate inter-room hallways in a manner that can
	// be done without
	// generating the rest of the room to avoid infinite loops
	// solution: create a separate cacheloader for hallway sets using roomkeys
	// each hallway set needs to know a region of superchunks and hallways (1 or 0?)
	// protruding from each superchunk
	// we don't need to care if the hallway exits line up with the actual rooms or
	// not, we can deal with everything
	// within our own superchunks
	public final List<ExitHallway> EXIT_RECTS = new ArrayList<ExitHallway>();
	public final List<ExitHallway> EXIT_HALLWAY_RECTS = new ArrayList<ExitHallway>(); // directions point from exit to room
	public final List<Rect> SUBDIVIDED_INTERIOR;
	public final Rect ROOM_HALLWAY_RECT_IN_GLOBAL_SPACE;
	public final Rect ROOM_WALL_RECT_IN_GLOBAL_SPACE;
	public final Rect ROOM_INTERIOR_RECT_IN_GLOBAL_SPACE;
	// public final EnumeratedDistribution WEIGHTED_DUNGEON_ELEMENTS;
	public final Map<Rect, Dungature> DUNGATURE_MAP;
	public final Map<Rect, Dungature> DOORWAY_MAP;
	public final Block STANDARD_BLOCK;
	/**
	 */
	public Room(RoomKey key)
	{
		// some definitions w.r.t. areas and measurements:
		// the area consists of a 3x3 grid of superchunks, divided into regions of
		// superchunks
		// the region consists of one or more superchunks, defined from the northwest
		// (least X, least Z) superchunk
		// each region contains one Room
		// each superchunk is a grid of 2x2 chunks
		// the Room consists of a room area and one or more exit hallways leading to
		// other regions
		// the exit hallways are not defined within this Room object
		// the room area consists of:
		// a surrounding hallway (or "outer floor") of variable thickness,
		// an exterior wall of variable thickness possibly containing doorways,
		// and an interior area within the walls
		InterRegionData regionData = RoomCaches.EXITLOADER.getUnchecked(key);
		this.Y_LAYER = key.y;
		this.SUPER_CHUNK_COORDS = key.superChunkCoords;
		this.WORLD_FLOOR_YLEVEL = regionData.roomFloorLevel;
		this.HEIGHT_SIZE = regionData.roomHeightSize;
		this.GLOBAL_REGION_START = regionData.globalRegionStart;
		
		this.REGION_SIZE_IN_BLOCKS = regionData.regionSizeInBlocks;
		this.ROOM_HALLWAY_WIDTH = regionData.roomHallwayWidth;
		this.WALL_THICKNESS = regionData.wallThickness;
		this.INTERIOR_SIZE = regionData.interiorSize;
		
		this.LOCAL_ROOM_AND_HALLWAY_START = regionData.localRoomAndHallwayStart;
		this.LOCAL_WALL_START = regionData.localWallStart;
		this.LOCAL_INTERIOR_START = regionData.localInteriorStart;
		this.LOCAL_INTERIOR_END = regionData.localInteriorEnd;
		this.LOCAL_WALL_END = regionData.localWallEnd;
		this.LOCAL_ROOM_AND_HALLWAY_END = regionData.localRoomAndHallwayEnd;
		
		this.OFFSET_OF_ROOM_START = regionData.globalRoomAndHallwayStart;
		
		
		this.GLOBAL_WESTMOST_BLOCK = this.SUPER_CHUNK_COORDS.X << 5;
		this.GLOBAL_EASTMOST_BLOCK = this.GLOBAL_WESTMOST_BLOCK + this.REGION_SIZE_IN_BLOCKS.X - 1;
		this.GLOBAL_NORTHMOST_BLOCK = this.SUPER_CHUNK_COORDS.Y << 5;
		this.GLOBAL_SOUTHMOST_BLOCK = this.GLOBAL_NORTHMOST_BLOCK + this.REGION_SIZE_IN_BLOCKS.Y - 1;
		Random rand = new Random(key.hashCode());
		

		Vec2i interiorSize = regionData.interiorSize;
		int roomSizeX = this.ROOM_HALLWAY_WIDTH * 2 + this.WALL_THICKNESS * 2 + interiorSize.X;
		int roomSizeZ = this.ROOM_HALLWAY_WIDTH * 2 + this.WALL_THICKNESS * 2 + interiorSize.Y;
		this.TOTAL_ROOM_SIZE = new Vec2i(roomSizeX, roomSizeZ);
		this.GLOBAL_REGION_RECT_IN_BLOCKSPACE = regionData.globalRegionRectInBlockspace;
		//////// find all exit rects
		// get all superchunks orthagonally adjacent to here northward and westward
		// convert to a Set of RoomKeys
		// each key -> RegionSideExits
		// each RSE -> get rect, adjust into this room
		// add this room's rect too
		Set<RoomKey> adjacentKeys = new HashSet<RoomKey>(); // set of all rooms that touch this room's north and west
															// borders
		IntStream.range(0, key.regionSize.X).forEach(x -> adjacentKeys
				.add(new RoomKey(x + this.SUPER_CHUNK_COORDS.X, this.SUPER_CHUNK_COORDS.Y - 1, key.y, key.worldSeed)));
		IntStream.range(0, key.regionSize.Y).forEach(z -> adjacentKeys
				.add(new RoomKey(this.SUPER_CHUNK_COORDS.X - 1, z + this.SUPER_CHUNK_COORDS.Y, key.y, key.worldSeed)));
		// now we have all the rooms, so get the exits relevant to this room
		adjacentKeys.stream().map(adjacentKey -> RoomCaches.EXITLOADER.getUnchecked(adjacentKey))
				.map(adjacentRegion -> new ExitHallway(
						adjacentRegion.exitIsOnEastSide ? Direction.EAST : Direction.SOUTH,
						adjacentRegion.exitAsRectInGlobalSpace.move(adjacentRegion.exitIsOnEastSide ? new Vec2i(1, 0) : new Vec2i(0, 1)),
						(adjacentRegion.roomFloorLevel + this.WORLD_FLOOR_YLEVEL) / 2,
						(adjacentRegion.roomFloorLevel + this.WORLD_FLOOR_YLEVEL) / 2,
						rand
						)) // get
																													// the
																													// exit
																													// rects
																													// as
																													// rects
																													// on
																													// the
																													// other
																													// side
																													// of
																													// the
																													// region
																													// boundary
				.filter(hall -> !hall.rect.intersection(this.GLOBAL_REGION_RECT_IN_BLOCKSPACE).equals(Rect.EMPTY_RECT)) // only
																													// keep
																													// the
																													// ones
																													// inside
																													// this
																													// region
				.forEach(hall -> this.EXIT_RECTS.add(hall)); // remember the relevant exits
		
		// also use this room's own hallway -- get the ylevel of the next room over
		boolean exitIsOnEastSide = regionData.exitIsOnEastSide;
		Vec2i someCoordInTheOtherRoom = regionData.exitAsRectInGlobalSpace.START.add(exitIsOnEastSide ? 1 : 0, exitIsOnEastSide ? 0 : 1);
		int superChunkX = someCoordInTheOtherRoom.X >> 5;
		int superChunkY = someCoordInTheOtherRoom.Y >> 5;
		RoomKey otherAdjacentKey = new RoomKey(
				//superChunkCoords.X + (regionData.exitIsOnEastSide ? 1 : 0),
				//superChunkCoords.Y + (regionData.exitIsOnEastSide ? 0 : 1),
				superChunkX,
				superChunkY,
				key.y,
				key.worldSeed);
		InterRegionData otherRegionData = RoomCaches.EXITLOADER.getUnchecked(otherAdjacentKey); // need this for ylevel
		
		ExitHallway hallway = new ExitHallway(
				regionData.exitIsOnEastSide ? Direction.WEST : Direction.NORTH,
				regionData.exitAsRectInGlobalSpace,
				(otherRegionData.roomFloorLevel + this.WORLD_FLOOR_YLEVEL) / 2,
				(otherRegionData.roomFloorLevel + this.WORLD_FLOOR_YLEVEL) / 2,
				rand);
		this.EXIT_RECTS.add(hallway); // also use this room's own
																							// east/south exit
		this.ROOM_HALLWAY_RECT_IN_GLOBAL_SPACE = regionData.globalRoomAndHallwayRect;

		// create hallways between room and exits
		for (ExitHallway exit : this.EXIT_RECTS)
		{
			Pair<ExitHallway, ExitHallway> extensionResults = this.getExtensionFromExitToFloor(exit, rand);
			ExitHallway firstExtension = extensionResults.getLeft();
			ExitHallway secondExtension = extensionResults.getRight();
			// will need exactly 0, 1, or 2 additional hallway rects per exit
			// case 0: exit is already adjacent to hallway -- do nothing with it
			// case 1: exit is orthagonally adjacent to hallway
			if (!firstExtension.rect.equals(Rect.EMPTY_RECT))
			{
				this.EXIT_HALLWAY_RECTS.add(firstExtension);
			}
			if (!secondExtension.rect.equals(Rect.EMPTY_RECT))
			{
				this.EXIT_HALLWAY_RECTS.add(secondExtension);
			}
		}

		this.ROOM_INTERIOR_RECT_IN_GLOBAL_SPACE = new Rect(this.LOCAL_INTERIOR_START, this.INTERIOR_SIZE)
				.move(this.GLOBAL_REGION_START);
		Vec2i wallSize = new Vec2i(this.INTERIOR_SIZE.X + this.WALL_THICKNESS * 2,
				this.INTERIOR_SIZE.Y + this.WALL_THICKNESS * 2);
		this.ROOM_WALL_RECT_IN_GLOBAL_SPACE = new Rect(this.LOCAL_WALL_START, wallSize).move(this.GLOBAL_REGION_START);
		this.SUBDIVIDED_INTERIOR = this.ROOM_INTERIOR_RECT_IN_GLOBAL_SPACE.asRandomSubdivisions(rand)
				.collect(Collectors.toCollection(ArrayList<Rect>::new));
		// let the placer divide rects further along chunk lines so we don't have to
		// deal with multiblock elements being generated across chunks
		// alternatively, have the Room decide what Element to place in each rect ahead
		// of time and have the placer
		this.DUNGATURE_MAP = this.generateDungatureMap(this.getDungatureTable(), rand, key);
		this.DOORWAY_MAP = this.generateDoorwayMap(this.getDoorwayTable(), rand);
		this.STANDARD_BLOCK = this.generateStandardBlock(key, rand);
		
	}
	
	protected Block generateStandardBlock(RoomKey key, Random rand)
	{
		int materialCount = DungeonMaterials.dungeonMaterials.size();
		if (rand.nextInt(6) > 0)
		{
			int areaX = MathBuddy.rescaleCoordinate(key.superChunkCoords.X + rand.nextInt(3)-1, 3);
			int areaY = MathBuddy.rescaleCoordinate(key.superChunkCoords.Y + rand.nextInt(3)-1, 3);
			int hasher = MathBuddy.absoluteMod(((areaX + areaY*2999) ^ (key.y+2)), materialCount);
			return DungeonMaterials.dungeonMaterials.get(hasher);
		}
		else
		{
			return DungeonMaterials.dungeonMaterials.get(rand.nextInt(DungeonMaterials.dungeonMaterials.size()));
		}
	}

	public DungatureTable getDungatureTable()
	{
		return StandardDungatures.table;
	}
	
	public DungatureTable getDoorwayTable()
	{
		return StandardDoorways.table;
	}

	public Map<Rect, Dungature> generateDoorwayMap(DungatureTable table, Random rand)
	{
		Map<Rect, Dungature> map = new HashMap<>();

		Vec2i northSouthSize = new Vec2i(this.INTERIOR_SIZE.X, this.WALL_THICKNESS);
		Vec2i westEastSize = new Vec2i(this.WALL_THICKNESS, this.INTERIOR_SIZE.Y);

		int northXStart = this.ROOM_INTERIOR_RECT_IN_GLOBAL_SPACE.START.X;
		int southXStart = northXStart;
		int northYStart = this.ROOM_WALL_RECT_IN_GLOBAL_SPACE.START.Y;
		int southYStart = northYStart + this.WALL_THICKNESS + this.INTERIOR_SIZE.Y;
		
		int westXStart =this.ROOM_WALL_RECT_IN_GLOBAL_SPACE.START.X;
		int eastXStart = westXStart + this.WALL_THICKNESS + this.INTERIOR_SIZE.X;
		int westYStart = this.ROOM_INTERIOR_RECT_IN_GLOBAL_SPACE.START.Y;
		int eastYStart = westYStart;
		
		Vec2i northStart = new Vec2i(northXStart, northYStart);
		Vec2i southStart = new Vec2i(southXStart, southYStart);
		Vec2i westStart = new Vec2i(westXStart, westYStart);
		Vec2i eastStart = new Vec2i(eastXStart, eastYStart);
		
		Rect northRect = new Rect(northStart, northSouthSize);
		Rect southRect = new Rect(southStart, northSouthSize);
		Rect westRect = new Rect(westStart, westEastSize);
		Rect eastRect = new Rect(eastStart, westEastSize);
		
		// by forge convention, south=0, west=1, north=2, east=3
		// we'd be wanting to generate the doorway dungatures as if the interior was to the north and the exterior was to the south
		southRect.asRandomSubdivisions(rand, false, true).forEach(rect -> this.mapDoorwayRect(map, table, rect, rand, 0));
		westRect.asRandomSubdivisions(rand, true, false).forEach(rect -> this.mapDoorwayRect(map, table, rect, rand, 1));
		northRect.asRandomSubdivisions(rand, false, true).forEach(rect -> this.mapDoorwayRect(map, table, rect, rand, 2));
		eastRect.asRandomSubdivisions(rand, true, false).forEach(rect -> this.mapDoorwayRect(map, table, rect, rand, 3));

		return map;
	}
	
	public void mapDoorwayRect(Map<Rect, Dungature> map, DungatureTable table, Rect rect, Random rand, int rotation)
	{
		map.put(rect, table.next(rect.minSize(), rand).apply(new DungatureContext(rect, this, rand)).withRotation(rotation));
	}

//	public DoorwayData getRandomDoorData(Direction dir, Random rand)
//	{
//		// figure out where to start door from
//		Vec2i start = this.getDoorStartingPos(dir, rand);
//		return this.generateDoorway(dir, start, rand);
//	}
//	
//	public DoorwayData generateDoorway(Direction dir, Vec2i start, Random rand)
//	{
//		if (rand.nextInt(3) > 0)
//		{
//			Consumer<Rect> placer = rect -> this.getDoorwayCoords(dir,start);
//		}
//		return null;
//	}
//	
//	public Stream<Pair<Integer, BlockPos>> getDoorwayCoords(Direction dir, Vec2i start)
//	{
//		return IntStream.range(0,  this.WALL_THICKNESS).mapToObj(i -> Pair.of(i, new BlockPos(start.X, this.WORLD_YLEVEL, start.Y).offset(dir, i)));
//	}
//
//	public Vec2i getDoorStartingPos(Direction dir, Random rand)
//	{
//		int minX = this.ROOM_INTERIOR_RECT_IN_GLOBAL_SPACE.START.X; // left wall edge = minX-1
//		int maxX = minX + this.ROOM_INTERIOR_RECT_IN_GLOBAL_SPACE.SIZE.X - 1; // right wall edge = maxY+1
//		int x = minX + rand.nextInt(maxX - minX);
//
//		int minY = this.ROOM_INTERIOR_RECT_IN_GLOBAL_SPACE.START.Y; // top wall edge = minY-1
//		int maxY = minY + this.ROOM_INTERIOR_RECT_IN_GLOBAL_SPACE.SIZE.Y - 1; // bottom wall edge = maxY+1
//		int y = minY + rand.nextInt(maxY - minY);
//
//		// now we have a vector somewhere in the interior
//		if (dir == Direction.NORTH)
//		{
//			return new Vec2i(x, minY - 1);
//		}
//		else if (dir == Direction.SOUTH)
//		{
//			return new Vec2i(x, maxY + 1);
//		}
//		else if (dir == Direction.WEST)
//		{
//			return new Vec2i(minX-1, y);
//		}
//		else
//		{
//			return new Vec2i(maxX+1, y);
//		}
//	}

	public Map<Rect, Dungature> generateDungatureMap(DungatureTable weightedTable, Random rand, RoomKey key)
	{
		Map<Rect, Dungature> map = new HashMap<>();
		this.SUBDIVIDED_INTERIOR
				.forEach(rect -> {
					if (rand.nextDouble() < 0.1F && key.y > 0 && !RoomCaches.EXITLOADER.getUnchecked(new RoomKey(rect.START, key.y-1, key.worldSeed)).globalRoomAndHallwayRect.intersection(rect).equals(Rect.EMPTY_RECT))
					{
						map.put(rect, new DeepwellDungature());	// TODO add support for arbitrary interroom dungatures
					}
					else
					{
						map.put(rect, weightedTable.next(rect.minSize(), rand).apply(new DungatureContext(rect, this, rand)));
					}
					
				});
		return map;
	}
	
	public int getCeilingLevel()
	{
		return this.WORLD_FLOOR_YLEVEL + this.HEIGHT_SIZE;
	}

	private Pair<ExitHallway, ExitHallway> getExtensionFromExitToFloor(ExitHallway exit, Random rand)
	{
		Direction first_dir;
		Direction second_dir;
		Rect rect = exit.rect;
		if (rect.START.X == this.GLOBAL_WESTMOST_BLOCK && rect.SIZE.X == 1)
		{
			first_dir = Direction.EAST;
			second_dir = (rect.START.Y + rect.SIZE.Y <= this.ROOM_HALLWAY_RECT_IN_GLOBAL_SPACE.START.Y)
					? Direction.SOUTH
					: Direction.NORTH;
		} else if (rect.START.Y == this.GLOBAL_NORTHMOST_BLOCK && rect.SIZE.Y == 1)
		{
			first_dir = Direction.SOUTH;
			second_dir = (rect.START.X + rect.SIZE.X <= this.ROOM_HALLWAY_RECT_IN_GLOBAL_SPACE.START.X) ? Direction.EAST
					: Direction.WEST;
		} else if (rect.START.X == this.GLOBAL_EASTMOST_BLOCK && rect.SIZE.X == 1)
		{
			first_dir = Direction.WEST;
			second_dir = (rect.START.Y + rect.SIZE.Y <= this.ROOM_HALLWAY_RECT_IN_GLOBAL_SPACE.START.Y)
					? Direction.SOUTH
					: Direction.NORTH;
		} else if (rect.START.Y == this.GLOBAL_SOUTHMOST_BLOCK && rect.SIZE.Y == 1)
		{
			first_dir = Direction.NORTH;
			second_dir = (rect.START.X + rect.SIZE.X <= this.ROOM_HALLWAY_RECT_IN_GLOBAL_SPACE.START.X) ? Direction.EAST
					: Direction.WEST;
		} else
		{
			ExitHallway empty = new ExitHallway(exit.dir, Rect.EMPTY_RECT, 0,0, rand);
			return Pair.of(empty, empty);
		}

		Rect firstExtension = rect.extension(this.ROOM_HALLWAY_RECT_IN_GLOBAL_SPACE, first_dir);

		Rect rectToUseToFindSecondExtension = firstExtension.equals(Rect.EMPTY_RECT) ? rect : firstExtension;

		// we may want a second extension (but no more than two)

		// firstExtension is guaranteed(?) to be orthagonally reachable from floor
		// secondExtension will run along opposite axis of first extension
		// we don't want to give the second extension a random width at this stage,
		// but it ought to be based on something
		// and it shouldn't necessarily be the same width as the first extension
		int secondWidth = (((int) Math.pow(
				Math.max(rectToUseToFindSecondExtension.SIZE.X, rectToUseToFindSecondExtension.SIZE.Y),
				Math.min(rectToUseToFindSecondExtension.SIZE.X, rectToUseToFindSecondExtension.SIZE.Y))) % 4) + 1;
		int maxSecondExtensionSize = first_dir.getAxis() == Axis.X ? rectToUseToFindSecondExtension.SIZE.X
				: rectToUseToFindSecondExtension.SIZE.Y;
		secondWidth = Math.min(secondWidth, maxSecondExtensionSize);

		int dummyXstart, dummyYstart, dummyXsize, dummyYsize;
		// make a dummy rect and extend from that
		if (second_dir.getAxis() == Axis.X)
		{
			dummyXsize = 1;
			dummyYsize = secondWidth;
		} else
		{
			dummyXsize = secondWidth;
			dummyYsize = 1;
		}

		if (first_dir == Direction.NORTH)
		{
			dummyXstart = second_dir == Direction.WEST
					? rectToUseToFindSecondExtension.START.X + rectToUseToFindSecondExtension.SIZE.X
					: rectToUseToFindSecondExtension.START.X - 1;
			dummyYstart = rectToUseToFindSecondExtension.START.Y - dummyYsize;
		} else if (first_dir == Direction.SOUTH)
		{
			dummyXstart = second_dir == Direction.WEST
					? rectToUseToFindSecondExtension.START.X + rectToUseToFindSecondExtension.SIZE.X
					: rectToUseToFindSecondExtension.START.X - 1;
			dummyYstart = rectToUseToFindSecondExtension.START.Y + rectToUseToFindSecondExtension.SIZE.Y;
		} else if (first_dir == Direction.WEST)
		{
			dummyXstart = rectToUseToFindSecondExtension.START.X - dummyXsize;
			dummyYstart = second_dir == Direction.NORTH
					? rectToUseToFindSecondExtension.START.Y + rectToUseToFindSecondExtension.SIZE.Y
					: rectToUseToFindSecondExtension.START.Y - 1;
		} else // first dir east
		{
			dummyXstart = rectToUseToFindSecondExtension.START.X + rectToUseToFindSecondExtension.SIZE.X;
			dummyYstart = second_dir == Direction.NORTH
					? rectToUseToFindSecondExtension.START.Y + rectToUseToFindSecondExtension.SIZE.Y
					: rectToUseToFindSecondExtension.START.Y - 1;
		}

		Rect dummy = new Rect(new Vec2i(dummyXstart, dummyYstart), new Vec2i(dummyXsize, dummyYsize));
		Rect secondExtension = dummy.extension(this.ROOM_HALLWAY_RECT_IN_GLOBAL_SPACE, second_dir);
		int yStart, yMiddle, yEnd;
		yStart = exit.endY;
		yEnd = this.WORLD_FLOOR_YLEVEL;
		if (secondExtension.equals(Rect.EMPTY_RECT))
		{
			yMiddle = yEnd;
		}
		else
		{
			double firstLength = firstExtension.getAxisSize(first_dir.getAxis());
			double secondLength = secondExtension.getAxisSize(second_dir.getAxis());
			double total = firstLength + secondLength;
			yMiddle = (int)MathHelper.lerp(firstLength / total, yStart, yEnd);
		}
		return Pair.of(new ExitHallway(first_dir, firstExtension, yStart, yMiddle, rand), new ExitHallway(second_dir, secondExtension, yMiddle, yEnd, rand));
	}

	// returns a stream of coords with x,z in range[0,15]
	public Stream<Vec2i> getWallPositionsWithinChunk(ChunkPos pos)
	{
		// get all Vec2i coordinates that
		// exist within the greater wall rect and
		// do not exist within the interior floor rect and
		// exist within the given chunk's rect
		// determine the coords of the walls relative to the given chunk

		// get the wall rect and subtract the floor rect
		return Rect
				.getRectCollectionAsRectsWithinChunk(
						this.ROOM_WALL_RECT_IN_GLOBAL_SPACE.excise(this.ROOM_INTERIOR_RECT_IN_GLOBAL_SPACE), pos, true)
				.map(rect -> rect.coords().stream()).flatMap(i -> i);

		// int relativeDominantChunkX = ((this.SUPERCHUNK_COORD.X << 1) - pos.x) << 4;
		// // in world coordinates relative to the given chunk
		// int relativeDominantChunkZ = ((this.SUPERCHUNK_COORD.Y << 1) - pos.z) << 4;
		// int relativeWallStartX = relativeDominantChunkX + this.LOCAL_WALL_START.X;
		// int relativeWallStartZ = relativeDominantChunkZ + this.LOCAL_WALL_START.Y;
		// int relativeWallEndX = relativeDominantChunkX + this.LOCAL_WALL_END.X;
		// int relativeWallEndZ = relativeDominantChunkZ + this.LOCAL_WALL_END.Y;
		// Set<Vec2i> wallCoords = new HashSet<>();
		//
		// int xStart = relativeWallStartX < 0 ? 0 : relativeWallStartX;
		// int xEnd = relativeWallEndX > 15 ? 15 : relativeWallEndX;
		// int zStart = relativeWallStartZ < 0 ? 0 : relativeWallStartZ;
		// int zEnd = relativeWallEndZ > 15 ? 15 : relativeWallEndZ;
		//
		// //minX segment of wall
		// if (relativeWallStartX >= 0 && relativeWallStartX < 16 && !(relativeWallEndZ
		// < 0 || relativeWallStartZ > 15))
		// { // portion of wall is within the chunk's x-domain
		// IntStream.range(zStart, zEnd+1).forEach(z -> wallCoords.add(new
		// Vec2i(relativeWallStartX, z)));
		// }
		// // maxX segment of wall
		// if (relativeWallEndX >= 0 && relativeWallEndX < 16 && !(relativeWallEndZ < 0
		// || relativeWallStartZ > 15))
		// {
		// IntStream.range(zStart, zEnd+1).forEach(z -> wallCoords.add(new
		// Vec2i(relativeWallEndX, z)));
		// }
		// // minZ segment of wall
		// if (relativeWallStartZ >= 0 && relativeWallStartZ < 16 && !(relativeWallEndX
		// < 0 || relativeWallStartX > 15))
		// {
		// IntStream.range(xStart, xEnd+1).forEach(x -> wallCoords.add(new Vec2i(x,
		// relativeWallStartZ)));
		// }
		// if (relativeWallEndZ >= 0 && relativeWallEndZ < 16 && !(relativeWallEndX < 0
		// || relativeWallStartX > 15))
		// {
		// IntStream.range(xStart, xEnd+1).forEach(x -> wallCoords.add(new Vec2i(x,
		// relativeWallEndZ)));
		// }
		//
		// return wallCoords.stream();
	}

	@Nonnull
	public Rect getOuterFloorRectWithinChunk(RoomKey key, ChunkPos pos)
	{
		int relativeDominantChunkX = ((key.superChunkCoords.X << 1) - pos.x) << 4; // in block coordinates relative to
																					// the given chunk
		int relativeDominantChunkZ = ((key.superChunkCoords.Y << 1) - pos.z) << 4;
		int relativeFloorStartX = relativeDominantChunkX + this.LOCAL_ROOM_AND_HALLWAY_START.X;
		int relativeFloorStartZ = relativeDominantChunkZ + this.LOCAL_ROOM_AND_HALLWAY_START.Y;
		Rect floorRect = new Rect(new Vec2i(relativeFloorStartX, relativeFloorStartZ), this.TOTAL_ROOM_SIZE);
		return floorRect.intersection(Rect.CHUNK_RECT);
	}

	public Stream<Rect> getRoomInteriorPerimeterWithinChunk(ChunkPos pos, boolean local)
	{
		return Rect.getRectCollectionAsRectsWithinChunk(this.ROOM_INTERIOR_RECT_IN_GLOBAL_SPACE.asPerimeterRects(), pos,
				local);
	}
}
