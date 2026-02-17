package net.commoble.dungeonfist.block;

import com.mojang.math.OctahedralGroup;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PipeBlock extends RotatedPillarBlock
{
	public static final MapCodec<? extends RotatedPillarBlock> CODEC = simpleCodec(PipeBlock::new);
	public static final EnumProperty<Axis> AXIS = RotatedPillarBlock.AXIS;
	
	public static final VoxelShape SHAPE_Z = Shapes.or(
		Block.box(0,0,0, 16,2,16),
		Block.box(0,14,0, 16,16,16),
		Block.box(0,0,0, 2,16,16),
		Block.box(14,0,0, 16,16,16));
	public static final VoxelShape SHAPE_X = Shapes.rotate(SHAPE_Z, OctahedralGroup.BLOCK_ROT_Y_90);
	public static final VoxelShape SHAPE_Y = Shapes.rotate(SHAPE_Z, OctahedralGroup.BLOCK_ROT_X_90);

	public PipeBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos)
	{
		return Shapes.block();
	}

	@Override
	protected VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos)
	{
		return Shapes.block();
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
	{
		return switch(state.getValue(AXIS))
		{
			case X -> SHAPE_X;
			case Y -> SHAPE_Y;
			case Z -> SHAPE_Z;
		};
	}
}
