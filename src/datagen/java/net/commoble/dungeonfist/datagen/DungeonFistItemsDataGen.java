package net.commoble.dungeonfist.datagen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.commoble.dungeonfist.DungeonFist;
import net.minecraft.client.color.item.Constant;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.properties.numeric.CrossbowPull;
import net.minecraft.client.renderer.item.properties.select.Charge;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CrossbowItem;
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
		registerDefaultBlockModel(clientItems, DungeonFist.PORTAL_GENERATOR_BLOCK);
		registerDefaultBlockModel(clientItems, DungeonFist.SUMMON_RUNE);
		registerDefaultBlockModel(clientItems, DungeonFist.TELEPORT_RUNE);
		
		clientItems.put(DungeonFist.PROVIDENCE_ITEM.getId(), new ClientItem(
			new BlockModelWrapper.Unbaked(
				Identifier.withDefaultNamespace("item/feather"),
				List.of(new Constant(0xFFFFF594))),
			ClientItem.Properties.DEFAULT));
		
		clientItems.put(DungeonFist.id("artifacts/bloodreaper"), new ClientItem(
			ItemModelUtils.plainModel(DungeonFist.id("item/artifacts/bloodreaper")),
			ClientItem.Properties.DEFAULT));

		clientItems.put(DungeonFist.id("artifacts/heckblaster"), new ClientItem(
			ItemModelUtils.select(
				new Charge(),
				ItemModelUtils.conditional(
					ItemModelUtils.isUsingItem(),
					ItemModelUtils.rangeSelect(
						new CrossbowPull(),
						ItemModelUtils.plainModel(DungeonFist.id("item/artifacts/heckblaster_pulling_0")),
						ItemModelUtils.override(ItemModelUtils.plainModel(DungeonFist.id("item/artifacts/heckblaster_pulling_1")), 0.58F),
						ItemModelUtils.override(ItemModelUtils.plainModel(DungeonFist.id("item/artifacts/heckblaster_pulling_2")), 1.0F)),
					ItemModelUtils.plainModel(DungeonFist.id("item/artifacts/heckblaster"))),
					ItemModelUtils.when(CrossbowItem.ChargeType.ARROW, ItemModelUtils.plainModel(DungeonFist.id("item/artifacts/heckblaster_arrow"))),
					ItemModelUtils.when(CrossbowItem.ChargeType.ROCKET, ItemModelUtils.plainModel(DungeonFist.id("item/artifacts/heckblaster_firework")))),
				ClientItem.Properties.DEFAULT));
		
		clientItems.put(DungeonFist.id("artifacts/morsmorde"), new ClientItem(
			ItemModelUtils.plainModel(DungeonFist.id("item/artifacts/morsmorde")),
			ClientItem.Properties.DEFAULT));
		
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
