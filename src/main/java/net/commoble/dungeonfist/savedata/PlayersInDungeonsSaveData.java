package net.commoble.dungeonfist.savedata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.jspecify.annotations.Nullable;

import com.mojang.serialization.Codec;

import net.commoble.dungeonfist.DungeonFist;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

/// We unload dungeons that don't have loaded chunks.
/// But, on dedicated servers, players might leave and rejoin
/// and find that the dungeon they were in is unloaded.
/// If this happens, they will spawn elsewhere.
/// We would prefer this didn't happen.
/// 
/// So, keep track of what dungeons players are in,
/// and remember this even if players log out.
public class PlayersInDungeonsSaveData extends SavedData
{
	public static final Identifier ID = DungeonFist.id("return_point");
	public static final SavedDataType<PlayersInDungeonsSaveData> TYPE = new SavedDataType<>(ID, PlayersInDungeonsSaveData::create, PlayersInDungeonsSaveData::codec);
	public static final Codec<PlayersInDungeonsSaveData> CODEC = Codec.unboundedMap(UUIDUtil.STRING_CODEC, ResourceKey.codec(Registries.DIMENSION))
		.xmap(PlayersInDungeonsSaveData::new, PlayersInDungeonsSaveData::dungeonsByPlayer);
	
	private final Map<UUID, ResourceKey<Level>> dungeonsByPlayer;
	public Map<UUID, ResourceKey<Level>> dungeonsByPlayer() { return this.dungeonsByPlayer; }
	
	private Set<ResourceKey<Level>> keyCache;
	
	public static PlayersInDungeonsSaveData getOrCreate(MinecraftServer server)
	{
		return server.getDataStorage().computeIfAbsent(TYPE);
	}
	
	public PlayersInDungeonsSaveData(Map<UUID, ResourceKey<Level>> dungeonsByPlayer)
	{
		this.dungeonsByPlayer = new HashMap<>(dungeonsByPlayer);
		this.keyCache = new HashSet<>(dungeonsByPlayer.values());
	}
	
	public void setPlayerInDungeon(UUID uuid, ResourceKey<Level> dungeonKey)
	{
		var oldKey = this.dungeonsByPlayer.put(uuid, dungeonKey);
		if (oldKey != dungeonKey)
		{
			this.keyCache = new HashSet<>(dungeonsByPlayer.values());
			this.setDirty();
		}
	}
	
	public void setPlayerNotInDungeon(UUID uuid)
	{
		var oldKey = this.dungeonsByPlayer.remove(uuid);
		if (oldKey != null)
		{
			this.keyCache = new HashSet<>(dungeonsByPlayer.values());
			this.setDirty();
		}
	}
	
	public boolean keepDungeonLoaded(ResourceKey<Level> dungeonKey)
	{
		return this.keyCache.contains(dungeonKey);
	}
	
	public static Codec<PlayersInDungeonsSaveData> codec(@Nullable ServerLevel level)
	{
		return CODEC;
	}
	
	public static PlayersInDungeonsSaveData create(@Nullable ServerLevel level)
	{
		return new PlayersInDungeonsSaveData(new HashMap<>());
	}
}
