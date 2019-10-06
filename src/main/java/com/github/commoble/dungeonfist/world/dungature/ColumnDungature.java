package com.github.commoble.dungeonfist.world.dungature;

import java.util.Random;
import java.util.stream.IntStream;

import com.github.commoble.dungeonfist.util.Rect;
import com.github.commoble.dungeonfist.util.Room;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class ColumnDungature extends Dungature
{

	@Override
	public void place(Rect rect, Rect chunkRect, Room room, IWorld world, Random random)
	{
		BlockState state = room.STANDARD_BLOCK.getDefaultState();
		int baseY = room.WORLD_YLEVEL;
		// assume rect is in chunk for now
		rect.coords().stream().filter(coord -> chunkRect.contains(coord))
			.forEach(vec ->
				IntStream.range(1, room.getLocalHeight()).forEach(yOff ->
						world.setBlockState(new BlockPos(vec.X, baseY+yOff, vec.Y), state, 2))
				);
	}

}
