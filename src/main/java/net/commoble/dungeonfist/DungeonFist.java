package net.commoble.dungeonfist;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.commoble.dungeonfist.attachment.PortalTimer;
import net.commoble.dungeonfist.block.AlertRuneBlock;
import net.commoble.dungeonfist.block.ChargedTntBlock;
import net.commoble.dungeonfist.block.DungeonPortalBlock;
import net.commoble.dungeonfist.block.PipeBlock;
import net.commoble.dungeonfist.block.PortalGeneratorBlock;
import net.commoble.dungeonfist.block.StatePredicates;
import net.commoble.dungeonfist.block.SummonRuneBlock;
import net.commoble.dungeonfist.block.TeleportRuneBlock;
import net.commoble.dungeonfist.block.entity.DungeonPortalBlockEntity;
import net.commoble.dungeonfist.block.entity.PortalGeneratorBlockEntity;
import net.commoble.dungeonfist.box_element.PillarBoxElement;
import net.commoble.dungeonfist.box_element.PillarBoxElement.PillarPieceFiller;
import net.commoble.dungeonfist.client.particle.DungeonPortalParticleOptions;
import net.commoble.dungeonfist.dynamic_jigsaw_element.DebugDynamicJigsawElement;
import net.commoble.dungeonfist.dynamic_jigsaw_element.DungeonRoomDynamicJigsawElement;
import net.commoble.dungeonfist.dynamic_jigsaw_element.DungeonRoomDynamicJigsawElement.DungeonRoomPieceFiller;
import net.commoble.dungeonfist.dynamic_jigsaw_element.SetDataDynamicJigsawElement;
import net.commoble.dungeonfist.dynamic_processor.AgeDynamicProcessor;
import net.commoble.dungeonfist.dynamic_processor.DungeonMaterialDynamicProcessor;
import net.commoble.dungeonfist.dynamic_processor.MoistenDynamicProcessor;
import net.commoble.dungeonfist.dynamic_processor.RandomizeDoorsDynamicProcessor;
import net.commoble.dungeonfist.item.ProvidenceItem;
import net.commoble.dungeonfist.pos_rule_test.HeightRangePosRuleTest;
import net.commoble.dungeonfist.rule_test.RandomRuleTest;
import net.commoble.dungeonfist.structure_placement.OriginStructurePlacement;
import net.commoble.dungeonfist.structure_processor.SetDataProcessor;
import net.commoble.structurebuddy.api.BoxElement;
import net.commoble.structurebuddy.api.DynamicJigsawElement;
import net.commoble.structurebuddy.api.DynamicProcessor;
import net.commoble.structurebuddy.api.JigsawDataType;
import net.commoble.structurebuddy.api.PieceFiller;
import net.commoble.structurebuddy.api.StructureBuddyRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.TicketType;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attribute.Sentiment;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

@Mod(DungeonFist.MODID)
public class DungeonFist
{	
	public static final String MODID = "dungeonfist";
	
	public static final TagKey<EntityType<?>> ITEM_FRAMES = TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("c", "item_frame"));
	public static final TagKey<Enchantment> PREVENTS_RUNE_TRIGGERING_WHEN_MINING = TagKey.create(Registries.ENCHANTMENT, id("prevents_rune_triggering_when_mining"));
	public static final TagKey<DimensionType> UNLOAD_WHILE_UNUSED = TagKey.create(Registries.DIMENSION_TYPE, id("unload_while_unused"));
	
	public static final DeferredRegister.Blocks BLOCKS = defreg(DeferredRegister::createBlocks);
	public static final DeferredRegister.Items ITEMS = defreg(DeferredRegister::createItems);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE = defreg(Registries.BLOCK_ENTITY_TYPE);
	public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = defreg(Registries.CREATIVE_MODE_TAB);
	public static final DeferredRegister<Attribute> ATTRIBUTES = defreg(Registries.ATTRIBUTE);
	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = defreg(Registries.PARTICLE_TYPE);
	public static final DeferredRegister<PoiType> POIS = defreg(Registries.POINT_OF_INTEREST_TYPE);
	public static final DeferredRegister<PosRuleTestType<?>> POS_RULE_TEST_TYPES = defreg(Registries.POS_RULE_TEST);
	public static final DeferredRegister<RuleTestType<?>> RULE_TESTS = defreg(Registries.RULE_TEST);
	public static final DeferredRegister<StructurePlacementType<?>> STRUCTURE_PLACEMENT_TYPES = defreg(Registries.STRUCTURE_PLACEMENT);
	public static final DeferredRegister<StructureProcessorType<?>> STRUCTURE_PROCESSORS = defreg(Registries.STRUCTURE_PROCESSOR);
	public static final DeferredRegister<TicketType> TICKET_TYPES = defreg(Registries.TICKET_TYPE);
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = defreg(NeoForgeRegistries.Keys.ATTACHMENT_TYPES);
	public static final DeferredRegister<MapCodec<? extends BoxElement>> BOX_ELEMENT_TYPES = defreg(StructureBuddyRegistries.BOX_ELEMENT_TYPE);
	public static final DeferredRegister<MapCodec<? extends DynamicJigsawElement>> DYNAMIC_JIGSAW_ELEMENT_TYPES = defreg(StructureBuddyRegistries.DYNAMIC_JIGSAW_ELEMENT_TYPE);
	public static final DeferredRegister<MapCodec<? extends DynamicProcessor>> DYNAMIC_PROCESSOR_TYPES = defreg(StructureBuddyRegistries.DYNAMIC_PROCESSOR_TYPE);
	public static final DeferredRegister<JigsawDataType<?>> JIGSAW_DATA_TYPES = defreg(StructureBuddyRegistries.JIGSAW_DATA_TYPE);
	public static final DeferredRegister<MapCodec<? extends PieceFiller>> PIECE_FILLER_TYPES = defreg(StructureBuddyRegistries.PIECE_FILLER_TYPE);
	
	/// map of original block id (e.g. cobblestone) to pipe block variant (e.g. cobblestone pipe)
	@SuppressWarnings("deprecation")
	public static final Map<ResourceKey<? extends Block>, DeferredBlock<? extends PipeBlock>> PIPE_BLOCKS = Util.make(new LinkedHashMap<>(), map -> {
		List<ResourceKey<? extends Block>> baseKeys = List.of(
			Blocks.COBBLESTONE.builtInRegistryHolder().key(),
			Blocks.STONE_BRICKS.builtInRegistryHolder().key(),
			Blocks.COBBLED_DEEPSLATE.builtInRegistryHolder().key()
		);
		for (var key : baseKeys)
		{
			map.put(key, registerSimpleBlockItem(
				depluralizeName(key.identifier().getPath()) + "_pipe",
				PipeBlock::new,
				() -> BlockBehaviour.Properties.ofFullCopy(BuiltInRegistries.BLOCK.getValueOrThrow(ResourceKey.create(Registries.BLOCK, key.identifier())))
					.isRedstoneConductor(StatePredicates::always)));
		}
	});

	/// map of original block id (e.g. cobblestone) to pressure plate block (e.g. cobblestone pressure plate)
	@SuppressWarnings("deprecation")
	public static final Map<ResourceKey<? extends Block>, DeferredBlock<? extends PressurePlateBlock>> PRESSURE_PLATE_BLOCKS = Util.make(new LinkedHashMap<>(), map -> {
		List<ResourceKey<? extends Block>> baseKeys = List.of(
			Blocks.COBBLESTONE.builtInRegistryHolder().key(),
			Blocks.STONE_BRICKS.builtInRegistryHolder().key(),
			Blocks.COBBLED_DEEPSLATE.builtInRegistryHolder().key()
		);
		for (var key : baseKeys)
		{
			map.put(key, registerSimpleBlockItem(
				depluralizeName(key.identifier().getPath()) + "_pressure_plate",
				props -> new PressurePlateBlock(BlockSetType.STONE, props),
				() -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_PRESSURE_PLATE)));
		}
	});

	public static final DeferredBlock<AlertRuneBlock> ALERT_RUNE = registerSimpleBlockItem(
		"alert_rune",
		AlertRuneBlock::new,
		() -> BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN)
			.randomTicks());

	public static final DeferredBlock<SummonRuneBlock> SUMMON_RUNE = registerSimpleBlockItem(
		"summon_rune",
		SummonRuneBlock::new,
		() -> BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN)
			.randomTicks());
	
	public static final DeferredBlock<ChargedTntBlock> CHARGED_TNT = registerSimpleBlockItem(
		"charged_tnt",
		ChargedTntBlock::new,
		() -> BlockBehaviour.Properties.ofFullCopy(Blocks.TNT));
	
	public static final DeferredBlock<TeleportRuneBlock> TELEPORT_RUNE = registerSimpleBlockItem(
		"teleport_rune",
		TeleportRuneBlock::new,
		() -> BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN));
	
	public static final DeferredBlock<DungeonPortalBlock> DUNGEON_PORTAL_BLOCK = BLOCKS.registerBlock(
		"dungeon_portal",
		props -> new DungeonPortalBlock(props, false),
		() -> BlockBehaviour.Properties.of()
			.noCollision()
			.noOcclusion()
            .strength(-1.0F, 3600000.0F)
            .noLootTable()
            .sound(SoundType.GLASS)
            .lightLevel(state -> 15)
            .pushReaction(PushReaction.BLOCK)
		);
	
	public static final DeferredBlock<PortalGeneratorBlock> PORTAL_GENERATOR_BLOCK = registerSimpleBlockItem(
		"portal_generator",
		PortalGeneratorBlock::new,
		() -> BlockBehaviour.Properties.ofFullCopy(Blocks.BEDROCK)
			.mapColor(MapColor.COLOR_GREEN) // match end portals
	);
	
	public static final DeferredBlock<DungeonPortalBlock> RETURN_PORTAL_BLOCK = BLOCKS.registerBlock(
		"return_portal",
		props -> new DungeonPortalBlock(props, true),
		() -> BlockBehaviour.Properties.of()
			.noCollision()
			.noOcclusion()
            .strength(-1.0F, 3600000.0F)
            .noLootTable()
            .sound(SoundType.GLASS)
            .lightLevel(state -> 15)
            .pushReaction(PushReaction.BLOCK)
		);
	
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DungeonPortalBlockEntity>> DUNGEON_PORTAL_BLOCK_ENTITY_TYPE = BLOCK_ENTITY_TYPE.register(
		"dungeon_portal",
		() -> new BlockEntityType<>(DungeonPortalBlockEntity::create,
			DUNGEON_PORTAL_BLOCK.get(),
			RETURN_PORTAL_BLOCK.get()
		));
	
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PortalGeneratorBlockEntity>> PORTAL_GENERATOR_BLOCK_ENTITY_TYPE = BLOCK_ENTITY_TYPE.register(
		"portal_generator",
		() -> new BlockEntityType<>(PortalGeneratorBlockEntity::create, PORTAL_GENERATOR_BLOCK.get()));
	
	public static final DeferredHolder<ParticleType<?>, ParticleType<DungeonPortalParticleOptions>> DUNGEON_PORTAL_PARTICLE_TYPE = PARTICLE_TYPES.register(
		"dungeon_portal",
		() -> new SimplerParticleType<>(false, DungeonPortalParticleOptions.CODEC, DungeonPortalParticleOptions.STREAM_CODEC));

	public static final DeferredHolder<PoiType,PoiType> RETURN_PORTAL_POI = POIS.register("return_portal", () -> new PoiType(
		RETURN_PORTAL_BLOCK.get().getStateDefinition().getPossibleStates().stream().collect(Collectors.toSet()), 0, 1));
	
	public static final DeferredItem<ProvidenceItem> PROVIDENCE_ITEM = ITEMS.registerItem(
		"providence",
		ProvidenceItem::new,
		props -> props
			.durability(64)
			.component(DataComponents.RARITY, Rarity.UNCOMMON)
			.component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
			.component(DataComponents.LORE, new ItemLore(List.of(Component.translatable("item.dungeonfist.providence.tooltip"))))
	);
	
	public static final DeferredHolder<Attribute,Attribute> SINKING_RESISTANCE = ATTRIBUTES.register("sinking_resistance", () -> new RangedAttribute("attribute.dungeonfist.name.sinking_resistance", 1D, 0D, 1D)
		.setSentiment(Sentiment.NEUTRAL)
		.setSyncable(true));
	
	public static final DeferredHolder<PosRuleTestType<?>, PosRuleTestType<HeightRangePosRuleTest>> HEIGHT_RANGE_POS_RULE_TEST = POS_RULE_TEST_TYPES.register("height_range", () -> () -> HeightRangePosRuleTest.CODEC);
	
	public static final DeferredHolder<RuleTestType<?>, RuleTestType<RandomRuleTest>> RANDOM_RULE_TEST = RULE_TESTS.register("random", () -> () -> RandomRuleTest.CODEC);
	
	public static final DeferredHolder<StructurePlacementType<?>, StructurePlacementType<OriginStructurePlacement>> ORIGIN_STRUCTURE_PLACEMENT_TYPE = STRUCTURE_PLACEMENT_TYPES.register("origin", () -> () -> OriginStructurePlacement.CODEC);
	
	public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<SetDataProcessor>> SET_DATA_PROCESSOR = STRUCTURE_PROCESSORS.register("set_data", () -> () -> SetDataProcessor.CODEC);
	
	public static final DeferredHolder<TicketType, TicketType> PORTAL_GENERATOR_TICKET = TICKET_TYPES.register("portal_generator", () -> new TicketType(
		300L,
		TicketType.FLAG_LOADING
	));
	
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<PortalTimer>> PORTAL_TIMER_ATTACHMENT = ATTACHMENT_TYPES.register("portal_timer", () -> AttachmentType.builder(() -> PortalTimer.DEFAULT)
		.serialize(PortalTimer.CODEC)
		.build());
	
	public static final DeferredHolder<JigsawDataType<?>, JigsawDataType<Holder<DungeonMaterial>>> DUNGEON_MATERIAL_JIGSAW_DATA = JIGSAW_DATA_TYPES.register("dungeon_material", () -> new JigsawDataType<>(DungeonMaterial.CODEC));
	public static final DeferredHolder<JigsawDataType<?>, JigsawDataType<Integer>> DUNGEON_AGE_JIGSAW_DATA = JIGSAW_DATA_TYPES.register("dungeon_age", () -> new JigsawDataType<>(Codec.INT));
	public static final DeferredHolder<JigsawDataType<?>, JigsawDataType<Integer>> DUNGEON_MOISTNESS_JIGSAW_DATA = JIGSAW_DATA_TYPES.register("dungeon_moistness", () -> new JigsawDataType<>(Codec.INT));
	
	public DungeonFist(IEventBus modBus)
	{
		CREATIVE_TABS.register(MODID, () -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.dungeonfist"))
			.icon(() -> new ItemStack(Items.IRON_BARS))
			.displayItems(ITEMS.getEntries())
			.build());
		
		DYNAMIC_JIGSAW_ELEMENT_TYPES.register("dungeon_room", () -> DungeonRoomDynamicJigsawElement.CODEC);
		DYNAMIC_JIGSAW_ELEMENT_TYPES.register("set_data", () -> SetDataDynamicJigsawElement.CODEC);
		DYNAMIC_JIGSAW_ELEMENT_TYPES.register("debug", () -> DebugDynamicJigsawElement.CODEC);
		
		DYNAMIC_PROCESSOR_TYPES.register("dungeon_material", () -> DungeonMaterialDynamicProcessor.CODEC);
		DYNAMIC_PROCESSOR_TYPES.register("age", () -> AgeDynamicProcessor.CODEC);
		DYNAMIC_PROCESSOR_TYPES.register("moisten", () -> MoistenDynamicProcessor.CODEC);
		DYNAMIC_PROCESSOR_TYPES.register("randomize_doors", () -> RandomizeDoorsDynamicProcessor.CODEC);
		
		BOX_ELEMENT_TYPES.register("pillar", () -> PillarBoxElement.CODEC);
		
		PIECE_FILLER_TYPES.register("dungeon_room", () -> DungeonRoomPieceFiller.CODEC);
		PIECE_FILLER_TYPES.register("pillar", () -> PillarPieceFiller.CODEC);
	}

	private static <T> DeferredRegister<T> defreg(ResourceKey<Registry<T>> registryKey)
	{
		return defreg(modid -> DeferredRegister.create(registryKey, modid));
	}
	
	private static <T, R extends DeferredRegister<T>> R defreg(Function<String, R> regFactory)
	{
		R register = regFactory.apply(MODID);
		register.register(ModList.get().getModContainerById(MODID).get().getEventBus());
		return register;
	}
	
	public static Identifier id(String path)
	{
		return Identifier.fromNamespaceAndPath(MODID, path);
	}
	
	private static <BLOCK extends Block> DeferredBlock<BLOCK> registerSimpleBlockItem(
		String name,
		Function<BlockBehaviour.Properties, BLOCK> blockFactory,
		Supplier<BlockBehaviour.Properties> blockProperties)
	{
		DeferredBlock<BLOCK> blockHolder = BLOCKS.registerBlock(name, blockFactory, blockProperties);
		ITEMS.registerSimpleBlockItem(blockHolder);
		return blockHolder;
	}
	
	private static String depluralizeName(String original)
	{
		if (original.endsWith("bricks"))
		{
			return original.replace("bricks", "brick");
		}
		
		return original;
	}

	
//	public static Identifier getIdentifier(String name)
//	{
//		return new Identifier(MODID, name);
//	}
//	
//	public static Optional<ClientProxy> clientProxy = Optional.ofNullable(DistExecutor.callWhenOn(Dist.CLIENT, () -> ClientProxy::new));
//	
//	// object names
//	public static final String[] ROCK_NAMES = {
//			"andesite", "basalt", "chert", "diorite",
//			"dolomite", "gabbro", "gneiss", "granite",
//			"halite", "limestone", "marble", "phyllite",
//			"rhyolite", "schist", "shale", "slate"
//	};
//	
//	public static final String[] IMPROVABLE_BLOCK_TYPES = {
//			"stone",
//			"smooth_stone",
//			"cobblestone",
//			"mossy_cobblestone",
//			"stone_bricks",
//			"mossy_stone_bricks",
//			"cracked_stone_bricks"
//	};
//	
//	public static final String CHISELED_STONE_BRICK = "chiseled_stone_brick";
//	
//	public static final String DUNGEON = "dungeon";
//	public static final String DUNGEON_PORTAL = "dungeon_portal";
//	public static final String DUNGEON_PORTAL_GLOWY_AIR = "dungeon_portal_glowy_air";
//	
//	public static final String CARCELITH = "carcelith";
}
