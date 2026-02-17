package net.commoble.dungeonfist;

import net.commoble.structurebuddy.api.util.BoxBuddy;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public final class MoreBoxing
{
	public MoreBoxing() {} // util class
	
	/**
	 * {@return Vec3i offset of from's minimal corner to to's minimal corner}
	 * @param from BoundingBox to measure from
	 * @param to BoundingBox to measure to
	 */
	public static Vec3i getMinimalOffset(BoundingBox from, BoundingBox to)
	{
		return BoxBuddy.minCorner(to).subtract(BoxBuddy.minCorner(from));
	}
	
	public static BoundingBox moved(BoundingBox box, Vec3i offset)
	{
		return box.moved(offset.getX(), offset.getY(), offset.getZ());
	}
	
	public static BoundingBox inflateToward(BoundingBox box, Direction dir)
	{
		int minX = box.minX();
		int minY = box.minY();
		int minZ = box.minZ();
		int maxX = box.maxX();
		int maxY = box.maxY();
		int maxZ = box.maxZ();
		return switch(dir) {
			case DOWN -> new BoundingBox(minX, minY-1, minZ, maxX, maxY, maxZ);
			case UP -> new BoundingBox (minX, minY, minZ, maxX, maxY+1, maxZ);
			case NORTH -> new BoundingBox (minX, minY, minZ-1, maxX, maxY, maxZ);
			case SOUTH -> new BoundingBox (minX, minY, minZ, maxX, maxY, maxZ+1);
			case WEST -> new BoundingBox (minX-1, minY, minZ, maxX, maxY, maxZ);
			case EAST -> new BoundingBox (minX, minY, minZ, maxX+1, maxY, maxZ);
		};
	}
}
