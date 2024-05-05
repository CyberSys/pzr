package org.lwjglx.util.vector;

import java.io.Serializable;
import java.nio.FloatBuffer;


public abstract class Vector implements Serializable,ReadableVector {

	protected Vector() {
	}

	public final float length() {
		return (float)Math.sqrt((double)this.lengthSquared());
	}

	public abstract float lengthSquared();

	public abstract Vector load(FloatBuffer floatBuffer);

	public abstract Vector negate();

	public final Vector normalise() {
		float float1 = this.length();
		if (float1 != 0.0F) {
			float float2 = 1.0F / float1;
			return this.scale(float2);
		} else {
			throw new IllegalStateException("Zero length vector");
		}
	}

	public abstract Vector store(FloatBuffer floatBuffer);

	public abstract Vector scale(float float1);
}
