package javax.vecmath;

import java.io.Serializable;


public class Quat4f extends Tuple4f implements Serializable {
	static final long serialVersionUID = 2675933778405442383L;
	static final double EPS = 1.0E-6;
	static final double EPS2 = 1.0E-30;
	static final double PIO2 = 1.57079632679;

	public Quat4f(float float1, float float2, float float3, float float4) {
		float float5 = (float)(1.0 / Math.sqrt((double)(float1 * float1 + float2 * float2 + float3 * float3 + float4 * float4)));
		this.x = float1 * float5;
		this.y = float2 * float5;
		this.z = float3 * float5;
		this.w = float4 * float5;
	}

	public Quat4f(float[] floatArray) {
		float float1 = (float)(1.0 / Math.sqrt((double)(floatArray[0] * floatArray[0] + floatArray[1] * floatArray[1] + floatArray[2] * floatArray[2] + floatArray[3] * floatArray[3])));
		this.x = floatArray[0] * float1;
		this.y = floatArray[1] * float1;
		this.z = floatArray[2] * float1;
		this.w = floatArray[3] * float1;
	}

	public Quat4f(Quat4f quat4f) {
		super((Tuple4f)quat4f);
	}

	public Quat4f(Quat4d quat4d) {
		super((Tuple4d)quat4d);
	}

	public Quat4f(Tuple4f tuple4f) {
		float float1 = (float)(1.0 / Math.sqrt((double)(tuple4f.x * tuple4f.x + tuple4f.y * tuple4f.y + tuple4f.z * tuple4f.z + tuple4f.w * tuple4f.w)));
		this.x = tuple4f.x * float1;
		this.y = tuple4f.y * float1;
		this.z = tuple4f.z * float1;
		this.w = tuple4f.w * float1;
	}

	public Quat4f(Tuple4d tuple4d) {
		double double1 = 1.0 / Math.sqrt(tuple4d.x * tuple4d.x + tuple4d.y * tuple4d.y + tuple4d.z * tuple4d.z + tuple4d.w * tuple4d.w);
		this.x = (float)(tuple4d.x * double1);
		this.y = (float)(tuple4d.y * double1);
		this.z = (float)(tuple4d.z * double1);
		this.w = (float)(tuple4d.w * double1);
	}

	public Quat4f() {
	}

	public final void conjugate(Quat4f quat4f) {
		this.x = -quat4f.x;
		this.y = -quat4f.y;
		this.z = -quat4f.z;
		this.w = quat4f.w;
	}

	public final void conjugate() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
	}

	public final void mul(Quat4f quat4f, Quat4f quat4f2) {
		if (this != quat4f && this != quat4f2) {
			this.w = quat4f.w * quat4f2.w - quat4f.x * quat4f2.x - quat4f.y * quat4f2.y - quat4f.z * quat4f2.z;
			this.x = quat4f.w * quat4f2.x + quat4f2.w * quat4f.x + quat4f.y * quat4f2.z - quat4f.z * quat4f2.y;
			this.y = quat4f.w * quat4f2.y + quat4f2.w * quat4f.y - quat4f.x * quat4f2.z + quat4f.z * quat4f2.x;
			this.z = quat4f.w * quat4f2.z + quat4f2.w * quat4f.z + quat4f.x * quat4f2.y - quat4f.y * quat4f2.x;
		} else {
			float float1 = quat4f.w * quat4f2.w - quat4f.x * quat4f2.x - quat4f.y * quat4f2.y - quat4f.z * quat4f2.z;
			float float2 = quat4f.w * quat4f2.x + quat4f2.w * quat4f.x + quat4f.y * quat4f2.z - quat4f.z * quat4f2.y;
			float float3 = quat4f.w * quat4f2.y + quat4f2.w * quat4f.y - quat4f.x * quat4f2.z + quat4f.z * quat4f2.x;
			this.z = quat4f.w * quat4f2.z + quat4f2.w * quat4f.z + quat4f.x * quat4f2.y - quat4f.y * quat4f2.x;
			this.w = float1;
			this.x = float2;
			this.y = float3;
		}
	}

	public final void mul(Quat4f quat4f) {
		float float1 = this.w * quat4f.w - this.x * quat4f.x - this.y * quat4f.y - this.z * quat4f.z;
		float float2 = this.w * quat4f.x + quat4f.w * this.x + this.y * quat4f.z - this.z * quat4f.y;
		float float3 = this.w * quat4f.y + quat4f.w * this.y - this.x * quat4f.z + this.z * quat4f.x;
		this.z = this.w * quat4f.z + quat4f.w * this.z + this.x * quat4f.y - this.y * quat4f.x;
		this.w = float1;
		this.x = float2;
		this.y = float3;
	}

	public final void mulInverse(Quat4f quat4f, Quat4f quat4f2) {
		Quat4f quat4f3 = new Quat4f(quat4f2);
		quat4f3.inverse();
		this.mul(quat4f, quat4f3);
	}

	public final void mulInverse(Quat4f quat4f) {
		Quat4f quat4f2 = new Quat4f(quat4f);
		quat4f2.inverse();
		this.mul(quat4f2);
	}

	public final void inverse(Quat4f quat4f) {
		float float1 = 1.0F / (quat4f.w * quat4f.w + quat4f.x * quat4f.x + quat4f.y * quat4f.y + quat4f.z * quat4f.z);
		this.w = float1 * quat4f.w;
		this.x = -float1 * quat4f.x;
		this.y = -float1 * quat4f.y;
		this.z = -float1 * quat4f.z;
	}

	public final void inverse() {
		float float1 = 1.0F / (this.w * this.w + this.x * this.x + this.y * this.y + this.z * this.z);
		this.w *= float1;
		this.x *= -float1;
		this.y *= -float1;
		this.z *= -float1;
	}

	public final void normalize(Quat4f quat4f) {
		float float1 = quat4f.x * quat4f.x + quat4f.y * quat4f.y + quat4f.z * quat4f.z + quat4f.w * quat4f.w;
		if (float1 > 0.0F) {
			float1 = 1.0F / (float)Math.sqrt((double)float1);
			this.x = float1 * quat4f.x;
			this.y = float1 * quat4f.y;
			this.z = float1 * quat4f.z;
			this.w = float1 * quat4f.w;
		} else {
			this.x = 0.0F;
			this.y = 0.0F;
			this.z = 0.0F;
			this.w = 0.0F;
		}
	}

	public final void normalize() {
		float float1 = this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
		if (float1 > 0.0F) {
			float1 = 1.0F / (float)Math.sqrt((double)float1);
			this.x *= float1;
			this.y *= float1;
			this.z *= float1;
			this.w *= float1;
		} else {
			this.x = 0.0F;
			this.y = 0.0F;
			this.z = 0.0F;
			this.w = 0.0F;
		}
	}

	public final void set(Matrix4f matrix4f) {
		float float1 = 0.25F * (matrix4f.m00 + matrix4f.m11 + matrix4f.m22 + matrix4f.m33);
		if (float1 >= 0.0F) {
			if ((double)float1 >= 1.0E-30) {
				this.w = (float)Math.sqrt((double)float1);
				float1 = 0.25F / this.w;
				this.x = (matrix4f.m21 - matrix4f.m12) * float1;
				this.y = (matrix4f.m02 - matrix4f.m20) * float1;
				this.z = (matrix4f.m10 - matrix4f.m01) * float1;
			} else {
				this.w = 0.0F;
				float1 = -0.5F * (matrix4f.m11 + matrix4f.m22);
				if (float1 >= 0.0F) {
					if ((double)float1 >= 1.0E-30) {
						this.x = (float)Math.sqrt((double)float1);
						float1 = 1.0F / (2.0F * this.x);
						this.y = matrix4f.m10 * float1;
						this.z = matrix4f.m20 * float1;
					} else {
						this.x = 0.0F;
						float1 = 0.5F * (1.0F - matrix4f.m22);
						if ((double)float1 >= 1.0E-30) {
							this.y = (float)Math.sqrt((double)float1);
							this.z = matrix4f.m21 / (2.0F * this.y);
						} else {
							this.y = 0.0F;
							this.z = 1.0F;
						}
					}
				} else {
					this.x = 0.0F;
					this.y = 0.0F;
					this.z = 1.0F;
				}
			}
		} else {
			this.w = 0.0F;
			this.x = 0.0F;
			this.y = 0.0F;
			this.z = 1.0F;
		}
	}

	public final void set(Matrix4d matrix4d) {
		double double1 = 0.25 * (matrix4d.m00 + matrix4d.m11 + matrix4d.m22 + matrix4d.m33);
		if (double1 >= 0.0) {
			if (double1 >= 1.0E-30) {
				this.w = (float)Math.sqrt(double1);
				double1 = 0.25 / (double)this.w;
				this.x = (float)((matrix4d.m21 - matrix4d.m12) * double1);
				this.y = (float)((matrix4d.m02 - matrix4d.m20) * double1);
				this.z = (float)((matrix4d.m10 - matrix4d.m01) * double1);
			} else {
				this.w = 0.0F;
				double1 = -0.5 * (matrix4d.m11 + matrix4d.m22);
				if (double1 >= 0.0) {
					if (double1 >= 1.0E-30) {
						this.x = (float)Math.sqrt(double1);
						double1 = 0.5 / (double)this.x;
						this.y = (float)(matrix4d.m10 * double1);
						this.z = (float)(matrix4d.m20 * double1);
					} else {
						this.x = 0.0F;
						double1 = 0.5 * (1.0 - matrix4d.m22);
						if (double1 >= 1.0E-30) {
							this.y = (float)Math.sqrt(double1);
							this.z = (float)(matrix4d.m21 / (2.0 * (double)this.y));
						} else {
							this.y = 0.0F;
							this.z = 1.0F;
						}
					}
				} else {
					this.x = 0.0F;
					this.y = 0.0F;
					this.z = 1.0F;
				}
			}
		} else {
			this.w = 0.0F;
			this.x = 0.0F;
			this.y = 0.0F;
			this.z = 1.0F;
		}
	}

	public final void set(Matrix3f matrix3f) {
		float float1 = 0.25F * (matrix3f.m00 + matrix3f.m11 + matrix3f.m22 + 1.0F);
		if (float1 >= 0.0F) {
			if ((double)float1 >= 1.0E-30) {
				this.w = (float)Math.sqrt((double)float1);
				float1 = 0.25F / this.w;
				this.x = (matrix3f.m21 - matrix3f.m12) * float1;
				this.y = (matrix3f.m02 - matrix3f.m20) * float1;
				this.z = (matrix3f.m10 - matrix3f.m01) * float1;
			} else {
				this.w = 0.0F;
				float1 = -0.5F * (matrix3f.m11 + matrix3f.m22);
				if (float1 >= 0.0F) {
					if ((double)float1 >= 1.0E-30) {
						this.x = (float)Math.sqrt((double)float1);
						float1 = 0.5F / this.x;
						this.y = matrix3f.m10 * float1;
						this.z = matrix3f.m20 * float1;
					} else {
						this.x = 0.0F;
						float1 = 0.5F * (1.0F - matrix3f.m22);
						if ((double)float1 >= 1.0E-30) {
							this.y = (float)Math.sqrt((double)float1);
							this.z = matrix3f.m21 / (2.0F * this.y);
						} else {
							this.y = 0.0F;
							this.z = 1.0F;
						}
					}
				} else {
					this.x = 0.0F;
					this.y = 0.0F;
					this.z = 1.0F;
				}
			}
		} else {
			this.w = 0.0F;
			this.x = 0.0F;
			this.y = 0.0F;
			this.z = 1.0F;
		}
	}

	public final void set(Matrix3d matrix3d) {
		double double1 = 0.25 * (matrix3d.m00 + matrix3d.m11 + matrix3d.m22 + 1.0);
		if (double1 >= 0.0) {
			if (double1 >= 1.0E-30) {
				this.w = (float)Math.sqrt(double1);
				double1 = 0.25 / (double)this.w;
				this.x = (float)((matrix3d.m21 - matrix3d.m12) * double1);
				this.y = (float)((matrix3d.m02 - matrix3d.m20) * double1);
				this.z = (float)((matrix3d.m10 - matrix3d.m01) * double1);
			} else {
				this.w = 0.0F;
				double1 = -0.5 * (matrix3d.m11 + matrix3d.m22);
				if (double1 >= 0.0) {
					if (double1 >= 1.0E-30) {
						this.x = (float)Math.sqrt(double1);
						double1 = 0.5 / (double)this.x;
						this.y = (float)(matrix3d.m10 * double1);
						this.z = (float)(matrix3d.m20 * double1);
					} else {
						this.x = 0.0F;
						double1 = 0.5 * (1.0 - matrix3d.m22);
						if (double1 >= 1.0E-30) {
							this.y = (float)Math.sqrt(double1);
							this.z = (float)(matrix3d.m21 / (2.0 * (double)this.y));
						} else {
							this.y = 0.0F;
							this.z = 1.0F;
						}
					}
				} else {
					this.x = 0.0F;
					this.y = 0.0F;
					this.z = 1.0F;
				}
			}
		} else {
			this.w = 0.0F;
			this.x = 0.0F;
			this.y = 0.0F;
			this.z = 1.0F;
		}
	}

	public final void set(AxisAngle4f axisAngle4f) {
		float float1 = (float)Math.sqrt((double)(axisAngle4f.x * axisAngle4f.x + axisAngle4f.y * axisAngle4f.y + axisAngle4f.z * axisAngle4f.z));
		if ((double)float1 < 1.0E-6) {
			this.w = 0.0F;
			this.x = 0.0F;
			this.y = 0.0F;
			this.z = 0.0F;
		} else {
			float1 = 1.0F / float1;
			float float2 = (float)Math.sin((double)axisAngle4f.angle / 2.0);
			this.w = (float)Math.cos((double)axisAngle4f.angle / 2.0);
			this.x = axisAngle4f.x * float1 * float2;
			this.y = axisAngle4f.y * float1 * float2;
			this.z = axisAngle4f.z * float1 * float2;
		}
	}

	public final void set(AxisAngle4d axisAngle4d) {
		float float1 = (float)(1.0 / Math.sqrt(axisAngle4d.x * axisAngle4d.x + axisAngle4d.y * axisAngle4d.y + axisAngle4d.z * axisAngle4d.z));
		if ((double)float1 < 1.0E-6) {
			this.w = 0.0F;
			this.x = 0.0F;
			this.y = 0.0F;
			this.z = 0.0F;
		} else {
			float1 = 1.0F / float1;
			float float2 = (float)Math.sin(axisAngle4d.angle / 2.0);
			this.w = (float)Math.cos(axisAngle4d.angle / 2.0);
			this.x = (float)axisAngle4d.x * float1 * float2;
			this.y = (float)axisAngle4d.y * float1 * float2;
			this.z = (float)axisAngle4d.z * float1 * float2;
		}
	}

	public final void interpolate(Quat4f quat4f, float float1) {
		double double1 = (double)(this.x * quat4f.x + this.y * quat4f.y + this.z * quat4f.z + this.w * quat4f.w);
		if (double1 < 0.0) {
			quat4f.x = -quat4f.x;
			quat4f.y = -quat4f.y;
			quat4f.z = -quat4f.z;
			quat4f.w = -quat4f.w;
			double1 = -double1;
		}

		double double2;
		double double3;
		if (1.0 - double1 > 1.0E-6) {
			double double4 = Math.acos(double1);
			double double5 = Math.sin(double4);
			double2 = Math.sin((1.0 - (double)float1) * double4) / double5;
			double3 = Math.sin((double)float1 * double4) / double5;
		} else {
			double2 = 1.0 - (double)float1;
			double3 = (double)float1;
		}

		this.w = (float)(double2 * (double)this.w + double3 * (double)quat4f.w);
		this.x = (float)(double2 * (double)this.x + double3 * (double)quat4f.x);
		this.y = (float)(double2 * (double)this.y + double3 * (double)quat4f.y);
		this.z = (float)(double2 * (double)this.z + double3 * (double)quat4f.z);
	}

	public final void interpolate(Quat4f quat4f, Quat4f quat4f2, float float1) {
		double double1 = (double)(quat4f2.x * quat4f.x + quat4f2.y * quat4f.y + quat4f2.z * quat4f.z + quat4f2.w * quat4f.w);
		if (double1 < 0.0) {
			quat4f.x = -quat4f.x;
			quat4f.y = -quat4f.y;
			quat4f.z = -quat4f.z;
			quat4f.w = -quat4f.w;
			double1 = -double1;
		}

		double double2;
		double double3;
		if (1.0 - double1 > 1.0E-6) {
			double double4 = Math.acos(double1);
			double double5 = Math.sin(double4);
			double2 = Math.sin((1.0 - (double)float1) * double4) / double5;
			double3 = Math.sin((double)float1 * double4) / double5;
		} else {
			double2 = 1.0 - (double)float1;
			double3 = (double)float1;
		}

		this.w = (float)(double2 * (double)quat4f.w + double3 * (double)quat4f2.w);
		this.x = (float)(double2 * (double)quat4f.x + double3 * (double)quat4f2.x);
		this.y = (float)(double2 * (double)quat4f.y + double3 * (double)quat4f2.y);
		this.z = (float)(double2 * (double)quat4f.z + double3 * (double)quat4f2.z);
	}
}
