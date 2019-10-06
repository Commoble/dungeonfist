package com.github.commoble.dungeonfist.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallBlock;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;

public class DungeonMaterials
{

	public static <T extends ForgeRegistryEntry<T>, Q extends T> ArrayList<Q> filterRegistryBySubclass(
			IForgeRegistry<T> registry, Class<Q> clazz, Predicate<T> filter)
	{
		return registry.getValues().stream().filter(filter).filter(clazz::isInstance).map(clazz::cast)
				.collect(Collectors.toCollection(ArrayList::new));
	}

	public static final List<Block> dungeonMaterials = new ArrayList<>();
	public static final Map<Block, StairsBlock> stairMap = new HashMap<>();
	public static final Map<Block, Block> mossyMap = new HashMap<>();
	public static final Map<Block, Block> damagedMap = new HashMap<>();
	public static final Map<Block, Block> plateMap = new HashMap<>();
	public static final Map<Block, WallBlock> wallMap = new HashMap<>();
	public static final Map<Block, SlabBlock> slabMap = new HashMap<>();

}
