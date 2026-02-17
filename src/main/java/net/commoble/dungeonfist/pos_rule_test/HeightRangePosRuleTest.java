package net.commoble.dungeonfist.pos_rule_test;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.commoble.dungeonfist.DungeonFist;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTestType;

public class HeightRangePosRuleTest extends PosRuleTest
{
	public static final MapCodec<HeightRangePosRuleTest> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
			Codec.INT.optionalFieldOf("min_y", Integer.MIN_VALUE).forGetter(HeightRangePosRuleTest::minY),
			Codec.INT.optionalFieldOf("max_y", Integer.MAX_VALUE).forGetter(HeightRangePosRuleTest::maxY),
			Codec.DOUBLE.optionalFieldOf("chance", 1D).forGetter(HeightRangePosRuleTest::chance)
		).apply(builder, HeightRangePosRuleTest::new));
	
	private final int minY;
	private final int maxY;
	private final double chance;
	
	public HeightRangePosRuleTest(int minY, int maxY, double chance)
	{
		this.minY = minY;
		this.maxY = maxY;
		this.chance = chance;
	}

	public int minY()
	{
		return minY;
	}

	public int maxY()
	{
		return maxY;
	}
	
	public double chance()
	{
		return this.chance;
	}

	@Override
	public boolean test(BlockPos inTemplatePos, BlockPos worldPos, BlockPos worldReference, RandomSource random)
	{
		int height = worldPos.getY();
		return height >= this.minY && height <= this.maxY && random.nextDouble() < this.chance;
	}

	@Override
	protected PosRuleTestType<?> getType()
	{
		return DungeonFist.HEIGHT_RANGE_POS_RULE_TEST.get();
	}

}
