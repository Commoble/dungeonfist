package net.commoble.dungeonfist;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public final class Subdivider
{
	public Subdivider() {}; // util class
	
	private static enum XZAxis {
		X,Z;
		
		/**
		 * {@return random axis to subdivide on, cut will be parallel to this axis; if size across one axis is 1 then will not subdivide that axis further}
		 * @param xSize int width of box, must be > 1 if zSize is 1
		 * @param zSize int length of box, must be > 1 if xSize is 1
		 * @param random random
		 */
		public static XZAxis randomCutAxis(int xSize, int zSize, RandomSource random)
		{
			if (xSize <= 1)
			{
				return X;
			}
			else if (zSize <= 1)
			{
				return Z;
			}
			else
			{
				return random.nextBoolean() ? X : Z;
			}
		}
	}
	
	public static List<BoundingBox> subdivideXZ(BoundingBox baseBox, int subdivisions, RandomSource random)
	{
		if (subdivisions <= 0)
			return List.of(baseBox);
		

		
		int xSize = baseBox.getXSpan();
		int zSize = baseBox.getZSpan();
		
		if (xSize <= 1 && zSize <= 1)
		{
			return List.of(baseBox);
		}

		int minX = baseBox.minX();
		int minY = baseBox.minY();
		int minZ = baseBox.minZ();
		int maxX = baseBox.maxX();
		int maxY = baseBox.maxY();
		int maxZ = baseBox.maxZ();
		
		XZAxis cutAxis = XZAxis.randomCutAxis(xSize, zSize, random);
		BoundingBox first,second;
		if (cutAxis == XZAxis.X)
		{
			int firstMaxZ = baseBox.minZ() + random.nextInt(zSize-1);
			int secondMinZ = firstMaxZ + 1;
			first = new BoundingBox(
				minX, minY, minZ,
				maxX, maxY, firstMaxZ);
			second = new BoundingBox(
				minX, minY, secondMinZ,
				maxX, maxY, maxZ);					
		}
		else
		{
			int firstMaxX = baseBox.minX() + random.nextInt(xSize-1);
			int secondMinX = firstMaxX + 1;
			first = new BoundingBox(
				minX, minY, minZ,
				firstMaxX, maxY, maxZ);
			second = new BoundingBox(
				secondMinX, minY, minZ,
				maxX, maxY, maxZ);
		}
		
		List<BoundingBox> firstResults = subdivideXZ(first, subdivisions-1, random);
		List<BoundingBox> secondResults = subdivideXZ(second, subdivisions-1, random);
		List<BoundingBox> results = new ArrayList<>(firstResults.size() + secondResults.size());
		results.addAll(firstResults);
		results.addAll(secondResults);
		
		return results;
	}
}
