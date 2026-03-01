package net.commoble.dungeonfist;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

public final class DungeonFistEnchantments
{
	private DungeonFistEnchantments() {}
	
	private static ResourceKey<Enchantment> key(String path)
	{
		return ResourceKey.create(Registries.ENCHANTMENT, DungeonFist.id(path));
	}
	
	public static final ResourceKey<Enchantment> NIGHT_VISION = key("night_vision");
}
