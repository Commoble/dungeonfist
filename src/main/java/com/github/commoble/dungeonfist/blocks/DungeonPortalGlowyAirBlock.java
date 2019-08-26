package com.github.commoble.dungeonfist.blocks;

import com.github.commoble.dungeonfist.registry.BlockRegistrar;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

/**
 * The two blocks y-adjacent to the primary portal block
 */
public class DungeonPortalGlowyAirBlock extends Block
{
    public DungeonPortalGlowyAirBlock(Block.Properties props)
    {
        super(props);
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving)
    {
    	// check if portal base has been removed
		if (world.getBlockState(pos.up()).getBlock() != BlockRegistrar.DUNGEON_PORTAL
				&& world.getBlockState(pos.down()).getBlock() != BlockRegistrar.DUNGEON_PORTAL)
		{
			world.removeBlock(pos, false);
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
        }
        else
        {
//        	// redirect to adjacent portal base if it exists, otherwise do nothing
//    		if (world.getBlockState(pos.up()).getBlock() == BlockRegistrar.DUNGEON_PORTAL)
//    		{
//    			return BlockRegistrar.DUNGEON_PORTAL.onBlockActivated(world, pos.up(), state, playerIn, hand, facing, hitX, hitY, hitZ);
//    		}
//    		else if (world.getBlockState(pos.down()).getBlock() == BlockRegistrar.dungeonPortalBase)
//    		{
//    			return BlockRegistrar.DUNGEON_PORTAL.onBlockActivated(world, pos.down(), state, playerIn, hand, facing, hitX, hitY, hitZ);
//    		}
//    		else
//    		{
//    			return false;
//    		}
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
