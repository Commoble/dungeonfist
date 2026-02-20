package net.commoble.dungeonfist.block;

import org.jspecify.annotations.Nullable;

import net.commoble.dungeonfist.DungeonFist;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;

public abstract class ChargeableRuneBlock extends Block
{
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final BooleanProperty CHARGED = BooleanProperty.create("charged");

	public ChargeableRuneBlock(Properties properties)
	{
		super(properties);
		this.registerDefaultState(this.defaultBlockState()
			.setValue(POWERED, false)
			.setValue(CHARGED, true));
	}
	
	public abstract boolean trigger(BlockState state, ServerLevel serverLevel, BlockPos pos);

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		super.createBlockStateDefinition(builder);
		builder.add(POWERED, CHARGED);
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
	{
		super.randomTick(state, level, pos, random);
		if (!state.getValue(CHARGED))
		{
			level.setBlock(pos, state.setValue(CHARGED, true), Block.UPDATE_ALL);
		}
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean movedByPiston)
	{
		if (level instanceof ServerLevel serverLevel)
		{
			boolean hasPower = level.hasNeighborSignal(pos);
			if (state.getValue(POWERED) != hasPower)
			{
				boolean armed = state.getValue(CHARGED);
				BlockState newState = state.setValue(POWERED, hasPower);
				if (hasPower && armed)
				{
					boolean nowArmed = !this.trigger(state, serverLevel, pos); // if trigger successful, disarm
					newState = newState.setValue(CHARGED, nowArmed);
				}
				level.setBlock(pos, newState, Block.UPDATE_ALL);
			}
		}
	}

	@Override
	protected void attack(BlockState state, Level level, BlockPos pos, Player player)
	{
		super.attack(state, level, pos, player);
		if (level instanceof ServerLevel serverLevel
			&& state.getValue(CHARGED)
			&& !EnchantmentHelper.hasTag(player.getActiveItem(), DungeonFist.PREVENTS_RUNE_TRIGGERING_WHEN_MINING)
			&& this.trigger(state, serverLevel, pos))
		{
			level.setBlock(pos, state.setValue(CHARGED, false), Block.UPDATE_ALL);
		}
	}
	
}
