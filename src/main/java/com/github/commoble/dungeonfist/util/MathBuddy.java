package com.github.commoble.dungeonfist.util;

public class MathBuddy
{
	public static int absoluteMod(int x, int n)
	{
		int out = x % n;
		if (out < 0) out += n;
		return out;
	}
	
	// scale coordinate from e.g. blockpos to chunk, or superchunk to area
	// if factor is a power of 2, use >> instead
	// if scaling from large to small, use << or *
	public static int rescaleCoordinate(int in, int factor)
	{
		if (in >= 0)
		{
			return in / factor;
		}
		else
		{
			return (in+1-factor) / factor;
		}
	}
}
