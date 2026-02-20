package net.commoble.dungeonfist.block;

import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.InstrumentTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class AlertRuneBlock extends ChargeableRuneBlock
{
	
	public AlertRuneBlock(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public boolean trigger(BlockState state, ServerLevel level, BlockPos pos)
	{
		level.registryAccess().lookupOrThrow(Registries.INSTRUMENT).get(InstrumentTags.REGULAR_GOAT_HORNS).ifPresent(goatHorns -> {
			@SuppressWarnings("deprecation")
			RandomSource posRandom = RandomSource.create(Mth.getSeed(pos));
			goatHorns.getRandomElement(posRandom).ifPresent(horn -> {
                level.playSound(null, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, horn.value().soundEvent().value(), SoundSource.BLOCKS);
			});
		});
        
		AABB targetBox = new AABB(pos).inflate(3);
		@Nullable Player targetPlayer = null;
		for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, targetBox))
		{
			if (entity instanceof Player foundPlayer
				&& !foundPlayer.isCreative()
				&& !foundPlayer.isSpectator())
			{
				targetPlayer = foundPlayer;
				break;
			}
			else if (entity instanceof Mob mob)
			{
				if (mob.getTarget() instanceof Player foundPlayer)
				{
					targetPlayer = foundPlayer;
					break;
				}
			}
		}
		if (targetPlayer != null)
		{
			AABB mobAlertBox = new AABB(pos).inflate(64);
			for (Monster monster : level.getEntitiesOfClass(Monster.class, mobAlertBox))
			{
				double followRange = monster.getAttributeValue(Attributes.FOLLOW_RANGE);
				if (monster.distanceToSqr(targetPlayer) < followRange * followRange
					&& monster.canAttack(targetPlayer))
				{
					monster.setTarget(targetPlayer);
					monster.addEffect(new MobEffectInstance(MobEffects.SPEED, 60, 1));
				}
			}
		}
		return true;
	}
	
	
}
