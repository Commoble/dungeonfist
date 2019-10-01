package com.github.commoble.dungeonfist.world.dungature;

import net.minecraft.world.storage.loot.LootTables;

public class StandardDungatures
{
	public static final DungatureTable table = new DungatureTable(16);
	static
	{
		table.add(WeightingFunctions.constantWeight(200), EmptyDungature.SUPPLIER);
		table.add(WeightingFunctions.linearScaling(10), context -> new ColumnDungature());
		table.add(WeightingFunctions.constantWeight(1), context -> new ChestDungature(LootTables.CHESTS_SIMPLE_DUNGEON).withRandomTransform(1, context));
		table.add(WeightingFunctions.withMinSize(10, 2), context -> new TransformTestDungature().withRandomTransform(2, context));
		table.add(WeightingFunctions.withMinSize(10, 2), context -> new TorchColumnDungature().withRandomTransform(2, context));
	}
}
