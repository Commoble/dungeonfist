package net.commoble.dungeonfist.item;

import java.util.Objects;
import java.util.Set;

import org.jspecify.annotations.Nullable;

import net.commoble.dungeonfist.DungeonFist;
import net.commoble.dungeonfist.attachment.PortalTimer;
import net.commoble.dungeonfist.savedata.ReturnPointSaveData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class ProvidenceItem extends Item
{
	public ProvidenceItem(Properties properties)
	{
		super(properties);
	}
	
	

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand)
	{
		ItemStack stack = player.getItemInHand(hand);
        player.getCooldowns().addCooldown(stack, 60);
		if (level instanceof ServerLevel serverLevel)
		{
			MinecraftServer server = Objects.requireNonNull(serverLevel.getServer());
			@Nullable GlobalPos returnPoint = ReturnPointSaveData.getOrCreate(serverLevel).returnPoint().orElse(null);
			GlobalPos currentPos = new GlobalPos(serverLevel.dimension(), player.blockPosition());
			GlobalPos teleportTarget = currentPos;
			boolean actuallyTeleporting = false;
			
			// make sure portal cooldown is reset in case player lands in a portal
			PortalTimer oldTimer = player.getData(DungeonFist.PORTAL_TIMER_ATTACHMENT.get());
			player.setData(DungeonFist.PORTAL_TIMER_ATTACHMENT.get(), oldTimer.resetCooldown(serverLevel.getGameTime()));

			// if we're in a dungeonfist dimension with a known return point,
			// teleport the player back to the root dimension
			if (returnPoint != null)
			{
				teleportTarget = returnPoint;
			}
			// also teleport to the heightmap if there's no ceiling
			ResourceKey<Level> newDimension = teleportTarget.dimension();
			@Nullable ServerLevel newLevel = server.getLevel(newDimension);
			if (newLevel != null)
			{
				// make sure the heightmap is gettable
				newLevel.getChunk(teleportTarget.pos());
				if (!newLevel.dimensionType().hasCeiling())
				{
					teleportTarget = new GlobalPos(
						newDimension,
						newLevel.getHeightmapPos(
							Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
							teleportTarget.pos()));
				}
				// don't bother teleporting if target is too close
				BlockPos newPos = teleportTarget.pos();
				if (newDimension != serverLevel.dimension()
					|| player.blockPosition().distManhattan(newPos) > 3)
				{
					actuallyTeleporting = true;
					
					stack.hurtAndBreak(1, player, hand.asEquipmentSlot());
					Vec3 targetVec = Vec3.atBottomCenterOf(newPos);
					serverLevel.playSound(player, player.blockPosition(), SoundEvents.BELL_BLOCK, SoundSource.PLAYERS, 1F, 1F);
					player.teleportTo(newLevel, targetVec.x, targetVec.y, targetVec.z, Set.of(), player.getYRot(), player.getXRot(), true);
					newLevel.playSound(null, player.blockPosition(), SoundEvents.BELL_BLOCK, SoundSource.PLAYERS, 1F, 1F);
				}
			}
			if (!actuallyTeleporting)
			{
				serverLevel.playSound(null, player.blockPosition(), SoundEvents.WANDERING_TRADER_HURT, SoundSource.PLAYERS, 0.5F, 2F);
			}
		}
		
		return InteractionResult.SUCCESS;
	}
}
