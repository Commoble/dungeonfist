package net.commoble.dungeonfist;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public final class BlockMasher
{
	private BlockMasher() {} // utility class
	
	public static BlockState copyProperties(BlockState from, BlockState to)
	{
		BlockState newState = to;
		var oldStates = from.getBlock().getStateDefinition();
		for (Property<?> property : oldStates.getProperties())
		{
			if (newState.hasProperty(property))
			{
				newState = copyValue(from, newState, property);
			}
		}
		return newState;
	}
	
	public static <T extends Comparable<T>> BlockState copyValue(BlockState from, BlockState to, Property<T> property)
	{
		return to.trySetValue(property, from.getValue(property));
	}
}
