package com.github.commoble.dungeonfist.world.dungature.interfloor;

import com.github.commoble.dungeonfist.world.dungature.DungatureTable;
import com.github.commoble.dungeonfist.world.dungature.WeightingFunctions;

public class StandardInterfloorDungatures
{
	public static DungatureTable table = new DungatureTable(16);
	
	static
	{
		table.add(WeightingFunctions.constantWeight(100), context -> new DeepwellDungature());
	}
}
