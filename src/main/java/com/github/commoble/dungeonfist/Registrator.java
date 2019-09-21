package com.github.commoble.dungeonfist;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * Helper class that we use to register things
 */
public class Registrator<T extends IForgeRegistryEntry<T>>
{
	public IForgeRegistry<T> registry;
	
	public Registrator(IForgeRegistry<T> registry)
	{
		this.registry = registry;
	}
	
	public T register(String registryKey, T entry)
	{
		entry.setRegistryName(new ResourceLocation(DungeonFist.MODID, registryKey));
		this.registry.register(entry);
		return entry;
	}
}
