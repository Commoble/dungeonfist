package com.github.commoble.dungeonfist.world.dungature;

import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import com.github.commoble.dungeonfist.util.Rect;
import com.github.commoble.dungeonfist.util.Room;
import com.github.commoble.dungeonfist.util.TransformedCoord;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class RisingBasicColumnDungature extends Dungature
{
	private int size;
	private BlockState state;
	
	public RisingBasicColumnDungature(DungatureContext context, int minSize, int maxSize, Supplier<BlockState> stateGetter)
	{
		this.size = context.rand.nextInt(maxSize - minSize + 1) + minSize;
		this.state = stateGetter.get();
	}

	@Override
	public void place(Rect rect, Rect chunkRect, Room room, IWorld world, Random rand)
	{
		this.transformSquareIntoChunk(1, chunkRect).forEach(pair -> this.placeColumn(pair, room, world, rand));
	}
	
	public void placeColumn(TransformedCoord pair, Room room, IWorld world, Random rand)
	{
		BlockState rotatedState = this.transform.applyToBlockState(this.state);
		IntStream.range(0, this.size).forEach(i -> world.setBlockState(new BlockPos(pair.finalVec.X, room.WORLD_YLEVEL + i + 1, pair.finalVec.Y), rotatedState, 2));
	}
}
