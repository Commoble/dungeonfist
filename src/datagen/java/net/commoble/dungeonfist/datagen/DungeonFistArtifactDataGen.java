package net.commoble.dungeonfist.datagen;

import java.util.function.UnaryOperator;

import net.commoble.dungeonfist.DungeonFist;
import net.commoble.dungeonfist.DungeonFistEnchantments;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
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
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.UniformContainerBase;
import net.minecraft.world.level.storage.loot.functions.SetAttributesFunction;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetLoreFunction;
import net.minecraft.world.level.storage.loot.functions.SetNameFunction;
import net.minecraft.world.level.storage.loot.functions.SetNameFunction.Target;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProvider;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
import net.minecraft.world.level.storage.loot.providers.number.ints.UniformGenerator;

public final class DungeonFistArtifactDataGen
{
	private DungeonFistArtifactDataGen() {}
	
	private static ResourceKey<LootTable> key(String path)
	{
		return ResourceKey.create(Registries.LOOT_TABLE, DungeonFist.id(path).withPrefix("artifacts/"));
	}
	
	static void gatherData(BootstrapContext<LootTable> bootstrap)
	{
		registerItem(bootstrap, Items.IRON_SPEAR, "archimedes_fulcrum", builder -> builder
			.apply(new SetEnchantmentsFunction.Builder()
				.withEnchantment(DummyHolder.of(Enchantments.LUNGE), constantInt(3))
				.withEnchantment(DummyHolder.of(Enchantments.SHARPNESS), uniform(1,5))
				.withEnchantment(DummyHolder.of(Enchantments.KNOCKBACK), uniform(2,3)))
		);
		registerItem(bootstrap, Items.IRON_BOOTS, "benthic_boots", builder -> builder
			.apply(new SetEnchantmentsFunction.Builder()
				.withEnchantment(DummyHolder.of(Enchantments.DEPTH_STRIDER), constantInt(3)))
			.apply(SetAttributesFunction.setAttributes()
				.withModifier(SetAttributesFunction.modifier(
					DungeonFist.id("benthic_boots_sinking_resistance"),
					DungeonFist.SINKING_RESISTANCE,
					Operation.ADD_MULTIPLIED_BASE,
					constantFloat(-1F))
					.forSlot(EquipmentSlotGroup.ARMOR))
				.withModifier(SetAttributesFunction.modifier(
					DungeonFist.id("benthic_boots_gravity"),
					Attributes.GRAVITY,
					Operation.ADD_MULTIPLIED_BASE,
					constantFloat(0.3F))
					.forSlot(EquipmentSlotGroup.ARMOR)))
		);
		registerItem(bootstrap, Items.IRON_HOE, "bloodreaper", builder -> builder
			.apply(new SetEnchantmentsFunction.Builder()
				.withEnchantment(DummyHolder.of(Enchantments.SHARPNESS), constantInt(5))
				.withEnchantment(DummyHolder.of(Enchantments.UNBREAKING), uniform(1,3))
				.withEnchantment(DummyHolder.of(DungeonFistEnchantments.LIFE_DRAIN), constantInt(1)))
			.apply(SetComponentsFunction.setComponent(DataComponents.ITEM_MODEL, DungeonFist.id("artifacts/bloodreaper")))
		);
		registerItem(bootstrap, Items.LEATHER_HELMET, "catseye", builder -> builder
			.apply(new SetEnchantmentsFunction.Builder()
				.withEnchantment(DummyHolder.of(DungeonFistEnchantments.NIGHT_VISION), constantInt(1)))
			.apply(SetComponentsFunction.setComponent(DataComponents.DYED_COLOR, new DyedItemColor(0x4042AA)))
		);
		registerItem(bootstrap, Items.CROSSBOW, "heckblaster", builder -> builder
			.apply(new SetEnchantmentsFunction.Builder()
				.withEnchantment(DummyHolder.of(Enchantments.PUNCH), uniform(1,2))
				.withEnchantment(DummyHolder.of(Enchantments.FLAME), constantInt(1))
				.withEnchantment(DummyHolder.of(DungeonFistEnchantments.FIRE_BLAST), uniform(1,3)))
			.apply(SetComponentsFunction.setComponent(DataComponents.ITEM_MODEL, DungeonFist.id("artifacts/heckblaster")))
		);
		registerItem(bootstrap, Items.STONE_SWORD, "insect_crush", builder -> builder
			.apply(new SetEnchantmentsFunction.Builder()
				.withEnchantment(DummyHolder.of(Enchantments.BANE_OF_ARTHROPODS), uniform(4,5))
				.withEnchantment(DummyHolder.of(Enchantments.SWEEPING_EDGE), uniform(1,3))
				.withEnchantment(DummyHolder.of(Enchantments.UNBREAKING), uniform(1,3)))
		);
		registerItem(bootstrap, Items.GOLDEN_SWORD, "lightbringer", builder -> builder
			.apply(new SetEnchantmentsFunction.Builder()
				.withEnchantment(DummyHolder.of(Enchantments.SMITE), uniform(1,5))
				.withEnchantment(DummyHolder.of(DungeonFistEnchantments.LIGHTBRINGER), constantInt(1))
				.withEnchantment(DummyHolder.of(Enchantments.UNBREAKING), uniform(1,3)))
		);
		registerItem(bootstrap, Items.GOLDEN_BOOTS, "moon_shoes", builder -> builder
			.apply(new SetEnchantmentsFunction.Builder()
				.withEnchantment(DummyHolder.of(Enchantments.FEATHER_FALLING), constantInt(1)))
			.apply(SetAttributesFunction.setAttributes()
				.withModifier(SetAttributesFunction
					.modifier(
						DungeonFist.id("moon_shoes_gravity"),
						Attributes.GRAVITY,
						Operation.ADD_MULTIPLIED_TOTAL,
						constantFloat(-0.84F))
					.forSlot(EquipmentSlotGroup.ANY))
				.withModifier(SetAttributesFunction
					.modifier(
						DungeonFist.id("moon_shoes_fall_distance"),
						Attributes.SAFE_FALL_DISTANCE,
						Operation.ADD_MULTIPLIED_TOTAL,
						constantFloat(5F))
					.forSlot(EquipmentSlotGroup.ANY)))
		);
		registerItem(bootstrap, Items.DIAMOND_AXE, "morsmorde", builder -> builder
			.apply(new SetEnchantmentsFunction.Builder()
				.withEnchantment(DummyHolder.of(Enchantments.SHARPNESS), uniform(1,5))
				.withEnchantment(DummyHolder.of(DungeonFistEnchantments.DEATH_FEAST), uniform(1,4)))
			.apply(SetComponentsFunction.setComponent(DataComponents.ITEM_MODEL, DungeonFist.id("artifacts/morsmorde")))
		);
		registerItem(bootstrap, Items.MACE, "newtons_hammer", builder -> builder
			.apply(new SetEnchantmentsFunction.Builder()
				.withEnchantment(DummyHolder.of(Enchantments.BREACH), uniform(1,4))
				.withEnchantment(DummyHolder.of(Enchantments.DENSITY), uniform(1,5))
				.withEnchantment(DummyHolder.of(Enchantments.WIND_BURST), constantInt(1)))
		);
		registerItem(bootstrap, Items.GOLDEN_CHESTPLATE, "prominence", builder -> builder
			.apply(new SetEnchantmentsFunction.Builder()
				.withEnchantment(DummyHolder.of(DungeonFistEnchantments.FIRE_THORNS), uniform(1,4))
				.withEnchantment(DummyHolder.of(Enchantments.FIRE_PROTECTION), uniform(1,4))
				.withEnchantment(DummyHolder.of(Enchantments.MENDING), constantInt(1)))
		);
		registerItem(bootstrap, Items.IRON_SHOVEL, "the_undertaker", builder -> builder
			.apply(new SetEnchantmentsFunction.Builder()
				.withEnchantment(DummyHolder.of(Enchantments.SMITE), uniform(4,5))
				.withEnchantment(DummyHolder.of(Enchantments.EFFICIENCY), uniform(1,5))
				.withEnchantment(DummyHolder.of(Enchantments.UNBREAKING), uniform(1,3)))
		);
		registerItem(bootstrap, Items.GOLDEN_AXE, "thunderhead", builder -> builder
			.apply(new SetEnchantmentsFunction.Builder()
				.withEnchantment(DummyHolder.of(DungeonFistEnchantments.STORM_CALLER), uniform(1,3))
				.withEnchantment(DummyHolder.of(Enchantments.UNBREAKING), uniform(1,3)))
		);
	}
	
	public static void registerItem(BootstrapContext<LootTable> bootstrap, ItemLike item, String path, UnaryOperator<UniformContainerBase.Builder<?>> builderBuilder)
	{
		var key = key(path);
		Identifier id = key.identifier();
		String translation = String.format("dungeonfist.artifact.%s.%s", id.getNamespace(), id.getPath());
		var builder = LootItem.lootTableItem(item);
		builder.apply(SetNameFunction.setName(Component.translatable(String.format("%s.name", translation)).setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD).withItalic(false)), Target.CUSTOM_NAME));
		builder.apply(SetLoreFunction.setLore().addLine(Component.translatable(String.format("%s.lore", translation))));
		builderBuilder.apply(builder);		
		bootstrap.register(key, LootTable.lootTable()
			.withPool(LootPool.lootPool()
				.add(builder))
			.build());
	}
	
	private static Holder<ContextIntProvider> constantInt(int n)
	{
		return ContextIntProviders.exactly(n);
	}
	
	private static Holder<ContextFloatProvider> constantFloat(float f)
	{
		return ContextFloatProviders.exactly(f);
	}
	
	private static Holder<ContextIntProvider> uniform(int min, int max)
	{
		return Holder.direct(new UniformGenerator(constantInt(min), constantInt(max)));
	}
}
