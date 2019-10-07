package com.github.commoble.dungeonfist.util;

import net.minecraft.util.Direction;

public class ExitHallway
{
	public final Rect rect;
	public final Direction dir;	// points toward the room this hallway is part of
	public final int otherRoomFloorY; // floor ylevel of the room on the other side of the exit
	
	public ExitHallway(Direction dir, Rect rect, int otherRoomFloorY)
	{
		this.dir = dir;
		this.rect = rect;
		this.otherRoomFloorY = otherRoomFloorY;
	}
	
	public ExitHallway move(Vec2i offset)
	{
		return new ExitHallway(this.dir, this.rect.move(offset), this.otherRoomFloorY);
	}
	
	public ExitHallway intersection(Rect rect)
	{
		return new ExitHallway(this.dir, this.rect.intersection(rect), this.otherRoomFloorY);
	}
}
