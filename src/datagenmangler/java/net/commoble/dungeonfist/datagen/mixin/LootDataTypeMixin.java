package net.commoble.dungeonfist.datagen.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.level.storage.loot.LootDataType;

@Mixin(LootDataType.class)
public class LootDataTypeMixin 
{
	/*
	 * There's a thing mojang does with the loot registries (loot tables, predicates, modifiers, context number providers)
	 * where, to validate them, after things get serialized to json elements, they get parsed back to proper java objects.
	 * This breaks if we have any loot tables that reference non-datagen files, which we do
	 * so we'll just throw a mixin at that to turn it off
	 * 
	 * Also we have to make this whole other sourceset to get a mixin that only loads in datagen
	 */
	@Inject(method="runValidationIfPresent", at=@At("HEAD"), cancellable=true)
	public void onRunValidationIfPresentHead(CallbackInfo info)
	{
		info.cancel();
	}
}
