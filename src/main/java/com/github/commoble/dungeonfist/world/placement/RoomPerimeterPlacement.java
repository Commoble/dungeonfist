package com.github.commoble.dungeonfist.world.placement;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.github.commoble.dungeonfist.util.RoomCaches;
import com.github.commoble.dungeonfist.util.RoomKey;
import com.mojang.datafixers.Dynamic;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.placement.Placement;

public class RoomPerimeterPlacement extends Placement<RoomPerimeterPlacementConfig>
{
	public RoomPerimeterPlacement(Function<Dynamic<?>, ? extends RoomPerimeterPlacementConfig> deserializer)
	{
		super(deserializer);
	}

	@Override
	public Stream<BlockPos> getPositions(IWorld world, ChunkGenerator<? extends GenerationSettings> generator, Random random, RoomPerimeterPlacementConfig config, BlockPos pos)
	{
		long seed = world.getSeed();
		ChunkPos chunkPos = new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4);
		return IntStream.range(0,5)
			.mapToObj(yLayer -> RoomCaches.ROOMLOADER.getUnchecked(new RoomKey(pos, yLayer, seed)))
			.map(room -> room.getRoomInteriorPerimeterWithinChunk(chunkPos, false)
					.map(rect -> rect.coords().stream())
					.flatMap(i -> i)
					.filter(i -> random.nextFloat() < 0.5F)
					.map(vec2i -> new BlockPos(vec2i.X, room.WORLD_YLEVEL, vec2i.Y)))
			.flatMap(i -> i);
	}
}
