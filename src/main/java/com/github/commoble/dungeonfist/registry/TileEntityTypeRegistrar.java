package com.github.commoble.dungeonfist.registry;

import com.github.commoble.dungeonfist.DungeonFist;
import com.github.commoble.dungeonfist.Registrator;
import com.github.commoble.dungeonfist.blocks.DungeonPortalTileEntity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(DungeonFist.MODID)
public class TileEntityTypeRegistrar
{
	@ObjectHolder(DungeonFist.DUNGEON_PORTAL)
	public static final TileEntityType<DungeonPortalTileEntity> DUNGEON_PORTAL = null;
	
	public static void registerTileEntities(Registrator<TileEntityType<?>> reg)
	{
		reg.register(
				DungeonFist.DUNGEON_PORTAL,
				TileEntityType.Builder.create(DungeonPortalTileEntity::new, BlockRegistrar.DUNGEON_PORTAL).build(null));
	}
}
