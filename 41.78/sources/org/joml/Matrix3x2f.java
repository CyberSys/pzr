package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;


public class Matrix3x2f implements Matrix3x2fc,Externalizable {
	private static final long serialVersionUID = 1L;
	public float m00;
	public float m01;
	public float m10;
	public float m11;
	public float m20;
	public float m21;

	public Matrix3x2f() {
		this.m00 = 1.0F;
		this.m11 = 1.0F;
	}

	public Matrix3x2f(Matrix3x2fc matrix3x2fc) {
		if (matrix3x2fc instanceof Matrix3x2f) {
			MemUtil.INSTANCE.copy((Matrix3x2f)matrix3x2fc, this);
		} else {
			this.setMatrix3x2fc(matrix3x2fc);
		}
	}

	public Matrix3x2f(Matrix2fc matrix2fc) {
		if (matrix2fc instanceof Matrix2f) {
			MemUtil.INSTANCE.copy((Matrix2f)matrix2fc, this);
		} else {
			this.setMatrix2fc(matrix2fc);
		}
	}

	public Matrix3x2f(float float1, float float2, float float3, float float4, float float5, float float6) {
		this.m00 = float1;
		this.m01 = float2;
		this.m10 = float3;
		this.m11 = float4;
		this.m20 = float5;
		this.m21 = float6;
	}

	public Matrix3x2f(FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.get(this, floatBuffer.position(), floatBuffer);
	}

	public float m00() {
		return this.m00;
	}

	public float m01() {
		return this.m01;
	}

	public float m10() {
		return this.m10;
	}

	public float m11() {
		return this.m11;
	}

	public float m20() {
		return this.m20;
	}

	public float m21() {
		return this.m21;
	}

	Matrix3x2f _m00(float float1) {
		this.m00 = float1;
		return this;
	}

	Matrix3x2f _m01(float float1) {
		this.m01 = float1;
		return this;
	}

	Matrix3x2f _m10(float float1) {
		this.m10 = float1;
		return this;
	}

	Matrix3x2f _m11(float float1) {
		this.m11 = float1;
		return this;
	}

	Matrix3x2f _m20(float float1) {
		this.m20 = float1;
		return this;
	}

	Matrix3x2f _m21(float float1) {
		this.m21 = float1;
		return this;
	}

	public Matrix3x2f set(Matrix3x2fc matrix3x2fc) {
		if (matrix3x2fc instanceof Matrix3x2f) {
			MemUtil.INSTANCE.copy((Matrix3x2f)matrix3x2fc, this);
		} else {
			this.setMatrix3x2fc(matrix3x2fc);
		}

		return this;
	}

	private void setMatrix3x2fc(Matrix3x2fc matrix3x2fc) {
		this.m00 = matrix3x2fc.m00();
		this.m01 = matrix3x2fc.m01();
		this.m10 = matrix3x2fc.m10();
		this.m11 = matrix3x2fc.m11();
		this.m20 = matrix3x2fc.m20();
		this.m21 = matrix3x2fc.m21();
	}

	public Matrix3x2f set(Matrix2fc matrix2fc) {
		if (matrix2fc instanceof Matrix2f) {
			MemUtil.INSTANCE.copy((Matrix2f)matrix2fc, this);
		} else {
			this.setMatrix2fc(matrix2fc);
		}

		return this;
	}

	private void setMatrix2fc(Matrix2fc matrix2fc) {
		this.m00 = matrix2fc.m00();
		this.m01 = matrix2fc.m01();
		this.m10 = matrix2fc.m10();
		this.m11 = matrix2fc.m11();
	}

	public Matrix3x2f mul(Matrix3x2fc matrix3x2fc) {
		return this.mul(matrix3x2fc, this);
	}

	public Matrix3x2f mul(Matrix3x2fc matrix3x2fc, Matrix3x2f matrix3x2f) {
		float float1 = this.m00 * matrix3x2fc.m00() + this.m10 * matrix3x2fc.m01();
		float float2 = this.m01 * matrix3x2fc.m00() + this.m11 * matrix3x2fc.m01();
		float float3 = this.m00 * matrix3x2fc.m10() + this.m10 * matrix3x2fc.m11();
		float float4 = this.m01 * matrix3x2fc.m10() + this.m11 * matrix3x2fc.m11();
		float float5 = this.m00 * matrix3x2fc.m20() + this.m10 * matrix3x2fc.m21() + this.m20;
		float float6 = this.m01 * matrix3x2fc.m20() + this.m11 * matrix3x2fc.m21() + this.m21;
		matrix3x2f.m00 = float1;
		matrix3x2f.m01 = float2;
		matrix3x2f.m10 = float3;
		matrix3x2f.m11 = float4;
		matrix3x2f.m20 = float5;
		matrix3x2f.m21 = float6;
		return matrix3x2f;
	}

	public Matrix3x2f mulLocal(Matrix3x2fc matrix3x2fc) {
		return this.mulLocal(matrix3x2fc, this);
	}

	public Matrix3x2f mulLocal(Matrix3x2fc matrix3x2fc, Matrix3x2f matrix3x2f) {
		float float1 = matrix3x2fc.m00() * this.m00 + matrix3x2fc.m10() * this.m01;
		float float2 = matrix3x2fc.m01() * this.m00 + matrix3x2fc.m11() * this.m01;
		float float3 = matrix3x2fc.m00() * this.m10 + matrix3x2fc.m10() * this.m11;
		float float4 = matrix3x2fc.m01() * this.m10 + matrix3x2fc.m11() * this.m11;
		float float5 = matrix3x2fc.m00() * this.m20 + matrix3x2fc.m10() * this.m21 + matrix3x2fc.m20();
		float float6 = matrix3x2fc.m01() * this.m20 + matrix3x2fc.m11() * this.m21 + matrix3x2fc.m21();
		matrix3x2f.m00 = float1;
		matrix3x2f.m01 = float2;
		matrix3x2f.m10 = float3;
		matrix3x2f.m11 = float4;
		matrix3x2f.m20 = float5;
		matrix3x2f.m21 = float6;
		return matrix3x2f;
	}

	public Matrix3x2f set(float float1, float float2, float float3, float float4, float float5, float float6) {
		this.m00 = float1;
		this.m01 = float2;
		this.m10 = float3;
		this.m11 = float4;
		this.m20 = float5;
		this.m21 = float6;
		return this;
	}

	public Matrix3x2f set(float[] floatArray) {
		MemUtil.INSTANCE.copy((float[])floatArray, 0, (Matrix3x2f)this);
		return this;
	}

	public float determinant() {
		return this.m00 * this.m11 - this.m01 * this.m10;
	}

	public Matrix3x2f invert() {
		return this.invert(this);
	}

	public Matrix3x2f invert(Matrix3x2f matrix3x2f) {
		float float1 = 1.0F / (this.m00 * this.m11 - this.m01 * this.m10);
		float float2 = this.m11 * float1;
		float float3 = -this.m01 * float1;
		float float4 = -this.m10 * float1;
		float float5 = this.m00 * float1;
		float float6 = (this.m10 * this.m21 - this.m20 * this.m11) * float1;
		float float7 = (this.m20 * this.m01 - this.m00 * this.m21) * float1;
		matrix3x2f.m00 = float2;
		matrix3x2f.m01 = float3;
		matrix3x2f.m10 = float4;
		matrix3x2f.m11 = float5;
		matrix3x2f.m20 = float6;
		matrix3x2f.m21 = float7;
		return matrix3x2f;
	}

	public Matrix3x2f translation(float float1, float float2) {
		this.m00 = 1.0F;
		this.m01 = 0.0F;
		this.m10 = 0.0F;
		this.m11 = 1.0F;
		this.m20 = float1;
		this.m21 = float2;
		return this;
	}

	public Matrix3x2f translation(Vector2fc vector2fc) {
		return this.translation(vector2fc.x(), vector2fc.y());
	}

	public Matrix3x2f setTranslation(float float1, float float2) {
		this.m20 = float1;
		this.m21 = float2;
		return this;
	}

	public Matrix3x2f setTranslation(Vector2f vector2f) {
		return this.setTranslation(vector2f.x, vector2f.y);
	}

	public Matrix3x2f translate(float float1, float float2, Matrix3x2f matrix3x2f) {
		matrix3x2f.m20 = this.m00 * float1 + this.m10 * float2 + this.m20;
		matrix3x2f.m21 = this.m01 * float1 + this.m11 * float2 + this.m21;
		matrix3x2f.m00 = this.m00;
		matrix3x2f.m01 = this.m01;
		matrix3x2f.m10 = this.m10;
		matrix3x2f.m11 = this.m11;
		return matrix3x2f;
	}

	public Matrix3x2f translate(float float1, float float2) {
		return this.translate(float1, float2, this);
	}

	public Matrix3x2f translate(Vector2fc vector2fc, Matrix3x2f matrix3x2f) {
		return this.translate(vector2fc.x(), vector2fc.y(), matrix3x2f);
	}

	public Matrix3x2f translate(Vector2fc vector2fc) {
		return this.translate(vector2fc.x(), vector2fc.y(), this);
	}

	public Matrix3x2f translateLocal(Vector2fc vector2fc) {
		return this.translateLocal(vector2fc.x(), vector2fc.y());
	}

	public Matrix3x2f translateLocal(Vector2fc vector2fc, Matrix3x2f matrix3x2f) {
		return this.translateLocal(vector2fc.x(), vector2fc.y(), matrix3x2f);
	}

	public Matrix3x2f translateLocal(float float1, float float2, Matrix3x2f matrix3x2f) {
		matrix3x2f.m00 = this.m00;
		matrix3x2f.m01 = this.m01;
		matrix3x2f.m10 = this.m10;
		matrix3x2f.m11 = this.m11;
		matrix3x2f.m20 = this.m20 + float1;
		matrix3x2f.m21 = this.m21 + float2;
		return matrix3x2f;
	}

	public Matrix3x2f translateLocal(float float1, float float2) {
		return this.translateLocal(float1, float2, this);
	}

	public String toString() {
		DecimalFormat decimalFormat = new DecimalFormat(" 0.000E0;-");
		String string = this.toString(decimalFormat);
		StringBuffer stringBuffer = new StringBuffer();
		int int1 = Integer.MIN_VALUE;
		for (int int2 = 0; int2 < string.length(); ++int2) {
			char char1 = string.charAt(int2);
			if (char1 == 'E') {
				int1 = int2;
			} else {
				if (char1 == ' ' && int1 == int2 - 1) {
					stringBuffer.append('+');
					continue;
				}

				if (Character.isDigit(char1) && int1 == int2 - 1) {
					stringBuffer.append('+');
				}
			}

			stringBuffer.append(char1);
		}

		return stringBuffer.toString();
	}

	public String toString(NumberFormat numberFormat) {
		String string = Runtime.format((double)this.m00, numberFormat);
		return string + " " + Runtime.format((double)this.m10, numberFormat) + " " + Runtime.format((double)this.m20, numberFormat) + "\n" + Runtime.format((double)this.m01, numberFormat) + " " + Runtime.format((double)this.m11, numberFormat) + " " + Runtime.format((double)this.m21, numberFormat) + "\n";
	}

	public Matrix3x2f get(Matrix3x2f matrix3x2f) {
		return matrix3x2f.set((Matrix3x2fc)this);
	}

	public FloatBuffer get(FloatBuffer floatBuffer) {
		return this.get(floatBuffer.position(), floatBuffer);
	}

	public FloatBuffer get(int int1, FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.put(this, int1, floatBuffer);
		return floatBuffer;
	}

	public ByteBuffer get(ByteBuffer byteBuffer) {
		return this.get(byteBuffer.position(), byteBuffer);
	}

	public ByteBuffer get(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put(this, int1, byteBuffer);
		return byteBuffer;
	}

	public FloatBuffer get3x3(FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.put3x3((Matrix3x2f)this, 0, (FloatBuffer)floatBuffer);
		return floatBuffer;
	}

	public FloatBuffer get3x3(int int1, FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.put3x3(this, int1, floatBuffer);
		return floatBuffer;
	}

	public ByteBuffer get3x3(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put3x3((Matrix3x2f)this, 0, (ByteBuffer)byteBuffer);
		return byteBuffer;
	}

	public ByteBuffer get3x3(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put3x3(this, int1, byteBuffer);
		return byteBuffer;
	}

	public FloatBuffer get4x4(FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.put4x4((Matrix3x2f)this, 0, (FloatBuffer)floatBuffer);
		return floatBuffer;
	}

	public FloatBuffer get4x4(int int1, FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.put4x4(this, int1, floatBuffer);
		return floatBuffer;
	}

	public ByteBuffer get4x4(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put4x4((Matrix3x2f)this, 0, (ByteBuffer)byteBuffer);
		return byteBuffer;
	}

	public ByteBuffer get4x4(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put4x4(this, int1, byteBuffer);
		return byteBuffer;
	}

	public Matrix3x2fc getToAddress(long long1) {
		if (Options.NO_UNSAFE) {
			throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
		} else {
			MemUtil.MemUtilUnsafe.put(this, long1);
			return this;
		}
	}

	public float[] get(float[] floatArray, int int1) {
		MemUtil.INSTANCE.copy(this, floatArray, int1);
		return floatArray;
	}

	public float[] get(float[] floatArray) {
		return this.get(floatArray, 0);
	}

	public float[] get3x3(float[] floatArray, int int1) {
		MemUtil.INSTANCE.copy3x3(this, floatArray, int1);
		return floatArray;
	}

	public float[] get3x3(float[] floatArray) {
		return this.get3x3(floatArray, 0);
	}

	public float[] get4x4(float[] floatArray, int int1) {
		MemUtil.INSTANCE.copy4x4(this, floatArray, int1);
		return floatArray;
	}

	public float[] get4x4(float[] floatArray) {
		return this.get4x4(floatArray, 0);
	}

	public Matrix3x2f set(FloatBuffer floatBuffer) {
		int int1 = floatBuffer.position();
		MemUtil.INSTANCE.get(this, int1, floatBuffer);
		return this;
	}

	public Matrix3x2f set(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.position();
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
		return this;
	}

	public Matrix3x2f setFromAddress(long long1) {
		if (Options.NO_UNSAFE) {
			throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
		} else {
			MemUtil.MemUtilUnsafe.get(this, long1);
			return this;
		}
	}

	public Matrix3x2f zero() {
		MemUtil.INSTANCE.zero(this);
		return this;
	}

	public Matrix3x2f identity() {
		MemUtil.INSTANCE.identity(this);
		return this;
	}

	public Matrix3x2f scale(float float1, float float2, Matrix3x2f matrix3x2f) {
		matrix3x2f.m00 = this.m00 * float1;
		matrix3x2f.m01 = this.m01 * float1;
		matrix3x2f.m10 = this.m10 * float2;
		matrix3x2f.m11 = this.m11 * float2;
		matrix3x2f.m20 = this.m20;
		matrix3x2f.m21 = this.m21;
		return matrix3x2f;
	}

	public Matrix3x2f scale(float float1, float float2) {
		return this.scale(float1, float2, this);
	}

	public Matrix3x2f scale(Vector2fc vector2fc) {
		return this.scale(vector2fc.x(), vector2fc.y(), this);
	}

	public Matrix3x2f scale(Vector2fc vector2fc, Matrix3x2f matrix3x2f) {
		return this.scale(vector2fc.x(), vector2fc.y(), matrix3x2f);
	}

	public Matrix3x2f scale(float float1, Matrix3x2f matrix3x2f) {
		return this.scale(float1, float1, matrix3x2f);
	}

	public Matrix3x2f scale(float float1) {
		return this.scale(float1, float1);
	}

	public Matrix3x2f scaleLocal(float float1, float float2, Matrix3x2f matrix3x2f) {
		matrix3x2f.m00 = float1 * this.m00;
		matrix3x2f.m01 = float2 * this.m01;
		matrix3x2f.m10 = float1 * this.m10;
		matrix3x2f.m11 = float2 * this.m11;
		matrix3x2f.m20 = float1 * this.m20;
		matrix3x2f.m21 = float2 * this.m21;
		return matrix3x2f;
	}

	public Matrix3x2f scaleLocal(float float1, float float2) {
		return this.scaleLocal(float1, float2, this);
	}

	public Matrix3x2f scaleLocal(float float1, Matrix3x2f matrix3x2f) {
		return this.scaleLocal(float1, float1, matrix3x2f);
	}

	public Matrix3x2f scaleLocal(float float1) {
		return this.scaleLocal(float1, float1, this);
	}

	public Matrix3x2f scaleAround(float float1, float float2, float float3, float float4, Matrix3x2f matrix3x2f) {
		float float5 = this.m00 * float3 + this.m10 * float4 + this.m20;
		float float6 = this.m01 * float3 + this.m11 * float4 + this.m21;
		matrix3x2f.m00 = this.m00 * float1;
		matrix3x2f.m01 = this.m01 * float1;
		matrix3x2f.m10 = this.m10 * float2;
		matrix3x2f.m11 = this.m11 * float2;
		matrix3x2f.m20 = matrix3x2f.m00 * -float3 + matrix3x2f.m10 * -float4 + float5;
		matrix3x2f.m21 = matrix3x2f.m01 * -float3 + matrix3x2f.m11 * -float4 + float6;
		return matrix3x2f;
	}

	public Matrix3x2f scaleAround(float float1, float float2, float float3, float float4) {
		return this.scaleAround(float1, float2, float3, float4, this);
	}

	public Matrix3x2f scaleAround(float float1, float float2, float float3, Matrix3x2f matrix3x2f) {
		return this.scaleAround(float1, float1, float2, float3, this);
	}

	public Matrix3x2f scaleAround(float float1, float float2, float float3) {
		return this.scaleAround(float1, float1, float2, float3, this);
	}

	public Matrix3x2f scaleAroundLocal(float float1, float float2, float float3, float float4, Matrix3x2f matrix3x2f) {
		matrix3x2f.m00 = float1 * this.m00;
		matrix3x2f.m01 = float2 * this.m01;
		matrix3x2f.m10 = float1 * this.m10;
		matrix3x2f.m11 = float2 * this.m11;
		matrix3x2f.m20 = float1 * this.m20 - float1 * float3 + float3;
		matrix3x2f.m21 = float2 * this.m21 - float2 * float4 + float4;
		return matrix3x2f;
	}

	public Matrix3x2f scaleAroundLocal(float float1, float float2, float float3, Matrix3x2f matrix3x2f) {
		return this.scaleAroundLocal(float1, float1, float2, float3, matrix3x2f);
	}

	public Matrix3x2f scaleAroundLocal(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.scaleAroundLocal(float1, float2, float4, float5, this);
	}

	public Matrix3x2f scaleAroundLocal(float float1, float float2, float float3) {
		return this.scaleAroundLocal(float1, float1, float2, float3, this);
	}

	public Matrix3x2f scaling(float float1) {
		return this.scaling(float1, float1);
	}

	public Matrix3x2f scaling(float float1, float float2) {
		this.m00 = float1;
		this.m01 = 0.0F;
		this.m10 = 0.0F;
		this.m11 = float2;
		this.m20 = 0.0F;
		this.m21 = 0.0F;
		return this;
	}

	public Matrix3x2f rotation(float float1) {
		float float2 = Math.cos(float1);
		float float3 = Math.sin(float1);
		this.m00 = float2;
		this.m10 = -float3;
		this.m20 = 0.0F;
		this.m01 = float3;
		this.m11 = float2;
		this.m21 = 0.0F;
		return this;
	}

	public Vector3f transform(Vector3f vector3f) {
		return vector3f.mul((Matrix3x2fc)this);
	}

	public Vector3f transform(Vector3f vector3f, Vector3f vector3f2) {
		return vector3f.mul((Matrix3x2fc)this, vector3f2);
	}

	public Vector3f transform(float float1, float float2, float float3, Vector3f vector3f) {
		return vector3f.set(this.m00 * float1 + this.m10 * float2 + this.m20 * float3, this.m01 * float1 + this.m11 * float2 + this.m21 * float3, float3);
	}

	public Vector2f transformPosition(Vector2f vector2f) {
		vector2f.set(this.m00 * vector2f.x + this.m10 * vector2f.y + this.m20, this.m01 * vector2f.x + this.m11 * vector2f.y + this.m21);
		return vector2f;
	}

	public Vector2f transformPosition(Vector2fc vector2fc, Vector2f vector2f) {
		vector2f.set(this.m00 * vector2fc.x() + this.m10 * vector2fc.y() + this.m20, this.m01 * vector2fc.x() + this.m11 * vector2fc.y() + this.m21);
		return vector2f;
	}

	public Vector2f transformPosition(float float1, float float2, Vector2f vector2f) {
		return vector2f.set(this.m00 * float1 + this.m10 * float2 + this.m20, this.m01 * float1 + this.m11 * float2 + this.m21);
	}

	public Vector2f transformDirection(Vector2f vector2f) {
		vector2f.set(this.m00 * vector2f.x + this.m10 * vector2f.y, this.m01 * vector2f.x + this.m11 * vector2f.y);
		return vector2f;
	}

	public Vector2f transformDirection(Vector2fc vector2fc, Vector2f vector2f) {
		vector2f.set(this.m00 * vector2fc.x() + this.m10 * vector2fc.y(), this.m01 * vector2fc.x() + this.m11 * vector2fc.y());
		return vector2f;
	}

	public Vector2f transformDirection(float float1, float float2, Vector2f vector2f) {
		return vector2f.set(this.m00 * float1 + this.m10 * float2, this.m01 * float1 + this.m11 * float2);
	}

	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeFloat(this.m00);
		objectOutput.writeFloat(this.m01);
		objectOutput.writeFloat(this.m10);
		objectOutput.writeFloat(this.m11);
		objectOutput.writeFloat(this.m20);
		objectOutput.writeFloat(this.m21);
	}

	public void readExternal(ObjectInput objectInput) throws IOException {
		this.m00 = objectInput.readFloat();
		this.m01 = objectInput.readFloat();
		this.m10 = objectInput.readFloat();
		this.m11 = objectInput.readFloat();
		this.m20 = objectInput.readFloat();
		this.m21 = objectInput.readFloat();
	}

	public Matrix3x2f rotate(float float1) {
		return this.rotate(float1, this);
	}

	public Matrix3x2f rotate(float float1, Matrix3x2f matrix3x2f) {
		float float2 = Math.cos(float1);
		float float3 = Math.sin(float1);
		float float4 = -float3;
		float float5 = this.m00 * float2 + this.m10 * float3;
		float float6 = this.m01 * float2 + this.m11 * float3;
		matrix3x2f.m10 = this.m00 * float4 + this.m10 * float2;
		matrix3x2f.m11 = this.m01 * float4 + this.m11 * float2;
		matrix3x2f.m00 = float5;
		matrix3x2f.m01 = float6;
		matrix3x2f.m20 = this.m20;
		matrix3x2f.m21 = this.m21;
		return matrix3x2f;
	}

	public Matrix3x2f rotateLocal(float float1, Matrix3x2f matrix3x2f) {
		float float2 = Math.sin(float1);
		float float3 = Math.cosFromSin(float2, float1);
		float float4 = float3 * this.m00 - float2 * this.m01;
		float float5 = float2 * this.m00 + float3 * this.m01;
		float float6 = float3 * this.m10 - float2 * this.m11;
		float float7 = float2 * this.m10 + float3 * this.m11;
		float float8 = float3 * this.m20 - float2 * this.m21;
		float float9 = float2 * this.m20 + float3 * this.m21;
		matrix3x2f.m00 = float4;
		matrix3x2f.m01 = float5;
		matrix3x2f.m10 = float6;
		matrix3x2f.m11 = float7;
		matrix3x2f.m20 = float8;
		matrix3x2f.m21 = float9;
		return matrix3x2f;
	}

	public Matrix3x2f rotateLocal(float float1) {
		return this.rotateLocal(float1, this);
	}

	public Matrix3x2f rotateAbout(float float1, float float2, float float3) {
		return this.rotateAbout(float1, float2, float3, this);
	}

	public Matrix3x2f rotateAbout(float float1, float float2, float float3, Matrix3x2f matrix3x2f) {
		float float4 = this.m00 * float2 + this.m10 * float3 + this.m20;
		float float5 = this.m01 * float2 + this.m11 * float3 + this.m21;
		float float6 = Math.cos(float1);
		float float7 = Math.sin(float1);
		float float8 = this.m00 * float6 + this.m10 * float7;
		float float9 = this.m01 * float6 + this.m11 * float7;
		matrix3x2f.m10 = this.m00 * -float7 + this.m10 * float6;
		matrix3x2f.m11 = this.m01 * -float7 + this.m11 * float6;
		matrix3x2f.m00 = float8;
		matrix3x2f.m01 = float9;
		matrix3x2f.m20 = matrix3x2f.m00 * -float2 + matrix3x2f.m10 * -float3 + float4;
		matrix3x2f.m21 = matrix3x2f.m01 * -float2 + matrix3x2f.m11 * -float3 + float5;
		return matrix3x2f;
	}

	public Matrix3x2f rotateTo(Vector2fc vector2fc, Vector2fc vector2fc2, Matrix3x2f matrix3x2f) {
		float float1 = vector2fc.x() * vector2fc2.x() + vector2fc.y() * vector2fc2.y();
		float float2 = vector2fc.x() * vector2fc2.y() - vector2fc.y() * vector2fc2.x();
		float float3 = -float2;
		float float4 = this.m00 * float1 + this.m10 * float2;
		float float5 = this.m01 * float1 + this.m11 * float2;
		matrix3x2f.m10 = this.m00 * float3 + this.m10 * float1;
		matrix3x2f.m11 = this.m01 * float3 + this.m11 * float1;
		matrix3x2f.m00 = float4;
		matrix3x2f.m01 = float5;
		matrix3x2f.m20 = this.m20;
		matrix3x2f.m21 = this.m21;
		return matrix3x2f;
	}

	public Matrix3x2f rotateTo(Vector2fc vector2fc, Vector2fc vector2fc2) {
		return this.rotateTo(vector2fc, vector2fc2, this);
	}

	public Matrix3x2f view(float float1, float float2, float float3, float float4, Matrix3x2f matrix3x2f) {
		float float5 = 2.0F / (float2 - float1);
		float float6 = 2.0F / (float4 - float3);
		float float7 = (float1 + float2) / (float1 - float2);
		float float8 = (float3 + float4) / (float3 - float4);
		matrix3x2f.m20 = this.m00 * float7 + this.m10 * float8 + this.m20;
		matrix3x2f.m21 = this.m01 * float7 + this.m11 * float8 + this.m21;
		matrix3x2f.m00 = this.m00 * float5;
		matrix3x2f.m01 = this.m01 * float5;
		matrix3x2f.m10 = this.m10 * float6;
		matrix3x2f.m11 = this.m11 * float6;
		return matrix3x2f;
	}

	public Matrix3x2f view(float float1, float float2, float float3, float float4) {
		return this.view(float1, float2, float3, float4, this);
	}

	public Matrix3x2f setView(float float1, float float2, float float3, float float4) {
		this.m00 = 2.0F / (float2 - float1);
		this.m01 = 0.0F;
		this.m10 = 0.0F;
		this.m11 = 2.0F / (float4 - float3);
		this.m20 = (float1 + float2) / (float1 - float2);
		this.m21 = (float3 + float4) / (float3 - float4);
		return this;
	}

	public Vector2f origin(Vector2f vector2f) {
		float float1 = 1.0F / (this.m00 * this.m11 - this.m01 * this.m10);
		vector2f.x = (this.m10 * this.m21 - this.m20 * this.m11) * float1;
		vector2f.y = (this.m20 * this.m01 - this.m00 * this.m21) * float1;
		return vector2f;
	}

	public float[] viewArea(float[] floatArray) {
		float float1 = 1.0F / (this.m00 * this.m11 - this.m01 * this.m10);
		float float2 = this.m11 * float1;
		float float3 = -this.m01 * float1;
		float float4 = -this.m10 * float1;
		float float5 = this.m00 * float1;
		float float6 = (this.m10 * this.m21 - this.m20 * this.m11) * float1;
		float float7 = (this.m20 * this.m01 - this.m00 * this.m21) * float1;
		float float8 = -float2 - float4;
		float float9 = -float3 - float5;
		float float10 = float2 - float4;
		float float11 = float3 - float5;
		float float12 = -float2 + float4;
		float float13 = -float3 + float5;
		float float14 = float2 + float4;
		float float15 = float3 + float5;
		float float16 = float8 < float12 ? float8 : float12;
		float16 = float16 < float10 ? float16 : float10;
		float16 = float16 < float14 ? float16 : float14;
		float float17 = float9 < float13 ? float9 : float13;
		float17 = float17 < float11 ? float17 : float11;
		float17 = float17 < float15 ? float17 : float15;
		float float18 = float8 > float12 ? float8 : float12;
		float18 = float18 > float10 ? float18 : float10;
		float18 = float18 > float14 ? float18 : float14;
		float float19 = float9 > float13 ? float9 : float13;
		float19 = float19 > float11 ? float19 : float11;
		float19 = float19 > float15 ? float19 : float15;
		floatArray[0] = float16 + float6;
		floatArray[1] = float17 + float7;
		floatArray[2] = float18 + float6;
		floatArray[3] = float19 + float7;
		return floatArray;
	}

	public Vector2f positiveX(Vector2f vector2f) {
		float float1 = this.m00 * this.m11 - this.m01 * this.m10;
		float1 = 1.0F / float1;
		vector2f.x = this.m11 * float1;
		vector2f.y = -this.m01 * float1;
		return vector2f.normalize(vector2f);
	}

	public Vector2f normalizedPositiveX(Vector2f vector2f) {
		vector2f.x = this.m11;
		vector2f.y = -this.m01;
		return vector2f;
	}

	public Vector2f positiveY(Vector2f vector2f) {
		float float1 = this.m00 * this.m11 - this.m01 * this.m10;
		float1 = 1.0F / float1;
		vector2f.x = -this.m10 * float1;
		vector2f.y = this.m00 * float1;
		return vector2f.normalize(vector2f);
	}

	public Vector2f normalizedPositiveY(Vector2f vector2f) {
		vector2f.x = -this.m10;
		vector2f.y = this.m00;
		return vector2f;
	}

	public Vector2f unproject(float float1, float float2, int[] intArray, Vector2f vector2f) {
		float float3 = 1.0F / (this.m00 * this.m11 - this.m01 * this.m10);
		float float4 = this.m11 * float3;
		float float5 = -this.m01 * float3;
		float float6 = -this.m10 * float3;
		float float7 = this.m00 * float3;
		float float8 = (this.m10 * this.m21 - this.m20 * this.m11) * float3;
		float float9 = (this.m20 * this.m01 - this.m00 * this.m21) * float3;
		float float10 = (float1 - (float)intArray[0]) / (float)intArray[2] * 2.0F - 1.0F;
		float float11 = (float2 - (float)intArray[1]) / (float)intArray[3] * 2.0F - 1.0F;
		vector2f.x = float4 * float10 + float6 * float11 + float8;
		vector2f.y = float5 * float10 + float7 * float11 + float9;
		return vector2f;
	}

	public Vector2f unprojectInv(float float1, float float2, int[] intArray, Vector2f vector2f) {
		float float3 = (float1 - (float)intArray[0]) / (float)intArray[2] * 2.0F - 1.0F;
		float float4 = (float2 - (float)intArray[1]) / (float)intArray[3] * 2.0F - 1.0F;
		vector2f.x = this.m00 * float3 + this.m10 * float4 + this.m20;
		vector2f.y = this.m01 * float3 + this.m11 * float4 + this.m21;
		return vector2f;
	}

	public Matrix3x2f shearX(float float1) {
		return this.shearX(float1, this);
	}

	public Matrix3x2f shearX(float float1, Matrix3x2f matrix3x2f) {
		float float2 = this.m00 * float1 + this.m10;
		float float3 = this.m01 * float1 + this.m11;
		matrix3x2f.m00 = this.m00;
		matrix3x2f.m01 = this.m01;
		matrix3x2f.m10 = float2;
		matrix3x2f.m11 = float3;
		matrix3x2f.m20 = this.m20;
		matrix3x2f.m21 = this.m21;
		return matrix3x2f;
	}

	public Matrix3x2f shearY(float float1) {
		return this.shearY(float1, this);
	}

	public Matrix3x2f shearY(float float1, Matrix3x2f matrix3x2f) {
		float float2 = this.m00 + this.m10 * float1;
		float float3 = this.m01 + this.m11 * float1;
		matrix3x2f.m00 = float2;
		matrix3x2f.m01 = float3;
		matrix3x2f.m10 = this.m10;
		matrix3x2f.m11 = this.m11;
		matrix3x2f.m20 = this.m20;
		matrix3x2f.m21 = this.m21;
		return matrix3x2f;
	}

	public Matrix3x2f span(Vector2f vector2f, Vector2f vector2f2, Vector2f vector2f3) {
		float float1 = 1.0F / (this.m00 * this.m11 - this.m01 * this.m10);
		float float2 = this.m11 * float1;
		float float3 = -this.m01 * float1;
		float float4 = -this.m10 * float1;
		float float5 = this.m00 * float1;
		vector2f.x = -float2 - float4 + (this.m10 * this.m21 - this.m20 * this.m11) * float1;
		vector2f.y = -float3 - float5 + (this.m20 * this.m01 - this.m00 * this.m21) * float1;
		vector2f2.x = 2.0F * float2;
		vector2f2.y = 2.0F * float3;
		vector2f3.x = 2.0F * float4;
		vector2f3.y = 2.0F * float5;
		return this;
	}

	public boolean testPoint(float float1, float float2) {
		float float3 = this.m00;
		float float4 = this.m10;
		float float5 = 1.0F + this.m20;
		float float6 = -this.m00;
		float float7 = -this.m10;
		float float8 = 1.0F - this.m20;
		float float9 = this.m01;
		float float10 = this.m11;
		float float11 = 1.0F + this.m21;
		float float12 = -this.m01;
		float float13 = -this.m11;
		float float14 = 1.0F - this.m21;
		return float3 * float1 + float4 * float2 + float5 >= 0.0F && float6 * float1 + float7 * float2 + float8 >= 0.0F && float9 * float1 + float10 * float2 + float11 >= 0.0F && float12 * float1 + float13 * float2 + float14 >= 0.0F;
	}

	public boolean testCircle(float float1, float float2, float float3) {
		float float4 = this.m00;
		float float5 = this.m10;
		float float6 = 1.0F + this.m20;
		float float7 = Math.invsqrt(float4 * float4 + float5 * float5);
		float4 *= float7;
		float5 *= float7;
		float6 *= float7;
		float float8 = -this.m00;
		float float9 = -this.m10;
		float float10 = 1.0F - this.m20;
		float7 = Math.invsqrt(float8 * float8 + float9 * float9);
		float8 *= float7;
		float9 *= float7;
		float10 *= float7;
		float float11 = this.m01;
		float float12 = this.m11;
		float float13 = 1.0F + this.m21;
		float7 = Math.invsqrt(float11 * float11 + float12 * float12);
		float11 *= float7;
		float12 *= float7;
		float13 *= float7;
		float float14 = -this.m01;
		float float15 = -this.m11;
		float float16 = 1.0F - this.m21;
		float7 = Math.invsqrt(float14 * float14 + float15 * float15);
		float14 *= float7;
		float15 *= float7;
		float16 *= float7;
		return float4 * float1 + float5 * float2 + float6 >= -float3 && float8 * float1 + float9 * float2 + float10 >= -float3 && float11 * float1 + float12 * float2 + float13 >= -float3 && float14 * float1 + float15 * float2 + float16 >= -float3;
	}

	public boolean testAar(float float1, float float2, float float3, float float4) {
		float float5 = this.m00;
		float float6 = this.m10;
		float float7 = 1.0F + this.m20;
		float float8 = -this.m00;
		float float9 = -this.m10;
		float float10 = 1.0F - this.m20;
		float float11 = this.m01;
		float float12 = this.m11;
		float float13 = 1.0F + this.m21;
		float float14 = -this.m01;
		float float15 = -this.m11;
		float float16 = 1.0F - this.m21;
		return float5 * (float5 < 0.0F ? float1 : float3) + float6 * (float6 < 0.0F ? float2 : float4) >= -float7 && float8 * (float8 < 0.0F ? float1 : float3) + float9 * (float9 < 0.0F ? float2 : float4) >= -float10 && float11 * (float11 < 0.0F ? float1 : float3) + float12 * (float12 < 0.0F ? float2 : float4) >= -float13 && float14 * (float14 < 0.0F ? float1 : float3) + float15 * (float15 < 0.0F ? float2 : float4) >= -float16;
	}

	public int hashCode() {
		byte byte1 = 1;
		int int1 = 31 * byte1 + Float.floatToIntBits(this.m00);
		int1 = 31 * int1 + Float.floatToIntBits(this.m01);
		int1 = 31 * int1 + Float.floatToIntBits(this.m10);
		int1 = 31 * int1 + Float.floatToIntBits(this.m11);
		int1 = 31 * int1 + Float.floatToIntBits(this.m20);
		int1 = 31 * int1 + Float.floatToIntBits(this.m21);
		return int1;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object == null) {
			return false;
		} else if (this.getClass() != object.getClass()) {
			return false;
		} else {
			Matrix3x2f matrix3x2f = (Matrix3x2f)object;
			if (Float.floatToIntBits(this.m00) != Float.floatToIntBits(matrix3x2f.m00)) {
				return false;
			} else if (Float.floatToIntBits(this.m01) != Float.floatToIntBits(matrix3x2f.m01)) {
				return false;
			} else if (Float.floatToIntBits(this.m10) != Float.floatToIntBits(matrix3x2f.m10)) {
				return false;
			} else if (Float.floatToIntBits(this.m11) != Float.floatToIntBits(matrix3x2f.m11)) {
				return false;
			} else if (Float.floatToIntBits(this.m20) != Float.floatToIntBits(matrix3x2f.m20)) {
				return false;
			} else {
				return Float.floatToIntBits(this.m21) == Float.floatToIntBits(matrix3x2f.m21);
			}
		}
	}

	public boolean equals(Matrix3x2fc matrix3x2fc, float float1) {
		if (this == matrix3x2fc) {
			return true;
		} else if (matrix3x2fc == null) {
			return false;
		} else if (!(matrix3x2fc instanceof Matrix3x2f)) {
			return false;
		} else if (!Runtime.equals(this.m00, matrix3x2fc.m00(), float1)) {
			return false;
		} else if (!Runtime.equals(this.m01, matrix3x2fc.m01(), float1)) {
			return false;
		} else if (!Runtime.equals(this.m10, matrix3x2fc.m10(), float1)) {
			return false;
		} else if (!Runtime.equals(this.m11, matrix3x2fc.m11(), float1)) {
			return false;
		} else if (!Runtime.equals(this.m20, matrix3x2fc.m20(), float1)) {
			return false;
		} else {
			return Runtime.equals(this.m21, matrix3x2fc.m21(), float1);
		}
	}

	public boolean isFinite() {
		return Math.isFinite(this.m00) && Math.isFinite(this.m01) && Math.isFinite(this.m10) && Math.isFinite(this.m11) && Math.isFinite(this.m20) && Math.isFinite(this.m21);
	}
}
