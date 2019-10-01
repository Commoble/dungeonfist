package com.github.commoble.dungeonfist.world.dungature;

import java.util.Random;
import java.util.function.Function;

import com.github.commoble.dungeonfist.util.Rect;
import com.github.commoble.dungeonfist.util.Room;

import net.minecraft.world.IWorld;

public class EmptyDungature extends Dungature
{
	public static final Function<DungatureContext, Dungature> SUPPLIER = context -> new EmptyDungature();

	@Override
	public void place(Rect rect, Rect chunkRect, Room room, IWorld world, Random rand)
	{
		// nope		
	}

}
