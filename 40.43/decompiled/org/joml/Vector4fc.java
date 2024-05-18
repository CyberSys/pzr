package org.joml;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public interface Vector4fc {
   float x();

   float y();

   float z();

   float w();

   FloatBuffer get(FloatBuffer var1);

   FloatBuffer get(int var1, FloatBuffer var2);

   ByteBuffer get(ByteBuffer var1);

   ByteBuffer get(int var1, ByteBuffer var2);

   Vector4f sub(Vector4fc var1, Vector4f var2);

   Vector4f sub(float var1, float var2, float var3, float var4, Vector4f var5);

   Vector4f add(Vector4fc var1, Vector4f var2);

   Vector4f add(float var1, float var2, float var3, float var4, Vector4f var5);

   Vector4f fma(Vector4fc var1, Vector4fc var2, Vector4f var3);

   Vector4f fma(float var1, Vector4fc var2, Vector4f var3);

   Vector4f mul(Vector4fc var1, Vector4f var2);

   Vector4f div(Vector4fc var1, Vector4f var2);

   Vector4f mul(Matrix4fc var1, Vector4f var2);

   Vector4f mul(Matrix4x3fc var1, Vector4f var2);

   Vector4f mulProject(Matrix4fc var1, Vector4f var2);

   Vector4f mul(float var1, Vector4f var2);

   Vector4f mul(float var1, float var2, float var3, float var4, Vector4f var5);

   Vector4f div(float var1, Vector4f var2);

   Vector4f div(float var1, float var2, float var3, float var4, Vector4f var5);

   Vector4f rotate(Quaternionfc var1, Vector4f var2);

   float lengthSquared();

   float length();

   Vector4f normalize(Vector4f var1);

   float distance(Vector4fc var1);

   float distance(float var1, float var2, float var3, float var4);

   float dot(Vector4fc var1);

   float dot(float var1, float var2, float var3, float var4);

   float angleCos(Vector4fc var1);

   float angle(Vector4fc var1);

   Vector4f negate(Vector4f var1);

   Vector4f lerp(Vector4fc var1, float var2, Vector4f var3);

   Vector4f smoothStep(Vector4fc var1, float var2, Vector4f var3);

   Vector4f hermite(Vector4fc var1, Vector4fc var2, Vector4fc var3, float var4, Vector4f var5);
}
