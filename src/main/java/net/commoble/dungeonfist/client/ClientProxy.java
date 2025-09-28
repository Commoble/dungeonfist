package net.commoble.dungeonfist.client;

import net.commoble.dungeonfist.DungeonFist;
import net.commoble.dungeonfist.block.DungeonPortalBlockEntity;
import net.commoble.dungeonfist.client.particle.DungeonPortalParticle.DungeonPortalParticleProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid=DungeonFist.MODID, value=Dist.CLIENT)
public final class ClientProxy
{
	private ClientProxy() {}
	
	private static LocalPlayer player()
	{
		Minecraft mc = Minecraft.getInstance();
		return mc.player;
	}
	
	@SubscribeEvent
	public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event)
	{
		event.registerSpriteSet(DungeonFist.DUNGEON_PORTAL_PARTICLE_TYPE.get(), DungeonPortalParticleProvider::new);
	}
	
	private static void dungeonPortalBlockEntityClientTick(Level level, BlockPos pos, BlockState state, DungeonPortalBlockEntity be)
	{
		RandomSource rand = level.random;
		
		// generate portal particles in a billboard facing the player
    	LocalPlayer player = player();
    	double playerX = player.getX();
    	double playerZ = player.getZ();
    	
    	// get center of block; blockpos's world position is a corner of the block, add 0.5 to both coordinates
    	double blockX = pos.getX() + 0.5D;
    	double blockZ = pos.getZ() + 0.5D;
    	
    	double dZ = blockZ - playerZ;
    	double dX = blockX - playerX;
    	
    	double angle = Math.atan2(dZ, dX) + (Math.PI * 0.5D);
    	
    	// generate a bunch of particles
    	int pcount = rand.nextInt(40) + 30;
    	for (int i=0; i<pcount; i++)
    	{
    		// generate particles in the 3-block column with this tile at the center
    		double height = rand.nextDouble();
    		double pY = -0.8D + height*2.6D + pos.getY();
    		
    		// horizontal radius from center pole is based on height
    		double radius = -3D*height*(height-1D) * (rand.nextDouble()-0.5D);
    		// this causes max diameter to be 0 when height value is 0 or 1, and 0.75 when height is 0.5
    		// radius is now between -0.375 and 0.375
    		
    		// depth potential gets wider further away from the center
    		double depth = (height-0.5D) * radius * 0.2D;
    		
    		double pX = radius*Math.cos(angle)+blockX;
    		double pZ = radius*Math.sin(angle)+blockZ;
    		
    		double vX = (blockX - pX) * 0.015D;
    		double vZ = (blockZ - pZ) * 0.015D;
    		double vY = (pos.getY() + 1.5D - pY) * 0.015D;
    		
    		level.addParticle(be.getColor(),
    			pX, pY, pZ, vX, vY, vZ);
    			
    	}
	}
	public static final BlockEntityTicker<DungeonPortalBlockEntity> DUNGEON_PORTAL_TICKER = ClientProxy::dungeonPortalBlockEntityClientTick;
}
