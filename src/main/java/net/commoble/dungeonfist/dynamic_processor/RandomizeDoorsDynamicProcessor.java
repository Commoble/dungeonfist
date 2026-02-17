package net.commoble.dungeonfist.dynamic_processor;

import org.jspecify.annotations.Nullable;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.commoble.dungeonfist.BlockMasher;
import net.commoble.dungeonfist.CodecPuncher;
import net.commoble.structurebuddy.api.DynamicProcessor;
import net.commoble.structurebuddy.api.JigsawPieceDataReader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public record RandomizeDoorsDynamicProcessor(HolderSet<Block> input, WeightedList<Block> outputs) implements DynamicProcessor
{
	public static final MapCodec<RandomizeDoorsDynamicProcessor> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
			CodecPuncher.BLOCKS.fieldOf("input").forGetter(RandomizeDoorsDynamicProcessor::input),
			WeightedList.nonEmptyCodec(BuiltInRegistries.BLOCK.byNameCodec()).fieldOf("outputs").forGetter(RandomizeDoorsDynamicProcessor::outputs)
		).apply(builder, RandomizeDoorsDynamicProcessor::new));
	
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
		BlockState oldState = processedBlockInfo.state();
		if (oldState.is(this.input))
		{
	        BlockPos pos = processedBlockInfo.pos();
	        // randomize from seed on xz but not y, so all blocks in the door column randomize to the same block
	        @SuppressWarnings("deprecation")
			RandomSource random = RandomSource.create(Mth.getSeed(new BlockPos(pos.getX(), 0, pos.getZ())));
			Block newBlock = this.outputs.getRandomOrThrow(random);
			BlockState newState = newBlock.defaultBlockState();
			if (!newState.isAir())
			{
				newState = BlockMasher.copyProperties(oldState, newState);
				// randomly flip hinge and facing (won't rotate door 90 degrees)
				if (random.nextBoolean())
				{
					newState = newState.mirror(Mirror.LEFT_RIGHT);
				}
				if (random.nextBoolean())
				{
					newState = newState.mirror(Mirror.FRONT_BACK);
				}
			}
			return new StructureBlockInfo(pos, newState, processedBlockInfo.nbt());
		}
		else
		{
			return processedBlockInfo;
		}
	}

	
}
