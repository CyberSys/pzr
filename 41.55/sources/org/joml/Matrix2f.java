package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;


public class Matrix2f implements Externalizable,Matrix2fc {
	private static final long serialVersionUID = 1L;
	public float m00;
	public float m01;
	public float m10;
	public float m11;

	public Matrix2f() {
		this.m00 = 1.0F;
		this.m11 = 1.0F;
	}

	public Matrix2f(Matrix2fc matrix2fc) {
		if (matrix2fc instanceof Matrix2f) {
			MemUtil.INSTANCE.copy((Matrix2f)matrix2fc, this);
		} else {
			this.setMatrix2fc(matrix2fc);
		}
	}

	public Matrix2f(Matrix3fc matrix3fc) {
		if (matrix3fc instanceof Matrix3f) {
			MemUtil.INSTANCE.copy((Matrix3f)matrix3fc, this);
		} else {
			this.setMatrix3fc(matrix3fc);
		}
	}

	public Matrix2f(float float1, float float2, float float3, float float4) {
		this.m00 = float1;
		this.m01 = float2;
		this.m10 = float3;
		this.m11 = float4;
	}

	public Matrix2f(FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.get(this, floatBuffer.position(), floatBuffer);
	}

	public Matrix2f(Vector2fc vector2fc, Vector2fc vector2fc2) {
		this.m00 = vector2fc.x();
		this.m01 = vector2fc.y();
		this.m10 = vector2fc2.x();
		this.m11 = vector2fc2.y();
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

	public Matrix2f m00(float float1) {
		this.m00 = float1;
		return this;
	}

	public Matrix2f m01(float float1) {
		this.m01 = float1;
		return this;
	}

	public Matrix2f m10(float float1) {
		this.m10 = float1;
		return this;
	}

	public Matrix2f m11(float float1) {
		this.m11 = float1;
		return this;
	}

	Matrix2f _m00(float float1) {
		this.m00 = float1;
		return this;
	}

	Matrix2f _m01(float float1) {
		this.m01 = float1;
		return this;
	}

	Matrix2f _m10(float float1) {
		this.m10 = float1;
		return this;
	}

	Matrix2f _m11(float float1) {
		this.m11 = float1;
		return this;
	}

	public Matrix2f set(Matrix2fc matrix2fc) {
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

	public Matrix2f set(Matrix3x2fc matrix3x2fc) {
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
	}

	public Matrix2f set(Matrix3fc matrix3fc) {
		if (matrix3fc instanceof Matrix3f) {
			MemUtil.INSTANCE.copy((Matrix3f)matrix3fc, this);
		} else {
			this.setMatrix3fc(matrix3fc);
		}

		return this;
	}

	private void setMatrix3fc(Matrix3fc matrix3fc) {
		this.m00 = matrix3fc.m00();
		this.m01 = matrix3fc.m01();
		this.m10 = matrix3fc.m10();
		this.m11 = matrix3fc.m11();
	}

	public Matrix2f mul(Matrix2fc matrix2fc) {
		return this.mul(matrix2fc, this);
	}

	public Matrix2f mul(Matrix2fc matrix2fc, Matrix2f matrix2f) {
		float float1 = this.m00 * matrix2fc.m00() + this.m10 * matrix2fc.m01();
		float float2 = this.m01 * matrix2fc.m00() + this.m11 * matrix2fc.m01();
		float float3 = this.m00 * matrix2fc.m10() + this.m10 * matrix2fc.m11();
		float float4 = this.m01 * matrix2fc.m10() + this.m11 * matrix2fc.m11();
		matrix2f.m00 = float1;
		matrix2f.m01 = float2;
		matrix2f.m10 = float3;
		matrix2f.m11 = float4;
		return matrix2f;
	}

	public Matrix2f mulLocal(Matrix2fc matrix2fc) {
		return this.mulLocal(matrix2fc, this);
	}

	public Matrix2f mulLocal(Matrix2fc matrix2fc, Matrix2f matrix2f) {
		float float1 = matrix2fc.m00() * this.m00 + matrix2fc.m10() * this.m01;
		float float2 = matrix2fc.m01() * this.m00 + matrix2fc.m11() * this.m01;
		float float3 = matrix2fc.m00() * this.m10 + matrix2fc.m10() * this.m11;
		float float4 = matrix2fc.m01() * this.m10 + matrix2fc.m11() * this.m11;
		matrix2f.m00 = float1;
		matrix2f.m01 = float2;
		matrix2f.m10 = float3;
		matrix2f.m11 = float4;
		return matrix2f;
	}

	public Matrix2f set(float float1, float float2, float float3, float float4) {
		this.m00 = float1;
		this.m01 = float2;
		this.m10 = float3;
		this.m11 = float4;
		return this;
	}

	public Matrix2f set(float[] floatArray) {
		MemUtil.INSTANCE.copy((float[])floatArray, 0, (Matrix2f)this);
		return this;
	}

	public Matrix2f set(Vector2fc vector2fc, Vector2fc vector2fc2) {
		this.m00 = vector2fc.x();
		this.m01 = vector2fc.y();
		this.m10 = vector2fc2.x();
		this.m11 = vector2fc2.y();
		return this;
	}

	public float determinant() {
		return this.m00 * this.m11 - this.m10 * this.m01;
	}

	public Matrix2f invert() {
		return this.invert(this);
	}

	public Matrix2f invert(Matrix2f matrix2f) {
		float float1 = 1.0F / this.determinant();
		float float2 = this.m11 * float1;
		float float3 = -this.m01 * float1;
		float float4 = -this.m10 * float1;
		float float5 = this.m00 * float1;
		matrix2f.m00 = float2;
		matrix2f.m01 = float3;
		matrix2f.m10 = float4;
		matrix2f.m11 = float5;
		return matrix2f;
	}

	public Matrix2f transpose() {
		return this.transpose(this);
	}

	public Matrix2f transpose(Matrix2f matrix2f) {
		matrix2f.set(this.m00, this.m10, this.m01, this.m11);
		return matrix2f;
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
		return string + " " + Runtime.format((double)this.m10, numberFormat) + "\n" + Runtime.format((double)this.m01, numberFormat) + " " + Runtime.format((double)this.m11, numberFormat) + "\n";
	}

	public Matrix2f get(Matrix2f matrix2f) {
		return matrix2f.set((Matrix2fc)this);
	}

	public Matrix3x2f get(Matrix3x2f matrix3x2f) {
		return matrix3x2f.set((Matrix2fc)this);
	}

	public Matrix3f get(Matrix3f matrix3f) {
		return matrix3f.set((Matrix2fc)this);
	}

	public float getRotation() {
		return Math.atan2(this.m01, this.m11);
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

	public FloatBuffer getTransposed(FloatBuffer floatBuffer) {
		return this.get(floatBuffer.position(), floatBuffer);
	}

	public FloatBuffer getTransposed(int int1, FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.putTransposed(this, int1, floatBuffer);
		return floatBuffer;
	}

	public ByteBuffer getTransposed(ByteBuffer byteBuffer) {
		return this.get(byteBuffer.position(), byteBuffer);
	}

	public ByteBuffer getTransposed(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.putTransposed(this, int1, byteBuffer);
		return byteBuffer;
	}

	public Matrix2fc getToAddress(long long1) {
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

	public Matrix2f set(FloatBuffer floatBuffer) {
		MemUtil.INSTANCE.get(this, floatBuffer.position(), floatBuffer);
		return this;
	}

	public Matrix2f set(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, byteBuffer.position(), byteBuffer);
		return this;
	}

	public Matrix2f setFromAddress(long long1) {
		if (Options.NO_UNSAFE) {
			throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
		} else {
			MemUtil.MemUtilUnsafe.get(this, long1);
			return this;
		}
	}

	public Matrix2f zero() {
		MemUtil.INSTANCE.zero(this);
		return this;
	}

	public Matrix2f identity() {
		MemUtil.INSTANCE.identity(this);
		return this;
	}

	public Matrix2f scale(Vector2fc vector2fc, Matrix2f matrix2f) {
		return this.scale(vector2fc.x(), vector2fc.y(), matrix2f);
	}

	public Matrix2f scale(Vector2fc vector2fc) {
		return this.scale(vector2fc.x(), vector2fc.y(), this);
	}

	public Matrix2f scale(float float1, float float2, Matrix2f matrix2f) {
		matrix2f.m00 = this.m00 * float1;
		matrix2f.m01 = this.m01 * float1;
		matrix2f.m10 = this.m10 * float2;
		matrix2f.m11 = this.m11 * float2;
		return matrix2f;
	}

	public Matrix2f scale(float float1, float float2) {
		return this.scale(float1, float2, this);
	}

	public Matrix2f scale(float float1, Matrix2f matrix2f) {
		return this.scale(float1, float1, matrix2f);
	}

	public Matrix2f scale(float float1) {
		return this.scale(float1, float1);
	}

	public Matrix2f scaleLocal(float float1, float float2, Matrix2f matrix2f) {
		matrix2f.m00 = float1 * this.m00;
		matrix2f.m01 = float2 * this.m01;
		matrix2f.m10 = float1 * this.m10;
		matrix2f.m11 = float2 * this.m11;
		return matrix2f;
	}

	public Matrix2f scaleLocal(float float1, float float2) {
		return this.scaleLocal(float1, float2, this);
	}

	public Matrix2f scaling(float float1) {
		MemUtil.INSTANCE.zero(this);
		this.m00 = float1;
		this.m11 = float1;
		return this;
	}

	public Matrix2f scaling(float float1, float float2) {
		MemUtil.INSTANCE.zero(this);
		this.m00 = float1;
		this.m11 = float2;
		return this;
	}

	public Matrix2f scaling(Vector2fc vector2fc) {
		return this.scaling(vector2fc.x(), vector2fc.y());
	}

	public Matrix2f rotation(float float1) {
		float float2 = Math.sin(float1);
		float float3 = Math.cosFromSin(float2, float1);
		this.m00 = float3;
		this.m01 = float2;
		this.m10 = -float2;
		this.m11 = float3;
		return this;
	}

	public Vector2f transform(Vector2f vector2f) {
		return vector2f.mul((Matrix2fc)this);
	}

	public Vector2f transform(Vector2fc vector2fc, Vector2f vector2f) {
		vector2fc.mul((Matrix2fc)this, vector2f);
		return vector2f;
	}

	public Vector2f transform(float float1, float float2, Vector2f vector2f) {
		vector2f.set(this.m00 * float1 + this.m10 * float2, this.m01 * float1 + this.m11 * float2);
		return vector2f;
	}

	public Vector2f transformTranspose(Vector2f vector2f) {
		return vector2f.mulTranspose(this);
	}

	public Vector2f transformTranspose(Vector2fc vector2fc, Vector2f vector2f) {
		vector2fc.mulTranspose(this, vector2f);
		return vector2f;
	}

	public Vector2f transformTranspose(float float1, float float2, Vector2f vector2f) {
		vector2f.set(this.m00 * float1 + this.m01 * float2, this.m10 * float1 + this.m11 * float2);
		return vector2f;
	}

	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeFloat(this.m00);
		objectOutput.writeFloat(this.m01);
		objectOutput.writeFloat(this.m10);
		objectOutput.writeFloat(this.m11);
	}

	public void readExternal(ObjectInput objectInput) throws IOException {
		this.m00 = objectInput.readFloat();
		this.m01 = objectInput.readFloat();
		this.m10 = objectInput.readFloat();
		this.m11 = objectInput.readFloat();
	}

	public Matrix2f rotate(float float1) {
		return this.rotate(float1, this);
	}

	public Matrix2f rotate(float float1, Matrix2f matrix2f) {
		float float2 = Math.sin(float1);
		float float3 = Math.cosFromSin(float2, float1);
		float float4 = this.m00 * float3 + this.m10 * float2;
		float float5 = this.m01 * float3 + this.m11 * float2;
		float float6 = this.m10 * float3 - this.m00 * float2;
		float float7 = this.m11 * float3 - this.m01 * float2;
		matrix2f.m00 = float4;
		matrix2f.m01 = float5;
		matrix2f.m10 = float6;
		matrix2f.m11 = float7;
		return matrix2f;
	}

	public Matrix2f rotateLocal(float float1) {
		return this.rotateLocal(float1, this);
	}

	public Matrix2f rotateLocal(float float1, Matrix2f matrix2f) {
		float float2 = Math.sin(float1);
		float float3 = Math.cosFromSin(float2, float1);
		float float4 = float3 * this.m00 - float2 * this.m01;
		float float5 = float2 * this.m00 + float3 * this.m01;
		float float6 = float3 * this.m10 - float2 * this.m11;
		float float7 = float2 * this.m10 + float3 * this.m11;
		matrix2f.m00 = float4;
		matrix2f.m01 = float5;
		matrix2f.m10 = float6;
		matrix2f.m11 = float7;
		return matrix2f;
	}

	public Vector2f getRow(int int1, Vector2f vector2f) throws IndexOutOfBoundsException {
		switch (int1) {
		case 0: 
			vector2f.x = this.m00;
			vector2f.y = this.m10;
			break;
		
		case 1: 
			vector2f.x = this.m01;
			vector2f.y = this.m11;
			break;
		
		default: 
			throw new IndexOutOfBoundsException();
		
		}
		return vector2f;
	}

	public Matrix2f setRow(int int1, Vector2fc vector2fc) throws IndexOutOfBoundsException {
		return this.setRow(int1, vector2fc.x(), vector2fc.y());
	}

	public Matrix2f setRow(int int1, float float1, float float2) throws IndexOutOfBoundsException {
		switch (int1) {
		case 0: 
			this.m00 = float1;
			this.m10 = float2;
			break;
		
		case 1: 
			this.m01 = float1;
			this.m11 = float2;
			break;
		
		default: 
			throw new IndexOutOfBoundsException();
		
		}
		return this;
	}

	public Vector2f getColumn(int int1, Vector2f vector2f) throws IndexOutOfBoundsException {
		switch (int1) {
		case 0: 
			vector2f.x = this.m00;
			vector2f.y = this.m01;
			break;
		
		case 1: 
			vector2f.x = this.m10;
			vector2f.y = this.m11;
			break;
		
		default: 
			throw new IndexOutOfBoundsException();
		
		}
		return vector2f;
	}

	public Matrix2f setColumn(int int1, Vector2fc vector2fc) throws IndexOutOfBoundsException {
		return this.setColumn(int1, vector2fc.x(), vector2fc.y());
	}

	public Matrix2f setColumn(int int1, float float1, float float2) throws IndexOutOfBoundsException {
		switch (int1) {
		case 0: 
			this.m00 = float1;
			this.m01 = float2;
			break;
		
		case 1: 
			this.m10 = float1;
			this.m11 = float2;
			break;
		
		default: 
			throw new IndexOutOfBoundsException();
		
		}
		return this;
	}

	public float get(int int1, int int2) {
		switch (int1) {
		case 0: 
			switch (int2) {
			case 0: 
				return this.m00;
			
			case 1: 
				return this.m01;
			
			default: 
				throw new IndexOutOfBoundsException();
			
			}

		
		case 1: 
			switch (int2) {
			case 0: 
				return this.m10;
			
			case 1: 
				return this.m11;
			
			}

		
		}
		throw new IndexOutOfBoundsException();
	}

	public Matrix2f set(int int1, int int2, float float1) {
		switch (int1) {
		case 0: 
			switch (int2) {
			case 0: 
				this.m00 = float1;
				return this;
			
			case 1: 
				this.m01 = float1;
				return this;
			
			default: 
				throw new IndexOutOfBoundsException();
			
			}

		
		case 1: 
			switch (int2) {
			case 0: 
				this.m10 = float1;
				return this;
			
			case 1: 
				this.m11 = float1;
				return this;
			
			}

		
		}
		throw new IndexOutOfBoundsException();
	}

	public Matrix2f normal() {
		return this.normal(this);
	}

	public Matrix2f normal(Matrix2f matrix2f) {
		float float1 = this.m00 * this.m11 - this.m10 * this.m01;
		float float2 = 1.0F / float1;
		float float3 = this.m11 * float2;
		float float4 = -this.m10 * float2;
		float float5 = -this.m01 * float2;
		float float6 = this.m00 * float2;
		matrix2f.m00 = float3;
		matrix2f.m01 = float4;
		matrix2f.m10 = float5;
		matrix2f.m11 = float6;
		return matrix2f;
	}

	public Vector2f getScale(Vector2f vector2f) {
		vector2f.x = Math.sqrt(this.m00 * this.m00 + this.m01 * this.m01);
		vector2f.y = Math.sqrt(this.m10 * this.m10 + this.m11 * this.m11);
		return vector2f;
	}

	public Vector2f positiveX(Vector2f vector2f) {
		if (this.m00 * this.m11 < this.m01 * this.m10) {
			vector2f.x = -this.m11;
			vector2f.y = this.m01;
		} else {
			vector2f.x = this.m11;
			vector2f.y = -this.m01;
		}

		return vector2f.normalize(vector2f);
	}

	public Vector2f normalizedPositiveX(Vector2f vector2f) {
		if (this.m00 * this.m11 < this.m01 * this.m10) {
			vector2f.x = -this.m11;
			vector2f.y = this.m01;
		} else {
			vector2f.x = this.m11;
			vector2f.y = -this.m01;
		}

		return vector2f;
	}

	public Vector2f positiveY(Vector2f vector2f) {
		if (this.m00 * this.m11 < this.m01 * this.m10) {
			vector2f.x = this.m10;
			vector2f.y = -this.m00;
		} else {
			vector2f.x = -this.m10;
			vector2f.y = this.m00;
		}

		return vector2f.normalize(vector2f);
	}

	public Vector2f normalizedPositiveY(Vector2f vector2f) {
		if (this.m00 * this.m11 < this.m01 * this.m10) {
			vector2f.x = this.m10;
			vector2f.y = -this.m00;
		} else {
			vector2f.x = -this.m10;
			vector2f.y = this.m00;
		}

		return vector2f;
	}

	public int hashCode() {
		byte byte1 = 1;
		int int1 = 31 * byte1 + Float.floatToIntBits(this.m00);
		int1 = 31 * int1 + Float.floatToIntBits(this.m01);
		int1 = 31 * int1 + Float.floatToIntBits(this.m10);
		int1 = 31 * int1 + Float.floatToIntBits(this.m11);
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
			Matrix2f matrix2f = (Matrix2f)object;
			if (Float.floatToIntBits(this.m00) != Float.floatToIntBits(matrix2f.m00)) {
				return false;
			} else if (Float.floatToIntBits(this.m01) != Float.floatToIntBits(matrix2f.m01)) {
				return false;
			} else if (Float.floatToIntBits(this.m10) != Float.floatToIntBits(matrix2f.m10)) {
				return false;
			} else {
				return Float.floatToIntBits(this.m11) == Float.floatToIntBits(matrix2f.m11);
			}
		}
	}

	public boolean equals(Matrix2fc matrix2fc, float float1) {
		if (this == matrix2fc) {
			return true;
		} else if (matrix2fc == null) {
			return false;
		} else if (!(matrix2fc instanceof Matrix2f)) {
			return false;
		} else if (!Runtime.equals(this.m00, matrix2fc.m00(), float1)) {
			return false;
		} else if (!Runtime.equals(this.m01, matrix2fc.m01(), float1)) {
			return false;
		} else if (!Runtime.equals(this.m10, matrix2fc.m10(), float1)) {
			return false;
		} else {
			return Runtime.equals(this.m11, matrix2fc.m11(), float1);
		}
	}

	public Matrix2f swap(Matrix2f matrix2f) {
		MemUtil.INSTANCE.swap(this, matrix2f);
		return this;
	}

	public Matrix2f add(Matrix2fc matrix2fc) {
		return this.add(matrix2fc, this);
	}

	public Matrix2f add(Matrix2fc matrix2fc, Matrix2f matrix2f) {
		matrix2f.m00 = this.m00 + matrix2fc.m00();
		matrix2f.m01 = this.m01 + matrix2fc.m01();
		matrix2f.m10 = this.m10 + matrix2fc.m10();
		matrix2f.m11 = this.m11 + matrix2fc.m11();
		return matrix2f;
	}

	public Matrix2f sub(Matrix2fc matrix2fc) {
		return this.sub(matrix2fc, this);
	}

	public Matrix2f sub(Matrix2fc matrix2fc, Matrix2f matrix2f) {
		matrix2f.m00 = this.m00 - matrix2fc.m00();
		matrix2f.m01 = this.m01 - matrix2fc.m01();
		matrix2f.m10 = this.m10 - matrix2fc.m10();
		matrix2f.m11 = this.m11 - matrix2fc.m11();
		return matrix2f;
	}

	public Matrix2f mulComponentWise(Matrix2fc matrix2fc) {
		return this.sub(matrix2fc, this);
	}

	public Matrix2f mulComponentWise(Matrix2fc matrix2fc, Matrix2f matrix2f) {
		matrix2f.m00 = this.m00 * matrix2fc.m00();
		matrix2f.m01 = this.m01 * matrix2fc.m01();
		matrix2f.m10 = this.m10 * matrix2fc.m10();
		matrix2f.m11 = this.m11 * matrix2fc.m11();
		return matrix2f;
	}

	public Matrix2f lerp(Matrix2fc matrix2fc, float float1) {
		return this.lerp(matrix2fc, float1, this);
	}

	public Matrix2f lerp(Matrix2fc matrix2fc, float float1, Matrix2f matrix2f) {
		matrix2f.m00 = Math.fma(matrix2fc.m00() - this.m00, float1, this.m00);
		matrix2f.m01 = Math.fma(matrix2fc.m01() - this.m01, float1, this.m01);
		matrix2f.m10 = Math.fma(matrix2fc.m10() - this.m10, float1, this.m10);
		matrix2f.m11 = Math.fma(matrix2fc.m11() - this.m11, float1, this.m11);
		return matrix2f;
	}

	public boolean isFinite() {
		return Math.isFinite(this.m00) && Math.isFinite(this.m01) && Math.isFinite(this.m10) && Math.isFinite(this.m11);
	}
}
