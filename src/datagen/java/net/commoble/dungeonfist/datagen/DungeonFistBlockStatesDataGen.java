package net.commoble.dungeonfist.datagen;

import java.util.HashMap;
import java.util.Map;

import com.mojang.math.Quadrant;

import net.commoble.dungeonfist.DungeonFist;
import net.commoble.dungeonfist.block.ChargeableRuneBlock;
import net.commoble.dungeonfist.block.PipeBlock;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.core.Direction.Axis;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredBlock;

public final class DungeonFistBlockStatesDataGen
{
	private DungeonFistBlockStatesDataGen() {}
	
	static void gatherData(GatherDataEvent event)
	{
		Map<Identifier, BlockModelDefinition> blockstates = new HashMap<>();
		
		DungeonFist.PIPE_BLOCKS.forEach((originalKey, blockHolder) -> {
			Identifier blockId = blockHolder.unwrapKey().get().identifier();
			Identifier modelId = blockId.withPrefix("block/");
			BlockModelDefinition blockstate = BlockStateBuilder.variants(variants -> variants
				.addVariant(PipeBlock.AXIS, Axis.Z, BlockStateBuilder.model(modelId, Quadrant.R0, Quadrant.R0, true))
				.addVariant(PipeBlock.AXIS, Axis.X, BlockStateBuilder.model(modelId, Quadrant.R0, Quadrant.R90, true))
				.addVariant(PipeBlock.AXIS, Axis.Y, BlockStateBuilder.model(modelId, Quadrant.R90, Quadrant.R0, true)));
			blockstates.put(blockId, blockstate);
		});
		
		DungeonFist.PRESSURE_PLATE_BLOCKS.forEach((originalKey, blockHolder) -> {
			Identifier blockId = blockHolder.unwrapKey().get().identifier();
			Identifier modelUp = blockId.withPrefix("block/");
			Identifier modelDown = modelUp.withSuffix("_down");
			BlockModelDefinition blockstate = BlockStateBuilder.variants(variants -> variants
				.addVariant(PressurePlateBlock.POWERED, false, BlockStateBuilder.model(modelUp))
				.addVariant(PressurePlateBlock.POWERED, true, BlockStateBuilder.model(modelDown)));
			blockstates.put(blockId, blockstate);
		});
		
		registerChargeableRuneBlock(blockstates, DungeonFist.ALERT_RUNE);
		simpleBlockWithParent(blockstates, DungeonFist.CHARGED_TNT, Identifier.withDefaultNamespace("block/tnt"));
		registerChargeableRuneBlock(blockstates, DungeonFist.SUMMON_RUNE);
		simpleBlock(blockstates, DungeonFist.TELEPORT_RUNE);
			
		
		JsonDataProvider.addProvider(event, Target.RESOURCE_PACK, "blockstates", BlockModelDefinition.CODEC, blockstates);
	}
	
	private static void registerChargeableRuneBlock(Map<Identifier, BlockModelDefinition> blockstates, DeferredBlock<?> holder)
	{
		blockstates.put(holder.getId(), BlockStateBuilder.variants(variants -> variants
			.addVariant(ChargeableRuneBlock.CHARGED, false, BlockStateBuilder.model(holder.getId().withPrefix("block/")))
			.addVariant(ChargeableRuneBlock.CHARGED, true, BlockStateBuilder.model(holder.getId().withPrefix("block/").withSuffix("_charged")))
		));
	}
	
	private static void simpleBlock(Map<Identifier, BlockModelDefinition> blockstates, DeferredBlock<?> holder)
	{
		simpleBlockWithParent(blockstates, holder, holder.getId().withPrefix("block/"));
	}
	
	private static void simpleBlockWithParent(Map<Identifier, BlockModelDefinition> blockstates, DeferredBlock<?> holder, Identifier parentModel)
	{
		blockstates.put(holder.getId(), BlockStateBuilder.singleVariant(BlockStateBuilder.model(parentModel)));
	}
}
