package com.github.commoble.dungeonfist.data;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.github.commoble.dungeonfist.DungeonFist;
import com.github.commoble.dungeonfist.util.JsonMuncher;
import com.github.commoble.dungeonfist.util.RandomHelper;
import com.google.gson.stream.JsonReader;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class EpicNameWords
{
	private final List<Map<ResourceLocation, List<String>>> maps;	// prefixes, nouns, and suffixes
	
	private static EpicNameWords INSTANCE;
	
	public static void load()
	{
		INSTANCE = getNameWords();
	}
	
	public static ITextComponent getName(ItemStack stack, Random random)
	{
		Pair<String,String> words = INSTANCE.getRandomWords(stack, random);
		return new TranslationTextComponent("dungeonfist.word."+words.getLeft())
		.appendSibling(new StringTextComponent(" ")
		.appendSibling(new TranslationTextComponent("dungeonfist.word."+words.getRight())));
	}

	private static EpicNameWords getNameWords()
	{
		ClassLoader classLoader = DungeonFist.class.getClassLoader();
		String[] filenames = {	// order is super important!
			"data/dungeonfist/epic_names/prefixes.json",
			"data/dungeonfist/epic_names/nouns.json",
			"data/dungeonfist/epic_names/suffixes.json"};
		
		return new EpicNameWords(Arrays.stream(filenames)
			.map(classLoader::getResourceAsStream)
			.map(EpicNameWords::loadReader)
			.map(EpicNameWords::readJson)
			.collect(Collectors.toCollection(ArrayList::new)));
	}
	
	// gets two random words
	private Pair<String, String> getRandomWords(ItemStack stack, Random random)
	{
		final String ALL_TAG = "all";
		TagCollection<Item> tags = ItemTags.getCollection();
		Item item = stack.getItem();
		
		int indices = random.nextInt(4);	// 0,1,2,3
		int first = indices / 2;			// 0,0,1,1 = prefix,prefix,noun,noun
		int second = (indices%2) + 1;		// 1,2,1,2 = noun  ,suffix,noun,suffix
		List<List<List<String>>> lists = this.maps.stream()
			.map(map ->map.entrySet().stream()
				.filter(entry -> entry.getKey().getPath().equals(ALL_TAG) || tags.get(entry.getKey()).contains(item))
				.map(entry -> entry.getValue())
				.collect(Collectors.toCollection(ArrayList<List<String>>::new)))
			.collect(Collectors.toCollection(ArrayList<List<List<String>>>::new));
		IntFunction<String> getter = i -> RandomHelper.getRandomThingFromMultipleLists(random, lists.get(i)).orElse("Failure");
		return Pair.of(getter.apply(first), getter.apply(second));
	}

	private static JsonReader loadReader(InputStream input)
	{
		JsonReader reader = null;
		try
		{
			reader = new JsonReader(new InputStreamReader(input, "UTF-8"));
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return reader;
	}
	
	private EpicNameWords(List<Map<ResourceLocation, List<String>>> maps)
	{
		this.maps = maps;
	}

	private static Map<ResourceLocation, List<String>> readJson(JsonReader reader)
	{
		return JsonMuncher.munch(reader).readSimpleObject(
			ResourceLocation::new,
			mapMuncher -> mapMuncher.readSimpleArray(JsonMuncher::readString));
	}
}
