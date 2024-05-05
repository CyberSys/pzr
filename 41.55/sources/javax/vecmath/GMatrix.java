package javax.vecmath;

import java.io.Serializable;


public class GMatrix implements Serializable,Cloneable {
	static final long serialVersionUID = 2777097312029690941L;
	private static final boolean debug = false;
	int nRow;
	int nCol;
	double[][] values;
	private static final double EPS = 1.0E-10;

	public GMatrix(int int1, int int2) {
		this.values = new double[int1][int2];
		this.nRow = int1;
		this.nCol = int2;
		int int3;
		for (int3 = 0; int3 < int1; ++int3) {
			for (int int4 = 0; int4 < int2; ++int4) {
				this.values[int3][int4] = 0.0;
			}
		}

		int int5;
		if (int1 < int2) {
			int5 = int1;
		} else {
			int5 = int2;
		}

		for (int3 = 0; int3 < int5; ++int3) {
			this.values[int3][int3] = 1.0;
		}
	}

	public GMatrix(int int1, int int2, double[] doubleArray) {
		this.values = new double[int1][int2];
		this.nRow = int1;
		this.nCol = int2;
		for (int int3 = 0; int3 < int1; ++int3) {
			for (int int4 = 0; int4 < int2; ++int4) {
				this.values[int3][int4] = doubleArray[int3 * int2 + int4];
			}
		}
	}

	public GMatrix(GMatrix gMatrix) {
		this.nRow = gMatrix.nRow;
		this.nCol = gMatrix.nCol;
		this.values = new double[this.nRow][this.nCol];
		for (int int1 = 0; int1 < this.nRow; ++int1) {
			for (int int2 = 0; int2 < this.nCol; ++int2) {
				this.values[int1][int2] = gMatrix.values[int1][int2];
			}
		}
	}

	public final void mul(GMatrix gMatrix) {
		if (this.nCol == gMatrix.nRow && this.nCol == gMatrix.nCol) {
			double[][] doubleArrayArray = new double[this.nRow][this.nCol];
			for (int int1 = 0; int1 < this.nRow; ++int1) {
				for (int int2 = 0; int2 < this.nCol; ++int2) {
					doubleArrayArray[int1][int2] = 0.0;
					for (int int3 = 0; int3 < this.nCol; ++int3) {
						doubleArrayArray[int1][int2] += this.values[int1][int3] * gMatrix.values[int3][int2];
					}
				}
			}

			this.values = doubleArrayArray;
		} else {
			throw new MismatchedSizeException(VecMathI18N.getString("GMatrix0"));
		}
	}

	public final void mul(GMatrix gMatrix, GMatrix gMatrix2) {
		if (gMatrix.nCol == gMatrix2.nRow && this.nRow == gMatrix.nRow && this.nCol == gMatrix2.nCol) {
			double[][] doubleArrayArray = new double[this.nRow][this.nCol];
			for (int int1 = 0; int1 < gMatrix.nRow; ++int1) {
				for (int int2 = 0; int2 < gMatrix2.nCol; ++int2) {
					doubleArrayArray[int1][int2] = 0.0;
					for (int int3 = 0; int3 < gMatrix.nCol; ++int3) {
						doubleArrayArray[int1][int2] += gMatrix.values[int1][int3] * gMatrix2.values[int3][int2];
					}
				}
			}

			this.values = doubleArrayArray;
		} else {
			throw new MismatchedSizeException(VecMathI18N.getString("GMatrix1"));
		}
	}

	public final void mul(GVector gVector, GVector gVector2) {
		if (this.nRow < gVector.getSize()) {
			throw new MismatchedSizeException(VecMathI18N.getString("GMatrix2"));
		} else if (this.nCol < gVector2.getSize()) {
			throw new MismatchedSizeException(VecMathI18N.getString("GMatrix3"));
		} else {
			for (int int1 = 0; int1 < gVector.getSize(); ++int1) {
				for (int int2 = 0; int2 < gVector2.getSize(); ++int2) {
					this.values[int1][int2] = gVector.values[int1] * gVector2.values[int2];
				}
			}
		}
	}

	public final void add(GMatrix gMatrix) {
		if (this.nRow != gMatrix.nRow) {
			throw new MismatchedSizeException(VecMathI18N.getString("GMatrix4"));
		} else if (this.nCol != gMatrix.nCol) {
			throw new MismatchedSizeException(VecMathI18N.getString("GMatrix5"));
		} else {
			for (int int1 = 0; int1 < this.nRow; ++int1) {
				for (int int2 = 0; int2 < this.nCol; ++int2) {
					this.values[int1][int2] += gMatrix.values[int1][int2];
				}
			}
		}
	}

	public final void add(GMatrix gMatrix, GMatrix gMatrix2) {
		if (gMatrix2.nRow != gMatrix.nRow) {
			throw new MismatchedSizeException(VecMathI18N.getString("GMatrix6"));
		} else if (gMatrix2.nCol != gMatrix.nCol) {
			throw new MismatchedSizeException(VecMathI18N.getString("GMatrix7"));
		} else if (this.nCol == gMatrix.nCol && this.nRow == gMatrix.nRow) {
			for (int int1 = 0; int1 < this.nRow; ++int1) {
				for (int int2 = 0; int2 < this.nCol; ++int2) {
					this.values[int1][int2] = gMatrix.values[int1][int2] + gMatrix2.values[int1][int2];
				}
			}
		} else {
			throw new MismatchedSizeException(VecMathI18N.getString("GMatrix8"));
		}
	}

	public final void sub(GMatrix gMatrix) {
		if (this.nRow != gMatrix.nRow) {
			throw new MismatchedSizeException(VecMathI18N.getString("GMatrix9"));
		} else if (this.nCol != gMatrix.nCol) {
			throw new MismatchedSizeException(VecMathI18N.getString("GMatrix28"));
		} else {
			for (int int1 = 0; int1 < this.nRow; ++int1) {
				for (int int2 = 0; int2 < this.nCol; ++int2) {
					this.values[int1][int2] -= gMatrix.values[int1][int2];
				}
			}
		}
	}

	public final void sub(GMatrix gMatrix, GMatrix gMatrix2) {
		if (gMatrix2.nRow != gMatrix.nRow) {
			throw new MismatchedSizeException(VecMathI18N.getString("GMatrix10"));
		} else if (gMatrix2.nCol != gMatrix.nCol) {
			throw new MismatchedSizeException(VecMathI18N.getString("GMatrix11"));
		} else if (this.nRow == gMatrix.nRow && this.nCol == gMatrix.nCol) {
			for (int int1 = 0; int1 < this.nRow; ++int1) {
				for (int int2 = 0; int2 < this.nCol; ++int2) {
					this.values[int1][int2] = gMatrix.values[int1][int2] - gMatrix2.values[int1][int2];
				}
			}
		} else {
			throw new MismatchedSizeException(VecMathI18N.getString("GMatrix12"));
		}
	}

	public final void negate() {
		for (int int1 = 0; int1 < this.nRow; ++int1) {
			for (int int2 = 0; int2 < this.nCol; ++int2) {
				this.values[int1][int2] = -this.values[int1][int2];
			}
		}
	}

	public final void negate(GMatrix gMatrix) {
		if (this.nRow == gMatrix.nRow && this.nCol == gMatrix.nCol) {
			for (int int1 = 0; int1 < this.nRow; ++int1) {
				for (int int2 = 0; int2 < this.nCol; ++int2) {
					this.values[int1][int2] = -gMatrix.values[int1][int2];
				}
			}
		} else {
			throw new MismatchedSizeException(VecMathI18N.getString("GMatrix13"));
		}
	}

	public final void setIdentity() {
		int int1;
		for (int1 = 0; int1 < this.nRow; ++int1) {
			for (int int2 = 0; int2 < this.nCol; ++int2) {
				this.values[int1][int2] = 0.0;
			}
		}

		int int3;
		if (this.nRow < this.nCol) {
			int3 = this.nRow;
		} else {
			int3 = this.nCol;
		}

		for (int1 = 0; int1 < int3; ++int1) {
			this.values[int1][int1] = 1.0;
		}
	}

	public final void setZero() {
		for (int int1 = 0; int1 < this.nRow; ++int1) {
			for (int int2 = 0; int2 < this.nCol; ++int2) {
				this.values[int1][int2] = 0.0;
			}
		}
	}

	public final void identityMinus() {
		int int1;
		for (int1 = 0; int1 < this.nRow; ++int1) {
			for (int int2 = 0; int2 < this.nCol; ++int2) {
				this.values[int1][int2] = -this.values[int1][int2];
			}
		}

		int int3;
		if (this.nRow < this.nCol) {
			int3 = this.nRow;
		} else {
			int3 = this.nCol;
		}

		for (int1 = 0; int1 < int3; ++int1) {
			int int4 = this.values[int1][int1]++;
		}
	}

	public final void invert() {
		this.invertGeneral(this);
	}

	public final void invert(GMatrix gMatrix) {
		this.invertGeneral(gMatrix);
	}

	public final void copySubMatrix(int int1, int int2, int int3, int int4, int int5, int int6, GMatrix gMatrix) {
		int int7;
		int int8;
		if (this != gMatrix) {
			for (int7 = 0; int7 < int3; ++int7) {
				for (int8 = 0; int8 < int4; ++int8) {
					gMatrix.values[int5 + int7][int6 + int8] = this.values[int1 + int7][int2 + int8];
				}
			}
		} else {
			double[][] doubleArrayArray = new double[int3][int4];
			for (int7 = 0; int7 < int3; ++int7) {
				for (int8 = 0; int8 < int4; ++int8) {
					doubleArrayArray[int7][int8] = this.values[int1 + int7][int2 + int8];
				}
			}

			for (int7 = 0; int7 < int3; ++int7) {
				for (int8 = 0; int8 < int4; ++int8) {
					gMatrix.values[int5 + int7][int6 + int8] = doubleArrayArray[int7][int8];
				}
			}
		}
	}

	public final void setSize(int int1, int int2) {
		double[][] doubleArrayArray = new double[int1][int2];
		int int3;
		if (this.nRow < int1) {
			int3 = this.nRow;
		} else {
			int3 = int1;
		}

		int int4;
		if (this.nCol < int2) {
			int4 = this.nCol;
		} else {
			int4 = int2;
		}

		for (int int5 = 0; int5 < int3; ++int5) {
			for (int int6 = 0; int6 < int4; ++int6) {
				doubleArrayArray[int5][int6] = this.values[int5][int6];
			}
		}

		this.nRow = int1;
		this.nCol = int2;
		this.values = doubleArrayArray;
	}

	public final void set(double[] doubleArray) {
		for (int int1 = 0; int1 < this.nRow; ++int1) {
			for (int int2 = 0; int2 < this.nCol; ++int2) {
				this.values[int1][int2] = doubleArray[this.nCol * int1 + int2];
			}
		}
	}

	public final void set(Matrix3f matrix3f) {
		if (this.nCol < 3 || this.nRow < 3) {
			this.nCol = 3;
			this.nRow = 3;
			this.values = new double[this.nRow][this.nCol];
		}

		this.values[0][0] = (double)matrix3f.m00;
		this.values[0][1] = (double)matrix3f.m01;
		this.values[0][2] = (double)matrix3f.m02;
		this.values[1][0] = (double)matrix3f.m10;
		this.values[1][1] = (double)matrix3f.m11;
		this.values[1][2] = (double)matrix3f.m12;
		this.values[2][0] = (double)matrix3f.m20;
		this.values[2][1] = (double)matrix3f.m21;
		this.values[2][2] = (double)matrix3f.m22;
		for (int int1 = 3; int1 < this.nRow; ++int1) {
			for (int int2 = 3; int2 < this.nCol; ++int2) {
				this.values[int1][int2] = 0.0;
			}
		}
	}

	public final void set(Matrix3d matrix3d) {
		if (this.nRow < 3 || this.nCol < 3) {
			this.values = new double[3][3];
			this.nRow = 3;
			this.nCol = 3;
		}

		this.values[0][0] = matrix3d.m00;
		this.values[0][1] = matrix3d.m01;
		this.values[0][2] = matrix3d.m02;
		this.values[1][0] = matrix3d.m10;
		this.values[1][1] = matrix3d.m11;
		this.values[1][2] = matrix3d.m12;
		this.values[2][0] = matrix3d.m20;
		this.values[2][1] = matrix3d.m21;
		this.values[2][2] = matrix3d.m22;
		for (int int1 = 3; int1 < this.nRow; ++int1) {
			for (int int2 = 3; int2 < this.nCol; ++int2) {
				this.values[int1][int2] = 0.0;
			}
		}
	}

	public final void set(Matrix4f matrix4f) {
		if (this.nRow < 4 || this.nCol < 4) {
			this.values = new double[4][4];
			this.nRow = 4;
			this.nCol = 4;
		}

		this.values[0][0] = (double)matrix4f.m00;
		this.values[0][1] = (double)matrix4f.m01;
		this.values[0][2] = (double)matrix4f.m02;
		this.values[0][3] = (double)matrix4f.m03;
		this.values[1][0] = (double)matrix4f.m10;
		this.values[1][1] = (double)matrix4f.m11;
		this.values[1][2] = (double)matrix4f.m12;
		this.values[1][3] = (double)matrix4f.m13;
		this.values[2][0] = (double)matrix4f.m20;
		this.values[2][1] = (double)matrix4f.m21;
		this.values[2][2] = (double)matrix4f.m22;
		this.values[2][3] = (double)matrix4f.m23;
		this.values[3][0] = (double)matrix4f.m30;
		this.values[3][1] = (double)matrix4f.m31;
		this.values[3][2] = (double)matrix4f.m32;
		this.values[3][3] = (double)matrix4f.m33;
		for (int int1 = 4; int1 < this.nRow; ++int1) {
			for (int int2 = 4; int2 < this.nCol; ++int2) {
				this.values[int1][int2] = 0.0;
			}
		}
	}

	public final void set(Matrix4d matrix4d) {
		if (this.nRow < 4 || this.nCol < 4) {
			this.values = new double[4][4];
			this.nRow = 4;
			this.nCol = 4;
		}

		this.values[0][0] = matrix4d.m00;
		this.values[0][1] = matrix4d.m01;
		this.values[0][2] = matrix4d.m02;
		this.values[0][3] = matrix4d.m03;
		this.values[1][0] = matrix4d.m10;
		this.values[1][1] = matrix4d.m11;
		this.values[1][2] = matrix4d.m12;
		this.values[1][3] = matrix4d.m13;
		this.values[2][0] = matrix4d.m20;
		this.values[2][1] = matrix4d.m21;
		this.values[2][2] = matrix4d.m22;
		this.values[2][3] = matrix4d.m23;
		this.values[3][0] = matrix4d.m30;
		this.values[3][1] = matrix4d.m31;
		this.values[3][2] = matrix4d.m32;
		this.values[3][3] = matrix4d.m33;
		for (int int1 = 4; int1 < this.nRow; ++int1) {
			for (int int2 = 4; int2 < this.nCol; ++int2) {
				this.values[int1][int2] = 0.0;
			}
		}
	}

	public final void set(GMatrix gMatrix) {
		if (this.nRow < gMatrix.nRow || this.nCol < gMatrix.nCol) {
			this.nRow = gMatrix.nRow;
			this.nCol = gMatrix.nCol;
			this.values = new double[this.nRow][this.nCol];
		}

		int int1;
		int int2;
		for (int1 = 0; int1 < Math.min(this.nRow, gMatrix.nRow); ++int1) {
			for (int2 = 0; int2 < Math.min(this.nCol, gMatrix.nCol); ++int2) {
				this.values[int1][int2] = gMatrix.values[int1][int2];
			}
		}

		for (int1 = gMatrix.nRow; int1 < this.nRow; ++int1) {
			for (int2 = gMatrix.nCol; int2 < this.nCol; ++int2) {
				this.values[int1][int2] = 0.0;
			}
		}
	}

	public final int getNumRow() {
		return this.nRow;
	}

	public final int getNumCol() {
		return this.nCol;
	}

	public final double getElement(int int1, int int2) {
		return this.values[int1][int2];
	}

	public final void setElement(int int1, int int2, double double1) {
		this.values[int1][int2] = double1;
	}

	public final void getRow(int int1, double[] doubleArray) {
		for (int int2 = 0; int2 < this.nCol; ++int2) {
			doubleArray[int2] = this.values[int1][int2];
		}
	}

	public final void getRow(int int1, GVector gVector) {
		if (gVector.getSize() < this.nCol) {
			gVector.setSize(this.nCol);
		}

		for (int int2 = 0; int2 < this.nCol; ++int2) {
			gVector.values[int2] = this.values[int1][int2];
		}
	}

	public final void getColumn(int int1, double[] doubleArray) {
		for (int int2 = 0; int2 < this.nRow; ++int2) {
			doubleArray[int2] = this.values[int2][int1];
		}
	}

	public final void getColumn(int int1, GVector gVector) {
		if (gVector.getSize() < this.nRow) {
			gVector.setSize(this.nRow);
		}

		for (int int2 = 0; int2 < this.nRow; ++int2) {
			gVector.values[int2] = this.values[int2][int1];
		}
	}

	public final void get(Matrix3d matrix3d) {
		if (this.nRow >= 3 && this.nCol >= 3) {
			matrix3d.m00 = this.values[0][0];
			matrix3d.m01 = this.values[0][1];
			matrix3d.m02 = this.values[0][2];
			matrix3d.m10 = this.values[1][0];
			matrix3d.m11 = this.values[1][1];
			matrix3d.m12 = this.values[1][2];
			matrix3d.m20 = this.values[2][0];
			matrix3d.m21 = this.values[2][1];
			matrix3d.m22 = this.values[2][2];
		} else {
			matrix3d.setZero();
			if (this.nCol > 0) {
				if (this.nRow > 0) {
					matrix3d.m00 = this.values[0][0];
					if (this.nRow > 1) {
						matrix3d.m10 = this.values[1][0];
						if (this.nRow > 2) {
							matrix3d.m20 = this.values[2][0];
						}
					}
				}

				if (this.nCol > 1) {
					if (this.nRow > 0) {
						matrix3d.m01 = this.values[0][1];
						if (this.nRow > 1) {
							matrix3d.m11 = this.values[1][1];
							if (this.nRow > 2) {
								matrix3d.m21 = this.values[2][1];
							}
						}
					}

					if (this.nCol > 2 && this.nRow > 0) {
						matrix3d.m02 = this.values[0][2];
						if (this.nRow > 1) {
							matrix3d.m12 = this.values[1][2];
							if (this.nRow > 2) {
								matrix3d.m22 = this.values[2][2];
							}
						}
					}
				}
			}
		}
	}

	public final void get(Matrix3f matrix3f) {
		if (this.nRow >= 3 && this.nCol >= 3) {
			matrix3f.m00 = (float)this.values[0][0];
			matrix3f.m01 = (float)this.values[0][1];
			matrix3f.m02 = (float)this.values[0][2];
			matrix3f.m10 = (float)this.values[1][0];
			matrix3f.m11 = (float)this.values[1][1];
			matrix3f.m12 = (float)this.values[1][2];
			matrix3f.m20 = (float)this.values[2][0];
			matrix3f.m21 = (float)this.values[2][1];
			matrix3f.m22 = (float)this.values[2][2];
		} else {
			matrix3f.setZero();
			if (this.nCol > 0) {
				if (this.nRow > 0) {
					matrix3f.m00 = (float)this.values[0][0];
					if (this.nRow > 1) {
						matrix3f.m10 = (float)this.values[1][0];
						if (this.nRow > 2) {
							matrix3f.m20 = (float)this.values[2][0];
						}
					}
				}

				if (this.nCol > 1) {
					if (this.nRow > 0) {
						matrix3f.m01 = (float)this.values[0][1];
						if (this.nRow > 1) {
							matrix3f.m11 = (float)this.values[1][1];
							if (this.nRow > 2) {
								matrix3f.m21 = (float)this.values[2][1];
							}
						}
					}

					if (this.nCol > 2 && this.nRow > 0) {
						matrix3f.m02 = (float)this.values[0][2];
						if (this.nRow > 1) {
							matrix3f.m12 = (float)this.values[1][2];
							if (this.nRow > 2) {
								matrix3f.m22 = (float)this.values[2][2];
							}
						}
					}
				}
			}
		}
	}

	public final void get(Matrix4d matrix4d) {
		if (this.nRow >= 4 && this.nCol >= 4) {
			matrix4d.m00 = this.values[0][0];
			matrix4d.m01 = this.values[0][1];
			matrix4d.m02 = this.values[0][2];
			matrix4d.m03 = this.values[0][3];
			matrix4d.m10 = this.values[1][0];
			matrix4d.m11 = this.values[1][1];
			matrix4d.m12 = this.values[1][2];
			matrix4d.m13 = this.values[1][3];
			matrix4d.m20 = this.values[2][0];
			matrix4d.m21 = this.values[2][1];
			matrix4d.m22 = this.values[2][2];
			matrix4d.m23 = this.values[2][3];
			matrix4d.m30 = this.values[3][0];
			matrix4d.m31 = this.values[3][1];
			matrix4d.m32 = this.values[3][2];
			matrix4d.m33 = this.values[3][3];
		} else {
			matrix4d.setZero();
			if (this.nCol > 0) {
				if (this.nRow > 0) {
					matrix4d.m00 = this.values[0][0];
					if (this.nRow > 1) {
						matrix4d.m10 = this.values[1][0];
						if (this.nRow > 2) {
							matrix4d.m20 = this.values[2][0];
							if (this.nRow > 3) {
								matrix4d.m30 = this.values[3][0];
							}
						}
					}
				}

				if (this.nCol > 1) {
					if (this.nRow > 0) {
						matrix4d.m01 = this.values[0][1];
						if (this.nRow > 1) {
							matrix4d.m11 = this.values[1][1];
							if (this.nRow > 2) {
								matrix4d.m21 = this.values[2][1];
								if (this.nRow > 3) {
									matrix4d.m31 = this.values[3][1];
								}
							}
						}
					}

					if (this.nCol > 2) {
						if (this.nRow > 0) {
							matrix4d.m02 = this.values[0][2];
							if (this.nRow > 1) {
								matrix4d.m12 = this.values[1][2];
								if (this.nRow > 2) {
									matrix4d.m22 = this.values[2][2];
									if (this.nRow > 3) {
										matrix4d.m32 = this.values[3][2];
									}
								}
							}
						}

						if (this.nCol > 3 && this.nRow > 0) {
							matrix4d.m03 = this.values[0][3];
							if (this.nRow > 1) {
								matrix4d.m13 = this.values[1][3];
								if (this.nRow > 2) {
									matrix4d.m23 = this.values[2][3];
									if (this.nRow > 3) {
										matrix4d.m33 = this.values[3][3];
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public final void get(Matrix4f matrix4f) {
		if (this.nRow >= 4 && this.nCol >= 4) {
			matrix4f.m00 = (float)this.values[0][0];
			matrix4f.m01 = (float)this.values[0][1];
			matrix4f.m02 = (float)this.values[0][2];
			matrix4f.m03 = (float)this.values[0][3];
			matrix4f.m10 = (float)this.values[1][0];
			matrix4f.m11 = (float)this.values[1][1];
			matrix4f.m12 = (float)this.values[1][2];
			matrix4f.m13 = (float)this.values[1][3];
			matrix4f.m20 = (float)this.values[2][0];
			matrix4f.m21 = (float)this.values[2][1];
			matrix4f.m22 = (float)this.values[2][2];
			matrix4f.m23 = (float)this.values[2][3];
			matrix4f.m30 = (float)this.values[3][0];
			matrix4f.m31 = (float)this.values[3][1];
			matrix4f.m32 = (float)this.values[3][2];
			matrix4f.m33 = (float)this.values[3][3];
		} else {
			matrix4f.setZero();
			if (this.nCol > 0) {
				if (this.nRow > 0) {
					matrix4f.m00 = (float)this.values[0][0];
					if (this.nRow > 1) {
						matrix4f.m10 = (float)this.values[1][0];
						if (this.nRow > 2) {
							matrix4f.m20 = (float)this.values[2][0];
							if (this.nRow > 3) {
								matrix4f.m30 = (float)this.values[3][0];
							}
						}
					}
				}

				if (this.nCol > 1) {
					if (this.nRow > 0) {
						matrix4f.m01 = (float)this.values[0][1];
						if (this.nRow > 1) {
							matrix4f.m11 = (float)this.values[1][1];
							if (this.nRow > 2) {
								matrix4f.m21 = (float)this.values[2][1];
								if (this.nRow > 3) {
									matrix4f.m31 = (float)this.values[3][1];
								}
							}
						}
					}

					if (this.nCol > 2) {
						if (this.nRow > 0) {
							matrix4f.m02 = (float)this.values[0][2];
							if (this.nRow > 1) {
								matrix4f.m12 = (float)this.values[1][2];
								if (this.nRow > 2) {
									matrix4f.m22 = (float)this.values[2][2];
									if (this.nRow > 3) {
										matrix4f.m32 = (float)this.values[3][2];
									}
								}
							}
						}

						if (this.nCol > 3 && this.nRow > 0) {
							matrix4f.m03 = (float)this.values[0][3];
							if (this.nRow > 1) {
								matrix4f.m13 = (float)this.values[1][3];
								if (this.nRow > 2) {
									matrix4f.m23 = (float)this.values[2][3];
									if (this.nRow > 3) {
										matrix4f.m33 = (float)this.values[3][3];
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public final void get(GMatrix gMatrix) {
		int int1;
		if (this.nCol < gMatrix.nCol) {
			int1 = this.nCol;
		} else {
			int1 = gMatrix.nCol;
		}

		int int2;
		if (this.nRow < gMatrix.nRow) {
			int2 = this.nRow;
		} else {
			int2 = gMatrix.nRow;
		}

		int int3;
		int int4;
		for (int3 = 0; int3 < int2; ++int3) {
			for (int4 = 0; int4 < int1; ++int4) {
				gMatrix.values[int3][int4] = this.values[int3][int4];
			}
		}

		for (int3 = int2; int3 < gMatrix.nRow; ++int3) {
			for (int4 = 0; int4 < gMatrix.nCol; ++int4) {
				gMatrix.values[int3][int4] = 0.0;
			}
		}

		for (int4 = int1; int4 < gMatrix.nCol; ++int4) {
			for (int3 = 0; int3 < int2; ++int3) {
				gMatrix.values[int3][int4] = 0.0;
			}
		}
	}

	public final void setRow(int int1, double[] doubleArray) {
		for (int int2 = 0; int2 < this.nCol; ++int2) {
			this.values[int1][int2] = doubleArray[int2];
		}
	}

	public final void setRow(int int1, GVector gVector) {
		for (int int2 = 0; int2 < this.nCol; ++int2) {
			this.values[int1][int2] = gVector.values[int2];
		}
	}

	public final void setColumn(int int1, double[] doubleArray) {
		for (int int2 = 0; int2 < this.nRow; ++int2) {
			this.values[int2][int1] = doubleArray[int2];
		}
	}

	public final void setColumn(int int1, GVector gVector) {
		for (int int2 = 0; int2 < this.nRow; ++int2) {
			this.values[int2][int1] = gVector.values[int2];
		}
	}

	public final void mulTransposeBoth(GMatrix gMatrix, GMatrix gMatrix2) {
		if (gMatrix.nRow == gMatrix2.nCol && this.nRow == gMatrix.nCol && this.nCol == gMatrix2.nRow) {
			int int1;
			int int2;
			int int3;
			if (gMatrix != this && gMatrix2 != this) {
				for (int1 = 0; int1 < this.nRow; ++int1) {
					for (int2 = 0; int2 < this.nCol; ++int2) {
						this.values[int1][int2] = 0.0;
						for (int3 = 0; int3 < gMatrix.nRow; ++int3) {
							double[] doubleArray = this.values[int1];
							doubleArray[int2] += gMatrix.values[int3][int1] * gMatrix2.values[int2][int3];
						}
					}
				}
			} else {
				double[][] doubleArrayArray = new double[this.nRow][this.nCol];
				for (int1 = 0; int1 < this.nRow; ++int1) {
					for (int2 = 0; int2 < this.nCol; ++int2) {
						doubleArrayArray[int1][int2] = 0.0;
						for (int3 = 0; int3 < gMatrix.nRow; ++int3) {
							doubleArrayArray[int1][int2] += gMatrix.values[int3][int1] * gMatrix2.values[int2][int3];
						}
					}
				}

				this.values = doubleArrayArray;
			}
		} else {
			throw new MismatchedSizeException(VecMathI18N.getString("GMatrix14"));
		}
	}

	public final void mulTransposeRight(GMatrix gMatrix, GMatrix gMatrix2) {
		if (gMatrix.nCol == gMatrix2.nCol && this.nCol == gMatrix2.nRow && this.nRow == gMatrix.nRow) {
			int int1;
			int int2;
			int int3;
			if (gMatrix != this && gMatrix2 != this) {
				for (int1 = 0; int1 < this.nRow; ++int1) {
					for (int2 = 0; int2 < this.nCol; ++int2) {
						this.values[int1][int2] = 0.0;
						for (int3 = 0; int3 < gMatrix.nCol; ++int3) {
							double[] doubleArray = this.values[int1];
							doubleArray[int2] += gMatrix.values[int1][int3] * gMatrix2.values[int2][int3];
						}
					}
				}
			} else {
				double[][] doubleArrayArray = new double[this.nRow][this.nCol];
				for (int1 = 0; int1 < this.nRow; ++int1) {
					for (int2 = 0; int2 < this.nCol; ++int2) {
						doubleArrayArray[int1][int2] = 0.0;
						for (int3 = 0; int3 < gMatrix.nCol; ++int3) {
							doubleArrayArray[int1][int2] += gMatrix.values[int1][int3] * gMatrix2.values[int2][int3];
						}
					}
				}

				this.values = doubleArrayArray;
			}
		} else {
			throw new MismatchedSizeException(VecMathI18N.getString("GMatrix15"));
		}
	}

	public final void mulTransposeLeft(GMatrix gMatrix, GMatrix gMatrix2) {
		if (gMatrix.nRow == gMatrix2.nRow && this.nCol == gMatrix2.nCol && this.nRow == gMatrix.nCol) {
			int int1;
			int int2;
			int int3;
			if (gMatrix != this && gMatrix2 != this) {
				for (int1 = 0; int1 < this.nRow; ++int1) {
					for (int2 = 0; int2 < this.nCol; ++int2) {
						this.values[int1][int2] = 0.0;
						for (int3 = 0; int3 < gMatrix.nRow; ++int3) {
							double[] doubleArray = this.values[int1];
							doubleArray[int2] += gMatrix.values[int3][int1] * gMatrix2.values[int3][int2];
						}
					}
				}
			} else {
				double[][] doubleArrayArray = new double[this.nRow][this.nCol];
				for (int1 = 0; int1 < this.nRow; ++int1) {
					for (int2 = 0; int2 < this.nCol; ++int2) {
						doubleArrayArray[int1][int2] = 0.0;
						for (int3 = 0; int3 < gMatrix.nRow; ++int3) {
							doubleArrayArray[int1][int2] += gMatrix.values[int3][int1] * gMatrix2.values[int3][int2];
						}
					}
				}

				this.values = doubleArrayArray;
			}
		} else {
			throw new MismatchedSizeException(VecMathI18N.getString("GMatrix16"));
		}
	}

	public final void transpose() {
		int int1;
		int int2;
		if (this.nRow != this.nCol) {
			int1 = this.nRow;
			this.nRow = this.nCol;
			this.nCol = int1;
			double[][] doubleArrayArray = new double[this.nRow][this.nCol];
			for (int1 = 0; int1 < this.nRow; ++int1) {
				for (int2 = 0; int2 < this.nCol; ++int2) {
					doubleArrayArray[int1][int2] = this.values[int2][int1];
				}
			}

			this.values = doubleArrayArray;
		} else {
			for (int1 = 0; int1 < this.nRow; ++int1) {
				for (int2 = 0; int2 < int1; ++int2) {
					double double1 = this.values[int1][int2];
					this.values[int1][int2] = this.values[int2][int1];
					this.values[int2][int1] = double1;
				}
			}
		}
	}

	public final void transpose(GMatrix gMatrix) {
		if (this.nRow == gMatrix.nCol && this.nCol == gMatrix.nRow) {
			if (gMatrix != this) {
				for (int int1 = 0; int1 < this.nRow; ++int1) {
					for (int int2 = 0; int2 < this.nCol; ++int2) {
						this.values[int1][int2] = gMatrix.values[int2][int1];
					}
				}
			} else {
				this.transpose();
			}
		} else {
			throw new MismatchedSizeException(VecMathI18N.getString("GMatrix17"));
		}
	}

	public String toString() {
		StringBuffer stringBuffer = new StringBuffer(this.nRow * this.nCol * 8);
		for (int int1 = 0; int1 < this.nRow; ++int1) {
			for (int int2 = 0; int2 < this.nCol; ++int2) {
				stringBuffer.append(this.values[int1][int2]).append(" ");
			}

			stringBuffer.append("\n");
		}

		return stringBuffer.toString();
	}

	private static void checkMatrix(GMatrix gMatrix) {
		for (int int1 = 0; int1 < gMatrix.nRow; ++int1) {
			for (int int2 = 0; int2 < gMatrix.nCol; ++int2) {
				if (Math.abs(gMatrix.values[int1][int2]) < 1.0E-10) {
					System.out.print(" 0.0	 ");
				} else {
					System.out.print(" " + gMatrix.values[int1][int2]);
				}
			}

			System.out.print("\n");
		}
	}

	public int hashCode() {
		long long1 = 1L;
		long1 = 31L * long1 + (long)this.nRow;
		long1 = 31L * long1 + (long)this.nCol;
		for (int int1 = 0; int1 < this.nRow; ++int1) {
			for (int int2 = 0; int2 < this.nCol; ++int2) {
				long1 = 31L * long1 + VecMathUtil.doubleToLongBits(this.values[int1][int2]);
			}
		}

		return (int)(long1 ^ long1 >> 32);
	}

	public boolean equals(GMatrix gMatrix) {
		try {
			if (this.nRow == gMatrix.nRow && this.nCol == gMatrix.nCol) {
				for (int int1 = 0; int1 < this.nRow; ++int1) {
					for (int int2 = 0; int2 < this.nCol; ++int2) {
						if (this.values[int1][int2] != gMatrix.values[int1][int2]) {
							return false;
						}
					}
				}

				return true;
			} else {
				return false;
			}
		} catch (NullPointerException nullPointerException) {
			return false;
		}
	}

	public boolean equals(Object object) {
		try {
			GMatrix gMatrix = (GMatrix)object;
			if (this.nRow == gMatrix.nRow && this.nCol == gMatrix.nCol) {
				for (int int1 = 0; int1 < this.nRow; ++int1) {
					for (int int2 = 0; int2 < this.nCol; ++int2) {
						if (this.values[int1][int2] != gMatrix.values[int1][int2]) {
							return false;
						}
					}
				}

				return true;
			} else {
				return false;
			}
		} catch (ClassCastException classCastException) {
			return false;
		} catch (NullPointerException nullPointerException) {
			return false;
		}
	}

	public boolean epsilonEquals(GMatrix gMatrix, float float1) {
		return this.epsilonEquals(gMatrix, (double)float1);
	}

	public boolean epsilonEquals(GMatrix gMatrix, double double1) {
		if (this.nRow == gMatrix.nRow && this.nCol == gMatrix.nCol) {
			for (int int1 = 0; int1 < this.nRow; ++int1) {
				for (int int2 = 0; int2 < this.nCol; ++int2) {
					double double2 = this.values[int1][int2] - gMatrix.values[int1][int2];
					if ((double2 < 0.0 ? -double2 : double2) > double1) {
						return false;
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}

	public final double trace() {
		int int1;
		if (this.nRow < this.nCol) {
			int1 = this.nRow;
		} else {
			int1 = this.nCol;
		}

		double double1 = 0.0;
		for (int int2 = 0; int2 < int1; ++int2) {
			double1 += this.values[int2][int2];
		}

		return double1;
	}

	public final int SVD(GMatrix gMatrix, GMatrix gMatrix2, GMatrix gMatrix3) {
		if (this.nCol == gMatrix3.nCol && this.nCol == gMatrix3.nRow) {
			if (this.nRow == gMatrix.nRow && this.nRow == gMatrix.nCol) {
				if (this.nRow == gMatrix2.nRow && this.nCol == gMatrix2.nCol) {
					if (this.nRow == 2 && this.nCol == 2 && this.values[1][0] == 0.0) {
						gMatrix.setIdentity();
						gMatrix3.setIdentity();
						if (this.values[0][1] == 0.0) {
							return 2;
						} else {
							double[] doubleArray = new double[1];
							double[] doubleArray2 = new double[1];
							double[] doubleArray3 = new double[1];
							double[] doubleArray4 = new double[1];
							double[] doubleArray5 = new double[]{this.values[0][0], this.values[1][1]};
							compute_2X2(this.values[0][0], this.values[0][1], this.values[1][1], doubleArray5, doubleArray, doubleArray3, doubleArray2, doubleArray4, 0);
							update_u(0, gMatrix, doubleArray3, doubleArray);
							update_v(0, gMatrix3, doubleArray4, doubleArray2);
							return 2;
						}
					} else {
						return computeSVD(this, gMatrix, gMatrix2, gMatrix3);
					}
				} else {
					throw new MismatchedSizeException(VecMathI18N.getString("GMatrix26"));
				}
			} else {
				throw new MismatchedSizeException(VecMathI18N.getString("GMatrix25"));
			}
		} else {
			throw new MismatchedSizeException(VecMathI18N.getString("GMatrix18"));
		}
	}

	public final int LUD(GMatrix gMatrix, GVector gVector) {
		int int1 = gMatrix.nRow * gMatrix.nCol;
		double[] doubleArray = new double[int1];
		int[] intArray = new int[1];
		int[] intArray2 = new int[gMatrix.nRow];
		if (this.nRow != this.nCol) {
			throw new MismatchedSizeException(VecMathI18N.getString("GMatrix19"));
		} else if (this.nRow != gMatrix.nRow) {
			throw new MismatchedSizeException(VecMathI18N.getString("GMatrix27"));
		} else if (this.nCol != gMatrix.nCol) {
			throw new MismatchedSizeException(VecMathI18N.getString("GMatrix27"));
		} else if (gMatrix.nRow != gVector.getSize()) {
			throw new MismatchedSizeException(VecMathI18N.getString("GMatrix20"));
		} else {
			int int2;
			int int3;
			for (int2 = 0; int2 < this.nRow; ++int2) {
				for (int3 = 0; int3 < this.nCol; ++int3) {
					doubleArray[int2 * this.nCol + int3] = this.values[int2][int3];
				}
			}

			if (!luDecomposition(gMatrix.nRow, doubleArray, intArray2, intArray)) {
				throw new SingularMatrixException(VecMathI18N.getString("GMatrix21"));
			} else {
				for (int2 = 0; int2 < this.nRow; ++int2) {
					for (int3 = 0; int3 < this.nCol; ++int3) {
						gMatrix.values[int2][int3] = doubleArray[int2 * this.nCol + int3];
					}
				}

				for (int2 = 0; int2 < gMatrix.nRow; ++int2) {
					gVector.values[int2] = (double)intArray2[int2];
				}

				return intArray[0];
			}
		}
	}

	public final void setScale(double double1) {
		int int1;
		if (this.nRow < this.nCol) {
			int1 = this.nRow;
		} else {
			int1 = this.nCol;
		}

		int int2;
		for (int2 = 0; int2 < this.nRow; ++int2) {
			for (int int3 = 0; int3 < this.nCol; ++int3) {
				this.values[int2][int3] = 0.0;
			}
		}

		for (int2 = 0; int2 < int1; ++int2) {
			this.values[int2][int2] = double1;
		}
	}

	final void invertGeneral(GMatrix gMatrix) {
		int int1 = gMatrix.nRow * gMatrix.nCol;
		double[] doubleArray = new double[int1];
		double[] doubleArray2 = new double[int1];
		int[] intArray = new int[gMatrix.nRow];
		int[] intArray2 = new int[1];
		if (gMatrix.nRow != gMatrix.nCol) {
			throw new MismatchedSizeException(VecMathI18N.getString("GMatrix22"));
		} else {
			int int2;
			int int3;
			for (int2 = 0; int2 < this.nRow; ++int2) {
				for (int3 = 0; int3 < this.nCol; ++int3) {
					doubleArray[int2 * this.nCol + int3] = gMatrix.values[int2][int3];
				}
			}

			if (!luDecomposition(gMatrix.nRow, doubleArray, intArray, intArray2)) {
				throw new SingularMatrixException(VecMathI18N.getString("GMatrix21"));
			} else {
				for (int2 = 0; int2 < int1; ++int2) {
					doubleArray2[int2] = 0.0;
				}

				for (int2 = 0; int2 < this.nCol; ++int2) {
					doubleArray2[int2 + int2 * this.nCol] = 1.0;
				}

				luBacksubstitution(gMatrix.nRow, doubleArray, intArray, doubleArray2);
				for (int2 = 0; int2 < this.nRow; ++int2) {
					for (int3 = 0; int3 < this.nCol; ++int3) {
						this.values[int2][int3] = doubleArray2[int2 * this.nCol + int3];
					}
				}
			}
		}
	}

	static boolean luDecomposition(int int1, double[] doubleArray, int[] intArray, int[] intArray2) {
		double[] doubleArray2 = new double[int1];
		int int2 = 0;
		int int3 = 0;
		intArray2[0] = 1;
		int int4;
		int int5;
		double double1;
		double double2;
		for (int4 = int1; int4-- != 0; doubleArray2[int3++] = 1.0 / double1) {
			double1 = 0.0;
			int5 = int1;
			while (int5-- != 0) {
				double2 = doubleArray[int2++];
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
		for (int5 = 0; int5 < int1; ++int5) {
			int int6;
			int int7;
			int int8;
			int int9;
			double double3;
			for (int4 = 0; int4 < int5; ++int4) {
				int7 = byte1 + int1 * int4 + int5;
				double3 = doubleArray[int7];
				int6 = int4;
				int8 = byte1 + int1 * int4;
				for (int9 = byte1 + int5; int6-- != 0; int9 += int1) {
					double3 -= doubleArray[int8] * doubleArray[int9];
					++int8;
				}

				doubleArray[int7] = double3;
			}

			double1 = 0.0;
			int int10 = -1;
			for (int4 = int5; int4 < int1; ++int4) {
				int7 = byte1 + int1 * int4 + int5;
				double3 = doubleArray[int7];
				int6 = int5;
				int8 = byte1 + int1 * int4;
				for (int9 = byte1 + int5; int6-- != 0; int9 += int1) {
					double3 -= doubleArray[int8] * doubleArray[int9];
					++int8;
				}

				doubleArray[int7] = double3;
				if ((double2 = doubleArray2[int4] * Math.abs(double3)) >= double1) {
					double1 = double2;
					int10 = int4;
				}
			}

			if (int10 < 0) {
				throw new RuntimeException(VecMathI18N.getString("GMatrix24"));
			}

			if (int5 != int10) {
				int6 = int1;
				int8 = byte1 + int1 * int10;
				for (int9 = byte1 + int1 * int5; int6-- != 0; doubleArray[int9++] = double2) {
					double2 = doubleArray[int8];
					doubleArray[int8++] = doubleArray[int9];
				}

				doubleArray2[int10] = doubleArray2[int5];
				intArray2[0] = -intArray2[0];
			}

			intArray[int5] = int10;
			if (doubleArray[byte1 + int1 * int5 + int5] == 0.0) {
				return false;
			}

			if (int5 != int1 - 1) {
				double2 = 1.0 / doubleArray[byte1 + int1 * int5 + int5];
				int7 = byte1 + int1 * (int5 + 1) + int5;
				for (int4 = int1 - 1 - int5; int4-- != 0; int7 += int1) {
					doubleArray[int7] *= double2;
				}
			}
		}

		return true;
	}

	static void luBacksubstitution(int int1, double[] doubleArray, int[] intArray, double[] doubleArray2) {
		byte byte1 = 0;
		for (int int2 = 0; int2 < int1; ++int2) {
			int int3 = int2;
			int int4 = -1;
			int int5;
			int int6;
			int int7;
			for (int5 = 0; int5 < int1; ++int5) {
				int int8 = intArray[byte1 + int5];
				double double1 = doubleArray2[int3 + int1 * int8];
				doubleArray2[int3 + int1 * int8] = doubleArray2[int3 + int1 * int5];
				if (int4 >= 0) {
					int7 = int5 * int1;
					for (int6 = int4; int6 <= int5 - 1; ++int6) {
						double1 -= doubleArray[int7 + int6] * doubleArray2[int3 + int1 * int6];
					}
				} else if (double1 != 0.0) {
					int4 = int5;
				}

				doubleArray2[int3 + int1 * int5] = double1;
			}

			for (int5 = 0; int5 < int1; ++int5) {
				int int9 = int1 - 1 - int5;
				int7 = int1 * int9;
				double double2 = 0.0;
				for (int6 = 1; int6 <= int5; ++int6) {
					double2 += doubleArray[int7 + int1 - int6] * doubleArray2[int3 + int1 * (int1 - int6)];
				}

				doubleArray2[int3 + int1 * int9] = (doubleArray2[int3 + int1 * int9] - double2) / doubleArray[int7 + int9];
			}
		}
	}

	static int computeSVD(GMatrix gMatrix, GMatrix gMatrix2, GMatrix gMatrix3, GMatrix gMatrix4) {
		GMatrix gMatrix5 = new GMatrix(gMatrix.nRow, gMatrix.nCol);
		GMatrix gMatrix6 = new GMatrix(gMatrix.nRow, gMatrix.nCol);
		GMatrix gMatrix7 = new GMatrix(gMatrix.nRow, gMatrix.nCol);
		GMatrix gMatrix8 = new GMatrix(gMatrix);
		int int1;
		int int2;
		if (gMatrix8.nRow >= gMatrix8.nCol) {
			int2 = gMatrix8.nCol;
			int1 = gMatrix8.nCol - 1;
		} else {
			int2 = gMatrix8.nRow;
			int1 = gMatrix8.nRow;
		}

		int int3;
		if (gMatrix8.nRow > gMatrix8.nCol) {
			int3 = gMatrix8.nRow;
		} else {
			int3 = gMatrix8.nCol;
		}

		double[] doubleArray = new double[int3];
		double[] doubleArray2 = new double[int2];
		double[] doubleArray3 = new double[int1];
		boolean boolean1 = false;
		gMatrix2.setIdentity();
		gMatrix4.setIdentity();
		int int4 = gMatrix8.nRow;
		int int5 = gMatrix8.nCol;
		int int6;
		for (int int7 = 0; int7 < int2; ++int7) {
			int int8;
			int int9;
			double[] doubleArray4;
			double double1;
			int int10;
			double double2;
			double double3;
			if (int4 > 1) {
				double1 = 0.0;
				for (int6 = 0; int6 < int4; ++int6) {
					double1 += gMatrix8.values[int6 + int7][int7] * gMatrix8.values[int6 + int7][int7];
				}

				double1 = Math.sqrt(double1);
				if (gMatrix8.values[int7][int7] == 0.0) {
					doubleArray[0] = double1;
				} else {
					doubleArray[0] = gMatrix8.values[int7][int7] + d_sign(double1, gMatrix8.values[int7][int7]);
				}

				for (int6 = 1; int6 < int4; ++int6) {
					doubleArray[int6] = gMatrix8.values[int7 + int6][int7];
				}

				double2 = 0.0;
				for (int6 = 0; int6 < int4; ++int6) {
					double2 += doubleArray[int6] * doubleArray[int6];
				}

				double2 = 2.0 / double2;
				int8 = int7;
				while (true) {
					if (int8 >= gMatrix8.nRow) {
						for (int6 = int7; int6 < gMatrix8.nRow; ++int6) {
							int10 = gMatrix6.values[int6][int6]++;
						}

						double3 = 0.0;
						for (int6 = int7; int6 < gMatrix8.nRow; ++int6) {
							double3 += gMatrix6.values[int7][int6] * gMatrix8.values[int6][int7];
						}

						gMatrix8.values[int7][int7] = double3;
						for (int8 = int7; int8 < gMatrix8.nRow; ++int8) {
							for (int9 = int7 + 1; int9 < gMatrix8.nCol; ++int9) {
								gMatrix5.values[int8][int9] = 0.0;
								for (int6 = int7; int6 < gMatrix8.nCol; ++int6) {
									doubleArray4 = gMatrix5.values[int8];
									doubleArray4[int9] += gMatrix6.values[int8][int6] * gMatrix8.values[int6][int9];
								}
							}
						}

						for (int8 = int7; int8 < gMatrix8.nRow; ++int8) {
							for (int9 = int7 + 1; int9 < gMatrix8.nCol; ++int9) {
								gMatrix8.values[int8][int9] = gMatrix5.values[int8][int9];
							}
						}

						for (int8 = int7; int8 < gMatrix8.nRow; ++int8) {
							for (int9 = 0; int9 < gMatrix8.nCol; ++int9) {
								gMatrix5.values[int8][int9] = 0.0;
								for (int6 = int7; int6 < gMatrix8.nCol; ++int6) {
									doubleArray4 = gMatrix5.values[int8];
									doubleArray4[int9] += gMatrix6.values[int8][int6] * gMatrix2.values[int6][int9];
								}
							}
						}

						for (int8 = int7; int8 < gMatrix8.nRow; ++int8) {
							for (int9 = 0; int9 < gMatrix8.nCol; ++int9) {
								gMatrix2.values[int8][int9] = gMatrix5.values[int8][int9];
							}
						}

						--int4;
						break;
					}

					for (int9 = int7; int9 < gMatrix8.nRow; ++int9) {
						gMatrix6.values[int8][int9] = -double2 * doubleArray[int8 - int7] * doubleArray[int9 - int7];
					}

					++int8;
				}
			}

			if (int5 > 2) {
				double1 = 0.0;
				for (int6 = 1; int6 < int5; ++int6) {
					double1 += gMatrix8.values[int7][int7 + int6] * gMatrix8.values[int7][int7 + int6];
				}

				double1 = Math.sqrt(double1);
				if (gMatrix8.values[int7][int7 + 1] == 0.0) {
					doubleArray[0] = double1;
				} else {
					doubleArray[0] = gMatrix8.values[int7][int7 + 1] + d_sign(double1, gMatrix8.values[int7][int7 + 1]);
				}

				for (int6 = 1; int6 < int5 - 1; ++int6) {
					doubleArray[int6] = gMatrix8.values[int7][int7 + int6 + 1];
				}

				double2 = 0.0;
				for (int6 = 0; int6 < int5 - 1; ++int6) {
					double2 += doubleArray[int6] * doubleArray[int6];
				}

				double2 = 2.0 / double2;
				for (int8 = int7 + 1; int8 < int5; ++int8) {
					for (int9 = int7 + 1; int9 < gMatrix8.nCol; ++int9) {
						gMatrix7.values[int8][int9] = -double2 * doubleArray[int8 - int7 - 1] * doubleArray[int9 - int7 - 1];
					}
				}

				for (int6 = int7 + 1; int6 < gMatrix8.nCol; ++int6) {
					int10 = gMatrix7.values[int6][int6]++;
				}

				double3 = 0.0;
				for (int6 = int7; int6 < gMatrix8.nCol; ++int6) {
					double3 += gMatrix7.values[int6][int7 + 1] * gMatrix8.values[int7][int6];
				}

				gMatrix8.values[int7][int7 + 1] = double3;
				for (int8 = int7 + 1; int8 < gMatrix8.nRow; ++int8) {
					for (int9 = int7 + 1; int9 < gMatrix8.nCol; ++int9) {
						gMatrix5.values[int8][int9] = 0.0;
						for (int6 = int7 + 1; int6 < gMatrix8.nCol; ++int6) {
							doubleArray4 = gMatrix5.values[int8];
							doubleArray4[int9] += gMatrix7.values[int6][int9] * gMatrix8.values[int8][int6];
						}
					}
				}

				for (int8 = int7 + 1; int8 < gMatrix8.nRow; ++int8) {
					for (int9 = int7 + 1; int9 < gMatrix8.nCol; ++int9) {
						gMatrix8.values[int8][int9] = gMatrix5.values[int8][int9];
					}
				}

				for (int8 = 0; int8 < gMatrix8.nRow; ++int8) {
					for (int9 = int7 + 1; int9 < gMatrix8.nCol; ++int9) {
						gMatrix5.values[int8][int9] = 0.0;
						for (int6 = int7 + 1; int6 < gMatrix8.nCol; ++int6) {
							doubleArray4 = gMatrix5.values[int8];
							doubleArray4[int9] += gMatrix7.values[int6][int9] * gMatrix4.values[int8][int6];
						}
					}
				}

				for (int8 = 0; int8 < gMatrix8.nRow; ++int8) {
					for (int9 = int7 + 1; int9 < gMatrix8.nCol; ++int9) {
						gMatrix4.values[int8][int9] = gMatrix5.values[int8][int9];
					}
				}

				--int5;
			}
		}

		for (int6 = 0; int6 < int2; ++int6) {
			doubleArray2[int6] = gMatrix8.values[int6][int6];
		}

		for (int6 = 0; int6 < int1; ++int6) {
			doubleArray3[int6] = gMatrix8.values[int6][int6 + 1];
		}

		if (gMatrix8.nRow == 2 && gMatrix8.nCol == 2) {
			double[] doubleArray5 = new double[1];
			double[] doubleArray6 = new double[1];
			double[] doubleArray7 = new double[1];
			double[] doubleArray8 = new double[1];
			compute_2X2(doubleArray2[0], doubleArray3[0], doubleArray2[1], doubleArray2, doubleArray7, doubleArray5, doubleArray8, doubleArray6, 0);
			update_u(0, gMatrix2, doubleArray5, doubleArray7);
			update_v(0, gMatrix4, doubleArray6, doubleArray8);
			return 2;
		} else {
			compute_qr(0, doubleArray3.length - 1, doubleArray2, doubleArray3, gMatrix2, gMatrix4);
			int int11 = doubleArray2.length;
			return int11;
		}
	}

	static void compute_qr(int int1, int int2, double[] doubleArray, double[] doubleArray2, GMatrix gMatrix, GMatrix gMatrix2) {
		double[] doubleArray3 = new double[1];
		double[] doubleArray4 = new double[1];
		double[] doubleArray5 = new double[1];
		double[] doubleArray6 = new double[1];
		new GMatrix(gMatrix.nCol, gMatrix2.nRow);
		double double1 = 1.0;
		double double2 = -1.0;
		boolean boolean1 = false;
		double double3 = 0.0;
		double double4 = 0.0;
		for (int int3 = 0; int3 < 2 && !boolean1; ++int3) {
			int int4;
			for (int4 = int1; int4 <= int2; ++int4) {
				if (int4 == int1) {
					int int5;
					if (doubleArray2.length == doubleArray.length) {
						int5 = int2;
					} else {
						int5 = int2 + 1;
					}

					double double5 = compute_shift(doubleArray[int5 - 1], doubleArray2[int2], doubleArray[int5]);
					double3 = (Math.abs(doubleArray[int4]) - double5) * (d_sign(double1, doubleArray[int4]) + double5 / doubleArray[int4]);
					double4 = doubleArray2[int4];
				}

				double double6 = compute_rot(double3, double4, doubleArray6, doubleArray4);
				if (int4 != int1) {
					doubleArray2[int4 - 1] = double6;
				}

				double3 = doubleArray4[0] * doubleArray[int4] + doubleArray6[0] * doubleArray2[int4];
				doubleArray2[int4] = doubleArray4[0] * doubleArray2[int4] - doubleArray6[0] * doubleArray[int4];
				double4 = doubleArray6[0] * doubleArray[int4 + 1];
				doubleArray[int4 + 1] = doubleArray4[0] * doubleArray[int4 + 1];
				update_v(int4, gMatrix2, doubleArray4, doubleArray6);
				double6 = compute_rot(double3, double4, doubleArray5, doubleArray3);
				doubleArray[int4] = double6;
				double3 = doubleArray3[0] * doubleArray2[int4] + doubleArray5[0] * doubleArray[int4 + 1];
				doubleArray[int4 + 1] = doubleArray3[0] * doubleArray[int4 + 1] - doubleArray5[0] * doubleArray2[int4];
				if (int4 < int2) {
					double4 = doubleArray5[0] * doubleArray2[int4 + 1];
					doubleArray2[int4 + 1] = doubleArray3[0] * doubleArray2[int4 + 1];
				}

				update_u(int4, gMatrix, doubleArray3, doubleArray5);
			}

			if (doubleArray.length == doubleArray2.length) {
				compute_rot(double3, double4, doubleArray6, doubleArray4);
				double3 = doubleArray4[0] * doubleArray[int4] + doubleArray6[0] * doubleArray2[int4];
				doubleArray2[int4] = doubleArray4[0] * doubleArray2[int4] - doubleArray6[0] * doubleArray[int4];
				doubleArray[int4 + 1] = doubleArray4[0] * doubleArray[int4 + 1];
				update_v(int4, gMatrix2, doubleArray4, doubleArray6);
			}

			while (int2 - int1 > 1 && Math.abs(doubleArray2[int2]) < 4.89E-15) {
				--int2;
			}

			for (int int6 = int2 - 2; int6 > int1; --int6) {
				if (Math.abs(doubleArray2[int6]) < 4.89E-15) {
					compute_qr(int6 + 1, int2, doubleArray, doubleArray2, gMatrix, gMatrix2);
					for (int2 = int6 - 1; int2 - int1 > 1 && Math.abs(doubleArray2[int2]) < 4.89E-15; --int2) {
					}
				}
			}

			if (int2 - int1 <= 1 && Math.abs(doubleArray2[int1 + 1]) < 4.89E-15) {
				boolean1 = true;
			}
		}

		if (Math.abs(doubleArray2[1]) < 4.89E-15) {
			compute_2X2(doubleArray[int1], doubleArray2[int1], doubleArray[int1 + 1], doubleArray, doubleArray5, doubleArray3, doubleArray6, doubleArray4, 0);
			doubleArray2[int1] = 0.0;
			doubleArray2[int1 + 1] = 0.0;
		}

		update_u(int1, gMatrix, doubleArray3, doubleArray5);
		update_v(int1, gMatrix2, doubleArray4, doubleArray6);
	}

	private static void print_se(double[] doubleArray, double[] doubleArray2) {
		System.out.println("\ns =" + doubleArray[0] + " " + doubleArray[1] + " " + doubleArray[2]);
		System.out.println("e =" + doubleArray2[0] + " " + doubleArray2[1]);
	}

	private static void update_v(int int1, GMatrix gMatrix, double[] doubleArray, double[] doubleArray2) {
		for (int int2 = 0; int2 < gMatrix.nRow; ++int2) {
			double double1 = gMatrix.values[int2][int1];
			gMatrix.values[int2][int1] = doubleArray[0] * double1 + doubleArray2[0] * gMatrix.values[int2][int1 + 1];
			gMatrix.values[int2][int1 + 1] = -doubleArray2[0] * double1 + doubleArray[0] * gMatrix.values[int2][int1 + 1];
		}
	}

	private static void chase_up(double[] doubleArray, double[] doubleArray2, int int1, GMatrix gMatrix) {
		double[] doubleArray3 = new double[1];
		double[] doubleArray4 = new double[1];
		GMatrix gMatrix2 = new GMatrix(gMatrix.nRow, gMatrix.nCol);
		GMatrix gMatrix3 = new GMatrix(gMatrix.nRow, gMatrix.nCol);
		double double1 = doubleArray2[int1];
		double double2 = doubleArray[int1];
		int int2;
		for (int2 = int1; int2 > 0; --int2) {
			double double3 = compute_rot(double1, double2, doubleArray4, doubleArray3);
			double1 = -doubleArray2[int2 - 1] * doubleArray4[0];
			double2 = doubleArray[int2 - 1];
			doubleArray[int2] = double3;
			doubleArray2[int2 - 1] *= doubleArray3[0];
			update_v_split(int2, int1 + 1, gMatrix, doubleArray3, doubleArray4, gMatrix2, gMatrix3);
		}

		doubleArray[int2 + 1] = compute_rot(double1, double2, doubleArray4, doubleArray3);
		update_v_split(int2, int1 + 1, gMatrix, doubleArray3, doubleArray4, gMatrix2, gMatrix3);
	}

	private static void chase_across(double[] doubleArray, double[] doubleArray2, int int1, GMatrix gMatrix) {
		double[] doubleArray3 = new double[1];
		double[] doubleArray4 = new double[1];
		GMatrix gMatrix2 = new GMatrix(gMatrix.nRow, gMatrix.nCol);
		GMatrix gMatrix3 = new GMatrix(gMatrix.nRow, gMatrix.nCol);
		double double1 = doubleArray2[int1];
		double double2 = doubleArray[int1 + 1];
		int int2;
		for (int2 = int1; int2 < gMatrix.nCol - 2; ++int2) {
			double double3 = compute_rot(double2, double1, doubleArray4, doubleArray3);
			double1 = -doubleArray2[int2 + 1] * doubleArray4[0];
			double2 = doubleArray[int2 + 2];
			doubleArray[int2 + 1] = double3;
			doubleArray2[int2 + 1] *= doubleArray3[0];
			update_u_split(int1, int2 + 1, gMatrix, doubleArray3, doubleArray4, gMatrix2, gMatrix3);
		}

		doubleArray[int2 + 1] = compute_rot(double2, double1, doubleArray4, doubleArray3);
		update_u_split(int1, int2 + 1, gMatrix, doubleArray3, doubleArray4, gMatrix2, gMatrix3);
	}

	private static void update_v_split(int int1, int int2, GMatrix gMatrix, double[] doubleArray, double[] doubleArray2, GMatrix gMatrix2, GMatrix gMatrix3) {
		for (int int3 = 0; int3 < gMatrix.nRow; ++int3) {
			double double1 = gMatrix.values[int3][int1];
			gMatrix.values[int3][int1] = doubleArray[0] * double1 - doubleArray2[0] * gMatrix.values[int3][int2];
			gMatrix.values[int3][int2] = doubleArray2[0] * double1 + doubleArray[0] * gMatrix.values[int3][int2];
		}

		System.out.println("topr	=" + int1);
		System.out.println("bottomr =" + int2);
		System.out.println("cosr =" + doubleArray[0]);
		System.out.println("sinr =" + doubleArray2[0]);
		System.out.println("\nm =");
		checkMatrix(gMatrix3);
		System.out.println("\nv =");
		checkMatrix(gMatrix2);
		gMatrix3.mul(gMatrix3, gMatrix2);
		System.out.println("\nt*m =");
		checkMatrix(gMatrix3);
	}

	private static void update_u_split(int int1, int int2, GMatrix gMatrix, double[] doubleArray, double[] doubleArray2, GMatrix gMatrix2, GMatrix gMatrix3) {
		for (int int3 = 0; int3 < gMatrix.nCol; ++int3) {
			double double1 = gMatrix.values[int1][int3];
			gMatrix.values[int1][int3] = doubleArray[0] * double1 - doubleArray2[0] * gMatrix.values[int2][int3];
			gMatrix.values[int2][int3] = doubleArray2[0] * double1 + doubleArray[0] * gMatrix.values[int2][int3];
		}

		System.out.println("\nm=");
		checkMatrix(gMatrix3);
		System.out.println("\nu=");
		checkMatrix(gMatrix2);
		gMatrix3.mul(gMatrix2, gMatrix3);
		System.out.println("\nt*m=");
		checkMatrix(gMatrix3);
	}

	private static void update_u(int int1, GMatrix gMatrix, double[] doubleArray, double[] doubleArray2) {
		for (int int2 = 0; int2 < gMatrix.nCol; ++int2) {
			double double1 = gMatrix.values[int1][int2];
			gMatrix.values[int1][int2] = doubleArray[0] * double1 + doubleArray2[0] * gMatrix.values[int1 + 1][int2];
			gMatrix.values[int1 + 1][int2] = -doubleArray2[0] * double1 + doubleArray[0] * gMatrix.values[int1 + 1][int2];
		}
	}

	private static void print_m(GMatrix gMatrix, GMatrix gMatrix2, GMatrix gMatrix3) {
		GMatrix gMatrix4 = new GMatrix(gMatrix.nCol, gMatrix.nRow);
		gMatrix4.mul(gMatrix2, gMatrix4);
		gMatrix4.mul(gMatrix4, gMatrix3);
		System.out.println("\n m = \n" + toString(gMatrix4));
	}

	private static String toString(GMatrix gMatrix) {
		StringBuffer stringBuffer = new StringBuffer(gMatrix.nRow * gMatrix.nCol * 8);
		for (int int1 = 0; int1 < gMatrix.nRow; ++int1) {
			for (int int2 = 0; int2 < gMatrix.nCol; ++int2) {
				if (Math.abs(gMatrix.values[int1][int2]) < 1.0E-9) {
					stringBuffer.append("0.0000 ");
				} else {
					stringBuffer.append(gMatrix.values[int1][int2]).append(" ");
				}
			}

			stringBuffer.append("\n");
		}

		return stringBuffer.toString();
	}

	private static void print_svd(double[] doubleArray, double[] doubleArray2, GMatrix gMatrix, GMatrix gMatrix2) {
		GMatrix gMatrix3 = new GMatrix(gMatrix.nCol, gMatrix2.nRow);
		System.out.println(" \ns = ");
		int int1;
		for (int1 = 0; int1 < doubleArray.length; ++int1) {
			System.out.println(" " + doubleArray[int1]);
		}

		System.out.println(" \ne = ");
		for (int1 = 0; int1 < doubleArray2.length; ++int1) {
			System.out.println(" " + doubleArray2[int1]);
		}

		System.out.println(" \nu  = \n" + gMatrix.toString());
		System.out.println(" \nv  = \n" + gMatrix2.toString());
		gMatrix3.setIdentity();
		for (int1 = 0; int1 < doubleArray.length; ++int1) {
			gMatrix3.values[int1][int1] = doubleArray[int1];
		}

		for (int1 = 0; int1 < doubleArray2.length; ++int1) {
			gMatrix3.values[int1][int1 + 1] = doubleArray2[int1];
		}

		System.out.println(" \nm  = \n" + gMatrix3.toString());
		gMatrix3.mulTransposeLeft(gMatrix, gMatrix3);
		gMatrix3.mulTransposeRight(gMatrix3, gMatrix2);
		System.out.println(" \n u.transpose*m*v.transpose  = \n" + gMatrix3.toString());
	}

	static double max(double double1, double double2) {
		return double1 > double2 ? double1 : double2;
	}

	static double min(double double1, double double2) {
		return double1 < double2 ? double1 : double2;
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
				if (double14 / double18 < 1.0E-10) {
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
					if (double14 / double18 < 1.0E-10) {
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

	static double compute_rot(double double1, double double2, double[] doubleArray, double[] doubleArray2) {
		double double3;
		double double4;
		double double5;
		if (double2 == 0.0) {
			double4 = 1.0;
			double5 = 0.0;
			double3 = double1;
		} else if (double1 == 0.0) {
			double4 = 0.0;
			double5 = 1.0;
			double3 = double2;
		} else {
			double double6 = double1;
			double double7 = double2;
			double double8 = max(Math.abs(double1), Math.abs(double2));
			int int1;
			int int2;
			if (double8 >= 4.9947976805055876E145) {
				for (int1 = 0; double8 >= 4.9947976805055876E145; double8 = max(Math.abs(double6), Math.abs(double7))) {
					++int1;
					double6 *= 2.002083095183101E-146;
					double7 *= 2.002083095183101E-146;
				}

				double3 = Math.sqrt(double6 * double6 + double7 * double7);
				double4 = double6 / double3;
				double5 = double7 / double3;
				for (int2 = 1; int2 <= int1; ++int2) {
					double3 *= 4.9947976805055876E145;
				}
			} else if (!(double8 <= 2.002083095183101E-146)) {
				double3 = Math.sqrt(double1 * double1 + double2 * double2);
				double4 = double1 / double3;
				double5 = double2 / double3;
			} else {
				for (int1 = 0; double8 <= 2.002083095183101E-146; double8 = max(Math.abs(double6), Math.abs(double7))) {
					++int1;
					double6 *= 4.9947976805055876E145;
					double7 *= 4.9947976805055876E145;
				}

				double3 = Math.sqrt(double6 * double6 + double7 * double7);
				double4 = double6 / double3;
				double5 = double7 / double3;
				for (int2 = 1; int2 <= int1; ++int2) {
					double3 *= 2.002083095183101E-146;
				}
			}

			if (Math.abs(double1) > Math.abs(double2) && double4 < 0.0) {
				double4 = -double4;
				double5 = -double5;
				double3 = -double3;
			}
		}

		doubleArray[0] = double5;
		doubleArray2[0] = double4;
		return double3;
	}

	static double d_sign(double double1, double double2) {
		double double3 = double1 >= 0.0 ? double1 : -double1;
		return double2 >= 0.0 ? double3 : -double3;
	}

	public Object clone() {
		GMatrix gMatrix = null;
		try {
			gMatrix = (GMatrix)super.clone();
		} catch (CloneNotSupportedException cloneNotSupportedException) {
			throw new InternalError();
		}

		gMatrix.values = new double[this.nRow][this.nCol];
		for (int int1 = 0; int1 < this.nRow; ++int1) {
			for (int int2 = 0; int2 < this.nCol; ++int2) {
				gMatrix.values[int1][int2] = this.values[int1][int2];
			}
		}

		return gMatrix;
	}
}
