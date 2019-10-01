package com.github.commoble.dungeonfist.world.dungature.doorway;

import java.util.Random;
import java.util.stream.IntStream;

import com.github.commoble.dungeonfist.util.Rect;
import com.github.commoble.dungeonfist.util.Room;
import com.github.commoble.dungeonfist.util.Vec2i;
import com.github.commoble.dungeonfist.world.dungature.Dungature;
import com.github.commoble.dungeonfist.world.dungature.DungatureContext;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class TinyEmptyCorridor extends Dungature
{
	private int positionHasher; // some positive integer

	public TinyEmptyCorridor(DungatureContext context)
	{
		this.positionHasher = Math.abs(context.rand.nextInt());
	}

	@Override
	public void place(Rect rect, Rect chunkRect, Room room, IWorld world, Random rand)
	{
		int minX, sizeX, minY, sizeY;
		int corridorOffset;
		if (this.transform.rotation % 2 == 0)// doorway is along west-east axis
		{
			corridorOffset = (this.positionHasher % rect.SIZE.Y);
			
			minX = rect.START.X;
			sizeX = rect.SIZE.X;
			
			minY = rect.START.X + corridorOffset;
			sizeY = 1;
		}
		else	// doorway corridor is along north-south axis
		{
			corridorOffset = (this.positionHasher % rect.SIZE.X);
			
			minX = rect.START.X + corridorOffset;
			sizeX = 1;
			
			minY = rect.START.Y;
			sizeY = rect.SIZE.Y;
		}
		
		Rect doorCorridor = new Rect(new Vec2i(minX, minY), new Vec2i(sizeX, sizeY));
		int baseY = room.WORLD_YLEVEL;
		BlockState air = Blocks.AIR.getDefaultState();
		rect.coords().stream().filter(coord -> chunkRect.contains(coord) && doorCorridor.contains(coord))
				.forEach(coord -> IntStream.range(1, 3)
						.forEach(yOff -> world.setBlockState(new BlockPos(coord.X, baseY + yOff, coord.Y), air, 2)));
	}

}
