package com.github.commoble.dungeonfist.data.loot;

import com.github.commoble.dungeonfist.DungeonFist;

import net.minecraft.util.ResourceLocation;

public class DungeonLootTables
{
	// loot functions
	public static final ResourceLocation APPLY_FUNCTIONS_IF_ITEM_HAS_TAG = DungeonFist.getResourceLocation("apply_functions_if_item_has_tag");
	public static final ResourceLocation IMPROVE_NAME_BASED_ON_ENCHANTMENTS = DungeonFist.getResourceLocation("improve_name_based_on_enchantments");
	
	public static final ResourceLocation TEST = DungeonFist.getResourceLocation("chests/test");
	
	public static final ResourceLocation WEAPONS = DungeonFist.getResourceLocation("subtables/weapons");
	public static final ResourceLocation ENCHANTED_WEAPONS = DungeonFist.getResourceLocation("subtables/enchanted_weapons");
}
