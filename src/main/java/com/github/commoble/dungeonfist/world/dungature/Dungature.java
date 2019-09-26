package com.github.commoble.dungeonfist.world.dungature;

import java.util.Random;

import com.github.commoble.dungeonfist.util.Rect;
import com.github.commoble.dungeonfist.util.Room;
import com.github.commoble.dungeonfist.util.Transform;
import com.github.commoble.dungeonfist.util.TransformHelper;
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
	 * @param rand Should NOT be used for generating the general shape of the dungature; should be used for chest loot tables, etc
	 */
	public abstract void place(Rect rect, Rect chunkRect, Room room, IWorld world, Random rand);
	
	public Dungature withTransform(Transform transform)
	{
		this.transform = transform;
		return this;
	}
	
	public Dungature withRandomTransform(int squareSize, Rect targetRect, Room room, Random rand)
	{
		Rect baseRect = Rect.SQUARE_RECTS[squareSize];
		int rotation = rand.nextInt(4);
		int mirror = rand.nextInt(4);
		Vec2i translation = TransformHelper.getRandomTranslation(baseRect, targetRect, rand);
		int yTranslation = 0;
		return this.withTransform(new Transform(rotation, mirror, translation, yTranslation, Vec3i.NULL_VECTOR));
	}
}
