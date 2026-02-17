package net.commoble.dungeonfist;

import java.util.Map;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record DungeonMaterial(Map<Block,Block> materials, Supplier<Map<BlockState, BlockState>> stateLookup)
{
	public static final ResourceKey<Registry<DungeonMaterial>> REGISTRY_KEY = ResourceKey.createRegistryKey(DungeonFist.id("dungeon_material"));
	public static final Codec<DungeonMaterial> DIRECT_CODEC = Codec
		.unboundedMap(BuiltInRegistries.BLOCK.byNameCodec(), BuiltInRegistries.BLOCK.byNameCodec())
		.xmap(DungeonMaterial::new, DungeonMaterial::materials);
	public static final Codec<Holder<DungeonMaterial>> CODEC = RegistryFileCodec.create(REGISTRY_KEY, DIRECT_CODEC);
	public static final Codec<HolderSet<DungeonMaterial>> HOLDERSET_CODCE = HolderSetCodec.create(REGISTRY_KEY, CODEC, false);
	
	public DungeonMaterial(Map<Block,Block> materials)
	{
		this(materials, Suppliers.memoize(() -> expand(materials)));
	}
	
	private static final Map<BlockState,BlockState> expand(Map<Block, Block> blockMap)
	{
		Map<BlockState, BlockState> newMap = new Reference2ObjectOpenHashMap<>();
		for (var entry : blockMap.entrySet())
		{
			Block oldBlock = entry.getKey();
			Block newBlock = entry.getValue();
			var oldStates = oldBlock.getStateDefinition();
			for (BlockState oldState : oldStates.getPossibleStates())
			{
				BlockState newState = BlockMasher.copyProperties(oldState, newBlock.defaultBlockState());
				newMap.put(oldState, newState);
			}
		}
		
		return newMap;
	}
}
