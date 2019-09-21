package com.github.commoble.dungeonfist.world.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.world.gen.placement.IPlacementConfig;

public class RoomPerimeterPlacementConfig implements IPlacementConfig
{
	public final int minPerChunk;
	public final int maxPerChunk;

	public RoomPerimeterPlacementConfig(int minPerChunk, int maxPerChunk)
	{
		this.minPerChunk = minPerChunk;
		this.maxPerChunk = maxPerChunk;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops)
	{
		return new Dynamic<>(ops,
				ops.createMap(ImmutableMap.of(
						ops.createString("min_per_chunk"), ops.createInt(this.minPerChunk),
						ops.createString("max_per_chunk"), ops.createInt(this.maxPerChunk))));
	}

	public static RoomPerimeterPlacementConfig deserialize(Dynamic<?> dynamic)
	{
		int min = dynamic.get("min_per_chunk").asInt(0);
		int max = dynamic.get("max_per_chunk").asInt(0);
		return new RoomPerimeterPlacementConfig(min, max);
	}
}
