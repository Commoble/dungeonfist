package com.github.commoble.dungeonfist.world;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

public class DungeonBiome extends Biome
{
	public DungeonBiome()
	{
		super((new Biome.Builder()).surfaceBuilder(SurfaceBuilder.NOPE, SurfaceBuilder.AIR_CONFIG).precipitation(Biome.RainType.NONE).category(Biome.Category.NONE).depth(0.1F).scale(0.2F).temperature(1.0F).downfall(0.0F).waterColor(4159204).waterFogColor(329011).parent((String)null));
		// add structures
		// add carver
		// add features
		// add mob spawns
	}

}
