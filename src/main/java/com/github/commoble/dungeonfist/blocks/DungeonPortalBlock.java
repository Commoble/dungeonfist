package com.github.commoble.dungeonfist.blocks;

import com.github.commoble.dungeonfist.registry.BlockRegistrar;
import com.github.commoble.dungeonfist.registry.TileEntityTypeRegistrar;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

/**
 * The primary portal blocks in the centers of portals
 */
public class DungeonPortalBlock extends Block
{
	public DungeonPortalBlock(Block.Properties props)
	{
		super(props);
	}

	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return TileEntityTypeRegistrar.DUNGEON_PORTAL.create();
	}

	/**
	 * Whether this Block can be replaced directly by other blocks (true for e.g.
	 * tall grass)
	 */
	@Override
	public boolean isReplaceable(BlockState state, BlockItemUseContext useContext)
	{
		return false;
	}

	/**
	 * Checks if this block can be placed exactly at the given position.
	 */
	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
	{
		// can place here if this and the two y-adjacent blocks are replaceable
		// first, make sure it's not too close to the y-boundaries of the world
		if (pos.getY() <= 6 || pos.getY() >= worldIn.getHeight() - 7)
		{
			return false;
		}
		for (int i = -1; i <= 1; i++)
		{
			if (!worldIn.getBlockState(pos.up(i)).isValidPosition(worldIn, pos))
			{
				return false;
			}
		}
		return super.isValidPosition(state, worldIn, pos);
	}

	/**
	 * Called after the block is set in the Chunk data, but before the Tile Entity
	 * is set
	 */
	@Override
	public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving)
	{
		super.onBlockAdded(state, worldIn, pos, oldState, isMoving);
		// set the two y-adjacent blocks to glowy air
		worldIn.setBlockState(pos.up(), BlockRegistrar.DUNGEON_PORTAL_GLOWY_AIR.getDefaultState());
		worldIn.setBlockState(pos.down(), BlockRegistrar.DUNGEON_PORTAL_GLOWY_AIR.getDefaultState());
	}

	/**
	 * Called serverside after this block is replaced with another in Chunk, but
	 * before the Tile Entity is updated when broken, break the two adjacent portal
	 * blocks
	 */
	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving)
	{
		super.onReplaced(state, worldIn, pos, newState, isMoving);
		if (worldIn.getBlockState(pos.up()).getBlock() == BlockRegistrar.DUNGEON_PORTAL_GLOWY_AIR)
		{
			worldIn.removeBlock(pos.up(), false);
		}
		if (worldIn.getBlockState(pos.down()).getBlock() == BlockRegistrar.DUNGEON_PORTAL_GLOWY_AIR)
		{
			worldIn.removeBlock(pos.down(), false);
		}
	}

	/**
	 * Called when the block is right clicked by a player.
	 */
	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn,
			BlockRayTraceResult hit)
	{
		if (world.isRemote || !(player instanceof ServerPlayerEntity))
		{
			return true;
		} else
		{
			ServerPlayerEntity mpPlayer = (ServerPlayerEntity) player;
			// if (!player.isRiding() && !player.isBeingRidden())
			// {
			// // copied section from EntityPlayerMP.changeDimension
			// int targetID; // id of dimension the player is teleporting to
			// if (world.provider.getDimensionType().getId() == 0)
			// {
			// targetID = DungeonfistMod.proxy.test_dim_id;
			// }
			// else
			// {
			// targetID = 0;
			// }
			// ServerWorld serv = mpPlayer.mcServer.getWorld(targetID); // will initialize
			// world if it's not loaded
			// mpPlayer.getServer().getPlayerList().transferPlayerToDimension(mpPlayer,
			// targetID, new TeleporterDungeon(serv, pos));
			//
			//
			// mpPlayer.connection.sendPacket(new SPacketEffect(1032, BlockPos.ORIGIN, 0,
			// false));
			// mpPlayer.addExperience(0); // set lastExperience to -1
			// mpPlayer.setPlayerHealthUpdated(); // set lastHealth to -1
			// //mpPlayer.lastFoodLevel = -1; // no equivalent public function
			// return true;
			// }
			return false;
		}
	}

	/**
	 * The type of render function called. MODEL for mixed tesr and static model,
	 * MODELBLOCK_ANIMATED for TESR-only, LIQUID for vanilla liquids, INVISIBLE to
	 * skip all rendering
	 */
	public BlockRenderType getRenderType(BlockState state)
	{
		return BlockRenderType.INVISIBLE;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
	{
		return VoxelShapes.empty();
	}
}