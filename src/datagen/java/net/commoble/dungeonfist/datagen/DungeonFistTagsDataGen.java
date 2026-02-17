package net.commoble.dungeonfist.datagen;

import net.commoble.dungeonfist.DungeonFist;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public final class DungeonFistTagsDataGen
{
	private DungeonFistTagsDataGen() {}
	
	static void gatherData(GatherDataEvent event)
	{
		TagProvider<Block> blocks = TagProvider.create(event, Registries.BLOCK);
		
		blocks.tag(BlockTags.MINEABLE_WITH_PICKAXE)
			.addAll(DungeonFist.PIPE_BLOCKS.values().stream().map(holder -> holder.unwrapKey().get()))
			.add(DungeonFist.ALERT_RUNE.unwrapKey().get())
			.add(DungeonFist.SUMMON_RUNE.unwrapKey().get())
			.add(DungeonFist.TELEPORT_RUNE.unwrapKey().get());
		blocks.tag(BlockTags.NEEDS_DIAMOND_TOOL)
			.add(DungeonFist.ALERT_RUNE.unwrapKey().get())
			.add(DungeonFist.SUMMON_RUNE.unwrapKey().get())
			.add(DungeonFist.TELEPORT_RUNE.unwrapKey().get());
			
		blocks.tag(BlockTags.STONE_PRESSURE_PLATES)
			.addAll(DungeonFist.PRESSURE_PLATE_BLOCKS.values().stream().map(holder -> holder.unwrapKey().get()));
		
		event.addProvider(blocks);
	}
}
