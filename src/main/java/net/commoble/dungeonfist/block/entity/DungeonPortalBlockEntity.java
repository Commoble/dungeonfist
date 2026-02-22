package net.commoble.dungeonfist.block.entity;

import org.jspecify.annotations.Nullable;

import net.commoble.dungeonfist.DungeonFist;
import net.commoble.dungeonfist.block.DungeonPortalBlock;
import net.commoble.dungeonfist.client.particle.DungeonPortalParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class DungeonPortalBlockEntity extends BlockEntity
{
	private @Nullable DungeonPortalParticleOptions color;
	private @Nullable BlockPos targetPortalPos;
	private @Nullable ResourceKey<Level> targetLevel;
	
	public static DungeonPortalBlockEntity create(BlockPos pos, BlockState state)
	{
		return new DungeonPortalBlockEntity(DungeonFist.DUNGEON_PORTAL_BLOCK_ENTITY_TYPE.get(), pos, state);
	}
	
	public DungeonPortalBlockEntity(BlockEntityType<? extends DungeonPortalBlockEntity> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}
	
	public DungeonPortalParticleOptions getColor()
	{
		DungeonPortalParticleOptions color = this.color;
		if (color == null)
		{
			BlockPos seedPos = this.getBlockState().getBlock() instanceof DungeonPortalBlock block
				&& block.returnPortal()
				&& this.targetPortalPos != null
				? this.targetPortalPos
				: this.worldPosition;
			RandomSource rand = RandomSource.create(seedPos.hashCode());
			color = new DungeonPortalParticleOptions(
				rand.nextFloat(),
				rand.nextFloat(),
				rand.nextFloat());
			this.color = color;
		}
		return color;
	}
	
	public BlockPos targetPortalPos()
	{
		return this.targetPortalPos == null
			? BlockPos.ZERO
			: this.targetPortalPos;
	}
	
	public ResourceKey<Level> targetLevel()
	{
		return this.targetLevel == null
			? Level.OVERWORLD
			: this.targetLevel;
	}
	
	public void setTarget(ResourceKey<Level> targetLevel, BlockPos targetPortalPos)
	{
		this.targetLevel = targetLevel;
		this.targetPortalPos = targetPortalPos;
		this.color = null;
		this.setChanged();
		assert this.level != null;
		this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 0);
	}
	
	@Override
	protected void saveAdditional(ValueOutput output)
	{
		super.saveAdditional(output);
		if (this.targetLevel != null)
		{
			output.store("target_level", ResourceKey.codec(Registries.DIMENSION), this.targetLevel);
		}
		if (this.targetPortalPos != null)
		{
			output.store("target_portal_pos", BlockPos.CODEC, this.targetPortalPos);
		}
	}

	@Override
	protected void loadAdditional(ValueInput input)
	{
		super.loadAdditional(input);
		this.targetLevel = input.read("target_level", ResourceKey.codec(Registries.DIMENSION)).orElse(null);
		this.targetPortalPos = input.read("target_portal_pos", BlockPos.CODEC).orElse(null);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider provider)
	{
		return this.saveWithoutMetadata(provider);
	}

	@Override
	public @Nullable Packet<ClientGamePacketListener> getUpdatePacket()
	{
        return ClientboundBlockEntityDataPacket.create(this);
	}
}
