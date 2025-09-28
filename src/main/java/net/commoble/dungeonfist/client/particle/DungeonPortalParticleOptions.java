package net.commoble.dungeonfist.client.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.commoble.dungeonfist.DungeonFist;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

// particle data instances are used to pass data from the things that we spawn
// particles from to the particle factories
// in this case we use this to pass color information from the portal tile entities to the particles
public record DungeonPortalParticleOptions(float red, float green, float blue) implements ParticleOptions
{
	public static final MapCodec<DungeonPortalParticleOptions> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
			Codec.FLOAT.fieldOf("red").forGetter(DungeonPortalParticleOptions::red),
			Codec.FLOAT.fieldOf("green").forGetter(DungeonPortalParticleOptions::green),
			Codec.FLOAT.fieldOf("blue").forGetter(DungeonPortalParticleOptions::blue)
		).apply(builder, DungeonPortalParticleOptions::new));
	
	public static final StreamCodec<ByteBuf, DungeonPortalParticleOptions> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.FLOAT, DungeonPortalParticleOptions::red,
		ByteBufCodecs.FLOAT, DungeonPortalParticleOptions::green,
		ByteBufCodecs.FLOAT, DungeonPortalParticleOptions::blue,
		DungeonPortalParticleOptions::new);
	
	@Override
	public ParticleType<?> getType()
	{
		return DungeonFist.DUNGEON_PORTAL_PARTICLE_TYPE.get();
	}
}
