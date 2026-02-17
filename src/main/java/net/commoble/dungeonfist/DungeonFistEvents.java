package net.commoble.dungeonfist;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
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
}
