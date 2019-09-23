package com.github.commoble.dungeonfist.world.dungature;

import net.minecraft.world.storage.loot.LootTables;

public class StandardDungatures
{
	public static DungatureTable table = new DungatureTable();
	static
	{
		table.add(10, 1, new ColumnDungature());
		table.add(1, 1, new ChestDungature(LootTables.CHESTS_SIMPLE_DUNGEON));
	}
}
