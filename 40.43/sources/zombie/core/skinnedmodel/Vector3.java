package zombie.core.skinnedmodel;


public class Vector3 {
	private float x;
	private float y;
	private float z;

	public Vector3() {
		this(0.0F, 0.0F, 0.0F);
	}

	public Vector3(float float1, float float2, float float3) {
		this.x = float1;
		this.y = float2;
		this.z = float3;
	}

	public Vector3(Vector3 vector3) {
		this.set(vector3);
	}

	public float x() {
		return this.x;
	}

	public Vector3 x(float float1) {
		this.x = float1;
		return this;
	}

	public float y() {
		return this.y;
	}

	public Vector3 y(float float1) {
		this.y = float1;
		return this;
	}

	public float z() {
		return this.z;
	}

	public Vector3 z(float float1) {
		this.z = float1;
		return this;
	}

	public Vector3 set(float float1, float float2, float float3) {
		this.x = float1;
		this.y = float2;
		this.z = float3;
		return this;
	}

	public Vector3 set(Vector3 vector3) {
		return this.set(vector3.x(), vector3.y(), vector3.z());
	}

	public Vector3 reset() {
		this.x = this.y = this.z = 0.0F;
		return this;
	}

	public float length() {
		return (float)Math.sqrt((double)(this.x * this.x + this.y * this.y + this.z * this.z));
	}

	public Vector3 normalize() {
		float float1 = this.length();
		this.x /= float1;
		this.y /= float1;
		this.z /= float1;
		return this;
	}

	public float dot(Vector3 vector3) {
		return this.x * vector3.x + this.y * vector3.y + this.z * vector3.z;
	}

	public Vector3 cross(Vector3 vector3) {
		return new Vector3(this.y() * vector3.z() - vector3.y() * this.z(), vector3.z() * this.x() - this.z() * vector3.x(), this.x() * vector3.y() - vector3.x() * this.y());
	}

	public Vector3 add(float float1, float float2, float float3) {
		this.x += float1;
		this.y += float2;
		this.z += float3;
		return this;
	}

	public Vector3 add(Vector3 vector3) {
		return this.add(vector3.x(), vector3.y(), vector3.z());
	}

	public Vector3 sub(float float1, float float2, float float3) {
		this.x -= float1;
		this.y -= float2;
		this.z -= float3;
		return this;
	}

	public Vector3 sub(Vector3 vector3) {
		return this.sub(vector3.x(), vector3.y(), vector3.z());
	}

	public Vector3 mul(float float1) {
		return this.mul(float1, float1, float1);
	}

	public Vector3 mul(float float1, float float2, float float3) {
		this.x *= float1;
		this.y *= float2;
		this.z *= float3;
		return this;
	}

	public Vector3 mul(Vector3 vector3) {
		return this.mul(vector3.x(), vector3.y(), vector3.z());
	}
}
