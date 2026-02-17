package net.commoble.dungeonfist.datagen;

import net.commoble.dungeonfist.DungeonFist;
import net.commoble.dungeonfist.DungeonMaterial;
import net.minecraft.core.RegistrySetBuilder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid=DungeonFist.MODID)
public class DungeonFistDataGen
{
	@SubscribeEvent
	public static void onGatherData(GatherDataEvent.Client event)
	{
		DungeonFistBlockStatesDataGen.gatherData(event);
		DungeonFistItemsDataGen.gatherData(event);
		DungeonFistModelsDataGen.gatherData(event);
		DungeonFistLangDataGen.gatherData(event);
		
		DungeonFistLootDataGen.gatherData(event);
		DungeonFistRecipesDataGen.gatherData(event);
		DungeonFistTagsDataGen.gatherData(event);

		event.createDatapackRegistryObjects(new RegistrySetBuilder()
			.add(DungeonMaterial.REGISTRY_KEY, DungeonFistDungeonMaterialDataGen::gatherData)
		);
	}
}
