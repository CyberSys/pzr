package org.joml;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public interface Vector2ic {
   int x();

   int y();

   ByteBuffer get(ByteBuffer var1);

   ByteBuffer get(int var1, ByteBuffer var2);

   IntBuffer get(IntBuffer var1);

   IntBuffer get(int var1, IntBuffer var2);

   Vector2i sub(Vector2ic var1, Vector2i var2);

   Vector2i sub(int var1, int var2, Vector2i var3);

   long lengthSquared();

   double length();

   double distance(Vector2ic var1);

   double distance(int var1, int var2);

   long distanceSquared(Vector2ic var1);

   long distanceSquared(int var1, int var2);

   Vector2i add(Vector2ic var1, Vector2i var2);

   Vector2i add(int var1, int var2, Vector2i var3);

   Vector2i mul(int var1, Vector2i var2);

   Vector2i mul(Vector2ic var1, Vector2i var2);

   Vector2i mul(int var1, int var2, Vector2i var3);

   Vector2i negate(Vector2i var1);
}
