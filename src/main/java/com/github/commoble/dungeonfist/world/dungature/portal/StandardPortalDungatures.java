package com.github.commoble.dungeonfist.world.dungature.portal;

import com.github.commoble.dungeonfist.world.dungature.DungatureTable;
import com.github.commoble.dungeonfist.world.dungature.WeightingFunctions;

public class StandardPortalDungatures
{
	public static final DungatureTable table = new DungatureTable(16);
	
	static
	{
		table.add(WeightingFunctions.constantWeight(10), context -> new JustPortalDungature());
	}
}
