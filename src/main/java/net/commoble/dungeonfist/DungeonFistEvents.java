package net.commoble.dungeonfist;

import java.util.Objects;

import net.commoble.dungeonfist.attachment.PortalTimer;
import net.commoble.infiniverse.api.InfiniverseAPI;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

@EventBusSubscriber(modid=DungeonFist.MODID)
public class DungeonFistEvents
{
	@SubscribeEvent
	public static void onRegisterDataMapTypes(RegisterDataMapTypesEvent event)
	{
		event.register(DungeonFistDataMaps.AGEABLES);
		event.register(DungeonFistDataMaps.MOISTABLES);
	}
	
	@SubscribeEvent
	public static void onRegisterDataPackRegistries(DataPackRegistryEvent.NewRegistry event)
	{
		event.dataPackRegistry(DungeonMaterial.REGISTRY_KEY, DungeonMaterial.DIRECT_CODEC);
	}
	
	@SubscribeEvent
	public static void beforePlayerTick(PlayerTickEvent.Pre event)
	{
		Player player = event.getEntity();
		PortalTimer oldTimer = player.getData(DungeonFist.PORTAL_TIMER_ATTACHMENT);
		if (oldTimer.inPortal())
		{
			// if player is still inside block,
			// entityInside will set inPortal back to true,
			// and the post playertick will see player is still in block
			player.setData(DungeonFist.PORTAL_TIMER_ATTACHMENT, new PortalTimer(
				oldTimer.cooldownTimestamp(),
				oldTimer.portalTime(),
				false,
				oldTimer.portalPos()));
		}
	}
	
	@SubscribeEvent
	public static void afterPlayerTick(PlayerTickEvent.Post event)
	{
		Player player = event.getEntity();
		PortalTimer oldTimer = player.getData(DungeonFist.PORTAL_TIMER_ATTACHMENT);
		int portalTime = oldTimer.portalTime();
		if (!oldTimer.inPortal())
		{
			player.setData(DungeonFist.PORTAL_TIMER_ATTACHMENT, new PortalTimer(
				oldTimer.cooldownTimestamp(),
				Math.max(0, portalTime - 4),
				false,
				oldTimer.portalPos()));
		}
	}
	
	@SubscribeEvent
	public static void afterLevelTick(LevelTickEvent.Post event)
	{
		if (event.getLevel() instanceof ServerLevel serverLevel)
		{
			if (serverLevel.dimensionTypeRegistration().is(DungeonFist.REMOVE_WHILE_UNUSED))
			{
				if (serverLevel.getChunkSource().getLoadedChunksCount() == 0)
				{
					InfiniverseAPI.get().markDimensionForUnregistration(Objects.requireNonNull(serverLevel.getServer()), serverLevel.dimension());
				}
			}
		}
	}
}
