package net.commoble.dungeonfist.datagen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.commoble.dungeonfist.DungeonFist;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public final class DungeonFistRecipesDataGen
{
	private DungeonFistRecipesDataGen() {}
	
	static void gatherData(GatherDataEvent event)
	{
		Map<Identifier, Recipe<?>> recipes = new HashMap<>();
		
		DungeonFist.PIPE_BLOCKS.forEach((originalKey, blockHolder) -> {
			Identifier blockId = blockHolder.getId();
			Ingredient ingredient = Ingredient.of(BuiltInRegistries.BLOCK.get(originalKey.identifier()).get().value());
			ItemStackTemplate result = new ItemStackTemplate(BuiltInRegistries.ITEM.get(blockId).get());
			StonecutterRecipe recipe = new StonecutterRecipe("", ingredient, result);
			recipes.put(blockId.withSuffix("_from_stonecutting"), recipe);
		});
		
		DungeonFist.PRESSURE_PLATE_BLOCKS.forEach((originalKey, blockHolder) -> {
			Identifier blockId = blockHolder.getId();
			Item originalItem = BuiltInRegistries.ITEM.get(originalKey.identifier()).get().value();
			ShapedRecipePattern pattern = ShapedRecipePattern.of(
				Map.of('#', Ingredient.of(originalItem)),
				"##");
			ItemStackTemplate result = new ItemStackTemplate(BuiltInRegistries.ITEM.get(blockId).get().value());
			ShapedRecipe recipe = new ShapedRecipe("", CraftingBookCategory.REDSTONE, pattern, result);  
			recipes.put(blockId, recipe);
		});
		
		recipes.put(
			DungeonFist.CHARGED_TNT.getId(),
			new ShapelessRecipe(
				"",
				CraftingBookCategory.REDSTONE,
				new ItemStackTemplate(DungeonFist.CHARGED_TNT.asItem()),
				List.of(
					Ingredient.of(Items.TNT),
					Ingredient.of(Items.FIRE_CHARGE))));
		
		JsonDataProvider.addProvider(event, Target.DATA_PACK, "recipe", Recipe.CODEC, recipes);
	}
}
