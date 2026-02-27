package net.commoble.dungeonfist.datagen;

import java.util.Comparator;

import net.commoble.dungeonfist.DungeonFist;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public final class DungeonFistTagsDataGen
{
	private DungeonFistTagsDataGen() {}
	
	static void gatherData(GatherDataEvent event)
	{
		TagProvider<Block> blocks = TagProvider.create(event, Registries.BLOCK);
		
		blocks.tag(BlockTags.MINEABLE_WITH_PICKAXE)
			.addAll(DungeonFist.PIPE_BLOCKS.values().stream().map(holder -> holder.unwrapKey().get())
				.toList())
			.add(DungeonFist.ALERT_RUNE.unwrapKey().get())
			.add(DungeonFist.SUMMON_RUNE.unwrapKey().get())
			.add(DungeonFist.TELEPORT_RUNE.unwrapKey().get());
		
		blocks.tag(BlockTags.NEEDS_DIAMOND_TOOL)
			.add(DungeonFist.ALERT_RUNE.unwrapKey().get())
			.add(DungeonFist.SUMMON_RUNE.unwrapKey().get())
			.add(DungeonFist.TELEPORT_RUNE.unwrapKey().get());
			
		blocks.tag(BlockTags.STONE_PRESSURE_PLATES)
			.addAll(DungeonFist.PRESSURE_PLATE_BLOCKS.values().stream().map(holder -> holder.unwrapKey().get())
				.toList());
		
		blocks.tag(BlockTags.DRAGON_IMMUNE)
			.add(DungeonFist.DUNGEON_PORTAL_BLOCK.unwrapKey().get())
			.add(DungeonFist.PORTAL_GENERATOR_BLOCK.unwrapKey().get());
		
		blocks.tag(BlockTags.WITHER_IMMUNE)
			.add(DungeonFist.DUNGEON_PORTAL_BLOCK.unwrapKey().get())
			.add(DungeonFist.PORTAL_GENERATOR_BLOCK.unwrapKey().get());
		
		blocks.tag(BlockTags.FEATURES_CANNOT_REPLACE)
			.add(DungeonFist.PORTAL_GENERATOR_BLOCK.unwrapKey().get());

		blocks.tag(Tags.Blocks.RELOCATION_NOT_SUPPORTED)
			.add(DungeonFist.DUNGEON_PORTAL_BLOCK.unwrapKey().get())
			.add(DungeonFist.PORTAL_GENERATOR_BLOCK.unwrapKey().get());
		
		event.addProvider(blocks);
	}
}
