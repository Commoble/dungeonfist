package com.github.commoble.dungeonfist.util;

import java.util.Random;

public class TransformHelper
{
	public static Vec2i withStandardTransforms(Vec2i input, int size, Transform transform)
	{
		input = withRotation(input, size, transform.rotation);
		input = withMirror(input, size, transform.mirror);
		input = input.add(transform.translation);
		return input;
	}
	
	/**
	 * Given a Vec2i that exists with a square rect that begins at 0,0 and has a SIZE of the given size,
	 * rotate the Vec2i zero, one, two, or three 90-degree rotations such that it
	 * remains within the given Rect
	 * if rotations is not in the range [0,3] then this returns the input vector
	 */
	public static Vec2i withRotation(Vec2i inputVec, int size, int rotations)
	{
		int maxPos = size-1;
		int x0 = inputVec.X;
		int y0 = inputVec.Y;
		switch(rotations)
		{
			case 0:
				return inputVec;
			case 1:
				return new Vec2i(-y0 + maxPos, x0);
			case 2:
				return new Vec2i(-x0 + maxPos, -y0 + maxPos);
			case 3:
				return new Vec2i(y0, -x0 + maxPos);
			default:
					return inputVec;
		}
	}
	
	/**
	 * Given a Vec2i that exists within a square Rect that begins at 0,0 and has a SIZE of the given size
	 * mirror the vec2i such that it still exists within the given rect but is flipped on zero, one, or both axes
	 * flips on X if mirror is 1 or 3, flips on Y if mirror is 2 or 3
	 * (passing the result of rand.nextInt(4) ensures an even spread of results)
	 */
	public static Vec2i withMirror(Vec2i vec, int size, int mirror)
	{
		int maxPos = size-1;
		int x = vec.X;
		int y = vec.Y;
		if (mirror % 2 != 0)	// flip X
		{
			x = maxPos-x;
		}
		if (mirror / 2 != 0)	// flip Y
		{
			y = maxPos-y;
		}
		return new Vec2i(x,y);
	}
	
	/**
	 * given a Rect, and given another rect whose size is sufficient to entirely contain the first rect,
	 * returns a random offset that is guaranteed to place the first rect somewhere inside the second rect
	 * If this is being used after a rotation has been determined, it is assumed that a square rect is being used
	 * To use this in the manner of the above methods, we can just add the result to all the component vec2is of the rect
	 */
	public static Vec2i getRandomTranslation(Rect movingRect, Rect target, Random rand)
	{
		int minXOffset = target.START.X - movingRect.START.X;	// the minimum amount of x offset we must use
		int maxExtraXOffset = target.SIZE.X - movingRect.SIZE.X;	// the most we can add to the minXOffset
		int minYOffset = target.START.Y - movingRect.START.Y;
		int maxExtraYOffset = target.SIZE.Y - movingRect.SIZE.Y;
		
		int xOffset = maxExtraXOffset > 0 ? rand.nextInt(maxExtraXOffset) + minXOffset : minXOffset;
		int yOffset = maxExtraYOffset > 0 ? rand.nextInt(maxExtraYOffset) + minYOffset : minYOffset;
		
		return new Vec2i(xOffset, yOffset);
	}
}
