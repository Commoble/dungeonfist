package com.github.commoble.dungeonfist.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

import com.github.commoble.dungeonfist.DungeonFist;
import com.github.commoble.dungeonfist.Registrator;
import com.github.commoble.dungeonfist.blocks.ConstructableStoneButtonBlock;
import com.github.commoble.dungeonfist.blocks.ConstructableStonePressurePlateBlock;
import com.github.commoble.dungeonfist.blocks.DungeonPortalBlock;
import com.github.commoble.dungeonfist.blocks.DungeonPortalGlowyAirBlock;

import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
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
	
	// used for registering items
	protected static final List<String> registry_keys = new ArrayList<String>(144);
	
	
	// register all the blocks, called by RegistryEventHandler
	public static void registerBlocks(Registrator<Block> reg)
	{

		
		HashMap<String, Block.Properties> blockprops = new HashMap<>();
		
		Block.Properties STONE_PROPERTIES = Block.Properties.create(Material.ROCK, MaterialColor.STONE).hardnessAndResistance(1.5F, 6.0F);
		Block.Properties SMOOTH_STONE_PROPERTIES = Block.Properties.create(Material.ROCK, MaterialColor.STONE).hardnessAndResistance(2.0F, 6.0F);
		// used by cobblestone and mossy cobblestone
		Block.Properties COBBLESTONE_PROPERTIES = Block.Properties.create(Material.ROCK).hardnessAndResistance(2.0F, 6.0F);
		// used by stone bricks, mossy stone bricks, cracked stone bricks, chiseled stone bricks
		Block.Properties STONE_BRICK_PROPERTIES = Block.Properties.create(Material.ROCK).hardnessAndResistance(1.5F, 6.0F);
		
		Block.Properties BUTTON_PROPERTIES = Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.5F);
		Block.Properties PRESSURE_PLATE_PROPERTIES = Block.Properties.create(Material.ROCK).doesNotBlockMovement().hardnessAndResistance(0.5F);

		blockprops.put("stone", STONE_PROPERTIES);
		blockprops.put("smooth_stone", SMOOTH_STONE_PROPERTIES);
		blockprops.put("cobblestone", COBBLESTONE_PROPERTIES);
		blockprops.put("mossy_cobblestone", COBBLESTONE_PROPERTIES);
		blockprops.put("stone_bricks", STONE_BRICK_PROPERTIES);
		blockprops.put("mossy_stone_bricks", STONE_BRICK_PROPERTIES);
		blockprops.put("cracked_stone_bricks", STONE_BRICK_PROPERTIES);
		blockprops.put("chiseled_stone_bricks", STONE_BRICK_PROPERTIES);
		
		reg.register(DungeonFist.DUNGEON_PORTAL, new DungeonPortalBlock(Block.Properties.create(Material.PORTAL).doesNotBlockMovement().hardnessAndResistance(-1.0F).sound(SoundType.GLASS).lightValue(11).noDrops()));
		reg.register(DungeonFist.DUNGEON_PORTAL_GLOWY_AIR, new DungeonPortalGlowyAirBlock(Block.Properties.create(Material.PORTAL).doesNotBlockMovement().hardnessAndResistance(-1.0F).sound(SoundType.GLASS).lightValue(11).noDrops()));
		
		BiFunction<String, Block, Block> specialReg = (registryKey, block) ->{
			reg.register(registryKey, block);
			registry_keys.add(registryKey);
			return block;
		};
		
		for (String rockname : DungeonFist.ROCK_NAMES)
		{
			for (String blockname : DungeonFist.IMPROVABLE_BLOCK_TYPES)
			{
				String rockblock = rockname + "_" + blockname;
				Block.Properties props = blockprops.get(blockname);
				Block block = specialReg.apply(rockblock, new Block(props)); // need to save this for stairs
				specialReg.apply(rockblock + "_slab", new SlabBlock(props));
				specialReg.apply(rockblock + "_stairs", new StairsBlock(block::getDefaultState, props));
				specialReg.apply(rockblock + "_wall", new WallBlock(props));
				specialReg.apply(rockblock + "_button", new ConstructableStoneButtonBlock(BUTTON_PROPERTIES));
				specialReg.apply(rockblock + "_pressure_plate", new ConstructableStonePressurePlateBlock(PRESSURE_PLATE_PROPERTIES));
			}
			specialReg.apply(rockname+"_chiseled_stone_bricks", new Block(STONE_BRICK_PROPERTIES));
		}
	}
}
