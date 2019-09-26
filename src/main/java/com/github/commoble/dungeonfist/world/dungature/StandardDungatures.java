package com.github.commoble.dungeonfist.world.dungature;

import com.github.commoble.dungeonfist.world.feature.TransformTestDungature;

public class StandardDungatures
{
	public static final DungatureTable table = new DungatureTable();
	static
	{
		//table.add(10, 1, (rect, room, rand) -> new ColumnDungature());
		//table.add(1, 1, (rect, room, rand) -> new ChestDungature(LootTables.CHESTS_SIMPLE_DUNGEON).withRandomTransform(1, rect, room, rand));
		table.add(100, 2, (rect, room, rand) -> new TransformTestDungature().withRandomTransform(2, rect, room, rand));
	}
}
