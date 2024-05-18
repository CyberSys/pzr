package org.joml;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public interface Vector2fc {
   float x();

   float y();

   ByteBuffer get(ByteBuffer var1);

   ByteBuffer get(int var1, ByteBuffer var2);

   FloatBuffer get(FloatBuffer var1);

   FloatBuffer get(int var1, FloatBuffer var2);

   Vector2f sub(Vector2fc var1, Vector2f var2);

   Vector2f sub(float var1, float var2, Vector2f var3);

   float dot(Vector2fc var1);

   float angle(Vector2fc var1);

   float length();

   float lengthSquared();

   float distance(Vector2fc var1);

   float distanceSquared(Vector2fc var1);

   float distance(float var1, float var2);

   float distanceSquared(float var1, float var2);

   Vector2f normalize(Vector2f var1);

   Vector2f add(Vector2fc var1, Vector2f var2);

   Vector2f add(float var1, float var2, Vector2f var3);

   Vector2f negate(Vector2f var1);

   Vector2f mul(float var1, Vector2f var2);

   Vector2f mul(float var1, float var2, Vector2f var3);

   Vector2f mul(Vector2fc var1, Vector2f var2);

   Vector2f lerp(Vector2fc var1, float var2, Vector2f var3);

   Vector2f fma(Vector2fc var1, Vector2fc var2, Vector2f var3);

   Vector2f fma(float var1, Vector2fc var2, Vector2f var3);
}
