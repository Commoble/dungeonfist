package com.github.commoble.dungeonfist.world.dungature;

import java.util.Random;

import com.github.commoble.dungeonfist.util.Rect;
import com.github.commoble.dungeonfist.util.Room;
import com.github.commoble.dungeonfist.util.TriFunction;

import net.minecraft.world.IWorld;

public class EmptyDungature extends Dungature
{
	public static final TriFunction<Rect, Room, Random, Dungature> SUPPLIER = (rect,room,rand) -> new EmptyDungature();

	@Override
	public void place(Rect rect, Rect chunkRect, Room room, IWorld world, Random rand)
	{
		// nope		
	}

}
