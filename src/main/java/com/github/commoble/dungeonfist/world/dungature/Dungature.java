package com.github.commoble.dungeonfist.world.dungature;

import java.util.Random;
import java.util.stream.Stream;

import com.github.commoble.dungeonfist.util.Rect;
import com.github.commoble.dungeonfist.util.Room;
import com.github.commoble.dungeonfist.util.Transform;
import com.github.commoble.dungeonfist.util.TransformHelper;
import com.github.commoble.dungeonfist.util.TransformedCoord;
import com.github.commoble.dungeonfist.util.Vec2i;

import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorld;

/**
 * I'd call these Features but Features are already called Features and these aren't Features
 **/
public abstract class Dungature
{
	public Transform transform = Transform.NONE;
	/**
	 * @param rect The rect from the room's subdivided floor rects
	 * @param chunkRect	The rect representing the chunk that this dungature or a portion thereof is being placed in
	 * @param room	The room that this dungature is being placed in
	 * @param world The world that this dungature is being placed in
	 * @param rand Random from the feature placer, not from the Room. Should only be used within a single chunk.
	 */
	public abstract void place(Rect rect, Rect chunkRect, Room room, IWorld world, Random rand);
	
	public Dungature withTransform(Transform transform)
	{
		this.transform = transform;
		return this;
	}
	
	public Dungature withRandomTransform(int squareSize, DungatureContext context)
	{
		Rect baseRect = Rect.SQUARE_RECTS[squareSize];
		int rotation = context.rand.nextInt(4);
		int mirror = context.rand.nextInt(4);
		Vec2i translation = TransformHelper.getRandomTranslation(baseRect, context.rect, context.rand);
		int yTranslation = 0;
		return this.withTransform(new Transform(rotation, mirror, translation, yTranslation, Vec3i.NULL_VECTOR));
	}
	
	
	public Dungature withRotation(int rotation)
	{
		return this.withTransform(this.transform.withRotation(rotation));
	}
	
	/**
	 * Returns the coordinates for a square rect of START 0,0 and SIZE size,size,
	 * transformed via this Dungature's transform into a stream of TransformedCoords
	 * that exist within the chunk defined by the given Rect
	 * @param size should be no less than 1 and no greater than 16
	 * @param chunkRect corresponds with a chunk that the returned coordinates should exist in
	 * @return stream of TransformedCoords whose final vectors are measured in worldspace and exist in the chunk
	 */
	public Stream<TransformedCoord> transformSquareIntoChunk(int size, Rect chunkRect)
	{
		return Rect.SQUARE_RECTS[size].coords().stream()
				.map(origin -> TransformHelper.withStandardTransforms(origin, size, this.transform))
				.filter(pair -> chunkRect.contains(pair.finalVec));
	}
}
