package com.github.commoble.dungeonfist.world;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import com.github.commoble.dungeonfist.dimension.DungeonGenSettings;
import com.github.commoble.dungeonfist.util.Averager;
import com.github.commoble.dungeonfist.util.Rect;
import com.github.commoble.dungeonfist.util.Room;
import com.github.commoble.dungeonfist.util.RoomCaches;
import com.github.commoble.dungeonfist.util.RoomKey;
import com.github.commoble.dungeonfist.util.Vec2i;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.INoiseGenerator;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraft.world.gen.OctavesNoiseGenerator;
import net.minecraftforge.registries.ForgeRegistries;

public class DungeonChunkGenerator extends NoiseChunkGenerator<DungeonGenSettings>
{
	private final double[] field_222573_h = this.func_222572_j(); // copied from nether
	private static final int horizontalNoiseGranularity = 4;
	private static final int verticalNoiseGranularity = 8;
	private static final int height = 256;
	private static final int noiseSizeX = 16 / horizontalNoiseGranularity;
	private static final int noiseSizeY = height / verticalNoiseGranularity;
	private static final int noiseSizeZ = 16 / horizontalNoiseGranularity;
	private static final BlockState AIR = Blocks.AIR.getDefaultState();
	private final INoiseGenerator surfaceDepthNoise;
	private final OctavesNoiseGenerator roomNoise;
	private final Averager avgGenTime = new Averager();
	public static final Block[] BLOCKS =
			{
					Blocks.DIRT,
					Blocks.STONE,
					Blocks.STONE_BRICKS,
					Blocks.BLACK_WOOL,
					Blocks.ACACIA_LOG,
					Blocks.OAK_PLANKS,
					Blocks.DARK_OAK_PLANKS,
					Blocks.RED_WOOL,
					Blocks.ORANGE_WOOL,
					Blocks.GREEN_WOOL,
					Blocks.BLUE_WOOL,
					Blocks.LIGHT_BLUE_WOOL,
					Blocks.GLASS,
					Blocks.GREEN_STAINED_GLASS,
					Blocks.CLAY,
					Blocks.SANDSTONE,
					Blocks.TERRACOTTA,
					Blocks.PUMPKIN,
					Blocks.COAL_BLOCK,
					Blocks.DIAMOND_BLOCK,
					Blocks.GOLD_BLOCK,
					Blocks.SPONGE,
					Blocks.NETHERRACK,
					Blocks.SPRUCE_LOG
			};

	public DungeonChunkGenerator(World world, BiomeProvider biomeProvider, DungeonGenSettings genSettings)
	{
		super(world, biomeProvider, horizontalNoiseGranularity, verticalNoiseGranularity, height, genSettings, false);
		this.surfaceDepthNoise = new OctavesNoiseGenerator(this.randomSeed, 4);
		this.roomNoise = new OctavesNoiseGenerator(this.randomSeed, 4);	// produces values in range of about -15 to 15, usually < half that
		Collection<Block> blocks = ForgeRegistries.BLOCKS.getValues();
	}
	
	public static double getXYZNoise(OctavesNoiseGenerator gen, double x, double y, double z)
	{
		return gen.func_215462_a(x, y, z, 0D, 0D, false);
	}

	@Override
	protected double[] func_222549_a(int p_222549_1_, int p_222549_2_)
	{
		return new double[] { 0.0D, 0.0D }; // copied from nether, something to do with biomes?
	}

	@Override
	protected double func_222545_a(double p_222545_1_, double p_222545_3_, int p_222545_5_)
	{
		return this.field_222573_h[p_222545_5_]; // copied from nether
	}

	@Override // copied from nether
	protected void func_222548_a(double[] p_222548_1_, int p_222548_2_, int p_222548_3_)
	{
		// double d0 = 684.412D;
		// double d1 = 2053.236D;
		// double d2 = 8.555150000000001D;
		// double d3 = 34.2206D;
		// int i = -10;
		// int j = 3;
		this.func_222546_a(p_222548_1_, p_222548_2_, p_222548_3_, 684.412D, 2053.236D, 8.555150000000001D, 34.2206D, 3,
				-10);
	}

	@Override
	public int getGroundHeight()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	// copied from nether -- get array used for generating noise?
	private double[] func_222572_j()
	{
		double[] adouble = new double[this.noiseSizeY()];

		for (int i = 0; i < this.noiseSizeY(); ++i)
		{
			adouble[i] = Math.cos((double) i * Math.PI * 6.0D / (double) this.noiseSizeY()) * 2.0D;
			double d0 = (double) i;
			if (i > this.noiseSizeY() / 2)
			{
				d0 = (double) (this.noiseSizeY() - 1 - i);
			}

			if (d0 < 4.0D)
			{
				d0 = 4.0D - d0;
				adouble[i] -= d0 * d0 * d0 * 10.0D;
			}
		}

		return adouble;
	}

	@Override // used for generating noise grid? seems to be strictly used for structures, doesn't run when no structures exist
	public int func_222529_a(int p_222529_1_, int p_222529_2_, Heightmap.Type p_222529_3_)
	{
		/*int i = Math.floorDiv(p_222529_1_, horizontalNoiseGranularity);
		int j = Math.floorDiv(p_222529_2_, horizontalNoiseGranularity);
		int k = Math.floorMod(p_222529_1_, horizontalNoiseGranularity);
		int l = Math.floorMod(p_222529_2_, horizontalNoiseGranularity);
		double d0 = (double) k / (double) horizontalNoiseGranularity;
		double d1 = (double) l / (double) horizontalNoiseGranularity;
		double[][] perlinGrid = new double[][] { this.func_222547_b(i, j), this.func_222547_b(i, j + 1),
				this.func_222547_b(i + 1, j), this.func_222547_b(i + 1, j + 1) };
		int i1 = this.getSeaLevel();

		for (int j1 = noiseSizeY - 1; j1 >= 0; --j1)
		{
			double d2 = perlinGrid[0][j1];
			double d3 = perlinGrid[1][j1];
			double d4 = perlinGrid[2][j1];
			double d5 = perlinGrid[3][j1];
			double d6 = perlinGrid[0][j1 + 1];
			double d7 = perlinGrid[1][j1 + 1];
			double d8 = perlinGrid[2][j1 + 1];
			double d9 = perlinGrid[3][j1 + 1];

			for (int k1 = verticalNoiseGranularity - 1; k1 >= 0; --k1)
			{
				double d10 = (double) k1 / (double) verticalNoiseGranularity;
				double d11 = MathHelper.lerp3(d10, d0, d1, d2, d6, d4, d8, d3, d7, d5, d9);
				int l1 = j1 * verticalNoiseGranularity + k1;
				if (d11 > 0.0D || l1 < i1)
				{
					BlockState blockstate;
					if (d11 > 0.0D)
					{
						blockstate = this.defaultBlock;
					} else
					{
						blockstate = Blocks.STONE_BRICKS.getDefaultState();
					}

					if (p_222529_3_.func_222684_d().test(blockstate))
					{
						return l1 + 1;
					}
				}
			}
		}*/

		return 0;
	}
	
//	public ChunkPos getDominantChunkPos(ChunkPos chunkPos, int y, Object2DoubleMap<ChunkPos> visited, ChunkPos mostDominantChunk, double mostDominance)
//	{
//		int globalXStart = chunkPos.getXStart();
//		int globalZStart = chunkPos.getZStart();
//		double dominanceHere = getXYZNoise(this.roomNoise, globalXStart, y, globalZStart);
//		visited.put(chunkPos, dominanceHere);
//		if (dominanceHere > mostDominance)
//		{
//			ChunkPos mostDominantNeighbor = chunkPos;
//			double mostNeighborDominance = dominanceHere;
//			
//
//			for (int i=0; i<4; i++)
//			{
//				int xOff = (i-1) % 2;	// -1,0,1,0
//				int zOff = (2-i) % 2;	// 	0,1,0,-1
//				ChunkPos nextPos = new ChunkPos(chunkPos.x + xOff, chunkPos.z + zOff);
//				if (!visited.containsKey(nextPos))
//				{
//					double nextDominance = 
//					visited.put(nextPos, nextDominance);
//				}
//			}
//		}
//		else
//		{
//			return mostDominantChunk;
//		}
//	}
	
//	public ChunkPos getDominantChunkPos(ChunkPos pos, int y)
//	{
//		int globalXStart = pos.getXStart();
//		int globalZStart = pos.getZStart();
//		double dominanceHere = getXYZNoise(this.roomNoise, globalXStart, y, globalZStart);
//		for (int i=0; i<4; i++)
//		{
//			int xOff = (i-1) % 2;	// -1,0,1,0
//			int zOff = (2-i) % 2;	// 	0,1,0,-1
//			ChunkPos nextPos = new ChunkPos(pos.x + xOff, pos.z + zOff);
//			double nextDominance = getXYZNoise(this.roomNoise, nextPos.getXStart(), y, nextPos.getZStart());
//			if ()
//		}
//	}
	
//	public Room getRoom(ChunkPos pos, int y)
//	{
//		ChunkPos dominantPos = this.getDominantChunkPos(pos, y);
//	}
	
	@Override
	public void makeBase(IWorld worldIn, IChunk chunk)
	{
		long time1 = Util.nanoTime();
		ChunkPos chunkpos = chunk.getPos();
		int baseFloor = 10;
		int globalXStart = chunkpos.getXStart();
		int globalZStart = chunkpos.getZStart();
		MutableBlockPos mutapos = new MutableBlockPos(0,0,0);
		//BlockState state = Blocks.STONE_BRICKS.getDefaultState();
		for (int yLayer=0; yLayer<5; yLayer++)
		{
//			ChunkPos dominantChunkPos = this.getRoom(chunkpos,y);
			int y = yLayer*50 + baseFloor;
//			Random superRand = new Random(dominantSuperChunkCoords.hashCode()*y);
			BlockState state = Blocks.STONE_BRICKS.getDefaultState(); //BLOCKS[superRand.nextInt(BLOCKS.length)].getDefaultState();
			RoomKey roomKey = new RoomKey(chunk, y, worldIn.getSeed());
			Room room = RoomCaches.ROOMLOADER.getUnchecked(roomKey);
			
			double noiseXYZ = getXYZNoise(this.roomNoise, globalXStart,y,globalZStart);
			Rect floorInThisChunk = room.getFloorRectWithinChunk(chunkpos);

			List<Vec2i> coords = floorInThisChunk.coords();
			for (Vec2i coord : coords)
			{
				mutapos.setPos(coord.X, y, coord.Y);
				chunk.setBlockState(mutapos, state, false);
			}
			//if (noiseXYZ > 0)
//			{
//				for (int localX = 0; localX < 16; localX++)
//				{
//					for (int localZ=0; localZ < 16; localZ++)
//					{
//						mutapos.setPos(localX, y, localZ);
//						chunk.setBlockState(mutapos, state, false);
//					}
//				}
//			}
			Stream<Vec2i> wallCoords = room.getWallPositionsWithinChunk(chunkpos);
			wallCoords.forEach(coord ->{
				
				for (int yOff=0; yOff<5; yOff++)
				{
					mutapos.setPos(coord.X, yOff+y+1, coord.Y);
					chunk.setBlockState(mutapos, state, false);
				}
			});
			
			BlockState exitState = Blocks.BEDROCK.getDefaultState();
			room.getExitRectsWithinChunk(chunkpos).forEach(exitRect ->
			{
				exitRect.coords().forEach(vec -> {
					mutapos.setPos(vec.X, y, vec.Y);
					chunk.setBlockState(mutapos, exitState, false);
				});
			});
			room.getExitHallwayRectsWithinChunk(chunkpos).forEach(exitRect ->
			{
				exitRect.coords().forEach(vec -> {
					mutapos.setPos(vec.X, y, vec.Y);
					chunk.setBlockState(mutapos,  exitState, false);
				});
			});
			
			
//			RegionSideExits exits = RoomCaches.EXITLOADER.getUnchecked(roomKey);
//			IntConsumer mutaposSetter;
//			BlockState exitState = Blocks.BEDROCK.getDefaultState();
//			if (exits.isOnEastSide)
//			{
//				mutaposSetter = z -> mutapos.setPos(15,y,z);
//			}
//			else
//			{
//				mutaposSetter = x -> mutapos.setPos(x,y,15);
//			}
//			exits.getPortionsOfExitInChunk(chunkpos).forEach(i -> {
//				mutaposSetter.accept(i);
//				chunk.setBlockState(mutapos, exitState, false);
//			});
			
		}
		long time2 = Util.nanoTime();
		System.out.println(avgGenTime.add(time2 - time1));
	}

	@Override
	public void generateSurface(IChunk chunkIn)
	{
		ChunkPos chunkpos = chunkIn.getPos();
		int chunkX = chunkpos.x;
		int chunkZ = chunkpos.z;
		SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
		sharedseedrandom.setBaseChunkSeed(chunkX, chunkZ);
//		int globalXStart = chunkpos.getXStart();
//		int globalZStart = chunkpos.getZStart();
//		double one16th = 0.0625D;
//		Biome[] abiome = chunkIn.getBiomes();
//
//		for (int localX = 0; localX < 16; ++localX)
//		{
//			for (int localZ = 0; localZ < 16; ++localZ)
//			{
//				int globalX = globalXStart + localX;
//				int globalZ = globalZStart + localZ;
//				int i2 = chunkIn.getTopBlockY(Heightmap.Type.WORLD_SURFACE_WG, localX, localZ) + 1;
//				double xzNoise = this.surfaceDepthNoise.func_215460_a(
//						(double) globalX * one16th,
//						(double) globalZ * one16th,
//						one16th,
//						(double) localX * one16th);
//				// we use NoopSurfaceBuilder so this should be ignored
//				abiome[localZ * 16 + localX].buildSurface(sharedseedrandom, chunkIn, globalX, globalZ, i2, xzNoise,
//						this.getSettings().getDefaultBlock(), this.getSettings().getDefaultFluid(), this.getSeaLevel(),
//						this.world.getSeed());
//			}
//		}

		this.makeBedrock(chunkIn, sharedseedrandom);
	}

}
