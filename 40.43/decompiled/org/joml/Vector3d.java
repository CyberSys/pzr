package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Vector3d implements Externalizable, Vector3dc {
   private static final long serialVersionUID = 1L;
   public double x;
   public double y;
   public double z;

   public Vector3d() {
   }

   public Vector3d(double var1) {
      this(var1, var1, var1);
   }

   public Vector3d(double var1, double var3, double var5) {
      this.x = var1;
      this.y = var3;
      this.z = var5;
   }

   public Vector3d(Vector3fc var1) {
      this.x = (double)var1.x();
      this.y = (double)var1.y();
      this.z = (double)var1.z();
   }

   public Vector3d(Vector2fc var1, double var2) {
      this.x = (double)var1.x();
      this.y = (double)var1.y();
      this.z = var2;
   }

   public Vector3d(Vector3dc var1) {
      this.x = var1.x();
      this.y = var1.y();
      this.z = var1.z();
   }

   public Vector3d(Vector2dc var1, double var2) {
      this.x = var1.x();
      this.y = var1.y();
      this.z = var2;
   }

   public Vector3d(ByteBuffer var1) {
      this(var1.position(), var1);
   }

   public Vector3d(int var1, ByteBuffer var2) {
      MemUtil.INSTANCE.get(this, var1, var2);
   }

   public Vector3d(DoubleBuffer var1) {
      this(var1.position(), var1);
   }

   public Vector3d(int var1, DoubleBuffer var2) {
      MemUtil.INSTANCE.get(this, var1, var2);
   }

   public double x() {
      return this.x;
   }

   public double y() {
      return this.y;
   }

   public double z() {
      return this.z;
   }

   public Vector3d set(Vector3dc var1) {
      this.x = var1.x();
      this.y = var1.y();
      this.z = var1.z();
      return this;
   }

   public Vector3d set(Vector2dc var1, double var2) {
      this.x = var1.x();
      this.y = var1.y();
      this.z = var2;
      return this;
   }

   public Vector3d set(Vector3fc var1) {
      this.x = (double)var1.x();
      this.y = (double)var1.y();
      this.z = (double)var1.z();
      return this;
   }

   public Vector3d set(Vector2fc var1, double var2) {
      this.x = (double)var1.x();
      this.y = (double)var1.y();
      this.z = var2;
      return this;
   }

   public Vector3d set(double var1) {
      return this.set(var1, var1, var1);
   }

   public Vector3d set(double var1, double var3, double var5) {
      this.x = var1;
      this.y = var3;
      this.z = var5;
      return this;
   }

   public Vector3d set(ByteBuffer var1) {
      return this.set(var1.position(), var1);
   }

   public Vector3d set(int var1, ByteBuffer var2) {
      MemUtil.INSTANCE.get(this, var1, var2);
      return this;
   }

   public Vector3d set(DoubleBuffer var1) {
      return this.set(var1.position(), var1);
   }

   public Vector3d set(int var1, DoubleBuffer var2) {
      MemUtil.INSTANCE.get(this, var1, var2);
      return this;
   }

   public Vector3d setComponent(int var1, double var2) throws IllegalArgumentException {
      switch(var1) {
      case 0:
         this.x = var2;
         break;
      case 1:
         this.y = var2;
         break;
      case 2:
         this.z = var2;
         break;
      default:
         throw new IllegalArgumentException();
      }

      return this;
   }

   public ByteBuffer get(ByteBuffer var1) {
      return this.get(var1.position(), var1);
   }

   public ByteBuffer get(int var1, ByteBuffer var2) {
      MemUtil.INSTANCE.put(this, var1, var2);
      return var2;
   }

   public DoubleBuffer get(DoubleBuffer var1) {
      return this.get(var1.position(), var1);
   }

   public DoubleBuffer get(int var1, DoubleBuffer var2) {
      MemUtil.INSTANCE.put(this, var1, var2);
      return var2;
   }

   public Vector3d sub(Vector3dc var1) {
      this.x -= var1.x();
      this.y -= var1.y();
      this.z -= var1.z();
      return this;
   }

   public Vector3d sub(Vector3dc var1, Vector3d var2) {
      var2.x = this.x - var1.x();
      var2.y = this.y - var1.y();
      var2.z = this.z - var1.z();
      return var2;
   }

   public Vector3d sub(Vector3fc var1) {
      this.x -= (double)var1.x();
      this.y -= (double)var1.y();
      this.z -= (double)var1.z();
      return this;
   }

   public Vector3d sub(Vector3fc var1, Vector3d var2) {
      var2.x = this.x - (double)var1.x();
      var2.y = this.y - (double)var1.y();
      var2.z = this.z - (double)var1.z();
      return var2;
   }

   public Vector3d sub(double var1, double var3, double var5) {
      this.x -= var1;
      this.y -= var3;
      this.z -= var5;
      return this;
   }

   public Vector3d sub(double var1, double var3, double var5, Vector3d var7) {
      var7.x = this.x - var1;
      var7.y = this.y - var3;
      var7.z = this.z - var5;
      return var7;
   }

   public Vector3d add(Vector3dc var1) {
      this.x += var1.x();
      this.y += var1.y();
      this.z += var1.z();
      return this;
   }

   public Vector3d add(Vector3dc var1, Vector3d var2) {
      var2.x = this.x + var1.x();
      var2.y = this.y + var1.y();
      var2.z = this.z + var1.z();
      return var2;
   }

   public Vector3d add(Vector3fc var1) {
      this.x += (double)var1.x();
      this.y += (double)var1.y();
      this.z += (double)var1.z();
      return this;
   }

   public Vector3d add(Vector3fc var1, Vector3d var2) {
      var2.x = this.x + (double)var1.x();
      var2.y = this.y + (double)var1.y();
      var2.z = this.z + (double)var1.z();
      return var2;
   }

   public Vector3d add(double var1, double var3, double var5) {
      this.x += var1;
      this.y += var3;
      this.z += var5;
      return this;
   }

   public Vector3d add(double var1, double var3, double var5, Vector3d var7) {
      var7.x = this.x + var1;
      var7.y = this.y + var3;
      var7.z = this.z + var5;
      return var7;
   }

   public Vector3d fma(Vector3dc var1, Vector3dc var2) {
      this.x += var1.x() * var2.x();
      this.y += var1.y() * var2.y();
      this.z += var1.z() * var2.z();
      return this;
   }

   public Vector3d fma(double var1, Vector3dc var3) {
      this.x += var1 * var3.x();
      this.y += var1 * var3.y();
      this.z += var1 * var3.z();
      return this;
   }

   public Vector3d fma(Vector3fc var1, Vector3fc var2) {
      this.x += (double)(var1.x() * var2.x());
      this.y += (double)(var1.y() * var2.y());
      this.z += (double)(var1.z() * var2.z());
      return this;
   }

   public Vector3d fma(double var1, Vector3fc var3) {
      this.x += var1 * (double)var3.x();
      this.y += var1 * (double)var3.y();
      this.z += var1 * (double)var3.z();
      return this;
   }

   public Vector3d fma(Vector3dc var1, Vector3dc var2, Vector3d var3) {
      var3.x = this.x + var1.x() * var2.x();
      var3.y = this.y + var1.y() * var2.y();
      var3.z = this.z + var1.z() * var2.z();
      return var3;
   }

   public Vector3d fma(double var1, Vector3dc var3, Vector3d var4) {
      var4.x = this.x + var1 * var3.x();
      var4.y = this.y + var1 * var3.y();
      var4.z = this.z + var1 * var3.z();
      return var4;
   }

   public Vector3d fma(Vector3dc var1, Vector3fc var2, Vector3d var3) {
      var3.x = this.x + var1.x() * (double)var2.x();
      var3.y = this.y + var1.y() * (double)var2.y();
      var3.z = this.z + var1.z() * (double)var2.z();
      return var3;
   }

   public Vector3d fma(double var1, Vector3fc var3, Vector3d var4) {
      var4.x = this.x + var1 * (double)var3.x();
      var4.y = this.y + var1 * (double)var3.y();
      var4.z = this.z + var1 * (double)var3.z();
      return var4;
   }

   public Vector3d mul(Vector3dc var1) {
      this.x *= var1.x();
      this.y *= var1.y();
      this.z *= var1.z();
      return this;
   }

   public Vector3d mul(Vector3fc var1) {
      this.x *= (double)var1.x();
      this.y *= (double)var1.y();
      this.z *= (double)var1.z();
      return this;
   }

   public Vector3d mul(Vector3fc var1, Vector3d var2) {
      var2.x = this.x * (double)var1.x();
      var2.y = this.y * (double)var1.y();
      var2.z = this.z * (double)var1.z();
      return var2;
   }

   public Vector3d mul(Vector3dc var1, Vector3d var2) {
      var2.x = this.x * var1.x();
      var2.y = this.y * var1.y();
      var2.z = this.z * var1.z();
      return var2;
   }

   public Vector3d div(Vector3d var1) {
      this.x /= var1.x();
      this.y /= var1.y();
      this.z /= var1.z();
      return this;
   }

   public Vector3d div(Vector3fc var1) {
      this.x /= (double)var1.x();
      this.y /= (double)var1.y();
      this.z /= (double)var1.z();
      return this;
   }

   public Vector3d div(Vector3fc var1, Vector3d var2) {
      var2.x = this.x / (double)var1.x();
      var2.y = this.y / (double)var1.y();
      var2.z = this.z / (double)var1.z();
      return var2;
   }

   public Vector3d div(Vector3dc var1, Vector3d var2) {
      var2.x = this.x / var1.x();
      var2.y = this.y / var1.y();
      var2.z = this.z / var1.z();
      return var2;
   }

   public Vector3d mulProject(Matrix4dc var1, Vector3d var2) {
      double var3 = 1.0D / (var1.m03() * this.x + var1.m13() * this.y + var1.m23() * this.z + var1.m33());
      var2.set((var1.m00() * this.x + var1.m10() * this.y + var1.m20() * this.z + var1.m30()) * var3, (var1.m01() * this.x + var1.m11() * this.y + var1.m21() * this.z + var1.m31()) * var3, (var1.m02() * this.x + var1.m12() * this.y + var1.m22() * this.z + var1.m32()) * var3);
      return var2;
   }

   public Vector3d mulProject(Matrix4dc var1) {
      return this.mulProject(var1, this);
   }

   public Vector3d mulProject(Matrix4fc var1, Vector3d var2) {
      double var3 = 1.0D / ((double)var1.m03() * this.x + (double)var1.m13() * this.y + (double)var1.m23() * this.z + (double)var1.m33());
      var2.set(((double)var1.m00() * this.x + (double)var1.m10() * this.y + (double)var1.m20() * this.z + (double)var1.m30()) * var3, ((double)var1.m01() * this.x + (double)var1.m11() * this.y + (double)var1.m21() * this.z + (double)var1.m31()) * var3, ((double)var1.m02() * this.x + (double)var1.m12() * this.y + (double)var1.m22() * this.z + (double)var1.m32()) * var3);
      return var2;
   }

   public Vector3d mulProject(Matrix4fc var1) {
      return this.mulProject(var1, this);
   }

   public Vector3d mul(Matrix3fc var1) {
      return this.mul(var1, this);
   }

   public Vector3d mul(Matrix3dc var1) {
      return this.mul(var1, this);
   }

   public Vector3d mul(Matrix3dc var1, Vector3d var2) {
      var2.set(var1.m00() * this.x + var1.m10() * this.y + var1.m20() * this.z, var1.m01() * this.x + var1.m11() * this.y + var1.m21() * this.z, var1.m02() * this.x + var1.m12() * this.y + var1.m22() * this.z);
      return var2;
   }

   public Vector3d mul(Matrix3fc var1, Vector3d var2) {
      var2.set((double)var1.m00() * this.x + (double)var1.m10() * this.y + (double)var1.m20() * this.z, (double)var1.m01() * this.x + (double)var1.m11() * this.y + (double)var1.m21() * this.z, (double)var1.m02() * this.x + (double)var1.m12() * this.y + (double)var1.m22() * this.z);
      return var2;
   }

   public Vector3d mulTranspose(Matrix3dc var1) {
      return this.mul(var1, this);
   }

   public Vector3d mulTranspose(Matrix3dc var1, Vector3d var2) {
      var2.set(var1.m00() * this.x + var1.m01() * this.y + var1.m02() * this.z, var1.m10() * this.x + var1.m11() * this.y + var1.m12() * this.z, var1.m20() * this.x + var1.m21() * this.y + var1.m22() * this.z);
      return var2;
   }

   public Vector3d mulTranspose(Matrix3fc var1) {
      return this.mul(var1, this);
   }

   public Vector3d mulTranspose(Matrix3fc var1, Vector3d var2) {
      var2.set((double)var1.m00() * this.x + (double)var1.m01() * this.y + (double)var1.m02() * this.z, (double)var1.m10() * this.x + (double)var1.m11() * this.y + (double)var1.m12() * this.z, (double)var1.m20() * this.x + (double)var1.m21() * this.y + (double)var1.m22() * this.z);
      return var2;
   }

   public Vector3d mulPosition(Matrix4fc var1) {
      return this.mulPosition(var1, this);
   }

   public Vector3d mulPosition(Matrix4dc var1) {
      return this.mulPosition(var1, this);
   }

   public Vector3d mulPosition(Matrix4x3dc var1) {
      return this.mulPosition(var1, this);
   }

   public Vector3d mulPosition(Matrix4x3fc var1) {
      return this.mulPosition(var1, this);
   }

   public Vector3d mulPosition(Matrix4dc var1, Vector3d var2) {
      var2.set(var1.m00() * this.x + var1.m10() * this.y + var1.m20() * this.z + var1.m30(), var1.m01() * this.x + var1.m11() * this.y + var1.m21() * this.z + var1.m31(), var1.m02() * this.x + var1.m12() * this.y + var1.m22() * this.z + var1.m32());
      return var2;
   }

   public Vector3d mulPosition(Matrix4fc var1, Vector3d var2) {
      var2.set((double)var1.m00() * this.x + (double)var1.m10() * this.y + (double)var1.m20() * this.z + (double)var1.m30(), (double)var1.m01() * this.x + (double)var1.m11() * this.y + (double)var1.m21() * this.z + (double)var1.m31(), (double)var1.m02() * this.x + (double)var1.m12() * this.y + (double)var1.m22() * this.z + (double)var1.m32());
      return var2;
   }

   public Vector3d mulPosition(Matrix4x3dc var1, Vector3d var2) {
      var2.set(var1.m00() * this.x + var1.m10() * this.y + var1.m20() * this.z + var1.m30(), var1.m01() * this.x + var1.m11() * this.y + var1.m21() * this.z + var1.m31(), var1.m02() * this.x + var1.m12() * this.y + var1.m22() * this.z + var1.m32());
      return var2;
   }

   public Vector3d mulPosition(Matrix4x3fc var1, Vector3d var2) {
      var2.set((double)var1.m00() * this.x + (double)var1.m10() * this.y + (double)var1.m20() * this.z + (double)var1.m30(), (double)var1.m01() * this.x + (double)var1.m11() * this.y + (double)var1.m21() * this.z + (double)var1.m31(), (double)var1.m02() * this.x + (double)var1.m12() * this.y + (double)var1.m22() * this.z + (double)var1.m32());
      return var2;
   }

   public Vector3d mulTransposePosition(Matrix4dc var1) {
      return this.mulTransposePosition(var1, this);
   }

   public Vector3d mulTransposePosition(Matrix4dc var1, Vector3d var2) {
      var2.set(var1.m00() * this.x + var1.m01() * this.y + var1.m02() * this.z + var1.m03(), var1.m10() * this.x + var1.m11() * this.y + var1.m12() * this.z + var1.m13(), var1.m20() * this.x + var1.m21() * this.y + var1.m22() * this.z + var1.m23());
      return var2;
   }

   public Vector3d mulTransposePosition(Matrix4fc var1) {
      return this.mulTransposePosition(var1, this);
   }

   public Vector3d mulTransposePosition(Matrix4fc var1, Vector3d var2) {
      var2.set((double)var1.m00() * this.x + (double)var1.m01() * this.y + (double)var1.m02() * this.z + (double)var1.m03(), (double)var1.m10() * this.x + (double)var1.m11() * this.y + (double)var1.m12() * this.z + (double)var1.m13(), (double)var1.m20() * this.x + (double)var1.m21() * this.y + (double)var1.m22() * this.z + (double)var1.m23());
      return var2;
   }

   public double mulPositionW(Matrix4fc var1) {
      return this.mulPositionW(var1, this);
   }

   public double mulPositionW(Matrix4fc var1, Vector3d var2) {
      double var3 = (double)var1.m03() * this.x + (double)var1.m13() * this.y + (double)var1.m23() * this.z + (double)var1.m33();
      var2.set((double)var1.m00() * this.x + (double)var1.m10() * this.y + (double)var1.m20() * this.z + (double)var1.m30(), (double)var1.m01() * this.x + (double)var1.m11() * this.y + (double)var1.m21() * this.z + (double)var1.m31(), (double)var1.m02() * this.x + (double)var1.m12() * this.y + (double)var1.m22() * this.z + (double)var1.m32());
      return var3;
   }

   public double mulPositionW(Matrix4dc var1) {
      return this.mulPositionW(var1, this);
   }

   public double mulPositionW(Matrix4dc var1, Vector3d var2) {
      double var3 = var1.m03() * this.x + var1.m13() * this.y + var1.m23() * this.z + var1.m33();
      var2.set(var1.m00() * this.x + var1.m10() * this.y + var1.m20() * this.z + var1.m30(), var1.m01() * this.x + var1.m11() * this.y + var1.m21() * this.z + var1.m31(), var1.m02() * this.x + var1.m12() * this.y + var1.m22() * this.z + var1.m32());
      return var3;
   }

   public Vector3d mulDirection(Matrix4fc var1) {
      return this.mulDirection(var1, this);
   }

   public Vector3d mulDirection(Matrix4dc var1) {
      return this.mulDirection(var1, this);
   }

   public Vector3d mulDirection(Matrix4x3dc var1) {
      return this.mulDirection(var1, this);
   }

   public Vector3d mulDirection(Matrix4x3fc var1) {
      return this.mulDirection(var1, this);
   }

   public Vector3d mulDirection(Matrix4dc var1, Vector3d var2) {
      var2.set(var1.m00() * this.x + var1.m10() * this.y + var1.m20() * this.z, var1.m01() * this.x + var1.m11() * this.y + var1.m21() * this.z, var1.m02() * this.x + var1.m12() * this.y + var1.m22() * this.z);
      return var2;
   }

   public Vector3d mulDirection(Matrix4fc var1, Vector3d var2) {
      var2.set((double)var1.m00() * this.x + (double)var1.m10() * this.y + (double)var1.m20() * this.z, (double)var1.m01() * this.x + (double)var1.m11() * this.y + (double)var1.m21() * this.z, (double)var1.m02() * this.x + (double)var1.m12() * this.y + (double)var1.m22() * this.z);
      return var2;
   }

   public Vector3d mulDirection(Matrix4x3dc var1, Vector3d var2) {
      var2.set(var1.m00() * this.x + var1.m10() * this.y + var1.m20() * this.z, var1.m01() * this.x + var1.m11() * this.y + var1.m21() * this.z, var1.m02() * this.x + var1.m12() * this.y + var1.m22() * this.z);
      return var2;
   }

   public Vector3d mulDirection(Matrix4x3fc var1, Vector3d var2) {
      var2.set((double)var1.m00() * this.x + (double)var1.m10() * this.y + (double)var1.m20() * this.z, (double)var1.m01() * this.x + (double)var1.m11() * this.y + (double)var1.m21() * this.z, (double)var1.m02() * this.x + (double)var1.m12() * this.y + (double)var1.m22() * this.z);
      return var2;
   }

   public Vector3d mulTransposeDirection(Matrix4dc var1) {
      return this.mulTransposeDirection(var1, this);
   }

   public Vector3d mulTransposeDirection(Matrix4dc var1, Vector3d var2) {
      var2.set(var1.m00() * this.x + var1.m01() * this.y + var1.m02() * this.z, var1.m10() * this.x + var1.m11() * this.y + var1.m12() * this.z, var1.m20() * this.x + var1.m21() * this.y + var1.m22() * this.z);
      return var2;
   }

   public Vector3d mulTransposeDirection(Matrix4fc var1) {
      return this.mulTransposeDirection(var1, this);
   }

   public Vector3d mulTransposeDirection(Matrix4fc var1, Vector3d var2) {
      var2.set((double)var1.m00() * this.x + (double)var1.m01() * this.y + (double)var1.m02() * this.z, (double)var1.m10() * this.x + (double)var1.m11() * this.y + (double)var1.m12() * this.z, (double)var1.m20() * this.x + (double)var1.m21() * this.y + (double)var1.m22() * this.z);
      return var2;
   }

   public Vector3d mul(double var1) {
      this.x *= var1;
      this.y *= var1;
      this.z *= var1;
      return this;
   }

   public Vector3d mul(double var1, Vector3d var3) {
      var3.x = this.x * var1;
      var3.y = this.y * var1;
      var3.z = this.z * var1;
      return var3;
   }

   public Vector3d mul(double var1, double var3, double var5) {
      this.x *= var1;
      this.y *= var3;
      this.z *= var5;
      return this;
   }

   public Vector3d mul(double var1, double var3, double var5, Vector3d var7) {
      var7.x = this.x * var1;
      var7.y = this.y * var3;
      var7.z = this.z * var5;
      return var7;
   }

   public Vector3d rotate(Quaterniondc var1) {
      var1.transform((Vector3dc)this, (Vector3d)this);
      return this;
   }

   public Vector3d rotate(Quaterniondc var1, Vector3d var2) {
      var1.transform((Vector3dc)this, (Vector3d)var2);
      return var2;
   }

   public Quaterniond rotationTo(Vector3dc var1, Quaterniond var2) {
      return var2.rotationTo(this, var1);
   }

   public Quaterniond rotationTo(double var1, double var3, double var5, Quaterniond var7) {
      return var7.rotationTo(this.x, this.y, this.z, var1, var3, var5);
   }

   public Vector3d div(double var1) {
      this.x /= var1;
      this.y /= var1;
      this.z /= var1;
      return this;
   }

   public Vector3d div(double var1, Vector3d var3) {
      var3.x = this.x / var1;
      var3.y = this.y / var1;
      var3.z = this.z / var1;
      return var3;
   }

   public Vector3d div(double var1, double var3, double var5) {
      this.x /= var1;
      this.y /= var3;
      this.z /= var5;
      return this;
   }

   public Vector3d div(double var1, double var3, double var5, Vector3d var7) {
      var7.x = this.x / var1;
      var7.y = this.y / var3;
      var7.z = this.z / var5;
      return var7;
   }

   public double lengthSquared() {
      return this.x * this.x + this.y * this.y + this.z * this.z;
   }

   public double length() {
      return Math.sqrt(this.lengthSquared());
   }

   public Vector3d normalize() {
      double var1 = 1.0D / this.length();
      this.x *= var1;
      this.y *= var1;
      this.z *= var1;
      return this;
   }

   public Vector3d normalize(Vector3d var1) {
      double var2 = 1.0D / this.length();
      var1.x = this.x * var2;
      var1.y = this.y * var2;
      var1.z = this.z * var2;
      return var1;
   }

   public Vector3d cross(Vector3dc var1) {
      this.set(this.y * var1.z() - this.z * var1.y(), this.z * var1.x() - this.x * var1.z(), this.x * var1.y() - this.y * var1.x());
      return this;
   }

   public Vector3d cross(double var1, double var3, double var5) {
      return this.set(this.y * var5 - this.z * var3, this.z * var1 - this.x * var5, this.x * var3 - this.y * var1);
   }

   public Vector3d cross(Vector3dc var1, Vector3d var2) {
      var2.set(this.y * var1.z() - this.z * var1.y(), this.z * var1.x() - this.x * var1.z(), this.x * var1.y() - this.y * var1.x());
      return var2;
   }

   public Vector3d cross(double var1, double var3, double var5, Vector3d var7) {
      return var7.set(this.y * var5 - this.z * var3, this.z * var1 - this.x * var5, this.x * var3 - this.y * var1);
   }

   public double distance(Vector3dc var1) {
      double var2 = var1.x() - this.x;
      double var4 = var1.y() - this.y;
      double var6 = var1.z() - this.z;
      return Math.sqrt(var2 * var2 + var4 * var4 + var6 * var6);
   }

   public double distance(double var1, double var3, double var5) {
      double var7 = this.x - var1;
      double var9 = this.y - var3;
      double var11 = this.z - var5;
      return Math.sqrt(var7 * var7 + var9 * var9 + var11 * var11);
   }

   public double distanceSquared(Vector3dc var1) {
      double var2 = var1.x() - this.x;
      double var4 = var1.y() - this.y;
      double var6 = var1.z() - this.z;
      return var2 * var2 + var4 * var4 + var6 * var6;
   }

   public double distanceSquared(double var1, double var3, double var5) {
      double var7 = this.x - var1;
      double var9 = this.y - var3;
      double var11 = this.z - var5;
      return var7 * var7 + var9 * var9 + var11 * var11;
   }

   public double dot(Vector3dc var1) {
      return this.x * var1.x() + this.y * var1.y() + this.z * var1.z();
   }

   public double dot(double var1, double var3, double var5) {
      return this.x * var1 + this.y * var3 + this.z * var5;
   }

   public double angleCos(Vector3dc var1) {
      double var2 = this.x * this.x + this.y * this.y + this.z * this.z;
      double var4 = var1.x() * var1.x() + var1.y() * var1.y() + var1.z() * var1.z();
      double var6 = this.x * var1.x() + this.y * var1.y() + this.z * var1.z();
      return var6 / Math.sqrt(var2 * var4);
   }

   public double angle(Vector3dc var1) {
      double var2 = this.angleCos(var1);
      var2 = var2 < 1.0D ? var2 : 1.0D;
      var2 = var2 > -1.0D ? var2 : -1.0D;
      return Math.acos(var2);
   }

   public Vector3d zero() {
      this.x = 0.0D;
      this.y = 0.0D;
      this.z = 0.0D;
      return this;
   }

   public String toString() {
      DecimalFormat var1 = new DecimalFormat(" 0.000E0;-");
      return this.toString(var1).replaceAll("E(\\d+)", "E+$1");
   }

   public String toString(NumberFormat var1) {
      return "(" + var1.format(this.x) + " " + var1.format(this.y) + " " + var1.format(this.z) + ")";
   }

   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeDouble(this.x);
      var1.writeDouble(this.y);
      var1.writeDouble(this.z);
   }

   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      this.x = var1.readDouble();
      this.y = var1.readDouble();
      this.z = var1.readDouble();
   }

   public Vector3d negate() {
      this.x = -this.x;
      this.y = -this.y;
      this.z = -this.z;
      return this;
   }

   public Vector3d negate(Vector3d var1) {
      var1.x = -this.x;
      var1.y = -this.y;
      var1.z = -this.z;
      return var1;
   }

   public int hashCode() {
      byte var2 = 1;
      long var3 = Double.doubleToLongBits(this.x);
      int var5 = 31 * var2 + (int)(var3 ^ var3 >>> 32);
      var3 = Double.doubleToLongBits(this.y);
      var5 = 31 * var5 + (int)(var3 ^ var3 >>> 32);
      var3 = Double.doubleToLongBits(this.z);
      var5 = 31 * var5 + (int)(var3 ^ var3 >>> 32);
      return var5;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         Vector3d var2 = (Vector3d)var1;
         if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(var2.x)) {
            return false;
         } else if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(var2.y)) {
            return false;
         } else {
            return Double.doubleToLongBits(this.z) == Double.doubleToLongBits(var2.z);
         }
      }
   }

   public Vector3d reflect(Vector3dc var1) {
      double var2 = this.dot(var1);
      this.x -= (var2 + var2) * var1.x();
      this.y -= (var2 + var2) * var1.y();
      this.z -= (var2 + var2) * var1.z();
      return this;
   }

   public Vector3d reflect(double var1, double var3, double var5) {
      double var7 = this.dot(var1, var3, var5);
      this.x -= (var7 + var7) * var1;
      this.y -= (var7 + var7) * var3;
      this.z -= (var7 + var7) * var5;
      return this;
   }

   public Vector3d reflect(Vector3dc var1, Vector3d var2) {
      double var3 = this.dot(var1);
      var2.x = this.x - (var3 + var3) * var1.x();
      var2.y = this.y - (var3 + var3) * var1.y();
      var2.z = this.z - (var3 + var3) * var1.z();
      return var2;
   }

   public Vector3d reflect(double var1, double var3, double var5, Vector3d var7) {
      double var8 = this.dot(var1, var3, var5);
      var7.x = this.x - (var8 + var8) * var1;
      var7.y = this.y - (var8 + var8) * var3;
      var7.z = this.z - (var8 + var8) * var5;
      return var7;
   }

   public Vector3d half(Vector3dc var1) {
      return this.add(var1).normalize();
   }

   public Vector3d half(double var1, double var3, double var5) {
      return this.add(var1, var3, var5).normalize();
   }

   public Vector3d half(Vector3dc var1, Vector3d var2) {
      return var2.set((Vector3dc)this).add(var1).normalize();
   }

   public Vector3d half(double var1, double var3, double var5, Vector3d var7) {
      return var7.set((Vector3dc)this).add(var1, var3, var5).normalize();
   }

   public Vector3d smoothStep(Vector3dc var1, double var2, Vector3d var4) {
      double var5 = var2 * var2;
      double var7 = var5 * var2;
      var4.x = (this.x + this.x - var1.x() - var1.x()) * var7 + (3.0D * var1.x() - 3.0D * this.x) * var5 + this.x * var2 + this.x;
      var4.y = (this.y + this.y - var1.y() - var1.y()) * var7 + (3.0D * var1.y() - 3.0D * this.y) * var5 + this.y * var2 + this.y;
      var4.z = (this.z + this.z - var1.z() - var1.z()) * var7 + (3.0D * var1.z() - 3.0D * this.z) * var5 + this.z * var2 + this.z;
      return var4;
   }

   public Vector3d hermite(Vector3dc var1, Vector3dc var2, Vector3dc var3, double var4, Vector3d var6) {
      double var7 = var4 * var4;
      double var9 = var7 * var4;
      var6.x = (this.x + this.x - var2.x() - var2.x() + var3.x() + var1.x()) * var9 + (3.0D * var2.x() - 3.0D * this.x - var1.x() - var1.x() - var3.x()) * var7 + this.x * var4 + this.x;
      var6.y = (this.y + this.y - var2.y() - var2.y() + var3.y() + var1.y()) * var9 + (3.0D * var2.y() - 3.0D * this.y - var1.y() - var1.y() - var3.y()) * var7 + this.y * var4 + this.y;
      var6.z = (this.z + this.z - var2.z() - var2.z() + var3.z() + var1.z()) * var9 + (3.0D * var2.z() - 3.0D * this.z - var1.z() - var1.z() - var3.z()) * var7 + this.z * var4 + this.z;
      return var6;
   }

   public Vector3d lerp(Vector3dc var1, double var2) {
      return this.lerp(var1, var2, this);
   }

   public Vector3d lerp(Vector3dc var1, double var2, Vector3d var4) {
      var4.x = this.x + (var1.x() - this.x) * var2;
      var4.y = this.y + (var1.y() - this.y) * var2;
      var4.z = this.z + (var1.z() - this.z) * var2;
      return var4;
   }

   public double get(int var1) throws IllegalArgumentException {
      switch(var1) {
      case 0:
         return this.x;
      case 1:
         return this.y;
      case 2:
         return this.z;
      default:
         throw new IllegalArgumentException();
      }
   }

   public int maxComponent() {
      double var1 = Math.abs(this.x);
      double var3 = Math.abs(this.y);
      double var5 = Math.abs(this.z);
      if (var1 >= var3 && var1 >= var5) {
         return 0;
      } else {
         return var3 >= var5 ? 1 : 2;
      }
   }

   public int minComponent() {
      double var1 = Math.abs(this.x);
      double var3 = Math.abs(this.y);
      double var5 = Math.abs(this.z);
      if (var1 < var3 && var1 < var5) {
         return 0;
      } else {
         return var3 < var5 ? 1 : 2;
      }
   }

   public Vector3d orthogonalize(Vector3dc var1, Vector3d var2) {
      double var3 = 1.0D / Math.sqrt(var1.x() * var1.x() + var1.y() * var1.y() + var1.z() * var1.z());
      double var5 = var1.x() * var3;
      double var7 = var1.y() * var3;
      double var9 = var1.z() * var3;
      double var11 = var5 * this.x + var7 * this.y + var9 * this.z;
      double var13 = this.x - var11 * var5;
      double var15 = this.y - var11 * var7;
      double var17 = this.z - var11 * var9;
      double var19 = 1.0D / Math.sqrt(var13 * var13 + var15 * var15 + var17 * var17);
      var2.x = var13 * var19;
      var2.y = var15 * var19;
      var2.z = var17 * var19;
      return var2;
   }

   public Vector3d orthogonalize(Vector3dc var1) {
      return this.orthogonalize(var1, this);
   }

   public Vector3d orthogonalizeUnit(Vector3dc var1, Vector3d var2) {
      double var3 = var1.x();
      double var5 = var1.y();
      double var7 = var1.z();
      double var9 = var3 * this.x + var5 * this.y + var7 * this.z;
      double var11 = this.x - var9 * var3;
      double var13 = this.y - var9 * var5;
      double var15 = this.z - var9 * var7;
      double var17 = 1.0D / Math.sqrt(var11 * var11 + var13 * var13 + var15 * var15);
      var2.x = var11 * var17;
      var2.y = var13 * var17;
      var2.z = var15 * var17;
      return var2;
   }

   public Vector3d orthogonalizeUnit(Vector3dc var1) {
      return this.orthogonalizeUnit(var1, this);
   }

   public Vector3dc toImmutable() {
      return (Vector3dc)(!Options.DEBUG ? this : new Vector3d.Proxy(this));
   }

   private final class Proxy implements Vector3dc {
      private final Vector3dc delegate;

      Proxy(Vector3dc var2) {
         this.delegate = var2;
      }

      public double x() {
         return this.delegate.x();
      }

      public double y() {
         return this.delegate.y();
      }

      public double z() {
         return this.delegate.z();
      }

      public ByteBuffer get(ByteBuffer var1) {
         return this.delegate.get(var1);
      }

      public ByteBuffer get(int var1, ByteBuffer var2) {
         return this.delegate.get(var1, var2);
      }

      public DoubleBuffer get(DoubleBuffer var1) {
         return this.delegate.get(var1);
      }

      public DoubleBuffer get(int var1, DoubleBuffer var2) {
         return this.delegate.get(var1, var2);
      }

      public Vector3d sub(Vector3dc var1, Vector3d var2) {
         return this.delegate.sub(var1, var2);
      }

      public Vector3d sub(Vector3fc var1, Vector3d var2) {
         return this.delegate.sub(var1, var2);
      }

      public Vector3d sub(double var1, double var3, double var5, Vector3d var7) {
         return this.delegate.sub(var1, var3, var5, var7);
      }

      public Vector3d add(Vector3dc var1, Vector3d var2) {
         return this.delegate.add(var1, var2);
      }

      public Vector3d add(Vector3fc var1, Vector3d var2) {
         return this.delegate.add(var1, var2);
      }

      public Vector3d add(double var1, double var3, double var5, Vector3d var7) {
         return this.delegate.add(var1, var3, var5, var7);
      }

      public Vector3d fma(Vector3dc var1, Vector3dc var2, Vector3d var3) {
         return this.delegate.fma(var1, var2, var3);
      }

      public Vector3d fma(double var1, Vector3dc var3, Vector3d var4) {
         return this.delegate.fma(var1, var3, var4);
      }

      public Vector3d fma(Vector3dc var1, Vector3fc var2, Vector3d var3) {
         return this.delegate.fma(var1, var2, var3);
      }

      public Vector3d fma(double var1, Vector3fc var3, Vector3d var4) {
         return this.delegate.fma(var1, var3, var4);
      }

      public Vector3d mul(Vector3fc var1, Vector3d var2) {
         return this.delegate.mul(var1, var2);
      }

      public Vector3d mul(Vector3dc var1, Vector3d var2) {
         return this.delegate.mul(var1, var2);
      }

      public Vector3d div(Vector3fc var1, Vector3d var2) {
         return this.delegate.div(var1, var2);
      }

      public Vector3d div(Vector3dc var1, Vector3d var2) {
         return this.delegate.div(var1, var2);
      }

      public Vector3d mulProject(Matrix4dc var1, Vector3d var2) {
         return this.delegate.mulProject(var1, var2);
      }

      public Vector3d mulProject(Matrix4fc var1, Vector3d var2) {
         return this.delegate.mulProject(var1, var2);
      }

      public Vector3d mul(Matrix3dc var1, Vector3d var2) {
         return this.delegate.mul(var1, var2);
      }

      public Vector3d mul(Matrix3fc var1, Vector3d var2) {
         return this.delegate.mul(var1, var2);
      }

      public Vector3d mulTranspose(Matrix3dc var1, Vector3d var2) {
         return this.delegate.mulTranspose(var1, var2);
      }

      public Vector3d mulTranspose(Matrix3fc var1, Vector3d var2) {
         return this.delegate.mulTranspose(var1, var2);
      }

      public Vector3d mulPosition(Matrix4dc var1, Vector3d var2) {
         return this.delegate.mulPosition(var1, var2);
      }

      public Vector3d mulPosition(Matrix4fc var1, Vector3d var2) {
         return this.delegate.mulPosition(var1, var2);
      }

      public Vector3d mulPosition(Matrix4x3dc var1, Vector3d var2) {
         return this.delegate.mulPosition(var1, var2);
      }

      public Vector3d mulPosition(Matrix4x3fc var1, Vector3d var2) {
         return this.delegate.mulPosition(var1, var2);
      }

      public Vector3d mulTransposePosition(Matrix4dc var1, Vector3d var2) {
         return this.delegate.mulTransposePosition(var1, var2);
      }

      public Vector3d mulTransposePosition(Matrix4fc var1, Vector3d var2) {
         return this.delegate.mulTransposePosition(var1, var2);
      }

      public double mulPositionW(Matrix4fc var1, Vector3d var2) {
         return this.delegate.mulPositionW(var1, var2);
      }

      public double mulPositionW(Matrix4dc var1, Vector3d var2) {
         return this.delegate.mulPositionW(var1, var2);
      }

      public Vector3d mulDirection(Matrix4dc var1, Vector3d var2) {
         return this.delegate.mulDirection(var1, var2);
      }

      public Vector3d mulDirection(Matrix4fc var1, Vector3d var2) {
         return this.delegate.mulDirection(var1, var2);
      }

      public Vector3d mulDirection(Matrix4x3dc var1, Vector3d var2) {
         return this.delegate.mulDirection(var1, var2);
      }

      public Vector3d mulDirection(Matrix4x3fc var1, Vector3d var2) {
         return this.delegate.mulDirection(var1, var2);
      }

      public Vector3d mulTransposeDirection(Matrix4dc var1, Vector3d var2) {
         return this.delegate.mulTransposeDirection(var1, var2);
      }

      public Vector3d mulTransposeDirection(Matrix4fc var1, Vector3d var2) {
         return this.delegate.mulTransposeDirection(var1, var2);
      }

      public Vector3d mul(double var1, Vector3d var3) {
         return this.delegate.mul(var1, var3);
      }

      public Vector3d mul(double var1, double var3, double var5, Vector3d var7) {
         return this.delegate.mul(var1, var3, var5, var7);
      }

      public Vector3d rotate(Quaterniondc var1, Vector3d var2) {
         return this.delegate.rotate(var1, var2);
      }

      public Vector3d div(double var1, Vector3d var3) {
         return this.delegate.div(var1, var3);
      }

      public Vector3d div(double var1, double var3, double var5, Vector3d var7) {
         return this.delegate.div(var1, var3, var5, var7);
      }

      public double lengthSquared() {
         return this.delegate.lengthSquared();
      }

      public double length() {
         return this.delegate.length();
      }

      public Vector3d normalize(Vector3d var1) {
         return this.delegate.normalize(var1);
      }

      public Vector3d cross(Vector3dc var1, Vector3d var2) {
         return this.delegate.cross(var1, var2);
      }

      public Vector3d cross(double var1, double var3, double var5, Vector3d var7) {
         return this.delegate.cross(var1, var3, var5, var7);
      }

      public double distance(Vector3dc var1) {
         return this.delegate.distance(var1);
      }

      public double distance(double var1, double var3, double var5) {
         return this.delegate.distance(var1, var3, var5);
      }

      public double distanceSquared(Vector3dc var1) {
         return this.delegate.distanceSquared(var1);
      }

      public double distanceSquared(double var1, double var3, double var5) {
         return this.delegate.distanceSquared(var1, var3, var5);
      }

      public double dot(Vector3dc var1) {
         return this.delegate.dot(var1);
      }

      public double dot(double var1, double var3, double var5) {
         return this.delegate.dot(var1, var3, var5);
      }

      public double angleCos(Vector3dc var1) {
         return this.delegate.angleCos(var1);
      }

      public double angle(Vector3dc var1) {
         return this.delegate.angle(var1);
      }

      public Vector3d negate(Vector3d var1) {
         return this.delegate.negate(var1);
      }

      public Vector3d reflect(Vector3dc var1, Vector3d var2) {
         return this.delegate.reflect(var1, var2);
      }

      public Vector3d reflect(double var1, double var3, double var5, Vector3d var7) {
         return this.delegate.reflect(var1, var3, var5, var7);
      }

      public Vector3d half(Vector3dc var1, Vector3d var2) {
         return this.delegate.half(var1, var2);
      }

      public Vector3d half(double var1, double var3, double var5, Vector3d var7) {
         return this.delegate.half(var1, var3, var5, var7);
      }

      public Vector3d smoothStep(Vector3dc var1, double var2, Vector3d var4) {
         return this.delegate.smoothStep(var1, var2, var4);
      }

      public Vector3d hermite(Vector3dc var1, Vector3dc var2, Vector3dc var3, double var4, Vector3d var6) {
         return this.delegate.hermite(var1, var2, var3, var4, var6);
      }

      public Vector3d lerp(Vector3dc var1, double var2, Vector3d var4) {
         return this.delegate.lerp(var1, var2, var4);
      }

      public double get(int var1) throws IllegalArgumentException {
         return this.delegate.get(var1);
      }

      public int maxComponent() {
         return this.delegate.maxComponent();
      }

      public int minComponent() {
         return this.delegate.minComponent();
      }

      public Vector3d orthogonalize(Vector3dc var1, Vector3d var2) {
         return this.delegate.orthogonalize(var1, var2);
      }

      public Vector3d orthogonalizeUnit(Vector3dc var1, Vector3d var2) {
         return this.delegate.orthogonalizeUnit(var1, var2);
      }
   }
}
