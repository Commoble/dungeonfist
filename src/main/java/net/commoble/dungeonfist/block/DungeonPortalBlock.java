package net.commoble.dungeonfist.block;

import java.util.Objects;
import java.util.Set;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import net.commoble.dungeonfist.DungeonFist;
import net.commoble.dungeonfist.attachments.PortalTimer;
import net.commoble.dungeonfist.block.entity.DungeonPortalBlockEntity;
import net.commoble.dungeonfist.client.ClientProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The primary portal blocks in the centers of portals
 */
public class DungeonPortalBlock extends Block implements EntityBlock
{
	public static final long COOLDOWN = 10L;
	
	protected final boolean returnPortal;
	public boolean returnPortal() { return this.returnPortal; }
	
	public DungeonPortalBlock(Block.Properties props, boolean returnPortal)
	{
		super(props);
		this.returnPortal = returnPortal;
	}

	@Override
	public @NonNull BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return DungeonFist.DUNGEON_PORTAL_BLOCK_ENTITY_TYPE.get().create(pos, state);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return type == DungeonFist.DUNGEON_PORTAL_BLOCK_ENTITY_TYPE.get() && level.isClientSide()
			? (BlockEntityTicker<T>)(ClientProxy.DUNGEON_PORTAL_TICKER)
			: null;
	}
	
	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
	{
		return Shapes.empty();
	}

	@Override
	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise)
	{
		super.entityInside(state, level, pos, entity, effectApplier, isPrecise);
		if (entity instanceof Player player && player.canUsePortal(false) && level.getBlockEntity(pos) instanceof DungeonPortalBlockEntity portal)
		{
			PortalTimer timer = player.getData(DungeonFist.PORTAL_TIMER_ATTACHMENT);
			int maxTicks = 80;
			long currentTime = level.getGameTime();
			int newTicks = Math.min(timer.portalTime() + 1, maxTicks);
			long cooldownTimestamp = timer.cooldownTimestamp();
			if (currentTime > cooldownTimestamp + COOLDOWN)
			{
				if (newTicks == 1 && level.isClientSide())
				{
					level.playSound(player, pos, SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 1F, 1F);
				}
				if (newTicks >= maxTicks)
				{
					if (level instanceof ServerLevel serverLevel)
					{
						MinecraftServer server = Objects.requireNonNull(serverLevel.getServer());
						ResourceKey<Level> targetKey = portal.targetLevel();
						BlockPos targetPortalPos = portal.targetPortalPos();
						BlockPos landingPos = targetPortalPos.below();
						@Nullable ServerLevel targetLevel = server.getLevel(targetKey);
						if (targetLevel != null)
						{
							Vec3 landingVec = Vec3.atBottomCenterOf(landingPos);
							double targetX = landingVec.x;
							double targetY = landingVec.y;
							double targetZ = landingVec.z;
							// ensure chunk is loaded
							targetLevel.getChunkAt(landingPos);
							player.teleportTo(targetLevel, targetX, targetY, targetZ, Set.of(), player.getYRot(), player.getXRot(), false);	
						}
					}
					player.setData(DungeonFist.PORTAL_TIMER_ATTACHMENT, new PortalTimer(
						currentTime,
						newTicks,
						true,
						pos.immutable()));
				}
				else
				{
					player.setData(DungeonFist.PORTAL_TIMER_ATTACHMENT, new PortalTimer(
						cooldownTimestamp,
						newTicks,
						true,
						pos.immutable()));
				}
			}
			else
			{
				player.setData(DungeonFist.PORTAL_TIMER_ATTACHMENT, new PortalTimer(
					currentTime,
					newTicks,
					true,
					pos.immutable()));
			}
		}
	}

	@Override
	protected RenderShape getRenderShape(BlockState state)
	{
		return RenderShape.INVISIBLE;
	}
}