package net.commoble.dungeonfist;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

import net.commoble.dungeonfist.attachment.PortalTimer;
import net.commoble.dungeonfist.savedata.PlayersInDungeonsSaveData;
import net.commoble.infiniverse.api.InfiniverseAPI;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
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
		event.dataPackRegistry(Artifact.KEY, LootPoolEntries.CODEC);
		event.dataPackRegistry(DungeonMaterial.REGISTRY_KEY, DungeonMaterial.DIRECT_CODEC);
	}
	
	@SubscribeEvent
	public static void onModifyEntityAttributes(EntityAttributeModificationEvent event)
	{
		for (var type : event.getTypes())
		{
			event.add(type, DungeonFist.SINKING_RESISTANCE);
		}
	}
	
	@SubscribeEvent
	public static void onRegisterCommands(RegisterCommandsEvent event)
	{
		event.getDispatcher().register(Commands.literal(DungeonFist.MODID)
			.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
			.then(Commands.literal("give_artifact")
				.then(Commands.argument("targets", EntityArgument.players())
					.then(Commands.argument("artifact", ResourceKeyArgument.key(Artifact.KEY))
						.executes(context -> {
							CommandSourceStack source = context.getSource();
							var err = new DynamicCommandExceptionType(value -> Component.translatableEscape("commands.dungeonfist.artifact.invalid", value));
							ResourceKey<LootPoolEntryContainer> key = ResourceKeyArgument.getRegistryKey(context, "artifact", Artifact.KEY, err);
							LootPoolEntryContainer container = context
								.getSource()
								.registryAccess()
								.lookupOrThrow(Artifact.KEY)
								.get(key)
								.orElseThrow(() -> err.create(key.identifier()))
								.value();
							Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
							for (ServerPlayer serverPlayer : players)
							{
								LootParams lootParams = new LootParams.Builder(source.getLevel())
									.withOptionalParameter(LootContextParams.THIS_ENTITY, source.getEntity())
									.withParameter(LootContextParams.ORIGIN, source.getPosition())
									.create(LootContextParamSets.COMMAND);
								LootContext lootContext = new LootContext.Builder(lootParams)
									.withOptionalRandomSource(source.getLevel().getRandom())
									.create(Optional.empty());
								container.expand(lootContext, entry -> {
									entry.createItemStack(stack -> {
										boolean added = serverPlayer.getInventory().add(stack.copy());
										source.sendSuccess(() -> Component.translatable("commands.give.success.single",
											added ? stack.count() : 0,
											stack.getDisplayName(),
											serverPlayer.getDisplayName()),
											false);
									}, lootContext);
								});
							}
							return players.size();
						})))));
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
		
		if (player.level() instanceof ServerLevel serverLevel)
		{
			MinecraftServer server = Objects.requireNonNull(serverLevel.getServer());
			var dungeons = PlayersInDungeonsSaveData.getOrCreate(server);
			if (serverLevel.dimensionTypeRegistration().is(DungeonFist.UNLOAD_WHILE_UNUSED))
			{
				dungeons.setPlayerInDungeon(player.getUUID(), serverLevel.dimension());
			}
			else
			{
				dungeons.setPlayerNotInDungeon(player.getUUID());
			}
		}
	}
	
	@SubscribeEvent
	public static void afterLevelTick(LevelTickEvent.Post event)
	{
		if (event.getLevel() instanceof ServerLevel serverLevel)
		{
			if (serverLevel.dimensionTypeRegistration().is(DungeonFist.UNLOAD_WHILE_UNUSED))
			{
				if (serverLevel.getChunkSource().getLoadedChunksCount() == 0)
				{
					MinecraftServer server = Objects.requireNonNull(serverLevel.getServer());
					ResourceKey<Level> dungeonKey = serverLevel.dimension();
					if (!PlayersInDungeonsSaveData.getOrCreate(server).keepDungeonLoaded(dungeonKey))
					{
						InfiniverseAPI.get().markDimensionForUnregistration(server, dungeonKey);
					}
				}
			}
		}
	}
}
