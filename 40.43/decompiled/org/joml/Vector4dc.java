package org.joml;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

public interface Vector4dc {
   double x();

   double y();

   double z();

   double w();

   ByteBuffer get(ByteBuffer var1);

   ByteBuffer get(int var1, ByteBuffer var2);

   DoubleBuffer get(DoubleBuffer var1);

   DoubleBuffer get(int var1, DoubleBuffer var2);

   Vector4d sub(double var1, double var3, double var5, double var7, Vector4d var9);

   Vector4d add(double var1, double var3, double var5, double var7, Vector4d var9);

   Vector4d fma(Vector4dc var1, Vector4dc var2, Vector4d var3);

   Vector4d fma(double var1, Vector4dc var3, Vector4d var4);

   Vector4d mul(Vector4dc var1, Vector4d var2);

   Vector4d div(Vector4dc var1, Vector4d var2);

   Vector4d mul(Matrix4dc var1, Vector4d var2);

   Vector4d mul(Matrix4x3dc var1, Vector4d var2);

   Vector4d mul(Matrix4x3fc var1, Vector4d var2);

   Vector4d mul(Matrix4fc var1, Vector4d var2);

   Vector4d mulProject(Matrix4dc var1, Vector4d var2);

   Vector4d mul(double var1, Vector4d var3);

   Vector4d div(double var1, Vector4d var3);

   Vector4d rotate(Quaterniondc var1, Vector4d var2);

   double lengthSquared();

   double length();

   Vector4d normalize(Vector4d var1);

   Vector4d normalize3(Vector4d var1);

   double distance(Vector4dc var1);

   double distance(double var1, double var3, double var5, double var7);

   double dot(Vector4dc var1);

   double dot(double var1, double var3, double var5, double var7);

   double angleCos(Vector4dc var1);

   double angle(Vector4dc var1);

   Vector4d negate(Vector4d var1);

   Vector4d smoothStep(Vector4dc var1, double var2, Vector4d var4);

   Vector4d hermite(Vector4dc var1, Vector4dc var2, Vector4dc var3, double var4, Vector4d var6);

   Vector4d lerp(Vector4dc var1, double var2, Vector4d var4);
}
