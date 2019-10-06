package com.github.commoble.dungeonfist.world.dungature;

import java.util.Random;
import java.util.stream.IntStream;

import com.github.commoble.dungeonfist.util.Rect;
import com.github.commoble.dungeonfist.util.Room;
import com.github.commoble.dungeonfist.util.TransformedCoord;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class TorchColumnDungature extends Dungature
{
	@Override
	public void place(Rect rect, Rect chunkRect, Room room, IWorld world, Random rand)
	{
		this.transformSquareIntoChunk(2, chunkRect).forEach(pair -> this.placeColumn(pair, room, world, rand));
	}

	private void placeColumn(TransformedCoord pair, Room room, IWorld world, Random rand)
	{
		if (pair.originalVec.Y == 0)
		{
			if (pair.originalVec.X == 0)
			{
				int height = room.getLocalHeight();
				BlockState state = room.STANDARD_BLOCK.getDefaultState();
				IntStream.range(0, height).forEach(yOff -> world.setBlockState(new BlockPos(pair.finalVec.X, yOff + room.WORLD_YLEVEL, pair.finalVec.Y), state, 2));
			}
			else
			{
				BlockState state = Blocks.REDSTONE_WALL_TORCH.getDefaultState().with(WallTorchBlock.HORIZONTAL_FACING, Direction.EAST);
				state = this.transform.applyToBlockState(state);
				world.setBlockState(new BlockPos(pair.finalVec.X, room.WORLD_YLEVEL + room.getLocalHeight()-1 - rand.nextInt(3), pair.finalVec.Y), state, 2);
			}
		}
	}
}
