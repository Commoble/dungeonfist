package com.github.commoble.dungeonfist.data.generated;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.github.commoble.dungeonfist.DungeonFist;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootParameterSet;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraft.world.storage.loot.ValidationResults;

public class DungeonLootTableProvider extends LootTableProvider
{
	// if you're familiar with the top-level property "type" in loot tables
	// e.g. "type": "minecraft:chest" or "type": "minecraft:generic"
	// this determines what sorts of parameters are available to the loot table for conditions, etc
	// and when we generate loot tables, we define them with their parameter sets in this list here
	// technically, loot table builders have a method to set it in the builder
	// but this seems to be ALWAYS overridden but what we define here
	private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> loot_tables =
			ImmutableList.of(Pair.of(DungeonChestLootTables::new, LootParameterSets.CHEST));
	
	public DungeonLootTableProvider(DataGenerator dataGenerator)
	{
		super(dataGenerator);
	}

	@Override
	protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables()
	{
		return this.loot_tables;
	}

	@Override
	public String getName()
	{
		// TODO Auto-generated method stub
		return DungeonFist.MODID + ":loot_tables";
	}

	@Override
	protected void validate(Map<ResourceLocation, LootTable> map, ValidationResults validationresults)
	{
		map.forEach((resourceLocation, lootTable) -> {
			LootTableManager.func_215302_a(validationresults, resourceLocation, lootTable, map::get);
		});
	}

}
