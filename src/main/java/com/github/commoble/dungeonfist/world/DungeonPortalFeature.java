package com.github.commoble.dungeonfist.world;

import java.util.Random;
import java.util.function.Function;

import com.github.commoble.dungeonfist.registry.BlockRegistrar;
import com.mojang.datafixers.Dynamic;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class DungeonPortalFeature extends Feature<NoFeatureConfig>
{

	public DungeonPortalFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> configFactoryIn)
	{
		super(configFactoryIn);
	}

	@Override
	public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand,
			BlockPos pos, NoFeatureConfig config)
	{
		worldIn.setBlockState(pos, BlockRegistrar.DUNGEON_PORTAL.getDefaultState(), 2);
		worldIn.setBlockState(pos.up(), BlockRegistrar.DUNGEON_PORTAL_GLOWY_AIR.getDefaultState(), 2);
		worldIn.setBlockState(pos.down(), BlockRegistrar.DUNGEON_PORTAL_GLOWY_AIR.getDefaultState(), 2);
		return true;
	}

}
