package javax.vecmath;

import java.io.Serializable;


public class Matrix4d implements Serializable,Cloneable {
	static final long serialVersionUID = 8223903484171633710L;
	public double m00;
	public double m01;
	public double m02;
	public double m03;
	public double m10;
	public double m11;
	public double m12;
	public double m13;
	public double m20;
	public double m21;
	public double m22;
	public double m23;
	public double m30;
	public double m31;
	public double m32;
	public double m33;
	private static final double EPS = 1.0E-10;

	public Matrix4d(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, double double11, double double12, double double13, double double14, double double15, double double16) {
		this.m00 = double1;
		this.m01 = double2;
		this.m02 = double3;
		this.m03 = double4;
		this.m10 = double5;
		this.m11 = double6;
		this.m12 = double7;
		this.m13 = double8;
		this.m20 = double9;
		this.m21 = double10;
		this.m22 = double11;
		this.m23 = double12;
		this.m30 = double13;
		this.m31 = double14;
		this.m32 = double15;
		this.m33 = double16;
	}

	public Matrix4d(double[] doubleArray) {
		this.m00 = doubleArray[0];
		this.m01 = doubleArray[1];
		this.m02 = doubleArray[2];
		this.m03 = doubleArray[3];
		this.m10 = doubleArray[4];
		this.m11 = doubleArray[5];
		this.m12 = doubleArray[6];
		this.m13 = doubleArray[7];
		this.m20 = doubleArray[8];
		this.m21 = doubleArray[9];
		this.m22 = doubleArray[10];
		this.m23 = doubleArray[11];
		this.m30 = doubleArray[12];
		this.m31 = doubleArray[13];
		this.m32 = doubleArray[14];
		this.m33 = doubleArray[15];
	}

	public Matrix4d(Quat4d quat4d, Vector3d vector3d, double double1) {
		this.m00 = double1 * (1.0 - 2.0 * quat4d.y * quat4d.y - 2.0 * quat4d.z * quat4d.z);
		this.m10 = double1 * 2.0 * (quat4d.x * quat4d.y + quat4d.w * quat4d.z);
		this.m20 = double1 * 2.0 * (quat4d.x * quat4d.z - quat4d.w * quat4d.y);
		this.m01 = double1 * 2.0 * (quat4d.x * quat4d.y - quat4d.w * quat4d.z);
		this.m11 = double1 * (1.0 - 2.0 * quat4d.x * quat4d.x - 2.0 * quat4d.z * quat4d.z);
		this.m21 = double1 * 2.0 * (quat4d.y * quat4d.z + quat4d.w * quat4d.x);
		this.m02 = double1 * 2.0 * (quat4d.x * quat4d.z + quat4d.w * quat4d.y);
		this.m12 = double1 * 2.0 * (quat4d.y * quat4d.z - quat4d.w * quat4d.x);
		this.m22 = double1 * (1.0 - 2.0 * quat4d.x * quat4d.x - 2.0 * quat4d.y * quat4d.y);
		this.m03 = vector3d.x;
		this.m13 = vector3d.y;
		this.m23 = vector3d.z;
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.m33 = 1.0;
	}

	public Matrix4d(Quat4f quat4f, Vector3d vector3d, double double1) {
		this.m00 = double1 * (1.0 - 2.0 * (double)quat4f.y * (double)quat4f.y - 2.0 * (double)quat4f.z * (double)quat4f.z);
		this.m10 = double1 * 2.0 * (double)(quat4f.x * quat4f.y + quat4f.w * quat4f.z);
		this.m20 = double1 * 2.0 * (double)(quat4f.x * quat4f.z - quat4f.w * quat4f.y);
		this.m01 = double1 * 2.0 * (double)(quat4f.x * quat4f.y - quat4f.w * quat4f.z);
		this.m11 = double1 * (1.0 - 2.0 * (double)quat4f.x * (double)quat4f.x - 2.0 * (double)quat4f.z * (double)quat4f.z);
		this.m21 = double1 * 2.0 * (double)(quat4f.y * quat4f.z + quat4f.w * quat4f.x);
		this.m02 = double1 * 2.0 * (double)(quat4f.x * quat4f.z + quat4f.w * quat4f.y);
		this.m12 = double1 * 2.0 * (double)(quat4f.y * quat4f.z - quat4f.w * quat4f.x);
		this.m22 = double1 * (1.0 - 2.0 * (double)quat4f.x * (double)quat4f.x - 2.0 * (double)quat4f.y * (double)quat4f.y);
		this.m03 = vector3d.x;
		this.m13 = vector3d.y;
		this.m23 = vector3d.z;
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.m33 = 1.0;
	}

	public Matrix4d(Matrix4d matrix4d) {
		this.m00 = matrix4d.m00;
		this.m01 = matrix4d.m01;
		this.m02 = matrix4d.m02;
		this.m03 = matrix4d.m03;
		this.m10 = matrix4d.m10;
		this.m11 = matrix4d.m11;
		this.m12 = matrix4d.m12;
		this.m13 = matrix4d.m13;
		this.m20 = matrix4d.m20;
		this.m21 = matrix4d.m21;
		this.m22 = matrix4d.m22;
		this.m23 = matrix4d.m23;
		this.m30 = matrix4d.m30;
		this.m31 = matrix4d.m31;
		this.m32 = matrix4d.m32;
		this.m33 = matrix4d.m33;
	}

	public Matrix4d(Matrix4f matrix4f) {
		this.m00 = (double)matrix4f.m00;
		this.m01 = (double)matrix4f.m01;
		this.m02 = (double)matrix4f.m02;
		this.m03 = (double)matrix4f.m03;
		this.m10 = (double)matrix4f.m10;
		this.m11 = (double)matrix4f.m11;
		this.m12 = (double)matrix4f.m12;
		this.m13 = (double)matrix4f.m13;
		this.m20 = (double)matrix4f.m20;
		this.m21 = (double)matrix4f.m21;
		this.m22 = (double)matrix4f.m22;
		this.m23 = (double)matrix4f.m23;
		this.m30 = (double)matrix4f.m30;
		this.m31 = (double)matrix4f.m31;
		this.m32 = (double)matrix4f.m32;
		this.m33 = (double)matrix4f.m33;
	}

	public Matrix4d(Matrix3f matrix3f, Vector3d vector3d, double double1) {
		this.m00 = (double)matrix3f.m00 * double1;
		this.m01 = (double)matrix3f.m01 * double1;
		this.m02 = (double)matrix3f.m02 * double1;
		this.m03 = vector3d.x;
		this.m10 = (double)matrix3f.m10 * double1;
		this.m11 = (double)matrix3f.m11 * double1;
		this.m12 = (double)matrix3f.m12 * double1;
		this.m13 = vector3d.y;
		this.m20 = (double)matrix3f.m20 * double1;
		this.m21 = (double)matrix3f.m21 * double1;
		this.m22 = (double)matrix3f.m22 * double1;
		this.m23 = vector3d.z;
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.m33 = 1.0;
	}

	public Matrix4d(Matrix3d matrix3d, Vector3d vector3d, double double1) {
		this.m00 = matrix3d.m00 * double1;
		this.m01 = matrix3d.m01 * double1;
		this.m02 = matrix3d.m02 * double1;
		this.m03 = vector3d.x;
		this.m10 = matrix3d.m10 * double1;
		this.m11 = matrix3d.m11 * double1;
		this.m12 = matrix3d.m12 * double1;
		this.m13 = vector3d.y;
		this.m20 = matrix3d.m20 * double1;
		this.m21 = matrix3d.m21 * double1;
		this.m22 = matrix3d.m22 * double1;
		this.m23 = vector3d.z;
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.m33 = 1.0;
	}

	public Matrix4d() {
		this.m00 = 0.0;
		this.m01 = 0.0;
		this.m02 = 0.0;
		this.m03 = 0.0;
		this.m10 = 0.0;
		this.m11 = 0.0;
		this.m12 = 0.0;
		this.m13 = 0.0;
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = 0.0;
		this.m23 = 0.0;
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.m33 = 0.0;
	}

	public String toString() {
		return this.m00 + ", " + this.m01 + ", " + this.m02 + ", " + this.m03 + "\n" + this.m10 + ", " + this.m11 + ", " + this.m12 + ", " + this.m13 + "\n" + this.m20 + ", " + this.m21 + ", " + this.m22 + ", " + this.m23 + "\n" + this.m30 + ", " + this.m31 + ", " + this.m32 + ", " + this.m33 + "\n";
	}

	public final void setIdentity() {
		this.m00 = 1.0;
		this.m01 = 0.0;
		this.m02 = 0.0;
		this.m03 = 0.0;
		this.m10 = 0.0;
		this.m11 = 1.0;
		this.m12 = 0.0;
		this.m13 = 0.0;
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = 1.0;
		this.m23 = 0.0;
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.m33 = 1.0;
	}

	public final void setElement(int int1, int int2, double double1) {
		switch (int1) {
		case 0: 
			switch (int2) {
			case 0: 
				this.m00 = double1;
				return;
			
			case 1: 
				this.m01 = double1;
				return;
			
			case 2: 
				this.m02 = double1;
				return;
			
			case 3: 
				this.m03 = double1;
				return;
			
			default: 
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d0"));
			
			}

		
		case 1: 
			switch (int2) {
			case 0: 
				this.m10 = double1;
				return;
			
			case 1: 
				this.m11 = double1;
				return;
			
			case 2: 
				this.m12 = double1;
				return;
			
			case 3: 
				this.m13 = double1;
				return;
			
			default: 
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d0"));
			
			}

		
		case 2: 
			switch (int2) {
			case 0: 
				this.m20 = double1;
				return;
			
			case 1: 
				this.m21 = double1;
				return;
			
			case 2: 
				this.m22 = double1;
				return;
			
			case 3: 
				this.m23 = double1;
				return;
			
			default: 
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d0"));
			
			}

		
		case 3: 
			switch (int2) {
			case 0: 
				this.m30 = double1;
				return;
			
			case 1: 
				this.m31 = double1;
				return;
			
			case 2: 
				this.m32 = double1;
				return;
			
			case 3: 
				this.m33 = double1;
				return;
			
			default: 
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d0"));
			
			}

		
		default: 
			throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d0"));
		
		}
	}

	public final double getElement(int int1, int int2) {
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
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d1"));
			
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
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d1"));
			
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
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d1"));
			
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
		throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d1"));
	}

	public final void getRow(int int1, Vector4d vector4d) {
		if (int1 == 0) {
			vector4d.x = this.m00;
			vector4d.y = this.m01;
			vector4d.z = this.m02;
			vector4d.w = this.m03;
		} else if (int1 == 1) {
			vector4d.x = this.m10;
			vector4d.y = this.m11;
			vector4d.z = this.m12;
			vector4d.w = this.m13;
		} else if (int1 == 2) {
			vector4d.x = this.m20;
			vector4d.y = this.m21;
			vector4d.z = this.m22;
			vector4d.w = this.m23;
		} else {
			if (int1 != 3) {
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d2"));
			}

			vector4d.x = this.m30;
			vector4d.y = this.m31;
			vector4d.z = this.m32;
			vector4d.w = this.m33;
		}
	}

	public final void getRow(int int1, double[] doubleArray) {
		if (int1 == 0) {
			doubleArray[0] = this.m00;
			doubleArray[1] = this.m01;
			doubleArray[2] = this.m02;
			doubleArray[3] = this.m03;
		} else if (int1 == 1) {
			doubleArray[0] = this.m10;
			doubleArray[1] = this.m11;
			doubleArray[2] = this.m12;
			doubleArray[3] = this.m13;
		} else if (int1 == 2) {
			doubleArray[0] = this.m20;
			doubleArray[1] = this.m21;
			doubleArray[2] = this.m22;
			doubleArray[3] = this.m23;
		} else {
			if (int1 != 3) {
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d2"));
			}

			doubleArray[0] = this.m30;
			doubleArray[1] = this.m31;
			doubleArray[2] = this.m32;
			doubleArray[3] = this.m33;
		}
	}

	public final void getColumn(int int1, Vector4d vector4d) {
		if (int1 == 0) {
			vector4d.x = this.m00;
			vector4d.y = this.m10;
			vector4d.z = this.m20;
			vector4d.w = this.m30;
		} else if (int1 == 1) {
			vector4d.x = this.m01;
			vector4d.y = this.m11;
			vector4d.z = this.m21;
			vector4d.w = this.m31;
		} else if (int1 == 2) {
			vector4d.x = this.m02;
			vector4d.y = this.m12;
			vector4d.z = this.m22;
			vector4d.w = this.m32;
		} else {
			if (int1 != 3) {
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d3"));
			}

			vector4d.x = this.m03;
			vector4d.y = this.m13;
			vector4d.z = this.m23;
			vector4d.w = this.m33;
		}
	}

	public final void getColumn(int int1, double[] doubleArray) {
		if (int1 == 0) {
			doubleArray[0] = this.m00;
			doubleArray[1] = this.m10;
			doubleArray[2] = this.m20;
			doubleArray[3] = this.m30;
		} else if (int1 == 1) {
			doubleArray[0] = this.m01;
			doubleArray[1] = this.m11;
			doubleArray[2] = this.m21;
			doubleArray[3] = this.m31;
		} else if (int1 == 2) {
			doubleArray[0] = this.m02;
			doubleArray[1] = this.m12;
			doubleArray[2] = this.m22;
			doubleArray[3] = this.m32;
		} else {
			if (int1 != 3) {
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d3"));
			}

			doubleArray[0] = this.m03;
			doubleArray[1] = this.m13;
			doubleArray[2] = this.m23;
			doubleArray[3] = this.m33;
		}
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

	public final double get(Matrix3d matrix3d, Vector3d vector3d) {
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
		vector3d.x = this.m03;
		vector3d.y = this.m13;
		vector3d.z = this.m23;
		return Matrix3d.max3(doubleArray2);
	}

	public final double get(Matrix3f matrix3f, Vector3d vector3d) {
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
		vector3d.x = this.m03;
		vector3d.y = this.m13;
		vector3d.z = this.m23;
		return Matrix3d.max3(doubleArray2);
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

	public final void get(Quat4d quat4d) {
		double[] doubleArray = new double[9];
		double[] doubleArray2 = new double[3];
		this.getScaleRotate(doubleArray2, doubleArray);
		double double1 = 0.25 * (1.0 + doubleArray[0] + doubleArray[4] + doubleArray[8]);
		if (!((double1 < 0.0 ? -double1 : double1) < 1.0E-30)) {
			quat4d.w = Math.sqrt(double1);
			double1 = 0.25 / quat4d.w;
			quat4d.x = (doubleArray[7] - doubleArray[5]) * double1;
			quat4d.y = (doubleArray[2] - doubleArray[6]) * double1;
			quat4d.z = (doubleArray[3] - doubleArray[1]) * double1;
		} else {
			quat4d.w = 0.0;
			double1 = -0.5 * (doubleArray[4] + doubleArray[8]);
			if (!((double1 < 0.0 ? -double1 : double1) < 1.0E-30)) {
				quat4d.x = Math.sqrt(double1);
				double1 = 0.5 / quat4d.x;
				quat4d.y = doubleArray[3] * double1;
				quat4d.z = doubleArray[6] * double1;
			} else {
				quat4d.x = 0.0;
				double1 = 0.5 * (1.0 - doubleArray[8]);
				if (!((double1 < 0.0 ? -double1 : double1) < 1.0E-30)) {
					quat4d.y = Math.sqrt(double1);
					quat4d.z = doubleArray[7] / (2.0 * quat4d.y);
				} else {
					quat4d.y = 0.0;
					quat4d.z = 1.0;
				}
			}
		}
	}

	public final void get(Vector3d vector3d) {
		vector3d.x = this.m03;
		vector3d.y = this.m13;
		vector3d.z = this.m23;
	}

	public final void getRotationScale(Matrix3f matrix3f) {
		matrix3f.m00 = (float)this.m00;
		matrix3f.m01 = (float)this.m01;
		matrix3f.m02 = (float)this.m02;
		matrix3f.m10 = (float)this.m10;
		matrix3f.m11 = (float)this.m11;
		matrix3f.m12 = (float)this.m12;
		matrix3f.m20 = (float)this.m20;
		matrix3f.m21 = (float)this.m21;
		matrix3f.m22 = (float)this.m22;
	}

	public final void getRotationScale(Matrix3d matrix3d) {
		matrix3d.m00 = this.m00;
		matrix3d.m01 = this.m01;
		matrix3d.m02 = this.m02;
		matrix3d.m10 = this.m10;
		matrix3d.m11 = this.m11;
		matrix3d.m12 = this.m12;
		matrix3d.m20 = this.m20;
		matrix3d.m21 = this.m21;
		matrix3d.m22 = this.m22;
	}

	public final double getScale() {
		double[] doubleArray = new double[9];
		double[] doubleArray2 = new double[3];
		this.getScaleRotate(doubleArray2, doubleArray);
		return Matrix3d.max3(doubleArray2);
	}

	public final void setRotationScale(Matrix3d matrix3d) {
		this.m00 = matrix3d.m00;
		this.m01 = matrix3d.m01;
		this.m02 = matrix3d.m02;
		this.m10 = matrix3d.m10;
		this.m11 = matrix3d.m11;
		this.m12 = matrix3d.m12;
		this.m20 = matrix3d.m20;
		this.m21 = matrix3d.m21;
		this.m22 = matrix3d.m22;
	}

	public final void setRotationScale(Matrix3f matrix3f) {
		this.m00 = (double)matrix3f.m00;
		this.m01 = (double)matrix3f.m01;
		this.m02 = (double)matrix3f.m02;
		this.m10 = (double)matrix3f.m10;
		this.m11 = (double)matrix3f.m11;
		this.m12 = (double)matrix3f.m12;
		this.m20 = (double)matrix3f.m20;
		this.m21 = (double)matrix3f.m21;
		this.m22 = (double)matrix3f.m22;
	}

	public final void setScale(double double1) {
		double[] doubleArray = new double[9];
		double[] doubleArray2 = new double[3];
		this.getScaleRotate(doubleArray2, doubleArray);
		this.m00 = doubleArray[0] * double1;
		this.m01 = doubleArray[1] * double1;
		this.m02 = doubleArray[2] * double1;
		this.m10 = doubleArray[3] * double1;
		this.m11 = doubleArray[4] * double1;
		this.m12 = doubleArray[5] * double1;
		this.m20 = doubleArray[6] * double1;
		this.m21 = doubleArray[7] * double1;
		this.m22 = doubleArray[8] * double1;
	}

	public final void setRow(int int1, double double1, double double2, double double3, double double4) {
		switch (int1) {
		case 0: 
			this.m00 = double1;
			this.m01 = double2;
			this.m02 = double3;
			this.m03 = double4;
			break;
		
		case 1: 
			this.m10 = double1;
			this.m11 = double2;
			this.m12 = double3;
			this.m13 = double4;
			break;
		
		case 2: 
			this.m20 = double1;
			this.m21 = double2;
			this.m22 = double3;
			this.m23 = double4;
			break;
		
		case 3: 
			this.m30 = double1;
			this.m31 = double2;
			this.m32 = double3;
			this.m33 = double4;
			break;
		
		default: 
			throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d4"));
		
		}
	}

	public final void setRow(int int1, Vector4d vector4d) {
		switch (int1) {
		case 0: 
			this.m00 = vector4d.x;
			this.m01 = vector4d.y;
			this.m02 = vector4d.z;
			this.m03 = vector4d.w;
			break;
		
		case 1: 
			this.m10 = vector4d.x;
			this.m11 = vector4d.y;
			this.m12 = vector4d.z;
			this.m13 = vector4d.w;
			break;
		
		case 2: 
			this.m20 = vector4d.x;
			this.m21 = vector4d.y;
			this.m22 = vector4d.z;
			this.m23 = vector4d.w;
			break;
		
		case 3: 
			this.m30 = vector4d.x;
			this.m31 = vector4d.y;
			this.m32 = vector4d.z;
			this.m33 = vector4d.w;
			break;
		
		default: 
			throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d4"));
		
		}
	}

	public final void setRow(int int1, double[] doubleArray) {
		switch (int1) {
		case 0: 
			this.m00 = doubleArray[0];
			this.m01 = doubleArray[1];
			this.m02 = doubleArray[2];
			this.m03 = doubleArray[3];
			break;
		
		case 1: 
			this.m10 = doubleArray[0];
			this.m11 = doubleArray[1];
			this.m12 = doubleArray[2];
			this.m13 = doubleArray[3];
			break;
		
		case 2: 
			this.m20 = doubleArray[0];
			this.m21 = doubleArray[1];
			this.m22 = doubleArray[2];
			this.m23 = doubleArray[3];
			break;
		
		case 3: 
			this.m30 = doubleArray[0];
			this.m31 = doubleArray[1];
			this.m32 = doubleArray[2];
			this.m33 = doubleArray[3];
			break;
		
		default: 
			throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d4"));
		
		}
	}

	public final void setColumn(int int1, double double1, double double2, double double3, double double4) {
		switch (int1) {
		case 0: 
			this.m00 = double1;
			this.m10 = double2;
			this.m20 = double3;
			this.m30 = double4;
			break;
		
		case 1: 
			this.m01 = double1;
			this.m11 = double2;
			this.m21 = double3;
			this.m31 = double4;
			break;
		
		case 2: 
			this.m02 = double1;
			this.m12 = double2;
			this.m22 = double3;
			this.m32 = double4;
			break;
		
		case 3: 
			this.m03 = double1;
			this.m13 = double2;
			this.m23 = double3;
			this.m33 = double4;
			break;
		
		default: 
			throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d7"));
		
		}
	}

	public final void setColumn(int int1, Vector4d vector4d) {
		switch (int1) {
		case 0: 
			this.m00 = vector4d.x;
			this.m10 = vector4d.y;
			this.m20 = vector4d.z;
			this.m30 = vector4d.w;
			break;
		
		case 1: 
			this.m01 = vector4d.x;
			this.m11 = vector4d.y;
			this.m21 = vector4d.z;
			this.m31 = vector4d.w;
			break;
		
		case 2: 
			this.m02 = vector4d.x;
			this.m12 = vector4d.y;
			this.m22 = vector4d.z;
			this.m32 = vector4d.w;
			break;
		
		case 3: 
			this.m03 = vector4d.x;
			this.m13 = vector4d.y;
			this.m23 = vector4d.z;
			this.m33 = vector4d.w;
			break;
		
		default: 
			throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d7"));
		
		}
	}

	public final void setColumn(int int1, double[] doubleArray) {
		switch (int1) {
		case 0: 
			this.m00 = doubleArray[0];
			this.m10 = doubleArray[1];
			this.m20 = doubleArray[2];
			this.m30 = doubleArray[3];
			break;
		
		case 1: 
			this.m01 = doubleArray[0];
			this.m11 = doubleArray[1];
			this.m21 = doubleArray[2];
			this.m31 = doubleArray[3];
			break;
		
		case 2: 
			this.m02 = doubleArray[0];
			this.m12 = doubleArray[1];
			this.m22 = doubleArray[2];
			this.m32 = doubleArray[3];
			break;
		
		case 3: 
			this.m03 = doubleArray[0];
			this.m13 = doubleArray[1];
			this.m23 = doubleArray[2];
			this.m33 = doubleArray[3];
			break;
		
		default: 
			throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d7"));
		
		}
	}

	public final void add(double double1) {
		this.m00 += double1;
		this.m01 += double1;
		this.m02 += double1;
		this.m03 += double1;
		this.m10 += double1;
		this.m11 += double1;
		this.m12 += double1;
		this.m13 += double1;
		this.m20 += double1;
		this.m21 += double1;
		this.m22 += double1;
		this.m23 += double1;
		this.m30 += double1;
		this.m31 += double1;
		this.m32 += double1;
		this.m33 += double1;
	}

	public final void add(double double1, Matrix4d matrix4d) {
		this.m00 = matrix4d.m00 + double1;
		this.m01 = matrix4d.m01 + double1;
		this.m02 = matrix4d.m02 + double1;
		this.m03 = matrix4d.m03 + double1;
		this.m10 = matrix4d.m10 + double1;
		this.m11 = matrix4d.m11 + double1;
		this.m12 = matrix4d.m12 + double1;
		this.m13 = matrix4d.m13 + double1;
		this.m20 = matrix4d.m20 + double1;
		this.m21 = matrix4d.m21 + double1;
		this.m22 = matrix4d.m22 + double1;
		this.m23 = matrix4d.m23 + double1;
		this.m30 = matrix4d.m30 + double1;
		this.m31 = matrix4d.m31 + double1;
		this.m32 = matrix4d.m32 + double1;
		this.m33 = matrix4d.m33 + double1;
	}

	public final void add(Matrix4d matrix4d, Matrix4d matrix4d2) {
		this.m00 = matrix4d.m00 + matrix4d2.m00;
		this.m01 = matrix4d.m01 + matrix4d2.m01;
		this.m02 = matrix4d.m02 + matrix4d2.m02;
		this.m03 = matrix4d.m03 + matrix4d2.m03;
		this.m10 = matrix4d.m10 + matrix4d2.m10;
		this.m11 = matrix4d.m11 + matrix4d2.m11;
		this.m12 = matrix4d.m12 + matrix4d2.m12;
		this.m13 = matrix4d.m13 + matrix4d2.m13;
		this.m20 = matrix4d.m20 + matrix4d2.m20;
		this.m21 = matrix4d.m21 + matrix4d2.m21;
		this.m22 = matrix4d.m22 + matrix4d2.m22;
		this.m23 = matrix4d.m23 + matrix4d2.m23;
		this.m30 = matrix4d.m30 + matrix4d2.m30;
		this.m31 = matrix4d.m31 + matrix4d2.m31;
		this.m32 = matrix4d.m32 + matrix4d2.m32;
		this.m33 = matrix4d.m33 + matrix4d2.m33;
	}

	public final void add(Matrix4d matrix4d) {
		this.m00 += matrix4d.m00;
		this.m01 += matrix4d.m01;
		this.m02 += matrix4d.m02;
		this.m03 += matrix4d.m03;
		this.m10 += matrix4d.m10;
		this.m11 += matrix4d.m11;
		this.m12 += matrix4d.m12;
		this.m13 += matrix4d.m13;
		this.m20 += matrix4d.m20;
		this.m21 += matrix4d.m21;
		this.m22 += matrix4d.m22;
		this.m23 += matrix4d.m23;
		this.m30 += matrix4d.m30;
		this.m31 += matrix4d.m31;
		this.m32 += matrix4d.m32;
		this.m33 += matrix4d.m33;
	}

	public final void sub(Matrix4d matrix4d, Matrix4d matrix4d2) {
		this.m00 = matrix4d.m00 - matrix4d2.m00;
		this.m01 = matrix4d.m01 - matrix4d2.m01;
		this.m02 = matrix4d.m02 - matrix4d2.m02;
		this.m03 = matrix4d.m03 - matrix4d2.m03;
		this.m10 = matrix4d.m10 - matrix4d2.m10;
		this.m11 = matrix4d.m11 - matrix4d2.m11;
		this.m12 = matrix4d.m12 - matrix4d2.m12;
		this.m13 = matrix4d.m13 - matrix4d2.m13;
		this.m20 = matrix4d.m20 - matrix4d2.m20;
		this.m21 = matrix4d.m21 - matrix4d2.m21;
		this.m22 = matrix4d.m22 - matrix4d2.m22;
		this.m23 = matrix4d.m23 - matrix4d2.m23;
		this.m30 = matrix4d.m30 - matrix4d2.m30;
		this.m31 = matrix4d.m31 - matrix4d2.m31;
		this.m32 = matrix4d.m32 - matrix4d2.m32;
		this.m33 = matrix4d.m33 - matrix4d2.m33;
	}

	public final void sub(Matrix4d matrix4d) {
		this.m00 -= matrix4d.m00;
		this.m01 -= matrix4d.m01;
		this.m02 -= matrix4d.m02;
		this.m03 -= matrix4d.m03;
		this.m10 -= matrix4d.m10;
		this.m11 -= matrix4d.m11;
		this.m12 -= matrix4d.m12;
		this.m13 -= matrix4d.m13;
		this.m20 -= matrix4d.m20;
		this.m21 -= matrix4d.m21;
		this.m22 -= matrix4d.m22;
		this.m23 -= matrix4d.m23;
		this.m30 -= matrix4d.m30;
		this.m31 -= matrix4d.m31;
		this.m32 -= matrix4d.m32;
		this.m33 -= matrix4d.m33;
	}

	public final void transpose() {
		double double1 = this.m10;
		this.m10 = this.m01;
		this.m01 = double1;
		double1 = this.m20;
		this.m20 = this.m02;
		this.m02 = double1;
		double1 = this.m30;
		this.m30 = this.m03;
		this.m03 = double1;
		double1 = this.m21;
		this.m21 = this.m12;
		this.m12 = double1;
		double1 = this.m31;
		this.m31 = this.m13;
		this.m13 = double1;
		double1 = this.m32;
		this.m32 = this.m23;
		this.m23 = double1;
	}

	public final void transpose(Matrix4d matrix4d) {
		if (this != matrix4d) {
			this.m00 = matrix4d.m00;
			this.m01 = matrix4d.m10;
			this.m02 = matrix4d.m20;
			this.m03 = matrix4d.m30;
			this.m10 = matrix4d.m01;
			this.m11 = matrix4d.m11;
			this.m12 = matrix4d.m21;
			this.m13 = matrix4d.m31;
			this.m20 = matrix4d.m02;
			this.m21 = matrix4d.m12;
			this.m22 = matrix4d.m22;
			this.m23 = matrix4d.m32;
			this.m30 = matrix4d.m03;
			this.m31 = matrix4d.m13;
			this.m32 = matrix4d.m23;
			this.m33 = matrix4d.m33;
		} else {
			this.transpose();
		}
	}

	public final void set(double[] doubleArray) {
		this.m00 = doubleArray[0];
		this.m01 = doubleArray[1];
		this.m02 = doubleArray[2];
		this.m03 = doubleArray[3];
		this.m10 = doubleArray[4];
		this.m11 = doubleArray[5];
		this.m12 = doubleArray[6];
		this.m13 = doubleArray[7];
		this.m20 = doubleArray[8];
		this.m21 = doubleArray[9];
		this.m22 = doubleArray[10];
		this.m23 = doubleArray[11];
		this.m30 = doubleArray[12];
		this.m31 = doubleArray[13];
		this.m32 = doubleArray[14];
		this.m33 = doubleArray[15];
	}

	public final void set(Matrix3f matrix3f) {
		this.m00 = (double)matrix3f.m00;
		this.m01 = (double)matrix3f.m01;
		this.m02 = (double)matrix3f.m02;
		this.m03 = 0.0;
		this.m10 = (double)matrix3f.m10;
		this.m11 = (double)matrix3f.m11;
		this.m12 = (double)matrix3f.m12;
		this.m13 = 0.0;
		this.m20 = (double)matrix3f.m20;
		this.m21 = (double)matrix3f.m21;
		this.m22 = (double)matrix3f.m22;
		this.m23 = 0.0;
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.m33 = 1.0;
	}

	public final void set(Matrix3d matrix3d) {
		this.m00 = matrix3d.m00;
		this.m01 = matrix3d.m01;
		this.m02 = matrix3d.m02;
		this.m03 = 0.0;
		this.m10 = matrix3d.m10;
		this.m11 = matrix3d.m11;
		this.m12 = matrix3d.m12;
		this.m13 = 0.0;
		this.m20 = matrix3d.m20;
		this.m21 = matrix3d.m21;
		this.m22 = matrix3d.m22;
		this.m23 = 0.0;
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.m33 = 1.0;
	}

	public final void set(Quat4d quat4d) {
		this.m00 = 1.0 - 2.0 * quat4d.y * quat4d.y - 2.0 * quat4d.z * quat4d.z;
		this.m10 = 2.0 * (quat4d.x * quat4d.y + quat4d.w * quat4d.z);
		this.m20 = 2.0 * (quat4d.x * quat4d.z - quat4d.w * quat4d.y);
		this.m01 = 2.0 * (quat4d.x * quat4d.y - quat4d.w * quat4d.z);
		this.m11 = 1.0 - 2.0 * quat4d.x * quat4d.x - 2.0 * quat4d.z * quat4d.z;
		this.m21 = 2.0 * (quat4d.y * quat4d.z + quat4d.w * quat4d.x);
		this.m02 = 2.0 * (quat4d.x * quat4d.z + quat4d.w * quat4d.y);
		this.m12 = 2.0 * (quat4d.y * quat4d.z - quat4d.w * quat4d.x);
		this.m22 = 1.0 - 2.0 * quat4d.x * quat4d.x - 2.0 * quat4d.y * quat4d.y;
		this.m03 = 0.0;
		this.m13 = 0.0;
		this.m23 = 0.0;
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.m33 = 1.0;
	}

	public final void set(AxisAngle4d axisAngle4d) {
		double double1 = Math.sqrt(axisAngle4d.x * axisAngle4d.x + axisAngle4d.y * axisAngle4d.y + axisAngle4d.z * axisAngle4d.z);
		if (double1 < 1.0E-10) {
			this.m00 = 1.0;
			this.m01 = 0.0;
			this.m02 = 0.0;
			this.m10 = 0.0;
			this.m11 = 1.0;
			this.m12 = 0.0;
			this.m20 = 0.0;
			this.m21 = 0.0;
			this.m22 = 1.0;
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
			this.m00 = double7 * double2 * double2 + double6;
			this.m01 = double7 * double9 - double5 * double4;
			this.m02 = double7 * double8 + double5 * double3;
			this.m10 = double7 * double9 + double5 * double4;
			this.m11 = double7 * double3 * double3 + double6;
			this.m12 = double7 * double10 - double5 * double2;
			this.m20 = double7 * double8 - double5 * double3;
			this.m21 = double7 * double10 + double5 * double2;
			this.m22 = double7 * double4 * double4 + double6;
		}

		this.m03 = 0.0;
		this.m13 = 0.0;
		this.m23 = 0.0;
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.m33 = 1.0;
	}

	public final void set(Quat4f quat4f) {
		this.m00 = 1.0 - 2.0 * (double)quat4f.y * (double)quat4f.y - 2.0 * (double)quat4f.z * (double)quat4f.z;
		this.m10 = 2.0 * (double)(quat4f.x * quat4f.y + quat4f.w * quat4f.z);
		this.m20 = 2.0 * (double)(quat4f.x * quat4f.z - quat4f.w * quat4f.y);
		this.m01 = 2.0 * (double)(quat4f.x * quat4f.y - quat4f.w * quat4f.z);
		this.m11 = 1.0 - 2.0 * (double)quat4f.x * (double)quat4f.x - 2.0 * (double)quat4f.z * (double)quat4f.z;
		this.m21 = 2.0 * (double)(quat4f.y * quat4f.z + quat4f.w * quat4f.x);
		this.m02 = 2.0 * (double)(quat4f.x * quat4f.z + quat4f.w * quat4f.y);
		this.m12 = 2.0 * (double)(quat4f.y * quat4f.z - quat4f.w * quat4f.x);
		this.m22 = 1.0 - 2.0 * (double)quat4f.x * (double)quat4f.x - 2.0 * (double)quat4f.y * (double)quat4f.y;
		this.m03 = 0.0;
		this.m13 = 0.0;
		this.m23 = 0.0;
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.m33 = 1.0;
	}

	public final void set(AxisAngle4f axisAngle4f) {
		double double1 = Math.sqrt((double)(axisAngle4f.x * axisAngle4f.x + axisAngle4f.y * axisAngle4f.y + axisAngle4f.z * axisAngle4f.z));
		if (double1 < 1.0E-10) {
			this.m00 = 1.0;
			this.m01 = 0.0;
			this.m02 = 0.0;
			this.m10 = 0.0;
			this.m11 = 1.0;
			this.m12 = 0.0;
			this.m20 = 0.0;
			this.m21 = 0.0;
			this.m22 = 1.0;
		} else {
			double1 = 1.0 / double1;
			double double2 = (double)axisAngle4f.x * double1;
			double double3 = (double)axisAngle4f.y * double1;
			double double4 = (double)axisAngle4f.z * double1;
			double double5 = Math.sin((double)axisAngle4f.angle);
			double double6 = Math.cos((double)axisAngle4f.angle);
			double double7 = 1.0 - double6;
			double double8 = double2 * double4;
			double double9 = double2 * double3;
			double double10 = double3 * double4;
			this.m00 = double7 * double2 * double2 + double6;
			this.m01 = double7 * double9 - double5 * double4;
			this.m02 = double7 * double8 + double5 * double3;
			this.m10 = double7 * double9 + double5 * double4;
			this.m11 = double7 * double3 * double3 + double6;
			this.m12 = double7 * double10 - double5 * double2;
			this.m20 = double7 * double8 - double5 * double3;
			this.m21 = double7 * double10 + double5 * double2;
			this.m22 = double7 * double4 * double4 + double6;
		}

		this.m03 = 0.0;
		this.m13 = 0.0;
		this.m23 = 0.0;
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.m33 = 1.0;
	}

	public final void set(Quat4d quat4d, Vector3d vector3d, double double1) {
		this.m00 = double1 * (1.0 - 2.0 * quat4d.y * quat4d.y - 2.0 * quat4d.z * quat4d.z);
		this.m10 = double1 * 2.0 * (quat4d.x * quat4d.y + quat4d.w * quat4d.z);
		this.m20 = double1 * 2.0 * (quat4d.x * quat4d.z - quat4d.w * quat4d.y);
		this.m01 = double1 * 2.0 * (quat4d.x * quat4d.y - quat4d.w * quat4d.z);
		this.m11 = double1 * (1.0 - 2.0 * quat4d.x * quat4d.x - 2.0 * quat4d.z * quat4d.z);
		this.m21 = double1 * 2.0 * (quat4d.y * quat4d.z + quat4d.w * quat4d.x);
		this.m02 = double1 * 2.0 * (quat4d.x * quat4d.z + quat4d.w * quat4d.y);
		this.m12 = double1 * 2.0 * (quat4d.y * quat4d.z - quat4d.w * quat4d.x);
		this.m22 = double1 * (1.0 - 2.0 * quat4d.x * quat4d.x - 2.0 * quat4d.y * quat4d.y);
		this.m03 = vector3d.x;
		this.m13 = vector3d.y;
		this.m23 = vector3d.z;
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.m33 = 1.0;
	}

	public final void set(Quat4f quat4f, Vector3d vector3d, double double1) {
		this.m00 = double1 * (1.0 - 2.0 * (double)quat4f.y * (double)quat4f.y - 2.0 * (double)quat4f.z * (double)quat4f.z);
		this.m10 = double1 * 2.0 * (double)(quat4f.x * quat4f.y + quat4f.w * quat4f.z);
		this.m20 = double1 * 2.0 * (double)(quat4f.x * quat4f.z - quat4f.w * quat4f.y);
		this.m01 = double1 * 2.0 * (double)(quat4f.x * quat4f.y - quat4f.w * quat4f.z);
		this.m11 = double1 * (1.0 - 2.0 * (double)quat4f.x * (double)quat4f.x - 2.0 * (double)quat4f.z * (double)quat4f.z);
		this.m21 = double1 * 2.0 * (double)(quat4f.y * quat4f.z + quat4f.w * quat4f.x);
		this.m02 = double1 * 2.0 * (double)(quat4f.x * quat4f.z + quat4f.w * quat4f.y);
		this.m12 = double1 * 2.0 * (double)(quat4f.y * quat4f.z - quat4f.w * quat4f.x);
		this.m22 = double1 * (1.0 - 2.0 * (double)quat4f.x * (double)quat4f.x - 2.0 * (double)quat4f.y * (double)quat4f.y);
		this.m03 = vector3d.x;
		this.m13 = vector3d.y;
		this.m23 = vector3d.z;
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.m33 = 1.0;
	}

	public final void set(Quat4f quat4f, Vector3f vector3f, float float1) {
		this.m00 = (double)float1 * (1.0 - 2.0 * (double)quat4f.y * (double)quat4f.y - 2.0 * (double)quat4f.z * (double)quat4f.z);
		this.m10 = (double)float1 * 2.0 * (double)(quat4f.x * quat4f.y + quat4f.w * quat4f.z);
		this.m20 = (double)float1 * 2.0 * (double)(quat4f.x * quat4f.z - quat4f.w * quat4f.y);
		this.m01 = (double)float1 * 2.0 * (double)(quat4f.x * quat4f.y - quat4f.w * quat4f.z);
		this.m11 = (double)float1 * (1.0 - 2.0 * (double)quat4f.x * (double)quat4f.x - 2.0 * (double)quat4f.z * (double)quat4f.z);
		this.m21 = (double)float1 * 2.0 * (double)(quat4f.y * quat4f.z + quat4f.w * quat4f.x);
		this.m02 = (double)float1 * 2.0 * (double)(quat4f.x * quat4f.z + quat4f.w * quat4f.y);
		this.m12 = (double)float1 * 2.0 * (double)(quat4f.y * quat4f.z - quat4f.w * quat4f.x);
		this.m22 = (double)float1 * (1.0 - 2.0 * (double)quat4f.x * (double)quat4f.x - 2.0 * (double)quat4f.y * (double)quat4f.y);
		this.m03 = (double)vector3f.x;
		this.m13 = (double)vector3f.y;
		this.m23 = (double)vector3f.z;
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.m33 = 1.0;
	}

	public final void set(Matrix4f matrix4f) {
		this.m00 = (double)matrix4f.m00;
		this.m01 = (double)matrix4f.m01;
		this.m02 = (double)matrix4f.m02;
		this.m03 = (double)matrix4f.m03;
		this.m10 = (double)matrix4f.m10;
		this.m11 = (double)matrix4f.m11;
		this.m12 = (double)matrix4f.m12;
		this.m13 = (double)matrix4f.m13;
		this.m20 = (double)matrix4f.m20;
		this.m21 = (double)matrix4f.m21;
		this.m22 = (double)matrix4f.m22;
		this.m23 = (double)matrix4f.m23;
		this.m30 = (double)matrix4f.m30;
		this.m31 = (double)matrix4f.m31;
		this.m32 = (double)matrix4f.m32;
		this.m33 = (double)matrix4f.m33;
	}

	public final void set(Matrix4d matrix4d) {
		this.m00 = matrix4d.m00;
		this.m01 = matrix4d.m01;
		this.m02 = matrix4d.m02;
		this.m03 = matrix4d.m03;
		this.m10 = matrix4d.m10;
		this.m11 = matrix4d.m11;
		this.m12 = matrix4d.m12;
		this.m13 = matrix4d.m13;
		this.m20 = matrix4d.m20;
		this.m21 = matrix4d.m21;
		this.m22 = matrix4d.m22;
		this.m23 = matrix4d.m23;
		this.m30 = matrix4d.m30;
		this.m31 = matrix4d.m31;
		this.m32 = matrix4d.m32;
		this.m33 = matrix4d.m33;
	}

	public final void invert(Matrix4d matrix4d) {
		this.invertGeneral(matrix4d);
	}

	public final void invert() {
		this.invertGeneral(this);
	}

	final void invertGeneral(Matrix4d matrix4d) {
		double[] doubleArray = new double[16];
		int[] intArray = new int[4];
		double[] doubleArray2 = new double[]{matrix4d.m00, matrix4d.m01, matrix4d.m02, matrix4d.m03, matrix4d.m10, matrix4d.m11, matrix4d.m12, matrix4d.m13, matrix4d.m20, matrix4d.m21, matrix4d.m22, matrix4d.m23, matrix4d.m30, matrix4d.m31, matrix4d.m32, matrix4d.m33};
		if (!luDecomposition(doubleArray2, intArray)) {
			throw new SingularMatrixException(VecMathI18N.getString("Matrix4d10"));
		} else {
			for (int int1 = 0; int1 < 16; ++int1) {
				doubleArray[int1] = 0.0;
			}

			doubleArray[0] = 1.0;
			doubleArray[5] = 1.0;
			doubleArray[10] = 1.0;
			doubleArray[15] = 1.0;
			luBacksubstitution(doubleArray2, intArray, doubleArray);
			this.m00 = doubleArray[0];
			this.m01 = doubleArray[1];
			this.m02 = doubleArray[2];
			this.m03 = doubleArray[3];
			this.m10 = doubleArray[4];
			this.m11 = doubleArray[5];
			this.m12 = doubleArray[6];
			this.m13 = doubleArray[7];
			this.m20 = doubleArray[8];
			this.m21 = doubleArray[9];
			this.m22 = doubleArray[10];
			this.m23 = doubleArray[11];
			this.m30 = doubleArray[12];
			this.m31 = doubleArray[13];
			this.m32 = doubleArray[14];
			this.m33 = doubleArray[15];
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
				throw new RuntimeException(VecMathI18N.getString("Matrix4d11"));
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

	public final double determinant() {
		double double1 = this.m00 * (this.m11 * this.m22 * this.m33 + this.m12 * this.m23 * this.m31 + this.m13 * this.m21 * this.m32 - this.m13 * this.m22 * this.m31 - this.m11 * this.m23 * this.m32 - this.m12 * this.m21 * this.m33);
		double1 -= this.m01 * (this.m10 * this.m22 * this.m33 + this.m12 * this.m23 * this.m30 + this.m13 * this.m20 * this.m32 - this.m13 * this.m22 * this.m30 - this.m10 * this.m23 * this.m32 - this.m12 * this.m20 * this.m33);
		double1 += this.m02 * (this.m10 * this.m21 * this.m33 + this.m11 * this.m23 * this.m30 + this.m13 * this.m20 * this.m31 - this.m13 * this.m21 * this.m30 - this.m10 * this.m23 * this.m31 - this.m11 * this.m20 * this.m33);
		double1 -= this.m03 * (this.m10 * this.m21 * this.m32 + this.m11 * this.m22 * this.m30 + this.m12 * this.m20 * this.m31 - this.m12 * this.m21 * this.m30 - this.m10 * this.m22 * this.m31 - this.m11 * this.m20 * this.m32);
		return double1;
	}

	public final void set(double double1) {
		this.m00 = double1;
		this.m01 = 0.0;
		this.m02 = 0.0;
		this.m03 = 0.0;
		this.m10 = 0.0;
		this.m11 = double1;
		this.m12 = 0.0;
		this.m13 = 0.0;
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = double1;
		this.m23 = 0.0;
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.m33 = 1.0;
	}

	public final void set(Vector3d vector3d) {
		this.m00 = 1.0;
		this.m01 = 0.0;
		this.m02 = 0.0;
		this.m03 = vector3d.x;
		this.m10 = 0.0;
		this.m11 = 1.0;
		this.m12 = 0.0;
		this.m13 = vector3d.y;
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = 1.0;
		this.m23 = vector3d.z;
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.m33 = 1.0;
	}

	public final void set(double double1, Vector3d vector3d) {
		this.m00 = double1;
		this.m01 = 0.0;
		this.m02 = 0.0;
		this.m03 = vector3d.x;
		this.m10 = 0.0;
		this.m11 = double1;
		this.m12 = 0.0;
		this.m13 = vector3d.y;
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = double1;
		this.m23 = vector3d.z;
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.m33 = 1.0;
	}

	public final void set(Vector3d vector3d, double double1) {
		this.m00 = double1;
		this.m01 = 0.0;
		this.m02 = 0.0;
		this.m03 = double1 * vector3d.x;
		this.m10 = 0.0;
		this.m11 = double1;
		this.m12 = 0.0;
		this.m13 = double1 * vector3d.y;
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = double1;
		this.m23 = double1 * vector3d.z;
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.m33 = 1.0;
	}

	public final void set(Matrix3f matrix3f, Vector3f vector3f, float float1) {
		this.m00 = (double)(matrix3f.m00 * float1);
		this.m01 = (double)(matrix3f.m01 * float1);
		this.m02 = (double)(matrix3f.m02 * float1);
		this.m03 = (double)vector3f.x;
		this.m10 = (double)(matrix3f.m10 * float1);
		this.m11 = (double)(matrix3f.m11 * float1);
		this.m12 = (double)(matrix3f.m12 * float1);
		this.m13 = (double)vector3f.y;
		this.m20 = (double)(matrix3f.m20 * float1);
		this.m21 = (double)(matrix3f.m21 * float1);
		this.m22 = (double)(matrix3f.m22 * float1);
		this.m23 = (double)vector3f.z;
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.m33 = 1.0;
	}

	public final void set(Matrix3d matrix3d, Vector3d vector3d, double double1) {
		this.m00 = matrix3d.m00 * double1;
		this.m01 = matrix3d.m01 * double1;
		this.m02 = matrix3d.m02 * double1;
		this.m03 = vector3d.x;
		this.m10 = matrix3d.m10 * double1;
		this.m11 = matrix3d.m11 * double1;
		this.m12 = matrix3d.m12 * double1;
		this.m13 = vector3d.y;
		this.m20 = matrix3d.m20 * double1;
		this.m21 = matrix3d.m21 * double1;
		this.m22 = matrix3d.m22 * double1;
		this.m23 = vector3d.z;
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.m33 = 1.0;
	}

	public final void setTranslation(Vector3d vector3d) {
		this.m03 = vector3d.x;
		this.m13 = vector3d.y;
		this.m23 = vector3d.z;
	}

	public final void rotX(double double1) {
		double double2 = Math.sin(double1);
		double double3 = Math.cos(double1);
		this.m00 = 1.0;
		this.m01 = 0.0;
		this.m02 = 0.0;
		this.m03 = 0.0;
		this.m10 = 0.0;
		this.m11 = double3;
		this.m12 = -double2;
		this.m13 = 0.0;
		this.m20 = 0.0;
		this.m21 = double2;
		this.m22 = double3;
		this.m23 = 0.0;
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.m33 = 1.0;
	}

	public final void rotY(double double1) {
		double double2 = Math.sin(double1);
		double double3 = Math.cos(double1);
		this.m00 = double3;
		this.m01 = 0.0;
		this.m02 = double2;
		this.m03 = 0.0;
		this.m10 = 0.0;
		this.m11 = 1.0;
		this.m12 = 0.0;
		this.m13 = 0.0;
		this.m20 = -double2;
		this.m21 = 0.0;
		this.m22 = double3;
		this.m23 = 0.0;
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.m33 = 1.0;
	}

	public final void rotZ(double double1) {
		double double2 = Math.sin(double1);
		double double3 = Math.cos(double1);
		this.m00 = double3;
		this.m01 = -double2;
		this.m02 = 0.0;
		this.m03 = 0.0;
		this.m10 = double2;
		this.m11 = double3;
		this.m12 = 0.0;
		this.m13 = 0.0;
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = 1.0;
		this.m23 = 0.0;
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.m33 = 1.0;
	}

	public final void mul(double double1) {
		this.m00 *= double1;
		this.m01 *= double1;
		this.m02 *= double1;
		this.m03 *= double1;
		this.m10 *= double1;
		this.m11 *= double1;
		this.m12 *= double1;
		this.m13 *= double1;
		this.m20 *= double1;
		this.m21 *= double1;
		this.m22 *= double1;
		this.m23 *= double1;
		this.m30 *= double1;
		this.m31 *= double1;
		this.m32 *= double1;
		this.m33 *= double1;
	}

	public final void mul(double double1, Matrix4d matrix4d) {
		this.m00 = matrix4d.m00 * double1;
		this.m01 = matrix4d.m01 * double1;
		this.m02 = matrix4d.m02 * double1;
		this.m03 = matrix4d.m03 * double1;
		this.m10 = matrix4d.m10 * double1;
		this.m11 = matrix4d.m11 * double1;
		this.m12 = matrix4d.m12 * double1;
		this.m13 = matrix4d.m13 * double1;
		this.m20 = matrix4d.m20 * double1;
		this.m21 = matrix4d.m21 * double1;
		this.m22 = matrix4d.m22 * double1;
		this.m23 = matrix4d.m23 * double1;
		this.m30 = matrix4d.m30 * double1;
		this.m31 = matrix4d.m31 * double1;
		this.m32 = matrix4d.m32 * double1;
		this.m33 = matrix4d.m33 * double1;
	}

	public final void mul(Matrix4d matrix4d) {
		double double1 = this.m00 * matrix4d.m00 + this.m01 * matrix4d.m10 + this.m02 * matrix4d.m20 + this.m03 * matrix4d.m30;
		double double2 = this.m00 * matrix4d.m01 + this.m01 * matrix4d.m11 + this.m02 * matrix4d.m21 + this.m03 * matrix4d.m31;
		double double3 = this.m00 * matrix4d.m02 + this.m01 * matrix4d.m12 + this.m02 * matrix4d.m22 + this.m03 * matrix4d.m32;
		double double4 = this.m00 * matrix4d.m03 + this.m01 * matrix4d.m13 + this.m02 * matrix4d.m23 + this.m03 * matrix4d.m33;
		double double5 = this.m10 * matrix4d.m00 + this.m11 * matrix4d.m10 + this.m12 * matrix4d.m20 + this.m13 * matrix4d.m30;
		double double6 = this.m10 * matrix4d.m01 + this.m11 * matrix4d.m11 + this.m12 * matrix4d.m21 + this.m13 * matrix4d.m31;
		double double7 = this.m10 * matrix4d.m02 + this.m11 * matrix4d.m12 + this.m12 * matrix4d.m22 + this.m13 * matrix4d.m32;
		double double8 = this.m10 * matrix4d.m03 + this.m11 * matrix4d.m13 + this.m12 * matrix4d.m23 + this.m13 * matrix4d.m33;
		double double9 = this.m20 * matrix4d.m00 + this.m21 * matrix4d.m10 + this.m22 * matrix4d.m20 + this.m23 * matrix4d.m30;
		double double10 = this.m20 * matrix4d.m01 + this.m21 * matrix4d.m11 + this.m22 * matrix4d.m21 + this.m23 * matrix4d.m31;
		double double11 = this.m20 * matrix4d.m02 + this.m21 * matrix4d.m12 + this.m22 * matrix4d.m22 + this.m23 * matrix4d.m32;
		double double12 = this.m20 * matrix4d.m03 + this.m21 * matrix4d.m13 + this.m22 * matrix4d.m23 + this.m23 * matrix4d.m33;
		double double13 = this.m30 * matrix4d.m00 + this.m31 * matrix4d.m10 + this.m32 * matrix4d.m20 + this.m33 * matrix4d.m30;
		double double14 = this.m30 * matrix4d.m01 + this.m31 * matrix4d.m11 + this.m32 * matrix4d.m21 + this.m33 * matrix4d.m31;
		double double15 = this.m30 * matrix4d.m02 + this.m31 * matrix4d.m12 + this.m32 * matrix4d.m22 + this.m33 * matrix4d.m32;
		double double16 = this.m30 * matrix4d.m03 + this.m31 * matrix4d.m13 + this.m32 * matrix4d.m23 + this.m33 * matrix4d.m33;
		this.m00 = double1;
		this.m01 = double2;
		this.m02 = double3;
		this.m03 = double4;
		this.m10 = double5;
		this.m11 = double6;
		this.m12 = double7;
		this.m13 = double8;
		this.m20 = double9;
		this.m21 = double10;
		this.m22 = double11;
		this.m23 = double12;
		this.m30 = double13;
		this.m31 = double14;
		this.m32 = double15;
		this.m33 = double16;
	}

	public final void mul(Matrix4d matrix4d, Matrix4d matrix4d2) {
		if (this != matrix4d && this != matrix4d2) {
			this.m00 = matrix4d.m00 * matrix4d2.m00 + matrix4d.m01 * matrix4d2.m10 + matrix4d.m02 * matrix4d2.m20 + matrix4d.m03 * matrix4d2.m30;
			this.m01 = matrix4d.m00 * matrix4d2.m01 + matrix4d.m01 * matrix4d2.m11 + matrix4d.m02 * matrix4d2.m21 + matrix4d.m03 * matrix4d2.m31;
			this.m02 = matrix4d.m00 * matrix4d2.m02 + matrix4d.m01 * matrix4d2.m12 + matrix4d.m02 * matrix4d2.m22 + matrix4d.m03 * matrix4d2.m32;
			this.m03 = matrix4d.m00 * matrix4d2.m03 + matrix4d.m01 * matrix4d2.m13 + matrix4d.m02 * matrix4d2.m23 + matrix4d.m03 * matrix4d2.m33;
			this.m10 = matrix4d.m10 * matrix4d2.m00 + matrix4d.m11 * matrix4d2.m10 + matrix4d.m12 * matrix4d2.m20 + matrix4d.m13 * matrix4d2.m30;
			this.m11 = matrix4d.m10 * matrix4d2.m01 + matrix4d.m11 * matrix4d2.m11 + matrix4d.m12 * matrix4d2.m21 + matrix4d.m13 * matrix4d2.m31;
			this.m12 = matrix4d.m10 * matrix4d2.m02 + matrix4d.m11 * matrix4d2.m12 + matrix4d.m12 * matrix4d2.m22 + matrix4d.m13 * matrix4d2.m32;
			this.m13 = matrix4d.m10 * matrix4d2.m03 + matrix4d.m11 * matrix4d2.m13 + matrix4d.m12 * matrix4d2.m23 + matrix4d.m13 * matrix4d2.m33;
			this.m20 = matrix4d.m20 * matrix4d2.m00 + matrix4d.m21 * matrix4d2.m10 + matrix4d.m22 * matrix4d2.m20 + matrix4d.m23 * matrix4d2.m30;
			this.m21 = matrix4d.m20 * matrix4d2.m01 + matrix4d.m21 * matrix4d2.m11 + matrix4d.m22 * matrix4d2.m21 + matrix4d.m23 * matrix4d2.m31;
			this.m22 = matrix4d.m20 * matrix4d2.m02 + matrix4d.m21 * matrix4d2.m12 + matrix4d.m22 * matrix4d2.m22 + matrix4d.m23 * matrix4d2.m32;
			this.m23 = matrix4d.m20 * matrix4d2.m03 + matrix4d.m21 * matrix4d2.m13 + matrix4d.m22 * matrix4d2.m23 + matrix4d.m23 * matrix4d2.m33;
			this.m30 = matrix4d.m30 * matrix4d2.m00 + matrix4d.m31 * matrix4d2.m10 + matrix4d.m32 * matrix4d2.m20 + matrix4d.m33 * matrix4d2.m30;
			this.m31 = matrix4d.m30 * matrix4d2.m01 + matrix4d.m31 * matrix4d2.m11 + matrix4d.m32 * matrix4d2.m21 + matrix4d.m33 * matrix4d2.m31;
			this.m32 = matrix4d.m30 * matrix4d2.m02 + matrix4d.m31 * matrix4d2.m12 + matrix4d.m32 * matrix4d2.m22 + matrix4d.m33 * matrix4d2.m32;
			this.m33 = matrix4d.m30 * matrix4d2.m03 + matrix4d.m31 * matrix4d2.m13 + matrix4d.m32 * matrix4d2.m23 + matrix4d.m33 * matrix4d2.m33;
		} else {
			double double1 = matrix4d.m00 * matrix4d2.m00 + matrix4d.m01 * matrix4d2.m10 + matrix4d.m02 * matrix4d2.m20 + matrix4d.m03 * matrix4d2.m30;
			double double2 = matrix4d.m00 * matrix4d2.m01 + matrix4d.m01 * matrix4d2.m11 + matrix4d.m02 * matrix4d2.m21 + matrix4d.m03 * matrix4d2.m31;
			double double3 = matrix4d.m00 * matrix4d2.m02 + matrix4d.m01 * matrix4d2.m12 + matrix4d.m02 * matrix4d2.m22 + matrix4d.m03 * matrix4d2.m32;
			double double4 = matrix4d.m00 * matrix4d2.m03 + matrix4d.m01 * matrix4d2.m13 + matrix4d.m02 * matrix4d2.m23 + matrix4d.m03 * matrix4d2.m33;
			double double5 = matrix4d.m10 * matrix4d2.m00 + matrix4d.m11 * matrix4d2.m10 + matrix4d.m12 * matrix4d2.m20 + matrix4d.m13 * matrix4d2.m30;
			double double6 = matrix4d.m10 * matrix4d2.m01 + matrix4d.m11 * matrix4d2.m11 + matrix4d.m12 * matrix4d2.m21 + matrix4d.m13 * matrix4d2.m31;
			double double7 = matrix4d.m10 * matrix4d2.m02 + matrix4d.m11 * matrix4d2.m12 + matrix4d.m12 * matrix4d2.m22 + matrix4d.m13 * matrix4d2.m32;
			double double8 = matrix4d.m10 * matrix4d2.m03 + matrix4d.m11 * matrix4d2.m13 + matrix4d.m12 * matrix4d2.m23 + matrix4d.m13 * matrix4d2.m33;
			double double9 = matrix4d.m20 * matrix4d2.m00 + matrix4d.m21 * matrix4d2.m10 + matrix4d.m22 * matrix4d2.m20 + matrix4d.m23 * matrix4d2.m30;
			double double10 = matrix4d.m20 * matrix4d2.m01 + matrix4d.m21 * matrix4d2.m11 + matrix4d.m22 * matrix4d2.m21 + matrix4d.m23 * matrix4d2.m31;
			double double11 = matrix4d.m20 * matrix4d2.m02 + matrix4d.m21 * matrix4d2.m12 + matrix4d.m22 * matrix4d2.m22 + matrix4d.m23 * matrix4d2.m32;
			double double12 = matrix4d.m20 * matrix4d2.m03 + matrix4d.m21 * matrix4d2.m13 + matrix4d.m22 * matrix4d2.m23 + matrix4d.m23 * matrix4d2.m33;
			double double13 = matrix4d.m30 * matrix4d2.m00 + matrix4d.m31 * matrix4d2.m10 + matrix4d.m32 * matrix4d2.m20 + matrix4d.m33 * matrix4d2.m30;
			double double14 = matrix4d.m30 * matrix4d2.m01 + matrix4d.m31 * matrix4d2.m11 + matrix4d.m32 * matrix4d2.m21 + matrix4d.m33 * matrix4d2.m31;
			double double15 = matrix4d.m30 * matrix4d2.m02 + matrix4d.m31 * matrix4d2.m12 + matrix4d.m32 * matrix4d2.m22 + matrix4d.m33 * matrix4d2.m32;
			double double16 = matrix4d.m30 * matrix4d2.m03 + matrix4d.m31 * matrix4d2.m13 + matrix4d.m32 * matrix4d2.m23 + matrix4d.m33 * matrix4d2.m33;
			this.m00 = double1;
			this.m01 = double2;
			this.m02 = double3;
			this.m03 = double4;
			this.m10 = double5;
			this.m11 = double6;
			this.m12 = double7;
			this.m13 = double8;
			this.m20 = double9;
			this.m21 = double10;
			this.m22 = double11;
			this.m23 = double12;
			this.m30 = double13;
			this.m31 = double14;
			this.m32 = double15;
			this.m33 = double16;
		}
	}

	public final void mulTransposeBoth(Matrix4d matrix4d, Matrix4d matrix4d2) {
		if (this != matrix4d && this != matrix4d2) {
			this.m00 = matrix4d.m00 * matrix4d2.m00 + matrix4d.m10 * matrix4d2.m01 + matrix4d.m20 * matrix4d2.m02 + matrix4d.m30 * matrix4d2.m03;
			this.m01 = matrix4d.m00 * matrix4d2.m10 + matrix4d.m10 * matrix4d2.m11 + matrix4d.m20 * matrix4d2.m12 + matrix4d.m30 * matrix4d2.m13;
			this.m02 = matrix4d.m00 * matrix4d2.m20 + matrix4d.m10 * matrix4d2.m21 + matrix4d.m20 * matrix4d2.m22 + matrix4d.m30 * matrix4d2.m23;
			this.m03 = matrix4d.m00 * matrix4d2.m30 + matrix4d.m10 * matrix4d2.m31 + matrix4d.m20 * matrix4d2.m32 + matrix4d.m30 * matrix4d2.m33;
			this.m10 = matrix4d.m01 * matrix4d2.m00 + matrix4d.m11 * matrix4d2.m01 + matrix4d.m21 * matrix4d2.m02 + matrix4d.m31 * matrix4d2.m03;
			this.m11 = matrix4d.m01 * matrix4d2.m10 + matrix4d.m11 * matrix4d2.m11 + matrix4d.m21 * matrix4d2.m12 + matrix4d.m31 * matrix4d2.m13;
			this.m12 = matrix4d.m01 * matrix4d2.m20 + matrix4d.m11 * matrix4d2.m21 + matrix4d.m21 * matrix4d2.m22 + matrix4d.m31 * matrix4d2.m23;
			this.m13 = matrix4d.m01 * matrix4d2.m30 + matrix4d.m11 * matrix4d2.m31 + matrix4d.m21 * matrix4d2.m32 + matrix4d.m31 * matrix4d2.m33;
			this.m20 = matrix4d.m02 * matrix4d2.m00 + matrix4d.m12 * matrix4d2.m01 + matrix4d.m22 * matrix4d2.m02 + matrix4d.m32 * matrix4d2.m03;
			this.m21 = matrix4d.m02 * matrix4d2.m10 + matrix4d.m12 * matrix4d2.m11 + matrix4d.m22 * matrix4d2.m12 + matrix4d.m32 * matrix4d2.m13;
			this.m22 = matrix4d.m02 * matrix4d2.m20 + matrix4d.m12 * matrix4d2.m21 + matrix4d.m22 * matrix4d2.m22 + matrix4d.m32 * matrix4d2.m23;
			this.m23 = matrix4d.m02 * matrix4d2.m30 + matrix4d.m12 * matrix4d2.m31 + matrix4d.m22 * matrix4d2.m32 + matrix4d.m32 * matrix4d2.m33;
			this.m30 = matrix4d.m03 * matrix4d2.m00 + matrix4d.m13 * matrix4d2.m01 + matrix4d.m23 * matrix4d2.m02 + matrix4d.m33 * matrix4d2.m03;
			this.m31 = matrix4d.m03 * matrix4d2.m10 + matrix4d.m13 * matrix4d2.m11 + matrix4d.m23 * matrix4d2.m12 + matrix4d.m33 * matrix4d2.m13;
			this.m32 = matrix4d.m03 * matrix4d2.m20 + matrix4d.m13 * matrix4d2.m21 + matrix4d.m23 * matrix4d2.m22 + matrix4d.m33 * matrix4d2.m23;
			this.m33 = matrix4d.m03 * matrix4d2.m30 + matrix4d.m13 * matrix4d2.m31 + matrix4d.m23 * matrix4d2.m32 + matrix4d.m33 * matrix4d2.m33;
		} else {
			double double1 = matrix4d.m00 * matrix4d2.m00 + matrix4d.m10 * matrix4d2.m01 + matrix4d.m20 * matrix4d2.m02 + matrix4d.m30 * matrix4d2.m03;
			double double2 = matrix4d.m00 * matrix4d2.m10 + matrix4d.m10 * matrix4d2.m11 + matrix4d.m20 * matrix4d2.m12 + matrix4d.m30 * matrix4d2.m13;
			double double3 = matrix4d.m00 * matrix4d2.m20 + matrix4d.m10 * matrix4d2.m21 + matrix4d.m20 * matrix4d2.m22 + matrix4d.m30 * matrix4d2.m23;
			double double4 = matrix4d.m00 * matrix4d2.m30 + matrix4d.m10 * matrix4d2.m31 + matrix4d.m20 * matrix4d2.m32 + matrix4d.m30 * matrix4d2.m33;
			double double5 = matrix4d.m01 * matrix4d2.m00 + matrix4d.m11 * matrix4d2.m01 + matrix4d.m21 * matrix4d2.m02 + matrix4d.m31 * matrix4d2.m03;
			double double6 = matrix4d.m01 * matrix4d2.m10 + matrix4d.m11 * matrix4d2.m11 + matrix4d.m21 * matrix4d2.m12 + matrix4d.m31 * matrix4d2.m13;
			double double7 = matrix4d.m01 * matrix4d2.m20 + matrix4d.m11 * matrix4d2.m21 + matrix4d.m21 * matrix4d2.m22 + matrix4d.m31 * matrix4d2.m23;
			double double8 = matrix4d.m01 * matrix4d2.m30 + matrix4d.m11 * matrix4d2.m31 + matrix4d.m21 * matrix4d2.m32 + matrix4d.m31 * matrix4d2.m33;
			double double9 = matrix4d.m02 * matrix4d2.m00 + matrix4d.m12 * matrix4d2.m01 + matrix4d.m22 * matrix4d2.m02 + matrix4d.m32 * matrix4d2.m03;
			double double10 = matrix4d.m02 * matrix4d2.m10 + matrix4d.m12 * matrix4d2.m11 + matrix4d.m22 * matrix4d2.m12 + matrix4d.m32 * matrix4d2.m13;
			double double11 = matrix4d.m02 * matrix4d2.m20 + matrix4d.m12 * matrix4d2.m21 + matrix4d.m22 * matrix4d2.m22 + matrix4d.m32 * matrix4d2.m23;
			double double12 = matrix4d.m02 * matrix4d2.m30 + matrix4d.m12 * matrix4d2.m31 + matrix4d.m22 * matrix4d2.m32 + matrix4d.m32 * matrix4d2.m33;
			double double13 = matrix4d.m03 * matrix4d2.m00 + matrix4d.m13 * matrix4d2.m01 + matrix4d.m23 * matrix4d2.m02 + matrix4d.m33 * matrix4d2.m03;
			double double14 = matrix4d.m03 * matrix4d2.m10 + matrix4d.m13 * matrix4d2.m11 + matrix4d.m23 * matrix4d2.m12 + matrix4d.m33 * matrix4d2.m13;
			double double15 = matrix4d.m03 * matrix4d2.m20 + matrix4d.m13 * matrix4d2.m21 + matrix4d.m23 * matrix4d2.m22 + matrix4d.m33 * matrix4d2.m23;
			double double16 = matrix4d.m03 * matrix4d2.m30 + matrix4d.m13 * matrix4d2.m31 + matrix4d.m23 * matrix4d2.m32 + matrix4d.m33 * matrix4d2.m33;
			this.m00 = double1;
			this.m01 = double2;
			this.m02 = double3;
			this.m03 = double4;
			this.m10 = double5;
			this.m11 = double6;
			this.m12 = double7;
			this.m13 = double8;
			this.m20 = double9;
			this.m21 = double10;
			this.m22 = double11;
			this.m23 = double12;
			this.m30 = double13;
			this.m31 = double14;
			this.m32 = double15;
			this.m33 = double16;
		}
	}

	public final void mulTransposeRight(Matrix4d matrix4d, Matrix4d matrix4d2) {
		if (this != matrix4d && this != matrix4d2) {
			this.m00 = matrix4d.m00 * matrix4d2.m00 + matrix4d.m01 * matrix4d2.m01 + matrix4d.m02 * matrix4d2.m02 + matrix4d.m03 * matrix4d2.m03;
			this.m01 = matrix4d.m00 * matrix4d2.m10 + matrix4d.m01 * matrix4d2.m11 + matrix4d.m02 * matrix4d2.m12 + matrix4d.m03 * matrix4d2.m13;
			this.m02 = matrix4d.m00 * matrix4d2.m20 + matrix4d.m01 * matrix4d2.m21 + matrix4d.m02 * matrix4d2.m22 + matrix4d.m03 * matrix4d2.m23;
			this.m03 = matrix4d.m00 * matrix4d2.m30 + matrix4d.m01 * matrix4d2.m31 + matrix4d.m02 * matrix4d2.m32 + matrix4d.m03 * matrix4d2.m33;
			this.m10 = matrix4d.m10 * matrix4d2.m00 + matrix4d.m11 * matrix4d2.m01 + matrix4d.m12 * matrix4d2.m02 + matrix4d.m13 * matrix4d2.m03;
			this.m11 = matrix4d.m10 * matrix4d2.m10 + matrix4d.m11 * matrix4d2.m11 + matrix4d.m12 * matrix4d2.m12 + matrix4d.m13 * matrix4d2.m13;
			this.m12 = matrix4d.m10 * matrix4d2.m20 + matrix4d.m11 * matrix4d2.m21 + matrix4d.m12 * matrix4d2.m22 + matrix4d.m13 * matrix4d2.m23;
			this.m13 = matrix4d.m10 * matrix4d2.m30 + matrix4d.m11 * matrix4d2.m31 + matrix4d.m12 * matrix4d2.m32 + matrix4d.m13 * matrix4d2.m33;
			this.m20 = matrix4d.m20 * matrix4d2.m00 + matrix4d.m21 * matrix4d2.m01 + matrix4d.m22 * matrix4d2.m02 + matrix4d.m23 * matrix4d2.m03;
			this.m21 = matrix4d.m20 * matrix4d2.m10 + matrix4d.m21 * matrix4d2.m11 + matrix4d.m22 * matrix4d2.m12 + matrix4d.m23 * matrix4d2.m13;
			this.m22 = matrix4d.m20 * matrix4d2.m20 + matrix4d.m21 * matrix4d2.m21 + matrix4d.m22 * matrix4d2.m22 + matrix4d.m23 * matrix4d2.m23;
			this.m23 = matrix4d.m20 * matrix4d2.m30 + matrix4d.m21 * matrix4d2.m31 + matrix4d.m22 * matrix4d2.m32 + matrix4d.m23 * matrix4d2.m33;
			this.m30 = matrix4d.m30 * matrix4d2.m00 + matrix4d.m31 * matrix4d2.m01 + matrix4d.m32 * matrix4d2.m02 + matrix4d.m33 * matrix4d2.m03;
			this.m31 = matrix4d.m30 * matrix4d2.m10 + matrix4d.m31 * matrix4d2.m11 + matrix4d.m32 * matrix4d2.m12 + matrix4d.m33 * matrix4d2.m13;
			this.m32 = matrix4d.m30 * matrix4d2.m20 + matrix4d.m31 * matrix4d2.m21 + matrix4d.m32 * matrix4d2.m22 + matrix4d.m33 * matrix4d2.m23;
			this.m33 = matrix4d.m30 * matrix4d2.m30 + matrix4d.m31 * matrix4d2.m31 + matrix4d.m32 * matrix4d2.m32 + matrix4d.m33 * matrix4d2.m33;
		} else {
			double double1 = matrix4d.m00 * matrix4d2.m00 + matrix4d.m01 * matrix4d2.m01 + matrix4d.m02 * matrix4d2.m02 + matrix4d.m03 * matrix4d2.m03;
			double double2 = matrix4d.m00 * matrix4d2.m10 + matrix4d.m01 * matrix4d2.m11 + matrix4d.m02 * matrix4d2.m12 + matrix4d.m03 * matrix4d2.m13;
			double double3 = matrix4d.m00 * matrix4d2.m20 + matrix4d.m01 * matrix4d2.m21 + matrix4d.m02 * matrix4d2.m22 + matrix4d.m03 * matrix4d2.m23;
			double double4 = matrix4d.m00 * matrix4d2.m30 + matrix4d.m01 * matrix4d2.m31 + matrix4d.m02 * matrix4d2.m32 + matrix4d.m03 * matrix4d2.m33;
			double double5 = matrix4d.m10 * matrix4d2.m00 + matrix4d.m11 * matrix4d2.m01 + matrix4d.m12 * matrix4d2.m02 + matrix4d.m13 * matrix4d2.m03;
			double double6 = matrix4d.m10 * matrix4d2.m10 + matrix4d.m11 * matrix4d2.m11 + matrix4d.m12 * matrix4d2.m12 + matrix4d.m13 * matrix4d2.m13;
			double double7 = matrix4d.m10 * matrix4d2.m20 + matrix4d.m11 * matrix4d2.m21 + matrix4d.m12 * matrix4d2.m22 + matrix4d.m13 * matrix4d2.m23;
			double double8 = matrix4d.m10 * matrix4d2.m30 + matrix4d.m11 * matrix4d2.m31 + matrix4d.m12 * matrix4d2.m32 + matrix4d.m13 * matrix4d2.m33;
			double double9 = matrix4d.m20 * matrix4d2.m00 + matrix4d.m21 * matrix4d2.m01 + matrix4d.m22 * matrix4d2.m02 + matrix4d.m23 * matrix4d2.m03;
			double double10 = matrix4d.m20 * matrix4d2.m10 + matrix4d.m21 * matrix4d2.m11 + matrix4d.m22 * matrix4d2.m12 + matrix4d.m23 * matrix4d2.m13;
			double double11 = matrix4d.m20 * matrix4d2.m20 + matrix4d.m21 * matrix4d2.m21 + matrix4d.m22 * matrix4d2.m22 + matrix4d.m23 * matrix4d2.m23;
			double double12 = matrix4d.m20 * matrix4d2.m30 + matrix4d.m21 * matrix4d2.m31 + matrix4d.m22 * matrix4d2.m32 + matrix4d.m23 * matrix4d2.m33;
			double double13 = matrix4d.m30 * matrix4d2.m00 + matrix4d.m31 * matrix4d2.m01 + matrix4d.m32 * matrix4d2.m02 + matrix4d.m33 * matrix4d2.m03;
			double double14 = matrix4d.m30 * matrix4d2.m10 + matrix4d.m31 * matrix4d2.m11 + matrix4d.m32 * matrix4d2.m12 + matrix4d.m33 * matrix4d2.m13;
			double double15 = matrix4d.m30 * matrix4d2.m20 + matrix4d.m31 * matrix4d2.m21 + matrix4d.m32 * matrix4d2.m22 + matrix4d.m33 * matrix4d2.m23;
			double double16 = matrix4d.m30 * matrix4d2.m30 + matrix4d.m31 * matrix4d2.m31 + matrix4d.m32 * matrix4d2.m32 + matrix4d.m33 * matrix4d2.m33;
			this.m00 = double1;
			this.m01 = double2;
			this.m02 = double3;
			this.m03 = double4;
			this.m10 = double5;
			this.m11 = double6;
			this.m12 = double7;
			this.m13 = double8;
			this.m20 = double9;
			this.m21 = double10;
			this.m22 = double11;
			this.m23 = double12;
			this.m30 = double13;
			this.m31 = double14;
			this.m32 = double15;
			this.m33 = double16;
		}
	}

	public final void mulTransposeLeft(Matrix4d matrix4d, Matrix4d matrix4d2) {
		if (this != matrix4d && this != matrix4d2) {
			this.m00 = matrix4d.m00 * matrix4d2.m00 + matrix4d.m10 * matrix4d2.m10 + matrix4d.m20 * matrix4d2.m20 + matrix4d.m30 * matrix4d2.m30;
			this.m01 = matrix4d.m00 * matrix4d2.m01 + matrix4d.m10 * matrix4d2.m11 + matrix4d.m20 * matrix4d2.m21 + matrix4d.m30 * matrix4d2.m31;
			this.m02 = matrix4d.m00 * matrix4d2.m02 + matrix4d.m10 * matrix4d2.m12 + matrix4d.m20 * matrix4d2.m22 + matrix4d.m30 * matrix4d2.m32;
			this.m03 = matrix4d.m00 * matrix4d2.m03 + matrix4d.m10 * matrix4d2.m13 + matrix4d.m20 * matrix4d2.m23 + matrix4d.m30 * matrix4d2.m33;
			this.m10 = matrix4d.m01 * matrix4d2.m00 + matrix4d.m11 * matrix4d2.m10 + matrix4d.m21 * matrix4d2.m20 + matrix4d.m31 * matrix4d2.m30;
			this.m11 = matrix4d.m01 * matrix4d2.m01 + matrix4d.m11 * matrix4d2.m11 + matrix4d.m21 * matrix4d2.m21 + matrix4d.m31 * matrix4d2.m31;
			this.m12 = matrix4d.m01 * matrix4d2.m02 + matrix4d.m11 * matrix4d2.m12 + matrix4d.m21 * matrix4d2.m22 + matrix4d.m31 * matrix4d2.m32;
			this.m13 = matrix4d.m01 * matrix4d2.m03 + matrix4d.m11 * matrix4d2.m13 + matrix4d.m21 * matrix4d2.m23 + matrix4d.m31 * matrix4d2.m33;
			this.m20 = matrix4d.m02 * matrix4d2.m00 + matrix4d.m12 * matrix4d2.m10 + matrix4d.m22 * matrix4d2.m20 + matrix4d.m32 * matrix4d2.m30;
			this.m21 = matrix4d.m02 * matrix4d2.m01 + matrix4d.m12 * matrix4d2.m11 + matrix4d.m22 * matrix4d2.m21 + matrix4d.m32 * matrix4d2.m31;
			this.m22 = matrix4d.m02 * matrix4d2.m02 + matrix4d.m12 * matrix4d2.m12 + matrix4d.m22 * matrix4d2.m22 + matrix4d.m32 * matrix4d2.m32;
			this.m23 = matrix4d.m02 * matrix4d2.m03 + matrix4d.m12 * matrix4d2.m13 + matrix4d.m22 * matrix4d2.m23 + matrix4d.m32 * matrix4d2.m33;
			this.m30 = matrix4d.m03 * matrix4d2.m00 + matrix4d.m13 * matrix4d2.m10 + matrix4d.m23 * matrix4d2.m20 + matrix4d.m33 * matrix4d2.m30;
			this.m31 = matrix4d.m03 * matrix4d2.m01 + matrix4d.m13 * matrix4d2.m11 + matrix4d.m23 * matrix4d2.m21 + matrix4d.m33 * matrix4d2.m31;
			this.m32 = matrix4d.m03 * matrix4d2.m02 + matrix4d.m13 * matrix4d2.m12 + matrix4d.m23 * matrix4d2.m22 + matrix4d.m33 * matrix4d2.m32;
			this.m33 = matrix4d.m03 * matrix4d2.m03 + matrix4d.m13 * matrix4d2.m13 + matrix4d.m23 * matrix4d2.m23 + matrix4d.m33 * matrix4d2.m33;
		} else {
			double double1 = matrix4d.m00 * matrix4d2.m00 + matrix4d.m10 * matrix4d2.m10 + matrix4d.m20 * matrix4d2.m20 + matrix4d.m30 * matrix4d2.m30;
			double double2 = matrix4d.m00 * matrix4d2.m01 + matrix4d.m10 * matrix4d2.m11 + matrix4d.m20 * matrix4d2.m21 + matrix4d.m30 * matrix4d2.m31;
			double double3 = matrix4d.m00 * matrix4d2.m02 + matrix4d.m10 * matrix4d2.m12 + matrix4d.m20 * matrix4d2.m22 + matrix4d.m30 * matrix4d2.m32;
			double double4 = matrix4d.m00 * matrix4d2.m03 + matrix4d.m10 * matrix4d2.m13 + matrix4d.m20 * matrix4d2.m23 + matrix4d.m30 * matrix4d2.m33;
			double double5 = matrix4d.m01 * matrix4d2.m00 + matrix4d.m11 * matrix4d2.m10 + matrix4d.m21 * matrix4d2.m20 + matrix4d.m31 * matrix4d2.m30;
			double double6 = matrix4d.m01 * matrix4d2.m01 + matrix4d.m11 * matrix4d2.m11 + matrix4d.m21 * matrix4d2.m21 + matrix4d.m31 * matrix4d2.m31;
			double double7 = matrix4d.m01 * matrix4d2.m02 + matrix4d.m11 * matrix4d2.m12 + matrix4d.m21 * matrix4d2.m22 + matrix4d.m31 * matrix4d2.m32;
			double double8 = matrix4d.m01 * matrix4d2.m03 + matrix4d.m11 * matrix4d2.m13 + matrix4d.m21 * matrix4d2.m23 + matrix4d.m31 * matrix4d2.m33;
			double double9 = matrix4d.m02 * matrix4d2.m00 + matrix4d.m12 * matrix4d2.m10 + matrix4d.m22 * matrix4d2.m20 + matrix4d.m32 * matrix4d2.m30;
			double double10 = matrix4d.m02 * matrix4d2.m01 + matrix4d.m12 * matrix4d2.m11 + matrix4d.m22 * matrix4d2.m21 + matrix4d.m32 * matrix4d2.m31;
			double double11 = matrix4d.m02 * matrix4d2.m02 + matrix4d.m12 * matrix4d2.m12 + matrix4d.m22 * matrix4d2.m22 + matrix4d.m32 * matrix4d2.m32;
			double double12 = matrix4d.m02 * matrix4d2.m03 + matrix4d.m12 * matrix4d2.m13 + matrix4d.m22 * matrix4d2.m23 + matrix4d.m32 * matrix4d2.m33;
			double double13 = matrix4d.m03 * matrix4d2.m00 + matrix4d.m13 * matrix4d2.m10 + matrix4d.m23 * matrix4d2.m20 + matrix4d.m33 * matrix4d2.m30;
			double double14 = matrix4d.m03 * matrix4d2.m01 + matrix4d.m13 * matrix4d2.m11 + matrix4d.m23 * matrix4d2.m21 + matrix4d.m33 * matrix4d2.m31;
			double double15 = matrix4d.m03 * matrix4d2.m02 + matrix4d.m13 * matrix4d2.m12 + matrix4d.m23 * matrix4d2.m22 + matrix4d.m33 * matrix4d2.m32;
			double double16 = matrix4d.m03 * matrix4d2.m03 + matrix4d.m13 * matrix4d2.m13 + matrix4d.m23 * matrix4d2.m23 + matrix4d.m33 * matrix4d2.m33;
			this.m00 = double1;
			this.m01 = double2;
			this.m02 = double3;
			this.m03 = double4;
			this.m10 = double5;
			this.m11 = double6;
			this.m12 = double7;
			this.m13 = double8;
			this.m20 = double9;
			this.m21 = double10;
			this.m22 = double11;
			this.m23 = double12;
			this.m30 = double13;
			this.m31 = double14;
			this.m32 = double15;
			this.m33 = double16;
		}
	}

	public boolean equals(Matrix4d matrix4d) {
		try {
			return this.m00 == matrix4d.m00 && this.m01 == matrix4d.m01 && this.m02 == matrix4d.m02 && this.m03 == matrix4d.m03 && this.m10 == matrix4d.m10 && this.m11 == matrix4d.m11 && this.m12 == matrix4d.m12 && this.m13 == matrix4d.m13 && this.m20 == matrix4d.m20 && this.m21 == matrix4d.m21 && this.m22 == matrix4d.m22 && this.m23 == matrix4d.m23 && this.m30 == matrix4d.m30 && this.m31 == matrix4d.m31 && this.m32 == matrix4d.m32 && this.m33 == matrix4d.m33;
		} catch (NullPointerException nullPointerException) {
			return false;
		}
	}

	public boolean equals(Object object) {
		try {
			Matrix4d matrix4d = (Matrix4d)object;
			return this.m00 == matrix4d.m00 && this.m01 == matrix4d.m01 && this.m02 == matrix4d.m02 && this.m03 == matrix4d.m03 && this.m10 == matrix4d.m10 && this.m11 == matrix4d.m11 && this.m12 == matrix4d.m12 && this.m13 == matrix4d.m13 && this.m20 == matrix4d.m20 && this.m21 == matrix4d.m21 && this.m22 == matrix4d.m22 && this.m23 == matrix4d.m23 && this.m30 == matrix4d.m30 && this.m31 == matrix4d.m31 && this.m32 == matrix4d.m32 && this.m33 == matrix4d.m33;
		} catch (ClassCastException classCastException) {
			return false;
		} catch (NullPointerException nullPointerException) {
			return false;
		}
	}

	public boolean epsilonEquals(Matrix4d matrix4d, float float1) {
		return this.epsilonEquals(matrix4d, (double)float1);
	}

	public boolean epsilonEquals(Matrix4d matrix4d, double double1) {
		double double2 = this.m00 - matrix4d.m00;
		if ((double2 < 0.0 ? -double2 : double2) > double1) {
			return false;
		} else {
			double2 = this.m01 - matrix4d.m01;
			if ((double2 < 0.0 ? -double2 : double2) > double1) {
				return false;
			} else {
				double2 = this.m02 - matrix4d.m02;
				if ((double2 < 0.0 ? -double2 : double2) > double1) {
					return false;
				} else {
					double2 = this.m03 - matrix4d.m03;
					if ((double2 < 0.0 ? -double2 : double2) > double1) {
						return false;
					} else {
						double2 = this.m10 - matrix4d.m10;
						if ((double2 < 0.0 ? -double2 : double2) > double1) {
							return false;
						} else {
							double2 = this.m11 - matrix4d.m11;
							if ((double2 < 0.0 ? -double2 : double2) > double1) {
								return false;
							} else {
								double2 = this.m12 - matrix4d.m12;
								if ((double2 < 0.0 ? -double2 : double2) > double1) {
									return false;
								} else {
									double2 = this.m13 - matrix4d.m13;
									if ((double2 < 0.0 ? -double2 : double2) > double1) {
										return false;
									} else {
										double2 = this.m20 - matrix4d.m20;
										if ((double2 < 0.0 ? -double2 : double2) > double1) {
											return false;
										} else {
											double2 = this.m21 - matrix4d.m21;
											if ((double2 < 0.0 ? -double2 : double2) > double1) {
												return false;
											} else {
												double2 = this.m22 - matrix4d.m22;
												if ((double2 < 0.0 ? -double2 : double2) > double1) {
													return false;
												} else {
													double2 = this.m23 - matrix4d.m23;
													if ((double2 < 0.0 ? -double2 : double2) > double1) {
														return false;
													} else {
														double2 = this.m30 - matrix4d.m30;
														if ((double2 < 0.0 ? -double2 : double2) > double1) {
															return false;
														} else {
															double2 = this.m31 - matrix4d.m31;
															if ((double2 < 0.0 ? -double2 : double2) > double1) {
																return false;
															} else {
																double2 = this.m32 - matrix4d.m32;
																if ((double2 < 0.0 ? -double2 : double2) > double1) {
																	return false;
																} else {
																	double2 = this.m33 - matrix4d.m33;
																	return !((double2 < 0.0 ? -double2 : double2) > double1);
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public int hashCode() {
		long long1 = 1L;
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.m00);
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.m01);
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.m02);
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.m03);
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.m10);
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.m11);
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.m12);
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.m13);
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.m20);
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.m21);
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.m22);
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.m23);
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.m30);
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.m31);
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.m32);
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.m33);
		return (int)(long1 ^ long1 >> 32);
	}

	public final void transform(Tuple4d tuple4d, Tuple4d tuple4d2) {
		double double1 = this.m00 * tuple4d.x + this.m01 * tuple4d.y + this.m02 * tuple4d.z + this.m03 * tuple4d.w;
		double double2 = this.m10 * tuple4d.x + this.m11 * tuple4d.y + this.m12 * tuple4d.z + this.m13 * tuple4d.w;
		double double3 = this.m20 * tuple4d.x + this.m21 * tuple4d.y + this.m22 * tuple4d.z + this.m23 * tuple4d.w;
		tuple4d2.w = this.m30 * tuple4d.x + this.m31 * tuple4d.y + this.m32 * tuple4d.z + this.m33 * tuple4d.w;
		tuple4d2.x = double1;
		tuple4d2.y = double2;
		tuple4d2.z = double3;
	}

	public final void transform(Tuple4d tuple4d) {
		double double1 = this.m00 * tuple4d.x + this.m01 * tuple4d.y + this.m02 * tuple4d.z + this.m03 * tuple4d.w;
		double double2 = this.m10 * tuple4d.x + this.m11 * tuple4d.y + this.m12 * tuple4d.z + this.m13 * tuple4d.w;
		double double3 = this.m20 * tuple4d.x + this.m21 * tuple4d.y + this.m22 * tuple4d.z + this.m23 * tuple4d.w;
		tuple4d.w = this.m30 * tuple4d.x + this.m31 * tuple4d.y + this.m32 * tuple4d.z + this.m33 * tuple4d.w;
		tuple4d.x = double1;
		tuple4d.y = double2;
		tuple4d.z = double3;
	}

	public final void transform(Tuple4f tuple4f, Tuple4f tuple4f2) {
		float float1 = (float)(this.m00 * (double)tuple4f.x + this.m01 * (double)tuple4f.y + this.m02 * (double)tuple4f.z + this.m03 * (double)tuple4f.w);
		float float2 = (float)(this.m10 * (double)tuple4f.x + this.m11 * (double)tuple4f.y + this.m12 * (double)tuple4f.z + this.m13 * (double)tuple4f.w);
		float float3 = (float)(this.m20 * (double)tuple4f.x + this.m21 * (double)tuple4f.y + this.m22 * (double)tuple4f.z + this.m23 * (double)tuple4f.w);
		tuple4f2.w = (float)(this.m30 * (double)tuple4f.x + this.m31 * (double)tuple4f.y + this.m32 * (double)tuple4f.z + this.m33 * (double)tuple4f.w);
		tuple4f2.x = float1;
		tuple4f2.y = float2;
		tuple4f2.z = float3;
	}

	public final void transform(Tuple4f tuple4f) {
		float float1 = (float)(this.m00 * (double)tuple4f.x + this.m01 * (double)tuple4f.y + this.m02 * (double)tuple4f.z + this.m03 * (double)tuple4f.w);
		float float2 = (float)(this.m10 * (double)tuple4f.x + this.m11 * (double)tuple4f.y + this.m12 * (double)tuple4f.z + this.m13 * (double)tuple4f.w);
		float float3 = (float)(this.m20 * (double)tuple4f.x + this.m21 * (double)tuple4f.y + this.m22 * (double)tuple4f.z + this.m23 * (double)tuple4f.w);
		tuple4f.w = (float)(this.m30 * (double)tuple4f.x + this.m31 * (double)tuple4f.y + this.m32 * (double)tuple4f.z + this.m33 * (double)tuple4f.w);
		tuple4f.x = float1;
		tuple4f.y = float2;
		tuple4f.z = float3;
	}

	public final void transform(Point3d point3d, Point3d point3d2) {
		double double1 = this.m00 * point3d.x + this.m01 * point3d.y + this.m02 * point3d.z + this.m03;
		double double2 = this.m10 * point3d.x + this.m11 * point3d.y + this.m12 * point3d.z + this.m13;
		point3d2.z = this.m20 * point3d.x + this.m21 * point3d.y + this.m22 * point3d.z + this.m23;
		point3d2.x = double1;
		point3d2.y = double2;
	}

	public final void transform(Point3d point3d) {
		double double1 = this.m00 * point3d.x + this.m01 * point3d.y + this.m02 * point3d.z + this.m03;
		double double2 = this.m10 * point3d.x + this.m11 * point3d.y + this.m12 * point3d.z + this.m13;
		point3d.z = this.m20 * point3d.x + this.m21 * point3d.y + this.m22 * point3d.z + this.m23;
		point3d.x = double1;
		point3d.y = double2;
	}

	public final void transform(Point3f point3f, Point3f point3f2) {
		float float1 = (float)(this.m00 * (double)point3f.x + this.m01 * (double)point3f.y + this.m02 * (double)point3f.z + this.m03);
		float float2 = (float)(this.m10 * (double)point3f.x + this.m11 * (double)point3f.y + this.m12 * (double)point3f.z + this.m13);
		point3f2.z = (float)(this.m20 * (double)point3f.x + this.m21 * (double)point3f.y + this.m22 * (double)point3f.z + this.m23);
		point3f2.x = float1;
		point3f2.y = float2;
	}

	public final void transform(Point3f point3f) {
		float float1 = (float)(this.m00 * (double)point3f.x + this.m01 * (double)point3f.y + this.m02 * (double)point3f.z + this.m03);
		float float2 = (float)(this.m10 * (double)point3f.x + this.m11 * (double)point3f.y + this.m12 * (double)point3f.z + this.m13);
		point3f.z = (float)(this.m20 * (double)point3f.x + this.m21 * (double)point3f.y + this.m22 * (double)point3f.z + this.m23);
		point3f.x = float1;
		point3f.y = float2;
	}

	public final void transform(Vector3d vector3d, Vector3d vector3d2) {
		double double1 = this.m00 * vector3d.x + this.m01 * vector3d.y + this.m02 * vector3d.z;
		double double2 = this.m10 * vector3d.x + this.m11 * vector3d.y + this.m12 * vector3d.z;
		vector3d2.z = this.m20 * vector3d.x + this.m21 * vector3d.y + this.m22 * vector3d.z;
		vector3d2.x = double1;
		vector3d2.y = double2;
	}

	public final void transform(Vector3d vector3d) {
		double double1 = this.m00 * vector3d.x + this.m01 * vector3d.y + this.m02 * vector3d.z;
		double double2 = this.m10 * vector3d.x + this.m11 * vector3d.y + this.m12 * vector3d.z;
		vector3d.z = this.m20 * vector3d.x + this.m21 * vector3d.y + this.m22 * vector3d.z;
		vector3d.x = double1;
		vector3d.y = double2;
	}

	public final void transform(Vector3f vector3f, Vector3f vector3f2) {
		float float1 = (float)(this.m00 * (double)vector3f.x + this.m01 * (double)vector3f.y + this.m02 * (double)vector3f.z);
		float float2 = (float)(this.m10 * (double)vector3f.x + this.m11 * (double)vector3f.y + this.m12 * (double)vector3f.z);
		vector3f2.z = (float)(this.m20 * (double)vector3f.x + this.m21 * (double)vector3f.y + this.m22 * (double)vector3f.z);
		vector3f2.x = float1;
		vector3f2.y = float2;
	}

	public final void transform(Vector3f vector3f) {
		float float1 = (float)(this.m00 * (double)vector3f.x + this.m01 * (double)vector3f.y + this.m02 * (double)vector3f.z);
		float float2 = (float)(this.m10 * (double)vector3f.x + this.m11 * (double)vector3f.y + this.m12 * (double)vector3f.z);
		vector3f.z = (float)(this.m20 * (double)vector3f.x + this.m21 * (double)vector3f.y + this.m22 * (double)vector3f.z);
		vector3f.x = float1;
		vector3f.y = float2;
	}

	public final void setRotation(Matrix3d matrix3d) {
		double[] doubleArray = new double[9];
		double[] doubleArray2 = new double[3];
		this.getScaleRotate(doubleArray2, doubleArray);
		this.m00 = matrix3d.m00 * doubleArray2[0];
		this.m01 = matrix3d.m01 * doubleArray2[1];
		this.m02 = matrix3d.m02 * doubleArray2[2];
		this.m10 = matrix3d.m10 * doubleArray2[0];
		this.m11 = matrix3d.m11 * doubleArray2[1];
		this.m12 = matrix3d.m12 * doubleArray2[2];
		this.m20 = matrix3d.m20 * doubleArray2[0];
		this.m21 = matrix3d.m21 * doubleArray2[1];
		this.m22 = matrix3d.m22 * doubleArray2[2];
	}

	public final void setRotation(Matrix3f matrix3f) {
		double[] doubleArray = new double[9];
		double[] doubleArray2 = new double[3];
		this.getScaleRotate(doubleArray2, doubleArray);
		this.m00 = (double)matrix3f.m00 * doubleArray2[0];
		this.m01 = (double)matrix3f.m01 * doubleArray2[1];
		this.m02 = (double)matrix3f.m02 * doubleArray2[2];
		this.m10 = (double)matrix3f.m10 * doubleArray2[0];
		this.m11 = (double)matrix3f.m11 * doubleArray2[1];
		this.m12 = (double)matrix3f.m12 * doubleArray2[2];
		this.m20 = (double)matrix3f.m20 * doubleArray2[0];
		this.m21 = (double)matrix3f.m21 * doubleArray2[1];
		this.m22 = (double)matrix3f.m22 * doubleArray2[2];
	}

	public final void setRotation(Quat4f quat4f) {
		double[] doubleArray = new double[9];
		double[] doubleArray2 = new double[3];
		this.getScaleRotate(doubleArray2, doubleArray);
		this.m00 = (1.0 - (double)(2.0F * quat4f.y * quat4f.y) - (double)(2.0F * quat4f.z * quat4f.z)) * doubleArray2[0];
		this.m10 = 2.0 * (double)(quat4f.x * quat4f.y + quat4f.w * quat4f.z) * doubleArray2[0];
		this.m20 = 2.0 * (double)(quat4f.x * quat4f.z - quat4f.w * quat4f.y) * doubleArray2[0];
		this.m01 = 2.0 * (double)(quat4f.x * quat4f.y - quat4f.w * quat4f.z) * doubleArray2[1];
		this.m11 = (1.0 - (double)(2.0F * quat4f.x * quat4f.x) - (double)(2.0F * quat4f.z * quat4f.z)) * doubleArray2[1];
		this.m21 = 2.0 * (double)(quat4f.y * quat4f.z + quat4f.w * quat4f.x) * doubleArray2[1];
		this.m02 = 2.0 * (double)(quat4f.x * quat4f.z + quat4f.w * quat4f.y) * doubleArray2[2];
		this.m12 = 2.0 * (double)(quat4f.y * quat4f.z - quat4f.w * quat4f.x) * doubleArray2[2];
		this.m22 = (1.0 - (double)(2.0F * quat4f.x * quat4f.x) - (double)(2.0F * quat4f.y * quat4f.y)) * doubleArray2[2];
	}

	public final void setRotation(Quat4d quat4d) {
		double[] doubleArray = new double[9];
		double[] doubleArray2 = new double[3];
		this.getScaleRotate(doubleArray2, doubleArray);
		this.m00 = (1.0 - 2.0 * quat4d.y * quat4d.y - 2.0 * quat4d.z * quat4d.z) * doubleArray2[0];
		this.m10 = 2.0 * (quat4d.x * quat4d.y + quat4d.w * quat4d.z) * doubleArray2[0];
		this.m20 = 2.0 * (quat4d.x * quat4d.z - quat4d.w * quat4d.y) * doubleArray2[0];
		this.m01 = 2.0 * (quat4d.x * quat4d.y - quat4d.w * quat4d.z) * doubleArray2[1];
		this.m11 = (1.0 - 2.0 * quat4d.x * quat4d.x - 2.0 * quat4d.z * quat4d.z) * doubleArray2[1];
		this.m21 = 2.0 * (quat4d.y * quat4d.z + quat4d.w * quat4d.x) * doubleArray2[1];
		this.m02 = 2.0 * (quat4d.x * quat4d.z + quat4d.w * quat4d.y) * doubleArray2[2];
		this.m12 = 2.0 * (quat4d.y * quat4d.z - quat4d.w * quat4d.x) * doubleArray2[2];
		this.m22 = (1.0 - 2.0 * quat4d.x * quat4d.x - 2.0 * quat4d.y * quat4d.y) * doubleArray2[2];
	}

	public final void setRotation(AxisAngle4d axisAngle4d) {
		double[] doubleArray = new double[9];
		double[] doubleArray2 = new double[3];
		this.getScaleRotate(doubleArray2, doubleArray);
		double double1 = 1.0 / Math.sqrt(axisAngle4d.x * axisAngle4d.x + axisAngle4d.y * axisAngle4d.y + axisAngle4d.z * axisAngle4d.z);
		double double2 = axisAngle4d.x * double1;
		double double3 = axisAngle4d.y * double1;
		double double4 = axisAngle4d.z * double1;
		double double5 = Math.sin(axisAngle4d.angle);
		double double6 = Math.cos(axisAngle4d.angle);
		double double7 = 1.0 - double6;
		double double8 = axisAngle4d.x * axisAngle4d.z;
		double double9 = axisAngle4d.x * axisAngle4d.y;
		double double10 = axisAngle4d.y * axisAngle4d.z;
		this.m00 = (double7 * double2 * double2 + double6) * doubleArray2[0];
		this.m01 = (double7 * double9 - double5 * double4) * doubleArray2[1];
		this.m02 = (double7 * double8 + double5 * double3) * doubleArray2[2];
		this.m10 = (double7 * double9 + double5 * double4) * doubleArray2[0];
		this.m11 = (double7 * double3 * double3 + double6) * doubleArray2[1];
		this.m12 = (double7 * double10 - double5 * double2) * doubleArray2[2];
		this.m20 = (double7 * double8 - double5 * double3) * doubleArray2[0];
		this.m21 = (double7 * double10 + double5 * double2) * doubleArray2[1];
		this.m22 = (double7 * double4 * double4 + double6) * doubleArray2[2];
	}

	public final void setZero() {
		this.m00 = 0.0;
		this.m01 = 0.0;
		this.m02 = 0.0;
		this.m03 = 0.0;
		this.m10 = 0.0;
		this.m11 = 0.0;
		this.m12 = 0.0;
		this.m13 = 0.0;
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = 0.0;
		this.m23 = 0.0;
		this.m30 = 0.0;
		this.m31 = 0.0;
		this.m32 = 0.0;
		this.m33 = 0.0;
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

	public final void negate(Matrix4d matrix4d) {
		this.m00 = -matrix4d.m00;
		this.m01 = -matrix4d.m01;
		this.m02 = -matrix4d.m02;
		this.m03 = -matrix4d.m03;
		this.m10 = -matrix4d.m10;
		this.m11 = -matrix4d.m11;
		this.m12 = -matrix4d.m12;
		this.m13 = -matrix4d.m13;
		this.m20 = -matrix4d.m20;
		this.m21 = -matrix4d.m21;
		this.m22 = -matrix4d.m22;
		this.m23 = -matrix4d.m23;
		this.m30 = -matrix4d.m30;
		this.m31 = -matrix4d.m31;
		this.m32 = -matrix4d.m32;
		this.m33 = -matrix4d.m33;
	}

	private final void getScaleRotate(double[] doubleArray, double[] doubleArray2) {
		double[] doubleArray3 = new double[]{this.m00, this.m01, this.m02, this.m10, this.m11, this.m12, this.m20, this.m21, this.m22};
		Matrix3d.compute_svd(doubleArray3, doubleArray, doubleArray2);
	}

	public Object clone() {
		Matrix4d matrix4d = null;
		try {
			matrix4d = (Matrix4d)super.clone();
			return matrix4d;
		} catch (CloneNotSupportedException cloneNotSupportedException) {
			throw new InternalError();
		}
	}

	public final double getM00() {
		return this.m00;
	}

	public final void setM00(double double1) {
		this.m00 = double1;
	}

	public final double getM01() {
		return this.m01;
	}

	public final void setM01(double double1) {
		this.m01 = double1;
	}

	public final double getM02() {
		return this.m02;
	}

	public final void setM02(double double1) {
		this.m02 = double1;
	}

	public final double getM10() {
		return this.m10;
	}

	public final void setM10(double double1) {
		this.m10 = double1;
	}

	public final double getM11() {
		return this.m11;
	}

	public final void setM11(double double1) {
		this.m11 = double1;
	}

	public final double getM12() {
		return this.m12;
	}

	public final void setM12(double double1) {
		this.m12 = double1;
	}

	public final double getM20() {
		return this.m20;
	}

	public final void setM20(double double1) {
		this.m20 = double1;
	}

	public final double getM21() {
		return this.m21;
	}

	public final void setM21(double double1) {
		this.m21 = double1;
	}

	public final double getM22() {
		return this.m22;
	}

	public final void setM22(double double1) {
		this.m22 = double1;
	}

	public final double getM03() {
		return this.m03;
	}

	public final void setM03(double double1) {
		this.m03 = double1;
	}

	public final double getM13() {
		return this.m13;
	}

	public final void setM13(double double1) {
		this.m13 = double1;
	}

	public final double getM23() {
		return this.m23;
	}

	public final void setM23(double double1) {
		this.m23 = double1;
	}

	public final double getM30() {
		return this.m30;
	}

	public final void setM30(double double1) {
		this.m30 = double1;
	}

	public final double getM31() {
		return this.m31;
	}

	public final void setM31(double double1) {
		this.m31 = double1;
	}

	public final double getM32() {
		return this.m32;
	}

	public final void setM32(double double1) {
		this.m32 = double1;
	}

	public final double getM33() {
		return this.m33;
	}

	public final void setM33(double double1) {
		this.m33 = double1;
	}
}
