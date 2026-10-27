package net.commoble.dungeonfist.datagen;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.resources.ResourceKey;

public class DummyHolder<T> extends Holder.Reference<T>
{
	private static class DummyOwner<T> implements HolderOwner<T>
	{
	}

	public DummyHolder(ResourceKey<T> key)
	{
		super(Type.STAND_ALONE, new DummyOwner<>(), key, null);
	}

	@Override
	public boolean canSerializeIn(HolderOwner<T> context)
	{
		return true;
	}

	public static <T> DummyHolder<T> of(ResourceKey<T> key)
	{
		return new DummyHolder<>(key);
	}
}
