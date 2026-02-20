package net.commoble.dungeonfist.structure_processor;

import org.jspecify.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.commoble.dungeonfist.CodecPuncher;
import net.commoble.dungeonfist.DungeonFist;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public class SetDataProcessor extends StructureProcessor
{
	public static final MapCodec<SetDataProcessor> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
			CodecPuncher.BLOCKS.fieldOf("blocks").forGetter(SetDataProcessor::blocks),
			CompoundTag.CODEC.fieldOf("data").forGetter(SetDataProcessor::data),
			Codec.FLOAT.optionalFieldOf("probability", 1F).forGetter(SetDataProcessor::probability)
		).apply(builder, SetDataProcessor::new));
	
	private final HolderSet<Block> blocks;
	private final CompoundTag data;
	private final float probability;
	
	public SetDataProcessor(HolderSet<Block> blocks, CompoundTag data, float probability)
	{
		this.blocks = blocks;
		this.data = data;
		this.probability = probability;
	}
	
	public HolderSet<Block> blocks()
	{
		return this.blocks;
	}
	
	public CompoundTag data()
	{
		return this.data;
	}
	
	public float probability()
	{
		return this.probability;
	}
	
	@Override
	protected StructureProcessorType<?> getType()
	{
		return DungeonFist.SET_DATA_PROCESSOR.get();
	}

	@Override
	public @Nullable StructureBlockInfo process(LevelReader level, BlockPos targetPosition, BlockPos referencePos, StructureBlockInfo originalBlockInfo,
		StructureBlockInfo processedBlockInfo, StructurePlaceSettings settings, @Nullable StructureTemplate template)
	{
		BlockPos pos = processedBlockInfo.pos();
        RandomSource random = settings.getRandom(pos);
		BlockState state = processedBlockInfo.state();
		if (random.nextFloat() < this.probability && state.is(this.blocks))
		{
			@Nullable CompoundTag oldTag = processedBlockInfo.nbt();
			CompoundTag baseTag = oldTag == null
				? new CompoundTag()
				: oldTag.copy();
			return new StructureBlockInfo(
				pos,
				state,
				baseTag.merge(this.data));
		}
		else
		{
			return processedBlockInfo;
		}
	}

	
}
