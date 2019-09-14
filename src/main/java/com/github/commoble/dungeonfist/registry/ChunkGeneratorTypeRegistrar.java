package com.github.commoble.dungeonfist.registry;

import com.github.commoble.dungeonfist.DungeonFist;
import com.github.commoble.dungeonfist.Registrator;
import com.github.commoble.dungeonfist.dimension.DungeonGenSettings;
import com.github.commoble.dungeonfist.world.DungeonChunkGenerator;

import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(DungeonFist.MODID)
public class ChunkGeneratorTypeRegistrar
{
	@ObjectHolder(DungeonFist.DUNGEON)
	public static final ChunkGeneratorType<DungeonGenSettings, DungeonChunkGenerator> DUNGEON = null;
	
	public static void registerChunkGeneratorTypes(Registrator<ChunkGeneratorType<?,?>> reg)
	{
		reg.register(DungeonFist.DUNGEON, new ChunkGeneratorType<DungeonGenSettings, DungeonChunkGenerator>(DungeonChunkGenerator::new, true, DungeonGenSettings::new));
	}
}
