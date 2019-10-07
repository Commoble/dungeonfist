package com.github.commoble.dungeonfist.world.dungature.doorway;

import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import com.github.commoble.dungeonfist.util.Rect;
import com.github.commoble.dungeonfist.util.Room;
import com.github.commoble.dungeonfist.world.dungature.Dungature;
import com.github.commoble.dungeonfist.world.dungature.DungatureContext;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class FilledWindow extends Dungature
{
	private BlockState state;
	private int size;
	private int baseY;

	public FilledWindow(DungatureContext context, Supplier<BlockState> stateGetter)
	{
		this.state = stateGetter.get();
		
		int minSize = 1;
		int maxSize = context.room.HEIGHT_SIZE;
		this.size = context.rand.nextInt(maxSize - minSize) + minSize;
		
		int minYStart = context.room.WORLD_FLOOR_YLEVEL + 1;	// minimum 1 above floor
		int maxYStart = context.room.WORLD_FLOOR_YLEVEL + context.room.HEIGHT_SIZE - this.size; // size=1, max = 5 above floor; size=5, max = 1 above floor
		// since max and min can be the same, add 1 to the nextInt arg since the arg must be strictly > 0
		this.baseY = context.rand.nextInt(maxYStart - minYStart + 1) + minYStart;
	}

	@Override
	public void place(Rect rect, Rect chunkRect, Room room, IWorld world, Random rand)
	{

		rect.intersection(chunkRect).coords().stream().forEach(vec -> IntStream.range(0, this.size)
				.forEach(yOff -> world.setBlockState(new BlockPos(vec.X, this.baseY + yOff, vec.Y), this.state, 2)));
	}

}
