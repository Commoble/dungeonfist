package com.github.commoble.dungeonfist.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

public class Rect
{
	// list of all rects with sides of length 1, 2, or 3 and start-coords of 0,0
	public static final ArrayList<Rect> BUILDING_BLOCKS = new ArrayList<Rect>();
	public static final Rect CHUNK_RECT = new Rect(Vec2i.ZERO, Vec2i.CHUNK_SIZE);
	public static final int[] START_VALS = {0,1,2};
	public static final int[] SIZE_VALS = {1,2,3};
	static
	{
		Vec2i start = new Vec2i(0,0);
		for (int x : SIZE_VALS)
		{
			for (int y : SIZE_VALS)
			{
				BUILDING_BLOCKS.add(new Rect(start, new Vec2i(x,y)));
			}
		}
	}
	
	public final Vec2i START;
	public final Vec2i SIZE;
	
	public Rect(Vec2i start, Vec2i size)
	{
		this.START = start;
		this.SIZE = size;
	}
	
	public Rect move(Vec2i start)
	{
		return new Rect(this.START.add(start), this.SIZE);
	}
	
	public boolean contains(Vec2i xy)
	{
		return xy.X >= this.START.X && xy.X < this.START.X+this.SIZE.X && xy.Y >= this.START.Y && xy.Y < this.START.Y+this.SIZE.Y;
	}
	
	public List<Vec2i> coords()
	{
		List<Vec2i> coords = new ArrayList<Vec2i>();
		IntStream.range(this.START.X, this.START.X+this.SIZE.X).forEach(x ->
			IntStream.range(this.START.Y, this.START.Y+this.SIZE.Y).forEach(y ->
				coords.add(new Vec2i(x,y))
			)
		);
		
		return coords;
	}
	
	@Nullable
	public Rect intersection(Rect other)
	{
		int thisXend = this.START.X + this.SIZE.X - 1;
		int thisYend = this.START.Y + this.SIZE.Y - 1;
		int otherXend = other.START.X + other.SIZE.X - 1;
		int otherYend = other.START.Y + other.SIZE.Y - 1;
		int newStartX = Math.max(this.START.X, other.START.X);
		int newStartY = Math.max(this.START.Y, other.START.Y);
		int newEndX = Math.min(thisXend, otherXend);
		int newEndY = Math.min(thisYend, otherYend);
		if (newEndX < newStartX || newEndY < newStartY)
			return null;
		int newSizeX = newEndX - newStartX + 1;
		int newSizeY = newEndY - newStartY + 1;
		
		return new Rect(new Vec2i(newStartX, newStartY), new Vec2i(newSizeX, newSizeY));
		
	}
	
	@Override
	public int hashCode()
	{
		int h1 = this.START.hashCode();
		int h2 = this.SIZE.hashCode();
		return (h1*h2) + h2;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Rect)
		{
			Rect otherRect = (Rect)other;
			return this.START.equals(otherRect.START) && this.SIZE.equals(otherRect.SIZE);
		}
		else
		{
			return false;
		}
	}

	@Override
	public String toString()
	{
		return "Start={"+this.START+"}, Size={"+this.SIZE+"}";
	}
}
