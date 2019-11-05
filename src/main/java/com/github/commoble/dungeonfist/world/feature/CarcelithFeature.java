package com.github.commoble.dungeonfist.world.feature;

import java.util.Random;
import java.util.function.Function;

import com.github.commoble.dungeonfist.registry.BlockRegistrar;
import com.mojang.datafixers.Dynamic;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class CarcelithFeature extends Feature<NoFeatureConfig>
{
	public CarcelithFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> configFactoryIn)
	{
		super(configFactoryIn);
	}

	@Override
	public boolean place(IWorld world, ChunkGenerator<? extends GenerationSettings> generator, Random rand,
			BlockPos pos, NoFeatureConfig config)
	{
		// where pos == the center of the object
		// object needs to be at least 5m wide
		int centerX = pos.getX();
		int centerZ = pos.getZ();
		int centerY = pos.getY();
		
		BlockState obsidian = Blocks.OBSIDIAN.getDefaultState();
		BlockState air = Blocks.AIR.getDefaultState();
		BlockState core = BlockRegistrar.DUNGEON_PORTAL.getDefaultState();
		BlockState tip = BlockRegistrar.DUNGEON_PORTAL_GLOWY_AIR.getDefaultState();
		
		ChunkPos chunkPos = new ChunkPos(pos);
		int minX = chunkPos.x << 4;
		int maxX = minX + 15;
		int minZ = chunkPos.z << 4;
		int maxZ = minZ + 15;
		
		int maxXRadius = Math.min(centerX - minX, maxX - centerX);
		int maxZRadius = Math.min(centerZ - minZ, maxZ - centerZ);
		int baseMaxRadius = Math.min(maxXRadius, maxZRadius);
		
		int xStart = centerX - baseMaxRadius;
		int xEnd = centerX + baseMaxRadius;
		int zStart = centerZ - baseMaxRadius;
		int zEnd = centerZ + baseMaxRadius;
		
		int minY = centerY - 5 - rand.nextInt(20);
		int maxY = centerY + 5 + rand.nextInt(20);
		if (minY < 5) minY = 5;
		if (maxY > world.getHeight() - 5) maxY = world.getMaxHeight() - 5;
		
		for (int y = minY; y <= maxY; y++)
		{
			int dY = Math.abs(y - centerY);
			int endpoint_dY = Math.min(Math.abs(y - minY), Math.abs(y - maxY)); // distance from nearest endpoint
			int maxRadius = Math.min(endpoint_dY+3, baseMaxRadius);
			int minRadius = maxRadius > 5 ? 5 : maxRadius-1;
			int radius = rand.nextInt(maxRadius - minRadius) + minRadius;
			
			for (int x = xStart; x <= xEnd; x++)
			{
				int dX = Math.abs(x - centerX);
				for (int z = zStart; z <= zEnd; z++)
				{
					int dZ = Math.abs(z - centerZ);
					if (dX < 2 && dZ < 2 && dY < 3)
					{
						world.setBlockState(new BlockPos(x,y,z), air, 2);
					}
					else if (dX*dX + dZ*dZ < radius*radius)
					{
						world.setBlockState(new BlockPos(x,y,z), obsidian, 2);
					}
				}
			}
			world.setBlockState(pos, core, 2);
			world.setBlockState(pos.down(), tip, 2);
			world.setBlockState(pos.up(), tip, 2);
			
		}
		
		
		return true;
	}
}
