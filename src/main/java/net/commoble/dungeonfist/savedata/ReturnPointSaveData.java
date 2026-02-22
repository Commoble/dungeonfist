package net.commoble.dungeonfist.savedata;

import java.util.Optional;

import org.jspecify.annotations.Nullable;

import com.mojang.serialization.Codec;

import net.commoble.dungeonfist.DungeonFist;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class ReturnPointSaveData extends SavedData
{
	public static final Identifier ID = DungeonFist.id("return_point");
	public static final Codec<ReturnPointSaveData> CODEC = GlobalPos.CODEC
		.optionalFieldOf("return_point")
		.xmap(ReturnPointSaveData::new, ReturnPointSaveData::returnPoint)
		.codec();
	
	public static Codec<ReturnPointSaveData> codec(@Nullable ServerLevel level)
	{
		return CODEC;
	}
	
	public static final SavedDataType<ReturnPointSaveData> TYPE = new SavedDataType<>(ID, ReturnPointSaveData::create, ReturnPointSaveData::codec);

	public static ReturnPointSaveData getOrCreate(ServerLevel level)
	{
		return level.getDataStorage().computeIfAbsent(TYPE);
	}
	
	private Optional<GlobalPos> returnPoint = Optional.empty();
	
	public Optional<GlobalPos> returnPoint()
	{
		return this.returnPoint;
	}
	
	public ReturnPointSaveData()
	{
		this.returnPoint = Optional.empty();
	}
	
	public ReturnPointSaveData(Optional<GlobalPos> returnPoint)
	{
		this.returnPoint = returnPoint;
	}
	
	public void setReturnPoint(ResourceKey<Level> key, BlockPos pos)
	{
		this.setReturnPoint(new GlobalPos(key, pos));
	}
	
	public void setReturnPoint(GlobalPos returnPoint)
	{
		this.returnPoint = Optional.of(returnPoint);
		this.setDirty();
	}
	
	public static ReturnPointSaveData create(@Nullable ServerLevel level)
	{
		return new ReturnPointSaveData();
	}
}
