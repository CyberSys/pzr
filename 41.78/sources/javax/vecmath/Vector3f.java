package javax.vecmath;

import java.io.Serializable;


public class Vector3f extends Tuple3f implements Serializable {
	static final long serialVersionUID = -7031930069184524614L;

	public Vector3f(float float1, float float2, float float3) {
		super(float1, float2, float3);
	}

	public Vector3f(float[] floatArray) {
		super(floatArray);
	}

	public Vector3f(Vector3f vector3f) {
		super((Tuple3f)vector3f);
	}

	public Vector3f(Vector3d vector3d) {
		super((Tuple3d)vector3d);
	}

	public Vector3f(Tuple3f tuple3f) {
		super(tuple3f);
	}

	public Vector3f(Tuple3d tuple3d) {
		super(tuple3d);
	}

	public Vector3f() {
	}

	public final float lengthSquared() {
		return this.x * this.x + this.y * this.y + this.z * this.z;
	}

	public final float length() {
		return (float)Math.sqrt((double)(this.x * this.x + this.y * this.y + this.z * this.z));
	}

	public final void cross(Vector3f vector3f, Vector3f vector3f2) {
		float float1 = vector3f.y * vector3f2.z - vector3f.z * vector3f2.y;
		float float2 = vector3f2.x * vector3f.z - vector3f2.z * vector3f.x;
		this.z = vector3f.x * vector3f2.y - vector3f.y * vector3f2.x;
		this.x = float1;
		this.y = float2;
	}

	public final float dot(Vector3f vector3f) {
		return this.x * vector3f.x + this.y * vector3f.y + this.z * vector3f.z;
	}

	public final void normalize(Vector3f vector3f) {
		float float1 = (float)(1.0 / Math.sqrt((double)(vector3f.x * vector3f.x + vector3f.y * vector3f.y + vector3f.z * vector3f.z)));
		this.x = vector3f.x * float1;
		this.y = vector3f.y * float1;
		this.z = vector3f.z * float1;
	}

	public final void normalize() {
		float float1 = (float)(1.0 / Math.sqrt((double)(this.x * this.x + this.y * this.y + this.z * this.z)));
		this.x *= float1;
		this.y *= float1;
		this.z *= float1;
	}

	public final float angle(Vector3f vector3f) {
		double double1 = (double)(this.dot(vector3f) / (this.length() * vector3f.length()));
		if (double1 < -1.0) {
			double1 = -1.0;
		}

		if (double1 > 1.0) {
			double1 = 1.0;
		}

		return (float)Math.acos(double1);
	}
}
