package net.commoble.dungeonfist.datagen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.commoble.dungeonfist.DungeonFist;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredBlock;

public final class DungeonFistItemsDataGen
{
	static void gatherData(GatherDataEvent event)
	{
		Map<Identifier, ClientItem> clientItems = new HashMap<>();
		
		DungeonFist.PIPE_BLOCKS.forEach((originalKey, blockHolder) -> {
			Identifier id = blockHolder.unwrapKey().get().identifier();
			clientItems.put(id, defaultBlockModel(id));
		});
		DungeonFist.PRESSURE_PLATE_BLOCKS.forEach((originalKey, blockHolder) -> {
			Identifier id = blockHolder.unwrapKey().get().identifier();
			clientItems.put(id, defaultBlockModel(id));
		});
		registerDefaultBlockModel(clientItems, DungeonFist.ALERT_RUNE);
		registerSimpleBlockModel(clientItems, DungeonFist.CHARGED_TNT, Identifier.withDefaultNamespace("block/tnt"));
		registerDefaultBlockModel(clientItems, DungeonFist.SUMMON_RUNE);
		registerDefaultBlockModel(clientItems, DungeonFist.TELEPORT_RUNE);
		
		JsonDataProvider.addProvider(event, Target.RESOURCE_PACK, "items", ClientItem.CODEC, clientItems);
	}
	
	private static void registerDefaultBlockModel(Map<Identifier, ClientItem> clientItems, DeferredBlock<?> holder)
	{
		clientItems.put(holder.getId(), defaultBlockModel(holder.getId()));
	}
	
	private static void registerSimpleBlockModel(Map<Identifier, ClientItem> clientItems, DeferredBlock<?> holder, Identifier modelId)
	{
		clientItems.put(holder.getId(), simpleBlockModel(modelId));
	}
	
	private static ClientItem defaultBlockModel(Identifier blockId)
	{
		return simpleBlockModel(blockId.withPrefix("block/"));
	}
	
	private static ClientItem simpleBlockModel(Identifier modelId)
	{
		return new ClientItem(
			new BlockModelWrapper.Unbaked(modelId, List.of()),
			ClientItem.Properties.DEFAULT);
	}
}
