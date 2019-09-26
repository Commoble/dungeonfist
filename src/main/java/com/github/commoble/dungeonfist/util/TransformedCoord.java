package com.github.commoble.dungeonfist.util;

public class TransformedCoord
{
	public final Vec2i originalVec;
	public final Vec2i finalVec;
	
	public TransformedCoord(Vec2i vec0, Vec2i vec1)
	{
		this.originalVec = vec0;
		this.finalVec = vec1;
	}
}
