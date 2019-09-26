package com.github.commoble.dungeonfist.world.dungature;

import net.minecraft.world.storage.loot.LootTables;

public class StandardDungatures
{
	public static final DungatureTable table = new DungatureTable();
	static
	{
		table.add(WeightingFunctions.constantWeight(200), EmptyDungature.SUPPLIER);
		table.add(WeightingFunctions.linearScaling(10), (rect, room, rand) -> new ColumnDungature());
		table.add(WeightingFunctions.constantWeight(1), (rect, room, rand) -> new ChestDungature(LootTables.CHESTS_SIMPLE_DUNGEON).withRandomTransform(1, rect, room, rand));
		table.add(WeightingFunctions.withMinSize(10, 2), (rect, room, rand) -> new TransformTestDungature().withRandomTransform(2, rect, room, rand));
		table.add(WeightingFunctions.withMinSize(10, 2), (rect, room, rand) -> new TorchColumnDungature().withRandomTransform(2, rect, room, rand));
	}
}
