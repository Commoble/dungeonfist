package com.github.commoble.dungeonfist.world.dungature.doorway;

import com.github.commoble.dungeonfist.world.dungature.DungatureTable;
import com.github.commoble.dungeonfist.world.dungature.EmptyDungature;
import com.github.commoble.dungeonfist.world.dungature.WeightingFunctions;

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
		table.add(WeightingFunctions.linearScaling(50), context -> new TinyEmptyCorridor(context));
		table.add(WeightingFunctions.linearScaling(10), context -> new DoorwayDungature(context));
	}
}
