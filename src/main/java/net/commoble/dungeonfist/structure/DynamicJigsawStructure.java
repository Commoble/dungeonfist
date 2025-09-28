package net.commoble.dungeonfist.structure;

import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.commoble.dungeonfist.DungeonFist;
import net.commoble.dungeonfist.OctreeJigsawPlacer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

public class DynamicJigsawStructure extends Structure
{
	// structure should be an interface but it isn't so we can't make our subclass a record
	// too lazy to write getters for each of these
	public static record DynamicJigsawStructureParams(
		Holder<StructureTemplatePool> startPool,
		Optional<ResourceLocation> startJigsawName,
		int size,
		HeightProvider startHeight,
		Optional<Heightmap.Types> projectStartToHeightmap,
		int maxDistanceFromCenter,
		List<PoolAliasBinding> poolAliases,
		DimensionPadding dimensionPadding,
		LiquidSettings liquidSettings) {}
	
	public static final MapCodec<DynamicJigsawStructureParams> PARAMS_CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
			StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(DynamicJigsawStructureParams::startPool),
			ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(DynamicJigsawStructureParams::startJigsawName),
			ExtraCodecs.NON_NEGATIVE_INT.fieldOf("size").forGetter(DynamicJigsawStructureParams::size),
			HeightProvider.CODEC.fieldOf("start_height").forGetter(DynamicJigsawStructureParams::startHeight),
			Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(DynamicJigsawStructureParams::projectStartToHeightmap),
			Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(DynamicJigsawStructureParams::maxDistanceFromCenter),
			Codec.list(PoolAliasBinding.CODEC).optionalFieldOf("pool_aliases", List.of()).forGetter(DynamicJigsawStructureParams::poolAliases),
			DimensionPadding.CODEC.optionalFieldOf("dimension_padding", DimensionPadding.ZERO).forGetter(DynamicJigsawStructureParams::dimensionPadding),
			LiquidSettings.CODEC.optionalFieldOf("liquid_settings", LiquidSettings.IGNORE_WATERLOGGING).forGetter(DynamicJigsawStructureParams::liquidSettings))
		.apply(builder, DynamicJigsawStructureParams::new));
		
	public static final MapCodec<DynamicJigsawStructure> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
			Structure.settingsCodec(builder),
			PARAMS_CODEC.forGetter(DynamicJigsawStructure::params)
	    ).apply(builder, DynamicJigsawStructure::new));
	
	private final DynamicJigsawStructureParams params;
	public DynamicJigsawStructureParams params()
	{
		return this.params;
	}

	public DynamicJigsawStructure(StructureSettings settings, DynamicJigsawStructureParams params)
	{
		super(settings);
		this.params = params;
	}

	@Override
	public StructureType<?> type()
	{
		return DungeonFist.DYNAMIC_JIGSAW_STRUCTURE_TYPE.get();
	}

	@Override
	protected Optional<GenerationStub> findGenerationPoint(GenerationContext context)
	{
		ChunkPos chunkPos = context.chunkPos();
		int startY = this.params.startHeight.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));
		BlockPos startPos = new BlockPos(chunkPos.getMinBlockX(), startY, chunkPos.getMinBlockZ());
		return OctreeJigsawPlacer.addPieces(context, this.params, startPos);
	}
}
