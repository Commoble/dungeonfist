package net.commoble.dungeonfist.block;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import net.commoble.dungeonfist.DungeonFist;
import net.commoble.dungeonfist.client.particle.DungeonPortalParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class DungeonPortalBlockEntity extends BlockEntity
{
	private @Nullable DungeonPortalParticleOptions color;
	
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
		if (this.color == null)
		{
			RandomSource rand = RandomSource.create(this.getBlockPos().hashCode());
			this.color = new DungeonPortalParticleOptions(
				rand.nextFloat(),
				rand.nextFloat(),
				rand.nextFloat());
		}
		return this.color;
	}
	
//	@Override
//	protected void saveAdditional(ValueOutput output)
//	{
//		super.saveAdditional(output);
//	}
//
//	@Override
//	protected void loadAdditional(ValueInput input)
//	{
//		super.loadAdditional(input);
//	}
//
//	@Override
//	public CompoundTag getUpdateTag(Provider provider)
//	{
//		CompoundTag tag = super.getUpdateTag(provider);
//		return tag;
//	}
//	
//	@Override
//	public Packet<ClientGamePacketListener> getUpdatePacket()
//	{
//        return ClientboundBlockEntityDataPacket.create(this);
//	}
}
