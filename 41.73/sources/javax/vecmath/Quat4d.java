package javax.vecmath;

import java.io.Serializable;


public class Quat4d extends Tuple4d implements Serializable {
	static final long serialVersionUID = 7577479888820201099L;
	static final double EPS = 1.0E-12;
	static final double EPS2 = 1.0E-30;
	static final double PIO2 = 1.57079632679;

	public Quat4d(double double1, double double2, double double3, double double4) {
		double double5 = 1.0 / Math.sqrt(double1 * double1 + double2 * double2 + double3 * double3 + double4 * double4);
		this.x = double1 * double5;
		this.y = double2 * double5;
		this.z = double3 * double5;
		this.w = double4 * double5;
	}

	public Quat4d(double[] doubleArray) {
		double double1 = 1.0 / Math.sqrt(doubleArray[0] * doubleArray[0] + doubleArray[1] * doubleArray[1] + doubleArray[2] * doubleArray[2] + doubleArray[3] * doubleArray[3]);
		this.x = doubleArray[0] * double1;
		this.y = doubleArray[1] * double1;
		this.z = doubleArray[2] * double1;
		this.w = doubleArray[3] * double1;
	}

	public Quat4d(Quat4d quat4d) {
		super((Tuple4d)quat4d);
	}

	public Quat4d(Quat4f quat4f) {
		super((Tuple4f)quat4f);
	}

	public Quat4d(Tuple4f tuple4f) {
		double double1 = 1.0 / Math.sqrt((double)(tuple4f.x * tuple4f.x + tuple4f.y * tuple4f.y + tuple4f.z * tuple4f.z + tuple4f.w * tuple4f.w));
		this.x = (double)tuple4f.x * double1;
		this.y = (double)tuple4f.y * double1;
		this.z = (double)tuple4f.z * double1;
		this.w = (double)tuple4f.w * double1;
	}

	public Quat4d(Tuple4d tuple4d) {
		double double1 = 1.0 / Math.sqrt(tuple4d.x * tuple4d.x + tuple4d.y * tuple4d.y + tuple4d.z * tuple4d.z + tuple4d.w * tuple4d.w);
		this.x = tuple4d.x * double1;
		this.y = tuple4d.y * double1;
		this.z = tuple4d.z * double1;
		this.w = tuple4d.w * double1;
	}

	public Quat4d() {
	}

	public final void conjugate(Quat4d quat4d) {
		this.x = -quat4d.x;
		this.y = -quat4d.y;
		this.z = -quat4d.z;
		this.w = quat4d.w;
	}

	public final void conjugate() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
	}

	public final void mul(Quat4d quat4d, Quat4d quat4d2) {
		if (this != quat4d && this != quat4d2) {
			this.w = quat4d.w * quat4d2.w - quat4d.x * quat4d2.x - quat4d.y * quat4d2.y - quat4d.z * quat4d2.z;
			this.x = quat4d.w * quat4d2.x + quat4d2.w * quat4d.x + quat4d.y * quat4d2.z - quat4d.z * quat4d2.y;
			this.y = quat4d.w * quat4d2.y + quat4d2.w * quat4d.y - quat4d.x * quat4d2.z + quat4d.z * quat4d2.x;
			this.z = quat4d.w * quat4d2.z + quat4d2.w * quat4d.z + quat4d.x * quat4d2.y - quat4d.y * quat4d2.x;
		} else {
			double double1 = quat4d.w * quat4d2.w - quat4d.x * quat4d2.x - quat4d.y * quat4d2.y - quat4d.z * quat4d2.z;
			double double2 = quat4d.w * quat4d2.x + quat4d2.w * quat4d.x + quat4d.y * quat4d2.z - quat4d.z * quat4d2.y;
			double double3 = quat4d.w * quat4d2.y + quat4d2.w * quat4d.y - quat4d.x * quat4d2.z + quat4d.z * quat4d2.x;
			this.z = quat4d.w * quat4d2.z + quat4d2.w * quat4d.z + quat4d.x * quat4d2.y - quat4d.y * quat4d2.x;
			this.w = double1;
			this.x = double2;
			this.y = double3;
		}
	}

	public final void mul(Quat4d quat4d) {
		double double1 = this.w * quat4d.w - this.x * quat4d.x - this.y * quat4d.y - this.z * quat4d.z;
		double double2 = this.w * quat4d.x + quat4d.w * this.x + this.y * quat4d.z - this.z * quat4d.y;
		double double3 = this.w * quat4d.y + quat4d.w * this.y - this.x * quat4d.z + this.z * quat4d.x;
		this.z = this.w * quat4d.z + quat4d.w * this.z + this.x * quat4d.y - this.y * quat4d.x;
		this.w = double1;
		this.x = double2;
		this.y = double3;
	}

	public final void mulInverse(Quat4d quat4d, Quat4d quat4d2) {
		Quat4d quat4d3 = new Quat4d(quat4d2);
		quat4d3.inverse();
		this.mul(quat4d, quat4d3);
	}

	public final void mulInverse(Quat4d quat4d) {
		Quat4d quat4d2 = new Quat4d(quat4d);
		quat4d2.inverse();
		this.mul(quat4d2);
	}

	public final void inverse(Quat4d quat4d) {
		double double1 = 1.0 / (quat4d.w * quat4d.w + quat4d.x * quat4d.x + quat4d.y * quat4d.y + quat4d.z * quat4d.z);
		this.w = double1 * quat4d.w;
		this.x = -double1 * quat4d.x;
		this.y = -double1 * quat4d.y;
		this.z = -double1 * quat4d.z;
	}

	public final void inverse() {
		double double1 = 1.0 / (this.w * this.w + this.x * this.x + this.y * this.y + this.z * this.z);
		this.w *= double1;
		this.x *= -double1;
		this.y *= -double1;
		this.z *= -double1;
	}

	public final void normalize(Quat4d quat4d) {
		double double1 = quat4d.x * quat4d.x + quat4d.y * quat4d.y + quat4d.z * quat4d.z + quat4d.w * quat4d.w;
		if (double1 > 0.0) {
			double1 = 1.0 / Math.sqrt(double1);
			this.x = double1 * quat4d.x;
			this.y = double1 * quat4d.y;
			this.z = double1 * quat4d.z;
			this.w = double1 * quat4d.w;
		} else {
			this.x = 0.0;
			this.y = 0.0;
			this.z = 0.0;
			this.w = 0.0;
		}
	}

	public final void normalize() {
		double double1 = this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
		if (double1 > 0.0) {
			double1 = 1.0 / Math.sqrt(double1);
			this.x *= double1;
			this.y *= double1;
			this.z *= double1;
			this.w *= double1;
		} else {
			this.x = 0.0;
			this.y = 0.0;
			this.z = 0.0;
			this.w = 0.0;
		}
	}

	public final void set(Matrix4f matrix4f) {
		double double1 = 0.25 * (double)(matrix4f.m00 + matrix4f.m11 + matrix4f.m22 + matrix4f.m33);
		if (double1 >= 0.0) {
			if (double1 >= 1.0E-30) {
				this.w = Math.sqrt(double1);
				double1 = 0.25 / this.w;
				this.x = (double)(matrix4f.m21 - matrix4f.m12) * double1;
				this.y = (double)(matrix4f.m02 - matrix4f.m20) * double1;
				this.z = (double)(matrix4f.m10 - matrix4f.m01) * double1;
			} else {
				this.w = 0.0;
				double1 = -0.5 * (double)(matrix4f.m11 + matrix4f.m22);
				if (double1 >= 0.0) {
					if (double1 >= 1.0E-30) {
						this.x = Math.sqrt(double1);
						double1 = 1.0 / (2.0 * this.x);
						this.y = (double)matrix4f.m10 * double1;
						this.z = (double)matrix4f.m20 * double1;
					} else {
						this.x = 0.0;
						double1 = 0.5 * (1.0 - (double)matrix4f.m22);
						if (double1 >= 1.0E-30) {
							this.y = Math.sqrt(double1);
							this.z = (double)matrix4f.m21 / (2.0 * this.y);
						} else {
							this.y = 0.0;
							this.z = 1.0;
						}
					}
				} else {
					this.x = 0.0;
					this.y = 0.0;
					this.z = 1.0;
				}
			}
		} else {
			this.w = 0.0;
			this.x = 0.0;
			this.y = 0.0;
			this.z = 1.0;
		}
	}

	public final void set(Matrix4d matrix4d) {
		double double1 = 0.25 * (matrix4d.m00 + matrix4d.m11 + matrix4d.m22 + matrix4d.m33);
		if (double1 >= 0.0) {
			if (double1 >= 1.0E-30) {
				this.w = Math.sqrt(double1);
				double1 = 0.25 / this.w;
				this.x = (matrix4d.m21 - matrix4d.m12) * double1;
				this.y = (matrix4d.m02 - matrix4d.m20) * double1;
				this.z = (matrix4d.m10 - matrix4d.m01) * double1;
			} else {
				this.w = 0.0;
				double1 = -0.5 * (matrix4d.m11 + matrix4d.m22);
				if (double1 >= 0.0) {
					if (double1 >= 1.0E-30) {
						this.x = Math.sqrt(double1);
						double1 = 0.5 / this.x;
						this.y = matrix4d.m10 * double1;
						this.z = matrix4d.m20 * double1;
					} else {
						this.x = 0.0;
						double1 = 0.5 * (1.0 - matrix4d.m22);
						if (double1 >= 1.0E-30) {
							this.y = Math.sqrt(double1);
							this.z = matrix4d.m21 / (2.0 * this.y);
						} else {
							this.y = 0.0;
							this.z = 1.0;
						}
					}
				} else {
					this.x = 0.0;
					this.y = 0.0;
					this.z = 1.0;
				}
			}
		} else {
			this.w = 0.0;
			this.x = 0.0;
			this.y = 0.0;
			this.z = 1.0;
		}
	}

	public final void set(Matrix3f matrix3f) {
		double double1 = 0.25 * ((double)(matrix3f.m00 + matrix3f.m11 + matrix3f.m22) + 1.0);
		if (double1 >= 0.0) {
			if (double1 >= 1.0E-30) {
				this.w = Math.sqrt(double1);
				double1 = 0.25 / this.w;
				this.x = (double)(matrix3f.m21 - matrix3f.m12) * double1;
				this.y = (double)(matrix3f.m02 - matrix3f.m20) * double1;
				this.z = (double)(matrix3f.m10 - matrix3f.m01) * double1;
			} else {
				this.w = 0.0;
				double1 = -0.5 * (double)(matrix3f.m11 + matrix3f.m22);
				if (double1 >= 0.0) {
					if (double1 >= 1.0E-30) {
						this.x = Math.sqrt(double1);
						double1 = 0.5 / this.x;
						this.y = (double)matrix3f.m10 * double1;
						this.z = (double)matrix3f.m20 * double1;
					} else {
						this.x = 0.0;
						double1 = 0.5 * (1.0 - (double)matrix3f.m22);
						if (double1 >= 1.0E-30) {
							this.y = Math.sqrt(double1);
							this.z = (double)matrix3f.m21 / (2.0 * this.y);
						}

						this.y = 0.0;
						this.z = 1.0;
					}
				} else {
					this.x = 0.0;
					this.y = 0.0;
					this.z = 1.0;
				}
			}
		} else {
			this.w = 0.0;
			this.x = 0.0;
			this.y = 0.0;
			this.z = 1.0;
		}
	}

	public final void set(Matrix3d matrix3d) {
		double double1 = 0.25 * (matrix3d.m00 + matrix3d.m11 + matrix3d.m22 + 1.0);
		if (double1 >= 0.0) {
			if (double1 >= 1.0E-30) {
				this.w = Math.sqrt(double1);
				double1 = 0.25 / this.w;
				this.x = (matrix3d.m21 - matrix3d.m12) * double1;
				this.y = (matrix3d.m02 - matrix3d.m20) * double1;
				this.z = (matrix3d.m10 - matrix3d.m01) * double1;
			} else {
				this.w = 0.0;
				double1 = -0.5 * (matrix3d.m11 + matrix3d.m22);
				if (double1 >= 0.0) {
					if (double1 >= 1.0E-30) {
						this.x = Math.sqrt(double1);
						double1 = 0.5 / this.x;
						this.y = matrix3d.m10 * double1;
						this.z = matrix3d.m20 * double1;
					} else {
						this.x = 0.0;
						double1 = 0.5 * (1.0 - matrix3d.m22);
						if (double1 >= 1.0E-30) {
							this.y = Math.sqrt(double1);
							this.z = matrix3d.m21 / (2.0 * this.y);
						} else {
							this.y = 0.0;
							this.z = 1.0;
						}
					}
				} else {
					this.x = 0.0;
					this.y = 0.0;
					this.z = 1.0;
				}
			}
		} else {
			this.w = 0.0;
			this.x = 0.0;
			this.y = 0.0;
			this.z = 1.0;
		}
	}

	public final void set(AxisAngle4f axisAngle4f) {
		double double1 = Math.sqrt((double)(axisAngle4f.x * axisAngle4f.x + axisAngle4f.y * axisAngle4f.y + axisAngle4f.z * axisAngle4f.z));
		if (double1 < 1.0E-12) {
			this.w = 0.0;
			this.x = 0.0;
			this.y = 0.0;
			this.z = 0.0;
		} else {
			double double2 = Math.sin((double)axisAngle4f.angle / 2.0);
			double1 = 1.0 / double1;
			this.w = Math.cos((double)axisAngle4f.angle / 2.0);
			this.x = (double)axisAngle4f.x * double1 * double2;
			this.y = (double)axisAngle4f.y * double1 * double2;
			this.z = (double)axisAngle4f.z * double1 * double2;
		}
	}

	public final void set(AxisAngle4d axisAngle4d) {
		double double1 = Math.sqrt(axisAngle4d.x * axisAngle4d.x + axisAngle4d.y * axisAngle4d.y + axisAngle4d.z * axisAngle4d.z);
		if (double1 < 1.0E-12) {
			this.w = 0.0;
			this.x = 0.0;
			this.y = 0.0;
			this.z = 0.0;
		} else {
			double1 = 1.0 / double1;
			double double2 = Math.sin(axisAngle4d.angle / 2.0);
			this.w = Math.cos(axisAngle4d.angle / 2.0);
			this.x = axisAngle4d.x * double1 * double2;
			this.y = axisAngle4d.y * double1 * double2;
			this.z = axisAngle4d.z * double1 * double2;
		}
	}

	public final void interpolate(Quat4d quat4d, double double1) {
		double double2 = this.x * quat4d.x + this.y * quat4d.y + this.z * quat4d.z + this.w * quat4d.w;
		if (double2 < 0.0) {
			quat4d.x = -quat4d.x;
			quat4d.y = -quat4d.y;
			quat4d.z = -quat4d.z;
			quat4d.w = -quat4d.w;
			double2 = -double2;
		}

		double double3;
		double double4;
		if (1.0 - double2 > 1.0E-12) {
			double double5 = Math.acos(double2);
			double double6 = Math.sin(double5);
			double3 = Math.sin((1.0 - double1) * double5) / double6;
			double4 = Math.sin(double1 * double5) / double6;
		} else {
			double3 = 1.0 - double1;
			double4 = double1;
		}

		this.w = double3 * this.w + double4 * quat4d.w;
		this.x = double3 * this.x + double4 * quat4d.x;
		this.y = double3 * this.y + double4 * quat4d.y;
		this.z = double3 * this.z + double4 * quat4d.z;
	}

	public final void interpolate(Quat4d quat4d, Quat4d quat4d2, double double1) {
		double double2 = quat4d2.x * quat4d.x + quat4d2.y * quat4d.y + quat4d2.z * quat4d.z + quat4d2.w * quat4d.w;
		if (double2 < 0.0) {
			quat4d.x = -quat4d.x;
			quat4d.y = -quat4d.y;
			quat4d.z = -quat4d.z;
			quat4d.w = -quat4d.w;
			double2 = -double2;
		}

		double double3;
		double double4;
		if (1.0 - double2 > 1.0E-12) {
			double double5 = Math.acos(double2);
			double double6 = Math.sin(double5);
			double3 = Math.sin((1.0 - double1) * double5) / double6;
			double4 = Math.sin(double1 * double5) / double6;
		} else {
			double3 = 1.0 - double1;
			double4 = double1;
		}

		this.w = double3 * quat4d.w + double4 * quat4d2.w;
		this.x = double3 * quat4d.x + double4 * quat4d2.x;
		this.y = double3 * quat4d.y + double4 * quat4d2.y;
		this.z = double3 * quat4d.z + double4 * quat4d2.z;
	}
}
