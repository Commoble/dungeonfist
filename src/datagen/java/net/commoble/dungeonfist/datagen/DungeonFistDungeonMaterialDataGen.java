package net.commoble.dungeonfist.datagen;

import java.util.Map;

import net.commoble.dungeonfist.DungeonFist;
import net.commoble.dungeonfist.DungeonMaterial;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public final class DungeonFistDungeonMaterialDataGen
{
	private DungeonFistDungeonMaterialDataGen() {}
	
	static void gatherData(BootstrapContext<DungeonMaterial> bootstrap)
	{
		// all blocks use cobblestone as templates, so the cobblestone material noops
		bootstrap.register(key("cobblestone"), new DungeonMaterial(Map.of()));
		
		bootstrap.register(key("stone_bricks"), standardTemplate(
			Blocks.STONE_BRICKS,
			Blocks.STONE_BRICK_STAIRS,
			Blocks.STONE_BRICK_WALL,
			Blocks.STONE_BRICK_SLAB,
			Blocks.INFESTED_STONE_BRICKS,
			pipeBlock(Blocks.STONE_BRICKS),
			pressurePlateBlock(Blocks.STONE_BRICKS)
		));
		bootstrap.register(key("cobbled_deepslate"), standardTemplate(
			Blocks.COBBLED_DEEPSLATE,
			Blocks.COBBLED_DEEPSLATE_STAIRS,
			Blocks.COBBLED_DEEPSLATE_WALL,
			Blocks.COBBLED_DEEPSLATE_SLAB,
			Blocks.INFESTED_DEEPSLATE,
			pipeBlock(Blocks.COBBLED_DEEPSLATE),
			pressurePlateBlock(Blocks.COBBLED_DEEPSLATE)
		));
	}
	
	private static ResourceKey<DungeonMaterial> key(String path)
	{
		return ResourceKey.create(DungeonMaterial.REGISTRY_KEY, DungeonFist.id(path));
	}
	
	private static DungeonMaterial standardTemplate(
		Block commonBlock,
		Block stairsBlock,
		Block wallBlock,
		Block slabBlock,
		Block infestedBlock,
		Block pipeBlock,
		Block pressurePlateBlock
	)
	{
		return new DungeonMaterial(Map.of(
			Blocks.COBBLESTONE, commonBlock,
			Blocks.COBBLESTONE_STAIRS, stairsBlock,
			Blocks.COBBLESTONE_WALL, wallBlock,
			Blocks.COBBLESTONE_SLAB, slabBlock,
			Blocks.INFESTED_COBBLESTONE, infestedBlock,
			pipeBlock(Blocks.COBBLESTONE), pipeBlock,
			pressurePlateBlock(Blocks.COBBLESTONE), pressurePlateBlock
		));
	}
	
	private static Block pipeBlock(Block originalBlock)
	{
		return DungeonFist.PIPE_BLOCKS.get(BuiltInRegistries.BLOCK.getResourceKey(originalBlock).get()).value();
	}
	
	private static Block pressurePlateBlock(Block originalBlock)
	{
		return DungeonFist.PRESSURE_PLATE_BLOCKS.get(BuiltInRegistries.BLOCK.getResourceKey(originalBlock).get()).value();
	}
}
