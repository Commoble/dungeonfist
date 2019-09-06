package com.github.commoble.dungeonfist.registry;

import com.github.commoble.dungeonfist.DungeonFist;
import com.github.commoble.dungeonfist.Registrator;
import com.github.commoble.dungeonfist.world.DungeonBiome;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(DungeonFist.MODID)
public class BiomeRegistrar
{
	@ObjectHolder(DungeonFist.DUNGEON)
	public static final Biome DUNGEON = null;
	
	public static void registerBiomes(Registrator<Biome> reg)
	{
		reg.register(DungeonFist.DUNGEON, new DungeonBiome());
	}
}
