package com.github.commoble.dungeonfist.world.dungature.interfloor;

import com.github.commoble.dungeonfist.world.dungature.DungatureTable;
import com.github.commoble.dungeonfist.world.dungature.WeightingFunctions;

public class StandardInterfloorDungatures
{
	public static DungatureTable table = new DungatureTable(16);
	
	static
	{
		table.add(WeightingFunctions.linearScaling(100), context -> new DeepwellDungature());
	}
}
