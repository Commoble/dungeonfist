package net.commoble.dungeonfist;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

public final class DungeonFistDataMaps
{
	private DungeonFistDataMaps() {} // just holds constants
	
	public static final DataMapType<Block, BlockStateProvider> AGEABLES = DataMapType.builder(DungeonFist.id("ageables"), Registries.BLOCK, BlockStateProvider.CODEC).build();
	public static final DataMapType<Block, WeightedList<HolderSet<Block>>> MOISTABLES = DataMapType.builder(DungeonFist.id("moistables"), Registries.BLOCK, WeightedList.nonEmptyCodec(CodecPuncher.BLOCKS)).build();
}
