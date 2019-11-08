package com.github.commoble.dungeonfist;

import net.minecraft.util.ResourceLocation;

public class DungeonLootTables
{
	public static ResourceLocation TEST = getDungeonLootTable("chests/test");
	
	public static ResourceLocation getDungeonLootTable(String name)
	{
		return new ResourceLocation(DungeonFist.MODID, name);
	}
}
