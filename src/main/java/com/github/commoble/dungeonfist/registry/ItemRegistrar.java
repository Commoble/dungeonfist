package com.github.commoble.dungeonfist.registry;

import com.github.commoble.dungeonfist.DungeonFist;
import com.github.commoble.dungeonfist.Registrator;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemRegistrar
{
	public static void registerItems(Registrator<Item> reg)
	{
		reg.register(DungeonFist.DUNGEON_PORTAL, new BlockItem(BlockRegistrar.DUNGEON_PORTAL, new Item.Properties().group(ItemGroup.MISC)));
	}
}
