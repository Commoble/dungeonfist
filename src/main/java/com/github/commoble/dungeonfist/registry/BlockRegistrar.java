package com.github.commoble.dungeonfist.registry;

import com.github.commoble.dungeonfist.DungeonFist;
import com.github.commoble.dungeonfist.Registrator;
import com.github.commoble.dungeonfist.blocks.DungeonPortalBlock;
import com.github.commoble.dungeonfist.blocks.DungeonPortalGlowyAirBlock;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.registries.ObjectHolder;

/**
 * Class for registering blocks and holding their references
 */
@ObjectHolder(DungeonFist.MODID)
public class BlockRegistrar
{	
	@ObjectHolder(DungeonFist.DUNGEON_PORTAL)
	public static final Block DUNGEON_PORTAL = null;
	@ObjectHolder(DungeonFist.DUNGEON_PORTAL_GLOWY_AIR)
	public static final Block DUNGEON_PORTAL_GLOWY_AIR = null;
	
	
	// register all the blocks, called by RegistryEventHandler
	public static void registerBlocks(Registrator<Block> reg)
	{
		reg.register(DungeonFist.DUNGEON_PORTAL, new DungeonPortalBlock(Block.Properties.create(Material.PORTAL).doesNotBlockMovement().hardnessAndResistance(-1.0F).sound(SoundType.GLASS).lightValue(11).noDrops()));
		reg.register(DungeonFist.DUNGEON_PORTAL_GLOWY_AIR, new DungeonPortalGlowyAirBlock(Block.Properties.create(Material.PORTAL).doesNotBlockMovement().hardnessAndResistance(-1.0F).sound(SoundType.GLASS).lightValue(11).noDrops()));
	}
}
