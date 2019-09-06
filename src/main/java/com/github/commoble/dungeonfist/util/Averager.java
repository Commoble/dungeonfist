package com.github.commoble.dungeonfist.util;

import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;

public class Averager
{
	long sum=0;
	LongArrayFIFOQueue q = new LongArrayFIFOQueue();
	
	public Averager() {}
	
	public float add(long in)
	{
		this.sum += in;
		q.enqueue(in);
		if (q.size() > 10)
		{
			sum -= q.dequeueLong();
		}
		return (float)sum / (float)(q.size());
	}
}
