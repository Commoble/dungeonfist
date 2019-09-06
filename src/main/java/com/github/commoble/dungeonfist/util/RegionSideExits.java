package com.github.commoble.dungeonfist.util;

import java.util.Random;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import net.minecraft.util.math.BlockPos;

public class RegionSideExits
{
	public final int offset;	// difference between 0,0 origin of region and first/minimal position of exit
	public final boolean isOnEastSide;	// if false, is on south side
	public final int exitSize;
	public final RoomKey roomKey;
	
	public RegionSideExits(RoomKey key)
	{
		// each room-containing region of superchunks has at least one exit
		// exits are defined on the east and south sides (can check adjacent regions to determine north and west exits)
		// the Room determines how to get to the exits given its own HallwayData and adjacent rooms' HallwayData
		Random rand = new Random(key.hashCode() + 42069);
		this.roomKey = key;
		this.exitSize = rand.nextInt(4) + 1;	// 1,2,3, or 4
		this.isOnEastSide = rand.nextBoolean();
		
		// minimum offset is 1 (cannot generate on leftmost edge of room
		// maximum offset is (end - hallway width)
		// e.g. region size in blockpocks is 32, hallway width is 1
		// if we ignore the edge restrictions, could generate hallways up to point 31
		// we want to generate hallways up to point 30
		// subtracting 1 (the hallway width) is sufficient
		// but we also want to start at 1
		int min = 1;
		int regionLengthInSuperChunks = (this.isOnEastSide ? key.regionSize.Y : key.regionSize.X);
		int max = (regionLengthInSuperChunks << 5) - this.exitSize;
		
		this.offset = rand.nextInt(max - min) + min;
	}
	
	public IntStream getExitOffsets()
	{
//		System.out.println("Actual chunkpos is " + pos);
//		System.out.println("Room coords is " + this.roomKey.superChunkCoords);
//		System.out.println("Localized chunkpos is " + localizedChunkPos);
//		System.out.println("Hallway offset is " + this.offset);
//		System.out.println("Total offset is" + totalOffset);
		return IntStream.range(0, this.exitSize).map(i -> i + this.offset);
	}
	
	public Stream<BlockPos> getExitBlockPositions()
	{
		int y = this.roomKey.yLayer;
		IntFunction<BlockPos> posMapper = this.isOnEastSide ? z -> new BlockPos(15,y,z) : x -> new BlockPos(x,y,15);
		return this.getExitOffsets().mapToObj(posMapper);
	}
	
}