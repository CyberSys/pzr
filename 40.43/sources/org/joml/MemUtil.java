package org.joml;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import sun.misc.Unsafe;


abstract class MemUtil {
	public static final MemUtil INSTANCE = createInstance();

	private static final MemUtil createInstance() {
		Object object;
		try {
			if (Options.NO_UNSAFE) {
				object = new MemUtil.MemUtilNIO();
			} else {
				object = new MemUtil.MemUtilUnsafe();
			}
		} catch (Throwable throwable) {
			object = new MemUtil.MemUtilNIO();
		}

		return (MemUtil)object;
	}

	public abstract MemUtil.MemUtilUnsafe UNSAFE();

	public abstract void put(Matrix4f matrix4f, int int1, FloatBuffer floatBuffer);

	public abstract void put(Matrix4f matrix4f, int int1, ByteBuffer byteBuffer);

	public abstract void put(Matrix4x3f matrix4x3f, int int1, FloatBuffer floatBuffer);

	public abstract void put(Matrix4x3f matrix4x3f, int int1, ByteBuffer byteBuffer);

	public abstract void put4x4(Matrix4x3f matrix4x3f, int int1, FloatBuffer floatBuffer);

	public abstract void put4x4(Matrix4x3f matrix4x3f, int int1, ByteBuffer byteBuffer);

	public abstract void put4x4(Matrix4x3d matrix4x3d, int int1, DoubleBuffer doubleBuffer);

	public abstract void put4x4(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer);

	public abstract void putTransposed(Matrix4f matrix4f, int int1, FloatBuffer floatBuffer);

	public abstract void putTransposed(Matrix4f matrix4f, int int1, ByteBuffer byteBuffer);

	public abstract void put4x3Transposed(Matrix4f matrix4f, int int1, FloatBuffer floatBuffer);

	public abstract void put4x3Transposed(Matrix4f matrix4f, int int1, ByteBuffer byteBuffer);

	public abstract void putTransposed(Matrix4x3f matrix4x3f, int int1, FloatBuffer floatBuffer);

	public abstract void putTransposed(Matrix4x3f matrix4x3f, int int1, ByteBuffer byteBuffer);

	public abstract void putTransposed(Matrix3f matrix3f, int int1, FloatBuffer floatBuffer);

	public abstract void putTransposed(Matrix3f matrix3f, int int1, ByteBuffer byteBuffer);

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

	public abstract void putfTransposed(Matrix4d matrix4d, int int1, FloatBuffer floatBuffer);

	public abstract void putfTransposed(Matrix4d matrix4d, int int1, ByteBuffer byteBuffer);

	public abstract void putfTransposed(Matrix4x3d matrix4x3d, int int1, FloatBuffer floatBuffer);

	public abstract void putfTransposed(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer);

	public abstract void put(Matrix3f matrix3f, int int1, FloatBuffer floatBuffer);

	public abstract void put(Matrix3f matrix3f, int int1, ByteBuffer byteBuffer);

	public abstract void put(Matrix3d matrix3d, int int1, DoubleBuffer doubleBuffer);

	public abstract void put(Matrix3d matrix3d, int int1, ByteBuffer byteBuffer);

	public abstract void putf(Matrix3d matrix3d, int int1, FloatBuffer floatBuffer);

	public abstract void putf(Matrix3d matrix3d, int int1, ByteBuffer byteBuffer);

	public abstract void put(Vector4d vector4d, int int1, DoubleBuffer doubleBuffer);

	public abstract void put(Vector4d vector4d, int int1, ByteBuffer byteBuffer);

	public abstract void put(Vector4f vector4f, int int1, FloatBuffer floatBuffer);

	public abstract void put(Vector4f vector4f, int int1, ByteBuffer byteBuffer);

	public abstract void put(Vector4i vector4i, int int1, IntBuffer intBuffer);

	public abstract void put(Vector4i vector4i, int int1, ByteBuffer byteBuffer);

	public abstract void put(Vector3f vector3f, int int1, FloatBuffer floatBuffer);

	public abstract void put(Vector3f vector3f, int int1, ByteBuffer byteBuffer);

	public abstract void put(Vector3d vector3d, int int1, DoubleBuffer doubleBuffer);

	public abstract void put(Vector3d vector3d, int int1, ByteBuffer byteBuffer);

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

	public abstract void getf(Matrix3d matrix3d, int int1, FloatBuffer floatBuffer);

	public abstract void getf(Matrix3d matrix3d, int int1, ByteBuffer byteBuffer);

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

	public abstract void copy(Matrix4f matrix4f, Matrix4f matrix4f2);

	public abstract void copy(Matrix4x3f matrix4x3f, Matrix4x3f matrix4x3f2);

	public abstract void copy(Matrix4f matrix4f, Matrix4x3f matrix4x3f);

	public abstract void copy(Matrix4x3f matrix4x3f, Matrix4f matrix4f);

	public abstract void copy(Matrix3f matrix3f, Matrix3f matrix3f2);

	public abstract void copy(Matrix3f matrix3f, Matrix4f matrix4f);

	public abstract void copy(Matrix4f matrix4f, Matrix3f matrix3f);

	public abstract void copy(Matrix3f matrix3f, Matrix4x3f matrix4x3f);

	public abstract void copy3x3(Matrix4f matrix4f, Matrix4f matrix4f2);

	public abstract void copy3x3(Matrix4x3f matrix4x3f, Matrix4x3f matrix4x3f2);

	public abstract void copy3x3(Matrix3f matrix3f, Matrix4x3f matrix4x3f);

	public abstract void copy3x3(Matrix3f matrix3f, Matrix4f matrix4f);

	public abstract void copy4x3(Matrix4f matrix4f, Matrix4f matrix4f2);

	public abstract void copy4x3(Matrix4x3f matrix4x3f, Matrix4f matrix4f);

	public abstract void copy(Vector4f vector4f, Vector4f vector4f2);

	public abstract void copy(Vector4i vector4i, Vector4i vector4i2);

	public abstract void copy(Quaternionf quaternionf, Quaternionf quaternionf2);

	public abstract void copy(float[] floatArray, int int1, Matrix4f matrix4f);

	public abstract void copy(float[] floatArray, int int1, Matrix3f matrix3f);

	public abstract void copy(float[] floatArray, int int1, Matrix4x3f matrix4x3f);

	public abstract void copy(Matrix4f matrix4f, float[] floatArray, int int1);

	public abstract void copy(Matrix3f matrix3f, float[] floatArray, int int1);

	public abstract void copy(Matrix4x3f matrix4x3f, float[] floatArray, int int1);

	public abstract void identity(Matrix4f matrix4f);

	public abstract void identity(Matrix4x3f matrix4x3f);

	public abstract void identity(Matrix3f matrix3f);

	public abstract void identity(Quaternionf quaternionf);

	public abstract void swap(Matrix4f matrix4f, Matrix4f matrix4f2);

	public abstract void swap(Matrix4x3f matrix4x3f, Matrix4x3f matrix4x3f2);

	public abstract void swap(Matrix3f matrix3f, Matrix3f matrix3f2);

	public abstract void zero(Matrix4f matrix4f);

	public abstract void zero(Matrix4x3f matrix4x3f);

	public abstract void zero(Matrix3f matrix3f);

	public abstract void zero(Vector4f vector4f);

	public abstract void zero(Vector4i vector4i);

	public abstract void putMatrix3f(Quaternionf quaternionf, int int1, ByteBuffer byteBuffer);

	public abstract void putMatrix3f(Quaternionf quaternionf, int int1, FloatBuffer floatBuffer);

	public abstract void putMatrix4f(Quaternionf quaternionf, int int1, ByteBuffer byteBuffer);

	public abstract void putMatrix4f(Quaternionf quaternionf, int int1, FloatBuffer floatBuffer);

	public abstract void putMatrix4x3f(Quaternionf quaternionf, int int1, ByteBuffer byteBuffer);

	public abstract void putMatrix4x3f(Quaternionf quaternionf, int int1, FloatBuffer floatBuffer);

	public abstract void set(Matrix4f matrix4f, Vector4f vector4f, Vector4f vector4f2, Vector4f vector4f3, Vector4f vector4f4);

	public abstract void set(Matrix4x3f matrix4x3f, Vector3f vector3f, Vector3f vector3f2, Vector3f vector3f3, Vector3f vector3f4);

	public abstract void set(Matrix3f matrix3f, Vector3f vector3f, Vector3f vector3f2, Vector3f vector3f3);

	public abstract void putColumn0(Matrix4f matrix4f, Vector4f vector4f);

	public abstract void putColumn1(Matrix4f matrix4f, Vector4f vector4f);

	public abstract void putColumn2(Matrix4f matrix4f, Vector4f vector4f);

	public abstract void putColumn3(Matrix4f matrix4f, Vector4f vector4f);

	public abstract void getColumn0(Matrix4f matrix4f, Vector4f vector4f);

	public abstract void getColumn1(Matrix4f matrix4f, Vector4f vector4f);

	public abstract void getColumn2(Matrix4f matrix4f, Vector4f vector4f);

	public abstract void getColumn3(Matrix4f matrix4f, Vector4f vector4f);

	public abstract void broadcast(float float1, Vector4f vector4f);

	public abstract void broadcast(int int1, Vector4i vector4i);

	public static final class MemUtilUnsafe extends MemUtil {
		private static final Unsafe UNSAFE = getUnsafeInstance();
		private static final long ADDRESS;
		private static final long Matrix3f_m00;
		private static final long Matrix4f_m00;
		private static final long Matrix4x3f_m00;
		private static final long Vector4f_x;
		private static final long Vector4d_x;
		private static final long Vector4i_x;
		private static final long Vector3f_x;
		private static final long Vector3d_x;
		private static final long Vector3i_x;
		private static final long Vector2f_x;
		private static final long Vector2d_x;
		private static final long Vector2i_x;
		private static final long Quaternionf_x;
		private static final long floatArrayOffset;

		public MemUtil.MemUtilUnsafe UNSAFE() {
			return this;
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
					throw new UnsupportedOperationException();
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
					throw new UnsupportedOperationException();
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
					throw new UnsupportedOperationException();
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
					throw new UnsupportedOperationException();
				}
			}

			return long1;
		}

		private static long checkVector4d() throws NoSuchFieldException, SecurityException {
			Field field = Vector4d.class.getDeclaredField("x");
			long long1 = UNSAFE.objectFieldOffset(field);
			String[] stringArray = new String[]{"y", "z", "w"};
			for (int int1 = 1; int1 < 4; ++int1) {
				field = Vector4d.class.getDeclaredField(stringArray[int1 - 1]);
				long long2 = UNSAFE.objectFieldOffset(field);
				if (long2 != long1 + (long)(int1 << 3)) {
					throw new UnsupportedOperationException();
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
					throw new UnsupportedOperationException();
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
					throw new UnsupportedOperationException();
				}
			}

			return long1;
		}

		private static long checkVector3d() throws NoSuchFieldException, SecurityException {
			Field field = Vector3d.class.getDeclaredField("x");
			long long1 = UNSAFE.objectFieldOffset(field);
			String[] stringArray = new String[]{"y", "z"};
			for (int int1 = 1; int1 < 3; ++int1) {
				field = Vector3d.class.getDeclaredField(stringArray[int1 - 1]);
				long long2 = UNSAFE.objectFieldOffset(field);
				if (long2 != long1 + (long)(int1 << 3)) {
					throw new UnsupportedOperationException();
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
					throw new UnsupportedOperationException();
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
				throw new UnsupportedOperationException();
			} else {
				return long1;
			}
		}

		private static long checkVector2d() throws NoSuchFieldException, SecurityException {
			Field field = Vector2d.class.getDeclaredField("x");
			long long1 = UNSAFE.objectFieldOffset(field);
			field = Vector2d.class.getDeclaredField("y");
			long long2 = UNSAFE.objectFieldOffset(field);
			if (long2 != long1 + 8L) {
				throw new UnsupportedOperationException();
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
				throw new UnsupportedOperationException();
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
					throw new UnsupportedOperationException();
				}
			}

			return long1;
		}

		private static final Field getDeclaredField(Class javaClass, String string) throws NoSuchFieldException {
			Class javaClass2 = javaClass;
			do {
				try {
					Field field = javaClass2.getDeclaredField(string);
					field.setAccessible(true);
					return field;
				} catch (NoSuchFieldException noSuchFieldException) {
					javaClass2 = javaClass2.getSuperclass();
				} catch (SecurityException securityException) {
					javaClass2 = javaClass2.getSuperclass();
				}
			} while (javaClass2 != null);

			throw new NoSuchFieldException(string + " does not exist in " + javaClass.getName() + " or any of its superclasses.");
		}

		private static final Unsafe getUnsafeInstance() throws SecurityException {
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

		public final long addressOf(Buffer buffer) {
			return UNSAFE.getLong(buffer, ADDRESS);
		}

		public final void put(Matrix4f matrix4f, long long1) {
			for (int int1 = 0; int1 < 8; ++int1) {
				UNSAFE.putOrderedLong((Object)null, long1 + (long)(int1 << 3), UNSAFE.getLong(matrix4f, Matrix4f_m00 + (long)(int1 << 3)));
			}
		}

		public final void put(Matrix4x3f matrix4x3f, long long1) {
			for (int int1 = 0; int1 < 6; ++int1) {
				UNSAFE.putOrderedLong((Object)null, long1 + (long)(int1 << 3), UNSAFE.getLong(matrix4x3f, Matrix4x3f_m00 + (long)(int1 << 3)));
			}
		}

		public final void put4x4(Matrix4x3f matrix4x3f, long long1) {
			for (int int1 = 0; int1 < 4; ++int1) {
				UNSAFE.putOrderedLong((Object)null, long1 + (long)(int1 << 4), UNSAFE.getLong(matrix4x3f, Matrix4x3f_m00 + (long)(12 * int1)));
				long long2 = (long)UNSAFE.getInt(matrix4x3f, Matrix4x3f_m00 + 8L + (long)(12 * int1)) & 4294967295L;
				UNSAFE.putOrderedLong((Object)null, long1 + 8L + (long)(int1 << 4), long2);
			}

			UNSAFE.putFloat((Object)null, long1 + 60L, 1.0F);
		}

		public final void put4x4(Matrix4x3d matrix4x3d, long long1) {
			UNSAFE.putDouble((Object)null, long1, matrix4x3d.m00);
			UNSAFE.putDouble((Object)null, long1 + 8L, matrix4x3d.m01);
			UNSAFE.putDouble((Object)null, long1 + 16L, matrix4x3d.m02);
			UNSAFE.putDouble((Object)null, long1 + 24L, 0.0);
			UNSAFE.putDouble((Object)null, long1 + 32L, matrix4x3d.m10);
			UNSAFE.putDouble((Object)null, long1 + 40L, matrix4x3d.m11);
			UNSAFE.putDouble((Object)null, long1 + 48L, matrix4x3d.m12);
			UNSAFE.putDouble((Object)null, long1 + 56L, 0.0);
			UNSAFE.putDouble((Object)null, long1 + 64L, matrix4x3d.m20);
			UNSAFE.putDouble((Object)null, long1 + 72L, matrix4x3d.m21);
			UNSAFE.putDouble((Object)null, long1 + 80L, matrix4x3d.m22);
			UNSAFE.putDouble((Object)null, long1 + 88L, 0.0);
			UNSAFE.putDouble((Object)null, long1 + 96L, matrix4x3d.m30);
			UNSAFE.putDouble((Object)null, long1 + 104L, matrix4x3d.m31);
			UNSAFE.putDouble((Object)null, long1 + 112L, matrix4x3d.m32);
			UNSAFE.putDouble((Object)null, long1 + 120L, 1.0);
		}

		public final void putTransposed(Matrix4f matrix4f, long long1) {
			UNSAFE.putFloat((Object)null, long1, matrix4f.m00);
			UNSAFE.putFloat((Object)null, long1 + 4L, matrix4f.m10);
			UNSAFE.putFloat((Object)null, long1 + 8L, matrix4f.m20);
			UNSAFE.putFloat((Object)null, long1 + 12L, matrix4f.m30);
			UNSAFE.putFloat((Object)null, long1 + 16L, matrix4f.m01);
			UNSAFE.putFloat((Object)null, long1 + 20L, matrix4f.m11);
			UNSAFE.putFloat((Object)null, long1 + 24L, matrix4f.m21);
			UNSAFE.putFloat((Object)null, long1 + 28L, matrix4f.m31);
			UNSAFE.putFloat((Object)null, long1 + 32L, matrix4f.m02);
			UNSAFE.putFloat((Object)null, long1 + 36L, matrix4f.m12);
			UNSAFE.putFloat((Object)null, long1 + 40L, matrix4f.m22);
			UNSAFE.putFloat((Object)null, long1 + 44L, matrix4f.m32);
			UNSAFE.putFloat((Object)null, long1 + 48L, matrix4f.m03);
			UNSAFE.putFloat((Object)null, long1 + 52L, matrix4f.m13);
			UNSAFE.putFloat((Object)null, long1 + 56L, matrix4f.m23);
			UNSAFE.putFloat((Object)null, long1 + 60L, matrix4f.m33);
		}

		public final void put4x3Transposed(Matrix4f matrix4f, long long1) {
			UNSAFE.putFloat((Object)null, long1, matrix4f.m00);
			UNSAFE.putFloat((Object)null, long1 + 4L, matrix4f.m10);
			UNSAFE.putFloat((Object)null, long1 + 8L, matrix4f.m20);
			UNSAFE.putFloat((Object)null, long1 + 12L, matrix4f.m30);
			UNSAFE.putFloat((Object)null, long1 + 16L, matrix4f.m01);
			UNSAFE.putFloat((Object)null, long1 + 20L, matrix4f.m11);
			UNSAFE.putFloat((Object)null, long1 + 24L, matrix4f.m21);
			UNSAFE.putFloat((Object)null, long1 + 28L, matrix4f.m31);
			UNSAFE.putFloat((Object)null, long1 + 32L, matrix4f.m02);
			UNSAFE.putFloat((Object)null, long1 + 36L, matrix4f.m12);
			UNSAFE.putFloat((Object)null, long1 + 40L, matrix4f.m22);
			UNSAFE.putFloat((Object)null, long1 + 44L, matrix4f.m32);
		}

		public final void putTransposed(Matrix4x3f matrix4x3f, long long1) {
			UNSAFE.putFloat((Object)null, long1, matrix4x3f.m00);
			UNSAFE.putFloat((Object)null, long1 + 4L, matrix4x3f.m10);
			UNSAFE.putFloat((Object)null, long1 + 8L, matrix4x3f.m20);
			UNSAFE.putFloat((Object)null, long1 + 12L, matrix4x3f.m30);
			UNSAFE.putFloat((Object)null, long1 + 16L, matrix4x3f.m01);
			UNSAFE.putFloat((Object)null, long1 + 20L, matrix4x3f.m11);
			UNSAFE.putFloat((Object)null, long1 + 24L, matrix4x3f.m21);
			UNSAFE.putFloat((Object)null, long1 + 28L, matrix4x3f.m31);
			UNSAFE.putFloat((Object)null, long1 + 32L, matrix4x3f.m02);
			UNSAFE.putFloat((Object)null, long1 + 36L, matrix4x3f.m12);
			UNSAFE.putFloat((Object)null, long1 + 40L, matrix4x3f.m22);
			UNSAFE.putFloat((Object)null, long1 + 44L, matrix4x3f.m32);
		}

		public final void putTransposed(Matrix3f matrix3f, long long1) {
			UNSAFE.putFloat((Object)null, long1, matrix3f.m00);
			UNSAFE.putFloat((Object)null, long1 + 4L, matrix3f.m10);
			UNSAFE.putFloat((Object)null, long1 + 8L, matrix3f.m20);
			UNSAFE.putFloat((Object)null, long1 + 12L, matrix3f.m01);
			UNSAFE.putFloat((Object)null, long1 + 16L, matrix3f.m11);
			UNSAFE.putFloat((Object)null, long1 + 20L, matrix3f.m21);
			UNSAFE.putFloat((Object)null, long1 + 24L, matrix3f.m02);
			UNSAFE.putFloat((Object)null, long1 + 28L, matrix3f.m12);
			UNSAFE.putFloat((Object)null, long1 + 32L, matrix3f.m22);
		}

		public final void put(Matrix4d matrix4d, long long1) {
			UNSAFE.putDouble((Object)null, long1, matrix4d.m00);
			UNSAFE.putDouble((Object)null, long1 + 8L, matrix4d.m01);
			UNSAFE.putDouble((Object)null, long1 + 16L, matrix4d.m02);
			UNSAFE.putDouble((Object)null, long1 + 24L, matrix4d.m03);
			UNSAFE.putDouble((Object)null, long1 + 32L, matrix4d.m10);
			UNSAFE.putDouble((Object)null, long1 + 40L, matrix4d.m11);
			UNSAFE.putDouble((Object)null, long1 + 48L, matrix4d.m12);
			UNSAFE.putDouble((Object)null, long1 + 56L, matrix4d.m13);
			UNSAFE.putDouble((Object)null, long1 + 64L, matrix4d.m20);
			UNSAFE.putDouble((Object)null, long1 + 72L, matrix4d.m21);
			UNSAFE.putDouble((Object)null, long1 + 80L, matrix4d.m22);
			UNSAFE.putDouble((Object)null, long1 + 88L, matrix4d.m23);
			UNSAFE.putDouble((Object)null, long1 + 96L, matrix4d.m30);
			UNSAFE.putDouble((Object)null, long1 + 104L, matrix4d.m31);
			UNSAFE.putDouble((Object)null, long1 + 112L, matrix4d.m32);
			UNSAFE.putDouble((Object)null, long1 + 120L, matrix4d.m33);
		}

		public final void put(Matrix4x3d matrix4x3d, long long1) {
			UNSAFE.putDouble((Object)null, long1, matrix4x3d.m00);
			UNSAFE.putDouble((Object)null, long1 + 8L, matrix4x3d.m01);
			UNSAFE.putDouble((Object)null, long1 + 16L, matrix4x3d.m02);
			UNSAFE.putDouble((Object)null, long1 + 24L, matrix4x3d.m10);
			UNSAFE.putDouble((Object)null, long1 + 32L, matrix4x3d.m11);
			UNSAFE.putDouble((Object)null, long1 + 40L, matrix4x3d.m12);
			UNSAFE.putDouble((Object)null, long1 + 48L, matrix4x3d.m20);
			UNSAFE.putDouble((Object)null, long1 + 56L, matrix4x3d.m21);
			UNSAFE.putDouble((Object)null, long1 + 64L, matrix4x3d.m22);
			UNSAFE.putDouble((Object)null, long1 + 72L, matrix4x3d.m30);
			UNSAFE.putDouble((Object)null, long1 + 80L, matrix4x3d.m31);
			UNSAFE.putDouble((Object)null, long1 + 88L, matrix4x3d.m32);
		}

		public final void putTransposed(Matrix4d matrix4d, long long1) {
			UNSAFE.putDouble((Object)null, long1, matrix4d.m00);
			UNSAFE.putDouble((Object)null, long1 + 8L, matrix4d.m10);
			UNSAFE.putDouble((Object)null, long1 + 16L, matrix4d.m20);
			UNSAFE.putDouble((Object)null, long1 + 24L, matrix4d.m30);
			UNSAFE.putDouble((Object)null, long1 + 32L, matrix4d.m01);
			UNSAFE.putDouble((Object)null, long1 + 40L, matrix4d.m11);
			UNSAFE.putDouble((Object)null, long1 + 48L, matrix4d.m21);
			UNSAFE.putDouble((Object)null, long1 + 56L, matrix4d.m31);
			UNSAFE.putDouble((Object)null, long1 + 64L, matrix4d.m02);
			UNSAFE.putDouble((Object)null, long1 + 72L, matrix4d.m12);
			UNSAFE.putDouble((Object)null, long1 + 80L, matrix4d.m22);
			UNSAFE.putDouble((Object)null, long1 + 88L, matrix4d.m32);
			UNSAFE.putDouble((Object)null, long1 + 96L, matrix4d.m03);
			UNSAFE.putDouble((Object)null, long1 + 104L, matrix4d.m13);
			UNSAFE.putDouble((Object)null, long1 + 112L, matrix4d.m23);
			UNSAFE.putDouble((Object)null, long1 + 120L, matrix4d.m33);
		}

		public final void putfTransposed(Matrix4d matrix4d, long long1) {
			UNSAFE.putFloat((Object)null, long1, (float)matrix4d.m00);
			UNSAFE.putFloat((Object)null, long1 + 4L, (float)matrix4d.m10);
			UNSAFE.putFloat((Object)null, long1 + 8L, (float)matrix4d.m20);
			UNSAFE.putFloat((Object)null, long1 + 12L, (float)matrix4d.m30);
			UNSAFE.putFloat((Object)null, long1 + 16L, (float)matrix4d.m01);
			UNSAFE.putFloat((Object)null, long1 + 20L, (float)matrix4d.m11);
			UNSAFE.putFloat((Object)null, long1 + 24L, (float)matrix4d.m21);
			UNSAFE.putFloat((Object)null, long1 + 28L, (float)matrix4d.m31);
			UNSAFE.putFloat((Object)null, long1 + 32L, (float)matrix4d.m02);
			UNSAFE.putFloat((Object)null, long1 + 36L, (float)matrix4d.m12);
			UNSAFE.putFloat((Object)null, long1 + 40L, (float)matrix4d.m22);
			UNSAFE.putFloat((Object)null, long1 + 44L, (float)matrix4d.m32);
			UNSAFE.putFloat((Object)null, long1 + 48L, (float)matrix4d.m03);
			UNSAFE.putFloat((Object)null, long1 + 52L, (float)matrix4d.m13);
			UNSAFE.putFloat((Object)null, long1 + 56L, (float)matrix4d.m23);
			UNSAFE.putFloat((Object)null, long1 + 60L, (float)matrix4d.m33);
		}

		public final void put4x3Transposed(Matrix4d matrix4d, long long1) {
			UNSAFE.putDouble((Object)null, long1, matrix4d.m00);
			UNSAFE.putDouble((Object)null, long1 + 8L, matrix4d.m10);
			UNSAFE.putDouble((Object)null, long1 + 16L, matrix4d.m20);
			UNSAFE.putDouble((Object)null, long1 + 24L, matrix4d.m30);
			UNSAFE.putDouble((Object)null, long1 + 32L, matrix4d.m01);
			UNSAFE.putDouble((Object)null, long1 + 40L, matrix4d.m11);
			UNSAFE.putDouble((Object)null, long1 + 48L, matrix4d.m21);
			UNSAFE.putDouble((Object)null, long1 + 56L, matrix4d.m31);
			UNSAFE.putDouble((Object)null, long1 + 64L, matrix4d.m02);
			UNSAFE.putDouble((Object)null, long1 + 72L, matrix4d.m12);
			UNSAFE.putDouble((Object)null, long1 + 80L, matrix4d.m22);
			UNSAFE.putDouble((Object)null, long1 + 88L, matrix4d.m32);
		}

		public final void putTransposed(Matrix4x3d matrix4x3d, long long1) {
			UNSAFE.putDouble((Object)null, long1, matrix4x3d.m00);
			UNSAFE.putDouble((Object)null, long1 + 8L, matrix4x3d.m10);
			UNSAFE.putDouble((Object)null, long1 + 16L, matrix4x3d.m20);
			UNSAFE.putDouble((Object)null, long1 + 24L, matrix4x3d.m30);
			UNSAFE.putDouble((Object)null, long1 + 32L, matrix4x3d.m01);
			UNSAFE.putDouble((Object)null, long1 + 40L, matrix4x3d.m11);
			UNSAFE.putDouble((Object)null, long1 + 48L, matrix4x3d.m21);
			UNSAFE.putDouble((Object)null, long1 + 56L, matrix4x3d.m31);
			UNSAFE.putDouble((Object)null, long1 + 64L, matrix4x3d.m02);
			UNSAFE.putDouble((Object)null, long1 + 72L, matrix4x3d.m12);
			UNSAFE.putDouble((Object)null, long1 + 80L, matrix4x3d.m22);
			UNSAFE.putDouble((Object)null, long1 + 88L, matrix4x3d.m32);
		}

		public final void putfTransposed(Matrix4x3d matrix4x3d, long long1) {
			UNSAFE.putFloat((Object)null, long1, (float)matrix4x3d.m00);
			UNSAFE.putFloat((Object)null, long1 + 4L, (float)matrix4x3d.m10);
			UNSAFE.putFloat((Object)null, long1 + 8L, (float)matrix4x3d.m20);
			UNSAFE.putFloat((Object)null, long1 + 12L, (float)matrix4x3d.m30);
			UNSAFE.putFloat((Object)null, long1 + 16L, (float)matrix4x3d.m01);
			UNSAFE.putFloat((Object)null, long1 + 20L, (float)matrix4x3d.m11);
			UNSAFE.putFloat((Object)null, long1 + 24L, (float)matrix4x3d.m21);
			UNSAFE.putFloat((Object)null, long1 + 28L, (float)matrix4x3d.m31);
			UNSAFE.putFloat((Object)null, long1 + 32L, (float)matrix4x3d.m02);
			UNSAFE.putFloat((Object)null, long1 + 36L, (float)matrix4x3d.m12);
			UNSAFE.putFloat((Object)null, long1 + 40L, (float)matrix4x3d.m22);
			UNSAFE.putFloat((Object)null, long1 + 44L, (float)matrix4x3d.m32);
		}

		public final void putf(Matrix4d matrix4d, long long1) {
			UNSAFE.putFloat((Object)null, long1, (float)matrix4d.m00);
			UNSAFE.putFloat((Object)null, long1 + 4L, (float)matrix4d.m01);
			UNSAFE.putFloat((Object)null, long1 + 8L, (float)matrix4d.m02);
			UNSAFE.putFloat((Object)null, long1 + 12L, (float)matrix4d.m03);
			UNSAFE.putFloat((Object)null, long1 + 16L, (float)matrix4d.m10);
			UNSAFE.putFloat((Object)null, long1 + 20L, (float)matrix4d.m11);
			UNSAFE.putFloat((Object)null, long1 + 24L, (float)matrix4d.m12);
			UNSAFE.putFloat((Object)null, long1 + 28L, (float)matrix4d.m13);
			UNSAFE.putFloat((Object)null, long1 + 32L, (float)matrix4d.m20);
			UNSAFE.putFloat((Object)null, long1 + 36L, (float)matrix4d.m21);
			UNSAFE.putFloat((Object)null, long1 + 40L, (float)matrix4d.m22);
			UNSAFE.putFloat((Object)null, long1 + 44L, (float)matrix4d.m23);
			UNSAFE.putFloat((Object)null, long1 + 48L, (float)matrix4d.m30);
			UNSAFE.putFloat((Object)null, long1 + 52L, (float)matrix4d.m31);
			UNSAFE.putFloat((Object)null, long1 + 56L, (float)matrix4d.m32);
			UNSAFE.putFloat((Object)null, long1 + 60L, (float)matrix4d.m33);
		}

		public final void putf(Matrix4x3d matrix4x3d, long long1) {
			UNSAFE.putFloat((Object)null, long1, (float)matrix4x3d.m00);
			UNSAFE.putFloat((Object)null, long1 + 4L, (float)matrix4x3d.m01);
			UNSAFE.putFloat((Object)null, long1 + 8L, (float)matrix4x3d.m02);
			UNSAFE.putFloat((Object)null, long1 + 12L, (float)matrix4x3d.m10);
			UNSAFE.putFloat((Object)null, long1 + 16L, (float)matrix4x3d.m11);
			UNSAFE.putFloat((Object)null, long1 + 20L, (float)matrix4x3d.m12);
			UNSAFE.putFloat((Object)null, long1 + 24L, (float)matrix4x3d.m20);
			UNSAFE.putFloat((Object)null, long1 + 28L, (float)matrix4x3d.m21);
			UNSAFE.putFloat((Object)null, long1 + 32L, (float)matrix4x3d.m22);
			UNSAFE.putFloat((Object)null, long1 + 36L, (float)matrix4x3d.m30);
			UNSAFE.putFloat((Object)null, long1 + 40L, (float)matrix4x3d.m31);
			UNSAFE.putFloat((Object)null, long1 + 44L, (float)matrix4x3d.m32);
		}

		public final void put(Matrix3f matrix3f, long long1) {
			for (int int1 = 0; int1 < 4; ++int1) {
				UNSAFE.putOrderedLong((Object)null, long1 + (long)(int1 << 3), UNSAFE.getLong(matrix3f, Matrix3f_m00 + (long)(int1 << 3)));
			}

			UNSAFE.putFloat((Object)null, long1 + 32L, matrix3f.m22);
		}

		public final void put(Matrix3d matrix3d, long long1) {
			UNSAFE.putDouble((Object)null, long1, matrix3d.m00);
			UNSAFE.putDouble((Object)null, long1 + 8L, matrix3d.m01);
			UNSAFE.putDouble((Object)null, long1 + 16L, matrix3d.m02);
			UNSAFE.putDouble((Object)null, long1 + 24L, matrix3d.m10);
			UNSAFE.putDouble((Object)null, long1 + 32L, matrix3d.m11);
			UNSAFE.putDouble((Object)null, long1 + 40L, matrix3d.m12);
			UNSAFE.putDouble((Object)null, long1 + 48L, matrix3d.m20);
			UNSAFE.putDouble((Object)null, long1 + 56L, matrix3d.m21);
			UNSAFE.putDouble((Object)null, long1 + 64L, matrix3d.m22);
		}

		public final void putf(Matrix3d matrix3d, long long1) {
			UNSAFE.putFloat((Object)null, long1, (float)matrix3d.m00);
			UNSAFE.putFloat((Object)null, long1 + 4L, (float)matrix3d.m01);
			UNSAFE.putFloat((Object)null, long1 + 8L, (float)matrix3d.m02);
			UNSAFE.putFloat((Object)null, long1 + 12L, (float)matrix3d.m10);
			UNSAFE.putFloat((Object)null, long1 + 16L, (float)matrix3d.m11);
			UNSAFE.putFloat((Object)null, long1 + 20L, (float)matrix3d.m12);
			UNSAFE.putFloat((Object)null, long1 + 24L, (float)matrix3d.m20);
			UNSAFE.putFloat((Object)null, long1 + 28L, (float)matrix3d.m21);
			UNSAFE.putFloat((Object)null, long1 + 32L, (float)matrix3d.m22);
		}

		public final void put(Vector4d vector4d, long long1) {
			for (int int1 = 0; int1 < 4; ++int1) {
				UNSAFE.putOrderedLong((Object)null, long1 + (long)(int1 << 3), UNSAFE.getLong(vector4d, Vector4d_x + (long)(int1 << 3)));
			}
		}

		public final void put(Vector4f vector4f, long long1) {
			UNSAFE.putOrderedLong((Object)null, long1, UNSAFE.getLong(vector4f, Vector4f_x));
			UNSAFE.putOrderedLong((Object)null, long1 + 8L, UNSAFE.getLong(vector4f, Vector4f_x + 8L));
		}

		public final void put(Vector4i vector4i, long long1) {
			UNSAFE.putOrderedLong((Object)null, long1, UNSAFE.getLong(vector4i, Vector4i_x));
			UNSAFE.putOrderedLong((Object)null, long1 + 8L, UNSAFE.getLong(vector4i, Vector4i_x + 8L));
		}

		public final void put(Vector3f vector3f, long long1) {
			UNSAFE.putOrderedLong((Object)null, long1, UNSAFE.getLong(vector3f, Vector3f_x));
			UNSAFE.putFloat((Object)null, long1 + 8L, UNSAFE.getFloat(vector3f, Vector3f_x + 8L));
		}

		public final void put(Vector3d vector3d, long long1) {
			UNSAFE.putDouble((Object)null, long1, UNSAFE.getDouble(vector3d, Vector3d_x));
			UNSAFE.putDouble((Object)null, long1 + 8L, UNSAFE.getDouble(vector3d, Vector3d_x + 8L));
			UNSAFE.putDouble((Object)null, long1 + 16L, UNSAFE.getDouble(vector3d, Vector3d_x + 16L));
		}

		public final void put(Vector3i vector3i, long long1) {
			UNSAFE.putOrderedLong((Object)null, long1, UNSAFE.getLong(vector3i, Vector3i_x));
			UNSAFE.putInt((Object)null, long1 + 8L, UNSAFE.getInt(vector3i, Vector3i_x + 8L));
		}

		public final void put(Vector2f vector2f, long long1) {
			UNSAFE.putOrderedLong((Object)null, long1, UNSAFE.getLong(vector2f, Vector2f_x));
		}

		public final void put(Vector2d vector2d, long long1) {
			UNSAFE.putDouble((Object)null, long1, UNSAFE.getDouble(vector2d, Vector2d_x));
			UNSAFE.putDouble((Object)null, long1 + 8L, UNSAFE.getDouble(vector2d, Vector2d_x + 8L));
		}

		public final void put(Vector2i vector2i, long long1) {
			UNSAFE.putOrderedLong((Object)null, long1, UNSAFE.getLong(vector2i, Vector2i_x));
		}

		public final void get(Matrix4f matrix4f, long long1) {
			for (int int1 = 0; int1 < 8; ++int1) {
				UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00 + (long)(int1 << 3), UNSAFE.getLong(long1 + (long)(int1 << 3)));
			}
		}

		public final void get(Matrix4x3f matrix4x3f, long long1) {
			for (int int1 = 0; int1 < 6; ++int1) {
				UNSAFE.putOrderedLong(matrix4x3f, Matrix4x3f_m00 + (long)(int1 << 3), UNSAFE.getLong(long1 + (long)(int1 << 3)));
			}
		}

		public final void get(Matrix4d matrix4d, long long1) {
			matrix4d.m00 = UNSAFE.getDouble((Object)null, long1);
			matrix4d.m01 = UNSAFE.getDouble((Object)null, long1 + 8L);
			matrix4d.m02 = UNSAFE.getDouble((Object)null, long1 + 16L);
			matrix4d.m03 = UNSAFE.getDouble((Object)null, long1 + 24L);
			matrix4d.m10 = UNSAFE.getDouble((Object)null, long1 + 32L);
			matrix4d.m11 = UNSAFE.getDouble((Object)null, long1 + 40L);
			matrix4d.m12 = UNSAFE.getDouble((Object)null, long1 + 48L);
			matrix4d.m13 = UNSAFE.getDouble((Object)null, long1 + 56L);
			matrix4d.m20 = UNSAFE.getDouble((Object)null, long1 + 64L);
			matrix4d.m21 = UNSAFE.getDouble((Object)null, long1 + 72L);
			matrix4d.m22 = UNSAFE.getDouble((Object)null, long1 + 80L);
			matrix4d.m23 = UNSAFE.getDouble((Object)null, long1 + 88L);
			matrix4d.m30 = UNSAFE.getDouble((Object)null, long1 + 96L);
			matrix4d.m31 = UNSAFE.getDouble((Object)null, long1 + 104L);
			matrix4d.m32 = UNSAFE.getDouble((Object)null, long1 + 112L);
			matrix4d.m33 = UNSAFE.getDouble((Object)null, long1 + 120L);
		}

		public final void get(Matrix4x3d matrix4x3d, long long1) {
			matrix4x3d.m00 = UNSAFE.getDouble((Object)null, long1);
			matrix4x3d.m01 = UNSAFE.getDouble((Object)null, long1 + 8L);
			matrix4x3d.m02 = UNSAFE.getDouble((Object)null, long1 + 16L);
			matrix4x3d.m10 = UNSAFE.getDouble((Object)null, long1 + 24L);
			matrix4x3d.m11 = UNSAFE.getDouble((Object)null, long1 + 32L);
			matrix4x3d.m12 = UNSAFE.getDouble((Object)null, long1 + 40L);
			matrix4x3d.m20 = UNSAFE.getDouble((Object)null, long1 + 48L);
			matrix4x3d.m21 = UNSAFE.getDouble((Object)null, long1 + 56L);
			matrix4x3d.m22 = UNSAFE.getDouble((Object)null, long1 + 64L);
			matrix4x3d.m30 = UNSAFE.getDouble((Object)null, long1 + 72L);
			matrix4x3d.m31 = UNSAFE.getDouble((Object)null, long1 + 80L);
			matrix4x3d.m32 = UNSAFE.getDouble((Object)null, long1 + 88L);
		}

		public final void getf(Matrix4d matrix4d, long long1) {
			matrix4d.m00 = (double)UNSAFE.getFloat((Object)null, long1);
			matrix4d.m01 = (double)UNSAFE.getFloat((Object)null, long1 + 4L);
			matrix4d.m02 = (double)UNSAFE.getFloat((Object)null, long1 + 8L);
			matrix4d.m03 = (double)UNSAFE.getFloat((Object)null, long1 + 12L);
			matrix4d.m10 = (double)UNSAFE.getFloat((Object)null, long1 + 16L);
			matrix4d.m11 = (double)UNSAFE.getFloat((Object)null, long1 + 20L);
			matrix4d.m12 = (double)UNSAFE.getFloat((Object)null, long1 + 24L);
			matrix4d.m13 = (double)UNSAFE.getFloat((Object)null, long1 + 28L);
			matrix4d.m20 = (double)UNSAFE.getFloat((Object)null, long1 + 32L);
			matrix4d.m21 = (double)UNSAFE.getFloat((Object)null, long1 + 36L);
			matrix4d.m22 = (double)UNSAFE.getFloat((Object)null, long1 + 40L);
			matrix4d.m23 = (double)UNSAFE.getFloat((Object)null, long1 + 44L);
			matrix4d.m30 = (double)UNSAFE.getFloat((Object)null, long1 + 48L);
			matrix4d.m31 = (double)UNSAFE.getFloat((Object)null, long1 + 52L);
			matrix4d.m32 = (double)UNSAFE.getFloat((Object)null, long1 + 56L);
			matrix4d.m33 = (double)UNSAFE.getFloat((Object)null, long1 + 60L);
		}

		public final void getf(Matrix4x3d matrix4x3d, long long1) {
			matrix4x3d.m00 = (double)UNSAFE.getFloat((Object)null, long1);
			matrix4x3d.m01 = (double)UNSAFE.getFloat((Object)null, long1 + 4L);
			matrix4x3d.m02 = (double)UNSAFE.getFloat((Object)null, long1 + 8L);
			matrix4x3d.m10 = (double)UNSAFE.getFloat((Object)null, long1 + 12L);
			matrix4x3d.m11 = (double)UNSAFE.getFloat((Object)null, long1 + 16L);
			matrix4x3d.m12 = (double)UNSAFE.getFloat((Object)null, long1 + 20L);
			matrix4x3d.m20 = (double)UNSAFE.getFloat((Object)null, long1 + 24L);
			matrix4x3d.m21 = (double)UNSAFE.getFloat((Object)null, long1 + 28L);
			matrix4x3d.m22 = (double)UNSAFE.getFloat((Object)null, long1 + 32L);
			matrix4x3d.m30 = (double)UNSAFE.getFloat((Object)null, long1 + 36L);
			matrix4x3d.m31 = (double)UNSAFE.getFloat((Object)null, long1 + 40L);
			matrix4x3d.m32 = (double)UNSAFE.getFloat((Object)null, long1 + 44L);
		}

		public final void get(Matrix3f matrix3f, long long1) {
			for (int int1 = 0; int1 < 4; ++int1) {
				UNSAFE.putOrderedLong(matrix3f, Matrix3f_m00 + (long)(int1 << 3), UNSAFE.getLong((Object)null, long1 + (long)(int1 << 3)));
			}

			matrix3f.m22 = UNSAFE.getFloat((Object)null, long1 + 32L);
		}

		public final void get(Matrix3d matrix3d, long long1) {
			matrix3d.m00 = UNSAFE.getDouble((Object)null, long1);
			matrix3d.m01 = UNSAFE.getDouble((Object)null, long1 + 8L);
			matrix3d.m02 = UNSAFE.getDouble((Object)null, long1 + 16L);
			matrix3d.m10 = UNSAFE.getDouble((Object)null, long1 + 24L);
			matrix3d.m11 = UNSAFE.getDouble((Object)null, long1 + 32L);
			matrix3d.m12 = UNSAFE.getDouble((Object)null, long1 + 40L);
			matrix3d.m20 = UNSAFE.getDouble((Object)null, long1 + 48L);
			matrix3d.m21 = UNSAFE.getDouble((Object)null, long1 + 56L);
			matrix3d.m22 = UNSAFE.getDouble((Object)null, long1 + 64L);
		}

		public final void getf(Matrix3d matrix3d, long long1) {
			matrix3d.m00 = (double)UNSAFE.getFloat((Object)null, long1);
			matrix3d.m01 = (double)UNSAFE.getFloat((Object)null, long1 + 4L);
			matrix3d.m02 = (double)UNSAFE.getFloat((Object)null, long1 + 8L);
			matrix3d.m10 = (double)UNSAFE.getFloat((Object)null, long1 + 12L);
			matrix3d.m11 = (double)UNSAFE.getFloat((Object)null, long1 + 16L);
			matrix3d.m12 = (double)UNSAFE.getFloat((Object)null, long1 + 20L);
			matrix3d.m20 = (double)UNSAFE.getFloat((Object)null, long1 + 24L);
			matrix3d.m21 = (double)UNSAFE.getFloat((Object)null, long1 + 28L);
			matrix3d.m22 = (double)UNSAFE.getFloat((Object)null, long1 + 32L);
		}

		public final void get(Vector4d vector4d, long long1) {
			for (int int1 = 0; int1 < 4; ++int1) {
				UNSAFE.putOrderedLong(vector4d, Vector4d_x + (long)(int1 << 3), UNSAFE.getLong((Object)null, long1 + (long)(int1 << 3)));
			}
		}

		public final void get(Vector4f vector4f, long long1) {
			UNSAFE.putOrderedLong(vector4f, Vector4f_x, UNSAFE.getLong((Object)null, long1));
			UNSAFE.putOrderedLong(vector4f, Vector4f_x + 8L, UNSAFE.getLong((Object)null, long1 + 8L));
		}

		public final void get(Vector4i vector4i, long long1) {
			UNSAFE.putOrderedLong(vector4i, Vector4i_x, UNSAFE.getLong((Object)null, long1));
			UNSAFE.putOrderedLong(vector4i, Vector4i_x + 8L, UNSAFE.getLong((Object)null, long1 + 8L));
		}

		public final void get(Vector3f vector3f, long long1) {
			UNSAFE.putOrderedLong(vector3f, Vector3f_x, UNSAFE.getLong((Object)null, long1));
			UNSAFE.putFloat(vector3f, Vector3f_x + 8L, UNSAFE.getFloat((Object)null, long1 + 8L));
		}

		public final void get(Vector3d vector3d, long long1) {
			UNSAFE.putDouble(vector3d, Vector3d_x, UNSAFE.getDouble((Object)null, long1));
			UNSAFE.putDouble(vector3d, Vector3d_x + 8L, UNSAFE.getDouble((Object)null, long1 + 8L));
			UNSAFE.putDouble(vector3d, Vector3d_x + 16L, UNSAFE.getDouble((Object)null, long1 + 16L));
		}

		public final void get(Vector3i vector3i, long long1) {
			UNSAFE.putOrderedLong(vector3i, Vector3i_x, UNSAFE.getLong((Object)null, long1));
			UNSAFE.putInt(vector3i, Vector3i_x + 8L, UNSAFE.getInt((Object)null, long1 + 8L));
		}

		public final void get(Vector2f vector2f, long long1) {
			UNSAFE.putOrderedLong(vector2f, Vector2f_x, UNSAFE.getLong((Object)null, long1));
		}

		public final void get(Vector2d vector2d, long long1) {
			UNSAFE.putDouble(vector2d, Vector2d_x, UNSAFE.getDouble((Object)null, long1));
			UNSAFE.putDouble(vector2d, Vector2d_x + 8L, UNSAFE.getDouble((Object)null, long1 + 8L));
		}

		public final void get(Vector2i vector2i, long long1) {
			UNSAFE.putOrderedLong(vector2i, Vector2i_x, UNSAFE.getLong((Object)null, long1));
		}

		public final void copy(Matrix4f matrix4f, Matrix4f matrix4f2) {
			for (int int1 = 0; int1 < 8; ++int1) {
				UNSAFE.putOrderedLong(matrix4f2, Matrix4f_m00 + (long)(int1 << 3), UNSAFE.getLong(matrix4f, Matrix4f_m00 + (long)(int1 << 3)));
			}
		}

		public final void copy(Matrix3f matrix3f, Matrix4f matrix4f) {
			for (int int1 = 0; int1 < 3; ++int1) {
				UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00 + (long)(int1 << 4), UNSAFE.getLong(matrix3f, Matrix3f_m00 + (long)(12 * int1)));
				long long1 = (long)UNSAFE.getInt(matrix3f, Matrix3f_m00 + 8L + (long)(12 * int1)) & 4294967295L;
				UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00 + 8L + (long)(int1 << 4), long1);
			}

			UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00 + 48L, 0L);
			UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00 + 56L, 4575657221408423936L);
		}

		public final void copy(Matrix4f matrix4f, Matrix3f matrix3f) {
			for (int int1 = 0; int1 < 3; ++int1) {
				UNSAFE.putOrderedLong(matrix3f, Matrix3f_m00 + (long)(12 * int1), UNSAFE.getLong(matrix4f, Matrix4f_m00 + (long)(int1 << 4)));
			}

			matrix3f.m02 = matrix4f.m02;
			matrix3f.m12 = matrix4f.m12;
			matrix3f.m22 = matrix4f.m22;
		}

		public final void copy(Matrix3f matrix3f, Matrix4x3f matrix4x3f) {
			for (int int1 = 0; int1 < 4; ++int1) {
				UNSAFE.putOrderedLong(matrix4x3f, Matrix4x3f_m00 + (long)(int1 << 3), UNSAFE.getLong(matrix3f, Matrix3f_m00 + (long)(int1 << 3)));
			}

			UNSAFE.putOrderedLong(matrix4x3f, Matrix4x3f_m00 + 32L, (long)UNSAFE.getInt(matrix3f, Matrix3f_m00 + 32L) & 4294967295L);
			UNSAFE.putOrderedLong(matrix4x3f, Matrix4x3f_m00 + 40L, 0L);
		}

		public final void copy3x3(Matrix4f matrix4f, Matrix4f matrix4f2) {
			for (int int1 = 0; int1 < 3; ++int1) {
				UNSAFE.putOrderedLong(matrix4f2, Matrix4f_m00 + (long)(int1 << 4), UNSAFE.getLong(matrix4f, Matrix4f_m00 + (long)(int1 << 4)));
			}

			matrix4f2.m02 = matrix4f.m02;
			matrix4f2.m12 = matrix4f.m12;
			matrix4f2.m22 = matrix4f.m22;
		}

		public final void copy3x3(Matrix4x3f matrix4x3f, Matrix4x3f matrix4x3f2) {
			for (int int1 = 0; int1 < 4; ++int1) {
				UNSAFE.putOrderedLong(matrix4x3f2, Matrix4x3f_m00 + (long)(int1 << 3), UNSAFE.getLong(matrix4x3f, Matrix4x3f_m00 + (long)(int1 << 3)));
			}

			matrix4x3f2.m22 = matrix4x3f.m22;
		}

		public final void copy3x3(Matrix3f matrix3f, Matrix4x3f matrix4x3f) {
			for (int int1 = 0; int1 < 4; ++int1) {
				UNSAFE.putOrderedLong(matrix4x3f, Matrix4x3f_m00 + (long)(int1 << 3), UNSAFE.getLong(matrix3f, Matrix3f_m00 + (long)(int1 << 3)));
			}

			matrix4x3f.m22 = matrix3f.m22;
		}

		public final void copy3x3(Matrix3f matrix3f, Matrix4f matrix4f) {
			for (int int1 = 0; int1 < 3; ++int1) {
				UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00 + (long)(int1 << 4), UNSAFE.getLong(matrix3f, Matrix3f_m00 + (long)(12 * int1)));
			}

			matrix4f.m02 = matrix3f.m02;
			matrix4f.m12 = matrix3f.m12;
			matrix4f.m22 = matrix3f.m22;
		}

		public final void copy4x3(Matrix4x3f matrix4x3f, Matrix4f matrix4f) {
			for (int int1 = 0; int1 < 4; ++int1) {
				UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00 + (long)(int1 << 4), UNSAFE.getLong(matrix4x3f, Matrix4x3f_m00 + (long)(12 * int1)));
			}

			matrix4f.m02 = matrix4x3f.m02;
			matrix4f.m12 = matrix4x3f.m12;
			matrix4f.m22 = matrix4x3f.m22;
			matrix4f.m32 = matrix4x3f.m32;
		}

		public final void copy4x3(Matrix4f matrix4f, Matrix4f matrix4f2) {
			for (int int1 = 0; int1 < 4; ++int1) {
				UNSAFE.putOrderedLong(matrix4f2, Matrix4f_m00 + (long)(int1 << 4), UNSAFE.getLong(matrix4f, Matrix4f_m00 + (long)(int1 << 4)));
			}

			matrix4f2.m02 = matrix4f.m02;
			matrix4f2.m12 = matrix4f.m12;
			matrix4f2.m22 = matrix4f.m22;
			matrix4f2.m32 = matrix4f.m32;
		}

		public final void copy(Matrix4f matrix4f, Matrix4x3f matrix4x3f) {
			for (int int1 = 0; int1 < 4; ++int1) {
				UNSAFE.putOrderedLong(matrix4x3f, Matrix4x3f_m00 + (long)(12 * int1), UNSAFE.getLong(matrix4f, Matrix4f_m00 + (long)(int1 << 4)));
			}

			matrix4x3f.m02 = matrix4f.m02;
			matrix4x3f.m12 = matrix4f.m12;
			matrix4x3f.m22 = matrix4f.m22;
			matrix4x3f.m32 = matrix4f.m32;
		}

		public final void copy(Matrix4x3f matrix4x3f, Matrix4f matrix4f) {
			for (int int1 = 0; int1 < 4; ++int1) {
				UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00 + (long)(int1 << 4), UNSAFE.getLong(matrix4x3f, Matrix4x3f_m00 + (long)(12 * int1)));
			}

			UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00 + 8L, (long)UNSAFE.getInt(matrix4x3f, Matrix4x3f_m00 + 8L) & 4294967295L);
			UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00 + 24L, (long)UNSAFE.getInt(matrix4x3f, Matrix4x3f_m00 + 20L) & 4294967295L);
			UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00 + 40L, (long)UNSAFE.getInt(matrix4x3f, Matrix4x3f_m00 + 32L) & 4294967295L);
			UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00 + 56L, 4575657221408423936L | (long)UNSAFE.getInt(matrix4x3f, Matrix4x3f_m00 + 44L) & 4294967295L);
		}

		public final void copy(Matrix4x3f matrix4x3f, Matrix4x3f matrix4x3f2) {
			for (int int1 = 0; int1 < 6; ++int1) {
				UNSAFE.putOrderedLong(matrix4x3f2, Matrix4x3f_m00 + (long)(int1 << 3), UNSAFE.getLong(matrix4x3f, Matrix4x3f_m00 + (long)(int1 << 3)));
			}
		}

		public final void copy(Matrix3f matrix3f, Matrix3f matrix3f2) {
			for (int int1 = 0; int1 < 4; ++int1) {
				UNSAFE.putOrderedLong(matrix3f2, Matrix3f_m00 + (long)(int1 << 3), UNSAFE.getLong(matrix3f, Matrix3f_m00 + (long)(int1 << 3)));
			}

			matrix3f2.m22 = matrix3f.m22;
		}

		public final void copy(Vector4f vector4f, Vector4f vector4f2) {
			UNSAFE.putOrderedLong(vector4f2, Vector4f_x, UNSAFE.getLong(vector4f, Vector4f_x));
			UNSAFE.putOrderedLong(vector4f2, Vector4f_x + 8L, UNSAFE.getLong(vector4f, Vector4f_x + 8L));
		}

		public final void copy(Vector4i vector4i, Vector4i vector4i2) {
			UNSAFE.putOrderedLong(vector4i2, Vector4i_x, UNSAFE.getLong(vector4i, Vector4i_x));
			UNSAFE.putOrderedLong(vector4i2, Vector4i_x + 8L, UNSAFE.getLong(vector4i, Vector4i_x + 8L));
		}

		public final void copy(Quaternionf quaternionf, Quaternionf quaternionf2) {
			UNSAFE.putOrderedLong(quaternionf2, Quaternionf_x, UNSAFE.getLong(quaternionf, Quaternionf_x));
			UNSAFE.putOrderedLong(quaternionf2, Quaternionf_x + 8L, UNSAFE.getLong(quaternionf, Quaternionf_x + 8L));
		}

		public final void copy(float[] floatArray, int int1, Matrix4f matrix4f) {
			for (int int2 = 0; int2 < 8; ++int2) {
				UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00 + (long)(int2 << 3), UNSAFE.getLong(floatArray, floatArrayOffset + (long)(int1 << 2) + (long)(int2 << 3)));
			}
		}

		public final void copy(float[] floatArray, int int1, Matrix3f matrix3f) {
			for (int int2 = 0; int2 < 4; ++int2) {
				UNSAFE.putOrderedLong(matrix3f, Matrix3f_m00 + (long)(int2 << 3), UNSAFE.getLong(floatArray, floatArrayOffset + (long)(int1 << 2) + (long)(int2 << 3)));
			}

			UNSAFE.putFloat(matrix3f, Matrix3f_m00 + 32L, UNSAFE.getFloat(floatArray, floatArrayOffset + (long)(int1 << 2) + 32L));
		}

		public final void copy(float[] floatArray, int int1, Matrix4x3f matrix4x3f) {
			for (int int2 = 0; int2 < 6; ++int2) {
				UNSAFE.putOrderedLong(matrix4x3f, Matrix4x3f_m00 + (long)(int2 << 3), UNSAFE.getLong(floatArray, floatArrayOffset + (long)(int1 << 2) + (long)(int2 << 3)));
			}
		}

		public final void copy(Matrix4f matrix4f, float[] floatArray, int int1) {
			for (int int2 = 0; int2 < 8; ++int2) {
				UNSAFE.putOrderedLong(floatArray, floatArrayOffset + (long)(int1 << 2) + (long)(int2 << 3), UNSAFE.getLong(matrix4f, Matrix4f_m00 + (long)(int2 << 3)));
			}
		}

		public final void copy(Matrix3f matrix3f, float[] floatArray, int int1) {
			for (int int2 = 0; int2 < 4; ++int2) {
				UNSAFE.putOrderedLong(floatArray, floatArrayOffset + (long)(int1 << 2) + (long)(int2 << 3), UNSAFE.getLong(matrix3f, Matrix3f_m00 + (long)(int2 << 3)));
			}

			UNSAFE.putFloat(floatArray, floatArrayOffset + (long)(int1 << 2) + 32L, UNSAFE.getFloat(matrix3f, Matrix3f_m00 + 32L));
		}

		public final void copy(Matrix4x3f matrix4x3f, float[] floatArray, int int1) {
			for (int int2 = 0; int2 < 6; ++int2) {
				UNSAFE.putOrderedLong(floatArray, floatArrayOffset + (long)(int1 << 2) + (long)(int2 << 3), UNSAFE.getLong(matrix4x3f, Matrix4x3f_m00 + (long)(int2 << 3)));
			}
		}

		public final void identity(Matrix4f matrix4f) {
			UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00, 1065353216L);
			UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00 + 8L, 0L);
			UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00 + 16L, 4575657221408423936L);
			UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00 + 24L, 0L);
			UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00 + 32L, 0L);
			UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00 + 40L, 1065353216L);
			UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00 + 48L, 0L);
			UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00 + 56L, 4575657221408423936L);
		}

		public final void identity(Matrix4x3f matrix4x3f) {
			UNSAFE.putOrderedLong(matrix4x3f, Matrix4x3f_m00, 1065353216L);
			UNSAFE.putOrderedLong(matrix4x3f, Matrix4x3f_m00 + 8L, 0L);
			UNSAFE.putOrderedLong(matrix4x3f, Matrix4x3f_m00 + 16L, 1065353216L);
			UNSAFE.putOrderedLong(matrix4x3f, Matrix4x3f_m00 + 24L, 0L);
			UNSAFE.putOrderedLong(matrix4x3f, Matrix4x3f_m00 + 32L, 1065353216L);
			UNSAFE.putOrderedLong(matrix4x3f, Matrix4x3f_m00 + 40L, 0L);
		}

		public final void identity(Matrix3f matrix3f) {
			UNSAFE.putOrderedLong(matrix3f, Matrix3f_m00, 1065353216L);
			UNSAFE.putOrderedLong(matrix3f, Matrix3f_m00 + 8L, 0L);
			UNSAFE.putOrderedLong(matrix3f, Matrix3f_m00 + 16L, 1065353216L);
			UNSAFE.putOrderedLong(matrix3f, Matrix3f_m00 + 24L, 0L);
			matrix3f.m22 = 1.0F;
		}

		public final void identity(Quaternionf quaternionf) {
			UNSAFE.putOrderedLong(quaternionf, Quaternionf_x, 0L);
			UNSAFE.putOrderedLong(quaternionf, Quaternionf_x + 8L, 4575657221408423936L);
		}

		public final void swap(Matrix4f matrix4f, Matrix4f matrix4f2) {
			for (int int1 = 0; int1 < 8; ++int1) {
				long long1 = UNSAFE.getLong(matrix4f, Matrix4f_m00 + (long)(int1 << 3));
				UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00 + (long)(int1 << 3), UNSAFE.getLong(matrix4f2, Matrix4f_m00 + (long)(int1 << 3)));
				UNSAFE.putOrderedLong(matrix4f2, Matrix4f_m00 + (long)(int1 << 3), long1);
			}
		}

		public final void swap(Matrix4x3f matrix4x3f, Matrix4x3f matrix4x3f2) {
			for (int int1 = 0; int1 < 6; ++int1) {
				long long1 = UNSAFE.getLong(matrix4x3f, Matrix4x3f_m00 + (long)(int1 << 3));
				UNSAFE.putOrderedLong(matrix4x3f, Matrix4x3f_m00 + (long)(int1 << 3), UNSAFE.getLong(matrix4x3f2, Matrix4x3f_m00 + (long)(int1 << 3)));
				UNSAFE.putOrderedLong(matrix4x3f2, Matrix4x3f_m00 + (long)(int1 << 3), long1);
			}
		}

		public final void swap(Matrix3f matrix3f, Matrix3f matrix3f2) {
			for (int int1 = 0; int1 < 4; ++int1) {
				long long1 = UNSAFE.getLong(matrix3f, Matrix3f_m00 + (long)(int1 << 3));
				UNSAFE.putOrderedLong(matrix3f, Matrix3f_m00 + (long)(int1 << 3), UNSAFE.getLong(matrix3f2, Matrix3f_m00 + (long)(int1 << 3)));
				UNSAFE.putOrderedLong(matrix3f2, Matrix3f_m00 + (long)(int1 << 3), long1);
			}

			float float1 = matrix3f.m22;
			matrix3f.m22 = matrix3f2.m22;
			matrix3f2.m22 = float1;
		}

		public final void zero(Matrix4f matrix4f) {
			for (int int1 = 0; int1 < 8; ++int1) {
				UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00 + (long)(int1 << 3), 0L);
			}
		}

		public final void zero(Matrix4x3f matrix4x3f) {
			for (int int1 = 0; int1 < 6; ++int1) {
				UNSAFE.putOrderedLong(matrix4x3f, Matrix4x3f_m00 + (long)(int1 << 3), 0L);
			}
		}

		public final void zero(Matrix3f matrix3f) {
			for (int int1 = 0; int1 < 4; ++int1) {
				UNSAFE.putOrderedLong(matrix3f, Matrix3f_m00 + (long)(int1 << 3), 0L);
			}

			matrix3f.m22 = 0.0F;
		}

		public final void zero(Vector4f vector4f) {
			UNSAFE.putOrderedLong(vector4f, Vector4f_x, 0L);
			UNSAFE.putOrderedLong(vector4f, Vector4f_x + 8L, 0L);
		}

		public final void zero(Vector4i vector4i) {
			UNSAFE.putOrderedLong(vector4i, Vector4i_x, 0L);
			UNSAFE.putOrderedLong(vector4i, Vector4i_x + 8L, 0L);
		}

		private void putMatrix3f(Quaternionf quaternionf, long long1) {
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
			UNSAFE.putFloat((Object)null, long1, 1.0F - float5 - float6);
			UNSAFE.putFloat((Object)null, long1 + 4L, float7 + float12);
			UNSAFE.putFloat((Object)null, long1 + 8L, float8 - float11);
			UNSAFE.putFloat((Object)null, long1 + 12L, float7 - float12);
			UNSAFE.putFloat((Object)null, long1 + 16L, 1.0F - float6 - float4);
			UNSAFE.putFloat((Object)null, long1 + 20L, float10 + float9);
			UNSAFE.putFloat((Object)null, long1 + 24L, float8 + float11);
			UNSAFE.putFloat((Object)null, long1 + 28L, float10 - float9);
			UNSAFE.putFloat((Object)null, long1 + 32L, 1.0F - float5 - float4);
		}

		private void putMatrix4f(Quaternionf quaternionf, long long1) {
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
			UNSAFE.putFloat((Object)null, long1, 1.0F - float5 - float6);
			UNSAFE.putFloat((Object)null, long1 + 4L, float7 + float12);
			UNSAFE.putOrderedLong((Object)null, long1 + 8L, (long)Float.floatToRawIntBits(float8 - float11) & 4294967295L);
			UNSAFE.putFloat((Object)null, long1 + 16L, float7 - float12);
			UNSAFE.putFloat((Object)null, long1 + 20L, 1.0F - float6 - float4);
			UNSAFE.putOrderedLong((Object)null, long1 + 24L, (long)Float.floatToRawIntBits(float10 + float9) & 4294967295L);
			UNSAFE.putFloat((Object)null, long1 + 32L, float8 + float11);
			UNSAFE.putFloat((Object)null, long1 + 36L, float10 - float9);
			UNSAFE.putOrderedLong((Object)null, long1 + 40L, (long)Float.floatToRawIntBits(1.0F - float5 - float4) & 4294967295L);
			UNSAFE.putOrderedLong((Object)null, long1 + 48L, 0L);
			UNSAFE.putOrderedLong((Object)null, long1 + 56L, 4575657221408423936L);
		}

		private void putMatrix4x3f(Quaternionf quaternionf, long long1) {
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
			UNSAFE.putFloat((Object)null, long1, 1.0F - float5 - float6);
			UNSAFE.putFloat((Object)null, long1 + 4L, float7 + float12);
			UNSAFE.putFloat((Object)null, long1 + 8L, float8 - float11);
			UNSAFE.putFloat((Object)null, long1 + 12L, float7 - float12);
			UNSAFE.putFloat((Object)null, long1 + 16L, 1.0F - float6 - float4);
			UNSAFE.putFloat((Object)null, long1 + 20L, float10 + float9);
			UNSAFE.putFloat((Object)null, long1 + 24L, float8 + float11);
			UNSAFE.putFloat((Object)null, long1 + 28L, float10 - float9);
			UNSAFE.putFloat((Object)null, long1 + 32L, 1.0F - float5 - float4);
			UNSAFE.putOrderedLong((Object)null, long1 + 36L, 0L);
			UNSAFE.putFloat((Object)null, long1 + 44L, 0.0F);
		}

		private static void throwNoDirectBufferException() {
			throw new IllegalArgumentException("Must use a direct buffer");
		}

		public final void putMatrix3f(Quaternionf quaternionf, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			long long1 = this.addressOf(byteBuffer) + (long)int1;
			this.putMatrix3f(quaternionf, long1);
		}

		public final void putMatrix3f(Quaternionf quaternionf, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG && !floatBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			long long1 = this.addressOf(floatBuffer) + (long)(int1 << 2);
			this.putMatrix3f(quaternionf, long1);
		}

		public final void putMatrix4f(Quaternionf quaternionf, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			long long1 = this.addressOf(byteBuffer) + (long)int1;
			this.putMatrix4f(quaternionf, long1);
		}

		public final void putMatrix4f(Quaternionf quaternionf, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG && !floatBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			long long1 = this.addressOf(floatBuffer) + (long)(int1 << 2);
			this.putMatrix4f(quaternionf, long1);
		}

		public final void putMatrix4x3f(Quaternionf quaternionf, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			long long1 = this.addressOf(byteBuffer) + (long)int1;
			this.putMatrix4x3f(quaternionf, long1);
		}

		public final void putMatrix4x3f(Quaternionf quaternionf, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG && !floatBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			long long1 = this.addressOf(floatBuffer) + (long)(int1 << 2);
			this.putMatrix4x3f(quaternionf, long1);
		}

		public final void put(Matrix4f matrix4f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG && !floatBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put(matrix4f, this.addressOf(floatBuffer) + (long)(int1 << 2));
		}

		public final void put(Matrix4f matrix4f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put(matrix4f, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void put(Matrix4x3f matrix4x3f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG && !floatBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put(matrix4x3f, this.addressOf(floatBuffer) + (long)(int1 << 2));
		}

		public final void put(Matrix4x3f matrix4x3f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put(matrix4x3f, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void put4x4(Matrix4x3f matrix4x3f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG && !floatBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put4x4(matrix4x3f, this.addressOf(floatBuffer) + (long)(int1 << 2));
		}

		public final void put4x4(Matrix4x3f matrix4x3f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put4x4(matrix4x3f, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void put4x4(Matrix4x3d matrix4x3d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG && !doubleBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put4x4(matrix4x3d, this.addressOf(doubleBuffer) + (long)(int1 << 3));
		}

		public final void put4x4(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put4x4(matrix4x3d, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void putTransposed(Matrix4f matrix4f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG && !floatBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.putTransposed(matrix4f, this.addressOf(floatBuffer) + (long)(int1 << 2));
		}

		public final void putTransposed(Matrix4f matrix4f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.putTransposed(matrix4f, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void put4x3Transposed(Matrix4f matrix4f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG && !floatBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put4x3Transposed(matrix4f, this.addressOf(floatBuffer) + (long)(int1 << 2));
		}

		public final void put4x3Transposed(Matrix4f matrix4f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put4x3Transposed(matrix4f, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void putTransposed(Matrix4x3f matrix4x3f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG && !floatBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.putTransposed(matrix4x3f, this.addressOf(floatBuffer) + (long)(int1 << 2));
		}

		public final void putTransposed(Matrix4x3f matrix4x3f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.putTransposed(matrix4x3f, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void putTransposed(Matrix3f matrix3f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG && !floatBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.putTransposed(matrix3f, this.addressOf(floatBuffer) + (long)(int1 << 2));
		}

		public final void putTransposed(Matrix3f matrix3f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.putTransposed(matrix3f, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void put(Matrix4d matrix4d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG && !doubleBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put(matrix4d, this.addressOf(doubleBuffer) + (long)(int1 << 3));
		}

		public final void put(Matrix4d matrix4d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put(matrix4d, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void put(Matrix4x3d matrix4x3d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG && !doubleBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put(matrix4x3d, this.addressOf(doubleBuffer) + (long)(int1 << 3));
		}

		public final void put(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put(matrix4x3d, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void putf(Matrix4d matrix4d, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG && !floatBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.putf(matrix4d, this.addressOf(floatBuffer) + (long)(int1 << 2));
		}

		public final void putf(Matrix4d matrix4d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.putf(matrix4d, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void putf(Matrix4x3d matrix4x3d, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG && !floatBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.putf(matrix4x3d, this.addressOf(floatBuffer) + (long)(int1 << 2));
		}

		public final void putf(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.putf(matrix4x3d, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void putTransposed(Matrix4d matrix4d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG && !doubleBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.putTransposed(matrix4d, this.addressOf(doubleBuffer) + (long)(int1 << 3));
		}

		public final void putTransposed(Matrix4d matrix4d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.putTransposed(matrix4d, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void put4x3Transposed(Matrix4d matrix4d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG && !doubleBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put4x3Transposed(matrix4d, this.addressOf(doubleBuffer) + (long)(int1 << 3));
		}

		public final void put4x3Transposed(Matrix4d matrix4d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put4x3Transposed(matrix4d, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void putTransposed(Matrix4x3d matrix4x3d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG && !doubleBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.putTransposed(matrix4x3d, this.addressOf(doubleBuffer) + (long)(int1 << 3));
		}

		public final void putTransposed(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.putTransposed(matrix4x3d, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void putfTransposed(Matrix4d matrix4d, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG && !floatBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.putfTransposed(matrix4d, this.addressOf(floatBuffer) + (long)(int1 << 2));
		}

		public final void putfTransposed(Matrix4d matrix4d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.putfTransposed(matrix4d, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void putfTransposed(Matrix4x3d matrix4x3d, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG && !floatBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.putfTransposed(matrix4x3d, this.addressOf(floatBuffer) + (long)(int1 << 2));
		}

		public final void putfTransposed(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.putfTransposed(matrix4x3d, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void put(Matrix3f matrix3f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG && !floatBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put(matrix3f, this.addressOf(floatBuffer) + (long)(int1 << 2));
		}

		public final void put(Matrix3f matrix3f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put(matrix3f, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void put(Matrix3d matrix3d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG && !doubleBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put(matrix3d, this.addressOf(doubleBuffer) + (long)(int1 << 3));
		}

		public final void put(Matrix3d matrix3d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put(matrix3d, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void putf(Matrix3d matrix3d, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG && !floatBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.putf(matrix3d, this.addressOf(floatBuffer) + (long)(int1 << 2));
		}

		public final void putf(Matrix3d matrix3d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.putf(matrix3d, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void put(Vector4d vector4d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG && !doubleBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put(vector4d, this.addressOf(doubleBuffer) + (long)(int1 << 3));
		}

		public final void put(Vector4d vector4d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put(vector4d, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void put(Vector4f vector4f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG && !floatBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put(vector4f, this.addressOf(floatBuffer) + (long)(int1 << 2));
		}

		public final void put(Vector4f vector4f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put(vector4f, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void put(Vector4i vector4i, int int1, IntBuffer intBuffer) {
			if (Options.DEBUG && !intBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put(vector4i, this.addressOf(intBuffer) + (long)(int1 << 2));
		}

		public final void put(Vector4i vector4i, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put(vector4i, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void put(Vector3f vector3f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG && !floatBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put(vector3f, this.addressOf(floatBuffer) + (long)(int1 << 2));
		}

		public final void put(Vector3f vector3f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put(vector3f, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void put(Vector3d vector3d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG && !doubleBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put(vector3d, this.addressOf(doubleBuffer) + (long)(int1 << 3));
		}

		public final void put(Vector3d vector3d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put(vector3d, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void put(Vector3i vector3i, int int1, IntBuffer intBuffer) {
			if (Options.DEBUG && !intBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put(vector3i, this.addressOf(intBuffer) + (long)(int1 << 2));
		}

		public final void put(Vector3i vector3i, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put(vector3i, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void put(Vector2f vector2f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG && !floatBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put(vector2f, this.addressOf(floatBuffer) + (long)(int1 << 2));
		}

		public final void put(Vector2f vector2f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put(vector2f, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void put(Vector2d vector2d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG && !doubleBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put(vector2d, this.addressOf(doubleBuffer) + (long)(int1 << 3));
		}

		public final void put(Vector2d vector2d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put(vector2d, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void put(Vector2i vector2i, int int1, IntBuffer intBuffer) {
			if (Options.DEBUG && !intBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put(vector2i, this.addressOf(intBuffer) + (long)(int1 << 2));
		}

		public final void put(Vector2i vector2i, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.put(vector2i, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void get(Matrix4f matrix4f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG && !floatBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.get(matrix4f, this.addressOf(floatBuffer) + (long)(int1 << 2));
		}

		public final void get(Matrix4f matrix4f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.get(matrix4f, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void get(Matrix4x3f matrix4x3f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG && !floatBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.get(matrix4x3f, this.addressOf(floatBuffer) + (long)(int1 << 2));
		}

		public final void get(Matrix4x3f matrix4x3f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.get(matrix4x3f, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void get(Matrix4d matrix4d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG && !doubleBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.get(matrix4d, this.addressOf(doubleBuffer) + (long)(int1 << 3));
		}

		public final void get(Matrix4d matrix4d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.get(matrix4d, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void get(Matrix4x3d matrix4x3d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG && !doubleBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.get(matrix4x3d, this.addressOf(doubleBuffer) + (long)(int1 << 3));
		}

		public final void get(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.get(matrix4x3d, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void getf(Matrix4d matrix4d, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG && !floatBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.getf(matrix4d, this.addressOf(floatBuffer) + (long)(int1 << 2));
		}

		public final void getf(Matrix4d matrix4d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.getf(matrix4d, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void getf(Matrix4x3d matrix4x3d, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG && !floatBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.getf(matrix4x3d, this.addressOf(floatBuffer) + (long)(int1 << 2));
		}

		public final void getf(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.getf(matrix4x3d, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void get(Matrix3f matrix3f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG && !floatBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.get(matrix3f, this.addressOf(floatBuffer) + (long)(int1 << 2));
		}

		public final void get(Matrix3f matrix3f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.get(matrix3f, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void get(Matrix3d matrix3d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG && !doubleBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.get(matrix3d, this.addressOf(doubleBuffer) + (long)(int1 << 3));
		}

		public final void get(Matrix3d matrix3d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.get(matrix3d, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void getf(Matrix3d matrix3d, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG && !floatBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.getf(matrix3d, this.addressOf(floatBuffer) + (long)(int1 << 2));
		}

		public final void getf(Matrix3d matrix3d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.getf(matrix3d, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void get(Vector4d vector4d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG && !doubleBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.get(vector4d, this.addressOf(doubleBuffer) + (long)(int1 << 3));
		}

		public final void get(Vector4d vector4d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.get(vector4d, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void get(Vector4f vector4f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG && !floatBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.get(vector4f, this.addressOf(floatBuffer) + (long)(int1 << 2));
		}

		public final void get(Vector4f vector4f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.get(vector4f, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void get(Vector4i vector4i, int int1, IntBuffer intBuffer) {
			if (Options.DEBUG && !intBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.get(vector4i, this.addressOf(intBuffer) + (long)(int1 << 2));
		}

		public final void get(Vector4i vector4i, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.get(vector4i, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void get(Vector3f vector3f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG && !floatBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.get(vector3f, this.addressOf(floatBuffer) + (long)(int1 << 2));
		}

		public final void get(Vector3f vector3f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.get(vector3f, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void get(Vector3d vector3d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG && !doubleBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.get(vector3d, this.addressOf(doubleBuffer) + (long)(int1 << 3));
		}

		public final void get(Vector3d vector3d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.get(vector3d, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void get(Vector3i vector3i, int int1, IntBuffer intBuffer) {
			if (Options.DEBUG && !intBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.get(vector3i, this.addressOf(intBuffer) + (long)(int1 << 2));
		}

		public final void get(Vector3i vector3i, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.get(vector3i, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void get(Vector2f vector2f, int int1, FloatBuffer floatBuffer) {
			if (Options.DEBUG && !floatBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.get(vector2f, this.addressOf(floatBuffer) + (long)(int1 << 2));
		}

		public final void get(Vector2f vector2f, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.get(vector2f, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void get(Vector2d vector2d, int int1, DoubleBuffer doubleBuffer) {
			if (Options.DEBUG && !doubleBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.get(vector2d, this.addressOf(doubleBuffer) + (long)(int1 << 3));
		}

		public final void get(Vector2d vector2d, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.get(vector2d, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void get(Vector2i vector2i, int int1, IntBuffer intBuffer) {
			if (Options.DEBUG && !intBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.get(vector2i, this.addressOf(intBuffer) + (long)(int1 << 2));
		}

		public final void get(Vector2i vector2i, int int1, ByteBuffer byteBuffer) {
			if (Options.DEBUG && !byteBuffer.isDirect()) {
				throwNoDirectBufferException();
			}

			this.get(vector2i, this.addressOf(byteBuffer) + (long)int1);
		}

		public final void set(Matrix4f matrix4f, Vector4f vector4f, Vector4f vector4f2, Vector4f vector4f3, Vector4f vector4f4) {
			UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00, UNSAFE.getLong(vector4f, Vector4f_x));
			UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00 + 8L, UNSAFE.getLong(vector4f, Vector4f_x + 8L));
			UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00 + 16L, UNSAFE.getLong(vector4f2, Vector4f_x));
			UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00 + 24L, UNSAFE.getLong(vector4f2, Vector4f_x + 8L));
			UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00 + 32L, UNSAFE.getLong(vector4f3, Vector4f_x));
			UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00 + 40L, UNSAFE.getLong(vector4f3, Vector4f_x + 8L));
			UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00 + 48L, UNSAFE.getLong(vector4f4, Vector4f_x));
			UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00 + 56L, UNSAFE.getLong(vector4f4, Vector4f_x + 8L));
		}

		public final void set(Matrix4x3f matrix4x3f, Vector3f vector3f, Vector3f vector3f2, Vector3f vector3f3, Vector3f vector3f4) {
			UNSAFE.putOrderedLong(matrix4x3f, Matrix4x3f_m00, UNSAFE.getLong(vector3f, Vector3f_x));
			UNSAFE.putOrderedLong(matrix4x3f, Matrix4x3f_m00 + 12L, UNSAFE.getLong(vector3f2, Vector3f_x));
			UNSAFE.putOrderedLong(matrix4x3f, Matrix4x3f_m00 + 24L, UNSAFE.getLong(vector3f3, Vector3f_x));
			UNSAFE.putOrderedLong(matrix4x3f, Matrix4x3f_m00 + 36L, UNSAFE.getLong(vector3f4, Vector3f_x));
			matrix4x3f.m02 = vector3f.z;
			matrix4x3f.m12 = vector3f2.z;
			matrix4x3f.m22 = vector3f3.z;
			matrix4x3f.m32 = vector3f4.z;
		}

		public final void set(Matrix3f matrix3f, Vector3f vector3f, Vector3f vector3f2, Vector3f vector3f3) {
			UNSAFE.putOrderedLong(matrix3f, Matrix3f_m00, UNSAFE.getLong(vector3f, Vector3f_x));
			UNSAFE.putOrderedLong(matrix3f, Matrix3f_m00 + 12L, UNSAFE.getLong(vector3f2, Vector3f_x));
			UNSAFE.putOrderedLong(matrix3f, Matrix3f_m00 + 24L, UNSAFE.getLong(vector3f3, Vector3f_x));
			matrix3f.m02 = vector3f.z;
			matrix3f.m12 = vector3f2.z;
			matrix3f.m22 = vector3f3.z;
		}

		public final void putColumn0(Matrix4f matrix4f, Vector4f vector4f) {
			UNSAFE.putOrderedLong(vector4f, Vector4f_x, UNSAFE.getLong(matrix4f, Matrix4f_m00));
			UNSAFE.putOrderedLong(vector4f, Vector4f_x + 8L, UNSAFE.getLong(matrix4f, Matrix4f_m00 + 8L));
		}

		public final void putColumn1(Matrix4f matrix4f, Vector4f vector4f) {
			UNSAFE.putOrderedLong(vector4f, Vector4f_x, UNSAFE.getLong(matrix4f, Matrix4f_m00 + 16L));
			UNSAFE.putOrderedLong(vector4f, Vector4f_x + 8L, UNSAFE.getLong(matrix4f, Matrix4f_m00 + 24L));
		}

		public final void putColumn2(Matrix4f matrix4f, Vector4f vector4f) {
			UNSAFE.putOrderedLong(vector4f, Vector4f_x, UNSAFE.getLong(matrix4f, Matrix4f_m00 + 32L));
			UNSAFE.putOrderedLong(vector4f, Vector4f_x + 8L, UNSAFE.getLong(matrix4f, Matrix4f_m00 + 40L));
		}

		public final void putColumn3(Matrix4f matrix4f, Vector4f vector4f) {
			UNSAFE.putOrderedLong(vector4f, Vector4f_x, UNSAFE.getLong(matrix4f, Matrix4f_m00 + 48L));
			UNSAFE.putOrderedLong(vector4f, Vector4f_x + 8L, UNSAFE.getLong(matrix4f, Matrix4f_m00 + 56L));
		}

		public final void getColumn0(Matrix4f matrix4f, Vector4f vector4f) {
			UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00, UNSAFE.getLong(vector4f, Vector4f_x));
			UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00 + 8L, UNSAFE.getLong(vector4f, Vector4f_x + 8L));
		}

		public final void getColumn1(Matrix4f matrix4f, Vector4f vector4f) {
			UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00 + 16L, UNSAFE.getLong(vector4f, Vector4f_x));
			UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00 + 24L, UNSAFE.getLong(vector4f, Vector4f_x + 8L));
		}

		public final void getColumn2(Matrix4f matrix4f, Vector4f vector4f) {
			UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00 + 32L, UNSAFE.getLong(vector4f, Vector4f_x));
			UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00 + 40L, UNSAFE.getLong(vector4f, Vector4f_x + 8L));
		}

		public final void getColumn3(Matrix4f matrix4f, Vector4f vector4f) {
			UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00 + 48L, UNSAFE.getLong(vector4f, Vector4f_x));
			UNSAFE.putOrderedLong(matrix4f, Matrix4f_m00 + 56L, UNSAFE.getLong(vector4f, Vector4f_x + 8L));
		}

		public final void broadcast(float float1, Vector4f vector4f) {
			int int1 = Float.floatToRawIntBits(float1);
			long long1 = (long)int1 & 4294967295L;
			long long2 = long1 | long1 << 32;
			UNSAFE.putOrderedLong(vector4f, Vector4f_x, long2);
			UNSAFE.putOrderedLong(vector4f, Vector4f_x + 8L, long2);
		}

		public final void broadcast(int int1, Vector4i vector4i) {
			long long1 = (long)int1 & 4294967295L;
			long long2 = long1 | long1 << 32;
			UNSAFE.putOrderedLong(vector4i, Vector4i_x, long2);
			UNSAFE.putOrderedLong(vector4i, Vector4i_x + 8L, long2);
		}

		static  {
		try {
			ADDRESS = UNSAFE.objectFieldOffset(getDeclaredField(Buffer.class, "address"));
			Matrix4f_m00 = checkMatrix4f();
			Matrix4x3f_m00 = checkMatrix4x3f();
			Matrix3f_m00 = checkMatrix3f();
			Vector4f_x = checkVector4f();
			Vector4d_x = checkVector4d();
			Vector4i_x = checkVector4i();
			Vector3f_x = checkVector3f();
			Vector3d_x = checkVector3d();
			Vector3i_x = checkVector3i();
			Vector2f_x = checkVector2f();
			Vector2d_x = checkVector2d();
			Vector2i_x = checkVector2i();
			Quaternionf_x = checkQuaternionf();
			floatArrayOffset = (long)UNSAFE.arrayBaseOffset(float[].class);
			Unsafe.class.getDeclaredMethod("getLong", Object.class, Long.TYPE);
			Unsafe.class.getDeclaredMethod("putOrderedLong", Object.class, Long.TYPE, Long.TYPE);
		} catch (NoSuchFieldException var1) {
			throw new UnsupportedOperationException();
		} catch (NoSuchMethodException var2) {
			throw new UnsupportedOperationException();
		}
		}
	}

	public static final class MemUtilNIO extends MemUtil {

		public MemUtil.MemUtilUnsafe UNSAFE() {
			return null;
		}

		private void put0(Matrix4f matrix4f, FloatBuffer floatBuffer) {
			floatBuffer.put(0, matrix4f.m00);
			floatBuffer.put(1, matrix4f.m01);
			floatBuffer.put(2, matrix4f.m02);
			floatBuffer.put(3, matrix4f.m03);
			floatBuffer.put(4, matrix4f.m10);
			floatBuffer.put(5, matrix4f.m11);
			floatBuffer.put(6, matrix4f.m12);
			floatBuffer.put(7, matrix4f.m13);
			floatBuffer.put(8, matrix4f.m20);
			floatBuffer.put(9, matrix4f.m21);
			floatBuffer.put(10, matrix4f.m22);
			floatBuffer.put(11, matrix4f.m23);
			floatBuffer.put(12, matrix4f.m30);
			floatBuffer.put(13, matrix4f.m31);
			floatBuffer.put(14, matrix4f.m32);
			floatBuffer.put(15, matrix4f.m33);
		}

		private void putN(Matrix4f matrix4f, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, matrix4f.m00);
			floatBuffer.put(int1 + 1, matrix4f.m01);
			floatBuffer.put(int1 + 2, matrix4f.m02);
			floatBuffer.put(int1 + 3, matrix4f.m03);
			floatBuffer.put(int1 + 4, matrix4f.m10);
			floatBuffer.put(int1 + 5, matrix4f.m11);
			floatBuffer.put(int1 + 6, matrix4f.m12);
			floatBuffer.put(int1 + 7, matrix4f.m13);
			floatBuffer.put(int1 + 8, matrix4f.m20);
			floatBuffer.put(int1 + 9, matrix4f.m21);
			floatBuffer.put(int1 + 10, matrix4f.m22);
			floatBuffer.put(int1 + 11, matrix4f.m23);
			floatBuffer.put(int1 + 12, matrix4f.m30);
			floatBuffer.put(int1 + 13, matrix4f.m31);
			floatBuffer.put(int1 + 14, matrix4f.m32);
			floatBuffer.put(int1 + 15, matrix4f.m33);
		}

		public void put(Matrix4f matrix4f, int int1, FloatBuffer floatBuffer) {
			if (int1 == 0) {
				this.put0(matrix4f, floatBuffer);
			} else {
				this.putN(matrix4f, int1, floatBuffer);
			}
		}

		private void put0(Matrix4f matrix4f, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(0, matrix4f.m00);
			byteBuffer.putFloat(4, matrix4f.m01);
			byteBuffer.putFloat(8, matrix4f.m02);
			byteBuffer.putFloat(12, matrix4f.m03);
			byteBuffer.putFloat(16, matrix4f.m10);
			byteBuffer.putFloat(20, matrix4f.m11);
			byteBuffer.putFloat(24, matrix4f.m12);
			byteBuffer.putFloat(28, matrix4f.m13);
			byteBuffer.putFloat(32, matrix4f.m20);
			byteBuffer.putFloat(36, matrix4f.m21);
			byteBuffer.putFloat(40, matrix4f.m22);
			byteBuffer.putFloat(44, matrix4f.m23);
			byteBuffer.putFloat(48, matrix4f.m30);
			byteBuffer.putFloat(52, matrix4f.m31);
			byteBuffer.putFloat(56, matrix4f.m32);
			byteBuffer.putFloat(60, matrix4f.m33);
		}

		private final void putN(Matrix4f matrix4f, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, matrix4f.m00);
			byteBuffer.putFloat(int1 + 4, matrix4f.m01);
			byteBuffer.putFloat(int1 + 8, matrix4f.m02);
			byteBuffer.putFloat(int1 + 12, matrix4f.m03);
			byteBuffer.putFloat(int1 + 16, matrix4f.m10);
			byteBuffer.putFloat(int1 + 20, matrix4f.m11);
			byteBuffer.putFloat(int1 + 24, matrix4f.m12);
			byteBuffer.putFloat(int1 + 28, matrix4f.m13);
			byteBuffer.putFloat(int1 + 32, matrix4f.m20);
			byteBuffer.putFloat(int1 + 36, matrix4f.m21);
			byteBuffer.putFloat(int1 + 40, matrix4f.m22);
			byteBuffer.putFloat(int1 + 44, matrix4f.m23);
			byteBuffer.putFloat(int1 + 48, matrix4f.m30);
			byteBuffer.putFloat(int1 + 52, matrix4f.m31);
			byteBuffer.putFloat(int1 + 56, matrix4f.m32);
			byteBuffer.putFloat(int1 + 60, matrix4f.m33);
		}

		public void put(Matrix4f matrix4f, int int1, ByteBuffer byteBuffer) {
			if (int1 == 0) {
				this.put0(matrix4f, byteBuffer);
			} else {
				this.putN(matrix4f, int1, byteBuffer);
			}
		}

		private void put0(Matrix4x3f matrix4x3f, FloatBuffer floatBuffer) {
			floatBuffer.put(0, matrix4x3f.m00);
			floatBuffer.put(1, matrix4x3f.m01);
			floatBuffer.put(2, matrix4x3f.m02);
			floatBuffer.put(3, matrix4x3f.m10);
			floatBuffer.put(4, matrix4x3f.m11);
			floatBuffer.put(5, matrix4x3f.m12);
			floatBuffer.put(6, matrix4x3f.m20);
			floatBuffer.put(7, matrix4x3f.m21);
			floatBuffer.put(8, matrix4x3f.m22);
			floatBuffer.put(9, matrix4x3f.m30);
			floatBuffer.put(10, matrix4x3f.m31);
			floatBuffer.put(11, matrix4x3f.m32);
		}

		private void putN(Matrix4x3f matrix4x3f, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, matrix4x3f.m00);
			floatBuffer.put(int1 + 1, matrix4x3f.m01);
			floatBuffer.put(int1 + 2, matrix4x3f.m02);
			floatBuffer.put(int1 + 3, matrix4x3f.m10);
			floatBuffer.put(int1 + 4, matrix4x3f.m11);
			floatBuffer.put(int1 + 5, matrix4x3f.m12);
			floatBuffer.put(int1 + 6, matrix4x3f.m20);
			floatBuffer.put(int1 + 7, matrix4x3f.m21);
			floatBuffer.put(int1 + 8, matrix4x3f.m22);
			floatBuffer.put(int1 + 9, matrix4x3f.m30);
			floatBuffer.put(int1 + 10, matrix4x3f.m31);
			floatBuffer.put(int1 + 11, matrix4x3f.m32);
		}

		public void put(Matrix4x3f matrix4x3f, int int1, FloatBuffer floatBuffer) {
			if (int1 == 0) {
				this.put0(matrix4x3f, floatBuffer);
			} else {
				this.putN(matrix4x3f, int1, floatBuffer);
			}
		}

		private void put0(Matrix4x3f matrix4x3f, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(0, matrix4x3f.m00);
			byteBuffer.putFloat(4, matrix4x3f.m01);
			byteBuffer.putFloat(8, matrix4x3f.m02);
			byteBuffer.putFloat(12, matrix4x3f.m10);
			byteBuffer.putFloat(16, matrix4x3f.m11);
			byteBuffer.putFloat(20, matrix4x3f.m12);
			byteBuffer.putFloat(24, matrix4x3f.m20);
			byteBuffer.putFloat(28, matrix4x3f.m21);
			byteBuffer.putFloat(32, matrix4x3f.m22);
			byteBuffer.putFloat(36, matrix4x3f.m30);
			byteBuffer.putFloat(40, matrix4x3f.m31);
			byteBuffer.putFloat(44, matrix4x3f.m32);
		}

		private void putN(Matrix4x3f matrix4x3f, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, matrix4x3f.m00);
			byteBuffer.putFloat(int1 + 4, matrix4x3f.m01);
			byteBuffer.putFloat(int1 + 8, matrix4x3f.m02);
			byteBuffer.putFloat(int1 + 12, matrix4x3f.m10);
			byteBuffer.putFloat(int1 + 16, matrix4x3f.m11);
			byteBuffer.putFloat(int1 + 20, matrix4x3f.m12);
			byteBuffer.putFloat(int1 + 24, matrix4x3f.m20);
			byteBuffer.putFloat(int1 + 28, matrix4x3f.m21);
			byteBuffer.putFloat(int1 + 32, matrix4x3f.m22);
			byteBuffer.putFloat(int1 + 36, matrix4x3f.m30);
			byteBuffer.putFloat(int1 + 40, matrix4x3f.m31);
			byteBuffer.putFloat(int1 + 44, matrix4x3f.m32);
		}

		public void put(Matrix4x3f matrix4x3f, int int1, ByteBuffer byteBuffer) {
			if (int1 == 0) {
				this.put0(matrix4x3f, byteBuffer);
			} else {
				this.putN(matrix4x3f, int1, byteBuffer);
			}
		}

		public final void put4x4(Matrix4x3f matrix4x3f, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, matrix4x3f.m00);
			floatBuffer.put(int1 + 1, matrix4x3f.m01);
			floatBuffer.put(int1 + 2, matrix4x3f.m02);
			floatBuffer.put(int1 + 3, 0.0F);
			floatBuffer.put(int1 + 4, matrix4x3f.m10);
			floatBuffer.put(int1 + 5, matrix4x3f.m11);
			floatBuffer.put(int1 + 6, matrix4x3f.m12);
			floatBuffer.put(int1 + 7, 0.0F);
			floatBuffer.put(int1 + 8, matrix4x3f.m20);
			floatBuffer.put(int1 + 9, matrix4x3f.m21);
			floatBuffer.put(int1 + 10, matrix4x3f.m22);
			floatBuffer.put(int1 + 11, 0.0F);
			floatBuffer.put(int1 + 12, matrix4x3f.m30);
			floatBuffer.put(int1 + 13, matrix4x3f.m31);
			floatBuffer.put(int1 + 14, matrix4x3f.m32);
			floatBuffer.put(int1 + 15, 1.0F);
		}

		public final void put4x4(Matrix4x3f matrix4x3f, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, matrix4x3f.m00);
			byteBuffer.putFloat(int1 + 4, matrix4x3f.m01);
			byteBuffer.putFloat(int1 + 8, matrix4x3f.m02);
			byteBuffer.putFloat(int1 + 12, 0.0F);
			byteBuffer.putFloat(int1 + 16, matrix4x3f.m10);
			byteBuffer.putFloat(int1 + 20, matrix4x3f.m11);
			byteBuffer.putFloat(int1 + 24, matrix4x3f.m12);
			byteBuffer.putFloat(int1 + 28, 0.0F);
			byteBuffer.putFloat(int1 + 32, matrix4x3f.m20);
			byteBuffer.putFloat(int1 + 36, matrix4x3f.m21);
			byteBuffer.putFloat(int1 + 40, matrix4x3f.m22);
			byteBuffer.putFloat(int1 + 44, 0.0F);
			byteBuffer.putFloat(int1 + 48, matrix4x3f.m30);
			byteBuffer.putFloat(int1 + 52, matrix4x3f.m31);
			byteBuffer.putFloat(int1 + 56, matrix4x3f.m32);
			byteBuffer.putFloat(int1 + 60, 1.0F);
		}

		public final void put4x4(Matrix4x3d matrix4x3d, int int1, DoubleBuffer doubleBuffer) {
			doubleBuffer.put(int1, matrix4x3d.m00);
			doubleBuffer.put(int1 + 1, matrix4x3d.m01);
			doubleBuffer.put(int1 + 2, matrix4x3d.m02);
			doubleBuffer.put(int1 + 3, 0.0);
			doubleBuffer.put(int1 + 4, matrix4x3d.m10);
			doubleBuffer.put(int1 + 5, matrix4x3d.m11);
			doubleBuffer.put(int1 + 6, matrix4x3d.m12);
			doubleBuffer.put(int1 + 7, 0.0);
			doubleBuffer.put(int1 + 8, matrix4x3d.m20);
			doubleBuffer.put(int1 + 9, matrix4x3d.m21);
			doubleBuffer.put(int1 + 10, matrix4x3d.m22);
			doubleBuffer.put(int1 + 11, 0.0);
			doubleBuffer.put(int1 + 12, matrix4x3d.m30);
			doubleBuffer.put(int1 + 13, matrix4x3d.m31);
			doubleBuffer.put(int1 + 14, matrix4x3d.m32);
			doubleBuffer.put(int1 + 15, 1.0);
		}

		public final void put4x4(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putDouble(int1, matrix4x3d.m00);
			byteBuffer.putDouble(int1 + 4, matrix4x3d.m01);
			byteBuffer.putDouble(int1 + 8, matrix4x3d.m02);
			byteBuffer.putDouble(int1 + 12, 0.0);
			byteBuffer.putDouble(int1 + 16, matrix4x3d.m10);
			byteBuffer.putDouble(int1 + 20, matrix4x3d.m11);
			byteBuffer.putDouble(int1 + 24, matrix4x3d.m12);
			byteBuffer.putDouble(int1 + 28, 0.0);
			byteBuffer.putDouble(int1 + 32, matrix4x3d.m20);
			byteBuffer.putDouble(int1 + 36, matrix4x3d.m21);
			byteBuffer.putDouble(int1 + 40, matrix4x3d.m22);
			byteBuffer.putDouble(int1 + 44, 0.0);
			byteBuffer.putDouble(int1 + 48, matrix4x3d.m30);
			byteBuffer.putDouble(int1 + 52, matrix4x3d.m31);
			byteBuffer.putDouble(int1 + 56, matrix4x3d.m32);
			byteBuffer.putDouble(int1 + 60, 1.0);
		}

		public final void putTransposed(Matrix4f matrix4f, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, matrix4f.m00);
			floatBuffer.put(int1 + 1, matrix4f.m10);
			floatBuffer.put(int1 + 2, matrix4f.m20);
			floatBuffer.put(int1 + 3, matrix4f.m30);
			floatBuffer.put(int1 + 4, matrix4f.m01);
			floatBuffer.put(int1 + 5, matrix4f.m11);
			floatBuffer.put(int1 + 6, matrix4f.m21);
			floatBuffer.put(int1 + 7, matrix4f.m31);
			floatBuffer.put(int1 + 8, matrix4f.m02);
			floatBuffer.put(int1 + 9, matrix4f.m12);
			floatBuffer.put(int1 + 10, matrix4f.m22);
			floatBuffer.put(int1 + 11, matrix4f.m32);
			floatBuffer.put(int1 + 12, matrix4f.m03);
			floatBuffer.put(int1 + 13, matrix4f.m13);
			floatBuffer.put(int1 + 14, matrix4f.m23);
			floatBuffer.put(int1 + 15, matrix4f.m33);
		}

		public final void putTransposed(Matrix4f matrix4f, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, matrix4f.m00);
			byteBuffer.putFloat(int1 + 4, matrix4f.m10);
			byteBuffer.putFloat(int1 + 8, matrix4f.m20);
			byteBuffer.putFloat(int1 + 12, matrix4f.m30);
			byteBuffer.putFloat(int1 + 16, matrix4f.m01);
			byteBuffer.putFloat(int1 + 20, matrix4f.m11);
			byteBuffer.putFloat(int1 + 24, matrix4f.m21);
			byteBuffer.putFloat(int1 + 28, matrix4f.m31);
			byteBuffer.putFloat(int1 + 32, matrix4f.m02);
			byteBuffer.putFloat(int1 + 36, matrix4f.m12);
			byteBuffer.putFloat(int1 + 40, matrix4f.m22);
			byteBuffer.putFloat(int1 + 44, matrix4f.m32);
			byteBuffer.putFloat(int1 + 48, matrix4f.m03);
			byteBuffer.putFloat(int1 + 52, matrix4f.m13);
			byteBuffer.putFloat(int1 + 56, matrix4f.m23);
			byteBuffer.putFloat(int1 + 60, matrix4f.m33);
		}

		public final void put4x3Transposed(Matrix4f matrix4f, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, matrix4f.m00);
			floatBuffer.put(int1 + 1, matrix4f.m10);
			floatBuffer.put(int1 + 2, matrix4f.m20);
			floatBuffer.put(int1 + 3, matrix4f.m30);
			floatBuffer.put(int1 + 4, matrix4f.m01);
			floatBuffer.put(int1 + 5, matrix4f.m11);
			floatBuffer.put(int1 + 6, matrix4f.m21);
			floatBuffer.put(int1 + 7, matrix4f.m31);
			floatBuffer.put(int1 + 8, matrix4f.m02);
			floatBuffer.put(int1 + 9, matrix4f.m12);
			floatBuffer.put(int1 + 10, matrix4f.m22);
			floatBuffer.put(int1 + 11, matrix4f.m32);
		}

		public final void put4x3Transposed(Matrix4f matrix4f, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, matrix4f.m00);
			byteBuffer.putFloat(int1 + 4, matrix4f.m10);
			byteBuffer.putFloat(int1 + 8, matrix4f.m20);
			byteBuffer.putFloat(int1 + 12, matrix4f.m30);
			byteBuffer.putFloat(int1 + 16, matrix4f.m01);
			byteBuffer.putFloat(int1 + 20, matrix4f.m11);
			byteBuffer.putFloat(int1 + 24, matrix4f.m21);
			byteBuffer.putFloat(int1 + 28, matrix4f.m31);
			byteBuffer.putFloat(int1 + 32, matrix4f.m02);
			byteBuffer.putFloat(int1 + 36, matrix4f.m12);
			byteBuffer.putFloat(int1 + 40, matrix4f.m22);
			byteBuffer.putFloat(int1 + 44, matrix4f.m32);
		}

		public final void putTransposed(Matrix4x3f matrix4x3f, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, matrix4x3f.m00);
			floatBuffer.put(int1 + 1, matrix4x3f.m10);
			floatBuffer.put(int1 + 2, matrix4x3f.m20);
			floatBuffer.put(int1 + 3, matrix4x3f.m30);
			floatBuffer.put(int1 + 4, matrix4x3f.m01);
			floatBuffer.put(int1 + 5, matrix4x3f.m11);
			floatBuffer.put(int1 + 6, matrix4x3f.m21);
			floatBuffer.put(int1 + 7, matrix4x3f.m31);
			floatBuffer.put(int1 + 8, matrix4x3f.m02);
			floatBuffer.put(int1 + 9, matrix4x3f.m12);
			floatBuffer.put(int1 + 10, matrix4x3f.m22);
			floatBuffer.put(int1 + 11, matrix4x3f.m32);
		}

		public final void putTransposed(Matrix4x3f matrix4x3f, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, matrix4x3f.m00);
			byteBuffer.putFloat(int1 + 4, matrix4x3f.m10);
			byteBuffer.putFloat(int1 + 8, matrix4x3f.m20);
			byteBuffer.putFloat(int1 + 12, matrix4x3f.m30);
			byteBuffer.putFloat(int1 + 16, matrix4x3f.m01);
			byteBuffer.putFloat(int1 + 20, matrix4x3f.m11);
			byteBuffer.putFloat(int1 + 24, matrix4x3f.m21);
			byteBuffer.putFloat(int1 + 28, matrix4x3f.m31);
			byteBuffer.putFloat(int1 + 32, matrix4x3f.m02);
			byteBuffer.putFloat(int1 + 36, matrix4x3f.m12);
			byteBuffer.putFloat(int1 + 40, matrix4x3f.m22);
			byteBuffer.putFloat(int1 + 44, matrix4x3f.m32);
		}

		public final void putTransposed(Matrix3f matrix3f, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, matrix3f.m00);
			floatBuffer.put(int1 + 1, matrix3f.m10);
			floatBuffer.put(int1 + 2, matrix3f.m20);
			floatBuffer.put(int1 + 3, matrix3f.m01);
			floatBuffer.put(int1 + 4, matrix3f.m11);
			floatBuffer.put(int1 + 5, matrix3f.m21);
			floatBuffer.put(int1 + 6, matrix3f.m02);
			floatBuffer.put(int1 + 7, matrix3f.m12);
			floatBuffer.put(int1 + 8, matrix3f.m22);
		}

		public final void putTransposed(Matrix3f matrix3f, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, matrix3f.m00);
			byteBuffer.putFloat(int1 + 4, matrix3f.m10);
			byteBuffer.putFloat(int1 + 8, matrix3f.m20);
			byteBuffer.putFloat(int1 + 12, matrix3f.m01);
			byteBuffer.putFloat(int1 + 16, matrix3f.m11);
			byteBuffer.putFloat(int1 + 20, matrix3f.m21);
			byteBuffer.putFloat(int1 + 24, matrix3f.m02);
			byteBuffer.putFloat(int1 + 28, matrix3f.m12);
			byteBuffer.putFloat(int1 + 32, matrix3f.m22);
		}

		public final void put(Matrix4d matrix4d, int int1, DoubleBuffer doubleBuffer) {
			doubleBuffer.put(int1, matrix4d.m00);
			doubleBuffer.put(int1 + 1, matrix4d.m01);
			doubleBuffer.put(int1 + 2, matrix4d.m02);
			doubleBuffer.put(int1 + 3, matrix4d.m03);
			doubleBuffer.put(int1 + 4, matrix4d.m10);
			doubleBuffer.put(int1 + 5, matrix4d.m11);
			doubleBuffer.put(int1 + 6, matrix4d.m12);
			doubleBuffer.put(int1 + 7, matrix4d.m13);
			doubleBuffer.put(int1 + 8, matrix4d.m20);
			doubleBuffer.put(int1 + 9, matrix4d.m21);
			doubleBuffer.put(int1 + 10, matrix4d.m22);
			doubleBuffer.put(int1 + 11, matrix4d.m23);
			doubleBuffer.put(int1 + 12, matrix4d.m30);
			doubleBuffer.put(int1 + 13, matrix4d.m31);
			doubleBuffer.put(int1 + 14, matrix4d.m32);
			doubleBuffer.put(int1 + 15, matrix4d.m33);
		}

		public final void put(Matrix4d matrix4d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putDouble(int1, matrix4d.m00);
			byteBuffer.putDouble(int1 + 4, matrix4d.m01);
			byteBuffer.putDouble(int1 + 8, matrix4d.m02);
			byteBuffer.putDouble(int1 + 12, matrix4d.m03);
			byteBuffer.putDouble(int1 + 16, matrix4d.m10);
			byteBuffer.putDouble(int1 + 20, matrix4d.m11);
			byteBuffer.putDouble(int1 + 24, matrix4d.m12);
			byteBuffer.putDouble(int1 + 28, matrix4d.m13);
			byteBuffer.putDouble(int1 + 32, matrix4d.m20);
			byteBuffer.putDouble(int1 + 36, matrix4d.m21);
			byteBuffer.putDouble(int1 + 40, matrix4d.m22);
			byteBuffer.putDouble(int1 + 44, matrix4d.m23);
			byteBuffer.putDouble(int1 + 48, matrix4d.m30);
			byteBuffer.putDouble(int1 + 52, matrix4d.m31);
			byteBuffer.putDouble(int1 + 56, matrix4d.m32);
			byteBuffer.putDouble(int1 + 60, matrix4d.m33);
		}

		public final void put(Matrix4x3d matrix4x3d, int int1, DoubleBuffer doubleBuffer) {
			doubleBuffer.put(int1, matrix4x3d.m00);
			doubleBuffer.put(int1 + 1, matrix4x3d.m01);
			doubleBuffer.put(int1 + 2, matrix4x3d.m02);
			doubleBuffer.put(int1 + 3, matrix4x3d.m10);
			doubleBuffer.put(int1 + 4, matrix4x3d.m11);
			doubleBuffer.put(int1 + 5, matrix4x3d.m12);
			doubleBuffer.put(int1 + 6, matrix4x3d.m20);
			doubleBuffer.put(int1 + 7, matrix4x3d.m21);
			doubleBuffer.put(int1 + 8, matrix4x3d.m22);
			doubleBuffer.put(int1 + 9, matrix4x3d.m30);
			doubleBuffer.put(int1 + 10, matrix4x3d.m31);
			doubleBuffer.put(int1 + 11, matrix4x3d.m32);
		}

		public final void put(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putDouble(int1, matrix4x3d.m00);
			byteBuffer.putDouble(int1 + 4, matrix4x3d.m01);
			byteBuffer.putDouble(int1 + 8, matrix4x3d.m02);
			byteBuffer.putDouble(int1 + 12, matrix4x3d.m10);
			byteBuffer.putDouble(int1 + 16, matrix4x3d.m11);
			byteBuffer.putDouble(int1 + 20, matrix4x3d.m12);
			byteBuffer.putDouble(int1 + 24, matrix4x3d.m20);
			byteBuffer.putDouble(int1 + 28, matrix4x3d.m21);
			byteBuffer.putDouble(int1 + 32, matrix4x3d.m22);
			byteBuffer.putDouble(int1 + 36, matrix4x3d.m30);
			byteBuffer.putDouble(int1 + 40, matrix4x3d.m31);
			byteBuffer.putDouble(int1 + 44, matrix4x3d.m32);
		}

		public final void putf(Matrix4d matrix4d, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, (float)matrix4d.m00);
			floatBuffer.put(int1 + 1, (float)matrix4d.m01);
			floatBuffer.put(int1 + 2, (float)matrix4d.m02);
			floatBuffer.put(int1 + 3, (float)matrix4d.m03);
			floatBuffer.put(int1 + 4, (float)matrix4d.m10);
			floatBuffer.put(int1 + 5, (float)matrix4d.m11);
			floatBuffer.put(int1 + 6, (float)matrix4d.m12);
			floatBuffer.put(int1 + 7, (float)matrix4d.m13);
			floatBuffer.put(int1 + 8, (float)matrix4d.m20);
			floatBuffer.put(int1 + 9, (float)matrix4d.m21);
			floatBuffer.put(int1 + 10, (float)matrix4d.m22);
			floatBuffer.put(int1 + 11, (float)matrix4d.m23);
			floatBuffer.put(int1 + 12, (float)matrix4d.m30);
			floatBuffer.put(int1 + 13, (float)matrix4d.m31);
			floatBuffer.put(int1 + 14, (float)matrix4d.m32);
			floatBuffer.put(int1 + 15, (float)matrix4d.m33);
		}

		public final void putf(Matrix4d matrix4d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, (float)matrix4d.m00);
			byteBuffer.putFloat(int1 + 4, (float)matrix4d.m01);
			byteBuffer.putFloat(int1 + 8, (float)matrix4d.m02);
			byteBuffer.putFloat(int1 + 12, (float)matrix4d.m03);
			byteBuffer.putFloat(int1 + 16, (float)matrix4d.m10);
			byteBuffer.putFloat(int1 + 20, (float)matrix4d.m11);
			byteBuffer.putFloat(int1 + 24, (float)matrix4d.m12);
			byteBuffer.putFloat(int1 + 28, (float)matrix4d.m13);
			byteBuffer.putFloat(int1 + 32, (float)matrix4d.m20);
			byteBuffer.putFloat(int1 + 36, (float)matrix4d.m21);
			byteBuffer.putFloat(int1 + 40, (float)matrix4d.m22);
			byteBuffer.putFloat(int1 + 44, (float)matrix4d.m23);
			byteBuffer.putFloat(int1 + 48, (float)matrix4d.m30);
			byteBuffer.putFloat(int1 + 52, (float)matrix4d.m31);
			byteBuffer.putFloat(int1 + 56, (float)matrix4d.m32);
			byteBuffer.putFloat(int1 + 60, (float)matrix4d.m33);
		}

		public final void putf(Matrix4x3d matrix4x3d, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, (float)matrix4x3d.m00);
			floatBuffer.put(int1 + 1, (float)matrix4x3d.m01);
			floatBuffer.put(int1 + 2, (float)matrix4x3d.m02);
			floatBuffer.put(int1 + 3, (float)matrix4x3d.m10);
			floatBuffer.put(int1 + 4, (float)matrix4x3d.m11);
			floatBuffer.put(int1 + 5, (float)matrix4x3d.m12);
			floatBuffer.put(int1 + 6, (float)matrix4x3d.m20);
			floatBuffer.put(int1 + 7, (float)matrix4x3d.m21);
			floatBuffer.put(int1 + 8, (float)matrix4x3d.m22);
			floatBuffer.put(int1 + 9, (float)matrix4x3d.m30);
			floatBuffer.put(int1 + 10, (float)matrix4x3d.m31);
			floatBuffer.put(int1 + 11, (float)matrix4x3d.m32);
		}

		public final void putf(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, (float)matrix4x3d.m00);
			byteBuffer.putFloat(int1 + 4, (float)matrix4x3d.m01);
			byteBuffer.putFloat(int1 + 8, (float)matrix4x3d.m02);
			byteBuffer.putFloat(int1 + 12, (float)matrix4x3d.m10);
			byteBuffer.putFloat(int1 + 16, (float)matrix4x3d.m11);
			byteBuffer.putFloat(int1 + 20, (float)matrix4x3d.m12);
			byteBuffer.putFloat(int1 + 24, (float)matrix4x3d.m20);
			byteBuffer.putFloat(int1 + 28, (float)matrix4x3d.m21);
			byteBuffer.putFloat(int1 + 32, (float)matrix4x3d.m22);
			byteBuffer.putFloat(int1 + 36, (float)matrix4x3d.m30);
			byteBuffer.putFloat(int1 + 40, (float)matrix4x3d.m31);
			byteBuffer.putFloat(int1 + 44, (float)matrix4x3d.m32);
		}

		public final void putTransposed(Matrix4d matrix4d, int int1, DoubleBuffer doubleBuffer) {
			doubleBuffer.put(int1, matrix4d.m00);
			doubleBuffer.put(int1 + 1, matrix4d.m10);
			doubleBuffer.put(int1 + 2, matrix4d.m20);
			doubleBuffer.put(int1 + 3, matrix4d.m30);
			doubleBuffer.put(int1 + 4, matrix4d.m01);
			doubleBuffer.put(int1 + 5, matrix4d.m11);
			doubleBuffer.put(int1 + 6, matrix4d.m21);
			doubleBuffer.put(int1 + 7, matrix4d.m31);
			doubleBuffer.put(int1 + 8, matrix4d.m02);
			doubleBuffer.put(int1 + 9, matrix4d.m12);
			doubleBuffer.put(int1 + 10, matrix4d.m22);
			doubleBuffer.put(int1 + 11, matrix4d.m32);
			doubleBuffer.put(int1 + 12, matrix4d.m03);
			doubleBuffer.put(int1 + 13, matrix4d.m13);
			doubleBuffer.put(int1 + 14, matrix4d.m23);
			doubleBuffer.put(int1 + 15, matrix4d.m33);
		}

		public final void putTransposed(Matrix4d matrix4d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putDouble(int1, matrix4d.m00);
			byteBuffer.putDouble(int1 + 8, matrix4d.m10);
			byteBuffer.putDouble(int1 + 16, matrix4d.m20);
			byteBuffer.putDouble(int1 + 24, matrix4d.m30);
			byteBuffer.putDouble(int1 + 32, matrix4d.m01);
			byteBuffer.putDouble(int1 + 40, matrix4d.m11);
			byteBuffer.putDouble(int1 + 48, matrix4d.m21);
			byteBuffer.putDouble(int1 + 56, matrix4d.m31);
			byteBuffer.putDouble(int1 + 64, matrix4d.m02);
			byteBuffer.putDouble(int1 + 72, matrix4d.m12);
			byteBuffer.putDouble(int1 + 80, matrix4d.m22);
			byteBuffer.putDouble(int1 + 88, matrix4d.m32);
			byteBuffer.putDouble(int1 + 96, matrix4d.m03);
			byteBuffer.putDouble(int1 + 104, matrix4d.m13);
			byteBuffer.putDouble(int1 + 112, matrix4d.m23);
			byteBuffer.putDouble(int1 + 120, matrix4d.m33);
		}

		public final void put4x3Transposed(Matrix4d matrix4d, int int1, DoubleBuffer doubleBuffer) {
			doubleBuffer.put(int1, matrix4d.m00);
			doubleBuffer.put(int1 + 1, matrix4d.m10);
			doubleBuffer.put(int1 + 2, matrix4d.m20);
			doubleBuffer.put(int1 + 3, matrix4d.m30);
			doubleBuffer.put(int1 + 4, matrix4d.m01);
			doubleBuffer.put(int1 + 5, matrix4d.m11);
			doubleBuffer.put(int1 + 6, matrix4d.m21);
			doubleBuffer.put(int1 + 7, matrix4d.m31);
			doubleBuffer.put(int1 + 8, matrix4d.m02);
			doubleBuffer.put(int1 + 9, matrix4d.m12);
			doubleBuffer.put(int1 + 10, matrix4d.m22);
			doubleBuffer.put(int1 + 11, matrix4d.m32);
		}

		public final void put4x3Transposed(Matrix4d matrix4d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putDouble(int1, matrix4d.m00);
			byteBuffer.putDouble(int1 + 8, matrix4d.m10);
			byteBuffer.putDouble(int1 + 16, matrix4d.m20);
			byteBuffer.putDouble(int1 + 24, matrix4d.m30);
			byteBuffer.putDouble(int1 + 32, matrix4d.m01);
			byteBuffer.putDouble(int1 + 40, matrix4d.m11);
			byteBuffer.putDouble(int1 + 48, matrix4d.m21);
			byteBuffer.putDouble(int1 + 56, matrix4d.m31);
			byteBuffer.putDouble(int1 + 64, matrix4d.m02);
			byteBuffer.putDouble(int1 + 72, matrix4d.m12);
			byteBuffer.putDouble(int1 + 80, matrix4d.m22);
			byteBuffer.putDouble(int1 + 88, matrix4d.m32);
		}

		public final void putTransposed(Matrix4x3d matrix4x3d, int int1, DoubleBuffer doubleBuffer) {
			doubleBuffer.put(int1, matrix4x3d.m00);
			doubleBuffer.put(int1 + 1, matrix4x3d.m10);
			doubleBuffer.put(int1 + 2, matrix4x3d.m20);
			doubleBuffer.put(int1 + 3, matrix4x3d.m30);
			doubleBuffer.put(int1 + 4, matrix4x3d.m01);
			doubleBuffer.put(int1 + 5, matrix4x3d.m11);
			doubleBuffer.put(int1 + 6, matrix4x3d.m21);
			doubleBuffer.put(int1 + 7, matrix4x3d.m31);
			doubleBuffer.put(int1 + 8, matrix4x3d.m02);
			doubleBuffer.put(int1 + 9, matrix4x3d.m12);
			doubleBuffer.put(int1 + 10, matrix4x3d.m22);
			doubleBuffer.put(int1 + 11, matrix4x3d.m32);
		}

		public final void putTransposed(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putDouble(int1, matrix4x3d.m00);
			byteBuffer.putDouble(int1 + 4, matrix4x3d.m10);
			byteBuffer.putDouble(int1 + 8, matrix4x3d.m20);
			byteBuffer.putDouble(int1 + 12, matrix4x3d.m30);
			byteBuffer.putDouble(int1 + 16, matrix4x3d.m01);
			byteBuffer.putDouble(int1 + 20, matrix4x3d.m11);
			byteBuffer.putDouble(int1 + 24, matrix4x3d.m21);
			byteBuffer.putDouble(int1 + 28, matrix4x3d.m31);
			byteBuffer.putDouble(int1 + 32, matrix4x3d.m02);
			byteBuffer.putDouble(int1 + 36, matrix4x3d.m12);
			byteBuffer.putDouble(int1 + 40, matrix4x3d.m22);
			byteBuffer.putDouble(int1 + 44, matrix4x3d.m32);
		}

		public final void putfTransposed(Matrix4x3d matrix4x3d, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, (float)matrix4x3d.m00);
			floatBuffer.put(int1 + 1, (float)matrix4x3d.m10);
			floatBuffer.put(int1 + 2, (float)matrix4x3d.m20);
			floatBuffer.put(int1 + 3, (float)matrix4x3d.m30);
			floatBuffer.put(int1 + 4, (float)matrix4x3d.m01);
			floatBuffer.put(int1 + 5, (float)matrix4x3d.m11);
			floatBuffer.put(int1 + 6, (float)matrix4x3d.m21);
			floatBuffer.put(int1 + 7, (float)matrix4x3d.m31);
			floatBuffer.put(int1 + 8, (float)matrix4x3d.m02);
			floatBuffer.put(int1 + 9, (float)matrix4x3d.m12);
			floatBuffer.put(int1 + 10, (float)matrix4x3d.m22);
			floatBuffer.put(int1 + 11, (float)matrix4x3d.m32);
		}

		public final void putfTransposed(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, (float)matrix4x3d.m00);
			byteBuffer.putFloat(int1 + 4, (float)matrix4x3d.m10);
			byteBuffer.putFloat(int1 + 8, (float)matrix4x3d.m20);
			byteBuffer.putFloat(int1 + 12, (float)matrix4x3d.m30);
			byteBuffer.putFloat(int1 + 16, (float)matrix4x3d.m01);
			byteBuffer.putFloat(int1 + 20, (float)matrix4x3d.m11);
			byteBuffer.putFloat(int1 + 24, (float)matrix4x3d.m21);
			byteBuffer.putFloat(int1 + 28, (float)matrix4x3d.m31);
			byteBuffer.putFloat(int1 + 32, (float)matrix4x3d.m02);
			byteBuffer.putFloat(int1 + 36, (float)matrix4x3d.m12);
			byteBuffer.putFloat(int1 + 40, (float)matrix4x3d.m22);
			byteBuffer.putFloat(int1 + 44, (float)matrix4x3d.m32);
		}

		public final void putfTransposed(Matrix4d matrix4d, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, (float)matrix4d.m00);
			floatBuffer.put(int1 + 1, (float)matrix4d.m10);
			floatBuffer.put(int1 + 2, (float)matrix4d.m20);
			floatBuffer.put(int1 + 3, (float)matrix4d.m30);
			floatBuffer.put(int1 + 4, (float)matrix4d.m01);
			floatBuffer.put(int1 + 5, (float)matrix4d.m11);
			floatBuffer.put(int1 + 6, (float)matrix4d.m21);
			floatBuffer.put(int1 + 7, (float)matrix4d.m31);
			floatBuffer.put(int1 + 8, (float)matrix4d.m02);
			floatBuffer.put(int1 + 9, (float)matrix4d.m12);
			floatBuffer.put(int1 + 10, (float)matrix4d.m22);
			floatBuffer.put(int1 + 11, (float)matrix4d.m32);
			floatBuffer.put(int1 + 12, (float)matrix4d.m03);
			floatBuffer.put(int1 + 13, (float)matrix4d.m13);
			floatBuffer.put(int1 + 14, (float)matrix4d.m23);
			floatBuffer.put(int1 + 15, (float)matrix4d.m33);
		}

		public final void putfTransposed(Matrix4d matrix4d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, (float)matrix4d.m00);
			byteBuffer.putFloat(int1 + 4, (float)matrix4d.m10);
			byteBuffer.putFloat(int1 + 8, (float)matrix4d.m20);
			byteBuffer.putFloat(int1 + 12, (float)matrix4d.m30);
			byteBuffer.putFloat(int1 + 16, (float)matrix4d.m01);
			byteBuffer.putFloat(int1 + 20, (float)matrix4d.m11);
			byteBuffer.putFloat(int1 + 24, (float)matrix4d.m21);
			byteBuffer.putFloat(int1 + 28, (float)matrix4d.m31);
			byteBuffer.putFloat(int1 + 32, (float)matrix4d.m02);
			byteBuffer.putFloat(int1 + 36, (float)matrix4d.m12);
			byteBuffer.putFloat(int1 + 40, (float)matrix4d.m22);
			byteBuffer.putFloat(int1 + 44, (float)matrix4d.m32);
			byteBuffer.putFloat(int1 + 48, (float)matrix4d.m03);
			byteBuffer.putFloat(int1 + 52, (float)matrix4d.m13);
			byteBuffer.putFloat(int1 + 56, (float)matrix4d.m23);
			byteBuffer.putFloat(int1 + 60, (float)matrix4d.m33);
		}

		public final void put(Matrix3f matrix3f, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, matrix3f.m00);
			floatBuffer.put(int1 + 1, matrix3f.m01);
			floatBuffer.put(int1 + 2, matrix3f.m02);
			floatBuffer.put(int1 + 3, matrix3f.m10);
			floatBuffer.put(int1 + 4, matrix3f.m11);
			floatBuffer.put(int1 + 5, matrix3f.m12);
			floatBuffer.put(int1 + 6, matrix3f.m20);
			floatBuffer.put(int1 + 7, matrix3f.m21);
			floatBuffer.put(int1 + 8, matrix3f.m22);
		}

		public final void put(Matrix3f matrix3f, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, matrix3f.m00);
			byteBuffer.putFloat(int1 + 4, matrix3f.m01);
			byteBuffer.putFloat(int1 + 8, matrix3f.m02);
			byteBuffer.putFloat(int1 + 12, matrix3f.m10);
			byteBuffer.putFloat(int1 + 16, matrix3f.m11);
			byteBuffer.putFloat(int1 + 20, matrix3f.m12);
			byteBuffer.putFloat(int1 + 24, matrix3f.m20);
			byteBuffer.putFloat(int1 + 28, matrix3f.m21);
			byteBuffer.putFloat(int1 + 32, matrix3f.m22);
		}

		public final void put(Matrix3d matrix3d, int int1, DoubleBuffer doubleBuffer) {
			doubleBuffer.put(int1, matrix3d.m00);
			doubleBuffer.put(int1 + 1, matrix3d.m01);
			doubleBuffer.put(int1 + 2, matrix3d.m02);
			doubleBuffer.put(int1 + 3, matrix3d.m10);
			doubleBuffer.put(int1 + 4, matrix3d.m11);
			doubleBuffer.put(int1 + 5, matrix3d.m12);
			doubleBuffer.put(int1 + 6, matrix3d.m20);
			doubleBuffer.put(int1 + 7, matrix3d.m21);
			doubleBuffer.put(int1 + 8, matrix3d.m22);
		}

		public final void put(Matrix3d matrix3d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putDouble(int1, matrix3d.m00);
			byteBuffer.putDouble(int1 + 8, matrix3d.m01);
			byteBuffer.putDouble(int1 + 16, matrix3d.m02);
			byteBuffer.putDouble(int1 + 24, matrix3d.m10);
			byteBuffer.putDouble(int1 + 32, matrix3d.m11);
			byteBuffer.putDouble(int1 + 40, matrix3d.m12);
			byteBuffer.putDouble(int1 + 48, matrix3d.m20);
			byteBuffer.putDouble(int1 + 56, matrix3d.m21);
			byteBuffer.putDouble(int1 + 64, matrix3d.m22);
		}

		public final void putf(Matrix3d matrix3d, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, (float)matrix3d.m00);
			floatBuffer.put(int1 + 1, (float)matrix3d.m01);
			floatBuffer.put(int1 + 2, (float)matrix3d.m02);
			floatBuffer.put(int1 + 3, (float)matrix3d.m10);
			floatBuffer.put(int1 + 4, (float)matrix3d.m11);
			floatBuffer.put(int1 + 5, (float)matrix3d.m12);
			floatBuffer.put(int1 + 6, (float)matrix3d.m20);
			floatBuffer.put(int1 + 7, (float)matrix3d.m21);
			floatBuffer.put(int1 + 8, (float)matrix3d.m22);
		}

		public final void putf(Matrix3d matrix3d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, (float)matrix3d.m00);
			byteBuffer.putFloat(int1 + 4, (float)matrix3d.m01);
			byteBuffer.putFloat(int1 + 8, (float)matrix3d.m02);
			byteBuffer.putFloat(int1 + 12, (float)matrix3d.m10);
			byteBuffer.putFloat(int1 + 16, (float)matrix3d.m11);
			byteBuffer.putFloat(int1 + 20, (float)matrix3d.m12);
			byteBuffer.putFloat(int1 + 24, (float)matrix3d.m20);
			byteBuffer.putFloat(int1 + 28, (float)matrix3d.m21);
			byteBuffer.putFloat(int1 + 32, (float)matrix3d.m22);
		}

		public final void put(Vector4d vector4d, int int1, DoubleBuffer doubleBuffer) {
			doubleBuffer.put(int1, vector4d.x);
			doubleBuffer.put(int1 + 1, vector4d.y);
			doubleBuffer.put(int1 + 2, vector4d.z);
			doubleBuffer.put(int1 + 3, vector4d.w);
		}

		public final void put(Vector4d vector4d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putDouble(int1, vector4d.x);
			byteBuffer.putDouble(int1 + 8, vector4d.y);
			byteBuffer.putDouble(int1 + 16, vector4d.z);
			byteBuffer.putDouble(int1 + 24, vector4d.w);
		}

		public final void put(Vector4f vector4f, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, vector4f.x);
			floatBuffer.put(int1 + 1, vector4f.y);
			floatBuffer.put(int1 + 2, vector4f.z);
			floatBuffer.put(int1 + 3, vector4f.w);
		}

		public final void put(Vector4f vector4f, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, vector4f.x);
			byteBuffer.putFloat(int1 + 4, vector4f.y);
			byteBuffer.putFloat(int1 + 8, vector4f.z);
			byteBuffer.putFloat(int1 + 12, vector4f.w);
		}

		public final void put(Vector4i vector4i, int int1, IntBuffer intBuffer) {
			intBuffer.put(int1, vector4i.x);
			intBuffer.put(int1 + 1, vector4i.y);
			intBuffer.put(int1 + 2, vector4i.z);
			intBuffer.put(int1 + 3, vector4i.w);
		}

		public final void put(Vector4i vector4i, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putInt(int1, vector4i.x);
			byteBuffer.putInt(int1 + 4, vector4i.y);
			byteBuffer.putInt(int1 + 8, vector4i.z);
			byteBuffer.putInt(int1 + 12, vector4i.w);
		}

		public final void put(Vector3f vector3f, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, vector3f.x);
			floatBuffer.put(int1 + 1, vector3f.y);
			floatBuffer.put(int1 + 2, vector3f.z);
		}

		public final void put(Vector3f vector3f, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, vector3f.x);
			byteBuffer.putFloat(int1 + 4, vector3f.y);
			byteBuffer.putFloat(int1 + 8, vector3f.z);
		}

		public final void put(Vector3d vector3d, int int1, DoubleBuffer doubleBuffer) {
			doubleBuffer.put(int1, vector3d.x);
			doubleBuffer.put(int1 + 1, vector3d.y);
			doubleBuffer.put(int1 + 2, vector3d.z);
		}

		public final void put(Vector3d vector3d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putDouble(int1, vector3d.x);
			byteBuffer.putDouble(int1 + 8, vector3d.y);
			byteBuffer.putDouble(int1 + 16, vector3d.z);
		}

		public final void put(Vector3i vector3i, int int1, IntBuffer intBuffer) {
			intBuffer.put(int1, vector3i.x);
			intBuffer.put(int1 + 1, vector3i.y);
			intBuffer.put(int1 + 2, vector3i.z);
		}

		public final void put(Vector3i vector3i, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putInt(int1, vector3i.x);
			byteBuffer.putInt(int1 + 4, vector3i.y);
			byteBuffer.putInt(int1 + 8, vector3i.z);
		}

		public final void put(Vector2f vector2f, int int1, FloatBuffer floatBuffer) {
			floatBuffer.put(int1, vector2f.x);
			floatBuffer.put(int1 + 1, vector2f.y);
		}

		public final void put(Vector2f vector2f, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putFloat(int1, vector2f.x);
			byteBuffer.putFloat(int1 + 4, vector2f.y);
		}

		public final void put(Vector2d vector2d, int int1, DoubleBuffer doubleBuffer) {
			doubleBuffer.put(int1, vector2d.x);
			doubleBuffer.put(int1 + 1, vector2d.y);
		}

		public final void put(Vector2d vector2d, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putDouble(int1, vector2d.x);
			byteBuffer.putDouble(int1 + 8, vector2d.y);
		}

		public final void put(Vector2i vector2i, int int1, IntBuffer intBuffer) {
			intBuffer.put(int1, vector2i.x);
			intBuffer.put(int1 + 1, vector2i.y);
		}

		public final void put(Vector2i vector2i, int int1, ByteBuffer byteBuffer) {
			byteBuffer.putInt(int1, vector2i.x);
			byteBuffer.putInt(int1 + 4, vector2i.y);
		}

		public final void get(Matrix4f matrix4f, int int1, FloatBuffer floatBuffer) {
			matrix4f.m00 = floatBuffer.get(int1);
			matrix4f.m01 = floatBuffer.get(int1 + 1);
			matrix4f.m02 = floatBuffer.get(int1 + 2);
			matrix4f.m03 = floatBuffer.get(int1 + 3);
			matrix4f.m10 = floatBuffer.get(int1 + 4);
			matrix4f.m11 = floatBuffer.get(int1 + 5);
			matrix4f.m12 = floatBuffer.get(int1 + 6);
			matrix4f.m13 = floatBuffer.get(int1 + 7);
			matrix4f.m20 = floatBuffer.get(int1 + 8);
			matrix4f.m21 = floatBuffer.get(int1 + 9);
			matrix4f.m22 = floatBuffer.get(int1 + 10);
			matrix4f.m23 = floatBuffer.get(int1 + 11);
			matrix4f.m30 = floatBuffer.get(int1 + 12);
			matrix4f.m31 = floatBuffer.get(int1 + 13);
			matrix4f.m32 = floatBuffer.get(int1 + 14);
			matrix4f.m33 = floatBuffer.get(int1 + 15);
		}

		public final void get(Matrix4f matrix4f, int int1, ByteBuffer byteBuffer) {
			matrix4f.m00 = byteBuffer.getFloat(int1);
			matrix4f.m01 = byteBuffer.getFloat(int1 + 4);
			matrix4f.m02 = byteBuffer.getFloat(int1 + 8);
			matrix4f.m03 = byteBuffer.getFloat(int1 + 12);
			matrix4f.m10 = byteBuffer.getFloat(int1 + 16);
			matrix4f.m11 = byteBuffer.getFloat(int1 + 20);
			matrix4f.m12 = byteBuffer.getFloat(int1 + 24);
			matrix4f.m13 = byteBuffer.getFloat(int1 + 28);
			matrix4f.m20 = byteBuffer.getFloat(int1 + 32);
			matrix4f.m21 = byteBuffer.getFloat(int1 + 36);
			matrix4f.m22 = byteBuffer.getFloat(int1 + 40);
			matrix4f.m23 = byteBuffer.getFloat(int1 + 44);
			matrix4f.m30 = byteBuffer.getFloat(int1 + 48);
			matrix4f.m31 = byteBuffer.getFloat(int1 + 52);
			matrix4f.m32 = byteBuffer.getFloat(int1 + 56);
			matrix4f.m33 = byteBuffer.getFloat(int1 + 60);
		}

		public final void get(Matrix4x3f matrix4x3f, int int1, FloatBuffer floatBuffer) {
			matrix4x3f.m00 = floatBuffer.get(int1);
			matrix4x3f.m01 = floatBuffer.get(int1 + 1);
			matrix4x3f.m02 = floatBuffer.get(int1 + 2);
			matrix4x3f.m10 = floatBuffer.get(int1 + 3);
			matrix4x3f.m11 = floatBuffer.get(int1 + 4);
			matrix4x3f.m12 = floatBuffer.get(int1 + 5);
			matrix4x3f.m20 = floatBuffer.get(int1 + 6);
			matrix4x3f.m21 = floatBuffer.get(int1 + 7);
			matrix4x3f.m22 = floatBuffer.get(int1 + 8);
			matrix4x3f.m30 = floatBuffer.get(int1 + 9);
			matrix4x3f.m31 = floatBuffer.get(int1 + 10);
			matrix4x3f.m32 = floatBuffer.get(int1 + 11);
		}

		public final void get(Matrix4x3f matrix4x3f, int int1, ByteBuffer byteBuffer) {
			matrix4x3f.m00 = byteBuffer.getFloat(int1);
			matrix4x3f.m01 = byteBuffer.getFloat(int1 + 4);
			matrix4x3f.m02 = byteBuffer.getFloat(int1 + 8);
			matrix4x3f.m10 = byteBuffer.getFloat(int1 + 12);
			matrix4x3f.m11 = byteBuffer.getFloat(int1 + 16);
			matrix4x3f.m12 = byteBuffer.getFloat(int1 + 20);
			matrix4x3f.m20 = byteBuffer.getFloat(int1 + 24);
			matrix4x3f.m21 = byteBuffer.getFloat(int1 + 28);
			matrix4x3f.m22 = byteBuffer.getFloat(int1 + 32);
			matrix4x3f.m30 = byteBuffer.getFloat(int1 + 36);
			matrix4x3f.m31 = byteBuffer.getFloat(int1 + 40);
			matrix4x3f.m32 = byteBuffer.getFloat(int1 + 44);
		}

		public final void get(Matrix4d matrix4d, int int1, DoubleBuffer doubleBuffer) {
			matrix4d.m00 = doubleBuffer.get(int1);
			matrix4d.m01 = doubleBuffer.get(int1 + 1);
			matrix4d.m02 = doubleBuffer.get(int1 + 2);
			matrix4d.m03 = doubleBuffer.get(int1 + 3);
			matrix4d.m10 = doubleBuffer.get(int1 + 4);
			matrix4d.m11 = doubleBuffer.get(int1 + 5);
			matrix4d.m12 = doubleBuffer.get(int1 + 6);
			matrix4d.m13 = doubleBuffer.get(int1 + 7);
			matrix4d.m20 = doubleBuffer.get(int1 + 8);
			matrix4d.m21 = doubleBuffer.get(int1 + 9);
			matrix4d.m22 = doubleBuffer.get(int1 + 10);
			matrix4d.m23 = doubleBuffer.get(int1 + 11);
			matrix4d.m30 = doubleBuffer.get(int1 + 12);
			matrix4d.m31 = doubleBuffer.get(int1 + 13);
			matrix4d.m32 = doubleBuffer.get(int1 + 14);
			matrix4d.m33 = doubleBuffer.get(int1 + 15);
		}

		public final void get(Matrix4d matrix4d, int int1, ByteBuffer byteBuffer) {
			matrix4d.m00 = byteBuffer.getDouble(int1);
			matrix4d.m01 = byteBuffer.getDouble(int1 + 8);
			matrix4d.m02 = byteBuffer.getDouble(int1 + 16);
			matrix4d.m03 = byteBuffer.getDouble(int1 + 24);
			matrix4d.m10 = byteBuffer.getDouble(int1 + 32);
			matrix4d.m11 = byteBuffer.getDouble(int1 + 40);
			matrix4d.m12 = byteBuffer.getDouble(int1 + 48);
			matrix4d.m13 = byteBuffer.getDouble(int1 + 56);
			matrix4d.m20 = byteBuffer.getDouble(int1 + 64);
			matrix4d.m21 = byteBuffer.getDouble(int1 + 72);
			matrix4d.m22 = byteBuffer.getDouble(int1 + 80);
			matrix4d.m23 = byteBuffer.getDouble(int1 + 88);
			matrix4d.m30 = byteBuffer.getDouble(int1 + 96);
			matrix4d.m31 = byteBuffer.getDouble(int1 + 104);
			matrix4d.m32 = byteBuffer.getDouble(int1 + 112);
			matrix4d.m33 = byteBuffer.getDouble(int1 + 120);
		}

		public final void get(Matrix4x3d matrix4x3d, int int1, DoubleBuffer doubleBuffer) {
			matrix4x3d.m00 = doubleBuffer.get(int1);
			matrix4x3d.m01 = doubleBuffer.get(int1 + 1);
			matrix4x3d.m02 = doubleBuffer.get(int1 + 2);
			matrix4x3d.m10 = doubleBuffer.get(int1 + 3);
			matrix4x3d.m11 = doubleBuffer.get(int1 + 4);
			matrix4x3d.m12 = doubleBuffer.get(int1 + 5);
			matrix4x3d.m20 = doubleBuffer.get(int1 + 6);
			matrix4x3d.m21 = doubleBuffer.get(int1 + 7);
			matrix4x3d.m22 = doubleBuffer.get(int1 + 8);
			matrix4x3d.m30 = doubleBuffer.get(int1 + 9);
			matrix4x3d.m31 = doubleBuffer.get(int1 + 10);
			matrix4x3d.m32 = doubleBuffer.get(int1 + 11);
		}

		public final void get(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer) {
			matrix4x3d.m00 = byteBuffer.getDouble(int1);
			matrix4x3d.m01 = byteBuffer.getDouble(int1 + 8);
			matrix4x3d.m02 = byteBuffer.getDouble(int1 + 16);
			matrix4x3d.m10 = byteBuffer.getDouble(int1 + 24);
			matrix4x3d.m11 = byteBuffer.getDouble(int1 + 32);
			matrix4x3d.m12 = byteBuffer.getDouble(int1 + 40);
			matrix4x3d.m20 = byteBuffer.getDouble(int1 + 48);
			matrix4x3d.m21 = byteBuffer.getDouble(int1 + 56);
			matrix4x3d.m22 = byteBuffer.getDouble(int1 + 64);
			matrix4x3d.m30 = byteBuffer.getDouble(int1 + 72);
			matrix4x3d.m31 = byteBuffer.getDouble(int1 + 80);
			matrix4x3d.m32 = byteBuffer.getDouble(int1 + 88);
		}

		public final void getf(Matrix4d matrix4d, int int1, FloatBuffer floatBuffer) {
			matrix4d.m00 = (double)floatBuffer.get(int1);
			matrix4d.m01 = (double)floatBuffer.get(int1 + 1);
			matrix4d.m02 = (double)floatBuffer.get(int1 + 2);
			matrix4d.m03 = (double)floatBuffer.get(int1 + 3);
			matrix4d.m10 = (double)floatBuffer.get(int1 + 4);
			matrix4d.m11 = (double)floatBuffer.get(int1 + 5);
			matrix4d.m12 = (double)floatBuffer.get(int1 + 6);
			matrix4d.m13 = (double)floatBuffer.get(int1 + 7);
			matrix4d.m20 = (double)floatBuffer.get(int1 + 8);
			matrix4d.m21 = (double)floatBuffer.get(int1 + 9);
			matrix4d.m22 = (double)floatBuffer.get(int1 + 10);
			matrix4d.m23 = (double)floatBuffer.get(int1 + 11);
			matrix4d.m30 = (double)floatBuffer.get(int1 + 12);
			matrix4d.m31 = (double)floatBuffer.get(int1 + 13);
			matrix4d.m32 = (double)floatBuffer.get(int1 + 14);
			matrix4d.m33 = (double)floatBuffer.get(int1 + 15);
		}

		public final void getf(Matrix4d matrix4d, int int1, ByteBuffer byteBuffer) {
			matrix4d.m00 = (double)byteBuffer.getFloat(int1);
			matrix4d.m01 = (double)byteBuffer.getFloat(int1 + 4);
			matrix4d.m02 = (double)byteBuffer.getFloat(int1 + 8);
			matrix4d.m03 = (double)byteBuffer.getFloat(int1 + 12);
			matrix4d.m10 = (double)byteBuffer.getFloat(int1 + 16);
			matrix4d.m11 = (double)byteBuffer.getFloat(int1 + 20);
			matrix4d.m12 = (double)byteBuffer.getFloat(int1 + 24);
			matrix4d.m13 = (double)byteBuffer.getFloat(int1 + 28);
			matrix4d.m20 = (double)byteBuffer.getFloat(int1 + 32);
			matrix4d.m21 = (double)byteBuffer.getFloat(int1 + 36);
			matrix4d.m22 = (double)byteBuffer.getFloat(int1 + 40);
			matrix4d.m23 = (double)byteBuffer.getFloat(int1 + 44);
			matrix4d.m30 = (double)byteBuffer.getFloat(int1 + 48);
			matrix4d.m31 = (double)byteBuffer.getFloat(int1 + 52);
			matrix4d.m32 = (double)byteBuffer.getFloat(int1 + 56);
			matrix4d.m33 = (double)byteBuffer.getFloat(int1 + 60);
		}

		public final void getf(Matrix4x3d matrix4x3d, int int1, FloatBuffer floatBuffer) {
			matrix4x3d.m00 = (double)floatBuffer.get(int1);
			matrix4x3d.m01 = (double)floatBuffer.get(int1 + 1);
			matrix4x3d.m02 = (double)floatBuffer.get(int1 + 2);
			matrix4x3d.m10 = (double)floatBuffer.get(int1 + 3);
			matrix4x3d.m11 = (double)floatBuffer.get(int1 + 4);
			matrix4x3d.m12 = (double)floatBuffer.get(int1 + 5);
			matrix4x3d.m20 = (double)floatBuffer.get(int1 + 6);
			matrix4x3d.m21 = (double)floatBuffer.get(int1 + 7);
			matrix4x3d.m22 = (double)floatBuffer.get(int1 + 8);
			matrix4x3d.m30 = (double)floatBuffer.get(int1 + 9);
			matrix4x3d.m31 = (double)floatBuffer.get(int1 + 10);
			matrix4x3d.m32 = (double)floatBuffer.get(int1 + 11);
		}

		public final void getf(Matrix4x3d matrix4x3d, int int1, ByteBuffer byteBuffer) {
			matrix4x3d.m00 = (double)byteBuffer.getFloat(int1);
			matrix4x3d.m01 = (double)byteBuffer.getFloat(int1 + 4);
			matrix4x3d.m02 = (double)byteBuffer.getFloat(int1 + 8);
			matrix4x3d.m10 = (double)byteBuffer.getFloat(int1 + 12);
			matrix4x3d.m11 = (double)byteBuffer.getFloat(int1 + 16);
			matrix4x3d.m12 = (double)byteBuffer.getFloat(int1 + 20);
			matrix4x3d.m20 = (double)byteBuffer.getFloat(int1 + 24);
			matrix4x3d.m21 = (double)byteBuffer.getFloat(int1 + 28);
			matrix4x3d.m22 = (double)byteBuffer.getFloat(int1 + 32);
			matrix4x3d.m30 = (double)byteBuffer.getFloat(int1 + 36);
			matrix4x3d.m31 = (double)byteBuffer.getFloat(int1 + 40);
			matrix4x3d.m32 = (double)byteBuffer.getFloat(int1 + 44);
		}

		public final void get(Matrix3f matrix3f, int int1, FloatBuffer floatBuffer) {
			matrix3f.m00 = floatBuffer.get(int1);
			matrix3f.m01 = floatBuffer.get(int1 + 1);
			matrix3f.m02 = floatBuffer.get(int1 + 2);
			matrix3f.m10 = floatBuffer.get(int1 + 3);
			matrix3f.m11 = floatBuffer.get(int1 + 4);
			matrix3f.m12 = floatBuffer.get(int1 + 5);
			matrix3f.m20 = floatBuffer.get(int1 + 6);
			matrix3f.m21 = floatBuffer.get(int1 + 7);
			matrix3f.m22 = floatBuffer.get(int1 + 8);
		}

		public final void get(Matrix3f matrix3f, int int1, ByteBuffer byteBuffer) {
			matrix3f.m00 = byteBuffer.getFloat(int1);
			matrix3f.m01 = byteBuffer.getFloat(int1 + 4);
			matrix3f.m02 = byteBuffer.getFloat(int1 + 8);
			matrix3f.m10 = byteBuffer.getFloat(int1 + 12);
			matrix3f.m11 = byteBuffer.getFloat(int1 + 16);
			matrix3f.m12 = byteBuffer.getFloat(int1 + 20);
			matrix3f.m20 = byteBuffer.getFloat(int1 + 24);
			matrix3f.m21 = byteBuffer.getFloat(int1 + 28);
			matrix3f.m22 = byteBuffer.getFloat(int1 + 32);
		}

		public final void get(Matrix3d matrix3d, int int1, DoubleBuffer doubleBuffer) {
			matrix3d.m00 = doubleBuffer.get(int1);
			matrix3d.m01 = doubleBuffer.get(int1 + 1);
			matrix3d.m02 = doubleBuffer.get(int1 + 2);
			matrix3d.m10 = doubleBuffer.get(int1 + 3);
			matrix3d.m11 = doubleBuffer.get(int1 + 4);
			matrix3d.m12 = doubleBuffer.get(int1 + 5);
			matrix3d.m20 = doubleBuffer.get(int1 + 6);
			matrix3d.m21 = doubleBuffer.get(int1 + 7);
			matrix3d.m22 = doubleBuffer.get(int1 + 8);
		}

		public final void get(Matrix3d matrix3d, int int1, ByteBuffer byteBuffer) {
			matrix3d.m00 = byteBuffer.getDouble(int1);
			matrix3d.m01 = byteBuffer.getDouble(int1 + 8);
			matrix3d.m02 = byteBuffer.getDouble(int1 + 16);
			matrix3d.m10 = byteBuffer.getDouble(int1 + 24);
			matrix3d.m11 = byteBuffer.getDouble(int1 + 32);
			matrix3d.m12 = byteBuffer.getDouble(int1 + 40);
			matrix3d.m20 = byteBuffer.getDouble(int1 + 48);
			matrix3d.m21 = byteBuffer.getDouble(int1 + 56);
			matrix3d.m22 = byteBuffer.getDouble(int1 + 64);
		}

		public final void getf(Matrix3d matrix3d, int int1, FloatBuffer floatBuffer) {
			matrix3d.m00 = (double)floatBuffer.get(int1);
			matrix3d.m01 = (double)floatBuffer.get(int1 + 1);
			matrix3d.m02 = (double)floatBuffer.get(int1 + 2);
			matrix3d.m10 = (double)floatBuffer.get(int1 + 3);
			matrix3d.m11 = (double)floatBuffer.get(int1 + 4);
			matrix3d.m12 = (double)floatBuffer.get(int1 + 5);
			matrix3d.m20 = (double)floatBuffer.get(int1 + 6);
			matrix3d.m21 = (double)floatBuffer.get(int1 + 7);
			matrix3d.m22 = (double)floatBuffer.get(int1 + 8);
		}

		public final void getf(Matrix3d matrix3d, int int1, ByteBuffer byteBuffer) {
			matrix3d.m00 = (double)byteBuffer.getFloat(int1);
			matrix3d.m01 = (double)byteBuffer.getFloat(int1 + 4);
			matrix3d.m02 = (double)byteBuffer.getFloat(int1 + 8);
			matrix3d.m10 = (double)byteBuffer.getFloat(int1 + 12);
			matrix3d.m11 = (double)byteBuffer.getFloat(int1 + 16);
			matrix3d.m12 = (double)byteBuffer.getFloat(int1 + 20);
			matrix3d.m20 = (double)byteBuffer.getFloat(int1 + 24);
			matrix3d.m21 = (double)byteBuffer.getFloat(int1 + 28);
			matrix3d.m22 = (double)byteBuffer.getFloat(int1 + 32);
		}

		public final void get(Vector4d vector4d, int int1, DoubleBuffer doubleBuffer) {
			vector4d.x = doubleBuffer.get(int1);
			vector4d.y = doubleBuffer.get(int1 + 1);
			vector4d.z = doubleBuffer.get(int1 + 2);
			vector4d.w = doubleBuffer.get(int1 + 3);
		}

		public final void get(Vector4d vector4d, int int1, ByteBuffer byteBuffer) {
			vector4d.x = byteBuffer.getDouble(int1);
			vector4d.y = byteBuffer.getDouble(int1 + 8);
			vector4d.z = byteBuffer.getDouble(int1 + 16);
			vector4d.w = byteBuffer.getDouble(int1 + 24);
		}

		public final void get(Vector4f vector4f, int int1, FloatBuffer floatBuffer) {
			vector4f.x = floatBuffer.get(int1);
			vector4f.y = floatBuffer.get(int1 + 1);
			vector4f.z = floatBuffer.get(int1 + 2);
			vector4f.w = floatBuffer.get(int1 + 3);
		}

		public final void get(Vector4f vector4f, int int1, ByteBuffer byteBuffer) {
			vector4f.x = byteBuffer.getFloat(int1);
			vector4f.y = byteBuffer.getFloat(int1 + 4);
			vector4f.z = byteBuffer.getFloat(int1 + 8);
			vector4f.w = byteBuffer.getFloat(int1 + 12);
		}

		public final void get(Vector4i vector4i, int int1, IntBuffer intBuffer) {
			vector4i.x = intBuffer.get(int1);
			vector4i.y = intBuffer.get(int1 + 1);
			vector4i.z = intBuffer.get(int1 + 2);
			vector4i.w = intBuffer.get(int1 + 3);
		}

		public final void get(Vector4i vector4i, int int1, ByteBuffer byteBuffer) {
			vector4i.x = byteBuffer.getInt(int1);
			vector4i.y = byteBuffer.getInt(int1 + 4);
			vector4i.z = byteBuffer.getInt(int1 + 8);
			vector4i.w = byteBuffer.getInt(int1 + 12);
		}

		public final void get(Vector3f vector3f, int int1, FloatBuffer floatBuffer) {
			vector3f.x = floatBuffer.get(int1);
			vector3f.y = floatBuffer.get(int1 + 1);
			vector3f.z = floatBuffer.get(int1 + 2);
		}

		public final void get(Vector3f vector3f, int int1, ByteBuffer byteBuffer) {
			vector3f.x = byteBuffer.getFloat(int1);
			vector3f.y = byteBuffer.getFloat(int1 + 4);
			vector3f.z = byteBuffer.getFloat(int1 + 8);
		}

		public final void get(Vector3d vector3d, int int1, DoubleBuffer doubleBuffer) {
			vector3d.x = doubleBuffer.get(int1);
			vector3d.y = doubleBuffer.get(int1 + 1);
			vector3d.z = doubleBuffer.get(int1 + 2);
		}

		public final void get(Vector3d vector3d, int int1, ByteBuffer byteBuffer) {
			vector3d.x = byteBuffer.getDouble(int1);
			vector3d.y = byteBuffer.getDouble(int1 + 8);
			vector3d.z = byteBuffer.getDouble(int1 + 16);
		}

		public final void get(Vector3i vector3i, int int1, IntBuffer intBuffer) {
			vector3i.x = intBuffer.get(int1);
			vector3i.y = intBuffer.get(int1 + 1);
			vector3i.z = intBuffer.get(int1 + 2);
		}

		public final void get(Vector3i vector3i, int int1, ByteBuffer byteBuffer) {
			vector3i.x = byteBuffer.getInt(int1);
			vector3i.y = byteBuffer.getInt(int1 + 4);
			vector3i.z = byteBuffer.getInt(int1 + 8);
		}

		public final void get(Vector2f vector2f, int int1, FloatBuffer floatBuffer) {
			vector2f.x = floatBuffer.get(int1);
			vector2f.y = floatBuffer.get(int1 + 1);
		}

		public final void get(Vector2f vector2f, int int1, ByteBuffer byteBuffer) {
			vector2f.x = byteBuffer.getFloat(int1);
			vector2f.y = byteBuffer.getFloat(int1 + 4);
		}

		public final void get(Vector2d vector2d, int int1, DoubleBuffer doubleBuffer) {
			vector2d.x = doubleBuffer.get(int1);
			vector2d.y = doubleBuffer.get(int1 + 1);
		}

		public final void get(Vector2d vector2d, int int1, ByteBuffer byteBuffer) {
			vector2d.x = byteBuffer.getDouble(int1);
			vector2d.y = byteBuffer.getDouble(int1 + 8);
		}

		public final void get(Vector2i vector2i, int int1, IntBuffer intBuffer) {
			vector2i.x = intBuffer.get(int1);
			vector2i.y = intBuffer.get(int1 + 1);
		}

		public final void get(Vector2i vector2i, int int1, ByteBuffer byteBuffer) {
			vector2i.x = byteBuffer.getInt(int1);
			vector2i.y = byteBuffer.getInt(int1 + 4);
		}

		public final void copy(Matrix4f matrix4f, Matrix4f matrix4f2) {
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
		}

		public final void copy(Matrix3f matrix3f, Matrix4f matrix4f) {
			matrix4f.m00 = matrix3f.m00;
			matrix4f.m01 = matrix3f.m01;
			matrix4f.m02 = matrix3f.m02;
			matrix4f.m03 = 0.0F;
			matrix4f.m10 = matrix3f.m10;
			matrix4f.m11 = matrix3f.m11;
			matrix4f.m12 = matrix3f.m12;
			matrix4f.m13 = 0.0F;
			matrix4f.m20 = matrix3f.m20;
			matrix4f.m21 = matrix3f.m21;
			matrix4f.m22 = matrix3f.m22;
			matrix4f.m23 = 0.0F;
			matrix4f.m30 = 0.0F;
			matrix4f.m31 = 0.0F;
			matrix4f.m32 = 0.0F;
			matrix4f.m33 = 1.0F;
		}

		public final void copy(Matrix4f matrix4f, Matrix3f matrix3f) {
			matrix3f.m00 = matrix4f.m00;
			matrix3f.m01 = matrix4f.m01;
			matrix3f.m02 = matrix4f.m02;
			matrix3f.m10 = matrix4f.m10;
			matrix3f.m11 = matrix4f.m11;
			matrix3f.m12 = matrix4f.m12;
			matrix3f.m20 = matrix4f.m20;
			matrix3f.m21 = matrix4f.m21;
			matrix3f.m22 = matrix4f.m22;
		}

		public final void copy(Matrix3f matrix3f, Matrix4x3f matrix4x3f) {
			matrix4x3f.m00 = matrix3f.m00;
			matrix4x3f.m01 = matrix3f.m01;
			matrix4x3f.m02 = matrix3f.m02;
			matrix4x3f.m10 = matrix3f.m10;
			matrix4x3f.m11 = matrix3f.m11;
			matrix4x3f.m12 = matrix3f.m12;
			matrix4x3f.m20 = matrix3f.m20;
			matrix4x3f.m21 = matrix3f.m21;
			matrix4x3f.m22 = matrix3f.m22;
			matrix4x3f.m30 = 0.0F;
			matrix4x3f.m31 = 0.0F;
			matrix4x3f.m32 = 0.0F;
		}

		public final void copy3x3(Matrix4f matrix4f, Matrix4f matrix4f2) {
			matrix4f2.m00 = matrix4f.m00;
			matrix4f2.m01 = matrix4f.m01;
			matrix4f2.m02 = matrix4f.m02;
			matrix4f2.m10 = matrix4f.m10;
			matrix4f2.m11 = matrix4f.m11;
			matrix4f2.m12 = matrix4f.m12;
			matrix4f2.m20 = matrix4f.m20;
			matrix4f2.m21 = matrix4f.m21;
			matrix4f2.m22 = matrix4f.m22;
		}

		public final void copy3x3(Matrix4x3f matrix4x3f, Matrix4x3f matrix4x3f2) {
			matrix4x3f2.m00 = matrix4x3f.m00;
			matrix4x3f2.m01 = matrix4x3f.m01;
			matrix4x3f2.m02 = matrix4x3f.m02;
			matrix4x3f2.m10 = matrix4x3f.m10;
			matrix4x3f2.m11 = matrix4x3f.m11;
			matrix4x3f2.m12 = matrix4x3f.m12;
			matrix4x3f2.m20 = matrix4x3f.m20;
			matrix4x3f2.m21 = matrix4x3f.m21;
			matrix4x3f2.m22 = matrix4x3f.m22;
		}

		public final void copy3x3(Matrix3f matrix3f, Matrix4x3f matrix4x3f) {
			matrix4x3f.m00 = matrix3f.m00;
			matrix4x3f.m01 = matrix3f.m01;
			matrix4x3f.m02 = matrix3f.m02;
			matrix4x3f.m10 = matrix3f.m10;
			matrix4x3f.m11 = matrix3f.m11;
			matrix4x3f.m12 = matrix3f.m12;
			matrix4x3f.m20 = matrix3f.m20;
			matrix4x3f.m21 = matrix3f.m21;
			matrix4x3f.m22 = matrix3f.m22;
		}

		public final void copy3x3(Matrix3f matrix3f, Matrix4f matrix4f) {
			matrix4f.m00 = matrix3f.m00;
			matrix4f.m01 = matrix3f.m01;
			matrix4f.m02 = matrix3f.m02;
			matrix4f.m10 = matrix3f.m10;
			matrix4f.m11 = matrix3f.m11;
			matrix4f.m12 = matrix3f.m12;
			matrix4f.m20 = matrix3f.m20;
			matrix4f.m21 = matrix3f.m21;
			matrix4f.m22 = matrix3f.m22;
		}

		public final void copy4x3(Matrix4x3f matrix4x3f, Matrix4f matrix4f) {
			matrix4f.m00 = matrix4x3f.m00;
			matrix4f.m01 = matrix4x3f.m01;
			matrix4f.m02 = matrix4x3f.m02;
			matrix4f.m10 = matrix4x3f.m10;
			matrix4f.m11 = matrix4x3f.m11;
			matrix4f.m12 = matrix4x3f.m12;
			matrix4f.m20 = matrix4x3f.m20;
			matrix4f.m21 = matrix4x3f.m21;
			matrix4f.m22 = matrix4x3f.m22;
			matrix4f.m30 = matrix4x3f.m30;
			matrix4f.m31 = matrix4x3f.m31;
			matrix4f.m32 = matrix4x3f.m32;
		}

		public final void copy(Vector4f vector4f, Vector4f vector4f2) {
			vector4f2.x = vector4f.x;
			vector4f2.y = vector4f.y;
			vector4f2.z = vector4f.z;
			vector4f2.w = vector4f.w;
		}

		public final void copy(Vector4i vector4i, Vector4i vector4i2) {
			vector4i2.x = vector4i.x;
			vector4i2.y = vector4i.y;
			vector4i2.z = vector4i.z;
			vector4i2.w = vector4i.w;
		}

		public final void copy(Quaternionf quaternionf, Quaternionf quaternionf2) {
			quaternionf2.x = quaternionf.x;
			quaternionf2.y = quaternionf.y;
			quaternionf2.z = quaternionf.z;
			quaternionf2.w = quaternionf.w;
		}

		public final void copy4x3(Matrix4f matrix4f, Matrix4f matrix4f2) {
			matrix4f2.m00 = matrix4f.m00;
			matrix4f2.m01 = matrix4f.m01;
			matrix4f2.m02 = matrix4f.m02;
			matrix4f2.m10 = matrix4f.m10;
			matrix4f2.m11 = matrix4f.m11;
			matrix4f2.m12 = matrix4f.m12;
			matrix4f2.m20 = matrix4f.m20;
			matrix4f2.m21 = matrix4f.m21;
			matrix4f2.m22 = matrix4f.m22;
			matrix4f2.m30 = matrix4f.m30;
			matrix4f2.m31 = matrix4f.m31;
			matrix4f2.m32 = matrix4f.m32;
		}

		public final void copy(Matrix4f matrix4f, Matrix4x3f matrix4x3f) {
			matrix4x3f.m00 = matrix4f.m00;
			matrix4x3f.m01 = matrix4f.m01;
			matrix4x3f.m02 = matrix4f.m02;
			matrix4x3f.m10 = matrix4f.m10;
			matrix4x3f.m11 = matrix4f.m11;
			matrix4x3f.m12 = matrix4f.m12;
			matrix4x3f.m20 = matrix4f.m20;
			matrix4x3f.m21 = matrix4f.m21;
			matrix4x3f.m22 = matrix4f.m22;
			matrix4x3f.m30 = matrix4f.m30;
			matrix4x3f.m31 = matrix4f.m31;
			matrix4x3f.m32 = matrix4f.m32;
		}

		public final void copy(Matrix4x3f matrix4x3f, Matrix4f matrix4f) {
			matrix4f.m00 = matrix4x3f.m00;
			matrix4f.m01 = matrix4x3f.m01;
			matrix4f.m02 = matrix4x3f.m02;
			matrix4f.m03 = 0.0F;
			matrix4f.m10 = matrix4x3f.m10;
			matrix4f.m11 = matrix4x3f.m11;
			matrix4f.m12 = matrix4x3f.m12;
			matrix4f.m13 = 0.0F;
			matrix4f.m20 = matrix4x3f.m20;
			matrix4f.m21 = matrix4x3f.m21;
			matrix4f.m22 = matrix4x3f.m22;
			matrix4f.m23 = 0.0F;
			matrix4f.m30 = matrix4x3f.m30;
			matrix4f.m31 = matrix4x3f.m31;
			matrix4f.m32 = matrix4x3f.m32;
			matrix4f.m33 = 1.0F;
		}

		public final void copy(Matrix4x3f matrix4x3f, Matrix4x3f matrix4x3f2) {
			matrix4x3f2.m00 = matrix4x3f.m00;
			matrix4x3f2.m01 = matrix4x3f.m01;
			matrix4x3f2.m02 = matrix4x3f.m02;
			matrix4x3f2.m10 = matrix4x3f.m10;
			matrix4x3f2.m11 = matrix4x3f.m11;
			matrix4x3f2.m12 = matrix4x3f.m12;
			matrix4x3f2.m20 = matrix4x3f.m20;
			matrix4x3f2.m21 = matrix4x3f.m21;
			matrix4x3f2.m22 = matrix4x3f.m22;
			matrix4x3f2.m30 = matrix4x3f.m30;
			matrix4x3f2.m31 = matrix4x3f.m31;
			matrix4x3f2.m32 = matrix4x3f.m32;
		}

		public final void copy(Matrix3f matrix3f, Matrix3f matrix3f2) {
			matrix3f2.m00 = matrix3f.m00;
			matrix3f2.m01 = matrix3f.m01;
			matrix3f2.m02 = matrix3f.m02;
			matrix3f2.m10 = matrix3f.m10;
			matrix3f2.m11 = matrix3f.m11;
			matrix3f2.m12 = matrix3f.m12;
			matrix3f2.m20 = matrix3f.m20;
			matrix3f2.m21 = matrix3f.m21;
			matrix3f2.m22 = matrix3f.m22;
		}

		public final void copy(float[] floatArray, int int1, Matrix4f matrix4f) {
			matrix4f.m00 = floatArray[int1 + 0];
			matrix4f.m01 = floatArray[int1 + 1];
			matrix4f.m02 = floatArray[int1 + 2];
			matrix4f.m03 = floatArray[int1 + 3];
			matrix4f.m10 = floatArray[int1 + 4];
			matrix4f.m11 = floatArray[int1 + 5];
			matrix4f.m12 = floatArray[int1 + 6];
			matrix4f.m13 = floatArray[int1 + 7];
			matrix4f.m20 = floatArray[int1 + 8];
			matrix4f.m21 = floatArray[int1 + 9];
			matrix4f.m22 = floatArray[int1 + 10];
			matrix4f.m23 = floatArray[int1 + 11];
			matrix4f.m30 = floatArray[int1 + 12];
			matrix4f.m31 = floatArray[int1 + 13];
			matrix4f.m32 = floatArray[int1 + 14];
			matrix4f.m33 = floatArray[int1 + 15];
		}

		public final void copy(float[] floatArray, int int1, Matrix3f matrix3f) {
			matrix3f.m00 = floatArray[int1 + 0];
			matrix3f.m01 = floatArray[int1 + 1];
			matrix3f.m02 = floatArray[int1 + 2];
			matrix3f.m10 = floatArray[int1 + 3];
			matrix3f.m11 = floatArray[int1 + 4];
			matrix3f.m12 = floatArray[int1 + 5];
			matrix3f.m20 = floatArray[int1 + 6];
			matrix3f.m21 = floatArray[int1 + 7];
			matrix3f.m22 = floatArray[int1 + 8];
		}

		public final void copy(float[] floatArray, int int1, Matrix4x3f matrix4x3f) {
			matrix4x3f.m00 = floatArray[int1 + 0];
			matrix4x3f.m01 = floatArray[int1 + 1];
			matrix4x3f.m02 = floatArray[int1 + 2];
			matrix4x3f.m10 = floatArray[int1 + 3];
			matrix4x3f.m11 = floatArray[int1 + 4];
			matrix4x3f.m12 = floatArray[int1 + 5];
			matrix4x3f.m20 = floatArray[int1 + 6];
			matrix4x3f.m21 = floatArray[int1 + 7];
			matrix4x3f.m22 = floatArray[int1 + 8];
			matrix4x3f.m30 = floatArray[int1 + 9];
			matrix4x3f.m31 = floatArray[int1 + 10];
			matrix4x3f.m32 = floatArray[int1 + 11];
		}

		public final void copy(Matrix4f matrix4f, float[] floatArray, int int1) {
			floatArray[int1 + 0] = matrix4f.m00;
			floatArray[int1 + 1] = matrix4f.m01;
			floatArray[int1 + 2] = matrix4f.m02;
			floatArray[int1 + 3] = matrix4f.m03;
			floatArray[int1 + 4] = matrix4f.m10;
			floatArray[int1 + 5] = matrix4f.m11;
			floatArray[int1 + 6] = matrix4f.m12;
			floatArray[int1 + 7] = matrix4f.m13;
			floatArray[int1 + 8] = matrix4f.m20;
			floatArray[int1 + 9] = matrix4f.m21;
			floatArray[int1 + 10] = matrix4f.m22;
			floatArray[int1 + 11] = matrix4f.m23;
			floatArray[int1 + 12] = matrix4f.m30;
			floatArray[int1 + 13] = matrix4f.m31;
			floatArray[int1 + 14] = matrix4f.m32;
			floatArray[int1 + 15] = matrix4f.m33;
		}

		public final void copy(Matrix3f matrix3f, float[] floatArray, int int1) {
			floatArray[int1 + 0] = matrix3f.m00;
			floatArray[int1 + 1] = matrix3f.m01;
			floatArray[int1 + 2] = matrix3f.m02;
			floatArray[int1 + 3] = matrix3f.m10;
			floatArray[int1 + 4] = matrix3f.m11;
			floatArray[int1 + 5] = matrix3f.m12;
			floatArray[int1 + 6] = matrix3f.m20;
			floatArray[int1 + 7] = matrix3f.m21;
			floatArray[int1 + 8] = matrix3f.m22;
		}

		public final void copy(Matrix4x3f matrix4x3f, float[] floatArray, int int1) {
			floatArray[int1 + 0] = matrix4x3f.m00;
			floatArray[int1 + 1] = matrix4x3f.m01;
			floatArray[int1 + 2] = matrix4x3f.m02;
			floatArray[int1 + 3] = matrix4x3f.m10;
			floatArray[int1 + 4] = matrix4x3f.m11;
			floatArray[int1 + 5] = matrix4x3f.m12;
			floatArray[int1 + 6] = matrix4x3f.m20;
			floatArray[int1 + 7] = matrix4x3f.m21;
			floatArray[int1 + 8] = matrix4x3f.m22;
			floatArray[int1 + 9] = matrix4x3f.m30;
			floatArray[int1 + 10] = matrix4x3f.m31;
			floatArray[int1 + 11] = matrix4x3f.m32;
		}

		public final void identity(Matrix4f matrix4f) {
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
		}

		public final void identity(Matrix4x3f matrix4x3f) {
			matrix4x3f.m00 = 1.0F;
			matrix4x3f.m01 = 0.0F;
			matrix4x3f.m02 = 0.0F;
			matrix4x3f.m10 = 0.0F;
			matrix4x3f.m11 = 1.0F;
			matrix4x3f.m12 = 0.0F;
			matrix4x3f.m20 = 0.0F;
			matrix4x3f.m21 = 0.0F;
			matrix4x3f.m22 = 1.0F;
			matrix4x3f.m30 = 0.0F;
			matrix4x3f.m31 = 0.0F;
			matrix4x3f.m32 = 0.0F;
		}

		public final void identity(Matrix3f matrix3f) {
			matrix3f.m00 = 1.0F;
			matrix3f.m01 = 0.0F;
			matrix3f.m02 = 0.0F;
			matrix3f.m10 = 0.0F;
			matrix3f.m11 = 1.0F;
			matrix3f.m12 = 0.0F;
			matrix3f.m20 = 0.0F;
			matrix3f.m21 = 0.0F;
			matrix3f.m22 = 1.0F;
		}

		public final void identity(Quaternionf quaternionf) {
			quaternionf.x = 0.0F;
			quaternionf.y = 0.0F;
			quaternionf.z = 0.0F;
			quaternionf.w = 1.0F;
		}

		public final void swap(Matrix4f matrix4f, Matrix4f matrix4f2) {
			float float1 = matrix4f.m00;
			matrix4f.m00 = matrix4f2.m00;
			matrix4f2.m00 = float1;
			float1 = matrix4f.m01;
			matrix4f.m01 = matrix4f2.m01;
			matrix4f2.m01 = float1;
			float1 = matrix4f.m02;
			matrix4f.m02 = matrix4f2.m02;
			matrix4f2.m02 = float1;
			float1 = matrix4f.m03;
			matrix4f.m03 = matrix4f2.m03;
			matrix4f2.m03 = float1;
			float1 = matrix4f.m10;
			matrix4f.m10 = matrix4f2.m10;
			matrix4f2.m10 = float1;
			float1 = matrix4f.m11;
			matrix4f.m11 = matrix4f2.m11;
			matrix4f2.m11 = float1;
			float1 = matrix4f.m12;
			matrix4f.m12 = matrix4f2.m12;
			matrix4f2.m12 = float1;
			float1 = matrix4f.m13;
			matrix4f.m13 = matrix4f2.m13;
			matrix4f2.m13 = float1;
			float1 = matrix4f.m20;
			matrix4f.m20 = matrix4f2.m20;
			matrix4f2.m20 = float1;
			float1 = matrix4f.m21;
			matrix4f.m21 = matrix4f2.m21;
			matrix4f2.m21 = float1;
			float1 = matrix4f.m22;
			matrix4f.m22 = matrix4f2.m22;
			matrix4f2.m22 = float1;
			float1 = matrix4f.m23;
			matrix4f.m23 = matrix4f2.m23;
			matrix4f2.m23 = float1;
			float1 = matrix4f.m30;
			matrix4f.m30 = matrix4f2.m30;
			matrix4f2.m30 = float1;
			float1 = matrix4f.m31;
			matrix4f.m31 = matrix4f2.m31;
			matrix4f2.m31 = float1;
			float1 = matrix4f.m32;
			matrix4f.m32 = matrix4f2.m32;
			matrix4f2.m32 = float1;
			float1 = matrix4f.m33;
			matrix4f.m33 = matrix4f2.m33;
			matrix4f2.m33 = float1;
		}

		public final void swap(Matrix4x3f matrix4x3f, Matrix4x3f matrix4x3f2) {
			float float1 = matrix4x3f.m00;
			matrix4x3f.m00 = matrix4x3f2.m00;
			matrix4x3f2.m00 = float1;
			float1 = matrix4x3f.m01;
			matrix4x3f.m01 = matrix4x3f2.m01;
			matrix4x3f2.m01 = float1;
			float1 = matrix4x3f.m02;
			matrix4x3f.m02 = matrix4x3f2.m02;
			matrix4x3f2.m02 = float1;
			float1 = matrix4x3f.m10;
			matrix4x3f.m10 = matrix4x3f2.m10;
			matrix4x3f2.m10 = float1;
			float1 = matrix4x3f.m11;
			matrix4x3f.m11 = matrix4x3f2.m11;
			matrix4x3f2.m11 = float1;
			float1 = matrix4x3f.m12;
			matrix4x3f.m12 = matrix4x3f2.m12;
			matrix4x3f2.m12 = float1;
			float1 = matrix4x3f.m20;
			matrix4x3f.m20 = matrix4x3f2.m20;
			matrix4x3f2.m20 = float1;
			float1 = matrix4x3f.m21;
			matrix4x3f.m21 = matrix4x3f2.m21;
			matrix4x3f2.m21 = float1;
			float1 = matrix4x3f.m22;
			matrix4x3f.m22 = matrix4x3f2.m22;
			matrix4x3f2.m22 = float1;
			float1 = matrix4x3f.m30;
			matrix4x3f.m30 = matrix4x3f2.m30;
			matrix4x3f2.m30 = float1;
			float1 = matrix4x3f.m31;
			matrix4x3f.m31 = matrix4x3f2.m31;
			matrix4x3f2.m31 = float1;
			float1 = matrix4x3f.m32;
			matrix4x3f.m32 = matrix4x3f2.m32;
			matrix4x3f2.m32 = float1;
		}

		public final void swap(Matrix3f matrix3f, Matrix3f matrix3f2) {
			float float1 = matrix3f.m00;
			matrix3f.m00 = matrix3f2.m00;
			matrix3f2.m00 = float1;
			float1 = matrix3f.m01;
			matrix3f.m01 = matrix3f2.m01;
			matrix3f2.m01 = float1;
			float1 = matrix3f.m02;
			matrix3f.m02 = matrix3f2.m02;
			matrix3f2.m02 = float1;
			float1 = matrix3f.m10;
			matrix3f.m10 = matrix3f2.m10;
			matrix3f2.m10 = float1;
			float1 = matrix3f.m11;
			matrix3f.m11 = matrix3f2.m11;
			matrix3f2.m11 = float1;
			float1 = matrix3f.m12;
			matrix3f.m12 = matrix3f2.m12;
			matrix3f2.m12 = float1;
			float1 = matrix3f.m20;
			matrix3f.m20 = matrix3f2.m20;
			matrix3f2.m20 = float1;
			float1 = matrix3f.m21;
			matrix3f.m21 = matrix3f2.m21;
			matrix3f2.m21 = float1;
			float1 = matrix3f.m22;
			matrix3f.m22 = matrix3f2.m22;
			matrix3f2.m22 = float1;
		}

		public final void zero(Matrix4f matrix4f) {
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
		}

		public final void zero(Matrix4x3f matrix4x3f) {
			matrix4x3f.m00 = 0.0F;
			matrix4x3f.m01 = 0.0F;
			matrix4x3f.m02 = 0.0F;
			matrix4x3f.m10 = 0.0F;
			matrix4x3f.m11 = 0.0F;
			matrix4x3f.m12 = 0.0F;
			matrix4x3f.m20 = 0.0F;
			matrix4x3f.m21 = 0.0F;
			matrix4x3f.m22 = 0.0F;
			matrix4x3f.m30 = 0.0F;
			matrix4x3f.m31 = 0.0F;
			matrix4x3f.m32 = 0.0F;
		}

		public final void zero(Matrix3f matrix3f) {
			matrix3f.m00 = 0.0F;
			matrix3f.m01 = 0.0F;
			matrix3f.m02 = 0.0F;
			matrix3f.m10 = 0.0F;
			matrix3f.m11 = 0.0F;
			matrix3f.m12 = 0.0F;
			matrix3f.m20 = 0.0F;
			matrix3f.m21 = 0.0F;
			matrix3f.m22 = 0.0F;
		}

		public final void zero(Vector4f vector4f) {
			vector4f.x = 0.0F;
			vector4f.y = 0.0F;
			vector4f.z = 0.0F;
			vector4f.w = 0.0F;
		}

		public final void zero(Vector4i vector4i) {
			vector4i.x = 0;
			vector4i.y = 0;
			vector4i.z = 0;
			vector4i.w = 0;
		}

		public final void putMatrix3f(Quaternionf quaternionf, int int1, ByteBuffer byteBuffer) {
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
			byteBuffer.putFloat(int1, float1 + float2 - float4 - float3);
			byteBuffer.putFloat(int1 + 4, float6 + float5 + float5 + float6);
			byteBuffer.putFloat(int1 + 8, float7 - float8 + float7 - float8);
			byteBuffer.putFloat(int1 + 12, -float5 + float6 - float5 + float6);
			byteBuffer.putFloat(int1 + 16, float3 - float4 + float1 - float2);
			byteBuffer.putFloat(int1 + 20, float9 + float9 + float10 + float10);
			byteBuffer.putFloat(int1 + 24, float8 + float7 + float7 + float8);
			byteBuffer.putFloat(int1 + 28, float9 + float9 - float10 - float10);
			byteBuffer.putFloat(int1 + 32, float4 - float3 - float2 + float1);
		}

		public final void putMatrix3f(Quaternionf quaternionf, int int1, FloatBuffer floatBuffer) {
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
			floatBuffer.put(int1, float1 + float2 - float4 - float3);
			floatBuffer.put(int1 + 1, float6 + float5 + float5 + float6);
			floatBuffer.put(int1 + 2, float7 - float8 + float7 - float8);
			floatBuffer.put(int1 + 3, -float5 + float6 - float5 + float6);
			floatBuffer.put(int1 + 4, float3 - float4 + float1 - float2);
			floatBuffer.put(int1 + 5, float9 + float9 + float10 + float10);
			floatBuffer.put(int1 + 6, float8 + float7 + float7 + float8);
			floatBuffer.put(int1 + 7, float9 + float9 - float10 - float10);
			floatBuffer.put(int1 + 8, float4 - float3 - float2 + float1);
		}

		public final void putMatrix4f(Quaternionf quaternionf, int int1, ByteBuffer byteBuffer) {
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
			byteBuffer.putFloat(int1, float1 + float2 - float4 - float3);
			byteBuffer.putFloat(int1 + 4, float6 + float5 + float5 + float6);
			byteBuffer.putFloat(int1 + 8, float7 - float8 + float7 - float8);
			byteBuffer.putFloat(int1 + 12, 0.0F);
			byteBuffer.putFloat(int1 + 16, -float5 + float6 - float5 + float6);
			byteBuffer.putFloat(int1 + 20, float3 - float4 + float1 - float2);
			byteBuffer.putFloat(int1 + 24, float9 + float9 + float10 + float10);
			byteBuffer.putFloat(int1 + 28, 0.0F);
			byteBuffer.putFloat(int1 + 32, float8 + float7 + float7 + float8);
			byteBuffer.putFloat(int1 + 36, float9 + float9 - float10 - float10);
			byteBuffer.putFloat(int1 + 40, float4 - float3 - float2 + float1);
			byteBuffer.putFloat(int1 + 44, 0.0F);
			byteBuffer.putLong(int1 + 48, 0L);
			byteBuffer.putLong(int1 + 56, 4575657221408423936L);
		}

		public final void putMatrix4f(Quaternionf quaternionf, int int1, FloatBuffer floatBuffer) {
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
			floatBuffer.put(int1, float1 + float2 - float4 - float3);
			floatBuffer.put(int1 + 1, float6 + float5 + float5 + float6);
			floatBuffer.put(int1 + 2, float7 - float8 + float7 - float8);
			floatBuffer.put(int1 + 3, 0.0F);
			floatBuffer.put(int1 + 4, -float5 + float6 - float5 + float6);
			floatBuffer.put(int1 + 5, float3 - float4 + float1 - float2);
			floatBuffer.put(int1 + 6, float9 + float9 + float10 + float10);
			floatBuffer.put(int1 + 7, 0.0F);
			floatBuffer.put(int1 + 8, float8 + float7 + float7 + float8);
			floatBuffer.put(int1 + 9, float9 + float9 - float10 - float10);
			floatBuffer.put(int1 + 10, float4 - float3 - float2 + float1);
			floatBuffer.put(int1 + 11, 0.0F);
			floatBuffer.put(int1 + 12, 0.0F);
			floatBuffer.put(int1 + 13, 0.0F);
			floatBuffer.put(int1 + 14, 0.0F);
			floatBuffer.put(int1 + 15, 1.0F);
		}

		public final void putMatrix4x3f(Quaternionf quaternionf, int int1, ByteBuffer byteBuffer) {
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
			byteBuffer.putFloat(int1, float1 + float2 - float4 - float3);
			byteBuffer.putFloat(int1 + 4, float6 + float5 + float5 + float6);
			byteBuffer.putFloat(int1 + 8, float7 - float8 + float7 - float8);
			byteBuffer.putFloat(int1 + 12, -float5 + float6 - float5 + float6);
			byteBuffer.putFloat(int1 + 16, float3 - float4 + float1 - float2);
			byteBuffer.putFloat(int1 + 20, float9 + float9 + float10 + float10);
			byteBuffer.putFloat(int1 + 24, float8 + float7 + float7 + float8);
			byteBuffer.putFloat(int1 + 28, float9 + float9 - float10 - float10);
			byteBuffer.putFloat(int1 + 32, float4 - float3 - float2 + float1);
			byteBuffer.putLong(int1 + 36, 0L);
			byteBuffer.putFloat(int1 + 44, 0.0F);
		}

		public final void putMatrix4x3f(Quaternionf quaternionf, int int1, FloatBuffer floatBuffer) {
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
			floatBuffer.put(int1, float1 + float2 - float4 - float3);
			floatBuffer.put(int1 + 1, float6 + float5 + float5 + float6);
			floatBuffer.put(int1 + 2, float7 - float8 + float7 - float8);
			floatBuffer.put(int1 + 3, -float5 + float6 - float5 + float6);
			floatBuffer.put(int1 + 4, float3 - float4 + float1 - float2);
			floatBuffer.put(int1 + 5, float9 + float9 + float10 + float10);
			floatBuffer.put(int1 + 6, float8 + float7 + float7 + float8);
			floatBuffer.put(int1 + 7, float9 + float9 - float10 - float10);
			floatBuffer.put(int1 + 8, float4 - float3 - float2 + float1);
			floatBuffer.put(int1 + 9, 0.0F);
			floatBuffer.put(int1 + 10, 0.0F);
			floatBuffer.put(int1 + 11, 0.0F);
		}

		public final void set(Matrix4f matrix4f, Vector4f vector4f, Vector4f vector4f2, Vector4f vector4f3, Vector4f vector4f4) {
			matrix4f.m00 = vector4f.x;
			matrix4f.m01 = vector4f.y;
			matrix4f.m02 = vector4f.z;
			matrix4f.m03 = vector4f.w;
			matrix4f.m10 = vector4f2.x;
			matrix4f.m11 = vector4f2.y;
			matrix4f.m12 = vector4f2.z;
			matrix4f.m13 = vector4f2.w;
			matrix4f.m20 = vector4f3.x;
			matrix4f.m21 = vector4f3.y;
			matrix4f.m22 = vector4f3.z;
			matrix4f.m23 = vector4f3.w;
			matrix4f.m30 = vector4f4.x;
			matrix4f.m31 = vector4f4.y;
			matrix4f.m32 = vector4f4.z;
			matrix4f.m33 = vector4f4.w;
		}

		public final void set(Matrix4x3f matrix4x3f, Vector3f vector3f, Vector3f vector3f2, Vector3f vector3f3, Vector3f vector3f4) {
			matrix4x3f.m00 = vector3f.x;
			matrix4x3f.m01 = vector3f.y;
			matrix4x3f.m02 = vector3f.z;
			matrix4x3f.m10 = vector3f2.x;
			matrix4x3f.m11 = vector3f2.y;
			matrix4x3f.m12 = vector3f2.z;
			matrix4x3f.m20 = vector3f3.x;
			matrix4x3f.m21 = vector3f3.y;
			matrix4x3f.m22 = vector3f3.z;
			matrix4x3f.m30 = vector3f4.x;
			matrix4x3f.m31 = vector3f4.y;
			matrix4x3f.m32 = vector3f4.z;
		}

		public final void set(Matrix3f matrix3f, Vector3f vector3f, Vector3f vector3f2, Vector3f vector3f3) {
			matrix3f.m00 = vector3f.x;
			matrix3f.m01 = vector3f.y;
			matrix3f.m02 = vector3f.z;
			matrix3f.m10 = vector3f2.x;
			matrix3f.m11 = vector3f2.y;
			matrix3f.m12 = vector3f2.z;
			matrix3f.m20 = vector3f3.x;
			matrix3f.m21 = vector3f3.y;
			matrix3f.m22 = vector3f3.z;
		}

		public final void putColumn0(Matrix4f matrix4f, Vector4f vector4f) {
			vector4f.x = matrix4f.m00;
			vector4f.y = matrix4f.m01;
			vector4f.z = matrix4f.m02;
			vector4f.w = matrix4f.m03;
		}

		public final void putColumn1(Matrix4f matrix4f, Vector4f vector4f) {
			vector4f.x = matrix4f.m10;
			vector4f.y = matrix4f.m11;
			vector4f.z = matrix4f.m12;
			vector4f.w = matrix4f.m13;
		}

		public final void putColumn2(Matrix4f matrix4f, Vector4f vector4f) {
			vector4f.x = matrix4f.m20;
			vector4f.y = matrix4f.m21;
			vector4f.z = matrix4f.m22;
			vector4f.w = matrix4f.m23;
		}

		public final void putColumn3(Matrix4f matrix4f, Vector4f vector4f) {
			vector4f.x = matrix4f.m30;
			vector4f.y = matrix4f.m31;
			vector4f.z = matrix4f.m32;
			vector4f.w = matrix4f.m33;
		}

		public final void getColumn0(Matrix4f matrix4f, Vector4f vector4f) {
			matrix4f.m00 = vector4f.x;
			matrix4f.m01 = vector4f.y;
			matrix4f.m02 = vector4f.z;
			matrix4f.m03 = vector4f.w;
		}

		public final void getColumn1(Matrix4f matrix4f, Vector4f vector4f) {
			matrix4f.m10 = vector4f.x;
			matrix4f.m11 = vector4f.y;
			matrix4f.m12 = vector4f.z;
			matrix4f.m13 = vector4f.w;
		}

		public final void getColumn2(Matrix4f matrix4f, Vector4f vector4f) {
			matrix4f.m20 = vector4f.x;
			matrix4f.m21 = vector4f.y;
			matrix4f.m22 = vector4f.z;
			matrix4f.m23 = vector4f.w;
		}

		public final void getColumn3(Matrix4f matrix4f, Vector4f vector4f) {
			matrix4f.m30 = vector4f.x;
			matrix4f.m31 = vector4f.y;
			matrix4f.m32 = vector4f.z;
			matrix4f.m33 = vector4f.w;
		}

		public final void broadcast(float float1, Vector4f vector4f) {
			vector4f.x = float1;
			vector4f.y = float1;
			vector4f.z = float1;
			vector4f.w = float1;
		}

		public final void broadcast(int int1, Vector4i vector4i) {
			vector4i.x = int1;
			vector4i.y = int1;
			vector4i.z = int1;
			vector4i.w = int1;
		}
	}
}
