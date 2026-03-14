package net.commoble.dungeonfist.mob_effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;

public class StormCallMobEffect extends MobEffect
{
	public StormCallMobEffect(MobEffectCategory category, int color)
	{
		super(category, color);
	}

	@Override
	public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity mob, int amplification)
	{
		LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(serverLevel, EntitySpawnReason.EVENT);
		if (bolt != null)
		{
			bolt.snapTo(mob.position());
			serverLevel.addFreshEntity(bolt);
		}
		return true;
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int tickCount, int amplification)
	{
		return tickCount == 1;
	}

	
}
