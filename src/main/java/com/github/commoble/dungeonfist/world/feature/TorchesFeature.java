package com.github.commoble.dungeonfist.world.feature;

import java.util.Random;
import java.util.function.Function;

import com.mojang.datafixers.Dynamic;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class TorchesFeature extends Feature<NoFeatureConfig>
{
	public TorchesFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> configFactoryIn)
	{
		super(configFactoryIn);
	}

	@Override	// normally placements use blockpos in global blockspace but we are using it in local chunk blockspace 
	public boolean place(IWorld world, ChunkGenerator<? extends GenerationSettings> generator, Random rand,
			BlockPos pos, NoFeatureConfig config)
	{
		/**
		 * Reminder for tomorrow joe:
		 * Problem 1: the thing that constricts a rect to a chunk returns rects in local chunk blockspace
		 * Problem 2: we *must* pass a blockpos in global blockspace because the blockpos is the only information
		 * 	we have w.r.t. which chunk the pos is supposed to be in
		 */
		world.setBlockState(pos.up(3), Blocks.LANTERN.getDefaultState(), 2);
		return true;
	}
}
