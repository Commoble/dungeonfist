package com.github.commoble.dungeonfist.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;

public class DungeonPortalParticleData implements IParticleData
{
	public static final IParticleData.IDeserializer<DungeonPortalParticleData> DESERIALIZER = new IParticleData.IDeserializer<DungeonPortalParticleData>()
	{
		public DungeonPortalParticleData deserialize(ParticleType<DungeonPortalParticleData> particleTypeIn,
				StringReader reader) throws CommandSyntaxException
		{
			reader.expect(' ');
			return new DungeonPortalParticleData(particleTypeIn,
					reader.readFloat(),
					reader.readFloat(),
					reader.readFloat());
		}

		public DungeonPortalParticleData read(ParticleType<DungeonPortalParticleData> particleTypeIn,
				PacketBuffer buffer)
		{
			return new DungeonPortalParticleData(
					particleTypeIn,
					buffer.readFloat(),
					buffer.readFloat(),
					buffer.readFloat());
		}
	};

	private final ParticleType<DungeonPortalParticleData> particleType;
	public final float red;
	public final float green;
	public final float blue;

	public DungeonPortalParticleData(ParticleType<DungeonPortalParticleData> particleType, float red, float green, float blue)
	{
		this.particleType = particleType;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	@Override
	public ParticleType<?> getType()
	{
		// TODO Auto-generated method stub
		return this.particleType;
	}

	@Override
	public void write(PacketBuffer buffer)
	{
		buffer.writeFloat(red);
		buffer.writeFloat(green);
		buffer.writeFloat(blue);
	}

	@Override
	public String getParameters()
	{
		// TODO Auto-generated method stub
		return ""+this.red+" "+this.green+" "+this.blue;
	}

}
