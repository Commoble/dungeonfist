package net.commoble.dungeonfist.dynamic_jigsaw_element;

import java.util.List;
import java.util.Map;

import org.jspecify.annotations.Nullable;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.commoble.structurebuddy.api.DynamicJigsawBakeContext;
import net.commoble.structurebuddy.api.DynamicJigsawElement;
import net.commoble.structurebuddy.api.DynamicJigsawResult;
import net.commoble.structurebuddy.api.JigsawConnectionToChild;
import net.commoble.structurebuddy.api.JigsawDataAccess;
import net.commoble.structurebuddy.api.JigsawDataType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosAlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTest;

public record SetDataDynamicJigsawElement(List<Pair<PosRuleTest,Map<JigsawDataType<?>, Object>>> rules, Holder<DynamicJigsawElement> element) implements DynamicJigsawElement
{
	public static final MapCodec<SetDataDynamicJigsawElement> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
			Codec.mapPair(
				PosRuleTest.CODEC.optionalFieldOf("rule", PosAlwaysTrueTest.INSTANCE),
				JigsawDataType.MAP_CODEC.fieldOf("data")
			).codec().listOf().fieldOf("rules").forGetter(SetDataDynamicJigsawElement::rules),
			DynamicJigsawElement.CODEC.fieldOf("element").forGetter(SetDataDynamicJigsawElement::element)
		).apply(builder, SetDataDynamicJigsawElement::new));

	@Override
	public MapCodec<? extends DynamicJigsawElement> codec()
	{
		return CODEC;
	}

	@Override
	public DynamicJigsawResult bake(DynamicJigsawBakeContext context)
	{
		DynamicJigsawResult delegate = this.element.value().bake(context);
		@Nullable JigsawConnectionToChild parent = context.parent();
		BlockPos referencePos = context.structureOrigin();
		BlockPos worldPos = parent == null ? referencePos : parent.pos();
		return new DynamicJigsawResult(
			delegate.pieceFillerFactory(),
			delegate.localBoundingBox(),
			delegate.shuffledLocalConnectionsToParent(),
			delegate.shuffledLocalConnectionsToChildren(),
			delegate.onSelected().andThen((access,random) -> {
				for (var rule : this.rules)
				{
					if (rule.getFirst().test(worldPos, worldPos, referencePos, random))
					{
						for (var entry : rule.getSecond().entrySet())
						{
							writeData(access, entry.getKey(), entry.getValue());
						}
					}
				}
			}));
	}

	@SuppressWarnings("unchecked")
	private static <T> void writeData(JigsawDataAccess access, JigsawDataType<T> key, Object value)
	{
		access.setBranchData(key, (T)value);
	}
}
