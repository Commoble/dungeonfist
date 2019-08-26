package com.github.commoble.dungeonfist;

import com.github.commoble.dungeonfist.registry.BlockRegistrar;
import com.github.commoble.dungeonfist.registry.FeatureRegistrar;
import com.github.commoble.dungeonfist.registry.ItemRegistrar;
import com.github.commoble.dungeonfist.registry.TileEntityTypeRegistrar;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.IForgeRegistryEntry;

@EventBusSubscriber(modid = DungeonFist.MODID, bus = Bus.MOD)
public class CommonModEventHandler
{
	@SubscribeEvent
	public static void onBlockRegistryEvent(RegistryEvent.Register<Block> event)
	{
		BlockRegistrar.registerBlocks(getRegistryHelper(event));
	}
	
	@SubscribeEvent
	public static void onItemRegistryEvent(RegistryEvent.Register<Item> event)
	{
		ItemRegistrar.registerItems(getRegistryHelper(event));
	}

	@SubscribeEvent
	public static void onTileEntityTypeRegistryEvent(RegistryEvent.Register<TileEntityType<?>> event)
	{
		TileEntityTypeRegistrar.registerTileEntities(getRegistryHelper(event));
	}

	@SubscribeEvent
	public static void onFeatureRegistryEvent(RegistryEvent.Register<Feature<?>> event)
	{
		FeatureRegistrar.registerFeatures(getRegistryHelper(event));
	}

	public static <T extends IForgeRegistryEntry<T>> RegistryHelper<T> getRegistryHelper(RegistryEvent.Register<T> event)
	{
		return new RegistryHelper<T>(event.getRegistry());
	}
}
