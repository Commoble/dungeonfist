package com.github.commoble.dungeonfist.util;

import java.util.ArrayList;
import java.util.List;

public class AreaGrid
{
	public static final List<AreaGrid> grids = new ArrayList<AreaGrid>();
	public static final int GRIDCOUNT;
	static
	{
		permutate(new GridPrimer(), grids);
		GRIDCOUNT = grids.size();
	}
	
	public static void permutate(GridPrimer primer, List<AreaGrid> output)
	{
		Vec2i start = primer.getFreePosition();
		if (start == null)
		{
			output.add(new AreaGrid(primer));
			return;
		}
		
		for (Rect rect : primer.getValidAdditionalRects(start))
		{
			GridPrimer newPrimer = primer.withRect(rect);
			permutate(newPrimer, output);
		}
	}
	
	// areagrid represents a 3x3 grid of randomly-arranged rectangles
	// the offset in this array represents the coordinate of the top-left corner
	// of the rectangle that overlaps that location in the array
	public final Vec2i[][] offsets;
	public final Vec2i[][] sizes;	// size of the rectangle that overlaps this location
	
	public AreaGrid(GridPrimer primer)
	{
		this.offsets = new Vec2i[3][3];
		this.sizes = new Vec2i[3][3];
		for (Rect rect : primer.rects)
		{
			List<Vec2i> coords = rect.coords();
			for (Vec2i coord : coords)
			{
				this.offsets[coord.X][coord.Y] = rect.START;
				this.sizes[coord.X][coord.Y] = rect.SIZE; 
			}
		}
	}
	
	@Override
	public String toString()
	{
		return "\n"+offsets[0][0]+","+offsets[1][0]+","+offsets[2][0]+'\n'+
				offsets[0][1]+','+offsets[1][1]+","+offsets[2][1]+'\n'+
				offsets[0][2]+','+offsets[1][2]+","+offsets[2][2]+"\n";
	}
}
