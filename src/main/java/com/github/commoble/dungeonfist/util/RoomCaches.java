package com.github.commoble.dungeonfist.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class RoomCaches
{

	public static final LoadingCache<RoomKey, Room> ROOMLOADER = CacheBuilder.newBuilder()
			.maximumSize(1000)
			.build(
				new CacheLoader<RoomKey, Room>()
				{
					public Room load(RoomKey key)
					{
						return new Room(key);
					}
				}
			);
	
	public static final LoadingCache<RoomKey, RegionSideExits> EXITLOADER = CacheBuilder.newBuilder()
			.maximumSize(1000)
			.build(
				new CacheLoader<RoomKey, RegionSideExits>()
				{
					public RegionSideExits load(RoomKey key)
					{
						return new RegionSideExits(key);
					}
				}
			);
}
