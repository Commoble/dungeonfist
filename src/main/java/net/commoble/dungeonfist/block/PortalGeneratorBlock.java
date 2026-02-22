package net.commoble.dungeonfist.block;

import org.jspecify.annotations.Nullable;

import net.commoble.dungeonfist.DimensionHelper;
import net.commoble.dungeonfist.DungeonFist;
import net.commoble.dungeonfist.block.entity.DungeonPortalBlockEntity;
import net.commoble.dungeonfist.block.entity.PortalGeneratorBlockEntity;
import net.commoble.dungeonfist.client.ClientProxy;
import net.commoble.dungeonfist.savedata.ReturnPointSaveData;
import net.commoble.infiniverse.api.InfiniverseAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.ai.village.poi.PoiManager.Occupancy;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.TeleportRandomlyConsumeEffect;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.Vec3;

public class PortalGeneratorBlock extends Block implements EntityBlock
{
	public static final BooleanProperty OPENING = BooleanProperty.create("opening");

	public PortalGeneratorBlock(Properties properties)
	{
		super(properties);
		this.registerDefaultState(this.defaultBlockState()
			.setValue(OPENING, false)
		);
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		super.createBlockStateDefinition(builder);
		builder.add(OPENING);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState)
	{
		return DungeonFist.PORTAL_GENERATOR_BLOCK_ENTITY_TYPE.get().create(worldPosition, blockState);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type)
	{
		return blockState.getValue(OPENING)
			&& type == DungeonFist.PORTAL_GENERATOR_BLOCK_ENTITY_TYPE.get()
			? level.isClientSide()
				? (BlockEntityTicker<T>)ClientProxy.PORTAL_GENERATOR_TICKER
				: (BlockEntityTicker<T>)(BlockEntityTicker<PortalGeneratorBlockEntity>)(this::serverTick)
			: null;
	}
	
	

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean movedByPiston)
	{
		super.neighborChanged(state, level, pos, block, orientation, movedByPiston);
		if (!level.isClientSide())
		{
			boolean hasPower = level.getBestNeighborSignal(pos) > 0;
			if (hasPower && !state.getValue(OPENING) && !level.getBlockState(pos.above(2)).is(DungeonFist.DUNGEON_PORTAL_BLOCK))
			{
				level.playSound(null, pos, SoundEvents.BELL_BLOCK, SoundSource.BLOCKS, 2F, 0.5F);
				level.playSound(null, pos, SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.BLOCKS, 1F,1F);
				level.playSound(null, pos, SoundEvents.BELL_RESONATE, SoundSource.BLOCKS, 1F, 0.6F);
				level.setBlock(pos, state.setValue(OPENING, true), Block.UPDATE_ALL);
			}
			else if (!hasPower && state.getValue(OPENING))
			{
				level.setBlock(pos, state.setValue(OPENING, false), Block.UPDATE_ALL);
			}
		}
	}

	protected void serverTick(final Level level, final BlockPos pos, final BlockState state, final PortalGeneratorBlockEntity entity)
	{
		if (!(level instanceof ServerLevel serverLevel))
			return;
		
		@Nullable MinecraftServer server = serverLevel.getServer();
		if (server == null)
			return;
		
		// we need to create a consistent random seed from:
		// * the current position (so the block at some pos in a given level always generates the same portal)
		// * the level id (so a block at the same pos in overworld+nether don't generate the same portal)
		// * the level seed (so blocks in different seeds don't generate the same portal)
		BlockPos seedPos = pos.above(2); // the portal pos
		@SuppressWarnings("deprecation")
		long newSeed = serverLevel.getSeed() + Mth.getSeed(seedPos) + level.dimension().identifier().hashCode();
		ResourceKey<Level> newLevelKey = entity.getOrCreateLevelKey(serverLevel, newSeed);
		ServerLevel newLevel = DimensionHelper.getOrCreateDungeonLevel(server, newLevelKey, newSeed);
		
		newLevel.getChunkSource().addTicketWithRadius(DungeonFist.PORTAL_GENERATOR_TICKET.get(), ChunkPos.ZERO, 2);
		@Nullable ChunkAccess chunk = getChunkIfLoaded(newLevel, 0, 0);
		if (chunk != null)
		{
			level.playSound(null, pos, SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 1F,1F);
			@Nullable PoiRecord targetPoi = newLevel.getPoiManager().getInSquare(poi -> DungeonFist.RETURN_PORTAL_POI.equals(poi), BlockPos.ZERO, 16, Occupancy.ANY).findFirst().orElse(null);
			if (targetPoi == null)
			{
				// we failed, delete this portal and unload dungeon
				level.setBlock(pos, Blocks.REINFORCED_DEEPSLATE.defaultBlockState(), Block.UPDATE_ALL);
				InfiniverseAPI.get().markDimensionForUnregistration(server, newLevelKey);
				// summon some endermen too
				// roll twice and take higher result
				RandomSource random = level.getRandom();
				int tries = Math.max(
					random.nextIntBetweenInclusive(1, 4),
					random.nextIntBetweenInclusive(1, 4));

				@Nullable Player nearestPlayer = serverLevel.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 10D, true);
				if (nearestPlayer != null)
				{
					for (int i=0; i<tries; i++)
					{
						@Nullable EnderMan enderman = EntityType.ENDERMAN.spawn(serverLevel, pos, EntitySpawnReason.MOB_SUMMONED);
						if (enderman != null
							&& new TeleportRandomlyConsumeEffect(5).apply(level, new ItemStack(this), enderman))
						{
							enderman.setTarget(nearestPlayer);
						}
						else if (enderman != null)
						{
							enderman.discard();
						}
					}
				}
			}
			else
			{
				serverLevel.setBlock(pos, state.setValue(OPENING, false), Block.UPDATE_ALL);
				serverLevel.setBlock(seedPos, DungeonFist.DUNGEON_PORTAL_BLOCK.get().defaultBlockState(), Block.UPDATE_ALL);
				BlockPos targetPortalPos = targetPoi.getPos().immutable();
				var currentLevelKey = serverLevel.dimension();
				if (serverLevel.getBlockEntity(seedPos) instanceof DungeonPortalBlockEntity portal)
				{
					portal.setTarget(newLevelKey, targetPortalPos, newSeed);
				}
				@Nullable GlobalPos currentReturnPoint = ReturnPointSaveData.getOrCreate(serverLevel).returnPoint().orElse(null);
				ReturnPointSaveData newLevelReturnPoint = ReturnPointSaveData.getOrCreate(newLevel);
				if (newLevel.getBlockEntity(targetPortalPos) instanceof DungeonPortalBlockEntity portal)
				{
					// if current level is a dungeon dimension
					// then we need to add its seed to the return portal
					// so it can reconstitute this level if needed
					// how do we know whether this is a dungeon dimension?
					// check savedata, we only store a return point in dungeon dimensions
					@Nullable Long returnSeed = currentReturnPoint == null
						? null
						: serverLevel.getSeed();
					portal.setTarget(currentLevelKey, seedPos, returnSeed);
				}
				if (currentReturnPoint == null)
				{
					// if parent is a root level, set child's return point to parent portal
					newLevelReturnPoint.setReturnPoint(currentLevelKey, seedPos);	
				}
				else
				{
					// otherwise, set child's return point equal to parent's return point
					newLevelReturnPoint.setReturnPoint(currentReturnPoint);
				}
			}
			LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(serverLevel, EntitySpawnReason.EVENT);
			if (bolt != null)
			{
				bolt.setVisualOnly(true);
				bolt.snapTo(Vec3.atBottomCenterOf(pos.above()));
				serverLevel.addFreshEntity(bolt);
			}
		}
	}

	private static @Nullable ChunkAccess getChunkIfLoaded(ServerLevel level, int chunkX, int chunkZ)
	{
		ServerChunkCache cache = level.getChunkSource();
		
		// even with loadOrGenerate=false, getChunk can block the server thread for some reason
		// so, check if the future is actually done or not before we get the chunk
		return cache.getChunkFutureMainThread(chunkX, chunkZ, ChunkStatus.FULL, false).isDone()
			? level.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false)
			: null;
	}
}
