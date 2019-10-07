package com.github.commoble.dungeonfist.util;

import java.util.Random;
import java.util.stream.IntStream;

/**
 * Holds room data that may need to be accessed by other rooms
 * (should not refer to other rooms in this class)
 * The purpose of this is to allow rooms to find data belonging to each other without cascading room generation
 */
public class InterRegionData
{
	public final int exitOffset;	// difference between 0,0 origin of region and first/minimal position of exit
	public final boolean exitIsOnEastSide;	// if false, is on south side
	public final int exitSize;
	public final Rect exitAsRectInGlobalSpace;
	public final int roomFloorLevel;	// y in worldspace - define this in here so rooms can check each other's floorlevels
	public final int roomHeightSize;	// only reason this is here is because roomFloorLevel is dependant on it
	
	public InterRegionData(RoomKey key)
	{
		// each room-containing region of superchunks has at least one exit
		// exits are defined on the east and south sides (can check adjacent regions to determine north and west exits)
		// the Room determines how to get to the exits given its own HallwayData and adjacent rooms' HallwayData
		Random rand = new Random(key.hashCode() + 42069);
		this.exitSize = rand.nextInt(4) + 1;	// 1,2,3, or 4
		this.exitIsOnEastSide = rand.nextBoolean();
		
		// minimum offset is 1 (cannot generate on leftmost edge of room
		// maximum offset is (end - hallway width)
		// e.g. region size in blockpocks is 32, hallway width is 1
		// if we ignore the edge restrictions, could generate hallways up to point 31
		// we want to generate hallways up to point 30
		// subtracting 1 (the hallway width) is sufficient
		// but we also want to start at 1
		int min = 1;
		int regionLengthInSuperChunks = (this.exitIsOnEastSide ? key.regionSize.Y : key.regionSize.X);
		int max = (regionLengthInSuperChunks << 5) - this.exitSize;
		
		this.exitOffset = rand.nextInt(max - min) + min;
		
		int absoluteDominantChunkX = (key.superChunkCoords.X << 5); // in absolute world coordinates
		int absoluteDominantChunkZ = (key.superChunkCoords.Y << 5);
		int absoluteExitStartX;
		int absoluteExitStartZ;
		int exitSizeX;
		int exitSizeZ;
		if (this.exitIsOnEastSide)
		{
			absoluteExitStartX = absoluteDominantChunkX + (key.regionSize.X << 5) - 1;
			absoluteExitStartZ = absoluteDominantChunkZ + this.exitOffset;
			exitSizeX = 1;
			exitSizeZ = this.exitSize;
		}
		else
		{
			absoluteExitStartX = absoluteDominantChunkX + this.exitOffset;
			absoluteExitStartZ = absoluteDominantChunkZ + (key.regionSize.Y << 5) - 1;
			exitSizeX = this.exitSize;
			exitSizeZ = 1;
		}
		this.exitAsRectInGlobalSpace = new Rect(new Vec2i(absoluteExitStartX, absoluteExitStartZ), new Vec2i(exitSizeX, exitSizeZ));
		
		// maximum room size (including floor but not ceiling) is 20
		// weight toward shorter rooms
		int roomHeightCounter = rand.nextInt(6);	// 0,1,2,3,4,5
		int height = 5;
		for (int i=0; i < roomHeightCounter; i++)
		{
			height += rand.nextInt(i+1);	// maximum is 5,6,8,11,15,20
		}
		this.roomHeightSize = height;
		
		int baseY = key.getWorldspaceBaseY();
		int minFloor = baseY + Room.FILLER_SIZE;
		int maxCeiling = baseY + Room.BEDROCK_CEILING_OFFSET - Room.FILLER_SIZE - 1;
		int maxFloor = maxCeiling - this.roomHeightSize;
		this.roomFloorLevel = rand.nextInt(maxFloor-minFloor + 1) + minFloor;
	}
	
	public IntStream getExitOffsets()
	{
//		System.out.println("Actual chunkpos is " + pos);
//		System.out.println("Room coords is " + this.roomKey.superChunkCoords);
//		System.out.println("Localized chunkpos is " + localizedChunkPos);
//		System.out.println("Hallway offset is " + this.offset);
//		System.out.println("Total offset is" + totalOffset);
		return IntStream.range(0, this.exitSize).map(i -> i + this.exitOffset);
	}
	
}