package org.joml;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

public interface Vector2dc {
   double x();

   double y();

   ByteBuffer get(ByteBuffer var1);

   ByteBuffer get(int var1, ByteBuffer var2);

   DoubleBuffer get(DoubleBuffer var1);

   DoubleBuffer get(int var1, DoubleBuffer var2);

   Vector2d sub(double var1, double var3, Vector2d var5);

   Vector2d sub(Vector2dc var1, Vector2d var2);

   Vector2d sub(Vector2fc var1, Vector2d var2);

   Vector2d mul(double var1, Vector2d var3);

   Vector2d mul(double var1, double var3, Vector2d var5);

   Vector2d mul(Vector2dc var1, Vector2d var2);

   double dot(Vector2dc var1);

   double angle(Vector2dc var1);

   double length();

   double distance(Vector2dc var1);

   double distance(Vector2fc var1);

   double distance(double var1, double var3);

   Vector2d normalize(Vector2d var1);

   Vector2d add(double var1, double var3, Vector2d var5);

   Vector2d add(Vector2dc var1, Vector2d var2);

   Vector2d add(Vector2fc var1, Vector2d var2);

   Vector2d negate(Vector2d var1);

   Vector2d lerp(Vector2dc var1, double var2, Vector2d var4);

   Vector2d fma(Vector2dc var1, Vector2dc var2, Vector2d var3);

   Vector2d fma(double var1, Vector2dc var3, Vector2d var4);
}
