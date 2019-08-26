package com.github.commoble.dungeonfist.registry;

import com.github.commoble.dungeonfist.DungeonFist;
import com.github.commoble.dungeonfist.RegistryHelper;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public class ItemRegistrar
{
	public static void registerItems(RegistryHelper<Item> reg)
	{
		reg.register(DungeonFist.DUNGEON_PORTAL, new BlockItem(BlockRegistrar.DUNGEON_PORTAL, new Item.Properties().group(ItemGroup.MISC)));
	}
}
