package org.joml;


public class FrustumRayBuilder {
	private float nxnyX;
	private float nxnyY;
	private float nxnyZ;
	private float pxnyX;
	private float pxnyY;
	private float pxnyZ;
	private float pxpyX;
	private float pxpyY;
	private float pxpyZ;
	private float nxpyX;
	private float nxpyY;
	private float nxpyZ;
	private float cx;
	private float cy;
	private float cz;

	public FrustumRayBuilder() {
	}

	public FrustumRayBuilder(Matrix4fc matrix4fc) {
		this.set(matrix4fc);
	}

	public FrustumRayBuilder set(Matrix4fc matrix4fc) {
		float float1 = matrix4fc.m03() + matrix4fc.m00();
		float float2 = matrix4fc.m13() + matrix4fc.m10();
		float float3 = matrix4fc.m23() + matrix4fc.m20();
		float float4 = matrix4fc.m33() + matrix4fc.m30();
		float float5 = matrix4fc.m03() - matrix4fc.m00();
		float float6 = matrix4fc.m13() - matrix4fc.m10();
		float float7 = matrix4fc.m23() - matrix4fc.m20();
		float float8 = matrix4fc.m33() - matrix4fc.m30();
		float float9 = matrix4fc.m03() + matrix4fc.m01();
		float float10 = matrix4fc.m13() + matrix4fc.m11();
		float float11 = matrix4fc.m23() + matrix4fc.m21();
		float float12 = matrix4fc.m03() - matrix4fc.m01();
		float float13 = matrix4fc.m13() - matrix4fc.m11();
		float float14 = matrix4fc.m23() - matrix4fc.m21();
		float float15 = matrix4fc.m33() - matrix4fc.m31();
		this.nxnyX = float10 * float3 - float11 * float2;
		this.nxnyY = float11 * float1 - float9 * float3;
		this.nxnyZ = float9 * float2 - float10 * float1;
		this.pxnyX = float6 * float11 - float7 * float10;
		this.pxnyY = float7 * float9 - float5 * float11;
		this.pxnyZ = float5 * float10 - float6 * float9;
		this.nxpyX = float2 * float14 - float3 * float13;
		this.nxpyY = float3 * float12 - float1 * float14;
		this.nxpyZ = float1 * float13 - float2 * float12;
		this.pxpyX = float13 * float7 - float14 * float6;
		this.pxpyY = float14 * float5 - float12 * float7;
		this.pxpyZ = float12 * float6 - float13 * float5;
		float float16 = float6 * float3 - float7 * float2;
		float float17 = float7 * float1 - float5 * float3;
		float float18 = float5 * float2 - float6 * float1;
		float float19 = 1.0F / (float1 * this.pxpyX + float2 * this.pxpyY + float3 * this.pxpyZ);
		this.cx = (-this.pxpyX * float4 - this.nxpyX * float8 - float16 * float15) * float19;
		this.cy = (-this.pxpyY * float4 - this.nxpyY * float8 - float17 * float15) * float19;
		this.cz = (-this.pxpyZ * float4 - this.nxpyZ * float8 - float18 * float15) * float19;
		return this;
	}

	public Vector3fc origin(Vector3f vector3f) {
		vector3f.x = this.cx;
		vector3f.y = this.cy;
		vector3f.z = this.cz;
		return vector3f;
	}

	public Vector3fc dir(float float1, float float2, Vector3f vector3f) {
		float float3 = this.nxnyX + (this.nxpyX - this.nxnyX) * float2;
		float float4 = this.nxnyY + (this.nxpyY - this.nxnyY) * float2;
		float float5 = this.nxnyZ + (this.nxpyZ - this.nxnyZ) * float2;
		float float6 = this.pxnyX + (this.pxpyX - this.pxnyX) * float2;
		float float7 = this.pxnyY + (this.pxpyY - this.pxnyY) * float2;
		float float8 = this.pxnyZ + (this.pxpyZ - this.pxnyZ) * float2;
		float float9 = float3 + (float6 - float3) * float1;
		float float10 = float4 + (float7 - float4) * float1;
		float float11 = float5 + (float8 - float5) * float1;
		float float12 = Math.invsqrt(float9 * float9 + float10 * float10 + float11 * float11);
		vector3f.x = float9 * float12;
		vector3f.y = float10 * float12;
		vector3f.z = float11 * float12;
		return vector3f;
	}
}
