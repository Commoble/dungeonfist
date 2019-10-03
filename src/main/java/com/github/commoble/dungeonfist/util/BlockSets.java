package com.github.commoble.dungeonfist.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.PaneBlock;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;

// it would be a good idea not to refer to this class before blocks exist
public class BlockSets
{
	public static List<DoorBlock> DOORS = filterRegistryBySubclass(ForgeRegistries.BLOCKS, DoorBlock.class, block->true);
	
	public static List<Block> GLASS_CUBES = ForgeRegistries.BLOCKS.getValues().stream()
			.filter(Tags.Blocks.GLASS::contains)
			.collect(Collectors.toCollection(ArrayList::new));
	
	public static List<PaneBlock> GLASS_PANES = filterRegistryBySubclass(ForgeRegistries.BLOCKS, PaneBlock.class, Tags.Blocks.GLASS_PANES::contains);
	
	public static <T extends ForgeRegistryEntry<T>, Q extends T> ArrayList<Q> filterRegistryBySubclass(IForgeRegistry<T> registry, Class<Q> clazz, Predicate<T> filter)
	{
		return registry.getValues().stream()
			.filter(filter)
			.filter(clazz::isInstance)
			.map(clazz::cast)
			.collect(Collectors.toCollection(ArrayList::new));
	}
}
