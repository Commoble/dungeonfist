package net.commoble.dungeonfist.dynamic_jigsaw_element;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.commoble.dungeonfist.BoxCutter;
import net.commoble.dungeonfist.DungeonFist;
import net.commoble.dungeonfist.MoreBoxing;
import net.commoble.dungeonfist.RandomHelper;
import net.commoble.dungeonfist.Subdivider;
import net.commoble.structurebuddy.api.BoxBakeContext;
import net.commoble.structurebuddy.api.BoxElement;
import net.commoble.structurebuddy.api.BoxPool;
import net.commoble.structurebuddy.api.BoxResult;
import net.commoble.structurebuddy.api.DynamicJigsawBakeContext;
import net.commoble.structurebuddy.api.DynamicJigsawElement;
import net.commoble.structurebuddy.api.DynamicJigsawFillContext;
import net.commoble.structurebuddy.api.DynamicJigsawResult;
import net.commoble.structurebuddy.api.DynamicProcessor;
import net.commoble.structurebuddy.api.JigsawConnectionToChild;
import net.commoble.structurebuddy.api.JigsawConnectionToParent;
import net.commoble.structurebuddy.api.JigsawDataAccess;
import net.commoble.structurebuddy.api.JigsawDataReader;
import net.commoble.structurebuddy.api.JigsawDataType;
import net.commoble.structurebuddy.api.PieceFiller;
import net.commoble.structurebuddy.api.StructureBuddyRegistries;
import net.commoble.structurebuddy.api.content.EmptyBoxElement;
import net.commoble.structurebuddy.api.util.BoxBuddy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public record DungeonRoomDynamicJigsawElement(
	Block templateBlock,
	int minWidth,
	int maxWidth,
	int minHeight,
	int maxHeight,
	IntProvider subdivisions,
	Exits exits,
	Optional<Holder<BoxPool>> featurePool,
	Optional<Holder<BoxPool>> wallFeaturePool,
	Optional<OccasionalBoxPool> wallEmbed,
	Optional<OccasionalBoxPool> floorEmbed,
	Optional<OccasionalBoxPool> ceilingEmbed,
	Optional<OccasionalBoxPool> cornerEmbed,
	Optional<Holder<List<DynamicProcessor>>> processors,
	DungeonProperty age,
	DungeonProperty moistness
	) implements DynamicJigsawElement
{
	public static final MapCodec<DungeonRoomDynamicJigsawElement> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
			BuiltInRegistries.BLOCK.byNameCodec().fieldOf("template_block").forGetter(DungeonRoomDynamicJigsawElement::templateBlock),
			Codec.intRange(3,272).fieldOf("min_width").forGetter(DungeonRoomDynamicJigsawElement::minWidth),
			Codec.intRange(3,272).fieldOf("max_width").forGetter(DungeonRoomDynamicJigsawElement::maxWidth),
			Codec.intRange(4,1000).fieldOf("min_height").forGetter(DungeonRoomDynamicJigsawElement::minHeight),
			Codec.intRange(4,1000).fieldOf("max_height").forGetter(DungeonRoomDynamicJigsawElement::maxHeight),
			IntProvider.CODEC.fieldOf("subdivisions").forGetter(DungeonRoomDynamicJigsawElement::subdivisions),
			Exits.CODEC.fieldOf("exits").forGetter(DungeonRoomDynamicJigsawElement::exits),
			BoxPool.CODEC.optionalFieldOf("feature_pool").forGetter(DungeonRoomDynamicJigsawElement::featurePool),
			BoxPool.CODEC.optionalFieldOf("wall_feature_pool").forGetter(DungeonRoomDynamicJigsawElement::wallFeaturePool),
			OccasionalBoxPool.CODEC.optionalFieldOf("wall_embed").forGetter(DungeonRoomDynamicJigsawElement::wallEmbed),
			OccasionalBoxPool.CODEC.optionalFieldOf("floor_embed").forGetter(DungeonRoomDynamicJigsawElement::floorEmbed),
			OccasionalBoxPool.CODEC.optionalFieldOf("ceiling_embed").forGetter(DungeonRoomDynamicJigsawElement::ceilingEmbed),
			OccasionalBoxPool.CODEC.optionalFieldOf("corner_embed").forGetter(DungeonRoomDynamicJigsawElement::cornerEmbed),
			DynamicProcessor.LIST_HOLDER_CODEC.optionalFieldOf("processors").forGetter(DungeonRoomDynamicJigsawElement::processors),
			DungeonProperty.CODEC.optionalFieldOf("age", DungeonProperty.SIMPLE).forGetter(DungeonRoomDynamicJigsawElement::age),
			DungeonProperty.CODEC.optionalFieldOf("moistness", DungeonProperty.SIMPLE).forGetter(DungeonRoomDynamicJigsawElement::moistness)
		).apply(builder, DungeonRoomDynamicJigsawElement::new))
		.validate(DungeonRoomDynamicJigsawElement::validate);
	
	public static DataResult<DungeonRoomDynamicJigsawElement> validate(DungeonRoomDynamicJigsawElement element)
	{
		if (element.minWidth > element.maxWidth)
		{
			return DataResult.error(() -> String.format("Invalid %s: minWidth %d > maxWidth %d", element, element.minWidth, element.maxWidth));
		}
		if (element.minHeight > element.maxHeight)
		{
			return DataResult.error(() -> String.format("Invalid %s: minHeight %d > maxHeight %d", element, element.minWidth, element.maxWidth));	
		}
		return DataResult.success(element);
	}
	
	@Override
	public MapCodec<? extends DynamicJigsawElement> codec()
	{
		return CODEC;
	}

	@Override
	public DynamicJigsawResult bake(DynamicJigsawBakeContext context)
	{
		GenerationContext generationContext = context.generationContext();
		LiquidSettings liquidSettings = context.liquidSettings();
		RandomSource random = generationContext.random();
		int remainingExits = this.exits.count.sample(random);
		if (remainingExits < 1)
		{
			// no point in generating room with no jigsaws
			return DynamicJigsawResult.invalid();
		}
		JigsawDataReader jigsawDataReader = context.data();
		BlockState templateState = this.templateBlock.defaultBlockState();
		int xSize = random.nextIntBetweenInclusive(this.minWidth, this.maxWidth);
		int ySize = random.nextIntBetweenInclusive(this.minHeight, this.maxHeight);
		int zSize = random.nextIntBetweenInclusive(this.minWidth, this.maxWidth);
		// sizes are in "number of blocks actually placed"
		// but bounding boxes are inclusive on both ends
		// so a box of (0,0,0,0,0,0) has size of 1 on each axis
		// so we need to subtract 1 from the sizes here
		int maxX = xSize-1;
		int maxY = ySize-1;
		int maxZ = zSize-1;
		BoundingBox localBoundingBox = new BoundingBox(0,0,0, maxX, maxY, maxZ);
		
		List<JigsawConnectionToParent> shuffledLocalConnectionsToParent = new ArrayList<>();
		List<JigsawConnectionToChild> shuffledLocalConnectionsToChildren = new ArrayList<>();
		
		Holder<BoxPool> emptyBoxPool = context.generationContext()
			.registryAccess()
			.lookupOrThrow(StructureBuddyRegistries.BOX_POOL)
			.getOrThrow(BoxPool.EMPTY);
		
		List<PartialDungeonStub> stubs = new ArrayList<>();
		
		int subdivisionCount = this.subdivisions.sample(random);

		BoxCutter cutBox = BoxCutter.cut(localBoundingBox);
		BoundingBox interior = cutBox.interior();
		Map<Direction,BoundingBox> sides = cutBox.sides();
		List<BoundingBox> interiorSubdivisions = RandomHelper.shuffled(Subdivider.subdivideXZ(interior, subdivisionCount, random), random);
		
		@Nullable OccasionalBoxPool wallEmbed = this.wallEmbed.orElse(null);
		@Nullable OccasionalBoxPool floorEmbed = this.floorEmbed.orElse(null);
		@Nullable OccasionalBoxPool ceilingEmbed = this.ceilingEmbed.orElse(null);
		@Nullable OccasionalBoxPool cornerEmbed = this.cornerEmbed.orElse(null);
		
		List<BiConsumer<JigsawDataAccess,RandomSource>> onStubsSelected = new ArrayList<>();
		
		loopOverSubdivisions:
		for (BoundingBox stubBox : interiorSubdivisions)
		{
			BoundingBox inflated = stubBox.inflatedBy(1);
			Optional<Holder<BoxPool>> optionalPoolHolder = this.featurePool;
			// check if this should be a wall feature
			// how does this work...
			// sometimes we want to use embedded features which can replace the wall itself
			// sometimes we don't, we just want things to be up against the wall
			// wall embeds shouldn't be used if we're in a corner (they'll get weird)
			// we can have a different pool for corner embeds if needed
			EnumSet<Direction> boundingSurfaces = EnumSet.of(Direction.DOWN, Direction.UP);
			EnumSet<Direction> walls = EnumSet.noneOf(Direction.class);
			for (int i=0; i<4; i++)
			{
				Direction dir = Direction.from2DDataValue(i);
				BoundingBox side = sides.get(dir);
				if (BoxBuddy.intersection(inflated, side).isPresent())
				{
					boundingSurfaces.add(dir);
					walls.add(dir);
				}
			}
			boolean embedWalls = false;
			boolean tryingExit = false;
			if (walls.isEmpty())
			{
				if (floorEmbed != null && floorEmbed.chance > 0D && random.nextDouble() < floorEmbed.chance)
				{
					stubBox = MoreBoxing.inflateToward(stubBox, Direction.DOWN);
					optionalPoolHolder = Optional.of(floorEmbed.pool);
				}
				else if (ceilingEmbed != null && ceilingEmbed.chance > 0D && random.nextDouble() < ceilingEmbed.chance)
				{
					stubBox = MoreBoxing.inflateToward(stubBox, Direction.UP);
					optionalPoolHolder = Optional.of(ceilingEmbed.pool);
				}
			}
			else
			{
				if (walls.size() > 1) // this is a corner
				{
					if (remainingExits > 0 && this.exits.cornerPool.isPresent())
					{
						embedWalls = true;
						tryingExit = true;
						optionalPoolHolder = this.exits.cornerPool;
					}
					else if (cornerEmbed != null && cornerEmbed.chance > 0D && random.nextDouble() < cornerEmbed.chance)
					{
						embedWalls = true;
						optionalPoolHolder = Optional.of(cornerEmbed.pool);
					}
					else if (floorEmbed != null && floorEmbed.chance > 0D && random.nextDouble() < floorEmbed.chance)
					{
						stubBox = MoreBoxing.inflateToward(stubBox, Direction.DOWN);
						optionalPoolHolder = Optional.of(floorEmbed.pool);
					}
					else if (ceilingEmbed != null && ceilingEmbed.chance > 0D && random.nextDouble() < ceilingEmbed.chance)
					{
						stubBox = MoreBoxing.inflateToward(stubBox, Direction.UP);
						optionalPoolHolder = Optional.of(ceilingEmbed.pool);
					}
					else
					{
						optionalPoolHolder = this.wallFeaturePool;
					}
				}
				else // not a corner but we do have a wall
				{
					if (remainingExits > 0)
					{
						embedWalls = true;
						tryingExit = true;
						optionalPoolHolder = this.exits.wallPool;
					}
					else if (wallEmbed != null && wallEmbed.chance > 0D && random.nextDouble() < wallEmbed.chance)
					{
						embedWalls = true;
						optionalPoolHolder = Optional.of(wallEmbed.pool);
					}
					else if (floorEmbed != null && floorEmbed.chance > 0D && random.nextDouble() < floorEmbed.chance)
					{
						stubBox = MoreBoxing.inflateToward(stubBox, Direction.DOWN);
						optionalPoolHolder = Optional.of(floorEmbed.pool);
					}
					else if (ceilingEmbed != null && ceilingEmbed.chance > 0D && random.nextDouble() < ceilingEmbed.chance)
					{
						stubBox = MoreBoxing.inflateToward(stubBox, Direction.UP);
						optionalPoolHolder = Optional.of(ceilingEmbed.pool);
					}
					else
					{
						optionalPoolHolder = this.wallFeaturePool;
					}
				}
			}
			if (embedWalls)
			{
				for (Direction dir : walls)
				{
					stubBox = MoreBoxing.inflateToward(stubBox, dir);
				}
			}
			// determine random features, then shuffle them into list
			Holder<BoxPool> targetFeaturePoolHolder = optionalPoolHolder.orElse(emptyBoxPool);
			BoxPool targetFeaturePool = targetFeaturePoolHolder.value();
			
			for (Holder<BoxElement> elementHolder : targetFeaturePool.getShuffledElements(random))
			{
				BoxElement element = elementHolder.value();
				// shortcut: if this is the empty element,
				// we have chosen not to generate anything in this subdivision
				if (element == EmptyBoxElement.INSTANCE)
					continue loopOverSubdivisions;
				
				for (Rotation stubRotation : Rotation.getShuffled(random))
				{						
					BoxBakeContext stubContext = new BoxBakeContext(
						context.generationContext(),
						stubBox,
						boundingSurfaces,
						jigsawDataReader,
						stubRotation,
						liquidSettings);
					
					BoxResult stubResult = element.bake(stubContext);
					BoundingBox childBox = stubResult.localBoundingBox();
					
					if (BoxBuddy.intersection(stubBox, childBox).map(childBox::equals).orElse(false))
					{
						stubs.add(new PartialDungeonStub(childBox, stubResult.pieceFillerFactory(), stubRotation));
						shuffledLocalConnectionsToParent.addAll(stubResult.connectionsToParent());
						shuffledLocalConnectionsToChildren.addAll(stubResult.connectionsToChildren());
						// if we placed an exit, decrement exit requirement counter
						if (tryingExit)
						{
							remainingExits--;
						}
						onStubsSelected.add(stubResult.onSelected());
						continue loopOverSubdivisions;
					}
				}
			}
		}
		
		BiConsumer<JigsawDataAccess,RandomSource> onSelected = (data,localRandom) -> {
			data.setBranchData(DungeonFist.DUNGEON_AGE_JIGSAW_DATA.get(), this.age.nextValue(jigsawDataReader, DungeonFist.DUNGEON_AGE_JIGSAW_DATA.get(), localRandom));
			data.setBranchData(DungeonFist.DUNGEON_MOISTNESS_JIGSAW_DATA.get(), this.moistness.nextValue(jigsawDataReader, DungeonFist.DUNGEON_MOISTNESS_JIGSAW_DATA.get(), localRandom));
			
			for (var onStubSelected : onStubsSelected)
			{
				onStubSelected.accept(data, localRandom);
			}
		};
		
		Function<JigsawDataReader, PieceFiller> pieceFactory = data -> {
			return new DungeonRoomPieceFiller(
				templateState,
				this.processors,
				stubs.stream().map(stub -> stub.finish(data)).toList());
		};
		return new DynamicJigsawResult(pieceFactory, localBoundingBox, shuffledLocalConnectionsToParent, shuffledLocalConnectionsToChildren, onSelected);
	}
	
	public static record PartialDungeonStub(BoundingBox box, Function<JigsawDataReader,PieceFiller> pieceFactory, Rotation rotation)
	{
		public DungeonStub finish(JigsawDataReader reader)
		{
			return new DungeonStub(this.box, this.pieceFactory.apply(reader), this.rotation);
		}
	}
	
	/**
	 * Represents something that can be generated somewhere in the dungeon room
	 * @param box BoundingBox of sub-piece relative to the dungeon room's box
	 * @param pieceFiller PieceFiller to fill that box with
	 * @param rotation Rotation for the piecefiller
	 */
	public static record DungeonStub(BoundingBox box, PieceFiller pieceFiller, Rotation rotation)
	{
		public static final Codec<DungeonStub> CODEC = RecordCodecBuilder.create(builder -> builder.group(
				BoundingBox.CODEC.fieldOf("box").forGetter(DungeonStub::box),
				PieceFiller.CODEC.fieldOf("piece_filler").forGetter(DungeonStub::pieceFiller),
				Rotation.CODEC.fieldOf("rotation").forGetter(DungeonStub::rotation)
			).apply(builder, DungeonStub::new));
	}

	public static record DungeonRoomPieceFiller(BlockState state, Optional<Holder<List<DynamicProcessor>>> processors, List<DungeonStub> stubs) implements PieceFiller
	{
		public static final MapCodec<DungeonRoomPieceFiller> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
				BlockState.CODEC.fieldOf("state").forGetter(DungeonRoomPieceFiller::state),
				DynamicProcessor.LIST_HOLDER_CODEC.optionalFieldOf("processors").forGetter(DungeonRoomPieceFiller::processors),
				DungeonStub.CODEC.listOf().fieldOf("stubs").forGetter(DungeonRoomPieceFiller::stubs)
			).apply(builder, DungeonRoomPieceFiller::new));
				
		@Override
		public MapCodec<? extends PieceFiller> codec()
		{
			return CODEC;
		}

		@Override
		public void fill(DynamicJigsawFillContext context)
		{
			WorldGenLevel level = context.level();
			BoundingBox chunkBox = context.chunkBoundingBox();
			BoundingBox pieceBox = context.pieceBoundingBox();
			BoxCutter boxCutter = BoxCutter.cut(pieceBox);
			BoundingBox interior = boxCutter.interior();
			
			BoxBuddy.intersection(chunkBox, interior)
				.ifPresent(air ->
					BoxBuddy.forEachPos(air, pos ->
						level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2)));
			

			List<DynamicProcessor> processors = this.processors.map(Holder::value).orElse(List.of());
			if (processors.isEmpty())
			{
				for (BoundingBox box : boxCutter.exterior())
				{
					BoxBuddy.intersection(chunkBox, box).ifPresent(intersection ->
						BoxBuddy.forEachPos(intersection, pos -> {
							level.setBlock(pos, this.state, 2);
						}));
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
				for (BoundingBox box : boxCutter.exterior())
				{
					BoxBuddy.forEachPos(box, pos -> {
						final StructureBlockInfo originalInfo = new StructureBlockInfo(pos, this.state, null);
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
								level.setBlock(finalPos, modifiedInfo.state(), 2);	
							}	
						}
					});
				}
			}
			
			for (DungeonStub stub : this.stubs)
			{
				// each stub has a local bounding box which is relative to the piece bounding box
				// i.e. if the box starts at 1,1,1, that is one xyz offset
				// from where the piece box would be if the piece started at 0,0,0
				// to get the absolute box, take the minimal corner of the absolute piece box
				// and offset the stub box by that much
				BoundingBox absoluteStubBox = MoreBoxing.moved(stub.box(), BoxBuddy.minCorner(pieceBox));
				DynamicJigsawFillContext stubFillContext = new DynamicJigsawFillContext(
					context.level(),
					context.structureManager(),
					context.chunkGenerator(),
					context.random(),
					context.chunkBoundingBox(),
					context.chunkPos(),
					context.startPieceFloorCenter(),
					absoluteStubBox,
					context.structureTemplateManager(),
					stub.rotation(),
					context.liquidSettings(),
					context.jigsawData());
				stub.pieceFiller().fill(stubFillContext);
			}
		}
		
	}
	
	public static record OccasionalBoxPool(Holder<BoxPool> pool, double chance)
	{
		public static final Codec<OccasionalBoxPool> CODEC = RecordCodecBuilder.create(builder -> builder.group(
				BoxPool.CODEC.fieldOf("pool").forGetter(OccasionalBoxPool::pool),
				Codec.doubleRange(0D, 1D).fieldOf("chance").forGetter(OccasionalBoxPool::chance)
			).apply(builder, OccasionalBoxPool::new));
	}
	
	public static record Exits(
		IntProvider count,
		Optional<Holder<BoxPool>> wallPool,
		Optional<Holder<BoxPool>> floorPool,
		Optional<Holder<BoxPool>> ceilingPool,
		Optional<Holder<BoxPool>> cornerPool)
	{
		public static final Codec<Exits> CODEC = RecordCodecBuilder.create(builder -> builder.group(
				IntProvider.CODEC.fieldOf("count").forGetter(Exits::count),
				BoxPool.CODEC.optionalFieldOf("wall_pool").forGetter(Exits::wallPool),
				BoxPool.CODEC.optionalFieldOf("floor_pool").forGetter(Exits::floorPool),
				BoxPool.CODEC.optionalFieldOf("ceiling_pool").forGetter(Exits::ceilingPool),
				BoxPool.CODEC.optionalFieldOf("corner_pool").forGetter(Exits::cornerPool)
			).apply(builder, Exits::new));
	}
	
	public static record DungeonProperty(
		int min,
		int max,
		int maxVariance)
	{
		public static final DungeonProperty SIMPLE = new DungeonProperty(0, 100, 10);
		public static final Codec<DungeonProperty> CODEC = RecordCodecBuilder.create(builder -> builder.group(
				Codec.INT.fieldOf("min").forGetter(DungeonProperty::min),
				Codec.INT.fieldOf("max").forGetter(DungeonProperty::max),
				Codec.INT.fieldOf("max_variance").forGetter(DungeonProperty::maxVariance)
			).apply(builder, DungeonProperty::new));
		
		public int nextValue(JigsawDataReader data, JigsawDataType<Integer> type, RandomSource random)
		{
			Integer currentData = data.getBranchData(type);
			if (currentData == null)
			{
				currentData = random.nextIntBetweenInclusive(this.min, this.max);
			}
			else if (currentData > this.max)
			{
				currentData = this.max;
			}
			else if (currentData < this.min)
			{
				currentData = this.min;
			}
			int randomMin = Math.max(this.min, currentData - this.maxVariance);
			int randomMax = Math.min(this.max, currentData + this.maxVariance);
			return random.nextIntBetweenInclusive(randomMin, randomMax);
		}
	}
}
