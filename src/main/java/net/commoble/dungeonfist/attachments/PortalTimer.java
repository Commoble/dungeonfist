package net.commoble.dungeonfist.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;

public record PortalTimer(long cooldownTimestamp, int portalTime, boolean inPortal, BlockPos portalPos)
{
	public static final PortalTimer DEFAULT = new PortalTimer(Long.MIN_VALUE, 0, false, BlockPos.ZERO);
	
	public static final MapCodec<PortalTimer> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
			Codec.LONG.fieldOf("cooldown_timestamp").forGetter(PortalTimer::cooldownTimestamp),
			Codec.INT.fieldOf("portal_time").forGetter(PortalTimer::portalTime),
			Codec.BOOL.fieldOf("in_portal").forGetter(PortalTimer::inPortal),
			BlockPos.CODEC.fieldOf("portal_pos").forGetter(PortalTimer::portalPos)
		).apply(builder, PortalTimer::new));
}
