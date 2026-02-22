package net.commoble.dungeonfist;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

public final class DungeonFistBiomes
{
	private DungeonFistBiomes() {}
	
	private static ResourceKey<Biome> key(String path)
	{
		return ResourceKey.create(Registries.BIOME, DungeonFist.id(path));
	}
	
	public static final ResourceKey<Biome> DUNGEON = key("dungeon");
}
