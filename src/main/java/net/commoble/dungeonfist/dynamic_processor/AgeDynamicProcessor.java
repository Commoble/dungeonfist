package net.commoble.dungeonfist.dynamic_processor;

import java.util.Optional;

import org.jspecify.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import net.commoble.dungeonfist.DungeonFist;
import net.commoble.dungeonfist.DungeonFistDataMaps;
import net.commoble.structurebuddy.api.DynamicProcessor;
import net.commoble.structurebuddy.api.JigsawPieceDataReader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.level.material.FluidState;

public record AgeDynamicProcessor(Optional<Block> airBlock) implements DynamicProcessor
{	
	public static final MapCodec<AgeDynamicProcessor> CODEC = BuiltInRegistries.BLOCK.byNameCodec()
		.optionalFieldOf("air_block")
		.xmap(AgeDynamicProcessor::new, AgeDynamicProcessor::airBlock);

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
		Integer age = jigsawData.getData(DungeonFist.DUNGEON_AGE_JIGSAW_DATA.get());
		if (age != null && random.nextInt(100) < age)
		{
			@Nullable BlockStateProvider provider = newState.typeHolder().getData(DungeonFistDataMaps.AGEABLES);
			if (provider != null)
			{
				newState = provider.getState(random, pos);
				if (newState.isAir())
				{
					if (this.airBlock.isEmpty())
					{
						if (level instanceof ScheduledTickAccess ticker)
						{
							FluidState fluidState = level.getFluidState(pos);
							if (!fluidState.isEmpty())
							{
								ticker.scheduleTick(pos, fluidState.getType(), 0);
							}
						}
						return null;	
					}
					newState = this.airBlock.map(Block::defaultBlockState).orElse(newState);
				}
			}
		}
		return newState == originalState
			? processedBlockInfo
			: new StructureBlockInfo(pos, newState, processedBlockInfo.nbt());
	}
}
