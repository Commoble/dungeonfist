package com.github.commoble.dungeonfist.world.dungature;

import com.github.commoble.dungeonfist.data.loot.DungeonLootTables;
import com.github.commoble.dungeonfist.util.BlockCollections;
import com.github.commoble.dungeonfist.util.RandomHelper;

import net.minecraft.block.Blocks;

public class StandardDungatures
{
	public static final DungatureTable table = new DungatureTable(16);
	static
	{
		table.add(WeightingFunctions.constantWeight(200), EmptyDungature.SUPPLIER);
		table.add(WeightingFunctions.linearScaling(10), context -> new ColumnDungature());
		//table.add(WeightingFunctions.constantWeight(1), context -> new ChestDungature(LootTables.CHESTS_SIMPLE_DUNGEON).withRandomTransform(1, context));
		table.add(WeightingFunctions.constantWeight(1), context -> new ChestDungature(DungeonLootTables.RANDOM_LOOT_BY_DEPTH).withRandomTransform(1, context));
		table.add(WeightingFunctions.withMinSize(10, 2), context -> new TransformTestDungature().withRandomTransform(2, context));
		table.add(WeightingFunctions.withMinSize(10, 2), context -> new TorchColumnDungature().withRandomTransform(2, context));
		table.add(WeightingFunctions.withMinSize(1, 3), context -> new ChandelierDungature(() ->
			RandomHelper.getRandomThingFrom(context.rand, BlockCollections.WOOD_FENCES))
			.withRandomTranslation(3, context));
		table.add(WeightingFunctions.constantWeight(5), context -> new HangingBasicColumnDungature(context, 1, 2, Blocks.IRON_BARS::getDefaultState).withRandomTranslation(1, context));
		table.add(WeightingFunctions.constantWeight(5), context -> new HangingBasicColumnDungature(context, 1, 2, () -> RandomHelper.getRandomThingFrom(context.rand, BlockCollections.WOOD_FENCES).getDefaultState()).withRandomTranslation(1, context));
	}
}
