package com.github.commoble.dungeonfist.network;

import java.util.function.Supplier;

import com.github.commoble.dungeonfist.registry.DimensionTypeRegistrar;
import com.github.commoble.dungeonfist.registry.ModDimensionRegistrar;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class OnClientJoinedServerPacket
{
	public int id;

	public OnClientJoinedServerPacket(int dimensionID)
	{
		this.id = dimensionID;
	}

	public void encode(PacketBuffer buf)
	{
		buf.writeInt(id);
	}

	public static OnClientJoinedServerPacket decode(PacketBuffer buf)
	{
		return new OnClientJoinedServerPacket(buf.readInt());
	}

	public void onPacketReceived(Supplier<NetworkEvent.Context> supplier)
	{
		NetworkEvent.Context ctx = supplier.get();
		if (ctx.getDirection() == NetworkDirection.LOGIN_TO_CLIENT)
		{
			if (DimensionManager.getRegistry().getByValue(this.id) == null)
			{
				
				DimensionManager.registerDimensionInternal(this.id, DimensionTypeRegistrar.dungeonResource, ModDimensionRegistrar.DUNGEON, null, false);
			}
		}
	}
}
