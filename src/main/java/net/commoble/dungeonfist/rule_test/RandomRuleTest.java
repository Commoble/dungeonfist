package net.commoble.dungeonfist.rule_test;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.commoble.dungeonfist.DungeonFist;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;

public class RandomRuleTest extends RuleTest
{
	public static final MapCodec<RandomRuleTest> CODEC = Codec.DOUBLE
		.xmap(RandomRuleTest::new, RandomRuleTest::probability)
		.fieldOf("probability");
	
	private final double probability;
	public double probability() { return this.probability; }
	
	public RandomRuleTest(double probability)
	{
		this.probability = probability;
	}
	
	@Override
	public boolean test(BlockState state, RandomSource random)
	{
		return random.nextDouble() < this.probability;
	}

	@Override
	protected RuleTestType<?> getType()
	{
		return DungeonFist.RANDOM_RULE_TEST.get();
	}

}
