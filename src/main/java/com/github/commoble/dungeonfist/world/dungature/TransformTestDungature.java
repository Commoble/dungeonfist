package com.github.commoble.dungeonfist.world.dungature;

import java.util.Random;

import com.github.commoble.dungeonfist.util.Rect;
import com.github.commoble.dungeonfist.util.Room;
import com.github.commoble.dungeonfist.util.TransformedCoord;
import com.github.commoble.dungeonfist.util.Vec2i;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StairsBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class TransformTestDungature extends Dungature
{
	@Override
	public void place(Rect rect, Rect chunkRect, Room room, IWorld world, Random rand)
	{
		this.transformSquareIntoChunk(2, chunkRect).forEach(pair -> this.setColumn(room, world, pair));
	}
	
	private void setColumn(Room room, IWorld world, TransformedCoord pair)
	{
		Vec2i originCoord = pair.originalVec;
		Vec2i finalCoord = pair.finalVec;
		BlockPos pos = new BlockPos(finalCoord.X, room.WORLD_FLOOR_YLEVEL+1, finalCoord.Y);
		BlockState state;
		
		if (originCoord.Y == 0)
		{
			if (originCoord.X == 0)
			{
				state = Blocks.OAK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH);
			}
			else
			{
				state = Blocks.ACACIA_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH);
			}
		}
		else
		{
			if (originCoord.X == 0)
			{
				state = Blocks.DARK_OAK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH);
			}
			else
			{
				state = Blocks.BIRCH_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH);
			}
		}
		state = this.transform.applyToBlockState(state);
		world.setBlockState(pos, state, 2);
	}

}
