package net.commoble.dungeonfist.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import net.commoble.dungeonfist.MixinCallbacks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

@Mixin(LivingEntity.class)
public class LivingEntityMixin
{
	@ModifyArgs(method="getFluidFallingAdjustedMovement", at = @At(value = "INVOKE", target="net/minecraft/world/phys/Vec3.<init>(DDD)V"))
	private void modifyGetFluidFallingAdjustedMovementYd(Args args, double baseGravity, boolean isFalling, Vec3 movement)
	{
		MixinCallbacks.modifyLivingEntityGetFluidFallingAdjustedMovementYd((LivingEntity)(Object)this, args, baseGravity, isFalling, movement);
	}
}
