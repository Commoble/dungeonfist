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
	
	public static final ResourceKey<Enchantment> DEATH_FEAST = key("death_feast");
	public static final ResourceKey<Enchantment> FIRE_BLAST = key("fire_blast");
	public static final ResourceKey<Enchantment> FIRE_THORNS = key("fire_thorns");
	public static final ResourceKey<Enchantment> LIGHTBRINGER = key("lightbringer");
	public static final ResourceKey<Enchantment> NIGHT_VISION = key("night_vision");
	public static final ResourceKey<Enchantment> STORM_CALLER = key("storm_caller");
}
