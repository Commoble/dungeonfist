package com.github.commoble.dungeonfist.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.material.Material;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

// it would be a good idea not to refer to this class before blocks exist
public class BlockCollections
{
	private static final Tag<Block> glassTag = BlockTags.getCollection().get(new ResourceLocation("forge", "glass"));
	private static final Tag<Block> glassPaneTag = BlockTags.getCollection().get(new ResourceLocation("forge", "glass_panes"));
	
	public static final List<DoorBlock> DOORS = DungeonMaterials.filterRegistryBySubclass(ForgeRegistries.BLOCKS, DoorBlock.class,
			block -> true);

	public static final List<Block> GLASS_CUBES = ForgeRegistries.BLOCKS.getValues().stream()
			.filter(glassTag::contains).collect(Collectors.toCollection(ArrayList::new));

	public static final List<PaneBlock> GLASS_PANES = DungeonMaterials.filterRegistryBySubclass(ForgeRegistries.BLOCKS, PaneBlock.class,
			glassPaneTag::contains);

	public static final List<FenceBlock> WOOD_FENCES = DungeonMaterials.filterRegistryBySubclass(ForgeRegistries.BLOCKS,
			FenceBlock.class, block -> block.getDefaultState().getMaterial() == Material.WOOD);

}
