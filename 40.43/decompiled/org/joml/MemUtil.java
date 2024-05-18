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
      Object var0;
      try {
         if (Options.NO_UNSAFE) {
            var0 = new MemUtil.MemUtilNIO();
         } else {
            var0 = new MemUtil.MemUtilUnsafe();
         }
      } catch (Throwable var2) {
         var0 = new MemUtil.MemUtilNIO();
      }

      return (MemUtil)var0;
   }

   public abstract MemUtil.MemUtilUnsafe UNSAFE();

   public abstract void put(Matrix4f var1, int var2, FloatBuffer var3);

   public abstract void put(Matrix4f var1, int var2, ByteBuffer var3);

   public abstract void put(Matrix4x3f var1, int var2, FloatBuffer var3);

   public abstract void put(Matrix4x3f var1, int var2, ByteBuffer var3);

   public abstract void put4x4(Matrix4x3f var1, int var2, FloatBuffer var3);

   public abstract void put4x4(Matrix4x3f var1, int var2, ByteBuffer var3);

   public abstract void put4x4(Matrix4x3d var1, int var2, DoubleBuffer var3);

   public abstract void put4x4(Matrix4x3d var1, int var2, ByteBuffer var3);

   public abstract void putTransposed(Matrix4f var1, int var2, FloatBuffer var3);

   public abstract void putTransposed(Matrix4f var1, int var2, ByteBuffer var3);

   public abstract void put4x3Transposed(Matrix4f var1, int var2, FloatBuffer var3);

   public abstract void put4x3Transposed(Matrix4f var1, int var2, ByteBuffer var3);

   public abstract void putTransposed(Matrix4x3f var1, int var2, FloatBuffer var3);

   public abstract void putTransposed(Matrix4x3f var1, int var2, ByteBuffer var3);

   public abstract void putTransposed(Matrix3f var1, int var2, FloatBuffer var3);

   public abstract void putTransposed(Matrix3f var1, int var2, ByteBuffer var3);

   public abstract void put(Matrix4d var1, int var2, DoubleBuffer var3);

   public abstract void put(Matrix4d var1, int var2, ByteBuffer var3);

   public abstract void put(Matrix4x3d var1, int var2, DoubleBuffer var3);

   public abstract void put(Matrix4x3d var1, int var2, ByteBuffer var3);

   public abstract void putf(Matrix4d var1, int var2, FloatBuffer var3);

   public abstract void putf(Matrix4d var1, int var2, ByteBuffer var3);

   public abstract void putf(Matrix4x3d var1, int var2, FloatBuffer var3);

   public abstract void putf(Matrix4x3d var1, int var2, ByteBuffer var3);

   public abstract void putTransposed(Matrix4d var1, int var2, DoubleBuffer var3);

   public abstract void putTransposed(Matrix4d var1, int var2, ByteBuffer var3);

   public abstract void put4x3Transposed(Matrix4d var1, int var2, DoubleBuffer var3);

   public abstract void put4x3Transposed(Matrix4d var1, int var2, ByteBuffer var3);

   public abstract void putTransposed(Matrix4x3d var1, int var2, DoubleBuffer var3);

   public abstract void putTransposed(Matrix4x3d var1, int var2, ByteBuffer var3);

   public abstract void putfTransposed(Matrix4d var1, int var2, FloatBuffer var3);

   public abstract void putfTransposed(Matrix4d var1, int var2, ByteBuffer var3);

   public abstract void putfTransposed(Matrix4x3d var1, int var2, FloatBuffer var3);

   public abstract void putfTransposed(Matrix4x3d var1, int var2, ByteBuffer var3);

   public abstract void put(Matrix3f var1, int var2, FloatBuffer var3);

   public abstract void put(Matrix3f var1, int var2, ByteBuffer var3);

   public abstract void put(Matrix3d var1, int var2, DoubleBuffer var3);

   public abstract void put(Matrix3d var1, int var2, ByteBuffer var3);

   public abstract void putf(Matrix3d var1, int var2, FloatBuffer var3);

   public abstract void putf(Matrix3d var1, int var2, ByteBuffer var3);

   public abstract void put(Vector4d var1, int var2, DoubleBuffer var3);

   public abstract void put(Vector4d var1, int var2, ByteBuffer var3);

   public abstract void put(Vector4f var1, int var2, FloatBuffer var3);

   public abstract void put(Vector4f var1, int var2, ByteBuffer var3);

   public abstract void put(Vector4i var1, int var2, IntBuffer var3);

   public abstract void put(Vector4i var1, int var2, ByteBuffer var3);

   public abstract void put(Vector3f var1, int var2, FloatBuffer var3);

   public abstract void put(Vector3f var1, int var2, ByteBuffer var3);

   public abstract void put(Vector3d var1, int var2, DoubleBuffer var3);

   public abstract void put(Vector3d var1, int var2, ByteBuffer var3);

   public abstract void put(Vector3i var1, int var2, IntBuffer var3);

   public abstract void put(Vector3i var1, int var2, ByteBuffer var3);

   public abstract void put(Vector2f var1, int var2, FloatBuffer var3);

   public abstract void put(Vector2f var1, int var2, ByteBuffer var3);

   public abstract void put(Vector2d var1, int var2, DoubleBuffer var3);

   public abstract void put(Vector2d var1, int var2, ByteBuffer var3);

   public abstract void put(Vector2i var1, int var2, IntBuffer var3);

   public abstract void put(Vector2i var1, int var2, ByteBuffer var3);

   public abstract void get(Matrix4f var1, int var2, FloatBuffer var3);

   public abstract void get(Matrix4f var1, int var2, ByteBuffer var3);

   public abstract void get(Matrix4x3f var1, int var2, FloatBuffer var3);

   public abstract void get(Matrix4x3f var1, int var2, ByteBuffer var3);

   public abstract void get(Matrix4d var1, int var2, DoubleBuffer var3);

   public abstract void get(Matrix4d var1, int var2, ByteBuffer var3);

   public abstract void get(Matrix4x3d var1, int var2, DoubleBuffer var3);

   public abstract void get(Matrix4x3d var1, int var2, ByteBuffer var3);

   public abstract void getf(Matrix4d var1, int var2, FloatBuffer var3);

   public abstract void getf(Matrix4d var1, int var2, ByteBuffer var3);

   public abstract void getf(Matrix4x3d var1, int var2, FloatBuffer var3);

   public abstract void getf(Matrix4x3d var1, int var2, ByteBuffer var3);

   public abstract void get(Matrix3f var1, int var2, FloatBuffer var3);

   public abstract void get(Matrix3f var1, int var2, ByteBuffer var3);

   public abstract void get(Matrix3d var1, int var2, DoubleBuffer var3);

   public abstract void get(Matrix3d var1, int var2, ByteBuffer var3);

   public abstract void getf(Matrix3d var1, int var2, FloatBuffer var3);

   public abstract void getf(Matrix3d var1, int var2, ByteBuffer var3);

   public abstract void get(Vector4d var1, int var2, DoubleBuffer var3);

   public abstract void get(Vector4d var1, int var2, ByteBuffer var3);

   public abstract void get(Vector4f var1, int var2, FloatBuffer var3);

   public abstract void get(Vector4f var1, int var2, ByteBuffer var3);

   public abstract void get(Vector4i var1, int var2, IntBuffer var3);

   public abstract void get(Vector4i var1, int var2, ByteBuffer var3);

   public abstract void get(Vector3f var1, int var2, FloatBuffer var3);

   public abstract void get(Vector3f var1, int var2, ByteBuffer var3);

   public abstract void get(Vector3d var1, int var2, DoubleBuffer var3);

   public abstract void get(Vector3d var1, int var2, ByteBuffer var3);

   public abstract void get(Vector3i var1, int var2, IntBuffer var3);

   public abstract void get(Vector3i var1, int var2, ByteBuffer var3);

   public abstract void get(Vector2f var1, int var2, FloatBuffer var3);

   public abstract void get(Vector2f var1, int var2, ByteBuffer var3);

   public abstract void get(Vector2d var1, int var2, DoubleBuffer var3);

   public abstract void get(Vector2d var1, int var2, ByteBuffer var3);

   public abstract void get(Vector2i var1, int var2, IntBuffer var3);

   public abstract void get(Vector2i var1, int var2, ByteBuffer var3);

   public abstract void copy(Matrix4f var1, Matrix4f var2);

   public abstract void copy(Matrix4x3f var1, Matrix4x3f var2);

   public abstract void copy(Matrix4f var1, Matrix4x3f var2);

   public abstract void copy(Matrix4x3f var1, Matrix4f var2);

   public abstract void copy(Matrix3f var1, Matrix3f var2);

   public abstract void copy(Matrix3f var1, Matrix4f var2);

   public abstract void copy(Matrix4f var1, Matrix3f var2);

   public abstract void copy(Matrix3f var1, Matrix4x3f var2);

   public abstract void copy3x3(Matrix4f var1, Matrix4f var2);

   public abstract void copy3x3(Matrix4x3f var1, Matrix4x3f var2);

   public abstract void copy3x3(Matrix3f var1, Matrix4x3f var2);

   public abstract void copy3x3(Matrix3f var1, Matrix4f var2);

   public abstract void copy4x3(Matrix4f var1, Matrix4f var2);

   public abstract void copy4x3(Matrix4x3f var1, Matrix4f var2);

   public abstract void copy(Vector4f var1, Vector4f var2);

   public abstract void copy(Vector4i var1, Vector4i var2);

   public abstract void copy(Quaternionf var1, Quaternionf var2);

   public abstract void copy(float[] var1, int var2, Matrix4f var3);

   public abstract void copy(float[] var1, int var2, Matrix3f var3);

   public abstract void copy(float[] var1, int var2, Matrix4x3f var3);

   public abstract void copy(Matrix4f var1, float[] var2, int var3);

   public abstract void copy(Matrix3f var1, float[] var2, int var3);

   public abstract void copy(Matrix4x3f var1, float[] var2, int var3);

   public abstract void identity(Matrix4f var1);

   public abstract void identity(Matrix4x3f var1);

   public abstract void identity(Matrix3f var1);

   public abstract void identity(Quaternionf var1);

   public abstract void swap(Matrix4f var1, Matrix4f var2);

   public abstract void swap(Matrix4x3f var1, Matrix4x3f var2);

   public abstract void swap(Matrix3f var1, Matrix3f var2);

   public abstract void zero(Matrix4f var1);

   public abstract void zero(Matrix4x3f var1);

   public abstract void zero(Matrix3f var1);

   public abstract void zero(Vector4f var1);

   public abstract void zero(Vector4i var1);

   public abstract void putMatrix3f(Quaternionf var1, int var2, ByteBuffer var3);

   public abstract void putMatrix3f(Quaternionf var1, int var2, FloatBuffer var3);

   public abstract void putMatrix4f(Quaternionf var1, int var2, ByteBuffer var3);

   public abstract void putMatrix4f(Quaternionf var1, int var2, FloatBuffer var3);

   public abstract void putMatrix4x3f(Quaternionf var1, int var2, ByteBuffer var3);

   public abstract void putMatrix4x3f(Quaternionf var1, int var2, FloatBuffer var3);

   public abstract void set(Matrix4f var1, Vector4f var2, Vector4f var3, Vector4f var4, Vector4f var5);

   public abstract void set(Matrix4x3f var1, Vector3f var2, Vector3f var3, Vector3f var4, Vector3f var5);

   public abstract void set(Matrix3f var1, Vector3f var2, Vector3f var3, Vector3f var4);

   public abstract void putColumn0(Matrix4f var1, Vector4f var2);

   public abstract void putColumn1(Matrix4f var1, Vector4f var2);

   public abstract void putColumn2(Matrix4f var1, Vector4f var2);

   public abstract void putColumn3(Matrix4f var1, Vector4f var2);

   public abstract void getColumn0(Matrix4f var1, Vector4f var2);

   public abstract void getColumn1(Matrix4f var1, Vector4f var2);

   public abstract void getColumn2(Matrix4f var1, Vector4f var2);

   public abstract void getColumn3(Matrix4f var1, Vector4f var2);

   public abstract void broadcast(float var1, Vector4f var2);

   public abstract void broadcast(int var1, Vector4i var2);

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
         Field var0 = Matrix4f.class.getDeclaredField("m00");
         long var1 = UNSAFE.objectFieldOffset(var0);

         for(int var3 = 1; var3 < 16; ++var3) {
            int var4 = var3 >>> 2;
            int var5 = var3 & 3;
            var0 = Matrix4f.class.getDeclaredField("m" + var4 + var5);
            long var6 = UNSAFE.objectFieldOffset(var0);
            if (var6 != var1 + (long)(var3 << 2)) {
               throw new UnsupportedOperationException();
            }
         }

         return var1;
      }

      private static long checkMatrix4x3f() throws NoSuchFieldException, SecurityException {
         Field var0 = Matrix4x3f.class.getDeclaredField("m00");
         long var1 = UNSAFE.objectFieldOffset(var0);

         for(int var3 = 1; var3 < 12; ++var3) {
            int var4 = var3 / 3;
            int var5 = var3 % 3;
            var0 = Matrix4x3f.class.getDeclaredField("m" + var4 + var5);
            long var6 = UNSAFE.objectFieldOffset(var0);
            if (var6 != var1 + (long)(var3 << 2)) {
               throw new UnsupportedOperationException();
            }
         }

         return var1;
      }

      private static long checkMatrix3f() throws NoSuchFieldException, SecurityException {
         Field var0 = Matrix3f.class.getDeclaredField("m00");
         long var1 = UNSAFE.objectFieldOffset(var0);

         for(int var3 = 1; var3 < 9; ++var3) {
            int var4 = var3 / 3;
            int var5 = var3 % 3;
            var0 = Matrix3f.class.getDeclaredField("m" + var4 + var5);
            long var6 = UNSAFE.objectFieldOffset(var0);
            if (var6 != var1 + (long)(var3 << 2)) {
               throw new UnsupportedOperationException();
            }
         }

         return var1;
      }

      private static long checkVector4f() throws NoSuchFieldException, SecurityException {
         Field var0 = Vector4f.class.getDeclaredField("x");
         long var1 = UNSAFE.objectFieldOffset(var0);
         String[] var3 = new String[]{"y", "z", "w"};

         for(int var4 = 1; var4 < 4; ++var4) {
            var0 = Vector4f.class.getDeclaredField(var3[var4 - 1]);
            long var5 = UNSAFE.objectFieldOffset(var0);
            if (var5 != var1 + (long)(var4 << 2)) {
               throw new UnsupportedOperationException();
            }
         }

         return var1;
      }

      private static long checkVector4d() throws NoSuchFieldException, SecurityException {
         Field var0 = Vector4d.class.getDeclaredField("x");
         long var1 = UNSAFE.objectFieldOffset(var0);
         String[] var3 = new String[]{"y", "z", "w"};

         for(int var4 = 1; var4 < 4; ++var4) {
            var0 = Vector4d.class.getDeclaredField(var3[var4 - 1]);
            long var5 = UNSAFE.objectFieldOffset(var0);
            if (var5 != var1 + (long)(var4 << 3)) {
               throw new UnsupportedOperationException();
            }
         }

         return var1;
      }

      private static long checkVector4i() throws NoSuchFieldException, SecurityException {
         Field var0 = Vector4i.class.getDeclaredField("x");
         long var1 = UNSAFE.objectFieldOffset(var0);
         String[] var3 = new String[]{"y", "z", "w"};

         for(int var4 = 1; var4 < 4; ++var4) {
            var0 = Vector4i.class.getDeclaredField(var3[var4 - 1]);
            long var5 = UNSAFE.objectFieldOffset(var0);
            if (var5 != var1 + (long)(var4 << 2)) {
               throw new UnsupportedOperationException();
            }
         }

         return var1;
      }

      private static long checkVector3f() throws NoSuchFieldException, SecurityException {
         Field var0 = Vector3f.class.getDeclaredField("x");
         long var1 = UNSAFE.objectFieldOffset(var0);
         String[] var3 = new String[]{"y", "z"};

         for(int var4 = 1; var4 < 3; ++var4) {
            var0 = Vector3f.class.getDeclaredField(var3[var4 - 1]);
            long var5 = UNSAFE.objectFieldOffset(var0);
            if (var5 != var1 + (long)(var4 << 2)) {
               throw new UnsupportedOperationException();
            }
         }

         return var1;
      }

      private static long checkVector3d() throws NoSuchFieldException, SecurityException {
         Field var0 = Vector3d.class.getDeclaredField("x");
         long var1 = UNSAFE.objectFieldOffset(var0);
         String[] var3 = new String[]{"y", "z"};

         for(int var4 = 1; var4 < 3; ++var4) {
            var0 = Vector3d.class.getDeclaredField(var3[var4 - 1]);
            long var5 = UNSAFE.objectFieldOffset(var0);
            if (var5 != var1 + (long)(var4 << 3)) {
               throw new UnsupportedOperationException();
            }
         }

         return var1;
      }

      private static long checkVector3i() throws NoSuchFieldException, SecurityException {
         Field var0 = Vector3i.class.getDeclaredField("x");
         long var1 = UNSAFE.objectFieldOffset(var0);
         String[] var3 = new String[]{"y", "z"};

         for(int var4 = 1; var4 < 3; ++var4) {
            var0 = Vector3i.class.getDeclaredField(var3[var4 - 1]);
            long var5 = UNSAFE.objectFieldOffset(var0);
            if (var5 != var1 + (long)(var4 << 2)) {
               throw new UnsupportedOperationException();
            }
         }

         return var1;
      }

      private static long checkVector2f() throws NoSuchFieldException, SecurityException {
         Field var0 = Vector2f.class.getDeclaredField("x");
         long var1 = UNSAFE.objectFieldOffset(var0);
         var0 = Vector2f.class.getDeclaredField("y");
         long var3 = UNSAFE.objectFieldOffset(var0);
         if (var3 != var1 + 4L) {
            throw new UnsupportedOperationException();
         } else {
            return var1;
         }
      }

      private static long checkVector2d() throws NoSuchFieldException, SecurityException {
         Field var0 = Vector2d.class.getDeclaredField("x");
         long var1 = UNSAFE.objectFieldOffset(var0);
         var0 = Vector2d.class.getDeclaredField("y");
         long var3 = UNSAFE.objectFieldOffset(var0);
         if (var3 != var1 + 8L) {
            throw new UnsupportedOperationException();
         } else {
            return var1;
         }
      }

      private static long checkVector2i() throws NoSuchFieldException, SecurityException {
         Field var0 = Vector2i.class.getDeclaredField("x");
         long var1 = UNSAFE.objectFieldOffset(var0);
         var0 = Vector2i.class.getDeclaredField("y");
         long var3 = UNSAFE.objectFieldOffset(var0);
         if (var3 != var1 + 4L) {
            throw new UnsupportedOperationException();
         } else {
            return var1;
         }
      }

      private static long checkQuaternionf() throws NoSuchFieldException, SecurityException {
         Field var0 = Quaternionf.class.getDeclaredField("x");
         long var1 = UNSAFE.objectFieldOffset(var0);
         String[] var3 = new String[]{"y", "z", "w"};

         for(int var4 = 1; var4 < 4; ++var4) {
            var0 = Quaternionf.class.getDeclaredField(var3[var4 - 1]);
            long var5 = UNSAFE.objectFieldOffset(var0);
            if (var5 != var1 + (long)(var4 << 2)) {
               throw new UnsupportedOperationException();
            }
         }

         return var1;
      }

      private static final Field getDeclaredField(Class var0, String var1) throws NoSuchFieldException {
         Class var2 = var0;

         do {
            try {
               Field var3 = var2.getDeclaredField(var1);
               var3.setAccessible(true);
               return var3;
            } catch (NoSuchFieldException var4) {
               var2 = var2.getSuperclass();
            } catch (SecurityException var5) {
               var2 = var2.getSuperclass();
            }
         } while(var2 != null);

         throw new NoSuchFieldException(var1 + " does not exist in " + var0.getName() + " or any of its superclasses.");
      }

      private static final Unsafe getUnsafeInstance() throws SecurityException {
         Field[] var0 = Unsafe.class.getDeclaredFields();
         int var1 = 0;

         while(true) {
            label31: {
               if (var1 < var0.length) {
                  Field var2 = var0[var1];
                  if (!var2.getType().equals(Unsafe.class)) {
                     break label31;
                  }

                  int var3 = var2.getModifiers();
                  if (!Modifier.isStatic(var3) || !Modifier.isFinal(var3)) {
                     break label31;
                  }

                  var2.setAccessible(true);

                  try {
                     return (Unsafe)var2.get((Object)null);
                  } catch (IllegalAccessException var5) {
                  }
               }

               throw new UnsupportedOperationException();
            }

            ++var1;
         }
      }

      public final long addressOf(Buffer var1) {
         return UNSAFE.getLong(var1, ADDRESS);
      }

      public final void put(Matrix4f var1, long var2) {
         for(int var4 = 0; var4 < 8; ++var4) {
            UNSAFE.putOrderedLong((Object)null, var2 + (long)(var4 << 3), UNSAFE.getLong(var1, Matrix4f_m00 + (long)(var4 << 3)));
         }

      }

      public final void put(Matrix4x3f var1, long var2) {
         for(int var4 = 0; var4 < 6; ++var4) {
            UNSAFE.putOrderedLong((Object)null, var2 + (long)(var4 << 3), UNSAFE.getLong(var1, Matrix4x3f_m00 + (long)(var4 << 3)));
         }

      }

      public final void put4x4(Matrix4x3f var1, long var2) {
         for(int var4 = 0; var4 < 4; ++var4) {
            UNSAFE.putOrderedLong((Object)null, var2 + (long)(var4 << 4), UNSAFE.getLong(var1, Matrix4x3f_m00 + (long)(12 * var4)));
            long var5 = (long)UNSAFE.getInt(var1, Matrix4x3f_m00 + 8L + (long)(12 * var4)) & 4294967295L;
            UNSAFE.putOrderedLong((Object)null, var2 + 8L + (long)(var4 << 4), var5);
         }

         UNSAFE.putFloat((Object)null, var2 + 60L, 1.0F);
      }

      public final void put4x4(Matrix4x3d var1, long var2) {
         UNSAFE.putDouble((Object)null, var2, var1.m00);
         UNSAFE.putDouble((Object)null, var2 + 8L, var1.m01);
         UNSAFE.putDouble((Object)null, var2 + 16L, var1.m02);
         UNSAFE.putDouble((Object)null, var2 + 24L, 0.0D);
         UNSAFE.putDouble((Object)null, var2 + 32L, var1.m10);
         UNSAFE.putDouble((Object)null, var2 + 40L, var1.m11);
         UNSAFE.putDouble((Object)null, var2 + 48L, var1.m12);
         UNSAFE.putDouble((Object)null, var2 + 56L, 0.0D);
         UNSAFE.putDouble((Object)null, var2 + 64L, var1.m20);
         UNSAFE.putDouble((Object)null, var2 + 72L, var1.m21);
         UNSAFE.putDouble((Object)null, var2 + 80L, var1.m22);
         UNSAFE.putDouble((Object)null, var2 + 88L, 0.0D);
         UNSAFE.putDouble((Object)null, var2 + 96L, var1.m30);
         UNSAFE.putDouble((Object)null, var2 + 104L, var1.m31);
         UNSAFE.putDouble((Object)null, var2 + 112L, var1.m32);
         UNSAFE.putDouble((Object)null, var2 + 120L, 1.0D);
      }

      public final void putTransposed(Matrix4f var1, long var2) {
         UNSAFE.putFloat((Object)null, var2, var1.m00);
         UNSAFE.putFloat((Object)null, var2 + 4L, var1.m10);
         UNSAFE.putFloat((Object)null, var2 + 8L, var1.m20);
         UNSAFE.putFloat((Object)null, var2 + 12L, var1.m30);
         UNSAFE.putFloat((Object)null, var2 + 16L, var1.m01);
         UNSAFE.putFloat((Object)null, var2 + 20L, var1.m11);
         UNSAFE.putFloat((Object)null, var2 + 24L, var1.m21);
         UNSAFE.putFloat((Object)null, var2 + 28L, var1.m31);
         UNSAFE.putFloat((Object)null, var2 + 32L, var1.m02);
         UNSAFE.putFloat((Object)null, var2 + 36L, var1.m12);
         UNSAFE.putFloat((Object)null, var2 + 40L, var1.m22);
         UNSAFE.putFloat((Object)null, var2 + 44L, var1.m32);
         UNSAFE.putFloat((Object)null, var2 + 48L, var1.m03);
         UNSAFE.putFloat((Object)null, var2 + 52L, var1.m13);
         UNSAFE.putFloat((Object)null, var2 + 56L, var1.m23);
         UNSAFE.putFloat((Object)null, var2 + 60L, var1.m33);
      }

      public final void put4x3Transposed(Matrix4f var1, long var2) {
         UNSAFE.putFloat((Object)null, var2, var1.m00);
         UNSAFE.putFloat((Object)null, var2 + 4L, var1.m10);
         UNSAFE.putFloat((Object)null, var2 + 8L, var1.m20);
         UNSAFE.putFloat((Object)null, var2 + 12L, var1.m30);
         UNSAFE.putFloat((Object)null, var2 + 16L, var1.m01);
         UNSAFE.putFloat((Object)null, var2 + 20L, var1.m11);
         UNSAFE.putFloat((Object)null, var2 + 24L, var1.m21);
         UNSAFE.putFloat((Object)null, var2 + 28L, var1.m31);
         UNSAFE.putFloat((Object)null, var2 + 32L, var1.m02);
         UNSAFE.putFloat((Object)null, var2 + 36L, var1.m12);
         UNSAFE.putFloat((Object)null, var2 + 40L, var1.m22);
         UNSAFE.putFloat((Object)null, var2 + 44L, var1.m32);
      }

      public final void putTransposed(Matrix4x3f var1, long var2) {
         UNSAFE.putFloat((Object)null, var2, var1.m00);
         UNSAFE.putFloat((Object)null, var2 + 4L, var1.m10);
         UNSAFE.putFloat((Object)null, var2 + 8L, var1.m20);
         UNSAFE.putFloat((Object)null, var2 + 12L, var1.m30);
         UNSAFE.putFloat((Object)null, var2 + 16L, var1.m01);
         UNSAFE.putFloat((Object)null, var2 + 20L, var1.m11);
         UNSAFE.putFloat((Object)null, var2 + 24L, var1.m21);
         UNSAFE.putFloat((Object)null, var2 + 28L, var1.m31);
         UNSAFE.putFloat((Object)null, var2 + 32L, var1.m02);
         UNSAFE.putFloat((Object)null, var2 + 36L, var1.m12);
         UNSAFE.putFloat((Object)null, var2 + 40L, var1.m22);
         UNSAFE.putFloat((Object)null, var2 + 44L, var1.m32);
      }

      public final void putTransposed(Matrix3f var1, long var2) {
         UNSAFE.putFloat((Object)null, var2, var1.m00);
         UNSAFE.putFloat((Object)null, var2 + 4L, var1.m10);
         UNSAFE.putFloat((Object)null, var2 + 8L, var1.m20);
         UNSAFE.putFloat((Object)null, var2 + 12L, var1.m01);
         UNSAFE.putFloat((Object)null, var2 + 16L, var1.m11);
         UNSAFE.putFloat((Object)null, var2 + 20L, var1.m21);
         UNSAFE.putFloat((Object)null, var2 + 24L, var1.m02);
         UNSAFE.putFloat((Object)null, var2 + 28L, var1.m12);
         UNSAFE.putFloat((Object)null, var2 + 32L, var1.m22);
      }

      public final void put(Matrix4d var1, long var2) {
         UNSAFE.putDouble((Object)null, var2, var1.m00);
         UNSAFE.putDouble((Object)null, var2 + 8L, var1.m01);
         UNSAFE.putDouble((Object)null, var2 + 16L, var1.m02);
         UNSAFE.putDouble((Object)null, var2 + 24L, var1.m03);
         UNSAFE.putDouble((Object)null, var2 + 32L, var1.m10);
         UNSAFE.putDouble((Object)null, var2 + 40L, var1.m11);
         UNSAFE.putDouble((Object)null, var2 + 48L, var1.m12);
         UNSAFE.putDouble((Object)null, var2 + 56L, var1.m13);
         UNSAFE.putDouble((Object)null, var2 + 64L, var1.m20);
         UNSAFE.putDouble((Object)null, var2 + 72L, var1.m21);
         UNSAFE.putDouble((Object)null, var2 + 80L, var1.m22);
         UNSAFE.putDouble((Object)null, var2 + 88L, var1.m23);
         UNSAFE.putDouble((Object)null, var2 + 96L, var1.m30);
         UNSAFE.putDouble((Object)null, var2 + 104L, var1.m31);
         UNSAFE.putDouble((Object)null, var2 + 112L, var1.m32);
         UNSAFE.putDouble((Object)null, var2 + 120L, var1.m33);
      }

      public final void put(Matrix4x3d var1, long var2) {
         UNSAFE.putDouble((Object)null, var2, var1.m00);
         UNSAFE.putDouble((Object)null, var2 + 8L, var1.m01);
         UNSAFE.putDouble((Object)null, var2 + 16L, var1.m02);
         UNSAFE.putDouble((Object)null, var2 + 24L, var1.m10);
         UNSAFE.putDouble((Object)null, var2 + 32L, var1.m11);
         UNSAFE.putDouble((Object)null, var2 + 40L, var1.m12);
         UNSAFE.putDouble((Object)null, var2 + 48L, var1.m20);
         UNSAFE.putDouble((Object)null, var2 + 56L, var1.m21);
         UNSAFE.putDouble((Object)null, var2 + 64L, var1.m22);
         UNSAFE.putDouble((Object)null, var2 + 72L, var1.m30);
         UNSAFE.putDouble((Object)null, var2 + 80L, var1.m31);
         UNSAFE.putDouble((Object)null, var2 + 88L, var1.m32);
      }

      public final void putTransposed(Matrix4d var1, long var2) {
         UNSAFE.putDouble((Object)null, var2, var1.m00);
         UNSAFE.putDouble((Object)null, var2 + 8L, var1.m10);
         UNSAFE.putDouble((Object)null, var2 + 16L, var1.m20);
         UNSAFE.putDouble((Object)null, var2 + 24L, var1.m30);
         UNSAFE.putDouble((Object)null, var2 + 32L, var1.m01);
         UNSAFE.putDouble((Object)null, var2 + 40L, var1.m11);
         UNSAFE.putDouble((Object)null, var2 + 48L, var1.m21);
         UNSAFE.putDouble((Object)null, var2 + 56L, var1.m31);
         UNSAFE.putDouble((Object)null, var2 + 64L, var1.m02);
         UNSAFE.putDouble((Object)null, var2 + 72L, var1.m12);
         UNSAFE.putDouble((Object)null, var2 + 80L, var1.m22);
         UNSAFE.putDouble((Object)null, var2 + 88L, var1.m32);
         UNSAFE.putDouble((Object)null, var2 + 96L, var1.m03);
         UNSAFE.putDouble((Object)null, var2 + 104L, var1.m13);
         UNSAFE.putDouble((Object)null, var2 + 112L, var1.m23);
         UNSAFE.putDouble((Object)null, var2 + 120L, var1.m33);
      }

      public final void putfTransposed(Matrix4d var1, long var2) {
         UNSAFE.putFloat((Object)null, var2, (float)var1.m00);
         UNSAFE.putFloat((Object)null, var2 + 4L, (float)var1.m10);
         UNSAFE.putFloat((Object)null, var2 + 8L, (float)var1.m20);
         UNSAFE.putFloat((Object)null, var2 + 12L, (float)var1.m30);
         UNSAFE.putFloat((Object)null, var2 + 16L, (float)var1.m01);
         UNSAFE.putFloat((Object)null, var2 + 20L, (float)var1.m11);
         UNSAFE.putFloat((Object)null, var2 + 24L, (float)var1.m21);
         UNSAFE.putFloat((Object)null, var2 + 28L, (float)var1.m31);
         UNSAFE.putFloat((Object)null, var2 + 32L, (float)var1.m02);
         UNSAFE.putFloat((Object)null, var2 + 36L, (float)var1.m12);
         UNSAFE.putFloat((Object)null, var2 + 40L, (float)var1.m22);
         UNSAFE.putFloat((Object)null, var2 + 44L, (float)var1.m32);
         UNSAFE.putFloat((Object)null, var2 + 48L, (float)var1.m03);
         UNSAFE.putFloat((Object)null, var2 + 52L, (float)var1.m13);
         UNSAFE.putFloat((Object)null, var2 + 56L, (float)var1.m23);
         UNSAFE.putFloat((Object)null, var2 + 60L, (float)var1.m33);
      }

      public final void put4x3Transposed(Matrix4d var1, long var2) {
         UNSAFE.putDouble((Object)null, var2, var1.m00);
         UNSAFE.putDouble((Object)null, var2 + 8L, var1.m10);
         UNSAFE.putDouble((Object)null, var2 + 16L, var1.m20);
         UNSAFE.putDouble((Object)null, var2 + 24L, var1.m30);
         UNSAFE.putDouble((Object)null, var2 + 32L, var1.m01);
         UNSAFE.putDouble((Object)null, var2 + 40L, var1.m11);
         UNSAFE.putDouble((Object)null, var2 + 48L, var1.m21);
         UNSAFE.putDouble((Object)null, var2 + 56L, var1.m31);
         UNSAFE.putDouble((Object)null, var2 + 64L, var1.m02);
         UNSAFE.putDouble((Object)null, var2 + 72L, var1.m12);
         UNSAFE.putDouble((Object)null, var2 + 80L, var1.m22);
         UNSAFE.putDouble((Object)null, var2 + 88L, var1.m32);
      }

      public final void putTransposed(Matrix4x3d var1, long var2) {
         UNSAFE.putDouble((Object)null, var2, var1.m00);
         UNSAFE.putDouble((Object)null, var2 + 8L, var1.m10);
         UNSAFE.putDouble((Object)null, var2 + 16L, var1.m20);
         UNSAFE.putDouble((Object)null, var2 + 24L, var1.m30);
         UNSAFE.putDouble((Object)null, var2 + 32L, var1.m01);
         UNSAFE.putDouble((Object)null, var2 + 40L, var1.m11);
         UNSAFE.putDouble((Object)null, var2 + 48L, var1.m21);
         UNSAFE.putDouble((Object)null, var2 + 56L, var1.m31);
         UNSAFE.putDouble((Object)null, var2 + 64L, var1.m02);
         UNSAFE.putDouble((Object)null, var2 + 72L, var1.m12);
         UNSAFE.putDouble((Object)null, var2 + 80L, var1.m22);
         UNSAFE.putDouble((Object)null, var2 + 88L, var1.m32);
      }

      public final void putfTransposed(Matrix4x3d var1, long var2) {
         UNSAFE.putFloat((Object)null, var2, (float)var1.m00);
         UNSAFE.putFloat((Object)null, var2 + 4L, (float)var1.m10);
         UNSAFE.putFloat((Object)null, var2 + 8L, (float)var1.m20);
         UNSAFE.putFloat((Object)null, var2 + 12L, (float)var1.m30);
         UNSAFE.putFloat((Object)null, var2 + 16L, (float)var1.m01);
         UNSAFE.putFloat((Object)null, var2 + 20L, (float)var1.m11);
         UNSAFE.putFloat((Object)null, var2 + 24L, (float)var1.m21);
         UNSAFE.putFloat((Object)null, var2 + 28L, (float)var1.m31);
         UNSAFE.putFloat((Object)null, var2 + 32L, (float)var1.m02);
         UNSAFE.putFloat((Object)null, var2 + 36L, (float)var1.m12);
         UNSAFE.putFloat((Object)null, var2 + 40L, (float)var1.m22);
         UNSAFE.putFloat((Object)null, var2 + 44L, (float)var1.m32);
      }

      public final void putf(Matrix4d var1, long var2) {
         UNSAFE.putFloat((Object)null, var2, (float)var1.m00);
         UNSAFE.putFloat((Object)null, var2 + 4L, (float)var1.m01);
         UNSAFE.putFloat((Object)null, var2 + 8L, (float)var1.m02);
         UNSAFE.putFloat((Object)null, var2 + 12L, (float)var1.m03);
         UNSAFE.putFloat((Object)null, var2 + 16L, (float)var1.m10);
         UNSAFE.putFloat((Object)null, var2 + 20L, (float)var1.m11);
         UNSAFE.putFloat((Object)null, var2 + 24L, (float)var1.m12);
         UNSAFE.putFloat((Object)null, var2 + 28L, (float)var1.m13);
         UNSAFE.putFloat((Object)null, var2 + 32L, (float)var1.m20);
         UNSAFE.putFloat((Object)null, var2 + 36L, (float)var1.m21);
         UNSAFE.putFloat((Object)null, var2 + 40L, (float)var1.m22);
         UNSAFE.putFloat((Object)null, var2 + 44L, (float)var1.m23);
         UNSAFE.putFloat((Object)null, var2 + 48L, (float)var1.m30);
         UNSAFE.putFloat((Object)null, var2 + 52L, (float)var1.m31);
         UNSAFE.putFloat((Object)null, var2 + 56L, (float)var1.m32);
         UNSAFE.putFloat((Object)null, var2 + 60L, (float)var1.m33);
      }

      public final void putf(Matrix4x3d var1, long var2) {
         UNSAFE.putFloat((Object)null, var2, (float)var1.m00);
         UNSAFE.putFloat((Object)null, var2 + 4L, (float)var1.m01);
         UNSAFE.putFloat((Object)null, var2 + 8L, (float)var1.m02);
         UNSAFE.putFloat((Object)null, var2 + 12L, (float)var1.m10);
         UNSAFE.putFloat((Object)null, var2 + 16L, (float)var1.m11);
         UNSAFE.putFloat((Object)null, var2 + 20L, (float)var1.m12);
         UNSAFE.putFloat((Object)null, var2 + 24L, (float)var1.m20);
         UNSAFE.putFloat((Object)null, var2 + 28L, (float)var1.m21);
         UNSAFE.putFloat((Object)null, var2 + 32L, (float)var1.m22);
         UNSAFE.putFloat((Object)null, var2 + 36L, (float)var1.m30);
         UNSAFE.putFloat((Object)null, var2 + 40L, (float)var1.m31);
         UNSAFE.putFloat((Object)null, var2 + 44L, (float)var1.m32);
      }

      public final void put(Matrix3f var1, long var2) {
         for(int var4 = 0; var4 < 4; ++var4) {
            UNSAFE.putOrderedLong((Object)null, var2 + (long)(var4 << 3), UNSAFE.getLong(var1, Matrix3f_m00 + (long)(var4 << 3)));
         }

         UNSAFE.putFloat((Object)null, var2 + 32L, var1.m22);
      }

      public final void put(Matrix3d var1, long var2) {
         UNSAFE.putDouble((Object)null, var2, var1.m00);
         UNSAFE.putDouble((Object)null, var2 + 8L, var1.m01);
         UNSAFE.putDouble((Object)null, var2 + 16L, var1.m02);
         UNSAFE.putDouble((Object)null, var2 + 24L, var1.m10);
         UNSAFE.putDouble((Object)null, var2 + 32L, var1.m11);
         UNSAFE.putDouble((Object)null, var2 + 40L, var1.m12);
         UNSAFE.putDouble((Object)null, var2 + 48L, var1.m20);
         UNSAFE.putDouble((Object)null, var2 + 56L, var1.m21);
         UNSAFE.putDouble((Object)null, var2 + 64L, var1.m22);
      }

      public final void putf(Matrix3d var1, long var2) {
         UNSAFE.putFloat((Object)null, var2, (float)var1.m00);
         UNSAFE.putFloat((Object)null, var2 + 4L, (float)var1.m01);
         UNSAFE.putFloat((Object)null, var2 + 8L, (float)var1.m02);
         UNSAFE.putFloat((Object)null, var2 + 12L, (float)var1.m10);
         UNSAFE.putFloat((Object)null, var2 + 16L, (float)var1.m11);
         UNSAFE.putFloat((Object)null, var2 + 20L, (float)var1.m12);
         UNSAFE.putFloat((Object)null, var2 + 24L, (float)var1.m20);
         UNSAFE.putFloat((Object)null, var2 + 28L, (float)var1.m21);
         UNSAFE.putFloat((Object)null, var2 + 32L, (float)var1.m22);
      }

      public final void put(Vector4d var1, long var2) {
         for(int var4 = 0; var4 < 4; ++var4) {
            UNSAFE.putOrderedLong((Object)null, var2 + (long)(var4 << 3), UNSAFE.getLong(var1, Vector4d_x + (long)(var4 << 3)));
         }

      }

      public final void put(Vector4f var1, long var2) {
         UNSAFE.putOrderedLong((Object)null, var2, UNSAFE.getLong(var1, Vector4f_x));
         UNSAFE.putOrderedLong((Object)null, var2 + 8L, UNSAFE.getLong(var1, Vector4f_x + 8L));
      }

      public final void put(Vector4i var1, long var2) {
         UNSAFE.putOrderedLong((Object)null, var2, UNSAFE.getLong(var1, Vector4i_x));
         UNSAFE.putOrderedLong((Object)null, var2 + 8L, UNSAFE.getLong(var1, Vector4i_x + 8L));
      }

      public final void put(Vector3f var1, long var2) {
         UNSAFE.putOrderedLong((Object)null, var2, UNSAFE.getLong(var1, Vector3f_x));
         UNSAFE.putFloat((Object)null, var2 + 8L, UNSAFE.getFloat(var1, Vector3f_x + 8L));
      }

      public final void put(Vector3d var1, long var2) {
         UNSAFE.putDouble((Object)null, var2, UNSAFE.getDouble(var1, Vector3d_x));
         UNSAFE.putDouble((Object)null, var2 + 8L, UNSAFE.getDouble(var1, Vector3d_x + 8L));
         UNSAFE.putDouble((Object)null, var2 + 16L, UNSAFE.getDouble(var1, Vector3d_x + 16L));
      }

      public final void put(Vector3i var1, long var2) {
         UNSAFE.putOrderedLong((Object)null, var2, UNSAFE.getLong(var1, Vector3i_x));
         UNSAFE.putInt((Object)null, var2 + 8L, UNSAFE.getInt(var1, Vector3i_x + 8L));
      }

      public final void put(Vector2f var1, long var2) {
         UNSAFE.putOrderedLong((Object)null, var2, UNSAFE.getLong(var1, Vector2f_x));
      }

      public final void put(Vector2d var1, long var2) {
         UNSAFE.putDouble((Object)null, var2, UNSAFE.getDouble(var1, Vector2d_x));
         UNSAFE.putDouble((Object)null, var2 + 8L, UNSAFE.getDouble(var1, Vector2d_x + 8L));
      }

      public final void put(Vector2i var1, long var2) {
         UNSAFE.putOrderedLong((Object)null, var2, UNSAFE.getLong(var1, Vector2i_x));
      }

      public final void get(Matrix4f var1, long var2) {
         for(int var4 = 0; var4 < 8; ++var4) {
            UNSAFE.putOrderedLong(var1, Matrix4f_m00 + (long)(var4 << 3), UNSAFE.getLong(var2 + (long)(var4 << 3)));
         }

      }

      public final void get(Matrix4x3f var1, long var2) {
         for(int var4 = 0; var4 < 6; ++var4) {
            UNSAFE.putOrderedLong(var1, Matrix4x3f_m00 + (long)(var4 << 3), UNSAFE.getLong(var2 + (long)(var4 << 3)));
         }

      }

      public final void get(Matrix4d var1, long var2) {
         var1.m00 = UNSAFE.getDouble((Object)null, var2);
         var1.m01 = UNSAFE.getDouble((Object)null, var2 + 8L);
         var1.m02 = UNSAFE.getDouble((Object)null, var2 + 16L);
         var1.m03 = UNSAFE.getDouble((Object)null, var2 + 24L);
         var1.m10 = UNSAFE.getDouble((Object)null, var2 + 32L);
         var1.m11 = UNSAFE.getDouble((Object)null, var2 + 40L);
         var1.m12 = UNSAFE.getDouble((Object)null, var2 + 48L);
         var1.m13 = UNSAFE.getDouble((Object)null, var2 + 56L);
         var1.m20 = UNSAFE.getDouble((Object)null, var2 + 64L);
         var1.m21 = UNSAFE.getDouble((Object)null, var2 + 72L);
         var1.m22 = UNSAFE.getDouble((Object)null, var2 + 80L);
         var1.m23 = UNSAFE.getDouble((Object)null, var2 + 88L);
         var1.m30 = UNSAFE.getDouble((Object)null, var2 + 96L);
         var1.m31 = UNSAFE.getDouble((Object)null, var2 + 104L);
         var1.m32 = UNSAFE.getDouble((Object)null, var2 + 112L);
         var1.m33 = UNSAFE.getDouble((Object)null, var2 + 120L);
      }

      public final void get(Matrix4x3d var1, long var2) {
         var1.m00 = UNSAFE.getDouble((Object)null, var2);
         var1.m01 = UNSAFE.getDouble((Object)null, var2 + 8L);
         var1.m02 = UNSAFE.getDouble((Object)null, var2 + 16L);
         var1.m10 = UNSAFE.getDouble((Object)null, var2 + 24L);
         var1.m11 = UNSAFE.getDouble((Object)null, var2 + 32L);
         var1.m12 = UNSAFE.getDouble((Object)null, var2 + 40L);
         var1.m20 = UNSAFE.getDouble((Object)null, var2 + 48L);
         var1.m21 = UNSAFE.getDouble((Object)null, var2 + 56L);
         var1.m22 = UNSAFE.getDouble((Object)null, var2 + 64L);
         var1.m30 = UNSAFE.getDouble((Object)null, var2 + 72L);
         var1.m31 = UNSAFE.getDouble((Object)null, var2 + 80L);
         var1.m32 = UNSAFE.getDouble((Object)null, var2 + 88L);
      }

      public final void getf(Matrix4d var1, long var2) {
         var1.m00 = (double)UNSAFE.getFloat((Object)null, var2);
         var1.m01 = (double)UNSAFE.getFloat((Object)null, var2 + 4L);
         var1.m02 = (double)UNSAFE.getFloat((Object)null, var2 + 8L);
         var1.m03 = (double)UNSAFE.getFloat((Object)null, var2 + 12L);
         var1.m10 = (double)UNSAFE.getFloat((Object)null, var2 + 16L);
         var1.m11 = (double)UNSAFE.getFloat((Object)null, var2 + 20L);
         var1.m12 = (double)UNSAFE.getFloat((Object)null, var2 + 24L);
         var1.m13 = (double)UNSAFE.getFloat((Object)null, var2 + 28L);
         var1.m20 = (double)UNSAFE.getFloat((Object)null, var2 + 32L);
         var1.m21 = (double)UNSAFE.getFloat((Object)null, var2 + 36L);
         var1.m22 = (double)UNSAFE.getFloat((Object)null, var2 + 40L);
         var1.m23 = (double)UNSAFE.getFloat((Object)null, var2 + 44L);
         var1.m30 = (double)UNSAFE.getFloat((Object)null, var2 + 48L);
         var1.m31 = (double)UNSAFE.getFloat((Object)null, var2 + 52L);
         var1.m32 = (double)UNSAFE.getFloat((Object)null, var2 + 56L);
         var1.m33 = (double)UNSAFE.getFloat((Object)null, var2 + 60L);
      }

      public final void getf(Matrix4x3d var1, long var2) {
         var1.m00 = (double)UNSAFE.getFloat((Object)null, var2);
         var1.m01 = (double)UNSAFE.getFloat((Object)null, var2 + 4L);
         var1.m02 = (double)UNSAFE.getFloat((Object)null, var2 + 8L);
         var1.m10 = (double)UNSAFE.getFloat((Object)null, var2 + 12L);
         var1.m11 = (double)UNSAFE.getFloat((Object)null, var2 + 16L);
         var1.m12 = (double)UNSAFE.getFloat((Object)null, var2 + 20L);
         var1.m20 = (double)UNSAFE.getFloat((Object)null, var2 + 24L);
         var1.m21 = (double)UNSAFE.getFloat((Object)null, var2 + 28L);
         var1.m22 = (double)UNSAFE.getFloat((Object)null, var2 + 32L);
         var1.m30 = (double)UNSAFE.getFloat((Object)null, var2 + 36L);
         var1.m31 = (double)UNSAFE.getFloat((Object)null, var2 + 40L);
         var1.m32 = (double)UNSAFE.getFloat((Object)null, var2 + 44L);
      }

      public final void get(Matrix3f var1, long var2) {
         for(int var4 = 0; var4 < 4; ++var4) {
            UNSAFE.putOrderedLong(var1, Matrix3f_m00 + (long)(var4 << 3), UNSAFE.getLong((Object)null, var2 + (long)(var4 << 3)));
         }

         var1.m22 = UNSAFE.getFloat((Object)null, var2 + 32L);
      }

      public final void get(Matrix3d var1, long var2) {
         var1.m00 = UNSAFE.getDouble((Object)null, var2);
         var1.m01 = UNSAFE.getDouble((Object)null, var2 + 8L);
         var1.m02 = UNSAFE.getDouble((Object)null, var2 + 16L);
         var1.m10 = UNSAFE.getDouble((Object)null, var2 + 24L);
         var1.m11 = UNSAFE.getDouble((Object)null, var2 + 32L);
         var1.m12 = UNSAFE.getDouble((Object)null, var2 + 40L);
         var1.m20 = UNSAFE.getDouble((Object)null, var2 + 48L);
         var1.m21 = UNSAFE.getDouble((Object)null, var2 + 56L);
         var1.m22 = UNSAFE.getDouble((Object)null, var2 + 64L);
      }

      public final void getf(Matrix3d var1, long var2) {
         var1.m00 = (double)UNSAFE.getFloat((Object)null, var2);
         var1.m01 = (double)UNSAFE.getFloat((Object)null, var2 + 4L);
         var1.m02 = (double)UNSAFE.getFloat((Object)null, var2 + 8L);
         var1.m10 = (double)UNSAFE.getFloat((Object)null, var2 + 12L);
         var1.m11 = (double)UNSAFE.getFloat((Object)null, var2 + 16L);
         var1.m12 = (double)UNSAFE.getFloat((Object)null, var2 + 20L);
         var1.m20 = (double)UNSAFE.getFloat((Object)null, var2 + 24L);
         var1.m21 = (double)UNSAFE.getFloat((Object)null, var2 + 28L);
         var1.m22 = (double)UNSAFE.getFloat((Object)null, var2 + 32L);
      }

      public final void get(Vector4d var1, long var2) {
         for(int var4 = 0; var4 < 4; ++var4) {
            UNSAFE.putOrderedLong(var1, Vector4d_x + (long)(var4 << 3), UNSAFE.getLong((Object)null, var2 + (long)(var4 << 3)));
         }

      }

      public final void get(Vector4f var1, long var2) {
         UNSAFE.putOrderedLong(var1, Vector4f_x, UNSAFE.getLong((Object)null, var2));
         UNSAFE.putOrderedLong(var1, Vector4f_x + 8L, UNSAFE.getLong((Object)null, var2 + 8L));
      }

      public final void get(Vector4i var1, long var2) {
         UNSAFE.putOrderedLong(var1, Vector4i_x, UNSAFE.getLong((Object)null, var2));
         UNSAFE.putOrderedLong(var1, Vector4i_x + 8L, UNSAFE.getLong((Object)null, var2 + 8L));
      }

      public final void get(Vector3f var1, long var2) {
         UNSAFE.putOrderedLong(var1, Vector3f_x, UNSAFE.getLong((Object)null, var2));
         UNSAFE.putFloat(var1, Vector3f_x + 8L, UNSAFE.getFloat((Object)null, var2 + 8L));
      }

      public final void get(Vector3d var1, long var2) {
         UNSAFE.putDouble(var1, Vector3d_x, UNSAFE.getDouble((Object)null, var2));
         UNSAFE.putDouble(var1, Vector3d_x + 8L, UNSAFE.getDouble((Object)null, var2 + 8L));
         UNSAFE.putDouble(var1, Vector3d_x + 16L, UNSAFE.getDouble((Object)null, var2 + 16L));
      }

      public final void get(Vector3i var1, long var2) {
         UNSAFE.putOrderedLong(var1, Vector3i_x, UNSAFE.getLong((Object)null, var2));
         UNSAFE.putInt(var1, Vector3i_x + 8L, UNSAFE.getInt((Object)null, var2 + 8L));
      }

      public final void get(Vector2f var1, long var2) {
         UNSAFE.putOrderedLong(var1, Vector2f_x, UNSAFE.getLong((Object)null, var2));
      }

      public final void get(Vector2d var1, long var2) {
         UNSAFE.putDouble(var1, Vector2d_x, UNSAFE.getDouble((Object)null, var2));
         UNSAFE.putDouble(var1, Vector2d_x + 8L, UNSAFE.getDouble((Object)null, var2 + 8L));
      }

      public final void get(Vector2i var1, long var2) {
         UNSAFE.putOrderedLong(var1, Vector2i_x, UNSAFE.getLong((Object)null, var2));
      }

      public final void copy(Matrix4f var1, Matrix4f var2) {
         for(int var3 = 0; var3 < 8; ++var3) {
            UNSAFE.putOrderedLong(var2, Matrix4f_m00 + (long)(var3 << 3), UNSAFE.getLong(var1, Matrix4f_m00 + (long)(var3 << 3)));
         }

      }

      public final void copy(Matrix3f var1, Matrix4f var2) {
         for(int var3 = 0; var3 < 3; ++var3) {
            UNSAFE.putOrderedLong(var2, Matrix4f_m00 + (long)(var3 << 4), UNSAFE.getLong(var1, Matrix3f_m00 + (long)(12 * var3)));
            long var4 = (long)UNSAFE.getInt(var1, Matrix3f_m00 + 8L + (long)(12 * var3)) & 4294967295L;
            UNSAFE.putOrderedLong(var2, Matrix4f_m00 + 8L + (long)(var3 << 4), var4);
         }

         UNSAFE.putOrderedLong(var2, Matrix4f_m00 + 48L, 0L);
         UNSAFE.putOrderedLong(var2, Matrix4f_m00 + 56L, 4575657221408423936L);
      }

      public final void copy(Matrix4f var1, Matrix3f var2) {
         for(int var3 = 0; var3 < 3; ++var3) {
            UNSAFE.putOrderedLong(var2, Matrix3f_m00 + (long)(12 * var3), UNSAFE.getLong(var1, Matrix4f_m00 + (long)(var3 << 4)));
         }

         var2.m02 = var1.m02;
         var2.m12 = var1.m12;
         var2.m22 = var1.m22;
      }

      public final void copy(Matrix3f var1, Matrix4x3f var2) {
         for(int var3 = 0; var3 < 4; ++var3) {
            UNSAFE.putOrderedLong(var2, Matrix4x3f_m00 + (long)(var3 << 3), UNSAFE.getLong(var1, Matrix3f_m00 + (long)(var3 << 3)));
         }

         UNSAFE.putOrderedLong(var2, Matrix4x3f_m00 + 32L, (long)UNSAFE.getInt(var1, Matrix3f_m00 + 32L) & 4294967295L);
         UNSAFE.putOrderedLong(var2, Matrix4x3f_m00 + 40L, 0L);
      }

      public final void copy3x3(Matrix4f var1, Matrix4f var2) {
         for(int var3 = 0; var3 < 3; ++var3) {
            UNSAFE.putOrderedLong(var2, Matrix4f_m00 + (long)(var3 << 4), UNSAFE.getLong(var1, Matrix4f_m00 + (long)(var3 << 4)));
         }

         var2.m02 = var1.m02;
         var2.m12 = var1.m12;
         var2.m22 = var1.m22;
      }

      public final void copy3x3(Matrix4x3f var1, Matrix4x3f var2) {
         for(int var3 = 0; var3 < 4; ++var3) {
            UNSAFE.putOrderedLong(var2, Matrix4x3f_m00 + (long)(var3 << 3), UNSAFE.getLong(var1, Matrix4x3f_m00 + (long)(var3 << 3)));
         }

         var2.m22 = var1.m22;
      }

      public final void copy3x3(Matrix3f var1, Matrix4x3f var2) {
         for(int var3 = 0; var3 < 4; ++var3) {
            UNSAFE.putOrderedLong(var2, Matrix4x3f_m00 + (long)(var3 << 3), UNSAFE.getLong(var1, Matrix3f_m00 + (long)(var3 << 3)));
         }

         var2.m22 = var1.m22;
      }

      public final void copy3x3(Matrix3f var1, Matrix4f var2) {
         for(int var3 = 0; var3 < 3; ++var3) {
            UNSAFE.putOrderedLong(var2, Matrix4f_m00 + (long)(var3 << 4), UNSAFE.getLong(var1, Matrix3f_m00 + (long)(12 * var3)));
         }

         var2.m02 = var1.m02;
         var2.m12 = var1.m12;
         var2.m22 = var1.m22;
      }

      public final void copy4x3(Matrix4x3f var1, Matrix4f var2) {
         for(int var3 = 0; var3 < 4; ++var3) {
            UNSAFE.putOrderedLong(var2, Matrix4f_m00 + (long)(var3 << 4), UNSAFE.getLong(var1, Matrix4x3f_m00 + (long)(12 * var3)));
         }

         var2.m02 = var1.m02;
         var2.m12 = var1.m12;
         var2.m22 = var1.m22;
         var2.m32 = var1.m32;
      }

      public final void copy4x3(Matrix4f var1, Matrix4f var2) {
         for(int var3 = 0; var3 < 4; ++var3) {
            UNSAFE.putOrderedLong(var2, Matrix4f_m00 + (long)(var3 << 4), UNSAFE.getLong(var1, Matrix4f_m00 + (long)(var3 << 4)));
         }

         var2.m02 = var1.m02;
         var2.m12 = var1.m12;
         var2.m22 = var1.m22;
         var2.m32 = var1.m32;
      }

      public final void copy(Matrix4f var1, Matrix4x3f var2) {
         for(int var3 = 0; var3 < 4; ++var3) {
            UNSAFE.putOrderedLong(var2, Matrix4x3f_m00 + (long)(12 * var3), UNSAFE.getLong(var1, Matrix4f_m00 + (long)(var3 << 4)));
         }

         var2.m02 = var1.m02;
         var2.m12 = var1.m12;
         var2.m22 = var1.m22;
         var2.m32 = var1.m32;
      }

      public final void copy(Matrix4x3f var1, Matrix4f var2) {
         for(int var3 = 0; var3 < 4; ++var3) {
            UNSAFE.putOrderedLong(var2, Matrix4f_m00 + (long)(var3 << 4), UNSAFE.getLong(var1, Matrix4x3f_m00 + (long)(12 * var3)));
         }

         UNSAFE.putOrderedLong(var2, Matrix4f_m00 + 8L, (long)UNSAFE.getInt(var1, Matrix4x3f_m00 + 8L) & 4294967295L);
         UNSAFE.putOrderedLong(var2, Matrix4f_m00 + 24L, (long)UNSAFE.getInt(var1, Matrix4x3f_m00 + 20L) & 4294967295L);
         UNSAFE.putOrderedLong(var2, Matrix4f_m00 + 40L, (long)UNSAFE.getInt(var1, Matrix4x3f_m00 + 32L) & 4294967295L);
         UNSAFE.putOrderedLong(var2, Matrix4f_m00 + 56L, 4575657221408423936L | (long)UNSAFE.getInt(var1, Matrix4x3f_m00 + 44L) & 4294967295L);
      }

      public final void copy(Matrix4x3f var1, Matrix4x3f var2) {
         for(int var3 = 0; var3 < 6; ++var3) {
            UNSAFE.putOrderedLong(var2, Matrix4x3f_m00 + (long)(var3 << 3), UNSAFE.getLong(var1, Matrix4x3f_m00 + (long)(var3 << 3)));
         }

      }

      public final void copy(Matrix3f var1, Matrix3f var2) {
         for(int var3 = 0; var3 < 4; ++var3) {
            UNSAFE.putOrderedLong(var2, Matrix3f_m00 + (long)(var3 << 3), UNSAFE.getLong(var1, Matrix3f_m00 + (long)(var3 << 3)));
         }

         var2.m22 = var1.m22;
      }

      public final void copy(Vector4f var1, Vector4f var2) {
         UNSAFE.putOrderedLong(var2, Vector4f_x, UNSAFE.getLong(var1, Vector4f_x));
         UNSAFE.putOrderedLong(var2, Vector4f_x + 8L, UNSAFE.getLong(var1, Vector4f_x + 8L));
      }

      public final void copy(Vector4i var1, Vector4i var2) {
         UNSAFE.putOrderedLong(var2, Vector4i_x, UNSAFE.getLong(var1, Vector4i_x));
         UNSAFE.putOrderedLong(var2, Vector4i_x + 8L, UNSAFE.getLong(var1, Vector4i_x + 8L));
      }

      public final void copy(Quaternionf var1, Quaternionf var2) {
         UNSAFE.putOrderedLong(var2, Quaternionf_x, UNSAFE.getLong(var1, Quaternionf_x));
         UNSAFE.putOrderedLong(var2, Quaternionf_x + 8L, UNSAFE.getLong(var1, Quaternionf_x + 8L));
      }

      public final void copy(float[] var1, int var2, Matrix4f var3) {
         for(int var4 = 0; var4 < 8; ++var4) {
            UNSAFE.putOrderedLong(var3, Matrix4f_m00 + (long)(var4 << 3), UNSAFE.getLong(var1, floatArrayOffset + (long)(var2 << 2) + (long)(var4 << 3)));
         }

      }

      public final void copy(float[] var1, int var2, Matrix3f var3) {
         for(int var4 = 0; var4 < 4; ++var4) {
            UNSAFE.putOrderedLong(var3, Matrix3f_m00 + (long)(var4 << 3), UNSAFE.getLong(var1, floatArrayOffset + (long)(var2 << 2) + (long)(var4 << 3)));
         }

         UNSAFE.putFloat(var3, Matrix3f_m00 + 32L, UNSAFE.getFloat(var1, floatArrayOffset + (long)(var2 << 2) + 32L));
      }

      public final void copy(float[] var1, int var2, Matrix4x3f var3) {
         for(int var4 = 0; var4 < 6; ++var4) {
            UNSAFE.putOrderedLong(var3, Matrix4x3f_m00 + (long)(var4 << 3), UNSAFE.getLong(var1, floatArrayOffset + (long)(var2 << 2) + (long)(var4 << 3)));
         }

      }

      public final void copy(Matrix4f var1, float[] var2, int var3) {
         for(int var4 = 0; var4 < 8; ++var4) {
            UNSAFE.putOrderedLong(var2, floatArrayOffset + (long)(var3 << 2) + (long)(var4 << 3), UNSAFE.getLong(var1, Matrix4f_m00 + (long)(var4 << 3)));
         }

      }

      public final void copy(Matrix3f var1, float[] var2, int var3) {
         for(int var4 = 0; var4 < 4; ++var4) {
            UNSAFE.putOrderedLong(var2, floatArrayOffset + (long)(var3 << 2) + (long)(var4 << 3), UNSAFE.getLong(var1, Matrix3f_m00 + (long)(var4 << 3)));
         }

         UNSAFE.putFloat(var2, floatArrayOffset + (long)(var3 << 2) + 32L, UNSAFE.getFloat(var1, Matrix3f_m00 + 32L));
      }

      public final void copy(Matrix4x3f var1, float[] var2, int var3) {
         for(int var4 = 0; var4 < 6; ++var4) {
            UNSAFE.putOrderedLong(var2, floatArrayOffset + (long)(var3 << 2) + (long)(var4 << 3), UNSAFE.getLong(var1, Matrix4x3f_m00 + (long)(var4 << 3)));
         }

      }

      public final void identity(Matrix4f var1) {
         UNSAFE.putOrderedLong(var1, Matrix4f_m00, 1065353216L);
         UNSAFE.putOrderedLong(var1, Matrix4f_m00 + 8L, 0L);
         UNSAFE.putOrderedLong(var1, Matrix4f_m00 + 16L, 4575657221408423936L);
         UNSAFE.putOrderedLong(var1, Matrix4f_m00 + 24L, 0L);
         UNSAFE.putOrderedLong(var1, Matrix4f_m00 + 32L, 0L);
         UNSAFE.putOrderedLong(var1, Matrix4f_m00 + 40L, 1065353216L);
         UNSAFE.putOrderedLong(var1, Matrix4f_m00 + 48L, 0L);
         UNSAFE.putOrderedLong(var1, Matrix4f_m00 + 56L, 4575657221408423936L);
      }

      public final void identity(Matrix4x3f var1) {
         UNSAFE.putOrderedLong(var1, Matrix4x3f_m00, 1065353216L);
         UNSAFE.putOrderedLong(var1, Matrix4x3f_m00 + 8L, 0L);
         UNSAFE.putOrderedLong(var1, Matrix4x3f_m00 + 16L, 1065353216L);
         UNSAFE.putOrderedLong(var1, Matrix4x3f_m00 + 24L, 0L);
         UNSAFE.putOrderedLong(var1, Matrix4x3f_m00 + 32L, 1065353216L);
         UNSAFE.putOrderedLong(var1, Matrix4x3f_m00 + 40L, 0L);
      }

      public final void identity(Matrix3f var1) {
         UNSAFE.putOrderedLong(var1, Matrix3f_m00, 1065353216L);
         UNSAFE.putOrderedLong(var1, Matrix3f_m00 + 8L, 0L);
         UNSAFE.putOrderedLong(var1, Matrix3f_m00 + 16L, 1065353216L);
         UNSAFE.putOrderedLong(var1, Matrix3f_m00 + 24L, 0L);
         var1.m22 = 1.0F;
      }

      public final void identity(Quaternionf var1) {
         UNSAFE.putOrderedLong(var1, Quaternionf_x, 0L);
         UNSAFE.putOrderedLong(var1, Quaternionf_x + 8L, 4575657221408423936L);
      }

      public final void swap(Matrix4f var1, Matrix4f var2) {
         for(int var3 = 0; var3 < 8; ++var3) {
            long var4 = UNSAFE.getLong(var1, Matrix4f_m00 + (long)(var3 << 3));
            UNSAFE.putOrderedLong(var1, Matrix4f_m00 + (long)(var3 << 3), UNSAFE.getLong(var2, Matrix4f_m00 + (long)(var3 << 3)));
            UNSAFE.putOrderedLong(var2, Matrix4f_m00 + (long)(var3 << 3), var4);
         }

      }

      public final void swap(Matrix4x3f var1, Matrix4x3f var2) {
         for(int var3 = 0; var3 < 6; ++var3) {
            long var4 = UNSAFE.getLong(var1, Matrix4x3f_m00 + (long)(var3 << 3));
            UNSAFE.putOrderedLong(var1, Matrix4x3f_m00 + (long)(var3 << 3), UNSAFE.getLong(var2, Matrix4x3f_m00 + (long)(var3 << 3)));
            UNSAFE.putOrderedLong(var2, Matrix4x3f_m00 + (long)(var3 << 3), var4);
         }

      }

      public final void swap(Matrix3f var1, Matrix3f var2) {
         for(int var3 = 0; var3 < 4; ++var3) {
            long var4 = UNSAFE.getLong(var1, Matrix3f_m00 + (long)(var3 << 3));
            UNSAFE.putOrderedLong(var1, Matrix3f_m00 + (long)(var3 << 3), UNSAFE.getLong(var2, Matrix3f_m00 + (long)(var3 << 3)));
            UNSAFE.putOrderedLong(var2, Matrix3f_m00 + (long)(var3 << 3), var4);
         }

         float var6 = var1.m22;
         var1.m22 = var2.m22;
         var2.m22 = var6;
      }

      public final void zero(Matrix4f var1) {
         for(int var2 = 0; var2 < 8; ++var2) {
            UNSAFE.putOrderedLong(var1, Matrix4f_m00 + (long)(var2 << 3), 0L);
         }

      }

      public final void zero(Matrix4x3f var1) {
         for(int var2 = 0; var2 < 6; ++var2) {
            UNSAFE.putOrderedLong(var1, Matrix4x3f_m00 + (long)(var2 << 3), 0L);
         }

      }

      public final void zero(Matrix3f var1) {
         for(int var2 = 0; var2 < 4; ++var2) {
            UNSAFE.putOrderedLong(var1, Matrix3f_m00 + (long)(var2 << 3), 0L);
         }

         var1.m22 = 0.0F;
      }

      public final void zero(Vector4f var1) {
         UNSAFE.putOrderedLong(var1, Vector4f_x, 0L);
         UNSAFE.putOrderedLong(var1, Vector4f_x + 8L, 0L);
      }

      public final void zero(Vector4i var1) {
         UNSAFE.putOrderedLong(var1, Vector4i_x, 0L);
         UNSAFE.putOrderedLong(var1, Vector4i_x + 8L, 0L);
      }

      private void putMatrix3f(Quaternionf var1, long var2) {
         float var4 = var1.x + var1.x;
         float var5 = var1.y + var1.y;
         float var6 = var1.z + var1.z;
         float var7 = var4 * var1.x;
         float var8 = var5 * var1.y;
         float var9 = var6 * var1.z;
         float var10 = var4 * var1.y;
         float var11 = var4 * var1.z;
         float var12 = var4 * var1.w;
         float var13 = var5 * var1.z;
         float var14 = var5 * var1.w;
         float var15 = var6 * var1.w;
         UNSAFE.putFloat((Object)null, var2, 1.0F - var8 - var9);
         UNSAFE.putFloat((Object)null, var2 + 4L, var10 + var15);
         UNSAFE.putFloat((Object)null, var2 + 8L, var11 - var14);
         UNSAFE.putFloat((Object)null, var2 + 12L, var10 - var15);
         UNSAFE.putFloat((Object)null, var2 + 16L, 1.0F - var9 - var7);
         UNSAFE.putFloat((Object)null, var2 + 20L, var13 + var12);
         UNSAFE.putFloat((Object)null, var2 + 24L, var11 + var14);
         UNSAFE.putFloat((Object)null, var2 + 28L, var13 - var12);
         UNSAFE.putFloat((Object)null, var2 + 32L, 1.0F - var8 - var7);
      }

      private void putMatrix4f(Quaternionf var1, long var2) {
         float var4 = var1.x + var1.x;
         float var5 = var1.y + var1.y;
         float var6 = var1.z + var1.z;
         float var7 = var4 * var1.x;
         float var8 = var5 * var1.y;
         float var9 = var6 * var1.z;
         float var10 = var4 * var1.y;
         float var11 = var4 * var1.z;
         float var12 = var4 * var1.w;
         float var13 = var5 * var1.z;
         float var14 = var5 * var1.w;
         float var15 = var6 * var1.w;
         UNSAFE.putFloat((Object)null, var2, 1.0F - var8 - var9);
         UNSAFE.putFloat((Object)null, var2 + 4L, var10 + var15);
         UNSAFE.putOrderedLong((Object)null, var2 + 8L, (long)Float.floatToRawIntBits(var11 - var14) & 4294967295L);
         UNSAFE.putFloat((Object)null, var2 + 16L, var10 - var15);
         UNSAFE.putFloat((Object)null, var2 + 20L, 1.0F - var9 - var7);
         UNSAFE.putOrderedLong((Object)null, var2 + 24L, (long)Float.floatToRawIntBits(var13 + var12) & 4294967295L);
         UNSAFE.putFloat((Object)null, var2 + 32L, var11 + var14);
         UNSAFE.putFloat((Object)null, var2 + 36L, var13 - var12);
         UNSAFE.putOrderedLong((Object)null, var2 + 40L, (long)Float.floatToRawIntBits(1.0F - var8 - var7) & 4294967295L);
         UNSAFE.putOrderedLong((Object)null, var2 + 48L, 0L);
         UNSAFE.putOrderedLong((Object)null, var2 + 56L, 4575657221408423936L);
      }

      private void putMatrix4x3f(Quaternionf var1, long var2) {
         float var4 = var1.x + var1.x;
         float var5 = var1.y + var1.y;
         float var6 = var1.z + var1.z;
         float var7 = var4 * var1.x;
         float var8 = var5 * var1.y;
         float var9 = var6 * var1.z;
         float var10 = var4 * var1.y;
         float var11 = var4 * var1.z;
         float var12 = var4 * var1.w;
         float var13 = var5 * var1.z;
         float var14 = var5 * var1.w;
         float var15 = var6 * var1.w;
         UNSAFE.putFloat((Object)null, var2, 1.0F - var8 - var9);
         UNSAFE.putFloat((Object)null, var2 + 4L, var10 + var15);
         UNSAFE.putFloat((Object)null, var2 + 8L, var11 - var14);
         UNSAFE.putFloat((Object)null, var2 + 12L, var10 - var15);
         UNSAFE.putFloat((Object)null, var2 + 16L, 1.0F - var9 - var7);
         UNSAFE.putFloat((Object)null, var2 + 20L, var13 + var12);
         UNSAFE.putFloat((Object)null, var2 + 24L, var11 + var14);
         UNSAFE.putFloat((Object)null, var2 + 28L, var13 - var12);
         UNSAFE.putFloat((Object)null, var2 + 32L, 1.0F - var8 - var7);
         UNSAFE.putOrderedLong((Object)null, var2 + 36L, 0L);
         UNSAFE.putFloat((Object)null, var2 + 44L, 0.0F);
      }

      private static void throwNoDirectBufferException() {
         throw new IllegalArgumentException("Must use a direct buffer");
      }

      public final void putMatrix3f(Quaternionf var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         long var4 = this.addressOf(var3) + (long)var2;
         this.putMatrix3f(var1, var4);
      }

      public final void putMatrix3f(Quaternionf var1, int var2, FloatBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         long var4 = this.addressOf(var3) + (long)(var2 << 2);
         this.putMatrix3f(var1, var4);
      }

      public final void putMatrix4f(Quaternionf var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         long var4 = this.addressOf(var3) + (long)var2;
         this.putMatrix4f(var1, var4);
      }

      public final void putMatrix4f(Quaternionf var1, int var2, FloatBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         long var4 = this.addressOf(var3) + (long)(var2 << 2);
         this.putMatrix4f(var1, var4);
      }

      public final void putMatrix4x3f(Quaternionf var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         long var4 = this.addressOf(var3) + (long)var2;
         this.putMatrix4x3f(var1, var4);
      }

      public final void putMatrix4x3f(Quaternionf var1, int var2, FloatBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         long var4 = this.addressOf(var3) + (long)(var2 << 2);
         this.putMatrix4x3f(var1, var4);
      }

      public final void put(Matrix4f var1, int var2, FloatBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put(var1, this.addressOf(var3) + (long)(var2 << 2));
      }

      public final void put(Matrix4f var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put(var1, this.addressOf(var3) + (long)var2);
      }

      public final void put(Matrix4x3f var1, int var2, FloatBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put(var1, this.addressOf(var3) + (long)(var2 << 2));
      }

      public final void put(Matrix4x3f var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put(var1, this.addressOf(var3) + (long)var2);
      }

      public final void put4x4(Matrix4x3f var1, int var2, FloatBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put4x4(var1, this.addressOf(var3) + (long)(var2 << 2));
      }

      public final void put4x4(Matrix4x3f var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put4x4(var1, this.addressOf(var3) + (long)var2);
      }

      public final void put4x4(Matrix4x3d var1, int var2, DoubleBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put4x4(var1, this.addressOf(var3) + (long)(var2 << 3));
      }

      public final void put4x4(Matrix4x3d var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put4x4(var1, this.addressOf(var3) + (long)var2);
      }

      public final void putTransposed(Matrix4f var1, int var2, FloatBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.putTransposed(var1, this.addressOf(var3) + (long)(var2 << 2));
      }

      public final void putTransposed(Matrix4f var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.putTransposed(var1, this.addressOf(var3) + (long)var2);
      }

      public final void put4x3Transposed(Matrix4f var1, int var2, FloatBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put4x3Transposed(var1, this.addressOf(var3) + (long)(var2 << 2));
      }

      public final void put4x3Transposed(Matrix4f var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put4x3Transposed(var1, this.addressOf(var3) + (long)var2);
      }

      public final void putTransposed(Matrix4x3f var1, int var2, FloatBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.putTransposed(var1, this.addressOf(var3) + (long)(var2 << 2));
      }

      public final void putTransposed(Matrix4x3f var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.putTransposed(var1, this.addressOf(var3) + (long)var2);
      }

      public final void putTransposed(Matrix3f var1, int var2, FloatBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.putTransposed(var1, this.addressOf(var3) + (long)(var2 << 2));
      }

      public final void putTransposed(Matrix3f var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.putTransposed(var1, this.addressOf(var3) + (long)var2);
      }

      public final void put(Matrix4d var1, int var2, DoubleBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put(var1, this.addressOf(var3) + (long)(var2 << 3));
      }

      public final void put(Matrix4d var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put(var1, this.addressOf(var3) + (long)var2);
      }

      public final void put(Matrix4x3d var1, int var2, DoubleBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put(var1, this.addressOf(var3) + (long)(var2 << 3));
      }

      public final void put(Matrix4x3d var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put(var1, this.addressOf(var3) + (long)var2);
      }

      public final void putf(Matrix4d var1, int var2, FloatBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.putf(var1, this.addressOf(var3) + (long)(var2 << 2));
      }

      public final void putf(Matrix4d var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.putf(var1, this.addressOf(var3) + (long)var2);
      }

      public final void putf(Matrix4x3d var1, int var2, FloatBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.putf(var1, this.addressOf(var3) + (long)(var2 << 2));
      }

      public final void putf(Matrix4x3d var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.putf(var1, this.addressOf(var3) + (long)var2);
      }

      public final void putTransposed(Matrix4d var1, int var2, DoubleBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.putTransposed(var1, this.addressOf(var3) + (long)(var2 << 3));
      }

      public final void putTransposed(Matrix4d var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.putTransposed(var1, this.addressOf(var3) + (long)var2);
      }

      public final void put4x3Transposed(Matrix4d var1, int var2, DoubleBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put4x3Transposed(var1, this.addressOf(var3) + (long)(var2 << 3));
      }

      public final void put4x3Transposed(Matrix4d var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put4x3Transposed(var1, this.addressOf(var3) + (long)var2);
      }

      public final void putTransposed(Matrix4x3d var1, int var2, DoubleBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.putTransposed(var1, this.addressOf(var3) + (long)(var2 << 3));
      }

      public final void putTransposed(Matrix4x3d var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.putTransposed(var1, this.addressOf(var3) + (long)var2);
      }

      public final void putfTransposed(Matrix4d var1, int var2, FloatBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.putfTransposed(var1, this.addressOf(var3) + (long)(var2 << 2));
      }

      public final void putfTransposed(Matrix4d var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.putfTransposed(var1, this.addressOf(var3) + (long)var2);
      }

      public final void putfTransposed(Matrix4x3d var1, int var2, FloatBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.putfTransposed(var1, this.addressOf(var3) + (long)(var2 << 2));
      }

      public final void putfTransposed(Matrix4x3d var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.putfTransposed(var1, this.addressOf(var3) + (long)var2);
      }

      public final void put(Matrix3f var1, int var2, FloatBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put(var1, this.addressOf(var3) + (long)(var2 << 2));
      }

      public final void put(Matrix3f var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put(var1, this.addressOf(var3) + (long)var2);
      }

      public final void put(Matrix3d var1, int var2, DoubleBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put(var1, this.addressOf(var3) + (long)(var2 << 3));
      }

      public final void put(Matrix3d var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put(var1, this.addressOf(var3) + (long)var2);
      }

      public final void putf(Matrix3d var1, int var2, FloatBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.putf(var1, this.addressOf(var3) + (long)(var2 << 2));
      }

      public final void putf(Matrix3d var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.putf(var1, this.addressOf(var3) + (long)var2);
      }

      public final void put(Vector4d var1, int var2, DoubleBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put(var1, this.addressOf(var3) + (long)(var2 << 3));
      }

      public final void put(Vector4d var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put(var1, this.addressOf(var3) + (long)var2);
      }

      public final void put(Vector4f var1, int var2, FloatBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put(var1, this.addressOf(var3) + (long)(var2 << 2));
      }

      public final void put(Vector4f var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put(var1, this.addressOf(var3) + (long)var2);
      }

      public final void put(Vector4i var1, int var2, IntBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put(var1, this.addressOf(var3) + (long)(var2 << 2));
      }

      public final void put(Vector4i var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put(var1, this.addressOf(var3) + (long)var2);
      }

      public final void put(Vector3f var1, int var2, FloatBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put(var1, this.addressOf(var3) + (long)(var2 << 2));
      }

      public final void put(Vector3f var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put(var1, this.addressOf(var3) + (long)var2);
      }

      public final void put(Vector3d var1, int var2, DoubleBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put(var1, this.addressOf(var3) + (long)(var2 << 3));
      }

      public final void put(Vector3d var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put(var1, this.addressOf(var3) + (long)var2);
      }

      public final void put(Vector3i var1, int var2, IntBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put(var1, this.addressOf(var3) + (long)(var2 << 2));
      }

      public final void put(Vector3i var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put(var1, this.addressOf(var3) + (long)var2);
      }

      public final void put(Vector2f var1, int var2, FloatBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put(var1, this.addressOf(var3) + (long)(var2 << 2));
      }

      public final void put(Vector2f var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put(var1, this.addressOf(var3) + (long)var2);
      }

      public final void put(Vector2d var1, int var2, DoubleBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put(var1, this.addressOf(var3) + (long)(var2 << 3));
      }

      public final void put(Vector2d var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put(var1, this.addressOf(var3) + (long)var2);
      }

      public final void put(Vector2i var1, int var2, IntBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put(var1, this.addressOf(var3) + (long)(var2 << 2));
      }

      public final void put(Vector2i var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.put(var1, this.addressOf(var3) + (long)var2);
      }

      public final void get(Matrix4f var1, int var2, FloatBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.get(var1, this.addressOf(var3) + (long)(var2 << 2));
      }

      public final void get(Matrix4f var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.get(var1, this.addressOf(var3) + (long)var2);
      }

      public final void get(Matrix4x3f var1, int var2, FloatBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.get(var1, this.addressOf(var3) + (long)(var2 << 2));
      }

      public final void get(Matrix4x3f var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.get(var1, this.addressOf(var3) + (long)var2);
      }

      public final void get(Matrix4d var1, int var2, DoubleBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.get(var1, this.addressOf(var3) + (long)(var2 << 3));
      }

      public final void get(Matrix4d var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.get(var1, this.addressOf(var3) + (long)var2);
      }

      public final void get(Matrix4x3d var1, int var2, DoubleBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.get(var1, this.addressOf(var3) + (long)(var2 << 3));
      }

      public final void get(Matrix4x3d var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.get(var1, this.addressOf(var3) + (long)var2);
      }

      public final void getf(Matrix4d var1, int var2, FloatBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.getf(var1, this.addressOf(var3) + (long)(var2 << 2));
      }

      public final void getf(Matrix4d var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.getf(var1, this.addressOf(var3) + (long)var2);
      }

      public final void getf(Matrix4x3d var1, int var2, FloatBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.getf(var1, this.addressOf(var3) + (long)(var2 << 2));
      }

      public final void getf(Matrix4x3d var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.getf(var1, this.addressOf(var3) + (long)var2);
      }

      public final void get(Matrix3f var1, int var2, FloatBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.get(var1, this.addressOf(var3) + (long)(var2 << 2));
      }

      public final void get(Matrix3f var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.get(var1, this.addressOf(var3) + (long)var2);
      }

      public final void get(Matrix3d var1, int var2, DoubleBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.get(var1, this.addressOf(var3) + (long)(var2 << 3));
      }

      public final void get(Matrix3d var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.get(var1, this.addressOf(var3) + (long)var2);
      }

      public final void getf(Matrix3d var1, int var2, FloatBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.getf(var1, this.addressOf(var3) + (long)(var2 << 2));
      }

      public final void getf(Matrix3d var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.getf(var1, this.addressOf(var3) + (long)var2);
      }

      public final void get(Vector4d var1, int var2, DoubleBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.get(var1, this.addressOf(var3) + (long)(var2 << 3));
      }

      public final void get(Vector4d var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.get(var1, this.addressOf(var3) + (long)var2);
      }

      public final void get(Vector4f var1, int var2, FloatBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.get(var1, this.addressOf(var3) + (long)(var2 << 2));
      }

      public final void get(Vector4f var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.get(var1, this.addressOf(var3) + (long)var2);
      }

      public final void get(Vector4i var1, int var2, IntBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.get(var1, this.addressOf(var3) + (long)(var2 << 2));
      }

      public final void get(Vector4i var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.get(var1, this.addressOf(var3) + (long)var2);
      }

      public final void get(Vector3f var1, int var2, FloatBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.get(var1, this.addressOf(var3) + (long)(var2 << 2));
      }

      public final void get(Vector3f var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.get(var1, this.addressOf(var3) + (long)var2);
      }

      public final void get(Vector3d var1, int var2, DoubleBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.get(var1, this.addressOf(var3) + (long)(var2 << 3));
      }

      public final void get(Vector3d var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.get(var1, this.addressOf(var3) + (long)var2);
      }

      public final void get(Vector3i var1, int var2, IntBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.get(var1, this.addressOf(var3) + (long)(var2 << 2));
      }

      public final void get(Vector3i var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.get(var1, this.addressOf(var3) + (long)var2);
      }

      public final void get(Vector2f var1, int var2, FloatBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.get(var1, this.addressOf(var3) + (long)(var2 << 2));
      }

      public final void get(Vector2f var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.get(var1, this.addressOf(var3) + (long)var2);
      }

      public final void get(Vector2d var1, int var2, DoubleBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.get(var1, this.addressOf(var3) + (long)(var2 << 3));
      }

      public final void get(Vector2d var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.get(var1, this.addressOf(var3) + (long)var2);
      }

      public final void get(Vector2i var1, int var2, IntBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.get(var1, this.addressOf(var3) + (long)(var2 << 2));
      }

      public final void get(Vector2i var1, int var2, ByteBuffer var3) {
         if (Options.DEBUG && !var3.isDirect()) {
            throwNoDirectBufferException();
         }

         this.get(var1, this.addressOf(var3) + (long)var2);
      }

      public final void set(Matrix4f var1, Vector4f var2, Vector4f var3, Vector4f var4, Vector4f var5) {
         UNSAFE.putOrderedLong(var1, Matrix4f_m00, UNSAFE.getLong(var2, Vector4f_x));
         UNSAFE.putOrderedLong(var1, Matrix4f_m00 + 8L, UNSAFE.getLong(var2, Vector4f_x + 8L));
         UNSAFE.putOrderedLong(var1, Matrix4f_m00 + 16L, UNSAFE.getLong(var3, Vector4f_x));
         UNSAFE.putOrderedLong(var1, Matrix4f_m00 + 24L, UNSAFE.getLong(var3, Vector4f_x + 8L));
         UNSAFE.putOrderedLong(var1, Matrix4f_m00 + 32L, UNSAFE.getLong(var4, Vector4f_x));
         UNSAFE.putOrderedLong(var1, Matrix4f_m00 + 40L, UNSAFE.getLong(var4, Vector4f_x + 8L));
         UNSAFE.putOrderedLong(var1, Matrix4f_m00 + 48L, UNSAFE.getLong(var5, Vector4f_x));
         UNSAFE.putOrderedLong(var1, Matrix4f_m00 + 56L, UNSAFE.getLong(var5, Vector4f_x + 8L));
      }

      public final void set(Matrix4x3f var1, Vector3f var2, Vector3f var3, Vector3f var4, Vector3f var5) {
         UNSAFE.putOrderedLong(var1, Matrix4x3f_m00, UNSAFE.getLong(var2, Vector3f_x));
         UNSAFE.putOrderedLong(var1, Matrix4x3f_m00 + 12L, UNSAFE.getLong(var3, Vector3f_x));
         UNSAFE.putOrderedLong(var1, Matrix4x3f_m00 + 24L, UNSAFE.getLong(var4, Vector3f_x));
         UNSAFE.putOrderedLong(var1, Matrix4x3f_m00 + 36L, UNSAFE.getLong(var5, Vector3f_x));
         var1.m02 = var2.z;
         var1.m12 = var3.z;
         var1.m22 = var4.z;
         var1.m32 = var5.z;
      }

      public final void set(Matrix3f var1, Vector3f var2, Vector3f var3, Vector3f var4) {
         UNSAFE.putOrderedLong(var1, Matrix3f_m00, UNSAFE.getLong(var2, Vector3f_x));
         UNSAFE.putOrderedLong(var1, Matrix3f_m00 + 12L, UNSAFE.getLong(var3, Vector3f_x));
         UNSAFE.putOrderedLong(var1, Matrix3f_m00 + 24L, UNSAFE.getLong(var4, Vector3f_x));
         var1.m02 = var2.z;
         var1.m12 = var3.z;
         var1.m22 = var4.z;
      }

      public final void putColumn0(Matrix4f var1, Vector4f var2) {
         UNSAFE.putOrderedLong(var2, Vector4f_x, UNSAFE.getLong(var1, Matrix4f_m00));
         UNSAFE.putOrderedLong(var2, Vector4f_x + 8L, UNSAFE.getLong(var1, Matrix4f_m00 + 8L));
      }

      public final void putColumn1(Matrix4f var1, Vector4f var2) {
         UNSAFE.putOrderedLong(var2, Vector4f_x, UNSAFE.getLong(var1, Matrix4f_m00 + 16L));
         UNSAFE.putOrderedLong(var2, Vector4f_x + 8L, UNSAFE.getLong(var1, Matrix4f_m00 + 24L));
      }

      public final void putColumn2(Matrix4f var1, Vector4f var2) {
         UNSAFE.putOrderedLong(var2, Vector4f_x, UNSAFE.getLong(var1, Matrix4f_m00 + 32L));
         UNSAFE.putOrderedLong(var2, Vector4f_x + 8L, UNSAFE.getLong(var1, Matrix4f_m00 + 40L));
      }

      public final void putColumn3(Matrix4f var1, Vector4f var2) {
         UNSAFE.putOrderedLong(var2, Vector4f_x, UNSAFE.getLong(var1, Matrix4f_m00 + 48L));
         UNSAFE.putOrderedLong(var2, Vector4f_x + 8L, UNSAFE.getLong(var1, Matrix4f_m00 + 56L));
      }

      public final void getColumn0(Matrix4f var1, Vector4f var2) {
         UNSAFE.putOrderedLong(var1, Matrix4f_m00, UNSAFE.getLong(var2, Vector4f_x));
         UNSAFE.putOrderedLong(var1, Matrix4f_m00 + 8L, UNSAFE.getLong(var2, Vector4f_x + 8L));
      }

      public final void getColumn1(Matrix4f var1, Vector4f var2) {
         UNSAFE.putOrderedLong(var1, Matrix4f_m00 + 16L, UNSAFE.getLong(var2, Vector4f_x));
         UNSAFE.putOrderedLong(var1, Matrix4f_m00 + 24L, UNSAFE.getLong(var2, Vector4f_x + 8L));
      }

      public final void getColumn2(Matrix4f var1, Vector4f var2) {
         UNSAFE.putOrderedLong(var1, Matrix4f_m00 + 32L, UNSAFE.getLong(var2, Vector4f_x));
         UNSAFE.putOrderedLong(var1, Matrix4f_m00 + 40L, UNSAFE.getLong(var2, Vector4f_x + 8L));
      }

      public final void getColumn3(Matrix4f var1, Vector4f var2) {
         UNSAFE.putOrderedLong(var1, Matrix4f_m00 + 48L, UNSAFE.getLong(var2, Vector4f_x));
         UNSAFE.putOrderedLong(var1, Matrix4f_m00 + 56L, UNSAFE.getLong(var2, Vector4f_x + 8L));
      }

      public final void broadcast(float var1, Vector4f var2) {
         int var3 = Float.floatToRawIntBits(var1);
         long var4 = (long)var3 & 4294967295L;
         long var6 = var4 | var4 << 32;
         UNSAFE.putOrderedLong(var2, Vector4f_x, var6);
         UNSAFE.putOrderedLong(var2, Vector4f_x + 8L, var6);
      }

      public final void broadcast(int var1, Vector4i var2) {
         long var3 = (long)var1 & 4294967295L;
         long var5 = var3 | var3 << 32;
         UNSAFE.putOrderedLong(var2, Vector4i_x, var5);
         UNSAFE.putOrderedLong(var2, Vector4i_x + 8L, var5);
      }

      static {
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

      private void put0(Matrix4f var1, FloatBuffer var2) {
         var2.put(0, var1.m00);
         var2.put(1, var1.m01);
         var2.put(2, var1.m02);
         var2.put(3, var1.m03);
         var2.put(4, var1.m10);
         var2.put(5, var1.m11);
         var2.put(6, var1.m12);
         var2.put(7, var1.m13);
         var2.put(8, var1.m20);
         var2.put(9, var1.m21);
         var2.put(10, var1.m22);
         var2.put(11, var1.m23);
         var2.put(12, var1.m30);
         var2.put(13, var1.m31);
         var2.put(14, var1.m32);
         var2.put(15, var1.m33);
      }

      private void putN(Matrix4f var1, int var2, FloatBuffer var3) {
         var3.put(var2, var1.m00);
         var3.put(var2 + 1, var1.m01);
         var3.put(var2 + 2, var1.m02);
         var3.put(var2 + 3, var1.m03);
         var3.put(var2 + 4, var1.m10);
         var3.put(var2 + 5, var1.m11);
         var3.put(var2 + 6, var1.m12);
         var3.put(var2 + 7, var1.m13);
         var3.put(var2 + 8, var1.m20);
         var3.put(var2 + 9, var1.m21);
         var3.put(var2 + 10, var1.m22);
         var3.put(var2 + 11, var1.m23);
         var3.put(var2 + 12, var1.m30);
         var3.put(var2 + 13, var1.m31);
         var3.put(var2 + 14, var1.m32);
         var3.put(var2 + 15, var1.m33);
      }

      public void put(Matrix4f var1, int var2, FloatBuffer var3) {
         if (var2 == 0) {
            this.put0(var1, var3);
         } else {
            this.putN(var1, var2, var3);
         }

      }

      private void put0(Matrix4f var1, ByteBuffer var2) {
         var2.putFloat(0, var1.m00);
         var2.putFloat(4, var1.m01);
         var2.putFloat(8, var1.m02);
         var2.putFloat(12, var1.m03);
         var2.putFloat(16, var1.m10);
         var2.putFloat(20, var1.m11);
         var2.putFloat(24, var1.m12);
         var2.putFloat(28, var1.m13);
         var2.putFloat(32, var1.m20);
         var2.putFloat(36, var1.m21);
         var2.putFloat(40, var1.m22);
         var2.putFloat(44, var1.m23);
         var2.putFloat(48, var1.m30);
         var2.putFloat(52, var1.m31);
         var2.putFloat(56, var1.m32);
         var2.putFloat(60, var1.m33);
      }

      private final void putN(Matrix4f var1, int var2, ByteBuffer var3) {
         var3.putFloat(var2, var1.m00);
         var3.putFloat(var2 + 4, var1.m01);
         var3.putFloat(var2 + 8, var1.m02);
         var3.putFloat(var2 + 12, var1.m03);
         var3.putFloat(var2 + 16, var1.m10);
         var3.putFloat(var2 + 20, var1.m11);
         var3.putFloat(var2 + 24, var1.m12);
         var3.putFloat(var2 + 28, var1.m13);
         var3.putFloat(var2 + 32, var1.m20);
         var3.putFloat(var2 + 36, var1.m21);
         var3.putFloat(var2 + 40, var1.m22);
         var3.putFloat(var2 + 44, var1.m23);
         var3.putFloat(var2 + 48, var1.m30);
         var3.putFloat(var2 + 52, var1.m31);
         var3.putFloat(var2 + 56, var1.m32);
         var3.putFloat(var2 + 60, var1.m33);
      }

      public void put(Matrix4f var1, int var2, ByteBuffer var3) {
         if (var2 == 0) {
            this.put0(var1, var3);
         } else {
            this.putN(var1, var2, var3);
         }

      }

      private void put0(Matrix4x3f var1, FloatBuffer var2) {
         var2.put(0, var1.m00);
         var2.put(1, var1.m01);
         var2.put(2, var1.m02);
         var2.put(3, var1.m10);
         var2.put(4, var1.m11);
         var2.put(5, var1.m12);
         var2.put(6, var1.m20);
         var2.put(7, var1.m21);
         var2.put(8, var1.m22);
         var2.put(9, var1.m30);
         var2.put(10, var1.m31);
         var2.put(11, var1.m32);
      }

      private void putN(Matrix4x3f var1, int var2, FloatBuffer var3) {
         var3.put(var2, var1.m00);
         var3.put(var2 + 1, var1.m01);
         var3.put(var2 + 2, var1.m02);
         var3.put(var2 + 3, var1.m10);
         var3.put(var2 + 4, var1.m11);
         var3.put(var2 + 5, var1.m12);
         var3.put(var2 + 6, var1.m20);
         var3.put(var2 + 7, var1.m21);
         var3.put(var2 + 8, var1.m22);
         var3.put(var2 + 9, var1.m30);
         var3.put(var2 + 10, var1.m31);
         var3.put(var2 + 11, var1.m32);
      }

      public void put(Matrix4x3f var1, int var2, FloatBuffer var3) {
         if (var2 == 0) {
            this.put0(var1, var3);
         } else {
            this.putN(var1, var2, var3);
         }

      }

      private void put0(Matrix4x3f var1, ByteBuffer var2) {
         var2.putFloat(0, var1.m00);
         var2.putFloat(4, var1.m01);
         var2.putFloat(8, var1.m02);
         var2.putFloat(12, var1.m10);
         var2.putFloat(16, var1.m11);
         var2.putFloat(20, var1.m12);
         var2.putFloat(24, var1.m20);
         var2.putFloat(28, var1.m21);
         var2.putFloat(32, var1.m22);
         var2.putFloat(36, var1.m30);
         var2.putFloat(40, var1.m31);
         var2.putFloat(44, var1.m32);
      }

      private void putN(Matrix4x3f var1, int var2, ByteBuffer var3) {
         var3.putFloat(var2, var1.m00);
         var3.putFloat(var2 + 4, var1.m01);
         var3.putFloat(var2 + 8, var1.m02);
         var3.putFloat(var2 + 12, var1.m10);
         var3.putFloat(var2 + 16, var1.m11);
         var3.putFloat(var2 + 20, var1.m12);
         var3.putFloat(var2 + 24, var1.m20);
         var3.putFloat(var2 + 28, var1.m21);
         var3.putFloat(var2 + 32, var1.m22);
         var3.putFloat(var2 + 36, var1.m30);
         var3.putFloat(var2 + 40, var1.m31);
         var3.putFloat(var2 + 44, var1.m32);
      }

      public void put(Matrix4x3f var1, int var2, ByteBuffer var3) {
         if (var2 == 0) {
            this.put0(var1, var3);
         } else {
            this.putN(var1, var2, var3);
         }

      }

      public final void put4x4(Matrix4x3f var1, int var2, FloatBuffer var3) {
         var3.put(var2, var1.m00);
         var3.put(var2 + 1, var1.m01);
         var3.put(var2 + 2, var1.m02);
         var3.put(var2 + 3, 0.0F);
         var3.put(var2 + 4, var1.m10);
         var3.put(var2 + 5, var1.m11);
         var3.put(var2 + 6, var1.m12);
         var3.put(var2 + 7, 0.0F);
         var3.put(var2 + 8, var1.m20);
         var3.put(var2 + 9, var1.m21);
         var3.put(var2 + 10, var1.m22);
         var3.put(var2 + 11, 0.0F);
         var3.put(var2 + 12, var1.m30);
         var3.put(var2 + 13, var1.m31);
         var3.put(var2 + 14, var1.m32);
         var3.put(var2 + 15, 1.0F);
      }

      public final void put4x4(Matrix4x3f var1, int var2, ByteBuffer var3) {
         var3.putFloat(var2, var1.m00);
         var3.putFloat(var2 + 4, var1.m01);
         var3.putFloat(var2 + 8, var1.m02);
         var3.putFloat(var2 + 12, 0.0F);
         var3.putFloat(var2 + 16, var1.m10);
         var3.putFloat(var2 + 20, var1.m11);
         var3.putFloat(var2 + 24, var1.m12);
         var3.putFloat(var2 + 28, 0.0F);
         var3.putFloat(var2 + 32, var1.m20);
         var3.putFloat(var2 + 36, var1.m21);
         var3.putFloat(var2 + 40, var1.m22);
         var3.putFloat(var2 + 44, 0.0F);
         var3.putFloat(var2 + 48, var1.m30);
         var3.putFloat(var2 + 52, var1.m31);
         var3.putFloat(var2 + 56, var1.m32);
         var3.putFloat(var2 + 60, 1.0F);
      }

      public final void put4x4(Matrix4x3d var1, int var2, DoubleBuffer var3) {
         var3.put(var2, var1.m00);
         var3.put(var2 + 1, var1.m01);
         var3.put(var2 + 2, var1.m02);
         var3.put(var2 + 3, 0.0D);
         var3.put(var2 + 4, var1.m10);
         var3.put(var2 + 5, var1.m11);
         var3.put(var2 + 6, var1.m12);
         var3.put(var2 + 7, 0.0D);
         var3.put(var2 + 8, var1.m20);
         var3.put(var2 + 9, var1.m21);
         var3.put(var2 + 10, var1.m22);
         var3.put(var2 + 11, 0.0D);
         var3.put(var2 + 12, var1.m30);
         var3.put(var2 + 13, var1.m31);
         var3.put(var2 + 14, var1.m32);
         var3.put(var2 + 15, 1.0D);
      }

      public final void put4x4(Matrix4x3d var1, int var2, ByteBuffer var3) {
         var3.putDouble(var2, var1.m00);
         var3.putDouble(var2 + 4, var1.m01);
         var3.putDouble(var2 + 8, var1.m02);
         var3.putDouble(var2 + 12, 0.0D);
         var3.putDouble(var2 + 16, var1.m10);
         var3.putDouble(var2 + 20, var1.m11);
         var3.putDouble(var2 + 24, var1.m12);
         var3.putDouble(var2 + 28, 0.0D);
         var3.putDouble(var2 + 32, var1.m20);
         var3.putDouble(var2 + 36, var1.m21);
         var3.putDouble(var2 + 40, var1.m22);
         var3.putDouble(var2 + 44, 0.0D);
         var3.putDouble(var2 + 48, var1.m30);
         var3.putDouble(var2 + 52, var1.m31);
         var3.putDouble(var2 + 56, var1.m32);
         var3.putDouble(var2 + 60, 1.0D);
      }

      public final void putTransposed(Matrix4f var1, int var2, FloatBuffer var3) {
         var3.put(var2, var1.m00);
         var3.put(var2 + 1, var1.m10);
         var3.put(var2 + 2, var1.m20);
         var3.put(var2 + 3, var1.m30);
         var3.put(var2 + 4, var1.m01);
         var3.put(var2 + 5, var1.m11);
         var3.put(var2 + 6, var1.m21);
         var3.put(var2 + 7, var1.m31);
         var3.put(var2 + 8, var1.m02);
         var3.put(var2 + 9, var1.m12);
         var3.put(var2 + 10, var1.m22);
         var3.put(var2 + 11, var1.m32);
         var3.put(var2 + 12, var1.m03);
         var3.put(var2 + 13, var1.m13);
         var3.put(var2 + 14, var1.m23);
         var3.put(var2 + 15, var1.m33);
      }

      public final void putTransposed(Matrix4f var1, int var2, ByteBuffer var3) {
         var3.putFloat(var2, var1.m00);
         var3.putFloat(var2 + 4, var1.m10);
         var3.putFloat(var2 + 8, var1.m20);
         var3.putFloat(var2 + 12, var1.m30);
         var3.putFloat(var2 + 16, var1.m01);
         var3.putFloat(var2 + 20, var1.m11);
         var3.putFloat(var2 + 24, var1.m21);
         var3.putFloat(var2 + 28, var1.m31);
         var3.putFloat(var2 + 32, var1.m02);
         var3.putFloat(var2 + 36, var1.m12);
         var3.putFloat(var2 + 40, var1.m22);
         var3.putFloat(var2 + 44, var1.m32);
         var3.putFloat(var2 + 48, var1.m03);
         var3.putFloat(var2 + 52, var1.m13);
         var3.putFloat(var2 + 56, var1.m23);
         var3.putFloat(var2 + 60, var1.m33);
      }

      public final void put4x3Transposed(Matrix4f var1, int var2, FloatBuffer var3) {
         var3.put(var2, var1.m00);
         var3.put(var2 + 1, var1.m10);
         var3.put(var2 + 2, var1.m20);
         var3.put(var2 + 3, var1.m30);
         var3.put(var2 + 4, var1.m01);
         var3.put(var2 + 5, var1.m11);
         var3.put(var2 + 6, var1.m21);
         var3.put(var2 + 7, var1.m31);
         var3.put(var2 + 8, var1.m02);
         var3.put(var2 + 9, var1.m12);
         var3.put(var2 + 10, var1.m22);
         var3.put(var2 + 11, var1.m32);
      }

      public final void put4x3Transposed(Matrix4f var1, int var2, ByteBuffer var3) {
         var3.putFloat(var2, var1.m00);
         var3.putFloat(var2 + 4, var1.m10);
         var3.putFloat(var2 + 8, var1.m20);
         var3.putFloat(var2 + 12, var1.m30);
         var3.putFloat(var2 + 16, var1.m01);
         var3.putFloat(var2 + 20, var1.m11);
         var3.putFloat(var2 + 24, var1.m21);
         var3.putFloat(var2 + 28, var1.m31);
         var3.putFloat(var2 + 32, var1.m02);
         var3.putFloat(var2 + 36, var1.m12);
         var3.putFloat(var2 + 40, var1.m22);
         var3.putFloat(var2 + 44, var1.m32);
      }

      public final void putTransposed(Matrix4x3f var1, int var2, FloatBuffer var3) {
         var3.put(var2, var1.m00);
         var3.put(var2 + 1, var1.m10);
         var3.put(var2 + 2, var1.m20);
         var3.put(var2 + 3, var1.m30);
         var3.put(var2 + 4, var1.m01);
         var3.put(var2 + 5, var1.m11);
         var3.put(var2 + 6, var1.m21);
         var3.put(var2 + 7, var1.m31);
         var3.put(var2 + 8, var1.m02);
         var3.put(var2 + 9, var1.m12);
         var3.put(var2 + 10, var1.m22);
         var3.put(var2 + 11, var1.m32);
      }

      public final void putTransposed(Matrix4x3f var1, int var2, ByteBuffer var3) {
         var3.putFloat(var2, var1.m00);
         var3.putFloat(var2 + 4, var1.m10);
         var3.putFloat(var2 + 8, var1.m20);
         var3.putFloat(var2 + 12, var1.m30);
         var3.putFloat(var2 + 16, var1.m01);
         var3.putFloat(var2 + 20, var1.m11);
         var3.putFloat(var2 + 24, var1.m21);
         var3.putFloat(var2 + 28, var1.m31);
         var3.putFloat(var2 + 32, var1.m02);
         var3.putFloat(var2 + 36, var1.m12);
         var3.putFloat(var2 + 40, var1.m22);
         var3.putFloat(var2 + 44, var1.m32);
      }

      public final void putTransposed(Matrix3f var1, int var2, FloatBuffer var3) {
         var3.put(var2, var1.m00);
         var3.put(var2 + 1, var1.m10);
         var3.put(var2 + 2, var1.m20);
         var3.put(var2 + 3, var1.m01);
         var3.put(var2 + 4, var1.m11);
         var3.put(var2 + 5, var1.m21);
         var3.put(var2 + 6, var1.m02);
         var3.put(var2 + 7, var1.m12);
         var3.put(var2 + 8, var1.m22);
      }

      public final void putTransposed(Matrix3f var1, int var2, ByteBuffer var3) {
         var3.putFloat(var2, var1.m00);
         var3.putFloat(var2 + 4, var1.m10);
         var3.putFloat(var2 + 8, var1.m20);
         var3.putFloat(var2 + 12, var1.m01);
         var3.putFloat(var2 + 16, var1.m11);
         var3.putFloat(var2 + 20, var1.m21);
         var3.putFloat(var2 + 24, var1.m02);
         var3.putFloat(var2 + 28, var1.m12);
         var3.putFloat(var2 + 32, var1.m22);
      }

      public final void put(Matrix4d var1, int var2, DoubleBuffer var3) {
         var3.put(var2, var1.m00);
         var3.put(var2 + 1, var1.m01);
         var3.put(var2 + 2, var1.m02);
         var3.put(var2 + 3, var1.m03);
         var3.put(var2 + 4, var1.m10);
         var3.put(var2 + 5, var1.m11);
         var3.put(var2 + 6, var1.m12);
         var3.put(var2 + 7, var1.m13);
         var3.put(var2 + 8, var1.m20);
         var3.put(var2 + 9, var1.m21);
         var3.put(var2 + 10, var1.m22);
         var3.put(var2 + 11, var1.m23);
         var3.put(var2 + 12, var1.m30);
         var3.put(var2 + 13, var1.m31);
         var3.put(var2 + 14, var1.m32);
         var3.put(var2 + 15, var1.m33);
      }

      public final void put(Matrix4d var1, int var2, ByteBuffer var3) {
         var3.putDouble(var2, var1.m00);
         var3.putDouble(var2 + 4, var1.m01);
         var3.putDouble(var2 + 8, var1.m02);
         var3.putDouble(var2 + 12, var1.m03);
         var3.putDouble(var2 + 16, var1.m10);
         var3.putDouble(var2 + 20, var1.m11);
         var3.putDouble(var2 + 24, var1.m12);
         var3.putDouble(var2 + 28, var1.m13);
         var3.putDouble(var2 + 32, var1.m20);
         var3.putDouble(var2 + 36, var1.m21);
         var3.putDouble(var2 + 40, var1.m22);
         var3.putDouble(var2 + 44, var1.m23);
         var3.putDouble(var2 + 48, var1.m30);
         var3.putDouble(var2 + 52, var1.m31);
         var3.putDouble(var2 + 56, var1.m32);
         var3.putDouble(var2 + 60, var1.m33);
      }

      public final void put(Matrix4x3d var1, int var2, DoubleBuffer var3) {
         var3.put(var2, var1.m00);
         var3.put(var2 + 1, var1.m01);
         var3.put(var2 + 2, var1.m02);
         var3.put(var2 + 3, var1.m10);
         var3.put(var2 + 4, var1.m11);
         var3.put(var2 + 5, var1.m12);
         var3.put(var2 + 6, var1.m20);
         var3.put(var2 + 7, var1.m21);
         var3.put(var2 + 8, var1.m22);
         var3.put(var2 + 9, var1.m30);
         var3.put(var2 + 10, var1.m31);
         var3.put(var2 + 11, var1.m32);
      }

      public final void put(Matrix4x3d var1, int var2, ByteBuffer var3) {
         var3.putDouble(var2, var1.m00);
         var3.putDouble(var2 + 4, var1.m01);
         var3.putDouble(var2 + 8, var1.m02);
         var3.putDouble(var2 + 12, var1.m10);
         var3.putDouble(var2 + 16, var1.m11);
         var3.putDouble(var2 + 20, var1.m12);
         var3.putDouble(var2 + 24, var1.m20);
         var3.putDouble(var2 + 28, var1.m21);
         var3.putDouble(var2 + 32, var1.m22);
         var3.putDouble(var2 + 36, var1.m30);
         var3.putDouble(var2 + 40, var1.m31);
         var3.putDouble(var2 + 44, var1.m32);
      }

      public final void putf(Matrix4d var1, int var2, FloatBuffer var3) {
         var3.put(var2, (float)var1.m00);
         var3.put(var2 + 1, (float)var1.m01);
         var3.put(var2 + 2, (float)var1.m02);
         var3.put(var2 + 3, (float)var1.m03);
         var3.put(var2 + 4, (float)var1.m10);
         var3.put(var2 + 5, (float)var1.m11);
         var3.put(var2 + 6, (float)var1.m12);
         var3.put(var2 + 7, (float)var1.m13);
         var3.put(var2 + 8, (float)var1.m20);
         var3.put(var2 + 9, (float)var1.m21);
         var3.put(var2 + 10, (float)var1.m22);
         var3.put(var2 + 11, (float)var1.m23);
         var3.put(var2 + 12, (float)var1.m30);
         var3.put(var2 + 13, (float)var1.m31);
         var3.put(var2 + 14, (float)var1.m32);
         var3.put(var2 + 15, (float)var1.m33);
      }

      public final void putf(Matrix4d var1, int var2, ByteBuffer var3) {
         var3.putFloat(var2, (float)var1.m00);
         var3.putFloat(var2 + 4, (float)var1.m01);
         var3.putFloat(var2 + 8, (float)var1.m02);
         var3.putFloat(var2 + 12, (float)var1.m03);
         var3.putFloat(var2 + 16, (float)var1.m10);
         var3.putFloat(var2 + 20, (float)var1.m11);
         var3.putFloat(var2 + 24, (float)var1.m12);
         var3.putFloat(var2 + 28, (float)var1.m13);
         var3.putFloat(var2 + 32, (float)var1.m20);
         var3.putFloat(var2 + 36, (float)var1.m21);
         var3.putFloat(var2 + 40, (float)var1.m22);
         var3.putFloat(var2 + 44, (float)var1.m23);
         var3.putFloat(var2 + 48, (float)var1.m30);
         var3.putFloat(var2 + 52, (float)var1.m31);
         var3.putFloat(var2 + 56, (float)var1.m32);
         var3.putFloat(var2 + 60, (float)var1.m33);
      }

      public final void putf(Matrix4x3d var1, int var2, FloatBuffer var3) {
         var3.put(var2, (float)var1.m00);
         var3.put(var2 + 1, (float)var1.m01);
         var3.put(var2 + 2, (float)var1.m02);
         var3.put(var2 + 3, (float)var1.m10);
         var3.put(var2 + 4, (float)var1.m11);
         var3.put(var2 + 5, (float)var1.m12);
         var3.put(var2 + 6, (float)var1.m20);
         var3.put(var2 + 7, (float)var1.m21);
         var3.put(var2 + 8, (float)var1.m22);
         var3.put(var2 + 9, (float)var1.m30);
         var3.put(var2 + 10, (float)var1.m31);
         var3.put(var2 + 11, (float)var1.m32);
      }

      public final void putf(Matrix4x3d var1, int var2, ByteBuffer var3) {
         var3.putFloat(var2, (float)var1.m00);
         var3.putFloat(var2 + 4, (float)var1.m01);
         var3.putFloat(var2 + 8, (float)var1.m02);
         var3.putFloat(var2 + 12, (float)var1.m10);
         var3.putFloat(var2 + 16, (float)var1.m11);
         var3.putFloat(var2 + 20, (float)var1.m12);
         var3.putFloat(var2 + 24, (float)var1.m20);
         var3.putFloat(var2 + 28, (float)var1.m21);
         var3.putFloat(var2 + 32, (float)var1.m22);
         var3.putFloat(var2 + 36, (float)var1.m30);
         var3.putFloat(var2 + 40, (float)var1.m31);
         var3.putFloat(var2 + 44, (float)var1.m32);
      }

      public final void putTransposed(Matrix4d var1, int var2, DoubleBuffer var3) {
         var3.put(var2, var1.m00);
         var3.put(var2 + 1, var1.m10);
         var3.put(var2 + 2, var1.m20);
         var3.put(var2 + 3, var1.m30);
         var3.put(var2 + 4, var1.m01);
         var3.put(var2 + 5, var1.m11);
         var3.put(var2 + 6, var1.m21);
         var3.put(var2 + 7, var1.m31);
         var3.put(var2 + 8, var1.m02);
         var3.put(var2 + 9, var1.m12);
         var3.put(var2 + 10, var1.m22);
         var3.put(var2 + 11, var1.m32);
         var3.put(var2 + 12, var1.m03);
         var3.put(var2 + 13, var1.m13);
         var3.put(var2 + 14, var1.m23);
         var3.put(var2 + 15, var1.m33);
      }

      public final void putTransposed(Matrix4d var1, int var2, ByteBuffer var3) {
         var3.putDouble(var2, var1.m00);
         var3.putDouble(var2 + 8, var1.m10);
         var3.putDouble(var2 + 16, var1.m20);
         var3.putDouble(var2 + 24, var1.m30);
         var3.putDouble(var2 + 32, var1.m01);
         var3.putDouble(var2 + 40, var1.m11);
         var3.putDouble(var2 + 48, var1.m21);
         var3.putDouble(var2 + 56, var1.m31);
         var3.putDouble(var2 + 64, var1.m02);
         var3.putDouble(var2 + 72, var1.m12);
         var3.putDouble(var2 + 80, var1.m22);
         var3.putDouble(var2 + 88, var1.m32);
         var3.putDouble(var2 + 96, var1.m03);
         var3.putDouble(var2 + 104, var1.m13);
         var3.putDouble(var2 + 112, var1.m23);
         var3.putDouble(var2 + 120, var1.m33);
      }

      public final void put4x3Transposed(Matrix4d var1, int var2, DoubleBuffer var3) {
         var3.put(var2, var1.m00);
         var3.put(var2 + 1, var1.m10);
         var3.put(var2 + 2, var1.m20);
         var3.put(var2 + 3, var1.m30);
         var3.put(var2 + 4, var1.m01);
         var3.put(var2 + 5, var1.m11);
         var3.put(var2 + 6, var1.m21);
         var3.put(var2 + 7, var1.m31);
         var3.put(var2 + 8, var1.m02);
         var3.put(var2 + 9, var1.m12);
         var3.put(var2 + 10, var1.m22);
         var3.put(var2 + 11, var1.m32);
      }

      public final void put4x3Transposed(Matrix4d var1, int var2, ByteBuffer var3) {
         var3.putDouble(var2, var1.m00);
         var3.putDouble(var2 + 8, var1.m10);
         var3.putDouble(var2 + 16, var1.m20);
         var3.putDouble(var2 + 24, var1.m30);
         var3.putDouble(var2 + 32, var1.m01);
         var3.putDouble(var2 + 40, var1.m11);
         var3.putDouble(var2 + 48, var1.m21);
         var3.putDouble(var2 + 56, var1.m31);
         var3.putDouble(var2 + 64, var1.m02);
         var3.putDouble(var2 + 72, var1.m12);
         var3.putDouble(var2 + 80, var1.m22);
         var3.putDouble(var2 + 88, var1.m32);
      }

      public final void putTransposed(Matrix4x3d var1, int var2, DoubleBuffer var3) {
         var3.put(var2, var1.m00);
         var3.put(var2 + 1, var1.m10);
         var3.put(var2 + 2, var1.m20);
         var3.put(var2 + 3, var1.m30);
         var3.put(var2 + 4, var1.m01);
         var3.put(var2 + 5, var1.m11);
         var3.put(var2 + 6, var1.m21);
         var3.put(var2 + 7, var1.m31);
         var3.put(var2 + 8, var1.m02);
         var3.put(var2 + 9, var1.m12);
         var3.put(var2 + 10, var1.m22);
         var3.put(var2 + 11, var1.m32);
      }

      public final void putTransposed(Matrix4x3d var1, int var2, ByteBuffer var3) {
         var3.putDouble(var2, var1.m00);
         var3.putDouble(var2 + 4, var1.m10);
         var3.putDouble(var2 + 8, var1.m20);
         var3.putDouble(var2 + 12, var1.m30);
         var3.putDouble(var2 + 16, var1.m01);
         var3.putDouble(var2 + 20, var1.m11);
         var3.putDouble(var2 + 24, var1.m21);
         var3.putDouble(var2 + 28, var1.m31);
         var3.putDouble(var2 + 32, var1.m02);
         var3.putDouble(var2 + 36, var1.m12);
         var3.putDouble(var2 + 40, var1.m22);
         var3.putDouble(var2 + 44, var1.m32);
      }

      public final void putfTransposed(Matrix4x3d var1, int var2, FloatBuffer var3) {
         var3.put(var2, (float)var1.m00);
         var3.put(var2 + 1, (float)var1.m10);
         var3.put(var2 + 2, (float)var1.m20);
         var3.put(var2 + 3, (float)var1.m30);
         var3.put(var2 + 4, (float)var1.m01);
         var3.put(var2 + 5, (float)var1.m11);
         var3.put(var2 + 6, (float)var1.m21);
         var3.put(var2 + 7, (float)var1.m31);
         var3.put(var2 + 8, (float)var1.m02);
         var3.put(var2 + 9, (float)var1.m12);
         var3.put(var2 + 10, (float)var1.m22);
         var3.put(var2 + 11, (float)var1.m32);
      }

      public final void putfTransposed(Matrix4x3d var1, int var2, ByteBuffer var3) {
         var3.putFloat(var2, (float)var1.m00);
         var3.putFloat(var2 + 4, (float)var1.m10);
         var3.putFloat(var2 + 8, (float)var1.m20);
         var3.putFloat(var2 + 12, (float)var1.m30);
         var3.putFloat(var2 + 16, (float)var1.m01);
         var3.putFloat(var2 + 20, (float)var1.m11);
         var3.putFloat(var2 + 24, (float)var1.m21);
         var3.putFloat(var2 + 28, (float)var1.m31);
         var3.putFloat(var2 + 32, (float)var1.m02);
         var3.putFloat(var2 + 36, (float)var1.m12);
         var3.putFloat(var2 + 40, (float)var1.m22);
         var3.putFloat(var2 + 44, (float)var1.m32);
      }

      public final void putfTransposed(Matrix4d var1, int var2, FloatBuffer var3) {
         var3.put(var2, (float)var1.m00);
         var3.put(var2 + 1, (float)var1.m10);
         var3.put(var2 + 2, (float)var1.m20);
         var3.put(var2 + 3, (float)var1.m30);
         var3.put(var2 + 4, (float)var1.m01);
         var3.put(var2 + 5, (float)var1.m11);
         var3.put(var2 + 6, (float)var1.m21);
         var3.put(var2 + 7, (float)var1.m31);
         var3.put(var2 + 8, (float)var1.m02);
         var3.put(var2 + 9, (float)var1.m12);
         var3.put(var2 + 10, (float)var1.m22);
         var3.put(var2 + 11, (float)var1.m32);
         var3.put(var2 + 12, (float)var1.m03);
         var3.put(var2 + 13, (float)var1.m13);
         var3.put(var2 + 14, (float)var1.m23);
         var3.put(var2 + 15, (float)var1.m33);
      }

      public final void putfTransposed(Matrix4d var1, int var2, ByteBuffer var3) {
         var3.putFloat(var2, (float)var1.m00);
         var3.putFloat(var2 + 4, (float)var1.m10);
         var3.putFloat(var2 + 8, (float)var1.m20);
         var3.putFloat(var2 + 12, (float)var1.m30);
         var3.putFloat(var2 + 16, (float)var1.m01);
         var3.putFloat(var2 + 20, (float)var1.m11);
         var3.putFloat(var2 + 24, (float)var1.m21);
         var3.putFloat(var2 + 28, (float)var1.m31);
         var3.putFloat(var2 + 32, (float)var1.m02);
         var3.putFloat(var2 + 36, (float)var1.m12);
         var3.putFloat(var2 + 40, (float)var1.m22);
         var3.putFloat(var2 + 44, (float)var1.m32);
         var3.putFloat(var2 + 48, (float)var1.m03);
         var3.putFloat(var2 + 52, (float)var1.m13);
         var3.putFloat(var2 + 56, (float)var1.m23);
         var3.putFloat(var2 + 60, (float)var1.m33);
      }

      public final void put(Matrix3f var1, int var2, FloatBuffer var3) {
         var3.put(var2, var1.m00);
         var3.put(var2 + 1, var1.m01);
         var3.put(var2 + 2, var1.m02);
         var3.put(var2 + 3, var1.m10);
         var3.put(var2 + 4, var1.m11);
         var3.put(var2 + 5, var1.m12);
         var3.put(var2 + 6, var1.m20);
         var3.put(var2 + 7, var1.m21);
         var3.put(var2 + 8, var1.m22);
      }

      public final void put(Matrix3f var1, int var2, ByteBuffer var3) {
         var3.putFloat(var2, var1.m00);
         var3.putFloat(var2 + 4, var1.m01);
         var3.putFloat(var2 + 8, var1.m02);
         var3.putFloat(var2 + 12, var1.m10);
         var3.putFloat(var2 + 16, var1.m11);
         var3.putFloat(var2 + 20, var1.m12);
         var3.putFloat(var2 + 24, var1.m20);
         var3.putFloat(var2 + 28, var1.m21);
         var3.putFloat(var2 + 32, var1.m22);
      }

      public final void put(Matrix3d var1, int var2, DoubleBuffer var3) {
         var3.put(var2, var1.m00);
         var3.put(var2 + 1, var1.m01);
         var3.put(var2 + 2, var1.m02);
         var3.put(var2 + 3, var1.m10);
         var3.put(var2 + 4, var1.m11);
         var3.put(var2 + 5, var1.m12);
         var3.put(var2 + 6, var1.m20);
         var3.put(var2 + 7, var1.m21);
         var3.put(var2 + 8, var1.m22);
      }

      public final void put(Matrix3d var1, int var2, ByteBuffer var3) {
         var3.putDouble(var2, var1.m00);
         var3.putDouble(var2 + 8, var1.m01);
         var3.putDouble(var2 + 16, var1.m02);
         var3.putDouble(var2 + 24, var1.m10);
         var3.putDouble(var2 + 32, var1.m11);
         var3.putDouble(var2 + 40, var1.m12);
         var3.putDouble(var2 + 48, var1.m20);
         var3.putDouble(var2 + 56, var1.m21);
         var3.putDouble(var2 + 64, var1.m22);
      }

      public final void putf(Matrix3d var1, int var2, FloatBuffer var3) {
         var3.put(var2, (float)var1.m00);
         var3.put(var2 + 1, (float)var1.m01);
         var3.put(var2 + 2, (float)var1.m02);
         var3.put(var2 + 3, (float)var1.m10);
         var3.put(var2 + 4, (float)var1.m11);
         var3.put(var2 + 5, (float)var1.m12);
         var3.put(var2 + 6, (float)var1.m20);
         var3.put(var2 + 7, (float)var1.m21);
         var3.put(var2 + 8, (float)var1.m22);
      }

      public final void putf(Matrix3d var1, int var2, ByteBuffer var3) {
         var3.putFloat(var2, (float)var1.m00);
         var3.putFloat(var2 + 4, (float)var1.m01);
         var3.putFloat(var2 + 8, (float)var1.m02);
         var3.putFloat(var2 + 12, (float)var1.m10);
         var3.putFloat(var2 + 16, (float)var1.m11);
         var3.putFloat(var2 + 20, (float)var1.m12);
         var3.putFloat(var2 + 24, (float)var1.m20);
         var3.putFloat(var2 + 28, (float)var1.m21);
         var3.putFloat(var2 + 32, (float)var1.m22);
      }

      public final void put(Vector4d var1, int var2, DoubleBuffer var3) {
         var3.put(var2, var1.x);
         var3.put(var2 + 1, var1.y);
         var3.put(var2 + 2, var1.z);
         var3.put(var2 + 3, var1.w);
      }

      public final void put(Vector4d var1, int var2, ByteBuffer var3) {
         var3.putDouble(var2, var1.x);
         var3.putDouble(var2 + 8, var1.y);
         var3.putDouble(var2 + 16, var1.z);
         var3.putDouble(var2 + 24, var1.w);
      }

      public final void put(Vector4f var1, int var2, FloatBuffer var3) {
         var3.put(var2, var1.x);
         var3.put(var2 + 1, var1.y);
         var3.put(var2 + 2, var1.z);
         var3.put(var2 + 3, var1.w);
      }

      public final void put(Vector4f var1, int var2, ByteBuffer var3) {
         var3.putFloat(var2, var1.x);
         var3.putFloat(var2 + 4, var1.y);
         var3.putFloat(var2 + 8, var1.z);
         var3.putFloat(var2 + 12, var1.w);
      }

      public final void put(Vector4i var1, int var2, IntBuffer var3) {
         var3.put(var2, var1.x);
         var3.put(var2 + 1, var1.y);
         var3.put(var2 + 2, var1.z);
         var3.put(var2 + 3, var1.w);
      }

      public final void put(Vector4i var1, int var2, ByteBuffer var3) {
         var3.putInt(var2, var1.x);
         var3.putInt(var2 + 4, var1.y);
         var3.putInt(var2 + 8, var1.z);
         var3.putInt(var2 + 12, var1.w);
      }

      public final void put(Vector3f var1, int var2, FloatBuffer var3) {
         var3.put(var2, var1.x);
         var3.put(var2 + 1, var1.y);
         var3.put(var2 + 2, var1.z);
      }

      public final void put(Vector3f var1, int var2, ByteBuffer var3) {
         var3.putFloat(var2, var1.x);
         var3.putFloat(var2 + 4, var1.y);
         var3.putFloat(var2 + 8, var1.z);
      }

      public final void put(Vector3d var1, int var2, DoubleBuffer var3) {
         var3.put(var2, var1.x);
         var3.put(var2 + 1, var1.y);
         var3.put(var2 + 2, var1.z);
      }

      public final void put(Vector3d var1, int var2, ByteBuffer var3) {
         var3.putDouble(var2, var1.x);
         var3.putDouble(var2 + 8, var1.y);
         var3.putDouble(var2 + 16, var1.z);
      }

      public final void put(Vector3i var1, int var2, IntBuffer var3) {
         var3.put(var2, var1.x);
         var3.put(var2 + 1, var1.y);
         var3.put(var2 + 2, var1.z);
      }

      public final void put(Vector3i var1, int var2, ByteBuffer var3) {
         var3.putInt(var2, var1.x);
         var3.putInt(var2 + 4, var1.y);
         var3.putInt(var2 + 8, var1.z);
      }

      public final void put(Vector2f var1, int var2, FloatBuffer var3) {
         var3.put(var2, var1.x);
         var3.put(var2 + 1, var1.y);
      }

      public final void put(Vector2f var1, int var2, ByteBuffer var3) {
         var3.putFloat(var2, var1.x);
         var3.putFloat(var2 + 4, var1.y);
      }

      public final void put(Vector2d var1, int var2, DoubleBuffer var3) {
         var3.put(var2, var1.x);
         var3.put(var2 + 1, var1.y);
      }

      public final void put(Vector2d var1, int var2, ByteBuffer var3) {
         var3.putDouble(var2, var1.x);
         var3.putDouble(var2 + 8, var1.y);
      }

      public final void put(Vector2i var1, int var2, IntBuffer var3) {
         var3.put(var2, var1.x);
         var3.put(var2 + 1, var1.y);
      }

      public final void put(Vector2i var1, int var2, ByteBuffer var3) {
         var3.putInt(var2, var1.x);
         var3.putInt(var2 + 4, var1.y);
      }

      public final void get(Matrix4f var1, int var2, FloatBuffer var3) {
         var1.m00 = var3.get(var2);
         var1.m01 = var3.get(var2 + 1);
         var1.m02 = var3.get(var2 + 2);
         var1.m03 = var3.get(var2 + 3);
         var1.m10 = var3.get(var2 + 4);
         var1.m11 = var3.get(var2 + 5);
         var1.m12 = var3.get(var2 + 6);
         var1.m13 = var3.get(var2 + 7);
         var1.m20 = var3.get(var2 + 8);
         var1.m21 = var3.get(var2 + 9);
         var1.m22 = var3.get(var2 + 10);
         var1.m23 = var3.get(var2 + 11);
         var1.m30 = var3.get(var2 + 12);
         var1.m31 = var3.get(var2 + 13);
         var1.m32 = var3.get(var2 + 14);
         var1.m33 = var3.get(var2 + 15);
      }

      public final void get(Matrix4f var1, int var2, ByteBuffer var3) {
         var1.m00 = var3.getFloat(var2);
         var1.m01 = var3.getFloat(var2 + 4);
         var1.m02 = var3.getFloat(var2 + 8);
         var1.m03 = var3.getFloat(var2 + 12);
         var1.m10 = var3.getFloat(var2 + 16);
         var1.m11 = var3.getFloat(var2 + 20);
         var1.m12 = var3.getFloat(var2 + 24);
         var1.m13 = var3.getFloat(var2 + 28);
         var1.m20 = var3.getFloat(var2 + 32);
         var1.m21 = var3.getFloat(var2 + 36);
         var1.m22 = var3.getFloat(var2 + 40);
         var1.m23 = var3.getFloat(var2 + 44);
         var1.m30 = var3.getFloat(var2 + 48);
         var1.m31 = var3.getFloat(var2 + 52);
         var1.m32 = var3.getFloat(var2 + 56);
         var1.m33 = var3.getFloat(var2 + 60);
      }

      public final void get(Matrix4x3f var1, int var2, FloatBuffer var3) {
         var1.m00 = var3.get(var2);
         var1.m01 = var3.get(var2 + 1);
         var1.m02 = var3.get(var2 + 2);
         var1.m10 = var3.get(var2 + 3);
         var1.m11 = var3.get(var2 + 4);
         var1.m12 = var3.get(var2 + 5);
         var1.m20 = var3.get(var2 + 6);
         var1.m21 = var3.get(var2 + 7);
         var1.m22 = var3.get(var2 + 8);
         var1.m30 = var3.get(var2 + 9);
         var1.m31 = var3.get(var2 + 10);
         var1.m32 = var3.get(var2 + 11);
      }

      public final void get(Matrix4x3f var1, int var2, ByteBuffer var3) {
         var1.m00 = var3.getFloat(var2);
         var1.m01 = var3.getFloat(var2 + 4);
         var1.m02 = var3.getFloat(var2 + 8);
         var1.m10 = var3.getFloat(var2 + 12);
         var1.m11 = var3.getFloat(var2 + 16);
         var1.m12 = var3.getFloat(var2 + 20);
         var1.m20 = var3.getFloat(var2 + 24);
         var1.m21 = var3.getFloat(var2 + 28);
         var1.m22 = var3.getFloat(var2 + 32);
         var1.m30 = var3.getFloat(var2 + 36);
         var1.m31 = var3.getFloat(var2 + 40);
         var1.m32 = var3.getFloat(var2 + 44);
      }

      public final void get(Matrix4d var1, int var2, DoubleBuffer var3) {
         var1.m00 = var3.get(var2);
         var1.m01 = var3.get(var2 + 1);
         var1.m02 = var3.get(var2 + 2);
         var1.m03 = var3.get(var2 + 3);
         var1.m10 = var3.get(var2 + 4);
         var1.m11 = var3.get(var2 + 5);
         var1.m12 = var3.get(var2 + 6);
         var1.m13 = var3.get(var2 + 7);
         var1.m20 = var3.get(var2 + 8);
         var1.m21 = var3.get(var2 + 9);
         var1.m22 = var3.get(var2 + 10);
         var1.m23 = var3.get(var2 + 11);
         var1.m30 = var3.get(var2 + 12);
         var1.m31 = var3.get(var2 + 13);
         var1.m32 = var3.get(var2 + 14);
         var1.m33 = var3.get(var2 + 15);
      }

      public final void get(Matrix4d var1, int var2, ByteBuffer var3) {
         var1.m00 = var3.getDouble(var2);
         var1.m01 = var3.getDouble(var2 + 8);
         var1.m02 = var3.getDouble(var2 + 16);
         var1.m03 = var3.getDouble(var2 + 24);
         var1.m10 = var3.getDouble(var2 + 32);
         var1.m11 = var3.getDouble(var2 + 40);
         var1.m12 = var3.getDouble(var2 + 48);
         var1.m13 = var3.getDouble(var2 + 56);
         var1.m20 = var3.getDouble(var2 + 64);
         var1.m21 = var3.getDouble(var2 + 72);
         var1.m22 = var3.getDouble(var2 + 80);
         var1.m23 = var3.getDouble(var2 + 88);
         var1.m30 = var3.getDouble(var2 + 96);
         var1.m31 = var3.getDouble(var2 + 104);
         var1.m32 = var3.getDouble(var2 + 112);
         var1.m33 = var3.getDouble(var2 + 120);
      }

      public final void get(Matrix4x3d var1, int var2, DoubleBuffer var3) {
         var1.m00 = var3.get(var2);
         var1.m01 = var3.get(var2 + 1);
         var1.m02 = var3.get(var2 + 2);
         var1.m10 = var3.get(var2 + 3);
         var1.m11 = var3.get(var2 + 4);
         var1.m12 = var3.get(var2 + 5);
         var1.m20 = var3.get(var2 + 6);
         var1.m21 = var3.get(var2 + 7);
         var1.m22 = var3.get(var2 + 8);
         var1.m30 = var3.get(var2 + 9);
         var1.m31 = var3.get(var2 + 10);
         var1.m32 = var3.get(var2 + 11);
      }

      public final void get(Matrix4x3d var1, int var2, ByteBuffer var3) {
         var1.m00 = var3.getDouble(var2);
         var1.m01 = var3.getDouble(var2 + 8);
         var1.m02 = var3.getDouble(var2 + 16);
         var1.m10 = var3.getDouble(var2 + 24);
         var1.m11 = var3.getDouble(var2 + 32);
         var1.m12 = var3.getDouble(var2 + 40);
         var1.m20 = var3.getDouble(var2 + 48);
         var1.m21 = var3.getDouble(var2 + 56);
         var1.m22 = var3.getDouble(var2 + 64);
         var1.m30 = var3.getDouble(var2 + 72);
         var1.m31 = var3.getDouble(var2 + 80);
         var1.m32 = var3.getDouble(var2 + 88);
      }

      public final void getf(Matrix4d var1, int var2, FloatBuffer var3) {
         var1.m00 = (double)var3.get(var2);
         var1.m01 = (double)var3.get(var2 + 1);
         var1.m02 = (double)var3.get(var2 + 2);
         var1.m03 = (double)var3.get(var2 + 3);
         var1.m10 = (double)var3.get(var2 + 4);
         var1.m11 = (double)var3.get(var2 + 5);
         var1.m12 = (double)var3.get(var2 + 6);
         var1.m13 = (double)var3.get(var2 + 7);
         var1.m20 = (double)var3.get(var2 + 8);
         var1.m21 = (double)var3.get(var2 + 9);
         var1.m22 = (double)var3.get(var2 + 10);
         var1.m23 = (double)var3.get(var2 + 11);
         var1.m30 = (double)var3.get(var2 + 12);
         var1.m31 = (double)var3.get(var2 + 13);
         var1.m32 = (double)var3.get(var2 + 14);
         var1.m33 = (double)var3.get(var2 + 15);
      }

      public final void getf(Matrix4d var1, int var2, ByteBuffer var3) {
         var1.m00 = (double)var3.getFloat(var2);
         var1.m01 = (double)var3.getFloat(var2 + 4);
         var1.m02 = (double)var3.getFloat(var2 + 8);
         var1.m03 = (double)var3.getFloat(var2 + 12);
         var1.m10 = (double)var3.getFloat(var2 + 16);
         var1.m11 = (double)var3.getFloat(var2 + 20);
         var1.m12 = (double)var3.getFloat(var2 + 24);
         var1.m13 = (double)var3.getFloat(var2 + 28);
         var1.m20 = (double)var3.getFloat(var2 + 32);
         var1.m21 = (double)var3.getFloat(var2 + 36);
         var1.m22 = (double)var3.getFloat(var2 + 40);
         var1.m23 = (double)var3.getFloat(var2 + 44);
         var1.m30 = (double)var3.getFloat(var2 + 48);
         var1.m31 = (double)var3.getFloat(var2 + 52);
         var1.m32 = (double)var3.getFloat(var2 + 56);
         var1.m33 = (double)var3.getFloat(var2 + 60);
      }

      public final void getf(Matrix4x3d var1, int var2, FloatBuffer var3) {
         var1.m00 = (double)var3.get(var2);
         var1.m01 = (double)var3.get(var2 + 1);
         var1.m02 = (double)var3.get(var2 + 2);
         var1.m10 = (double)var3.get(var2 + 3);
         var1.m11 = (double)var3.get(var2 + 4);
         var1.m12 = (double)var3.get(var2 + 5);
         var1.m20 = (double)var3.get(var2 + 6);
         var1.m21 = (double)var3.get(var2 + 7);
         var1.m22 = (double)var3.get(var2 + 8);
         var1.m30 = (double)var3.get(var2 + 9);
         var1.m31 = (double)var3.get(var2 + 10);
         var1.m32 = (double)var3.get(var2 + 11);
      }

      public final void getf(Matrix4x3d var1, int var2, ByteBuffer var3) {
         var1.m00 = (double)var3.getFloat(var2);
         var1.m01 = (double)var3.getFloat(var2 + 4);
         var1.m02 = (double)var3.getFloat(var2 + 8);
         var1.m10 = (double)var3.getFloat(var2 + 12);
         var1.m11 = (double)var3.getFloat(var2 + 16);
         var1.m12 = (double)var3.getFloat(var2 + 20);
         var1.m20 = (double)var3.getFloat(var2 + 24);
         var1.m21 = (double)var3.getFloat(var2 + 28);
         var1.m22 = (double)var3.getFloat(var2 + 32);
         var1.m30 = (double)var3.getFloat(var2 + 36);
         var1.m31 = (double)var3.getFloat(var2 + 40);
         var1.m32 = (double)var3.getFloat(var2 + 44);
      }

      public final void get(Matrix3f var1, int var2, FloatBuffer var3) {
         var1.m00 = var3.get(var2);
         var1.m01 = var3.get(var2 + 1);
         var1.m02 = var3.get(var2 + 2);
         var1.m10 = var3.get(var2 + 3);
         var1.m11 = var3.get(var2 + 4);
         var1.m12 = var3.get(var2 + 5);
         var1.m20 = var3.get(var2 + 6);
         var1.m21 = var3.get(var2 + 7);
         var1.m22 = var3.get(var2 + 8);
      }

      public final void get(Matrix3f var1, int var2, ByteBuffer var3) {
         var1.m00 = var3.getFloat(var2);
         var1.m01 = var3.getFloat(var2 + 4);
         var1.m02 = var3.getFloat(var2 + 8);
         var1.m10 = var3.getFloat(var2 + 12);
         var1.m11 = var3.getFloat(var2 + 16);
         var1.m12 = var3.getFloat(var2 + 20);
         var1.m20 = var3.getFloat(var2 + 24);
         var1.m21 = var3.getFloat(var2 + 28);
         var1.m22 = var3.getFloat(var2 + 32);
      }

      public final void get(Matrix3d var1, int var2, DoubleBuffer var3) {
         var1.m00 = var3.get(var2);
         var1.m01 = var3.get(var2 + 1);
         var1.m02 = var3.get(var2 + 2);
         var1.m10 = var3.get(var2 + 3);
         var1.m11 = var3.get(var2 + 4);
         var1.m12 = var3.get(var2 + 5);
         var1.m20 = var3.get(var2 + 6);
         var1.m21 = var3.get(var2 + 7);
         var1.m22 = var3.get(var2 + 8);
      }

      public final void get(Matrix3d var1, int var2, ByteBuffer var3) {
         var1.m00 = var3.getDouble(var2);
         var1.m01 = var3.getDouble(var2 + 8);
         var1.m02 = var3.getDouble(var2 + 16);
         var1.m10 = var3.getDouble(var2 + 24);
         var1.m11 = var3.getDouble(var2 + 32);
         var1.m12 = var3.getDouble(var2 + 40);
         var1.m20 = var3.getDouble(var2 + 48);
         var1.m21 = var3.getDouble(var2 + 56);
         var1.m22 = var3.getDouble(var2 + 64);
      }

      public final void getf(Matrix3d var1, int var2, FloatBuffer var3) {
         var1.m00 = (double)var3.get(var2);
         var1.m01 = (double)var3.get(var2 + 1);
         var1.m02 = (double)var3.get(var2 + 2);
         var1.m10 = (double)var3.get(var2 + 3);
         var1.m11 = (double)var3.get(var2 + 4);
         var1.m12 = (double)var3.get(var2 + 5);
         var1.m20 = (double)var3.get(var2 + 6);
         var1.m21 = (double)var3.get(var2 + 7);
         var1.m22 = (double)var3.get(var2 + 8);
      }

      public final void getf(Matrix3d var1, int var2, ByteBuffer var3) {
         var1.m00 = (double)var3.getFloat(var2);
         var1.m01 = (double)var3.getFloat(var2 + 4);
         var1.m02 = (double)var3.getFloat(var2 + 8);
         var1.m10 = (double)var3.getFloat(var2 + 12);
         var1.m11 = (double)var3.getFloat(var2 + 16);
         var1.m12 = (double)var3.getFloat(var2 + 20);
         var1.m20 = (double)var3.getFloat(var2 + 24);
         var1.m21 = (double)var3.getFloat(var2 + 28);
         var1.m22 = (double)var3.getFloat(var2 + 32);
      }

      public final void get(Vector4d var1, int var2, DoubleBuffer var3) {
         var1.x = var3.get(var2);
         var1.y = var3.get(var2 + 1);
         var1.z = var3.get(var2 + 2);
         var1.w = var3.get(var2 + 3);
      }

      public final void get(Vector4d var1, int var2, ByteBuffer var3) {
         var1.x = var3.getDouble(var2);
         var1.y = var3.getDouble(var2 + 8);
         var1.z = var3.getDouble(var2 + 16);
         var1.w = var3.getDouble(var2 + 24);
      }

      public final void get(Vector4f var1, int var2, FloatBuffer var3) {
         var1.x = var3.get(var2);
         var1.y = var3.get(var2 + 1);
         var1.z = var3.get(var2 + 2);
         var1.w = var3.get(var2 + 3);
      }

      public final void get(Vector4f var1, int var2, ByteBuffer var3) {
         var1.x = var3.getFloat(var2);
         var1.y = var3.getFloat(var2 + 4);
         var1.z = var3.getFloat(var2 + 8);
         var1.w = var3.getFloat(var2 + 12);
      }

      public final void get(Vector4i var1, int var2, IntBuffer var3) {
         var1.x = var3.get(var2);
         var1.y = var3.get(var2 + 1);
         var1.z = var3.get(var2 + 2);
         var1.w = var3.get(var2 + 3);
      }

      public final void get(Vector4i var1, int var2, ByteBuffer var3) {
         var1.x = var3.getInt(var2);
         var1.y = var3.getInt(var2 + 4);
         var1.z = var3.getInt(var2 + 8);
         var1.w = var3.getInt(var2 + 12);
      }

      public final void get(Vector3f var1, int var2, FloatBuffer var3) {
         var1.x = var3.get(var2);
         var1.y = var3.get(var2 + 1);
         var1.z = var3.get(var2 + 2);
      }

      public final void get(Vector3f var1, int var2, ByteBuffer var3) {
         var1.x = var3.getFloat(var2);
         var1.y = var3.getFloat(var2 + 4);
         var1.z = var3.getFloat(var2 + 8);
      }

      public final void get(Vector3d var1, int var2, DoubleBuffer var3) {
         var1.x = var3.get(var2);
         var1.y = var3.get(var2 + 1);
         var1.z = var3.get(var2 + 2);
      }

      public final void get(Vector3d var1, int var2, ByteBuffer var3) {
         var1.x = var3.getDouble(var2);
         var1.y = var3.getDouble(var2 + 8);
         var1.z = var3.getDouble(var2 + 16);
      }

      public final void get(Vector3i var1, int var2, IntBuffer var3) {
         var1.x = var3.get(var2);
         var1.y = var3.get(var2 + 1);
         var1.z = var3.get(var2 + 2);
      }

      public final void get(Vector3i var1, int var2, ByteBuffer var3) {
         var1.x = var3.getInt(var2);
         var1.y = var3.getInt(var2 + 4);
         var1.z = var3.getInt(var2 + 8);
      }

      public final void get(Vector2f var1, int var2, FloatBuffer var3) {
         var1.x = var3.get(var2);
         var1.y = var3.get(var2 + 1);
      }

      public final void get(Vector2f var1, int var2, ByteBuffer var3) {
         var1.x = var3.getFloat(var2);
         var1.y = var3.getFloat(var2 + 4);
      }

      public final void get(Vector2d var1, int var2, DoubleBuffer var3) {
         var1.x = var3.get(var2);
         var1.y = var3.get(var2 + 1);
      }

      public final void get(Vector2d var1, int var2, ByteBuffer var3) {
         var1.x = var3.getDouble(var2);
         var1.y = var3.getDouble(var2 + 8);
      }

      public final void get(Vector2i var1, int var2, IntBuffer var3) {
         var1.x = var3.get(var2);
         var1.y = var3.get(var2 + 1);
      }

      public final void get(Vector2i var1, int var2, ByteBuffer var3) {
         var1.x = var3.getInt(var2);
         var1.y = var3.getInt(var2 + 4);
      }

      public final void copy(Matrix4f var1, Matrix4f var2) {
         var2.m00 = var1.m00;
         var2.m01 = var1.m01;
         var2.m02 = var1.m02;
         var2.m03 = var1.m03;
         var2.m10 = var1.m10;
         var2.m11 = var1.m11;
         var2.m12 = var1.m12;
         var2.m13 = var1.m13;
         var2.m20 = var1.m20;
         var2.m21 = var1.m21;
         var2.m22 = var1.m22;
         var2.m23 = var1.m23;
         var2.m30 = var1.m30;
         var2.m31 = var1.m31;
         var2.m32 = var1.m32;
         var2.m33 = var1.m33;
      }

      public final void copy(Matrix3f var1, Matrix4f var2) {
         var2.m00 = var1.m00;
         var2.m01 = var1.m01;
         var2.m02 = var1.m02;
         var2.m03 = 0.0F;
         var2.m10 = var1.m10;
         var2.m11 = var1.m11;
         var2.m12 = var1.m12;
         var2.m13 = 0.0F;
         var2.m20 = var1.m20;
         var2.m21 = var1.m21;
         var2.m22 = var1.m22;
         var2.m23 = 0.0F;
         var2.m30 = 0.0F;
         var2.m31 = 0.0F;
         var2.m32 = 0.0F;
         var2.m33 = 1.0F;
      }

      public final void copy(Matrix4f var1, Matrix3f var2) {
         var2.m00 = var1.m00;
         var2.m01 = var1.m01;
         var2.m02 = var1.m02;
         var2.m10 = var1.m10;
         var2.m11 = var1.m11;
         var2.m12 = var1.m12;
         var2.m20 = var1.m20;
         var2.m21 = var1.m21;
         var2.m22 = var1.m22;
      }

      public final void copy(Matrix3f var1, Matrix4x3f var2) {
         var2.m00 = var1.m00;
         var2.m01 = var1.m01;
         var2.m02 = var1.m02;
         var2.m10 = var1.m10;
         var2.m11 = var1.m11;
         var2.m12 = var1.m12;
         var2.m20 = var1.m20;
         var2.m21 = var1.m21;
         var2.m22 = var1.m22;
         var2.m30 = 0.0F;
         var2.m31 = 0.0F;
         var2.m32 = 0.0F;
      }

      public final void copy3x3(Matrix4f var1, Matrix4f var2) {
         var2.m00 = var1.m00;
         var2.m01 = var1.m01;
         var2.m02 = var1.m02;
         var2.m10 = var1.m10;
         var2.m11 = var1.m11;
         var2.m12 = var1.m12;
         var2.m20 = var1.m20;
         var2.m21 = var1.m21;
         var2.m22 = var1.m22;
      }

      public final void copy3x3(Matrix4x3f var1, Matrix4x3f var2) {
         var2.m00 = var1.m00;
         var2.m01 = var1.m01;
         var2.m02 = var1.m02;
         var2.m10 = var1.m10;
         var2.m11 = var1.m11;
         var2.m12 = var1.m12;
         var2.m20 = var1.m20;
         var2.m21 = var1.m21;
         var2.m22 = var1.m22;
      }

      public final void copy3x3(Matrix3f var1, Matrix4x3f var2) {
         var2.m00 = var1.m00;
         var2.m01 = var1.m01;
         var2.m02 = var1.m02;
         var2.m10 = var1.m10;
         var2.m11 = var1.m11;
         var2.m12 = var1.m12;
         var2.m20 = var1.m20;
         var2.m21 = var1.m21;
         var2.m22 = var1.m22;
      }

      public final void copy3x3(Matrix3f var1, Matrix4f var2) {
         var2.m00 = var1.m00;
         var2.m01 = var1.m01;
         var2.m02 = var1.m02;
         var2.m10 = var1.m10;
         var2.m11 = var1.m11;
         var2.m12 = var1.m12;
         var2.m20 = var1.m20;
         var2.m21 = var1.m21;
         var2.m22 = var1.m22;
      }

      public final void copy4x3(Matrix4x3f var1, Matrix4f var2) {
         var2.m00 = var1.m00;
         var2.m01 = var1.m01;
         var2.m02 = var1.m02;
         var2.m10 = var1.m10;
         var2.m11 = var1.m11;
         var2.m12 = var1.m12;
         var2.m20 = var1.m20;
         var2.m21 = var1.m21;
         var2.m22 = var1.m22;
         var2.m30 = var1.m30;
         var2.m31 = var1.m31;
         var2.m32 = var1.m32;
      }

      public final void copy(Vector4f var1, Vector4f var2) {
         var2.x = var1.x;
         var2.y = var1.y;
         var2.z = var1.z;
         var2.w = var1.w;
      }

      public final void copy(Vector4i var1, Vector4i var2) {
         var2.x = var1.x;
         var2.y = var1.y;
         var2.z = var1.z;
         var2.w = var1.w;
      }

      public final void copy(Quaternionf var1, Quaternionf var2) {
         var2.x = var1.x;
         var2.y = var1.y;
         var2.z = var1.z;
         var2.w = var1.w;
      }

      public final void copy4x3(Matrix4f var1, Matrix4f var2) {
         var2.m00 = var1.m00;
         var2.m01 = var1.m01;
         var2.m02 = var1.m02;
         var2.m10 = var1.m10;
         var2.m11 = var1.m11;
         var2.m12 = var1.m12;
         var2.m20 = var1.m20;
         var2.m21 = var1.m21;
         var2.m22 = var1.m22;
         var2.m30 = var1.m30;
         var2.m31 = var1.m31;
         var2.m32 = var1.m32;
      }

      public final void copy(Matrix4f var1, Matrix4x3f var2) {
         var2.m00 = var1.m00;
         var2.m01 = var1.m01;
         var2.m02 = var1.m02;
         var2.m10 = var1.m10;
         var2.m11 = var1.m11;
         var2.m12 = var1.m12;
         var2.m20 = var1.m20;
         var2.m21 = var1.m21;
         var2.m22 = var1.m22;
         var2.m30 = var1.m30;
         var2.m31 = var1.m31;
         var2.m32 = var1.m32;
      }

      public final void copy(Matrix4x3f var1, Matrix4f var2) {
         var2.m00 = var1.m00;
         var2.m01 = var1.m01;
         var2.m02 = var1.m02;
         var2.m03 = 0.0F;
         var2.m10 = var1.m10;
         var2.m11 = var1.m11;
         var2.m12 = var1.m12;
         var2.m13 = 0.0F;
         var2.m20 = var1.m20;
         var2.m21 = var1.m21;
         var2.m22 = var1.m22;
         var2.m23 = 0.0F;
         var2.m30 = var1.m30;
         var2.m31 = var1.m31;
         var2.m32 = var1.m32;
         var2.m33 = 1.0F;
      }

      public final void copy(Matrix4x3f var1, Matrix4x3f var2) {
         var2.m00 = var1.m00;
         var2.m01 = var1.m01;
         var2.m02 = var1.m02;
         var2.m10 = var1.m10;
         var2.m11 = var1.m11;
         var2.m12 = var1.m12;
         var2.m20 = var1.m20;
         var2.m21 = var1.m21;
         var2.m22 = var1.m22;
         var2.m30 = var1.m30;
         var2.m31 = var1.m31;
         var2.m32 = var1.m32;
      }

      public final void copy(Matrix3f var1, Matrix3f var2) {
         var2.m00 = var1.m00;
         var2.m01 = var1.m01;
         var2.m02 = var1.m02;
         var2.m10 = var1.m10;
         var2.m11 = var1.m11;
         var2.m12 = var1.m12;
         var2.m20 = var1.m20;
         var2.m21 = var1.m21;
         var2.m22 = var1.m22;
      }

      public final void copy(float[] var1, int var2, Matrix4f var3) {
         var3.m00 = var1[var2 + 0];
         var3.m01 = var1[var2 + 1];
         var3.m02 = var1[var2 + 2];
         var3.m03 = var1[var2 + 3];
         var3.m10 = var1[var2 + 4];
         var3.m11 = var1[var2 + 5];
         var3.m12 = var1[var2 + 6];
         var3.m13 = var1[var2 + 7];
         var3.m20 = var1[var2 + 8];
         var3.m21 = var1[var2 + 9];
         var3.m22 = var1[var2 + 10];
         var3.m23 = var1[var2 + 11];
         var3.m30 = var1[var2 + 12];
         var3.m31 = var1[var2 + 13];
         var3.m32 = var1[var2 + 14];
         var3.m33 = var1[var2 + 15];
      }

      public final void copy(float[] var1, int var2, Matrix3f var3) {
         var3.m00 = var1[var2 + 0];
         var3.m01 = var1[var2 + 1];
         var3.m02 = var1[var2 + 2];
         var3.m10 = var1[var2 + 3];
         var3.m11 = var1[var2 + 4];
         var3.m12 = var1[var2 + 5];
         var3.m20 = var1[var2 + 6];
         var3.m21 = var1[var2 + 7];
         var3.m22 = var1[var2 + 8];
      }

      public final void copy(float[] var1, int var2, Matrix4x3f var3) {
         var3.m00 = var1[var2 + 0];
         var3.m01 = var1[var2 + 1];
         var3.m02 = var1[var2 + 2];
         var3.m10 = var1[var2 + 3];
         var3.m11 = var1[var2 + 4];
         var3.m12 = var1[var2 + 5];
         var3.m20 = var1[var2 + 6];
         var3.m21 = var1[var2 + 7];
         var3.m22 = var1[var2 + 8];
         var3.m30 = var1[var2 + 9];
         var3.m31 = var1[var2 + 10];
         var3.m32 = var1[var2 + 11];
      }

      public final void copy(Matrix4f var1, float[] var2, int var3) {
         var2[var3 + 0] = var1.m00;
         var2[var3 + 1] = var1.m01;
         var2[var3 + 2] = var1.m02;
         var2[var3 + 3] = var1.m03;
         var2[var3 + 4] = var1.m10;
         var2[var3 + 5] = var1.m11;
         var2[var3 + 6] = var1.m12;
         var2[var3 + 7] = var1.m13;
         var2[var3 + 8] = var1.m20;
         var2[var3 + 9] = var1.m21;
         var2[var3 + 10] = var1.m22;
         var2[var3 + 11] = var1.m23;
         var2[var3 + 12] = var1.m30;
         var2[var3 + 13] = var1.m31;
         var2[var3 + 14] = var1.m32;
         var2[var3 + 15] = var1.m33;
      }

      public final void copy(Matrix3f var1, float[] var2, int var3) {
         var2[var3 + 0] = var1.m00;
         var2[var3 + 1] = var1.m01;
         var2[var3 + 2] = var1.m02;
         var2[var3 + 3] = var1.m10;
         var2[var3 + 4] = var1.m11;
         var2[var3 + 5] = var1.m12;
         var2[var3 + 6] = var1.m20;
         var2[var3 + 7] = var1.m21;
         var2[var3 + 8] = var1.m22;
      }

      public final void copy(Matrix4x3f var1, float[] var2, int var3) {
         var2[var3 + 0] = var1.m00;
         var2[var3 + 1] = var1.m01;
         var2[var3 + 2] = var1.m02;
         var2[var3 + 3] = var1.m10;
         var2[var3 + 4] = var1.m11;
         var2[var3 + 5] = var1.m12;
         var2[var3 + 6] = var1.m20;
         var2[var3 + 7] = var1.m21;
         var2[var3 + 8] = var1.m22;
         var2[var3 + 9] = var1.m30;
         var2[var3 + 10] = var1.m31;
         var2[var3 + 11] = var1.m32;
      }

      public final void identity(Matrix4f var1) {
         var1.m00 = 1.0F;
         var1.m01 = 0.0F;
         var1.m02 = 0.0F;
         var1.m03 = 0.0F;
         var1.m10 = 0.0F;
         var1.m11 = 1.0F;
         var1.m12 = 0.0F;
         var1.m13 = 0.0F;
         var1.m20 = 0.0F;
         var1.m21 = 0.0F;
         var1.m22 = 1.0F;
         var1.m23 = 0.0F;
         var1.m30 = 0.0F;
         var1.m31 = 0.0F;
         var1.m32 = 0.0F;
         var1.m33 = 1.0F;
      }

      public final void identity(Matrix4x3f var1) {
         var1.m00 = 1.0F;
         var1.m01 = 0.0F;
         var1.m02 = 0.0F;
         var1.m10 = 0.0F;
         var1.m11 = 1.0F;
         var1.m12 = 0.0F;
         var1.m20 = 0.0F;
         var1.m21 = 0.0F;
         var1.m22 = 1.0F;
         var1.m30 = 0.0F;
         var1.m31 = 0.0F;
         var1.m32 = 0.0F;
      }

      public final void identity(Matrix3f var1) {
         var1.m00 = 1.0F;
         var1.m01 = 0.0F;
         var1.m02 = 0.0F;
         var1.m10 = 0.0F;
         var1.m11 = 1.0F;
         var1.m12 = 0.0F;
         var1.m20 = 0.0F;
         var1.m21 = 0.0F;
         var1.m22 = 1.0F;
      }

      public final void identity(Quaternionf var1) {
         var1.x = 0.0F;
         var1.y = 0.0F;
         var1.z = 0.0F;
         var1.w = 1.0F;
      }

      public final void swap(Matrix4f var1, Matrix4f var2) {
         float var3 = var1.m00;
         var1.m00 = var2.m00;
         var2.m00 = var3;
         var3 = var1.m01;
         var1.m01 = var2.m01;
         var2.m01 = var3;
         var3 = var1.m02;
         var1.m02 = var2.m02;
         var2.m02 = var3;
         var3 = var1.m03;
         var1.m03 = var2.m03;
         var2.m03 = var3;
         var3 = var1.m10;
         var1.m10 = var2.m10;
         var2.m10 = var3;
         var3 = var1.m11;
         var1.m11 = var2.m11;
         var2.m11 = var3;
         var3 = var1.m12;
         var1.m12 = var2.m12;
         var2.m12 = var3;
         var3 = var1.m13;
         var1.m13 = var2.m13;
         var2.m13 = var3;
         var3 = var1.m20;
         var1.m20 = var2.m20;
         var2.m20 = var3;
         var3 = var1.m21;
         var1.m21 = var2.m21;
         var2.m21 = var3;
         var3 = var1.m22;
         var1.m22 = var2.m22;
         var2.m22 = var3;
         var3 = var1.m23;
         var1.m23 = var2.m23;
         var2.m23 = var3;
         var3 = var1.m30;
         var1.m30 = var2.m30;
         var2.m30 = var3;
         var3 = var1.m31;
         var1.m31 = var2.m31;
         var2.m31 = var3;
         var3 = var1.m32;
         var1.m32 = var2.m32;
         var2.m32 = var3;
         var3 = var1.m33;
         var1.m33 = var2.m33;
         var2.m33 = var3;
      }

      public final void swap(Matrix4x3f var1, Matrix4x3f var2) {
         float var3 = var1.m00;
         var1.m00 = var2.m00;
         var2.m00 = var3;
         var3 = var1.m01;
         var1.m01 = var2.m01;
         var2.m01 = var3;
         var3 = var1.m02;
         var1.m02 = var2.m02;
         var2.m02 = var3;
         var3 = var1.m10;
         var1.m10 = var2.m10;
         var2.m10 = var3;
         var3 = var1.m11;
         var1.m11 = var2.m11;
         var2.m11 = var3;
         var3 = var1.m12;
         var1.m12 = var2.m12;
         var2.m12 = var3;
         var3 = var1.m20;
         var1.m20 = var2.m20;
         var2.m20 = var3;
         var3 = var1.m21;
         var1.m21 = var2.m21;
         var2.m21 = var3;
         var3 = var1.m22;
         var1.m22 = var2.m22;
         var2.m22 = var3;
         var3 = var1.m30;
         var1.m30 = var2.m30;
         var2.m30 = var3;
         var3 = var1.m31;
         var1.m31 = var2.m31;
         var2.m31 = var3;
         var3 = var1.m32;
         var1.m32 = var2.m32;
         var2.m32 = var3;
      }

      public final void swap(Matrix3f var1, Matrix3f var2) {
         float var3 = var1.m00;
         var1.m00 = var2.m00;
         var2.m00 = var3;
         var3 = var1.m01;
         var1.m01 = var2.m01;
         var2.m01 = var3;
         var3 = var1.m02;
         var1.m02 = var2.m02;
         var2.m02 = var3;
         var3 = var1.m10;
         var1.m10 = var2.m10;
         var2.m10 = var3;
         var3 = var1.m11;
         var1.m11 = var2.m11;
         var2.m11 = var3;
         var3 = var1.m12;
         var1.m12 = var2.m12;
         var2.m12 = var3;
         var3 = var1.m20;
         var1.m20 = var2.m20;
         var2.m20 = var3;
         var3 = var1.m21;
         var1.m21 = var2.m21;
         var2.m21 = var3;
         var3 = var1.m22;
         var1.m22 = var2.m22;
         var2.m22 = var3;
      }

      public final void zero(Matrix4f var1) {
         var1.m00 = 0.0F;
         var1.m01 = 0.0F;
         var1.m02 = 0.0F;
         var1.m03 = 0.0F;
         var1.m10 = 0.0F;
         var1.m11 = 0.0F;
         var1.m12 = 0.0F;
         var1.m13 = 0.0F;
         var1.m20 = 0.0F;
         var1.m21 = 0.0F;
         var1.m22 = 0.0F;
         var1.m23 = 0.0F;
         var1.m30 = 0.0F;
         var1.m31 = 0.0F;
         var1.m32 = 0.0F;
         var1.m33 = 0.0F;
      }

      public final void zero(Matrix4x3f var1) {
         var1.m00 = 0.0F;
         var1.m01 = 0.0F;
         var1.m02 = 0.0F;
         var1.m10 = 0.0F;
         var1.m11 = 0.0F;
         var1.m12 = 0.0F;
         var1.m20 = 0.0F;
         var1.m21 = 0.0F;
         var1.m22 = 0.0F;
         var1.m30 = 0.0F;
         var1.m31 = 0.0F;
         var1.m32 = 0.0F;
      }

      public final void zero(Matrix3f var1) {
         var1.m00 = 0.0F;
         var1.m01 = 0.0F;
         var1.m02 = 0.0F;
         var1.m10 = 0.0F;
         var1.m11 = 0.0F;
         var1.m12 = 0.0F;
         var1.m20 = 0.0F;
         var1.m21 = 0.0F;
         var1.m22 = 0.0F;
      }

      public final void zero(Vector4f var1) {
         var1.x = 0.0F;
         var1.y = 0.0F;
         var1.z = 0.0F;
         var1.w = 0.0F;
      }

      public final void zero(Vector4i var1) {
         var1.x = 0;
         var1.y = 0;
         var1.z = 0;
         var1.w = 0;
      }

      public final void putMatrix3f(Quaternionf var1, int var2, ByteBuffer var3) {
         float var4 = var1.w * var1.w;
         float var5 = var1.x * var1.x;
         float var6 = var1.y * var1.y;
         float var7 = var1.z * var1.z;
         float var8 = var1.z * var1.w;
         float var9 = var1.x * var1.y;
         float var10 = var1.x * var1.z;
         float var11 = var1.y * var1.w;
         float var12 = var1.y * var1.z;
         float var13 = var1.x * var1.w;
         var3.putFloat(var2, var4 + var5 - var7 - var6);
         var3.putFloat(var2 + 4, var9 + var8 + var8 + var9);
         var3.putFloat(var2 + 8, var10 - var11 + var10 - var11);
         var3.putFloat(var2 + 12, -var8 + var9 - var8 + var9);
         var3.putFloat(var2 + 16, var6 - var7 + var4 - var5);
         var3.putFloat(var2 + 20, var12 + var12 + var13 + var13);
         var3.putFloat(var2 + 24, var11 + var10 + var10 + var11);
         var3.putFloat(var2 + 28, var12 + var12 - var13 - var13);
         var3.putFloat(var2 + 32, var7 - var6 - var5 + var4);
      }

      public final void putMatrix3f(Quaternionf var1, int var2, FloatBuffer var3) {
         float var4 = var1.w * var1.w;
         float var5 = var1.x * var1.x;
         float var6 = var1.y * var1.y;
         float var7 = var1.z * var1.z;
         float var8 = var1.z * var1.w;
         float var9 = var1.x * var1.y;
         float var10 = var1.x * var1.z;
         float var11 = var1.y * var1.w;
         float var12 = var1.y * var1.z;
         float var13 = var1.x * var1.w;
         var3.put(var2, var4 + var5 - var7 - var6);
         var3.put(var2 + 1, var9 + var8 + var8 + var9);
         var3.put(var2 + 2, var10 - var11 + var10 - var11);
         var3.put(var2 + 3, -var8 + var9 - var8 + var9);
         var3.put(var2 + 4, var6 - var7 + var4 - var5);
         var3.put(var2 + 5, var12 + var12 + var13 + var13);
         var3.put(var2 + 6, var11 + var10 + var10 + var11);
         var3.put(var2 + 7, var12 + var12 - var13 - var13);
         var3.put(var2 + 8, var7 - var6 - var5 + var4);
      }

      public final void putMatrix4f(Quaternionf var1, int var2, ByteBuffer var3) {
         float var4 = var1.w * var1.w;
         float var5 = var1.x * var1.x;
         float var6 = var1.y * var1.y;
         float var7 = var1.z * var1.z;
         float var8 = var1.z * var1.w;
         float var9 = var1.x * var1.y;
         float var10 = var1.x * var1.z;
         float var11 = var1.y * var1.w;
         float var12 = var1.y * var1.z;
         float var13 = var1.x * var1.w;
         var3.putFloat(var2, var4 + var5 - var7 - var6);
         var3.putFloat(var2 + 4, var9 + var8 + var8 + var9);
         var3.putFloat(var2 + 8, var10 - var11 + var10 - var11);
         var3.putFloat(var2 + 12, 0.0F);
         var3.putFloat(var2 + 16, -var8 + var9 - var8 + var9);
         var3.putFloat(var2 + 20, var6 - var7 + var4 - var5);
         var3.putFloat(var2 + 24, var12 + var12 + var13 + var13);
         var3.putFloat(var2 + 28, 0.0F);
         var3.putFloat(var2 + 32, var11 + var10 + var10 + var11);
         var3.putFloat(var2 + 36, var12 + var12 - var13 - var13);
         var3.putFloat(var2 + 40, var7 - var6 - var5 + var4);
         var3.putFloat(var2 + 44, 0.0F);
         var3.putLong(var2 + 48, 0L);
         var3.putLong(var2 + 56, 4575657221408423936L);
      }

      public final void putMatrix4f(Quaternionf var1, int var2, FloatBuffer var3) {
         float var4 = var1.w * var1.w;
         float var5 = var1.x * var1.x;
         float var6 = var1.y * var1.y;
         float var7 = var1.z * var1.z;
         float var8 = var1.z * var1.w;
         float var9 = var1.x * var1.y;
         float var10 = var1.x * var1.z;
         float var11 = var1.y * var1.w;
         float var12 = var1.y * var1.z;
         float var13 = var1.x * var1.w;
         var3.put(var2, var4 + var5 - var7 - var6);
         var3.put(var2 + 1, var9 + var8 + var8 + var9);
         var3.put(var2 + 2, var10 - var11 + var10 - var11);
         var3.put(var2 + 3, 0.0F);
         var3.put(var2 + 4, -var8 + var9 - var8 + var9);
         var3.put(var2 + 5, var6 - var7 + var4 - var5);
         var3.put(var2 + 6, var12 + var12 + var13 + var13);
         var3.put(var2 + 7, 0.0F);
         var3.put(var2 + 8, var11 + var10 + var10 + var11);
         var3.put(var2 + 9, var12 + var12 - var13 - var13);
         var3.put(var2 + 10, var7 - var6 - var5 + var4);
         var3.put(var2 + 11, 0.0F);
         var3.put(var2 + 12, 0.0F);
         var3.put(var2 + 13, 0.0F);
         var3.put(var2 + 14, 0.0F);
         var3.put(var2 + 15, 1.0F);
      }

      public final void putMatrix4x3f(Quaternionf var1, int var2, ByteBuffer var3) {
         float var4 = var1.w * var1.w;
         float var5 = var1.x * var1.x;
         float var6 = var1.y * var1.y;
         float var7 = var1.z * var1.z;
         float var8 = var1.z * var1.w;
         float var9 = var1.x * var1.y;
         float var10 = var1.x * var1.z;
         float var11 = var1.y * var1.w;
         float var12 = var1.y * var1.z;
         float var13 = var1.x * var1.w;
         var3.putFloat(var2, var4 + var5 - var7 - var6);
         var3.putFloat(var2 + 4, var9 + var8 + var8 + var9);
         var3.putFloat(var2 + 8, var10 - var11 + var10 - var11);
         var3.putFloat(var2 + 12, -var8 + var9 - var8 + var9);
         var3.putFloat(var2 + 16, var6 - var7 + var4 - var5);
         var3.putFloat(var2 + 20, var12 + var12 + var13 + var13);
         var3.putFloat(var2 + 24, var11 + var10 + var10 + var11);
         var3.putFloat(var2 + 28, var12 + var12 - var13 - var13);
         var3.putFloat(var2 + 32, var7 - var6 - var5 + var4);
         var3.putLong(var2 + 36, 0L);
         var3.putFloat(var2 + 44, 0.0F);
      }

      public final void putMatrix4x3f(Quaternionf var1, int var2, FloatBuffer var3) {
         float var4 = var1.w * var1.w;
         float var5 = var1.x * var1.x;
         float var6 = var1.y * var1.y;
         float var7 = var1.z * var1.z;
         float var8 = var1.z * var1.w;
         float var9 = var1.x * var1.y;
         float var10 = var1.x * var1.z;
         float var11 = var1.y * var1.w;
         float var12 = var1.y * var1.z;
         float var13 = var1.x * var1.w;
         var3.put(var2, var4 + var5 - var7 - var6);
         var3.put(var2 + 1, var9 + var8 + var8 + var9);
         var3.put(var2 + 2, var10 - var11 + var10 - var11);
         var3.put(var2 + 3, -var8 + var9 - var8 + var9);
         var3.put(var2 + 4, var6 - var7 + var4 - var5);
         var3.put(var2 + 5, var12 + var12 + var13 + var13);
         var3.put(var2 + 6, var11 + var10 + var10 + var11);
         var3.put(var2 + 7, var12 + var12 - var13 - var13);
         var3.put(var2 + 8, var7 - var6 - var5 + var4);
         var3.put(var2 + 9, 0.0F);
         var3.put(var2 + 10, 0.0F);
         var3.put(var2 + 11, 0.0F);
      }

      public final void set(Matrix4f var1, Vector4f var2, Vector4f var3, Vector4f var4, Vector4f var5) {
         var1.m00 = var2.x;
         var1.m01 = var2.y;
         var1.m02 = var2.z;
         var1.m03 = var2.w;
         var1.m10 = var3.x;
         var1.m11 = var3.y;
         var1.m12 = var3.z;
         var1.m13 = var3.w;
         var1.m20 = var4.x;
         var1.m21 = var4.y;
         var1.m22 = var4.z;
         var1.m23 = var4.w;
         var1.m30 = var5.x;
         var1.m31 = var5.y;
         var1.m32 = var5.z;
         var1.m33 = var5.w;
      }

      public final void set(Matrix4x3f var1, Vector3f var2, Vector3f var3, Vector3f var4, Vector3f var5) {
         var1.m00 = var2.x;
         var1.m01 = var2.y;
         var1.m02 = var2.z;
         var1.m10 = var3.x;
         var1.m11 = var3.y;
         var1.m12 = var3.z;
         var1.m20 = var4.x;
         var1.m21 = var4.y;
         var1.m22 = var4.z;
         var1.m30 = var5.x;
         var1.m31 = var5.y;
         var1.m32 = var5.z;
      }

      public final void set(Matrix3f var1, Vector3f var2, Vector3f var3, Vector3f var4) {
         var1.m00 = var2.x;
         var1.m01 = var2.y;
         var1.m02 = var2.z;
         var1.m10 = var3.x;
         var1.m11 = var3.y;
         var1.m12 = var3.z;
         var1.m20 = var4.x;
         var1.m21 = var4.y;
         var1.m22 = var4.z;
      }

      public final void putColumn0(Matrix4f var1, Vector4f var2) {
         var2.x = var1.m00;
         var2.y = var1.m01;
         var2.z = var1.m02;
         var2.w = var1.m03;
      }

      public final void putColumn1(Matrix4f var1, Vector4f var2) {
         var2.x = var1.m10;
         var2.y = var1.m11;
         var2.z = var1.m12;
         var2.w = var1.m13;
      }

      public final void putColumn2(Matrix4f var1, Vector4f var2) {
         var2.x = var1.m20;
         var2.y = var1.m21;
         var2.z = var1.m22;
         var2.w = var1.m23;
      }

      public final void putColumn3(Matrix4f var1, Vector4f var2) {
         var2.x = var1.m30;
         var2.y = var1.m31;
         var2.z = var1.m32;
         var2.w = var1.m33;
      }

      public final void getColumn0(Matrix4f var1, Vector4f var2) {
         var1.m00 = var2.x;
         var1.m01 = var2.y;
         var1.m02 = var2.z;
         var1.m03 = var2.w;
      }

      public final void getColumn1(Matrix4f var1, Vector4f var2) {
         var1.m10 = var2.x;
         var1.m11 = var2.y;
         var1.m12 = var2.z;
         var1.m13 = var2.w;
      }

      public final void getColumn2(Matrix4f var1, Vector4f var2) {
         var1.m20 = var2.x;
         var1.m21 = var2.y;
         var1.m22 = var2.z;
         var1.m23 = var2.w;
      }

      public final void getColumn3(Matrix4f var1, Vector4f var2) {
         var1.m30 = var2.x;
         var1.m31 = var2.y;
         var1.m32 = var2.z;
         var1.m33 = var2.w;
      }

      public final void broadcast(float var1, Vector4f var2) {
         var2.x = var1;
         var2.y = var1;
         var2.z = var1;
         var2.w = var1;
      }

      public final void broadcast(int var1, Vector4i var2) {
         var2.x = var1;
         var2.y = var1;
         var2.z = var1;
         var2.w = var1;
      }
   }
}
