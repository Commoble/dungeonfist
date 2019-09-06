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
	public static final String DUNGEON = "dungeon";
	public static final String DUNGEON_PORTAL = "dungeon_portal";
	public static final String DUNGEON_PORTAL_GLOWY_AIR = "dungeon_portal_glowy_air";
}
