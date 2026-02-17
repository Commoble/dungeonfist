package net.commoble.dungeonfist.dynamic_processor;

import org.jspecify.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import net.commoble.dungeonfist.BlockMasher;
import net.commoble.dungeonfist.DungeonFist;
import net.commoble.dungeonfist.DungeonFistDataMaps;
import net.commoble.structurebuddy.api.DynamicProcessor;
import net.commoble.structurebuddy.api.JigsawPieceDataReader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public enum MoistenDynamicProcessor implements DynamicProcessor
{
	INSTANCE;
	
	public static final MapCodec<MoistenDynamicProcessor> CODEC = MapCodec.unit(INSTANCE);

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
        RandomSource random = settings.getRandom(pos);
		Integer moistness = jigsawData.getData(DungeonFist.DUNGEON_MOISTNESS_JIGSAW_DATA.get());
		if (moistness != null && random.nextInt(100) < moistness)
		{
			@Nullable WeightedList<HolderSet<Block>> weightedList = newState.typeHolder().getData(DungeonFistDataMaps.MOISTABLES);
			if (weightedList != null)
			{
				@Nullable Holder<Block> newHolder = weightedList.getRandomOrThrow(random).getRandomElement(random).orElse(null);
				if (newHolder != null)
				{
					Block newBlock = newHolder.value();
					BlockState previousState = newState;
					newState = newBlock.defaultBlockState();
					newState = BlockMasher.copyProperties(previousState, newState);
				}
			}
		}
		return newState == originalState
			? processedBlockInfo
			: new StructureBlockInfo(pos, newState, processedBlockInfo.nbt());
	}	
}
