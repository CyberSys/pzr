package zombie.core.math;

import org.joml.Vector3f;


public class Vector3 extends Vector3f {

	public Vector3() {
	}

	public Vector3(float float1, float float2, float float3) {
		super(float1, float2, float3);
	}

	public Vector3(org.lwjgl.util.vector.Vector3f vector3f) {
		super(vector3f.x, vector3f.y, vector3f.z);
	}

	public Vector3(Vector3 vector3) {
		super(vector3.x, vector3.y, vector3.z);
	}

	public static org.lwjgl.util.vector.Vector3f addScaled(org.lwjgl.util.vector.Vector3f vector3f, org.lwjgl.util.vector.Vector3f vector3f2, float float1, org.lwjgl.util.vector.Vector3f vector3f3) {
		vector3f3.set(vector3f.x + vector3f2.x * float1, vector3f.y + vector3f2.y * float1, vector3f.z + vector3f2.z * float1);
		return vector3f3;
	}

	public static org.lwjgl.util.vector.Vector3f setScaled(org.lwjgl.util.vector.Vector3f vector3f, float float1, org.lwjgl.util.vector.Vector3f vector3f2) {
		vector3f2.set(vector3f.x * float1, vector3f.y * float1, vector3f.z * float1);
		return vector3f2;
	}

	public org.lwjgl.util.vector.Vector3f Get() {
		org.lwjgl.util.vector.Vector3f vector3f = new org.lwjgl.util.vector.Vector3f();
		vector3f.set(this.x, this.y, this.z);
		return vector3f;
	}

	public void Set(org.lwjgl.util.vector.Vector3f vector3f) {
		this.x = vector3f.x;
		this.y = vector3f.y;
		this.z = vector3f.z;
	}

	public Vector3 reset() {
		this.x = this.y = this.z = 0.0F;
		return this;
	}

	public float dot(Vector3 vector3) {
		return this.x * vector3.x + this.y * vector3.y + this.z * vector3.z;
	}

	public Vector3 cross(Vector3 vector3) {
		return new Vector3(this.y() * vector3.z() - vector3.y() * this.z(), vector3.z() * this.x() - this.z() * vector3.x(), this.x() * vector3.y() - vector3.x() * this.y());
	}
}
