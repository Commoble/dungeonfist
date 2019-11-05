package com.github.commoble.dungeonfist;

import com.github.commoble.dungeonfist.registry.BiomeRegistrar;
import com.github.commoble.dungeonfist.registry.BlockRegistrar;
import com.github.commoble.dungeonfist.registry.ChunkGeneratorTypeRegistrar;
import com.github.commoble.dungeonfist.registry.FeatureRegistrar;
import com.github.commoble.dungeonfist.registry.ItemRegistrar;
import com.github.commoble.dungeonfist.registry.ModDimensionRegistrar;
import com.github.commoble.dungeonfist.registry.TileEntityTypeRegistrar;
import com.github.commoble.dungeonfist.util.DungeonMaterialImporter;
import com.github.commoble.dungeonfist.world.placement.CarcelithPlacement;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * In this class we keep all of the MOD events that fire during init for both sides,
 * particularly registry events
 */
@EventBusSubscriber(modid = DungeonFist.MODID, bus = Bus.MOD)
public class CommonModEventHandler
{
	@SubscribeEvent
	public static void onBlockRegistryEvent(RegistryEvent.Register<Block> event)
	{
		BlockRegistrar.registerBlocks(getRegistrator(event));
	}

	@SubscribeEvent
	public static void onItemRegistryEvent(RegistryEvent.Register<Item> event)
	{
		ItemRegistrar.registerItems(getRegistrator(event));
	}

	@SubscribeEvent
	public static void onTileEntityTypeRegistryEvent(RegistryEvent.Register<TileEntityType<?>> event)
	{
		TileEntityTypeRegistrar.registerTileEntities(getRegistrator(event));
	}

	@SubscribeEvent
	public static void onBiomeRegistryEvent(RegistryEvent.Register<Biome> event)
	{
		BiomeRegistrar.registerBiomes(getRegistrator(event));
	}

	@SubscribeEvent
	public static void onFeatureRegistryEvent(RegistryEvent.Register<Feature<?>> event)
	{
		FeatureRegistrar.registerFeatures(getRegistrator(event));
	}

	@SubscribeEvent
	public static void onDimensionRegistryEvent(RegistryEvent.Register<ModDimension> event)
	{
		ModDimensionRegistrar.registerModDimensions(getRegistrator(event));
	}

	@SubscribeEvent
	public static void onChunkGeneratorRegistryEvent(RegistryEvent.Register<ChunkGeneratorType<?, ?>> event)
	{
		ChunkGeneratorTypeRegistrar.registerChunkGeneratorTypes(getRegistrator(event));
	}
	
	@SubscribeEvent
	public static void onCommonSetup(FMLCommonSetupEvent event)
	{
		DungeonMaterialImporter.importDungeonMaterialJson(DungeonFist.class);
		
		ConfiguredFeature<?> carcelithFeature = Biome.createDecoratedFeature(
				FeatureRegistrar.CARCELITH,
				IFeatureConfig.NO_FEATURE_CONFIG,
				new CarcelithPlacement(NoPlacementConfig::deserialize),
				IPlacementConfig.NO_PLACEMENT_CONFIG);
		
		ForgeRegistries.BIOMES.forEach(biome -> biome.addFeature(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, carcelithFeature));
	}

	public static <T extends IForgeRegistryEntry<T>> Registrator<T> getRegistrator(RegistryEvent.Register<T> event)
	{
		return new Registrator<T>(event.getRegistry());
	}
}
