package javax.vecmath;

import java.io.Serializable;


public class Matrix3d implements Serializable,Cloneable {
	static final long serialVersionUID = 6837536777072402710L;
	public double m00;
	public double m01;
	public double m02;
	public double m10;
	public double m11;
	public double m12;
	public double m20;
	public double m21;
	public double m22;
	private static final double EPS = 1.110223024E-16;
	private static final double ERR_EPS = 1.0E-8;
	private static double xin;
	private static double yin;
	private static double zin;
	private static double xout;
	private static double yout;
	private static double zout;

	public Matrix3d(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9) {
		this.m00 = double1;
		this.m01 = double2;
		this.m02 = double3;
		this.m10 = double4;
		this.m11 = double5;
		this.m12 = double6;
		this.m20 = double7;
		this.m21 = double8;
		this.m22 = double9;
	}

	public Matrix3d(double[] doubleArray) {
		this.m00 = doubleArray[0];
		this.m01 = doubleArray[1];
		this.m02 = doubleArray[2];
		this.m10 = doubleArray[3];
		this.m11 = doubleArray[4];
		this.m12 = doubleArray[5];
		this.m20 = doubleArray[6];
		this.m21 = doubleArray[7];
		this.m22 = doubleArray[8];
	}

	public Matrix3d(Matrix3d matrix3d) {
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

	public Matrix3d(Matrix3f matrix3f) {
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

	public Matrix3d() {
		this.m00 = 0.0;
		this.m01 = 0.0;
		this.m02 = 0.0;
		this.m10 = 0.0;
		this.m11 = 0.0;
		this.m12 = 0.0;
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = 0.0;
	}

	public String toString() {
		return this.m00 + ", " + this.m01 + ", " + this.m02 + "\n" + this.m10 + ", " + this.m11 + ", " + this.m12 + "\n" + this.m20 + ", " + this.m21 + ", " + this.m22 + "\n";
	}

	public final void setIdentity() {
		this.m00 = 1.0;
		this.m01 = 0.0;
		this.m02 = 0.0;
		this.m10 = 0.0;
		this.m11 = 1.0;
		this.m12 = 0.0;
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = 1.0;
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
			
			default: 
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d0"));
			
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
			
			default: 
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d0"));
			
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
			
			default: 
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d0"));
			
			}

		
		default: 
			throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d0"));
		
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
			
			default: 
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d1"));
			
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
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d1"));
			
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
		throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d1"));
	}

	public final void getRow(int int1, Vector3d vector3d) {
		if (int1 == 0) {
			vector3d.x = this.m00;
			vector3d.y = this.m01;
			vector3d.z = this.m02;
		} else if (int1 == 1) {
			vector3d.x = this.m10;
			vector3d.y = this.m11;
			vector3d.z = this.m12;
		} else {
			if (int1 != 2) {
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d2"));
			}

			vector3d.x = this.m20;
			vector3d.y = this.m21;
			vector3d.z = this.m22;
		}
	}

	public final void getRow(int int1, double[] doubleArray) {
		if (int1 == 0) {
			doubleArray[0] = this.m00;
			doubleArray[1] = this.m01;
			doubleArray[2] = this.m02;
		} else if (int1 == 1) {
			doubleArray[0] = this.m10;
			doubleArray[1] = this.m11;
			doubleArray[2] = this.m12;
		} else {
			if (int1 != 2) {
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d2"));
			}

			doubleArray[0] = this.m20;
			doubleArray[1] = this.m21;
			doubleArray[2] = this.m22;
		}
	}

	public final void getColumn(int int1, Vector3d vector3d) {
		if (int1 == 0) {
			vector3d.x = this.m00;
			vector3d.y = this.m10;
			vector3d.z = this.m20;
		} else if (int1 == 1) {
			vector3d.x = this.m01;
			vector3d.y = this.m11;
			vector3d.z = this.m21;
		} else {
			if (int1 != 2) {
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d4"));
			}

			vector3d.x = this.m02;
			vector3d.y = this.m12;
			vector3d.z = this.m22;
		}
	}

	public final void getColumn(int int1, double[] doubleArray) {
		if (int1 == 0) {
			doubleArray[0] = this.m00;
			doubleArray[1] = this.m10;
			doubleArray[2] = this.m20;
		} else if (int1 == 1) {
			doubleArray[0] = this.m01;
			doubleArray[1] = this.m11;
			doubleArray[2] = this.m21;
		} else {
			if (int1 != 2) {
				throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d4"));
			}

			doubleArray[0] = this.m02;
			doubleArray[1] = this.m12;
			doubleArray[2] = this.m22;
		}
	}

	public final void setRow(int int1, double double1, double double2, double double3) {
		switch (int1) {
		case 0: 
			this.m00 = double1;
			this.m01 = double2;
			this.m02 = double3;
			break;
		
		case 1: 
			this.m10 = double1;
			this.m11 = double2;
			this.m12 = double3;
			break;
		
		case 2: 
			this.m20 = double1;
			this.m21 = double2;
			this.m22 = double3;
			break;
		
		default: 
			throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d6"));
		
		}
	}

	public final void setRow(int int1, Vector3d vector3d) {
		switch (int1) {
		case 0: 
			this.m00 = vector3d.x;
			this.m01 = vector3d.y;
			this.m02 = vector3d.z;
			break;
		
		case 1: 
			this.m10 = vector3d.x;
			this.m11 = vector3d.y;
			this.m12 = vector3d.z;
			break;
		
		case 2: 
			this.m20 = vector3d.x;
			this.m21 = vector3d.y;
			this.m22 = vector3d.z;
			break;
		
		default: 
			throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d6"));
		
		}
	}

	public final void setRow(int int1, double[] doubleArray) {
		switch (int1) {
		case 0: 
			this.m00 = doubleArray[0];
			this.m01 = doubleArray[1];
			this.m02 = doubleArray[2];
			break;
		
		case 1: 
			this.m10 = doubleArray[0];
			this.m11 = doubleArray[1];
			this.m12 = doubleArray[2];
			break;
		
		case 2: 
			this.m20 = doubleArray[0];
			this.m21 = doubleArray[1];
			this.m22 = doubleArray[2];
			break;
		
		default: 
			throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d6"));
		
		}
	}

	public final void setColumn(int int1, double double1, double double2, double double3) {
		switch (int1) {
		case 0: 
			this.m00 = double1;
			this.m10 = double2;
			this.m20 = double3;
			break;
		
		case 1: 
			this.m01 = double1;
			this.m11 = double2;
			this.m21 = double3;
			break;
		
		case 2: 
			this.m02 = double1;
			this.m12 = double2;
			this.m22 = double3;
			break;
		
		default: 
			throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d9"));
		
		}
	}

	public final void setColumn(int int1, Vector3d vector3d) {
		switch (int1) {
		case 0: 
			this.m00 = vector3d.x;
			this.m10 = vector3d.y;
			this.m20 = vector3d.z;
			break;
		
		case 1: 
			this.m01 = vector3d.x;
			this.m11 = vector3d.y;
			this.m21 = vector3d.z;
			break;
		
		case 2: 
			this.m02 = vector3d.x;
			this.m12 = vector3d.y;
			this.m22 = vector3d.z;
			break;
		
		default: 
			throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d9"));
		
		}
	}

	public final void setColumn(int int1, double[] doubleArray) {
		switch (int1) {
		case 0: 
			this.m00 = doubleArray[0];
			this.m10 = doubleArray[1];
			this.m20 = doubleArray[2];
			break;
		
		case 1: 
			this.m01 = doubleArray[0];
			this.m11 = doubleArray[1];
			this.m21 = doubleArray[2];
			break;
		
		case 2: 
			this.m02 = doubleArray[0];
			this.m12 = doubleArray[1];
			this.m22 = doubleArray[2];
			break;
		
		default: 
			throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d9"));
		
		}
	}

	public final double getScale() {
		double[] doubleArray = new double[3];
		double[] doubleArray2 = new double[9];
		this.getScaleRotate(doubleArray, doubleArray2);
		return max3(doubleArray);
	}

	public final void add(double double1) {
		this.m00 += double1;
		this.m01 += double1;
		this.m02 += double1;
		this.m10 += double1;
		this.m11 += double1;
		this.m12 += double1;
		this.m20 += double1;
		this.m21 += double1;
		this.m22 += double1;
	}

	public final void add(double double1, Matrix3d matrix3d) {
		this.m00 = matrix3d.m00 + double1;
		this.m01 = matrix3d.m01 + double1;
		this.m02 = matrix3d.m02 + double1;
		this.m10 = matrix3d.m10 + double1;
		this.m11 = matrix3d.m11 + double1;
		this.m12 = matrix3d.m12 + double1;
		this.m20 = matrix3d.m20 + double1;
		this.m21 = matrix3d.m21 + double1;
		this.m22 = matrix3d.m22 + double1;
	}

	public final void add(Matrix3d matrix3d, Matrix3d matrix3d2) {
		this.m00 = matrix3d.m00 + matrix3d2.m00;
		this.m01 = matrix3d.m01 + matrix3d2.m01;
		this.m02 = matrix3d.m02 + matrix3d2.m02;
		this.m10 = matrix3d.m10 + matrix3d2.m10;
		this.m11 = matrix3d.m11 + matrix3d2.m11;
		this.m12 = matrix3d.m12 + matrix3d2.m12;
		this.m20 = matrix3d.m20 + matrix3d2.m20;
		this.m21 = matrix3d.m21 + matrix3d2.m21;
		this.m22 = matrix3d.m22 + matrix3d2.m22;
	}

	public final void add(Matrix3d matrix3d) {
		this.m00 += matrix3d.m00;
		this.m01 += matrix3d.m01;
		this.m02 += matrix3d.m02;
		this.m10 += matrix3d.m10;
		this.m11 += matrix3d.m11;
		this.m12 += matrix3d.m12;
		this.m20 += matrix3d.m20;
		this.m21 += matrix3d.m21;
		this.m22 += matrix3d.m22;
	}

	public final void sub(Matrix3d matrix3d, Matrix3d matrix3d2) {
		this.m00 = matrix3d.m00 - matrix3d2.m00;
		this.m01 = matrix3d.m01 - matrix3d2.m01;
		this.m02 = matrix3d.m02 - matrix3d2.m02;
		this.m10 = matrix3d.m10 - matrix3d2.m10;
		this.m11 = matrix3d.m11 - matrix3d2.m11;
		this.m12 = matrix3d.m12 - matrix3d2.m12;
		this.m20 = matrix3d.m20 - matrix3d2.m20;
		this.m21 = matrix3d.m21 - matrix3d2.m21;
		this.m22 = matrix3d.m22 - matrix3d2.m22;
	}

	public final void sub(Matrix3d matrix3d) {
		this.m00 -= matrix3d.m00;
		this.m01 -= matrix3d.m01;
		this.m02 -= matrix3d.m02;
		this.m10 -= matrix3d.m10;
		this.m11 -= matrix3d.m11;
		this.m12 -= matrix3d.m12;
		this.m20 -= matrix3d.m20;
		this.m21 -= matrix3d.m21;
		this.m22 -= matrix3d.m22;
	}

	public final void transpose() {
		double double1 = this.m10;
		this.m10 = this.m01;
		this.m01 = double1;
		double1 = this.m20;
		this.m20 = this.m02;
		this.m02 = double1;
		double1 = this.m21;
		this.m21 = this.m12;
		this.m12 = double1;
	}

	public final void transpose(Matrix3d matrix3d) {
		if (this != matrix3d) {
			this.m00 = matrix3d.m00;
			this.m01 = matrix3d.m10;
			this.m02 = matrix3d.m20;
			this.m10 = matrix3d.m01;
			this.m11 = matrix3d.m11;
			this.m12 = matrix3d.m21;
			this.m20 = matrix3d.m02;
			this.m21 = matrix3d.m12;
			this.m22 = matrix3d.m22;
		} else {
			this.transpose();
		}
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
	}

	public final void set(AxisAngle4d axisAngle4d) {
		double double1 = Math.sqrt(axisAngle4d.x * axisAngle4d.x + axisAngle4d.y * axisAngle4d.y + axisAngle4d.z * axisAngle4d.z);
		if (double1 < 1.110223024E-16) {
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
	}

	public final void set(AxisAngle4f axisAngle4f) {
		double double1 = Math.sqrt((double)(axisAngle4f.x * axisAngle4f.x + axisAngle4f.y * axisAngle4f.y + axisAngle4f.z * axisAngle4f.z));
		if (double1 < 1.110223024E-16) {
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
	}

	public final void set(Matrix3f matrix3f) {
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

	public final void set(Matrix3d matrix3d) {
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

	public final void set(double[] doubleArray) {
		this.m00 = doubleArray[0];
		this.m01 = doubleArray[1];
		this.m02 = doubleArray[2];
		this.m10 = doubleArray[3];
		this.m11 = doubleArray[4];
		this.m12 = doubleArray[5];
		this.m20 = doubleArray[6];
		this.m21 = doubleArray[7];
		this.m22 = doubleArray[8];
	}

	public final void invert(Matrix3d matrix3d) {
		this.invertGeneral(matrix3d);
	}

	public final void invert() {
		this.invertGeneral(this);
	}

	private final void invertGeneral(Matrix3d matrix3d) {
		double[] doubleArray = new double[9];
		int[] intArray = new int[3];
		double[] doubleArray2 = new double[]{matrix3d.m00, matrix3d.m01, matrix3d.m02, matrix3d.m10, matrix3d.m11, matrix3d.m12, matrix3d.m20, matrix3d.m21, matrix3d.m22};
		if (!luDecomposition(doubleArray2, intArray)) {
			throw new SingularMatrixException(VecMathI18N.getString("Matrix3d12"));
		} else {
			for (int int1 = 0; int1 < 9; ++int1) {
				doubleArray[int1] = 0.0;
			}

			doubleArray[0] = 1.0;
			doubleArray[4] = 1.0;
			doubleArray[8] = 1.0;
			luBacksubstitution(doubleArray2, intArray, doubleArray);
			this.m00 = doubleArray[0];
			this.m01 = doubleArray[1];
			this.m02 = doubleArray[2];
			this.m10 = doubleArray[3];
			this.m11 = doubleArray[4];
			this.m12 = doubleArray[5];
			this.m20 = doubleArray[6];
			this.m21 = doubleArray[7];
			this.m22 = doubleArray[8];
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
				throw new RuntimeException(VecMathI18N.getString("Matrix3d13"));
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

	public final double determinant() {
		double double1 = this.m00 * (this.m11 * this.m22 - this.m12 * this.m21) + this.m01 * (this.m12 * this.m20 - this.m10 * this.m22) + this.m02 * (this.m10 * this.m21 - this.m11 * this.m20);
		return double1;
	}

	public final void set(double double1) {
		this.m00 = double1;
		this.m01 = 0.0;
		this.m02 = 0.0;
		this.m10 = 0.0;
		this.m11 = double1;
		this.m12 = 0.0;
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = double1;
	}

	public final void rotX(double double1) {
		double double2 = Math.sin(double1);
		double double3 = Math.cos(double1);
		this.m00 = 1.0;
		this.m01 = 0.0;
		this.m02 = 0.0;
		this.m10 = 0.0;
		this.m11 = double3;
		this.m12 = -double2;
		this.m20 = 0.0;
		this.m21 = double2;
		this.m22 = double3;
	}

	public final void rotY(double double1) {
		double double2 = Math.sin(double1);
		double double3 = Math.cos(double1);
		this.m00 = double3;
		this.m01 = 0.0;
		this.m02 = double2;
		this.m10 = 0.0;
		this.m11 = 1.0;
		this.m12 = 0.0;
		this.m20 = -double2;
		this.m21 = 0.0;
		this.m22 = double3;
	}

	public final void rotZ(double double1) {
		double double2 = Math.sin(double1);
		double double3 = Math.cos(double1);
		this.m00 = double3;
		this.m01 = -double2;
		this.m02 = 0.0;
		this.m10 = double2;
		this.m11 = double3;
		this.m12 = 0.0;
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = 1.0;
	}

	public final void mul(double double1) {
		this.m00 *= double1;
		this.m01 *= double1;
		this.m02 *= double1;
		this.m10 *= double1;
		this.m11 *= double1;
		this.m12 *= double1;
		this.m20 *= double1;
		this.m21 *= double1;
		this.m22 *= double1;
	}

	public final void mul(double double1, Matrix3d matrix3d) {
		this.m00 = double1 * matrix3d.m00;
		this.m01 = double1 * matrix3d.m01;
		this.m02 = double1 * matrix3d.m02;
		this.m10 = double1 * matrix3d.m10;
		this.m11 = double1 * matrix3d.m11;
		this.m12 = double1 * matrix3d.m12;
		this.m20 = double1 * matrix3d.m20;
		this.m21 = double1 * matrix3d.m21;
		this.m22 = double1 * matrix3d.m22;
	}

	public final void mul(Matrix3d matrix3d) {
		double double1 = this.m00 * matrix3d.m00 + this.m01 * matrix3d.m10 + this.m02 * matrix3d.m20;
		double double2 = this.m00 * matrix3d.m01 + this.m01 * matrix3d.m11 + this.m02 * matrix3d.m21;
		double double3 = this.m00 * matrix3d.m02 + this.m01 * matrix3d.m12 + this.m02 * matrix3d.m22;
		double double4 = this.m10 * matrix3d.m00 + this.m11 * matrix3d.m10 + this.m12 * matrix3d.m20;
		double double5 = this.m10 * matrix3d.m01 + this.m11 * matrix3d.m11 + this.m12 * matrix3d.m21;
		double double6 = this.m10 * matrix3d.m02 + this.m11 * matrix3d.m12 + this.m12 * matrix3d.m22;
		double double7 = this.m20 * matrix3d.m00 + this.m21 * matrix3d.m10 + this.m22 * matrix3d.m20;
		double double8 = this.m20 * matrix3d.m01 + this.m21 * matrix3d.m11 + this.m22 * matrix3d.m21;
		double double9 = this.m20 * matrix3d.m02 + this.m21 * matrix3d.m12 + this.m22 * matrix3d.m22;
		this.m00 = double1;
		this.m01 = double2;
		this.m02 = double3;
		this.m10 = double4;
		this.m11 = double5;
		this.m12 = double6;
		this.m20 = double7;
		this.m21 = double8;
		this.m22 = double9;
	}

	public final void mul(Matrix3d matrix3d, Matrix3d matrix3d2) {
		if (this != matrix3d && this != matrix3d2) {
			this.m00 = matrix3d.m00 * matrix3d2.m00 + matrix3d.m01 * matrix3d2.m10 + matrix3d.m02 * matrix3d2.m20;
			this.m01 = matrix3d.m00 * matrix3d2.m01 + matrix3d.m01 * matrix3d2.m11 + matrix3d.m02 * matrix3d2.m21;
			this.m02 = matrix3d.m00 * matrix3d2.m02 + matrix3d.m01 * matrix3d2.m12 + matrix3d.m02 * matrix3d2.m22;
			this.m10 = matrix3d.m10 * matrix3d2.m00 + matrix3d.m11 * matrix3d2.m10 + matrix3d.m12 * matrix3d2.m20;
			this.m11 = matrix3d.m10 * matrix3d2.m01 + matrix3d.m11 * matrix3d2.m11 + matrix3d.m12 * matrix3d2.m21;
			this.m12 = matrix3d.m10 * matrix3d2.m02 + matrix3d.m11 * matrix3d2.m12 + matrix3d.m12 * matrix3d2.m22;
			this.m20 = matrix3d.m20 * matrix3d2.m00 + matrix3d.m21 * matrix3d2.m10 + matrix3d.m22 * matrix3d2.m20;
			this.m21 = matrix3d.m20 * matrix3d2.m01 + matrix3d.m21 * matrix3d2.m11 + matrix3d.m22 * matrix3d2.m21;
			this.m22 = matrix3d.m20 * matrix3d2.m02 + matrix3d.m21 * matrix3d2.m12 + matrix3d.m22 * matrix3d2.m22;
		} else {
			double double1 = matrix3d.m00 * matrix3d2.m00 + matrix3d.m01 * matrix3d2.m10 + matrix3d.m02 * matrix3d2.m20;
			double double2 = matrix3d.m00 * matrix3d2.m01 + matrix3d.m01 * matrix3d2.m11 + matrix3d.m02 * matrix3d2.m21;
			double double3 = matrix3d.m00 * matrix3d2.m02 + matrix3d.m01 * matrix3d2.m12 + matrix3d.m02 * matrix3d2.m22;
			double double4 = matrix3d.m10 * matrix3d2.m00 + matrix3d.m11 * matrix3d2.m10 + matrix3d.m12 * matrix3d2.m20;
			double double5 = matrix3d.m10 * matrix3d2.m01 + matrix3d.m11 * matrix3d2.m11 + matrix3d.m12 * matrix3d2.m21;
			double double6 = matrix3d.m10 * matrix3d2.m02 + matrix3d.m11 * matrix3d2.m12 + matrix3d.m12 * matrix3d2.m22;
			double double7 = matrix3d.m20 * matrix3d2.m00 + matrix3d.m21 * matrix3d2.m10 + matrix3d.m22 * matrix3d2.m20;
			double double8 = matrix3d.m20 * matrix3d2.m01 + matrix3d.m21 * matrix3d2.m11 + matrix3d.m22 * matrix3d2.m21;
			double double9 = matrix3d.m20 * matrix3d2.m02 + matrix3d.m21 * matrix3d2.m12 + matrix3d.m22 * matrix3d2.m22;
			this.m00 = double1;
			this.m01 = double2;
			this.m02 = double3;
			this.m10 = double4;
			this.m11 = double5;
			this.m12 = double6;
			this.m20 = double7;
			this.m21 = double8;
			this.m22 = double9;
		}
	}

	public final void mulNormalize(Matrix3d matrix3d) {
		double[] doubleArray = new double[9];
		double[] doubleArray2 = new double[9];
		double[] doubleArray3 = new double[3];
		doubleArray[0] = this.m00 * matrix3d.m00 + this.m01 * matrix3d.m10 + this.m02 * matrix3d.m20;
		doubleArray[1] = this.m00 * matrix3d.m01 + this.m01 * matrix3d.m11 + this.m02 * matrix3d.m21;
		doubleArray[2] = this.m00 * matrix3d.m02 + this.m01 * matrix3d.m12 + this.m02 * matrix3d.m22;
		doubleArray[3] = this.m10 * matrix3d.m00 + this.m11 * matrix3d.m10 + this.m12 * matrix3d.m20;
		doubleArray[4] = this.m10 * matrix3d.m01 + this.m11 * matrix3d.m11 + this.m12 * matrix3d.m21;
		doubleArray[5] = this.m10 * matrix3d.m02 + this.m11 * matrix3d.m12 + this.m12 * matrix3d.m22;
		doubleArray[6] = this.m20 * matrix3d.m00 + this.m21 * matrix3d.m10 + this.m22 * matrix3d.m20;
		doubleArray[7] = this.m20 * matrix3d.m01 + this.m21 * matrix3d.m11 + this.m22 * matrix3d.m21;
		doubleArray[8] = this.m20 * matrix3d.m02 + this.m21 * matrix3d.m12 + this.m22 * matrix3d.m22;
		compute_svd(doubleArray, doubleArray3, doubleArray2);
		this.m00 = doubleArray2[0];
		this.m01 = doubleArray2[1];
		this.m02 = doubleArray2[2];
		this.m10 = doubleArray2[3];
		this.m11 = doubleArray2[4];
		this.m12 = doubleArray2[5];
		this.m20 = doubleArray2[6];
		this.m21 = doubleArray2[7];
		this.m22 = doubleArray2[8];
	}

	public final void mulNormalize(Matrix3d matrix3d, Matrix3d matrix3d2) {
		double[] doubleArray = new double[9];
		double[] doubleArray2 = new double[9];
		double[] doubleArray3 = new double[3];
		doubleArray[0] = matrix3d.m00 * matrix3d2.m00 + matrix3d.m01 * matrix3d2.m10 + matrix3d.m02 * matrix3d2.m20;
		doubleArray[1] = matrix3d.m00 * matrix3d2.m01 + matrix3d.m01 * matrix3d2.m11 + matrix3d.m02 * matrix3d2.m21;
		doubleArray[2] = matrix3d.m00 * matrix3d2.m02 + matrix3d.m01 * matrix3d2.m12 + matrix3d.m02 * matrix3d2.m22;
		doubleArray[3] = matrix3d.m10 * matrix3d2.m00 + matrix3d.m11 * matrix3d2.m10 + matrix3d.m12 * matrix3d2.m20;
		doubleArray[4] = matrix3d.m10 * matrix3d2.m01 + matrix3d.m11 * matrix3d2.m11 + matrix3d.m12 * matrix3d2.m21;
		doubleArray[5] = matrix3d.m10 * matrix3d2.m02 + matrix3d.m11 * matrix3d2.m12 + matrix3d.m12 * matrix3d2.m22;
		doubleArray[6] = matrix3d.m20 * matrix3d2.m00 + matrix3d.m21 * matrix3d2.m10 + matrix3d.m22 * matrix3d2.m20;
		doubleArray[7] = matrix3d.m20 * matrix3d2.m01 + matrix3d.m21 * matrix3d2.m11 + matrix3d.m22 * matrix3d2.m21;
		doubleArray[8] = matrix3d.m20 * matrix3d2.m02 + matrix3d.m21 * matrix3d2.m12 + matrix3d.m22 * matrix3d2.m22;
		compute_svd(doubleArray, doubleArray3, doubleArray2);
		this.m00 = doubleArray2[0];
		this.m01 = doubleArray2[1];
		this.m02 = doubleArray2[2];
		this.m10 = doubleArray2[3];
		this.m11 = doubleArray2[4];
		this.m12 = doubleArray2[5];
		this.m20 = doubleArray2[6];
		this.m21 = doubleArray2[7];
		this.m22 = doubleArray2[8];
	}

	public final void mulTransposeBoth(Matrix3d matrix3d, Matrix3d matrix3d2) {
		if (this != matrix3d && this != matrix3d2) {
			this.m00 = matrix3d.m00 * matrix3d2.m00 + matrix3d.m10 * matrix3d2.m01 + matrix3d.m20 * matrix3d2.m02;
			this.m01 = matrix3d.m00 * matrix3d2.m10 + matrix3d.m10 * matrix3d2.m11 + matrix3d.m20 * matrix3d2.m12;
			this.m02 = matrix3d.m00 * matrix3d2.m20 + matrix3d.m10 * matrix3d2.m21 + matrix3d.m20 * matrix3d2.m22;
			this.m10 = matrix3d.m01 * matrix3d2.m00 + matrix3d.m11 * matrix3d2.m01 + matrix3d.m21 * matrix3d2.m02;
			this.m11 = matrix3d.m01 * matrix3d2.m10 + matrix3d.m11 * matrix3d2.m11 + matrix3d.m21 * matrix3d2.m12;
			this.m12 = matrix3d.m01 * matrix3d2.m20 + matrix3d.m11 * matrix3d2.m21 + matrix3d.m21 * matrix3d2.m22;
			this.m20 = matrix3d.m02 * matrix3d2.m00 + matrix3d.m12 * matrix3d2.m01 + matrix3d.m22 * matrix3d2.m02;
			this.m21 = matrix3d.m02 * matrix3d2.m10 + matrix3d.m12 * matrix3d2.m11 + matrix3d.m22 * matrix3d2.m12;
			this.m22 = matrix3d.m02 * matrix3d2.m20 + matrix3d.m12 * matrix3d2.m21 + matrix3d.m22 * matrix3d2.m22;
		} else {
			double double1 = matrix3d.m00 * matrix3d2.m00 + matrix3d.m10 * matrix3d2.m01 + matrix3d.m20 * matrix3d2.m02;
			double double2 = matrix3d.m00 * matrix3d2.m10 + matrix3d.m10 * matrix3d2.m11 + matrix3d.m20 * matrix3d2.m12;
			double double3 = matrix3d.m00 * matrix3d2.m20 + matrix3d.m10 * matrix3d2.m21 + matrix3d.m20 * matrix3d2.m22;
			double double4 = matrix3d.m01 * matrix3d2.m00 + matrix3d.m11 * matrix3d2.m01 + matrix3d.m21 * matrix3d2.m02;
			double double5 = matrix3d.m01 * matrix3d2.m10 + matrix3d.m11 * matrix3d2.m11 + matrix3d.m21 * matrix3d2.m12;
			double double6 = matrix3d.m01 * matrix3d2.m20 + matrix3d.m11 * matrix3d2.m21 + matrix3d.m21 * matrix3d2.m22;
			double double7 = matrix3d.m02 * matrix3d2.m00 + matrix3d.m12 * matrix3d2.m01 + matrix3d.m22 * matrix3d2.m02;
			double double8 = matrix3d.m02 * matrix3d2.m10 + matrix3d.m12 * matrix3d2.m11 + matrix3d.m22 * matrix3d2.m12;
			double double9 = matrix3d.m02 * matrix3d2.m20 + matrix3d.m12 * matrix3d2.m21 + matrix3d.m22 * matrix3d2.m22;
			this.m00 = double1;
			this.m01 = double2;
			this.m02 = double3;
			this.m10 = double4;
			this.m11 = double5;
			this.m12 = double6;
			this.m20 = double7;
			this.m21 = double8;
			this.m22 = double9;
		}
	}

	public final void mulTransposeRight(Matrix3d matrix3d, Matrix3d matrix3d2) {
		if (this != matrix3d && this != matrix3d2) {
			this.m00 = matrix3d.m00 * matrix3d2.m00 + matrix3d.m01 * matrix3d2.m01 + matrix3d.m02 * matrix3d2.m02;
			this.m01 = matrix3d.m00 * matrix3d2.m10 + matrix3d.m01 * matrix3d2.m11 + matrix3d.m02 * matrix3d2.m12;
			this.m02 = matrix3d.m00 * matrix3d2.m20 + matrix3d.m01 * matrix3d2.m21 + matrix3d.m02 * matrix3d2.m22;
			this.m10 = matrix3d.m10 * matrix3d2.m00 + matrix3d.m11 * matrix3d2.m01 + matrix3d.m12 * matrix3d2.m02;
			this.m11 = matrix3d.m10 * matrix3d2.m10 + matrix3d.m11 * matrix3d2.m11 + matrix3d.m12 * matrix3d2.m12;
			this.m12 = matrix3d.m10 * matrix3d2.m20 + matrix3d.m11 * matrix3d2.m21 + matrix3d.m12 * matrix3d2.m22;
			this.m20 = matrix3d.m20 * matrix3d2.m00 + matrix3d.m21 * matrix3d2.m01 + matrix3d.m22 * matrix3d2.m02;
			this.m21 = matrix3d.m20 * matrix3d2.m10 + matrix3d.m21 * matrix3d2.m11 + matrix3d.m22 * matrix3d2.m12;
			this.m22 = matrix3d.m20 * matrix3d2.m20 + matrix3d.m21 * matrix3d2.m21 + matrix3d.m22 * matrix3d2.m22;
		} else {
			double double1 = matrix3d.m00 * matrix3d2.m00 + matrix3d.m01 * matrix3d2.m01 + matrix3d.m02 * matrix3d2.m02;
			double double2 = matrix3d.m00 * matrix3d2.m10 + matrix3d.m01 * matrix3d2.m11 + matrix3d.m02 * matrix3d2.m12;
			double double3 = matrix3d.m00 * matrix3d2.m20 + matrix3d.m01 * matrix3d2.m21 + matrix3d.m02 * matrix3d2.m22;
			double double4 = matrix3d.m10 * matrix3d2.m00 + matrix3d.m11 * matrix3d2.m01 + matrix3d.m12 * matrix3d2.m02;
			double double5 = matrix3d.m10 * matrix3d2.m10 + matrix3d.m11 * matrix3d2.m11 + matrix3d.m12 * matrix3d2.m12;
			double double6 = matrix3d.m10 * matrix3d2.m20 + matrix3d.m11 * matrix3d2.m21 + matrix3d.m12 * matrix3d2.m22;
			double double7 = matrix3d.m20 * matrix3d2.m00 + matrix3d.m21 * matrix3d2.m01 + matrix3d.m22 * matrix3d2.m02;
			double double8 = matrix3d.m20 * matrix3d2.m10 + matrix3d.m21 * matrix3d2.m11 + matrix3d.m22 * matrix3d2.m12;
			double double9 = matrix3d.m20 * matrix3d2.m20 + matrix3d.m21 * matrix3d2.m21 + matrix3d.m22 * matrix3d2.m22;
			this.m00 = double1;
			this.m01 = double2;
			this.m02 = double3;
			this.m10 = double4;
			this.m11 = double5;
			this.m12 = double6;
			this.m20 = double7;
			this.m21 = double8;
			this.m22 = double9;
		}
	}

	public final void mulTransposeLeft(Matrix3d matrix3d, Matrix3d matrix3d2) {
		if (this != matrix3d && this != matrix3d2) {
			this.m00 = matrix3d.m00 * matrix3d2.m00 + matrix3d.m10 * matrix3d2.m10 + matrix3d.m20 * matrix3d2.m20;
			this.m01 = matrix3d.m00 * matrix3d2.m01 + matrix3d.m10 * matrix3d2.m11 + matrix3d.m20 * matrix3d2.m21;
			this.m02 = matrix3d.m00 * matrix3d2.m02 + matrix3d.m10 * matrix3d2.m12 + matrix3d.m20 * matrix3d2.m22;
			this.m10 = matrix3d.m01 * matrix3d2.m00 + matrix3d.m11 * matrix3d2.m10 + matrix3d.m21 * matrix3d2.m20;
			this.m11 = matrix3d.m01 * matrix3d2.m01 + matrix3d.m11 * matrix3d2.m11 + matrix3d.m21 * matrix3d2.m21;
			this.m12 = matrix3d.m01 * matrix3d2.m02 + matrix3d.m11 * matrix3d2.m12 + matrix3d.m21 * matrix3d2.m22;
			this.m20 = matrix3d.m02 * matrix3d2.m00 + matrix3d.m12 * matrix3d2.m10 + matrix3d.m22 * matrix3d2.m20;
			this.m21 = matrix3d.m02 * matrix3d2.m01 + matrix3d.m12 * matrix3d2.m11 + matrix3d.m22 * matrix3d2.m21;
			this.m22 = matrix3d.m02 * matrix3d2.m02 + matrix3d.m12 * matrix3d2.m12 + matrix3d.m22 * matrix3d2.m22;
		} else {
			double double1 = matrix3d.m00 * matrix3d2.m00 + matrix3d.m10 * matrix3d2.m10 + matrix3d.m20 * matrix3d2.m20;
			double double2 = matrix3d.m00 * matrix3d2.m01 + matrix3d.m10 * matrix3d2.m11 + matrix3d.m20 * matrix3d2.m21;
			double double3 = matrix3d.m00 * matrix3d2.m02 + matrix3d.m10 * matrix3d2.m12 + matrix3d.m20 * matrix3d2.m22;
			double double4 = matrix3d.m01 * matrix3d2.m00 + matrix3d.m11 * matrix3d2.m10 + matrix3d.m21 * matrix3d2.m20;
			double double5 = matrix3d.m01 * matrix3d2.m01 + matrix3d.m11 * matrix3d2.m11 + matrix3d.m21 * matrix3d2.m21;
			double double6 = matrix3d.m01 * matrix3d2.m02 + matrix3d.m11 * matrix3d2.m12 + matrix3d.m21 * matrix3d2.m22;
			double double7 = matrix3d.m02 * matrix3d2.m00 + matrix3d.m12 * matrix3d2.m10 + matrix3d.m22 * matrix3d2.m20;
			double double8 = matrix3d.m02 * matrix3d2.m01 + matrix3d.m12 * matrix3d2.m11 + matrix3d.m22 * matrix3d2.m21;
			double double9 = matrix3d.m02 * matrix3d2.m02 + matrix3d.m12 * matrix3d2.m12 + matrix3d.m22 * matrix3d2.m22;
			this.m00 = double1;
			this.m01 = double2;
			this.m02 = double3;
			this.m10 = double4;
			this.m11 = double5;
			this.m12 = double6;
			this.m20 = double7;
			this.m21 = double8;
			this.m22 = double9;
		}
	}

	public final void normalize() {
		double[] doubleArray = new double[9];
		double[] doubleArray2 = new double[3];
		this.getScaleRotate(doubleArray2, doubleArray);
		this.m00 = doubleArray[0];
		this.m01 = doubleArray[1];
		this.m02 = doubleArray[2];
		this.m10 = doubleArray[3];
		this.m11 = doubleArray[4];
		this.m12 = doubleArray[5];
		this.m20 = doubleArray[6];
		this.m21 = doubleArray[7];
		this.m22 = doubleArray[8];
	}

	public final void normalize(Matrix3d matrix3d) {
		double[] doubleArray = new double[9];
		double[] doubleArray2 = new double[9];
		double[] doubleArray3 = new double[3];
		doubleArray[0] = matrix3d.m00;
		doubleArray[1] = matrix3d.m01;
		doubleArray[2] = matrix3d.m02;
		doubleArray[3] = matrix3d.m10;
		doubleArray[4] = matrix3d.m11;
		doubleArray[5] = matrix3d.m12;
		doubleArray[6] = matrix3d.m20;
		doubleArray[7] = matrix3d.m21;
		doubleArray[8] = matrix3d.m22;
		compute_svd(doubleArray, doubleArray3, doubleArray2);
		this.m00 = doubleArray2[0];
		this.m01 = doubleArray2[1];
		this.m02 = doubleArray2[2];
		this.m10 = doubleArray2[3];
		this.m11 = doubleArray2[4];
		this.m12 = doubleArray2[5];
		this.m20 = doubleArray2[6];
		this.m21 = doubleArray2[7];
		this.m22 = doubleArray2[8];
	}

	public final void normalizeCP() {
		double double1 = 1.0 / Math.sqrt(this.m00 * this.m00 + this.m10 * this.m10 + this.m20 * this.m20);
		this.m00 *= double1;
		this.m10 *= double1;
		this.m20 *= double1;
		double1 = 1.0 / Math.sqrt(this.m01 * this.m01 + this.m11 * this.m11 + this.m21 * this.m21);
		this.m01 *= double1;
		this.m11 *= double1;
		this.m21 *= double1;
		this.m02 = this.m10 * this.m21 - this.m11 * this.m20;
		this.m12 = this.m01 * this.m20 - this.m00 * this.m21;
		this.m22 = this.m00 * this.m11 - this.m01 * this.m10;
	}

	public final void normalizeCP(Matrix3d matrix3d) {
		double double1 = 1.0 / Math.sqrt(matrix3d.m00 * matrix3d.m00 + matrix3d.m10 * matrix3d.m10 + matrix3d.m20 * matrix3d.m20);
		this.m00 = matrix3d.m00 * double1;
		this.m10 = matrix3d.m10 * double1;
		this.m20 = matrix3d.m20 * double1;
		double1 = 1.0 / Math.sqrt(matrix3d.m01 * matrix3d.m01 + matrix3d.m11 * matrix3d.m11 + matrix3d.m21 * matrix3d.m21);
		this.m01 = matrix3d.m01 * double1;
		this.m11 = matrix3d.m11 * double1;
		this.m21 = matrix3d.m21 * double1;
		this.m02 = this.m10 * this.m21 - this.m11 * this.m20;
		this.m12 = this.m01 * this.m20 - this.m00 * this.m21;
		this.m22 = this.m00 * this.m11 - this.m01 * this.m10;
	}

	public boolean equals(Matrix3d matrix3d) {
		try {
			return this.m00 == matrix3d.m00 && this.m01 == matrix3d.m01 && this.m02 == matrix3d.m02 && this.m10 == matrix3d.m10 && this.m11 == matrix3d.m11 && this.m12 == matrix3d.m12 && this.m20 == matrix3d.m20 && this.m21 == matrix3d.m21 && this.m22 == matrix3d.m22;
		} catch (NullPointerException nullPointerException) {
			return false;
		}
	}

	public boolean equals(Object object) {
		try {
			Matrix3d matrix3d = (Matrix3d)object;
			return this.m00 == matrix3d.m00 && this.m01 == matrix3d.m01 && this.m02 == matrix3d.m02 && this.m10 == matrix3d.m10 && this.m11 == matrix3d.m11 && this.m12 == matrix3d.m12 && this.m20 == matrix3d.m20 && this.m21 == matrix3d.m21 && this.m22 == matrix3d.m22;
		} catch (ClassCastException classCastException) {
			return false;
		} catch (NullPointerException nullPointerException) {
			return false;
		}
	}

	public boolean epsilonEquals(Matrix3d matrix3d, double double1) {
		double double2 = this.m00 - matrix3d.m00;
		if ((double2 < 0.0 ? -double2 : double2) > double1) {
			return false;
		} else {
			double2 = this.m01 - matrix3d.m01;
			if ((double2 < 0.0 ? -double2 : double2) > double1) {
				return false;
			} else {
				double2 = this.m02 - matrix3d.m02;
				if ((double2 < 0.0 ? -double2 : double2) > double1) {
					return false;
				} else {
					double2 = this.m10 - matrix3d.m10;
					if ((double2 < 0.0 ? -double2 : double2) > double1) {
						return false;
					} else {
						double2 = this.m11 - matrix3d.m11;
						if ((double2 < 0.0 ? -double2 : double2) > double1) {
							return false;
						} else {
							double2 = this.m12 - matrix3d.m12;
							if ((double2 < 0.0 ? -double2 : double2) > double1) {
								return false;
							} else {
								double2 = this.m20 - matrix3d.m20;
								if ((double2 < 0.0 ? -double2 : double2) > double1) {
									return false;
								} else {
									double2 = this.m21 - matrix3d.m21;
									if ((double2 < 0.0 ? -double2 : double2) > double1) {
										return false;
									} else {
										double2 = this.m22 - matrix3d.m22;
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

	public int hashCode() {
		long long1 = 1L;
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.m00);
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.m01);
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.m02);
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.m10);
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.m11);
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.m12);
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.m20);
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.m21);
		long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.m22);
		return (int)(long1 ^ long1 >> 32);
	}

	public final void setZero() {
		this.m00 = 0.0;
		this.m01 = 0.0;
		this.m02 = 0.0;
		this.m10 = 0.0;
		this.m11 = 0.0;
		this.m12 = 0.0;
		this.m20 = 0.0;
		this.m21 = 0.0;
		this.m22 = 0.0;
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

	public final void negate(Matrix3d matrix3d) {
		this.m00 = -matrix3d.m00;
		this.m01 = -matrix3d.m01;
		this.m02 = -matrix3d.m02;
		this.m10 = -matrix3d.m10;
		this.m11 = -matrix3d.m11;
		this.m12 = -matrix3d.m12;
		this.m20 = -matrix3d.m20;
		this.m21 = -matrix3d.m21;
		this.m22 = -matrix3d.m22;
	}

	public final void transform(Tuple3d tuple3d) {
		double double1 = this.m00 * tuple3d.x + this.m01 * tuple3d.y + this.m02 * tuple3d.z;
		double double2 = this.m10 * tuple3d.x + this.m11 * tuple3d.y + this.m12 * tuple3d.z;
		double double3 = this.m20 * tuple3d.x + this.m21 * tuple3d.y + this.m22 * tuple3d.z;
		tuple3d.set(double1, double2, double3);
	}

	public final void transform(Tuple3d tuple3d, Tuple3d tuple3d2) {
		double double1 = this.m00 * tuple3d.x + this.m01 * tuple3d.y + this.m02 * tuple3d.z;
		double double2 = this.m10 * tuple3d.x + this.m11 * tuple3d.y + this.m12 * tuple3d.z;
		tuple3d2.z = this.m20 * tuple3d.x + this.m21 * tuple3d.y + this.m22 * tuple3d.z;
		tuple3d2.x = double1;
		tuple3d2.y = double2;
	}

	final void getScaleRotate(double[] doubleArray, double[] doubleArray2) {
		double[] doubleArray3 = new double[]{this.m00, this.m01, this.m02, this.m10, this.m11, this.m12, this.m20, this.m21, this.m22};
		compute_svd(doubleArray3, doubleArray, doubleArray2);
	}

	static void compute_svd(double[] doubleArray, double[] doubleArray2, double[] doubleArray3) {
		double[] doubleArray4 = new double[9];
		double[] doubleArray5 = new double[9];
		double[] doubleArray6 = new double[9];
		double[] doubleArray7 = new double[9];
		double[] doubleArray8 = new double[9];
		double[] doubleArray9 = new double[3];
		double[] doubleArray10 = new double[3];
		int int1 = 0;
		int int2;
		for (int2 = 0; int2 < 9; ++int2) {
			doubleArray8[int2] = doubleArray[int2];
		}

		double double1;
		if (doubleArray[3] * doubleArray[3] < 1.110223024E-16) {
			doubleArray4[0] = 1.0;
			doubleArray4[1] = 0.0;
			doubleArray4[2] = 0.0;
			doubleArray4[3] = 0.0;
			doubleArray4[4] = 1.0;
			doubleArray4[5] = 0.0;
			doubleArray4[6] = 0.0;
			doubleArray4[7] = 0.0;
			doubleArray4[8] = 1.0;
		} else if (doubleArray[0] * doubleArray[0] < 1.110223024E-16) {
			doubleArray6[0] = doubleArray[0];
			doubleArray6[1] = doubleArray[1];
			doubleArray6[2] = doubleArray[2];
			doubleArray[0] = doubleArray[3];
			doubleArray[1] = doubleArray[4];
			doubleArray[2] = doubleArray[5];
			doubleArray[3] = -doubleArray6[0];
			doubleArray[4] = -doubleArray6[1];
			doubleArray[5] = -doubleArray6[2];
			doubleArray4[0] = 0.0;
			doubleArray4[1] = 1.0;
			doubleArray4[2] = 0.0;
			doubleArray4[3] = -1.0;
			doubleArray4[4] = 0.0;
			doubleArray4[5] = 0.0;
			doubleArray4[6] = 0.0;
			doubleArray4[7] = 0.0;
			doubleArray4[8] = 1.0;
		} else {
			double1 = 1.0 / Math.sqrt(doubleArray[0] * doubleArray[0] + doubleArray[3] * doubleArray[3]);
			double double2 = doubleArray[0] * double1;
			double double3 = doubleArray[3] * double1;
			doubleArray6[0] = double2 * doubleArray[0] + double3 * doubleArray[3];
			doubleArray6[1] = double2 * doubleArray[1] + double3 * doubleArray[4];
			doubleArray6[2] = double2 * doubleArray[2] + double3 * doubleArray[5];
			doubleArray[3] = -double3 * doubleArray[0] + double2 * doubleArray[3];
			doubleArray[4] = -double3 * doubleArray[1] + double2 * doubleArray[4];
			doubleArray[5] = -double3 * doubleArray[2] + double2 * doubleArray[5];
			doubleArray[0] = doubleArray6[0];
			doubleArray[1] = doubleArray6[1];
			doubleArray[2] = doubleArray6[2];
			doubleArray4[0] = double2;
			doubleArray4[1] = double3;
			doubleArray4[2] = 0.0;
			doubleArray4[3] = -double3;
			doubleArray4[4] = double2;
			doubleArray4[5] = 0.0;
			doubleArray4[6] = 0.0;
			doubleArray4[7] = 0.0;
			doubleArray4[8] = 1.0;
		}

		if (!(doubleArray[6] * doubleArray[6] < 1.110223024E-16)) {
			if (doubleArray[0] * doubleArray[0] < 1.110223024E-16) {
				doubleArray6[0] = doubleArray[0];
				doubleArray6[1] = doubleArray[1];
				doubleArray6[2] = doubleArray[2];
				doubleArray[0] = doubleArray[6];
				doubleArray[1] = doubleArray[7];
				doubleArray[2] = doubleArray[8];
				doubleArray[6] = -doubleArray6[0];
				doubleArray[7] = -doubleArray6[1];
				doubleArray[8] = -doubleArray6[2];
				doubleArray6[0] = doubleArray4[0];
				doubleArray6[1] = doubleArray4[1];
				doubleArray6[2] = doubleArray4[2];
				doubleArray4[0] = doubleArray4[6];
				doubleArray4[1] = doubleArray4[7];
				doubleArray4[2] = doubleArray4[8];
				doubleArray4[6] = -doubleArray6[0];
				doubleArray4[7] = -doubleArray6[1];
				doubleArray4[8] = -doubleArray6[2];
			} else {
				double1 = 1.0 / Math.sqrt(doubleArray[0] * doubleArray[0] + doubleArray[6] * doubleArray[6]);
				double double4 = doubleArray[0] * double1;
				double double5 = doubleArray[6] * double1;
				doubleArray6[0] = double4 * doubleArray[0] + double5 * doubleArray[6];
				doubleArray6[1] = double4 * doubleArray[1] + double5 * doubleArray[7];
				doubleArray6[2] = double4 * doubleArray[2] + double5 * doubleArray[8];
				doubleArray[6] = -double5 * doubleArray[0] + double4 * doubleArray[6];
				doubleArray[7] = -double5 * doubleArray[1] + double4 * doubleArray[7];
				doubleArray[8] = -double5 * doubleArray[2] + double4 * doubleArray[8];
				doubleArray[0] = doubleArray6[0];
				doubleArray[1] = doubleArray6[1];
				doubleArray[2] = doubleArray6[2];
				doubleArray6[0] = double4 * doubleArray4[0];
				doubleArray6[1] = double4 * doubleArray4[1];
				doubleArray4[2] = double5;
				doubleArray6[6] = -doubleArray4[0] * double5;
				doubleArray6[7] = -doubleArray4[1] * double5;
				doubleArray4[8] = double4;
				doubleArray4[0] = doubleArray6[0];
				doubleArray4[1] = doubleArray6[1];
				doubleArray4[6] = doubleArray6[6];
				doubleArray4[7] = doubleArray6[7];
			}
		}

		if (doubleArray[2] * doubleArray[2] < 1.110223024E-16) {
			doubleArray5[0] = 1.0;
			doubleArray5[1] = 0.0;
			doubleArray5[2] = 0.0;
			doubleArray5[3] = 0.0;
			doubleArray5[4] = 1.0;
			doubleArray5[5] = 0.0;
			doubleArray5[6] = 0.0;
			doubleArray5[7] = 0.0;
			doubleArray5[8] = 1.0;
		} else if (doubleArray[1] * doubleArray[1] < 1.110223024E-16) {
			doubleArray6[2] = doubleArray[2];
			doubleArray6[5] = doubleArray[5];
			doubleArray6[8] = doubleArray[8];
			doubleArray[2] = -doubleArray[1];
			doubleArray[5] = -doubleArray[4];
			doubleArray[8] = -doubleArray[7];
			doubleArray[1] = doubleArray6[2];
			doubleArray[4] = doubleArray6[5];
			doubleArray[7] = doubleArray6[8];
			doubleArray5[0] = 1.0;
			doubleArray5[1] = 0.0;
			doubleArray5[2] = 0.0;
			doubleArray5[3] = 0.0;
			doubleArray5[4] = 0.0;
			doubleArray5[5] = -1.0;
			doubleArray5[6] = 0.0;
			doubleArray5[7] = 1.0;
			doubleArray5[8] = 0.0;
		} else {
			double1 = 1.0 / Math.sqrt(doubleArray[1] * doubleArray[1] + doubleArray[2] * doubleArray[2]);
			double double6 = doubleArray[1] * double1;
			double double7 = doubleArray[2] * double1;
			doubleArray6[1] = double6 * doubleArray[1] + double7 * doubleArray[2];
			doubleArray[2] = -double7 * doubleArray[1] + double6 * doubleArray[2];
			doubleArray[1] = doubleArray6[1];
			doubleArray6[4] = double6 * doubleArray[4] + double7 * doubleArray[5];
			doubleArray[5] = -double7 * doubleArray[4] + double6 * doubleArray[5];
			doubleArray[4] = doubleArray6[4];
			doubleArray6[7] = double6 * doubleArray[7] + double7 * doubleArray[8];
			doubleArray[8] = -double7 * doubleArray[7] + double6 * doubleArray[8];
			doubleArray[7] = doubleArray6[7];
			doubleArray5[0] = 1.0;
			doubleArray5[1] = 0.0;
			doubleArray5[2] = 0.0;
			doubleArray5[3] = 0.0;
			doubleArray5[4] = double6;
			doubleArray5[5] = -double7;
			doubleArray5[6] = 0.0;
			doubleArray5[7] = double7;
			doubleArray5[8] = double6;
		}

		if (!(doubleArray[7] * doubleArray[7] < 1.110223024E-16)) {
			if (doubleArray[4] * doubleArray[4] < 1.110223024E-16) {
				doubleArray6[3] = doubleArray[3];
				doubleArray6[4] = doubleArray[4];
				doubleArray6[5] = doubleArray[5];
				doubleArray[3] = doubleArray[6];
				doubleArray[4] = doubleArray[7];
				doubleArray[5] = doubleArray[8];
				doubleArray[6] = -doubleArray6[3];
				doubleArray[7] = -doubleArray6[4];
				doubleArray[8] = -doubleArray6[5];
				doubleArray6[3] = doubleArray4[3];
				doubleArray6[4] = doubleArray4[4];
				doubleArray6[5] = doubleArray4[5];
				doubleArray4[3] = doubleArray4[6];
				doubleArray4[4] = doubleArray4[7];
				doubleArray4[5] = doubleArray4[8];
				doubleArray4[6] = -doubleArray6[3];
				doubleArray4[7] = -doubleArray6[4];
				doubleArray4[8] = -doubleArray6[5];
			} else {
				double1 = 1.0 / Math.sqrt(doubleArray[4] * doubleArray[4] + doubleArray[7] * doubleArray[7]);
				double double8 = doubleArray[4] * double1;
				double double9 = doubleArray[7] * double1;
				doubleArray6[3] = double8 * doubleArray[3] + double9 * doubleArray[6];
				doubleArray[6] = -double9 * doubleArray[3] + double8 * doubleArray[6];
				doubleArray[3] = doubleArray6[3];
				doubleArray6[4] = double8 * doubleArray[4] + double9 * doubleArray[7];
				doubleArray[7] = -double9 * doubleArray[4] + double8 * doubleArray[7];
				doubleArray[4] = doubleArray6[4];
				doubleArray6[5] = double8 * doubleArray[5] + double9 * doubleArray[8];
				doubleArray[8] = -double9 * doubleArray[5] + double8 * doubleArray[8];
				doubleArray[5] = doubleArray6[5];
				doubleArray6[3] = double8 * doubleArray4[3] + double9 * doubleArray4[6];
				doubleArray4[6] = -double9 * doubleArray4[3] + double8 * doubleArray4[6];
				doubleArray4[3] = doubleArray6[3];
				doubleArray6[4] = double8 * doubleArray4[4] + double9 * doubleArray4[7];
				doubleArray4[7] = -double9 * doubleArray4[4] + double8 * doubleArray4[7];
				doubleArray4[4] = doubleArray6[4];
				doubleArray6[5] = double8 * doubleArray4[5] + double9 * doubleArray4[8];
				doubleArray4[8] = -double9 * doubleArray4[5] + double8 * doubleArray4[8];
				doubleArray4[5] = doubleArray6[5];
			}
		}

		doubleArray7[0] = doubleArray[0];
		doubleArray7[1] = doubleArray[4];
		doubleArray7[2] = doubleArray[8];
		doubleArray9[0] = doubleArray[1];
		doubleArray9[1] = doubleArray[5];
		if (!(doubleArray9[0] * doubleArray9[0] < 1.110223024E-16) || !(doubleArray9[1] * doubleArray9[1] < 1.110223024E-16)) {
			compute_qr(doubleArray7, doubleArray9, doubleArray4, doubleArray5);
		}

		doubleArray10[0] = doubleArray7[0];
		doubleArray10[1] = doubleArray7[1];
		doubleArray10[2] = doubleArray7[2];
		if (almostEqual(Math.abs(doubleArray10[0]), 1.0) && almostEqual(Math.abs(doubleArray10[1]), 1.0) && almostEqual(Math.abs(doubleArray10[2]), 1.0)) {
			for (int2 = 0; int2 < 3; ++int2) {
				if (doubleArray10[int2] < 0.0) {
					++int1;
				}
			}

			if (int1 == 0 || int1 == 2) {
				doubleArray2[0] = doubleArray2[1] = doubleArray2[2] = 1.0;
				for (int2 = 0; int2 < 9; ++int2) {
					doubleArray3[int2] = doubleArray8[int2];
				}

				return;
			}
		}

		transpose_mat(doubleArray4, doubleArray6);
		transpose_mat(doubleArray5, doubleArray7);
		svdReorder(doubleArray, doubleArray6, doubleArray7, doubleArray10, doubleArray3, doubleArray2);
	}

	static void svdReorder(double[] doubleArray, double[] doubleArray2, double[] doubleArray3, double[] doubleArray4, double[] doubleArray5, double[] doubleArray6) {
		int[] intArray = new int[3];
		int[] intArray2 = new int[3];
		double[] doubleArray7 = new double[3];
		double[] doubleArray8 = new double[9];
		if (doubleArray4[0] < 0.0) {
			doubleArray4[0] = -doubleArray4[0];
			doubleArray3[0] = -doubleArray3[0];
			doubleArray3[1] = -doubleArray3[1];
			doubleArray3[2] = -doubleArray3[2];
		}

		if (doubleArray4[1] < 0.0) {
			doubleArray4[1] = -doubleArray4[1];
			doubleArray3[3] = -doubleArray3[3];
			doubleArray3[4] = -doubleArray3[4];
			doubleArray3[5] = -doubleArray3[5];
		}

		if (doubleArray4[2] < 0.0) {
			doubleArray4[2] = -doubleArray4[2];
			doubleArray3[6] = -doubleArray3[6];
			doubleArray3[7] = -doubleArray3[7];
			doubleArray3[8] = -doubleArray3[8];
		}

		mat_mul(doubleArray2, doubleArray3, doubleArray8);
		if (almostEqual(Math.abs(doubleArray4[0]), Math.abs(doubleArray4[1])) && almostEqual(Math.abs(doubleArray4[1]), Math.abs(doubleArray4[2]))) {
			int int1;
			for (int1 = 0; int1 < 9; ++int1) {
				doubleArray5[int1] = doubleArray8[int1];
			}

			for (int1 = 0; int1 < 3; ++int1) {
				doubleArray6[int1] = doubleArray4[int1];
			}
		} else {
			if (doubleArray4[0] > doubleArray4[1]) {
				if (doubleArray4[0] > doubleArray4[2]) {
					if (doubleArray4[2] > doubleArray4[1]) {
						intArray[0] = 0;
						intArray[1] = 2;
						intArray[2] = 1;
					} else {
						intArray[0] = 0;
						intArray[1] = 1;
						intArray[2] = 2;
					}
				} else {
					intArray[0] = 2;
					intArray[1] = 0;
					intArray[2] = 1;
				}
			} else if (doubleArray4[1] > doubleArray4[2]) {
				if (doubleArray4[2] > doubleArray4[0]) {
					intArray[0] = 1;
					intArray[1] = 2;
					intArray[2] = 0;
				} else {
					intArray[0] = 1;
					intArray[1] = 0;
					intArray[2] = 2;
				}
			} else {
				intArray[0] = 2;
				intArray[1] = 1;
				intArray[2] = 0;
			}

			doubleArray7[0] = doubleArray[0] * doubleArray[0] + doubleArray[1] * doubleArray[1] + doubleArray[2] * doubleArray[2];
			doubleArray7[1] = doubleArray[3] * doubleArray[3] + doubleArray[4] * doubleArray[4] + doubleArray[5] * doubleArray[5];
			doubleArray7[2] = doubleArray[6] * doubleArray[6] + doubleArray[7] * doubleArray[7] + doubleArray[8] * doubleArray[8];
			byte byte1;
			byte byte2;
			byte byte3;
			if (doubleArray7[0] > doubleArray7[1]) {
				if (doubleArray7[0] > doubleArray7[2]) {
					if (doubleArray7[2] > doubleArray7[1]) {
						byte1 = 0;
						byte3 = 1;
						byte2 = 2;
					} else {
						byte1 = 0;
						byte2 = 1;
						byte3 = 2;
					}
				} else {
					byte3 = 0;
					byte1 = 1;
					byte2 = 2;
				}
			} else if (doubleArray7[1] > doubleArray7[2]) {
				if (doubleArray7[2] > doubleArray7[0]) {
					byte2 = 0;
					byte3 = 1;
					byte1 = 2;
				} else {
					byte2 = 0;
					byte1 = 1;
					byte3 = 2;
				}
			} else {
				byte3 = 0;
				byte2 = 1;
				byte1 = 2;
			}

			int int2 = intArray[byte1];
			doubleArray6[0] = doubleArray4[int2];
			int2 = intArray[byte2];
			doubleArray6[1] = doubleArray4[int2];
			int2 = intArray[byte3];
			doubleArray6[2] = doubleArray4[int2];
			int2 = intArray[byte1];
			doubleArray5[0] = doubleArray8[int2];
			int2 = intArray[byte1] + 3;
			doubleArray5[3] = doubleArray8[int2];
			int2 = intArray[byte1] + 6;
			doubleArray5[6] = doubleArray8[int2];
			int2 = intArray[byte2];
			doubleArray5[1] = doubleArray8[int2];
			int2 = intArray[byte2] + 3;
			doubleArray5[4] = doubleArray8[int2];
			int2 = intArray[byte2] + 6;
			doubleArray5[7] = doubleArray8[int2];
			int2 = intArray[byte3];
			doubleArray5[2] = doubleArray8[int2];
			int2 = intArray[byte3] + 3;
			doubleArray5[5] = doubleArray8[int2];
			int2 = intArray[byte3] + 6;
			doubleArray5[8] = doubleArray8[int2];
		}
	}

	static int compute_qr(double[] doubleArray, double[] doubleArray2, double[] doubleArray3, double[] doubleArray4) {
		double[] doubleArray5 = new double[2];
		double[] doubleArray6 = new double[2];
		double[] doubleArray7 = new double[2];
		double[] doubleArray8 = new double[2];
		double[] doubleArray9 = new double[9];
		double double1 = 1.0;
		double double2 = -1.0;
		boolean boolean1 = false;
		byte byte1 = 1;
		if (Math.abs(doubleArray2[1]) < 4.89E-15 || Math.abs(doubleArray2[0]) < 4.89E-15) {
			boolean1 = true;
		}

		double double3;
		double double4;
		for (int int1 = 0; int1 < 10 && !boolean1; ++int1) {
			double double5 = compute_shift(doubleArray[1], doubleArray2[1], doubleArray[2]);
			double double6 = (Math.abs(doubleArray[0]) - double5) * (d_sign(double1, doubleArray[0]) + double5 / doubleArray[0]);
			double double7 = doubleArray2[0];
			compute_rot(double6, double7, doubleArray8, doubleArray6, 0, byte1);
			double6 = doubleArray6[0] * doubleArray[0] + doubleArray8[0] * doubleArray2[0];
			doubleArray2[0] = doubleArray6[0] * doubleArray2[0] - doubleArray8[0] * doubleArray[0];
			double7 = doubleArray8[0] * doubleArray[1];
			doubleArray[1] = doubleArray6[0] * doubleArray[1];
			double double8 = compute_rot(double6, double7, doubleArray7, doubleArray5, 0, byte1);
			byte1 = 0;
			doubleArray[0] = double8;
			double6 = doubleArray5[0] * doubleArray2[0] + doubleArray7[0] * doubleArray[1];
			doubleArray[1] = doubleArray5[0] * doubleArray[1] - doubleArray7[0] * doubleArray2[0];
			double7 = doubleArray7[0] * doubleArray2[1];
			doubleArray2[1] = doubleArray5[0] * doubleArray2[1];
			double8 = compute_rot(double6, double7, doubleArray8, doubleArray6, 1, byte1);
			doubleArray2[0] = double8;
			double6 = doubleArray6[1] * doubleArray[1] + doubleArray8[1] * doubleArray2[1];
			doubleArray2[1] = doubleArray6[1] * doubleArray2[1] - doubleArray8[1] * doubleArray[1];
			double7 = doubleArray8[1] * doubleArray[2];
			doubleArray[2] = doubleArray6[1] * doubleArray[2];
			double8 = compute_rot(double6, double7, doubleArray7, doubleArray5, 1, byte1);
			doubleArray[1] = double8;
			double6 = doubleArray5[1] * doubleArray2[1] + doubleArray7[1] * doubleArray[2];
			doubleArray[2] = doubleArray5[1] * doubleArray[2] - doubleArray7[1] * doubleArray2[1];
			doubleArray2[1] = double6;
			double3 = doubleArray3[0];
			doubleArray3[0] = doubleArray5[0] * double3 + doubleArray7[0] * doubleArray3[3];
			doubleArray3[3] = -doubleArray7[0] * double3 + doubleArray5[0] * doubleArray3[3];
			double3 = doubleArray3[1];
			doubleArray3[1] = doubleArray5[0] * double3 + doubleArray7[0] * doubleArray3[4];
			doubleArray3[4] = -doubleArray7[0] * double3 + doubleArray5[0] * doubleArray3[4];
			double3 = doubleArray3[2];
			doubleArray3[2] = doubleArray5[0] * double3 + doubleArray7[0] * doubleArray3[5];
			doubleArray3[5] = -doubleArray7[0] * double3 + doubleArray5[0] * doubleArray3[5];
			double3 = doubleArray3[3];
			doubleArray3[3] = doubleArray5[1] * double3 + doubleArray7[1] * doubleArray3[6];
			doubleArray3[6] = -doubleArray7[1] * double3 + doubleArray5[1] * doubleArray3[6];
			double3 = doubleArray3[4];
			doubleArray3[4] = doubleArray5[1] * double3 + doubleArray7[1] * doubleArray3[7];
			doubleArray3[7] = -doubleArray7[1] * double3 + doubleArray5[1] * doubleArray3[7];
			double3 = doubleArray3[5];
			doubleArray3[5] = doubleArray5[1] * double3 + doubleArray7[1] * doubleArray3[8];
			doubleArray3[8] = -doubleArray7[1] * double3 + doubleArray5[1] * doubleArray3[8];
			double4 = doubleArray4[0];
			doubleArray4[0] = doubleArray6[0] * double4 + doubleArray8[0] * doubleArray4[1];
			doubleArray4[1] = -doubleArray8[0] * double4 + doubleArray6[0] * doubleArray4[1];
			double4 = doubleArray4[3];
			doubleArray4[3] = doubleArray6[0] * double4 + doubleArray8[0] * doubleArray4[4];
			doubleArray4[4] = -doubleArray8[0] * double4 + doubleArray6[0] * doubleArray4[4];
			double4 = doubleArray4[6];
			doubleArray4[6] = doubleArray6[0] * double4 + doubleArray8[0] * doubleArray4[7];
			doubleArray4[7] = -doubleArray8[0] * double4 + doubleArray6[0] * doubleArray4[7];
			double4 = doubleArray4[1];
			doubleArray4[1] = doubleArray6[1] * double4 + doubleArray8[1] * doubleArray4[2];
			doubleArray4[2] = -doubleArray8[1] * double4 + doubleArray6[1] * doubleArray4[2];
			double4 = doubleArray4[4];
			doubleArray4[4] = doubleArray6[1] * double4 + doubleArray8[1] * doubleArray4[5];
			doubleArray4[5] = -doubleArray8[1] * double4 + doubleArray6[1] * doubleArray4[5];
			double4 = doubleArray4[7];
			doubleArray4[7] = doubleArray6[1] * double4 + doubleArray8[1] * doubleArray4[8];
			doubleArray4[8] = -doubleArray8[1] * double4 + doubleArray6[1] * doubleArray4[8];
			doubleArray9[0] = doubleArray[0];
			doubleArray9[1] = doubleArray2[0];
			doubleArray9[2] = 0.0;
			doubleArray9[3] = 0.0;
			doubleArray9[4] = doubleArray[1];
			doubleArray9[5] = doubleArray2[1];
			doubleArray9[6] = 0.0;
			doubleArray9[7] = 0.0;
			doubleArray9[8] = doubleArray[2];
			if (Math.abs(doubleArray2[1]) < 4.89E-15 || Math.abs(doubleArray2[0]) < 4.89E-15) {
				boolean1 = true;
			}
		}

		if (Math.abs(doubleArray2[1]) < 4.89E-15) {
			compute_2X2(doubleArray[0], doubleArray2[0], doubleArray[1], doubleArray, doubleArray7, doubleArray5, doubleArray8, doubleArray6, 0);
			double3 = doubleArray3[0];
			doubleArray3[0] = doubleArray5[0] * double3 + doubleArray7[0] * doubleArray3[3];
			doubleArray3[3] = -doubleArray7[0] * double3 + doubleArray5[0] * doubleArray3[3];
			double3 = doubleArray3[1];
			doubleArray3[1] = doubleArray5[0] * double3 + doubleArray7[0] * doubleArray3[4];
			doubleArray3[4] = -doubleArray7[0] * double3 + doubleArray5[0] * doubleArray3[4];
			double3 = doubleArray3[2];
			doubleArray3[2] = doubleArray5[0] * double3 + doubleArray7[0] * doubleArray3[5];
			doubleArray3[5] = -doubleArray7[0] * double3 + doubleArray5[0] * doubleArray3[5];
			double4 = doubleArray4[0];
			doubleArray4[0] = doubleArray6[0] * double4 + doubleArray8[0] * doubleArray4[1];
			doubleArray4[1] = -doubleArray8[0] * double4 + doubleArray6[0] * doubleArray4[1];
			double4 = doubleArray4[3];
			doubleArray4[3] = doubleArray6[0] * double4 + doubleArray8[0] * doubleArray4[4];
			doubleArray4[4] = -doubleArray8[0] * double4 + doubleArray6[0] * doubleArray4[4];
			double4 = doubleArray4[6];
			doubleArray4[6] = doubleArray6[0] * double4 + doubleArray8[0] * doubleArray4[7];
			doubleArray4[7] = -doubleArray8[0] * double4 + doubleArray6[0] * doubleArray4[7];
		} else {
			compute_2X2(doubleArray[1], doubleArray2[1], doubleArray[2], doubleArray, doubleArray7, doubleArray5, doubleArray8, doubleArray6, 1);
			double3 = doubleArray3[3];
			doubleArray3[3] = doubleArray5[0] * double3 + doubleArray7[0] * doubleArray3[6];
			doubleArray3[6] = -doubleArray7[0] * double3 + doubleArray5[0] * doubleArray3[6];
			double3 = doubleArray3[4];
			doubleArray3[4] = doubleArray5[0] * double3 + doubleArray7[0] * doubleArray3[7];
			doubleArray3[7] = -doubleArray7[0] * double3 + doubleArray5[0] * doubleArray3[7];
			double3 = doubleArray3[5];
			doubleArray3[5] = doubleArray5[0] * double3 + doubleArray7[0] * doubleArray3[8];
			doubleArray3[8] = -doubleArray7[0] * double3 + doubleArray5[0] * doubleArray3[8];
			double4 = doubleArray4[1];
			doubleArray4[1] = doubleArray6[0] * double4 + doubleArray8[0] * doubleArray4[2];
			doubleArray4[2] = -doubleArray8[0] * double4 + doubleArray6[0] * doubleArray4[2];
			double4 = doubleArray4[4];
			doubleArray4[4] = doubleArray6[0] * double4 + doubleArray8[0] * doubleArray4[5];
			doubleArray4[5] = -doubleArray8[0] * double4 + doubleArray6[0] * doubleArray4[5];
			double4 = doubleArray4[7];
			doubleArray4[7] = doubleArray6[0] * double4 + doubleArray8[0] * doubleArray4[8];
			doubleArray4[8] = -doubleArray8[0] * double4 + doubleArray6[0] * doubleArray4[8];
		}

		return 0;
	}

	static double max(double double1, double double2) {
		return double1 > double2 ? double1 : double2;
	}

	static double min(double double1, double double2) {
		return double1 < double2 ? double1 : double2;
	}

	static double d_sign(double double1, double double2) {
		double double3 = double1 >= 0.0 ? double1 : -double1;
		return double2 >= 0.0 ? double3 : -double3;
	}

	static double compute_shift(double double1, double double2, double double3) {
		double double4 = Math.abs(double1);
		double double5 = Math.abs(double2);
		double double6 = Math.abs(double3);
		double double7 = min(double4, double6);
		double double8 = max(double4, double6);
		double double9;
		double double10;
		if (double7 == 0.0) {
			double10 = 0.0;
			if (double8 != 0.0) {
				double9 = min(double8, double5) / max(double8, double5);
			}
		} else {
			double double11;
			double double12;
			double double13;
			double double14;
			if (double5 < double8) {
				double12 = double7 / double8 + 1.0;
				double13 = (double8 - double7) / double8;
				double9 = double5 / double8;
				double14 = double9 * double9;
				double11 = 2.0 / (Math.sqrt(double12 * double12 + double14) + Math.sqrt(double13 * double13 + double14));
				double10 = double7 * double11;
			} else {
				double14 = double8 / double5;
				if (double14 == 0.0) {
					double10 = double7 * double8 / double5;
				} else {
					double12 = double7 / double8 + 1.0;
					double13 = (double8 - double7) / double8;
					double9 = double12 * double14;
					double double15 = double13 * double14;
					double11 = 1.0 / (Math.sqrt(double9 * double9 + 1.0) + Math.sqrt(double15 * double15 + 1.0));
					double10 = double7 * double11 * double14;
					double10 += double10;
				}
			}
		}

		return double10;
	}

	static int compute_2X2(double double1, double double2, double double3, double[] doubleArray, double[] doubleArray2, double[] doubleArray3, double[] doubleArray4, double[] doubleArray5, int int1) {
		double double4 = 2.0;
		double double5 = 1.0;
		double double6 = doubleArray[0];
		double double7 = doubleArray[1];
		double double8 = 0.0;
		double double9 = 0.0;
		double double10 = 0.0;
		double double11 = 0.0;
		double double12 = 0.0;
		double double13 = double1;
		double double14 = Math.abs(double1);
		double double15 = double3;
		double double16 = Math.abs(double3);
		byte byte1 = 1;
		boolean boolean1;
		if (double16 > double14) {
			boolean1 = true;
		} else {
			boolean1 = false;
		}

		if (boolean1) {
			byte1 = 3;
			double13 = double3;
			double15 = double1;
			double double17 = double14;
			double14 = double16;
			double16 = double17;
		}

		double double18 = Math.abs(double2);
		if (double18 == 0.0) {
			doubleArray[1] = double16;
			doubleArray[0] = double14;
			double8 = 1.0;
			double9 = 1.0;
			double10 = 0.0;
			double11 = 0.0;
		} else {
			boolean boolean2 = true;
			if (double18 > double14) {
				byte1 = 2;
				if (double14 / double18 < 1.110223024E-16) {
					boolean2 = false;
					double6 = double18;
					if (double16 > 1.0) {
						double7 = double14 / (double18 / double16);
					} else {
						double7 = double14 / double18 * double16;
					}

					double8 = 1.0;
					double10 = double15 / double2;
					double11 = 1.0;
					double9 = double13 / double2;
				}
			}

			if (boolean2) {
				double double19 = double14 - double16;
				double double20;
				if (double19 == double14) {
					double20 = 1.0;
				} else {
					double20 = double19 / double14;
				}

				double double21 = double2 / double13;
				double double22 = 2.0 - double20;
				double double23 = double21 * double21;
				double double24 = double22 * double22;
				double double25 = Math.sqrt(double24 + double23);
				double double26;
				if (double20 == 0.0) {
					double26 = Math.abs(double21);
				} else {
					double26 = Math.sqrt(double20 * double20 + double23);
				}

				double double27 = (double25 + double26) * 0.5;
				if (double18 > double14) {
					byte1 = 2;
					if (double14 / double18 < 1.110223024E-16) {
						boolean2 = false;
						double6 = double18;
						if (double16 > 1.0) {
							double7 = double14 / (double18 / double16);
						} else {
							double7 = double14 / double18 * double16;
						}

						double8 = 1.0;
						double10 = double15 / double2;
						double11 = 1.0;
						double9 = double13 / double2;
					}
				}

				if (boolean2) {
					double19 = double14 - double16;
					if (double19 == double14) {
						double20 = 1.0;
					} else {
						double20 = double19 / double14;
					}

					double21 = double2 / double13;
					double22 = 2.0 - double20;
					double23 = double21 * double21;
					double24 = double22 * double22;
					double25 = Math.sqrt(double24 + double23);
					if (double20 == 0.0) {
						double26 = Math.abs(double21);
					} else {
						double26 = Math.sqrt(double20 * double20 + double23);
					}

					double27 = (double25 + double26) * 0.5;
					double7 = double16 / double27;
					double6 = double14 * double27;
					if (double23 == 0.0) {
						if (double20 == 0.0) {
							double22 = d_sign(double4, double13) * d_sign(double5, double2);
						} else {
							double22 = double2 / d_sign(double19, double13) + double21 / double22;
						}
					} else {
						double22 = (double21 / (double25 + double22) + double21 / (double26 + double20)) * (double27 + 1.0);
					}

					double20 = Math.sqrt(double22 * double22 + 4.0);
					double9 = 2.0 / double20;
					double11 = double22 / double20;
					double8 = (double9 + double11 * double21) / double27;
					double10 = double15 / double13 * double11 / double27;
				}
			}

			if (boolean1) {
				doubleArray3[0] = double11;
				doubleArray2[0] = double9;
				doubleArray5[0] = double10;
				doubleArray4[0] = double8;
			} else {
				doubleArray3[0] = double8;
				doubleArray2[0] = double10;
				doubleArray5[0] = double9;
				doubleArray4[0] = double11;
			}

			if (byte1 == 1) {
				double12 = d_sign(double5, doubleArray5[0]) * d_sign(double5, doubleArray3[0]) * d_sign(double5, double1);
			}

			if (byte1 == 2) {
				double12 = d_sign(double5, doubleArray4[0]) * d_sign(double5, doubleArray3[0]) * d_sign(double5, double2);
			}

			if (byte1 == 3) {
				double12 = d_sign(double5, doubleArray4[0]) * d_sign(double5, doubleArray2[0]) * d_sign(double5, double3);
			}

			doubleArray[int1] = d_sign(double6, double12);
			double double28 = double12 * d_sign(double5, double1) * d_sign(double5, double3);
			doubleArray[int1 + 1] = d_sign(double7, double28);
		}

		return 0;
	}

	static double compute_rot(double double1, double double2, double[] doubleArray, double[] doubleArray2, int int1, int int2) {
		double double3;
		double double4;
		double double5;
		if (double2 == 0.0) {
			double3 = 1.0;
			double4 = 0.0;
			double5 = double1;
		} else if (double1 == 0.0) {
			double3 = 0.0;
			double4 = 1.0;
			double5 = double2;
		} else {
			double double6 = double1;
			double double7 = double2;
			double double8 = max(Math.abs(double1), Math.abs(double2));
			int int3;
			int int4;
			if (double8 >= 4.9947976805055876E145) {
				for (int4 = 0; double8 >= 4.9947976805055876E145; double8 = max(Math.abs(double6), Math.abs(double7))) {
					++int4;
					double6 *= 2.002083095183101E-146;
					double7 *= 2.002083095183101E-146;
				}

				double5 = Math.sqrt(double6 * double6 + double7 * double7);
				double3 = double6 / double5;
				double4 = double7 / double5;
				for (int3 = 1; int3 <= int4; ++int3) {
					double5 *= 4.9947976805055876E145;
				}
			} else if (!(double8 <= 2.002083095183101E-146)) {
				double5 = Math.sqrt(double1 * double1 + double2 * double2);
				double3 = double1 / double5;
				double4 = double2 / double5;
			} else {
				for (int4 = 0; double8 <= 2.002083095183101E-146; double8 = max(Math.abs(double6), Math.abs(double7))) {
					++int4;
					double6 *= 4.9947976805055876E145;
					double7 *= 4.9947976805055876E145;
				}

				double5 = Math.sqrt(double6 * double6 + double7 * double7);
				double3 = double6 / double5;
				double4 = double7 / double5;
				for (int3 = 1; int3 <= int4; ++int3) {
					double5 *= 2.002083095183101E-146;
				}
			}

			if (Math.abs(double1) > Math.abs(double2) && double3 < 0.0) {
				double3 = -double3;
				double4 = -double4;
				double5 = -double5;
			}
		}

		doubleArray[int1] = double4;
		doubleArray2[int1] = double3;
		return double5;
	}

	static void print_mat(double[] doubleArray) {
		for (int int1 = 0; int1 < 3; ++int1) {
			System.out.println(doubleArray[int1 * 3 + 0] + " " + doubleArray[int1 * 3 + 1] + " " + doubleArray[int1 * 3 + 2] + "\n");
		}
	}

	static void print_det(double[] doubleArray) {
		double double1 = doubleArray[0] * doubleArray[4] * doubleArray[8] + doubleArray[1] * doubleArray[5] * doubleArray[6] + doubleArray[2] * doubleArray[3] * doubleArray[7] - doubleArray[2] * doubleArray[4] * doubleArray[6] - doubleArray[0] * doubleArray[5] * doubleArray[7] - doubleArray[1] * doubleArray[3] * doubleArray[8];
		System.out.println("det= " + double1);
	}

	static void mat_mul(double[] doubleArray, double[] doubleArray2, double[] doubleArray3) {
		double[] doubleArray4 = new double[]{doubleArray[0] * doubleArray2[0] + doubleArray[1] * doubleArray2[3] + doubleArray[2] * doubleArray2[6], doubleArray[0] * doubleArray2[1] + doubleArray[1] * doubleArray2[4] + doubleArray[2] * doubleArray2[7], doubleArray[0] * doubleArray2[2] + doubleArray[1] * doubleArray2[5] + doubleArray[2] * doubleArray2[8], doubleArray[3] * doubleArray2[0] + doubleArray[4] * doubleArray2[3] + doubleArray[5] * doubleArray2[6], doubleArray[3] * doubleArray2[1] + doubleArray[4] * doubleArray2[4] + doubleArray[5] * doubleArray2[7], doubleArray[3] * doubleArray2[2] + doubleArray[4] * doubleArray2[5] + doubleArray[5] * doubleArray2[8], doubleArray[6] * doubleArray2[0] + doubleArray[7] * doubleArray2[3] + doubleArray[8] * doubleArray2[6], doubleArray[6] * doubleArray2[1] + doubleArray[7] * doubleArray2[4] + doubleArray[8] * doubleArray2[7], doubleArray[6] * doubleArray2[2] + doubleArray[7] * doubleArray2[5] + doubleArray[8] * doubleArray2[8]};
		for (int int1 = 0; int1 < 9; ++int1) {
			doubleArray3[int1] = doubleArray4[int1];
		}
	}

	static void transpose_mat(double[] doubleArray, double[] doubleArray2) {
		doubleArray2[0] = doubleArray[0];
		doubleArray2[1] = doubleArray[3];
		doubleArray2[2] = doubleArray[6];
		doubleArray2[3] = doubleArray[1];
		doubleArray2[4] = doubleArray[4];
		doubleArray2[5] = doubleArray[7];
		doubleArray2[6] = doubleArray[2];
		doubleArray2[7] = doubleArray[5];
		doubleArray2[8] = doubleArray[8];
	}

	static double max3(double[] doubleArray) {
		if (doubleArray[0] > doubleArray[1]) {
			return doubleArray[0] > doubleArray[2] ? doubleArray[0] : doubleArray[2];
		} else {
			return doubleArray[1] > doubleArray[2] ? doubleArray[1] : doubleArray[2];
		}
	}

	private static final boolean almostEqual(double double1, double double2) {
		if (double1 == double2) {
			return true;
		} else {
			double double3 = Math.abs(double1 - double2);
			double double4 = Math.abs(double1);
			double double5 = Math.abs(double2);
			double double6 = double4 >= double5 ? double4 : double5;
			if (double3 < 1.0E-6) {
				return true;
			} else {
				return double3 / double6 < 1.0E-4;
			}
		}
	}

	public Object clone() {
		Matrix3d matrix3d = null;
		try {
			matrix3d = (Matrix3d)super.clone();
			return matrix3d;
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
}
