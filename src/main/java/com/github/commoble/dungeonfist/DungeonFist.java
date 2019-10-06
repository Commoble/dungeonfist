package com.github.commoble.dungeonfist;

import java.util.Optional;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(DungeonFist.MODID)
public class DungeonFist
{	
	public static final String MODID = "dungeonfist";
	public static Optional<ClientProxy> clientProxy = Optional.ofNullable(DistExecutor.callWhenOn(Dist.CLIENT, () -> ClientProxy::new));
	
	// object names
	public static final String[] ROCK_NAMES = {
			"andesite", "basalt", "chert", "diorite",
			"dolomite", "gabbro", "gneiss", "granite",
			"halite", "limestone", "marble", "phyllite",
			"rhyolite", "schist", "shale", "slate"
	};
	
	public static final String[] IMPROVABLE_BLOCK_TYPES = {
			"stone",
			"smooth_stone",
			"cobblestone",
			"mossy_cobblestone",
			"stone_bricks",
			"mossy_stone_bricks",
			"cracked_stone_bricks"
	};
	
	public static final String CHISELED_STONE_BRICK = "chiseled_stone_brick";
	
	public static final String DUNGEON = "dungeon";
	public static final String DUNGEON_PORTAL = "dungeon_portal";
	public static final String DUNGEON_PORTAL_GLOWY_AIR = "dungeon_portal_glowy_air";
}
