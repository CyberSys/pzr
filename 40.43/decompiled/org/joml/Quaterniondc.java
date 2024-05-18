package org.joml;

public interface Quaterniondc {
   double x();

   double y();

   double z();

   double w();

   Quaterniond normalize(Quaterniond var1);

   Quaterniond add(double var1, double var3, double var5, double var7, Quaterniond var9);

   Quaterniond add(Quaterniondc var1, Quaterniond var2);

   double dot(Quaterniondc var1);

   double angle();

   Matrix3d get(Matrix3d var1);

   Matrix3f get(Matrix3f var1);

   Matrix4d get(Matrix4d var1);

   Matrix4f get(Matrix4f var1);

   Quaterniond get(Quaterniond var1);

   Quaterniond mul(Quaterniondc var1, Quaterniond var2);

   Quaterniond mul(double var1, double var3, double var5, double var7, Quaterniond var9);

   Quaterniond premul(Quaterniondc var1, Quaterniond var2);

   Quaterniond premul(double var1, double var3, double var5, double var7, Quaterniond var9);

   Vector3d transform(Vector3d var1);

   Vector4d transform(Vector4d var1);

   Vector3d transform(Vector3dc var1, Vector3d var2);

   Vector3d transform(double var1, double var3, double var5, Vector3d var7);

   Vector4d transform(Vector4dc var1, Vector4d var2);

   Vector4d transform(double var1, double var3, double var5, Vector4d var7);

   Quaterniond invert(Quaterniond var1);

   Quaterniond div(Quaterniondc var1, Quaterniond var2);

   Quaterniond conjugate(Quaterniond var1);

   double lengthSquared();

   Quaterniond slerp(Quaterniondc var1, double var2, Quaterniond var4);

   Quaterniond scale(double var1, Quaterniond var3);

   Quaterniond integrate(double var1, double var3, double var5, double var7, Quaterniond var9);

   Quaterniond nlerp(Quaterniondc var1, double var2, Quaterniond var4);

   Quaterniond nlerpIterative(Quaterniondc var1, double var2, double var4, Quaterniond var6);

   Quaterniond lookAlong(Vector3dc var1, Vector3dc var2, Quaterniond var3);

   Quaterniond lookAlong(double var1, double var3, double var5, double var7, double var9, double var11, Quaterniond var13);

   Quaterniond difference(Quaterniondc var1, Quaterniond var2);

   Quaterniond rotateTo(double var1, double var3, double var5, double var7, double var9, double var11, Quaterniond var13);

   Quaterniond rotateTo(Vector3dc var1, Vector3dc var2, Quaterniond var3);

   Quaterniond rotate(Vector3dc var1, Quaterniond var2);

   Quaterniond rotate(double var1, double var3, double var5, Quaterniond var7);

   Quaterniond rotateLocal(double var1, double var3, double var5, Quaterniond var7);

   Quaterniond rotateX(double var1, Quaterniond var3);

   Quaterniond rotateY(double var1, Quaterniond var3);

   Quaterniond rotateZ(double var1, Quaterniond var3);

   Quaterniond rotateLocalX(double var1, Quaterniond var3);

   Quaterniond rotateLocalY(double var1, Quaterniond var3);

   Quaterniond rotateLocalZ(double var1, Quaterniond var3);

   Quaterniond rotateXYZ(double var1, double var3, double var5, Quaterniond var7);

   Quaterniond rotateZYX(double var1, double var3, double var5, Quaterniond var7);

   Quaterniond rotateYXZ(double var1, double var3, double var5, Quaterniond var7);

   Vector3d getEulerAnglesXYZ(Vector3d var1);

   Quaterniond rotateAxis(double var1, double var3, double var5, double var7, Quaterniond var9);

   Quaterniond rotateAxis(double var1, Vector3dc var3, Quaterniond var4);

   Vector3d positiveX(Vector3d var1);

   Vector3d normalizedPositiveX(Vector3d var1);

   Vector3d positiveY(Vector3d var1);

   Vector3d normalizedPositiveY(Vector3d var1);

   Vector3d positiveZ(Vector3d var1);

   Vector3d normalizedPositiveZ(Vector3d var1);
}
