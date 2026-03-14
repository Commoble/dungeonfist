package net.commoble.dungeonfist;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.phys.Vec3;

public final class MixinCallbacks
{
	private MixinCallbacks() {}
	
	public static void modifyLivingEntityGetFluidFallingAdjustedMovementYd(LivingEntity livingEntity, Args args, double baseGravity, boolean isFalling, Vec3 movement)
	{
		double yd = args.get(1);
		if (!isFalling || baseGravity == 0D || livingEntity.isSprinting() || livingEntity.isJumping())
			return;
		
		@Nullable AttributeInstance attribute = livingEntity.getAttribute(DungeonFist.SINKING_RESISTANCE);
		if (attribute == null)
			return;
		
		double sinkingResistance = attribute.getValue();
		if (sinkingResistance >= 1D)
			return;
		
		double ydWithTrueGravity = movement.y - baseGravity;
		double lerpedYd = Mth.lerp(sinkingResistance, ydWithTrueGravity, yd);
		args.set(1, lerpedYd);
	}
}
