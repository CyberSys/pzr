package zombie.core.math;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;


public final class Matrix4 extends Matrix4f {

	public Matrix4(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16) {
		super(float1, float2, float3, float4, float5, float6, float7, float8, float9, float10, float11, float12, float13, float14, float15, float16);
	}

	public Matrix4(Matrix4 matrix4) {
		super((Matrix4fc)matrix4);
	}

	public org.lwjgl.util.vector.Matrix4f Get() {
		org.lwjgl.util.vector.Matrix4f matrix4f = new org.lwjgl.util.vector.Matrix4f();
		matrix4f.m00 = this.m00();
		matrix4f.m01 = this.m01();
		matrix4f.m02 = this.m02();
		matrix4f.m03 = this.m03();
		matrix4f.m10 = this.m10();
		matrix4f.m11 = this.m11();
		matrix4f.m12 = this.m12();
		matrix4f.m13 = this.m13();
		matrix4f.m20 = this.m20();
		matrix4f.m21 = this.m21();
		matrix4f.m22 = this.m22();
		matrix4f.m23 = this.m23();
		matrix4f.m30 = this.m30();
		matrix4f.m31 = this.m31();
		matrix4f.m32 = this.m32();
		matrix4f.m33 = this.m33();
		return matrix4f;
	}

	public void Set(org.lwjgl.util.vector.Matrix4f matrix4f) {
		this.set(matrix4f.m00, matrix4f.m01, matrix4f.m02, matrix4f.m03, matrix4f.m10, matrix4f.m11, matrix4f.m12, matrix4f.m13, matrix4f.m20, matrix4f.m21, matrix4f.m22, matrix4f.m23, matrix4f.m30, matrix4f.m31, matrix4f.m32, matrix4f.m33);
	}
}
