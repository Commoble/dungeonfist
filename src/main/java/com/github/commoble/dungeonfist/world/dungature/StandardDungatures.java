package com.github.commoble.dungeonfist.world.dungature;

import com.github.commoble.dungeonfist.util.BlockCollections;
import com.github.commoble.dungeonfist.util.CollectionHelper;

import net.minecraft.block.Blocks;
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
		table.add(WeightingFunctions.withMinSize(1, 3), context -> new ChandelierDungature(() ->
			CollectionHelper.getRandomItem(BlockCollections.WOOD_FENCES, context.rand))
			.withRandomTranslation(3, context));
		table.add(WeightingFunctions.constantWeight(5), context -> new HangingBasicColumnDungature(context, 1, 2, Blocks.IRON_BARS::getDefaultState).withRandomTranslation(1, context));
		table.add(WeightingFunctions.constantWeight(5), context -> new HangingBasicColumnDungature(context, 1, 2, () -> CollectionHelper.getRandomItem(BlockCollections.WOOD_FENCES, context.rand).getDefaultState()).withRandomTranslation(1, context));
	}
}
