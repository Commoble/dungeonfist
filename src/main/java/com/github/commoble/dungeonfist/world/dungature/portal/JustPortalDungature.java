package com.github.commoble.dungeonfist.world.dungature.portal;

import java.util.Random;

import com.github.commoble.dungeonfist.registry.BlockRegistrar;
import com.github.commoble.dungeonfist.util.Rect;
import com.github.commoble.dungeonfist.util.Room;
import com.github.commoble.dungeonfist.world.dungature.Dungature;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class JustPortalDungature extends Dungature
{

	@Override
	public void place(Rect rect, Rect chunkRect, Room room, IWorld world, Random rand)
	{		
		//Vec2i coord = this.transform.translation;
		room.PORTAL_COORD.ifPresent(coord ->
		//if (chunkRect.contains(coord))
		{
			BlockState core = BlockRegistrar.DUNGEON_PORTAL.getDefaultState();
			BlockState tip = BlockRegistrar.DUNGEON_PORTAL_GLOWY_AIR.getDefaultState();
			
			int minY = room.WORLD_FLOOR_YLEVEL + 2;
			int maxY = room.getCeilingLevel() - 2;
			int heightWidth = maxY - minY;
			if (heightWidth < 1)
				heightWidth = 1;
			
			int coreY = rand.nextInt(heightWidth) + minY;
			BlockPos corePos = coord.toBlockPos(coreY);
			world.setBlockState(corePos, core, 2);
			world.setBlockState(corePos.down(), tip, 2);
			world.setBlockState(corePos.up(), tip, 2);
		});
	}

}
