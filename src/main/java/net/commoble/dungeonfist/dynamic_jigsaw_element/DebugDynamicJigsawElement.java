package net.commoble.dungeonfist.dynamic_jigsaw_element;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.commoble.structurebuddy.api.DynamicJigsawBakeContext;
import net.commoble.structurebuddy.api.DynamicJigsawElement;
import net.commoble.structurebuddy.api.DynamicJigsawResult;
import net.minecraft.core.Holder;

public record DebugDynamicJigsawElement(Holder<DynamicJigsawElement> element, String name) implements DynamicJigsawElement
{
	public static final Logger LOGGER = LogUtils.getLogger();
	
	public static final MapCodec<DebugDynamicJigsawElement> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
			DynamicJigsawElement.CODEC.fieldOf("element").forGetter(DebugDynamicJigsawElement::element),
			Codec.STRING.fieldOf("name").forGetter(DebugDynamicJigsawElement::name)
		).apply(builder, DebugDynamicJigsawElement::new));
	
	@Override
	public MapCodec<? extends DynamicJigsawElement> codec()
	{
		return CODEC;
	}

	@Override
	public DynamicJigsawResult bake(DynamicJigsawBakeContext context)
	{
		DynamicJigsawResult delegateResult = this.element.value().bake(context);
		var parent = context.parent();
		String position = parent == null ? "UNKNOWN" : parent.pos().toShortString();
		String name = this.name;
		return new DynamicJigsawResult(
			delegateResult.pieceFillerFactory(),
			delegateResult.localBoundingBox(),
			delegateResult.shuffledLocalConnectionsToParent(),
			delegateResult.shuffledLocalConnectionsToChildren(),
			delegateResult.onSelected().andThen((data,random) -> {
				LOGGER.info("Generated Dynamic Jigsaw Element {} at {}", name, position);
			}));
	}

}
