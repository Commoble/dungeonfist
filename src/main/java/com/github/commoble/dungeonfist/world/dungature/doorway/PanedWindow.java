package com.github.commoble.dungeonfist.world.dungature.doorway;

import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import com.github.commoble.dungeonfist.util.Rect;
import com.github.commoble.dungeonfist.util.Room;
import com.github.commoble.dungeonfist.util.Vec2i;
import com.github.commoble.dungeonfist.world.dungature.Dungature;
import com.github.commoble.dungeonfist.world.dungature.DungatureContext;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PaneBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class PanedWindow extends Dungature
{
	private int positionHasher; // some random positive integer
	private PaneBlock block;
	private int size;
	private int baseY;

	public PanedWindow(DungatureContext context, Supplier<PaneBlock> blockGetter)
	{
		this.block = blockGetter.get();
		this.positionHasher = Math.abs(context.rand.nextInt());

		int minSize = 1;
		int maxSize = context.room.getLocalHeight();
		this.size = context.rand.nextInt(maxSize - minSize) + minSize;

		int minYStart = context.room.WORLD_YLEVEL + 1; // minimum 1 above floor
		int maxYStart = context.room.WORLD_YLEVEL + context.room.getLocalHeight() - this.size; // size=1, max = 5 above
																								// floor; size=5, max =
																								// 1 above floor
		// since max and min can be the same, add 1 to the nextInt arg since the arg
		// must be strictly > 0
		this.baseY = context.rand.nextInt(maxYStart - minYStart + 1) + minYStart;
	}

	@Override
	public void place(Rect rect, Rect chunkRect, Room room, IWorld world, Random rand)
	{
		BlockState air = Blocks.AIR.getDefaultState();
		BlockState rotatedPane;
		
		int paneSizeX, paneSizeZ, paneStartX, paneStartZ;
		int paneOffset;
		// define a Rect within which to generate the window pane
		if (this.transform.rotation % 2 == 0)// hole is along north-south axis, window pane is along west-east axis
		{
			paneOffset = (this.positionHasher % rect.SIZE.Y);
			
			paneStartX = rect.START.X;
			paneSizeX = rect.SIZE.X;
			
			paneStartZ = rect.START.Y + paneOffset;
			paneSizeZ = 1;
			rotatedPane = this.block.getDefaultState().with(PaneBlock.EAST, true).with(PaneBlock.WEST, true);
		}
		else	// window hole is along west-east axis, pane is along north-south axis
		{
			paneOffset = (this.positionHasher % rect.SIZE.X);
			
			paneStartX = rect.START.X + paneOffset;
			paneSizeX = 1;
			
			paneStartZ = rect.START.Y;
			paneSizeZ = rect.SIZE.Y;
			rotatedPane = this.block.getDefaultState().with(PaneBlock.NORTH, true).with(PaneBlock.SOUTH, true);
		}
		// if door is on interior and corridor is south or east, use minX,minY of rect	// rotation = 0 or 3
		// if door is on exterior and corridor is west or north, use minX,minY of rect
		// otherwise use maxX, maxY of rect
		boolean isWestOrNorth = this.transform.rotation == 1 || this.transform.rotation == 2;
		
		Rect paneRect = new Rect(new Vec2i(paneStartX, paneStartZ), new Vec2i(paneSizeX, paneSizeZ));


		rect.coords().stream().filter(vec -> chunkRect.contains(vec)).forEach(vec -> {
			BlockState whichState = paneRect.contains(vec) ? rotatedPane : air;
				IntStream.range(0, this.size)
				.forEach(yOff -> world.setBlockState(new BlockPos(vec.X, this.baseY + yOff, vec.Y), whichState, 2));
		});
}

}
