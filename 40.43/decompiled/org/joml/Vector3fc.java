package org.joml;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public interface Vector3fc {
   float x();

   float y();

   float z();

   FloatBuffer get(FloatBuffer var1);

   FloatBuffer get(int var1, FloatBuffer var2);

   ByteBuffer get(ByteBuffer var1);

   ByteBuffer get(int var1, ByteBuffer var2);

   Vector3f sub(Vector3fc var1, Vector3f var2);

   Vector3f sub(float var1, float var2, float var3, Vector3f var4);

   Vector3f add(Vector3fc var1, Vector3f var2);

   Vector3f add(float var1, float var2, float var3, Vector3f var4);

   Vector3f fma(Vector3fc var1, Vector3fc var2, Vector3f var3);

   Vector3f fma(float var1, Vector3fc var2, Vector3f var3);

   Vector3f mul(Vector3fc var1, Vector3f var2);

   Vector3f div(Vector3fc var1, Vector3f var2);

   Vector3f mulProject(Matrix4fc var1, Vector3f var2);

   Vector3f mul(Matrix3fc var1, Vector3f var2);

   Vector3f mulTranspose(Matrix3fc var1, Vector3f var2);

   Vector3f mulPosition(Matrix4fc var1, Vector3f var2);

   Vector3f mulPosition(Matrix4x3fc var1, Vector3f var2);

   Vector3f mulTransposePosition(Matrix4fc var1, Vector3f var2);

   float mulPositionW(Matrix4fc var1, Vector3f var2);

   Vector3f mulDirection(Matrix4fc var1, Vector3f var2);

   Vector3f mulDirection(Matrix4x3fc var1, Vector3f var2);

   Vector3f mulTransposeDirection(Matrix4fc var1, Vector3f var2);

   Vector3f mul(float var1, Vector3f var2);

   Vector3f mul(float var1, float var2, float var3, Vector3f var4);

   Vector3f div(float var1, Vector3f var2);

   Vector3f div(float var1, float var2, float var3, Vector3f var4);

   Vector3f rotate(Quaternionfc var1, Vector3f var2);

   Quaternionf rotationTo(Vector3fc var1, Quaternionf var2);

   Quaternionf rotationTo(float var1, float var2, float var3, Quaternionf var4);

   float lengthSquared();

   float length();

   Vector3f normalize(Vector3f var1);

   Vector3f cross(Vector3fc var1, Vector3f var2);

   Vector3f cross(float var1, float var2, float var3, Vector3f var4);

   float distance(Vector3fc var1);

   float distance(float var1, float var2, float var3);

   float distanceSquared(Vector3fc var1);

   float distanceSquared(float var1, float var2, float var3);

   float dot(Vector3fc var1);

   float dot(float var1, float var2, float var3);

   float angleCos(Vector3fc var1);

   float angle(Vector3fc var1);

   Vector3f negate(Vector3f var1);

   Vector3f reflect(Vector3fc var1, Vector3f var2);

   Vector3f reflect(float var1, float var2, float var3, Vector3f var4);

   Vector3f half(Vector3fc var1, Vector3f var2);

   Vector3f half(float var1, float var2, float var3, Vector3f var4);

   Vector3f smoothStep(Vector3fc var1, float var2, Vector3f var3);

   Vector3f hermite(Vector3fc var1, Vector3fc var2, Vector3fc var3, float var4, Vector3f var5);

   Vector3f lerp(Vector3fc var1, float var2, Vector3f var3);

   float get(int var1) throws IllegalArgumentException;

   int maxComponent();

   int minComponent();

   Vector3f orthogonalize(Vector3fc var1, Vector3f var2);

   Vector3f orthogonalizeUnit(Vector3fc var1, Vector3f var2);
}
