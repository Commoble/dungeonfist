package com.github.commoble.dungeonfist.dimension;

import com.github.commoble.dungeonfist.registry.BiomeRegistrar;
import com.github.commoble.dungeonfist.registry.ChunkGeneratorTypeRegistrar;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DungeonDimension extends Dimension
{

	public DungeonDimension(World worldIn, DimensionType typeIn)
	{
		super(worldIn, typeIn);
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator()
	{
		DungeonGenSettings settings = ChunkGeneratorTypeRegistrar.DUNGEON.createSettings();
		settings.setDefaultBlock(Blocks.AIR.getDefaultState());
		settings.setDefaultFluid(Blocks.DIRT.getDefaultState());
		return ChunkGeneratorTypeRegistrar.DUNGEON.create(this.world,
				BiomeProviderType.FIXED.create(BiomeProviderType.FIXED.createSettings().setBiome(BiomeRegistrar.DUNGEON)),
				settings);
	}

	@Override
	public BlockPos findSpawn(ChunkPos chunkPosIn, boolean checkValid)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BlockPos findSpawn(int posX, int posZ, boolean checkValid)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float calculateCelestialAngle(long worldTime, float partialTicks)
	{
		return 1F;
	}

	@Override
	public boolean isSurfaceWorld()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Vec3d getFogColor(float celestialAngle, float partialTicks)
	{
		return new Vec3d((double) 0.01F, (double) 0.01F, (double) 0.01F);
	}

	@Override
	public boolean canRespawnHere()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean doesXZShowFog(int x, int z)
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isSkyColored()
	{
		return true;
	}

}
