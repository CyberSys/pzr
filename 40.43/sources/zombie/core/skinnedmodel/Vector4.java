package zombie.core.skinnedmodel;


public class Vector4 {
	public float x;
	public float y;
	public float z;
	public float w;

	public Vector4() {
		this(0.0F, 0.0F, 0.0F, 0.0F);
	}

	public Vector4(float float1, float float2, float float3, float float4) {
		this.x = float1;
		this.y = float2;
		this.z = float3;
		this.w = float4;
	}

	public Vector4(Vector4 vector4) {
		this.set(vector4);
	}

	public Vector4 set(float float1, float float2, float float3, float float4) {
		this.x = float1;
		this.y = float2;
		this.z = float3;
		this.w = float4;
		return this;
	}

	public Vector4 set(Vector4 vector4) {
		return this.set(vector4.x, vector4.y, vector4.z, vector4.w);
	}
}
