package com.github.commoble.dungeonfist.world;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

import com.mojang.datafixers.Dynamic;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;

public class DungeonPortalPlacement extends Placement<NoPlacementConfig>
{
	public DungeonPortalPlacement(Function<Dynamic<?>, ? extends NoPlacementConfig> configFactoryIn)
	{
		super(configFactoryIn);
	}

	// pos is chunkX*16, 0, chunkZ*16
	@Override
	public Stream<BlockPos> getPositions(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generatorIn,
			Random random, NoPlacementConfig configIn, BlockPos pos)
	{
		if (worldIn.getDimension().getType().getId() == 0)
		{
			return Stream.of(
					new BlockPos(
							pos.getX() + random.nextInt(16),
							20 + random.nextInt(worldIn.getHeight() - 40),
							pos.getZ() + random.nextInt(16)
							)
					);
		}
		else
		{
			return Stream.empty();
		}
	}
}
