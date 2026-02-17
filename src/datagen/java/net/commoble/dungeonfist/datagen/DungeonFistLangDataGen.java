package net.commoble.dungeonfist.datagen;

import org.apache.commons.lang3.text.WordUtils;

import net.commoble.dungeonfist.DungeonFist;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public final class DungeonFistLangDataGen
{
	private DungeonFistLangDataGen() {}
	
	static void gatherData(GatherDataEvent event)
	{
		LanguageProvider lang = new LanguageProvider(event.getGenerator().getPackOutput(), DungeonFist.MODID, "en_us")
		{
			@Override
			protected void addTranslations()
			{
				this.add("itemGroup.dungeonfist", "Dungeonfist");
				
				DungeonFist.PIPE_BLOCKS.forEach((originalKey, blockHolder) -> {
					this.add(blockHolder.value(), getCleanName(blockHolder.unwrapKey().get().identifier()));
				});
				DungeonFist.PRESSURE_PLATE_BLOCKS.forEach((originalKey, blockHolder) -> {
					this.add(blockHolder.value(), getCleanName(blockHolder.unwrapKey().get().identifier()));
				});
				this.add(DungeonFist.ALERT_RUNE.value(), "Alert Rune");
				this.add(DungeonFist.CHARGED_TNT.value(), "Charged TNT");
				this.add(DungeonFist.SUMMON_RUNE.value(), "Summon Rune");
				this.add(DungeonFist.TELEPORT_RUNE.value(), "Teleport Rune");
			}
		};
		event.addProvider(lang);
	}
	
	@SuppressWarnings("deprecation")
	private static String getCleanName(Identifier id)
	{
		return WordUtils.capitalize(id.getPath().replace("_", " "));
	}
}
