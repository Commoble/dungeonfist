package com.github.commoble.dungeonfist.world.feature;

import java.util.Random;

import com.github.commoble.dungeonfist.util.Rect;
import com.github.commoble.dungeonfist.util.Room;
import com.github.commoble.dungeonfist.util.TransformHelper;
import com.github.commoble.dungeonfist.util.Vec2i;
import com.github.commoble.dungeonfist.world.dungature.Dungature;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StairsBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class TransformTestDungature extends Dungature
{
	@Override
	public void place(Rect rect, Rect chunkRect, Room room, IWorld world, Random rand)
	{
		
		Rect.SQUARE_RECTS[2].coords().stream().forEach(originCoord -> {
			Vec2i finalCoord = TransformHelper.withStandardTransforms(originCoord, 2, this.transform);
			if (chunkRect.contains(finalCoord))
			{
				BlockPos pos = new BlockPos(finalCoord.X, room.WORLD_YLEVEL+1, finalCoord.Y);
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
				state = state.rotate(Rotation.values()[this.transform.rotation]);
				state = state.mirror(this.transform.xAxisMirror());
				state = state.mirror(this.transform.zAxisMirror());
				world.setBlockState(pos, state, 2);
			}
		});
	}

}
