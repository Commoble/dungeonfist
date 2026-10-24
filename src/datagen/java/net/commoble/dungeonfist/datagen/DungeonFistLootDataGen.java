package net.commoble.dungeonfist.datagen;

import net.commoble.dungeonfist.DungeonFist;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class DungeonFistLootDataGen
{
	static void gatherData(BootstrapContext<LootTable> context)
	{
		DungeonFist.PIPE_BLOCKS.forEach((originalKey, blockHolder) -> {
			addSimpleBlockItem(blockHolder, context);
		});
		
		DungeonFist.PRESSURE_PLATE_BLOCKS.forEach((originalKey, blockHolder) -> {
			addSimpleBlockItem(blockHolder, context);
		});
		
		addSimpleBlockItem(DungeonFist.ALERT_RUNE, context);
		addSimpleBlockItem(DungeonFist.CHARGED_TNT, context);
		addSimpleBlockItem(DungeonFist.SUMMON_RUNE, context);
		addSimpleBlockItem(DungeonFist.TELEPORT_RUNE, context);
		
		DungeonFistArtifactDataGen.gatherData(context);
	}
	
	private static void addSimpleBlockItem(Holder<Block> blockHolder, BootstrapContext<LootTable> context)
	{
		Identifier lootTableId = blockHolder.unwrapKey().get().identifier().withPrefix("blocks/");
		LootTable lootTable = LootTable.lootTable()
			.setParamSet(LootContextParamSets.BLOCK)
			.setRandomSequence(lootTableId)
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(blockHolder.value())))
			.build();
		context.register(ResourceKey.create(Registries.LOOT_TABLE, lootTableId), lootTable);
	}
}
