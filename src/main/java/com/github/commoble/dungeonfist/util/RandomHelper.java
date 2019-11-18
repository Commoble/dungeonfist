package com.github.commoble.dungeonfist.util;

import java.util.List;
import java.util.Random;

public class RandomHelper
{
	public static <T> T getRandomThingFrom(Random rand, List<T> things)
	{
		return things.get(rand.nextInt(things.size()));
	}
	
	@SafeVarargs
	public static <T> T chooseRandomThing(Random rand, T...things)
	{
		return things[rand.nextInt(things.length)];
	}
}
