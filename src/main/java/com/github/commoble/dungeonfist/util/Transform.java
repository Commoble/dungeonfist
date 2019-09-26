package com.github.commoble.dungeonfist.util;

import net.minecraft.util.Mirror;
import net.minecraft.util.math.Vec3i;

public class Transform
{
	public static final Transform NONE = new Transform(0,0,null,0,null);
	
	public final int rotation;	// 0,1,2,3 -> 0, 90, 180, 270 degrees
	public final int mirror;	// 0,1,2,3 -> none, X, Z, both
	public final Vec2i translation;	//
	public final int yTranslation;
	public final Vec3i scale;
	
	public Transform (int rotation, int mirror, Vec2i translation, int yTranslation, Vec3i scale)
	{
		this.rotation = rotation;
		this.mirror = mirror;
		this.translation = translation == null ? Vec2i.ZERO : translation;
		this.yTranslation = yTranslation;
		this.scale = scale == null ? new Vec3i(1,1,1) : scale;
	}
	
	public Transform withRotation(int rotation)
	{
		return new Transform(rotation, this.mirror, this.translation, this.yTranslation, this.scale);
	}
	
	public Transform withMirror(int mirror)
	{
		return new Transform(this.rotation, mirror, this.translation, this.yTranslation, this.scale);
	}
	
	public Transform withRotationAndMirror(int rotation, int mirror)
	{
		return new Transform(rotation, mirror, this.translation, this.yTranslation, this.scale);
	}
	
	public Transform withTranslation(Vec2i translation, int yTranslation)
	{
		return new Transform(this.rotation, this.mirror, translation, yTranslation, this.scale);
	}
	
	public Transform withScale(Vec3i scale)
	{
		return new Transform(this.rotation, this.mirror, this.translation, this.yTranslation, scale);
	}
	
	public Mirror xAxisMirror()
	{
		return this.mirror % 2 == 0 ? Mirror.NONE : Mirror.FRONT_BACK;
	}
	
	public Mirror zAxisMirror()
	{
		return this.mirror / 2 == 0 ? Mirror.NONE : Mirror.LEFT_RIGHT;
	}
}
