package com.github.commoble.dungeonfist.world;

import com.github.commoble.dungeonfist.registry.BlockRegistrar;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;

public class DungeonTeleporter
{
	public static void teleportPlayer(ServerPlayerEntity player, DimensionType destinationType, BlockPos destinationPos)
	{
		ServerWorld nextWorld = player.getServer().getWorld(destinationType);
		nextWorld.getChunk(destinationPos);	// can't remember why this is here, I think it's to make sure the chunk is loaded
		BlockPos existingPortalPos = getExistingPortal(destinationPos, nextWorld);
		if (existingPortalPos == null)
		{
			existingPortalPos = createPortal(destinationPos, nextWorld);
		}
		player.teleport(nextWorld, destinationPos.getX(), destinationPos.getY(), destinationPos.getZ(), player.rotationYaw, player.rotationPitch);
	}
	
	public static BlockPos getDestination(BlockPos entryPortalPos, DimensionType type)
	{
		return entryPortalPos;
	}
	
	// search in a cube around where the player would come out for existing portals
	// returns portal pos if it finds one, null if it doesn't
	public static BlockPos getExistingPortal(BlockPos destinationPos, ServerWorld world)
	{
		BlockPos existingPortalPos = null;
		MutableBlockPos testPos = new MutableBlockPos(0,0,0);
		for (int x=-5; x<=5; x++)
		{
			for (int y=-5; y<=5; y++)
			{
				for (int z=-5; z<=5; z++)
				{
					testPos.setPos(destinationPos.add(x,y,z));
					if (world.getBlockState(testPos) == BlockRegistrar.DUNGEON_PORTAL.getDefaultState())
					{
						return testPos;
					}
				}
			}
		}
		
		return existingPortalPos;
	}
	
	public static BlockPos createPortal(BlockPos pos, ServerWorld world)
	{
		world.setBlockState(pos, BlockRegistrar.DUNGEON_PORTAL.getDefaultState());
		world.setBlockState(pos.up(), BlockRegistrar.DUNGEON_PORTAL_GLOWY_AIR.getDefaultState());
		world.setBlockState(pos.down(), BlockRegistrar.DUNGEON_PORTAL_GLOWY_AIR.getDefaultState());
		return pos;
	}
}
