package com.github.commoble.dungeonfist.world.dungature;

import java.util.function.IntUnaryOperator;

public class WeightingFunctions
{
	public static int identity(int weight, int size)
	{
		return weight;
	}
	
	public static IntUnaryOperator constantWeight(int weight)
	{
		return size -> weight;
	}
	
	public static IntUnaryOperator linearScaling(int weight)
	{
		return size -> size*weight;
	}
	
	public static IntUnaryOperator withMinSize(int weight, int minSize)
	{
		return size-> size < minSize ? 0 : weight*minSize*minSize;
	}
}
