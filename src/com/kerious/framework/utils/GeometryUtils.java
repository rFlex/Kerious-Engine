package com.kerious.framework.utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class GeometryUtils {
	private static Vector2 tmp = new Vector2();
	
	public static float greedyEuclidean(float x1, float x2, float y1, float y2) {
		return (float) Math.sqrt((((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2))));
	}
	
	public static float lazyEuclidean(float x1, float x2, float y1, float y2) {
		return (((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2)));
	}
	
	public static float lazyEuclidean(Vector2 vec, Vector2 oth) {
		return lazyEuclidean(vec.x, oth.x, vec.y, oth.y);
	}
	
	public static float lazyEuclidean(Actor act, Actor oth) {
		return lazyEuclidean(act.getX(), oth.getX(), act.getY(), oth.getY());
	}
	
	public static float lazyVectorLength(float x, float y) {
		return (float) Math.sqrt((x * x) + (y * y));
	}
	
	public static float lazyVectorLength(Vector2 vec) {
		return lazyVectorLength(vec.x, vec.y);
	}
	
	public static Vector2 normalizeVector(float x, float y) {
		tmp.x = x;
		tmp.y = y;
		
		return angleToVector(tmp.angle());
	}
	
	public static Vector2 angleToVector(float angle) {
		tmp.x = MathUtils.cos(angle * MathUtils.degreesToRadians);
		tmp.y = MathUtils.sin(angle * MathUtils.degreesToRadians);
		
		return tmp;
	}

	public static Rectangle commonRectangle(Rectangle rect1, Rectangle rect2) {
		float x1 = Math.max(rect1.x, rect2.x);
		float x2 = Math.min(rect1.x + rect1.width, rect2.x + rect2.width);
		float y1 = Math.max(rect1.y, rect2.y);
		float y2 = Math.min(rect1.y + rect1.height, rect2.y + rect2.height);
		return new Rectangle(x1, y1, x2 - x1, y2 - y1);
	}
}
