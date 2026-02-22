package net.commoble.dungeonfist.block.entity;

import org.jspecify.annotations.Nullable;

import net.commoble.dungeonfist.DungeonFist;
import net.commoble.dungeonfist.NameMaker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PortalGeneratorBlockEntity extends BlockEntity
{
	private @Nullable ResourceKey<Level> levelKey;
	
	public PortalGeneratorBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState)
	{
		super(type, worldPosition, blockState);
	}
	
	public static PortalGeneratorBlockEntity create(BlockPos worldPosition, BlockState blockState)
	{
		return new PortalGeneratorBlockEntity(DungeonFist.PORTAL_GENERATOR_BLOCK_ENTITY_TYPE.get(), worldPosition, blockState);
	}

	public ResourceKey<Level> getOrCreateLevelKey(ServerLevel serverLevel, long newSeed)
	{
		@Nullable ResourceKey<Level> levelKey = this.levelKey;
		if (levelKey == null)
		{
			long seed = BiomeManager.obfuscateSeed(newSeed);
			RandomSource random = RandomSource.create(seed);
			var currentLevelId = serverLevel.dimension().identifier();
			String currentNamespace = currentLevelId.getNamespace();
			String currentPath = currentLevelId.getPath();
			// if current namespace is not dungeonfist (this is a root dimension)
			// new id will be dungeonfist:oldnamespace/oldpath/generatedpath
			// if current name IS dungeonfist (this is a dungeon dimension)
			// new id will be dungeonfist:oldpath/generatedpath
			String generatedPath = String.format("%s-%s-%s",
				NameMaker.getWord(random),
				NameMaker.getWord(random),
				NameMaker.getWord(random));
			String newPath = currentNamespace.equals(DungeonFist.MODID) || currentNamespace.equals(Identifier.DEFAULT_NAMESPACE)
				? currentPath + "/" + generatedPath
				: currentNamespace + "/" + 	currentPath + "/" + generatedPath;
			levelKey = ResourceKey.create(Registries.DIMENSION, DungeonFist.id(newPath));
			this.levelKey = levelKey;
		}
		return levelKey;
	}
}
