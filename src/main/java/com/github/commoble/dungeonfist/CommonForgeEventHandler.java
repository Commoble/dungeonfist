package com.github.commoble.dungeonfist;

import java.lang.reflect.Field;
import java.util.function.IntSupplier;

import org.apache.commons.lang3.tuple.Pair;

import com.github.commoble.dungeonfist.registry.DimensionTypeRegistrar;
import com.google.common.collect.ImmutableList;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.event.world.RegisterDimensionsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.network.FMLHandshakeMessages;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber(modid=DungeonFist.MODID, bus=Bus.FORGE)
public class CommonForgeEventHandler
{

	/**
	 * Dimension registry hack borrowed from the Midnight mod pending a forge
	 * syncing fix The Midnight is licensed under GPL 3
	 * https://github.com/Cryptic-Mushroom/The-Midnight/tree/edc173a3cba230a383457966598b97e843fab291
	 * https://github.com/Cryptic-Mushroom/The-Midnight/blob/1.14.4-dev/LICENSE
	 */
	static
	{
		// TODO: Temporary hack until Forge fix
		try
		{
			Field channelField = FMLNetworkConstants.class.getDeclaredField("handshakeChannel");
			channelField.setAccessible(true);

			SimpleChannel handshakeChannel = (SimpleChannel) channelField.get(null);
			handshakeChannel.messageBuilder(S2CDimensionSync.class, 100)
					.loginIndex(S2CDimensionSync::getLoginIndex, S2CDimensionSync::setLoginIndex)
					.decoder(S2CDimensionSync::decode).encoder(S2CDimensionSync::encode)
					.buildLoginPacketList(isLocal -> {
						if (isLocal)
							return ImmutableList.of();
						return ImmutableList
								.of(Pair.of("DungeonFist Dim Sync", new S2CDimensionSync(DimensionTypeRegistrar.getDungeonDimensionType())));
					}).consumer((msg, ctx) -> {
						if (DimensionManager.getRegistry().getByValue(msg.id) == null)
						{
							DimensionManager.registerDimensionInternal(msg.id, msg.name, msg.dimension, null,
									msg.skyLight);
						}
						ctx.get().setPacketHandled(true);
						handshakeChannel.reply(new FMLHandshakeMessages.C2SAcknowledge(), ctx.get());
					}).add();
		} catch (ReflectiveOperationException e)
		{
			System.out.println("Failed to add dimension sync to handshake channel");
			System.err.print(e);
			//Midnight.LOGGER.error("Failed to add dimension sync to handshake channel", e);
			// TODO add logger
		}
	}

	public static class S2CDimensionSync implements IntSupplier
	{
		final int id;
		final ResourceLocation name;
		final ModDimension dimension;
		final boolean skyLight;

		private int loginIndex;

		@Override
		public int getAsInt()
		{
			return this.getLoginIndex();
		}

		public S2CDimensionSync(DimensionType dimensionType)
		{
			this.id = dimensionType.getId() + 1;
			this.name = DimensionType.getKey(dimensionType);
			this.dimension = dimensionType.getModType();
			this.skyLight = dimensionType.func_218272_d();
		}

		S2CDimensionSync(int id, ResourceLocation name, ModDimension dimension, boolean skyLight)
		{
			this.id = id;
			this.name = name;
			this.dimension = dimension;
			this.skyLight = skyLight;
		}

		void setLoginIndex(final int loginIndex)
		{
			this.loginIndex = loginIndex;
		}

		int getLoginIndex()
		{
			return this.loginIndex;
		}

		void encode(PacketBuffer buffer)
		{
			buffer.writeInt(this.id);
			buffer.writeResourceLocation(this.name);
			buffer.writeResourceLocation(this.dimension.getRegistryName());
			buffer.writeBoolean(this.skyLight);
		}

		public static S2CDimensionSync decode(PacketBuffer buffer)
		{
			int id = buffer.readInt();
			ResourceLocation name = buffer.readResourceLocation();
			ModDimension dimension = ForgeRegistries.MOD_DIMENSIONS.getValue(buffer.readResourceLocation());
			boolean skyLight = buffer.readBoolean();

			return new S2CDimensionSync(id, name, dimension, skyLight);
		}
	}

	/** end Midnight horking **/
	
	@SubscribeEvent
	public static void onRegisterDimensionsEvent(RegisterDimensionsEvent event)
	{
		DimensionTypeRegistrar.registerDimensionTypes();
	}
}
