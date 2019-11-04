package com.github.commoble.dungeonfist.world.dungature.interfloor;

import java.util.Random;
import java.util.stream.IntStream;

import com.github.commoble.dungeonfist.util.InterRegionData;
import com.github.commoble.dungeonfist.util.Rect;
import com.github.commoble.dungeonfist.util.Room;
import com.github.commoble.dungeonfist.util.RoomCaches;
import com.github.commoble.dungeonfist.util.RoomKey;
import com.github.commoble.dungeonfist.world.dungature.Dungature;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.IWorld;

public class DeepwellDungature extends Dungature
{
	@Override
	//
	public void place(Rect rect, Rect chunkRect, Room room, IWorld world, Random rand)
	{
		int lowerRoomLayer = room.Y_LAYER - 1;
		RoomKey key = new RoomKey(rect.START.toBlockPos(0), lowerRoomLayer, world.getSeed());
		InterRegionData lowerRegionData = RoomCaches.EXITLOADER.getUnchecked(key);

		int upperRoomFloor = room.WORLD_FLOOR_YLEVEL;
		int lowerRoomCeiling = lowerRegionData.roomFloorLevel + lowerRegionData.roomHeightSize;

		BlockState airState = Blocks.AIR.getDefaultState();
		BlockState wallState = room.STANDARD_BLOCK.getDefaultState();

		if (rect.SIZE.X > 2 && rect.SIZE.Y > 2)
		{
			rect.coords().stream().filter(coord -> chunkRect.contains(coord))
					.forEach(coord -> IntStream.rangeClosed(lowerRoomCeiling, upperRoomFloor).forEach(y -> world
							.setBlockState(coord.toBlockPos(y), rect.isPerimeter(coord) ? wallState : airState, 2)));
		}
		else
		{
			rect.coords().stream().filter(coord -> chunkRect.contains(coord))
					.forEach(coord -> IntStream.rangeClosed(lowerRoomCeiling, upperRoomFloor)
							.forEach(y -> world.setBlockState(coord.toBlockPos(y), airState, 2)));
		}

	}

}
