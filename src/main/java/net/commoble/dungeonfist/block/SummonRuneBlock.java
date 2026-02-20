package net.commoble.dungeonfist.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.TeleportRandomlyConsumeEffect;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SummonRuneBlock extends ChargeableRuneBlock
{
	
	public SummonRuneBlock(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public boolean trigger(BlockState state, ServerLevel level, BlockPos pos)
	{        
		AABB targetBox = new AABB(pos).inflate(3);
		boolean foundPlayer = false;
		for (Player player : level.getEntitiesOfClass(Player.class, targetBox))
		{
			player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100));
			foundPlayer = true;
		}
		
		// it would be neat if mobs could trigger the trap to summon more mobs
		// but in practice they just slowly fill up a room
		// so, require a player to trigger it
		if (!foundPlayer)
			return false;

		Vec3 center = pos.getCenter(); 
		double radius = 3D;
		double x = center.x;
		double y = center.y;
		double z = center.z;
		int particles = 1000;

		level.playSound(null, new BlockPos((int)center.x, (int)center.y, (int)center.z), SoundEvents.EVOKER_CAST_SPELL, SoundSource.BLOCKS, 1, 1f);
		level.sendParticles(ParticleTypes.EXPLOSION, false, false, x, y, z, particles, radius, radius, radius, 0);
		RandomSource random = level.getRandom();
		level.getBiome(pos).value().getMobSettings().getMobs(MobCategory.MONSTER).getRandom(random).ifPresent(spawnerData -> {
			int min = spawnerData.minCount();
			int max = spawnerData.maxCount();
			// roll twice and take higher result
			int tries = Math.max(
				random.nextIntBetweenInclusive(min, max),
				random.nextIntBetweenInclusive(min, max));
			EntityType<?> type = spawnerData.type();
			BlockPos epicenter = pos.above(2);
			for (int i=0; i<tries; i++)
			{
				Entity entity = type.spawn(level, epicenter, EntitySpawnReason.MOB_SUMMONED);
				if (entity instanceof LivingEntity livingEntity
					&& new TeleportRandomlyConsumeEffect(5).apply(level, new ItemStack(this), livingEntity))
				{
					// we good
				}
				else if (entity != null)
				{
					entity.discard();
				}
			}
		});
		
		return true;
	}
	
	
}
