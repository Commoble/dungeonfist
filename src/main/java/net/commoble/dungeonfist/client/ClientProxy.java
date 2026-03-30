package net.commoble.dungeonfist.client;

import org.jspecify.annotations.Nullable;

import net.commoble.dungeonfist.DungeonFist;
import net.commoble.dungeonfist.attachment.PortalTimer;
import net.commoble.dungeonfist.block.entity.DungeonPortalBlockEntity;
import net.commoble.dungeonfist.block.entity.PortalGeneratorBlockEntity;
import net.commoble.dungeonfist.client.particle.DungeonPortalParticle.DungeonPortalParticleProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid=DungeonFist.MODID, value=Dist.CLIENT)
public final class ClientProxy
{
	private ClientProxy() {}

	public static final BlockEntityTicker<DungeonPortalBlockEntity> DUNGEON_PORTAL_TICKER = ClientProxy::dungeonPortalBlockEntityClientTick;
	public static final BlockEntityTicker<PortalGeneratorBlockEntity> PORTAL_GENERATOR_TICKER = ClientProxy::portalGeneratorBlockEntityClientTick;
	
	private static @Nullable LocalPlayer player()
	{
		Minecraft mc = Minecraft.getInstance();
		return mc.player;
	}
	
	@SubscribeEvent
	public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event)
	{
		event.registerSpriteSet(DungeonFist.DUNGEON_PORTAL_PARTICLE_TYPE.get(), DungeonPortalParticleProvider::new);
	}
	
	@SubscribeEvent
	public static void onCloneClientPlayer(ClientPlayerNetworkEvent.Clone event)
	{
		LocalPlayer oldPlayer = event.getOldPlayer();
		LocalPlayer newPlayer = event.getNewPlayer();
		@Nullable PortalTimer oldTimer = oldPlayer.getExistingDataOrNull(DungeonFist.PORTAL_TIMER_ATTACHMENT);
		if (oldTimer != null)
		{
			newPlayer.setData(DungeonFist.PORTAL_TIMER_ATTACHMENT.get(), oldTimer);
		}
	}
	
	@SubscribeEvent
	public static void onRenderGuiLayerPre(RenderGuiLayerEvent.Pre event)
	{
		if (event.getName().equals(VanillaGuiLayers.CAMERA_OVERLAYS))
		{
			@Nullable LocalPlayer player = Minecraft.getInstance().player;
			if (player == null)
				return;

			PortalTimer timer = player.getData(DungeonFist.PORTAL_TIMER_ATTACHMENT);
			int portalTicks = timer.portalTime();
			if (portalTicks <= 0)
				return;
			
			
			Level level = player.level();
			if (level.getBlockEntity(timer.portalPos()) instanceof DungeonPortalBlockEntity portal)
			{
				float maxPortalTicks = 80F;
				float partialPortalTicks = event.getPartialTick().getGameTimeDeltaTicks() + (float)portalTicks;
				float multiplier = partialPortalTicks / maxPortalTicks;
				
				// do the overlay
				GuiGraphicsExtractor graphics = event.getGuiGraphics();
				int width = graphics.guiWidth();
				int height = graphics.guiHeight();
//				float red = 0.1F * multiplier + 0.8F;
//				float green = 0.8F * multiplier + 0.1F;
//				float blue = 0.1F * multiplier + 0.8F;
				var color = portal.getColor();
				float red = multiplier * color.red();
				float green = multiplier * color.green();
				float blue = multiplier * color.blue();
				float alpha = 0.8F * multiplier;
				int colorInt = ARGB.colorFromFloat(alpha, red, green, blue);
				graphics.fill(0, 0, width, height, colorInt);
				
				if (timer.inPortal())
				{
					// then spawn particles everywhere
					RandomSource rand = level.getRandom();
					int totalParticles = portalTicks/4;
					double xBase = player.getX();
					double yBase = player.getY();
					double zBase = player.getZ();
					for (int i=0; i < totalParticles; i++)
					{
						double xOff = rand.nextDouble()*10D - 5D;
						double yOff = rand.nextDouble()*10D - 5D;
						double zOff = rand.nextDouble()*10D - 5D;
						double x = xBase + xOff;
						double y = yBase + yOff + 1D;
						double z = zBase + zOff;
						level.addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0, 0);
					}
				}
			}
		}
	}
	
	private static void dungeonPortalBlockEntityClientTick(Level level, BlockPos pos, BlockState state, DungeonPortalBlockEntity be)
	{
		RandomSource rand = level.getRandom();
		
		// generate portal particles in a billboard facing the player
    	LocalPlayer player = player();
    	if (player == null)
    		return;
    	
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
//    		double depth = (height-0.5D) * radius * 0.2D;
    		
    		double pX = radius*Math.cos(angle)+blockX;
    		double pZ = radius*Math.sin(angle)+blockZ;
    		
    		double vX = (blockX - pX) * 0.015D;
    		double vZ = (blockZ - pZ) * 0.015D;
    		double vY = (pos.getY() + 1.5D - pY) * 0.015D;
    		
    		level.addParticle(be.getColor(),
    			pX, pY, pZ, vX, vY, vZ);
    			
    	}
	}
	
	private static void portalGeneratorBlockEntityClientTick(Level level, BlockPos pos, BlockState state, PortalGeneratorBlockEntity be)
	{
		RandomSource rand = level.getRandom();

		level.playSound(player(), pos, SoundEvents.PHANTOM_AMBIENT, SoundSource.BLOCKS, 0.2F,2F);
		
		Vec3 center = Vec3.atCenterOf(pos.above(2));
		float yaw = rand.nextFloat() * Mth.TWO_PI;
		float pitch = rand.nextFloat() * Mth.TWO_PI;
		float yOff = Mth.sin(pitch);
		float h = Mth.cos(pitch);
		float xOff = Mth.sin(yaw) * h;
		float zOff = Mth.cos(yaw) * h;
		double distFactor = 1D;
		double x = center.x + xOff*distFactor;
		double y = center.y + yOff*distFactor;
		double z = center.z + zOff*distFactor;
		double speed = -1D;
		double dx = speed*xOff;
		double dy = speed*yOff;
		double dz = speed*zOff;
		level.addParticle(ParticleTypes.ELECTRIC_SPARK, x, y, z, dx, dy, dz);
	}
}
