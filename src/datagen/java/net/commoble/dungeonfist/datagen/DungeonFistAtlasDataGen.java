package net.commoble.dungeonfist.datagen;

import java.util.List;
import java.util.Map;

import net.commoble.dungeonfist.DungeonFist;
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.data.SpriteSourceProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public final class DungeonFistAtlasDataGen
{
	private DungeonFistAtlasDataGen() {}
	
	static void gatherData(GatherDataEvent event)
	{
		event.addProvider(new SpriteSourceProvider(event.getGenerator().getPackOutput(), event.getLookupProvider(), DungeonFist.MODID) {
			@Override
			protected void gather()
			{
				this.atlas(AtlasIds.ITEMS)
					.addSource(new PalettedPermutations(
						List.of(
							Identifier.withDefaultNamespace("item/crossbow_arrow"),
							Identifier.withDefaultNamespace("item/crossbow_firework"),
							Identifier.withDefaultNamespace("item/crossbow_pulling_0"),
							Identifier.withDefaultNamespace("item/crossbow_pulling_1"),
							Identifier.withDefaultNamespace("item/crossbow_pulling_2"),
							Identifier.withDefaultNamespace("item/crossbow_standby")
						),
						DungeonFist.id("palette_keys/item/crossbow"),
						Map.of("dungeonfist/heckblaster", DungeonFist.id("permutations/item/heckblaster")),
						"/"
					));
			}
		});
	}
}
