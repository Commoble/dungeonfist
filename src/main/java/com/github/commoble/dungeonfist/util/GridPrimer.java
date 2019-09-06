package com.github.commoble.dungeonfist.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Thanks to gigaherz and julian from the Minecraft Mod Development discord for helping me with the math on this one
 *
 */
public class GridPrimer
{
	public List<Rect> rects;
	
	public GridPrimer()
	{
		this.rects = new ArrayList<>();
	}
	
	public GridPrimer(List<Rect> rects)
	{
		this.rects = rects;
	}
	
	// return a GridPrimer with this grid's rects + an additional rect
	public GridPrimer withRect(Rect rect)
	{
		List<Rect> newList = new ArrayList<>();
		for (Rect existingRect : this.rects)
		{
			newList.add(existingRect);
		}
		newList.add(rect);
		return new GridPrimer(newList);
	}
	
	// returns the next unused position in the 3x3 grid in read order
	public Vec2i getFreePosition()
	{
		for (int x : Rect.START_VALS)
		{
			for (int y : Rect.START_VALS)
			{
				Vec2i coord = new Vec2i(x,y);
				if (this.getSomeBlockingRect(coord) == null)
				{
					return coord;
				}
			}
		}
		
		return null;
	}
	
	// if at least one of this grid's rects overlaps the given coordinate, return that rect
	// otherwise return null if the position is unused
	public Rect getSomeBlockingRect(Vec2i coord)
	{
		for (Rect rect : this.rects)
		{
			if (rect.contains(coord))
				return rect;
		}
		return null;
	}
	
	// returns a list of all rects that can be placed in the grid with the following conditions:
	// 1) the given coord is the top-left corner of the rect
	// 2) the rect doesn't overlap any existing rects
	// 3) the rect doesn't breach the boundaries of the 3x3 grid
	public List<Rect> getValidAdditionalRects(Vec2i coord)
	{
		List<Rect> validRects = new ArrayList<Rect>();
		for (Rect block : Rect.BUILDING_BLOCKS)
		{
			Rect rect = block.move(coord);
			if (this.isRectValid(rect))
			{
				validRects.add(rect);
			}
		}
		return validRects;
	}
	
	// returns true if the rect can be placed in the grid given these conditions:
	// 1) the rect doesn't breach the boundaries of the 3x3 grid
	// 2) the rect doesn't overlap any existing rects in the grid
	public boolean isRectValid(Rect rect)
	{
		List<Vec2i> coords = rect.coords();
		for (Vec2i coord : coords)
		{
			if (coord.X < 0 || coord.X > 2 || coord.Y < 0 || coord.Y > 2)
			{
				return false;
			}
			if (this.getSomeBlockingRect(coord) != null)
			{
				return false;
			}
		}
		return true;
	}
}
