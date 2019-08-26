package com.github.commoble.dungeonfist;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RegistryHelper<T extends IForgeRegistryEntry<T>>
{
	public IForgeRegistry<T> registry;
	
	public RegistryHelper(IForgeRegistry<T> registry)
	{
		this.registry = registry;
	}
	
	public T register(String registryKey, T entry)
	{
		entry.setRegistryName(new ResourceLocation(DungeonFist.MODID, registryKey));
		registry.register(entry);
		return entry;
	}
}
