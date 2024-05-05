package javax.vecmath;

import java.io.Serializable;


public class Vector4f extends Tuple4f implements Serializable {
	static final long serialVersionUID = 8749319902347760659L;

	public Vector4f(float float1, float float2, float float3, float float4) {
		super(float1, float2, float3, float4);
	}

	public Vector4f(float[] floatArray) {
		super(floatArray);
	}

	public Vector4f(Vector4f vector4f) {
		super((Tuple4f)vector4f);
	}

	public Vector4f(Vector4d vector4d) {
		super((Tuple4d)vector4d);
	}

	public Vector4f(Tuple4f tuple4f) {
		super(tuple4f);
	}

	public Vector4f(Tuple4d tuple4d) {
		super(tuple4d);
	}

	public Vector4f(Tuple3f tuple3f) {
		super(tuple3f.x, tuple3f.y, tuple3f.z, 0.0F);
	}

	public Vector4f() {
	}

	public final void set(Tuple3f tuple3f) {
		this.x = tuple3f.x;
		this.y = tuple3f.y;
		this.z = tuple3f.z;
		this.w = 0.0F;
	}

	public final float length() {
		return (float)Math.sqrt((double)(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w));
	}

	public final float lengthSquared() {
		return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
	}

	public final float dot(Vector4f vector4f) {
		return this.x * vector4f.x + this.y * vector4f.y + this.z * vector4f.z + this.w * vector4f.w;
	}

	public final void normalize(Vector4f vector4f) {
		float float1 = (float)(1.0 / Math.sqrt((double)(vector4f.x * vector4f.x + vector4f.y * vector4f.y + vector4f.z * vector4f.z + vector4f.w * vector4f.w)));
		this.x = vector4f.x * float1;
		this.y = vector4f.y * float1;
		this.z = vector4f.z * float1;
		this.w = vector4f.w * float1;
	}

	public final void normalize() {
		float float1 = (float)(1.0 / Math.sqrt((double)(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w)));
		this.x *= float1;
		this.y *= float1;
		this.z *= float1;
		this.w *= float1;
	}

	public final float angle(Vector4f vector4f) {
		double double1 = (double)(this.dot(vector4f) / (this.length() * vector4f.length()));
		if (double1 < -1.0) {
			double1 = -1.0;
		}

		if (double1 > 1.0) {
			double1 = 1.0;
		}

		return (float)Math.acos(double1);
	}
}
