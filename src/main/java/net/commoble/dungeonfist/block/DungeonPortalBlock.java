package net.commoble.dungeonfist.block;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import net.commoble.dungeonfist.DungeonFist;
import net.commoble.dungeonfist.client.ClientProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The primary portal blocks in the centers of portals
 */
public class DungeonPortalBlock extends Block implements EntityBlock
{
	public DungeonPortalBlock(Block.Properties props)
	{
		super(props);
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
		return type == DungeonFist.DUNGEON_PORTAL_BLOCK_ENTITY_TYPE.get()
			? (BlockEntityTicker<T>)(level.isClientSide()
				? ClientProxy.DUNGEON_PORTAL_TICKER
				: DungeonPortalBlock.SERVER_TICKER)
			: null;
	}

//	/**
//	 * Called when the block is right clicked by a player.
//	 */
//	@Override
//	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn,
//			BlockRayTraceResult hit)
//	{
//		if (world.isRemote || !(player instanceof ServerPlayerEntity))
//		{
//			return true;
//		} else
//		{
//			ServerPlayerEntity mpPlayer = (ServerPlayerEntity) player;
//			if (player.getRidingEntity() == null && !player.isBeingRidden())
//			{
//				if (world.dimension.getType() == DimensionType.OVERWORLD)
//				{
//					BlockPos dungeonPos = PortalPlacementRules.getDungeonPortalPosFromOverworldPos(world.getSeed(), pos);
//					
//					DungeonTeleporter.teleportPlayer(mpPlayer, DimensionTypeRegistrar.getDungeonDimensionType(), dungeonPos);
//				} else	// going form dungeon to overworld
//				{
//					BlockPos overworldPos = PortalPlacementRules.getOverworldPortalPosFromDungeonPortalPos(world, pos);
//					
//					DungeonTeleporter.teleportPlayer(mpPlayer, DimensionType.OVERWORLD, overworldPos);
//				}
//				return true;
//			}
//		}
//		return false;
//	}
	
	@Override
	protected RenderShape getRenderShape(BlockState state)
	{
		return RenderShape.INVISIBLE;
	}
	
	private static final BlockEntityTicker<DungeonPortalBlockEntity> SERVER_TICKER = DungeonPortalBlock::serverTick;
	private static void serverTick(Level level, BlockPos pos, BlockState state, DungeonPortalBlockEntity be)
	{
		
	}
}