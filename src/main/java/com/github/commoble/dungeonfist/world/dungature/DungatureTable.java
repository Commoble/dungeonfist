package com.github.commoble.dungeonfist.world.dungature;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;

// thanks to Peter Lawrey from the stackoverflow whence this was found
// https://stackoverflow.com/questions/6409652/random-weighted-selection-in-java/30362366
public class DungatureTable
{
	private final ArrayList<TreeMap<Integer, Function<DungatureContext, Dungature>>> maps = new ArrayList<TreeMap<Integer, Function<DungatureContext, Dungature>>>();
	private final int size;	// number of tables in this table
		// so the first table would hold all dungatures that can generate with size 1 or greater,
		// the fifth table would hold all dungatures that must be of at least size 5, etc
	private int[] totals;
	
	public DungatureTable(int size)
	{
		this.size = size;
		this.totals = new int[size]; // i <==> size-1; i in range [0,15], size in range [1,16]
		IntStream.range(0,16).forEach(i -> this.maps.add(new TreeMap<Integer, Function<DungatureContext, Dungature>>()));
	}
	
	/**
	 * Add an item to the collection with the given selection weight, and returns the collection for chaining.
	 * If weight <= 0, then the item is not added.
	 * minSizes < 1 are treated as 1
	 */
	
	public DungatureTable add(IntUnaryOperator size2weightFunction, Function<DungatureContext, Dungature> generator)
	{
		for (int index=0; index < this.size; index++)
		{
			int size = index + 1;
			int weight = size2weightFunction.applyAsInt(size);
			if (weight > 0)
			{
				this.totals[index] += weight;	// add weight to totals for all sizes of this size or greater 
				this.maps.get(index).put(this.totals[index], generator);
			}
		}
		
		return this;
	}
	/**
	 * Returns a random item from the collection, selected proportionally to the given weights
	 * (will return null if and only if the collection is empty)
	 */
	public Function<DungatureContext, Dungature> next(int minSize, Random rand)
	{
		int i = minSize-1;
		if (i > this.size-1)
			i = this.size-1;
		
		int maxRoll = this.totals[i];
		if (maxRoll <= 0)
			return EmptyDungature.SUPPLIER;
		
		int roll = rand.nextInt(this.totals[i]);
		return this.maps.get(i).higherEntry(roll).getValue();
	}
}
