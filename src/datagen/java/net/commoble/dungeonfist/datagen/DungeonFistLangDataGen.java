package net.commoble.dungeonfist.datagen;

import org.apache.commons.lang3.text.WordUtils;

import net.commoble.dungeonfist.DungeonFist;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
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
				this.addBlock(DungeonFist.FADING_LIGHT_BLOCK);
				this.addBlock(DungeonFist.PORTAL_GENERATOR_BLOCK);
				this.addBlock(DungeonFist.SUMMON_RUNE);
				this.addBlock(DungeonFist.TELEPORT_RUNE);
				
				this.addItem(DungeonFist.PROVIDENCE_ITEM);
				this.add("item.dungeonfist.providence.tooltip", "Those who dream of the sun will find it.");
				
				this.addAttribute(DungeonFist.SINKING_RESISTANCE);

				this.addArtifact("archimedes_fulcrum", "Archimedes's Fulcrum", "A man in the right place can move heaven and earth.");
				this.addArtifact("benthic_boots", "Benthic Boots", "What came from the sea will return to the sea.");
				this.addArtifact("catseye", "Catseye", "The better to see you with.");
				this.addArtifact("heckblaster", "Heckblaster", "Wrath and fury, tightly bound.");
				this.addArtifact("insect_crush", "Insect Crush", "Seven in one blow.");
				this.addArtifact("lightbringer", "Lightbringer", "The unrighteous will be burned to ash.");
				this.addArtifact("moon_shoes", "Moon Shoes", "Unshackled from the grasping Earth.");
				this.addArtifact("newtons_hammer", "Newton's Hammer", "Actions beget consequence.");
				this.addArtifact("prominence", "Prominence", "Those who fly too near the sun risk its ire.");

				this.add("dungeonfist.enchantment.gratuitous_violence", "Gratuitous Violence");
				this.add("dungeonfist.enchantment.lightbringer", "Lightbringer");
				this.add("dungeonfist.enchantment.night_vision", "Night Vision");
			}
			
			private void addBlock(DeferredBlock<?> holder)
			{
				this.add(holder.value(), getCleanName(holder.getId()));
			}
			
			private void addItem(DeferredItem<?> holder)
			{
				this.add(holder.value(), getCleanName(holder.getId()));
			}
			
			private void addAttribute(DeferredHolder<Attribute, ? extends Attribute> holder)
			{
				this.add(holder.value().getDescriptionId(), getCleanName(holder.getId()));
			}
			
			private void addArtifact(String path, String name, String lore)
			{
				String root = String.format("dungeonfist.artifact.%s.%s", DungeonFist.MODID, path);
				this.add(String.format("%s.name", root), name);
				this.add(String.format("%s.lore", root), lore);
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
