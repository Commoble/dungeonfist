package com.github.commoble.dungeonfist.world.feature;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;

import com.github.commoble.dungeonfist.util.Rect;
import com.github.commoble.dungeonfist.util.Room;
import com.github.commoble.dungeonfist.util.RoomCaches;
import com.github.commoble.dungeonfist.util.RoomKey;
import com.github.commoble.dungeonfist.world.DungeonChunkGenerator;
import com.github.commoble.dungeonfist.world.dungature.StandardDungatures;
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
				Room room = RoomCaches.ROOMLOADER.getUnchecked(new RoomKey(pos, yLayer, seed));
				Rect.getRectCollectionAsRectsWithinChunk(room.SUBDIVIDED_INTERIOR.stream(), chunkPos, false)
				.filter(rect -> rand.nextDouble() < 0.05F)
				.forEach(rect -> {
					BlockState state = blocks.get(rand.nextInt(DungeonChunkGenerator.debugBlockCount));
					rect.coords().forEach(coord ->
						world.setBlockState(new BlockPos(coord.X, room.WORLD_YLEVEL, coord.Y), state, 2));
					StandardDungatures.table.next(rect.minSize(), rand).place(rect, room, world, rand);
				});
			});
				
		});
		
		return true;
	}

}
