package net.commoble.dungeonfist.datagen;

import java.util.Map;

import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;

public class VisibleMobSpawnSettingsBuilder extends MobSpawnSettings.Builder
{
	public Map<MobCategory, WeightedList.Builder<MobSpawnSettings.SpawnerData>> getSpawns()
	{
		return this.spawnsByCategory;
	}
	
	public Map<EntityType<?>, MobSpawnSettings.MobSpawnCost> getCosts()
	{
		return this.mobSpawnCosts;
	}
}
