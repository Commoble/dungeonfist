package net.commoble.dungeonfist;

import java.util.function.Function;

import net.commoble.dungeonfist.block.DungeonPortalBlock;
import net.commoble.dungeonfist.block.DungeonPortalBlockEntity;
import net.commoble.dungeonfist.client.particle.DungeonPortalParticleOptions;
import net.commoble.dungeonfist.structure.DynamicJigsawStructure;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(DungeonFist.MODID)
public class DungeonFist
{	
	public static final String MODID = "dungeonfist";
	
	public static final DeferredRegister.Blocks BLOCKS = defreg(DeferredRegister::createBlocks);
	public static final DeferredRegister.Items ITEMS = defreg(DeferredRegister::createItems);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE = defreg(Registries.BLOCK_ENTITY_TYPE);
	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = defreg(Registries.PARTICLE_TYPE);
	public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = defreg(Registries.STRUCTURE_TYPE);
	
	public static final DeferredBlock<DungeonPortalBlock> DUNGEON_PORTAL_BLOCK = BLOCKS.registerBlock(
		"dungeon_portal",
		DungeonPortalBlock::new,
		BlockBehaviour.Properties.of()
			.noCollission()
			.noOcclusion()
            .strength(-1.0F, 3600000.0F)
            .noLootTable()
            .sound(SoundType.GLASS)
            .lightLevel(state -> 15)
            .pushReaction(PushReaction.BLOCK)
		);
	
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DungeonPortalBlockEntity>> DUNGEON_PORTAL_BLOCK_ENTITY_TYPE = BLOCK_ENTITY_TYPE.register(
		"dungeon_portal",
		() -> new BlockEntityType<>(DungeonPortalBlockEntity::create, DUNGEON_PORTAL_BLOCK.get()));
	
	public static final DeferredHolder<ParticleType<?>, ParticleType<DungeonPortalParticleOptions>> DUNGEON_PORTAL_PARTICLE_TYPE = PARTICLE_TYPES.register(
		"dungeon_portal",
		() -> new SimplerParticleType<>(false, DungeonPortalParticleOptions.CODEC, DungeonPortalParticleOptions.STREAM_CODEC));

	public static final DeferredHolder<StructureType<?>, StructureType<DynamicJigsawStructure>> DYNAMIC_JIGSAW_STRUCTURE_TYPE = STRUCTURE_TYPES.register(
		"dynamic_jigsaw",
		() -> () -> DynamicJigsawStructure.CODEC);
	
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
	
	public static ResourceLocation id(String path)
	{
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}
//	public static ResourceLocation getResourceLocation(String name)
//	{
//		return new ResourceLocation(MODID, name);
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
