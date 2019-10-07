package com.github.commoble.dungeonfist.util;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.world.chunk.IChunk;

public class ExitHallway
{
	public final Rect rect;
	public final Direction dir;	// points toward the room this hallway is part of
	//public final int otherRoomFloorY; // floor ylevel of the room on the other side of the exit
	public final int endY;	// floor ylevel of the side of the hallway closer to the room this hallway is associated with
	public final int startY;	// floor ylevel of the other side
	public Rect stairsRect = Rect.EMPTY_RECT;
	
	public ExitHallway(Direction dir, Rect rect, int startY, int endY, Random rand)
	{
		this.dir = dir;
		this.rect = rect;
		this.startY = startY;
		this.endY = endY;
		this.allocateStairs(rand);
	}
	
	private void allocateStairs(Random rand)
	{
		int stairsAllocated = Math.abs(this.startY - this.endY);
		int max = this.getSizeAlongMajorAxis() - 2;
		if (stairsAllocated > max)
		{
			stairsAllocated = max;
		}
		if (stairsAllocated < 1)
		{
			this.stairsRect = Rect.EMPTY_RECT;
			return;
		}
		
		if (this.dir.getAxis() == Axis.X)
		{
			int minStartX = this.rect.START.X + 1;
			int maxStartX = this.rect.START.X + this.rect.SIZE.X - 1 - stairsAllocated;
			int startX = rand.nextInt(maxStartX - minStartX + 1) + minStartX;
			this.stairsRect = Rect.of(startX, this.rect.START.Y, stairsAllocated, this.rect.SIZE.Y);
		}
		else
		{
			int minStartY = this.rect.START.Y + 1;
			int maxStartY = this.rect.START.Y + this.rect.SIZE.Y - 1 - stairsAllocated;
			int startY = rand.nextInt(maxStartY - minStartY + 1) + minStartY;
			this.stairsRect = Rect.of(this.rect.START.X, startY, this.rect.SIZE.X, stairsAllocated);
		}
		
	}
	
	public void generateHallwayInLocalChunk(Vec2i chunkStart, Room room, IChunk chunk)
	{
		// exit hallway is in block worldspace, chunkrect is in block worldspace
		if (this.stairsRect.equals(Rect.EMPTY_RECT))
		{
			BlockState state = room.STANDARD_BLOCK.getDefaultState();
			int y = (this.startY + this.endY) / 2; 
			this.rect.coords().stream()
			.map(coord -> coord.subtract(chunkStart))
			.filter(coord -> Rect.CHUNK_RECT.contains(coord))
			.forEach(coord -> {
				chunk.setBlockState(coord.toBlockPos(y), state, false);
			});
		}
		else
		{
			// direction of stairs is the "full" side of the stairs (faces toward the higher floor)
			// direction of hallway points toward the room we are generating in
			Direction stairFacing = this.startY < this.endY ? this.dir : this.dir.getOpposite();
			int minY = Math.min(this.startY, this.endY);
			if (this.dir.getAxis() == Axis.X)
			{
				int stairsStart = this.stairsRect.START.X;
				int stairsEnd = this.stairsRect.START.X + this.stairsRect.SIZE.X - 1;
				BlockState floorState = room.STANDARD_BLOCK.getDefaultState();
				BlockState stairState = DungeonMaterials.stairMap.get(room.STANDARD_BLOCK).getDefaultState().with(StairsBlock.FACING, stairFacing);
				this.rect.coords().stream()
				.map(coord -> new TransformedCoord(coord, coord.subtract(chunkStart)))
				.filter(coord -> Rect.CHUNK_RECT.contains(coord.finalVec))
				.forEach(coord -> {
					if (coord.originalVec.X < stairsStart)
					{
						chunk.setBlockState(coord.finalVec.toBlockPos(this.dir == Direction.EAST ? this.startY : this.endY), floorState, false);
					}
					else if (coord.originalVec.X > stairsEnd)
					{
						chunk.setBlockState(coord.finalVec.toBlockPos(this.dir == Direction.WEST ? this.startY : this.endY), floorState, false);
					}
					else // in stair rect
					{
						int verticalOffset = stairFacing == Direction.EAST 
								? coord.originalVec.X - this.stairsRect.START.X + 1
								: this.stairsRect.SIZE.X + this.stairsRect.START.X - coord.originalVec.X;
						chunk.setBlockState(coord.finalVec.toBlockPos(minY + verticalOffset), stairState, false);
					}
				});
			}
			else
			{
				int stairsStart = this.stairsRect.START.Y;
				int stairsEnd = this.stairsRect.START.Y + this.stairsRect.SIZE.Y - 1;
				BlockState floorState = room.STANDARD_BLOCK.getDefaultState();
				BlockState stairState = DungeonMaterials.stairMap.get(room.STANDARD_BLOCK).getDefaultState().with(StairsBlock.FACING, stairFacing);
				this.rect.coords().stream()
				.map(coord -> new TransformedCoord(coord, coord.subtract(chunkStart)))
				.filter(coord -> Rect.CHUNK_RECT.contains(coord.finalVec))
				.forEach(coord -> {
					if (coord.originalVec.Y < stairsStart)
					{
						chunk.setBlockState(coord.finalVec.toBlockPos(this.dir == Direction.SOUTH ? this.startY : this.endY), floorState, false);
					}
					else if (coord.originalVec.Y > stairsEnd)
					{
						chunk.setBlockState(coord.finalVec.toBlockPos(this.dir == Direction.NORTH ? this.startY : this.endY), floorState, false);
					}
					else // in stair rect
					{
						int verticalOffset = stairFacing == Direction.SOUTH 
								? coord.originalVec.Y - this.stairsRect.START.Y + 1
								: this.stairsRect.SIZE.Y + this.stairsRect.START.Y - coord.originalVec.Y;
						chunk.setBlockState(coord.finalVec.toBlockPos(minY + verticalOffset), stairState, false);
					}
				});
			}
		}
	}
	
	public int getSizeAlongMajorAxis()
	{
		if (this.dir == Direction.WEST || this.dir == Direction.EAST)
		{
			return this.rect.SIZE.X;
		}
		else
		{
			return this.rect.SIZE.Y;
		}
	}
}
