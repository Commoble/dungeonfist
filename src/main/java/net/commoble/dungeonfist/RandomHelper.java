package net.commoble.dungeonfist;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.RandomSource;

public final class RandomHelper
{
	private RandomHelper() {}
	
	public static <T> List<T> shuffled(List<T> list, RandomSource random)
	{
		list = new ArrayList<>(list); // make shallow copy so we can remove without modifying
		List<T> shuffled = new ArrayList<>();
		while (!list.isEmpty())
		{
			shuffled.add(list.remove(random.nextInt(list.size())));
		}
		return shuffled;
	}
}
