package com.github.commoble.dungeonfist.world.dungature;

import java.util.Random;

import com.github.commoble.dungeonfist.util.Rect;
import com.github.commoble.dungeonfist.util.Room;
import com.github.commoble.dungeonfist.util.Vec2i;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class ChestDungature extends Dungature
{
	private final ResourceLocation lootTable;
	
	public ChestDungature(ResourceLocation lootTable)
	{
		this.lootTable = lootTable;
	}

	@Override
	public void place(Rect rect, Room room, IWorld world, Random rand)
	{
		BlockState state = Blocks.CHEST.getDefaultState().with(HorizontalBlock.HORIZONTAL_FACING, Direction.byHorizontalIndex(rand.nextInt(4)));
		int baseY = room.WORLD_YLEVEL+1;
		// assume rect is in chunk for now
		Vec2i coord = rect.randomCoord(rand);
		BlockPos pos = new BlockPos(coord.X, baseY, coord.Y);
		world.setBlockState(pos, state, 2);
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof ChestTileEntity)
		{
			 ((ChestTileEntity)te).setLootTable(this.lootTable, rand.nextLong());
		}
	}

}
