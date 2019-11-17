package com.github.commoble.dungeonfist.data.loot;

import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.apache.commons.lang3.tuple.Pair;

import com.github.commoble.dungeonfist.data.NameWords;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class ImproveNameBasedOnEnchantments extends LootFunction
{
	public static final ImproveNameBasedOnEnchantmentsSerializer SERIALIZER = new ImproveNameBasedOnEnchantmentsSerializer();
	
	protected ImproveNameBasedOnEnchantments(ILootCondition[] conditionsIn)
	{
		super(conditionsIn);
	}

	@Override
	protected ItemStack doApply(ItemStack stack, LootContext context)
	{
		Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
		BinaryOperator<Map.Entry<Enchantment, Integer>> biggestReducer = (a,b) -> b.getValue() > a.getValue() ? b : a;
		BinaryOperator<Map.Entry<Enchantment, Integer>> smallestReducer = (a,b) -> b.getValue() < a.getValue() ? b : a;
		Optional<TextFormatting> epicFormatting = Optional.empty();
		
		// if number of enchantments is at least three, generate an epic name and ignore the three smallest enchantments in the next phase
		if (enchantments.size() > 2)
		{
			stack.setDisplayName(getEpicName(stack, context));
			epicFormatting = Optional.of(TextFormatting.LIGHT_PURPLE);
			
			IntStream.range(0,3).forEach(
				i-> enchantments.entrySet().stream()
				.reduce(smallestReducer)
				.ifPresent(entry -> enchantments.remove(entry.getKey()))
			);
		}
		
		// if any enchantments remain, get the two biggest (if any) and generate prefix/suffix based on them
		Optional<Map.Entry<Enchantment,Integer>> biggest = enchantments.entrySet().stream()
			.reduce(biggestReducer);
		
		Optional<Map.Entry<Enchantment, Integer>> secondBiggest = enchantments.entrySet().stream()
			.filter(entry -> entry != biggest.get())	// if biggest is empty then this won't be evaluated
			.reduce(biggestReducer);
		
		Pair<Optional<Map.Entry<Enchantment, Integer>>, Optional<Map.Entry<Enchantment, Integer>>> twoBiggest = context.getRandom().nextBoolean()
			? Pair.of(biggest, secondBiggest)
			: Pair.of(secondBiggest, biggest);
		
		Function<String, Function<? super Map.Entry<Enchantment, Integer>, ? extends ITextComponent>> mapperGetter =
			position -> entry -> new TranslationTextComponent(entry.getKey().getName()+position+entry.getValue().toString());
		
		Optional<ITextComponent> prefix = twoBiggest.getLeft().map(mapperGetter.apply(".prefix."));
		Optional<ITextComponent> suffix = twoBiggest.getRight().map(mapperGetter.apply(".suffix."));
		
		prefix.ifPresent(text -> stack.setDisplayName(text.appendText(" ").appendSibling(stack.getDisplayName())));
		suffix.ifPresent(text -> stack.setDisplayName(stack.getDisplayName().appendText(" ").appendSibling(text)));
		
		epicFormatting.ifPresent(color -> stack.setDisplayName(stack.getDisplayName().applyTextStyle(color)));

		return stack;
	}

	private static class ImproveNameBasedOnEnchantmentsSerializer extends LootFunction.Serializer<ImproveNameBasedOnEnchantments>
	{

		public ImproveNameBasedOnEnchantmentsSerializer()
		{
			super(DungeonLootTables.IMPROVE_NAME_BASED_ON_ENCHANTMENTS, ImproveNameBasedOnEnchantments.class);
		}

		@Override
		public ImproveNameBasedOnEnchantments deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn)
		{
			// TODO Auto-generated method stub
			return new ImproveNameBasedOnEnchantments(conditionsIn);
		}
		
	}
	
	public static ITextComponent getEpicName(ItemStack stack, LootContext context)
	{
		String noun = NameWords.generic_nouns.get(context.getRandom().nextInt(NameWords.generic_nouns.size()));
		String verber = NameWords.weapon_verbers.get(context.getRandom().nextInt(NameWords.weapon_verbers.size()));
		
		return new TranslationTextComponent("dungeonfist.generic.noun."+noun).appendSibling(new StringTextComponent(" ").appendSibling(new TranslationTextComponent("dungeonfist.weapon.verber."+verber)));
	}
}
