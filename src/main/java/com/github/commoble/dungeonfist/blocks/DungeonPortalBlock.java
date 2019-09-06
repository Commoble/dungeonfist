package com.github.commoble.dungeonfist.blocks;

import com.github.commoble.dungeonfist.registry.BlockRegistrar;
import com.github.commoble.dungeonfist.registry.DimensionTypeRegistrar;
import com.github.commoble.dungeonfist.registry.TileEntityTypeRegistrar;
import com.github.commoble.dungeonfist.util.AreaGrid;
import com.github.commoble.dungeonfist.world.DungeonTeleporter;

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
import net.minecraft.world.dimension.DimensionType;

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
			if (player.getRidingEntity() == null && !player.isBeingRidden())
			{
				if (world.dimension.getType().getId() == 0)
				{
					DungeonTeleporter.teleportPlayer(mpPlayer, DimensionTypeRegistrar.getDungeonDimensionType(), pos);
				} else
				{
					DungeonTeleporter.teleportPlayer(mpPlayer, DimensionType.OVERWORLD, pos);
				}
				return true;
			}
		}
		return false;
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
		return VoxelShapes.fullCube();
	}

	@Override
	public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos)
	{
		return VoxelShapes.empty();
	}

	@Override
	public VoxelShape getRaytraceShape(BlockState state, IBlockReader worldIn, BlockPos pos)
	{
		return VoxelShapes.fullCube();
	}
}