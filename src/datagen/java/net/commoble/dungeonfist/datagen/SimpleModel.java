/*

The MIT License (MIT)

Copyright (c) 2026 Joseph Bettendorff a.k.a. "Commoble"

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

 */
package net.commoble.dungeonfist.datagen;

import java.util.HashMap;
import java.util.Map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.data.event.GatherDataEvent;

/**
 * Represents a parented model file. The codec can be used for datagen via {@link #addDataProvider(GatherDataEvent, String, DynamicOps, Map)}.
 * @param parent Identifier of the parent model
 * @param textures Map of String texture identifiers (specific to this model or its parent) to Identifier ids of textures
 * If renderType is absent then the block renderer will check for a render type registered to the block,
 * or else use solid. Explicitly specifying a solid rendertype here is preferable as it averts a map lookup.
 */
public record SimpleModel(Identifier parent, Map<String, Identifier> textures)
{
	/** codec **/
	public static final Codec<SimpleModel> CODEC = RecordCodecBuilder.create(builder -> builder.group(
			Identifier.CODEC.fieldOf("parent").forGetter(SimpleModel::parent),
			Codec.unboundedMap(Codec.STRING, Identifier.CODEC).optionalFieldOf("textures", Map.of()).forGetter(SimpleModel::textures)
		).apply(builder, SimpleModel::new));
	
	/**
	 * Creates a SimpleModel with specified parent
	 * @param parent Model id of the parent modek, e.g. "minecraft:block/cube_all"
	 * @return Builder-like model that allows chaining via {@link SimpleModel#addTexture(String, Identifier)}
	 */
	public static SimpleModel create(Identifier parent)
	{
		return new SimpleModel(parent, new HashMap<>());
	}
	
	/**
	 * Chaining method for building a SimpleModel. Call on a SimpleModel created via {@link SimpleModel#create(Identifier)}
	 * @param textureName Texture key in the parent model, e.g. "texture" or "down" or "particle"
	 * @param textureId e.g. "minecraft:block/cobblestone"
	 * @return This SimpleModel, or crashes if this was called on a deserialized SimpleModel as the map will not be mutable
	 */
	public SimpleModel addTexture(String textureName, Identifier textureId)
	{
		this.textures.put(textureName, textureId);
		return this;
	}
}