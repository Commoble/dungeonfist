package com.github.commoble.dungeonfist.util;

import net.minecraft.util.math.ChunkPos;

public class Vec2i
{
	public static final Vec2i ZERO = new Vec2i(0,0);
	public static final Vec2i CHUNK_SIZE = new Vec2i(16,16);
	
	public final int X;
	public final int Y;
	public Vec2i(int x, int y)
	{
		this.X = x;
		this.Y = y;
	}
	
	public int minDimension()
	{
		return Math.min(this.X, this.Y);
	}
	
	public Vec2i add(Vec2i otherVec)
	{
		return new Vec2i(this.X + otherVec.X, this.Y + otherVec.Y);
	}
	
	public Vec2i subtract(Vec2i otherVec)
	{
		return new Vec2i(this.X - otherVec.X, this.Y - otherVec.Y);
	}
	
	public ChunkPos subtractThisFrom(ChunkPos otherVec)
	{
		return new ChunkPos(otherVec.x - this.X, otherVec.z - this.Y);
	}
	
	@Override
	public int hashCode()
	{
		return (this.X+this.Y)*173+this.X;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Vec2i)
		{
			Vec2i coord = (Vec2i)other;
			return this.X == coord.X && this.Y == coord.Y;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public String toString()
	{
		return "["+this.X+","+this.Y+"]";
	}
}
