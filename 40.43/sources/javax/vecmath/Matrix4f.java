package javax.vecmath;

import java.io.Serializable;


public class Matrix4f implements Serializable,Cloneable {
	static final long serialVersionUID = -8405036035410109353L;
	public float m00;
	public float m01;
	public float m02;
	public float m03;
	public float m10;
	public float m11;
	public float m12;
	public float m13;
	public float m20;
	public float m21;
	public float m22;
	public float m23;
	public float m30;
	public float m31;
	public float m32;
	public float m33;
	private static final double EPS = 1.0E-8;

	public Matrix4f(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16) {
		this.m00 = float1;
		this.m01 = float2;
		this.m02 = float3;
		this.m03 = float4;
		this.m10 = float5;
		this.m11 = float6;
		this.m12 = float7;
		this.m13 = float8;
		this.m20 = float9;
		this.m21 = float10;
		this.m22 = float11;
		this.m23 = float12;
		this.m30 = float13;
		this.m31 = float14;
		this.m32 = float15;
		this.m33 = float16;
	}

	public Matrix4f(float[] floatArray) {
		this.m00 = floatArray[0];
		this.m01 = floatArray[1];
		this.m02 = floatArray[2];
		this.m03 = floatArray[3];
		this.m10 = floatArray[4];
		this.m11 = floatArray[5];
		this.m12 = floatArray[6];
		this.m13 = floatArray[7];
		this.m20 = floatArray[8];
		this.m21 = floatArray[9];
		this.m22 = floatArray[10];
		this.m23 = floatArray[11];
		this.m30 = floatArray[12];
		this.m31 = floatArray[13];
		this.m32 = floatArray[14];
		this.m33 = floatArray[15];
	}

	public Matrix4f(Quat4f quat4f, Vector3f vector3f, float float1) {
		this.m00 = (float)((double)float1 * (1.0 - 2.0 * (double)quat4f.y * (double)quat4f.y - 2.0 * (double)quat4f.z * (double)quat4f.z));
		this.m10 = (float)((double)float1 * 2.0 * (double)(quat4f.x * quat4f.y + quat4f.w * quat4f.z));
		this.m20 = (float)((double)float1 * 2.0 * (double)(quat4f.x * quat4f.z - quat4f.w * quat4f.y));
		this.m01 = (float)((double)float1 * 2.0 * (double)(quat4f.x * quat4f.y - quat4f.w * quat4f.z));
		this.m11 = (float)((double)float1 * (1.0 - 2.0 * (double)quat4f.x * (double)quat4f.x - 2.0 * (double)quat4f.z * (double)quat4f.z));
		this.m21 = (float)((double)float1 * 2.0 * (double)(quat4f.y * quat4f.z + quat4f.w * quat4f.x));
		this.m02 = (float)((double)float1 * 2.0 * (double)(quat4f.x * quat4f.z + quat4f.w * quat4f.y));
		this.m12 = (float)((double)float1 * 2.0 * (double)(quat4f.y * quat4f.z - quat4f.w * quat4f.x));
		this.m22 = (float)((double)float1 * (1.0 - 2.0 * (double)quat4f.x * (double)quat4f.x - 2.0 * (double)quat4f.y * (double)quat4f.y));
		this.m03 = vector3f.x;
		this.m13 = vector3f.y;
		this.m23 = vector3f.z;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.m33 = 1.0F;
	}

	public Matrix4f(Matrix4d matrix4d) {
		this.m00 = (float)matrix4d.m00;
		this.m01 = (float)matrix4d.m01;
		this.m02 = (float)matrix4d.m02;
		this.m03 = (float)matrix4d.m03;
		this.m10 = (float)matrix4d.m10;
		this.m11 = (float)matrix4d.m11;
		this.m12 = (float)matrix4d.m12;
		this.m13 = (float)matrix4d.m13;
		this.m20 = (float)matrix4d.m20;
		this.m21 = (float)matrix4d.m21;
		this.m22 = (float)matrix4d.m22;
		this.m23 = (float)matrix4d.m23;
		this.m30 = (float)matrix4d.m30;
		this.m31 = (float)matrix4d.m31;
		this.m32 = (float)matrix4d.m32;
		this.m33 = (float)matrix4d.m33;
	}

	public Matrix4f(Matrix4f matrix4f) {
		this.m00 = matrix4f.m00;
		this.m01 = matrix4f.m01;
		this.m02 = matrix4f.m02;
		this.m03 = matrix4f.m03;
		this.m10 = matrix4f.m10;
		this.m11 = matrix4f.m11;
		this.m12 = matrix4f.m12;
		this.m13 = matrix4f.m13;
		this.m20 = matrix4f.m20;
		this.m21 = matrix4f.m21;
		this.m22 = matrix4f.m22;
		this.m23 = matrix4f.m23;
		this.m30 = matrix4f.m30;
		this.m31 = matrix4f.m31;
		this.m32 = matrix4f.m32;
		this.m33 = matrix4f.m33;
	}

	public Matrix4f(Matrix3f matrix3f, Vector3f vector3f, float float1) {
		this.m00 = matrix3f.m00 * float1;
		this.m01 = matrix3f.m01 * float1;
		this.m02 = matrix3f.m02 * float1;
		this.m03 = vector3f.x;
		this.m10 = matrix3f.m10 * float1;
		this.m11 = matrix3f.m11 * float1;
		this.m12 = matrix3f.m12 * float1;
		this.m13 = vector3f.y;
		this.m20 = matrix3f.m20 * float1;
		this.m21 = matrix3f.m21 * float1;
		this.m22 = matrix3f.m22 * float1;
		this.m23 = vector3f.z;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.m33 = 1.0F;
	}

	public Matrix4f() {
		this.m00 = 0.0F;
		this.m01 = 0.0F;
		this.m02 = 0.0F;
		this.m03 = 0.0F;
		this.m10 = 0.0F;
		this.m11 = 0.0F;
		this.m12 = 0.0F;
		this.m13 = 0.0F;
		this.m20 = 0.0F;
		this.m21 = 0.0F;
		this.m22 = 0.0F;
		this.m23 = 0.0F;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.m33 = 0.0F;
	}

	public String toString() {
		return this.m00 + ", " + this.m01 + ", " + this.m02 + ", " + this.m03 + "\n" + this.m10 + ", " + this.m11 + ", " + this.m12 + ", " + this.m13 + "\n" + this.m20 + ", " + this.m21 + ", " + this.m22 + ", " + this.m23 + "\n" + this.m30 + ", " + this.m31 + ", " + this.m32 + ", " + this.m33 + "\n";
	}

	public final void setIdentity() {
		this.m00 = 1.0F;
		this.m01 = 0.0F;
		this.m02 = 0.0F;
		this.m03 = 0.0F;
		this.m10 = 0.0F;
		this.m11 = 1.0F;
		this.m12 = 0.0F;
		this.m13 = 0.0F;
		this.m20 = 0.0F;
		this.m21 = 0.0F;
		this.m22 = 1.0F;
		this.m23 = 0.0F;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.m33 = 1.0F;
	}

	public final void setElement(int int1, int int2, float float1) {
		switch (int1) {
		case 0: 
			switch (int2) {
			case 0: 
				this.m00 = float1;
				return;
			
			case 1: 
				this.m01 = float1;
				return;
			
			case 2: 
				this.m02 = float1;
				return;
			
			case 3: 
				this.m03 = float1;
				return;
			
			default: 
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f0"));
			
			}

		
		case 1: 
			switch (int2) {
			case 0: 
				this.m10 = float1;
				return;
			
			case 1: 
				this.m11 = float1;
				return;
			
			case 2: 
				this.m12 = float1;
				return;
			
			case 3: 
				this.m13 = float1;
				return;
			
			default: 
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f0"));
			
			}

		
		case 2: 
			switch (int2) {
			case 0: 
				this.m20 = float1;
				return;
			
			case 1: 
				this.m21 = float1;
				return;
			
			case 2: 
				this.m22 = float1;
				return;
			
			case 3: 
				this.m23 = float1;
				return;
			
			default: 
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f0"));
			
			}

		
		case 3: 
			switch (int2) {
			case 0: 
				this.m30 = float1;
				return;
			
			case 1: 
				this.m31 = float1;
				return;
			
			case 2: 
				this.m32 = float1;
				return;
			
			case 3: 
				this.m33 = float1;
				return;
			
			default: 
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f0"));
			
			}

		
		default: 
			throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f0"));
		
		}
	}

	public final float getElement(int int1, int int2) {
		switch (int1) {
		case 0: 
			switch (int2) {
			case 0: 
				return this.m00;
			
			case 1: 
				return this.m01;
			
			case 2: 
				return this.m02;
			
			case 3: 
				return this.m03;
			
			default: 
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f1"));
			
			}

		
		case 1: 
			switch (int2) {
			case 0: 
				return this.m10;
			
			case 1: 
				return this.m11;
			
			case 2: 
				return this.m12;
			
			case 3: 
				return this.m13;
			
			default: 
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f1"));
			
			}

		
		case 2: 
			switch (int2) {
			case 0: 
				return this.m20;
			
			case 1: 
				return this.m21;
			
			case 2: 
				return this.m22;
			
			case 3: 
				return this.m23;
			
			default: 
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f1"));
			
			}

		
		case 3: 
			switch (int2) {
			case 0: 
				return this.m30;
			
			case 1: 
				return this.m31;
			
			case 2: 
				return this.m32;
			
			case 3: 
				return this.m33;
			
			}

		
		}
		throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f1"));
	}

	public final void getRow(int int1, Vector4f vector4f) {
		if (int1 == 0) {
			vector4f.x = this.m00;
			vector4f.y = this.m01;
			vector4f.z = this.m02;
			vector4f.w = this.m03;
		} else if (int1 == 1) {
			vector4f.x = this.m10;
			vector4f.y = this.m11;
			vector4f.z = this.m12;
			vector4f.w = this.m13;
		} else if (int1 == 2) {
			vector4f.x = this.m20;
			vector4f.y = this.m21;
			vector4f.z = this.m22;
			vector4f.w = this.m23;
		} else {
			if (int1 != 3) {
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f2"));
			}

			vector4f.x = this.m30;
			vector4f.y = this.m31;
			vector4f.z = this.m32;
			vector4f.w = this.m33;
		}
	}

	public final void getRow(int int1, float[] floatArray) {
		if (int1 == 0) {
			floatArray[0] = this.m00;
			floatArray[1] = this.m01;
			floatArray[2] = this.m02;
			floatArray[3] = this.m03;
		} else if (int1 == 1) {
			floatArray[0] = this.m10;
			floatArray[1] = this.m11;
			floatArray[2] = this.m12;
			floatArray[3] = this.m13;
		} else if (int1 == 2) {
			floatArray[0] = this.m20;
			floatArray[1] = this.m21;
			floatArray[2] = this.m22;
			floatArray[3] = this.m23;
		} else {
			if (int1 != 3) {
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f2"));
			}

			floatArray[0] = this.m30;
			floatArray[1] = this.m31;
			floatArray[2] = this.m32;
			floatArray[3] = this.m33;
		}
	}

	public final void getColumn(int int1, Vector4f vector4f) {
		if (int1 == 0) {
			vector4f.x = this.m00;
			vector4f.y = this.m10;
			vector4f.z = this.m20;
			vector4f.w = this.m30;
		} else if (int1 == 1) {
			vector4f.x = this.m01;
			vector4f.y = this.m11;
			vector4f.z = this.m21;
			vector4f.w = this.m31;
		} else if (int1 == 2) {
			vector4f.x = this.m02;
			vector4f.y = this.m12;
			vector4f.z = this.m22;
			vector4f.w = this.m32;
		} else {
			if (int1 != 3) {
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f4"));
			}

			vector4f.x = this.m03;
			vector4f.y = this.m13;
			vector4f.z = this.m23;
			vector4f.w = this.m33;
		}
	}

	public final void getColumn(int int1, float[] floatArray) {
		if (int1 == 0) {
			floatArray[0] = this.m00;
			floatArray[1] = this.m10;
			floatArray[2] = this.m20;
			floatArray[3] = this.m30;
		} else if (int1 == 1) {
			floatArray[0] = this.m01;
			floatArray[1] = this.m11;
			floatArray[2] = this.m21;
			floatArray[3] = this.m31;
		} else if (int1 == 2) {
			floatArray[0] = this.m02;
			floatArray[1] = this.m12;
			floatArray[2] = this.m22;
			floatArray[3] = this.m32;
		} else {
			if (int1 != 3) {
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f4"));
			}

			floatArray[0] = this.m03;
			floatArray[1] = this.m13;
			floatArray[2] = this.m23;
			floatArray[3] = this.m33;
		}
	}

	public final void setScale(float float1) {
		double[] doubleArray = new double[9];
		double[] doubleArray2 = new double[3];
		this.getScaleRotate(doubleArray2, doubleArray);
		this.m00 = (float)(doubleArray[0] * (double)float1);
		this.m01 = (float)(doubleArray[1] * (double)float1);
		this.m02 = (float)(doubleArray[2] * (double)float1);
		this.m10 = (float)(doubleArray[3] * (double)float1);
		this.m11 = (float)(doubleArray[4] * (double)float1);
		this.m12 = (float)(doubleArray[5] * (double)float1);
		this.m20 = (float)(doubleArray[6] * (double)float1);
		this.m21 = (float)(doubleArray[7] * (double)float1);
		this.m22 = (float)(doubleArray[8] * (double)float1);
	}

	public final void get(Matrix3d matrix3d) {
		double[] doubleArray = new double[9];
		double[] doubleArray2 = new double[3];
		this.getScaleRotate(doubleArray2, doubleArray);
		matrix3d.m00 = doubleArray[0];
		matrix3d.m01 = doubleArray[1];
		matrix3d.m02 = doubleArray[2];
		matrix3d.m10 = doubleArray[3];
		matrix3d.m11 = doubleArray[4];
		matrix3d.m12 = doubleArray[5];
		matrix3d.m20 = doubleArray[6];
		matrix3d.m21 = doubleArray[7];
		matrix3d.m22 = doubleArray[8];
	}

	public final void get(Matrix3f matrix3f) {
		double[] doubleArray = new double[9];
		double[] doubleArray2 = new double[3];
		this.getScaleRotate(doubleArray2, doubleArray);
		matrix3f.m00 = (float)doubleArray[0];
		matrix3f.m01 = (float)doubleArray[1];
		matrix3f.m02 = (float)doubleArray[2];
		matrix3f.m10 = (float)doubleArray[3];
		matrix3f.m11 = (float)doubleArray[4];
		matrix3f.m12 = (float)doubleArray[5];
		matrix3f.m20 = (float)doubleArray[6];
		matrix3f.m21 = (float)doubleArray[7];
		matrix3f.m22 = (float)doubleArray[8];
	}

	public final float get(Matrix3f matrix3f, Vector3f vector3f) {
		double[] doubleArray = new double[9];
		double[] doubleArray2 = new double[3];
		this.getScaleRotate(doubleArray2, doubleArray);
		matrix3f.m00 = (float)doubleArray[0];
		matrix3f.m01 = (float)doubleArray[1];
		matrix3f.m02 = (float)doubleArray[2];
		matrix3f.m10 = (float)doubleArray[3];
		matrix3f.m11 = (float)doubleArray[4];
		matrix3f.m12 = (float)doubleArray[5];
		matrix3f.m20 = (float)doubleArray[6];
		matrix3f.m21 = (float)doubleArray[7];
		matrix3f.m22 = (float)doubleArray[8];
		vector3f.x = this.m03;
		vector3f.y = this.m13;
		vector3f.z = this.m23;
		return (float)Matrix3d.max3(doubleArray2);
	}

	public final void get(Quat4f quat4f) {
		double[] doubleArray = new double[9];
		double[] doubleArray2 = new double[3];
		this.getScaleRotate(doubleArray2, doubleArray);
		double double1 = 0.25 * (1.0 + doubleArray[0] + doubleArray[4] + doubleArray[8]);
		if (!((double1 < 0.0 ? -double1 : double1) < 1.0E-30)) {
			quat4f.w = (float)Math.sqrt(double1);
			double1 = 0.25 / (double)quat4f.w;
			quat4f.x = (float)((doubleArray[7] - doubleArray[5]) * double1);
			quat4f.y = (float)((doubleArray[2] - doubleArray[6]) * double1);
			quat4f.z = (float)((doubleArray[3] - doubleArray[1]) * double1);
		} else {
			quat4f.w = 0.0F;
			double1 = -0.5 * (doubleArray[4] + doubleArray[8]);
			if (!((double1 < 0.0 ? -double1 : double1) < 1.0E-30)) {
				quat4f.x = (float)Math.sqrt(double1);
				double1 = 0.5 / (double)quat4f.x;
				quat4f.y = (float)(doubleArray[3] * double1);
				quat4f.z = (float)(doubleArray[6] * double1);
			} else {
				quat4f.x = 0.0F;
				double1 = 0.5 * (1.0 - doubleArray[8]);
				if (!((double1 < 0.0 ? -double1 : double1) < 1.0E-30)) {
					quat4f.y = (float)Math.sqrt(double1);
					quat4f.z = (float)(doubleArray[7] / (2.0 * (double)quat4f.y));
				} else {
					quat4f.y = 0.0F;
					quat4f.z = 1.0F;
				}
			}
		}
	}

	public final void get(Vector3f vector3f) {
		vector3f.x = this.m03;
		vector3f.y = this.m13;
		vector3f.z = this.m23;
	}

	public final void getRotationScale(Matrix3f matrix3f) {
		matrix3f.m00 = this.m00;
		matrix3f.m01 = this.m01;
		matrix3f.m02 = this.m02;
		matrix3f.m10 = this.m10;
		matrix3f.m11 = this.m11;
		matrix3f.m12 = this.m12;
		matrix3f.m20 = this.m20;
		matrix3f.m21 = this.m21;
		matrix3f.m22 = this.m22;
	}

	public final float getScale() {
		double[] doubleArray = new double[9];
		double[] doubleArray2 = new double[3];
		this.getScaleRotate(doubleArray2, doubleArray);
		return (float)Matrix3d.max3(doubleArray2);
	}

	public final void setRotationScale(Matrix3f matrix3f) {
		this.m00 = matrix3f.m00;
		this.m01 = matrix3f.m01;
		this.m02 = matrix3f.m02;
		this.m10 = matrix3f.m10;
		this.m11 = matrix3f.m11;
		this.m12 = matrix3f.m12;
		this.m20 = matrix3f.m20;
		this.m21 = matrix3f.m21;
		this.m22 = matrix3f.m22;
	}

	public final void setRow(int int1, float float1, float float2, float float3, float float4) {
		switch (int1) {
		case 0: 
			this.m00 = float1;
			this.m01 = float2;
			this.m02 = float3;
			this.m03 = float4;
			break;
		
		case 1: 
			this.m10 = float1;
			this.m11 = float2;
			this.m12 = float3;
			this.m13 = float4;
			break;
		
		case 2: 
			this.m20 = float1;
			this.m21 = float2;
			this.m22 = float3;
			this.m23 = float4;
			break;
		
		case 3: 
			this.m30 = float1;
			this.m31 = float2;
			this.m32 = float3;
			this.m33 = float4;
			break;
		
		default: 
			throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f6"));
		
		}
	}

	public final void setRow(int int1, Vector4f vector4f) {
		switch (int1) {
		case 0: 
			this.m00 = vector4f.x;
			this.m01 = vector4f.y;
			this.m02 = vector4f.z;
			this.m03 = vector4f.w;
			break;
		
		case 1: 
			this.m10 = vector4f.x;
			this.m11 = vector4f.y;
			this.m12 = vector4f.z;
			this.m13 = vector4f.w;
			break;
		
		case 2: 
			this.m20 = vector4f.x;
			this.m21 = vector4f.y;
			this.m22 = vector4f.z;
			this.m23 = vector4f.w;
			break;
		
		case 3: 
			this.m30 = vector4f.x;
			this.m31 = vector4f.y;
			this.m32 = vector4f.z;
			this.m33 = vector4f.w;
			break;
		
		default: 
			throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f6"));
		
		}
	}

	public final void setRow(int int1, float[] floatArray) {
		switch (int1) {
		case 0: 
			this.m00 = floatArray[0];
			this.m01 = floatArray[1];
			this.m02 = floatArray[2];
			this.m03 = floatArray[3];
			break;
		
		case 1: 
			this.m10 = floatArray[0];
			this.m11 = floatArray[1];
			this.m12 = floatArray[2];
			this.m13 = floatArray[3];
			break;
		
		case 2: 
			this.m20 = floatArray[0];
			this.m21 = floatArray[1];
			this.m22 = floatArray[2];
			this.m23 = floatArray[3];
			break;
		
		case 3: 
			this.m30 = floatArray[0];
			this.m31 = floatArray[1];
			this.m32 = floatArray[2];
			this.m33 = floatArray[3];
			break;
		
		default: 
			throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f6"));
		
		}
	}

	public final void setColumn(int int1, float float1, float float2, float float3, float float4) {
		switch (int1) {
		case 0: 
			this.m00 = float1;
			this.m10 = float2;
			this.m20 = float3;
			this.m30 = float4;
			break;
		
		case 1: 
			this.m01 = float1;
			this.m11 = float2;
			this.m21 = float3;
			this.m31 = float4;
			break;
		
		case 2: 
			this.m02 = float1;
			this.m12 = float2;
			this.m22 = float3;
			this.m32 = float4;
			break;
		
		case 3: 
			this.m03 = float1;
			this.m13 = float2;
			this.m23 = float3;
			this.m33 = float4;
			break;
		
		default: 
			throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f9"));
		
		}
	}

	public final void setColumn(int int1, Vector4f vector4f) {
		switch (int1) {
		case 0: 
			this.m00 = vector4f.x;
			this.m10 = vector4f.y;
			this.m20 = vector4f.z;
			this.m30 = vector4f.w;
			break;
		
		case 1: 
			this.m01 = vector4f.x;
			this.m11 = vector4f.y;
			this.m21 = vector4f.z;
			this.m31 = vector4f.w;
			break;
		
		case 2: 
			this.m02 = vector4f.x;
			this.m12 = vector4f.y;
			this.m22 = vector4f.z;
			this.m32 = vector4f.w;
			break;
		
		case 3: 
			this.m03 = vector4f.x;
			this.m13 = vector4f.y;
			this.m23 = vector4f.z;
			this.m33 = vector4f.w;
			break;
		
		default: 
			throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f9"));
		
		}
	}

	public final void setColumn(int int1, float[] floatArray) {
		switch (int1) {
		case 0: 
			this.m00 = floatArray[0];
			this.m10 = floatArray[1];
			this.m20 = floatArray[2];
			this.m30 = floatArray[3];
			break;
		
		case 1: 
			this.m01 = floatArray[0];
			this.m11 = floatArray[1];
			this.m21 = floatArray[2];
			this.m31 = floatArray[3];
			break;
		
		case 2: 
			this.m02 = floatArray[0];
			this.m12 = floatArray[1];
			this.m22 = floatArray[2];
			this.m32 = floatArray[3];
			break;
		
		case 3: 
			this.m03 = floatArray[0];
			this.m13 = floatArray[1];
			this.m23 = floatArray[2];
			this.m33 = floatArray[3];
			break;
		
		default: 
			throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f9"));
		
		}
	}

	public final void add(float float1) {
		this.m00 += float1;
		this.m01 += float1;
		this.m02 += float1;
		this.m03 += float1;
		this.m10 += float1;
		this.m11 += float1;
		this.m12 += float1;
		this.m13 += float1;
		this.m20 += float1;
		this.m21 += float1;
		this.m22 += float1;
		this.m23 += float1;
		this.m30 += float1;
		this.m31 += float1;
		this.m32 += float1;
		this.m33 += float1;
	}

	public final void add(float float1, Matrix4f matrix4f) {
		this.m00 = matrix4f.m00 + float1;
		this.m01 = matrix4f.m01 + float1;
		this.m02 = matrix4f.m02 + float1;
		this.m03 = matrix4f.m03 + float1;
		this.m10 = matrix4f.m10 + float1;
		this.m11 = matrix4f.m11 + float1;
		this.m12 = matrix4f.m12 + float1;
		this.m13 = matrix4f.m13 + float1;
		this.m20 = matrix4f.m20 + float1;
		this.m21 = matrix4f.m21 + float1;
		this.m22 = matrix4f.m22 + float1;
		this.m23 = matrix4f.m23 + float1;
		this.m30 = matrix4f.m30 + float1;
		this.m31 = matrix4f.m31 + float1;
		this.m32 = matrix4f.m32 + float1;
		this.m33 = matrix4f.m33 + float1;
	}

	public final void add(Matrix4f matrix4f, Matrix4f matrix4f2) {
		this.m00 = matrix4f.m00 + matrix4f2.m00;
		this.m01 = matrix4f.m01 + matrix4f2.m01;
		this.m02 = matrix4f.m02 + matrix4f2.m02;
		this.m03 = matrix4f.m03 + matrix4f2.m03;
		this.m10 = matrix4f.m10 + matrix4f2.m10;
		this.m11 = matrix4f.m11 + matrix4f2.m11;
		this.m12 = matrix4f.m12 + matrix4f2.m12;
		this.m13 = matrix4f.m13 + matrix4f2.m13;
		this.m20 = matrix4f.m20 + matrix4f2.m20;
		this.m21 = matrix4f.m21 + matrix4f2.m21;
		this.m22 = matrix4f.m22 + matrix4f2.m22;
		this.m23 = matrix4f.m23 + matrix4f2.m23;
		this.m30 = matrix4f.m30 + matrix4f2.m30;
		this.m31 = matrix4f.m31 + matrix4f2.m31;
		this.m32 = matrix4f.m32 + matrix4f2.m32;
		this.m33 = matrix4f.m33 + matrix4f2.m33;
	}

	public final void add(Matrix4f matrix4f) {
		this.m00 += matrix4f.m00;
		this.m01 += matrix4f.m01;
		this.m02 += matrix4f.m02;
		this.m03 += matrix4f.m03;
		this.m10 += matrix4f.m10;
		this.m11 += matrix4f.m11;
		this.m12 += matrix4f.m12;
		this.m13 += matrix4f.m13;
		this.m20 += matrix4f.m20;
		this.m21 += matrix4f.m21;
		this.m22 += matrix4f.m22;
		this.m23 += matrix4f.m23;
		this.m30 += matrix4f.m30;
		this.m31 += matrix4f.m31;
		this.m32 += matrix4f.m32;
		this.m33 += matrix4f.m33;
	}

	public final void sub(Matrix4f matrix4f, Matrix4f matrix4f2) {
		this.m00 = matrix4f.m00 - matrix4f2.m00;
		this.m01 = matrix4f.m01 - matrix4f2.m01;
		this.m02 = matrix4f.m02 - matrix4f2.m02;
		this.m03 = matrix4f.m03 - matrix4f2.m03;
		this.m10 = matrix4f.m10 - matrix4f2.m10;
		this.m11 = matrix4f.m11 - matrix4f2.m11;
		this.m12 = matrix4f.m12 - matrix4f2.m12;
		this.m13 = matrix4f.m13 - matrix4f2.m13;
		this.m20 = matrix4f.m20 - matrix4f2.m20;
		this.m21 = matrix4f.m21 - matrix4f2.m21;
		this.m22 = matrix4f.m22 - matrix4f2.m22;
		this.m23 = matrix4f.m23 - matrix4f2.m23;
		this.m30 = matrix4f.m30 - matrix4f2.m30;
		this.m31 = matrix4f.m31 - matrix4f2.m31;
		this.m32 = matrix4f.m32 - matrix4f2.m32;
		this.m33 = matrix4f.m33 - matrix4f2.m33;
	}

	public final void sub(Matrix4f matrix4f) {
		this.m00 -= matrix4f.m00;
		this.m01 -= matrix4f.m01;
		this.m02 -= matrix4f.m02;
		this.m03 -= matrix4f.m03;
		this.m10 -= matrix4f.m10;
		this.m11 -= matrix4f.m11;
		this.m12 -= matrix4f.m12;
		this.m13 -= matrix4f.m13;
		this.m20 -= matrix4f.m20;
		this.m21 -= matrix4f.m21;
		this.m22 -= matrix4f.m22;
		this.m23 -= matrix4f.m23;
		this.m30 -= matrix4f.m30;
		this.m31 -= matrix4f.m31;
		this.m32 -= matrix4f.m32;
		this.m33 -= matrix4f.m33;
	}

	public final void transpose() {
		float float1 = this.m10;
		this.m10 = this.m01;
		this.m01 = float1;
		float1 = this.m20;
		this.m20 = this.m02;
		this.m02 = float1;
		float1 = this.m30;
		this.m30 = this.m03;
		this.m03 = float1;
		float1 = this.m21;
		this.m21 = this.m12;
		this.m12 = float1;
		float1 = this.m31;
		this.m31 = this.m13;
		this.m13 = float1;
		float1 = this.m32;
		this.m32 = this.m23;
		this.m23 = float1;
	}

	public final void transpose(Matrix4f matrix4f) {
		if (this != matrix4f) {
			this.m00 = matrix4f.m00;
			this.m01 = matrix4f.m10;
			this.m02 = matrix4f.m20;
			this.m03 = matrix4f.m30;
			this.m10 = matrix4f.m01;
			this.m11 = matrix4f.m11;
			this.m12 = matrix4f.m21;
			this.m13 = matrix4f.m31;
			this.m20 = matrix4f.m02;
			this.m21 = matrix4f.m12;
			this.m22 = matrix4f.m22;
			this.m23 = matrix4f.m32;
			this.m30 = matrix4f.m03;
			this.m31 = matrix4f.m13;
			this.m32 = matrix4f.m23;
			this.m33 = matrix4f.m33;
		} else {
			this.transpose();
		}
	}

	public final void set(Quat4f quat4f) {
		this.m00 = 1.0F - 2.0F * quat4f.y * quat4f.y - 2.0F * quat4f.z * quat4f.z;
		this.m10 = 2.0F * (quat4f.x * quat4f.y + quat4f.w * quat4f.z);
		this.m20 = 2.0F * (quat4f.x * quat4f.z - quat4f.w * quat4f.y);
		this.m01 = 2.0F * (quat4f.x * quat4f.y - quat4f.w * quat4f.z);
		this.m11 = 1.0F - 2.0F * quat4f.x * quat4f.x - 2.0F * quat4f.z * quat4f.z;
		this.m21 = 2.0F * (quat4f.y * quat4f.z + quat4f.w * quat4f.x);
		this.m02 = 2.0F * (quat4f.x * quat4f.z + quat4f.w * quat4f.y);
		this.m12 = 2.0F * (quat4f.y * quat4f.z - quat4f.w * quat4f.x);
		this.m22 = 1.0F - 2.0F * quat4f.x * quat4f.x - 2.0F * quat4f.y * quat4f.y;
		this.m03 = 0.0F;
		this.m13 = 0.0F;
		this.m23 = 0.0F;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.m33 = 1.0F;
	}

	public final void set(AxisAngle4f axisAngle4f) {
		float float1 = (float)Math.sqrt((double)(axisAngle4f.x * axisAngle4f.x + axisAngle4f.y * axisAngle4f.y + axisAngle4f.z * axisAngle4f.z));
		if ((double)float1 < 1.0E-8) {
			this.m00 = 1.0F;
			this.m01 = 0.0F;
			this.m02 = 0.0F;
			this.m10 = 0.0F;
			this.m11 = 1.0F;
			this.m12 = 0.0F;
			this.m20 = 0.0F;
			this.m21 = 0.0F;
			this.m22 = 1.0F;
		} else {
			float1 = 1.0F / float1;
			float float2 = axisAngle4f.x * float1;
			float float3 = axisAngle4f.y * float1;
			float float4 = axisAngle4f.z * float1;
			float float5 = (float)Math.sin((double)axisAngle4f.angle);
			float float6 = (float)Math.cos((double)axisAngle4f.angle);
			float float7 = 1.0F - float6;
			float float8 = float2 * float4;
			float float9 = float2 * float3;
			float float10 = float3 * float4;
			this.m00 = float7 * float2 * float2 + float6;
			this.m01 = float7 * float9 - float5 * float4;
			this.m02 = float7 * float8 + float5 * float3;
			this.m10 = float7 * float9 + float5 * float4;
			this.m11 = float7 * float3 * float3 + float6;
			this.m12 = float7 * float10 - float5 * float2;
			this.m20 = float7 * float8 - float5 * float3;
			this.m21 = float7 * float10 + float5 * float2;
			this.m22 = float7 * float4 * float4 + float6;
		}

		this.m03 = 0.0F;
		this.m13 = 0.0F;
		this.m23 = 0.0F;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.m33 = 1.0F;
	}

	public final void set(Quat4d quat4d) {
		this.m00 = (float)(1.0 - 2.0 * quat4d.y * quat4d.y - 2.0 * quat4d.z * quat4d.z);
		this.m10 = (float)(2.0 * (quat4d.x * quat4d.y + quat4d.w * quat4d.z));
		this.m20 = (float)(2.0 * (quat4d.x * quat4d.z - quat4d.w * quat4d.y));
		this.m01 = (float)(2.0 * (quat4d.x * quat4d.y - quat4d.w * quat4d.z));
		this.m11 = (float)(1.0 - 2.0 * quat4d.x * quat4d.x - 2.0 * quat4d.z * quat4d.z);
		this.m21 = (float)(2.0 * (quat4d.y * quat4d.z + quat4d.w * quat4d.x));
		this.m02 = (float)(2.0 * (quat4d.x * quat4d.z + quat4d.w * quat4d.y));
		this.m12 = (float)(2.0 * (quat4d.y * quat4d.z - quat4d.w * quat4d.x));
		this.m22 = (float)(1.0 - 2.0 * quat4d.x * quat4d.x - 2.0 * quat4d.y * quat4d.y);
		this.m03 = 0.0F;
		this.m13 = 0.0F;
		this.m23 = 0.0F;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.m33 = 1.0F;
	}

	public final void set(AxisAngle4d axisAngle4d) {
		double double1 = Math.sqrt(axisAngle4d.x * axisAngle4d.x + axisAngle4d.y * axisAngle4d.y + axisAngle4d.z * axisAngle4d.z);
		if (double1 < 1.0E-8) {
			this.m00 = 1.0F;
			this.m01 = 0.0F;
			this.m02 = 0.0F;
			this.m10 = 0.0F;
			this.m11 = 1.0F;
			this.m12 = 0.0F;
			this.m20 = 0.0F;
			this.m21 = 0.0F;
			this.m22 = 1.0F;
		} else {
			double1 = 1.0 / double1;
			double double2 = axisAngle4d.x * double1;
			double double3 = axisAngle4d.y * double1;
			double double4 = axisAngle4d.z * double1;
			float float1 = (float)Math.sin(axisAngle4d.angle);
			float float2 = (float)Math.cos(axisAngle4d.angle);
			float float3 = 1.0F - float2;
			float float4 = (float)(double2 * double4);
			float float5 = (float)(double2 * double3);
			float float6 = (float)(double3 * double4);
			this.m00 = float3 * (float)(double2 * double2) + float2;
			this.m01 = float3 * float5 - float1 * (float)double4;
			this.m02 = float3 * float4 + float1 * (float)double3;
			this.m10 = float3 * float5 + float1 * (float)double4;
			this.m11 = float3 * (float)(double3 * double3) + float2;
			this.m12 = float3 * float6 - float1 * (float)double2;
			this.m20 = float3 * float4 - float1 * (float)double3;
			this.m21 = float3 * float6 + float1 * (float)double2;
			this.m22 = float3 * (float)(double4 * double4) + float2;
		}

		this.m03 = 0.0F;
		this.m13 = 0.0F;
		this.m23 = 0.0F;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.m33 = 1.0F;
	}

	public final void set(Quat4d quat4d, Vector3d vector3d, double double1) {
		this.m00 = (float)(double1 * (1.0 - 2.0 * quat4d.y * quat4d.y - 2.0 * quat4d.z * quat4d.z));
		this.m10 = (float)(double1 * 2.0 * (quat4d.x * quat4d.y + quat4d.w * quat4d.z));
		this.m20 = (float)(double1 * 2.0 * (quat4d.x * quat4d.z - quat4d.w * quat4d.y));
		this.m01 = (float)(double1 * 2.0 * (quat4d.x * quat4d.y - quat4d.w * quat4d.z));
		this.m11 = (float)(double1 * (1.0 - 2.0 * quat4d.x * quat4d.x - 2.0 * quat4d.z * quat4d.z));
		this.m21 = (float)(double1 * 2.0 * (quat4d.y * quat4d.z + quat4d.w * quat4d.x));
		this.m02 = (float)(double1 * 2.0 * (quat4d.x * quat4d.z + quat4d.w * quat4d.y));
		this.m12 = (float)(double1 * 2.0 * (quat4d.y * quat4d.z - quat4d.w * quat4d.x));
		this.m22 = (float)(double1 * (1.0 - 2.0 * quat4d.x * quat4d.x - 2.0 * quat4d.y * quat4d.y));
		this.m03 = (float)vector3d.x;
		this.m13 = (float)vector3d.y;
		this.m23 = (float)vector3d.z;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.m33 = 1.0F;
	}

	public final void set(Quat4f quat4f, Vector3f vector3f, float float1) {
		this.m00 = float1 * (1.0F - 2.0F * quat4f.y * quat4f.y - 2.0F * quat4f.z * quat4f.z);
		this.m10 = float1 * 2.0F * (quat4f.x * quat4f.y + quat4f.w * quat4f.z);
		this.m20 = float1 * 2.0F * (quat4f.x * quat4f.z - quat4f.w * quat4f.y);
		this.m01 = float1 * 2.0F * (quat4f.x * quat4f.y - quat4f.w * quat4f.z);
		this.m11 = float1 * (1.0F - 2.0F * quat4f.x * quat4f.x - 2.0F * quat4f.z * quat4f.z);
		this.m21 = float1 * 2.0F * (quat4f.y * quat4f.z + quat4f.w * quat4f.x);
		this.m02 = float1 * 2.0F * (quat4f.x * quat4f.z + quat4f.w * quat4f.y);
		this.m12 = float1 * 2.0F * (quat4f.y * quat4f.z - quat4f.w * quat4f.x);
		this.m22 = float1 * (1.0F - 2.0F * quat4f.x * quat4f.x - 2.0F * quat4f.y * quat4f.y);
		this.m03 = vector3f.x;
		this.m13 = vector3f.y;
		this.m23 = vector3f.z;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.m33 = 1.0F;
	}

	public final void set(Matrix4d matrix4d) {
		this.m00 = (float)matrix4d.m00;
		this.m01 = (float)matrix4d.m01;
		this.m02 = (float)matrix4d.m02;
		this.m03 = (float)matrix4d.m03;
		this.m10 = (float)matrix4d.m10;
		this.m11 = (float)matrix4d.m11;
		this.m12 = (float)matrix4d.m12;
		this.m13 = (float)matrix4d.m13;
		this.m20 = (float)matrix4d.m20;
		this.m21 = (float)matrix4d.m21;
		this.m22 = (float)matrix4d.m22;
		this.m23 = (float)matrix4d.m23;
		this.m30 = (float)matrix4d.m30;
		this.m31 = (float)matrix4d.m31;
		this.m32 = (float)matrix4d.m32;
		this.m33 = (float)matrix4d.m33;
	}

	public final void set(Matrix4f matrix4f) {
		this.m00 = matrix4f.m00;
		this.m01 = matrix4f.m01;
		this.m02 = matrix4f.m02;
		this.m03 = matrix4f.m03;
		this.m10 = matrix4f.m10;
		this.m11 = matrix4f.m11;
		this.m12 = matrix4f.m12;
		this.m13 = matrix4f.m13;
		this.m20 = matrix4f.m20;
		this.m21 = matrix4f.m21;
		this.m22 = matrix4f.m22;
		this.m23 = matrix4f.m23;
		this.m30 = matrix4f.m30;
		this.m31 = matrix4f.m31;
		this.m32 = matrix4f.m32;
		this.m33 = matrix4f.m33;
	}

	public final void invert(Matrix4f matrix4f) {
		this.invertGeneral(matrix4f);
	}

	public final void invert() {
		this.invertGeneral(this);
	}

	final void invertGeneral(Matrix4f matrix4f) {
		double[] doubleArray = new double[16];
		double[] doubleArray2 = new double[16];
		int[] intArray = new int[4];
		doubleArray[0] = (double)matrix4f.m00;
		doubleArray[1] = (double)matrix4f.m01;
		doubleArray[2] = (double)matrix4f.m02;
		doubleArray[3] = (double)matrix4f.m03;
		doubleArray[4] = (double)matrix4f.m10;
		doubleArray[5] = (double)matrix4f.m11;
		doubleArray[6] = (double)matrix4f.m12;
		doubleArray[7] = (double)matrix4f.m13;
		doubleArray[8] = (double)matrix4f.m20;
		doubleArray[9] = (double)matrix4f.m21;
		doubleArray[10] = (double)matrix4f.m22;
		doubleArray[11] = (double)matrix4f.m23;
		doubleArray[12] = (double)matrix4f.m30;
		doubleArray[13] = (double)matrix4f.m31;
		doubleArray[14] = (double)matrix4f.m32;
		doubleArray[15] = (double)matrix4f.m33;
		if (!luDecomposition(doubleArray, intArray)) {
			throw new SingularMatrixException(VecMathI18N.getString("Matrix4f12"));
		} else {
			for (int int1 = 0; int1 < 16; ++int1) {
				doubleArray2[int1] = 0.0;
			}

			doubleArray2[0] = 1.0;
			doubleArray2[5] = 1.0;
			doubleArray2[10] = 1.0;
			doubleArray2[15] = 1.0;
			luBacksubstitution(doubleArray, intArray, doubleArray2);
			this.m00 = (float)doubleArray2[0];
			this.m01 = (float)doubleArray2[1];
			this.m02 = (float)doubleArray2[2];
			this.m03 = (float)doubleArray2[3];
			this.m10 = (float)doubleArray2[4];
			this.m11 = (float)doubleArray2[5];
			this.m12 = (float)doubleArray2[6];
			this.m13 = (float)doubleArray2[7];
			this.m20 = (float)doubleArray2[8];
			this.m21 = (float)doubleArray2[9];
			this.m22 = (float)doubleArray2[10];
			this.m23 = (float)doubleArray2[11];
			this.m30 = (float)doubleArray2[12];
			this.m31 = (float)doubleArray2[13];
			this.m32 = (float)doubleArray2[14];
			this.m33 = (float)doubleArray2[15];
		}
	}

	static boolean luDecomposition(double[] doubleArray, int[] intArray) {
		double[] doubleArray2 = new double[4];
		int int1 = 0;
		int int2 = 0;
		int int3;
		double double1;
		for (int3 = 4; int3-- != 0; doubleArray2[int2++] = 1.0 / double1) {
			double1 = 0.0;
			int int4 = 4;
			while (int4-- != 0) {
				double double2 = doubleArray[int1++];
				double2 = Math.abs(double2);
				if (double2 > double1) {
					double1 = double2;
				}
			}

			if (double1 == 0.0) {
				return false;
			}
		}

		byte byte1 = 0;
		for (int3 = 0; int3 < 4; ++int3) {
			int int5;
			int int6;
			double double3;
			int int7;
			int int8;
			for (int1 = 0; int1 < int3; ++int1) {
				int5 = byte1 + 4 * int1 + int3;
				double3 = doubleArray[int5];
				int7 = int1;
				int8 = byte1 + 4 * int1;
				for (int6 = byte1 + int3; int7-- != 0; int6 += 4) {
					double3 -= doubleArray[int8] * doubleArray[int6];
					++int8;
				}

				doubleArray[int5] = double3;
			}

			double double4 = 0.0;
			int2 = -1;
			double double5;
			for (int1 = int3; int1 < 4; ++int1) {
				int5 = byte1 + 4 * int1 + int3;
				double3 = doubleArray[int5];
				int7 = int3;
				int8 = byte1 + 4 * int1;
				for (int6 = byte1 + int3; int7-- != 0; int6 += 4) {
					double3 -= doubleArray[int8] * doubleArray[int6];
					++int8;
				}

				doubleArray[int5] = double3;
				if ((double5 = doubleArray2[int1] * Math.abs(double3)) >= double4) {
					double4 = double5;
					int2 = int1;
				}
			}

			if (int2 < 0) {
				throw new RuntimeException(VecMathI18N.getString("Matrix4f13"));
			}

			if (int3 != int2) {
				int7 = 4;
				int8 = byte1 + 4 * int2;
				for (int6 = byte1 + 4 * int3; int7-- != 0; doubleArray[int6++] = double5) {
					double5 = doubleArray[int8];
					doubleArray[int8++] = doubleArray[int6];
				}

				doubleArray2[int2] = doubleArray2[int3];
			}

			intArray[int3] = int2;
			if (doubleArray[byte1 + 4 * int3 + int3] == 0.0) {
				return false;
			}

			if (int3 != 3) {
				double5 = 1.0 / doubleArray[byte1 + 4 * int3 + int3];
				int5 = byte1 + 4 * (int3 + 1) + int3;
				for (int1 = 3 - int3; int1-- != 0; int5 += 4) {
					doubleArray[int5] *= double5;
				}
			}
		}

		return true;
	}

	static void luBacksubstitution(double[] doubleArray, int[] intArray, double[] doubleArray2) {
		byte byte1 = 0;
		for (int int1 = 0; int1 < 4; ++int1) {
			int int2 = int1;
			int int3 = -1;
			int int4;
			for (int int5 = 0; int5 < 4; ++int5) {
				int int6 = intArray[byte1 + int5];
				double double1 = doubleArray2[int2 + 4 * int6];
				doubleArray2[int2 + 4 * int6] = doubleArray2[int2 + 4 * int5];
				if (int3 >= 0) {
					int4 = int5 * 4;
					for (int int7 = int3; int7 <= int5 - 1; ++int7) {
						double1 -= doubleArray[int4 + int7] * doubleArray2[int2 + 4 * int7];
					}
				} else if (double1 != 0.0) {
					int3 = int5;
				}

				doubleArray2[int2 + 4 * int5] = double1;
			}

			byte byte2 = 12;
			doubleArray2[int2 + 12] /= doubleArray[byte2 + 3];
			int4 = byte2 - 4;
			doubleArray2[int2 + 8] = (doubleArray2[int2 + 8] - doubleArray[int4 + 3] * doubleArray2[int2 + 12]) / doubleArray[int4 + 2];
			int4 -= 4;
			doubleArray2[int2 + 4] = (doubleArray2[int2 + 4] - doubleArray[int4 + 2] * doubleArray2[int2 + 8] - doubleArray[int4 + 3] * doubleArray2[int2 + 12]) / doubleArray[int4 + 1];
			int4 -= 4;
			doubleArray2[int2 + 0] = (doubleArray2[int2 + 0] - doubleArray[int4 + 1] * doubleArray2[int2 + 4] - doubleArray[int4 + 2] * doubleArray2[int2 + 8] - doubleArray[int4 + 3] * doubleArray2[int2 + 12]) / doubleArray[int4 + 0];
		}
	}

	public final float determinant() {
		float float1 = this.m00 * (this.m11 * this.m22 * this.m33 + this.m12 * this.m23 * this.m31 + this.m13 * this.m21 * this.m32 - this.m13 * this.m22 * this.m31 - this.m11 * this.m23 * this.m32 - this.m12 * this.m21 * this.m33);
		float1 -= this.m01 * (this.m10 * this.m22 * this.m33 + this.m12 * this.m23 * this.m30 + this.m13 * this.m20 * this.m32 - this.m13 * this.m22 * this.m30 - this.m10 * this.m23 * this.m32 - this.m12 * this.m20 * this.m33);
		float1 += this.m02 * (this.m10 * this.m21 * this.m33 + this.m11 * this.m23 * this.m30 + this.m13 * this.m20 * this.m31 - this.m13 * this.m21 * this.m30 - this.m10 * this.m23 * this.m31 - this.m11 * this.m20 * this.m33);
		float1 -= this.m03 * (this.m10 * this.m21 * this.m32 + this.m11 * this.m22 * this.m30 + this.m12 * this.m20 * this.m31 - this.m12 * this.m21 * this.m30 - this.m10 * this.m22 * this.m31 - this.m11 * this.m20 * this.m32);
		return float1;
	}

	public final void set(Matrix3f matrix3f) {
		this.m00 = matrix3f.m00;
		this.m01 = matrix3f.m01;
		this.m02 = matrix3f.m02;
		this.m03 = 0.0F;
		this.m10 = matrix3f.m10;
		this.m11 = matrix3f.m11;
		this.m12 = matrix3f.m12;
		this.m13 = 0.0F;
		this.m20 = matrix3f.m20;
		this.m21 = matrix3f.m21;
		this.m22 = matrix3f.m22;
		this.m23 = 0.0F;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.m33 = 1.0F;
	}

	public final void set(Matrix3d matrix3d) {
		this.m00 = (float)matrix3d.m00;
		this.m01 = (float)matrix3d.m01;
		this.m02 = (float)matrix3d.m02;
		this.m03 = 0.0F;
		this.m10 = (float)matrix3d.m10;
		this.m11 = (float)matrix3d.m11;
		this.m12 = (float)matrix3d.m12;
		this.m13 = 0.0F;
		this.m20 = (float)matrix3d.m20;
		this.m21 = (float)matrix3d.m21;
		this.m22 = (float)matrix3d.m22;
		this.m23 = 0.0F;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.m33 = 1.0F;
	}

	public final void set(float float1) {
		this.m00 = float1;
		this.m01 = 0.0F;
		this.m02 = 0.0F;
		this.m03 = 0.0F;
		this.m10 = 0.0F;
		this.m11 = float1;
		this.m12 = 0.0F;
		this.m13 = 0.0F;
		this.m20 = 0.0F;
		this.m21 = 0.0F;
		this.m22 = float1;
		this.m23 = 0.0F;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.m33 = 1.0F;
	}

	public final void set(float[] floatArray) {
		this.m00 = floatArray[0];
		this.m01 = floatArray[1];
		this.m02 = floatArray[2];
		this.m03 = floatArray[3];
		this.m10 = floatArray[4];
		this.m11 = floatArray[5];
		this.m12 = floatArray[6];
		this.m13 = floatArray[7];
		this.m20 = floatArray[8];
		this.m21 = floatArray[9];
		this.m22 = floatArray[10];
		this.m23 = floatArray[11];
		this.m30 = floatArray[12];
		this.m31 = floatArray[13];
		this.m32 = floatArray[14];
		this.m33 = floatArray[15];
	}

	public final void set(Vector3f vector3f) {
		this.m00 = 1.0F;
		this.m01 = 0.0F;
		this.m02 = 0.0F;
		this.m03 = vector3f.x;
		this.m10 = 0.0F;
		this.m11 = 1.0F;
		this.m12 = 0.0F;
		this.m13 = vector3f.y;
		this.m20 = 0.0F;
		this.m21 = 0.0F;
		this.m22 = 1.0F;
		this.m23 = vector3f.z;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.m33 = 1.0F;
	}

	public final void set(float float1, Vector3f vector3f) {
		this.m00 = float1;
		this.m01 = 0.0F;
		this.m02 = 0.0F;
		this.m03 = vector3f.x;
		this.m10 = 0.0F;
		this.m11 = float1;
		this.m12 = 0.0F;
		this.m13 = vector3f.y;
		this.m20 = 0.0F;
		this.m21 = 0.0F;
		this.m22 = float1;
		this.m23 = vector3f.z;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.m33 = 1.0F;
	}

	public final void set(Vector3f vector3f, float float1) {
		this.m00 = float1;
		this.m01 = 0.0F;
		this.m02 = 0.0F;
		this.m03 = float1 * vector3f.x;
		this.m10 = 0.0F;
		this.m11 = float1;
		this.m12 = 0.0F;
		this.m13 = float1 * vector3f.y;
		this.m20 = 0.0F;
		this.m21 = 0.0F;
		this.m22 = float1;
		this.m23 = float1 * vector3f.z;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.m33 = 1.0F;
	}

	public final void set(Matrix3f matrix3f, Vector3f vector3f, float float1) {
		this.m00 = matrix3f.m00 * float1;
		this.m01 = matrix3f.m01 * float1;
		this.m02 = matrix3f.m02 * float1;
		this.m03 = vector3f.x;
		this.m10 = matrix3f.m10 * float1;
		this.m11 = matrix3f.m11 * float1;
		this.m12 = matrix3f.m12 * float1;
		this.m13 = vector3f.y;
		this.m20 = matrix3f.m20 * float1;
		this.m21 = matrix3f.m21 * float1;
		this.m22 = matrix3f.m22 * float1;
		this.m23 = vector3f.z;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.m33 = 1.0F;
	}

	public final void set(Matrix3d matrix3d, Vector3d vector3d, double double1) {
		this.m00 = (float)(matrix3d.m00 * double1);
		this.m01 = (float)(matrix3d.m01 * double1);
		this.m02 = (float)(matrix3d.m02 * double1);
		this.m03 = (float)vector3d.x;
		this.m10 = (float)(matrix3d.m10 * double1);
		this.m11 = (float)(matrix3d.m11 * double1);
		this.m12 = (float)(matrix3d.m12 * double1);
		this.m13 = (float)vector3d.y;
		this.m20 = (float)(matrix3d.m20 * double1);
		this.m21 = (float)(matrix3d.m21 * double1);
		this.m22 = (float)(matrix3d.m22 * double1);
		this.m23 = (float)vector3d.z;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.m33 = 1.0F;
	}

	public final void setTranslation(Vector3f vector3f) {
		this.m03 = vector3f.x;
		this.m13 = vector3f.y;
		this.m23 = vector3f.z;
	}

	public final void rotX(float float1) {
		float float2 = (float)Math.sin((double)float1);
		float float3 = (float)Math.cos((double)float1);
		this.m00 = 1.0F;
		this.m01 = 0.0F;
		this.m02 = 0.0F;
		this.m03 = 0.0F;
		this.m10 = 0.0F;
		this.m11 = float3;
		this.m12 = -float2;
		this.m13 = 0.0F;
		this.m20 = 0.0F;
		this.m21 = float2;
		this.m22 = float3;
		this.m23 = 0.0F;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.m33 = 1.0F;
	}

	public final void rotY(float float1) {
		float float2 = (float)Math.sin((double)float1);
		float float3 = (float)Math.cos((double)float1);
		this.m00 = float3;
		this.m01 = 0.0F;
		this.m02 = float2;
		this.m03 = 0.0F;
		this.m10 = 0.0F;
		this.m11 = 1.0F;
		this.m12 = 0.0F;
		this.m13 = 0.0F;
		this.m20 = -float2;
		this.m21 = 0.0F;
		this.m22 = float3;
		this.m23 = 0.0F;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.m33 = 1.0F;
	}

	public final void rotZ(float float1) {
		float float2 = (float)Math.sin((double)float1);
		float float3 = (float)Math.cos((double)float1);
		this.m00 = float3;
		this.m01 = -float2;
		this.m02 = 0.0F;
		this.m03 = 0.0F;
		this.m10 = float2;
		this.m11 = float3;
		this.m12 = 0.0F;
		this.m13 = 0.0F;
		this.m20 = 0.0F;
		this.m21 = 0.0F;
		this.m22 = 1.0F;
		this.m23 = 0.0F;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.m33 = 1.0F;
	}

	public final void mul(float float1) {
		this.m00 *= float1;
		this.m01 *= float1;
		this.m02 *= float1;
		this.m03 *= float1;
		this.m10 *= float1;
		this.m11 *= float1;
		this.m12 *= float1;
		this.m13 *= float1;
		this.m20 *= float1;
		this.m21 *= float1;
		this.m22 *= float1;
		this.m23 *= float1;
		this.m30 *= float1;
		this.m31 *= float1;
		this.m32 *= float1;
		this.m33 *= float1;
	}

	public final void mul(float float1, Matrix4f matrix4f) {
		this.m00 = matrix4f.m00 * float1;
		this.m01 = matrix4f.m01 * float1;
		this.m02 = matrix4f.m02 * float1;
		this.m03 = matrix4f.m03 * float1;
		this.m10 = matrix4f.m10 * float1;
		this.m11 = matrix4f.m11 * float1;
		this.m12 = matrix4f.m12 * float1;
		this.m13 = matrix4f.m13 * float1;
		this.m20 = matrix4f.m20 * float1;
		this.m21 = matrix4f.m21 * float1;
		this.m22 = matrix4f.m22 * float1;
		this.m23 = matrix4f.m23 * float1;
		this.m30 = matrix4f.m30 * float1;
		this.m31 = matrix4f.m31 * float1;
		this.m32 = matrix4f.m32 * float1;
		this.m33 = matrix4f.m33 * float1;
	}

	public final void mul(Matrix4f matrix4f) {
		float float1 = this.m00 * matrix4f.m00 + this.m01 * matrix4f.m10 + this.m02 * matrix4f.m20 + this.m03 * matrix4f.m30;
		float float2 = this.m00 * matrix4f.m01 + this.m01 * matrix4f.m11 + this.m02 * matrix4f.m21 + this.m03 * matrix4f.m31;
		float float3 = this.m00 * matrix4f.m02 + this.m01 * matrix4f.m12 + this.m02 * matrix4f.m22 + this.m03 * matrix4f.m32;
		float float4 = this.m00 * matrix4f.m03 + this.m01 * matrix4f.m13 + this.m02 * matrix4f.m23 + this.m03 * matrix4f.m33;
		float float5 = this.m10 * matrix4f.m00 + this.m11 * matrix4f.m10 + this.m12 * matrix4f.m20 + this.m13 * matrix4f.m30;
		float float6 = this.m10 * matrix4f.m01 + this.m11 * matrix4f.m11 + this.m12 * matrix4f.m21 + this.m13 * matrix4f.m31;
		float float7 = this.m10 * matrix4f.m02 + this.m11 * matrix4f.m12 + this.m12 * matrix4f.m22 + this.m13 * matrix4f.m32;
		float float8 = this.m10 * matrix4f.m03 + this.m11 * matrix4f.m13 + this.m12 * matrix4f.m23 + this.m13 * matrix4f.m33;
		float float9 = this.m20 * matrix4f.m00 + this.m21 * matrix4f.m10 + this.m22 * matrix4f.m20 + this.m23 * matrix4f.m30;
		float float10 = this.m20 * matrix4f.m01 + this.m21 * matrix4f.m11 + this.m22 * matrix4f.m21 + this.m23 * matrix4f.m31;
		float float11 = this.m20 * matrix4f.m02 + this.m21 * matrix4f.m12 + this.m22 * matrix4f.m22 + this.m23 * matrix4f.m32;
		float float12 = this.m20 * matrix4f.m03 + this.m21 * matrix4f.m13 + this.m22 * matrix4f.m23 + this.m23 * matrix4f.m33;
		float float13 = this.m30 * matrix4f.m00 + this.m31 * matrix4f.m10 + this.m32 * matrix4f.m20 + this.m33 * matrix4f.m30;
		float float14 = this.m30 * matrix4f.m01 + this.m31 * matrix4f.m11 + this.m32 * matrix4f.m21 + this.m33 * matrix4f.m31;
		float float15 = this.m30 * matrix4f.m02 + this.m31 * matrix4f.m12 + this.m32 * matrix4f.m22 + this.m33 * matrix4f.m32;
		float float16 = this.m30 * matrix4f.m03 + this.m31 * matrix4f.m13 + this.m32 * matrix4f.m23 + this.m33 * matrix4f.m33;
		this.m00 = float1;
		this.m01 = float2;
		this.m02 = float3;
		this.m03 = float4;
		this.m10 = float5;
		this.m11 = float6;
		this.m12 = float7;
		this.m13 = float8;
		this.m20 = float9;
		this.m21 = float10;
		this.m22 = float11;
		this.m23 = float12;
		this.m30 = float13;
		this.m31 = float14;
		this.m32 = float15;
		this.m33 = float16;
	}

	public final void mul(Matrix4f matrix4f, Matrix4f matrix4f2) {
		if (this != matrix4f && this != matrix4f2) {
			this.m00 = matrix4f.m00 * matrix4f2.m00 + matrix4f.m01 * matrix4f2.m10 + matrix4f.m02 * matrix4f2.m20 + matrix4f.m03 * matrix4f2.m30;
			this.m01 = matrix4f.m00 * matrix4f2.m01 + matrix4f.m01 * matrix4f2.m11 + matrix4f.m02 * matrix4f2.m21 + matrix4f.m03 * matrix4f2.m31;
			this.m02 = matrix4f.m00 * matrix4f2.m02 + matrix4f.m01 * matrix4f2.m12 + matrix4f.m02 * matrix4f2.m22 + matrix4f.m03 * matrix4f2.m32;
			this.m03 = matrix4f.m00 * matrix4f2.m03 + matrix4f.m01 * matrix4f2.m13 + matrix4f.m02 * matrix4f2.m23 + matrix4f.m03 * matrix4f2.m33;
			this.m10 = matrix4f.m10 * matrix4f2.m00 + matrix4f.m11 * matrix4f2.m10 + matrix4f.m12 * matrix4f2.m20 + matrix4f.m13 * matrix4f2.m30;
			this.m11 = matrix4f.m10 * matrix4f2.m01 + matrix4f.m11 * matrix4f2.m11 + matrix4f.m12 * matrix4f2.m21 + matrix4f.m13 * matrix4f2.m31;
			this.m12 = matrix4f.m10 * matrix4f2.m02 + matrix4f.m11 * matrix4f2.m12 + matrix4f.m12 * matrix4f2.m22 + matrix4f.m13 * matrix4f2.m32;
			this.m13 = matrix4f.m10 * matrix4f2.m03 + matrix4f.m11 * matrix4f2.m13 + matrix4f.m12 * matrix4f2.m23 + matrix4f.m13 * matrix4f2.m33;
			this.m20 = matrix4f.m20 * matrix4f2.m00 + matrix4f.m21 * matrix4f2.m10 + matrix4f.m22 * matrix4f2.m20 + matrix4f.m23 * matrix4f2.m30;
			this.m21 = matrix4f.m20 * matrix4f2.m01 + matrix4f.m21 * matrix4f2.m11 + matrix4f.m22 * matrix4f2.m21 + matrix4f.m23 * matrix4f2.m31;
			this.m22 = matrix4f.m20 * matrix4f2.m02 + matrix4f.m21 * matrix4f2.m12 + matrix4f.m22 * matrix4f2.m22 + matrix4f.m23 * matrix4f2.m32;
			this.m23 = matrix4f.m20 * matrix4f2.m03 + matrix4f.m21 * matrix4f2.m13 + matrix4f.m22 * matrix4f2.m23 + matrix4f.m23 * matrix4f2.m33;
			this.m30 = matrix4f.m30 * matrix4f2.m00 + matrix4f.m31 * matrix4f2.m10 + matrix4f.m32 * matrix4f2.m20 + matrix4f.m33 * matrix4f2.m30;
			this.m31 = matrix4f.m30 * matrix4f2.m01 + matrix4f.m31 * matrix4f2.m11 + matrix4f.m32 * matrix4f2.m21 + matrix4f.m33 * matrix4f2.m31;
			this.m32 = matrix4f.m30 * matrix4f2.m02 + matrix4f.m31 * matrix4f2.m12 + matrix4f.m32 * matrix4f2.m22 + matrix4f.m33 * matrix4f2.m32;
			this.m33 = matrix4f.m30 * matrix4f2.m03 + matrix4f.m31 * matrix4f2.m13 + matrix4f.m32 * matrix4f2.m23 + matrix4f.m33 * matrix4f2.m33;
		} else {
			float float1 = matrix4f.m00 * matrix4f2.m00 + matrix4f.m01 * matrix4f2.m10 + matrix4f.m02 * matrix4f2.m20 + matrix4f.m03 * matrix4f2.m30;
			float float2 = matrix4f.m00 * matrix4f2.m01 + matrix4f.m01 * matrix4f2.m11 + matrix4f.m02 * matrix4f2.m21 + matrix4f.m03 * matrix4f2.m31;
			float float3 = matrix4f.m00 * matrix4f2.m02 + matrix4f.m01 * matrix4f2.m12 + matrix4f.m02 * matrix4f2.m22 + matrix4f.m03 * matrix4f2.m32;
			float float4 = matrix4f.m00 * matrix4f2.m03 + matrix4f.m01 * matrix4f2.m13 + matrix4f.m02 * matrix4f2.m23 + matrix4f.m03 * matrix4f2.m33;
			float float5 = matrix4f.m10 * matrix4f2.m00 + matrix4f.m11 * matrix4f2.m10 + matrix4f.m12 * matrix4f2.m20 + matrix4f.m13 * matrix4f2.m30;
			float float6 = matrix4f.m10 * matrix4f2.m01 + matrix4f.m11 * matrix4f2.m11 + matrix4f.m12 * matrix4f2.m21 + matrix4f.m13 * matrix4f2.m31;
			float float7 = matrix4f.m10 * matrix4f2.m02 + matrix4f.m11 * matrix4f2.m12 + matrix4f.m12 * matrix4f2.m22 + matrix4f.m13 * matrix4f2.m32;
			float float8 = matrix4f.m10 * matrix4f2.m03 + matrix4f.m11 * matrix4f2.m13 + matrix4f.m12 * matrix4f2.m23 + matrix4f.m13 * matrix4f2.m33;
			float float9 = matrix4f.m20 * matrix4f2.m00 + matrix4f.m21 * matrix4f2.m10 + matrix4f.m22 * matrix4f2.m20 + matrix4f.m23 * matrix4f2.m30;
			float float10 = matrix4f.m20 * matrix4f2.m01 + matrix4f.m21 * matrix4f2.m11 + matrix4f.m22 * matrix4f2.m21 + matrix4f.m23 * matrix4f2.m31;
			float float11 = matrix4f.m20 * matrix4f2.m02 + matrix4f.m21 * matrix4f2.m12 + matrix4f.m22 * matrix4f2.m22 + matrix4f.m23 * matrix4f2.m32;
			float float12 = matrix4f.m20 * matrix4f2.m03 + matrix4f.m21 * matrix4f2.m13 + matrix4f.m22 * matrix4f2.m23 + matrix4f.m23 * matrix4f2.m33;
			float float13 = matrix4f.m30 * matrix4f2.m00 + matrix4f.m31 * matrix4f2.m10 + matrix4f.m32 * matrix4f2.m20 + matrix4f.m33 * matrix4f2.m30;
			float float14 = matrix4f.m30 * matrix4f2.m01 + matrix4f.m31 * matrix4f2.m11 + matrix4f.m32 * matrix4f2.m21 + matrix4f.m33 * matrix4f2.m31;
			float float15 = matrix4f.m30 * matrix4f2.m02 + matrix4f.m31 * matrix4f2.m12 + matrix4f.m32 * matrix4f2.m22 + matrix4f.m33 * matrix4f2.m32;
			float float16 = matrix4f.m30 * matrix4f2.m03 + matrix4f.m31 * matrix4f2.m13 + matrix4f.m32 * matrix4f2.m23 + matrix4f.m33 * matrix4f2.m33;
			this.m00 = float1;
			this.m01 = float2;
			this.m02 = float3;
			this.m03 = float4;
			this.m10 = float5;
			this.m11 = float6;
			this.m12 = float7;
			this.m13 = float8;
			this.m20 = float9;
			this.m21 = float10;
			this.m22 = float11;
			this.m23 = float12;
			this.m30 = float13;
			this.m31 = float14;
			this.m32 = float15;
			this.m33 = float16;
		}
	}

	public final void mulTransposeBoth(Matrix4f matrix4f, Matrix4f matrix4f2) {
		if (this != matrix4f && this != matrix4f2) {
			this.m00 = matrix4f.m00 * matrix4f2.m00 + matrix4f.m10 * matrix4f2.m01 + matrix4f.m20 * matrix4f2.m02 + matrix4f.m30 * matrix4f2.m03;
			this.m01 = matrix4f.m00 * matrix4f2.m10 + matrix4f.m10 * matrix4f2.m11 + matrix4f.m20 * matrix4f2.m12 + matrix4f.m30 * matrix4f2.m13;
			this.m02 = matrix4f.m00 * matrix4f2.m20 + matrix4f.m10 * matrix4f2.m21 + matrix4f.m20 * matrix4f2.m22 + matrix4f.m30 * matrix4f2.m23;
			this.m03 = matrix4f.m00 * matrix4f2.m30 + matrix4f.m10 * matrix4f2.m31 + matrix4f.m20 * matrix4f2.m32 + matrix4f.m30 * matrix4f2.m33;
			this.m10 = matrix4f.m01 * matrix4f2.m00 + matrix4f.m11 * matrix4f2.m01 + matrix4f.m21 * matrix4f2.m02 + matrix4f.m31 * matrix4f2.m03;
			this.m11 = matrix4f.m01 * matrix4f2.m10 + matrix4f.m11 * matrix4f2.m11 + matrix4f.m21 * matrix4f2.m12 + matrix4f.m31 * matrix4f2.m13;
			this.m12 = matrix4f.m01 * matrix4f2.m20 + matrix4f.m11 * matrix4f2.m21 + matrix4f.m21 * matrix4f2.m22 + matrix4f.m31 * matrix4f2.m23;
			this.m13 = matrix4f.m01 * matrix4f2.m30 + matrix4f.m11 * matrix4f2.m31 + matrix4f.m21 * matrix4f2.m32 + matrix4f.m31 * matrix4f2.m33;
			this.m20 = matrix4f.m02 * matrix4f2.m00 + matrix4f.m12 * matrix4f2.m01 + matrix4f.m22 * matrix4f2.m02 + matrix4f.m32 * matrix4f2.m03;
			this.m21 = matrix4f.m02 * matrix4f2.m10 + matrix4f.m12 * matrix4f2.m11 + matrix4f.m22 * matrix4f2.m12 + matrix4f.m32 * matrix4f2.m13;
			this.m22 = matrix4f.m02 * matrix4f2.m20 + matrix4f.m12 * matrix4f2.m21 + matrix4f.m22 * matrix4f2.m22 + matrix4f.m32 * matrix4f2.m23;
			this.m23 = matrix4f.m02 * matrix4f2.m30 + matrix4f.m12 * matrix4f2.m31 + matrix4f.m22 * matrix4f2.m32 + matrix4f.m32 * matrix4f2.m33;
			this.m30 = matrix4f.m03 * matrix4f2.m00 + matrix4f.m13 * matrix4f2.m01 + matrix4f.m23 * matrix4f2.m02 + matrix4f.m33 * matrix4f2.m03;
			this.m31 = matrix4f.m03 * matrix4f2.m10 + matrix4f.m13 * matrix4f2.m11 + matrix4f.m23 * matrix4f2.m12 + matrix4f.m33 * matrix4f2.m13;
			this.m32 = matrix4f.m03 * matrix4f2.m20 + matrix4f.m13 * matrix4f2.m21 + matrix4f.m23 * matrix4f2.m22 + matrix4f.m33 * matrix4f2.m23;
			this.m33 = matrix4f.m03 * matrix4f2.m30 + matrix4f.m13 * matrix4f2.m31 + matrix4f.m23 * matrix4f2.m32 + matrix4f.m33 * matrix4f2.m33;
		} else {
			float float1 = matrix4f.m00 * matrix4f2.m00 + matrix4f.m10 * matrix4f2.m01 + matrix4f.m20 * matrix4f2.m02 + matrix4f.m30 * matrix4f2.m03;
			float float2 = matrix4f.m00 * matrix4f2.m10 + matrix4f.m10 * matrix4f2.m11 + matrix4f.m20 * matrix4f2.m12 + matrix4f.m30 * matrix4f2.m13;
			float float3 = matrix4f.m00 * matrix4f2.m20 + matrix4f.m10 * matrix4f2.m21 + matrix4f.m20 * matrix4f2.m22 + matrix4f.m30 * matrix4f2.m23;
			float float4 = matrix4f.m00 * matrix4f2.m30 + matrix4f.m10 * matrix4f2.m31 + matrix4f.m20 * matrix4f2.m32 + matrix4f.m30 * matrix4f2.m33;
			float float5 = matrix4f.m01 * matrix4f2.m00 + matrix4f.m11 * matrix4f2.m01 + matrix4f.m21 * matrix4f2.m02 + matrix4f.m31 * matrix4f2.m03;
			float float6 = matrix4f.m01 * matrix4f2.m10 + matrix4f.m11 * matrix4f2.m11 + matrix4f.m21 * matrix4f2.m12 + matrix4f.m31 * matrix4f2.m13;
			float float7 = matrix4f.m01 * matrix4f2.m20 + matrix4f.m11 * matrix4f2.m21 + matrix4f.m21 * matrix4f2.m22 + matrix4f.m31 * matrix4f2.m23;
			float float8 = matrix4f.m01 * matrix4f2.m30 + matrix4f.m11 * matrix4f2.m31 + matrix4f.m21 * matrix4f2.m32 + matrix4f.m31 * matrix4f2.m33;
			float float9 = matrix4f.m02 * matrix4f2.m00 + matrix4f.m12 * matrix4f2.m01 + matrix4f.m22 * matrix4f2.m02 + matrix4f.m32 * matrix4f2.m03;
			float float10 = matrix4f.m02 * matrix4f2.m10 + matrix4f.m12 * matrix4f2.m11 + matrix4f.m22 * matrix4f2.m12 + matrix4f.m32 * matrix4f2.m13;
			float float11 = matrix4f.m02 * matrix4f2.m20 + matrix4f.m12 * matrix4f2.m21 + matrix4f.m22 * matrix4f2.m22 + matrix4f.m32 * matrix4f2.m23;
			float float12 = matrix4f.m02 * matrix4f2.m30 + matrix4f.m12 * matrix4f2.m31 + matrix4f.m22 * matrix4f2.m32 + matrix4f.m32 * matrix4f2.m33;
			float float13 = matrix4f.m03 * matrix4f2.m00 + matrix4f.m13 * matrix4f2.m01 + matrix4f.m23 * matrix4f2.m02 + matrix4f.m33 * matrix4f2.m03;
			float float14 = matrix4f.m03 * matrix4f2.m10 + matrix4f.m13 * matrix4f2.m11 + matrix4f.m23 * matrix4f2.m12 + matrix4f.m33 * matrix4f2.m13;
			float float15 = matrix4f.m03 * matrix4f2.m20 + matrix4f.m13 * matrix4f2.m21 + matrix4f.m23 * matrix4f2.m22 + matrix4f.m33 * matrix4f2.m23;
			float float16 = matrix4f.m03 * matrix4f2.m30 + matrix4f.m13 * matrix4f2.m31 + matrix4f.m23 * matrix4f2.m32 + matrix4f.m33 * matrix4f2.m33;
			this.m00 = float1;
			this.m01 = float2;
			this.m02 = float3;
			this.m03 = float4;
			this.m10 = float5;
			this.m11 = float6;
			this.m12 = float7;
			this.m13 = float8;
			this.m20 = float9;
			this.m21 = float10;
			this.m22 = float11;
			this.m23 = float12;
			this.m30 = float13;
			this.m31 = float14;
			this.m32 = float15;
			this.m33 = float16;
		}
	}

	public final void mulTransposeRight(Matrix4f matrix4f, Matrix4f matrix4f2) {
		if (this != matrix4f && this != matrix4f2) {
			this.m00 = matrix4f.m00 * matrix4f2.m00 + matrix4f.m01 * matrix4f2.m01 + matrix4f.m02 * matrix4f2.m02 + matrix4f.m03 * matrix4f2.m03;
			this.m01 = matrix4f.m00 * matrix4f2.m10 + matrix4f.m01 * matrix4f2.m11 + matrix4f.m02 * matrix4f2.m12 + matrix4f.m03 * matrix4f2.m13;
			this.m02 = matrix4f.m00 * matrix4f2.m20 + matrix4f.m01 * matrix4f2.m21 + matrix4f.m02 * matrix4f2.m22 + matrix4f.m03 * matrix4f2.m23;
			this.m03 = matrix4f.m00 * matrix4f2.m30 + matrix4f.m01 * matrix4f2.m31 + matrix4f.m02 * matrix4f2.m32 + matrix4f.m03 * matrix4f2.m33;
			this.m10 = matrix4f.m10 * matrix4f2.m00 + matrix4f.m11 * matrix4f2.m01 + matrix4f.m12 * matrix4f2.m02 + matrix4f.m13 * matrix4f2.m03;
			this.m11 = matrix4f.m10 * matrix4f2.m10 + matrix4f.m11 * matrix4f2.m11 + matrix4f.m12 * matrix4f2.m12 + matrix4f.m13 * matrix4f2.m13;
			this.m12 = matrix4f.m10 * matrix4f2.m20 + matrix4f.m11 * matrix4f2.m21 + matrix4f.m12 * matrix4f2.m22 + matrix4f.m13 * matrix4f2.m23;
			this.m13 = matrix4f.m10 * matrix4f2.m30 + matrix4f.m11 * matrix4f2.m31 + matrix4f.m12 * matrix4f2.m32 + matrix4f.m13 * matrix4f2.m33;
			this.m20 = matrix4f.m20 * matrix4f2.m00 + matrix4f.m21 * matrix4f2.m01 + matrix4f.m22 * matrix4f2.m02 + matrix4f.m23 * matrix4f2.m03;
			this.m21 = matrix4f.m20 * matrix4f2.m10 + matrix4f.m21 * matrix4f2.m11 + matrix4f.m22 * matrix4f2.m12 + matrix4f.m23 * matrix4f2.m13;
			this.m22 = matrix4f.m20 * matrix4f2.m20 + matrix4f.m21 * matrix4f2.m21 + matrix4f.m22 * matrix4f2.m22 + matrix4f.m23 * matrix4f2.m23;
			this.m23 = matrix4f.m20 * matrix4f2.m30 + matrix4f.m21 * matrix4f2.m31 + matrix4f.m22 * matrix4f2.m32 + matrix4f.m23 * matrix4f2.m33;
			this.m30 = matrix4f.m30 * matrix4f2.m00 + matrix4f.m31 * matrix4f2.m01 + matrix4f.m32 * matrix4f2.m02 + matrix4f.m33 * matrix4f2.m03;
			this.m31 = matrix4f.m30 * matrix4f2.m10 + matrix4f.m31 * matrix4f2.m11 + matrix4f.m32 * matrix4f2.m12 + matrix4f.m33 * matrix4f2.m13;
			this.m32 = matrix4f.m30 * matrix4f2.m20 + matrix4f.m31 * matrix4f2.m21 + matrix4f.m32 * matrix4f2.m22 + matrix4f.m33 * matrix4f2.m23;
			this.m33 = matrix4f.m30 * matrix4f2.m30 + matrix4f.m31 * matrix4f2.m31 + matrix4f.m32 * matrix4f2.m32 + matrix4f.m33 * matrix4f2.m33;
		} else {
			float float1 = matrix4f.m00 * matrix4f2.m00 + matrix4f.m01 * matrix4f2.m01 + matrix4f.m02 * matrix4f2.m02 + matrix4f.m03 * matrix4f2.m03;
			float float2 = matrix4f.m00 * matrix4f2.m10 + matrix4f.m01 * matrix4f2.m11 + matrix4f.m02 * matrix4f2.m12 + matrix4f.m03 * matrix4f2.m13;
			float float3 = matrix4f.m00 * matrix4f2.m20 + matrix4f.m01 * matrix4f2.m21 + matrix4f.m02 * matrix4f2.m22 + matrix4f.m03 * matrix4f2.m23;
			float float4 = matrix4f.m00 * matrix4f2.m30 + matrix4f.m01 * matrix4f2.m31 + matrix4f.m02 * matrix4f2.m32 + matrix4f.m03 * matrix4f2.m33;
			float float5 = matrix4f.m10 * matrix4f2.m00 + matrix4f.m11 * matrix4f2.m01 + matrix4f.m12 * matrix4f2.m02 + matrix4f.m13 * matrix4f2.m03;
			float float6 = matrix4f.m10 * matrix4f2.m10 + matrix4f.m11 * matrix4f2.m11 + matrix4f.m12 * matrix4f2.m12 + matrix4f.m13 * matrix4f2.m13;
			float float7 = matrix4f.m10 * matrix4f2.m20 + matrix4f.m11 * matrix4f2.m21 + matrix4f.m12 * matrix4f2.m22 + matrix4f.m13 * matrix4f2.m23;
			float float8 = matrix4f.m10 * matrix4f2.m30 + matrix4f.m11 * matrix4f2.m31 + matrix4f.m12 * matrix4f2.m32 + matrix4f.m13 * matrix4f2.m33;
			float float9 = matrix4f.m20 * matrix4f2.m00 + matrix4f.m21 * matrix4f2.m01 + matrix4f.m22 * matrix4f2.m02 + matrix4f.m23 * matrix4f2.m03;
			float float10 = matrix4f.m20 * matrix4f2.m10 + matrix4f.m21 * matrix4f2.m11 + matrix4f.m22 * matrix4f2.m12 + matrix4f.m23 * matrix4f2.m13;
			float float11 = matrix4f.m20 * matrix4f2.m20 + matrix4f.m21 * matrix4f2.m21 + matrix4f.m22 * matrix4f2.m22 + matrix4f.m23 * matrix4f2.m23;
			float float12 = matrix4f.m20 * matrix4f2.m30 + matrix4f.m21 * matrix4f2.m31 + matrix4f.m22 * matrix4f2.m32 + matrix4f.m23 * matrix4f2.m33;
			float float13 = matrix4f.m30 * matrix4f2.m00 + matrix4f.m31 * matrix4f2.m01 + matrix4f.m32 * matrix4f2.m02 + matrix4f.m33 * matrix4f2.m03;
			float float14 = matrix4f.m30 * matrix4f2.m10 + matrix4f.m31 * matrix4f2.m11 + matrix4f.m32 * matrix4f2.m12 + matrix4f.m33 * matrix4f2.m13;
			float float15 = matrix4f.m30 * matrix4f2.m20 + matrix4f.m31 * matrix4f2.m21 + matrix4f.m32 * matrix4f2.m22 + matrix4f.m33 * matrix4f2.m23;
			float float16 = matrix4f.m30 * matrix4f2.m30 + matrix4f.m31 * matrix4f2.m31 + matrix4f.m32 * matrix4f2.m32 + matrix4f.m33 * matrix4f2.m33;
			this.m00 = float1;
			this.m01 = float2;
			this.m02 = float3;
			this.m03 = float4;
			this.m10 = float5;
			this.m11 = float6;
			this.m12 = float7;
			this.m13 = float8;
			this.m20 = float9;
			this.m21 = float10;
			this.m22 = float11;
			this.m23 = float12;
			this.m30 = float13;
			this.m31 = float14;
			this.m32 = float15;
			this.m33 = float16;
		}
	}

	public final void mulTransposeLeft(Matrix4f matrix4f, Matrix4f matrix4f2) {
		if (this != matrix4f && this != matrix4f2) {
			this.m00 = matrix4f.m00 * matrix4f2.m00 + matrix4f.m10 * matrix4f2.m10 + matrix4f.m20 * matrix4f2.m20 + matrix4f.m30 * matrix4f2.m30;
			this.m01 = matrix4f.m00 * matrix4f2.m01 + matrix4f.m10 * matrix4f2.m11 + matrix4f.m20 * matrix4f2.m21 + matrix4f.m30 * matrix4f2.m31;
			this.m02 = matrix4f.m00 * matrix4f2.m02 + matrix4f.m10 * matrix4f2.m12 + matrix4f.m20 * matrix4f2.m22 + matrix4f.m30 * matrix4f2.m32;
			this.m03 = matrix4f.m00 * matrix4f2.m03 + matrix4f.m10 * matrix4f2.m13 + matrix4f.m20 * matrix4f2.m23 + matrix4f.m30 * matrix4f2.m33;
			this.m10 = matrix4f.m01 * matrix4f2.m00 + matrix4f.m11 * matrix4f2.m10 + matrix4f.m21 * matrix4f2.m20 + matrix4f.m31 * matrix4f2.m30;
			this.m11 = matrix4f.m01 * matrix4f2.m01 + matrix4f.m11 * matrix4f2.m11 + matrix4f.m21 * matrix4f2.m21 + matrix4f.m31 * matrix4f2.m31;
			this.m12 = matrix4f.m01 * matrix4f2.m02 + matrix4f.m11 * matrix4f2.m12 + matrix4f.m21 * matrix4f2.m22 + matrix4f.m31 * matrix4f2.m32;
			this.m13 = matrix4f.m01 * matrix4f2.m03 + matrix4f.m11 * matrix4f2.m13 + matrix4f.m21 * matrix4f2.m23 + matrix4f.m31 * matrix4f2.m33;
			this.m20 = matrix4f.m02 * matrix4f2.m00 + matrix4f.m12 * matrix4f2.m10 + matrix4f.m22 * matrix4f2.m20 + matrix4f.m32 * matrix4f2.m30;
			this.m21 = matrix4f.m02 * matrix4f2.m01 + matrix4f.m12 * matrix4f2.m11 + matrix4f.m22 * matrix4f2.m21 + matrix4f.m32 * matrix4f2.m31;
			this.m22 = matrix4f.m02 * matrix4f2.m02 + matrix4f.m12 * matrix4f2.m12 + matrix4f.m22 * matrix4f2.m22 + matrix4f.m32 * matrix4f2.m32;
			this.m23 = matrix4f.m02 * matrix4f2.m03 + matrix4f.m12 * matrix4f2.m13 + matrix4f.m22 * matrix4f2.m23 + matrix4f.m32 * matrix4f2.m33;
			this.m30 = matrix4f.m03 * matrix4f2.m00 + matrix4f.m13 * matrix4f2.m10 + matrix4f.m23 * matrix4f2.m20 + matrix4f.m33 * matrix4f2.m30;
			this.m31 = matrix4f.m03 * matrix4f2.m01 + matrix4f.m13 * matrix4f2.m11 + matrix4f.m23 * matrix4f2.m21 + matrix4f.m33 * matrix4f2.m31;
			this.m32 = matrix4f.m03 * matrix4f2.m02 + matrix4f.m13 * matrix4f2.m12 + matrix4f.m23 * matrix4f2.m22 + matrix4f.m33 * matrix4f2.m32;
			this.m33 = matrix4f.m03 * matrix4f2.m03 + matrix4f.m13 * matrix4f2.m13 + matrix4f.m23 * matrix4f2.m23 + matrix4f.m33 * matrix4f2.m33;
		} else {
			float float1 = matrix4f.m00 * matrix4f2.m00 + matrix4f.m10 * matrix4f2.m10 + matrix4f.m20 * matrix4f2.m20 + matrix4f.m30 * matrix4f2.m30;
			float float2 = matrix4f.m00 * matrix4f2.m01 + matrix4f.m10 * matrix4f2.m11 + matrix4f.m20 * matrix4f2.m21 + matrix4f.m30 * matrix4f2.m31;
			float float3 = matrix4f.m00 * matrix4f2.m02 + matrix4f.m10 * matrix4f2.m12 + matrix4f.m20 * matrix4f2.m22 + matrix4f.m30 * matrix4f2.m32;
			float float4 = matrix4f.m00 * matrix4f2.m03 + matrix4f.m10 * matrix4f2.m13 + matrix4f.m20 * matrix4f2.m23 + matrix4f.m30 * matrix4f2.m33;
			float float5 = matrix4f.m01 * matrix4f2.m00 + matrix4f.m11 * matrix4f2.m10 + matrix4f.m21 * matrix4f2.m20 + matrix4f.m31 * matrix4f2.m30;
			float float6 = matrix4f.m01 * matrix4f2.m01 + matrix4f.m11 * matrix4f2.m11 + matrix4f.m21 * matrix4f2.m21 + matrix4f.m31 * matrix4f2.m31;
			float float7 = matrix4f.m01 * matrix4f2.m02 + matrix4f.m11 * matrix4f2.m12 + matrix4f.m21 * matrix4f2.m22 + matrix4f.m31 * matrix4f2.m32;
			float float8 = matrix4f.m01 * matrix4f2.m03 + matrix4f.m11 * matrix4f2.m13 + matrix4f.m21 * matrix4f2.m23 + matrix4f.m31 * matrix4f2.m33;
			float float9 = matrix4f.m02 * matrix4f2.m00 + matrix4f.m12 * matrix4f2.m10 + matrix4f.m22 * matrix4f2.m20 + matrix4f.m32 * matrix4f2.m30;
			float float10 = matrix4f.m02 * matrix4f2.m01 + matrix4f.m12 * matrix4f2.m11 + matrix4f.m22 * matrix4f2.m21 + matrix4f.m32 * matrix4f2.m31;
			float float11 = matrix4f.m02 * matrix4f2.m02 + matrix4f.m12 * matrix4f2.m12 + matrix4f.m22 * matrix4f2.m22 + matrix4f.m32 * matrix4f2.m32;
			float float12 = matrix4f.m02 * matrix4f2.m03 + matrix4f.m12 * matrix4f2.m13 + matrix4f.m22 * matrix4f2.m23 + matrix4f.m32 * matrix4f2.m33;
			float float13 = matrix4f.m03 * matrix4f2.m00 + matrix4f.m13 * matrix4f2.m10 + matrix4f.m23 * matrix4f2.m20 + matrix4f.m33 * matrix4f2.m30;
			float float14 = matrix4f.m03 * matrix4f2.m01 + matrix4f.m13 * matrix4f2.m11 + matrix4f.m23 * matrix4f2.m21 + matrix4f.m33 * matrix4f2.m31;
			float float15 = matrix4f.m03 * matrix4f2.m02 + matrix4f.m13 * matrix4f2.m12 + matrix4f.m23 * matrix4f2.m22 + matrix4f.m33 * matrix4f2.m32;
			float float16 = matrix4f.m03 * matrix4f2.m03 + matrix4f.m13 * matrix4f2.m13 + matrix4f.m23 * matrix4f2.m23 + matrix4f.m33 * matrix4f2.m33;
			this.m00 = float1;
			this.m01 = float2;
			this.m02 = float3;
			this.m03 = float4;
			this.m10 = float5;
			this.m11 = float6;
			this.m12 = float7;
			this.m13 = float8;
			this.m20 = float9;
			this.m21 = float10;
			this.m22 = float11;
			this.m23 = float12;
			this.m30 = float13;
			this.m31 = float14;
			this.m32 = float15;
			this.m33 = float16;
		}
	}

	public boolean equals(Matrix4f matrix4f) {
		try {
			return this.m00 == matrix4f.m00 && this.m01 == matrix4f.m01 && this.m02 == matrix4f.m02 && this.m03 == matrix4f.m03 && this.m10 == matrix4f.m10 && this.m11 == matrix4f.m11 && this.m12 == matrix4f.m12 && this.m13 == matrix4f.m13 && this.m20 == matrix4f.m20 && this.m21 == matrix4f.m21 && this.m22 == matrix4f.m22 && this.m23 == matrix4f.m23 && this.m30 == matrix4f.m30 && this.m31 == matrix4f.m31 && this.m32 == matrix4f.m32 && this.m33 == matrix4f.m33;
		} catch (NullPointerException nullPointerException) {
			return false;
		}
	}

	public boolean equals(Object object) {
		try {
			Matrix4f matrix4f = (Matrix4f)object;
			return this.m00 == matrix4f.m00 && this.m01 == matrix4f.m01 && this.m02 == matrix4f.m02 && this.m03 == matrix4f.m03 && this.m10 == matrix4f.m10 && this.m11 == matrix4f.m11 && this.m12 == matrix4f.m12 && this.m13 == matrix4f.m13 && this.m20 == matrix4f.m20 && this.m21 == matrix4f.m21 && this.m22 == matrix4f.m22 && this.m23 == matrix4f.m23 && this.m30 == matrix4f.m30 && this.m31 == matrix4f.m31 && this.m32 == matrix4f.m32 && this.m33 == matrix4f.m33;
		} catch (ClassCastException classCastException) {
			return false;
		} catch (NullPointerException nullPointerException) {
			return false;
		}
	}

	public boolean epsilonEquals(Matrix4f matrix4f, float float1) {
		boolean boolean1 = true;
		if (Math.abs(this.m00 - matrix4f.m00) > float1) {
			boolean1 = false;
		}

		if (Math.abs(this.m01 - matrix4f.m01) > float1) {
			boolean1 = false;
		}

		if (Math.abs(this.m02 - matrix4f.m02) > float1) {
			boolean1 = false;
		}

		if (Math.abs(this.m03 - matrix4f.m03) > float1) {
			boolean1 = false;
		}

		if (Math.abs(this.m10 - matrix4f.m10) > float1) {
			boolean1 = false;
		}

		if (Math.abs(this.m11 - matrix4f.m11) > float1) {
			boolean1 = false;
		}

		if (Math.abs(this.m12 - matrix4f.m12) > float1) {
			boolean1 = false;
		}

		if (Math.abs(this.m13 - matrix4f.m13) > float1) {
			boolean1 = false;
		}

		if (Math.abs(this.m20 - matrix4f.m20) > float1) {
			boolean1 = false;
		}

		if (Math.abs(this.m21 - matrix4f.m21) > float1) {
			boolean1 = false;
		}

		if (Math.abs(this.m22 - matrix4f.m22) > float1) {
			boolean1 = false;
		}

		if (Math.abs(this.m23 - matrix4f.m23) > float1) {
			boolean1 = false;
		}

		if (Math.abs(this.m30 - matrix4f.m30) > float1) {
			boolean1 = false;
		}

		if (Math.abs(this.m31 - matrix4f.m31) > float1) {
			boolean1 = false;
		}

		if (Math.abs(this.m32 - matrix4f.m32) > float1) {
			boolean1 = false;
		}

		if (Math.abs(this.m33 - matrix4f.m33) > float1) {
			boolean1 = false;
		}

		return boolean1;
	}

	public int hashCode() {
		long long1 = 1L;
		long1 = 31L * long1 + (long)VecMathUtil.floatToIntBits(this.m00);
		long1 = 31L * long1 + (long)VecMathUtil.floatToIntBits(this.m01);
		long1 = 31L * long1 + (long)VecMathUtil.floatToIntBits(this.m02);
		long1 = 31L * long1 + (long)VecMathUtil.floatToIntBits(this.m03);
		long1 = 31L * long1 + (long)VecMathUtil.floatToIntBits(this.m10);
		long1 = 31L * long1 + (long)VecMathUtil.floatToIntBits(this.m11);
		long1 = 31L * long1 + (long)VecMathUtil.floatToIntBits(this.m12);
		long1 = 31L * long1 + (long)VecMathUtil.floatToIntBits(this.m13);
		long1 = 31L * long1 + (long)VecMathUtil.floatToIntBits(this.m20);
		long1 = 31L * long1 + (long)VecMathUtil.floatToIntBits(this.m21);
		long1 = 31L * long1 + (long)VecMathUtil.floatToIntBits(this.m22);
		long1 = 31L * long1 + (long)VecMathUtil.floatToIntBits(this.m23);
		long1 = 31L * long1 + (long)VecMathUtil.floatToIntBits(this.m30);
		long1 = 31L * long1 + (long)VecMathUtil.floatToIntBits(this.m31);
		long1 = 31L * long1 + (long)VecMathUtil.floatToIntBits(this.m32);
		long1 = 31L * long1 + (long)VecMathUtil.floatToIntBits(this.m33);
		return (int)(long1 ^ long1 >> 32);
	}

	public final void transform(Tuple4f tuple4f, Tuple4f tuple4f2) {
		float float1 = this.m00 * tuple4f.x + this.m01 * tuple4f.y + this.m02 * tuple4f.z + this.m03 * tuple4f.w;
		float float2 = this.m10 * tuple4f.x + this.m11 * tuple4f.y + this.m12 * tuple4f.z + this.m13 * tuple4f.w;
		float float3 = this.m20 * tuple4f.x + this.m21 * tuple4f.y + this.m22 * tuple4f.z + this.m23 * tuple4f.w;
		tuple4f2.w = this.m30 * tuple4f.x + this.m31 * tuple4f.y + this.m32 * tuple4f.z + this.m33 * tuple4f.w;
		tuple4f2.x = float1;
		tuple4f2.y = float2;
		tuple4f2.z = float3;
	}

	public final void transform(Tuple4f tuple4f) {
		float float1 = this.m00 * tuple4f.x + this.m01 * tuple4f.y + this.m02 * tuple4f.z + this.m03 * tuple4f.w;
		float float2 = this.m10 * tuple4f.x + this.m11 * tuple4f.y + this.m12 * tuple4f.z + this.m13 * tuple4f.w;
		float float3 = this.m20 * tuple4f.x + this.m21 * tuple4f.y + this.m22 * tuple4f.z + this.m23 * tuple4f.w;
		tuple4f.w = this.m30 * tuple4f.x + this.m31 * tuple4f.y + this.m32 * tuple4f.z + this.m33 * tuple4f.w;
		tuple4f.x = float1;
		tuple4f.y = float2;
		tuple4f.z = float3;
	}

	public final void transform(Point3f point3f, Point3f point3f2) {
		float float1 = this.m00 * point3f.x + this.m01 * point3f.y + this.m02 * point3f.z + this.m03;
		float float2 = this.m10 * point3f.x + this.m11 * point3f.y + this.m12 * point3f.z + this.m13;
		point3f2.z = this.m20 * point3f.x + this.m21 * point3f.y + this.m22 * point3f.z + this.m23;
		point3f2.x = float1;
		point3f2.y = float2;
	}

	public final void transform(Point3f point3f) {
		float float1 = this.m00 * point3f.x + this.m01 * point3f.y + this.m02 * point3f.z + this.m03;
		float float2 = this.m10 * point3f.x + this.m11 * point3f.y + this.m12 * point3f.z + this.m13;
		point3f.z = this.m20 * point3f.x + this.m21 * point3f.y + this.m22 * point3f.z + this.m23;
		point3f.x = float1;
		point3f.y = float2;
	}

	public final void transform(Vector3f vector3f, Vector3f vector3f2) {
		float float1 = this.m00 * vector3f.x + this.m01 * vector3f.y + this.m02 * vector3f.z;
		float float2 = this.m10 * vector3f.x + this.m11 * vector3f.y + this.m12 * vector3f.z;
		vector3f2.z = this.m20 * vector3f.x + this.m21 * vector3f.y + this.m22 * vector3f.z;
		vector3f2.x = float1;
		vector3f2.y = float2;
	}

	public final void transform(Vector3f vector3f) {
		float float1 = this.m00 * vector3f.x + this.m01 * vector3f.y + this.m02 * vector3f.z;
		float float2 = this.m10 * vector3f.x + this.m11 * vector3f.y + this.m12 * vector3f.z;
		vector3f.z = this.m20 * vector3f.x + this.m21 * vector3f.y + this.m22 * vector3f.z;
		vector3f.x = float1;
		vector3f.y = float2;
	}

	public final void setRotation(Matrix3d matrix3d) {
		double[] doubleArray = new double[9];
		double[] doubleArray2 = new double[3];
		this.getScaleRotate(doubleArray2, doubleArray);
		this.m00 = (float)(matrix3d.m00 * doubleArray2[0]);
		this.m01 = (float)(matrix3d.m01 * doubleArray2[1]);
		this.m02 = (float)(matrix3d.m02 * doubleArray2[2]);
		this.m10 = (float)(matrix3d.m10 * doubleArray2[0]);
		this.m11 = (float)(matrix3d.m11 * doubleArray2[1]);
		this.m12 = (float)(matrix3d.m12 * doubleArray2[2]);
		this.m20 = (float)(matrix3d.m20 * doubleArray2[0]);
		this.m21 = (float)(matrix3d.m21 * doubleArray2[1]);
		this.m22 = (float)(matrix3d.m22 * doubleArray2[2]);
	}

	public final void setRotation(Matrix3f matrix3f) {
		double[] doubleArray = new double[9];
		double[] doubleArray2 = new double[3];
		this.getScaleRotate(doubleArray2, doubleArray);
		this.m00 = (float)((double)matrix3f.m00 * doubleArray2[0]);
		this.m01 = (float)((double)matrix3f.m01 * doubleArray2[1]);
		this.m02 = (float)((double)matrix3f.m02 * doubleArray2[2]);
		this.m10 = (float)((double)matrix3f.m10 * doubleArray2[0]);
		this.m11 = (float)((double)matrix3f.m11 * doubleArray2[1]);
		this.m12 = (float)((double)matrix3f.m12 * doubleArray2[2]);
		this.m20 = (float)((double)matrix3f.m20 * doubleArray2[0]);
		this.m21 = (float)((double)matrix3f.m21 * doubleArray2[1]);
		this.m22 = (float)((double)matrix3f.m22 * doubleArray2[2]);
	}

	public final void setRotation(Quat4f quat4f) {
		double[] doubleArray = new double[9];
		double[] doubleArray2 = new double[3];
		this.getScaleRotate(doubleArray2, doubleArray);
		this.m00 = (float)((double)(1.0F - 2.0F * quat4f.y * quat4f.y - 2.0F * quat4f.z * quat4f.z) * doubleArray2[0]);
		this.m10 = (float)((double)(2.0F * (quat4f.x * quat4f.y + quat4f.w * quat4f.z)) * doubleArray2[0]);
		this.m20 = (float)((double)(2.0F * (quat4f.x * quat4f.z - quat4f.w * quat4f.y)) * doubleArray2[0]);
		this.m01 = (float)((double)(2.0F * (quat4f.x * quat4f.y - quat4f.w * quat4f.z)) * doubleArray2[1]);
		this.m11 = (float)((double)(1.0F - 2.0F * quat4f.x * quat4f.x - 2.0F * quat4f.z * quat4f.z) * doubleArray2[1]);
		this.m21 = (float)((double)(2.0F * (quat4f.y * quat4f.z + quat4f.w * quat4f.x)) * doubleArray2[1]);
		this.m02 = (float)((double)(2.0F * (quat4f.x * quat4f.z + quat4f.w * quat4f.y)) * doubleArray2[2]);
		this.m12 = (float)((double)(2.0F * (quat4f.y * quat4f.z - quat4f.w * quat4f.x)) * doubleArray2[2]);
		this.m22 = (float)((double)(1.0F - 2.0F * quat4f.x * quat4f.x - 2.0F * quat4f.y * quat4f.y) * doubleArray2[2]);
	}

	public final void setRotation(Quat4d quat4d) {
		double[] doubleArray = new double[9];
		double[] doubleArray2 = new double[3];
		this.getScaleRotate(doubleArray2, doubleArray);
		this.m00 = (float)((1.0 - 2.0 * quat4d.y * quat4d.y - 2.0 * quat4d.z * quat4d.z) * doubleArray2[0]);
		this.m10 = (float)(2.0 * (quat4d.x * quat4d.y + quat4d.w * quat4d.z) * doubleArray2[0]);
		this.m20 = (float)(2.0 * (quat4d.x * quat4d.z - quat4d.w * quat4d.y) * doubleArray2[0]);
		this.m01 = (float)(2.0 * (quat4d.x * quat4d.y - quat4d.w * quat4d.z) * doubleArray2[1]);
		this.m11 = (float)((1.0 - 2.0 * quat4d.x * quat4d.x - 2.0 * quat4d.z * quat4d.z) * doubleArray2[1]);
		this.m21 = (float)(2.0 * (quat4d.y * quat4d.z + quat4d.w * quat4d.x) * doubleArray2[1]);
		this.m02 = (float)(2.0 * (quat4d.x * quat4d.z + quat4d.w * quat4d.y) * doubleArray2[2]);
		this.m12 = (float)(2.0 * (quat4d.y * quat4d.z - quat4d.w * quat4d.x) * doubleArray2[2]);
		this.m22 = (float)((1.0 - 2.0 * quat4d.x * quat4d.x - 2.0 * quat4d.y * quat4d.y) * doubleArray2[2]);
	}

	public final void setRotation(AxisAngle4f axisAngle4f) {
		double[] doubleArray = new double[9];
		double[] doubleArray2 = new double[3];
		this.getScaleRotate(doubleArray2, doubleArray);
		double double1 = Math.sqrt((double)(axisAngle4f.x * axisAngle4f.x + axisAngle4f.y * axisAngle4f.y + axisAngle4f.z * axisAngle4f.z));
		if (double1 < 1.0E-8) {
			this.m00 = 1.0F;
			this.m01 = 0.0F;
			this.m02 = 0.0F;
			this.m10 = 0.0F;
			this.m11 = 1.0F;
			this.m12 = 0.0F;
			this.m20 = 0.0F;
			this.m21 = 0.0F;
			this.m22 = 1.0F;
		} else {
			double1 = 1.0 / double1;
			double double2 = (double)axisAngle4f.x * double1;
			double double3 = (double)axisAngle4f.y * double1;
			double double4 = (double)axisAngle4f.z * double1;
			double double5 = Math.sin((double)axisAngle4f.angle);
			double double6 = Math.cos((double)axisAngle4f.angle);
			double double7 = 1.0 - double6;
			double double8 = (double)(axisAngle4f.x * axisAngle4f.z);
			double double9 = (double)(axisAngle4f.x * axisAngle4f.y);
			double double10 = (double)(axisAngle4f.y * axisAngle4f.z);
			this.m00 = (float)((double7 * double2 * double2 + double6) * doubleArray2[0]);
			this.m01 = (float)((double7 * double9 - double5 * double4) * doubleArray2[1]);
			this.m02 = (float)((double7 * double8 + double5 * double3) * doubleArray2[2]);
			this.m10 = (float)((double7 * double9 + double5 * double4) * doubleArray2[0]);
			this.m11 = (float)((double7 * double3 * double3 + double6) * doubleArray2[1]);
			this.m12 = (float)((double7 * double10 - double5 * double2) * doubleArray2[2]);
			this.m20 = (float)((double7 * double8 - double5 * double3) * doubleArray2[0]);
			this.m21 = (float)((double7 * double10 + double5 * double2) * doubleArray2[1]);
			this.m22 = (float)((double7 * double4 * double4 + double6) * doubleArray2[2]);
		}
	}

	public final void setZero() {
		this.m00 = 0.0F;
		this.m01 = 0.0F;
		this.m02 = 0.0F;
		this.m03 = 0.0F;
		this.m10 = 0.0F;
		this.m11 = 0.0F;
		this.m12 = 0.0F;
		this.m13 = 0.0F;
		this.m20 = 0.0F;
		this.m21 = 0.0F;
		this.m22 = 0.0F;
		this.m23 = 0.0F;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.m33 = 0.0F;
	}

	public final void negate() {
		this.m00 = -this.m00;
		this.m01 = -this.m01;
		this.m02 = -this.m02;
		this.m03 = -this.m03;
		this.m10 = -this.m10;
		this.m11 = -this.m11;
		this.m12 = -this.m12;
		this.m13 = -this.m13;
		this.m20 = -this.m20;
		this.m21 = -this.m21;
		this.m22 = -this.m22;
		this.m23 = -this.m23;
		this.m30 = -this.m30;
		this.m31 = -this.m31;
		this.m32 = -this.m32;
		this.m33 = -this.m33;
	}

	public final void negate(Matrix4f matrix4f) {
		this.m00 = -matrix4f.m00;
		this.m01 = -matrix4f.m01;
		this.m02 = -matrix4f.m02;
		this.m03 = -matrix4f.m03;
		this.m10 = -matrix4f.m10;
		this.m11 = -matrix4f.m11;
		this.m12 = -matrix4f.m12;
		this.m13 = -matrix4f.m13;
		this.m20 = -matrix4f.m20;
		this.m21 = -matrix4f.m21;
		this.m22 = -matrix4f.m22;
		this.m23 = -matrix4f.m23;
		this.m30 = -matrix4f.m30;
		this.m31 = -matrix4f.m31;
		this.m32 = -matrix4f.m32;
		this.m33 = -matrix4f.m33;
	}

	private final void getScaleRotate(double[] doubleArray, double[] doubleArray2) {
		double[] doubleArray3 = new double[]{(double)this.m00, (double)this.m01, (double)this.m02, (double)this.m10, (double)this.m11, (double)this.m12, (double)this.m20, (double)this.m21, (double)this.m22};
		Matrix3d.compute_svd(doubleArray3, doubleArray, doubleArray2);
	}

	public Object clone() {
		Matrix4f matrix4f = null;
		try {
			matrix4f = (Matrix4f)super.clone();
			return matrix4f;
		} catch (CloneNotSupportedException cloneNotSupportedException) {
			throw new InternalError();
		}
	}

	public final float getM00() {
		return this.m00;
	}

	public final void setM00(float float1) {
		this.m00 = float1;
	}

	public final float getM01() {
		return this.m01;
	}

	public final void setM01(float float1) {
		this.m01 = float1;
	}

	public final float getM02() {
		return this.m02;
	}

	public final void setM02(float float1) {
		this.m02 = float1;
	}

	public final float getM10() {
		return this.m10;
	}

	public final void setM10(float float1) {
		this.m10 = float1;
	}

	public final float getM11() {
		return this.m11;
	}

	public final void setM11(float float1) {
		this.m11 = float1;
	}

	public final float getM12() {
		return this.m12;
	}

	public final void setM12(float float1) {
		this.m12 = float1;
	}

	public final float getM20() {
		return this.m20;
	}

	public final void setM20(float float1) {
		this.m20 = float1;
	}

	public final float getM21() {
		return this.m21;
	}

	public final void setM21(float float1) {
		this.m21 = float1;
	}

	public final float getM22() {
		return this.m22;
	}

	public final void setM22(float float1) {
		this.m22 = float1;
	}

	public final float getM03() {
		return this.m03;
	}

	public final void setM03(float float1) {
		this.m03 = float1;
	}

	public final float getM13() {
		return this.m13;
	}

	public final void setM13(float float1) {
		this.m13 = float1;
	}

	public final float getM23() {
		return this.m23;
	}

	public final void setM23(float float1) {
		this.m23 = float1;
	}

	public final float getM30() {
		return this.m30;
	}

	public final void setM30(float float1) {
		this.m30 = float1;
	}

	public final float getM31() {
		return this.m31;
	}

	public final void setM31(float float1) {
		this.m31 = float1;
	}

	public final float getM32() {
		return this.m32;
	}

	public final void setM32(float float1) {
		this.m32 = float1;
	}

	public final float getM33() {
		return this.m33;
	}

	public final void setM33(float float1) {
		this.m33 = float1;
	}
}
