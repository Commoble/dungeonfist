package com.github.commoble.dungeonfist.dimension;

import java.util.function.BiFunction;

import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.ModDimension;

public class DungeonModDimension extends ModDimension
{

	@Override
	public BiFunction<World, DimensionType, ? extends Dimension> getFactory()
	{
		// TODO Auto-generated method stub
		return DungeonDimension::new;
	}

}
