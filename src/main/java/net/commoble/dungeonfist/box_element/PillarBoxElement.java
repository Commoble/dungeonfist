package net.commoble.dungeonfist.box_element;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.commoble.dungeonfist.BoxCutter;
import net.commoble.dungeonfist.BoxCutter.BoxSection;
import net.commoble.dungeonfist.CodecPuncher;
import net.commoble.structurebuddy.api.AxisAnchor;
import net.commoble.structurebuddy.api.BoxBakeContext;
import net.commoble.structurebuddy.api.BoxElement;
import net.commoble.structurebuddy.api.BoxResult;
import net.commoble.structurebuddy.api.BoxSnap;
import net.commoble.structurebuddy.api.BoxSnap.FaceBoxSnap;
import net.commoble.structurebuddy.api.DynamicJigsawFillContext;
import net.commoble.structurebuddy.api.DynamicJigsawPool;
import net.commoble.structurebuddy.api.DynamicJigsawResult;
import net.commoble.structurebuddy.api.DynamicProcessor;
import net.commoble.structurebuddy.api.JigsawConnectionToChild;
import net.commoble.structurebuddy.api.JigsawConnectionToParent;
import net.commoble.structurebuddy.api.JigsawDataReader;
import net.commoble.structurebuddy.api.JigsawOverrides;
import net.commoble.structurebuddy.api.PieceFiller;
import net.commoble.structurebuddy.api.SelectableJigsawConnectionToParent;
import net.commoble.structurebuddy.api.SnapResult;
import net.commoble.structurebuddy.api.util.BoxBuddy;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity.JointType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.TagValueInput;

public record PillarBoxElement(
	// blocks to fill in the entire pillar with
	Optional<HolderSet<Block>> blocks,
	int minWidth,
	int maxWidth,
	int minHeight,
	int maxHeight,
	boolean fillWidth,
	boolean fillHeight,
	BoxSnap snap,
	Optional<Holder<List<DynamicProcessor>>> processors,
	List<JigsawPlacer> jigsaws,
	// sections are only used if pillar is at least 3x3x3
	// if pillar won't be at least 3x3x3 then use the optional blocks field
	List<FillableSection> sections
	) implements BoxElement
{
	private static final Logger LOGGER = LogUtils.getLogger();

	public static final MapCodec<PillarBoxElement> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
			CodecPuncher.BLOCKS.optionalFieldOf("blocks").forGetter(PillarBoxElement::blocks),
			ExtraCodecs.intRange(1,1000).optionalFieldOf("min_width", 1).forGetter(PillarBoxElement::minWidth),
			ExtraCodecs.intRange(1,1000).optionalFieldOf("max_width", 1000).forGetter(PillarBoxElement::maxWidth),
			ExtraCodecs.intRange(1,1000).optionalFieldOf("min_height", 1).forGetter(PillarBoxElement::minHeight),
			ExtraCodecs.intRange(1,1000).optionalFieldOf("max_height", 1000).forGetter(PillarBoxElement::maxHeight),
			Codec.BOOL.optionalFieldOf("fill_width", false).forGetter(PillarBoxElement::fillWidth),
			Codec.BOOL.optionalFieldOf("fill_height", false).forGetter(PillarBoxElement::fillHeight),
			BoxSnap.CODEC.optionalFieldOf("snap", FaceBoxSnap.FLOOR).forGetter(PillarBoxElement::snap),
			DynamicProcessor.LIST_HOLDER_CODEC.optionalFieldOf("processors").forGetter(PillarBoxElement::processors),
			JigsawPlacer.CODEC.listOf().optionalFieldOf("jigsaws", List.of()).forGetter(PillarBoxElement::jigsaws),
			FillableSection.CODEC.listOf().optionalFieldOf("sections", List.of()).forGetter(PillarBoxElement::sections)
		)
		.apply(builder, PillarBoxElement::new));
			
	@Override
	public MapCodec<? extends BoxElement> codec()
	{
		return CODEC;
	}

	@Override
	public BoxResult bake(BoxBakeContext context)
	{
		BoundingBox parentBox = context.box();
		int parentWidthX = parentBox.getXSpan();
		int parentWidthZ = parentBox.getZSpan();
		int parentHeight = parentBox.getYSpan();
		if (this.minHeight > parentHeight
			|| this.minWidth > parentWidthX
			|| this.minWidth > parentWidthZ) {
			return BoxResult.invalid();
		}
		RandomSource random = context.generationContext().random();
		int maxWidthX = Math.min(this.maxWidth, parentWidthX);
		int maxWidthZ = Math.min(this.maxWidth, parentWidthZ);
		int maxHeight = Math.min(this.maxHeight, parentHeight);
		int xSpan = this.fillWidth ? parentWidthX : random.nextIntBetweenInclusive(this.minWidth, maxWidthX);
		int zSpan = this.fillWidth ? parentWidthZ : random.nextIntBetweenInclusive(this.minWidth, maxWidthZ);
		int ySpan = this.fillHeight ? parentHeight : random.nextIntBetweenInclusive(this.minHeight, maxHeight);
		BoundingBox localBox = new BoundingBox(0,0,0, xSpan-1, ySpan-1, zSpan-1);
		@Nullable SnapResult snapResult = this.snap.getSnap(List.of(), context.boundingSurfaces(), random);
		if (snapResult == null)
			return BoxResult.invalid();

		BoundingBox finalBox = snapResult.snap(localBox, parentBox, random);

		List<JigsawPlacer> jigsaws = new ArrayList<>(this.jigsaws.size());
		for (JigsawPlacer jigsaw : this.jigsaws)
		{
			jigsaws.add(jigsaw);
		}
		// shuffle jigsaws
		List<SelectableJigsawConnectionToParent> shuffledConnectionsToParent = new ArrayList<>(jigsaws.size());
		List<JigsawConnectionToChild> connectionsToChildren = new ArrayList<>(jigsaws.size());
		while (!jigsaws.isEmpty())
		{
			JigsawPlacer jigsaw = jigsaws.remove(random.nextInt(jigsaws.size()));
			jigsaw.addConnections(finalBox, random, shuffledConnectionsToParent, connectionsToChildren);
		}
		shuffledConnectionsToParent.sort(Comparator.comparingInt(SelectableJigsawConnectionToParent::selectionPriority).reversed());
		List<JigsawConnectionToParent> selectedConnectionsToParent = new ArrayList<>(shuffledConnectionsToParent.size());
		for (var selectable : shuffledConnectionsToParent)
		{
			selectedConnectionsToParent.add(selectable.connection());
		}
		
		Function<JigsawDataReader, PieceFiller> pieceFactory = data -> {
			Optional<BlockState> optionalState = this.blocks
				.flatMap(blocks -> blocks.getRandomElement(random))
				.map(holder -> holder.value().defaultBlockState());
			List<Pair<BlockState,BoxSection>> sections = this.sections
				.stream()
				.map(section -> Pair.of(
					section.blocks.getRandomElement(random)
						.map(holder -> holder.value().defaultBlockState())
						.orElse(Blocks.AIR.defaultBlockState()),
					section.section()))
				.toList();
			return new PillarPieceFiller(optionalState, sections, this.processors);
		};
		return new BoxResult(pieceFactory, finalBox, selectedConnectionsToParent, connectionsToChildren, DynamicJigsawResult.NOOP_ON_SELECTED); 
	}
	
	// TODO rotation not supported
	// works fine for vertical jigsaws and xz-symmetrical pillars
	// horizontal pieces need more work done first
	public static record JigsawPlacer(
		AxisAnchor x,
		AxisAnchor y,
		AxisAnchor z,
		FrontAndTop orientation,
		JigsawOverrides jigsaw)
	{		
		public static final Codec<JigsawPlacer> CODEC = RecordCodecBuilder.create(builder -> builder.group(
				AxisAnchor.CODEC.fieldOf("x").forGetter(JigsawPlacer::x),
				AxisAnchor.CODEC.fieldOf("y").forGetter(JigsawPlacer::y),
				AxisAnchor.CODEC.fieldOf("z").forGetter(JigsawPlacer::z),
				StringRepresentable.fromEnum(FrontAndTop::values).fieldOf("orientation").forGetter(JigsawPlacer::orientation),
				JigsawOverrides.CODEC.fieldOf("jigsaw").forGetter(JigsawPlacer::jigsaw)
			).apply(builder, JigsawPlacer::new));
		
		public void addConnections(BoundingBox box, RandomSource random, List<SelectableJigsawConnectionToParent> selectableJigsawConnectionsToParents, List<JigsawConnectionToChild> connectionsToChildren)
		{
			Identifier name = this.jigsaw.name().orElse(DynamicJigsawResult.EMPTY_NAME);
			Identifier targetName = this.jigsaw.targetName().orElse(DynamicJigsawResult.EMPTY_NAME);
			ResourceKey<DynamicJigsawPool> targetPoolKey = this.jigsaw.targetPool().orElse(DynamicJigsawPool.EMPTY);
			BlockPos pos = AxisAnchor.getPos(box, this.x, this.y, this.z, random);
			if (!targetName.equals(DynamicJigsawResult.EMPTY_NAME) && targetPoolKey != DynamicJigsawPool.EMPTY)
			{
				connectionsToChildren.add(new JigsawConnectionToChild(
					pos,
					orientation,
					jigsaw.jointType().orElse(JointType.ROLLABLE),
					targetPoolKey,
					targetName
				));
			}
			if (!name.equals(DynamicJigsawResult.EMPTY_NAME))
			{
				selectableJigsawConnectionsToParents.add(
					new SelectableJigsawConnectionToParent(
						new JigsawConnectionToParent(
							pos,
							this.orientation,
							name,
							jigsaw.placementPriority().orElse(0)),
						jigsaw.selectionPriority().orElse(0)));
			}
		}
	}
	
	public record FillableSection(
		BoxSection section,
		HolderSet<Block> blocks
	) {
		public static final Codec<FillableSection> CODEC = RecordCodecBuilder.create(builder -> builder.group(
				BoxSection.CODEC.fieldOf("section").forGetter(FillableSection::section),
				CodecPuncher.BLOCKS.fieldOf("blocks").forGetter(FillableSection::blocks)
			).apply(builder, FillableSection::new));
	}

	public record PillarPieceFiller(Optional<BlockState> state, List<Pair<BlockState,BoxSection>> sections, Optional<Holder<List<DynamicProcessor>>> processors) implements PieceFiller
	{
		public static final MapCodec<PillarPieceFiller> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
				BlockState.CODEC.optionalFieldOf("state").forGetter(PillarPieceFiller::state),
				Codec.mapPair(
					BlockState.CODEC.fieldOf("state"),
					BoxSection.CODEC.fieldOf("section"))
				.codec().listOf().fieldOf("sections").forGetter(PillarPieceFiller::sections),
				DynamicProcessor.LIST_HOLDER_CODEC.optionalFieldOf("processors").forGetter(PillarPieceFiller::processors)
			).apply(builder, PillarPieceFiller::new));
		
		@Override
		public MapCodec<? extends PieceFiller> codec()
		{
			return CODEC;
		}

		@Override
		public void fill(DynamicJigsawFillContext context)
		{
			WorldGenLevel level = context.level();
			List<DynamicProcessor> processors = this.processors.map(Holder::value).orElse(List.of());
			BoundingBox pieceBox = context.pieceBoundingBox();
			BoundingBox chunkBox = context.chunkBoundingBox();
			RandomSource random = context.random();
			if (processors.isEmpty())
			{
				this.state.ifPresent(state -> {
					BoxBuddy.intersection(chunkBox, pieceBox).ifPresent(intersection -> {
						BoxBuddy.forEachPos(intersection, pos -> {
							level.setBlock(pos, state, 2);
						});
						FluidState fluidState = state.getFluidState();
						if (!fluidState.isEmpty())
						{
							BoxBuddy.forEachPos(intersection, pos -> {
								level.scheduleTick(pos, fluidState.getType(), 0);
							});
						}
					});
				});
				if (!this.sections.isEmpty() && BoxCutter.canCut(pieceBox))
				{
					BoxCutter boxCutter = BoxCutter.cut(pieceBox);
					for (var pair : this.sections)
					{
						BlockState state = pair.getFirst();
						BoxSection section = pair.getSecond();
						for (BoundingBox sectionBox : section.getSections(boxCutter))
						{
							BoxBuddy.intersection(chunkBox, sectionBox).ifPresent(intersection -> {
								BoxBuddy.forEachPos(intersection, pos -> {
									level.setBlock(pos, state, 2);	
								});
								FluidState fluidState = state.getFluidState();
								if (!fluidState.isEmpty())
								{
									BoxBuddy.forEachPos(intersection, pos -> {
										level.scheduleTick(pos, fluidState.getType(), 0);
									});
								}
							});
						}
					}
				}
			}
			else {
				// processors can move blocks...
				// so, have to calculate the blocks first,
				// THEN check if any are in the structure piece bounds
				BlockPos pieceStart = BoxBuddy.minCorner(pieceBox);
				BlockPos startPieceFloorCenter = context.startPieceFloorCenter();
				StructurePlaceSettings settings = new StructurePlaceSettings();
				settings.setRandom(context.random());
				var jigsawData = context.jigsawData();
                try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(LOGGER))
                {
					this.state.ifPresent(state -> {
						BoxBuddy.forEachPos(pieceBox, pos -> {
							final StructureBlockInfo originalInfo = new StructureBlockInfo(pos, state, null);
							@Nullable StructureBlockInfo modifiedInfo = originalInfo; 
							for (DynamicProcessor processor : processors)
							{
								if (modifiedInfo == null)
									break;
								modifiedInfo = processor.process(level, pieceStart, startPieceFloorCenter, originalInfo, modifiedInfo, settings, null, jigsawData);
							}
							if (modifiedInfo != null)
							{
								BlockPos finalPos = modifiedInfo.pos();
								if (chunkBox.isInside(finalPos))
								{
									BlockState finalState = modifiedInfo.state();
									if (level.setBlock(finalPos, finalState, 2))
									{
										@Nullable CompoundTag tag = modifiedInfo.nbt();
										if (tag != null)
										{
											BlockEntity blockEntity = level.getBlockEntity(finalPos);
											if (blockEntity != null)
											{
												if (!SharedConstants.DEBUG_STRUCTURE_EDIT_MODE && blockEntity instanceof RandomizableContainer)
												{
													tag.putLong("LootTableSeed", random.nextLong());
												}
	
												blockEntity.loadWithComponents(TagValueInput.create(reporter.forChild(blockEntity.problemPath()), level.registryAccess(), tag));
											}
										}
									}
									FluidState fluidState = finalState.getFluidState();
									if (!fluidState.isEmpty())
									{
										level.scheduleTick(finalPos, fluidState.getType(), 0);
									}
								}	
							}
						});
					});
					if (!this.sections.isEmpty() && BoxCutter.canCut(pieceBox))
					{
						BoxCutter boxCutter = BoxCutter.cut(pieceBox);
						for (var pair : this.sections)
						{
							BlockState state = pair.getFirst();
							BoxSection section = pair.getSecond();
							for (BoundingBox sectionBox : section.getSections(boxCutter))
							{
								BoxBuddy.forEachPos(sectionBox, pos -> {
									final StructureBlockInfo originalInfo = new StructureBlockInfo(pos, state, null);
									@Nullable StructureBlockInfo modifiedInfo = originalInfo; 
									for (DynamicProcessor processor : processors)
									{
										if (modifiedInfo == null)
											break;
										modifiedInfo = processor.process(level, pieceStart, startPieceFloorCenter, originalInfo, modifiedInfo, settings, null, jigsawData);
									}
									if (modifiedInfo != null)
									{
										BlockPos finalPos = modifiedInfo.pos();
										if (chunkBox.isInside(finalPos))
										{
											BlockState finalState = modifiedInfo.state();
											if (level.setBlock(finalPos, finalState, 2))
											{
												@Nullable CompoundTag tag = modifiedInfo.nbt();
												if (tag != null)
												{
													BlockEntity blockEntity = level.getBlockEntity(finalPos);
													if (blockEntity != null)
													{
														if (!SharedConstants.DEBUG_STRUCTURE_EDIT_MODE && blockEntity instanceof RandomizableContainer)
														{
															tag.putLong("LootTableSeed", random.nextLong());
														}
			
														blockEntity.loadWithComponents(TagValueInput.create(reporter.forChild(blockEntity.problemPath()), level.registryAccess(), tag));
													}
												}
											}
											FluidState fluidState = finalState.getFluidState();
											if (!fluidState.isEmpty())
											{
												level.scheduleTick(finalPos, fluidState.getType(), 0);
											}
										}	
									}
								});
							}
						}
					}
                }
			}
		}
	}
}
