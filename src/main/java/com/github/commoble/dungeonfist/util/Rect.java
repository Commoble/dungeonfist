package com.github.commoble.dungeonfist.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import net.minecraft.util.Direction;
import net.minecraft.util.math.ChunkPos;

public class Rect
{
	// list of all rects with sides of length 1, 2, or 3 and start-coords of 0,0
	public static final ArrayList<Rect> BUILDING_BLOCKS = new ArrayList<Rect>();
	public static final Rect CHUNK_RECT = new Rect(Vec2i.ZERO, Vec2i.CHUNK_SIZE);
	public static final Rect EMPTY_RECT = new Rect(Vec2i.ZERO, Vec2i.ZERO);
	// array of all square rects of START 0,0 and SIZE i,i where i is from 0 to 16 inclusive
	public static final Rect[] SQUARE_RECTS = IntStream.rangeClosed(0, 16).mapToObj(i -> new Rect(Vec2i.ZERO, new Vec2i(i,i))).toArray(size -> new Rect[size]);
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
	public final int AREA;
	
	public Rect(Vec2i start, Vec2i size)
	{
		this.START = start;
		this.SIZE = size;
		this.AREA = size.X * size.Y;
	}
	
	public int minSize()
	{
		return this.SIZE.minDimension();
	}
	
	public boolean isEmpty()
	{
		return this.SIZE.X <= 0 || this.SIZE.Y <= 0;
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
	
	public Vec2i randomCoord(Random rand)
	{
		List<Vec2i> coords = this.coords();
		int size = coords.size();
		return coords.get(rand.nextInt(size));
	}
	
	// returns true if this Rect can fit into another rect
	// i.e. both dimensions of this rect are no larger than the corresponding direction of the other rect
	public boolean fitsIn(Rect rect)
	{
		return this.SIZE.X <= rect.SIZE.X && this.SIZE.Y <= rect.SIZE.Y;
	}
	
	@Nonnull
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
			return Rect.EMPTY_RECT;
		int newSizeX = newEndX - newStartX + 1;
		int newSizeY = newEndY - newStartY + 1;
		
		return new Rect(new Vec2i(newStartX, newStartY), new Vec2i(newSizeX, newSizeY));
		
	}
	
	/**
	 * returns a Rect extending from this rect toward another rect in the specified direction
	 * size of the rect will be
	 */
	public Rect extension(Rect other, Direction extensionDirection)
	{
		if (other.equals(Rect.EMPTY_RECT))
		{
			return Rect.EMPTY_RECT;
		}
		
		int xSize, ySize, xStart, yStart;
		if (extensionDirection == Direction.NORTH || extensionDirection == Direction.UP)
		{
			xSize = this.SIZE.X;
			ySize = this.START.Y - other.START.Y - other.SIZE.Y;
			xStart = this.START.X;
			yStart = other.START.Y + other.SIZE.Y;
		}
		else if (extensionDirection == Direction.SOUTH || extensionDirection == Direction.DOWN)
		{
			xSize = this.SIZE.X;
			ySize = other.START.Y - this.START.Y - this.SIZE.Y;
			xStart = this.START.X;
			yStart = this.START.Y + this.SIZE.Y;
		}
		else if (extensionDirection == Direction.WEST)
		{
			xSize = this.START.X - other.START.X - other.SIZE.X;
			ySize = this.SIZE.Y;
			xStart = other.START.X + other.SIZE.X;
			yStart = this.START.Y;
		}
		else	// east
		{
			xSize = other.START.X - this.START.X - this.SIZE.X;
			ySize = this.SIZE.Y;
			xStart = this.START.X + this.SIZE.X;
			yStart = this.START.Y;
		}
		
		if (xSize <= 0 || ySize <= 0)
		{
			return Rect.EMPTY_RECT;
		}

		return new Rect(new Vec2i(xStart, yStart), new Vec2i(xSize, ySize));
	}
	
	/**
	 * -Returns a stream of Rects consisting of 1-width rects that represent the perimeter of this object
	 * -If this Rect is already of size 1 on either axis it just returns this Rect
	 * -If this Rect is of size 2 on either dimension it returns 2 rects
	 * -Otherwise it returns 4 rects, consisting of the entire top perimeter of the rect and the entire bottom perimeter,
	 * 	and the portions of the side perimeters that don't include the first two rects
	 */
	public Stream<Rect> asPerimeterRects()
	{
		if (this.SIZE.X <= 1 || this.SIZE.Y <= 1)
			return Stream.of(this);
		if (this.SIZE.X == 2)
		{
			Vec2i newSize = new Vec2i(1, this.SIZE.Y);
			Vec2i start1 = this.START;
			Vec2i start2 = new Vec2i(this.START.X+1, this.START.Y);
			Rect side1 = new Rect(start1, newSize);
			Rect side2 = new Rect(start2, newSize);
			return Stream.of(side1, side2);
		}
		if (this.SIZE.Y == 2)
		{
			Vec2i newSize = new Vec2i(this.SIZE.X, 1);
			Vec2i start1 = this.START;
			Vec2i start2 = new Vec2i(this.START.X, this.START.Y+1);
			Rect side1 = new Rect(start1, newSize);
			Rect side2 = new Rect(start2, newSize);
			return Stream.of(side1, side2);
		}
		int width = this.SIZE.X;
		int height = this.SIZE.Y - 2;
		Vec2i horizontalSize = new Vec2i(width, 1);
		Vec2i verticalSize = new Vec2i(1, height);
		Rect top = new Rect(this.START, horizontalSize);
		Rect bottom = new Rect(new Vec2i(this.START.X, this.START.Y + height + 1), horizontalSize);
		Rect left = new Rect(new Vec2i(this.START.X, this.START.Y + 1), verticalSize);
		Rect right = new Rect(new Vec2i(this.START.X + width - 1, this.START.Y + 1), verticalSize);
		return Stream.of(top, bottom, left, right);
	}
	
	public static Stream<Rect> getRectCollectionAsRectsWithinChunk(Stream<Rect> rects, ChunkPos pos, boolean local)
	{
		if (local)
		{
			// subtract this chunk's global position from rect's global position to get rect's relative position
			Vec2i offset = new Vec2i(-(pos.x << 4), -(pos.z << 4));
			return rects.map(rect -> rect.move(offset).intersection(Rect.CHUNK_RECT))
					.filter(rect -> !rect.equals(Rect.EMPTY_RECT));
		}
		else
		{
			// add this chunk's global position to the (pos 0,0; size 16,16) base rect to get a rect representing the chunk
			Rect chunkRect = Rect.CHUNK_RECT.move(new Vec2i(pos.x << 4, pos.z << 4));
			return rects.map(rect -> rect.intersection(chunkRect))
					.filter(rect -> !rect.equals(Rect.EMPTY_RECT));
		}
	}
	
	/**
	 * Returns a stream of rects that, when added together, would form the space defined by
	 * this rect, with the given rect subtracted from it
	 */
	public Stream<Rect> excise(Rect removedRect)
	{
		// 9-slice the original rect into regions based on the removedRect
		int startX0 = this.START.X;
		int startX1 = removedRect.START.X;
		int startX2 = removedRect.START.X + removedRect.SIZE.X;
		
		int startY0 = this.START.Y;
		int startY1 = removedRect.START.Y;
		int startY2 = removedRect.START.Y + removedRect.SIZE.Y;
		
		int sizeX0 = startX1-startX0;
		int sizeX1 = startX2-startX1;
		int sizeX2 = this.SIZE.X - sizeX0 - sizeX1;
		
		int sizeY0 = startY1-startY0;
		int sizeY1 = startY2-startY1;
		int sizeY2 = this.SIZE.Y - sizeY0 - sizeY1;
		
		int[] xStarts = {startX0, startX1, startX2};
		int[] yStarts = {startY0, startY1, startY2};
		int[] xSizes = {sizeX0, sizeX1, sizeX2};
		int[] ySizes = {sizeY0, sizeY1, sizeY2};
		
		ArrayList<Rect> output = new ArrayList<Rect>();
		
		IntStream.range(0, 3).forEach(x ->{
			IntStream.range(0, 3).forEach(y ->{
				if (x != 1 || y != 1)	// ignore the middle rect
				{
					output.add(new Rect(new Vec2i(xStarts[x], yStarts[y]), new Vec2i(xSizes[x], ySizes[y])));
				}
			});
		});
		
		// filter out the rects that shouldn't exist
		return output.stream().filter(rect ->
			!rect.isEmpty()
			&& rect.START.X >= this.START.X
			&& rect.START.Y >= this.START.Y
			&& rect.START.X + rect.SIZE.X <= this.START.X + this.SIZE.X
			&& rect.START.Y + rect.SIZE.Y <= this.START.Y + this.SIZE.Y
		);
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
		if (this == other)
		{
			return true;
		}
		else if (other instanceof Rect)
		{
			Rect otherRect = (Rect)other;
			return this.START.equals(otherRect.START) && this.SIZE.equals(otherRect.SIZE);
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Recursively divides a rect into randomly arranged rects that take up the same area
	 * Each iteration divides the rect into 1, 2, or 4 rects
	 * The bigger a given Rect is, the less likely it is to return itself without subdividing
	 */
	public Stream<Rect> asRandomSubdivisions(Random rand)
	{
		int xSizeA = rand.nextInt(this.SIZE.X)+1;	// min 1, max existing size
		int xSizeB = this.SIZE.X - xSizeA;	// may be 0
		int ySizeA = rand.nextInt(this.SIZE.Y)+1;	// min 1, max existing size
		int ySizeB = this.SIZE.Y - ySizeA;	// may be 0
		
		Vec2i startA = this.START;
		Vec2i sizeA = new Vec2i(xSizeA, ySizeA);
		Rect rectA = new Rect(startA, sizeA);
		if (xSizeB <= 0 && ySizeB <= 0)
		{
			return Stream.of(this);	// don't quadfurcate further if only returning single rect
		}
		
		Rect rectB = null;	// top-right,		+X 0Y
		Rect rectC = null;	// bottom-left,		0X +Y
		Rect rectD = null;	// bottom-right,	+X +Y
		int xStartB = startA.X + xSizeA;
		int yStartB = startA.Y + ySizeA;
		if(xSizeB > 0)
		{
			Vec2i startB = new Vec2i(xStartB, startA.Y);
			Vec2i sizeB = new Vec2i(xSizeB, ySizeA);
			rectB = new Rect(startB, sizeB);
		}
		if (ySizeB > 0)
		{
			Vec2i startC = new Vec2i(startA.X, yStartB);
			Vec2i sizeC = new Vec2i(xSizeA, ySizeB);
			rectC = new Rect(startC, sizeC);
		}
		if (xSizeB > 0 && ySizeB > 0)
		{
			Vec2i startD = new Vec2i(xStartB, yStartB);
			Vec2i sizeD = new Vec2i(xSizeB, ySizeB);
			rectD = new Rect(startD, sizeD);
		}
		return Stream.of(rectA, rectB, rectC, rectD).filter(rect -> rect != null).map(rect -> rect.asRandomSubdivisions(rand)).flatMap(i->i);
	}

	@Override
	public String toString()
	{
		return "Start={"+this.START+"}, Size={"+this.SIZE+"}";
	}
}
