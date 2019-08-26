package com.github.commoble.dungeonfist.client;

import com.github.commoble.dungeonfist.DungeonFist;
import com.github.commoble.dungeonfist.RegistryHelper;

import net.minecraft.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid=DungeonFist.MODID, value=Dist.CLIENT, bus=Bus.MOD)
public class ClientModEventHandler
{
	@SubscribeEvent
	public static void onParticleTypeRegistryEvent(RegistryEvent.Register<ParticleType<?>> event)
	{
		ParticleTypeRegistrar.registerParticleTypes(new RegistryHelper<ParticleType<?>>(event.getRegistry()));
	}
	
	@SubscribeEvent
	public static void onClientSetupEvent(FMLClientSetupEvent event)
	{
		ParticleRegistrar.registerParticles();
	}
}
