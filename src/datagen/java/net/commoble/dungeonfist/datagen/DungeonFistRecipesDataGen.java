package net.commoble.dungeonfist.datagen;

import java.util.List;
import java.util.Map;

import net.commoble.dungeonfist.DungeonFist;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe.CraftingBookInfo;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.Recipe.CommonInfo;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;

public final class DungeonFistRecipesDataGen
{
	private DungeonFistRecipesDataGen() {}
	
	public static ResourceKey<Recipe<?>> key(Identifier id)
	{
		return ResourceKey.create(Registries.RECIPE, id);
	}
	
	static void gatherData(BootstrapContext<Recipe<?>> bootstrap)
	{		
		DungeonFist.PIPE_BLOCKS.forEach((originalKey, blockHolder) -> {
			Identifier blockId = blockHolder.getId();
			Ingredient ingredient = Ingredient.of(BuiltInRegistries.BLOCK.get(originalKey.identifier()).get().value());
			ItemStackTemplate result = new ItemStackTemplate(BuiltInRegistries.ITEM.get(blockId).get());
			StonecutterRecipe recipe = new StonecutterRecipe(new CommonInfo(false), ingredient, result);
			bootstrap.register(key(blockId.withSuffix("_from_stonecutting")), recipe);
		});
		
		DungeonFist.PRESSURE_PLATE_BLOCKS.forEach((originalKey, blockHolder) -> {
			Identifier blockId = blockHolder.getId();
			Item originalItem = BuiltInRegistries.ITEM.get(originalKey.identifier()).get().value();
			ShapedRecipePattern pattern = ShapedRecipePattern.of(
				Map.of('#', Ingredient.of(originalItem)),
				"##");
			ItemStackTemplate result = new ItemStackTemplate(BuiltInRegistries.ITEM.get(blockId).get().value());
			ShapedRecipe recipe = new ShapedRecipe(new CommonInfo(false), new CraftingBookInfo(CraftingBookCategory.REDSTONE, ""), pattern, result);  
			bootstrap.register(key(blockId), recipe);
		});
		
		bootstrap.register(
			key(DungeonFist.CHARGED_TNT.getId()),
			new ShapelessRecipe(
				new CommonInfo(false),
				new CraftingBookInfo(CraftingBookCategory.REDSTONE, ""),
				new ItemStackTemplate(DungeonFist.CHARGED_TNT.asItem()),
				List.of(
					Ingredient.of(Items.TNT),
					Ingredient.of(Items.FIRE_CHARGE))));
	}
}
