package com.github.commoble.dungeonfist.util;

import java.util.List;
import java.util.Random;

public class CollectionHelper
{
	public static <T> T getRandomItem(List<T> list, Random random)
	{
		return list.get(random.nextInt(list.size()));
	}
}
