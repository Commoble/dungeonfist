package com.github.commoble.dungeonfist.registry;

import com.github.commoble.dungeonfist.DungeonFist;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;

public class DimensionTypeRegistrar
{
	public static final ResourceLocation dungeonResource = new ResourceLocation(DungeonFist.MODID, DungeonFist.DUNGEON);
	
	public static void registerDimensionTypes()
	{
		if (getDungeonDimensionType() == null)
		{
			DimensionManager.registerDimension(dungeonResource, ModDimensionRegistrar.DUNGEON, null, true);
		}
		// DimensionManager.registerDimensionInternal(DUNGEON.getId(), dungeonResource, ModDimensionRegistrar.DUNGEON, null, false);
	}
	
	public static DimensionType getDungeonDimensionType()
	{
		return DimensionType.byName(dungeonResource);
	}
}
