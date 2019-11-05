package com.github.commoble.dungeonfist.world;

import com.github.commoble.dungeonfist.util.InterRegionData;
import com.github.commoble.dungeonfist.util.MathBuddy;
import com.github.commoble.dungeonfist.util.RoomCaches;
import com.github.commoble.dungeonfist.util.RoomKey;
import com.github.commoble.dungeonfist.util.Vec2i;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;

public class PortalPlacementRules
{
	public static boolean shouldPlaceInOverworld(long seed, ChunkPos pos)
	{
		return MathBuddy.absoluteMod(pos.x + (int) (seed >> 32) * pos.z + (int) (seed & Integer.MAX_VALUE), 2) == 0;
	}

	// get the global blockpos of a portal for a given chunk
	// whether a portal should or shouldn't be placed in the chunk has no outcome on
	// the result, so check that first
	public static BlockPos getPortalPosInOverworld(IWorld world, ChunkPos pos)
	{
		long seed = world.getSeed();
		int xOff = MathBuddy.absoluteMod((pos.x * 12345 + (int) (seed >> 32)) + pos.z, 6);
		int zOff = MathBuddy.absoluteMod((pos.z * 54321 + (int) (seed & Integer.MAX_VALUE)) + pos.x, 6);

		int minX = (pos.x << 4) + 5;
		int minZ = (pos.z << 4) + 5;

		int x = minX + xOff;
		int z = minZ + zOff;
		int y = getOverworldPortalY(world, x, z);

		return new BlockPos(minX + xOff, y, minZ + zOff);
	}

	public static Vec2i getDungeonPortalCoordFromOverworldPos(long seed, BlockPos pos)
	{
		// coordinates of the blockpos scaled into the dungeon
		int dungeonX = pos.getX() << 4;
		int dungeonZ = pos.getZ() << 4;

		int superChunkX = dungeonX >> 5;
		int superChunkZ = dungeonZ >> 5;
		RoomKey key = new RoomKey(superChunkX, superChunkZ, YLayerRules.TOP_LAYER, seed);

		return RoomCaches.EXITLOADER.getUnchecked(key).portalPos.orElse(Vec2i.ZERO);
	}

	public static BlockPos getDungeonPortalPosFromOverworldPos(long seed, BlockPos pos)
	{
		// coordinates of the blockpos scaled into the dungeon
		int dungeonX = pos.getX() << 4;
		int dungeonZ = pos.getZ() << 4;

		int superChunkX = dungeonX >> 5;
		int superChunkZ = dungeonZ >> 5;
		RoomKey key = new RoomKey(superChunkX, superChunkZ, YLayerRules.TOP_LAYER, seed);

		InterRegionData regionData = RoomCaches.EXITLOADER.getUnchecked(key);
		int y = regionData.roomFloorLevel + 2;

		return regionData.portalPos.orElse(Vec2i.ZERO).toBlockPos(y);
	}

	public static BlockPos getOverworldPortalPosFromDungeonPortalPos(IWorld dungeonWorld, BlockPos pos)
	{
		int superChunkX = pos.getX() >> 5;
		int superChunkZ = pos.getZ() >> 5;
		RoomKey key = new RoomKey(superChunkX, superChunkZ, YLayerRules.TOP_LAYER, dungeonWorld.getSeed());
		
		if (!(dungeonWorld instanceof ServerWorld))
		{
			return getOverworldPosFromDungeonPos(pos);
		}
		
		ServerWorld dungeonServer = (ServerWorld)dungeonWorld;
		ServerWorld overworld = dungeonServer.getServer().getWorld(DimensionType.OVERWORLD);

		InterRegionData regionData = RoomCaches.EXITLOADER.getUnchecked(key);
		
		return getPortalPosInOverworld(overworld, regionData.portalExitChunk.orElse(new ChunkPos(getOverworldPosFromDungeonPos(pos))));
	}

	public static int getOverworldPortalY(IWorld overworld, int x, int z)
	{
		long seed = overworld.getSeed();
		int minY = 20;
		int maxY = overworld.getMaxHeight() - 20;
		int dY = maxY - minY;
		int yOff = MathBuddy.absoluteMod(
				x * z + x * (int) (seed >> 32) + z * (int) (seed & Integer.MAX_VALUE) + x + z + (int) seed, dY);
		return 64;
	}

	// scale an overworld blockpos by 16, getting a position in the corner of a
	// chunk in the dungeon
	public static BlockPos getDungeonCornerPosFromOverworldPos(BlockPos overworldPos)
	{
		return new BlockPos(overworldPos.getX() << 4, overworldPos.getY(), overworldPos.getZ() << 4);
	}

	public static BlockPos getOverworldPosFromDungeonPos(BlockPos dungeonPos)
	{
		return new BlockPos(dungeonPos.getX() >> 4, dungeonPos.getY(), dungeonPos.getZ() >> 4);
	}
}
