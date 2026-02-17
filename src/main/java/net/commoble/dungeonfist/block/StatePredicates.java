package net.commoble.dungeonfist.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public final class StatePredicates
{
	private StatePredicates() {} // util class
	
	public static boolean always(BlockState state, BlockGetter blockGetter, BlockPos pos)
	{
		return true;
	}
}
