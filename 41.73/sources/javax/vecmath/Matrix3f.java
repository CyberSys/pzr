package javax.vecmath;

import java.io.Serializable;


public class Matrix3f implements Serializable,Cloneable {
	static final long serialVersionUID = 329697160112089834L;
	public float m00;
	public float m01;
	public float m02;
	public float m10;
	public float m11;
	public float m12;
	public float m20;
	public float m21;
	public float m22;
	private static final double EPS = 1.0E-8;

	public Matrix3f(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9) {
		this.m00 = float1;
		this.m01 = float2;
		this.m02 = float3;
		this.m10 = float4;
		this.m11 = float5;
		this.m12 = float6;
		this.m20 = float7;
		this.m21 = float8;
		this.m22 = float9;
	}

	public Matrix3f(float[] floatArray) {
		this.m00 = floatArray[0];
		this.m01 = floatArray[1];
		this.m02 = floatArray[2];
		this.m10 = floatArray[3];
		this.m11 = floatArray[4];
		this.m12 = floatArray[5];
		this.m20 = floatArray[6];
		this.m21 = floatArray[7];
		this.m22 = floatArray[8];
	}

	public Matrix3f(Matrix3d matrix3d) {
		this.m00 = (float)matrix3d.m00;
		this.m01 = (float)matrix3d.m01;
		this.m02 = (float)matrix3d.m02;
		this.m10 = (float)matrix3d.m10;
		this.m11 = (float)matrix3d.m11;
		this.m12 = (float)matrix3d.m12;
		this.m20 = (float)matrix3d.m20;
		this.m21 = (float)matrix3d.m21;
		this.m22 = (float)matrix3d.m22;
	}

	public Matrix3f(Matrix3f matrix3f) {
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

	public Matrix3f() {
		this.m00 = 0.0F;
		this.m01 = 0.0F;
		this.m02 = 0.0F;
		this.m10 = 0.0F;
		this.m11 = 0.0F;
		this.m12 = 0.0F;
		this.m20 = 0.0F;
		this.m21 = 0.0F;
		this.m22 = 0.0F;
	}

	public String toString() {
		return this.m00 + ", " + this.m01 + ", " + this.m02 + "\n" + this.m10 + ", " + this.m11 + ", " + this.m12 + "\n" + this.m20 + ", " + this.m21 + ", " + this.m22 + "\n";
	}

	public final void setIdentity() {
		this.m00 = 1.0F;
		this.m01 = 0.0F;
		this.m02 = 0.0F;
		this.m10 = 0.0F;
		this.m11 = 1.0F;
		this.m12 = 0.0F;
		this.m20 = 0.0F;
		this.m21 = 0.0F;
		this.m22 = 1.0F;
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
			
			default: 
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f0"));
			
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
			
			default: 
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f0"));
			
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
			
			default: 
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f0"));
			
			}

		
		default: 
			throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f0"));
		
		}
	}

	public final void getRow(int int1, Vector3f vector3f) {
		if (int1 == 0) {
			vector3f.x = this.m00;
			vector3f.y = this.m01;
			vector3f.z = this.m02;
		} else if (int1 == 1) {
			vector3f.x = this.m10;
			vector3f.y = this.m11;
			vector3f.z = this.m12;
		} else {
			if (int1 != 2) {
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f1"));
			}

			vector3f.x = this.m20;
			vector3f.y = this.m21;
			vector3f.z = this.m22;
		}
	}

	public final void getRow(int int1, float[] floatArray) {
		if (int1 == 0) {
			floatArray[0] = this.m00;
			floatArray[1] = this.m01;
			floatArray[2] = this.m02;
		} else if (int1 == 1) {
			floatArray[0] = this.m10;
			floatArray[1] = this.m11;
			floatArray[2] = this.m12;
		} else {
			if (int1 != 2) {
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f1"));
			}

			floatArray[0] = this.m20;
			floatArray[1] = this.m21;
			floatArray[2] = this.m22;
		}
	}

	public final void getColumn(int int1, Vector3f vector3f) {
		if (int1 == 0) {
			vector3f.x = this.m00;
			vector3f.y = this.m10;
			vector3f.z = this.m20;
		} else if (int1 == 1) {
			vector3f.x = this.m01;
			vector3f.y = this.m11;
			vector3f.z = this.m21;
		} else {
			if (int1 != 2) {
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f3"));
			}

			vector3f.x = this.m02;
			vector3f.y = this.m12;
			vector3f.z = this.m22;
		}
	}

	public final void getColumn(int int1, float[] floatArray) {
		if (int1 == 0) {
			floatArray[0] = this.m00;
			floatArray[1] = this.m10;
			floatArray[2] = this.m20;
		} else if (int1 == 1) {
			floatArray[0] = this.m01;
			floatArray[1] = this.m11;
			floatArray[2] = this.m21;
		} else {
			if (int1 != 2) {
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f3"));
			}

			floatArray[0] = this.m02;
			floatArray[1] = this.m12;
			floatArray[2] = this.m22;
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
			
			default: 
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f5"));
			
			}

		
		case 1: 
			switch (int2) {
			case 0: 
				return this.m10;
			
			case 1: 
				return this.m11;
			
			case 2: 
				return this.m12;
			
			default: 
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f5"));
			
			}

		
		case 2: 
			switch (int2) {
			case 0: 
				return this.m20;
			
			case 1: 
				return this.m21;
			
			case 2: 
				return this.m22;
			
			}

		
		}
		throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f5"));
	}

	public final void setRow(int int1, float float1, float float2, float float3) {
		switch (int1) {
		case 0: 
			this.m00 = float1;
			this.m01 = float2;
			this.m02 = float3;
			break;
		
		case 1: 
			this.m10 = float1;
			this.m11 = float2;
			this.m12 = float3;
			break;
		
		case 2: 
			this.m20 = float1;
			this.m21 = float2;
			this.m22 = float3;
			break;
		
		default: 
			throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f6"));
		
		}
	}

	public final void setRow(int int1, Vector3f vector3f) {
		switch (int1) {
		case 0: 
			this.m00 = vector3f.x;
			this.m01 = vector3f.y;
			this.m02 = vector3f.z;
			break;
		
		case 1: 
			this.m10 = vector3f.x;
			this.m11 = vector3f.y;
			this.m12 = vector3f.z;
			break;
		
		case 2: 
			this.m20 = vector3f.x;
			this.m21 = vector3f.y;
			this.m22 = vector3f.z;
			break;
		
		default: 
			throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f6"));
		
		}
	}

	public final void setRow(int int1, float[] floatArray) {
		switch (int1) {
		case 0: 
			this.m00 = floatArray[0];
			this.m01 = floatArray[1];
			this.m02 = floatArray[2];
			break;
		
		case 1: 
			this.m10 = floatArray[0];
			this.m11 = floatArray[1];
			this.m12 = floatArray[2];
			break;
		
		case 2: 
			this.m20 = floatArray[0];
			this.m21 = floatArray[1];
			this.m22 = floatArray[2];
			break;
		
		default: 
			throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f6"));
		
		}
	}

	public final void setColumn(int int1, float float1, float float2, float float3) {
		switch (int1) {
		case 0: 
			this.m00 = float1;
			this.m10 = float2;
			this.m20 = float3;
			break;
		
		case 1: 
			this.m01 = float1;
			this.m11 = float2;
			this.m21 = float3;
			break;
		
		case 2: 
			this.m02 = float1;
			this.m12 = float2;
			this.m22 = float3;
			break;
		
		default: 
			throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f9"));
		
		}
	}

	public final void setColumn(int int1, Vector3f vector3f) {
		switch (int1) {
		case 0: 
			this.m00 = vector3f.x;
			this.m10 = vector3f.y;
			this.m20 = vector3f.z;
			break;
		
		case 1: 
			this.m01 = vector3f.x;
			this.m11 = vector3f.y;
			this.m21 = vector3f.z;
			break;
		
		case 2: 
			this.m02 = vector3f.x;
			this.m12 = vector3f.y;
			this.m22 = vector3f.z;
			break;
		
		default: 
			throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f9"));
		
		}
	}

	public final void setColumn(int int1, float[] floatArray) {
		switch (int1) {
		case 0: 
			this.m00 = floatArray[0];
			this.m10 = floatArray[1];
			this.m20 = floatArray[2];
			break;
		
		case 1: 
			this.m01 = floatArray[0];
			this.m11 = floatArray[1];
			this.m21 = floatArray[2];
			break;
		
		case 2: 
			this.m02 = floatArray[0];
			this.m12 = floatArray[1];
			this.m22 = floatArray[2];
			break;
		
		default: 
			throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f9"));
		
		}
	}

	public final float getScale() {
		double[] doubleArray = new double[9];
		double[] doubleArray2 = new double[3];
		this.getScaleRotate(doubleArray2, doubleArray);
		return (float)Matrix3d.max3(doubleArray2);
	}

	public final void add(float float1) {
		this.m00 += float1;
		this.m01 += float1;
		this.m02 += float1;
		this.m10 += float1;
		this.m11 += float1;
		this.m12 += float1;
		this.m20 += float1;
		this.m21 += float1;
		this.m22 += float1;
	}

	public final void add(float float1, Matrix3f matrix3f) {
		this.m00 = matrix3f.m00 + float1;
		this.m01 = matrix3f.m01 + float1;
		this.m02 = matrix3f.m02 + float1;
		this.m10 = matrix3f.m10 + float1;
		this.m11 = matrix3f.m11 + float1;
		this.m12 = matrix3f.m12 + float1;
		this.m20 = matrix3f.m20 + float1;
		this.m21 = matrix3f.m21 + float1;
		this.m22 = matrix3f.m22 + float1;
	}

	public final void add(Matrix3f matrix3f, Matrix3f matrix3f2) {
		this.m00 = matrix3f.m00 + matrix3f2.m00;
		this.m01 = matrix3f.m01 + matrix3f2.m01;
		this.m02 = matrix3f.m02 + matrix3f2.m02;
		this.m10 = matrix3f.m10 + matrix3f2.m10;
		this.m11 = matrix3f.m11 + matrix3f2.m11;
		this.m12 = matrix3f.m12 + matrix3f2.m12;
		this.m20 = matrix3f.m20 + matrix3f2.m20;
		this.m21 = matrix3f.m21 + matrix3f2.m21;
		this.m22 = matrix3f.m22 + matrix3f2.m22;
	}

	public final void add(Matrix3f matrix3f) {
		this.m00 += matrix3f.m00;
		this.m01 += matrix3f.m01;
		this.m02 += matrix3f.m02;
		this.m10 += matrix3f.m10;
		this.m11 += matrix3f.m11;
		this.m12 += matrix3f.m12;
		this.m20 += matrix3f.m20;
		this.m21 += matrix3f.m21;
		this.m22 += matrix3f.m22;
	}

	public final void sub(Matrix3f matrix3f, Matrix3f matrix3f2) {
		this.m00 = matrix3f.m00 - matrix3f2.m00;
		this.m01 = matrix3f.m01 - matrix3f2.m01;
		this.m02 = matrix3f.m02 - matrix3f2.m02;
		this.m10 = matrix3f.m10 - matrix3f2.m10;
		this.m11 = matrix3f.m11 - matrix3f2.m11;
		this.m12 = matrix3f.m12 - matrix3f2.m12;
		this.m20 = matrix3f.m20 - matrix3f2.m20;
		this.m21 = matrix3f.m21 - matrix3f2.m21;
		this.m22 = matrix3f.m22 - matrix3f2.m22;
	}

	public final void sub(Matrix3f matrix3f) {
		this.m00 -= matrix3f.m00;
		this.m01 -= matrix3f.m01;
		this.m02 -= matrix3f.m02;
		this.m10 -= matrix3f.m10;
		this.m11 -= matrix3f.m11;
		this.m12 -= matrix3f.m12;
		this.m20 -= matrix3f.m20;
		this.m21 -= matrix3f.m21;
		this.m22 -= matrix3f.m22;
	}

	public final void transpose() {
		float float1 = this.m10;
		this.m10 = this.m01;
		this.m01 = float1;
		float1 = this.m20;
		this.m20 = this.m02;
		this.m02 = float1;
		float1 = this.m21;
		this.m21 = this.m12;
		this.m12 = float1;
	}

	public final void transpose(Matrix3f matrix3f) {
		if (this != matrix3f) {
			this.m00 = matrix3f.m00;
			this.m01 = matrix3f.m10;
			this.m02 = matrix3f.m20;
			this.m10 = matrix3f.m01;
			this.m11 = matrix3f.m11;
			this.m12 = matrix3f.m21;
			this.m20 = matrix3f.m02;
			this.m21 = matrix3f.m12;
			this.m22 = matrix3f.m22;
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
			double double5 = Math.sin(axisAngle4d.angle);
			double double6 = Math.cos(axisAngle4d.angle);
			double double7 = 1.0 - double6;
			double double8 = double2 * double4;
			double double9 = double2 * double3;
			double double10 = double3 * double4;
			this.m00 = (float)(double7 * double2 * double2 + double6);
			this.m01 = (float)(double7 * double9 - double5 * double4);
			this.m02 = (float)(double7 * double8 + double5 * double3);
			this.m10 = (float)(double7 * double9 + double5 * double4);
			this.m11 = (float)(double7 * double3 * double3 + double6);
			this.m12 = (float)(double7 * double10 - double5 * double2);
			this.m20 = (float)(double7 * double8 - double5 * double3);
			this.m21 = (float)(double7 * double10 + double5 * double2);
			this.m22 = (float)(double7 * double4 * double4 + double6);
		}
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
	}

	public final void set(float[] floatArray) {
		this.m00 = floatArray[0];
		this.m01 = floatArray[1];
		this.m02 = floatArray[2];
		this.m10 = floatArray[3];
		this.m11 = floatArray[4];
		this.m12 = floatArray[5];
		this.m20 = floatArray[6];
		this.m21 = floatArray[7];
		this.m22 = floatArray[8];
	}

	public final void set(Matrix3f matrix3f) {
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

	public final void set(Matrix3d matrix3d) {
		this.m00 = (float)matrix3d.m00;
		this.m01 = (float)matrix3d.m01;
		this.m02 = (float)matrix3d.m02;
		this.m10 = (float)matrix3d.m10;
		this.m11 = (float)matrix3d.m11;
		this.m12 = (float)matrix3d.m12;
		this.m20 = (float)matrix3d.m20;
		this.m21 = (float)matrix3d.m21;
		this.m22 = (float)matrix3d.m22;
	}

	public final void invert(Matrix3f matrix3f) {
		this.invertGeneral(matrix3f);
	}

	public final void invert() {
		this.invertGeneral(this);
	}

	private final void invertGeneral(Matrix3f matrix3f) {
		double[] doubleArray = new double[9];
		double[] doubleArray2 = new double[9];
		int[] intArray = new int[3];
		doubleArray[0] = (double)matrix3f.m00;
		doubleArray[1] = (double)matrix3f.m01;
		doubleArray[2] = (double)matrix3f.m02;
		doubleArray[3] = (double)matrix3f.m10;
		doubleArray[4] = (double)matrix3f.m11;
		doubleArray[5] = (double)matrix3f.m12;
		doubleArray[6] = (double)matrix3f.m20;
		doubleArray[7] = (double)matrix3f.m21;
		doubleArray[8] = (double)matrix3f.m22;
		if (!luDecomposition(doubleArray, intArray)) {
			throw new SingularMatrixException(VecMathI18N.getString("Matrix3f12"));
		} else {
			for (int int1 = 0; int1 < 9; ++int1) {
				doubleArray2[int1] = 0.0;
			}

			doubleArray2[0] = 1.0;
			doubleArray2[4] = 1.0;
			doubleArray2[8] = 1.0;
			luBacksubstitution(doubleArray, intArray, doubleArray2);
			this.m00 = (float)doubleArray2[0];
			this.m01 = (float)doubleArray2[1];
			this.m02 = (float)doubleArray2[2];
			this.m10 = (float)doubleArray2[3];
			this.m11 = (float)doubleArray2[4];
			this.m12 = (float)doubleArray2[5];
			this.m20 = (float)doubleArray2[6];
			this.m21 = (float)doubleArray2[7];
			this.m22 = (float)doubleArray2[8];
		}
	}

	static boolean luDecomposition(double[] doubleArray, int[] intArray) {
		double[] doubleArray2 = new double[3];
		int int1 = 0;
		int int2 = 0;
		int int3;
		double double1;
		for (int3 = 3; int3-- != 0; doubleArray2[int2++] = 1.0 / double1) {
			double1 = 0.0;
			int int4 = 3;
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
		for (int3 = 0; int3 < 3; ++int3) {
			int int5;
			int int6;
			double double3;
			int int7;
			int int8;
			for (int1 = 0; int1 < int3; ++int1) {
				int5 = byte1 + 3 * int1 + int3;
				double3 = doubleArray[int5];
				int7 = int1;
				int8 = byte1 + 3 * int1;
				for (int6 = byte1 + int3; int7-- != 0; int6 += 3) {
					double3 -= doubleArray[int8] * doubleArray[int6];
					++int8;
				}

				doubleArray[int5] = double3;
			}

			double double4 = 0.0;
			int2 = -1;
			double double5;
			for (int1 = int3; int1 < 3; ++int1) {
				int5 = byte1 + 3 * int1 + int3;
				double3 = doubleArray[int5];
				int7 = int3;
				int8 = byte1 + 3 * int1;
				for (int6 = byte1 + int3; int7-- != 0; int6 += 3) {
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
				throw new RuntimeException(VecMathI18N.getString("Matrix3f13"));
			}

			if (int3 != int2) {
				int7 = 3;
				int8 = byte1 + 3 * int2;
				for (int6 = byte1 + 3 * int3; int7-- != 0; doubleArray[int6++] = double5) {
					double5 = doubleArray[int8];
					doubleArray[int8++] = doubleArray[int6];
				}

				doubleArray2[int2] = doubleArray2[int3];
			}

			intArray[int3] = int2;
			if (doubleArray[byte1 + 3 * int3 + int3] == 0.0) {
				return false;
			}

			if (int3 != 2) {
				double5 = 1.0 / doubleArray[byte1 + 3 * int3 + int3];
				int5 = byte1 + 3 * (int3 + 1) + int3;
				for (int1 = 2 - int3; int1-- != 0; int5 += 3) {
					doubleArray[int5] *= double5;
				}
			}
		}

		return true;
	}

	static void luBacksubstitution(double[] doubleArray, int[] intArray, double[] doubleArray2) {
		byte byte1 = 0;
		for (int int1 = 0; int1 < 3; ++int1) {
			int int2 = int1;
			int int3 = -1;
			int int4;
			for (int int5 = 0; int5 < 3; ++int5) {
				int int6 = intArray[byte1 + int5];
				double double1 = doubleArray2[int2 + 3 * int6];
				doubleArray2[int2 + 3 * int6] = doubleArray2[int2 + 3 * int5];
				if (int3 >= 0) {
					int4 = int5 * 3;
					for (int int7 = int3; int7 <= int5 - 1; ++int7) {
						double1 -= doubleArray[int4 + int7] * doubleArray2[int2 + 3 * int7];
					}
				} else if (double1 != 0.0) {
					int3 = int5;
				}

				doubleArray2[int2 + 3 * int5] = double1;
			}

			byte byte2 = 6;
			doubleArray2[int2 + 6] /= doubleArray[byte2 + 2];
			int4 = byte2 - 3;
			doubleArray2[int2 + 3] = (doubleArray2[int2 + 3] - doubleArray[int4 + 2] * doubleArray2[int2 + 6]) / doubleArray[int4 + 1];
			int4 -= 3;
			doubleArray2[int2 + 0] = (doubleArray2[int2 + 0] - doubleArray[int4 + 1] * doubleArray2[int2 + 3] - doubleArray[int4 + 2] * doubleArray2[int2 + 6]) / doubleArray[int4 + 0];
		}
	}

	public final float determinant() {
		float float1 = this.m00 * (this.m11 * this.m22 - this.m12 * this.m21) + this.m01 * (this.m12 * this.m20 - this.m10 * this.m22) + this.m02 * (this.m10 * this.m21 - this.m11 * this.m20);
		return float1;
	}

	public final void set(float float1) {
		this.m00 = float1;
		this.m01 = 0.0F;
		this.m02 = 0.0F;
		this.m10 = 0.0F;
		this.m11 = float1;
		this.m12 = 0.0F;
		this.m20 = 0.0F;
		this.m21 = 0.0F;
		this.m22 = float1;
	}

	public final void rotX(float float1) {
		float float2 = (float)Math.sin((double)float1);
		float float3 = (float)Math.cos((double)float1);
		this.m00 = 1.0F;
		this.m01 = 0.0F;
		this.m02 = 0.0F;
		this.m10 = 0.0F;
		this.m11 = float3;
		this.m12 = -float2;
		this.m20 = 0.0F;
		this.m21 = float2;
		this.m22 = float3;
	}

	public final void rotY(float float1) {
		float float2 = (float)Math.sin((double)float1);
		float float3 = (float)Math.cos((double)float1);
		this.m00 = float3;
		this.m01 = 0.0F;
		this.m02 = float2;
		this.m10 = 0.0F;
		this.m11 = 1.0F;
		this.m12 = 0.0F;
		this.m20 = -float2;
		this.m21 = 0.0F;
		this.m22 = float3;
	}

	public final void rotZ(float float1) {
		float float2 = (float)Math.sin((double)float1);
		float float3 = (float)Math.cos((double)float1);
		this.m00 = float3;
		this.m01 = -float2;
		this.m02 = 0.0F;
		this.m10 = float2;
		this.m11 = float3;
		this.m12 = 0.0F;
		this.m20 = 0.0F;
		this.m21 = 0.0F;
		this.m22 = 1.0F;
	}

	public final void mul(float float1) {
		this.m00 *= float1;
		this.m01 *= float1;
		this.m02 *= float1;
		this.m10 *= float1;
		this.m11 *= float1;
		this.m12 *= float1;
		this.m20 *= float1;
		this.m21 *= float1;
		this.m22 *= float1;
	}

	public final void mul(float float1, Matrix3f matrix3f) {
		this.m00 = float1 * matrix3f.m00;
		this.m01 = float1 * matrix3f.m01;
		this.m02 = float1 * matrix3f.m02;
		this.m10 = float1 * matrix3f.m10;
		this.m11 = float1 * matrix3f.m11;
		this.m12 = float1 * matrix3f.m12;
		this.m20 = float1 * matrix3f.m20;
		this.m21 = float1 * matrix3f.m21;
		this.m22 = float1 * matrix3f.m22;
	}

	public final void mul(Matrix3f matrix3f) {
		float float1 = this.m00 * matrix3f.m00 + this.m01 * matrix3f.m10 + this.m02 * matrix3f.m20;
		float float2 = this.m00 * matrix3f.m01 + this.m01 * matrix3f.m11 + this.m02 * matrix3f.m21;
		float float3 = this.m00 * matrix3f.m02 + this.m01 * matrix3f.m12 + this.m02 * matrix3f.m22;
		float float4 = this.m10 * matrix3f.m00 + this.m11 * matrix3f.m10 + this.m12 * matrix3f.m20;
		float float5 = this.m10 * matrix3f.m01 + this.m11 * matrix3f.m11 + this.m12 * matrix3f.m21;
		float float6 = this.m10 * matrix3f.m02 + this.m11 * matrix3f.m12 + this.m12 * matrix3f.m22;
		float float7 = this.m20 * matrix3f.m00 + this.m21 * matrix3f.m10 + this.m22 * matrix3f.m20;
		float float8 = this.m20 * matrix3f.m01 + this.m21 * matrix3f.m11 + this.m22 * matrix3f.m21;
		float float9 = this.m20 * matrix3f.m02 + this.m21 * matrix3f.m12 + this.m22 * matrix3f.m22;
		this.m00 = float1;
		this.m01 = float2;
		this.m02 = float3;
		this.m10 = float4;
		this.m11 = float5;
		this.m12 = float6;
		this.m20 = float7;
		this.m21 = float8;
		this.m22 = float9;
	}

	public final void mul(Matrix3f matrix3f, Matrix3f matrix3f2) {
		if (this != matrix3f && this != matrix3f2) {
			this.m00 = matrix3f.m00 * matrix3f2.m00 + matrix3f.m01 * matrix3f2.m10 + matrix3f.m02 * matrix3f2.m20;
			this.m01 = matrix3f.m00 * matrix3f2.m01 + matrix3f.m01 * matrix3f2.m11 + matrix3f.m02 * matrix3f2.m21;
			this.m02 = matrix3f.m00 * matrix3f2.m02 + matrix3f.m01 * matrix3f2.m12 + matrix3f.m02 * matrix3f2.m22;
			this.m10 = matrix3f.m10 * matrix3f2.m00 + matrix3f.m11 * matrix3f2.m10 + matrix3f.m12 * matrix3f2.m20;
			this.m11 = matrix3f.m10 * matrix3f2.m01 + matrix3f.m11 * matrix3f2.m11 + matrix3f.m12 * matrix3f2.m21;
			this.m12 = matrix3f.m10 * matrix3f2.m02 + matrix3f.m11 * matrix3f2.m12 + matrix3f.m12 * matrix3f2.m22;
			this.m20 = matrix3f.m20 * matrix3f2.m00 + matrix3f.m21 * matrix3f2.m10 + matrix3f.m22 * matrix3f2.m20;
			this.m21 = matrix3f.m20 * matrix3f2.m01 + matrix3f.m21 * matrix3f2.m11 + matrix3f.m22 * matrix3f2.m21;
			this.m22 = matrix3f.m20 * matrix3f2.m02 + matrix3f.m21 * matrix3f2.m12 + matrix3f.m22 * matrix3f2.m22;
		} else {
			float float1 = matrix3f.m00 * matrix3f2.m00 + matrix3f.m01 * matrix3f2.m10 + matrix3f.m02 * matrix3f2.m20;
			float float2 = matrix3f.m00 * matrix3f2.m01 + matrix3f.m01 * matrix3f2.m11 + matrix3f.m02 * matrix3f2.m21;
			float float3 = matrix3f.m00 * matrix3f2.m02 + matrix3f.m01 * matrix3f2.m12 + matrix3f.m02 * matrix3f2.m22;
			float float4 = matrix3f.m10 * matrix3f2.m00 + matrix3f.m11 * matrix3f2.m10 + matrix3f.m12 * matrix3f2.m20;
			float float5 = matrix3f.m10 * matrix3f2.m01 + matrix3f.m11 * matrix3f2.m11 + matrix3f.m12 * matrix3f2.m21;
			float float6 = matrix3f.m10 * matrix3f2.m02 + matrix3f.m11 * matrix3f2.m12 + matrix3f.m12 * matrix3f2.m22;
			float float7 = matrix3f.m20 * matrix3f2.m00 + matrix3f.m21 * matrix3f2.m10 + matrix3f.m22 * matrix3f2.m20;
			float float8 = matrix3f.m20 * matrix3f2.m01 + matrix3f.m21 * matrix3f2.m11 + matrix3f.m22 * matrix3f2.m21;
			float float9 = matrix3f.m20 * matrix3f2.m02 + matrix3f.m21 * matrix3f2.m12 + matrix3f.m22 * matrix3f2.m22;
			this.m00 = float1;
			this.m01 = float2;
			this.m02 = float3;
			this.m10 = float4;
			this.m11 = float5;
			this.m12 = float6;
			this.m20 = float7;
			this.m21 = float8;
			this.m22 = float9;
		}
	}

	public final void mulNormalize(Matrix3f matrix3f) {
		double[] doubleArray = new double[9];
		double[] doubleArray2 = new double[9];
		double[] doubleArray3 = new double[3];
		doubleArray[0] = (double)(this.m00 * matrix3f.m00 + this.m01 * matrix3f.m10 + this.m02 * matrix3f.m20);
		doubleArray[1] = (double)(this.m00 * matrix3f.m01 + this.m01 * matrix3f.m11 + this.m02 * matrix3f.m21);
		doubleArray[2] = (double)(this.m00 * matrix3f.m02 + this.m01 * matrix3f.m12 + this.m02 * matrix3f.m22);
		doubleArray[3] = (double)(this.m10 * matrix3f.m00 + this.m11 * matrix3f.m10 + this.m12 * matrix3f.m20);
		doubleArray[4] = (double)(this.m10 * matrix3f.m01 + this.m11 * matrix3f.m11 + this.m12 * matrix3f.m21);
		doubleArray[5] = (double)(this.m10 * matrix3f.m02 + this.m11 * matrix3f.m12 + this.m12 * matrix3f.m22);
		doubleArray[6] = (double)(this.m20 * matrix3f.m00 + this.m21 * matrix3f.m10 + this.m22 * matrix3f.m20);
		doubleArray[7] = (double)(this.m20 * matrix3f.m01 + this.m21 * matrix3f.m11 + this.m22 * matrix3f.m21);
		doubleArray[8] = (double)(this.m20 * matrix3f.m02 + this.m21 * matrix3f.m12 + this.m22 * matrix3f.m22);
		Matrix3d.compute_svd(doubleArray, doubleArray3, doubleArray2);
		this.m00 = (float)doubleArray2[0];
		this.m01 = (float)doubleArray2[1];
		this.m02 = (float)doubleArray2[2];
		this.m10 = (float)doubleArray2[3];
		this.m11 = (float)doubleArray2[4];
		this.m12 = (float)doubleArray2[5];
		this.m20 = (float)doubleArray2[6];
		this.m21 = (float)doubleArray2[7];
		this.m22 = (float)doubleArray2[8];
	}

	public final void mulNormalize(Matrix3f matrix3f, Matrix3f matrix3f2) {
		double[] doubleArray = new double[9];
		double[] doubleArray2 = new double[9];
		double[] doubleArray3 = new double[3];
		doubleArray[0] = (double)(matrix3f.m00 * matrix3f2.m00 + matrix3f.m01 * matrix3f2.m10 + matrix3f.m02 * matrix3f2.m20);
		doubleArray[1] = (double)(matrix3f.m00 * matrix3f2.m01 + matrix3f.m01 * matrix3f2.m11 + matrix3f.m02 * matrix3f2.m21);
		doubleArray[2] = (double)(matrix3f.m00 * matrix3f2.m02 + matrix3f.m01 * matrix3f2.m12 + matrix3f.m02 * matrix3f2.m22);
		doubleArray[3] = (double)(matrix3f.m10 * matrix3f2.m00 + matrix3f.m11 * matrix3f2.m10 + matrix3f.m12 * matrix3f2.m20);
		doubleArray[4] = (double)(matrix3f.m10 * matrix3f2.m01 + matrix3f.m11 * matrix3f2.m11 + matrix3f.m12 * matrix3f2.m21);
		doubleArray[5] = (double)(matrix3f.m10 * matrix3f2.m02 + matrix3f.m11 * matrix3f2.m12 + matrix3f.m12 * matrix3f2.m22);
		doubleArray[6] = (double)(matrix3f.m20 * matrix3f2.m00 + matrix3f.m21 * matrix3f2.m10 + matrix3f.m22 * matrix3f2.m20);
		doubleArray[7] = (double)(matrix3f.m20 * matrix3f2.m01 + matrix3f.m21 * matrix3f2.m11 + matrix3f.m22 * matrix3f2.m21);
		doubleArray[8] = (double)(matrix3f.m20 * matrix3f2.m02 + matrix3f.m21 * matrix3f2.m12 + matrix3f.m22 * matrix3f2.m22);
		Matrix3d.compute_svd(doubleArray, doubleArray3, doubleArray2);
		this.m00 = (float)doubleArray2[0];
		this.m01 = (float)doubleArray2[1];
		this.m02 = (float)doubleArray2[2];
		this.m10 = (float)doubleArray2[3];
		this.m11 = (float)doubleArray2[4];
		this.m12 = (float)doubleArray2[5];
		this.m20 = (float)doubleArray2[6];
		this.m21 = (float)doubleArray2[7];
		this.m22 = (float)doubleArray2[8];
	}

	public final void mulTransposeBoth(Matrix3f matrix3f, Matrix3f matrix3f2) {
		if (this != matrix3f && this != matrix3f2) {
			this.m00 = matrix3f.m00 * matrix3f2.m00 + matrix3f.m10 * matrix3f2.m01 + matrix3f.m20 * matrix3f2.m02;
			this.m01 = matrix3f.m00 * matrix3f2.m10 + matrix3f.m10 * matrix3f2.m11 + matrix3f.m20 * matrix3f2.m12;
			this.m02 = matrix3f.m00 * matrix3f2.m20 + matrix3f.m10 * matrix3f2.m21 + matrix3f.m20 * matrix3f2.m22;
			this.m10 = matrix3f.m01 * matrix3f2.m00 + matrix3f.m11 * matrix3f2.m01 + matrix3f.m21 * matrix3f2.m02;
			this.m11 = matrix3f.m01 * matrix3f2.m10 + matrix3f.m11 * matrix3f2.m11 + matrix3f.m21 * matrix3f2.m12;
			this.m12 = matrix3f.m01 * matrix3f2.m20 + matrix3f.m11 * matrix3f2.m21 + matrix3f.m21 * matrix3f2.m22;
			this.m20 = matrix3f.m02 * matrix3f2.m00 + matrix3f.m12 * matrix3f2.m01 + matrix3f.m22 * matrix3f2.m02;
			this.m21 = matrix3f.m02 * matrix3f2.m10 + matrix3f.m12 * matrix3f2.m11 + matrix3f.m22 * matrix3f2.m12;
			this.m22 = matrix3f.m02 * matrix3f2.m20 + matrix3f.m12 * matrix3f2.m21 + matrix3f.m22 * matrix3f2.m22;
		} else {
			float float1 = matrix3f.m00 * matrix3f2.m00 + matrix3f.m10 * matrix3f2.m01 + matrix3f.m20 * matrix3f2.m02;
			float float2 = matrix3f.m00 * matrix3f2.m10 + matrix3f.m10 * matrix3f2.m11 + matrix3f.m20 * matrix3f2.m12;
			float float3 = matrix3f.m00 * matrix3f2.m20 + matrix3f.m10 * matrix3f2.m21 + matrix3f.m20 * matrix3f2.m22;
			float float4 = matrix3f.m01 * matrix3f2.m00 + matrix3f.m11 * matrix3f2.m01 + matrix3f.m21 * matrix3f2.m02;
			float float5 = matrix3f.m01 * matrix3f2.m10 + matrix3f.m11 * matrix3f2.m11 + matrix3f.m21 * matrix3f2.m12;
			float float6 = matrix3f.m01 * matrix3f2.m20 + matrix3f.m11 * matrix3f2.m21 + matrix3f.m21 * matrix3f2.m22;
			float float7 = matrix3f.m02 * matrix3f2.m00 + matrix3f.m12 * matrix3f2.m01 + matrix3f.m22 * matrix3f2.m02;
			float float8 = matrix3f.m02 * matrix3f2.m10 + matrix3f.m12 * matrix3f2.m11 + matrix3f.m22 * matrix3f2.m12;
			float float9 = matrix3f.m02 * matrix3f2.m20 + matrix3f.m12 * matrix3f2.m21 + matrix3f.m22 * matrix3f2.m22;
			this.m00 = float1;
			this.m01 = float2;
			this.m02 = float3;
			this.m10 = float4;
			this.m11 = float5;
			this.m12 = float6;
			this.m20 = float7;
			this.m21 = float8;
			this.m22 = float9;
		}
	}

	public final void mulTransposeRight(Matrix3f matrix3f, Matrix3f matrix3f2) {
		if (this != matrix3f && this != matrix3f2) {
			this.m00 = matrix3f.m00 * matrix3f2.m00 + matrix3f.m01 * matrix3f2.m01 + matrix3f.m02 * matrix3f2.m02;
			this.m01 = matrix3f.m00 * matrix3f2.m10 + matrix3f.m01 * matrix3f2.m11 + matrix3f.m02 * matrix3f2.m12;
			this.m02 = matrix3f.m00 * matrix3f2.m20 + matrix3f.m01 * matrix3f2.m21 + matrix3f.m02 * matrix3f2.m22;
			this.m10 = matrix3f.m10 * matrix3f2.m00 + matrix3f.m11 * matrix3f2.m01 + matrix3f.m12 * matrix3f2.m02;
			this.m11 = matrix3f.m10 * matrix3f2.m10 + matrix3f.m11 * matrix3f2.m11 + matrix3f.m12 * matrix3f2.m12;
			this.m12 = matrix3f.m10 * matrix3f2.m20 + matrix3f.m11 * matrix3f2.m21 + matrix3f.m12 * matrix3f2.m22;
			this.m20 = matrix3f.m20 * matrix3f2.m00 + matrix3f.m21 * matrix3f2.m01 + matrix3f.m22 * matrix3f2.m02;
			this.m21 = matrix3f.m20 * matrix3f2.m10 + matrix3f.m21 * matrix3f2.m11 + matrix3f.m22 * matrix3f2.m12;
			this.m22 = matrix3f.m20 * matrix3f2.m20 + matrix3f.m21 * matrix3f2.m21 + matrix3f.m22 * matrix3f2.m22;
		} else {
			float float1 = matrix3f.m00 * matrix3f2.m00 + matrix3f.m01 * matrix3f2.m01 + matrix3f.m02 * matrix3f2.m02;
			float float2 = matrix3f.m00 * matrix3f2.m10 + matrix3f.m01 * matrix3f2.m11 + matrix3f.m02 * matrix3f2.m12;
			float float3 = matrix3f.m00 * matrix3f2.m20 + matrix3f.m01 * matrix3f2.m21 + matrix3f.m02 * matrix3f2.m22;
			float float4 = matrix3f.m10 * matrix3f2.m00 + matrix3f.m11 * matrix3f2.m01 + matrix3f.m12 * matrix3f2.m02;
			float float5 = matrix3f.m10 * matrix3f2.m10 + matrix3f.m11 * matrix3f2.m11 + matrix3f.m12 * matrix3f2.m12;
			float float6 = matrix3f.m10 * matrix3f2.m20 + matrix3f.m11 * matrix3f2.m21 + matrix3f.m12 * matrix3f2.m22;
			float float7 = matrix3f.m20 * matrix3f2.m00 + matrix3f.m21 * matrix3f2.m01 + matrix3f.m22 * matrix3f2.m02;
			float float8 = matrix3f.m20 * matrix3f2.m10 + matrix3f.m21 * matrix3f2.m11 + matrix3f.m22 * matrix3f2.m12;
			float float9 = matrix3f.m20 * matrix3f2.m20 + matrix3f.m21 * matrix3f2.m21 + matrix3f.m22 * matrix3f2.m22;
			this.m00 = float1;
			this.m01 = float2;
			this.m02 = float3;
			this.m10 = float4;
			this.m11 = float5;
			this.m12 = float6;
			this.m20 = float7;
			this.m21 = float8;
			this.m22 = float9;
		}
	}

	public final void mulTransposeLeft(Matrix3f matrix3f, Matrix3f matrix3f2) {
		if (this != matrix3f && this != matrix3f2) {
			this.m00 = matrix3f.m00 * matrix3f2.m00 + matrix3f.m10 * matrix3f2.m10 + matrix3f.m20 * matrix3f2.m20;
			this.m01 = matrix3f.m00 * matrix3f2.m01 + matrix3f.m10 * matrix3f2.m11 + matrix3f.m20 * matrix3f2.m21;
			this.m02 = matrix3f.m00 * matrix3f2.m02 + matrix3f.m10 * matrix3f2.m12 + matrix3f.m20 * matrix3f2.m22;
			this.m10 = matrix3f.m01 * matrix3f2.m00 + matrix3f.m11 * matrix3f2.m10 + matrix3f.m21 * matrix3f2.m20;
			this.m11 = matrix3f.m01 * matrix3f2.m01 + matrix3f.m11 * matrix3f2.m11 + matrix3f.m21 * matrix3f2.m21;
			this.m12 = matrix3f.m01 * matrix3f2.m02 + matrix3f.m11 * matrix3f2.m12 + matrix3f.m21 * matrix3f2.m22;
			this.m20 = matrix3f.m02 * matrix3f2.m00 + matrix3f.m12 * matrix3f2.m10 + matrix3f.m22 * matrix3f2.m20;
			this.m21 = matrix3f.m02 * matrix3f2.m01 + matrix3f.m12 * matrix3f2.m11 + matrix3f.m22 * matrix3f2.m21;
			this.m22 = matrix3f.m02 * matrix3f2.m02 + matrix3f.m12 * matrix3f2.m12 + matrix3f.m22 * matrix3f2.m22;
		} else {
			float float1 = matrix3f.m00 * matrix3f2.m00 + matrix3f.m10 * matrix3f2.m10 + matrix3f.m20 * matrix3f2.m20;
			float float2 = matrix3f.m00 * matrix3f2.m01 + matrix3f.m10 * matrix3f2.m11 + matrix3f.m20 * matrix3f2.m21;
			float float3 = matrix3f.m00 * matrix3f2.m02 + matrix3f.m10 * matrix3f2.m12 + matrix3f.m20 * matrix3f2.m22;
			float float4 = matrix3f.m01 * matrix3f2.m00 + matrix3f.m11 * matrix3f2.m10 + matrix3f.m21 * matrix3f2.m20;
			float float5 = matrix3f.m01 * matrix3f2.m01 + matrix3f.m11 * matrix3f2.m11 + matrix3f.m21 * matrix3f2.m21;
			float float6 = matrix3f.m01 * matrix3f2.m02 + matrix3f.m11 * matrix3f2.m12 + matrix3f.m21 * matrix3f2.m22;
			float float7 = matrix3f.m02 * matrix3f2.m00 + matrix3f.m12 * matrix3f2.m10 + matrix3f.m22 * matrix3f2.m20;
			float float8 = matrix3f.m02 * matrix3f2.m01 + matrix3f.m12 * matrix3f2.m11 + matrix3f.m22 * matrix3f2.m21;
			float float9 = matrix3f.m02 * matrix3f2.m02 + matrix3f.m12 * matrix3f2.m12 + matrix3f.m22 * matrix3f2.m22;
			this.m00 = float1;
			this.m01 = float2;
			this.m02 = float3;
			this.m10 = float4;
			this.m11 = float5;
			this.m12 = float6;
			this.m20 = float7;
			this.m21 = float8;
			this.m22 = float9;
		}
	}

	public final void normalize() {
		double[] doubleArray = new double[9];
		double[] doubleArray2 = new double[3];
		this.getScaleRotate(doubleArray2, doubleArray);
		this.m00 = (float)doubleArray[0];
		this.m01 = (float)doubleArray[1];
		this.m02 = (float)doubleArray[2];
		this.m10 = (float)doubleArray[3];
		this.m11 = (float)doubleArray[4];
		this.m12 = (float)doubleArray[5];
		this.m20 = (float)doubleArray[6];
		this.m21 = (float)doubleArray[7];
		this.m22 = (float)doubleArray[8];
	}

	public final void normalize(Matrix3f matrix3f) {
		double[] doubleArray = new double[9];
		double[] doubleArray2 = new double[9];
		double[] doubleArray3 = new double[3];
		doubleArray[0] = (double)matrix3f.m00;
		doubleArray[1] = (double)matrix3f.m01;
		doubleArray[2] = (double)matrix3f.m02;
		doubleArray[3] = (double)matrix3f.m10;
		doubleArray[4] = (double)matrix3f.m11;
		doubleArray[5] = (double)matrix3f.m12;
		doubleArray[6] = (double)matrix3f.m20;
		doubleArray[7] = (double)matrix3f.m21;
		doubleArray[8] = (double)matrix3f.m22;
		Matrix3d.compute_svd(doubleArray, doubleArray3, doubleArray2);
		this.m00 = (float)doubleArray2[0];
		this.m01 = (float)doubleArray2[1];
		this.m02 = (float)doubleArray2[2];
		this.m10 = (float)doubleArray2[3];
		this.m11 = (float)doubleArray2[4];
		this.m12 = (float)doubleArray2[5];
		this.m20 = (float)doubleArray2[6];
		this.m21 = (float)doubleArray2[7];
		this.m22 = (float)doubleArray2[8];
	}

	public final void normalizeCP() {
		float float1 = 1.0F / (float)Math.sqrt((double)(this.m00 * this.m00 + this.m10 * this.m10 + this.m20 * this.m20));
		this.m00 *= float1;
		this.m10 *= float1;
		this.m20 *= float1;
		float1 = 1.0F / (float)Math.sqrt((double)(this.m01 * this.m01 + this.m11 * this.m11 + this.m21 * this.m21));
		this.m01 *= float1;
		this.m11 *= float1;
		this.m21 *= float1;
		this.m02 = this.m10 * this.m21 - this.m11 * this.m20;
		this.m12 = this.m01 * this.m20 - this.m00 * this.m21;
		this.m22 = this.m00 * this.m11 - this.m01 * this.m10;
	}

	public final void normalizeCP(Matrix3f matrix3f) {
		float float1 = 1.0F / (float)Math.sqrt((double)(matrix3f.m00 * matrix3f.m00 + matrix3f.m10 * matrix3f.m10 + matrix3f.m20 * matrix3f.m20));
		this.m00 = matrix3f.m00 * float1;
		this.m10 = matrix3f.m10 * float1;
		this.m20 = matrix3f.m20 * float1;
		float1 = 1.0F / (float)Math.sqrt((double)(matrix3f.m01 * matrix3f.m01 + matrix3f.m11 * matrix3f.m11 + matrix3f.m21 * matrix3f.m21));
		this.m01 = matrix3f.m01 * float1;
		this.m11 = matrix3f.m11 * float1;
		this.m21 = matrix3f.m21 * float1;
		this.m02 = this.m10 * this.m21 - this.m11 * this.m20;
		this.m12 = this.m01 * this.m20 - this.m00 * this.m21;
		this.m22 = this.m00 * this.m11 - this.m01 * this.m10;
	}

	public boolean equals(Matrix3f matrix3f) {
		try {
			return this.m00 == matrix3f.m00 && this.m01 == matrix3f.m01 && this.m02 == matrix3f.m02 && this.m10 == matrix3f.m10 && this.m11 == matrix3f.m11 && this.m12 == matrix3f.m12 && this.m20 == matrix3f.m20 && this.m21 == matrix3f.m21 && this.m22 == matrix3f.m22;
		} catch (NullPointerException nullPointerException) {
			return false;
		}
	}

	public boolean equals(Object object) {
		try {
			Matrix3f matrix3f = (Matrix3f)object;
			return this.m00 == matrix3f.m00 && this.m01 == matrix3f.m01 && this.m02 == matrix3f.m02 && this.m10 == matrix3f.m10 && this.m11 == matrix3f.m11 && this.m12 == matrix3f.m12 && this.m20 == matrix3f.m20 && this.m21 == matrix3f.m21 && this.m22 == matrix3f.m22;
		} catch (ClassCastException classCastException) {
			return false;
		} catch (NullPointerException nullPointerException) {
			return false;
		}
	}

	public boolean epsilonEquals(Matrix3f matrix3f, float float1) {
		boolean boolean1 = true;
		if (Math.abs(this.m00 - matrix3f.m00) > float1) {
			boolean1 = false;
		}

		if (Math.abs(this.m01 - matrix3f.m01) > float1) {
			boolean1 = false;
		}

		if (Math.abs(this.m02 - matrix3f.m02) > float1) {
			boolean1 = false;
		}

		if (Math.abs(this.m10 - matrix3f.m10) > float1) {
			boolean1 = false;
		}

		if (Math.abs(this.m11 - matrix3f.m11) > float1) {
			boolean1 = false;
		}

		if (Math.abs(this.m12 - matrix3f.m12) > float1) {
			boolean1 = false;
		}

		if (Math.abs(this.m20 - matrix3f.m20) > float1) {
			boolean1 = false;
		}

		if (Math.abs(this.m21 - matrix3f.m21) > float1) {
			boolean1 = false;
		}

		if (Math.abs(this.m22 - matrix3f.m22) > float1) {
			boolean1 = false;
		}

		return boolean1;
	}

	public int hashCode() {
		long long1 = 1L;
		long1 = 31L * long1 + (long)VecMathUtil.floatToIntBits(this.m00);
		long1 = 31L * long1 + (long)VecMathUtil.floatToIntBits(this.m01);
		long1 = 31L * long1 + (long)VecMathUtil.floatToIntBits(this.m02);
		long1 = 31L * long1 + (long)VecMathUtil.floatToIntBits(this.m10);
		long1 = 31L * long1 + (long)VecMathUtil.floatToIntBits(this.m11);
		long1 = 31L * long1 + (long)VecMathUtil.floatToIntBits(this.m12);
		long1 = 31L * long1 + (long)VecMathUtil.floatToIntBits(this.m20);
		long1 = 31L * long1 + (long)VecMathUtil.floatToIntBits(this.m21);
		long1 = 31L * long1 + (long)VecMathUtil.floatToIntBits(this.m22);
		return (int)(long1 ^ long1 >> 32);
	}

	public final void setZero() {
		this.m00 = 0.0F;
		this.m01 = 0.0F;
		this.m02 = 0.0F;
		this.m10 = 0.0F;
		this.m11 = 0.0F;
		this.m12 = 0.0F;
		this.m20 = 0.0F;
		this.m21 = 0.0F;
		this.m22 = 0.0F;
	}

	public final void negate() {
		this.m00 = -this.m00;
		this.m01 = -this.m01;
		this.m02 = -this.m02;
		this.m10 = -this.m10;
		this.m11 = -this.m11;
		this.m12 = -this.m12;
		this.m20 = -this.m20;
		this.m21 = -this.m21;
		this.m22 = -this.m22;
	}

	public final void negate(Matrix3f matrix3f) {
		this.m00 = -matrix3f.m00;
		this.m01 = -matrix3f.m01;
		this.m02 = -matrix3f.m02;
		this.m10 = -matrix3f.m10;
		this.m11 = -matrix3f.m11;
		this.m12 = -matrix3f.m12;
		this.m20 = -matrix3f.m20;
		this.m21 = -matrix3f.m21;
		this.m22 = -matrix3f.m22;
	}

	public final void transform(Tuple3f tuple3f) {
		float float1 = this.m00 * tuple3f.x + this.m01 * tuple3f.y + this.m02 * tuple3f.z;
		float float2 = this.m10 * tuple3f.x + this.m11 * tuple3f.y + this.m12 * tuple3f.z;
		float float3 = this.m20 * tuple3f.x + this.m21 * tuple3f.y + this.m22 * tuple3f.z;
		tuple3f.set(float1, float2, float3);
	}

	public final void transform(Tuple3f tuple3f, Tuple3f tuple3f2) {
		float float1 = this.m00 * tuple3f.x + this.m01 * tuple3f.y + this.m02 * tuple3f.z;
		float float2 = this.m10 * tuple3f.x + this.m11 * tuple3f.y + this.m12 * tuple3f.z;
		tuple3f2.z = this.m20 * tuple3f.x + this.m21 * tuple3f.y + this.m22 * tuple3f.z;
		tuple3f2.x = float1;
		tuple3f2.y = float2;
	}

	void getScaleRotate(double[] doubleArray, double[] doubleArray2) {
		double[] doubleArray3 = new double[]{(double)this.m00, (double)this.m01, (double)this.m02, (double)this.m10, (double)this.m11, (double)this.m12, (double)this.m20, (double)this.m21, (double)this.m22};
		Matrix3d.compute_svd(doubleArray3, doubleArray, doubleArray2);
	}

	public Object clone() {
		Matrix3f matrix3f = null;
		try {
			matrix3f = (Matrix3f)super.clone();
			return matrix3f;
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
}
