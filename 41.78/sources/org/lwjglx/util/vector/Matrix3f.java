package org.lwjglx.util.vector;

import java.io.Serializable;
import java.nio.FloatBuffer;


public class Matrix3f extends Matrix implements Serializable {
	private static final long serialVersionUID = 1L;
	public float m00;
	public float m01;
	public float m02;
	public float m10;
	public float m11;
	public float m12;
	public float m20;
	public float m21;
	public float m22;

	public Matrix3f() {
		this.setIdentity();
	}

	public Matrix3f load(Matrix3f matrix3f) {
		return load(matrix3f, this);
	}

	public static Matrix3f load(Matrix3f matrix3f, Matrix3f matrix3f2) {
		if (matrix3f2 == null) {
			matrix3f2 = new Matrix3f();
		}

		matrix3f2.m00 = matrix3f.m00;
		matrix3f2.m10 = matrix3f.m10;
		matrix3f2.m20 = matrix3f.m20;
		matrix3f2.m01 = matrix3f.m01;
		matrix3f2.m11 = matrix3f.m11;
		matrix3f2.m21 = matrix3f.m21;
		matrix3f2.m02 = matrix3f.m02;
		matrix3f2.m12 = matrix3f.m12;
		matrix3f2.m22 = matrix3f.m22;
		return matrix3f2;
	}

	public Matrix load(FloatBuffer floatBuffer) {
		this.m00 = floatBuffer.get();
		this.m01 = floatBuffer.get();
		this.m02 = floatBuffer.get();
		this.m10 = floatBuffer.get();
		this.m11 = floatBuffer.get();
		this.m12 = floatBuffer.get();
		this.m20 = floatBuffer.get();
		this.m21 = floatBuffer.get();
		this.m22 = floatBuffer.get();
		return this;
	}

	public Matrix loadTranspose(FloatBuffer floatBuffer) {
		this.m00 = floatBuffer.get();
		this.m10 = floatBuffer.get();
		this.m20 = floatBuffer.get();
		this.m01 = floatBuffer.get();
		this.m11 = floatBuffer.get();
		this.m21 = floatBuffer.get();
		this.m02 = floatBuffer.get();
		this.m12 = floatBuffer.get();
		this.m22 = floatBuffer.get();
		return this;
	}

	public Matrix store(FloatBuffer floatBuffer) {
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

	public Matrix storeTranspose(FloatBuffer floatBuffer) {
		floatBuffer.put(this.m00);
		floatBuffer.put(this.m10);
		floatBuffer.put(this.m20);
		floatBuffer.put(this.m01);
		floatBuffer.put(this.m11);
		floatBuffer.put(this.m21);
		floatBuffer.put(this.m02);
		floatBuffer.put(this.m12);
		floatBuffer.put(this.m22);
		return this;
	}

	public static Matrix3f add(Matrix3f matrix3f, Matrix3f matrix3f2, Matrix3f matrix3f3) {
		if (matrix3f3 == null) {
			matrix3f3 = new Matrix3f();
		}

		matrix3f3.m00 = matrix3f.m00 + matrix3f2.m00;
		matrix3f3.m01 = matrix3f.m01 + matrix3f2.m01;
		matrix3f3.m02 = matrix3f.m02 + matrix3f2.m02;
		matrix3f3.m10 = matrix3f.m10 + matrix3f2.m10;
		matrix3f3.m11 = matrix3f.m11 + matrix3f2.m11;
		matrix3f3.m12 = matrix3f.m12 + matrix3f2.m12;
		matrix3f3.m20 = matrix3f.m20 + matrix3f2.m20;
		matrix3f3.m21 = matrix3f.m21 + matrix3f2.m21;
		matrix3f3.m22 = matrix3f.m22 + matrix3f2.m22;
		return matrix3f3;
	}

	public static Matrix3f sub(Matrix3f matrix3f, Matrix3f matrix3f2, Matrix3f matrix3f3) {
		if (matrix3f3 == null) {
			matrix3f3 = new Matrix3f();
		}

		matrix3f3.m00 = matrix3f.m00 - matrix3f2.m00;
		matrix3f3.m01 = matrix3f.m01 - matrix3f2.m01;
		matrix3f3.m02 = matrix3f.m02 - matrix3f2.m02;
		matrix3f3.m10 = matrix3f.m10 - matrix3f2.m10;
		matrix3f3.m11 = matrix3f.m11 - matrix3f2.m11;
		matrix3f3.m12 = matrix3f.m12 - matrix3f2.m12;
		matrix3f3.m20 = matrix3f.m20 - matrix3f2.m20;
		matrix3f3.m21 = matrix3f.m21 - matrix3f2.m21;
		matrix3f3.m22 = matrix3f.m22 - matrix3f2.m22;
		return matrix3f3;
	}

	public static Matrix3f mul(Matrix3f matrix3f, Matrix3f matrix3f2, Matrix3f matrix3f3) {
		if (matrix3f3 == null) {
			matrix3f3 = new Matrix3f();
		}

		float float1 = matrix3f.m00 * matrix3f2.m00 + matrix3f.m10 * matrix3f2.m01 + matrix3f.m20 * matrix3f2.m02;
		float float2 = matrix3f.m01 * matrix3f2.m00 + matrix3f.m11 * matrix3f2.m01 + matrix3f.m21 * matrix3f2.m02;
		float float3 = matrix3f.m02 * matrix3f2.m00 + matrix3f.m12 * matrix3f2.m01 + matrix3f.m22 * matrix3f2.m02;
		float float4 = matrix3f.m00 * matrix3f2.m10 + matrix3f.m10 * matrix3f2.m11 + matrix3f.m20 * matrix3f2.m12;
		float float5 = matrix3f.m01 * matrix3f2.m10 + matrix3f.m11 * matrix3f2.m11 + matrix3f.m21 * matrix3f2.m12;
		float float6 = matrix3f.m02 * matrix3f2.m10 + matrix3f.m12 * matrix3f2.m11 + matrix3f.m22 * matrix3f2.m12;
		float float7 = matrix3f.m00 * matrix3f2.m20 + matrix3f.m10 * matrix3f2.m21 + matrix3f.m20 * matrix3f2.m22;
		float float8 = matrix3f.m01 * matrix3f2.m20 + matrix3f.m11 * matrix3f2.m21 + matrix3f.m21 * matrix3f2.m22;
		float float9 = matrix3f.m02 * matrix3f2.m20 + matrix3f.m12 * matrix3f2.m21 + matrix3f.m22 * matrix3f2.m22;
		matrix3f3.m00 = float1;
		matrix3f3.m01 = float2;
		matrix3f3.m02 = float3;
		matrix3f3.m10 = float4;
		matrix3f3.m11 = float5;
		matrix3f3.m12 = float6;
		matrix3f3.m20 = float7;
		matrix3f3.m21 = float8;
		matrix3f3.m22 = float9;
		return matrix3f3;
	}

	public static Vector3f transform(Matrix3f matrix3f, Vector3f vector3f, Vector3f vector3f2) {
		if (vector3f2 == null) {
			vector3f2 = new Vector3f();
		}

		float float1 = matrix3f.m00 * vector3f.x + matrix3f.m10 * vector3f.y + matrix3f.m20 * vector3f.z;
		float float2 = matrix3f.m01 * vector3f.x + matrix3f.m11 * vector3f.y + matrix3f.m21 * vector3f.z;
		float float3 = matrix3f.m02 * vector3f.x + matrix3f.m12 * vector3f.y + matrix3f.m22 * vector3f.z;
		vector3f2.x = float1;
		vector3f2.y = float2;
		vector3f2.z = float3;
		return vector3f2;
	}

	public Matrix transpose() {
		return transpose(this, this);
	}

	public Matrix3f transpose(Matrix3f matrix3f) {
		return transpose(this, matrix3f);
	}

	public static Matrix3f transpose(Matrix3f matrix3f, Matrix3f matrix3f2) {
		if (matrix3f2 == null) {
			matrix3f2 = new Matrix3f();
		}

		float float1 = matrix3f.m00;
		float float2 = matrix3f.m10;
		float float3 = matrix3f.m20;
		float float4 = matrix3f.m01;
		float float5 = matrix3f.m11;
		float float6 = matrix3f.m21;
		float float7 = matrix3f.m02;
		float float8 = matrix3f.m12;
		float float9 = matrix3f.m22;
		matrix3f2.m00 = float1;
		matrix3f2.m01 = float2;
		matrix3f2.m02 = float3;
		matrix3f2.m10 = float4;
		matrix3f2.m11 = float5;
		matrix3f2.m12 = float6;
		matrix3f2.m20 = float7;
		matrix3f2.m21 = float8;
		matrix3f2.m22 = float9;
		return matrix3f2;
	}

	public float determinant() {
		float float1 = this.m00 * (this.m11 * this.m22 - this.m12 * this.m21) + this.m01 * (this.m12 * this.m20 - this.m10 * this.m22) + this.m02 * (this.m10 * this.m21 - this.m11 * this.m20);
		return float1;
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(this.m00).append(' ').append(this.m10).append(' ').append(this.m20).append(' ').append('\n');
		stringBuilder.append(this.m01).append(' ').append(this.m11).append(' ').append(this.m21).append(' ').append('\n');
		stringBuilder.append(this.m02).append(' ').append(this.m12).append(' ').append(this.m22).append(' ').append('\n');
		return stringBuilder.toString();
	}

	public Matrix invert() {
		return invert(this, this);
	}

	public static Matrix3f invert(Matrix3f matrix3f, Matrix3f matrix3f2) {
		float float1 = matrix3f.determinant();
		if (float1 != 0.0F) {
			if (matrix3f2 == null) {
				matrix3f2 = new Matrix3f();
			}

			float float2 = 1.0F / float1;
			float float3 = matrix3f.m11 * matrix3f.m22 - matrix3f.m12 * matrix3f.m21;
			float float4 = -matrix3f.m10 * matrix3f.m22 + matrix3f.m12 * matrix3f.m20;
			float float5 = matrix3f.m10 * matrix3f.m21 - matrix3f.m11 * matrix3f.m20;
			float float6 = -matrix3f.m01 * matrix3f.m22 + matrix3f.m02 * matrix3f.m21;
			float float7 = matrix3f.m00 * matrix3f.m22 - matrix3f.m02 * matrix3f.m20;
			float float8 = -matrix3f.m00 * matrix3f.m21 + matrix3f.m01 * matrix3f.m20;
			float float9 = matrix3f.m01 * matrix3f.m12 - matrix3f.m02 * matrix3f.m11;
			float float10 = -matrix3f.m00 * matrix3f.m12 + matrix3f.m02 * matrix3f.m10;
			float float11 = matrix3f.m00 * matrix3f.m11 - matrix3f.m01 * matrix3f.m10;
			matrix3f2.m00 = float3 * float2;
			matrix3f2.m11 = float7 * float2;
			matrix3f2.m22 = float11 * float2;
			matrix3f2.m01 = float6 * float2;
			matrix3f2.m10 = float4 * float2;
			matrix3f2.m20 = float5 * float2;
			matrix3f2.m02 = float9 * float2;
			matrix3f2.m12 = float10 * float2;
			matrix3f2.m21 = float8 * float2;
			return matrix3f2;
		} else {
			return null;
		}
	}

	public Matrix negate() {
		return this.negate(this);
	}

	public Matrix3f negate(Matrix3f matrix3f) {
		return negate(this, matrix3f);
	}

	public static Matrix3f negate(Matrix3f matrix3f, Matrix3f matrix3f2) {
		if (matrix3f2 == null) {
			matrix3f2 = new Matrix3f();
		}

		matrix3f2.m00 = -matrix3f.m00;
		matrix3f2.m01 = -matrix3f.m02;
		matrix3f2.m02 = -matrix3f.m01;
		matrix3f2.m10 = -matrix3f.m10;
		matrix3f2.m11 = -matrix3f.m12;
		matrix3f2.m12 = -matrix3f.m11;
		matrix3f2.m20 = -matrix3f.m20;
		matrix3f2.m21 = -matrix3f.m22;
		matrix3f2.m22 = -matrix3f.m21;
		return matrix3f2;
	}

	public Matrix setIdentity() {
		return setIdentity(this);
	}

	public static Matrix3f setIdentity(Matrix3f matrix3f) {
		matrix3f.m00 = 1.0F;
		matrix3f.m01 = 0.0F;
		matrix3f.m02 = 0.0F;
		matrix3f.m10 = 0.0F;
		matrix3f.m11 = 1.0F;
		matrix3f.m12 = 0.0F;
		matrix3f.m20 = 0.0F;
		matrix3f.m21 = 0.0F;
		matrix3f.m22 = 1.0F;
		return matrix3f;
	}

	public Matrix setZero() {
		return setZero(this);
	}

	public static Matrix3f setZero(Matrix3f matrix3f) {
		matrix3f.m00 = 0.0F;
		matrix3f.m01 = 0.0F;
		matrix3f.m02 = 0.0F;
		matrix3f.m10 = 0.0F;
		matrix3f.m11 = 0.0F;
		matrix3f.m12 = 0.0F;
		matrix3f.m20 = 0.0F;
		matrix3f.m21 = 0.0F;
		matrix3f.m22 = 0.0F;
		return matrix3f;
	}
}
