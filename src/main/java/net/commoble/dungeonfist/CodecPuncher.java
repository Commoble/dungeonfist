package net.commoble.dungeonfist;

import com.mojang.serialization.Codec;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.world.level.block.Block;

public class CodecPuncher
{
	public static final Codec<HolderSet<Block>> BLOCKS = HolderSetCodec.create(Registries.BLOCK, BuiltInRegistries.BLOCK.holderByNameCodec(), false);
}
