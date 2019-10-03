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

public class FilledCorridor extends Dungature
{
	private BlockState state;
	private int size;
	
	public FilledCorridor(DungatureContext context, Supplier<BlockState> stateGetter)
	{
		this.state = stateGetter.get();
		
		int minSize = 2;
		int maxSize = context.room.getLocalHeight();
		this.size = context.rand.nextInt(maxSize - minSize) + minSize;
	}

	@Override
	public void place(Rect rect, Rect chunkRect, Room room, IWorld world, Random rand)
	{
		int baseY = room.WORLD_YLEVEL+1;
		
		rect.intersection(chunkRect).coords().stream()
			.forEach(vec -> IntStream.range(0, this.size).forEach(yOff -> world.setBlockState(new BlockPos(vec.X, baseY + yOff, vec.Y), this.state, 2)));
	}

}
