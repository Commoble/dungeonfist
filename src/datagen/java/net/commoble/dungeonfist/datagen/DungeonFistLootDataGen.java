package net.commoble.dungeonfist.datagen;

import java.util.HashMap;
import java.util.Map;

import net.commoble.dungeonfist.DungeonFist;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class DungeonFistLootDataGen
{
	static void gatherData(GatherDataEvent event)
	{
		Map<Identifier, LootTable> lootTables = new HashMap<>();
		
		DungeonFist.PIPE_BLOCKS.forEach((originalKey, blockHolder) -> {
			addSimpleBlockItem(blockHolder, lootTables);
		});
		
		DungeonFist.PRESSURE_PLATE_BLOCKS.forEach((originalKey, blockHolder) -> {
			addSimpleBlockItem(blockHolder, lootTables);
		});
		
		addSimpleBlockItem(DungeonFist.ALERT_RUNE, lootTables);
		addSimpleBlockItem(DungeonFist.CHARGED_TNT, lootTables);
		addSimpleBlockItem(DungeonFist.SUMMON_RUNE, lootTables);
		addSimpleBlockItem(DungeonFist.TELEPORT_RUNE, lootTables);
		
		JsonDataProvider.addProvider(event, Target.DATA_PACK, "loot_table", LootTable.DIRECT_CODEC, lootTables);
	}
	
	private static void addSimpleBlockItem(Holder<Block> blockHolder, Map<Identifier, LootTable> lootTables)
	{
		Identifier lootTableId = blockHolder.unwrapKey().get().identifier().withPrefix("blocks/");
		LootTable lootTable = LootTable.lootTable()
			.setParamSet(LootContextParamSets.BLOCK)
			.setRandomSequence(lootTableId)
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(blockHolder.value())))
			.build();
		lootTables.put(lootTableId, lootTable);
	}
}
