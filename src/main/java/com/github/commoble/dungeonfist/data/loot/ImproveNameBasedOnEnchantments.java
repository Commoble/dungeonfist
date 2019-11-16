package com.github.commoble.dungeonfist.data.loot;

import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
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
		BinaryOperator<Map.Entry<Enchantment, Integer>> reducer = (a,b) -> b.getValue() > a.getValue() ? b : a;
		
		Optional<Map.Entry<Enchantment,Integer>> biggest = enchantments.entrySet().stream()
			.reduce(reducer);
		
		Optional<Map.Entry<Enchantment, Integer>> secondBiggest = enchantments.entrySet().stream()
			.filter(entry -> entry != biggest.get())	// if biggest is empty then this won't be evaluated
			.reduce(reducer);
		
		Pair<Optional<Map.Entry<Enchantment, Integer>>, Optional<Map.Entry<Enchantment, Integer>>> twoBiggest = context.getRandom().nextBoolean()
			? Pair.of(biggest, secondBiggest)
			: Pair.of(secondBiggest, biggest);
		
		Function<String, Function<? super Map.Entry<Enchantment, Integer>, ? extends ITextComponent>> mapperGetter =
			position -> entry -> new TranslationTextComponent(entry.getKey().getName()+position+entry.getValue().toString());
		
		Optional<ITextComponent> prefix = twoBiggest.getLeft().map(mapperGetter.apply(".prefix."));
		Optional<ITextComponent> suffix = twoBiggest.getRight().map(mapperGetter.apply(".suffix."));
		
		prefix.ifPresent(text -> stack.setDisplayName(text.appendText(" ").appendSibling(stack.getDisplayName())));
		suffix.ifPresent(text -> stack.setDisplayName(stack.getDisplayName().appendText(" ").appendSibling(text)));

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
}
