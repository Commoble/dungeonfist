package net.commoble.dungeonfist.datagen;

import java.util.function.UnaryOperator;

import net.commoble.dungeonfist.DungeonFist;
import net.commoble.dungeonfist.DungeonFistEnchantments;
import net.commoble.looot.data.Artifact;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.SetAttributesFunction;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetLoreFunction;
import net.minecraft.world.level.storage.loot.functions.SetNameFunction;
import net.minecraft.world.level.storage.loot.functions.SetNameFunction.Target;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public final class DungeonFistArtifactDataGen
{
	private DungeonFistArtifactDataGen() {}
	
	private static ResourceKey<LootPoolEntryContainer> key(String path)
	{
		return ResourceKey.create(Artifact.KEY, DungeonFist.id(path));
	}
	
	static void gatherData(BootstrapContext<LootPoolEntryContainer> bootstrap)
	{
		var enchantments = bootstrap.lookup(Registries.ENCHANTMENT);
		registerItem(bootstrap, Items.IRON_SPEAR, "archimedes_fulcrum", builder -> builder
			.apply(new SetEnchantmentsFunction.Builder()
				.withEnchantment(enchantments.getOrThrow(Enchantments.LUNGE), new ConstantValue(3))
				.withEnchantment(enchantments.getOrThrow(Enchantments.SHARPNESS), uniform(1,5))
				.withEnchantment(enchantments.getOrThrow(Enchantments.KNOCKBACK), uniform(2,3)))
		);
		registerItem(bootstrap, Items.IRON_BOOTS, "benthic_boots", builder -> builder
			.apply(new SetEnchantmentsFunction.Builder()
				.withEnchantment(enchantments.getOrThrow(Enchantments.DEPTH_STRIDER), new ConstantValue(3)))
			.apply(SetAttributesFunction.setAttributes()
				.withModifier(SetAttributesFunction.modifier(
					DungeonFist.id("benthic_boots_sinking_resistance"),
					DungeonFist.SINKING_RESISTANCE,
					Operation.ADD_MULTIPLIED_BASE,
					ConstantValue.exactly(-1F))
					.forSlot(EquipmentSlotGroup.ARMOR))
				.withModifier(SetAttributesFunction.modifier(
					DungeonFist.id("benthic_boots_gravity"),
					Attributes.GRAVITY,
					Operation.ADD_MULTIPLIED_BASE,
					ConstantValue.exactly(0.3F))
					.forSlot(EquipmentSlotGroup.ARMOR)))
		);
		registerItem(bootstrap, Items.LEATHER_HELMET, "catseye", builder -> builder
			.apply(new SetEnchantmentsFunction.Builder()
				.withEnchantment(enchantments.getOrThrow(DungeonFistEnchantments.NIGHT_VISION), ConstantValue.exactly(1)))
			.apply(SetComponentsFunction.setComponent(DataComponents.DYED_COLOR, new DyedItemColor(0x4042AA)))
		);
		registerItem(bootstrap, Items.CROSSBOW, "heckblaster", builder -> builder
			.apply(new SetEnchantmentsFunction.Builder()
				.withEnchantment(enchantments.getOrThrow(Enchantments.PUNCH), uniform(1,2))
				.withEnchantment(enchantments.getOrThrow(Enchantments.FLAME), ConstantValue.exactly(1))
				.withEnchantment(enchantments.getOrThrow(DungeonFistEnchantments.GRATUITOUS_VIOLENCE), uniform(1,3)))
			.apply(SetComponentsFunction.setComponent(DataComponents.ITEM_MODEL, DungeonFist.id("artifacts/heckblaster")))
		);
		registerItem(bootstrap, Items.STONE_SWORD, "insect_crush", builder -> builder
			.apply(new SetEnchantmentsFunction.Builder()
				.withEnchantment(enchantments.getOrThrow(Enchantments.BANE_OF_ARTHROPODS), uniform(4,5))
				.withEnchantment(enchantments.getOrThrow(Enchantments.SWEEPING_EDGE), uniform(1,3))
				.withEnchantment(enchantments.getOrThrow(Enchantments.UNBREAKING), uniform(1,3)))
		);
		registerItem(bootstrap, Items.GOLDEN_SWORD, "lightbringer", builder -> builder
			.apply(new SetEnchantmentsFunction.Builder()
				.withEnchantment(enchantments.getOrThrow(Enchantments.SMITE), uniform(1,5))
				.withEnchantment(enchantments.getOrThrow(DungeonFistEnchantments.LIGHTBRINGER), ConstantValue.exactly(1))
				.withEnchantment(enchantments.getOrThrow(Enchantments.UNBREAKING), uniform(1,3)))
		);
		registerItem(bootstrap, Items.GOLDEN_BOOTS, "moon_shoes", builder -> builder
			.apply(new SetEnchantmentsFunction.Builder()
				.withEnchantment(enchantments.getOrThrow(Enchantments.FEATHER_FALLING), new ConstantValue(1)))
			.apply(SetAttributesFunction.setAttributes()
				.withModifier(SetAttributesFunction
					.modifier(
						DungeonFist.id("moon_shoes_gravity"),
						Attributes.GRAVITY,
						Operation.ADD_MULTIPLIED_TOTAL,
						ConstantValue.exactly(-0.84F))
					.forSlot(EquipmentSlotGroup.ANY))
				.withModifier(SetAttributesFunction
					.modifier(
						DungeonFist.id("moon_shoes_fall_distance"),
						Attributes.SAFE_FALL_DISTANCE,
						Operation.ADD_MULTIPLIED_TOTAL,
						ConstantValue.exactly(5F))
					.forSlot(EquipmentSlotGroup.ANY)))
		);
		registerItem(bootstrap, Items.MACE, "newtons_hammer", builder -> builder
			.apply(new SetEnchantmentsFunction.Builder()
				.withEnchantment(enchantments.getOrThrow(Enchantments.BREACH), uniform(1,4))
				.withEnchantment(enchantments.getOrThrow(Enchantments.DENSITY), uniform(1,5))
				.withEnchantment(enchantments.getOrThrow(Enchantments.WIND_BURST), new ConstantValue(1)))
		);
		registerItem(bootstrap, Items.GOLDEN_CHESTPLATE, "prominence", builder -> builder
			.apply(new SetEnchantmentsFunction.Builder()
				.withEnchantment(enchantments.getOrThrow(Enchantments.THORNS), uniform(3,4))
				.withEnchantment(enchantments.getOrThrow(Enchantments.FIRE_PROTECTION), uniform(1,4))
				.withEnchantment(enchantments.getOrThrow(Enchantments.MENDING), ConstantValue.exactly(1)))
		);
	}
	
	public static void registerItem(BootstrapContext<LootPoolEntryContainer> bootstrap, ItemLike item, String path, UnaryOperator<LootPoolSingletonContainer.Builder<?>> builderBuilder)
	{
		var key = key(path);
		Identifier id = key.identifier();
		String translation = String.format("dungeonfist.artifact.%s.%s", id.getNamespace(), id.getPath());
		var builder = LootItem.lootTableItem(item);
		builder.apply(SetNameFunction.setName(Component.translatable(String.format("%s.name", translation)).setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD).withItalic(false)), Target.CUSTOM_NAME));
		builder.apply(SetLoreFunction.setLore().addLine(Component.translatable(String.format("%s.lore", translation))));
		builderBuilder.apply(builder);
		bootstrap.register(key, builder.build());
	}
	
	private static UniformGenerator uniform(int min, int max)
	{
		return new UniformGenerator(new ConstantValue(min), new ConstantValue(max));
	}
}
