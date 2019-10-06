package com.github.commoble.dungeonfist.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.PressurePlateBlock;

public class ConstructableStonePressurePlateBlock extends PressurePlateBlock
{
	public ConstructableStonePressurePlateBlock(Block.Properties props)
	{
		super(Sensitivity.MOBS, props);
	}
}
