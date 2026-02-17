package net.commoble.dungeonfist.datagen;

import java.util.HashMap;
import java.util.Map;

import net.commoble.dungeonfist.DungeonFist;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredBlock;

public final class DungeonFistModelsDataGen
{
	private DungeonFistModelsDataGen() {}
	
	private static final Identifier RUNE_TEMPLATE = DungeonFist.id("block/rune_template");
	private static final Identifier RUNE_TEMPLATE_CHARGED = DungeonFist.id("block/rune_template_charged");
	
	static void gatherData(GatherDataEvent event)
	{
		Map<Identifier, SimpleModel> models = new HashMap<>();
		
		DungeonFist.PIPE_BLOCKS.forEach((originalKey, blockHolder) -> {
			Identifier textureId = originalKey.identifier().withPrefix("block/");
			Identifier blockId = blockHolder.unwrapKey().get().identifier();
			Identifier modelId = blockId.withPrefix("block/");
			SimpleModel model = SimpleModel.create(DungeonFist.id("block/pipe_template"))
				.addTexture("all", textureId);
			models.put(modelId, model);
		});
		
		DungeonFist.PRESSURE_PLATE_BLOCKS.forEach((originalKey, blockHolder) -> {
			Identifier textureId = originalKey.identifier().withPrefix("block/");
			Identifier blockId = blockHolder.unwrapKey().get().identifier();
			Identifier modelId = blockId.withPrefix("block/");
			SimpleModel modelUp = SimpleModel.create(Identifier.withDefaultNamespace("block/pressure_plate_up"))
				.addTexture("texture", textureId);
			SimpleModel modelDown = SimpleModel.create(Identifier.withDefaultNamespace("block/pressure_plate_down"))
				.addTexture("texture", textureId);
			models.put(modelId, modelUp);
			models.put(modelId.withSuffix("_down"), modelDown);
		});

		registerChargedRuneBlock(models, DungeonFist.ALERT_RUNE, DungeonFist.id("block/alert_rune"));
		registerChargedRuneBlock(models, DungeonFist.SUMMON_RUNE, DungeonFist.id("block/summon_rune"));
		models.put(DungeonFist.TELEPORT_RUNE.getId().withPrefix("block/"), SimpleModel.create(RUNE_TEMPLATE_CHARGED)
			.addTexture("base", Identifier.withDefaultNamespace("block/obsidian"))
			.addTexture("overlay", DungeonFist.id("block/teleport_rune")));
		
		JsonDataProvider.addProvider(event, Target.RESOURCE_PACK, "models", SimpleModel.CODEC, models);
	}
	
	private static void registerChargedRuneBlock(Map<Identifier, SimpleModel> models, DeferredBlock<?> holder, Identifier overlayTexture)
	{
		models.put(holder.getId().withPrefix("block/"), SimpleModel.create(RUNE_TEMPLATE)
			.addTexture("base", Identifier.withDefaultNamespace("block/obsidian"))
			.addTexture("overlay", overlayTexture));
		models.put(holder.getId().withPrefix("block/").withSuffix("_charged"), SimpleModel.create(RUNE_TEMPLATE_CHARGED)
			.addTexture("base", Identifier.withDefaultNamespace("block/obsidian"))
			.addTexture("overlay", overlayTexture));
	}
}
