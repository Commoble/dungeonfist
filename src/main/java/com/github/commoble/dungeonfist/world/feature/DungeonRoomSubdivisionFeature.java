package com.github.commoble.dungeonfist.world.feature;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;

import com.github.commoble.dungeonfist.util.RoomCaches;
import com.github.commoble.dungeonfist.util.RoomKey;
import com.github.commoble.dungeonfist.world.DungeonChunkGenerator;
import com.mojang.datafixers.Dynamic;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

// intention: use with Placement.NOPE which uses a single blockpos at each chunk's origin
public class DungeonRoomSubdivisionFeature extends Feature<NoFeatureConfig>
{
	public DungeonRoomSubdivisionFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> configFactoryIn)
	{
		super(configFactoryIn);
	}

	@Override
	public boolean place(IWorld world, ChunkGenerator<? extends GenerationSettings> generator, Random rand,
			BlockPos pos, NoFeatureConfig config)
	{
		final int blockCount = DungeonChunkGenerator.debugBlockCount;
		DungeonChunkGenerator.debugBlocks.ifPresent(blocks -> 
		{
			long seed = world.getSeed();
			ChunkPos chunkPos = new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4);
			IntStream.range(0,5).forEach(yLayer -> {
				RoomCaches.ROOMLOADER.getUnchecked(new RoomKey(pos, yLayer, seed)).SUBDIVIDED_INTERIOR
				.forEach(rect -> {
					BlockState state = blocks.get(rand.nextInt(DungeonChunkGenerator.debugBlockCount));
					rect.coords().forEach(coord ->
						world.setBlockState(new BlockPos(coord.X, yLayer*50 + 10, coord.Y), state, 2));
				});
			});
				
		});
		
		return true;
	}

}
