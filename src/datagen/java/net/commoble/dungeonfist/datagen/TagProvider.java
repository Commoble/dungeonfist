package net.commoble.dungeonfist.datagen;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.google.common.collect.Maps;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.data.event.GatherDataEvent;

/**
 * Better TagsProvider that allows other things to add tags to it
 */
public class TagProvider<T> extends TagsProvider<T>
{
	// mojang clears the map for some reason so we have to maintain our own
	protected final Map<Identifier, TagBuilder> subclassBuilders = Maps.newLinkedHashMap();

	public static <T> TagProvider<T> create(GatherDataEvent event, ResourceKey<Registry<T>> registry)
	{
		return event.createProvider((output, future) -> new TagProvider<>(output, registry, future, ModLoadingContext.get().getActiveContainer().getModId()));
	}

	protected TagProvider(PackOutput output, ResourceKey<Registry<T>> registry, CompletableFuture<HolderLookup.Provider> holders, String modId)
	{
		super(output, registry, holders, modId);
	}

	public TagBuilder getOrCreateRawBuilder(TagKey<T> tagKey)
	{
		super.getOrCreateRawBuilder(tagKey); // track file
		return this.subclassBuilders.computeIfAbsent(tagKey.location(), (key) -> TagBuilder.create());
	}

	public TagAppender<T> tag(TagKey<T> tagKey)
	{
		return super.tag(tagKey);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider)
	{
		this.builders.putAll(this.subclassBuilders);
	}

}