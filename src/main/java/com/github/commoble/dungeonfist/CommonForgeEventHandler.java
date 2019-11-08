package com.github.commoble.dungeonfist;

import com.github.commoble.dungeonfist.registry.DimensionTypeRegistrar;

import net.minecraftforge.event.world.RegisterDimensionsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid=DungeonFist.MODID, bus=Bus.FORGE)
public class CommonForgeEventHandler
{		
	@SubscribeEvent
	public static void onRegisterDimensionsEvent(RegisterDimensionsEvent event)
	{
		DimensionTypeRegistrar.registerDimensionTypes();
	}
}
