package org.lwjglx.util.vector;

import java.io.Serializable;
import java.nio.FloatBuffer;


public class Matrix4f extends Matrix implements Serializable {
	private static final long serialVersionUID = 1L;
	public float m00;
	public float m01;
	public float m02;
	public float m03;
	public float m10;
	public float m11;
	public float m12;
	public float m13;
	public float m20;
	public float m21;
	public float m22;
	public float m23;
	public float m30;
	public float m31;
	public float m32;
	public float m33;

	public Matrix4f() {
		this.setIdentity();
	}

	public Matrix4f(Matrix4f matrix4f) {
		this.load(matrix4f);
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(this.m00).append(' ').append(this.m10).append(' ').append(this.m20).append(' ').append(this.m30).append('\n');
		stringBuilder.append(this.m01).append(' ').append(this.m11).append(' ').append(this.m21).append(' ').append(this.m31).append('\n');
		stringBuilder.append(this.m02).append(' ').append(this.m12).append(' ').append(this.m22).append(' ').append(this.m32).append('\n');
		stringBuilder.append(this.m03).append(' ').append(this.m13).append(' ').append(this.m23).append(' ').append(this.m33).append('\n');
		return stringBuilder.toString();
	}

	public Matrix setIdentity() {
		return setIdentity(this);
	}

	public static Matrix4f setIdentity(Matrix4f matrix4f) {
		matrix4f.m00 = 1.0F;
		matrix4f.m01 = 0.0F;
		matrix4f.m02 = 0.0F;
		matrix4f.m03 = 0.0F;
		matrix4f.m10 = 0.0F;
		matrix4f.m11 = 1.0F;
		matrix4f.m12 = 0.0F;
		matrix4f.m13 = 0.0F;
		matrix4f.m20 = 0.0F;
		matrix4f.m21 = 0.0F;
		matrix4f.m22 = 1.0F;
		matrix4f.m23 = 0.0F;
		matrix4f.m30 = 0.0F;
		matrix4f.m31 = 0.0F;
		matrix4f.m32 = 0.0F;
		matrix4f.m33 = 1.0F;
		return matrix4f;
	}

	public Matrix setZero() {
		return setZero(this);
	}

	public static Matrix4f setZero(Matrix4f matrix4f) {
		matrix4f.m00 = 0.0F;
		matrix4f.m01 = 0.0F;
		matrix4f.m02 = 0.0F;
		matrix4f.m03 = 0.0F;
		matrix4f.m10 = 0.0F;
		matrix4f.m11 = 0.0F;
		matrix4f.m12 = 0.0F;
		matrix4f.m13 = 0.0F;
		matrix4f.m20 = 0.0F;
		matrix4f.m21 = 0.0F;
		matrix4f.m22 = 0.0F;
		matrix4f.m23 = 0.0F;
		matrix4f.m30 = 0.0F;
		matrix4f.m31 = 0.0F;
		matrix4f.m32 = 0.0F;
		matrix4f.m33 = 0.0F;
		return matrix4f;
	}

	public Matrix4f load(Matrix4f matrix4f) {
		return load(matrix4f, this);
	}

	public static Matrix4f load(Matrix4f matrix4f, Matrix4f matrix4f2) {
		if (matrix4f2 == null) {
			matrix4f2 = new Matrix4f();
		}

		matrix4f2.m00 = matrix4f.m00;
		matrix4f2.m01 = matrix4f.m01;
		matrix4f2.m02 = matrix4f.m02;
		matrix4f2.m03 = matrix4f.m03;
		matrix4f2.m10 = matrix4f.m10;
		matrix4f2.m11 = matrix4f.m11;
		matrix4f2.m12 = matrix4f.m12;
		matrix4f2.m13 = matrix4f.m13;
		matrix4f2.m20 = matrix4f.m20;
		matrix4f2.m21 = matrix4f.m21;
		matrix4f2.m22 = matrix4f.m22;
		matrix4f2.m23 = matrix4f.m23;
		matrix4f2.m30 = matrix4f.m30;
		matrix4f2.m31 = matrix4f.m31;
		matrix4f2.m32 = matrix4f.m32;
		matrix4f2.m33 = matrix4f.m33;
		return matrix4f2;
	}

	public Matrix load(FloatBuffer floatBuffer) {
		this.m00 = floatBuffer.get();
		this.m01 = floatBuffer.get();
		this.m02 = floatBuffer.get();
		this.m03 = floatBuffer.get();
		this.m10 = floatBuffer.get();
		this.m11 = floatBuffer.get();
		this.m12 = floatBuffer.get();
		this.m13 = floatBuffer.get();
		this.m20 = floatBuffer.get();
		this.m21 = floatBuffer.get();
		this.m22 = floatBuffer.get();
		this.m23 = floatBuffer.get();
		this.m30 = floatBuffer.get();
		this.m31 = floatBuffer.get();
		this.m32 = floatBuffer.get();
		this.m33 = floatBuffer.get();
		return this;
	}

	public Matrix loadTranspose(FloatBuffer floatBuffer) {
		this.m00 = floatBuffer.get();
		this.m10 = floatBuffer.get();
		this.m20 = floatBuffer.get();
		this.m30 = floatBuffer.get();
		this.m01 = floatBuffer.get();
		this.m11 = floatBuffer.get();
		this.m21 = floatBuffer.get();
		this.m31 = floatBuffer.get();
		this.m02 = floatBuffer.get();
		this.m12 = floatBuffer.get();
		this.m22 = floatBuffer.get();
		this.m32 = floatBuffer.get();
		this.m03 = floatBuffer.get();
		this.m13 = floatBuffer.get();
		this.m23 = floatBuffer.get();
		this.m33 = floatBuffer.get();
		return this;
	}

	public Matrix store(FloatBuffer floatBuffer) {
		floatBuffer.put(this.m00);
		floatBuffer.put(this.m01);
		floatBuffer.put(this.m02);
		floatBuffer.put(this.m03);
		floatBuffer.put(this.m10);
		floatBuffer.put(this.m11);
		floatBuffer.put(this.m12);
		floatBuffer.put(this.m13);
		floatBuffer.put(this.m20);
		floatBuffer.put(this.m21);
		floatBuffer.put(this.m22);
		floatBuffer.put(this.m23);
		floatBuffer.put(this.m30);
		floatBuffer.put(this.m31);
		floatBuffer.put(this.m32);
		floatBuffer.put(this.m33);
		return this;
	}

	public Matrix storeTranspose(FloatBuffer floatBuffer) {
		floatBuffer.put(this.m00);
		floatBuffer.put(this.m10);
		floatBuffer.put(this.m20);
		floatBuffer.put(this.m30);
		floatBuffer.put(this.m01);
		floatBuffer.put(this.m11);
		floatBuffer.put(this.m21);
		floatBuffer.put(this.m31);
		floatBuffer.put(this.m02);
		floatBuffer.put(this.m12);
		floatBuffer.put(this.m22);
		floatBuffer.put(this.m32);
		floatBuffer.put(this.m03);
		floatBuffer.put(this.m13);
		floatBuffer.put(this.m23);
		floatBuffer.put(this.m33);
		return this;
	}

	public Matrix store3f(FloatBuffer floatBuffer) {
		floatBuffer.put(this.m00);
		floatBuffer.put(this.m01);
		floatBuffer.put(this.m02);
		floatBuffer.put(this.m10);
		floatBuffer.put(this.m11);
		floatBuffer.put(this.m12);
		floatBuffer.put(this.m20);
		floatBuffer.put(this.m21);
		floatBuffer.put(this.m22);
		return this;
	}

	public static Matrix4f add(Matrix4f matrix4f, Matrix4f matrix4f2, Matrix4f matrix4f3) {
		if (matrix4f3 == null) {
			matrix4f3 = new Matrix4f();
		}

		matrix4f3.m00 = matrix4f.m00 + matrix4f2.m00;
		matrix4f3.m01 = matrix4f.m01 + matrix4f2.m01;
		matrix4f3.m02 = matrix4f.m02 + matrix4f2.m02;
		matrix4f3.m03 = matrix4f.m03 + matrix4f2.m03;
		matrix4f3.m10 = matrix4f.m10 + matrix4f2.m10;
		matrix4f3.m11 = matrix4f.m11 + matrix4f2.m11;
		matrix4f3.m12 = matrix4f.m12 + matrix4f2.m12;
		matrix4f3.m13 = matrix4f.m13 + matrix4f2.m13;
		matrix4f3.m20 = matrix4f.m20 + matrix4f2.m20;
		matrix4f3.m21 = matrix4f.m21 + matrix4f2.m21;
		matrix4f3.m22 = matrix4f.m22 + matrix4f2.m22;
		matrix4f3.m23 = matrix4f.m23 + matrix4f2.m23;
		matrix4f3.m30 = matrix4f.m30 + matrix4f2.m30;
		matrix4f3.m31 = matrix4f.m31 + matrix4f2.m31;
		matrix4f3.m32 = matrix4f.m32 + matrix4f2.m32;
		matrix4f3.m33 = matrix4f.m33 + matrix4f2.m33;
		return matrix4f3;
	}

	public static Matrix4f sub(Matrix4f matrix4f, Matrix4f matrix4f2, Matrix4f matrix4f3) {
		if (matrix4f3 == null) {
			matrix4f3 = new Matrix4f();
		}

		matrix4f3.m00 = matrix4f.m00 - matrix4f2.m00;
		matrix4f3.m01 = matrix4f.m01 - matrix4f2.m01;
		matrix4f3.m02 = matrix4f.m02 - matrix4f2.m02;
		matrix4f3.m03 = matrix4f.m03 - matrix4f2.m03;
		matrix4f3.m10 = matrix4f.m10 - matrix4f2.m10;
		matrix4f3.m11 = matrix4f.m11 - matrix4f2.m11;
		matrix4f3.m12 = matrix4f.m12 - matrix4f2.m12;
		matrix4f3.m13 = matrix4f.m13 - matrix4f2.m13;
		matrix4f3.m20 = matrix4f.m20 - matrix4f2.m20;
		matrix4f3.m21 = matrix4f.m21 - matrix4f2.m21;
		matrix4f3.m22 = matrix4f.m22 - matrix4f2.m22;
		matrix4f3.m23 = matrix4f.m23 - matrix4f2.m23;
		matrix4f3.m30 = matrix4f.m30 - matrix4f2.m30;
		matrix4f3.m31 = matrix4f.m31 - matrix4f2.m31;
		matrix4f3.m32 = matrix4f.m32 - matrix4f2.m32;
		matrix4f3.m33 = matrix4f.m33 - matrix4f2.m33;
		return matrix4f3;
	}

	public static Matrix4f mul(Matrix4f matrix4f, Matrix4f matrix4f2, Matrix4f matrix4f3) {
		if (matrix4f3 == null) {
			matrix4f3 = new Matrix4f();
		}

		float float1 = matrix4f.m00 * matrix4f2.m00 + matrix4f.m10 * matrix4f2.m01 + matrix4f.m20 * matrix4f2.m02 + matrix4f.m30 * matrix4f2.m03;
		float float2 = matrix4f.m01 * matrix4f2.m00 + matrix4f.m11 * matrix4f2.m01 + matrix4f.m21 * matrix4f2.m02 + matrix4f.m31 * matrix4f2.m03;
		float float3 = matrix4f.m02 * matrix4f2.m00 + matrix4f.m12 * matrix4f2.m01 + matrix4f.m22 * matrix4f2.m02 + matrix4f.m32 * matrix4f2.m03;
		float float4 = matrix4f.m03 * matrix4f2.m00 + matrix4f.m13 * matrix4f2.m01 + matrix4f.m23 * matrix4f2.m02 + matrix4f.m33 * matrix4f2.m03;
		float float5 = matrix4f.m00 * matrix4f2.m10 + matrix4f.m10 * matrix4f2.m11 + matrix4f.m20 * matrix4f2.m12 + matrix4f.m30 * matrix4f2.m13;
		float float6 = matrix4f.m01 * matrix4f2.m10 + matrix4f.m11 * matrix4f2.m11 + matrix4f.m21 * matrix4f2.m12 + matrix4f.m31 * matrix4f2.m13;
		float float7 = matrix4f.m02 * matrix4f2.m10 + matrix4f.m12 * matrix4f2.m11 + matrix4f.m22 * matrix4f2.m12 + matrix4f.m32 * matrix4f2.m13;
		float float8 = matrix4f.m03 * matrix4f2.m10 + matrix4f.m13 * matrix4f2.m11 + matrix4f.m23 * matrix4f2.m12 + matrix4f.m33 * matrix4f2.m13;
		float float9 = matrix4f.m00 * matrix4f2.m20 + matrix4f.m10 * matrix4f2.m21 + matrix4f.m20 * matrix4f2.m22 + matrix4f.m30 * matrix4f2.m23;
		float float10 = matrix4f.m01 * matrix4f2.m20 + matrix4f.m11 * matrix4f2.m21 + matrix4f.m21 * matrix4f2.m22 + matrix4f.m31 * matrix4f2.m23;
		float float11 = matrix4f.m02 * matrix4f2.m20 + matrix4f.m12 * matrix4f2.m21 + matrix4f.m22 * matrix4f2.m22 + matrix4f.m32 * matrix4f2.m23;
		float float12 = matrix4f.m03 * matrix4f2.m20 + matrix4f.m13 * matrix4f2.m21 + matrix4f.m23 * matrix4f2.m22 + matrix4f.m33 * matrix4f2.m23;
		float float13 = matrix4f.m00 * matrix4f2.m30 + matrix4f.m10 * matrix4f2.m31 + matrix4f.m20 * matrix4f2.m32 + matrix4f.m30 * matrix4f2.m33;
		float float14 = matrix4f.m01 * matrix4f2.m30 + matrix4f.m11 * matrix4f2.m31 + matrix4f.m21 * matrix4f2.m32 + matrix4f.m31 * matrix4f2.m33;
		float float15 = matrix4f.m02 * matrix4f2.m30 + matrix4f.m12 * matrix4f2.m31 + matrix4f.m22 * matrix4f2.m32 + matrix4f.m32 * matrix4f2.m33;
		float float16 = matrix4f.m03 * matrix4f2.m30 + matrix4f.m13 * matrix4f2.m31 + matrix4f.m23 * matrix4f2.m32 + matrix4f.m33 * matrix4f2.m33;
		matrix4f3.m00 = float1;
		matrix4f3.m01 = float2;
		matrix4f3.m02 = float3;
		matrix4f3.m03 = float4;
		matrix4f3.m10 = float5;
		matrix4f3.m11 = float6;
		matrix4f3.m12 = float7;
		matrix4f3.m13 = float8;
		matrix4f3.m20 = float9;
		matrix4f3.m21 = float10;
		matrix4f3.m22 = float11;
		matrix4f3.m23 = float12;
		matrix4f3.m30 = float13;
		matrix4f3.m31 = float14;
		matrix4f3.m32 = float15;
		matrix4f3.m33 = float16;
		return matrix4f3;
	}

	public static Vector4f transform(Matrix4f matrix4f, Vector4f vector4f, Vector4f vector4f2) {
		if (vector4f2 == null) {
			vector4f2 = new Vector4f();
		}

		float float1 = matrix4f.m00 * vector4f.x + matrix4f.m10 * vector4f.y + matrix4f.m20 * vector4f.z + matrix4f.m30 * vector4f.w;
		float float2 = matrix4f.m01 * vector4f.x + matrix4f.m11 * vector4f.y + matrix4f.m21 * vector4f.z + matrix4f.m31 * vector4f.w;
		float float3 = matrix4f.m02 * vector4f.x + matrix4f.m12 * vector4f.y + matrix4f.m22 * vector4f.z + matrix4f.m32 * vector4f.w;
		float float4 = matrix4f.m03 * vector4f.x + matrix4f.m13 * vector4f.y + matrix4f.m23 * vector4f.z + matrix4f.m33 * vector4f.w;
		vector4f2.x = float1;
		vector4f2.y = float2;
		vector4f2.z = float3;
		vector4f2.w = float4;
		return vector4f2;
	}

	public Matrix transpose() {
		return this.transpose(this);
	}

	public Matrix4f translate(Vector2f vector2f) {
		return this.translate(vector2f, this);
	}

	public Matrix4f translate(Vector3f vector3f) {
		return this.translate(vector3f, this);
	}

	public Matrix4f scale(Vector3f vector3f) {
		return scale(vector3f, this, this);
	}

	public static Matrix4f scale(Vector3f vector3f, Matrix4f matrix4f, Matrix4f matrix4f2) {
		if (matrix4f2 == null) {
			matrix4f2 = new Matrix4f();
		}

		matrix4f2.m00 = matrix4f.m00 * vector3f.x;
		matrix4f2.m01 = matrix4f.m01 * vector3f.x;
		matrix4f2.m02 = matrix4f.m02 * vector3f.x;
		matrix4f2.m03 = matrix4f.m03 * vector3f.x;
		matrix4f2.m10 = matrix4f.m10 * vector3f.y;
		matrix4f2.m11 = matrix4f.m11 * vector3f.y;
		matrix4f2.m12 = matrix4f.m12 * vector3f.y;
		matrix4f2.m13 = matrix4f.m13 * vector3f.y;
		matrix4f2.m20 = matrix4f.m20 * vector3f.z;
		matrix4f2.m21 = matrix4f.m21 * vector3f.z;
		matrix4f2.m22 = matrix4f.m22 * vector3f.z;
		matrix4f2.m23 = matrix4f.m23 * vector3f.z;
		return matrix4f2;
	}

	public Matrix4f rotate(float float1, Vector3f vector3f) {
		return this.rotate(float1, vector3f, this);
	}

	public Matrix4f rotate(float float1, Vector3f vector3f, Matrix4f matrix4f) {
		return rotate(float1, vector3f, this, matrix4f);
	}

	public static Matrix4f rotate(float float1, Vector3f vector3f, Matrix4f matrix4f, Matrix4f matrix4f2) {
		if (matrix4f2 == null) {
			matrix4f2 = new Matrix4f();
		}

		float float2 = (float)Math.cos((double)float1);
		float float3 = (float)Math.sin((double)float1);
		float float4 = 1.0F - float2;
		float float5 = vector3f.x * vector3f.y;
		float float6 = vector3f.y * vector3f.z;
		float float7 = vector3f.x * vector3f.z;
		float float8 = vector3f.x * float3;
		float float9 = vector3f.y * float3;
		float float10 = vector3f.z * float3;
		float float11 = vector3f.x * vector3f.x * float4 + float2;
		float float12 = float5 * float4 + float10;
		float float13 = float7 * float4 - float9;
		float float14 = float5 * float4 - float10;
		float float15 = vector3f.y * vector3f.y * float4 + float2;
		float float16 = float6 * float4 + float8;
		float float17 = float7 * float4 + float9;
		float float18 = float6 * float4 - float8;
		float float19 = vector3f.z * vector3f.z * float4 + float2;
		float float20 = matrix4f.m00 * float11 + matrix4f.m10 * float12 + matrix4f.m20 * float13;
		float float21 = matrix4f.m01 * float11 + matrix4f.m11 * float12 + matrix4f.m21 * float13;
		float float22 = matrix4f.m02 * float11 + matrix4f.m12 * float12 + matrix4f.m22 * float13;
		float float23 = matrix4f.m03 * float11 + matrix4f.m13 * float12 + matrix4f.m23 * float13;
		float float24 = matrix4f.m00 * float14 + matrix4f.m10 * float15 + matrix4f.m20 * float16;
		float float25 = matrix4f.m01 * float14 + matrix4f.m11 * float15 + matrix4f.m21 * float16;
		float float26 = matrix4f.m02 * float14 + matrix4f.m12 * float15 + matrix4f.m22 * float16;
		float float27 = matrix4f.m03 * float14 + matrix4f.m13 * float15 + matrix4f.m23 * float16;
		matrix4f2.m20 = matrix4f.m00 * float17 + matrix4f.m10 * float18 + matrix4f.m20 * float19;
		matrix4f2.m21 = matrix4f.m01 * float17 + matrix4f.m11 * float18 + matrix4f.m21 * float19;
		matrix4f2.m22 = matrix4f.m02 * float17 + matrix4f.m12 * float18 + matrix4f.m22 * float19;
		matrix4f2.m23 = matrix4f.m03 * float17 + matrix4f.m13 * float18 + matrix4f.m23 * float19;
		matrix4f2.m00 = float20;
		matrix4f2.m01 = float21;
		matrix4f2.m02 = float22;
		matrix4f2.m03 = float23;
		matrix4f2.m10 = float24;
		matrix4f2.m11 = float25;
		matrix4f2.m12 = float26;
		matrix4f2.m13 = float27;
		return matrix4f2;
	}

	public Matrix4f translate(Vector3f vector3f, Matrix4f matrix4f) {
		return translate(vector3f, this, matrix4f);
	}

	public static Matrix4f translate(Vector3f vector3f, Matrix4f matrix4f, Matrix4f matrix4f2) {
		if (matrix4f2 == null) {
			matrix4f2 = new Matrix4f();
		}

		matrix4f2.m30 += matrix4f.m00 * vector3f.x + matrix4f.m10 * vector3f.y + matrix4f.m20 * vector3f.z;
		matrix4f2.m31 += matrix4f.m01 * vector3f.x + matrix4f.m11 * vector3f.y + matrix4f.m21 * vector3f.z;
		matrix4f2.m32 += matrix4f.m02 * vector3f.x + matrix4f.m12 * vector3f.y + matrix4f.m22 * vector3f.z;
		matrix4f2.m33 += matrix4f.m03 * vector3f.x + matrix4f.m13 * vector3f.y + matrix4f.m23 * vector3f.z;
		return matrix4f2;
	}

	public Matrix4f translate(Vector2f vector2f, Matrix4f matrix4f) {
		return translate(vector2f, this, matrix4f);
	}

	public static Matrix4f translate(Vector2f vector2f, Matrix4f matrix4f, Matrix4f matrix4f2) {
		if (matrix4f2 == null) {
			matrix4f2 = new Matrix4f();
		}

		matrix4f2.m30 += matrix4f.m00 * vector2f.x + matrix4f.m10 * vector2f.y;
		matrix4f2.m31 += matrix4f.m01 * vector2f.x + matrix4f.m11 * vector2f.y;
		matrix4f2.m32 += matrix4f.m02 * vector2f.x + matrix4f.m12 * vector2f.y;
		matrix4f2.m33 += matrix4f.m03 * vector2f.x + matrix4f.m13 * vector2f.y;
		return matrix4f2;
	}

	public Matrix4f transpose(Matrix4f matrix4f) {
		return transpose(this, matrix4f);
	}

	public static Matrix4f transpose(Matrix4f matrix4f, Matrix4f matrix4f2) {
		if (matrix4f2 == null) {
			matrix4f2 = new Matrix4f();
		}

		float float1 = matrix4f.m00;
		float float2 = matrix4f.m10;
		float float3 = matrix4f.m20;
		float float4 = matrix4f.m30;
		float float5 = matrix4f.m01;
		float float6 = matrix4f.m11;
		float float7 = matrix4f.m21;
		float float8 = matrix4f.m31;
		float float9 = matrix4f.m02;
		float float10 = matrix4f.m12;
		float float11 = matrix4f.m22;
		float float12 = matrix4f.m32;
		float float13 = matrix4f.m03;
		float float14 = matrix4f.m13;
		float float15 = matrix4f.m23;
		float float16 = matrix4f.m33;
		matrix4f2.m00 = float1;
		matrix4f2.m01 = float2;
		matrix4f2.m02 = float3;
		matrix4f2.m03 = float4;
		matrix4f2.m10 = float5;
		matrix4f2.m11 = float6;
		matrix4f2.m12 = float7;
		matrix4f2.m13 = float8;
		matrix4f2.m20 = float9;
		matrix4f2.m21 = float10;
		matrix4f2.m22 = float11;
		matrix4f2.m23 = float12;
		matrix4f2.m30 = float13;
		matrix4f2.m31 = float14;
		matrix4f2.m32 = float15;
		matrix4f2.m33 = float16;
		return matrix4f2;
	}

	public float determinant() {
		float float1 = this.m00 * (this.m11 * this.m22 * this.m33 + this.m12 * this.m23 * this.m31 + this.m13 * this.m21 * this.m32 - this.m13 * this.m22 * this.m31 - this.m11 * this.m23 * this.m32 - this.m12 * this.m21 * this.m33);
		float1 -= this.m01 * (this.m10 * this.m22 * this.m33 + this.m12 * this.m23 * this.m30 + this.m13 * this.m20 * this.m32 - this.m13 * this.m22 * this.m30 - this.m10 * this.m23 * this.m32 - this.m12 * this.m20 * this.m33);
		float1 += this.m02 * (this.m10 * this.m21 * this.m33 + this.m11 * this.m23 * this.m30 + this.m13 * this.m20 * this.m31 - this.m13 * this.m21 * this.m30 - this.m10 * this.m23 * this.m31 - this.m11 * this.m20 * this.m33);
		float1 -= this.m03 * (this.m10 * this.m21 * this.m32 + this.m11 * this.m22 * this.m30 + this.m12 * this.m20 * this.m31 - this.m12 * this.m21 * this.m30 - this.m10 * this.m22 * this.m31 - this.m11 * this.m20 * this.m32);
		return float1;
	}

	private static float determinant3x3(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9) {
		return float1 * (float5 * float9 - float6 * float8) + float2 * (float6 * float7 - float4 * float9) + float3 * (float4 * float8 - float5 * float7);
	}

	public Matrix invert() {
		return invert(this, this);
	}

	public static Matrix4f invert(Matrix4f matrix4f, Matrix4f matrix4f2) {
		float float1 = matrix4f.determinant();
		if (float1 != 0.0F) {
			if (matrix4f2 == null) {
				matrix4f2 = new Matrix4f();
			}

			float float2 = 1.0F / float1;
			float float3 = determinant3x3(matrix4f.m11, matrix4f.m12, matrix4f.m13, matrix4f.m21, matrix4f.m22, matrix4f.m23, matrix4f.m31, matrix4f.m32, matrix4f.m33);
			float float4 = -determinant3x3(matrix4f.m10, matrix4f.m12, matrix4f.m13, matrix4f.m20, matrix4f.m22, matrix4f.m23, matrix4f.m30, matrix4f.m32, matrix4f.m33);
			float float5 = determinant3x3(matrix4f.m10, matrix4f.m11, matrix4f.m13, matrix4f.m20, matrix4f.m21, matrix4f.m23, matrix4f.m30, matrix4f.m31, matrix4f.m33);
			float float6 = -determinant3x3(matrix4f.m10, matrix4f.m11, matrix4f.m12, matrix4f.m20, matrix4f.m21, matrix4f.m22, matrix4f.m30, matrix4f.m31, matrix4f.m32);
			float float7 = -determinant3x3(matrix4f.m01, matrix4f.m02, matrix4f.m03, matrix4f.m21, matrix4f.m22, matrix4f.m23, matrix4f.m31, matrix4f.m32, matrix4f.m33);
			float float8 = determinant3x3(matrix4f.m00, matrix4f.m02, matrix4f.m03, matrix4f.m20, matrix4f.m22, matrix4f.m23, matrix4f.m30, matrix4f.m32, matrix4f.m33);
			float float9 = -determinant3x3(matrix4f.m00, matrix4f.m01, matrix4f.m03, matrix4f.m20, matrix4f.m21, matrix4f.m23, matrix4f.m30, matrix4f.m31, matrix4f.m33);
			float float10 = determinant3x3(matrix4f.m00, matrix4f.m01, matrix4f.m02, matrix4f.m20, matrix4f.m21, matrix4f.m22, matrix4f.m30, matrix4f.m31, matrix4f.m32);
			float float11 = determinant3x3(matrix4f.m01, matrix4f.m02, matrix4f.m03, matrix4f.m11, matrix4f.m12, matrix4f.m13, matrix4f.m31, matrix4f.m32, matrix4f.m33);
			float float12 = -determinant3x3(matrix4f.m00, matrix4f.m02, matrix4f.m03, matrix4f.m10, matrix4f.m12, matrix4f.m13, matrix4f.m30, matrix4f.m32, matrix4f.m33);
			float float13 = determinant3x3(matrix4f.m00, matrix4f.m01, matrix4f.m03, matrix4f.m10, matrix4f.m11, matrix4f.m13, matrix4f.m30, matrix4f.m31, matrix4f.m33);
			float float14 = -determinant3x3(matrix4f.m00, matrix4f.m01, matrix4f.m02, matrix4f.m10, matrix4f.m11, matrix4f.m12, matrix4f.m30, matrix4f.m31, matrix4f.m32);
			float float15 = -determinant3x3(matrix4f.m01, matrix4f.m02, matrix4f.m03, matrix4f.m11, matrix4f.m12, matrix4f.m13, matrix4f.m21, matrix4f.m22, matrix4f.m23);
			float float16 = determinant3x3(matrix4f.m00, matrix4f.m02, matrix4f.m03, matrix4f.m10, matrix4f.m12, matrix4f.m13, matrix4f.m20, matrix4f.m22, matrix4f.m23);
			float float17 = -determinant3x3(matrix4f.m00, matrix4f.m01, matrix4f.m03, matrix4f.m10, matrix4f.m11, matrix4f.m13, matrix4f.m20, matrix4f.m21, matrix4f.m23);
			float float18 = determinant3x3(matrix4f.m00, matrix4f.m01, matrix4f.m02, matrix4f.m10, matrix4f.m11, matrix4f.m12, matrix4f.m20, matrix4f.m21, matrix4f.m22);
			matrix4f2.m00 = float3 * float2;
			matrix4f2.m11 = float8 * float2;
			matrix4f2.m22 = float13 * float2;
			matrix4f2.m33 = float18 * float2;
			matrix4f2.m01 = float7 * float2;
			matrix4f2.m10 = float4 * float2;
			matrix4f2.m20 = float5 * float2;
			matrix4f2.m02 = float11 * float2;
			matrix4f2.m12 = float12 * float2;
			matrix4f2.m21 = float9 * float2;
			matrix4f2.m03 = float15 * float2;
			matrix4f2.m30 = float6 * float2;
			matrix4f2.m13 = float16 * float2;
			matrix4f2.m31 = float10 * float2;
			matrix4f2.m32 = float14 * float2;
			matrix4f2.m23 = float17 * float2;
			return matrix4f2;
		} else {
			return null;
		}
	}

	public Matrix negate() {
		return this.negate(this);
	}

	public Matrix4f negate(Matrix4f matrix4f) {
		return negate(this, matrix4f);
	}

	public static Matrix4f negate(Matrix4f matrix4f, Matrix4f matrix4f2) {
		if (matrix4f2 == null) {
			matrix4f2 = new Matrix4f();
		}

		matrix4f2.m00 = -matrix4f.m00;
		matrix4f2.m01 = -matrix4f.m01;
		matrix4f2.m02 = -matrix4f.m02;
		matrix4f2.m03 = -matrix4f.m03;
		matrix4f2.m10 = -matrix4f.m10;
		matrix4f2.m11 = -matrix4f.m11;
		matrix4f2.m12 = -matrix4f.m12;
		matrix4f2.m13 = -matrix4f.m13;
		matrix4f2.m20 = -matrix4f.m20;
		matrix4f2.m21 = -matrix4f.m21;
		matrix4f2.m22 = -matrix4f.m22;
		matrix4f2.m23 = -matrix4f.m23;
		matrix4f2.m30 = -matrix4f.m30;
		matrix4f2.m31 = -matrix4f.m31;
		matrix4f2.m32 = -matrix4f.m32;
		matrix4f2.m33 = -matrix4f.m33;
		return matrix4f2;
	}
}
