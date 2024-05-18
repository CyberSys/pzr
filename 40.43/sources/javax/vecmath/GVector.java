package javax.vecmath;

import java.io.Serializable;


public class GVector implements Serializable,Cloneable {
	private int length;
	double[] values;
	static final long serialVersionUID = 1398850036893875112L;

	public GVector(int int1) {
		this.length = int1;
		this.values = new double[int1];
		for (int int2 = 0; int2 < int1; ++int2) {
			this.values[int2] = 0.0;
		}
	}

	public GVector(double[] doubleArray) {
		this.length = doubleArray.length;
		this.values = new double[doubleArray.length];
		for (int int1 = 0; int1 < this.length; ++int1) {
			this.values[int1] = doubleArray[int1];
		}
	}

	public GVector(GVector gVector) {
		this.values = new double[gVector.length];
		this.length = gVector.length;
		for (int int1 = 0; int1 < this.length; ++int1) {
			this.values[int1] = gVector.values[int1];
		}
	}

	public GVector(Tuple2f tuple2f) {
		this.values = new double[2];
		this.values[0] = (double)tuple2f.x;
		this.values[1] = (double)tuple2f.y;
		this.length = 2;
	}

	public GVector(Tuple3f tuple3f) {
		this.values = new double[3];
		this.values[0] = (double)tuple3f.x;
		this.values[1] = (double)tuple3f.y;
		this.values[2] = (double)tuple3f.z;
		this.length = 3;
	}

	public GVector(Tuple3d tuple3d) {
		this.values = new double[3];
		this.values[0] = tuple3d.x;
		this.values[1] = tuple3d.y;
		this.values[2] = tuple3d.z;
		this.length = 3;
	}

	public GVector(Tuple4f tuple4f) {
		this.values = new double[4];
		this.values[0] = (double)tuple4f.x;
		this.values[1] = (double)tuple4f.y;
		this.values[2] = (double)tuple4f.z;
		this.values[3] = (double)tuple4f.w;
		this.length = 4;
	}

	public GVector(Tuple4d tuple4d) {
		this.values = new double[4];
		this.values[0] = tuple4d.x;
		this.values[1] = tuple4d.y;
		this.values[2] = tuple4d.z;
		this.values[3] = tuple4d.w;
		this.length = 4;
	}

	public GVector(double[] doubleArray, int int1) {
		this.length = int1;
		this.values = new double[int1];
		for (int int2 = 0; int2 < int1; ++int2) {
			this.values[int2] = doubleArray[int2];
		}
	}

	public final double norm() {
		double double1 = 0.0;
		for (int int1 = 0; int1 < this.length; ++int1) {
			double1 += this.values[int1] * this.values[int1];
		}

		return Math.sqrt(double1);
	}

	public final double normSquared() {
		double double1 = 0.0;
		for (int int1 = 0; int1 < this.length; ++int1) {
			double1 += this.values[int1] * this.values[int1];
		}

		return double1;
	}

	public final void normalize(GVector gVector) {
		double double1 = 0.0;
		if (this.length != gVector.length) {
			throw new MismatchedSizeException(VecMathI18N.getString("GVector0"));
		} else {
			int int1;
			for (int1 = 0; int1 < this.length; ++int1) {
				double1 += gVector.values[int1] * gVector.values[int1];
			}

			double double2 = 1.0 / Math.sqrt(double1);
			for (int1 = 0; int1 < this.length; ++int1) {
				this.values[int1] = gVector.values[int1] * double2;
			}
		}
	}

	public final void normalize() {
		double double1 = 0.0;
		int int1;
		for (int1 = 0; int1 < this.length; ++int1) {
			double1 += this.values[int1] * this.values[int1];
		}

		double double2 = 1.0 / Math.sqrt(double1);
		for (int1 = 0; int1 < this.length; ++int1) {
			this.values[int1] *= double2;
		}
	}

	public final void scale(double double1, GVector gVector) {
		if (this.length != gVector.length) {
			throw new MismatchedSizeException(VecMathI18N.getString("GVector1"));
		} else {
			for (int int1 = 0; int1 < this.length; ++int1) {
				this.values[int1] = gVector.values[int1] * double1;
			}
		}
	}

	public final void scale(double double1) {
		for (int int1 = 0; int1 < this.length; ++int1) {
			this.values[int1] *= double1;
		}
	}

	public final void scaleAdd(double double1, GVector gVector, GVector gVector2) {
		if (gVector2.length != gVector.length) {
			throw new MismatchedSizeException(VecMathI18N.getString("GVector2"));
		} else if (this.length != gVector.length) {
			throw new MismatchedSizeException(VecMathI18N.getString("GVector3"));
		} else {
			for (int int1 = 0; int1 < this.length; ++int1) {
				this.values[int1] = gVector.values[int1] * double1 + gVector2.values[int1];
			}
		}
	}

	public final void add(GVector gVector) {
		if (this.length != gVector.length) {
			throw new MismatchedSizeException(VecMathI18N.getString("GVector4"));
		} else {
			for (int int1 = 0; int1 < this.length; ++int1) {
				double[] doubleArray = this.values;
				doubleArray[int1] += gVector.values[int1];
			}
		}
	}

	public final void add(GVector gVector, GVector gVector2) {
		if (gVector.length != gVector2.length) {
			throw new MismatchedSizeException(VecMathI18N.getString("GVector5"));
		} else if (this.length != gVector.length) {
			throw new MismatchedSizeException(VecMathI18N.getString("GVector6"));
		} else {
			for (int int1 = 0; int1 < this.length; ++int1) {
				this.values[int1] = gVector.values[int1] + gVector2.values[int1];
			}
		}
	}

	public final void sub(GVector gVector) {
		if (this.length != gVector.length) {
			throw new MismatchedSizeException(VecMathI18N.getString("GVector7"));
		} else {
			for (int int1 = 0; int1 < this.length; ++int1) {
				double[] doubleArray = this.values;
				doubleArray[int1] -= gVector.values[int1];
			}
		}
	}

	public final void sub(GVector gVector, GVector gVector2) {
		if (gVector.length != gVector2.length) {
			throw new MismatchedSizeException(VecMathI18N.getString("GVector8"));
		} else if (this.length != gVector.length) {
			throw new MismatchedSizeException(VecMathI18N.getString("GVector9"));
		} else {
			for (int int1 = 0; int1 < this.length; ++int1) {
				this.values[int1] = gVector.values[int1] - gVector2.values[int1];
			}
		}
	}

	public final void mul(GMatrix gMatrix, GVector gVector) {
		if (gMatrix.getNumCol() != gVector.length) {
			throw new MismatchedSizeException(VecMathI18N.getString("GVector10"));
		} else if (this.length != gMatrix.getNumRow()) {
			throw new MismatchedSizeException(VecMathI18N.getString("GVector11"));
		} else {
			double[] doubleArray;
			if (gVector != this) {
				doubleArray = gVector.values;
			} else {
				doubleArray = (double[])((double[])this.values.clone());
			}

			for (int int1 = this.length - 1; int1 >= 0; --int1) {
				this.values[int1] = 0.0;
				for (int int2 = gVector.length - 1; int2 >= 0; --int2) {
					double[] doubleArray2 = this.values;
					doubleArray2[int1] += gMatrix.values[int1][int2] * doubleArray[int2];
				}
			}
		}
	}

	public final void mul(GVector gVector, GMatrix gMatrix) {
		if (gMatrix.getNumRow() != gVector.length) {
			throw new MismatchedSizeException(VecMathI18N.getString("GVector12"));
		} else if (this.length != gMatrix.getNumCol()) {
			throw new MismatchedSizeException(VecMathI18N.getString("GVector13"));
		} else {
			double[] doubleArray;
			if (gVector != this) {
				doubleArray = gVector.values;
			} else {
				doubleArray = (double[])((double[])this.values.clone());
			}

			for (int int1 = this.length - 1; int1 >= 0; --int1) {
				this.values[int1] = 0.0;
				for (int int2 = gVector.length - 1; int2 >= 0; --int2) {
					double[] doubleArray2 = this.values;
					doubleArray2[int1] += gMatrix.values[int2][int1] * doubleArray[int2];
				}
			}
		}
	}

	public final void negate() {
		for (int int1 = this.length - 1; int1 >= 0; --int1) {
			double[] doubleArray = this.values;
			doubleArray[int1] *= -1.0;
		}
	}

	public final void zero() {
		for (int int1 = 0; int1 < this.length; ++int1) {
			this.values[int1] = 0.0;
		}
	}

	public final void setSize(int int1) {
		double[] doubleArray = new double[int1];
		int int2;
		if (this.length < int1) {
			int2 = this.length;
		} else {
			int2 = int1;
		}

		for (int int3 = 0; int3 < int2; ++int3) {
			doubleArray[int3] = this.values[int3];
		}

		this.length = int1;
		this.values = doubleArray;
	}

	public final void set(double[] doubleArray) {
		for (int int1 = this.length - 1; int1 >= 0; --int1) {
			this.values[int1] = doubleArray[int1];
		}
	}

	public final void set(GVector gVector) {
		int int1;
		if (this.length < gVector.length) {
			this.length = gVector.length;
			this.values = new double[this.length];
			for (int1 = 0; int1 < this.length; ++int1) {
				this.values[int1] = gVector.values[int1];
			}
		} else {
			for (int1 = 0; int1 < gVector.length; ++int1) {
				this.values[int1] = gVector.values[int1];
			}

			for (int1 = gVector.length; int1 < this.length; ++int1) {
				this.values[int1] = 0.0;
			}
		}
	}

	public final void set(Tuple2f tuple2f) {
		if (this.length < 2) {
			this.length = 2;
			this.values = new double[2];
		}

		this.values[0] = (double)tuple2f.x;
		this.values[1] = (double)tuple2f.y;
		for (int int1 = 2; int1 < this.length; ++int1) {
			this.values[int1] = 0.0;
		}
	}

	public final void set(Tuple3f tuple3f) {
		if (this.length < 3) {
			this.length = 3;
			this.values = new double[3];
		}

		this.values[0] = (double)tuple3f.x;
		this.values[1] = (double)tuple3f.y;
		this.values[2] = (double)tuple3f.z;
		for (int int1 = 3; int1 < this.length; ++int1) {
			this.values[int1] = 0.0;
		}
	}

	public final void set(Tuple3d tuple3d) {
		if (this.length < 3) {
			this.length = 3;
			this.values = new double[3];
		}

		this.values[0] = tuple3d.x;
		this.values[1] = tuple3d.y;
		this.values[2] = tuple3d.z;
		for (int int1 = 3; int1 < this.length; ++int1) {
			this.values[int1] = 0.0;
		}
	}

	public final void set(Tuple4f tuple4f) {
		if (this.length < 4) {
			this.length = 4;
			this.values = new double[4];
		}

		this.values[0] = (double)tuple4f.x;
		this.values[1] = (double)tuple4f.y;
		this.values[2] = (double)tuple4f.z;
		this.values[3] = (double)tuple4f.w;
		for (int int1 = 4; int1 < this.length; ++int1) {
			this.values[int1] = 0.0;
		}
	}

	public final void set(Tuple4d tuple4d) {
		if (this.length < 4) {
			this.length = 4;
			this.values = new double[4];
		}

		this.values[0] = tuple4d.x;
		this.values[1] = tuple4d.y;
		this.values[2] = tuple4d.z;
		this.values[3] = tuple4d.w;
		for (int int1 = 4; int1 < this.length; ++int1) {
			this.values[int1] = 0.0;
		}
	}

	public final int getSize() {
		return this.values.length;
	}

	public final double getElement(int int1) {
		return this.values[int1];
	}

	public final void setElement(int int1, double double1) {
		this.values[int1] = double1;
	}

	public String toString() {
		StringBuffer stringBuffer = new StringBuffer(this.length * 8);
		for (int int1 = 0; int1 < this.length; ++int1) {
			stringBuffer.append(this.values[int1]).append(" ");
		}

		return stringBuffer.toString();
	}

	public int hashCode() {
		long long1 = 1L;
		for (int int1 = 0; int1 < this.length; ++int1) {
			long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.values[int1]);
		}

		return (int)(long1 ^ long1 >> 32);
	}

	public boolean equals(GVector gVector) {
		try {
			if (this.length != gVector.length) {
				return false;
			} else {
				for (int int1 = 0; int1 < this.length; ++int1) {
					if (this.values[int1] != gVector.values[int1]) {
						return false;
					}
				}

				return true;
			}
		} catch (NullPointerException nullPointerException) {
			return false;
		}
	}

	public boolean equals(Object object) {
		try {
			GVector gVector = (GVector)object;
			if (this.length != gVector.length) {
				return false;
			} else {
				for (int int1 = 0; int1 < this.length; ++int1) {
					if (this.values[int1] != gVector.values[int1]) {
						return false;
					}
				}

				return true;
			}
		} catch (ClassCastException classCastException) {
			return false;
		} catch (NullPointerException nullPointerException) {
			return false;
		}
	}

	public boolean epsilonEquals(GVector gVector, double double1) {
		if (this.length != gVector.length) {
			return false;
		} else {
			for (int int1 = 0; int1 < this.length; ++int1) {
				double double2 = this.values[int1] - gVector.values[int1];
				if ((double2 < 0.0 ? -double2 : double2) > double1) {
					return false;
				}
			}

			return true;
		}
	}

	public final double dot(GVector gVector) {
		if (this.length != gVector.length) {
			throw new MismatchedSizeException(VecMathI18N.getString("GVector14"));
		} else {
			double double1 = 0.0;
			for (int int1 = 0; int1 < this.length; ++int1) {
				double1 += this.values[int1] * gVector.values[int1];
			}

			return double1;
		}
	}

	public final void SVDBackSolve(GMatrix gMatrix, GMatrix gMatrix2, GMatrix gMatrix3, GVector gVector) {
		if (gMatrix.nRow == gVector.getSize() && gMatrix.nRow == gMatrix.nCol && gMatrix.nRow == gMatrix2.nRow) {
			if (gMatrix2.nCol == this.values.length && gMatrix2.nCol == gMatrix3.nCol && gMatrix2.nCol == gMatrix3.nRow) {
				GMatrix gMatrix4 = new GMatrix(gMatrix.nRow, gMatrix2.nCol);
				gMatrix4.mul(gMatrix, gMatrix3);
				gMatrix4.mulTransposeRight(gMatrix, gMatrix2);
				gMatrix4.invert();
				this.mul(gMatrix4, gVector);
			} else {
				throw new MismatchedSizeException(VecMathI18N.getString("GVector23"));
			}
		} else {
			throw new MismatchedSizeException(VecMathI18N.getString("GVector15"));
		}
	}

	public final void LUDBackSolve(GMatrix gMatrix, GVector gVector, GVector gVector2) {
		int int1 = gMatrix.nRow * gMatrix.nCol;
		double[] doubleArray = new double[int1];
		double[] doubleArray2 = new double[int1];
		int[] intArray = new int[gVector.getSize()];
		if (gMatrix.nRow != gVector.getSize()) {
			throw new MismatchedSizeException(VecMathI18N.getString("GVector16"));
		} else if (gMatrix.nRow != gVector2.getSize()) {
			throw new MismatchedSizeException(VecMathI18N.getString("GVector24"));
		} else if (gMatrix.nRow != gMatrix.nCol) {
			throw new MismatchedSizeException(VecMathI18N.getString("GVector25"));
		} else {
			int int2;
			for (int2 = 0; int2 < gMatrix.nRow; ++int2) {
				for (int int3 = 0; int3 < gMatrix.nCol; ++int3) {
					doubleArray[int2 * gMatrix.nCol + int3] = gMatrix.values[int2][int3];
				}
			}

			for (int2 = 0; int2 < int1; ++int2) {
				doubleArray2[int2] = 0.0;
			}

			for (int2 = 0; int2 < gMatrix.nRow; ++int2) {
				doubleArray2[int2 * gMatrix.nCol] = gVector.values[int2];
			}

			for (int2 = 0; int2 < gMatrix.nCol; ++int2) {
				intArray[int2] = (int)gVector2.values[int2];
			}

			GMatrix.luBacksubstitution(gMatrix.nRow, doubleArray, intArray, doubleArray2);
			for (int2 = 0; int2 < gMatrix.nRow; ++int2) {
				this.values[int2] = doubleArray2[int2 * gMatrix.nCol];
			}
		}
	}

	public final double angle(GVector gVector) {
		return Math.acos(this.dot(gVector) / (this.norm() * gVector.norm()));
	}

	public final void interpolate(GVector gVector, GVector gVector2, float float1) {
		this.interpolate(gVector, gVector2, (double)float1);
	}

	public final void interpolate(GVector gVector, float float1) {
		this.interpolate(gVector, (double)float1);
	}

	public final void interpolate(GVector gVector, GVector gVector2, double double1) {
		if (gVector2.length != gVector.length) {
			throw new MismatchedSizeException(VecMathI18N.getString("GVector20"));
		} else if (this.length != gVector.length) {
			throw new MismatchedSizeException(VecMathI18N.getString("GVector21"));
		} else {
			for (int int1 = 0; int1 < this.length; ++int1) {
				this.values[int1] = (1.0 - double1) * gVector.values[int1] + double1 * gVector2.values[int1];
			}
		}
	}

	public final void interpolate(GVector gVector, double double1) {
		if (gVector.length != this.length) {
			throw new MismatchedSizeException(VecMathI18N.getString("GVector22"));
		} else {
			for (int int1 = 0; int1 < this.length; ++int1) {
				this.values[int1] = (1.0 - double1) * this.values[int1] + double1 * gVector.values[int1];
			}
		}
	}

	public Object clone() {
		GVector gVector = null;
		try {
			gVector = (GVector)super.clone();
		} catch (CloneNotSupportedException cloneNotSupportedException) {
			throw new InternalError();
		}

		gVector.values = new double[this.length];
		for (int int1 = 0; int1 < this.length; ++int1) {
			gVector.values[int1] = this.values[int1];
		}

		return gVector;
	}
}
