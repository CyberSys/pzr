package zombie.core.math;

import org.joml.Vector4f;


public class Vector4 extends Vector4f {

	public Vector4() {
	}

	public Vector4(float float1, float float2, float float3, float float4) {
		super(float1, float2, float3, float4);
	}

	public Vector4(org.lwjgl.util.vector.Vector4f vector4f) {
		super(vector4f.x, vector4f.y, vector4f.z, vector4f.w);
	}

	public org.lwjgl.util.vector.Vector4f Get() {
		org.lwjgl.util.vector.Vector4f vector4f = new org.lwjgl.util.vector.Vector4f();
		vector4f.set(this.x, this.y, this.z, this.w);
		return vector4f;
	}

	public void Set(org.lwjgl.util.vector.Vector4f vector4f) {
		this.x = vector4f.x;
		this.y = vector4f.y;
		this.z = vector4f.z;
		this.w = vector4f.w;
	}
}
