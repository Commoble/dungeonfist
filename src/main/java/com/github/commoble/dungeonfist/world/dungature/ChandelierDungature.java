package com.github.commoble.dungeonfist.world.dungature;

import java.util.Random;
import java.util.function.Supplier;

import com.github.commoble.dungeonfist.util.Rect;
import com.github.commoble.dungeonfist.util.Room;
import com.github.commoble.dungeonfist.util.TransformedCoord;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class ChandelierDungature extends Dungature
{
	private BlockState fenceBase;
	
	public ChandelierDungature(Supplier<FenceBlock> fenceBlockGetter)
	{
		this.fenceBase = fenceBlockGetter.get().getDefaultState();
	}
	
	@Override
	public void place(Rect rect, Rect chunkRect, Room room, IWorld world, Random rand)
	{
		this.transformSquareIntoChunk(3, chunkRect).forEach(pair -> this.placeColumn(pair, room, world));
	}
	
	public void placeColumn(TransformedCoord pair, Room room, IWorld world)
	{
		int x0 = pair.originalVec.X;
		int y0 = pair.originalVec.Y;
		int ceiling = room.getCeilingLevel();
		
		if (x0 == 1)	// middle column
		{
			if (y0 == 0) // north
			{
				world.setBlockState(new BlockPos(pair.finalVec.X, ceiling - 1, pair.finalVec.Y), Blocks.REDSTONE_TORCH.getDefaultState(), 2);
				world.setBlockState(new BlockPos(pair.finalVec.X, ceiling - 2, pair.finalVec.Y), this.fenceBase.with(FenceBlock.SOUTH, true), 2);
			}
			else if (y0 == 1) // center
			{
				world.setBlockState(new BlockPos(pair.finalVec.X, ceiling - 1, pair.finalVec.Y), this.fenceBase, 2);
				world.setBlockState(new BlockPos(pair.finalVec.X, ceiling - 2, pair.finalVec.Y), this.fenceBase
						.with(FenceBlock.SOUTH, true)
						.with(FenceBlock.WEST, true)
						.with(FenceBlock.EAST, true)
						.with(FenceBlock.NORTH, true), 2);
			}
			else // y = 2, south
			{
				world.setBlockState(new BlockPos(pair.finalVec.X, ceiling - 1, pair.finalVec.Y), Blocks.REDSTONE_TORCH.getDefaultState(), 2);
				world.setBlockState(new BlockPos(pair.finalVec.X, ceiling - 2, pair.finalVec.Y), this.fenceBase.with(FenceBlock.NORTH, true), 2);
			}
		}
		else if (y0 == 1) // middle row
		{
			if (x0 == 0) // west
			{
				world.setBlockState(new BlockPos(pair.finalVec.X, ceiling - 1, pair.finalVec.Y), Blocks.REDSTONE_TORCH.getDefaultState(), 2);
				world.setBlockState(new BlockPos(pair.finalVec.X, ceiling - 2, pair.finalVec.Y), this.fenceBase.with(FenceBlock.EAST, true), 2);
			}
			else // x0 == 2, east
			{
				world.setBlockState(new BlockPos(pair.finalVec.X, ceiling - 1, pair.finalVec.Y), Blocks.REDSTONE_TORCH.getDefaultState(), 2);
				world.setBlockState(new BlockPos(pair.finalVec.X, ceiling - 2, pair.finalVec.Y), this.fenceBase.with(FenceBlock.WEST, true), 2);
			}
		}
	}
}
