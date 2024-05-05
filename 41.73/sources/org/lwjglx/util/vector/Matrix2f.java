package org.lwjglx.util.vector;

import java.io.Serializable;
import java.nio.FloatBuffer;


public class Matrix2f extends Matrix implements Serializable {
	private static final long serialVersionUID = 1L;
	public float m00;
	public float m01;
	public float m10;
	public float m11;

	public Matrix2f() {
		this.setIdentity();
	}

	public Matrix2f(Matrix2f matrix2f) {
		this.load(matrix2f);
	}

	public Matrix2f load(Matrix2f matrix2f) {
		return load(matrix2f, this);
	}

	public static Matrix2f load(Matrix2f matrix2f, Matrix2f matrix2f2) {
		if (matrix2f2 == null) {
			matrix2f2 = new Matrix2f();
		}

		matrix2f2.m00 = matrix2f.m00;
		matrix2f2.m01 = matrix2f.m01;
		matrix2f2.m10 = matrix2f.m10;
		matrix2f2.m11 = matrix2f.m11;
		return matrix2f2;
	}

	public Matrix load(FloatBuffer floatBuffer) {
		this.m00 = floatBuffer.get();
		this.m01 = floatBuffer.get();
		this.m10 = floatBuffer.get();
		this.m11 = floatBuffer.get();
		return this;
	}

	public Matrix loadTranspose(FloatBuffer floatBuffer) {
		this.m00 = floatBuffer.get();
		this.m10 = floatBuffer.get();
		this.m01 = floatBuffer.get();
		this.m11 = floatBuffer.get();
		return this;
	}

	public Matrix store(FloatBuffer floatBuffer) {
		floatBuffer.put(this.m00);
		floatBuffer.put(this.m01);
		floatBuffer.put(this.m10);
		floatBuffer.put(this.m11);
		return this;
	}

	public Matrix storeTranspose(FloatBuffer floatBuffer) {
		floatBuffer.put(this.m00);
		floatBuffer.put(this.m10);
		floatBuffer.put(this.m01);
		floatBuffer.put(this.m11);
		return this;
	}

	public static Matrix2f add(Matrix2f matrix2f, Matrix2f matrix2f2, Matrix2f matrix2f3) {
		if (matrix2f3 == null) {
			matrix2f3 = new Matrix2f();
		}

		matrix2f3.m00 = matrix2f.m00 + matrix2f2.m00;
		matrix2f3.m01 = matrix2f.m01 + matrix2f2.m01;
		matrix2f3.m10 = matrix2f.m10 + matrix2f2.m10;
		matrix2f3.m11 = matrix2f.m11 + matrix2f2.m11;
		return matrix2f3;
	}

	public static Matrix2f sub(Matrix2f matrix2f, Matrix2f matrix2f2, Matrix2f matrix2f3) {
		if (matrix2f3 == null) {
			matrix2f3 = new Matrix2f();
		}

		matrix2f3.m00 = matrix2f.m00 - matrix2f2.m00;
		matrix2f3.m01 = matrix2f.m01 - matrix2f2.m01;
		matrix2f3.m10 = matrix2f.m10 - matrix2f2.m10;
		matrix2f3.m11 = matrix2f.m11 - matrix2f2.m11;
		return matrix2f3;
	}

	public static Matrix2f mul(Matrix2f matrix2f, Matrix2f matrix2f2, Matrix2f matrix2f3) {
		if (matrix2f3 == null) {
			matrix2f3 = new Matrix2f();
		}

		float float1 = matrix2f.m00 * matrix2f2.m00 + matrix2f.m10 * matrix2f2.m01;
		float float2 = matrix2f.m01 * matrix2f2.m00 + matrix2f.m11 * matrix2f2.m01;
		float float3 = matrix2f.m00 * matrix2f2.m10 + matrix2f.m10 * matrix2f2.m11;
		float float4 = matrix2f.m01 * matrix2f2.m10 + matrix2f.m11 * matrix2f2.m11;
		matrix2f3.m00 = float1;
		matrix2f3.m01 = float2;
		matrix2f3.m10 = float3;
		matrix2f3.m11 = float4;
		return matrix2f3;
	}

	public static Vector2f transform(Matrix2f matrix2f, Vector2f vector2f, Vector2f vector2f2) {
		if (vector2f2 == null) {
			vector2f2 = new Vector2f();
		}

		float float1 = matrix2f.m00 * vector2f.x + matrix2f.m10 * vector2f.y;
		float float2 = matrix2f.m01 * vector2f.x + matrix2f.m11 * vector2f.y;
		vector2f2.x = float1;
		vector2f2.y = float2;
		return vector2f2;
	}

	public Matrix transpose() {
		return this.transpose(this);
	}

	public Matrix2f transpose(Matrix2f matrix2f) {
		return transpose(this, matrix2f);
	}

	public static Matrix2f transpose(Matrix2f matrix2f, Matrix2f matrix2f2) {
		if (matrix2f2 == null) {
			matrix2f2 = new Matrix2f();
		}

		float float1 = matrix2f.m10;
		float float2 = matrix2f.m01;
		matrix2f2.m01 = float1;
		matrix2f2.m10 = float2;
		return matrix2f2;
	}

	public Matrix invert() {
		return invert(this, this);
	}

	public static Matrix2f invert(Matrix2f matrix2f, Matrix2f matrix2f2) {
		float float1 = matrix2f.determinant();
		if (float1 != 0.0F) {
			if (matrix2f2 == null) {
				matrix2f2 = new Matrix2f();
			}

			float float2 = 1.0F / float1;
			float float3 = matrix2f.m11 * float2;
			float float4 = -matrix2f.m01 * float2;
			float float5 = matrix2f.m00 * float2;
			float float6 = -matrix2f.m10 * float2;
			matrix2f2.m00 = float3;
			matrix2f2.m01 = float4;
			matrix2f2.m10 = float6;
			matrix2f2.m11 = float5;
			return matrix2f2;
		} else {
			return null;
		}
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(this.m00).append(' ').append(this.m10).append(' ').append('\n');
		stringBuilder.append(this.m01).append(' ').append(this.m11).append(' ').append('\n');
		return stringBuilder.toString();
	}

	public Matrix negate() {
		return this.negate(this);
	}

	public Matrix2f negate(Matrix2f matrix2f) {
		return negate(this, matrix2f);
	}

	public static Matrix2f negate(Matrix2f matrix2f, Matrix2f matrix2f2) {
		if (matrix2f2 == null) {
			matrix2f2 = new Matrix2f();
		}

		matrix2f2.m00 = -matrix2f.m00;
		matrix2f2.m01 = -matrix2f.m01;
		matrix2f2.m10 = -matrix2f.m10;
		matrix2f2.m11 = -matrix2f.m11;
		return matrix2f2;
	}

	public Matrix setIdentity() {
		return setIdentity(this);
	}

	public static Matrix2f setIdentity(Matrix2f matrix2f) {
		matrix2f.m00 = 1.0F;
		matrix2f.m01 = 0.0F;
		matrix2f.m10 = 0.0F;
		matrix2f.m11 = 1.0F;
		return matrix2f;
	}

	public Matrix setZero() {
		return setZero(this);
	}

	public static Matrix2f setZero(Matrix2f matrix2f) {
		matrix2f.m00 = 0.0F;
		matrix2f.m01 = 0.0F;
		matrix2f.m10 = 0.0F;
		matrix2f.m11 = 0.0F;
		return matrix2f;
	}

	public float determinant() {
		return this.m00 * this.m11 - this.m01 * this.m10;
	}
}
