package com.github.commoble.dungeonfist.world.dungature.doorway;

import com.github.commoble.dungeonfist.util.BlockCollections;
import com.github.commoble.dungeonfist.world.dungature.DungatureTable;
import com.github.commoble.dungeonfist.world.dungature.EmptyDungature;
import com.github.commoble.dungeonfist.world.dungature.WeightingFunctions;

import net.minecraft.block.Blocks;
import net.minecraft.block.PaneBlock;

public class StandardDoorways
{
	public static DungatureTable table = new DungatureTable(4);
	
	static
	{	// it's okay to set random transforms here
		// keep in mind that the Room will be setting the rotation after calling the context -> dungature functions given here
		// the rotation that the Room adds will represent which side of the room the wall segment that the doorway
		// will be generated in exists on
		// rotation of 0,1,2,3 = east, south, west, north
		table.add(WeightingFunctions.constantWeight(1000), EmptyDungature.SUPPLIER);
		table.add(WeightingFunctions.constantWeight(50), context -> new TinyEmptyCorridor(context));
		table.add(WeightingFunctions.constantWeight(10), context -> new DoorwayDungature(context));
		table.add(WeightingFunctions.linearScaling(30), context -> new FilledCorridor(context, Blocks.AIR::getDefaultState));
		table.add(WeightingFunctions.linearScaling(1), context -> new FilledWindow(context, Blocks.GLASS::getDefaultState));
		table.add(WeightingFunctions.linearScaling(1), context -> new FilledWindow(context, () -> BlockCollections.GLASS_CUBES.get(context.rand.nextInt(BlockCollections.GLASS_CUBES.size())).getDefaultState()));
		table.add(WeightingFunctions.linearScaling(10), context -> new PanedWindow(context, () -> (PaneBlock)Blocks.GLASS_PANE));
		table.add(WeightingFunctions.linearScaling(60), context -> new PanedWindow(context, () -> (PaneBlock)Blocks.IRON_BARS));
		table.add(WeightingFunctions.linearScaling(10), context -> new PanedWindow(context, () -> BlockCollections.GLASS_PANES.get(context.rand.nextInt(BlockCollections.GLASS_PANES.size()))));
	}
}
