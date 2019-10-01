package com.github.commoble.dungeonfist.world.dungature;

import java.util.Random;

import com.github.commoble.dungeonfist.util.Rect;
import com.github.commoble.dungeonfist.util.Room;

public class DungatureContext
{
	public final Rect rect;
	public final Room room;
	public final Random rand;
	
	public DungatureContext(Rect rect, Room room, Random rand)
	{
		this.rect = rect;
		this.room = room;
		this.rand = rand;
	}
}
