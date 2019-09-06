package com.github.commoble.dungeonfist.registry;

import com.github.commoble.dungeonfist.DungeonFist;
import com.github.commoble.dungeonfist.Registrator;
import com.github.commoble.dungeonfist.dimension.DungeonModDimension;

import net.minecraftforge.common.ModDimension;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(DungeonFist.MODID)
public class ModDimensionRegistrar
{
	@ObjectHolder(DungeonFist.DUNGEON)
	public static final ModDimension DUNGEON = null;
	
	public static void registerModDimensions(Registrator<ModDimension> reg)
	{
		reg.register(DungeonFist.DUNGEON, new DungeonModDimension());
	}
}
