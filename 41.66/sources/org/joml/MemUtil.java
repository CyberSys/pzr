package org.joml;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.Buffer;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import sun.misc.Unsafe;


abstract class MemUtil {
	public static final MemUtil INSTANCE = createInstance();

	private static MemUtil createInstance() {
		Object object;
		try {
			if (Options.NO_UNSAFE && Options.FORCE_UNSAFE) {
				throw new ConfigurationException("Cannot enable both -Djoml.nounsafe and -Djoml.forceUnsafe", (Throwable)null);
			}

			if (Options.NO_UNSAFE) {
				object = new MemUtil.MemUtilNIO();
			} else {
				object = new MemUtil.MemUtilUnsafe();
			}
		} catch (Throwable throwable) {
			if (Options.FORCE_UNSAFE) {
				throw new ConfigurationException("Unsafe is not supported but its use was forced via -Djoml.forceUnsafe", throwable);
			}

			object = new MemUtil.MemUtilNIO();
		}

		return (MemUtil)object;
	}

	public abstract void put(Matrix4f matrix4f, int int1, FloatBuffer floatBuffer);

	public abstract void put(Matrix4f matrix4f, int int1, ByteBuffer byteBuffer);

	public abstract void put(Matrix4x3f matrix4x3f, int int1, FloatBuffer floatBuffer);

	public abstract void put(Matrix4x3f matrix4x3f, int int1, ByteBuffer byteBuffer);

	public abstract void put4x4(Matrix4x3f matrix4x3f, int int1, FloatBuffer floatBuffer);

	public abstract void put4x4(Matrix4x3f matrix4x3f, int int1, ByteBuffer byteBuffer);

	public abstract void put4x4(Matrix4x3d matrix4x3d, int int1, DoubleBuffer doubleBuffer);

	public abstract void put4x4(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer);

	public abstract void put4x4(Matrix3x2f matrix3x2f, int int1, FloatBuffer floatBuffer);

	public abstract void put4x4(Matrix3x2f matrix3x2f, int int1, ByteBuffer byteBuffer);

	public abstract void put4x4(Matrix3x2d matrix3x2d, int int1, DoubleBuffer doubleBuffer);

	public abstract void put4x4(Matrix3x2d matrix3x2d, int int1, ByteBuffer byteBuffer);

	public abstract void put3x3(Matrix3x2f matrix3x2f, int int1, FloatBuffer floatBuffer);

	public abstract void put3x3(Matrix3x2f matrix3x2f, int int1, ByteBuffer byteBuffer);

	public abstract void put3x3(Matrix3x2d matrix3x2d, int int1, DoubleBuffer doubleBuffer);

	public abstract void put3x3(Matrix3x2d matrix3x2d, int int1, ByteBuffer byteBuffer);

	public abstract void put4x3(Matrix4f matrix4f, int int1, FloatBuffer floatBuffer);

	public abstract void put4x3(Matrix4f matrix4f, int int1, ByteBuffer byteBuffer);

	public abstract void put3x4(Matrix4f matrix4f, int int1, FloatBuffer floatBuffer);

	public abstract void put3x4(Matrix4f matrix4f, int int1, ByteBuffer byteBuffer);

	public abstract void put3x4(Matrix4x3f matrix4x3f, int int1, FloatBuffer floatBuffer);

	public abstract void put3x4(Matrix4x3f matrix4x3f, int int1, ByteBuffer byteBuffer);

	public abstract void put3x4(Matrix3f matrix3f, int int1, FloatBuffer floatBuffer);

	public abstract void put3x4(Matrix3f matrix3f, int int1, ByteBuffer byteBuffer);

	public abstract void putTransposed(Matrix4f matrix4f, int int1, FloatBuffer floatBuffer);

	public abstract void putTransposed(Matrix4f matrix4f, int int1, ByteBuffer byteBuffer);

	public abstract void put4x3Transposed(Matrix4f matrix4f, int int1, FloatBuffer floatBuffer);

	public abstract void put4x3Transposed(Matrix4f matrix4f, int int1, ByteBuffer byteBuffer);

	public abstract void putTransposed(Matrix4x3f matrix4x3f, int int1, FloatBuffer floatBuffer);

	public abstract void putTransposed(Matrix4x3f matrix4x3f, int int1, ByteBuffer byteBuffer);

	public abstract void putTransposed(Matrix3f matrix3f, int int1, FloatBuffer floatBuffer);

	public abstract void putTransposed(Matrix3f matrix3f, int int1, ByteBuffer byteBuffer);

	public abstract void putTransposed(Matrix2f matrix2f, int int1, FloatBuffer floatBuffer);

	public abstract void putTransposed(Matrix2f matrix2f, int int1, ByteBuffer byteBuffer);

	public abstract void put(Matrix4d matrix4d, int int1, DoubleBuffer doubleBuffer);

	public abstract void put(Matrix4d matrix4d, int int1, ByteBuffer byteBuffer);

	public abstract void put(Matrix4x3d matrix4x3d, int int1, DoubleBuffer doubleBuffer);

	public abstract void put(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer);

	public abstract void putf(Matrix4d matrix4d, int int1, FloatBuffer floatBuffer);

	public abstract void putf(Matrix4d matrix4d, int int1, ByteBuffer byteBuffer);

	public abstract void putf(Matrix4x3d matrix4x3d, int int1, FloatBuffer floatBuffer);

	public abstract void putf(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer);

	public abstract void putTransposed(Matrix4d matrix4d, int int1, DoubleBuffer doubleBuffer);

	public abstract void putTransposed(Matrix4d matrix4d, int int1, ByteBuffer byteBuffer);

	public abstract void put4x3Transposed(Matrix4d matrix4d, int int1, DoubleBuffer doubleBuffer);

	public abstract void put4x3Transposed(Matrix4d matrix4d, int int1, ByteBuffer byteBuffer);

	public abstract void putTransposed(Matrix4x3d matrix4x3d, int int1, DoubleBuffer doubleBuffer);

	public abstract void putTransposed(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer);

	public abstract void putTransposed(Matrix2d matrix2d, int int1, DoubleBuffer doubleBuffer);

	public abstract void putTransposed(Matrix2d matrix2d, int int1, ByteBuffer byteBuffer);

	public abstract void putfTransposed(Matrix4d matrix4d, int int1, FloatBuffer floatBuffer);

	public abstract void putfTransposed(Matrix4d matrix4d, int int1, ByteBuffer byteBuffer);

	public abstract void putfTransposed(Matrix4x3d matrix4x3d, int int1, FloatBuffer floatBuffer);

	public abstract void putfTransposed(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer);

	public abstract void putfTransposed(Matrix2d matrix2d, int int1, FloatBuffer floatBuffer);

	public abstract void putfTransposed(Matrix2d matrix2d, int int1, ByteBuffer byteBuffer);

	public abstract void put(Matrix3f matrix3f, int int1, FloatBuffer floatBuffer);

	public abstract void put(Matrix3f matrix3f, int int1, ByteBuffer byteBuffer);

	public abstract void put(Matrix3d matrix3d, int int1, DoubleBuffer doubleBuffer);

	public abstract void put(Matrix3d matrix3d, int int1, ByteBuffer byteBuffer);

	public abstract void putf(Matrix3d matrix3d, int int1, FloatBuffer floatBuffer);

	public abstract void putf(Matrix3d matrix3d, int int1, ByteBuffer byteBuffer);

	public abstract void put(Matrix3x2f matrix3x2f, int int1, FloatBuffer floatBuffer);

	public abstract void put(Matrix3x2f matrix3x2f, int int1, ByteBuffer byteBuffer);

	public abstract void put(Matrix3x2d matrix3x2d, int int1, DoubleBuffer doubleBuffer);

	public abstract void put(Matrix3x2d matrix3x2d, int int1, ByteBuffer byteBuffer);

	public abstract void put(Matrix2f matrix2f, int int1, FloatBuffer floatBuffer);

	public abstract void put(Matrix2f matrix2f, int int1, ByteBuffer byteBuffer);

	public abstract void put(Matrix2d matrix2d, int int1, DoubleBuffer doubleBuffer);

	public abstract void put(Matrix2d matrix2d, int int1, ByteBuffer byteBuffer);

	public abstract void putf(Matrix2d matrix2d, int int1, FloatBuffer floatBuffer);

	public abstract void putf(Matrix2d matrix2d, int int1, ByteBuffer byteBuffer);

	public abstract void put(Vector4d vector4d, int int1, DoubleBuffer doubleBuffer);

	public abstract void put(Vector4d vector4d, int int1, FloatBuffer floatBuffer);

	public abstract void put(Vector4d vector4d, int int1, ByteBuffer byteBuffer);

	public abstract void putf(Vector4d vector4d, int int1, ByteBuffer byteBuffer);

	public abstract void put(Vector4f vector4f, int int1, FloatBuffer floatBuffer);

	public abstract void put(Vector4f vector4f, int int1, ByteBuffer byteBuffer);

	public abstract void put(Vector4i vector4i, int int1, IntBuffer intBuffer);

	public abstract void put(Vector4i vector4i, int int1, ByteBuffer byteBuffer);

	public abstract void put(Vector3f vector3f, int int1, FloatBuffer floatBuffer);

	public abstract void put(Vector3f vector3f, int int1, ByteBuffer byteBuffer);

	public abstract void put(Vector3d vector3d, int int1, DoubleBuffer doubleBuffer);

	public abstract void put(Vector3d vector3d, int int1, FloatBuffer floatBuffer);

	public abstract void put(Vector3d vector3d, int int1, ByteBuffer byteBuffer);

	public abstract void putf(Vector3d vector3d, int int1, ByteBuffer byteBuffer);

	public abstract void put(Vector3i vector3i, int int1, IntBuffer intBuffer);

	public abstract void put(Vector3i vector3i, int int1, ByteBuffer byteBuffer);

	public abstract void put(Vector2f vector2f, int int1, FloatBuffer floatBuffer);

	public abstract void put(Vector2f vector2f, int int1, ByteBuffer byteBuffer);

	public abstract void put(Vector2d vector2d, int int1, DoubleBuffer doubleBuffer);

	public abstract void put(Vector2d vector2d, int int1, ByteBuffer byteBuffer);

	public abstract void put(Vector2i vector2i, int int1, IntBuffer intBuffer);

	public abstract void put(Vector2i vector2i, int int1, ByteBuffer byteBuffer);

	public abstract void get(Matrix4f matrix4f, int int1, FloatBuffer floatBuffer);

	public abstract void get(Matrix4f matrix4f, int int1, ByteBuffer byteBuffer);

	public abstract void getTransposed(Matrix4f matrix4f, int int1, FloatBuffer floatBuffer);

	public abstract void getTransposed(Matrix4f matrix4f, int int1, ByteBuffer byteBuffer);

	public abstract void get(Matrix4x3f matrix4x3f, int int1, FloatBuffer floatBuffer);

	public abstract void get(Matrix4x3f matrix4x3f, int int1, ByteBuffer byteBuffer);

	public abstract void get(Matrix4d matrix4d, int int1, DoubleBuffer doubleBuffer);

	public abstract void get(Matrix4d matrix4d, int int1, ByteBuffer byteBuffer);

	public abstract void get(Matrix4x3d matrix4x3d, int int1, DoubleBuffer doubleBuffer);

	public abstract void get(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer);

	public abstract void getf(Matrix4d matrix4d, int int1, FloatBuffer floatBuffer);

	public abstract void getf(Matrix4d matrix4d, int int1, ByteBuffer byteBuffer);

	public abstract void getf(Matrix4x3d matrix4x3d, int int1, FloatBuffer floatBuffer);

	public abstract void getf(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer);

	public abstract void get(Matrix3f matrix3f, int int1, FloatBuffer floatBuffer);

	public abstract void get(Matrix3f matrix3f, int int1, ByteBuffer byteBuffer);

	public abstract void get(Matrix3d matrix3d, int int1, DoubleBuffer doubleBuffer);

	public abstract void get(Matrix3d matrix3d, int int1, ByteBuffer byteBuffer);

	public abstract void get(Matrix3x2f matrix3x2f, int int1, FloatBuffer floatBuffer);

	public abstract void get(Matrix3x2f matrix3x2f, int int1, ByteBuffer byteBuffer);

	public abstract void get(Matrix3x2d matrix3x2d, int int1, DoubleBuffer doubleBuffer);

	public abstract void get(Matrix3x2d matrix3x2d, int int1, ByteBuffer byteBuffer);

	public abstract void getf(Matrix3d matrix3d, int int1, FloatBuffer floatBuffer);

	public abstract void getf(Matrix3d matrix3d, int int1, ByteBuffer byteBuffer);

	public abstract void get(Matrix2f matrix2f, int int1, FloatBuffer floatBuffer);

	public abstract void get(Matrix2f matrix2f, int int1, ByteBuffer byteBuffer);

	public abstract void get(Matrix2d matrix2d, int int1, DoubleBuffer doubleBuffer);

	public abstract void get(Matrix2d matrix2d, int int1, ByteBuffer byteBuffer);

	public abstract void getf(Matrix2d matrix2d, int int1, FloatBuffer floatBuffer);

	public abstract void getf(Matrix2d matrix2d, int int1, ByteBuffer byteBuffer);

	public abstract void get(Vector4d vector4d, int int1, DoubleBuffer doubleBuffer);

	public abstract void get(Vector4d vector4d, int int1, ByteBuffer byteBuffer);

	public abstract void get(Vector4f vector4f, int int1, FloatBuffer floatBuffer);

	public abstract void get(Vector4f vector4f, int int1, ByteBuffer byteBuffer);

	public abstract void get(Vector4i vector4i, int int1, IntBuffer intBuffer);

	public abstract void get(Vector4i vector4i, int int1, ByteBuffer byteBuffer);

	public abstract void get(Vector3f vector3f, int int1, FloatBuffer floatBuffer);

	public abstract void get(Vector3f vector3f, int int1, ByteBuffer byteBuffer);

	public abstract void get(Vector3d vector3d, int int1, DoubleBuffer doubleBuffer);

	public abstract void get(Vector3d vector3d, int int1, ByteBuffer byteBuffer);

	public abstract void get(Vector3i vector3i, int int1, IntBuffer intBuffer);

	public abstract void get(Vector3i vector3i, int int1, ByteBuffer byteBuffer);

	public abstract void get(Vector2f vector2f, int int1, FloatBuffer floatBuffer);

	public abstract void get(Vector2f vector2f, int int1, ByteBuffer byteBuffer);

	public abstract void get(Vector2d vector2d, int int1, DoubleBuffer doubleBuffer);

	public abstract void get(Vector2d vector2d, int int1, ByteBuffer byteBuffer);

	public abstract void get(Vector2i vector2i, int int1, IntBuffer intBuffer);

	public abstract void get(Vector2i vector2i, int int1, ByteBuffer byteBuffer);

	public abstract void putMatrix3f(Quaternionf quaternionf, int int1, ByteBuffer byteBuffer);

	public abstract void putMatrix3f(Quaternionf quaternionf, int int1, FloatBuffer floatBuffer);

	public abstract void putMatrix4f(Quaternionf quaternionf, int int1, ByteBuffer byteBuffer);

	public abstract void putMatrix4f(Quaternionf quaternionf, int int1, FloatBuffer floatBuffer);

	public abstract void putMatrix4x3f(Quaternionf quaternionf, int int1, ByteBuffer byteBuffer);

	public abstract void putMatrix4x3f(Quaternionf quaternionf, int int1, FloatBuffer floatBuffer);

	public abstract float get(Matrix4f matrix4f, int int1, int int2);

	public abstract Matrix4f set(Matrix4f matrix4f, int int1, int int2, float float1);

	public abstract double get(Matrix4d matrix4d, int int1, int int2);

	public abstract Matrix4d set(Matrix4d matrix4d, int int1, int int2, double double1);

	public abstract float get(Matrix3f matrix3f, int int1, int int2);

	public abstract Matrix3f set(Matrix3f matrix3f, int int1, int int2, float float1);

	public abstract double get(Matrix3d matrix3d, int int1, int int2);

	public abstract Matrix3d set(Matrix3d matrix3d, int int1, int int2, double double1);

	public abstract Vector4f getColumn(Matrix4f matrix4f, int int1, Vector4f vector4f);

	public abstract Matrix4f setColumn(Vector4f vector4f, int int1, Matrix4f matrix4f);

	public abstract Matrix4f setColumn(Vector4fc vector4fc, int int1, Matrix4f matrix4f);

	public abstract void copy(Matrix4f matrix4f, Matrix4f matrix4f2);

	public abstract void copy(Matrix4x3f matrix4x3f, Matrix4x3f matrix4x3f2);

	public abstract void copy(Matrix4f matrix4f, Matrix4x3f matrix4x3f);

	public abstract void copy(Matrix4x3f matrix4x3f, Matrix4f matrix4f);

	public abstract void copy(Matrix3f matrix3f, Matrix3f matrix3f2);

	public abstract void copy(Matrix3f matrix3f, Matrix4f matrix4f);

	public abstract void copy(Matrix4f matrix4f, Matrix3f matrix3f);

	public abstract void copy(Matrix3f matrix3f, Matrix4x3f matrix4x3f);

	public abstract void copy(Matrix3x2f matrix3x2f, Matrix3x2f matrix3x2f2);

	public abstract void copy(Matrix3x2d matrix3x2d, Matrix3x2d matrix3x2d2);

	public abstract void copy(Matrix2f matrix2f, Matrix2f matrix2f2);

	public abstract void copy(Matrix2d matrix2d, Matrix2d matrix2d2);

	public abstract void copy(Matrix2f matrix2f, Matrix3f matrix3f);

	public abstract void copy(Matrix3f matrix3f, Matrix2f matrix2f);

	public abstract void copy(Matrix2f matrix2f, Matrix3x2f matrix3x2f);

	public abstract void copy(Matrix3x2f matrix3x2f, Matrix2f matrix2f);

	public abstract void copy(Matrix2d matrix2d, Matrix3d matrix3d);

	public abstract void copy(Matrix3d matrix3d, Matrix2d matrix2d);

	public abstract void copy(Matrix2d matrix2d, Matrix3x2d matrix3x2d);

	public abstract void copy(Matrix3x2d matrix3x2d, Matrix2d matrix2d);

	public abstract void copy3x3(Matrix4f matrix4f, Matrix4f matrix4f2);

	public abstract void copy3x3(Matrix4x3f matrix4x3f, Matrix4x3f matrix4x3f2);

	public abstract void copy3x3(Matrix3f matrix3f, Matrix4x3f matrix4x3f);

	public abstract void copy3x3(Matrix3f matrix3f, Matrix4f matrix4f);

	public abstract void copy4x3(Matrix4f matrix4f, Matrix4f matrix4f2);

	public abstract void copy4x3(Matrix4x3f matrix4x3f, Matrix4f matrix4f);

	public abstract void copy(float[] floatArray, int int1, Matrix4f matrix4f);

	public abstract void copyTransposed(float[] floatArray, int int1, Matrix4f matrix4f);

	public abstract void copy(float[] floatArray, int int1, Matrix3f matrix3f);

	public abstract void copy(float[] floatArray, int int1, Matrix4x3f matrix4x3f);

	public abstract void copy(float[] floatArray, int int1, Matrix3x2f matrix3x2f);

	public abstract void copy(double[] doubleArray, int int1, Matrix3x2d matrix3x2d);

	public abstract void copy(float[] floatArray, int int1, Matrix2f matrix2f);

	public abstract void copy(double[] doubleArray, int int1, Matrix2d matrix2d);

	public abstract void copy(Matrix4f matrix4f, float[] floatArray, int int1);

	public abstract void copy(Matrix3f matrix3f, float[] floatArray, int int1);

	public abstract void copy(Matrix4x3f matrix4x3f, float[] floatArray, int int1);

	public abstract void copy(Matrix3x2f matrix3x2f, float[] floatArray, int int1);

	public abstract void copy(Matrix3x2d matrix3x2d, double[] doubleArray, int int1);

	public abstract void copy(Matrix2f matrix2f, float[] floatArray, int int1);

	public abstract void copy(Matrix2d matrix2d, double[] doubleArray, int int1);

	public abstract void copy4x4(Matrix4x3f matrix4x3f, float[] floatArray, int int1);

	public abstract void copy4x4(Matrix4x3d matrix4x3d, float[] floatArray, int int1);

	public abstract void copy4x4(Matrix4x3d matrix4x3d, double[] doubleArray, int int1);

	public abstract void copy4x4(Matrix3x2f matrix3x2f, float[] floatArray, int int1);

	public abstract void copy4x4(Matrix3x2d matrix3x2d, double[] doubleArray, int int1);

	public abstract void copy3x3(Matrix3x2f matrix3x2f, float[] floatArray, int int1);

	public abstract void copy3x3(Matrix3x2d matrix3x2d, double[] doubleArray, int int1);

	public abstract void identity(Matrix4f matrix4f);

	public abstract void identity(Matrix4x3f matrix4x3f);

	public abstract void identity(Matrix3f matrix3f);

	public abstract void identity(Matrix3x2f matrix3x2f);

	public abstract void identity(Matrix3x2d matrix3x2d);

	public abstract void identity(Matrix2f matrix2f);

	public abstract void swap(Matrix4f matrix4f, Matrix4f matrix4f2);

	public abstract void swap(Matrix4x3f matrix4x3f, Matrix4x3f matrix4x3f2);

	public abstract void swap(Matrix3f matrix3f, Matrix3f matrix3f2);

	public abstract void swap(Matrix2f matrix2f, Matrix2f matrix2f2);

	public abstract void swap(Matrix2d matrix2d, Matrix2d matrix2d2);

	public abstract void zero(Matrix4f matrix4f);

	public abstract void zero(Matrix4x3f matrix4x3f);

	public abstract void zero(Matrix3f matrix3f);

	public abstract void zero(Matrix3x2f matrix3x2f);

	public abstract void zero(Matrix3x2d matrix3x2d);

	public abstract void zero(Matrix2f matrix2f);

	public abstract void zero(Matrix2d matrix2d);

	public static class MemUtilNIO extends MemUtil {

		public void put0(Matrix4f matrix4f, FloatBuffer floatBuffer) {
			floatBuffer.put(0, matrix4f.m00()).put(1, matrix4f.m01()).put(2, matrix4f.m02()).put(3, matrix4f.m03()).put(4, matrix4f.m10()).put(5, matrix4f.m11()).put(6, matrix4f.m12()).put(7, matrix4f.m13()).put(8, matrix4f.m20()).put(9, matrix4f.m21()).put(10, matrix4f.m22()).put(11, matrix4f.m23()).put(12, matrix4f.m30()).put(13, matrix4f.m31()).put(14, matrix4f.m32()).put(15, matrix4f.m33());
		}

		public void putN(Matrix4f matrix4f, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, matrix4f.m00()).put(int1 + 1, matrix4f.m01()).put(int1 + 2, matrix4f.m02()).put(int1 + 3, matrix4f.m03()).put(int1 + 4, matrix4f.m10()).put(int1 + 5, matrix4f.m11()).put(int1 + 6, matrix4f.m12()).put(int1 + 7, matrix4f.m13()).put(int1 + 8, matrix4f.m20()).put(int1 + 9, matrix4f.m21()).put(int1 + 10, matrix4f.m22()).put(int1 + 11, matrix4f.m23()).put(int1 + 12, matrix4f.m30()).put(int1 + 13, matrix4f.m31()).put(int1 + 14, matrix4f.m32()).put(int1 + 15, matrix4f.m33());
		}

		public void put(Matrix4f matrix4f, int int1, FloatBuffer floatBuffer) {
			if (int1 == 0) {
				this.put0(matrix4f, floatBuffer);
			} else {
				this.putN(matrix4f, int1, floatBuffer);
			}
		}

		public void put0(Matrix4f matrix4f, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(0, matrix4f.m00()).putFloat(4, matrix4f.m01()).putFloat(8, matrix4f.m02()).putFloat(12, matrix4f.m03()).putFloat(16, matrix4f.m10()).putFloat(20, matrix4f.m11()).putFloat(24, matrix4f.m12()).putFloat(28, matrix4f.m13()).putFloat(32, matrix4f.m20()).putFloat(36, matrix4f.m21()).putFloat(40, matrix4f.m22()).putFloat(44, matrix4f.m23()).putFloat(48, matrix4f.m30()).putFloat(52, matrix4f.m31()).putFloat(56, matrix4f.m32()).putFloat(60, matrix4f.m33());
		}

		private void putN(Matrix4f matrix4f, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, matrix4f.m00()).putFloat(int1 + 4, matrix4f.m01()).putFloat(int1 + 8, matrix4f.m02()).putFloat(int1 + 12, matrix4f.m03()).putFloat(int1 + 16, matrix4f.m10()).putFloat(int1 + 20, matrix4f.m11()).putFloat(int1 + 24, matrix4f.m12()).putFloat(int1 + 28, matrix4f.m13()).putFloat(int1 + 32, matrix4f.m20()).putFloat(int1 + 36, matrix4f.m21()).putFloat(int1 + 40, matrix4f.m22()).putFloat(int1 + 44, matrix4f.m23()).putFloat(int1 + 48, matrix4f.m30()).putFloat(int1 + 52, matrix4f.m31()).putFloat(int1 + 56, matrix4f.m32()).putFloat(int1 + 60, matrix4f.m33());
		}

		public void put(Matrix4f matrix4f, int int1, ByteBuffer byteBuffer) {
			if (int1 == 0) {
				this.put0(matrix4f, byteBuffer);
			} else {
				this.putN(matrix4f, int1, byteBuffer);
			}
		}

		public void put4x3_0(Matrix4f matrix4f, FloatBuffer floatBuffer) {
			floatBuffer.put(0, matrix4f.m00()).put(1, matrix4f.m01()).put(2, matrix4f.m02()).put(3, matrix4f.m10()).put(4, matrix4f.m11()).put(5, matrix4f.m12()).put(6, matrix4f.m20()).put(7, matrix4f.m21()).put(8, matrix4f.m22()).put(9, matrix4f.m30()).put(10, matrix4f.m31()).put(11, matrix4f.m32());
		}

		public void put4x3_N(Matrix4f matrix4f, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, matrix4f.m00()).put(int1 + 1, matrix4f.m01()).put(int1 + 2, matrix4f.m02()).put(int1 + 3, matrix4f.m10()).put(int1 + 4, matrix4f.m11()).put(int1 + 5, matrix4f.m12()).put(int1 + 6, matrix4f.m20()).put(int1 + 7, matrix4f.m21()).put(int1 + 8, matrix4f.m22()).put(int1 + 9, matrix4f.m30()).put(int1 + 10, matrix4f.m31()).put(int1 + 11, matrix4f.m32());
		}

		public void put4x3(Matrix4f matrix4f, int int1, FloatBuffer floatBuffer) {
			if (int1 == 0) {
				this.put4x3_0(matrix4f, floatBuffer);
			} else {
				this.put4x3_N(matrix4f, int1, floatBuffer);
			}
		}

		public void put4x3_0(Matrix4f matrix4f, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(0, matrix4f.m00()).putFloat(4, matrix4f.m01()).putFloat(8, matrix4f.m02()).putFloat(12, matrix4f.m10()).putFloat(16, matrix4f.m11()).putFloat(20, matrix4f.m12()).putFloat(24, matrix4f.m20()).putFloat(28, matrix4f.m21()).putFloat(32, matrix4f.m22()).putFloat(36, matrix4f.m30()).putFloat(40, matrix4f.m31()).putFloat(44, matrix4f.m32());
		}

		private void put4x3_N(Matrix4f matrix4f, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, matrix4f.m00()).putFloat(int1 + 4, matrix4f.m01()).putFloat(int1 + 8, matrix4f.m02()).putFloat(int1 + 12, matrix4f.m10()).putFloat(int1 + 16, matrix4f.m11()).putFloat(int1 + 20, matrix4f.m12()).putFloat(int1 + 24, matrix4f.m20()).putFloat(int1 + 28, matrix4f.m21()).putFloat(int1 + 32, matrix4f.m22()).putFloat(int1 + 36, matrix4f.m30()).putFloat(int1 + 40, matrix4f.m31()).putFloat(int1 + 44, matrix4f.m32());
		}

		public void put4x3(Matrix4f matrix4f, int int1, ByteBuffer byteBuffer) {
			if (int1 == 0) {
				this.put4x3_0(matrix4f, byteBuffer);
			} else {
				this.put4x3_N(matrix4f, int1, byteBuffer);
			}
		}

		public void put3x4_0(Matrix4f matrix4f, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(0, matrix4f.m00()).putFloat(4, matrix4f.m01()).putFloat(8, matrix4f.m02()).putFloat(12, matrix4f.m03()).putFloat(16, matrix4f.m10()).putFloat(20, matrix4f.m11()).putFloat(24, matrix4f.m12()).putFloat(28, matrix4f.m13()).putFloat(32, matrix4f.m20()).putFloat(36, matrix4f.m21()).putFloat(40, matrix4f.m22()).putFloat(44, matrix4f.m23());
		}

		private void put3x4_N(Matrix4f matrix4f, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, matrix4f.m00()).putFloat(int1 + 4, matrix4f.m01()).putFloat(int1 + 8, matrix4f.m02()).putFloat(int1 + 12, matrix4f.m03()).putFloat(int1 + 16, matrix4f.m10()).putFloat(int1 + 20, matrix4f.m11()).putFloat(int1 + 24, matrix4f.m12()).putFloat(int1 + 28, matrix4f.m13()).putFloat(int1 + 32, matrix4f.m20()).putFloat(int1 + 36, matrix4f.m21()).putFloat(int1 + 40, matrix4f.m22()).putFloat(int1 + 44, matrix4f.m23());
		}

		public void put3x4(Matrix4f matrix4f, int int1, ByteBuffer byteBuffer) {
			if (int1 == 0) {
				this.put3x4_0(matrix4f, byteBuffer);
			} else {
				this.put3x4_N(matrix4f, int1, byteBuffer);
			}
		}

		public void put3x4_0(Matrix4f matrix4f, FloatBuffer floatBuffer) {
			floatBuffer.put(0, matrix4f.m00()).put(1, matrix4f.m01()).put(2, matrix4f.m02()).put(3, matrix4f.m03()).put(4, matrix4f.m10()).put(5, matrix4f.m11()).put(6, matrix4f.m12()).put(7, matrix4f.m13()).put(8, matrix4f.m20()).put(9, matrix4f.m21()).put(10, matrix4f.m22()).put(11, matrix4f.m23());
		}

		public void put3x4_N(Matrix4f matrix4f, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, matrix4f.m00()).put(int1 + 1, matrix4f.m01()).put(int1 + 2, matrix4f.m02()).put(int1 + 3, matrix4f.m03()).put(int1 + 4, matrix4f.m10()).put(int1 + 5, matrix4f.m11()).put(int1 + 6, matrix4f.m12()).put(int1 + 7, matrix4f.m13()).put(int1 + 8, matrix4f.m20()).put(int1 + 9, matrix4f.m21()).put(int1 + 10, matrix4f.m22()).put(int1 + 11, matrix4f.m23());
		}

		public void put3x4(Matrix4f matrix4f, int int1, FloatBuffer floatBuffer) {
			if (int1 == 0) {
				this.put3x4_0(matrix4f, floatBuffer);
			} else {
				this.put3x4_N(matrix4f, int1, floatBuffer);
			}
		}

		public void put3x4_0(Matrix4x3f matrix4x3f, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(0, matrix4x3f.m00()).putFloat(4, matrix4x3f.m01()).putFloat(8, matrix4x3f.m02()).putFloat(12, 0.0F).putFloat(16, matrix4x3f.m10()).putFloat(20, matrix4x3f.m11()).putFloat(24, matrix4x3f.m12()).putFloat(28, 0.0F).putFloat(32, matrix4x3f.m20()).putFloat(36, matrix4x3f.m21()).putFloat(40, matrix4x3f.m22()).putFloat(44, 0.0F);
		}

		private void put3x4_N(Matrix4x3f matrix4x3f, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, matrix4x3f.m00()).putFloat(int1 + 4, matrix4x3f.m01()).putFloat(int1 + 8, matrix4x3f.m02()).putFloat(int1 + 12, 0.0F).putFloat(int1 + 16, matrix4x3f.m10()).putFloat(int1 + 20, matrix4x3f.m11()).putFloat(int1 + 24, matrix4x3f.m12()).putFloat(int1 + 28, 0.0F).putFloat(int1 + 32, matrix4x3f.m20()).putFloat(int1 + 36, matrix4x3f.m21()).putFloat(int1 + 40, matrix4x3f.m22()).putFloat(int1 + 44, 0.0F);
		}

		public void put3x4(Matrix4x3f matrix4x3f, int int1, ByteBuffer byteBuffer) {
			if (int1 == 0) {
				this.put3x4_0(matrix4x3f, byteBuffer);
			} else {
				this.put3x4_N(matrix4x3f, int1, byteBuffer);
			}
		}

		public void put3x4_0(Matrix4x3f matrix4x3f, FloatBuffer floatBuffer) {
			floatBuffer.put(0, matrix4x3f.m00()).put(1, matrix4x3f.m01()).put(2, matrix4x3f.m02()).put(3, 0.0F).put(4, matrix4x3f.m10()).put(5, matrix4x3f.m11()).put(6, matrix4x3f.m12()).put(7, 0.0F).put(8, matrix4x3f.m20()).put(9, matrix4x3f.m21()).put(10, matrix4x3f.m22()).put(11, 0.0F);
		}

		public void put3x4_N(Matrix4x3f matrix4x3f, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, matrix4x3f.m00()).put(int1 + 1, matrix4x3f.m01()).put(int1 + 2, matrix4x3f.m02()).put(int1 + 3, 0.0F).put(int1 + 4, matrix4x3f.m10()).put(int1 + 5, matrix4x3f.m11()).put(int1 + 6, matrix4x3f.m12()).put(int1 + 7, 0.0F).put(int1 + 8, matrix4x3f.m20()).put(int1 + 9, matrix4x3f.m21()).put(int1 + 10, matrix4x3f.m22()).put(int1 + 11, 0.0F);
		}

		public void put3x4(Matrix4x3f matrix4x3f, int int1, FloatBuffer floatBuffer) {
			if (int1 == 0) {
				this.put3x4_0(matrix4x3f, floatBuffer);
			} else {
				this.put3x4_N(matrix4x3f, int1, floatBuffer);
			}
		}

		public void put0(Matrix4x3f matrix4x3f, FloatBuffer floatBuffer) {
			floatBuffer.put(0, matrix4x3f.m00()).put(1, matrix4x3f.m01()).put(2, matrix4x3f.m02()).put(3, matrix4x3f.m10()).put(4, matrix4x3f.m11()).put(5, matrix4x3f.m12()).put(6, matrix4x3f.m20()).put(7, matrix4x3f.m21()).put(8, matrix4x3f.m22()).put(9, matrix4x3f.m30()).put(10, matrix4x3f.m31()).put(11, matrix4x3f.m32());
		}

		public void putN(Matrix4x3f matrix4x3f, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, matrix4x3f.m00()).put(int1 + 1, matrix4x3f.m01()).put(int1 + 2, matrix4x3f.m02()).put(int1 + 3, matrix4x3f.m10()).put(int1 + 4, matrix4x3f.m11()).put(int1 + 5, matrix4x3f.m12()).put(int1 + 6, matrix4x3f.m20()).put(int1 + 7, matrix4x3f.m21()).put(int1 + 8, matrix4x3f.m22()).put(int1 + 9, matrix4x3f.m30()).put(int1 + 10, matrix4x3f.m31()).put(int1 + 11, matrix4x3f.m32());
		}

		public void put(Matrix4x3f matrix4x3f, int int1, FloatBuffer floatBuffer) {
			if (int1 == 0) {
				this.put0(matrix4x3f, floatBuffer);
			} else {
				this.putN(matrix4x3f, int1, floatBuffer);
			}
		}

		public void put0(Matrix4x3f matrix4x3f, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(0, matrix4x3f.m00()).putFloat(4, matrix4x3f.m01()).putFloat(8, matrix4x3f.m02()).putFloat(12, matrix4x3f.m10()).putFloat(16, matrix4x3f.m11()).putFloat(20, matrix4x3f.m12()).putFloat(24, matrix4x3f.m20()).putFloat(28, matrix4x3f.m21()).putFloat(32, matrix4x3f.m22()).putFloat(36, matrix4x3f.m30()).putFloat(40, matrix4x3f.m31()).putFloat(44, matrix4x3f.m32());
		}

		public void putN(Matrix4x3f matrix4x3f, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, matrix4x3f.m00()).putFloat(int1 + 4, matrix4x3f.m01()).putFloat(int1 + 8, matrix4x3f.m02()).putFloat(int1 + 12, matrix4x3f.m10()).putFloat(int1 + 16, matrix4x3f.m11()).putFloat(int1 + 20, matrix4x3f.m12()).putFloat(int1 + 24, matrix4x3f.m20()).putFloat(int1 + 28, matrix4x3f.m21()).putFloat(int1 + 32, matrix4x3f.m22()).putFloat(int1 + 36, matrix4x3f.m30()).putFloat(int1 + 40, matrix4x3f.m31()).putFloat(int1 + 44, matrix4x3f.m32());
		}

		public void put(Matrix4x3f matrix4x3f, int int1, ByteBuffer byteBuffer) {
			if (int1 == 0) {
				this.put0(matrix4x3f, byteBuffer);
			} else {
				this.putN(matrix4x3f, int1, byteBuffer);
			}
		}

		public void put4x4(Matrix4x3f matrix4x3f, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, matrix4x3f.m00()).put(int1 + 1, matrix4x3f.m01()).put(int1 + 2, matrix4x3f.m02()).put(int1 + 3, 0.0F).put(int1 + 4, matrix4x3f.m10()).put(int1 + 5, matrix4x3f.m11()).put(int1 + 6, matrix4x3f.m12()).put(int1 + 7, 0.0F).put(int1 + 8, matrix4x3f.m20()).put(int1 + 9, matrix4x3f.m21()).put(int1 + 10, matrix4x3f.m22()).put(int1 + 11, 0.0F).put(int1 + 12, matrix4x3f.m30()).put(int1 + 13, matrix4x3f.m31()).put(int1 + 14, matrix4x3f.m32()).put(int1 + 15, 1.0F);
		}

		public void put4x4(Matrix4x3f matrix4x3f, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, matrix4x3f.m00()).putFloat(int1 + 4, matrix4x3f.m01()).putFloat(int1 + 8, matrix4x3f.m02()).putFloat(int1 + 12, 0.0F).putFloat(int1 + 16, matrix4x3f.m10()).putFloat(int1 + 20, matrix4x3f.m11()).putFloat(int1 + 24, matrix4x3f.m12()).putFloat(int1 + 28, 0.0F).putFloat(int1 + 32, matrix4x3f.m20()).putFloat(int1 + 36, matrix4x3f.m21()).putFloat(int1 + 40, matrix4x3f.m22()).putFloat(int1 + 44, 0.0F).putFloat(int1 + 48, matrix4x3f.m30()).putFloat(int1 + 52, matrix4x3f.m31()).putFloat(int1 + 56, matrix4x3f.m32()).putFloat(int1 + 60, 1.0F);
		}

		public void put4x4(Matrix4x3d matrix4x3d, int int1, DoubleBuffer doubleBuffer) {
			doubleBuffer.put(int1, matrix4x3d.m00()).put(int1 + 1, matrix4x3d.m01()).put(int1 + 2, matrix4x3d.m02()).put(int1 + 3, 0.0).put(int1 + 4, matrix4x3d.m10()).put(int1 + 5, matrix4x3d.m11()).put(int1 + 6, matrix4x3d.m12()).put(int1 + 7, 0.0).put(int1 + 8, matrix4x3d.m20()).put(int1 + 9, matrix4x3d.m21()).put(int1 + 10, matrix4x3d.m22()).put(int1 + 11, 0.0).put(int1 + 12, matrix4x3d.m30()).put(int1 + 13, matrix4x3d.m31()).put(int1 + 14, matrix4x3d.m32()).put(int1 + 15, 1.0);
		}

		public void put4x4(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putDouble(int1, matrix4x3d.m00()).putDouble(int1 + 8, matrix4x3d.m01()).putDouble(int1 + 16, matrix4x3d.m02()).putDouble(int1 + 24, 0.0).putDouble(int1 + 32, matrix4x3d.m10()).putDouble(int1 + 40, matrix4x3d.m11()).putDouble(int1 + 48, matrix4x3d.m12()).putDouble(int1 + 56, 0.0).putDouble(int1 + 64, matrix4x3d.m20()).putDouble(int1 + 72, matrix4x3d.m21()).putDouble(int1 + 80, matrix4x3d.m22()).putDouble(int1 + 88, 0.0).putDouble(int1 + 96, matrix4x3d.m30()).putDouble(int1 + 104, matrix4x3d.m31()).putDouble(int1 + 112, matrix4x3d.m32()).putDouble(int1 + 120, 1.0);
		}

		public void put4x4(Matrix3x2f matrix3x2f, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, matrix3x2f.m00()).put(int1 + 1, matrix3x2f.m01()).put(int1 + 2, 0.0F).put(int1 + 3, 0.0F).put(int1 + 4, matrix3x2f.m10()).put(int1 + 5, matrix3x2f.m11()).put(int1 + 6, 0.0F).put(int1 + 7, 0.0F).put(int1 + 8, 0.0F).put(int1 + 9, 0.0F).put(int1 + 10, 1.0F).put(int1 + 11, 0.0F).put(int1 + 12, matrix3x2f.m20()).put(int1 + 13, matrix3x2f.m21()).put(int1 + 14, 0.0F).put(int1 + 15, 1.0F);
		}

		public void put4x4(Matrix3x2f matrix3x2f, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, matrix3x2f.m00()).putFloat(int1 + 4, matrix3x2f.m01()).putFloat(int1 + 8, 0.0F).putFloat(int1 + 12, 0.0F).putFloat(int1 + 16, matrix3x2f.m10()).putFloat(int1 + 20, matrix3x2f.m11()).putFloat(int1 + 24, 0.0F).putFloat(int1 + 28, 0.0F).putFloat(int1 + 32, 0.0F).putFloat(int1 + 36, 0.0F).putFloat(int1 + 40, 1.0F).putFloat(int1 + 44, 0.0F).putFloat(int1 + 48, matrix3x2f.m20()).putFloat(int1 + 52, matrix3x2f.m21()).putFloat(int1 + 56, 0.0F).putFloat(int1 + 60, 1.0F);
		}

		public void put4x4(Matrix3x2d matrix3x2d, int int1, DoubleBuffer doubleBuffer) {
			doubleBuffer.put(int1, matrix3x2d.m00()).put(int1 + 1, matrix3x2d.m01()).put(int1 + 2, 0.0).put(int1 + 3, 0.0).put(int1 + 4, matrix3x2d.m10()).put(int1 + 5, matrix3x2d.m11()).put(int1 + 6, 0.0).put(int1 + 7, 0.0).put(int1 + 8, 0.0).put(int1 + 9, 0.0).put(int1 + 10, 1.0).put(int1 + 11, 0.0).put(int1 + 12, matrix3x2d.m20()).put(int1 + 13, matrix3x2d.m21()).put(int1 + 14, 0.0).put(int1 + 15, 1.0);
		}

		public void put4x4(Matrix3x2d matrix3x2d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putDouble(int1, matrix3x2d.m00()).putDouble(int1 + 8, matrix3x2d.m01()).putDouble(int1 + 16, 0.0).putDouble(int1 + 24, 0.0).putDouble(int1 + 32, matrix3x2d.m10()).putDouble(int1 + 40, matrix3x2d.m11()).putDouble(int1 + 48, 0.0).putDouble(int1 + 56, 0.0).putDouble(int1 + 64, 0.0).putDouble(int1 + 72, 0.0).putDouble(int1 + 80, 1.0).putDouble(int1 + 88, 0.0).putDouble(int1 + 96, matrix3x2d.m20()).putDouble(int1 + 104, matrix3x2d.m21()).putDouble(int1 + 112, 0.0).putDouble(int1 + 120, 1.0);
		}

		public void put3x3(Matrix3x2f matrix3x2f, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, matrix3x2f.m00()).put(int1 + 1, matrix3x2f.m01()).put(int1 + 2, 0.0F).put(int1 + 3, matrix3x2f.m10()).put(int1 + 4, matrix3x2f.m11()).put(int1 + 5, 0.0F).put(int1 + 6, matrix3x2f.m20()).put(int1 + 7, matrix3x2f.m21()).put(int1 + 8, 1.0F);
		}

		public void put3x3(Matrix3x2f matrix3x2f, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, matrix3x2f.m00()).putFloat(int1 + 4, matrix3x2f.m01()).putFloat(int1 + 8, 0.0F).putFloat(int1 + 12, matrix3x2f.m10()).putFloat(int1 + 16, matrix3x2f.m11()).putFloat(int1 + 20, 0.0F).putFloat(int1 + 24, matrix3x2f.m20()).putFloat(int1 + 28, matrix3x2f.m21()).putFloat(int1 + 32, 1.0F);
		}

		public void put3x3(Matrix3x2d matrix3x2d, int int1, DoubleBuffer doubleBuffer) {
			doubleBuffer.put(int1, matrix3x2d.m00()).put(int1 + 1, matrix3x2d.m01()).put(int1 + 2, 0.0).put(int1 + 3, matrix3x2d.m10()).put(int1 + 4, matrix3x2d.m11()).put(int1 + 5, 0.0).put(int1 + 6, matrix3x2d.m20()).put(int1 + 7, matrix3x2d.m21()).put(int1 + 8, 1.0);
		}

		public void put3x3(Matrix3x2d matrix3x2d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putDouble(int1, matrix3x2d.m00()).putDouble(int1 + 8, matrix3x2d.m01()).putDouble(int1 + 16, 0.0).putDouble(int1 + 24, matrix3x2d.m10()).putDouble(int1 + 32, matrix3x2d.m11()).putDouble(int1 + 40, 0.0).putDouble(int1 + 48, matrix3x2d.m20()).putDouble(int1 + 56, matrix3x2d.m21()).putDouble(int1 + 64, 1.0);
		}

		private void putTransposedN(Matrix4f matrix4f, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, matrix4f.m00()).put(int1 + 1, matrix4f.m10()).put(int1 + 2, matrix4f.m20()).put(int1 + 3, matrix4f.m30()).put(int1 + 4, matrix4f.m01()).put(int1 + 5, matrix4f.m11()).put(int1 + 6, matrix4f.m21()).put(int1 + 7, matrix4f.m31()).put(int1 + 8, matrix4f.m02()).put(int1 + 9, matrix4f.m12()).put(int1 + 10, matrix4f.m22()).put(int1 + 11, matrix4f.m32()).put(int1 + 12, matrix4f.m03()).put(int1 + 13, matrix4f.m13()).put(int1 + 14, matrix4f.m23()).put(int1 + 15, matrix4f.m33());
		}

		private void putTransposed0(Matrix4f matrix4f, FloatBuffer floatBuffer) {
			floatBuffer.put(0, matrix4f.m00()).put(1, matrix4f.m10()).put(2, matrix4f.m20()).put(3, matrix4f.m30()).put(4, matrix4f.m01()).put(5, matrix4f.m11()).put(6, matrix4f.m21()).put(7, matrix4f.m31()).put(8, matrix4f.m02()).put(9, matrix4f.m12()).put(10, matrix4f.m22()).put(11, matrix4f.m32()).put(12, matrix4f.m03()).put(13, matrix4f.m13()).put(14, matrix4f.m23()).put(15, matrix4f.m33());
		}

		public void putTransposed(Matrix4f matrix4f, int int1, FloatBuffer floatBuffer) {
			if (int1 == 0) {
				this.putTransposed0(matrix4f, floatBuffer);
			} else {
				this.putTransposedN(matrix4f, int1, floatBuffer);
			}
		}

		private void putTransposedN(Matrix4f matrix4f, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, matrix4f.m00()).putFloat(int1 + 4, matrix4f.m10()).putFloat(int1 + 8, matrix4f.m20()).putFloat(int1 + 12, matrix4f.m30()).putFloat(int1 + 16, matrix4f.m01()).putFloat(int1 + 20, matrix4f.m11()).putFloat(int1 + 24, matrix4f.m21()).putFloat(int1 + 28, matrix4f.m31()).putFloat(int1 + 32, matrix4f.m02()).putFloat(int1 + 36, matrix4f.m12()).putFloat(int1 + 40, matrix4f.m22()).putFloat(int1 + 44, matrix4f.m32()).putFloat(int1 + 48, matrix4f.m03()).putFloat(int1 + 52, matrix4f.m13()).putFloat(int1 + 56, matrix4f.m23()).putFloat(int1 + 60, matrix4f.m33());
		}

		private void putTransposed0(Matrix4f matrix4f, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(0, matrix4f.m00()).putFloat(4, matrix4f.m10()).putFloat(8, matrix4f.m20()).putFloat(12, matrix4f.m30()).putFloat(16, matrix4f.m01()).putFloat(20, matrix4f.m11()).putFloat(24, matrix4f.m21()).putFloat(28, matrix4f.m31()).putFloat(32, matrix4f.m02()).putFloat(36, matrix4f.m12()).putFloat(40, matrix4f.m22()).putFloat(44, matrix4f.m32()).putFloat(48, matrix4f.m03()).putFloat(52, matrix4f.m13()).putFloat(56, matrix4f.m23()).putFloat(60, matrix4f.m33());
		}

		public void putTransposed(Matrix4f matrix4f, int int1, ByteBuffer byteBuffer) {
			if (int1 == 0) {
				this.putTransposed0(matrix4f, byteBuffer);
			} else {
				this.putTransposedN(matrix4f, int1, byteBuffer);
			}
		}

		public void put4x3Transposed(Matrix4f matrix4f, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, matrix4f.m00()).put(int1 + 1, matrix4f.m10()).put(int1 + 2, matrix4f.m20()).put(int1 + 3, matrix4f.m30()).put(int1 + 4, matrix4f.m01()).put(int1 + 5, matrix4f.m11()).put(int1 + 6, matrix4f.m21()).put(int1 + 7, matrix4f.m31()).put(int1 + 8, matrix4f.m02()).put(int1 + 9, matrix4f.m12()).put(int1 + 10, matrix4f.m22()).put(int1 + 11, matrix4f.m32());
		}

		public void put4x3Transposed(Matrix4f matrix4f, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, matrix4f.m00()).putFloat(int1 + 4, matrix4f.m10()).putFloat(int1 + 8, matrix4f.m20()).putFloat(int1 + 12, matrix4f.m30()).putFloat(int1 + 16, matrix4f.m01()).putFloat(int1 + 20, matrix4f.m11()).putFloat(int1 + 24, matrix4f.m21()).putFloat(int1 + 28, matrix4f.m31()).putFloat(int1 + 32, matrix4f.m02()).putFloat(int1 + 36, matrix4f.m12()).putFloat(int1 + 40, matrix4f.m22()).putFloat(int1 + 44, matrix4f.m32());
		}

		public void putTransposed(Matrix4x3f matrix4x3f, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, matrix4x3f.m00()).put(int1 + 1, matrix4x3f.m10()).put(int1 + 2, matrix4x3f.m20()).put(int1 + 3, matrix4x3f.m30()).put(int1 + 4, matrix4x3f.m01()).put(int1 + 5, matrix4x3f.m11()).put(int1 + 6, matrix4x3f.m21()).put(int1 + 7, matrix4x3f.m31()).put(int1 + 8, matrix4x3f.m02()).put(int1 + 9, matrix4x3f.m12()).put(int1 + 10, matrix4x3f.m22()).put(int1 + 11, matrix4x3f.m32());
		}

		public void putTransposed(Matrix4x3f matrix4x3f, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, matrix4x3f.m00()).putFloat(int1 + 4, matrix4x3f.m10()).putFloat(int1 + 8, matrix4x3f.m20()).putFloat(int1 + 12, matrix4x3f.m30()).putFloat(int1 + 16, matrix4x3f.m01()).putFloat(int1 + 20, matrix4x3f.m11()).putFloat(int1 + 24, matrix4x3f.m21()).putFloat(int1 + 28, matrix4x3f.m31()).putFloat(int1 + 32, matrix4x3f.m02()).putFloat(int1 + 36, matrix4x3f.m12()).putFloat(int1 + 40, matrix4x3f.m22()).putFloat(int1 + 44, matrix4x3f.m32());
		}

		public void putTransposed(Matrix3f matrix3f, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, matrix3f.m00()).put(int1 + 1, matrix3f.m10()).put(int1 + 2, matrix3f.m20()).put(int1 + 3, matrix3f.m01()).put(int1 + 4, matrix3f.m11()).put(int1 + 5, matrix3f.m21()).put(int1 + 6, matrix3f.m02()).put(int1 + 7, matrix3f.m12()).put(int1 + 8, matrix3f.m22());
		}

		public void putTransposed(Matrix3f matrix3f, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, matrix3f.m00()).putFloat(int1 + 4, matrix3f.m10()).putFloat(int1 + 8, matrix3f.m20()).putFloat(int1 + 12, matrix3f.m01()).putFloat(int1 + 16, matrix3f.m11()).putFloat(int1 + 20, matrix3f.m21()).putFloat(int1 + 24, matrix3f.m02()).putFloat(int1 + 28, matrix3f.m12()).putFloat(int1 + 32, matrix3f.m22());
		}

		public void putTransposed(Matrix2f matrix2f, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, matrix2f.m00()).put(int1 + 1, matrix2f.m10()).put(int1 + 2, matrix2f.m01()).put(int1 + 3, matrix2f.m11());
		}

		public void putTransposed(Matrix2f matrix2f, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, matrix2f.m00()).putFloat(int1 + 4, matrix2f.m10()).putFloat(int1 + 8, matrix2f.m01()).putFloat(int1 + 12, matrix2f.m11());
		}

		public void put(Matrix4d matrix4d, int int1, DoubleBuffer doubleBuffer) {
			doubleBuffer.put(int1, matrix4d.m00()).put(int1 + 1, matrix4d.m01()).put(int1 + 2, matrix4d.m02()).put(int1 + 3, matrix4d.m03()).put(int1 + 4, matrix4d.m10()).put(int1 + 5, matrix4d.m11()).put(int1 + 6, matrix4d.m12()).put(int1 + 7, matrix4d.m13()).put(int1 + 8, matrix4d.m20()).put(int1 + 9, matrix4d.m21()).put(int1 + 10, matrix4d.m22()).put(int1 + 11, matrix4d.m23()).put(int1 + 12, matrix4d.m30()).put(int1 + 13, matrix4d.m31()).put(int1 + 14, matrix4d.m32()).put(int1 + 15, matrix4d.m33());
		}

		public void put(Matrix4d matrix4d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putDouble(int1, matrix4d.m00()).putDouble(int1 + 8, matrix4d.m01()).putDouble(int1 + 16, matrix4d.m02()).putDouble(int1 + 24, matrix4d.m03()).putDouble(int1 + 32, matrix4d.m10()).putDouble(int1 + 40, matrix4d.m11()).putDouble(int1 + 48, matrix4d.m12()).putDouble(int1 + 56, matrix4d.m13()).putDouble(int1 + 64, matrix4d.m20()).putDouble(int1 + 72, matrix4d.m21()).putDouble(int1 + 80, matrix4d.m22()).putDouble(int1 + 88, matrix4d.m23()).putDouble(int1 + 96, matrix4d.m30()).putDouble(int1 + 104, matrix4d.m31()).putDouble(int1 + 112, matrix4d.m32()).putDouble(int1 + 120, matrix4d.m33());
		}

		public void put(Matrix4x3d matrix4x3d, int int1, DoubleBuffer doubleBuffer) {
			doubleBuffer.put(int1, matrix4x3d.m00()).put(int1 + 1, matrix4x3d.m01()).put(int1 + 2, matrix4x3d.m02()).put(int1 + 3, matrix4x3d.m10()).put(int1 + 4, matrix4x3d.m11()).put(int1 + 5, matrix4x3d.m12()).put(int1 + 6, matrix4x3d.m20()).put(int1 + 7, matrix4x3d.m21()).put(int1 + 8, matrix4x3d.m22()).put(int1 + 9, matrix4x3d.m30()).put(int1 + 10, matrix4x3d.m31()).put(int1 + 11, matrix4x3d.m32());
		}

		public void put(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putDouble(int1, matrix4x3d.m00()).putDouble(int1 + 8, matrix4x3d.m01()).putDouble(int1 + 16, matrix4x3d.m02()).putDouble(int1 + 24, matrix4x3d.m10()).putDouble(int1 + 32, matrix4x3d.m11()).putDouble(int1 + 40, matrix4x3d.m12()).putDouble(int1 + 48, matrix4x3d.m20()).putDouble(int1 + 56, matrix4x3d.m21()).putDouble(int1 + 64, matrix4x3d.m22()).putDouble(int1 + 72, matrix4x3d.m30()).putDouble(int1 + 80, matrix4x3d.m31()).putDouble(int1 + 88, matrix4x3d.m32());
		}

		public void putf(Matrix4d matrix4d, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, (float)matrix4d.m00()).put(int1 + 1, (float)matrix4d.m01()).put(int1 + 2, (float)matrix4d.m02()).put(int1 + 3, (float)matrix4d.m03()).put(int1 + 4, (float)matrix4d.m10()).put(int1 + 5, (float)matrix4d.m11()).put(int1 + 6, (float)matrix4d.m12()).put(int1 + 7, (float)matrix4d.m13()).put(int1 + 8, (float)matrix4d.m20()).put(int1 + 9, (float)matrix4d.m21()).put(int1 + 10, (float)matrix4d.m22()).put(int1 + 11, (float)matrix4d.m23()).put(int1 + 12, (float)matrix4d.m30()).put(int1 + 13, (float)matrix4d.m31()).put(int1 + 14, (float)matrix4d.m32()).put(int1 + 15, (float)matrix4d.m33());
		}

		public void putf(Matrix4d matrix4d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, (float)matrix4d.m00()).putFloat(int1 + 4, (float)matrix4d.m01()).putFloat(int1 + 8, (float)matrix4d.m02()).putFloat(int1 + 12, (float)matrix4d.m03()).putFloat(int1 + 16, (float)matrix4d.m10()).putFloat(int1 + 20, (float)matrix4d.m11()).putFloat(int1 + 24, (float)matrix4d.m12()).putFloat(int1 + 28, (float)matrix4d.m13()).putFloat(int1 + 32, (float)matrix4d.m20()).putFloat(int1 + 36, (float)matrix4d.m21()).putFloat(int1 + 40, (float)matrix4d.m22()).putFloat(int1 + 44, (float)matrix4d.m23()).putFloat(int1 + 48, (float)matrix4d.m30()).putFloat(int1 + 52, (float)matrix4d.m31()).putFloat(int1 + 56, (float)matrix4d.m32()).putFloat(int1 + 60, (float)matrix4d.m33());
		}

		public void putf(Matrix4x3d matrix4x3d, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, (float)matrix4x3d.m00()).put(int1 + 1, (float)matrix4x3d.m01()).put(int1 + 2, (float)matrix4x3d.m02()).put(int1 + 3, (float)matrix4x3d.m10()).put(int1 + 4, (float)matrix4x3d.m11()).put(int1 + 5, (float)matrix4x3d.m12()).put(int1 + 6, (float)matrix4x3d.m20()).put(int1 + 7, (float)matrix4x3d.m21()).put(int1 + 8, (float)matrix4x3d.m22()).put(int1 + 9, (float)matrix4x3d.m30()).put(int1 + 10, (float)matrix4x3d.m31()).put(int1 + 11, (float)matrix4x3d.m32());
		}

		public void putf(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, (float)matrix4x3d.m00()).putFloat(int1 + 4, (float)matrix4x3d.m01()).putFloat(int1 + 8, (float)matrix4x3d.m02()).putFloat(int1 + 12, (float)matrix4x3d.m10()).putFloat(int1 + 16, (float)matrix4x3d.m11()).putFloat(int1 + 20, (float)matrix4x3d.m12()).putFloat(int1 + 24, (float)matrix4x3d.m20()).putFloat(int1 + 28, (float)matrix4x3d.m21()).putFloat(int1 + 32, (float)matrix4x3d.m22()).putFloat(int1 + 36, (float)matrix4x3d.m30()).putFloat(int1 + 40, (float)matrix4x3d.m31()).putFloat(int1 + 44, (float)matrix4x3d.m32());
		}

		public void putTransposed(Matrix4d matrix4d, int int1, DoubleBuffer doubleBuffer) {
			doubleBuffer.put(int1, matrix4d.m00()).put(int1 + 1, matrix4d.m10()).put(int1 + 2, matrix4d.m20()).put(int1 + 3, matrix4d.m30()).put(int1 + 4, matrix4d.m01()).put(int1 + 5, matrix4d.m11()).put(int1 + 6, matrix4d.m21()).put(int1 + 7, matrix4d.m31()).put(int1 + 8, matrix4d.m02()).put(int1 + 9, matrix4d.m12()).put(int1 + 10, matrix4d.m22()).put(int1 + 11, matrix4d.m32()).put(int1 + 12, matrix4d.m03()).put(int1 + 13, matrix4d.m13()).put(int1 + 14, matrix4d.m23()).put(int1 + 15, matrix4d.m33());
		}

		public void putTransposed(Matrix4d matrix4d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putDouble(int1, matrix4d.m00()).putDouble(int1 + 8, matrix4d.m10()).putDouble(int1 + 16, matrix4d.m20()).putDouble(int1 + 24, matrix4d.m30()).putDouble(int1 + 32, matrix4d.m01()).putDouble(int1 + 40, matrix4d.m11()).putDouble(int1 + 48, matrix4d.m21()).putDouble(int1 + 56, matrix4d.m31()).putDouble(int1 + 64, matrix4d.m02()).putDouble(int1 + 72, matrix4d.m12()).putDouble(int1 + 80, matrix4d.m22()).putDouble(int1 + 88, matrix4d.m32()).putDouble(int1 + 96, matrix4d.m03()).putDouble(int1 + 104, matrix4d.m13()).putDouble(int1 + 112, matrix4d.m23()).putDouble(int1 + 120, matrix4d.m33());
		}

		public void put4x3Transposed(Matrix4d matrix4d, int int1, DoubleBuffer doubleBuffer) {
			doubleBuffer.put(int1, matrix4d.m00()).put(int1 + 1, matrix4d.m10()).put(int1 + 2, matrix4d.m20()).put(int1 + 3, matrix4d.m30()).put(int1 + 4, matrix4d.m01()).put(int1 + 5, matrix4d.m11()).put(int1 + 6, matrix4d.m21()).put(int1 + 7, matrix4d.m31()).put(int1 + 8, matrix4d.m02()).put(int1 + 9, matrix4d.m12()).put(int1 + 10, matrix4d.m22()).put(int1 + 11, matrix4d.m32());
		}

		public void put4x3Transposed(Matrix4d matrix4d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putDouble(int1, matrix4d.m00()).putDouble(int1 + 8, matrix4d.m10()).putDouble(int1 + 16, matrix4d.m20()).putDouble(int1 + 24, matrix4d.m30()).putDouble(int1 + 32, matrix4d.m01()).putDouble(int1 + 40, matrix4d.m11()).putDouble(int1 + 48, matrix4d.m21()).putDouble(int1 + 56, matrix4d.m31()).putDouble(int1 + 64, matrix4d.m02()).putDouble(int1 + 72, matrix4d.m12()).putDouble(int1 + 80, matrix4d.m22()).putDouble(int1 + 88, matrix4d.m32());
		}

		public void putTransposed(Matrix4x3d matrix4x3d, int int1, DoubleBuffer doubleBuffer) {
			doubleBuffer.put(int1, matrix4x3d.m00()).put(int1 + 1, matrix4x3d.m10()).put(int1 + 2, matrix4x3d.m20()).put(int1 + 3, matrix4x3d.m30()).put(int1 + 4, matrix4x3d.m01()).put(int1 + 5, matrix4x3d.m11()).put(int1 + 6, matrix4x3d.m21()).put(int1 + 7, matrix4x3d.m31()).put(int1 + 8, matrix4x3d.m02()).put(int1 + 9, matrix4x3d.m12()).put(int1 + 10, matrix4x3d.m22()).put(int1 + 11, matrix4x3d.m32());
		}

		public void putTransposed(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putDouble(int1, matrix4x3d.m00()).putDouble(int1 + 8, matrix4x3d.m10()).putDouble(int1 + 16, matrix4x3d.m20()).putDouble(int1 + 24, matrix4x3d.m30()).putDouble(int1 + 32, matrix4x3d.m01()).putDouble(int1 + 40, matrix4x3d.m11()).putDouble(int1 + 48, matrix4x3d.m21()).putDouble(int1 + 56, matrix4x3d.m31()).putDouble(int1 + 64, matrix4x3d.m02()).putDouble(int1 + 72, matrix4x3d.m12()).putDouble(int1 + 80, matrix4x3d.m22()).putDouble(int1 + 88, matrix4x3d.m32());
		}

		public void putTransposed(Matrix2d matrix2d, int int1, DoubleBuffer doubleBuffer) {
			doubleBuffer.put(int1, matrix2d.m00()).put(int1 + 1, matrix2d.m10()).put(int1 + 2, matrix2d.m01()).put(int1 + 3, matrix2d.m11());
		}

		public void putTransposed(Matrix2d matrix2d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putDouble(int1, matrix2d.m00()).putDouble(int1 + 8, matrix2d.m10()).putDouble(int1 + 16, matrix2d.m01()).putDouble(int1 + 24, matrix2d.m11());
		}

		public void putfTransposed(Matrix4x3d matrix4x3d, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, (float)matrix4x3d.m00()).put(int1 + 1, (float)matrix4x3d.m10()).put(int1 + 2, (float)matrix4x3d.m20()).put(int1 + 3, (float)matrix4x3d.m30()).put(int1 + 4, (float)matrix4x3d.m01()).put(int1 + 5, (float)matrix4x3d.m11()).put(int1 + 6, (float)matrix4x3d.m21()).put(int1 + 7, (float)matrix4x3d.m31()).put(int1 + 8, (float)matrix4x3d.m02()).put(int1 + 9, (float)matrix4x3d.m12()).put(int1 + 10, (float)matrix4x3d.m22()).put(int1 + 11, (float)matrix4x3d.m32());
		}

		public void putfTransposed(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, (float)matrix4x3d.m00()).putFloat(int1 + 4, (float)matrix4x3d.m10()).putFloat(int1 + 8, (float)matrix4x3d.m20()).putFloat(int1 + 12, (float)matrix4x3d.m30()).putFloat(int1 + 16, (float)matrix4x3d.m01()).putFloat(int1 + 20, (float)matrix4x3d.m11()).putFloat(int1 + 24, (float)matrix4x3d.m21()).putFloat(int1 + 28, (float)matrix4x3d.m31()).putFloat(int1 + 32, (float)matrix4x3d.m02()).putFloat(int1 + 36, (float)matrix4x3d.m12()).putFloat(int1 + 40, (float)matrix4x3d.m22()).putFloat(int1 + 44, (float)matrix4x3d.m32());
		}

		public void putfTransposed(Matrix2d matrix2d, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, (float)matrix2d.m00()).put(int1 + 1, (float)matrix2d.m10()).put(int1 + 2, (float)matrix2d.m01()).put(int1 + 3, (float)matrix2d.m11());
		}

		public void putfTransposed(Matrix2d matrix2d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, (float)matrix2d.m00()).putFloat(int1 + 4, (float)matrix2d.m10()).putFloat(int1 + 8, (float)matrix2d.m01()).putFloat(int1 + 12, (float)matrix2d.m11());
		}

		public void putfTransposed(Matrix4d matrix4d, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, (float)matrix4d.m00()).put(int1 + 1, (float)matrix4d.m10()).put(int1 + 2, (float)matrix4d.m20()).put(int1 + 3, (float)matrix4d.m30()).put(int1 + 4, (float)matrix4d.m01()).put(int1 + 5, (float)matrix4d.m11()).put(int1 + 6, (float)matrix4d.m21()).put(int1 + 7, (float)matrix4d.m31()).put(int1 + 8, (float)matrix4d.m02()).put(int1 + 9, (float)matrix4d.m12()).put(int1 + 10, (float)matrix4d.m22()).put(int1 + 11, (float)matrix4d.m32()).put(int1 + 12, (float)matrix4d.m03()).put(int1 + 13, (float)matrix4d.m13()).put(int1 + 14, (float)matrix4d.m23()).put(int1 + 15, (float)matrix4d.m33());
		}

		public void putfTransposed(Matrix4d matrix4d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, (float)matrix4d.m00()).putFloat(int1 + 4, (float)matrix4d.m10()).putFloat(int1 + 8, (float)matrix4d.m20()).putFloat(int1 + 12, (float)matrix4d.m30()).putFloat(int1 + 16, (float)matrix4d.m01()).putFloat(int1 + 20, (float)matrix4d.m11()).putFloat(int1 + 24, (float)matrix4d.m21()).putFloat(int1 + 28, (float)matrix4d.m31()).putFloat(int1 + 32, (float)matrix4d.m02()).putFloat(int1 + 36, (float)matrix4d.m12()).putFloat(int1 + 40, (float)matrix4d.m22()).putFloat(int1 + 44, (float)matrix4d.m32()).putFloat(int1 + 48, (float)matrix4d.m03()).putFloat(int1 + 52, (float)matrix4d.m13()).putFloat(int1 + 56, (float)matrix4d.m23()).putFloat(int1 + 60, (float)matrix4d.m33());
		}

		public void put0(Matrix3f matrix3f, FloatBuffer floatBuffer) {
			floatBuffer.put(0, matrix3f.m00()).put(1, matrix3f.m01()).put(2, matrix3f.m02()).put(3, matrix3f.m10()).put(4, matrix3f.m11()).put(5, matrix3f.m12()).put(6, matrix3f.m20()).put(7, matrix3f.m21()).put(8, matrix3f.m22());
		}

		public void putN(Matrix3f matrix3f, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, matrix3f.m00()).put(int1 + 1, matrix3f.m01()).put(int1 + 2, matrix3f.m02()).put(int1 + 3, matrix3f.m10()).put(int1 + 4, matrix3f.m11()).put(int1 + 5, matrix3f.m12()).put(int1 + 6, matrix3f.m20()).put(int1 + 7, matrix3f.m21()).put(int1 + 8, matrix3f.m22());
		}

		public void put(Matrix3f matrix3f, int int1, FloatBuffer floatBuffer) {
			if (int1 == 0) {
				this.put0(matrix3f, floatBuffer);
			} else {
				this.putN(matrix3f, int1, floatBuffer);
			}
		}

		public void put0(Matrix3f matrix3f, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(0, matrix3f.m00()).putFloat(4, matrix3f.m01()).putFloat(8, matrix3f.m02()).putFloat(12, matrix3f.m10()).putFloat(16, matrix3f.m11()).putFloat(20, matrix3f.m12()).putFloat(24, matrix3f.m20()).putFloat(28, matrix3f.m21()).putFloat(32, matrix3f.m22());
		}

		public void putN(Matrix3f matrix3f, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, matrix3f.m00()).putFloat(int1 + 4, matrix3f.m01()).putFloat(int1 + 8, matrix3f.m02()).putFloat(int1 + 12, matrix3f.m10()).putFloat(int1 + 16, matrix3f.m11()).putFloat(int1 + 20, matrix3f.m12()).putFloat(int1 + 24, matrix3f.m20()).putFloat(int1 + 28, matrix3f.m21()).putFloat(int1 + 32, matrix3f.m22());
		}

		public void put(Matrix3f matrix3f, int int1, ByteBuffer byteBuffer) {
			if (int1 == 0) {
				this.put0(matrix3f, byteBuffer);
			} else {
				this.putN(matrix3f, int1, byteBuffer);
			}
		}

		public void put3x4_0(Matrix3f matrix3f, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(0, matrix3f.m00()).putFloat(4, matrix3f.m01()).putFloat(8, matrix3f.m02()).putFloat(12, 0.0F).putFloat(16, matrix3f.m10()).putFloat(20, matrix3f.m11()).putFloat(24, matrix3f.m12()).putFloat(28, 0.0F).putFloat(32, matrix3f.m20()).putFloat(36, matrix3f.m21()).putFloat(40, matrix3f.m22()).putFloat(44, 0.0F);
		}

		private void put3x4_N(Matrix3f matrix3f, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, matrix3f.m00()).putFloat(int1 + 4, matrix3f.m01()).putFloat(int1 + 8, matrix3f.m02()).putFloat(int1 + 12, 0.0F).putFloat(int1 + 16, matrix3f.m10()).putFloat(int1 + 20, matrix3f.m11()).putFloat(int1 + 24, matrix3f.m12()).putFloat(int1 + 28, 0.0F).putFloat(int1 + 32, matrix3f.m20()).putFloat(int1 + 36, matrix3f.m21()).putFloat(int1 + 40, matrix3f.m22()).putFloat(int1 + 44, 0.0F);
		}

		public void put3x4(Matrix3f matrix3f, int int1, ByteBuffer byteBuffer) {
			if (int1 == 0) {
				this.put3x4_0(matrix3f, byteBuffer);
			} else {
				this.put3x4_N(matrix3f, int1, byteBuffer);
			}
		}

		public void put3x4_0(Matrix3f matrix3f, FloatBuffer floatBuffer) {
			floatBuffer.put(0, matrix3f.m00()).put(1, matrix3f.m01()).put(2, matrix3f.m02()).put(3, 0.0F).put(4, matrix3f.m10()).put(5, matrix3f.m11()).put(6, matrix3f.m12()).put(7, 0.0F).put(8, matrix3f.m20()).put(9, matrix3f.m21()).put(10, matrix3f.m22()).put(11, 0.0F);
		}

		public void put3x4_N(Matrix3f matrix3f, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, matrix3f.m00()).put(int1 + 1, matrix3f.m01()).put(int1 + 2, matrix3f.m02()).put(int1 + 3, 0.0F).put(int1 + 4, matrix3f.m10()).put(int1 + 5, matrix3f.m11()).put(int1 + 6, matrix3f.m12()).put(int1 + 7, 0.0F).put(int1 + 8, matrix3f.m20()).put(int1 + 9, matrix3f.m21()).put(int1 + 10, matrix3f.m22()).put(int1 + 11, 0.0F);
		}

		public void put3x4(Matrix3f matrix3f, int int1, FloatBuffer floatBuffer) {
			if (int1 == 0) {
				this.put3x4_0(matrix3f, floatBuffer);
			} else {
				this.put3x4_N(matrix3f, int1, floatBuffer);
			}
		}

		public void put(Matrix3d matrix3d, int int1, DoubleBuffer doubleBuffer) {
			doubleBuffer.put(int1, matrix3d.m00()).put(int1 + 1, matrix3d.m01()).put(int1 + 2, matrix3d.m02()).put(int1 + 3, matrix3d.m10()).put(int1 + 4, matrix3d.m11()).put(int1 + 5, matrix3d.m12()).put(int1 + 6, matrix3d.m20()).put(int1 + 7, matrix3d.m21()).put(int1 + 8, matrix3d.m22());
		}

		public void put(Matrix3d matrix3d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putDouble(int1, matrix3d.m00()).putDouble(int1 + 8, matrix3d.m01()).putDouble(int1 + 16, matrix3d.m02()).putDouble(int1 + 24, matrix3d.m10()).putDouble(int1 + 32, matrix3d.m11()).putDouble(int1 + 40, matrix3d.m12()).putDouble(int1 + 48, matrix3d.m20()).putDouble(int1 + 56, matrix3d.m21()).putDouble(int1 + 64, matrix3d.m22());
		}

		public void put(Matrix3x2f matrix3x2f, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, matrix3x2f.m00()).put(int1 + 1, matrix3x2f.m01()).put(int1 + 2, matrix3x2f.m10()).put(int1 + 3, matrix3x2f.m11()).put(int1 + 4, matrix3x2f.m20()).put(int1 + 5, matrix3x2f.m21());
		}

		public void put(Matrix3x2f matrix3x2f, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, matrix3x2f.m00()).putFloat(int1 + 4, matrix3x2f.m01()).putFloat(int1 + 8, matrix3x2f.m10()).putFloat(int1 + 12, matrix3x2f.m11()).putFloat(int1 + 16, matrix3x2f.m20()).putFloat(int1 + 20, matrix3x2f.m21());
		}

		public void put(Matrix3x2d matrix3x2d, int int1, DoubleBuffer doubleBuffer) {
			doubleBuffer.put(int1, matrix3x2d.m00()).put(int1 + 1, matrix3x2d.m01()).put(int1 + 2, matrix3x2d.m10()).put(int1 + 3, matrix3x2d.m11()).put(int1 + 4, matrix3x2d.m20()).put(int1 + 5, matrix3x2d.m21());
		}

		public void put(Matrix3x2d matrix3x2d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putDouble(int1, matrix3x2d.m00()).putDouble(int1 + 8, matrix3x2d.m01()).putDouble(int1 + 16, matrix3x2d.m10()).putDouble(int1 + 24, matrix3x2d.m11()).putDouble(int1 + 32, matrix3x2d.m20()).putDouble(int1 + 40, matrix3x2d.m21());
		}

		public void putf(Matrix3d matrix3d, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, (float)matrix3d.m00()).put(int1 + 1, (float)matrix3d.m01()).put(int1 + 2, (float)matrix3d.m02()).put(int1 + 3, (float)matrix3d.m10()).put(int1 + 4, (float)matrix3d.m11()).put(int1 + 5, (float)matrix3d.m12()).put(int1 + 6, (float)matrix3d.m20()).put(int1 + 7, (float)matrix3d.m21()).put(int1 + 8, (float)matrix3d.m22());
		}

		public void put(Matrix2f matrix2f, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, matrix2f.m00()).put(int1 + 1, matrix2f.m01()).put(int1 + 2, matrix2f.m10()).put(int1 + 3, matrix2f.m11());
		}

		public void put(Matrix2f matrix2f, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, matrix2f.m00()).putFloat(int1 + 4, matrix2f.m01()).putFloat(int1 + 8, matrix2f.m10()).putFloat(int1 + 12, matrix2f.m11());
		}

		public void put(Matrix2d matrix2d, int int1, DoubleBuffer doubleBuffer) {
			doubleBuffer.put(int1, matrix2d.m00()).put(int1 + 1, matrix2d.m01()).put(int1 + 2, matrix2d.m10()).put(int1 + 3, matrix2d.m11());
		}

		public void put(Matrix2d matrix2d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putDouble(int1, matrix2d.m00()).putDouble(int1 + 8, matrix2d.m01()).putDouble(int1 + 16, matrix2d.m10()).putDouble(int1 + 24, matrix2d.m11());
		}

		public void putf(Matrix2d matrix2d, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, (float)matrix2d.m00()).put(int1 + 1, (float)matrix2d.m01()).put(int1 + 2, (float)matrix2d.m10()).put(int1 + 3, (float)matrix2d.m11());
		}

		public void putf(Matrix2d matrix2d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, (float)matrix2d.m00()).putFloat(int1 + 4, (float)matrix2d.m01()).putFloat(int1 + 8, (float)matrix2d.m10()).putFloat(int1 + 12, (float)matrix2d.m11());
		}

		public void putf(Matrix3d matrix3d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, (float)matrix3d.m00()).putFloat(int1 + 4, (float)matrix3d.m01()).putFloat(int1 + 8, (float)matrix3d.m02()).putFloat(int1 + 12, (float)matrix3d.m10()).putFloat(int1 + 16, (float)matrix3d.m11()).putFloat(int1 + 20, (float)matrix3d.m12()).putFloat(int1 + 24, (float)matrix3d.m20()).putFloat(int1 + 28, (float)matrix3d.m21()).putFloat(int1 + 32, (float)matrix3d.m22());
		}

		public void put(Vector4d vector4d, int int1, DoubleBuffer doubleBuffer) {
			doubleBuffer.put(int1, vector4d.x).put(int1 + 1, vector4d.y).put(int1 + 2, vector4d.z).put(int1 + 3, vector4d.w);
		}

		public void put(Vector4d vector4d, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, (float)vector4d.x).put(int1 + 1, (float)vector4d.y).put(int1 + 2, (float)vector4d.z).put(int1 + 3, (float)vector4d.w);
		}

		public void put(Vector4d vector4d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putDouble(int1, vector4d.x).putDouble(int1 + 8, vector4d.y).putDouble(int1 + 16, vector4d.z).putDouble(int1 + 24, vector4d.w);
		}

		public void putf(Vector4d vector4d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, (float)vector4d.x).putFloat(int1 + 4, (float)vector4d.y).putFloat(int1 + 8, (float)vector4d.z).putFloat(int1 + 12, (float)vector4d.w);
		}

		public void put(Vector4f vector4f, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, vector4f.x).put(int1 + 1, vector4f.y).put(int1 + 2, vector4f.z).put(int1 + 3, vector4f.w);
		}

		public void put(Vector4f vector4f, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, vector4f.x).putFloat(int1 + 4, vector4f.y).putFloat(int1 + 8, vector4f.z).putFloat(int1 + 12, vector4f.w);
		}

		public void put(Vector4i vector4i, int int1, IntBuffer intBuffer) {
			intBuffer.put(int1, vector4i.x).put(int1 + 1, vector4i.y).put(int1 + 2, vector4i.z).put(int1 + 3, vector4i.w);
		}

		public void put(Vector4i vector4i, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putInt(int1, vector4i.x).putInt(int1 + 4, vector4i.y).putInt(int1 + 8, vector4i.z).putInt(int1 + 12, vector4i.w);
		}

		public void put(Vector3f vector3f, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, vector3f.x).put(int1 + 1, vector3f.y).put(int1 + 2, vector3f.z);
		}

		public void put(Vector3f vector3f, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, vector3f.x).putFloat(int1 + 4, vector3f.y).putFloat(int1 + 8, vector3f.z);
		}

		public void put(Vector3d vector3d, int int1, DoubleBuffer doubleBuffer) {
			doubleBuffer.put(int1, vector3d.x).put(int1 + 1, vector3d.y).put(int1 + 2, vector3d.z);
		}

		public void put(Vector3d vector3d, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, (float)vector3d.x).put(int1 + 1, (float)vector3d.y).put(int1 + 2, (float)vector3d.z);
		}

		public void put(Vector3d vector3d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putDouble(int1, vector3d.x).putDouble(int1 + 8, vector3d.y).putDouble(int1 + 16, vector3d.z);
		}

		public void putf(Vector3d vector3d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, (float)vector3d.x).putFloat(int1 + 4, (float)vector3d.y).putFloat(int1 + 8, (float)vector3d.z);
		}

		public void put(Vector3i vector3i, int int1, IntBuffer intBuffer) {
			intBuffer.put(int1, vector3i.x).put(int1 + 1, vector3i.y).put(int1 + 2, vector3i.z);
		}

		public void put(Vector3i vector3i, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putInt(int1, vector3i.x).putInt(int1 + 4, vector3i.y).putInt(int1 + 8, vector3i.z);
		}

		public void put(Vector2f vector2f, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, vector2f.x).put(int1 + 1, vector2f.y);
		}

		public void put(Vector2f vector2f, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, vector2f.x).putFloat(int1 + 4, vector2f.y);
		}

		public void put(Vector2d vector2d, int int1, DoubleBuffer doubleBuffer) {
			doubleBuffer.put(int1, vector2d.x).put(int1 + 1, vector2d.y);
		}

		public void put(Vector2d vector2d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putDouble(int1, vector2d.x).putDouble(int1 + 8, vector2d.y);
		}

		public void put(Vector2i vector2i, int int1, IntBuffer intBuffer) {
			intBuffer.put(int1, vector2i.x).put(int1 + 1, vector2i.y);
		}

		public void put(Vector2i vector2i, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putInt(int1, vector2i.x).putInt(int1 + 4, vector2i.y);
		}

		public void get(Matrix4f matrix4f, int int1, FloatBuffer floatBuffer) {
			matrix4f._m00(floatBuffer.get(int1))._m01(floatBuffer.get(int1 + 1))._m02(floatBuffer.get(int1 + 2))._m03(floatBuffer.get(int1 + 3))._m10(floatBuffer.get(int1 + 4))._m11(floatBuffer.get(int1 + 5))._m12(floatBuffer.get(int1 + 6))._m13(floatBuffer.get(int1 + 7))._m20(floatBuffer.get(int1 + 8))._m21(floatBuffer.get(int1 + 9))._m22(floatBuffer.get(int1 + 10))._m23(floatBuffer.get(int1 + 11))._m30(floatBuffer.get(int1 + 12))._m31(floatBuffer.get(int1 + 13))._m32(floatBuffer.get(int1 + 14))._m33(floatBuffer.get(int1 + 15));
		}

		public void get(Matrix4f matrix4f, int int1, ByteBuffer byteBuffer) {
			matrix4f._m00(byteBuffer.getFloat(int1))._m01(byteBuffer.getFloat(int1 + 4))._m02(byteBuffer.getFloat(int1 + 8))._m03(byteBuffer.getFloat(int1 + 12))._m10(byteBuffer.getFloat(int1 + 16))._m11(byteBuffer.getFloat(int1 + 20))._m12(byteBuffer.getFloat(int1 + 24))._m13(byteBuffer.getFloat(int1 + 28))._m20(byteBuffer.getFloat(int1 + 32))._m21(byteBuffer.getFloat(int1 + 36))._m22(byteBuffer.getFloat(int1 + 40))._m23(byteBuffer.getFloat(int1 + 44))._m30(byteBuffer.getFloat(int1 + 48))._m31(byteBuffer.getFloat(int1 + 52))._m32(byteBuffer.getFloat(int1 + 56))._m33(byteBuffer.getFloat(int1 + 60));
		}

		public void getTransposed(Matrix4f matrix4f, int int1, FloatBuffer floatBuffer) {
			matrix4f._m00(floatBuffer.get(int1))._m10(floatBuffer.get(int1 + 1))._m20(floatBuffer.get(int1 + 2))._m30(floatBuffer.get(int1 + 3))._m01(floatBuffer.get(int1 + 4))._m11(floatBuffer.get(int1 + 5))._m21(floatBuffer.get(int1 + 6))._m31(floatBuffer.get(int1 + 7))._m02(floatBuffer.get(int1 + 8))._m12(floatBuffer.get(int1 + 9))._m22(floatBuffer.get(int1 + 10))._m32(floatBuffer.get(int1 + 11))._m03(floatBuffer.get(int1 + 12))._m13(floatBuffer.get(int1 + 13))._m23(floatBuffer.get(int1 + 14))._m33(floatBuffer.get(int1 + 15));
		}

		public void getTransposed(Matrix4f matrix4f, int int1, ByteBuffer byteBuffer) {
			matrix4f._m00(byteBuffer.getFloat(int1))._m10(byteBuffer.getFloat(int1 + 4))._m20(byteBuffer.getFloat(int1 + 8))._m30(byteBuffer.getFloat(int1 + 12))._m01(byteBuffer.getFloat(int1 + 16))._m11(byteBuffer.getFloat(int1 + 20))._m21(byteBuffer.getFloat(int1 + 24))._m31(byteBuffer.getFloat(int1 + 28))._m02(byteBuffer.getFloat(int1 + 32))._m12(byteBuffer.getFloat(int1 + 36))._m22(byteBuffer.getFloat(int1 + 40))._m32(byteBuffer.getFloat(int1 + 44))._m03(byteBuffer.getFloat(int1 + 48))._m13(byteBuffer.getFloat(int1 + 52))._m23(byteBuffer.getFloat(int1 + 56))._m33(byteBuffer.getFloat(int1 + 60));
		}

		public void get(Matrix4x3f matrix4x3f, int int1, FloatBuffer floatBuffer) {
			matrix4x3f._m00(floatBuffer.get(int1))._m01(floatBuffer.get(int1 + 1))._m02(floatBuffer.get(int1 + 2))._m10(floatBuffer.get(int1 + 3))._m11(floatBuffer.get(int1 + 4))._m12(floatBuffer.get(int1 + 5))._m20(floatBuffer.get(int1 + 6))._m21(floatBuffer.get(int1 + 7))._m22(floatBuffer.get(int1 + 8))._m30(floatBuffer.get(int1 + 9))._m31(floatBuffer.get(int1 + 10))._m32(floatBuffer.get(int1 + 11));
		}

		public void get(Matrix4x3f matrix4x3f, int int1, ByteBuffer byteBuffer) {
			matrix4x3f._m00(byteBuffer.getFloat(int1))._m01(byteBuffer.getFloat(int1 + 4))._m02(byteBuffer.getFloat(int1 + 8))._m10(byteBuffer.getFloat(int1 + 12))._m11(byteBuffer.getFloat(int1 + 16))._m12(byteBuffer.getFloat(int1 + 20))._m20(byteBuffer.getFloat(int1 + 24))._m21(byteBuffer.getFloat(int1 + 28))._m22(byteBuffer.getFloat(int1 + 32))._m30(byteBuffer.getFloat(int1 + 36))._m31(byteBuffer.getFloat(int1 + 40))._m32(byteBuffer.getFloat(int1 + 44));
		}

		public void get(Matrix4d matrix4d, int int1, DoubleBuffer doubleBuffer) {
			matrix4d._m00(doubleBuffer.get(int1))._m01(doubleBuffer.get(int1 + 1))._m02(doubleBuffer.get(int1 + 2))._m03(doubleBuffer.get(int1 + 3))._m10(doubleBuffer.get(int1 + 4))._m11(doubleBuffer.get(int1 + 5))._m12(doubleBuffer.get(int1 + 6))._m13(doubleBuffer.get(int1 + 7))._m20(doubleBuffer.get(int1 + 8))._m21(doubleBuffer.get(int1 + 9))._m22(doubleBuffer.get(int1 + 10))._m23(doubleBuffer.get(int1 + 11))._m30(doubleBuffer.get(int1 + 12))._m31(doubleBuffer.get(int1 + 13))._m32(doubleBuffer.get(int1 + 14))._m33(doubleBuffer.get(int1 + 15));
		}

		public void get(Matrix4d matrix4d, int int1, ByteBuffer byteBuffer) {
			matrix4d._m00(byteBuffer.getDouble(int1))._m01(byteBuffer.getDouble(int1 + 8))._m02(byteBuffer.getDouble(int1 + 16))._m03(byteBuffer.getDouble(int1 + 24))._m10(byteBuffer.getDouble(int1 + 32))._m11(byteBuffer.getDouble(int1 + 40))._m12(byteBuffer.getDouble(int1 + 48))._m13(byteBuffer.getDouble(int1 + 56))._m20(byteBuffer.getDouble(int1 + 64))._m21(byteBuffer.getDouble(int1 + 72))._m22(byteBuffer.getDouble(int1 + 80))._m23(byteBuffer.getDouble(int1 + 88))._m30(byteBuffer.getDouble(int1 + 96))._m31(byteBuffer.getDouble(int1 + 104))._m32(byteBuffer.getDouble(int1 + 112))._m33(byteBuffer.getDouble(int1 + 120));
		}

		public void get(Matrix4x3d matrix4x3d, int int1, DoubleBuffer doubleBuffer) {
			matrix4x3d._m00(doubleBuffer.get(int1))._m01(doubleBuffer.get(int1 + 1))._m02(doubleBuffer.get(int1 + 2))._m10(doubleBuffer.get(int1 + 3))._m11(doubleBuffer.get(int1 + 4))._m12(doubleBuffer.get(int1 + 5))._m20(doubleBuffer.get(int1 + 6))._m21(doubleBuffer.get(int1 + 7))._m22(doubleBuffer.get(int1 + 8))._m30(doubleBuffer.get(int1 + 9))._m31(doubleBuffer.get(int1 + 10))._m32(doubleBuffer.get(int1 + 11));
		}

		public void get(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer) {
			matrix4x3d._m00(byteBuffer.getDouble(int1))._m01(byteBuffer.getDouble(int1 + 8))._m02(byteBuffer.getDouble(int1 + 16))._m10(byteBuffer.getDouble(int1 + 24))._m11(byteBuffer.getDouble(int1 + 32))._m12(byteBuffer.getDouble(int1 + 40))._m20(byteBuffer.getDouble(int1 + 48))._m21(byteBuffer.getDouble(int1 + 56))._m22(byteBuffer.getDouble(int1 + 64))._m30(byteBuffer.getDouble(int1 + 72))._m31(byteBuffer.getDouble(int1 + 80))._m32(byteBuffer.getDouble(int1 + 88));
		}

		public void getf(Matrix4d matrix4d, int int1, FloatBuffer floatBuffer) {
			matrix4d._m00((double)floatBuffer.get(int1))._m01((double)floatBuffer.get(int1 + 1))._m02((double)floatBuffer.get(int1 + 2))._m03((double)floatBuffer.get(int1 + 3))._m10((double)floatBuffer.get(int1 + 4))._m11((double)floatBuffer.get(int1 + 5))._m12((double)floatBuffer.get(int1 + 6))._m13((double)floatBuffer.get(int1 + 7))._m20((double)floatBuffer.get(int1 + 8))._m21((double)floatBuffer.get(int1 + 9))._m22((double)floatBuffer.get(int1 + 10))._m23((double)floatBuffer.get(int1 + 11))._m30((double)floatBuffer.get(int1 + 12))._m31((double)floatBuffer.get(int1 + 13))._m32((double)floatBuffer.get(int1 + 14))._m33((double)floatBuffer.get(int1 + 15));
		}

		public void getf(Matrix4d matrix4d, int int1, ByteBuffer byteBuffer) {
			matrix4d._m00((double)byteBuffer.getFloat(int1))._m01((double)byteBuffer.getFloat(int1 + 4))._m02((double)byteBuffer.getFloat(int1 + 8))._m03((double)byteBuffer.getFloat(int1 + 12))._m10((double)byteBuffer.getFloat(int1 + 16))._m11((double)byteBuffer.getFloat(int1 + 20))._m12((double)byteBuffer.getFloat(int1 + 24))._m13((double)byteBuffer.getFloat(int1 + 28))._m20((double)byteBuffer.getFloat(int1 + 32))._m21((double)byteBuffer.getFloat(int1 + 36))._m22((double)byteBuffer.getFloat(int1 + 40))._m23((double)byteBuffer.getFloat(int1 + 44))._m30((double)byteBuffer.getFloat(int1 + 48))._m31((double)byteBuffer.getFloat(int1 + 52))._m32((double)byteBuffer.getFloat(int1 + 56))._m33((double)byteBuffer.getFloat(int1 + 60));
		}

		public void getf(Matrix4x3d matrix4x3d, int int1, FloatBuffer floatBuffer) {
			matrix4x3d._m00((double)floatBuffer.get(int1))._m01((double)floatBuffer.get(int1 + 1))._m02((double)floatBuffer.get(int1 + 2))._m10((double)floatBuffer.get(int1 + 3))._m11((double)floatBuffer.get(int1 + 4))._m12((double)floatBuffer.get(int1 + 5))._m20((double)floatBuffer.get(int1 + 6))._m21((double)floatBuffer.get(int1 + 7))._m22((double)floatBuffer.get(int1 + 8))._m30((double)floatBuffer.get(int1 + 9))._m31((double)floatBuffer.get(int1 + 10))._m32((double)floatBuffer.get(int1 + 11));
		}

		public void getf(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer) {
			matrix4x3d._m00((double)byteBuffer.getFloat(int1))._m01((double)byteBuffer.getFloat(int1 + 4))._m02((double)byteBuffer.getFloat(int1 + 8))._m10((double)byteBuffer.getFloat(int1 + 12))._m11((double)byteBuffer.getFloat(int1 + 16))._m12((double)byteBuffer.getFloat(int1 + 20))._m20((double)byteBuffer.getFloat(int1 + 24))._m21((double)byteBuffer.getFloat(int1 + 28))._m22((double)byteBuffer.getFloat(int1 + 32))._m30((double)byteBuffer.getFloat(int1 + 36))._m31((double)byteBuffer.getFloat(int1 + 40))._m32((double)byteBuffer.getFloat(int1 + 44));
		}

		public void get(Matrix3f matrix3f, int int1, FloatBuffer floatBuffer) {
			matrix3f._m00(floatBuffer.get(int1))._m01(floatBuffer.get(int1 + 1))._m02(floatBuffer.get(int1 + 2))._m10(floatBuffer.get(int1 + 3))._m11(floatBuffer.get(int1 + 4))._m12(floatBuffer.get(int1 + 5))._m20(floatBuffer.get(int1 + 6))._m21(floatBuffer.get(int1 + 7))._m22(floatBuffer.get(int1 + 8));
		}

		public void get(Matrix3f matrix3f, int int1, ByteBuffer byteBuffer) {
			matrix3f._m00(byteBuffer.getFloat(int1))._m01(byteBuffer.getFloat(int1 + 4))._m02(byteBuffer.getFloat(int1 + 8))._m10(byteBuffer.getFloat(int1 + 12))._m11(byteBuffer.getFloat(int1 + 16))._m12(byteBuffer.getFloat(int1 + 20))._m20(byteBuffer.getFloat(int1 + 24))._m21(byteBuffer.getFloat(int1 + 28))._m22(byteBuffer.getFloat(int1 + 32));
		}

		public void get(Matrix3d matrix3d, int int1, DoubleBuffer doubleBuffer) {
			matrix3d._m00(doubleBuffer.get(int1))._m01(doubleBuffer.get(int1 + 1))._m02(doubleBuffer.get(int1 + 2))._m10(doubleBuffer.get(int1 + 3))._m11(doubleBuffer.get(int1 + 4))._m12(doubleBuffer.get(int1 + 5))._m20(doubleBuffer.get(int1 + 6))._m21(doubleBuffer.get(int1 + 7))._m22(doubleBuffer.get(int1 + 8));
		}

		public void get(Matrix3d matrix3d, int int1, ByteBuffer byteBuffer) {
			matrix3d._m00(byteBuffer.getDouble(int1))._m01(byteBuffer.getDouble(int1 + 8))._m02(byteBuffer.getDouble(int1 + 16))._m10(byteBuffer.getDouble(int1 + 24))._m11(byteBuffer.getDouble(int1 + 32))._m12(byteBuffer.getDouble(int1 + 40))._m20(byteBuffer.getDouble(int1 + 48))._m21(byteBuffer.getDouble(int1 + 56))._m22(byteBuffer.getDouble(int1 + 64));
		}

		public void get(Matrix3x2f matrix3x2f, int int1, FloatBuffer floatBuffer) {
			matrix3x2f._m00(floatBuffer.get(int1))._m01(floatBuffer.get(int1 + 1))._m10(floatBuffer.get(int1 + 2))._m11(floatBuffer.get(int1 + 3))._m20(floatBuffer.get(int1 + 4))._m21(floatBuffer.get(int1 + 5));
		}

		public void get(Matrix3x2f matrix3x2f, int int1, ByteBuffer byteBuffer) {
			matrix3x2f._m00(byteBuffer.getFloat(int1))._m01(byteBuffer.getFloat(int1 + 4))._m10(byteBuffer.getFloat(int1 + 8))._m11(byteBuffer.getFloat(int1 + 12))._m20(byteBuffer.getFloat(int1 + 16))._m21(byteBuffer.getFloat(int1 + 20));
		}

		public void get(Matrix3x2d matrix3x2d, int int1, DoubleBuffer doubleBuffer) {
			matrix3x2d._m00(doubleBuffer.get(int1))._m01(doubleBuffer.get(int1 + 1))._m10(doubleBuffer.get(int1 + 2))._m11(doubleBuffer.get(int1 + 3))._m20(doubleBuffer.get(int1 + 4))._m21(doubleBuffer.get(int1 + 5));
		}

		public void get(Matrix3x2d matrix3x2d, int int1, ByteBuffer byteBuffer) {
			matrix3x2d._m00(byteBuffer.getDouble(int1))._m01(byteBuffer.getDouble(int1 + 8))._m10(byteBuffer.getDouble(int1 + 16))._m11(byteBuffer.getDouble(int1 + 24))._m20(byteBuffer.getDouble(int1 + 32))._m21(byteBuffer.getDouble(int1 + 40));
		}

		public void getf(Matrix3d matrix3d, int int1, FloatBuffer floatBuffer) {
			matrix3d._m00((double)floatBuffer.get(int1))._m01((double)floatBuffer.get(int1 + 1))._m02((double)floatBuffer.get(int1 + 2))._m10((double)floatBuffer.get(int1 + 3))._m11((double)floatBuffer.get(int1 + 4))._m12((double)floatBuffer.get(int1 + 5))._m20((double)floatBuffer.get(int1 + 6))._m21((double)floatBuffer.get(int1 + 7))._m22((double)floatBuffer.get(int1 + 8));
		}

		public void getf(Matrix3d matrix3d, int int1, ByteBuffer byteBuffer) {
			matrix3d._m00((double)byteBuffer.getFloat(int1))._m01((double)byteBuffer.getFloat(int1 + 4))._m02((double)byteBuffer.getFloat(int1 + 8))._m10((double)byteBuffer.getFloat(int1 + 12))._m11((double)byteBuffer.getFloat(int1 + 16))._m12((double)byteBuffer.getFloat(int1 + 20))._m20((double)byteBuffer.getFloat(int1 + 24))._m21((double)byteBuffer.getFloat(int1 + 28))._m22((double)byteBuffer.getFloat(int1 + 32));
		}

		public void get(Matrix2f matrix2f, int int1, FloatBuffer floatBuffer) {
			matrix2f._m00(floatBuffer.get(int1))._m01(floatBuffer.get(int1 + 1))._m10(floatBuffer.get(int1 + 2))._m11(floatBuffer.get(int1 + 3));
		}

		public void get(Matrix2f matrix2f, int int1, ByteBuffer byteBuffer) {
			matrix2f._m00(byteBuffer.getFloat(int1))._m01(byteBuffer.getFloat(int1 + 4))._m10(byteBuffer.getFloat(int1 + 8))._m11(byteBuffer.getFloat(int1 + 12));
		}

		public void get(Matrix2d matrix2d, int int1, DoubleBuffer doubleBuffer) {
			matrix2d._m00(doubleBuffer.get(int1))._m01(doubleBuffer.get(int1 + 1))._m10(doubleBuffer.get(int1 + 2))._m11(doubleBuffer.get(int1 + 3));
		}

		public void get(Matrix2d matrix2d, int int1, ByteBuffer byteBuffer) {
			matrix2d._m00(byteBuffer.getDouble(int1))._m01(byteBuffer.getDouble(int1 + 8))._m10(byteBuffer.getDouble(int1 + 16))._m11(byteBuffer.getDouble(int1 + 24));
		}

		public void getf(Matrix2d matrix2d, int int1, FloatBuffer floatBuffer) {
			matrix2d._m00((double)floatBuffer.get(int1))._m01((double)floatBuffer.get(int1 + 1))._m10((double)floatBuffer.get(int1 + 2))._m11((double)floatBuffer.get(int1 + 3));
		}

		public void getf(Matrix2d matrix2d, int int1, ByteBuffer byteBuffer) {
			matrix2d._m00((double)byteBuffer.getFloat(int1))._m01((double)byteBuffer.getFloat(int1 + 4))._m10((double)byteBuffer.getFloat(int1 + 8))._m11((double)byteBuffer.getFloat(int1 + 12));
		}

		public void get(Vector4d vector4d, int int1, DoubleBuffer doubleBuffer) {
			vector4d.x = doubleBuffer.get(int1);
			vector4d.y = doubleBuffer.get(int1 + 1);
			vector4d.z = doubleBuffer.get(int1 + 2);
			vector4d.w = doubleBuffer.get(int1 + 3);
		}

		public void get(Vector4d vector4d, int int1, ByteBuffer byteBuffer) {
			vector4d.x = byteBuffer.getDouble(int1);
			vector4d.y = byteBuffer.getDouble(int1 + 8);
			vector4d.z = byteBuffer.getDouble(int1 + 16);
			vector4d.w = byteBuffer.getDouble(int1 + 24);
		}

		public void get(Vector4f vector4f, int int1, FloatBuffer floatBuffer) {
			vector4f.x = floatBuffer.get(int1);
			vector4f.y = floatBuffer.get(int1 + 1);
			vector4f.z = floatBuffer.get(int1 + 2);
			vector4f.w = floatBuffer.get(int1 + 3);
		}

		public void get(Vector4f vector4f, int int1, ByteBuffer byteBuffer) {
			vector4f.x = byteBuffer.getFloat(int1);
			vector4f.y = byteBuffer.getFloat(int1 + 4);
			vector4f.z = byteBuffer.getFloat(int1 + 8);
			vector4f.w = byteBuffer.getFloat(int1 + 12);
		}

		public void get(Vector4i vector4i, int int1, IntBuffer intBuffer) {
			vector4i.x = intBuffer.get(int1);
			vector4i.y = intBuffer.get(int1 + 1);
			vector4i.z = intBuffer.get(int1 + 2);
			vector4i.w = intBuffer.get(int1 + 3);
		}

		public void get(Vector4i vector4i, int int1, ByteBuffer byteBuffer) {
			vector4i.x = byteBuffer.getInt(int1);
			vector4i.y = byteBuffer.getInt(int1 + 4);
			vector4i.z = byteBuffer.getInt(int1 + 8);
			vector4i.w = byteBuffer.getInt(int1 + 12);
		}

		public void get(Vector3f vector3f, int int1, FloatBuffer floatBuffer) {
			vector3f.x = floatBuffer.get(int1);
			vector3f.y = floatBuffer.get(int1 + 1);
			vector3f.z = floatBuffer.get(int1 + 2);
		}

		public void get(Vector3f vector3f, int int1, ByteBuffer byteBuffer) {
			vector3f.x = byteBuffer.getFloat(int1);
			vector3f.y = byteBuffer.getFloat(int1 + 4);
			vector3f.z = byteBuffer.getFloat(int1 + 8);
		}

		public void get(Vector3d vector3d, int int1, DoubleBuffer doubleBuffer) {
			vector3d.x = doubleBuffer.get(int1);
			vector3d.y = doubleBuffer.get(int1 + 1);
			vector3d.z = doubleBuffer.get(int1 + 2);
		}

		public void get(Vector3d vector3d, int int1, ByteBuffer byteBuffer) {
			vector3d.x = byteBuffer.getDouble(int1);
			vector3d.y = byteBuffer.getDouble(int1 + 8);
			vector3d.z = byteBuffer.getDouble(int1 + 16);
		}

		public void get(Vector3i vector3i, int int1, IntBuffer intBuffer) {
			vector3i.x = intBuffer.get(int1);
			vector3i.y = intBuffer.get(int1 + 1);
			vector3i.z = intBuffer.get(int1 + 2);
		}

		public void get(Vector3i vector3i, int int1, ByteBuffer byteBuffer) {
			vector3i.x = byteBuffer.getInt(int1);
			vector3i.y = byteBuffer.getInt(int1 + 4);
			vector3i.z = byteBuffer.getInt(int1 + 8);
		}

		public void get(Vector2f vector2f, int int1, FloatBuffer floatBuffer) {
			vector2f.x = floatBuffer.get(int1);
			vector2f.y = floatBuffer.get(int1 + 1);
		}

		public void get(Vector2f vector2f, int int1, ByteBuffer byteBuffer) {
			vector2f.x = byteBuffer.getFloat(int1);
			vector2f.y = byteBuffer.getFloat(int1 + 4);
		}

		public void get(Vector2d vector2d, int int1, DoubleBuffer doubleBuffer) {
			vector2d.x = doubleBuffer.get(int1);
			vector2d.y = doubleBuffer.get(int1 + 1);
		}

		public void get(Vector2d vector2d, int int1, ByteBuffer byteBuffer) {
			vector2d.x = byteBuffer.getDouble(int1);
			vector2d.y = byteBuffer.getDouble(int1 + 8);
		}

		public void get(Vector2i vector2i, int int1, IntBuffer intBuffer) {
			vector2i.x = intBuffer.get(int1);
			vector2i.y = intBuffer.get(int1 + 1);
		}

		public void get(Vector2i vector2i, int int1, ByteBuffer byteBuffer) {
			vector2i.x = byteBuffer.getInt(int1);
			vector2i.y = byteBuffer.getInt(int1 + 4);
		}

		public float get(Matrix4f matrix4f, int int1, int int2) {
			switch (int1) {
			case 0: 
				switch (int2) {
				case 0: 
					return matrix4f.m00;
				
				case 1: 
					return matrix4f.m01;
				
				case 2: 
					return matrix4f.m02;
				
				case 3: 
					return matrix4f.m03;
				
				default: 
					throw new IllegalArgumentException();
				
				}

			
			case 1: 
				switch (int2) {
				case 0: 
					return matrix4f.m10;
				
				case 1: 
					return matrix4f.m11;
				
				case 2: 
					return matrix4f.m12;
				
				case 3: 
					return matrix4f.m13;
				
				default: 
					throw new IllegalArgumentException();
				
				}

			
			case 2: 
				switch (int2) {
				case 0: 
					return matrix4f.m20;
				
				case 1: 
					return matrix4f.m21;
				
				case 2: 
					return matrix4f.m22;
				
				case 3: 
					return matrix4f.m23;
				
				default: 
					throw new IllegalArgumentException();
				
				}

			
			case 3: 
				switch (int2) {
				case 0: 
					return matrix4f.m30;
				
				case 1: 
					return matrix4f.m31;
				
				case 2: 
					return matrix4f.m32;
				
				case 3: 
					return matrix4f.m33;
				
				}

			
			}
			throw new IllegalArgumentException();
		}

		public Matrix4f set(Matrix4f matrix4f, int int1, int int2, float float1) {
			switch (int1) {
			case 0: 
				switch (int2) {
				case 0: 
					return matrix4f.m00(float1);
				
				case 1: 
					return matrix4f.m01(float1);
				
				case 2: 
					return matrix4f.m02(float1);
				
				case 3: 
					return matrix4f.m03(float1);
				
				default: 
					throw new IllegalArgumentException();
				
				}

			
			case 1: 
				switch (int2) {
				case 0: 
					return matrix4f.m10(float1);
				
				case 1: 
					return matrix4f.m11(float1);
				
				case 2: 
					return matrix4f.m12(float1);
				
				case 3: 
					return matrix4f.m13(float1);
				
				default: 
					throw new IllegalArgumentException();
				
				}

			
			case 2: 
				switch (int2) {
				case 0: 
					return matrix4f.m20(float1);
				
				case 1: 
					return matrix4f.m21(float1);
				
				case 2: 
					return matrix4f.m22(float1);
				
				case 3: 
					return matrix4f.m23(float1);
				
				default: 
					throw new IllegalArgumentException();
				
				}

			
			case 3: 
				switch (int2) {
				case 0: 
					return matrix4f.m30(float1);
				
				case 1: 
					return matrix4f.m31(float1);
				
				case 2: 
					return matrix4f.m32(float1);
				
				case 3: 
					return matrix4f.m33(float1);
				
				}

			
			}
			throw new IllegalArgumentException();
		}

		public double get(Matrix4d matrix4d, int int1, int int2) {
			switch (int1) {
			case 0: 
				switch (int2) {
				case 0: 
					return matrix4d.m00;
				
				case 1: 
					return matrix4d.m01;
				
				case 2: 
					return matrix4d.m02;
				
				case 3: 
					return matrix4d.m03;
				
				default: 
					throw new IllegalArgumentException();
				
				}

			
			case 1: 
				switch (int2) {
				case 0: 
					return matrix4d.m10;
				
				case 1: 
					return matrix4d.m11;
				
				case 2: 
					return matrix4d.m12;
				
				case 3: 
					return matrix4d.m13;
				
				default: 
					throw new IllegalArgumentException();
				
				}

			
			case 2: 
				switch (int2) {
				case 0: 
					return matrix4d.m20;
				
				case 1: 
					return matrix4d.m21;
				
				case 2: 
					return matrix4d.m22;
				
				case 3: 
					return matrix4d.m23;
				
				default: 
					throw new IllegalArgumentException();
				
				}

			
			case 3: 
				switch (int2) {
				case 0: 
					return matrix4d.m30;
				
				case 1: 
					return matrix4d.m31;
				
				case 2: 
					return matrix4d.m32;
				
				case 3: 
					return matrix4d.m33;
				
				}

			
			}
			throw new IllegalArgumentException();
		}

		public Matrix4d set(Matrix4d matrix4d, int int1, int int2, double double1) {
			switch (int1) {
			case 0: 
				switch (int2) {
				case 0: 
					return matrix4d.m00(double1);
				
				case 1: 
					return matrix4d.m01(double1);
				
				case 2: 
					return matrix4d.m02(double1);
				
				case 3: 
					return matrix4d.m03(double1);
				
				default: 
					throw new IllegalArgumentException();
				
				}

			
			case 1: 
				switch (int2) {
				case 0: 
					return matrix4d.m10(double1);
				
				case 1: 
					return matrix4d.m11(double1);
				
				case 2: 
					return matrix4d.m12(double1);
				
				case 3: 
					return matrix4d.m13(double1);
				
				default: 
					throw new IllegalArgumentException();
				
				}

			
			case 2: 
				switch (int2) {
				case 0: 
					return matrix4d.m20(double1);
				
				case 1: 
					return matrix4d.m21(double1);
				
				case 2: 
					return matrix4d.m22(double1);
				
				case 3: 
					return matrix4d.m23(double1);
				
				default: 
					throw new IllegalArgumentException();
				
				}

			
			case 3: 
				switch (int2) {
				case 0: 
					return matrix4d.m30(double1);
				
				case 1: 
					return matrix4d.m31(double1);
				
				case 2: 
					return matrix4d.m32(double1);
				
				case 3: 
					return matrix4d.m33(double1);
				
				}

			
			}
			throw new IllegalArgumentException();
		}

		public float get(Matrix3f matrix3f, int int1, int int2) {
			switch (int1) {
			case 0: 
				switch (int2) {
				case 0: 
					return matrix3f.m00;
				
				case 1: 
					return matrix3f.m01;
				
				case 2: 
					return matrix3f.m02;
				
				default: 
					throw new IllegalArgumentException();
				
				}

			
			case 1: 
				switch (int2) {
				case 0: 
					return matrix3f.m10;
				
				case 1: 
					return matrix3f.m11;
				
				case 2: 
					return matrix3f.m12;
				
				default: 
					throw new IllegalArgumentException();
				
				}

			
			case 2: 
				switch (int2) {
				case 0: 
					return matrix3f.m20;
				
				case 1: 
					return matrix3f.m21;
				
				case 2: 
					return matrix3f.m22;
				
				}

			
			}
			throw new IllegalArgumentException();
		}

		public Matrix3f set(Matrix3f matrix3f, int int1, int int2, float float1) {
			switch (int1) {
			case 0: 
				switch (int2) {
				case 0: 
					return matrix3f.m00(float1);
				
				case 1: 
					return matrix3f.m01(float1);
				
				case 2: 
					return matrix3f.m02(float1);
				
				default: 
					throw new IllegalArgumentException();
				
				}

			
			case 1: 
				switch (int2) {
				case 0: 
					return matrix3f.m10(float1);
				
				case 1: 
					return matrix3f.m11(float1);
				
				case 2: 
					return matrix3f.m12(float1);
				
				default: 
					throw new IllegalArgumentException();
				
				}

			
			case 2: 
				switch (int2) {
				case 0: 
					return matrix3f.m20(float1);
				
				case 1: 
					return matrix3f.m21(float1);
				
				case 2: 
					return matrix3f.m22(float1);
				
				}

			
			}
			throw new IllegalArgumentException();
		}

		public double get(Matrix3d matrix3d, int int1, int int2) {
			switch (int1) {
			case 0: 
				switch (int2) {
				case 0: 
					return matrix3d.m00;
				
				case 1: 
					return matrix3d.m01;
				
				case 2: 
					return matrix3d.m02;
				
				default: 
					throw new IllegalArgumentException();
				
				}

			
			case 1: 
				switch (int2) {
				case 0: 
					return matrix3d.m10;
				
				case 1: 
					return matrix3d.m11;
				
				case 2: 
					return matrix3d.m12;
				
				default: 
					throw new IllegalArgumentException();
				
				}

			
			case 2: 
				switch (int2) {
				case 0: 
					return matrix3d.m20;
				
				case 1: 
					return matrix3d.m21;
				
				case 2: 
					return matrix3d.m22;
				
				}

			
			}
			throw new IllegalArgumentException();
		}

		public Matrix3d set(Matrix3d matrix3d, int int1, int int2, double double1) {
			switch (int1) {
			case 0: 
				switch (int2) {
				case 0: 
					return matrix3d.m00(double1);
				
				case 1: 
					return matrix3d.m01(double1);
				
				case 2: 
					return matrix3d.m02(double1);
				
				default: 
					throw new IllegalArgumentException();
				
				}

			
			case 1: 
				switch (int2) {
				case 0: 
					return matrix3d.m10(double1);
				
				case 1: 
					return matrix3d.m11(double1);
				
				case 2: 
					return matrix3d.m12(double1);
				
				default: 
					throw new IllegalArgumentException();
				
				}

			
			case 2: 
				switch (int2) {
				case 0: 
					return matrix3d.m20(double1);
				
				case 1: 
					return matrix3d.m21(double1);
				
				case 2: 
					return matrix3d.m22(double1);
				
				}

			
			}
			throw new IllegalArgumentException();
		}

		public Vector4f getColumn(Matrix4f matrix4f, int int1, Vector4f vector4f) {
			switch (int1) {
			case 0: 
				return vector4f.set(matrix4f.m00, matrix4f.m01, matrix4f.m02, matrix4f.m03);
			
			case 1: 
				return vector4f.set(matrix4f.m10, matrix4f.m11, matrix4f.m12, matrix4f.m13);
			
			case 2: 
				return vector4f.set(matrix4f.m20, matrix4f.m21, matrix4f.m22, matrix4f.m23);
			
			case 3: 
				return vector4f.set(matrix4f.m30, matrix4f.m31, matrix4f.m32, matrix4f.m33);
			
			default: 
				throw new IndexOutOfBoundsException();
			
			}
		}

		public Matrix4f setColumn(Vector4f vector4f, int int1, Matrix4f matrix4f) {
			switch (int1) {
			case 0: 
				return matrix4f._m00(vector4f.x)._m01(vector4f.y)._m02(vector4f.z)._m03(vector4f.w);
			
			case 1: 
				return matrix4f._m10(vector4f.x)._m11(vector4f.y)._m12(vector4f.z)._m13(vector4f.w);
			
			case 2: 
				return matrix4f._m20(vector4f.x)._m21(vector4f.y)._m22(vector4f.z)._m23(vector4f.w);
			
			case 3: 
				return matrix4f._m30(vector4f.x)._m31(vector4f.y)._m32(vector4f.z)._m33(vector4f.w);
			
			default: 
				throw new IndexOutOfBoundsException();
			
			}
		}

		public Matrix4f setColumn(Vector4fc vector4fc, int int1, Matrix4f matrix4f) {
			switch (int1) {
			case 0: 
				return matrix4f._m00(vector4fc.x())._m01(vector4fc.y())._m02(vector4fc.z())._m03(vector4fc.w());
			
			case 1: 
				return matrix4f._m10(vector4fc.x())._m11(vector4fc.y())._m12(vector4fc.z())._m13(vector4fc.w());
			
			case 2: 
				return matrix4f._m20(vector4fc.x())._m21(vector4fc.y())._m22(vector4fc.z())._m23(vector4fc.w());
			
			case 3: 
				return matrix4f._m30(vector4fc.x())._m31(vector4fc.y())._m32(vector4fc.z())._m33(vector4fc.w());
			
			default: 
				throw new IndexOutOfBoundsException();
			
			}
		}

		public void copy(Matrix4f matrix4f, Matrix4f matrix4f2) {
			matrix4f2._m00(matrix4f.m00())._m01(matrix4f.m01())._m02(matrix4f.m02())._m03(matrix4f.m03())._m10(matrix4f.m10())._m11(matrix4f.m11())._m12(matrix4f.m12())._m13(matrix4f.m13())._m20(matrix4f.m20())._m21(matrix4f.m21())._m22(matrix4f.m22())._m23(matrix4f.m23())._m30(matrix4f.m30())._m31(matrix4f.m31())._m32(matrix4f.m32())._m33(matrix4f.m33());
		}

		public void copy(Matrix3f matrix3f, Matrix4f matrix4f) {
			matrix4f._m00(matrix3f.m00())._m01(matrix3f.m01())._m02(matrix3f.m02())._m03(0.0F)._m10(matrix3f.m10())._m11(matrix3f.m11())._m12(matrix3f.m12())._m13(0.0F)._m20(matrix3f.m20())._m21(matrix3f.m21())._m22(matrix3f.m22())._m23(0.0F)._m30(0.0F)._m31(0.0F)._m32(0.0F)._m33(1.0F);
		}

		public void copy(Matrix4f matrix4f, Matrix3f matrix3f) {
			matrix3f._m00(matrix4f.m00())._m01(matrix4f.m01())._m02(matrix4f.m02())._m10(matrix4f.m10())._m11(matrix4f.m11())._m12(matrix4f.m12())._m20(matrix4f.m20())._m21(matrix4f.m21())._m22(matrix4f.m22());
		}

		public void copy(Matrix3f matrix3f, Matrix4x3f matrix4x3f) {
			matrix4x3f._m00(matrix3f.m00())._m01(matrix3f.m01())._m02(matrix3f.m02())._m10(matrix3f.m10())._m11(matrix3f.m11())._m12(matrix3f.m12())._m20(matrix3f.m20())._m21(matrix3f.m21())._m22(matrix3f.m22())._m30(0.0F)._m31(0.0F)._m32(0.0F);
		}

		public void copy(Matrix3x2f matrix3x2f, Matrix3x2f matrix3x2f2) {
			matrix3x2f2._m00(matrix3x2f.m00())._m01(matrix3x2f.m01())._m10(matrix3x2f.m10())._m11(matrix3x2f.m11())._m20(matrix3x2f.m20())._m21(matrix3x2f.m21());
		}

		public void copy(Matrix3x2d matrix3x2d, Matrix3x2d matrix3x2d2) {
			matrix3x2d2._m00(matrix3x2d.m00())._m01(matrix3x2d.m01())._m10(matrix3x2d.m10())._m11(matrix3x2d.m11())._m20(matrix3x2d.m20())._m21(matrix3x2d.m21());
		}

		public void copy(Matrix2f matrix2f, Matrix2f matrix2f2) {
			matrix2f2._m00(matrix2f.m00())._m01(matrix2f.m01())._m10(matrix2f.m10())._m11(matrix2f.m11());
		}

		public void copy(Matrix2d matrix2d, Matrix2d matrix2d2) {
			matrix2d2._m00(matrix2d.m00())._m01(matrix2d.m01())._m10(matrix2d.m10())._m11(matrix2d.m11());
		}

		public void copy(Matrix2f matrix2f, Matrix3f matrix3f) {
			matrix3f._m00(matrix2f.m00())._m01(matrix2f.m01())._m02(0.0F)._m10(matrix2f.m10())._m11(matrix2f.m11())._m12(0.0F)._m20(0.0F)._m21(0.0F)._m22(1.0F);
		}

		public void copy(Matrix3f matrix3f, Matrix2f matrix2f) {
			matrix2f._m00(matrix3f.m00())._m01(matrix3f.m01())._m10(matrix3f.m10())._m11(matrix3f.m11());
		}

		public void copy(Matrix2f matrix2f, Matrix3x2f matrix3x2f) {
			matrix3x2f._m00(matrix2f.m00())._m01(matrix2f.m01())._m10(matrix2f.m10())._m11(matrix2f.m11())._m20(0.0F)._m21(0.0F);
		}

		public void copy(Matrix3x2f matrix3x2f, Matrix2f matrix2f) {
			matrix2f._m00(matrix3x2f.m00())._m01(matrix3x2f.m01())._m10(matrix3x2f.m10())._m11(matrix3x2f.m11());
		}

		public void copy(Matrix2d matrix2d, Matrix3d matrix3d) {
			matrix3d._m00(matrix2d.m00())._m01(matrix2d.m01())._m02(0.0)._m10(matrix2d.m10())._m11(matrix2d.m11())._m12(0.0)._m20(0.0)._m21(0.0)._m22(1.0);
		}

		public void copy(Matrix3d matrix3d, Matrix2d matrix2d) {
			matrix2d._m00(matrix3d.m00())._m01(matrix3d.m01())._m10(matrix3d.m10())._m11(matrix3d.m11());
		}

		public void copy(Matrix2d matrix2d, Matrix3x2d matrix3x2d) {
			matrix3x2d._m00(matrix2d.m00())._m01(matrix2d.m01())._m10(matrix2d.m10())._m11(matrix2d.m11())._m20(0.0)._m21(0.0);
		}

		public void copy(Matrix3x2d matrix3x2d, Matrix2d matrix2d) {
			matrix2d._m00(matrix3x2d.m00())._m01(matrix3x2d.m01())._m10(matrix3x2d.m10())._m11(matrix3x2d.m11());
		}

		public void copy3x3(Matrix4f matrix4f, Matrix4f matrix4f2) {
			matrix4f2._m00(matrix4f.m00())._m01(matrix4f.m01())._m02(matrix4f.m02())._m10(matrix4f.m10())._m11(matrix4f.m11())._m12(matrix4f.m12())._m20(matrix4f.m20())._m21(matrix4f.m21())._m22(matrix4f.m22());
		}

		public void copy3x3(Matrix4x3f matrix4x3f, Matrix4x3f matrix4x3f2) {
			matrix4x3f2._m00(matrix4x3f.m00())._m01(matrix4x3f.m01())._m02(matrix4x3f.m02())._m10(matrix4x3f.m10())._m11(matrix4x3f.m11())._m12(matrix4x3f.m12())._m20(matrix4x3f.m20())._m21(matrix4x3f.m21())._m22(matrix4x3f.m22());
		}

		public void copy3x3(Matrix3f matrix3f, Matrix4x3f matrix4x3f) {
			matrix4x3f._m00(matrix3f.m00())._m01(matrix3f.m01())._m02(matrix3f.m02())._m10(matrix3f.m10())._m11(matrix3f.m11())._m12(matrix3f.m12())._m20(matrix3f.m20())._m21(matrix3f.m21())._m22(matrix3f.m22());
		}

		public void copy3x3(Matrix3f matrix3f, Matrix4f matrix4f) {
			matrix4f._m00(matrix3f.m00())._m01(matrix3f.m01())._m02(matrix3f.m02())._m10(matrix3f.m10())._m11(matrix3f.m11())._m12(matrix3f.m12())._m20(matrix3f.m20())._m21(matrix3f.m21())._m22(matrix3f.m22());
		}

		public void copy4x3(Matrix4x3f matrix4x3f, Matrix4f matrix4f) {
			matrix4f._m00(matrix4x3f.m00())._m01(matrix4x3f.m01())._m02(matrix4x3f.m02())._m10(matrix4x3f.m10())._m11(matrix4x3f.m11())._m12(matrix4x3f.m12())._m20(matrix4x3f.m20())._m21(matrix4x3f.m21())._m22(matrix4x3f.m22())._m30(matrix4x3f.m30())._m31(matrix4x3f.m31())._m32(matrix4x3f.m32());
		}

		public void copy4x3(Matrix4f matrix4f, Matrix4f matrix4f2) {
			matrix4f2._m00(matrix4f.m00())._m01(matrix4f.m01())._m02(matrix4f.m02())._m10(matrix4f.m10())._m11(matrix4f.m11())._m12(matrix4f.m12())._m20(matrix4f.m20())._m21(matrix4f.m21())._m22(matrix4f.m22())._m30(matrix4f.m30())._m31(matrix4f.m31())._m32(matrix4f.m32());
		}

		public void copy(Matrix4f matrix4f, Matrix4x3f matrix4x3f) {
			matrix4x3f._m00(matrix4f.m00())._m01(matrix4f.m01())._m02(matrix4f.m02())._m10(matrix4f.m10())._m11(matrix4f.m11())._m12(matrix4f.m12())._m20(matrix4f.m20())._m21(matrix4f.m21())._m22(matrix4f.m22())._m30(matrix4f.m30())._m31(matrix4f.m31())._m32(matrix4f.m32());
		}

		public void copy(Matrix4x3f matrix4x3f, Matrix4f matrix4f) {
			matrix4f._m00(matrix4x3f.m00())._m01(matrix4x3f.m01())._m02(matrix4x3f.m02())._m03(0.0F)._m10(matrix4x3f.m10())._m11(matrix4x3f.m11())._m12(matrix4x3f.m12())._m13(0.0F)._m20(matrix4x3f.m20())._m21(matrix4x3f.m21())._m22(matrix4x3f.m22())._m23(0.0F)._m30(matrix4x3f.m30())._m31(matrix4x3f.m31())._m32(matrix4x3f.m32())._m33(1.0F);
		}

		public void copy(Matrix4x3f matrix4x3f, Matrix4x3f matrix4x3f2) {
			matrix4x3f2._m00(matrix4x3f.m00())._m01(matrix4x3f.m01())._m02(matrix4x3f.m02())._m10(matrix4x3f.m10())._m11(matrix4x3f.m11())._m12(matrix4x3f.m12())._m20(matrix4x3f.m20())._m21(matrix4x3f.m21())._m22(matrix4x3f.m22())._m30(matrix4x3f.m30())._m31(matrix4x3f.m31())._m32(matrix4x3f.m32());
		}

		public void copy(Matrix3f matrix3f, Matrix3f matrix3f2) {
			matrix3f2._m00(matrix3f.m00())._m01(matrix3f.m01())._m02(matrix3f.m02())._m10(matrix3f.m10())._m11(matrix3f.m11())._m12(matrix3f.m12())._m20(matrix3f.m20())._m21(matrix3f.m21())._m22(matrix3f.m22());
		}

		public void copy(float[] floatArray, int int1, Matrix4f matrix4f) {
			matrix4f._m00(floatArray[int1 + 0])._m01(floatArray[int1 + 1])._m02(floatArray[int1 + 2])._m03(floatArray[int1 + 3])._m10(floatArray[int1 + 4])._m11(floatArray[int1 + 5])._m12(floatArray[int1 + 6])._m13(floatArray[int1 + 7])._m20(floatArray[int1 + 8])._m21(floatArray[int1 + 9])._m22(floatArray[int1 + 10])._m23(floatArray[int1 + 11])._m30(floatArray[int1 + 12])._m31(floatArray[int1 + 13])._m32(floatArray[int1 + 14])._m33(floatArray[int1 + 15]);
		}

		public void copyTransposed(float[] floatArray, int int1, Matrix4f matrix4f) {
			matrix4f._m00(floatArray[int1 + 0])._m10(floatArray[int1 + 1])._m20(floatArray[int1 + 2])._m30(floatArray[int1 + 3])._m01(floatArray[int1 + 4])._m11(floatArray[int1 + 5])._m21(floatArray[int1 + 6])._m31(floatArray[int1 + 7])._m02(floatArray[int1 + 8])._m12(floatArray[int1 + 9])._m22(floatArray[int1 + 10])._m32(floatArray[int1 + 11])._m03(floatArray[int1 + 12])._m13(floatArray[int1 + 13])._m23(floatArray[int1 + 14])._m33(floatArray[int1 + 15]);
		}

		public void copy(float[] floatArray, int int1, Matrix3f matrix3f) {
			matrix3f._m00(floatArray[int1 + 0])._m01(floatArray[int1 + 1])._m02(floatArray[int1 + 2])._m10(floatArray[int1 + 3])._m11(floatArray[int1 + 4])._m12(floatArray[int1 + 5])._m20(floatArray[int1 + 6])._m21(floatArray[int1 + 7])._m22(floatArray[int1 + 8]);
		}

		public void copy(float[] floatArray, int int1, Matrix4x3f matrix4x3f) {
			matrix4x3f._m00(floatArray[int1 + 0])._m01(floatArray[int1 + 1])._m02(floatArray[int1 + 2])._m10(floatArray[int1 + 3])._m11(floatArray[int1 + 4])._m12(floatArray[int1 + 5])._m20(floatArray[int1 + 6])._m21(floatArray[int1 + 7])._m22(floatArray[int1 + 8])._m30(floatArray[int1 + 9])._m31(floatArray[int1 + 10])._m32(floatArray[int1 + 11]);
		}

		public void copy(float[] floatArray, int int1, Matrix3x2f matrix3x2f) {
			matrix3x2f._m00(floatArray[int1 + 0])._m01(floatArray[int1 + 1])._m10(floatArray[int1 + 2])._m11(floatArray[int1 + 3])._m20(floatArray[int1 + 4])._m21(floatArray[int1 + 5]);
		}

		public void copy(double[] doubleArray, int int1, Matrix3x2d matrix3x2d) {
			matrix3x2d._m00(doubleArray[int1 + 0])._m01(doubleArray[int1 + 1])._m10(doubleArray[int1 + 2])._m11(doubleArray[int1 + 3])._m20(doubleArray[int1 + 4])._m21(doubleArray[int1 + 5]);
		}

		public void copy(float[] floatArray, int int1, Matrix2f matrix2f) {
			matrix2f._m00(floatArray[int1 + 0])._m01(floatArray[int1 + 1])._m10(floatArray[int1 + 2])._m11(floatArray[int1 + 3]);
		}

		public void copy(double[] doubleArray, int int1, Matrix2d matrix2d) {
			matrix2d._m00(doubleArray[int1 + 0])._m01(doubleArray[int1 + 1])._m10(doubleArray[int1 + 2])._m11(doubleArray[int1 + 3]);
		}

		public void copy(Matrix4f matrix4f, float[] floatArray, int int1) {
			floatArray[int1 + 0] = matrix4f.m00();
			floatArray[int1 + 1] = matrix4f.m01();
			floatArray[int1 + 2] = matrix4f.m02();
			floatArray[int1 + 3] = matrix4f.m03();
			floatArray[int1 + 4] = matrix4f.m10();
			floatArray[int1 + 5] = matrix4f.m11();
			floatArray[int1 + 6] = matrix4f.m12();
			floatArray[int1 + 7] = matrix4f.m13();
			floatArray[int1 + 8] = matrix4f.m20();
			floatArray[int1 + 9] = matrix4f.m21();
			floatArray[int1 + 10] = matrix4f.m22();
			floatArray[int1 + 11] = matrix4f.m23();
			floatArray[int1 + 12] = matrix4f.m30();
			floatArray[int1 + 13] = matrix4f.m31();
			floatArray[int1 + 14] = matrix4f.m32();
			floatArray[int1 + 15] = matrix4f.m33();
		}

		public void copy(Matrix3f matrix3f, float[] floatArray, int int1) {
			floatArray[int1 + 0] = matrix3f.m00();
			floatArray[int1 + 1] = matrix3f.m01();
			floatArray[int1 + 2] = matrix3f.m02();
			floatArray[int1 + 3] = matrix3f.m10();
			floatArray[int1 + 4] = matrix3f.m11();
			floatArray[int1 + 5] = matrix3f.m12();
			floatArray[int1 + 6] = matrix3f.m20();
			floatArray[int1 + 7] = matrix3f.m21();
			floatArray[int1 + 8] = matrix3f.m22();
		}

		public void copy(Matrix4x3f matrix4x3f, float[] floatArray, int int1) {
			floatArray[int1 + 0] = matrix4x3f.m00();
			floatArray[int1 + 1] = matrix4x3f.m01();
			floatArray[int1 + 2] = matrix4x3f.m02();
			floatArray[int1 + 3] = matrix4x3f.m10();
			floatArray[int1 + 4] = matrix4x3f.m11();
			floatArray[int1 + 5] = matrix4x3f.m12();
			floatArray[int1 + 6] = matrix4x3f.m20();
			floatArray[int1 + 7] = matrix4x3f.m21();
			floatArray[int1 + 8] = matrix4x3f.m22();
			floatArray[int1 + 9] = matrix4x3f.m30();
			floatArray[int1 + 10] = matrix4x3f.m31();
			floatArray[int1 + 11] = matrix4x3f.m32();
		}

		public void copy(Matrix3x2f matrix3x2f, float[] floatArray, int int1) {
			floatArray[int1 + 0] = matrix3x2f.m00();
			floatArray[int1 + 1] = matrix3x2f.m01();
			floatArray[int1 + 2] = matrix3x2f.m10();
			floatArray[int1 + 3] = matrix3x2f.m11();
			floatArray[int1 + 4] = matrix3x2f.m20();
			floatArray[int1 + 5] = matrix3x2f.m21();
		}

		public void copy(Matrix3x2d matrix3x2d, double[] doubleArray, int int1) {
			doubleArray[int1 + 0] = matrix3x2d.m00();
			doubleArray[int1 + 1] = matrix3x2d.m01();
			doubleArray[int1 + 2] = matrix3x2d.m10();
			doubleArray[int1 + 3] = matrix3x2d.m11();
			doubleArray[int1 + 4] = matrix3x2d.m20();
			doubleArray[int1 + 5] = matrix3x2d.m21();
		}

		public void copy(Matrix2f matrix2f, float[] floatArray, int int1) {
			floatArray[int1 + 0] = matrix2f.m00();
			floatArray[int1 + 1] = matrix2f.m01();
			floatArray[int1 + 2] = matrix2f.m10();
			floatArray[int1 + 3] = matrix2f.m11();
		}

		public void copy(Matrix2d matrix2d, double[] doubleArray, int int1) {
			doubleArray[int1 + 0] = matrix2d.m00();
			doubleArray[int1 + 1] = matrix2d.m01();
			doubleArray[int1 + 2] = matrix2d.m10();
			doubleArray[int1 + 3] = matrix2d.m11();
		}

		public void copy4x4(Matrix4x3f matrix4x3f, float[] floatArray, int int1) {
			floatArray[int1 + 0] = matrix4x3f.m00();
			floatArray[int1 + 1] = matrix4x3f.m01();
			floatArray[int1 + 2] = matrix4x3f.m02();
			floatArray[int1 + 3] = 0.0F;
			floatArray[int1 + 4] = matrix4x3f.m10();
			floatArray[int1 + 5] = matrix4x3f.m11();
			floatArray[int1 + 6] = matrix4x3f.m12();
			floatArray[int1 + 7] = 0.0F;
			floatArray[int1 + 8] = matrix4x3f.m20();
			floatArray[int1 + 9] = matrix4x3f.m21();
			floatArray[int1 + 10] = matrix4x3f.m22();
			floatArray[int1 + 11] = 0.0F;
			floatArray[int1 + 12] = matrix4x3f.m30();
			floatArray[int1 + 13] = matrix4x3f.m31();
			floatArray[int1 + 14] = matrix4x3f.m32();
			floatArray[int1 + 15] = 1.0F;
		}

		public void copy4x4(Matrix4x3d matrix4x3d, float[] floatArray, int int1) {
			floatArray[int1 + 0] = (float)matrix4x3d.m00();
			floatArray[int1 + 1] = (float)matrix4x3d.m01();
			floatArray[int1 + 2] = (float)matrix4x3d.m02();
			floatArray[int1 + 3] = 0.0F;
			floatArray[int1 + 4] = (float)matrix4x3d.m10();
			floatArray[int1 + 5] = (float)matrix4x3d.m11();
			floatArray[int1 + 6] = (float)matrix4x3d.m12();
			floatArray[int1 + 7] = 0.0F;
			floatArray[int1 + 8] = (float)matrix4x3d.m20();
			floatArray[int1 + 9] = (float)matrix4x3d.m21();
			floatArray[int1 + 10] = (float)matrix4x3d.m22();
			floatArray[int1 + 11] = 0.0F;
			floatArray[int1 + 12] = (float)matrix4x3d.m30();
			floatArray[int1 + 13] = (float)matrix4x3d.m31();
			floatArray[int1 + 14] = (float)matrix4x3d.m32();
			floatArray[int1 + 15] = 1.0F;
		}

		public void copy4x4(Matrix4x3d matrix4x3d, double[] doubleArray, int int1) {
			doubleArray[int1 + 0] = matrix4x3d.m00();
			doubleArray[int1 + 1] = matrix4x3d.m01();
			doubleArray[int1 + 2] = matrix4x3d.m02();
			doubleArray[int1 + 3] = 0.0;
			doubleArray[int1 + 4] = matrix4x3d.m10();
			doubleArray[int1 + 5] = matrix4x3d.m11();
			doubleArray[int1 + 6] = matrix4x3d.m12();
			doubleArray[int1 + 7] = 0.0;
			doubleArray[int1 + 8] = matrix4x3d.m20();
			doubleArray[int1 + 9] = matrix4x3d.m21();
			doubleArray[int1 + 10] = matrix4x3d.m22();
			doubleArray[int1 + 11] = 0.0;
			doubleArray[int1 + 12] = matrix4x3d.m30();
			doubleArray[int1 + 13] = matrix4x3d.m31();
			doubleArray[int1 + 14] = matrix4x3d.m32();
			doubleArray[int1 + 15] = 1.0;
		}

		public void copy3x3(Matrix3x2f matrix3x2f, float[] floatArray, int int1) {
			floatArray[int1 + 0] = matrix3x2f.m00();
			floatArray[int1 + 1] = matrix3x2f.m01();
			floatArray[int1 + 2] = 0.0F;
			floatArray[int1 + 3] = matrix3x2f.m10();
			floatArray[int1 + 4] = matrix3x2f.m11();
			floatArray[int1 + 5] = 0.0F;
			floatArray[int1 + 6] = matrix3x2f.m20();
			floatArray[int1 + 7] = matrix3x2f.m21();
			floatArray[int1 + 8] = 1.0F;
		}

		public void copy3x3(Matrix3x2d matrix3x2d, double[] doubleArray, int int1) {
			doubleArray[int1 + 0] = matrix3x2d.m00();
			doubleArray[int1 + 1] = matrix3x2d.m01();
			doubleArray[int1 + 2] = 0.0;
			doubleArray[int1 + 3] = matrix3x2d.m10();
			doubleArray[int1 + 4] = matrix3x2d.m11();
			doubleArray[int1 + 5] = 0.0;
			doubleArray[int1 + 6] = matrix3x2d.m20();
			doubleArray[int1 + 7] = matrix3x2d.m21();
			doubleArray[int1 + 8] = 1.0;
		}

		public void copy4x4(Matrix3x2f matrix3x2f, float[] floatArray, int int1) {
			floatArray[int1 + 0] = matrix3x2f.m00();
			floatArray[int1 + 1] = matrix3x2f.m01();
			floatArray[int1 + 2] = 0.0F;
			floatArray[int1 + 3] = 0.0F;
			floatArray[int1 + 4] = matrix3x2f.m10();
			floatArray[int1 + 5] = matrix3x2f.m11();
			floatArray[int1 + 6] = 0.0F;
			floatArray[int1 + 7] = 0.0F;
			floatArray[int1 + 8] = 0.0F;
			floatArray[int1 + 9] = 0.0F;
			floatArray[int1 + 10] = 1.0F;
			floatArray[int1 + 11] = 0.0F;
			floatArray[int1 + 12] = matrix3x2f.m20();
			floatArray[int1 + 13] = matrix3x2f.m21();
			floatArray[int1 + 14] = 0.0F;
			floatArray[int1 + 15] = 1.0F;
		}

		public void copy4x4(Matrix3x2d matrix3x2d, double[] doubleArray, int int1) {
			doubleArray[int1 + 0] = matrix3x2d.m00();
			doubleArray[int1 + 1] = matrix3x2d.m01();
			doubleArray[int1 + 2] = 0.0;
			doubleArray[int1 + 3] = 0.0;
			doubleArray[int1 + 4] = matrix3x2d.m10();
			doubleArray[int1 + 5] = matrix3x2d.m11();
			doubleArray[int1 + 6] = 0.0;
			doubleArray[int1 + 7] = 0.0;
			doubleArray[int1 + 8] = 0.0;
			doubleArray[int1 + 9] = 0.0;
			doubleArray[int1 + 10] = 1.0;
			doubleArray[int1 + 11] = 0.0;
			doubleArray[int1 + 12] = matrix3x2d.m20();
			doubleArray[int1 + 13] = matrix3x2d.m21();
			doubleArray[int1 + 14] = 0.0;
			doubleArray[int1 + 15] = 1.0;
		}

		public void identity(Matrix4f matrix4f) {
			matrix4f._m00(1.0F)._m01(0.0F)._m02(0.0F)._m03(0.0F)._m10(0.0F)._m11(1.0F)._m12(0.0F)._m13(0.0F)._m20(0.0F)._m21(0.0F)._m22(1.0F)._m23(0.0F)._m30(0.0F)._m31(0.0F)._m32(0.0F)._m33(1.0F);
		}

		public void identity(Matrix4x3f matrix4x3f) {
			matrix4x3f._m00(1.0F)._m01(0.0F)._m02(0.0F)._m10(0.0F)._m11(1.0F)._m12(0.0F)._m20(0.0F)._m21(0.0F)._m22(1.0F)._m30(0.0F)._m31(0.0F)._m32(0.0F);
		}

		public void identity(Matrix3f matrix3f) {
			matrix3f._m00(1.0F)._m01(0.0F)._m02(0.0F)._m10(0.0F)._m11(1.0F)._m12(0.0F)._m20(0.0F)._m21(0.0F)._m22(1.0F);
		}

		public void identity(Matrix3x2f matrix3x2f) {
			matrix3x2f._m00(1.0F)._m01(0.0F)._m10(0.0F)._m11(1.0F)._m20(0.0F)._m21(0.0F);
		}

		public void identity(Matrix3x2d matrix3x2d) {
			matrix3x2d._m00(1.0)._m01(0.0)._m10(0.0)._m11(1.0)._m20(0.0)._m21(0.0);
		}

		public void identity(Matrix2f matrix2f) {
			matrix2f._m00(1.0F)._m01(0.0F)._m10(0.0F)._m11(1.0F);
		}

		public void swap(Matrix4f matrix4f, Matrix4f matrix4f2) {
			float float1 = matrix4f.m00();
			matrix4f._m00(matrix4f2.m00());
			matrix4f2._m00(float1);
			float1 = matrix4f.m01();
			matrix4f._m01(matrix4f2.m01());
			matrix4f2._m01(float1);
			float1 = matrix4f.m02();
			matrix4f._m02(matrix4f2.m02());
			matrix4f2._m02(float1);
			float1 = matrix4f.m03();
			matrix4f._m03(matrix4f2.m03());
			matrix4f2._m03(float1);
			float1 = matrix4f.m10();
			matrix4f._m10(matrix4f2.m10());
			matrix4f2._m10(float1);
			float1 = matrix4f.m11();
			matrix4f._m11(matrix4f2.m11());
			matrix4f2._m11(float1);
			float1 = matrix4f.m12();
			matrix4f._m12(matrix4f2.m12());
			matrix4f2._m12(float1);
			float1 = matrix4f.m13();
			matrix4f._m13(matrix4f2.m13());
			matrix4f2._m13(float1);
			float1 = matrix4f.m20();
			matrix4f._m20(matrix4f2.m20());
			matrix4f2._m20(float1);
			float1 = matrix4f.m21();
			matrix4f._m21(matrix4f2.m21());
			matrix4f2._m21(float1);
			float1 = matrix4f.m22();
			matrix4f._m22(matrix4f2.m22());
			matrix4f2._m22(float1);
			float1 = matrix4f.m23();
			matrix4f._m23(matrix4f2.m23());
			matrix4f2._m23(float1);
			float1 = matrix4f.m30();
			matrix4f._m30(matrix4f2.m30());
			matrix4f2._m30(float1);
			float1 = matrix4f.m31();
			matrix4f._m31(matrix4f2.m31());
			matrix4f2._m31(float1);
			float1 = matrix4f.m32();
			matrix4f._m32(matrix4f2.m32());
			matrix4f2._m32(float1);
			float1 = matrix4f.m33();
			matrix4f._m33(matrix4f2.m33());
			matrix4f2._m33(float1);
		}

		public void swap(Matrix4x3f matrix4x3f, Matrix4x3f matrix4x3f2) {
			float float1 = matrix4x3f.m00();
			matrix4x3f._m00(matrix4x3f2.m00());
			matrix4x3f2._m00(float1);
			float1 = matrix4x3f.m01();
			matrix4x3f._m01(matrix4x3f2.m01());
			matrix4x3f2._m01(float1);
			float1 = matrix4x3f.m02();
			matrix4x3f._m02(matrix4x3f2.m02());
			matrix4x3f2._m02(float1);
			float1 = matrix4x3f.m10();
			matrix4x3f._m10(matrix4x3f2.m10());
			matrix4x3f2._m10(float1);
			float1 = matrix4x3f.m11();
			matrix4x3f._m11(matrix4x3f2.m11());
			matrix4x3f2._m11(float1);
			float1 = matrix4x3f.m12();
			matrix4x3f._m12(matrix4x3f2.m12());
			matrix4x3f2._m12(float1);
			float1 = matrix4x3f.m20();
			matrix4x3f._m20(matrix4x3f2.m20());
			matrix4x3f2._m20(float1);
			float1 = matrix4x3f.m21();
			matrix4x3f._m21(matrix4x3f2.m21());
			matrix4x3f2._m21(float1);
			float1 = matrix4x3f.m22();
			matrix4x3f._m22(matrix4x3f2.m22());
			matrix4x3f2._m22(float1);
			float1 = matrix4x3f.m30();
			matrix4x3f._m30(matrix4x3f2.m30());
			matrix4x3f2._m30(float1);
			float1 = matrix4x3f.m31();
			matrix4x3f._m31(matrix4x3f2.m31());
			matrix4x3f2._m31(float1);
			float1 = matrix4x3f.m32();
			matrix4x3f._m32(matrix4x3f2.m32());
			matrix4x3f2._m32(float1);
		}

		public void swap(Matrix3f matrix3f, Matrix3f matrix3f2) {
			float float1 = matrix3f.m00();
			matrix3f._m00(matrix3f2.m00());
			matrix3f2._m00(float1);
			float1 = matrix3f.m01();
			matrix3f._m01(matrix3f2.m01());
			matrix3f2._m01(float1);
			float1 = matrix3f.m02();
			matrix3f._m02(matrix3f2.m02());
			matrix3f2._m02(float1);
			float1 = matrix3f.m10();
			matrix3f._m10(matrix3f2.m10());
			matrix3f2._m10(float1);
			float1 = matrix3f.m11();
			matrix3f._m11(matrix3f2.m11());
			matrix3f2._m11(float1);
			float1 = matrix3f.m12();
			matrix3f._m12(matrix3f2.m12());
			matrix3f2._m12(float1);
			float1 = matrix3f.m20();
			matrix3f._m20(matrix3f2.m20());
			matrix3f2._m20(float1);
			float1 = matrix3f.m21();
			matrix3f._m21(matrix3f2.m21());
			matrix3f2._m21(float1);
			float1 = matrix3f.m22();
			matrix3f._m22(matrix3f2.m22());
			matrix3f2._m22(float1);
		}

		public void swap(Matrix2f matrix2f, Matrix2f matrix2f2) {
			float float1 = matrix2f.m00();
			matrix2f._m00(matrix2f2.m00());
			matrix2f2._m00(float1);
			float1 = matrix2f.m01();
			matrix2f._m00(matrix2f2.m01());
			matrix2f2._m01(float1);
			float1 = matrix2f.m10();
			matrix2f._m00(matrix2f2.m10());
			matrix2f2._m10(float1);
			float1 = matrix2f.m11();
			matrix2f._m00(matrix2f2.m11());
			matrix2f2._m11(float1);
		}

		public void swap(Matrix2d matrix2d, Matrix2d matrix2d2) {
			double double1 = matrix2d.m00();
			matrix2d._m00(matrix2d2.m00());
			matrix2d2._m00(double1);
			double1 = matrix2d.m01();
			matrix2d._m00(matrix2d2.m01());
			matrix2d2._m01(double1);
			double1 = matrix2d.m10();
			matrix2d._m00(matrix2d2.m10());
			matrix2d2._m10(double1);
			double1 = matrix2d.m11();
			matrix2d._m00(matrix2d2.m11());
			matrix2d2._m11(double1);
		}

		public void zero(Matrix4f matrix4f) {
			matrix4f._m00(0.0F)._m01(0.0F)._m02(0.0F)._m03(0.0F)._m10(0.0F)._m11(0.0F)._m12(0.0F)._m13(0.0F)._m20(0.0F)._m21(0.0F)._m22(0.0F)._m23(0.0F)._m30(0.0F)._m31(0.0F)._m32(0.0F)._m33(0.0F);
		}

		public void zero(Matrix4x3f matrix4x3f) {
			matrix4x3f._m00(0.0F)._m01(0.0F)._m02(0.0F)._m10(0.0F)._m11(0.0F)._m12(0.0F)._m20(0.0F)._m21(0.0F)._m22(0.0F)._m30(0.0F)._m31(0.0F)._m32(0.0F);
		}

		public void zero(Matrix3f matrix3f) {
			matrix3f._m00(0.0F)._m01(0.0F)._m02(0.0F)._m10(0.0F)._m11(0.0F)._m12(0.0F)._m20(0.0F)._m21(0.0F)._m22(0.0F);
		}

		public void zero(Matrix3x2f matrix3x2f) {
			matrix3x2f._m00(0.0F)._m01(0.0F)._m10(0.0F)._m11(0.0F)._m20(0.0F)._m21(0.0F);
		}

		public void zero(Matrix3x2d matrix3x2d) {
			matrix3x2d._m00(0.0)._m01(0.0)._m10(0.0)._m11(0.0)._m20(0.0)._m21(0.0);
		}

		public void zero(Matrix2f matrix2f) {
			matrix2f._m00(0.0F)._m01(0.0F)._m10(0.0F)._m11(0.0F);
		}

		public void zero(Matrix2d matrix2d) {
			matrix2d._m00(0.0)._m01(0.0)._m10(0.0)._m11(0.0);
		}

		public void putMatrix3f(Quaternionf quaternionf, int int1, ByteBuffer byteBuffer) {
			float float1 = quaternionf.w * quaternionf.w;
			float float2 = quaternionf.x * quaternionf.x;
			float float3 = quaternionf.y * quaternionf.y;
			float float4 = quaternionf.z * quaternionf.z;
			float float5 = quaternionf.z * quaternionf.w;
			float float6 = quaternionf.x * quaternionf.y;
			float float7 = quaternionf.x * quaternionf.z;
			float float8 = quaternionf.y * quaternionf.w;
			float float9 = quaternionf.y * quaternionf.z;
			float float10 = quaternionf.x * quaternionf.w;
			byteBuffer.putFloat(int1, float1 + float2 - float4 - float3).putFloat(int1 + 4, float6 + float5 + float5 + float6).putFloat(int1 + 8, float7 - float8 + float7 - float8).putFloat(int1 + 12, -float5 + float6 - float5 + float6).putFloat(int1 + 16, float3 - float4 + float1 - float2).putFloat(int1 + 20, float9 + float9 + float10 + float10).putFloat(int1 + 24, float8 + float7 + float7 + float8).putFloat(int1 + 28, float9 + float9 - float10 - float10).putFloat(int1 + 32, float4 - float3 - float2 + float1);
		}

		public void putMatrix3f(Quaternionf quaternionf, int int1, FloatBuffer floatBuffer) {
			float float1 = quaternionf.w * quaternionf.w;
			float float2 = quaternionf.x * quaternionf.x;
			float float3 = quaternionf.y * quaternionf.y;
			float float4 = quaternionf.z * quaternionf.z;
			float float5 = quaternionf.z * quaternionf.w;
			float float6 = quaternionf.x * quaternionf.y;
			float float7 = quaternionf.x * quaternionf.z;
			float float8 = quaternionf.y * quaternionf.w;
			float float9 = quaternionf.y * quaternionf.z;
			float float10 = quaternionf.x * quaternionf.w;
			floatBuffer.put(int1, float1 + float2 - float4 - float3).put(int1 + 1, float6 + float5 + float5 + float6).put(int1 + 2, float7 - float8 + float7 - float8).put(int1 + 3, -float5 + float6 - float5 + float6).put(int1 + 4, float3 - float4 + float1 - float2).put(int1 + 5, float9 + float9 + float10 + float10).put(int1 + 6, float8 + float7 + float7 + float8).put(int1 + 7, float9 + float9 - float10 - float10).put(int1 + 8, float4 - float3 - float2 + float1);
		}

		public void putMatrix4f(Quaternionf quaternionf, int int1, ByteBuffer byteBuffer) {
			float float1 = quaternionf.w * quaternionf.w;
			float float2 = quaternionf.x * quaternionf.x;
			float float3 = quaternionf.y * quaternionf.y;
			float float4 = quaternionf.z * quaternionf.z;
			float float5 = quaternionf.z * quaternionf.w;
			float float6 = quaternionf.x * quaternionf.y;
			float float7 = quaternionf.x * quaternionf.z;
			float float8 = quaternionf.y * quaternionf.w;
			float float9 = quaternionf.y * quaternionf.z;
			float float10 = quaternionf.x * quaternionf.w;
			byteBuffer.putFloat(int1, float1 + float2 - float4 - float3).putFloat(int1 + 4, float6 + float5 + float5 + float6).putFloat(int1 + 8, float7 - float8 + float7 - float8).putFloat(int1 + 12, 0.0F).putFloat(int1 + 16, -float5 + float6 - float5 + float6).putFloat(int1 + 20, float3 - float4 + float1 - float2).putFloat(int1 + 24, float9 + float9 + float10 + float10).putFloat(int1 + 28, 0.0F).putFloat(int1 + 32, float8 + float7 + float7 + float8).putFloat(int1 + 36, float9 + float9 - float10 - float10).putFloat(int1 + 40, float4 - float3 - float2 + float1).putFloat(int1 + 44, 0.0F).putLong(int1 + 48, 0L).putLong(int1 + 56, 4575657221408423936L);
		}

		public void putMatrix4f(Quaternionf quaternionf, int int1, FloatBuffer floatBuffer) {
			float float1 = quaternionf.w * quaternionf.w;
			float float2 = quaternionf.x * quaternionf.x;
			float float3 = quaternionf.y * quaternionf.y;
			float float4 = quaternionf.z * quaternionf.z;
			float float5 = quaternionf.z * quaternionf.w;
			float float6 = quaternionf.x * quaternionf.y;
			float float7 = quaternionf.x * quaternionf.z;
			float float8 = quaternionf.y * quaternionf.w;
			float float9 = quaternionf.y * quaternionf.z;
			float float10 = quaternionf.x * quaternionf.w;
			floatBuffer.put(int1, float1 + float2 - float4 - float3).put(int1 + 1, float6 + float5 + float5 + float6).put(int1 + 2, float7 - float8 + float7 - float8).put(int1 + 3, 0.0F).put(int1 + 4, -float5 + float6 - float5 + float6).put(int1 + 5, float3 - float4 + float1 - float2).put(int1 + 6, float9 + float9 + float10 + float10).put(int1 + 7, 0.0F).put(int1 + 8, float8 + float7 + float7 + float8).put(int1 + 9, float9 + float9 - float10 - float10).put(int1 + 10, float4 - float3 - float2 + float1).put(int1 + 11, 0.0F).put(int1 + 12, 0.0F).put(int1 + 13, 0.0F).put(int1 + 14, 0.0F).put(int1 + 15, 1.0F);
		}

		public void putMatrix4x3f(Quaternionf quaternionf, int int1, ByteBuffer byteBuffer) {
			float float1 = quaternionf.w * quaternionf.w;
			float float2 = quaternionf.x * quaternionf.x;
			float float3 = quaternionf.y * quaternionf.y;
			float float4 = quaternionf.z * quaternionf.z;
			float float5 = quaternionf.z * quaternionf.w;
			float float6 = quaternionf.x * quaternionf.y;
			float float7 = quaternionf.x * quaternionf.z;
			float float8 = quaternionf.y * quaternionf.w;
			float float9 = quaternionf.y * quaternionf.z;
			float float10 = quaternionf.x * quaternionf.w;
			byteBuffer.putFloat(int1, float1 + float2 - float4 - float3).putFloat(int1 + 4, float6 + float5 + float5 + float6).putFloat(int1 + 8, float7 - float8 + float7 - float8).putFloat(int1 + 12, -float5 + float6 - float5 + float6).putFloat(int1 + 16, float3 - float4 + float1 - float2).putFloat(int1 + 20, float9 + float9 + float10 + float10).putFloat(int1 + 24, float8 + float7 + float7 + float8).putFloat(int1 + 28, float9 + float9 - float10 - float10).putFloat(int1 + 32, float4 - float3 - float2 + float1).putLong(int1 + 36, 0L).putFloat(int1 + 44, 0.0F);
		}

		public void putMatrix4x3f(Quaternionf quaternionf, int int1, FloatBuffer floatBuffer) {
			float float1 = quaternionf.w * quaternionf.w;
			float float2 = quaternionf.x * quaternionf.x;
			float float3 = quaternionf.y * quaternionf.y;
			float float4 = quaternionf.z * quaternionf.z;
			float float5 = quaternionf.z * quaternionf.w;
			float float6 = quaternionf.x * quaternionf.y;
			float float7 = quaternionf.x * quaternionf.z;
			float float8 = quaternionf.y * quaternionf.w;
			float float9 = quaternionf.y * quaternionf.z;
			float float10 = quaternionf.x * quaternionf.w;
			floatBuffer.put(int1, float1 + float2 - float4 - float3).put(int1 + 1, float6 + float5 + float5 + float6).put(int1 + 2, float7 - float8 + float7 - float8).put(int1 + 3, -float5 + float6 - float5 + float6).put(int1 + 4, float3 - float4 + float1 - float2).put(int1 + 5, float9 + float9 + float10 + float10).put(int1 + 6, float8 + float7 + float7 + float8).put(int1 + 7, float9 + float9 - float10 - float10).put(int1 + 8, float4 - float3 - float2 + float1).put(int1 + 9, 0.0F).put(int1 + 10, 0.0F).put(int1 + 11, 0.0F);
		}
	}

	public static class MemUtilUnsafe extends MemUtil.MemUtilNIO {
		public static final Unsafe UNSAFE = getUnsafeInstance();
		public static final long ADDRESS;
		public static final long Matrix2f_m00;
		public static final long Matrix3f_m00;
		public static final long Matrix3d_m00;
		public static final long Matrix4f_m00;
		public static final long Matrix4d_m00;
		public static final long Matrix4x3f_m00;
		public static final long Matrix3x2f_m00;
		public static final long Vector4f_x;
		public static final long Vector4i_x;
		public static final long Vector3f_x;
		public static final long Vector3i_x;
		public static final long Vector2f_x;
		public static final long Vector2i_x;
		public static final long Quaternionf_x;
		public static final long floatArrayOffset;

		private static long findBufferAddress() {
			try {
				return UNSAFE.objectFieldOffset(getDeclaredField(Buffer.class, "address"));
			} catch (Exception exception) {
				throw new UnsupportedOperationException(exception);
			}
		}

		private static long checkMatrix4f() throws NoSuchFieldException, SecurityException {
			Field field = Matrix4f.class.getDeclaredField("m00");
			long long1 = UNSAFE.objectFieldOffset(field);
			for (int int1 = 1; int1 < 16; ++int1) {
				int int2 = int1 >>> 2;
				int int3 = int1 & 3;
				field = Matrix4f.class.getDeclaredField("m" + int2 + int3);
				long long2 = UNSAFE.objectFieldOffset(field);
				if (long2 != long1 + (long)(int1 << 2)) {
					throw new UnsupportedOperationException("Unexpected Matrix4f element offset");
				}
			}

			return long1;
		}

		private static long checkMatrix4d() throws NoSuchFieldException, SecurityException {
			Field field = Matrix4d.class.getDeclaredField("m00");
			long long1 = UNSAFE.objectFieldOffset(field);
			for (int int1 = 1; int1 < 16; ++int1) {
				int int2 = int1 >>> 2;
				int int3 = int1 & 3;
				field = Matrix4d.class.getDeclaredField("m" + int2 + int3);
				long long2 = UNSAFE.objectFieldOffset(field);
				if (long2 != long1 + (long)(int1 << 3)) {
					throw new UnsupportedOperationException("Unexpected Matrix4d element offset");
				}
			}

			return long1;
		}

		private static long checkMatrix4x3f() throws NoSuchFieldException, SecurityException {
			Field field = Matrix4x3f.class.getDeclaredField("m00");
			long long1 = UNSAFE.objectFieldOffset(field);
			for (int int1 = 1; int1 < 12; ++int1) {
				int int2 = int1 / 3;
				int int3 = int1 % 3;
				field = Matrix4x3f.class.getDeclaredField("m" + int2 + int3);
				long long2 = UNSAFE.objectFieldOffset(field);
				if (long2 != long1 + (long)(int1 << 2)) {
					throw new UnsupportedOperationException("Unexpected Matrix4x3f element offset");
				}
			}

			return long1;
		}

		private static long checkMatrix3f() throws NoSuchFieldException, SecurityException {
			Field field = Matrix3f.class.getDeclaredField("m00");
			long long1 = UNSAFE.objectFieldOffset(field);
			for (int int1 = 1; int1 < 9; ++int1) {
				int int2 = int1 / 3;
				int int3 = int1 % 3;
				field = Matrix3f.class.getDeclaredField("m" + int2 + int3);
				long long2 = UNSAFE.objectFieldOffset(field);
				if (long2 != long1 + (long)(int1 << 2)) {
					throw new UnsupportedOperationException("Unexpected Matrix3f element offset");
				}
			}

			return long1;
		}

		private static long checkMatrix3d() throws NoSuchFieldException, SecurityException {
			Field field = Matrix3d.class.getDeclaredField("m00");
			long long1 = UNSAFE.objectFieldOffset(field);
			for (int int1 = 1; int1 < 9; ++int1) {
				int int2 = int1 / 3;
				int int3 = int1 % 3;
				field = Matrix3d.class.getDeclaredField("m" + int2 + int3);
				long long2 = UNSAFE.objectFieldOffset(field);
				if (long2 != long1 + (long)(int1 << 3)) {
					throw new UnsupportedOperationException("Unexpected Matrix3d element offset");
				}
			}

			return long1;
		}

		private static long checkMatrix3x2f() throws NoSuchFieldException, SecurityException {
			Field field = Matrix3x2f.class.getDeclaredField("m00");
			long long1 = UNSAFE.objectFieldOffset(field);
			for (int int1 = 1; int1 < 6; ++int1) {
				int int2 = int1 / 2;
				int int3 = int1 % 2;
				field = Matrix3x2f.class.getDeclaredField("m" + int2 + int3);
				long long2 = UNSAFE.objectFieldOffset(field);
				if (long2 != long1 + (long)(int1 << 2)) {
					throw new UnsupportedOperationException("Unexpected Matrix3x2f element offset");
				}
			}

			return long1;
		}

		private static long checkMatrix2f() throws NoSuchFieldException, SecurityException {
			Field field = Matrix2f.class.getDeclaredField("m00");
			long long1 = UNSAFE.objectFieldOffset(field);
			for (int int1 = 1; int1 < 4; ++int1) {
				int int2 = int1 / 2;
				int int3 = int1 % 2;
				field = Matrix2f.class.getDeclaredField("m" + int2 + int3);
				long long2 = UNSAFE.objectFieldOffset(field);
				if (long2 != long1 + (long)(int1 << 2)) {
					throw new UnsupportedOperationException("Unexpected Matrix2f element offset");
				}
			}

			return long1;
		}

		private static long checkVector4f() throws NoSuchFieldException, SecurityException {
			Field field = Vector4f.class.getDeclaredField("x");
			long long1 = UNSAFE.objectFieldOffset(field);
			String[] stringArray = new String[]{"y", "z", "w"};
			for (int int1 = 1; int1 < 4; ++int1) {
				field = Vector4f.class.getDeclaredField(stringArray[int1 - 1]);
				long long2 = UNSAFE.objectFieldOffset(field);
				if (long2 != long1 + (long)(int1 << 2)) {
					throw new UnsupportedOperationException("Unexpected Vector4f element offset");
				}
			}

			return long1;
		}

		private static long checkVector4i() throws NoSuchFieldException, SecurityException {
			Field field = Vector4i.class.getDeclaredField("x");
			long long1 = UNSAFE.objectFieldOffset(field);
			String[] stringArray = new String[]{"y", "z", "w"};
			for (int int1 = 1; int1 < 4; ++int1) {
				field = Vector4i.class.getDeclaredField(stringArray[int1 - 1]);
				long long2 = UNSAFE.objectFieldOffset(field);
				if (long2 != long1 + (long)(int1 << 2)) {
					throw new UnsupportedOperationException("Unexpected Vector4i element offset");
				}
			}

			return long1;
		}

		private static long checkVector3f() throws NoSuchFieldException, SecurityException {
			Field field = Vector3f.class.getDeclaredField("x");
			long long1 = UNSAFE.objectFieldOffset(field);
			String[] stringArray = new String[]{"y", "z"};
			for (int int1 = 1; int1 < 3; ++int1) {
				field = Vector3f.class.getDeclaredField(stringArray[int1 - 1]);
				long long2 = UNSAFE.objectFieldOffset(field);
				if (long2 != long1 + (long)(int1 << 2)) {
					throw new UnsupportedOperationException("Unexpected Vector3f element offset");
				}
			}

			return long1;
		}

		private static long checkVector3i() throws NoSuchFieldException, SecurityException {
			Field field = Vector3i.class.getDeclaredField("x");
			long long1 = UNSAFE.objectFieldOffset(field);
			String[] stringArray = new String[]{"y", "z"};
			for (int int1 = 1; int1 < 3; ++int1) {
				field = Vector3i.class.getDeclaredField(stringArray[int1 - 1]);
				long long2 = UNSAFE.objectFieldOffset(field);
				if (long2 != long1 + (long)(int1 << 2)) {
					throw new UnsupportedOperationException("Unexpected Vector3i element offset");
				}
			}

			return long1;
		}

		private static long checkVector2f() throws NoSuchFieldException, SecurityException {
			Field field = Vector2f.class.getDeclaredField("x");
			long long1 = UNSAFE.objectFieldOffset(field);
			field = Vector2f.class.getDeclaredField("y");
			long long2 = UNSAFE.objectFieldOffset(field);
			if (long2 != long1 + 4L) {
				throw new UnsupportedOperationException("Unexpected Vector2f element offset");
			} else {
				return long1;
			}
		}

		private static long checkVector2i() throws NoSuchFieldException, SecurityException {
			Field field = Vector2i.class.getDeclaredField("x");
			long long1 = UNSAFE.objectFieldOffset(field);
			field = Vector2i.class.getDeclaredField("y");
			long long2 = UNSAFE.objectFieldOffset(field);
			if (long2 != long1 + 4L) {
				throw new UnsupportedOperationException("Unexpected Vector2i element offset");
			} else {
				return long1;
			}
		}

		private static long checkQuaternionf() throws NoSuchFieldException, SecurityException {
			Field field = Quaternionf.class.getDeclaredField("x");
			long long1 = UNSAFE.objectFieldOffset(field);
			String[] stringArray = new String[]{"y", "z", "w"};
			for (int int1 = 1; int1 < 4; ++int1) {
				field = Quaternionf.class.getDeclaredField(stringArray[int1 - 1]);
				long long2 = UNSAFE.objectFieldOffset(field);
				if (long2 != long1 + (long)(int1 << 2)) {
					throw new UnsupportedOperationException("Unexpected Quaternionf element offset");
				}
			}

			return long1;
		}

		private static Field getDeclaredField(Class javaClass, String string) throws NoSuchFieldException {
			Class javaClass2 = javaClass;
			do {
				try {
					Field field = javaClass2.getDeclaredField(string);
					return field;
				} catch (NoSuchFieldException noSuchFieldException) {
					javaClass2 = javaClass2.getSuperclass();
				} catch (SecurityException securityException) {
					javaClass2 = javaClass2.getSuperclass();
				}
			} while (javaClass2 != null);

			throw new NoSuchFieldException(string + " does not exist in " + javaClass.getName() + " or any of its superclasses.");
		}

		public static Unsafe getUnsafeInstance() throws SecurityException {
			Field[] fieldArray = Unsafe.class.getDeclaredFields();
			int int1 = 0;
			while (true) {
				label31: {
					if (int1 < fieldArray.length) {
						Field field = fieldArray[int1];
						if (!field.getType().equals(Unsafe.class)) {
							break label31;
						}

						int int2 = field.getModifiers();
						if (!Modifier.isStatic(int2) || !Modifier.isFinal(int2)) {
							break label31;
						}

						field.setAccessible(true);
						try {
							return (Unsafe)field.get((Object)null);
						} catch (IllegalAccessException illegalAccessException) {
						}
					}

					throw new UnsupportedOperationException();
				}

				++int1;
			}
		}

		public static void put(Matrix4f matrix4f, long long1) {
			for (int int1 = 0; int1 < 8; ++int1) {
				UNSAFE.putLong((Object)null, long1 + (long)(int1 << 3), UNSAFE.getLong(matrix4f, Matrix4f_m00 + (long)(int1 << 3)));
			}
		}

		public static void put4x3(Matrix4f matrix4f, long long1) {
			Unsafe unsafe = UNSAFE;
			for (int int1 = 0; int1 < 4; ++int1) {
				unsafe.putLong((Object)null, long1 + (long)(12 * int1), unsafe.getLong(matrix4f, Matrix4f_m00 + (long)(int1 << 4)));
			}

			unsafe.putFloat((Object)null, long1 + 8L, matrix4f.m02());
			unsafe.putFloat((Object)null, long1 + 20L, matrix4f.m12());
			unsafe.putFloat((Object)null, long1 + 32L, matrix4f.m22());
			unsafe.putFloat((Object)null, long1 + 44L, matrix4f.m32());
		}

		public static void put3x4(Matrix4f matrix4f, long long1) {
			for (int int1 = 0; int1 < 6; ++int1) {
				UNSAFE.putLong((Object)null, long1 + (long)(int1 << 3), UNSAFE.getLong(matrix4f, Matrix4f_m00 + (long)(int1 << 3)));
			}
		}

		public static void put(Matrix4x3f matrix4x3f, long long1) {
			for (int int1 = 0; int1 < 6; ++int1) {
				UNSAFE.putLong((Object)null, long1 + (long)(int1 << 3), UNSAFE.getLong(matrix4x3f, Matrix4x3f_m00 + (long)(int1 << 3)));
			}
		}

		public static void put4x4(Matrix4x3f matrix4x3f, long long1) {
			for (int int1 = 0; int1 < 4; ++int1) {
				UNSAFE.putLong((Object)null, long1 + (long)(int1 << 4), UNSAFE.getLong(matrix4x3f, Matrix4x3f_m00 + (long)(12 * int1)));
				long long2 = (long)UNSAFE.getInt(matrix4x3f, Matrix4x3f_m00 + 8L + (long)(12 * int1)) & 4294967295L;
				UNSAFE.putLong((Object)null, long1 + 8L + (long)(int1 << 4), long2);
			}

			UNSAFE.putFloat((Object)null, long1 + 60L, 1.0F);
		}

		public static void put3x4(Matrix4x3f matrix4x3f, long long1) {
			for (int int1 = 0; int1 < 3; ++int1) {
				UNSAFE.putLong((Object)null, long1 + (long)(int1 << 4), UNSAFE.getLong(matrix4x3f, Matrix4x3f_m00 + (long)(12 * int1)));
				UNSAFE.putFloat((Object)null, long1 + (long)(int1 << 4) + 8L, UNSAFE.getFloat(matrix4x3f, Matrix4x3f_m00 + 8L + (long)(12 * int1)));
				UNSAFE.putFloat((Object)null, long1 + (long)(int1 << 4) + 12L, 0.0F);
			}
		}

		public static void put4x4(Matrix4x3d matrix4x3d, long long1) {
			Unsafe unsafe = UNSAFE;
			unsafe.putDouble((Object)null, long1, matrix4x3d.m00());
			unsafe.putDouble((Object)null, long1 + 8L, matrix4x3d.m01());
			unsafe.putDouble((Object)null, long1 + 16L, matrix4x3d.m02());
			unsafe.putDouble((Object)null, long1 + 24L, 0.0);
			unsafe.putDouble((Object)null, long1 + 32L, matrix4x3d.m10());
			unsafe.putDouble((Object)null, long1 + 40L, matrix4x3d.m11());
			unsafe.putDouble((Object)null, long1 + 48L, matrix4x3d.m12());
			unsafe.putDouble((Object)null, long1 + 56L, 0.0);
			unsafe.putDouble((Object)null, long1 + 64L, matrix4x3d.m20());
			unsafe.putDouble((Object)null, long1 + 72L, matrix4x3d.m21());
			unsafe.putDouble((Object)null, long1 + 80L, matrix4x3d.m22());
			unsafe.putDouble((Object)null, long1 + 88L, 0.0);
			unsafe.putDouble((Object)null, long1 + 96L, matrix4x3d.m30());
			unsafe.putDouble((Object)null, long1 + 104L, matrix4x3d.m31());
			unsafe.putDouble((Object)null, long1 + 112L, matrix4x3d.m32());
			unsafe.putDouble((Object)null, long1 + 120L, 1.0);
		}

		public static void put4x4(Matrix3x2f matrix3x2f, long long1) {
			Unsafe unsafe = UNSAFE;
			unsafe.putLong((Object)null, long1, unsafe.getLong(matrix3x2f, Matrix3x2f_m00));
			unsafe.putLong((Object)null, long1 + 8L, 0L);
			unsafe.putLong((Object)null, long1 + 16L, unsafe.getLong(matrix3x2f, Matrix3x2f_m00 + 8L));
			unsafe.putLong((Object)null, long1 + 24L, 0L);
			unsafe.putLong((Object)null, long1 + 32L, 0L);
			unsafe.putLong((Object)null, long1 + 40L, 1065353216L);
			unsafe.putLong((Object)null, long1 + 48L, unsafe.getLong(matrix3x2f, Matrix3x2f_m00 + 16L));
			unsafe.putLong((Object)null, long1 + 56L, 4575657221408423936L);
		}

		public static void put4x4(Matrix3x2d matrix3x2d, long long1) {
			Unsafe unsafe = UNSAFE;
			unsafe.putDouble((Object)null, long1, matrix3x2d.m00());
			unsafe.putDouble((Object)null, long1 + 8L, matrix3x2d.m01());
			unsafe.putDouble((Object)null, long1 + 16L, 0.0);
			unsafe.putDouble((Object)null, long1 + 24L, 0.0);
			unsafe.putDouble((Object)null, long1 + 32L, matrix3x2d.m10());
			unsafe.putDouble((Object)null, long1 + 40L, matrix3x2d.m11());
			unsafe.putDouble((Object)null, long1 + 48L, 0.0);
			unsafe.putDouble((Object)null, long1 + 56L, 0.0);
			unsafe.putDouble((Object)null, long1 + 64L, 0.0);
			unsafe.putDouble((Object)null, long1 + 72L, 0.0);
			unsafe.putDouble((Object)null, long1 + 80L, 1.0);
			unsafe.putDouble((Object)null, long1 + 88L, 0.0);
			unsafe.putDouble((Object)null, long1 + 96L, matrix3x2d.m20());
			unsafe.putDouble((Object)null, long1 + 104L, matrix3x2d.m21());
			unsafe.putDouble((Object)null, long1 + 112L, 0.0);
			unsafe.putDouble((Object)null, long1 + 120L, 1.0);
		}

		public static void put3x3(Matrix3x2f matrix3x2f, long long1) {
			Unsafe unsafe = UNSAFE;
			unsafe.putLong((Object)null, long1, unsafe.getLong(matrix3x2f, Matrix3x2f_m00));
			unsafe.putInt((Object)null, long1 + 8L, 0);
			unsafe.putLong((Object)null, long1 + 12L, unsafe.getLong(matrix3x2f, Matrix3x2f_m00 + 8L));
			unsafe.putInt((Object)null, long1 + 20L, 0);
			unsafe.putLong((Object)null, long1 + 24L, unsafe.getLong(matrix3x2f, Matrix3x2f_m00 + 16L));
			unsafe.putFloat((Object)null, long1 + 32L, 0.0F);
		}

		public static void put3x3(Matrix3x2d matrix3x2d, long long1) {
			Unsafe unsafe = UNSAFE;
			unsafe.putDouble((Object)null, long1, matrix3x2d.m00());
			unsafe.putDouble((Object)null, long1 + 8L, matrix3x2d.m01());
			unsafe.putDouble((Object)null, long1 + 16L, 0.0);
			unsafe.putDouble((Object)null, long1 + 24L, matrix3x2d.m10());
			unsafe.putDouble((Object)null, long1 + 32L, matrix3x2d.m11());
			unsafe.putDouble((Object)null, long1 + 40L, 0.0);
			unsafe.putDouble((Object)null, long1 + 48L, matrix3x2d.m20());
			unsafe.putDouble((Object)null, long1 + 56L, matrix3x2d.m21());
			unsafe.putDouble((Object)null, long1 + 64L, 1.0);
		}

		public static void putTransposed(Matrix4f matrix4f, long long1) {
			Unsafe unsafe = UNSAFE;
			unsafe.putFloat((Object)null, long1, matrix4f.m00());
			unsafe.putFloat((Object)null, long1 + 4L, matrix4f.m10());
			unsafe.putFloat((Object)null, long1 + 8L, matrix4f.m20());
			unsafe.putFloat((Object)null, long1 + 12L, matrix4f.m30());
			unsafe.putFloat((Object)null, long1 + 16L, matrix4f.m01());
			unsafe.putFloat((Object)null, long1 + 20L, matrix4f.m11());
			unsafe.putFloat((Object)null, long1 + 24L, matrix4f.m21());
			unsafe.putFloat((Object)null, long1 + 28L, matrix4f.m31());
			unsafe.putFloat((Object)null, long1 + 32L, matrix4f.m02());
			unsafe.putFloat((Object)null, long1 + 36L, matrix4f.m12());
			unsafe.putFloat((Object)null, long1 + 40L, matrix4f.m22());
			unsafe.putFloat((Object)null, long1 + 44L, matrix4f.m32());
			unsafe.putFloat((Object)null, long1 + 48L, matrix4f.m03());
			unsafe.putFloat((Object)null, long1 + 52L, matrix4f.m13());
			unsafe.putFloat((Object)null, long1 + 56L, matrix4f.m23());
			unsafe.putFloat((Object)null, long1 + 60L, matrix4f.m33());
		}

		public static void put4x3Transposed(Matrix4f matrix4f, long long1) {
			Unsafe unsafe = UNSAFE;
			unsafe.putFloat((Object)null, long1, matrix4f.m00());
			unsafe.putFloat((Object)null, long1 + 4L, matrix4f.m10());
			unsafe.putFloat((Object)null, long1 + 8L, matrix4f.m20());
			unsafe.putFloat((Object)null, long1 + 12L, matrix4f.m30());
			unsafe.putFloat((Object)null, long1 + 16L, matrix4f.m01());
			unsafe.putFloat((Object)null, long1 + 20L, matrix4f.m11());
			unsafe.putFloat((Object)null, long1 + 24L, matrix4f.m21());
			unsafe.putFloat((Object)null, long1 + 28L, matrix4f.m31());
			unsafe.putFloat((Object)null, long1 + 32L, matrix4f.m02());
			unsafe.putFloat((Object)null, long1 + 36L, matrix4f.m12());
			unsafe.putFloat((Object)null, long1 + 40L, matrix4f.m22());
			unsafe.putFloat((Object)null, long1 + 44L, matrix4f.m32());
		}

		public static void putTransposed(Matrix4x3f matrix4x3f, long long1) {
			Unsafe unsafe = UNSAFE;
			unsafe.putFloat((Object)null, long1, matrix4x3f.m00());
			unsafe.putFloat((Object)null, long1 + 4L, matrix4x3f.m10());
			unsafe.putFloat((Object)null, long1 + 8L, matrix4x3f.m20());
			unsafe.putFloat((Object)null, long1 + 12L, matrix4x3f.m30());
			unsafe.putFloat((Object)null, long1 + 16L, matrix4x3f.m01());
			unsafe.putFloat((Object)null, long1 + 20L, matrix4x3f.m11());
			unsafe.putFloat((Object)null, long1 + 24L, matrix4x3f.m21());
			unsafe.putFloat((Object)null, long1 + 28L, matrix4x3f.m31());
			unsafe.putFloat((Object)null, long1 + 32L, matrix4x3f.m02());
			unsafe.putFloat((Object)null, long1 + 36L, matrix4x3f.m12());
			unsafe.putFloat((Object)null, long1 + 40L, matrix4x3f.m22());
			unsafe.putFloat((Object)null, long1 + 44L, matrix4x3f.m32());
		}

		public static void putTransposed(Matrix3f matrix3f, long long1) {
			Unsafe unsafe = UNSAFE;
			unsafe.putFloat((Object)null, long1, matrix3f.m00());
			unsafe.putFloat((Object)null, long1 + 4L, matrix3f.m10());
			unsafe.putFloat((Object)null, long1 + 8L, matrix3f.m20());
			unsafe.putFloat((Object)null, long1 + 12L, matrix3f.m01());
			unsafe.putFloat((Object)null, long1 + 16L, matrix3f.m11());
			unsafe.putFloat((Object)null, long1 + 20L, matrix3f.m21());
			unsafe.putFloat((Object)null, long1 + 24L, matrix3f.m02());
			unsafe.putFloat((Object)null, long1 + 28L, matrix3f.m12());
			unsafe.putFloat((Object)null, long1 + 32L, matrix3f.m22());
		}

		public static void putTransposed(Matrix2f matrix2f, long long1) {
			UNSAFE.putFloat((Object)null, long1, matrix2f.m00());
			UNSAFE.putFloat((Object)null, long1 + 4L, matrix2f.m10());
			UNSAFE.putFloat((Object)null, long1 + 8L, matrix2f.m01());
			UNSAFE.putFloat((Object)null, long1 + 12L, matrix2f.m11());
		}

		public static void put(Matrix4d matrix4d, long long1) {
			Unsafe unsafe = UNSAFE;
			unsafe.putDouble((Object)null, long1, matrix4d.m00());
			unsafe.putDouble((Object)null, long1 + 8L, matrix4d.m01());
			unsafe.putDouble((Object)null, long1 + 16L, matrix4d.m02());
			unsafe.putDouble((Object)null, long1 + 24L, matrix4d.m03());
			unsafe.putDouble((Object)null, long1 + 32L, matrix4d.m10());
			unsafe.putDouble((Object)null, long1 + 40L, matrix4d.m11());
			unsafe.putDouble((Object)null, long1 + 48L, matrix4d.m12());
			unsafe.putDouble((Object)null, long1 + 56L, matrix4d.m13());
			unsafe.putDouble((Object)null, long1 + 64L, matrix4d.m20());
			unsafe.putDouble((Object)null, long1 + 72L, matrix4d.m21());
			unsafe.putDouble((Object)null, long1 + 80L, matrix4d.m22());
			unsafe.putDouble((Object)null, long1 + 88L, matrix4d.m23());
			unsafe.putDouble((Object)null, long1 + 96L, matrix4d.m30());
			unsafe.putDouble((Object)null, long1 + 104L, matrix4d.m31());
			unsafe.putDouble((Object)null, long1 + 112L, matrix4d.m32());
			unsafe.putDouble((Object)null, long1 + 120L, matrix4d.m33());
		}

		public static void put(Matrix4x3d matrix4x3d, long long1) {
			Unsafe unsafe = UNSAFE;
			unsafe.putDouble((Object)null, long1, matrix4x3d.m00());
			unsafe.putDouble((Object)null, long1 + 8L, matrix4x3d.m01());
			unsafe.putDouble((Object)null, long1 + 16L, matrix4x3d.m02());
			unsafe.putDouble((Object)null, long1 + 24L, matrix4x3d.m10());
			unsafe.putDouble((Object)null, long1 + 32L, matrix4x3d.m11());
			unsafe.putDouble((Object)null, long1 + 40L, matrix4x3d.m12());
			unsafe.putDouble((Object)null, long1 + 48L, matrix4x3d.m20());
			unsafe.putDouble((Object)null, long1 + 56L, matrix4x3d.m21());
			unsafe.putDouble((Object)null, long1 + 64L, matrix4x3d.m22());
			unsafe.putDouble((Object)null, long1 + 72L, matrix4x3d.m30());
			unsafe.putDouble((Object)null, long1 + 80L, matrix4x3d.m31());
			unsafe.putDouble((Object)null, long1 + 88L, matrix4x3d.m32());
		}

		public static void putTransposed(Matrix4d matrix4d, long long1) {
			Unsafe unsafe = UNSAFE;
			unsafe.putDouble((Object)null, long1, matrix4d.m00());
			unsafe.putDouble((Object)null, long1 + 8L, matrix4d.m10());
			unsafe.putDouble((Object)null, long1 + 16L, matrix4d.m20());
			unsafe.putDouble((Object)null, long1 + 24L, matrix4d.m30());
			unsafe.putDouble((Object)null, long1 + 32L, matrix4d.m01());
			unsafe.putDouble((Object)null, long1 + 40L, matrix4d.m11());
			unsafe.putDouble((Object)null, long1 + 48L, matrix4d.m21());
			unsafe.putDouble((Object)null, long1 + 56L, matrix4d.m31());
			unsafe.putDouble((Object)null, long1 + 64L, matrix4d.m02());
			unsafe.putDouble((Object)null, long1 + 72L, matrix4d.m12());
			unsafe.putDouble((Object)null, long1 + 80L, matrix4d.m22());
			unsafe.putDouble((Object)null, long1 + 88L, matrix4d.m32());
			unsafe.putDouble((Object)null, long1 + 96L, matrix4d.m03());
			unsafe.putDouble((Object)null, long1 + 104L, matrix4d.m13());
			unsafe.putDouble((Object)null, long1 + 112L, matrix4d.m23());
			unsafe.putDouble((Object)null, long1 + 120L, matrix4d.m33());
		}

		public static void putfTransposed(Matrix4d matrix4d, long long1) {
			Unsafe unsafe = UNSAFE;
			unsafe.putFloat((Object)null, long1, (float)matrix4d.m00());
			unsafe.putFloat((Object)null, long1 + 4L, (float)matrix4d.m10());
			unsafe.putFloat((Object)null, long1 + 8L, (float)matrix4d.m20());
			unsafe.putFloat((Object)null, long1 + 12L, (float)matrix4d.m30());
			unsafe.putFloat((Object)null, long1 + 16L, (float)matrix4d.m01());
			unsafe.putFloat((Object)null, long1 + 20L, (float)matrix4d.m11());
			unsafe.putFloat((Object)null, long1 + 24L, (float)matrix4d.m21());
			unsafe.putFloat((Object)null, long1 + 28L, (float)matrix4d.m31());
			unsafe.putFloat((Object)null, long1 + 32L, (float)matrix4d.m02());
			unsafe.putFloat((Object)null, long1 + 36L, (float)matrix4d.m12());
			unsafe.putFloat((Object)null, long1 + 40L, (float)matrix4d.m22());
			unsafe.putFloat((Object)null, long1 + 44L, (float)matrix4d.m32());
			unsafe.putFloat((Object)null, long1 + 48L, (float)matrix4d.m03());
			unsafe.putFloat((Object)null, long1 + 52L, (float)matrix4d.m13());
			unsafe.putFloat((Object)null, long1 + 56L, (float)matrix4d.m23());
			unsafe.putFloat((Object)null, long1 + 60L, (float)matrix4d.m33());
		}

		public static void put4x3Transposed(Matrix4d matrix4d, long long1) {
			Unsafe unsafe = UNSAFE;
			unsafe.putDouble((Object)null, long1, matrix4d.m00());
			unsafe.putDouble((Object)null, long1 + 8L, matrix4d.m10());
			unsafe.putDouble((Object)null, long1 + 16L, matrix4d.m20());
			unsafe.putDouble((Object)null, long1 + 24L, matrix4d.m30());
			unsafe.putDouble((Object)null, long1 + 32L, matrix4d.m01());
			unsafe.putDouble((Object)null, long1 + 40L, matrix4d.m11());
			unsafe.putDouble((Object)null, long1 + 48L, matrix4d.m21());
			unsafe.putDouble((Object)null, long1 + 56L, matrix4d.m31());
			unsafe.putDouble((Object)null, long1 + 64L, matrix4d.m02());
			unsafe.putDouble((Object)null, long1 + 72L, matrix4d.m12());
			unsafe.putDouble((Object)null, long1 + 80L, matrix4d.m22());
			unsafe.putDouble((Object)null, long1 + 88L, matrix4d.m32());
		}

		public static void putTransposed(Matrix4x3d matrix4x3d, long long1) {
			Unsafe unsafe = UNSAFE;
			unsafe.putDouble((Object)null, long1, matrix4x3d.m00());
			unsafe.putDouble((Object)null, long1 + 8L, matrix4x3d.m10());
			unsafe.putDouble((Object)null, long1 + 16L, matrix4x3d.m20());
			unsafe.putDouble((Object)null, long1 + 24L, matrix4x3d.m30());
			unsafe.putDouble((Object)null, long1 + 32L, matrix4x3d.m01());
			unsafe.putDouble((Object)null, long1 + 40L, matrix4x3d.m11());
			unsafe.putDouble((Object)null, long1 + 48L, matrix4x3d.m21());
			unsafe.putDouble((Object)null, long1 + 56L, matrix4x3d.m31());
			unsafe.putDouble((Object)null, long1 + 64L, matrix4x3d.m02());
			unsafe.putDouble((Object)null, long1 + 72L, matrix4x3d.m12());
			unsafe.putDouble((Object)null, long1 + 80L, matrix4x3d.m22());
			unsafe.putDouble((Object)null, long1 + 88L, matrix4x3d.m32());
		}

		public static void putTransposed(Matrix2d matrix2d, long long1) {
			UNSAFE.putDouble((Object)null, long1, matrix2d.m00());
			UNSAFE.putDouble((Object)null, long1 + 8L, matrix2d.m10());
			UNSAFE.putDouble((Object)null, long1 + 16L, matrix2d.m10());
			UNSAFE.putDouble((Object)null, long1 + 24L, matrix2d.m10());
		}

		public static void putfTransposed(Matrix4x3d matrix4x3d, long long1) {
			Unsafe unsafe = UNSAFE;
			unsafe.putFloat((Object)null, long1, (float)matrix4x3d.m00());
			unsafe.putFloat((Object)null, long1 + 4L, (float)matrix4x3d.m10());
			unsafe.putFloat((Object)null, long1 + 8L, (float)matrix4x3d.m20());
			unsafe.putFloat((Object)null, long1 + 12L, (float)matrix4x3d.m30());
			unsafe.putFloat((Object)null, long1 + 16L, (float)matrix4x3d.m01());
			unsafe.putFloat((Object)null, long1 + 20L, (float)matrix4x3d.m11());
			unsafe.putFloat((Object)null, long1 + 24L, (float)matrix4x3d.m21());
			unsafe.putFloat((Object)null, long1 + 28L, (float)matrix4x3d.m31());
			unsafe.putFloat((Object)null, long1 + 32L, (float)matrix4x3d.m02());
			unsafe.putFloat((Object)null, long1 + 36L, (float)matrix4x3d.m12());
			unsafe.putFloat((Object)null, long1 + 40L, (float)matrix4x3d.m22());
			unsafe.putFloat((Object)null, long1 + 44L, (float)matrix4x3d.m32());
		}

		public static void putfTransposed(Matrix2d matrix2d, long long1) {
			UNSAFE.putFloat((Object)null, long1, (float)matrix2d.m00());
			UNSAFE.putFloat((Object)null, long1 + 4L, (float)matrix2d.m00());
			UNSAFE.putFloat((Object)null, long1 + 8L, (float)matrix2d.m00());
			UNSAFE.putFloat((Object)null, long1 + 12L, (float)matrix2d.m00());
		}

		public static void putf(Matrix4d matrix4d, long long1) {
			Unsafe unsafe = UNSAFE;
			unsafe.putFloat((Object)null, long1, (float)matrix4d.m00());
			unsafe.putFloat((Object)null, long1 + 4L, (float)matrix4d.m01());
			unsafe.putFloat((Object)null, long1 + 8L, (float)matrix4d.m02());
			unsafe.putFloat((Object)null, long1 + 12L, (float)matrix4d.m03());
			unsafe.putFloat((Object)null, long1 + 16L, (float)matrix4d.m10());
			unsafe.putFloat((Object)null, long1 + 20L, (float)matrix4d.m11());
			unsafe.putFloat((Object)null, long1 + 24L, (float)matrix4d.m12());
			unsafe.putFloat((Object)null, long1 + 28L, (float)matrix4d.m13());
			unsafe.putFloat((Object)null, long1 + 32L, (float)matrix4d.m20());
			unsafe.putFloat((Object)null, long1 + 36L, (float)matrix4d.m21());
			unsafe.putFloat((Object)null, long1 + 40L, (float)matrix4d.m22());
			unsafe.putFloat((Object)null, long1 + 44L, (float)matrix4d.m23());
			unsafe.putFloat((Object)null, long1 + 48L, (float)matrix4d.m30());
			unsafe.putFloat((Object)null, long1 + 52L, (float)matrix4d.m31());
			unsafe.putFloat((Object)null, long1 + 56L, (float)matrix4d.m32());
			unsafe.putFloat((Object)null, long1 + 60L, (float)matrix4d.m33());
		}

		public static void putf(Matrix4x3d matrix4x3d, long long1) {
			Unsafe unsafe = UNSAFE;
			unsafe.putFloat((Object)null, long1, (float)matrix4x3d.m00());
			unsafe.putFloat((Object)null, long1 + 4L, (float)matrix4x3d.m01());
			unsafe.putFloat((Object)null, long1 + 8L, (float)matrix4x3d.m02());
			unsafe.putFloat((Object)null, long1 + 12L, (float)matrix4x3d.m10());
			unsafe.putFloat((Object)null, long1 + 16L, (float)matrix4x3d.m11());
			unsafe.putFloat((Object)null, long1 + 20L, (float)matrix4x3d.m12());
			unsafe.putFloat((Object)null, long1 + 24L, (float)matrix4x3d.m20());
			unsafe.putFloat((Object)null, long1 + 28L, (float)matrix4x3d.m21());
			unsafe.putFloat((Object)null, long1 + 32L, (float)matrix4x3d.m22());
			unsafe.putFloat((Object)null, long1 + 36L, (float)matrix4x3d.m30());
			unsafe.putFloat((Object)null, long1 + 40L, (float)matrix4x3d.m31());
			unsafe.putFloat((Object)null, long1 + 44L, (float)matrix4x3d.m32());
		}

		public static void put(Matrix3f matrix3f, long long1) {
			for (int int1 = 0; int1 < 4; ++int1) {
				UNSAFE.putLong((Object)null, long1 + (long)(int1 << 3), UNSAFE.getLong(matrix3f, Matrix3f_m00 + (long)(int1 << 3)));
			}

			UNSAFE.putFloat((Object)null, long1 + 32L, matrix3f.m22());
		}

		public static void put3x4(Matrix3f matrix3f, long long1) {
			for (int int1 = 0; int1 < 3; ++int1) {
				UNSAFE.putLong((Object)null, long1 + (long)(int1 << 4), UNSAFE.getLong(matrix3f, Matrix3f_m00 + (long)(12 * int1)));
				UNSAFE.putFloat((Object)null, long1 + (long)(int1 << 4) + 8L, UNSAFE.getFloat(matrix3f, Matrix3f_m00 + 8L + (long)(12 * int1)));
				UNSAFE.putFloat((Object)null, long1 + (long)(12 * int1), 0.0F);
			}
		}

		public static void put(Matrix3d matrix3d, long long1) {
			Unsafe unsafe = UNSAFE;
			unsafe.putDouble((Object)null, long1, matrix3d.m00());
			unsafe.putDouble((Object)null, long1 + 8L, matrix3d.m01());
			unsafe.putDouble((Object)null, long1 + 16L, matrix3d.m02());
			unsafe.putDouble((Object)null, long1 + 24L, matrix3d.m10());
			unsafe.putDouble((Object)null, long1 + 32L, matrix3d.m11());
			unsafe.putDouble((Object)null, long1 + 40L, matrix3d.m12());
			unsafe.putDouble((Object)null, long1 + 48L, matrix3d.m20());
			unsafe.putDouble((Object)null, long1 + 56L, matrix3d.m21());
			unsafe.putDouble((Object)null, long1 + 64L, matrix3d.m22());
		}

		public static void put(Matrix3x2f matrix3x2f, long long1) {
			for (int int1 = 0; int1 < 3; ++int1) {
				UNSAFE.putLong((Object)null, long1 + (long)(int1 << 3), UNSAFE.getLong(matrix3x2f, Matrix3x2f_m00 + (long)(int1 << 3)));
			}
		}

		public static void put(Matrix3x2d matrix3x2d, long long1) {
			Unsafe unsafe = UNSAFE;
			unsafe.putDouble((Object)null, long1, matrix3x2d.m00());
			unsafe.putDouble((Object)null, long1 + 8L, matrix3x2d.m01());
			unsafe.putDouble((Object)null, long1 + 16L, matrix3x2d.m10());
			unsafe.putDouble((Object)null, long1 + 24L, matrix3x2d.m11());
			unsafe.putDouble((Object)null, long1 + 32L, matrix3x2d.m20());
			unsafe.putDouble((Object)null, long1 + 40L, matrix3x2d.m21());
		}

		public static void putf(Matrix3d matrix3d, long long1) {
			Unsafe unsafe = UNSAFE;
			unsafe.putFloat((Object)null, long1, (float)matrix3d.m00());
			unsafe.putFloat((Object)null, long1 + 4L, (float)matrix3d.m01());
			unsafe.putFloat((Object)null, long1 + 8L, (float)matrix3d.m02());
			unsafe.putFloat((Object)null, long1 + 12L, (float)matrix3d.m10());
			unsafe.putFloat((Object)null, long1 + 16L, (float)matrix3d.m11());
			unsafe.putFloat((Object)null, long1 + 20L, (float)matrix3d.m12());
			unsafe.putFloat((Object)null, long1 + 24L, (float)matrix3d.m20());
			unsafe.putFloat((Object)null, long1 + 28L, (float)matrix3d.m21());
			unsafe.putFloat((Object)null, long1 + 32L, (float)matrix3d.m22());
		}

		public static void put(Matrix2f matrix2f, long long1) {
			UNSAFE.putLong((Object)null, long1, UNSAFE.getLong(matrix2f, Matrix2f_m00));
			UNSAFE.putLong((Object)null, long1 + 8L, UNSAFE.getLong(matrix2f, Matrix2f_m00 + 8L));
		}

		public static void put(Matrix2d matrix2d, long long1) {
			UNSAFE.putDouble((Object)null, long1, matrix2d.m00());
			UNSAFE.putDouble((Object)null, long1 + 8L, matrix2d.m01());
			UNSAFE.putDouble((Object)null, long1 + 16L, matrix2d.m10());
			UNSAFE.putDouble((Object)null, long1 + 24L, matrix2d.m11());
		}

		public static void putf(Matrix2d matrix2d, long long1) {
			UNSAFE.putFloat((Object)null, long1, (float)matrix2d.m00());
			UNSAFE.putFloat((Object)null, long1 + 4L, (float)matrix2d.m01());
			UNSAFE.putFloat((Object)null, long1 + 8L, (float)matrix2d.m10());
			UNSAFE.putFloat((Object)null, long1 + 12L, (float)matrix2d.m11());
		}

		public static void put(Vector4d vector4d, long long1) {
			UNSAFE.putDouble((Object)null, long1, vector4d.x);
			UNSAFE.putDouble((Object)null, long1 + 8L, vector4d.y);
			UNSAFE.putDouble((Object)null, long1 + 16L, vector4d.z);
			UNSAFE.putDouble((Object)null, long1 + 24L, vector4d.w);
		}

		public static void putf(Vector4d vector4d, long long1) {
			UNSAFE.putFloat((Object)null, long1, (float)vector4d.x);
			UNSAFE.putFloat((Object)null, long1 + 4L, (float)vector4d.y);
			UNSAFE.putFloat((Object)null, long1 + 8L, (float)vector4d.z);
			UNSAFE.putFloat((Object)null, long1 + 12L, (float)vector4d.w);
		}

		public static void put(Vector4f vector4f, long long1) {
			UNSAFE.putLong((Object)null, long1, UNSAFE.getLong(vector4f, Vector4f_x));
			UNSAFE.putLong((Object)null, long1 + 8L, UNSAFE.getLong(vector4f, Vector4f_x + 8L));
		}

		public static void put(Vector4i vector4i, long long1) {
			UNSAFE.putLong((Object)null, long1, UNSAFE.getLong(vector4i, Vector4i_x));
			UNSAFE.putLong((Object)null, long1 + 8L, UNSAFE.getLong(vector4i, Vector4i_x + 8L));
		}

		public static void put(Vector3f vector3f, long long1) {
			UNSAFE.putLong((Object)null, long1, UNSAFE.getLong(vector3f, Vector3f_x));
			UNSAFE.putFloat((Object)null, long1 + 8L, vector3f.z);
		}

		public static void put(Vector3d vector3d, long long1) {
			UNSAFE.putDouble((Object)null, long1, vector3d.x);
			UNSAFE.putDouble((Object)null, long1 + 8L, vector3d.y);
			UNSAFE.putDouble((Object)null, long1 + 16L, vector3d.z);
		}

		public static void putf(Vector3d vector3d, long long1) {
			UNSAFE.putFloat((Object)null, long1, (float)vector3d.x);
			UNSAFE.putFloat((Object)null, long1 + 4L, (float)vector3d.y);
			UNSAFE.putFloat((Object)null, long1 + 8L, (float)vector3d.z);
		}

		public static void put(Vector3i vector3i, long long1) {
			UNSAFE.putLong((Object)null, long1, UNSAFE.getLong(vector3i, Vector3i_x));
			UNSAFE.putInt((Object)null, long1 + 8L, vector3i.z);
		}

		public static void put(Vector2f vector2f, long long1) {
			UNSAFE.putLong((Object)null, long1, UNSAFE.getLong(vector2f, Vector2f_x));
		}

		public static void put(Vector2d vector2d, long long1) {
			UNSAFE.putDouble((Object)null, long1, vector2d.x);
			UNSAFE.putDouble((Object)null, long1 + 8L, vector2d.y);
		}

		public static void put(Vector2i vector2i, long long1) {
			UNSAFE.putLong((Object)null, long1, UNSAFE.getLong(vector2i, Vector2i_x));
		}

		public static void get(Matrix4f matrix4f, long long1) {
			for (int int1 = 0; int1 < 8; ++int1) {
				UNSAFE.putLong(matrix4f, Matrix4f_m00 + (long)(int1 << 3), UNSAFE.getLong(long1 + (long)(int1 << 3)));
			}
		}

		public static void getTransposed(Matrix4f matrix4f, long long1) {
			matrix4f._m00(UNSAFE.getFloat(long1))._m10(UNSAFE.getFloat(long1 + 4L))._m20(UNSAFE.getFloat(long1 + 8L))._m30(UNSAFE.getFloat(long1 + 12L))._m01(UNSAFE.getFloat(long1 + 16L))._m11(UNSAFE.getFloat(long1 + 20L))._m21(UNSAFE.getFloat(long1 + 24L))._m31(UNSAFE.getFloat(long1 + 28L))._m02(UNSAFE.getFloat(long1 + 32L))._m12(UNSAFE.getFloat(long1 + 36L))._m22(UNSAFE.getFloat(long1 + 40L))._m32(UNSAFE.getFloat(long1 + 44L))._m03(UNSAFE.getFloat(long1 + 48L))._m13(UNSAFE.getFloat(long1 + 52L))._m23(UNSAFE.getFloat(long1 + 56L))._m33(UNSAFE.getFloat(long1 + 60L));
		}

		public static void get(Matrix4x3f matrix4x3f, long long1) {
			for (int int1 = 0; int1 < 6; ++int1) {
				UNSAFE.putLong(matrix4x3f, Matrix4x3f_m00 + (long)(int1 << 3), UNSAFE.getLong(long1 + (long)(int1 << 3)));
			}
		}

		public static void get(Matrix4d matrix4d, long long1) {
			Unsafe unsafe = UNSAFE;
			matrix4d._m00(unsafe.getDouble((Object)null, long1))._m01(unsafe.getDouble((Object)null, long1 + 8L))._m02(unsafe.getDouble((Object)null, long1 + 16L))._m03(unsafe.getDouble((Object)null, long1 + 24L))._m10(unsafe.getDouble((Object)null, long1 + 32L))._m11(unsafe.getDouble((Object)null, long1 + 40L))._m12(unsafe.getDouble((Object)null, long1 + 48L))._m13(unsafe.getDouble((Object)null, long1 + 56L))._m20(unsafe.getDouble((Object)null, long1 + 64L))._m21(unsafe.getDouble((Object)null, long1 + 72L))._m22(unsafe.getDouble((Object)null, long1 + 80L))._m23(unsafe.getDouble((Object)null, long1 + 88L))._m30(unsafe.getDouble((Object)null, long1 + 96L))._m31(unsafe.getDouble((Object)null, long1 + 104L))._m32(unsafe.getDouble((Object)null, long1 + 112L))._m33(unsafe.getDouble((Object)null, long1 + 120L));
		}

		public static void get(Matrix4x3d matrix4x3d, long long1) {
			Unsafe unsafe = UNSAFE;
			matrix4x3d._m00(unsafe.getDouble((Object)null, long1))._m01(unsafe.getDouble((Object)null, long1 + 8L))._m02(unsafe.getDouble((Object)null, long1 + 16L))._m10(unsafe.getDouble((Object)null, long1 + 24L))._m11(unsafe.getDouble((Object)null, long1 + 32L))._m12(unsafe.getDouble((Object)null, long1 + 40L))._m20(unsafe.getDouble((Object)null, long1 + 48L))._m21(unsafe.getDouble((Object)null, long1 + 56L))._m22(unsafe.getDouble((Object)null, long1 + 64L))._m30(unsafe.getDouble((Object)null, long1 + 72L))._m31(unsafe.getDouble((Object)null, long1 + 80L))._m32(unsafe.getDouble((Object)null, long1 + 88L));
		}

		public static void getf(Matrix4d matrix4d, long long1) {
			Unsafe unsafe = UNSAFE;
			matrix4d._m00((double)unsafe.getFloat((Object)null, long1))._m01((double)unsafe.getFloat((Object)null, long1 + 4L))._m02((double)unsafe.getFloat((Object)null, long1 + 8L))._m03((double)unsafe.getFloat((Object)null, long1 + 12L))._m10((double)unsafe.getFloat((Object)null, long1 + 16L))._m11((double)unsafe.getFloat((Object)null, long1 + 20L))._m12((double)unsafe.getFloat((Object)null, long1 + 24L))._m13((double)unsafe.getFloat((Object)null, long1 + 28L))._m20((double)unsafe.getFloat((Object)null, long1 + 32L))._m21((double)unsafe.getFloat((Object)null, long1 + 36L))._m22((double)unsafe.getFloat((Object)null, long1 + 40L))._m23((double)unsafe.getFloat((Object)null, long1 + 44L))._m30((double)unsafe.getFloat((Object)null, long1 + 48L))._m31((double)unsafe.getFloat((Object)null, long1 + 52L))._m32((double)unsafe.getFloat((Object)null, long1 + 56L))._m33((double)unsafe.getFloat((Object)null, long1 + 60L));
		}

		public static void getf(Matrix4x3d matrix4x3d, long long1) {
			Unsafe unsafe = UNSAFE;
			matrix4x3d._m00((double)unsafe.getFloat((Object)null, long1))._m01((double)unsafe.getFloat((Object)null, long1 + 4L))._m02((double)unsafe.getFloat((Object)null, long1 + 8L))._m10((double)unsafe.getFloat((Object)null, long1 + 12L))._m11((double)unsafe.getFloat((Object)null, long1 + 16L))._m12((double)unsafe.getFloat((Object)null, long1 + 20L))._m20((double)unsafe.getFloat((Object)null, long1 + 24L))._m21((double)unsafe.getFloat((Object)null, long1 + 28L))._m22((double)unsafe.getFloat((Object)null, long1 + 32L))._m30((double)unsafe.getFloat((Object)null, long1 + 36L))._m31((double)unsafe.getFloat((Object)null, long1 + 40L))._m32((double)unsafe.getFloat((Object)null, long1 + 44L));
		}

		public static void get(Matrix3f matrix3f, long long1) {
			for (int int1 = 0; int1 < 4; ++int1) {
				UNSAFE.putLong(matrix3f, Matrix3f_m00 + (long)(int1 << 3), UNSAFE.getLong((Object)null, long1 + (long)(int1 << 3)));
			}

			matrix3f._m22(UNSAFE.getFloat((Object)null, long1 + 32L));
		}

		public static void get(Matrix3d matrix3d, long long1) {
			Unsafe unsafe = UNSAFE;
			matrix3d._m00(unsafe.getDouble((Object)null, long1))._m01(unsafe.getDouble((Object)null, long1 + 8L))._m02(unsafe.getDouble((Object)null, long1 + 16L))._m10(unsafe.getDouble((Object)null, long1 + 24L))._m11(unsafe.getDouble((Object)null, long1 + 32L))._m12(unsafe.getDouble((Object)null, long1 + 40L))._m20(unsafe.getDouble((Object)null, long1 + 48L))._m21(unsafe.getDouble((Object)null, long1 + 56L))._m22(unsafe.getDouble((Object)null, long1 + 64L));
		}

		public static void get(Matrix3x2f matrix3x2f, long long1) {
			for (int int1 = 0; int1 < 3; ++int1) {
				UNSAFE.putLong(matrix3x2f, Matrix3x2f_m00 + (long)(int1 << 3), UNSAFE.getLong((Object)null, long1 + (long)(int1 << 3)));
			}
		}

		public static void get(Matrix3x2d matrix3x2d, long long1) {
			Unsafe unsafe = UNSAFE;
			matrix3x2d._m00(unsafe.getDouble((Object)null, long1))._m01(unsafe.getDouble((Object)null, long1 + 8L))._m10(unsafe.getDouble((Object)null, long1 + 16L))._m11(unsafe.getDouble((Object)null, long1 + 24L))._m20(unsafe.getDouble((Object)null, long1 + 32L))._m21(unsafe.getDouble((Object)null, long1 + 40L));
		}

		public static void getf(Matrix3d matrix3d, long long1) {
			Unsafe unsafe = UNSAFE;
			matrix3d._m00((double)unsafe.getFloat((Object)null, long1))._m01((double)unsafe.getFloat((Object)null, long1 + 4L))._m02((double)unsafe.getFloat((Object)null, long1 + 8L))._m10((double)unsafe.getFloat((Object)null, long1 + 12L))._m11((double)unsafe.getFloat((Object)null, long1 + 16L))._m12((double)unsafe.getFloat((Object)null, long1 + 20L))._m20((double)unsafe.getFloat((Object)null, long1 + 24L))._m21((double)unsafe.getFloat((Object)null, long1 + 28L))._m22((double)unsafe.getFloat((Object)null, long1 + 32L));
		}

		public static void get(Matrix2f matrix2f, long long1) {
			UNSAFE.putLong(matrix2f, Matrix2f_m00, UNSAFE.getLong((Object)null, long1));
			UNSAFE.putLong(matrix2f, Matrix2f_m00 + 8L, UNSAFE.getLong((Object)null, long1 + 8L));
		}

		public static void get(Matrix2d matrix2d, long long1) {
			matrix2d._m00(UNSAFE.getDouble((Object)null, long1))._m01(UNSAFE.getDouble((Object)null, long1 + 8L))._m10(UNSAFE.getDouble((Object)null, long1 + 16L))._m11(UNSAFE.getDouble((Object)null, long1 + 24L));
		}

		public static void getf(Matrix2d matrix2d, long long1) {
			matrix2d._m00((double)UNSAFE.getFloat((Object)null, long1))._m01((double)UNSAFE.getFloat((Object)null, long1 + 4L))._m10((double)UNSAFE.getFloat((Object)null, long1 + 8L))._m11((double)UNSAFE.getFloat((Object)null, long1 + 12L));
		}

		public static void get(Vector4d vector4d, long long1) {
			vector4d.x = UNSAFE.getDouble((Object)null, long1);
			vector4d.y = UNSAFE.getDouble((Object)null, long1 + 8L);
			vector4d.z = UNSAFE.getDouble((Object)null, long1 + 16L);
			vector4d.w = UNSAFE.getDouble((Object)null, long1 + 24L);
		}

		public static void get(Vector4f vector4f, long long1) {
			vector4f.x = UNSAFE.getFloat((Object)null, long1);
			vector4f.y = UNSAFE.getFloat((Object)null, long1 + 4L);
			vector4f.z = UNSAFE.getFloat((Object)null, long1 + 8L);
			vector4f.w = UNSAFE.getFloat((Object)null, long1 + 12L);
		}

		public static void get(Vector4i vector4i, long long1) {
			vector4i.x = UNSAFE.getInt((Object)null, long1);
			vector4i.y = UNSAFE.getInt((Object)null, long1 + 4L);
			vector4i.z = UNSAFE.getInt((Object)null, long1 + 8L);
			vector4i.w = UNSAFE.getInt((Object)null, long1 + 12L);
		}

		public static void get(Vector3f vector3f, long long1) {
			vector3f.x = UNSAFE.getFloat((Object)null, long1);
			vector3f.y = UNSAFE.getFloat((Object)null, long1 + 4L);
			vector3f.z = UNSAFE.getFloat((Object)null, long1 + 8L);
		}

		public static void get(Vector3d vector3d, long long1) {
			vector3d.x = UNSAFE.getDouble((Object)null, long1);
			vector3d.y = UNSAFE.getDouble((Object)null, long1 + 8L);
			vector3d.z = UNSAFE.getDouble((Object)null, long1 + 16L);
		}

		public static void get(Vector3i vector3i, long long1) {
			vector3i.x = UNSAFE.getInt((Object)null, long1);
			vector3i.y = UNSAFE.getInt((Object)null, long1 + 4L);
			vector3i.z = UNSAFE.getInt((Object)null, long1 + 8L);
		}

		public static void get(Vector2f vector2f, long long1) {
			vector2f.x = UNSAFE.getFloat((Object)null, long1);
			vector2f.y = UNSAFE.getFloat((Object)null, long1 + 4L);
		}

		public static void get(Vector2d vector2d, long long1) {
			vector2d.x = UNSAFE.getDouble((Object)null, long1);
			vector2d.y = UNSAFE.getDouble((Object)null, long1 + 8L);
		}

		public static void get(Vector2i vector2i, long long1) {
			vector2i.x = UNSAFE.getInt((Object)null, long1);
			vector2i.y = UNSAFE.getInt((Object)null, long1 + 4L);
		}

		public static void putMatrix3f(Quaternionf quaternionf, long long1) {
			float float1 = quaternionf.x + quaternionf.x;
			float float2 = quaternionf.y + quaternionf.y;
			float float3 = quaternionf.z + quaternionf.z;
			float float4 = float1 * quaternionf.x;
			float float5 = float2 * quaternionf.y;
			float float6 = float3 * quaternionf.z;
			float float7 = float1 * quaternionf.y;
			float float8 = float1 * quaternionf.z;
			float float9 = float1 * quaternionf.w;
			float float10 = float2 * quaternionf.z;
			float float11 = float2 * quaternionf.w;
			float float12 = float3 * quaternionf.w;
			Unsafe unsafe = UNSAFE;
			unsafe.putFloat((Object)null, long1, 1.0F - float5 - float6);
			unsafe.putFloat((Object)null, long1 + 4L, float7 + float12);
			unsafe.putFloat((Object)null, long1 + 8L, float8 - float11);
			unsafe.putFloat((Object)null, long1 + 12L, float7 - float12);
			unsafe.putFloat((Object)null, long1 + 16L, 1.0F - float6 - float4);
			unsafe.putFloat((Object)null, long1 + 20L, float10 + float9);
			unsafe.putFloat((Object)null, long1 + 24L, float8 + float11);
			unsafe.putFloat((Object)null, long1 + 28L, float10 - float9);
			unsafe.putFloat((Object)null, long1 + 32L, 1.0F - float5 - float4);
		}

		public static void putMatrix4f(Quaternionf quaternionf, long long1) {
			float float1 = quaternionf.x + quaternionf.x;
			float float2 = quaternionf.y + quaternionf.y;
			float float3 = quaternionf.z + quaternionf.z;
			float float4 = float1 * quaternionf.x;
			float float5 = float2 * quaternionf.y;
			float float6 = float3 * quaternionf.z;
			float float7 = float1 * quaternionf.y;
			float float8 = float1 * quaternionf.z;
			float float9 = float1 * quaternionf.w;
			float float10 = float2 * quaternionf.z;
			float float11 = float2 * quaternionf.w;
			float float12 = float3 * quaternionf.w;
			Unsafe unsafe = UNSAFE;
			unsafe.putFloat((Object)null, long1, 1.0F - float5 - float6);
			unsafe.putFloat((Object)null, long1 + 4L, float7 + float12);
			unsafe.putLong((Object)null, long1 + 8L, (long)Float.floatToRawIntBits(float8 - float11) & 4294967295L);
			unsafe.putFloat((Object)null, long1 + 16L, float7 - float12);
			unsafe.putFloat((Object)null, long1 + 20L, 1.0F - float6 - float4);
			unsafe.putLong((Object)null, long1 + 24L, (long)Float.floatToRawIntBits(float10 + float9) & 4294967295L);
			unsafe.putFloat((Object)null, long1 + 32L, float8 + float11);
			unsafe.putFloat((Object)null, long1 + 36L, float10 - float9);
			unsafe.putLong((Object)null, long1 + 40L, (long)Float.floatToRawIntBits(1.0F - float5 - float4) & 4294967295L);
			unsafe.putLong((Object)null, long1 + 48L, 0L);
			unsafe.putLong((Object)null, long1 + 56L, 4575657221408423936L);
		}

		public static void putMatrix4x3f(Quaternionf quaternionf, long long1) {
			float float1 = quaternionf.x + quaternionf.x;
			float float2 = quaternionf.y + quaternionf.y;
			float float3 = quaternionf.z + quaternionf.z;
			float float4 = float1 * quaternionf.x;
			float float5 = float2 * quaternionf.y;
			float float6 = float3 * quaternionf.z;
			float float7 = float1 * quaternionf.y;
			float float8 = float1 * quaternionf.z;
			float float9 = float1 * quaternionf.w;
			float float10 = float2 * quaternionf.z;
			float float11 = float2 * quaternionf.w;
			float float12 = float3 * quaternionf.w;
			Unsafe unsafe = UNSAFE;
			unsafe.putFloat((Object)null, long1, 1.0F - float5 - float6);
			unsafe.putFloat((Object)null, long1 + 4L, float7 + float12);
			unsafe.putFloat((Object)null, long1 + 8L, float8 - float11);
			unsafe.putFloat((Object)null, long1 + 12L, float7 - float12);
			unsafe.putFloat((Object)null, long1 + 16L, 1.0F - float6 - float4);
			unsafe.putFloat((Object)null, long1 + 20L, float10 + float9);
			unsafe.putFloat((Object)null, long1 + 24L, float8 + float11);
			unsafe.putFloat((Object)null, long1 + 28L, float10 - float9);
			unsafe.putFloat((Object)null, long1 + 32L, 1.0F - float5 - float4);
			unsafe.putLong((Object)null, long1 + 36L, 0L);
			unsafe.putFloat((Object)null, long1 + 44L, 0.0F);
		}

		private static void throwNoDirectBufferException() {
			throw new IllegalArgumentException("Must use a direct buffer");
		}

		public void putMatrix3f(Quaternionf quaternionf, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 36);
			}

			putMatrix3f(quaternionf, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void putMatrix3f(Quaternionf quaternionf, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 9);
			}

			putMatrix3f(quaternionf, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		private static void checkPut(int int1, boolean boolean1, int int2, int int3) {
			if (!boolean1) {
				throwNoDirectBufferException();
			}

			if (int2 - int1 < int3) {
				throw new BufferOverflowException();
			}
		}

		public void putMatrix4f(Quaternionf quaternionf, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 64);
			}

			putMatrix4f(quaternionf, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void putMatrix4f(Quaternionf quaternionf, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 16);
			}

			putMatrix4f(quaternionf, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void putMatrix4x3f(Quaternionf quaternionf, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 48);
			}

			putMatrix4x3f(quaternionf, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void putMatrix4x3f(Quaternionf quaternionf, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 12);
			}

			putMatrix4x3f(quaternionf, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void put(Matrix4f matrix4f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 16);
			}

			put(matrix4f, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void put(Matrix4f matrix4f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 64);
			}

			put(matrix4f, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void put4x3(Matrix4f matrix4f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 12);
			}

			put4x3(matrix4f, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void put4x3(Matrix4f matrix4f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 48);
			}

			put4x3(matrix4f, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void put3x4(Matrix4f matrix4f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 12);
			}

			put3x4(matrix4f, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void put3x4(Matrix4f matrix4f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 48);
			}

			put3x4(matrix4f, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void put(Matrix4x3f matrix4x3f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 12);
			}

			put(matrix4x3f, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void put(Matrix4x3f matrix4x3f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 48);
			}

			put(matrix4x3f, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void put4x4(Matrix4x3f matrix4x3f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 16);
			}

			put4x4(matrix4x3f, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void put4x4(Matrix4x3f matrix4x3f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 64);
			}

			put4x4(matrix4x3f, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void put3x4(Matrix4x3f matrix4x3f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 12);
			}

			put3x4(matrix4x3f, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void put3x4(Matrix4x3f matrix4x3f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 48);
			}

			put3x4(matrix4x3f, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void put4x4(Matrix4x3d matrix4x3d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, doubleBuffer.isDirect(), doubleBuffer.capacity(), 16);
			}

			put4x4(matrix4x3d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (long)(int1 << 3));
		}

		public void put4x4(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 128);
			}

			put4x4(matrix4x3d, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void put4x4(Matrix3x2f matrix3x2f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 16);
			}

			put4x4(matrix3x2f, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void put4x4(Matrix3x2f matrix3x2f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 64);
			}

			put4x4(matrix3x2f, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void put4x4(Matrix3x2d matrix3x2d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, doubleBuffer.isDirect(), doubleBuffer.capacity(), 16);
			}

			put4x4(matrix3x2d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (long)(int1 << 3));
		}

		public void put4x4(Matrix3x2d matrix3x2d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 128);
			}

			put4x4(matrix3x2d, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void put3x3(Matrix3x2f matrix3x2f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 9);
			}

			put3x3(matrix3x2f, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void put3x3(Matrix3x2f matrix3x2f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 36);
			}

			put3x3(matrix3x2f, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void put3x3(Matrix3x2d matrix3x2d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, doubleBuffer.isDirect(), doubleBuffer.capacity(), 9);
			}

			put3x3(matrix3x2d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (long)(int1 << 3));
		}

		public void put3x3(Matrix3x2d matrix3x2d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 72);
			}

			put3x3(matrix3x2d, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void putTransposed(Matrix4f matrix4f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 16);
			}

			putTransposed(matrix4f, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void putTransposed(Matrix4f matrix4f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 64);
			}

			putTransposed(matrix4f, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void put4x3Transposed(Matrix4f matrix4f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 12);
			}

			put4x3Transposed(matrix4f, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void put4x3Transposed(Matrix4f matrix4f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 48);
			}

			put4x3Transposed(matrix4f, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void putTransposed(Matrix4x3f matrix4x3f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 12);
			}

			putTransposed(matrix4x3f, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void putTransposed(Matrix4x3f matrix4x3f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 48);
			}

			putTransposed(matrix4x3f, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void putTransposed(Matrix3f matrix3f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 9);
			}

			putTransposed(matrix3f, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void putTransposed(Matrix3f matrix3f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 36);
			}

			putTransposed(matrix3f, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void putTransposed(Matrix2f matrix2f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 4);
			}

			putTransposed(matrix2f, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void putTransposed(Matrix2f matrix2f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 16);
			}

			putTransposed(matrix2f, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void put(Matrix4d matrix4d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, doubleBuffer.isDirect(), doubleBuffer.capacity(), 16);
			}

			put(matrix4d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (long)(int1 << 3));
		}

		public void put(Matrix4d matrix4d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 128);
			}

			put(matrix4d, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void put(Matrix4x3d matrix4x3d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, doubleBuffer.isDirect(), doubleBuffer.capacity(), 12);
			}

			put(matrix4x3d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (long)(int1 << 3));
		}

		public void put(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 96);
			}

			put(matrix4x3d, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void putf(Matrix4d matrix4d, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 16);
			}

			putf(matrix4d, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void putf(Matrix4d matrix4d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 64);
			}

			putf(matrix4d, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void putf(Matrix4x3d matrix4x3d, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 12);
			}

			putf(matrix4x3d, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void putf(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 48);
			}

			putf(matrix4x3d, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void putTransposed(Matrix4d matrix4d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, doubleBuffer.isDirect(), doubleBuffer.capacity(), 16);
			}

			putTransposed(matrix4d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (long)(int1 << 3));
		}

		public void putTransposed(Matrix4d matrix4d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 128);
			}

			putTransposed(matrix4d, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void put4x3Transposed(Matrix4d matrix4d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, doubleBuffer.isDirect(), doubleBuffer.capacity(), 12);
			}

			put4x3Transposed(matrix4d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (long)(int1 << 3));
		}

		public void put4x3Transposed(Matrix4d matrix4d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 96);
			}

			put4x3Transposed(matrix4d, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void putTransposed(Matrix4x3d matrix4x3d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, doubleBuffer.isDirect(), doubleBuffer.capacity(), 12);
			}

			putTransposed(matrix4x3d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (long)(int1 << 3));
		}

		public void putTransposed(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 96);
			}

			putTransposed(matrix4x3d, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void putTransposed(Matrix2d matrix2d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, doubleBuffer.isDirect(), doubleBuffer.capacity(), 4);
			}

			putTransposed(matrix2d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (long)(int1 << 3));
		}

		public void putTransposed(Matrix2d matrix2d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 32);
			}

			putTransposed(matrix2d, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void putfTransposed(Matrix4d matrix4d, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 16);
			}

			putfTransposed(matrix4d, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void putfTransposed(Matrix4d matrix4d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 64);
			}

			putfTransposed(matrix4d, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void putfTransposed(Matrix4x3d matrix4x3d, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 12);
			}

			putfTransposed(matrix4x3d, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void putfTransposed(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 48);
			}

			putfTransposed(matrix4x3d, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void putfTransposed(Matrix2d matrix2d, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 4);
			}

			putfTransposed(matrix2d, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void putfTransposed(Matrix2d matrix2d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 16);
			}

			putfTransposed(matrix2d, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void put(Matrix3f matrix3f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 9);
			}

			put(matrix3f, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void put(Matrix3f matrix3f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 36);
			}

			put(matrix3f, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void put3x4(Matrix3f matrix3f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 12);
			}

			put3x4(matrix3f, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void put3x4(Matrix3f matrix3f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 48);
			}

			put3x4(matrix3f, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void put(Matrix3d matrix3d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, doubleBuffer.isDirect(), doubleBuffer.capacity(), 9);
			}

			put(matrix3d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (long)(int1 << 3));
		}

		public void put(Matrix3d matrix3d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 72);
			}

			put(matrix3d, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void put(Matrix3x2f matrix3x2f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 6);
			}

			put(matrix3x2f, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void put(Matrix3x2f matrix3x2f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 24);
			}

			put(matrix3x2f, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void put(Matrix3x2d matrix3x2d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, doubleBuffer.isDirect(), doubleBuffer.capacity(), 6);
			}

			put(matrix3x2d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (long)(int1 << 3));
		}

		public void put(Matrix3x2d matrix3x2d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 48);
			}

			put(matrix3x2d, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void putf(Matrix3d matrix3d, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 9);
			}

			putf(matrix3d, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void putf(Matrix3d matrix3d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 36);
			}

			putf(matrix3d, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void put(Matrix2f matrix2f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 4);
			}

			put(matrix2f, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void put(Matrix2f matrix2f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 16);
			}

			put(matrix2f, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void put(Matrix2d matrix2d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, doubleBuffer.isDirect(), doubleBuffer.capacity(), 4);
			}

			put(matrix2d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (long)(int1 << 3));
		}

		public void put(Matrix2d matrix2d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 16);
			}

			put(matrix2d, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void putf(Matrix2d matrix2d, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 4);
			}

			putf(matrix2d, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void putf(Matrix2d matrix2d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 16);
			}

			putf(matrix2d, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void put(Vector4d vector4d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, doubleBuffer.isDirect(), doubleBuffer.capacity(), 4);
			}

			put(vector4d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (long)(int1 << 3));
		}

		public void put(Vector4d vector4d, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 4);
			}

			putf(vector4d, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void put(Vector4d vector4d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 32);
			}

			put(vector4d, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void putf(Vector4d vector4d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 16);
			}

			putf(vector4d, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void put(Vector4f vector4f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 4);
			}

			put(vector4f, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void put(Vector4f vector4f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 16);
			}

			put(vector4f, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void put(Vector4i vector4i, int int1, IntBuffer intBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, intBuffer.isDirect(), intBuffer.capacity(), 4);
			}

			put(vector4i, UNSAFE.getLong(intBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void put(Vector4i vector4i, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 16);
			}

			put(vector4i, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void put(Vector3f vector3f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 3);
			}

			put(vector3f, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void put(Vector3f vector3f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 12);
			}

			put(vector3f, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void put(Vector3d vector3d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, doubleBuffer.isDirect(), doubleBuffer.capacity(), 3);
			}

			put(vector3d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (long)(int1 << 3));
		}

		public void put(Vector3d vector3d, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 3);
			}

			putf(vector3d, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void put(Vector3d vector3d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 24);
			}

			put(vector3d, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void putf(Vector3d vector3d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 12);
			}

			putf(vector3d, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void put(Vector3i vector3i, int int1, IntBuffer intBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, intBuffer.isDirect(), intBuffer.capacity(), 3);
			}

			put(vector3i, UNSAFE.getLong(intBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void put(Vector3i vector3i, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 12);
			}

			put(vector3i, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void put(Vector2f vector2f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 2);
			}

			put(vector2f, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void put(Vector2f vector2f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 8);
			}

			put(vector2f, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void put(Vector2d vector2d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, doubleBuffer.isDirect(), doubleBuffer.capacity(), 2);
			}

			put(vector2d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (long)(int1 << 3));
		}

		public void put(Vector2d vector2d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 16);
			}

			put(vector2d, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void put(Vector2i vector2i, int int1, IntBuffer intBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, intBuffer.isDirect(), intBuffer.capacity(), 2);
			}

			put(vector2i, UNSAFE.getLong(intBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void put(Vector2i vector2i, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkPut(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 8);
			}

			put(vector2i, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void get(Matrix4f matrix4f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 16);
			}

			get(matrix4f, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void get(Matrix4f matrix4f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 64);
			}

			get(matrix4f, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public float get(Matrix4f matrix4f, int int1, int int2) {
			return UNSAFE.getFloat(matrix4f, Matrix4f_m00 + (long)(int1 << 4) + (long)(int2 << 2));
		}

		public Matrix4f set(Matrix4f matrix4f, int int1, int int2, float float1) {
			UNSAFE.putFloat(matrix4f, Matrix4f_m00 + (long)(int1 << 4) + (long)(int2 << 2), float1);
			return matrix4f;
		}

		public double get(Matrix4d matrix4d, int int1, int int2) {
			return UNSAFE.getDouble(matrix4d, Matrix4d_m00 + (long)(int1 << 5) + (long)(int2 << 3));
		}

		public Matrix4d set(Matrix4d matrix4d, int int1, int int2, double double1) {
			UNSAFE.putDouble(matrix4d, Matrix4d_m00 + (long)(int1 << 5) + (long)(int2 << 3), double1);
			return matrix4d;
		}

		public float get(Matrix3f matrix3f, int int1, int int2) {
			return UNSAFE.getFloat(matrix3f, Matrix3f_m00 + (long)(int1 * 12) + (long)(int2 << 2));
		}

		public Matrix3f set(Matrix3f matrix3f, int int1, int int2, float float1) {
			UNSAFE.putFloat(matrix3f, Matrix3f_m00 + (long)(int1 * 12) + (long)(int2 << 2), float1);
			return matrix3f;
		}

		public double get(Matrix3d matrix3d, int int1, int int2) {
			return UNSAFE.getDouble(matrix3d, Matrix3d_m00 + (long)(int1 * 24) + (long)(int2 << 3));
		}

		public Matrix3d set(Matrix3d matrix3d, int int1, int int2, double double1) {
			UNSAFE.putDouble(matrix3d, Matrix3d_m00 + (long)(int1 * 24) + (long)(int2 << 3), double1);
			return matrix3d;
		}

		public void get(Matrix4x3f matrix4x3f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 12);
			}

			get(matrix4x3f, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void get(Matrix4x3f matrix4x3f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 48);
			}

			get(matrix4x3f, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void get(Matrix4d matrix4d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, doubleBuffer.isDirect(), doubleBuffer.capacity(), 16);
			}

			get(matrix4d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (long)(int1 << 3));
		}

		public void get(Matrix4d matrix4d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 128);
			}

			get(matrix4d, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void get(Matrix4x3d matrix4x3d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, doubleBuffer.isDirect(), doubleBuffer.capacity(), 12);
			}

			get(matrix4x3d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (long)(int1 << 3));
		}

		public void get(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 96);
			}

			get(matrix4x3d, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void getf(Matrix4d matrix4d, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 16);
			}

			getf(matrix4d, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void getf(Matrix4d matrix4d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 64);
			}

			getf(matrix4d, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void getf(Matrix4x3d matrix4x3d, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 12);
			}

			getf(matrix4x3d, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		private static void checkGet(int int1, boolean boolean1, int int2, int int3) {
			if (!boolean1) {
				throwNoDirectBufferException();
			}

			if (int2 - int1 < int3) {
				throw new BufferUnderflowException();
			}
		}

		public void getf(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 48);
			}

			getf(matrix4x3d, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void get(Matrix3f matrix3f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 9);
			}

			get(matrix3f, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void get(Matrix3f matrix3f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 36);
			}

			get(matrix3f, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void get(Matrix3d matrix3d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, doubleBuffer.isDirect(), doubleBuffer.capacity(), 9);
			}

			get(matrix3d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (long)(int1 << 3));
		}

		public void get(Matrix3d matrix3d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 72);
			}

			get(matrix3d, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void get(Matrix3x2f matrix3x2f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 6);
			}

			get(matrix3x2f, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void get(Matrix3x2f matrix3x2f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 24);
			}

			get(matrix3x2f, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void get(Matrix3x2d matrix3x2d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, doubleBuffer.isDirect(), doubleBuffer.capacity(), 6);
			}

			get(matrix3x2d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (long)(int1 << 3));
		}

		public void get(Matrix3x2d matrix3x2d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 48);
			}

			get(matrix3x2d, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void getf(Matrix3d matrix3d, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 9);
			}

			getf(matrix3d, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void getf(Matrix3d matrix3d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 36);
			}

			getf(matrix3d, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void get(Matrix2f matrix2f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 4);
			}

			get(matrix2f, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void get(Matrix2f matrix2f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 16);
			}

			get(matrix2f, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void get(Matrix2d matrix2d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, doubleBuffer.isDirect(), doubleBuffer.capacity(), 4);
			}

			get(matrix2d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (long)(int1 << 3));
		}

		public void get(Matrix2d matrix2d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 32);
			}

			get(matrix2d, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void getf(Matrix2d matrix2d, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 4);
			}

			getf(matrix2d, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void getf(Matrix2d matrix2d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 16);
			}

			getf(matrix2d, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void get(Vector4d vector4d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, doubleBuffer.isDirect(), doubleBuffer.capacity(), 4);
			}

			get(vector4d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (long)(int1 << 3));
		}

		public void get(Vector4d vector4d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 32);
			}

			get(vector4d, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void get(Vector4f vector4f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 4);
			}

			get(vector4f, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void get(Vector4f vector4f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 16);
			}

			get(vector4f, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void get(Vector4i vector4i, int int1, IntBuffer intBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, intBuffer.isDirect(), intBuffer.capacity(), 4);
			}

			get(vector4i, UNSAFE.getLong(intBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void get(Vector4i vector4i, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 16);
			}

			get(vector4i, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void get(Vector3f vector3f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 3);
			}

			get(vector3f, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void get(Vector3f vector3f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 12);
			}

			get(vector3f, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void get(Vector3d vector3d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, doubleBuffer.isDirect(), doubleBuffer.capacity(), 3);
			}

			get(vector3d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (long)(int1 << 3));
		}

		public void get(Vector3d vector3d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 24);
			}

			get(vector3d, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void get(Vector3i vector3i, int int1, IntBuffer intBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, intBuffer.isDirect(), intBuffer.capacity(), 3);
			}

			get(vector3i, UNSAFE.getLong(intBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void get(Vector3i vector3i, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 12);
			}

			get(vector3i, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void get(Vector2f vector2f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, floatBuffer.isDirect(), floatBuffer.capacity(), 2);
			}

			get(vector2f, UNSAFE.getLong(floatBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void get(Vector2f vector2f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 8);
			}

			get(vector2f, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void get(Vector2d vector2d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, doubleBuffer.isDirect(), doubleBuffer.capacity(), 2);
			}

			get(vector2d, UNSAFE.getLong(doubleBuffer, ADDRESS) + (long)(int1 << 3));
		}

		public void get(Vector2d vector2d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 16);
			}

			get(vector2d, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		public void get(Vector2i vector2i, int int1, IntBuffer intBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, intBuffer.isDirect(), intBuffer.capacity(), 2);
			}

			get(vector2i, UNSAFE.getLong(intBuffer, ADDRESS) + (long)(int1 << 2));
		}

		public void get(Vector2i vector2i, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG) {
				checkGet(int1, byteBuffer.isDirect(), byteBuffer.capacity(), 8);
			}

			get(vector2i, UNSAFE.getLong(byteBuffer, ADDRESS) + (long)int1);
		}

		static  {
		try {
			ADDRESS = findBufferAddress();
			Matrix4f_m00 = checkMatrix4f();
			Matrix4d_m00 = checkMatrix4d();
			Matrix4x3f_m00 = checkMatrix4x3f();
			Matrix3f_m00 = checkMatrix3f();
			Matrix3d_m00 = checkMatrix3d();
			Matrix3x2f_m00 = checkMatrix3x2f();
			Matrix2f_m00 = checkMatrix2f();
			Vector4f_x = checkVector4f();
			Vector4i_x = checkVector4i();
			Vector3f_x = checkVector3f();
			Vector3i_x = checkVector3i();
			Vector2f_x = checkVector2f();
			Vector2i_x = checkVector2i();
			Quaternionf_x = checkQuaternionf();
			floatArrayOffset = (long)UNSAFE.arrayBaseOffset(float[].class);
			Unsafe.class.getDeclaredMethod("getLong", Object.class, Long.TYPE);
			Unsafe.class.getDeclaredMethod("putLong", Object.class, Long.TYPE, Long.TYPE);
		} catch (NoSuchFieldException var1) {
			throw new UnsupportedOperationException(var1);
		} catch (NoSuchMethodException var2) {
			throw new UnsupportedOperationException(var2);
		}
		}
	}
}
