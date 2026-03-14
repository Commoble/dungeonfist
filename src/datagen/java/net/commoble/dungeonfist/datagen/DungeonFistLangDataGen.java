package net.commoble.dungeonfist.datagen;

import java.util.List;

import org.apache.commons.lang3.text.WordUtils;

import net.commoble.dungeonfist.DungeonFist;
import net.commoble.dungeonfist.DungeonFistEnchantments;
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
				this.addArtifact("bloodreaper", "Bloodreaper", "From life, death. From death, life.");
				this.addArtifact("catseye", "Catseye", "The better to see you with.");
				this.addArtifact("heckblaster", "Heckblaster", "Wrath and fury, tightly bound.");
				this.addArtifact("insect_crush", "Insect Crush", "Seven in one blow.");
				this.addArtifact("lightbringer", "Lightbringer", "The unrighteous will be burned to ash.");
				this.addArtifact("moon_shoes", "Moon Shoes", "Unshackled from the grasping Earth.");
				this.addArtifact("morsmorde", "Morsmorde", "Immortal strength from mortal flesh.");
				this.addArtifact("newtons_hammer", "Newton's Hammer", "Actions beget consequence.");
				this.addArtifact("prominence", "Prominence", "He who sows embers shall reap wildfires.");
				this.addArtifact("the_undertaker", "The Undertaker", "The dead should remain buried.");
				this.addArtifact("thunderhead", "Thunderhead", "For great justice.");
				
				for (var key : List.of(
					DungeonFistEnchantments.DEATH_FEAST,
					DungeonFistEnchantments.FIRE_BLAST,
					DungeonFistEnchantments.FIRE_THORNS,
					DungeonFistEnchantments.LIFE_DRAIN,
					DungeonFistEnchantments.LIGHTBRINGER,
					DungeonFistEnchantments.NIGHT_VISION,
					DungeonFistEnchantments.STORM_CALLER))
				{
					this.addEnchantment(key.identifier().getPath());
				}
				
				this.add("effect.dungeonfist.storm_call", "Storm Call");
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
			
			private void addEnchantment(String path)
			{
				this.add(String.format("enchantment.dungeonfist.%s", path), getCleanName(path));
			}
			
		};
		event.addProvider(lang);
	}
	
	private static String getCleanName(Identifier id)
	{
		return getCleanName(id.getPath());
	}
	
	@SuppressWarnings("deprecation")
	private static String getCleanName(String path)
	{
		return WordUtils.capitalize(path.replace("_", " "));
	}
}
