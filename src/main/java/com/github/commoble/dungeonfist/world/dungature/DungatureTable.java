package com.github.commoble.dungeonfist.world.dungature;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;
import java.util.stream.IntStream;

// thanks to Peter Lawrey from the stackoverflow whence this was found
// https://stackoverflow.com/questions/6409652/random-weighted-selection-in-java/30362366
public class DungatureTable
{
	private final ArrayList<TreeMap<Integer, Dungature>> maps = new ArrayList<TreeMap<Integer, Dungature>>();
	private int[] totals = new int[16];	// i <==> size-1; i in range [0,15], size in range [1,16]
	
	public DungatureTable()
	{
		IntStream.range(0,16).forEach(i -> this.maps.add(new TreeMap<Integer, Dungature>()));
	}
	
	/**
	 * Add an item to the collection with the given selection weight, and returns the collection for chaining.
	 * If weight <= 0, then the item is not added.
	 * minSizes < 1 are treated as 1
	 */
	public DungatureTable add(int weight, int minSize, Dungature item)
	{
		if (minSize < 1)
			minSize = 1;
		if (weight > 0 && minSize > 0)
		{
			for (int i=minSize-1; i < 16; i++)
			{
				this.totals[i] += weight;	// add weight to totals for all sizes of this size or greater 
				this.maps.get(i).put(this.totals[i], item);
			}
		}
		
		return this;
	}
	
	/**
	 * Returns a random item from the collection, selected proportionally to the given weights
	 * (will return null if and only if the collection is empty)
	 */
	public Dungature next(int minSize, Random rand)
	{
		int i = minSize-1;
		int roll = rand.nextInt(this.totals[i]);
		return this.maps.get(i).higherEntry(roll).getValue();
	}
}
