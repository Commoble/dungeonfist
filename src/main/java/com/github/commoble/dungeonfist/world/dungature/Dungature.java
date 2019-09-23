package com.github.commoble.dungeonfist.world.dungature;

import java.util.Random;

import com.github.commoble.dungeonfist.util.Rect;
import com.github.commoble.dungeonfist.util.Room;

import net.minecraft.world.IWorld;

/**
 * I'd call these Features but Features are already called Features and these aren't Features
 **/
public abstract class Dungature
{
	public abstract void place(Rect rect, Room room, IWorld world, Random random);
}
