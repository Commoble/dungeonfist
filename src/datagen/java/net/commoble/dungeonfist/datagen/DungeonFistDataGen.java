package net.commoble.dungeonfist.datagen;

import net.commoble.dungeonfist.DungeonFist;
import net.commoble.dungeonfist.DungeonMaterial;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.data.event.GatherDataRegistryEntriesEvent;

@EventBusSubscriber(modid=DungeonFist.MODID)
public class DungeonFistDataGen
{
	@SubscribeEvent
	public static void onGatherData(GatherDataEvent.Client event)
	{
		
		DungeonFistAtlasDataGen.gatherData(event);
		DungeonFistBlockStatesDataGen.gatherData(event);
		DungeonFistItemsDataGen.gatherData(event);
		DungeonFistModelsDataGen.gatherData(event);
		DungeonFistLangDataGen.gatherData(event);
		
		DungeonFistTagsDataGen.gatherData(event);
	}
	
	@SubscribeEvent
	public static void onGatherRegistries(GatherDataRegistryEntriesEvent event)
	{
		event.add(DungeonMaterial.REGISTRY_KEY, DungeonFistDungeonMaterialDataGen::gatherData);
		event.add(Registries.LOOT_TABLE, DungeonFistLootDataGen::gatherData);
		event.add(Registries.RECIPE, DungeonFistRecipesDataGen::gatherData);
	}
}
