package com.github.commoble.dungeonfist.blocks;

import com.github.commoble.dungeonfist.DungeonFist;
import com.github.commoble.dungeonfist.registry.TileEntityTypeRegistrar;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

public class DungeonPortalTileEntity extends TileEntity implements ITickableTileEntity
{
	public DungeonPortalTileEntity()
	{
		super(TileEntityTypeRegistrar.DUNGEON_PORTAL);
	}

	public static final long RED_MASK = 0x1FFFFFL;	// least signficant 21 bits
	public static final long GREEN_MASK = 0x7FFFFE00000L;	// next 22 bits
	public static final long BLUE_MASK = 0xFFFFF80000000000L;	// most significant 21 bits
	
	// id based on position and seed, use getID() to ensure values are initialized
	private Long id = null;
	
	// color data of particles based on id, use getParticleRed, etc to ensure values are initialized
	private Float red = null;
	private Float green = null;
	private Float blue = null;
	
	/**
	 * Returns a non-random, arbitrary number based on world seed and tile location
	 */
	public long getID()
	{
		if (this.id == null)
		{
			// avoid zeros because they muck with multiplication
			long value = this.world.getSeed();
			if (value == 0L) value = 2L;
			long xval = this.pos.getX();
			value = value * xval + (7*this.pos.getY());
			if (value == 0L) value = 2L;
			long zval = ((long)this.pos.getZ() << 10L);
			value = value * zval + (3*this.pos.getZ());
			if (value == 0L) value = 2L;
			long yval = ((long)this.pos.getY() << 20L);
			value = value * yval + (5*this.pos.getY());
			this.id = new Long(value);
		}
		return this.id.longValue();
	}
	
	// get the particle color values using the least significant 21 bits of the ID
	public float getParticleRed()
	{
		if (this.red == null)
		{
			long bits = (this.getID() * this.getID()) % 107L;
			this.red = new Float(bits / 107F);
		}
		return this.red.floatValue();
	}
	
	// get the particle color values using the middle 22 bits of the ID
	public float getParticleGreen()
	{
		if (this.green == null)
		{
			this.green = new Float(this.getID() * this.getID() * this.getID() % 107L / 107F);
		}
		return this.green.floatValue();
	}
	
	// get the particle color values using the most significant 21 bits of the ID
	public float getParticleBlue()
	{
		if (this.blue == null)
		{
			this.blue = new Float(this.getID() * this.getID() * this.getID() * this.getID() % 107L / 107F);
		}
		return this.blue.floatValue();
	}
	
	@Override
	public void tick()
	{
    	if (this.world.isRemote)
    	{
    		// generate portal particles in a billboard facing the player
        	ClientPlayerEntity player = Minecraft.getInstance().player;
        	double playerX = player.posX;
        	double playerZ = player.posZ;
        	
        	// get center of block; blockpos's world position is a corner of the block, add 0.5 to both coordinates
        	double blockX = this.pos.getX() + 0.5D;
        	double blockZ = this.pos.getZ() + 0.5D;
        	
        	double dZ = blockZ - playerZ;
        	double dX = blockX - playerX;
        	
        	double angle = Math.atan2(dZ, dX) + (Math.PI * 0.5D);
        	
        	// generate a bunch of particles
        	int pcount = this.world.rand.nextInt(40) + 30;
        	for (int i=0; i<pcount; i++)
        	{
        		// generate particles in the 3-block column with this tile at the center
        		double height = this.world.rand.nextDouble();
        		double pY = -0.8D + height*2.6D + this.pos.getY();
        		
        		// horizontal radius from center pole is based on height
        		double radius = -3D*height*(height-1D) * (this.world.rand.nextDouble()-0.5D);
        		// this causes max diameter to be 0 when height value is 0 or 1, and 0.75 when height is 0.5
        		// radius is now between -0.375 and 0.375
        		
        		// depth potential gets wider further away from the center
        		double depth = (height-0.5D) * radius * 0.2D;
        		
        		double pX = radius*Math.cos(angle)+blockX;
        		double pZ = radius*Math.sin(angle)+blockZ;
        		
        		double vX = (blockX - pX) * 0.015D;
        		double vZ = (blockZ - pZ) * 0.015D;
        		double vY = (this.pos.getY() + 1.5D - pY) * 0.015D;
        		
        		DungeonFist.clientProxy.ifPresent(proxy -> proxy.spawnDungeonPortalParticle(this.world, pX, pY, pZ, vX, vY, vZ,
        				this.getParticleRed(), this.getParticleGreen(), this.getParticleBlue()));
        	}
    	}
	}
	
	
}
