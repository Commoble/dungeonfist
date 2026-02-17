package net.commoble.dungeonfist.dynamic_processor;

import org.jspecify.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import net.commoble.dungeonfist.DungeonFist;
import net.commoble.dungeonfist.DungeonMaterial;
import net.commoble.structurebuddy.api.DynamicProcessor;
import net.commoble.structurebuddy.api.JigsawPieceDataReader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public enum DungeonMaterialDynamicProcessor implements DynamicProcessor
{
	INSTANCE;
	
	public static final MapCodec<DungeonMaterialDynamicProcessor> CODEC = MapCodec.unit(INSTANCE); 
	
	@Override
	public MapCodec<? extends DynamicProcessor> codec()
	{
		return CODEC;
	}

	@Override
	public @Nullable StructureBlockInfo process(
		LevelReader level,
		BlockPos targetPosition,
		BlockPos referencePos,
		StructureBlockInfo originalBlockInfo,
		StructureBlockInfo processedBlockInfo,
		StructurePlaceSettings settings,
		@Nullable StructureTemplate template,
		JigsawPieceDataReader jigsawData)
	{
		BlockState originalState = processedBlockInfo.state();
		BlockState newState = originalState;
		BlockPos pos = processedBlockInfo.pos();
		@Nullable Holder<DungeonMaterial> materialHolder = jigsawData.getData(DungeonFist.DUNGEON_MATERIAL_JIGSAW_DATA.get());
		if (materialHolder != null)
		{
			var lookup = materialHolder.value().stateLookup().get();
			@Nullable BlockState mappedState = lookup.get(newState);
			if (mappedState != null)
			{
				newState = mappedState;
			}
		}
		return newState == originalState
			? processedBlockInfo
			: new StructureBlockInfo(pos, newState, processedBlockInfo.nbt());
	}
}
