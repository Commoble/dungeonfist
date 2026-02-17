package net.commoble.dungeonfist.block;

import org.jspecify.annotations.Nullable;

import net.commoble.dungeonfist.DungeonFist;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.TeleportRandomlyConsumeEffect;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.AABB;

public class TeleportRuneBlock extends Block
{
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	
	public TeleportRuneBlock(Properties properties)
	{
		super(properties);
		this.registerDefaultState(this.defaultBlockState().setValue(POWERED, false));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		super.createBlockStateDefinition(builder);
		builder.add(POWERED);
	}
	
	public void trigger(BlockState state, ServerLevel level, BlockPos pos)
	{
		AABB targetBox = new AABB(pos).inflate(3);
		for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, targetBox))
		{
			new TeleportRandomlyConsumeEffect(64f).apply(level, new ItemStack(this), entity);
		}
        level.playSound(null, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.BLOCKS);
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean movedByPiston)
	{
		if (level instanceof ServerLevel serverLevel)
		{
			boolean hasPower = level.hasNeighborSignal(pos);
			if (state.getValue(POWERED) != hasPower)
			{
				if (hasPower)
				{
					this.trigger(state, serverLevel, pos);
				}
				level.setBlock(pos, state.setValue(POWERED, hasPower), Block.UPDATE_ALL);
			}
		}
	}

	@Override
	protected void attack(BlockState state, Level level, BlockPos pos, Player player)
	{
		super.attack(state, level, pos, player);
		if (level instanceof ServerLevel serverLevel
			&& !EnchantmentHelper.hasTag(player.getActiveItem(), DungeonFist.PREVENTS_RUNE_TRIGGERING_WHEN_MINING))
		{
			this.trigger(state, serverLevel, pos);
		}
	}
}
