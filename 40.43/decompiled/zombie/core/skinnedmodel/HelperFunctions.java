package zombie.core.skinnedmodel;

import java.util.Stack;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import zombie.core.Color;

public class HelperFunctions {
   public static Stack MatrixStack = new Stack();

   public static int ToRgba(Color var0) {
      return (int)var0.a << 24 | (int)var0.b << 16 | (int)var0.g << 8 | (int)var0.r;
   }

   public static void returnMatrix(Matrix4f var0) {
      MatrixStack.push(var0);
   }

   public static Matrix4f getMatrix() {
      Matrix4f var0 = null;
      if (MatrixStack.isEmpty()) {
         var0 = new Matrix4f();
      } else {
         var0 = (Matrix4f)MatrixStack.pop();
      }

      return var0;
   }

   public static Matrix4f getMatrix(Matrix4f var0) {
      Matrix4f var1 = null;
      if (MatrixStack.isEmpty()) {
         var1 = new Matrix4f();
      } else {
         var1 = (Matrix4f)MatrixStack.pop();
      }

      var1.load(var0);
      return var1;
   }

   public static Matrix4f CreateFromQuaternion(Quaternion var0) {
      Matrix4f var1 = getMatrix();
      var1.setIdentity();
      if (var0.length() > 0.0F) {
         var0.normalise();
      }

      float var2 = var0.x * var0.x;
      float var3 = var0.x * var0.y;
      float var4 = var0.x * var0.z;
      float var5 = var0.x * var0.w;
      float var6 = var0.y * var0.y;
      float var7 = var0.y * var0.z;
      float var8 = var0.y * var0.w;
      float var9 = var0.z * var0.z;
      float var10 = var0.z * var0.w;
      var1.m00 = 1.0F - 2.0F * (var6 + var9);
      var1.m10 = 2.0F * (var3 - var10);
      var1.m20 = 2.0F * (var4 + var8);
      var1.m30 = 0.0F;
      var1.m01 = 2.0F * (var3 + var10);
      var1.m11 = 1.0F - 2.0F * (var2 + var9);
      var1.m21 = 2.0F * (var7 - var5) * 1.0F;
      var1.m31 = 0.0F;
      var1.m02 = 2.0F * (var4 - var8);
      var1.m12 = 2.0F * (var7 + var5);
      var1.m22 = 1.0F - 2.0F * (var2 + var6);
      var1.m32 = 0.0F;
      var1.m03 = 0.0F;
      var1.m13 = 0.0F;
      var1.m23 = 0.0F;
      var1.m33 = 1.0F;
      var1.m30 = 0.0F;
      var1.m31 = 0.0F;
      var1.m32 = 0.0F;
      var1 = (Matrix4f)var1.transpose();
      return var1;
   }

   public static Matrix4f CreateFromQuaternionPosition(Quaternion var0, Vector3f var1) {
      Matrix4f var2 = CreateFromQuaternion(var0);
      Matrix4f var3 = getMatrix();
      var3.setIdentity();
      var3.translate(var1);
      var3 = (Matrix4f)var3.transpose();
      Matrix4f var4 = getMatrix();
      Matrix4f.mul(var2, var3, var4);
      returnMatrix(var3);
      returnMatrix(var2);
      return var4;
   }
}
