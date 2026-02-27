package net.commoble.dungeonfist.datagen;

import org.apache.commons.lang3.text.WordUtils;

import net.commoble.dungeonfist.DungeonFist;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

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
				this.addBlock(DungeonFist.ALERT_RUNE);
				this.add(DungeonFist.CHARGED_TNT.value(), "Charged TNT");
				this.addBlock(DungeonFist.PORTAL_GENERATOR_BLOCK);
				this.addBlock(DungeonFist.SUMMON_RUNE);
				this.addBlock(DungeonFist.TELEPORT_RUNE);
				
				this.addItem(DungeonFist.PROVIDENCE_ITEM);
				this.add("item.dungeonfist.providence.tooltip", "Those who dream of the sun will find it.");
			}
			
			private void addBlock(DeferredBlock<?> holder)
			{
				this.add(holder.value(), getCleanName(holder.getId()));
			}
			
			private void addItem(DeferredItem<?> holder)
			{
				this.add(holder.value(), getCleanName(holder.getId()));
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
