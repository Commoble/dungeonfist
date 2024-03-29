package com.github.commoble.dungeonfist.world.dungature.doorway;

import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

import com.github.commoble.dungeonfist.util.BlockCollections;
import com.github.commoble.dungeonfist.util.MathBuddy;
import com.github.commoble.dungeonfist.util.Rect;
import com.github.commoble.dungeonfist.util.Room;
import com.github.commoble.dungeonfist.util.Vec2i;
import com.github.commoble.dungeonfist.world.dungature.Dungature;
import com.github.commoble.dungeonfist.world.dungature.DungatureContext;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class DoorwayDungature extends Dungature
{
	private boolean doorFacesInterior;	// if true, door will generate next to the room interior; if false, door generates next to the outside of the wall
	private int positionHasher;	// some random positive integer
	private DoorBlock doorBlock;
	
	public DoorwayDungature(DungatureContext context)
	{
		Random rand = context.rand;
		this.positionHasher = Math.abs(rand.nextInt());
		this.doorBlock = BlockCollections.DOORS.get(rand.nextInt(BlockCollections.DOORS.size()));
		
		this.doorFacesInterior = rand.nextBoolean();
	}

	@Override
	public void place(Rect rect, Rect chunkRect, Room room, IWorld world, Random rand)
	{
		int minX, sizeX, minZ, sizeZ;
		Vec2i doorPosition;
		int corridorOffset;
		if (this.transform.rotation % 2 != 0)// doorway is along west-east axis
		{
			corridorOffset = (this.positionHasher % rect.SIZE.Y);
			
			minX = rect.START.X;
			sizeX = rect.SIZE.X;
			
			minZ = rect.START.Y + corridorOffset;
			sizeZ = 1;
		}
		else	// doorway corridor is along north-south axis
		{
			corridorOffset = (this.positionHasher % rect.SIZE.X);
			
			minX = rect.START.X + corridorOffset;
			sizeX = 1;
			
			minZ = rect.START.Y;
			sizeZ = rect.SIZE.Y;
		}
		// if door is on interior and corridor is south or east, use minX,minY of rect	// rotation = 0 or 3
		// if door is on exterior and corridor is west or north, use minX,minY of rect
		// otherwise use maxX, maxY of rect
		boolean isWestOrNorth = this.transform.rotation == 1 || this.transform.rotation == 2;
		doorPosition = this.doorFacesInterior ^ isWestOrNorth ? new Vec2i(minX, minZ) : new Vec2i(minX + sizeX - 1, minZ + sizeZ - 1);
		
		Rect doorCorridor = new Rect(new Vec2i(minX, minZ), new Vec2i(sizeX, sizeZ));
		int baseY = room.WORLD_FLOOR_YLEVEL;
		BlockState air = Blocks.AIR.getDefaultState();
		rect.coords().stream().filter(coord -> chunkRect.contains(coord) && doorCorridor.contains(coord))
				.forEach(coord -> IntStream.range(1, 3)
						.forEach(yOff -> world.setBlockState(new BlockPos(coord.X, baseY + yOff, coord.Y), air, 2)));
		
		Optional.of(doorPosition).filter(coord -> chunkRect.contains(coord)).ifPresent(coord -> {
			BlockState lowerState = this.doorBlock.getDefaultState()
					.with(DoorBlock.HALF, DoubleBlockHalf.LOWER)
					.with(DoorBlock.HINGE, rand.nextBoolean() ? DoorHingeSide.LEFT : DoorHingeSide.RIGHT)
					.with(DoorBlock.FACING, Direction.byHorizontalIndex(MathBuddy.absoluteMod(this.transform.rotation + (this.doorFacesInterior ? 0 : 2), 4)));
			BlockState upperState = lowerState.with(DoorBlock.HALF, DoubleBlockHalf.UPPER);
			world.setBlockState(new BlockPos(coord.X, baseY + 1, coord.Y), lowerState, 2);
			world.setBlockState(new BlockPos(coord.X, baseY + 2, coord.Y), upperState, 2);
		});
	}

}
