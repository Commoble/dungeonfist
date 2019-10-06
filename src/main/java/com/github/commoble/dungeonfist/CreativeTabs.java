package com.github.commoble.dungeonfist;

import net.minecraft.block.Block;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.ObjectHolder;

public class CreativeTabs
{
	@ObjectHolder(DungeonFist.MODID + ":slate_stone_bricks")
	public static final Block slateStoneBricks = null;

	public static final ItemGroup rocks = new ItemGroup(DungeonFist.MODID + ".rocks")
	{
		@Override
		public ItemStack createIcon()
		{
			return new ItemStack(slateStoneBricks);
		}
	};
}
