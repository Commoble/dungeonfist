package net.commoble.dungeonfist;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.DimensionType;

public final class DungeonFistDimensionTypes
{
	private DungeonFistDimensionTypes() {}
	
	private static ResourceKey<DimensionType> key(String path)
	{
		return ResourceKey.create(Registries.DIMENSION_TYPE, DungeonFist.id(path));
	}
	
	public static final ResourceKey<DimensionType> DUNGEON = key("dungeon");
}
