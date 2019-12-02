package com.github.commoble.dungeonfist.data.loot;

import com.github.commoble.dungeonfist.util.RoomKey;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.util.JSONUtils;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class IsDungeonTier implements ILootCondition
{
	public static final Serializer SERIALIZER = new Serializer();
	
	public final int min;
	public final int max;
	
	public IsDungeonTier(int min, int max)
	{
		this.min = min;
		this.max = max;
	}
	
	public IsDungeonTier(int tier)
	{
		this(tier, tier);
	}
	
	@Override
	public boolean test(LootContext context)
	{
		int tier = RoomKey.getDungeonTierFromWorldSpace(context.get(LootParameters.POSITION).getY());
		return tier >= this.min && tier <= this.max;
	}

	public static class Serializer extends ILootCondition.AbstractSerializer<IsDungeonTier>
	{
		protected Serializer()
		{
			super(DungeonLootTables.IS_DUNGEON_TIER, IsDungeonTier.class);
		}

		@Override
		public void serialize(JsonObject json, IsDungeonTier condition, JsonSerializationContext context)
		{
			if (condition.min == condition.max)
			{
				json.addProperty("tier", condition.min);
			}
			else
			{
				json.addProperty("min", condition.min);
				json.addProperty("max", condition.max);
			}
		}

		@Override
		public IsDungeonTier deserialize(JsonObject json, JsonDeserializationContext context)
		{
			if (json.has("tier"))
			{
				return new IsDungeonTier(JSONUtils.getInt(json, "tier"));
			}
			else
			{
				int min = json.has("min") ? JSONUtils.getInt(json, "min") : RoomKey.MIN_TIER;
				int max = json.has("max") ? JSONUtils.getInt(json, "max") : RoomKey.MAX_TIER;
				return new IsDungeonTier(min, max);
			}
		}
	}
}
