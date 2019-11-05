package com.github.commoble.dungeonfist.world.placement;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

import com.github.commoble.dungeonfist.world.PortalPlacementRules;
import com.mojang.datafixers.Dynamic;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;

public class CarcelithPlacement extends Placement<NoPlacementConfig>
{

	public CarcelithPlacement(Function<Dynamic<?>, ? extends NoPlacementConfig> configFactoryIn)
	{
		super(configFactoryIn);
	}

	@Override
	public Stream<BlockPos> getPositions(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generatorIn,
			Random random, NoPlacementConfig configIn, BlockPos pos)
	{
		ChunkPos chunkPos = new ChunkPos(pos);
		if (worldIn.getDimension().getType() == DimensionType.OVERWORLD && PortalPlacementRules.shouldPlaceInOverworld(worldIn.getSeed(), chunkPos))
		{
			return Stream.of(PortalPlacementRules.getPortalPosInOverworld(worldIn, chunkPos));
		}
		else
		{
			return Stream.empty();
		}
	}

}
