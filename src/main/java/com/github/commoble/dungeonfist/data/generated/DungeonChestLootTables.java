package com.github.commoble.dungeonfist.data.generated;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.github.commoble.dungeonfist.DungeonFist;
import com.github.commoble.dungeonfist.data.loot.ApplyFunctionsIfItemHasTag;

import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.storage.loot.ConstantRange;
import net.minecraft.world.storage.loot.ItemLootEntry;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTable.Builder;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.TableLootEntry;
import net.minecraft.world.storage.loot.functions.EnchantWithLevels;

public class DungeonChestLootTables implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>
{
	public ResourceLocation TEST_CHEST = new ResourceLocation(DungeonFist.MODID, "chests/test");
	
	// subtable locations
	public ResourceLocation ENCHANTED_WEAPONS = new ResourceLocation(DungeonFist.MODID, "subtables/enchanted_weapons");
	public ResourceLocation WEAPONS = new ResourceLocation(DungeonFist.MODID, "subtables/weapons");
	
	public ResourceLocation WOODEN_TOOLS_TAG = new ResourceLocation(DungeonFist.MODID, "wooden_tools");
	
	@Override
	public void accept(BiConsumer<ResourceLocation, Builder> builderConsumer)
	{
		builderConsumer.accept(
			this.WEAPONS,
			LootTable.builder().addLootPool(
				LootPool.builder().rolls(ConstantRange.of(1))
				.addEntry(ItemLootEntry.builder(Items.IRON_SWORD).weight(40))
				.addEntry(ItemLootEntry.builder(Items.IRON_AXE).weight(40))
				.addEntry(ItemLootEntry.builder(Items.BOW).weight(11))
				.addEntry(ItemLootEntry.builder(Items.GOLDEN_SWORD).weight(1))
				.addEntry(ItemLootEntry.builder(Items.GOLDEN_AXE).weight(1))
				.addEntry(ItemLootEntry.builder(Items.WOODEN_SWORD).weight(40))
				.addEntry(ItemLootEntry.builder(Items.WOODEN_AXE).weight(1))
				.addEntry(ItemLootEntry.builder(Items.DIAMOND_SWORD).weight(1))
				.addEntry(ItemLootEntry.builder(Items.DIAMOND_AXE).weight(1))
				.addEntry(ItemLootEntry.builder(Items.TRIDENT).weight(1))
			)
		);
		
		builderConsumer.accept(
			this.ENCHANTED_WEAPONS,
			LootTable.builder().addLootPool(
				LootPool.builder().rolls(ConstantRange.of(1)).addEntry(
					TableLootEntry.builder(this.WEAPONS).weight(1).acceptFunction(
						// unmapped function names: This gets the builder for EnchantWithLevels,
						// sets the random level range to between 1 and 5, and enables treasure enchantments
						EnchantWithLevels.func_215895_a(RandomValueRange.func_215837_a(1F, 5F)).func_216059_e()
					)
				).addEntry(
					TableLootEntry.builder(this.WEAPONS).weight(1).acceptFunction(
						EnchantWithLevels.func_215895_a(RandomValueRange.func_215837_a(10F, 20F)).func_216059_e()
					)
				)
			)
		);
		
		builderConsumer.accept(
			this.TEST_CHEST,
			LootTable.builder().setParameterSet(LootParameterSets.CHEST).addLootPool(
				LootPool.builder().rolls(RandomValueRange.func_215837_a(3F, 10F)).addEntry(
					TableLootEntry.builder(this.ENCHANTED_WEAPONS).acceptFunction(
						ApplyFunctionsIfItemHasTag.getBuilder(
							this.WOODEN_TOOLS_TAG, 
							ApplyFunctionsIfItemHasTag.getSetNameBuilder(new StringTextComponent("Yep, it's wood"), null).build()
						)
					)
				)
			)
		);
	}
}
