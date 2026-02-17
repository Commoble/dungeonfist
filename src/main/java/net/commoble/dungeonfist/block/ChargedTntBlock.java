package net.commoble.dungeonfist.block;

import org.jspecify.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gamerules.GameRules;

/// TntBlock but it explodes immediately
public class ChargedTntBlock extends TntBlock
{
	public static final MapCodec<TntBlock> CODEC = simpleCodec(TntBlock::new);

	public ChargedTntBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	public MapCodec<TntBlock> codec()
	{
		return CODEC;
	}

	@Override
	public boolean onCaughtFire(BlockState state, Level level, BlockPos pos, @Nullable Direction face, @Nullable LivingEntity igniter)
	{
		if (level instanceof ServerLevel serverLevel && serverLevel.getGameRules().get(GameRules.TNT_EXPLODES))
		{
			PrimedTnt tnt = new PrimedTnt(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, igniter);
			tnt.setFuse(1);
			level.addFreshEntity(tnt);
			level.playSound(null, tnt.getX(), tnt.getY(), tnt.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
			level.gameEvent(igniter, GameEvent.PRIME_FUSE, pos);
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public void wasExploded(ServerLevel level, BlockPos pos, Explosion explosion)
	{
		if (level.getGameRules().get(GameRules.TNT_EXPLODES))
		{
			PrimedTnt primed = new PrimedTnt(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, explosion.getIndirectSourceEntity());
			primed.setFuse(1);
			level.addFreshEntity(primed);
		}
	}

}
