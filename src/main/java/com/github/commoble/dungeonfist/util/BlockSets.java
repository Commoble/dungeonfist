package com.github.commoble.dungeonfist.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.block.DoorBlock;
import net.minecraftforge.registries.ForgeRegistries;

// it would be a good idea not to refer to this class before blocks exist
public class BlockSets
{
	public static List<DoorBlock> DOORS = ForgeRegistries.BLOCKS.getValues().stream()
			.filter(DoorBlock.class::isInstance)
			.map(DoorBlock.class::cast)
			.collect(Collectors.toCollection(ArrayList::new));
}
