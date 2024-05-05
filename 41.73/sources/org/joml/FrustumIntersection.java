package org.joml;


public class FrustumIntersection {
	public static final int PLANE_NX = 0;
	public static final int PLANE_PX = 1;
	public static final int PLANE_NY = 2;
	public static final int PLANE_PY = 3;
	public static final int PLANE_NZ = 4;
	public static final int PLANE_PZ = 5;
	public static final int INTERSECT = -1;
	public static final int INSIDE = -2;
	public static final int OUTSIDE = -3;
	public static final int PLANE_MASK_NX = 1;
	public static final int PLANE_MASK_PX = 2;
	public static final int PLANE_MASK_NY = 4;
	public static final int PLANE_MASK_PY = 8;
	public static final int PLANE_MASK_NZ = 16;
	public static final int PLANE_MASK_PZ = 32;
	private float nxX;
	private float nxY;
	private float nxZ;
	private float nxW;
	private float pxX;
	private float pxY;
	private float pxZ;
	private float pxW;
	private float nyX;
	private float nyY;
	private float nyZ;
	private float nyW;
	private float pyX;
	private float pyY;
	private float pyZ;
	private float pyW;
	private float nzX;
	private float nzY;
	private float nzZ;
	private float nzW;
	private float pzX;
	private float pzY;
	private float pzZ;
	private float pzW;
	private final Vector4f[] planes = new Vector4f[6];

	public FrustumIntersection() {
		for (int int1 = 0; int1 < 6; ++int1) {
			this.planes[int1] = new Vector4f();
		}
	}

	public FrustumIntersection(Matrix4fc matrix4fc) {
		for (int int1 = 0; int1 < 6; ++int1) {
			this.planes[int1] = new Vector4f();
		}

		this.set(matrix4fc, true);
	}

	public FrustumIntersection(Matrix4fc matrix4fc, boolean boolean1) {
		for (int int1 = 0; int1 < 6; ++int1) {
			this.planes[int1] = new Vector4f();
		}

		this.set(matrix4fc, boolean1);
	}

	public FrustumIntersection set(Matrix4fc matrix4fc) {
		return this.set(matrix4fc, true);
	}

	public FrustumIntersection set(Matrix4fc matrix4fc, boolean boolean1) {
		this.nxX = matrix4fc.m03() + matrix4fc.m00();
		this.nxY = matrix4fc.m13() + matrix4fc.m10();
		this.nxZ = matrix4fc.m23() + matrix4fc.m20();
		this.nxW = matrix4fc.m33() + matrix4fc.m30();
		float float1;
		if (boolean1) {
			float1 = Math.invsqrt(this.nxX * this.nxX + this.nxY * this.nxY + this.nxZ * this.nxZ);
			this.nxX *= float1;
			this.nxY *= float1;
			this.nxZ *= float1;
			this.nxW *= float1;
		}

		this.planes[0].set(this.nxX, this.nxY, this.nxZ, this.nxW);
		this.pxX = matrix4fc.m03() - matrix4fc.m00();
		this.pxY = matrix4fc.m13() - matrix4fc.m10();
		this.pxZ = matrix4fc.m23() - matrix4fc.m20();
		this.pxW = matrix4fc.m33() - matrix4fc.m30();
		if (boolean1) {
			float1 = Math.invsqrt(this.pxX * this.pxX + this.pxY * this.pxY + this.pxZ * this.pxZ);
			this.pxX *= float1;
			this.pxY *= float1;
			this.pxZ *= float1;
			this.pxW *= float1;
		}

		this.planes[1].set(this.pxX, this.pxY, this.pxZ, this.pxW);
		this.nyX = matrix4fc.m03() + matrix4fc.m01();
		this.nyY = matrix4fc.m13() + matrix4fc.m11();
		this.nyZ = matrix4fc.m23() + matrix4fc.m21();
		this.nyW = matrix4fc.m33() + matrix4fc.m31();
		if (boolean1) {
			float1 = Math.invsqrt(this.nyX * this.nyX + this.nyY * this.nyY + this.nyZ * this.nyZ);
			this.nyX *= float1;
			this.nyY *= float1;
			this.nyZ *= float1;
			this.nyW *= float1;
		}

		this.planes[2].set(this.nyX, this.nyY, this.nyZ, this.nyW);
		this.pyX = matrix4fc.m03() - matrix4fc.m01();
		this.pyY = matrix4fc.m13() - matrix4fc.m11();
		this.pyZ = matrix4fc.m23() - matrix4fc.m21();
		this.pyW = matrix4fc.m33() - matrix4fc.m31();
		if (boolean1) {
			float1 = Math.invsqrt(this.pyX * this.pyX + this.pyY * this.pyY + this.pyZ * this.pyZ);
			this.pyX *= float1;
			this.pyY *= float1;
			this.pyZ *= float1;
			this.pyW *= float1;
		}

		this.planes[3].set(this.pyX, this.pyY, this.pyZ, this.pyW);
		this.nzX = matrix4fc.m03() + matrix4fc.m02();
		this.nzY = matrix4fc.m13() + matrix4fc.m12();
		this.nzZ = matrix4fc.m23() + matrix4fc.m22();
		this.nzW = matrix4fc.m33() + matrix4fc.m32();
		if (boolean1) {
			float1 = Math.invsqrt(this.nzX * this.nzX + this.nzY * this.nzY + this.nzZ * this.nzZ);
			this.nzX *= float1;
			this.nzY *= float1;
			this.nzZ *= float1;
			this.nzW *= float1;
		}

		this.planes[4].set(this.nzX, this.nzY, this.nzZ, this.nzW);
		this.pzX = matrix4fc.m03() - matrix4fc.m02();
		this.pzY = matrix4fc.m13() - matrix4fc.m12();
		this.pzZ = matrix4fc.m23() - matrix4fc.m22();
		this.pzW = matrix4fc.m33() - matrix4fc.m32();
		if (boolean1) {
			float1 = Math.invsqrt(this.pzX * this.pzX + this.pzY * this.pzY + this.pzZ * this.pzZ);
			this.pzX *= float1;
			this.pzY *= float1;
			this.pzZ *= float1;
			this.pzW *= float1;
		}

		this.planes[5].set(this.pzX, this.pzY, this.pzZ, this.pzW);
		return this;
	}

	public boolean testPoint(Vector3fc vector3fc) {
		return this.testPoint(vector3fc.x(), vector3fc.y(), vector3fc.z());
	}

	public boolean testPoint(float float1, float float2, float float3) {
		return this.nxX * float1 + this.nxY * float2 + this.nxZ * float3 + this.nxW >= 0.0F && this.pxX * float1 + this.pxY * float2 + this.pxZ * float3 + this.pxW >= 0.0F && this.nyX * float1 + this.nyY * float2 + this.nyZ * float3 + this.nyW >= 0.0F && this.pyX * float1 + this.pyY * float2 + this.pyZ * float3 + this.pyW >= 0.0F && this.nzX * float1 + this.nzY * float2 + this.nzZ * float3 + this.nzW >= 0.0F && this.pzX * float1 + this.pzY * float2 + this.pzZ * float3 + this.pzW >= 0.0F;
	}

	public boolean testSphere(Vector3fc vector3fc, float float1) {
		return this.testSphere(vector3fc.x(), vector3fc.y(), vector3fc.z(), float1);
	}

	public boolean testSphere(float float1, float float2, float float3, float float4) {
		return this.nxX * float1 + this.nxY * float2 + this.nxZ * float3 + this.nxW >= -float4 && this.pxX * float1 + this.pxY * float2 + this.pxZ * float3 + this.pxW >= -float4 && this.nyX * float1 + this.nyY * float2 + this.nyZ * float3 + this.nyW >= -float4 && this.pyX * float1 + this.pyY * float2 + this.pyZ * float3 + this.pyW >= -float4 && this.nzX * float1 + this.nzY * float2 + this.nzZ * float3 + this.nzW >= -float4 && this.pzX * float1 + this.pzY * float2 + this.pzZ * float3 + this.pzW >= -float4;
	}

	public int intersectSphere(Vector3fc vector3fc, float float1) {
		return this.intersectSphere(vector3fc.x(), vector3fc.y(), vector3fc.z(), float1);
	}

	public int intersectSphere(float float1, float float2, float float3, float float4) {
		boolean boolean1 = true;
		float float5 = this.nxX * float1 + this.nxY * float2 + this.nxZ * float3 + this.nxW;
		if (float5 >= -float4) {
			boolean1 &= float5 >= float4;
			float5 = this.pxX * float1 + this.pxY * float2 + this.pxZ * float3 + this.pxW;
			if (float5 >= -float4) {
				boolean1 &= float5 >= float4;
				float5 = this.nyX * float1 + this.nyY * float2 + this.nyZ * float3 + this.nyW;
				if (float5 >= -float4) {
					boolean1 &= float5 >= float4;
					float5 = this.pyX * float1 + this.pyY * float2 + this.pyZ * float3 + this.pyW;
					if (float5 >= -float4) {
						boolean1 &= float5 >= float4;
						float5 = this.nzX * float1 + this.nzY * float2 + this.nzZ * float3 + this.nzW;
						if (float5 >= -float4) {
							boolean1 &= float5 >= float4;
							float5 = this.pzX * float1 + this.pzY * float2 + this.pzZ * float3 + this.pzW;
							if (float5 >= -float4) {
								boolean1 &= float5 >= float4;
								return boolean1 ? -2 : -1;
							}
						}
					}
				}
			}
		}

		return -3;
	}

	public boolean testAab(Vector3fc vector3fc, Vector3fc vector3fc2) {
		return this.testAab(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z());
	}

	public boolean testAab(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.nxX * (this.nxX < 0.0F ? float1 : float4) + this.nxY * (this.nxY < 0.0F ? float2 : float5) + this.nxZ * (this.nxZ < 0.0F ? float3 : float6) >= -this.nxW && this.pxX * (this.pxX < 0.0F ? float1 : float4) + this.pxY * (this.pxY < 0.0F ? float2 : float5) + this.pxZ * (this.pxZ < 0.0F ? float3 : float6) >= -this.pxW && this.nyX * (this.nyX < 0.0F ? float1 : float4) + this.nyY * (this.nyY < 0.0F ? float2 : float5) + this.nyZ * (this.nyZ < 0.0F ? float3 : float6) >= -this.nyW && this.pyX * (this.pyX < 0.0F ? float1 : float4) + this.pyY * (this.pyY < 0.0F ? float2 : float5) + this.pyZ * (this.pyZ < 0.0F ? float3 : float6) >= -this.pyW && this.nzX * (this.nzX < 0.0F ? float1 : float4) + this.nzY * (this.nzY < 0.0F ? float2 : float5) + this.nzZ * (this.nzZ < 0.0F ? float3 : float6) >= -this.nzW && this.pzX * (this.pzX < 0.0F ? float1 : float4) + this.pzY * (this.pzY < 0.0F ? float2 : float5) + this.pzZ * (this.pzZ < 0.0F ? float3 : float6) >= -this.pzW;
	}

	public boolean testPlaneXY(Vector2fc vector2fc, Vector2fc vector2fc2) {
		return this.testPlaneXY(vector2fc.x(), vector2fc.y(), vector2fc2.x(), vector2fc2.y());
	}

	public boolean testPlaneXY(float float1, float float2, float float3, float float4) {
		return this.nxX * (this.nxX < 0.0F ? float1 : float3) + this.nxY * (this.nxY < 0.0F ? float2 : float4) >= -this.nxW && this.pxX * (this.pxX < 0.0F ? float1 : float3) + this.pxY * (this.pxY < 0.0F ? float2 : float4) >= -this.pxW && this.nyX * (this.nyX < 0.0F ? float1 : float3) + this.nyY * (this.nyY < 0.0F ? float2 : float4) >= -this.nyW && this.pyX * (this.pyX < 0.0F ? float1 : float3) + this.pyY * (this.pyY < 0.0F ? float2 : float4) >= -this.pyW && this.nzX * (this.nzX < 0.0F ? float1 : float3) + this.nzY * (this.nzY < 0.0F ? float2 : float4) >= -this.nzW && this.pzX * (this.pzX < 0.0F ? float1 : float3) + this.pzY * (this.pzY < 0.0F ? float2 : float4) >= -this.pzW;
	}

	public boolean testPlaneXZ(float float1, float float2, float float3, float float4) {
		return this.nxX * (this.nxX < 0.0F ? float1 : float3) + this.nxZ * (this.nxZ < 0.0F ? float2 : float4) >= -this.nxW && this.pxX * (this.pxX < 0.0F ? float1 : float3) + this.pxZ * (this.pxZ < 0.0F ? float2 : float4) >= -this.pxW && this.nyX * (this.nyX < 0.0F ? float1 : float3) + this.nyZ * (this.nyZ < 0.0F ? float2 : float4) >= -this.nyW && this.pyX * (this.pyX < 0.0F ? float1 : float3) + this.pyZ * (this.pyZ < 0.0F ? float2 : float4) >= -this.pyW && this.nzX * (this.nzX < 0.0F ? float1 : float3) + this.nzZ * (this.nzZ < 0.0F ? float2 : float4) >= -this.nzW && this.pzX * (this.pzX < 0.0F ? float1 : float3) + this.pzZ * (this.pzZ < 0.0F ? float2 : float4) >= -this.pzW;
	}

	public int intersectAab(Vector3fc vector3fc, Vector3fc vector3fc2) {
		return this.intersectAab(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z());
	}

	public int intersectAab(float float1, float float2, float float3, float float4, float float5, float float6) {
		byte byte1 = 0;
		boolean boolean1 = true;
		if (this.nxX * (this.nxX < 0.0F ? float1 : float4) + this.nxY * (this.nxY < 0.0F ? float2 : float5) + this.nxZ * (this.nxZ < 0.0F ? float3 : float6) >= -this.nxW) {
			byte1 = 1;
			boolean1 &= this.nxX * (this.nxX < 0.0F ? float4 : float1) + this.nxY * (this.nxY < 0.0F ? float5 : float2) + this.nxZ * (this.nxZ < 0.0F ? float6 : float3) >= -this.nxW;
			if (this.pxX * (this.pxX < 0.0F ? float1 : float4) + this.pxY * (this.pxY < 0.0F ? float2 : float5) + this.pxZ * (this.pxZ < 0.0F ? float3 : float6) >= -this.pxW) {
				byte1 = 2;
				boolean1 &= this.pxX * (this.pxX < 0.0F ? float4 : float1) + this.pxY * (this.pxY < 0.0F ? float5 : float2) + this.pxZ * (this.pxZ < 0.0F ? float6 : float3) >= -this.pxW;
				if (this.nyX * (this.nyX < 0.0F ? float1 : float4) + this.nyY * (this.nyY < 0.0F ? float2 : float5) + this.nyZ * (this.nyZ < 0.0F ? float3 : float6) >= -this.nyW) {
					byte1 = 3;
					boolean1 &= this.nyX * (this.nyX < 0.0F ? float4 : float1) + this.nyY * (this.nyY < 0.0F ? float5 : float2) + this.nyZ * (this.nyZ < 0.0F ? float6 : float3) >= -this.nyW;
					if (this.pyX * (this.pyX < 0.0F ? float1 : float4) + this.pyY * (this.pyY < 0.0F ? float2 : float5) + this.pyZ * (this.pyZ < 0.0F ? float3 : float6) >= -this.pyW) {
						byte1 = 4;
						boolean1 &= this.pyX * (this.pyX < 0.0F ? float4 : float1) + this.pyY * (this.pyY < 0.0F ? float5 : float2) + this.pyZ * (this.pyZ < 0.0F ? float6 : float3) >= -this.pyW;
						if (this.nzX * (this.nzX < 0.0F ? float1 : float4) + this.nzY * (this.nzY < 0.0F ? float2 : float5) + this.nzZ * (this.nzZ < 0.0F ? float3 : float6) >= -this.nzW) {
							byte1 = 5;
							boolean1 &= this.nzX * (this.nzX < 0.0F ? float4 : float1) + this.nzY * (this.nzY < 0.0F ? float5 : float2) + this.nzZ * (this.nzZ < 0.0F ? float6 : float3) >= -this.nzW;
							if (this.pzX * (this.pzX < 0.0F ? float1 : float4) + this.pzY * (this.pzY < 0.0F ? float2 : float5) + this.pzZ * (this.pzZ < 0.0F ? float3 : float6) >= -this.pzW) {
								boolean1 &= this.pzX * (this.pzX < 0.0F ? float4 : float1) + this.pzY * (this.pzY < 0.0F ? float5 : float2) + this.pzZ * (this.pzZ < 0.0F ? float6 : float3) >= -this.pzW;
								return boolean1 ? -2 : -1;
							}
						}
					}
				}
			}
		}

		return byte1;
	}

	public float distanceToPlane(float float1, float float2, float float3, float float4, float float5, float float6, int int1) {
		return this.planes[int1].x * (this.planes[int1].x < 0.0F ? float4 : float1) + this.planes[int1].y * (this.planes[int1].y < 0.0F ? float5 : float2) + this.planes[int1].z * (this.planes[int1].z < 0.0F ? float6 : float3) + this.planes[int1].w;
	}

	public int intersectAab(Vector3fc vector3fc, Vector3fc vector3fc2, int int1) {
		return this.intersectAab(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), int1);
	}

	public int intersectAab(float float1, float float2, float float3, float float4, float float5, float float6, int int1) {
		byte byte1 = 0;
		boolean boolean1 = true;
		if ((int1 & 1) == 0 || this.nxX * (this.nxX < 0.0F ? float1 : float4) + this.nxY * (this.nxY < 0.0F ? float2 : float5) + this.nxZ * (this.nxZ < 0.0F ? float3 : float6) >= -this.nxW) {
			byte1 = 1;
			boolean1 &= this.nxX * (this.nxX < 0.0F ? float4 : float1) + this.nxY * (this.nxY < 0.0F ? float5 : float2) + this.nxZ * (this.nxZ < 0.0F ? float6 : float3) >= -this.nxW;
			if ((int1 & 2) == 0 || this.pxX * (this.pxX < 0.0F ? float1 : float4) + this.pxY * (this.pxY < 0.0F ? float2 : float5) + this.pxZ * (this.pxZ < 0.0F ? float3 : float6) >= -this.pxW) {
				byte1 = 2;
				boolean1 &= this.pxX * (this.pxX < 0.0F ? float4 : float1) + this.pxY * (this.pxY < 0.0F ? float5 : float2) + this.pxZ * (this.pxZ < 0.0F ? float6 : float3) >= -this.pxW;
				if ((int1 & 4) == 0 || this.nyX * (this.nyX < 0.0F ? float1 : float4) + this.nyY * (this.nyY < 0.0F ? float2 : float5) + this.nyZ * (this.nyZ < 0.0F ? float3 : float6) >= -this.nyW) {
					byte1 = 3;
					boolean1 &= this.nyX * (this.nyX < 0.0F ? float4 : float1) + this.nyY * (this.nyY < 0.0F ? float5 : float2) + this.nyZ * (this.nyZ < 0.0F ? float6 : float3) >= -this.nyW;
					if ((int1 & 8) == 0 || this.pyX * (this.pyX < 0.0F ? float1 : float4) + this.pyY * (this.pyY < 0.0F ? float2 : float5) + this.pyZ * (this.pyZ < 0.0F ? float3 : float6) >= -this.pyW) {
						byte1 = 4;
						boolean1 &= this.pyX * (this.pyX < 0.0F ? float4 : float1) + this.pyY * (this.pyY < 0.0F ? float5 : float2) + this.pyZ * (this.pyZ < 0.0F ? float6 : float3) >= -this.pyW;
						if ((int1 & 16) == 0 || this.nzX * (this.nzX < 0.0F ? float1 : float4) + this.nzY * (this.nzY < 0.0F ? float2 : float5) + this.nzZ * (this.nzZ < 0.0F ? float3 : float6) >= -this.nzW) {
							byte1 = 5;
							boolean1 &= this.nzX * (this.nzX < 0.0F ? float4 : float1) + this.nzY * (this.nzY < 0.0F ? float5 : float2) + this.nzZ * (this.nzZ < 0.0F ? float6 : float3) >= -this.nzW;
							if ((int1 & 32) == 0 || this.pzX * (this.pzX < 0.0F ? float1 : float4) + this.pzY * (this.pzY < 0.0F ? float2 : float5) + this.pzZ * (this.pzZ < 0.0F ? float3 : float6) >= -this.pzW) {
								boolean1 &= this.pzX * (this.pzX < 0.0F ? float4 : float1) + this.pzY * (this.pzY < 0.0F ? float5 : float2) + this.pzZ * (this.pzZ < 0.0F ? float6 : float3) >= -this.pzW;
								return boolean1 ? -2 : -1;
							}
						}
					}
				}
			}
		}

		return byte1;
	}

	public int intersectAab(Vector3fc vector3fc, Vector3fc vector3fc2, int int1, int int2) {
		return this.intersectAab(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), int1, int2);
	}

	public int intersectAab(float float1, float float2, float float3, float float4, float float5, float float6, int int1, int int2) {
		int int3 = int2;
		boolean boolean1 = true;
		Vector4f vector4f = this.planes[int2];
		if ((int1 & 1 << int2) != 0 && vector4f.x * (vector4f.x < 0.0F ? float1 : float4) + vector4f.y * (vector4f.y < 0.0F ? float2 : float5) + vector4f.z * (vector4f.z < 0.0F ? float3 : float6) < -vector4f.w) {
			return int2;
		} else {
			if ((int1 & 1) == 0 || this.nxX * (this.nxX < 0.0F ? float1 : float4) + this.nxY * (this.nxY < 0.0F ? float2 : float5) + this.nxZ * (this.nxZ < 0.0F ? float3 : float6) >= -this.nxW) {
				int3 = 1;
				boolean1 &= this.nxX * (this.nxX < 0.0F ? float4 : float1) + this.nxY * (this.nxY < 0.0F ? float5 : float2) + this.nxZ * (this.nxZ < 0.0F ? float6 : float3) >= -this.nxW;
				if ((int1 & 2) == 0 || this.pxX * (this.pxX < 0.0F ? float1 : float4) + this.pxY * (this.pxY < 0.0F ? float2 : float5) + this.pxZ * (this.pxZ < 0.0F ? float3 : float6) >= -this.pxW) {
					int3 = 2;
					boolean1 &= this.pxX * (this.pxX < 0.0F ? float4 : float1) + this.pxY * (this.pxY < 0.0F ? float5 : float2) + this.pxZ * (this.pxZ < 0.0F ? float6 : float3) >= -this.pxW;
					if ((int1 & 4) == 0 || this.nyX * (this.nyX < 0.0F ? float1 : float4) + this.nyY * (this.nyY < 0.0F ? float2 : float5) + this.nyZ * (this.nyZ < 0.0F ? float3 : float6) >= -this.nyW) {
						int3 = 3;
						boolean1 &= this.nyX * (this.nyX < 0.0F ? float4 : float1) + this.nyY * (this.nyY < 0.0F ? float5 : float2) + this.nyZ * (this.nyZ < 0.0F ? float6 : float3) >= -this.nyW;
						if ((int1 & 8) == 0 || this.pyX * (this.pyX < 0.0F ? float1 : float4) + this.pyY * (this.pyY < 0.0F ? float2 : float5) + this.pyZ * (this.pyZ < 0.0F ? float3 : float6) >= -this.pyW) {
							int3 = 4;
							boolean1 &= this.pyX * (this.pyX < 0.0F ? float4 : float1) + this.pyY * (this.pyY < 0.0F ? float5 : float2) + this.pyZ * (this.pyZ < 0.0F ? float6 : float3) >= -this.pyW;
							if ((int1 & 16) == 0 || this.nzX * (this.nzX < 0.0F ? float1 : float4) + this.nzY * (this.nzY < 0.0F ? float2 : float5) + this.nzZ * (this.nzZ < 0.0F ? float3 : float6) >= -this.nzW) {
								int3 = 5;
								boolean1 &= this.nzX * (this.nzX < 0.0F ? float4 : float1) + this.nzY * (this.nzY < 0.0F ? float5 : float2) + this.nzZ * (this.nzZ < 0.0F ? float6 : float3) >= -this.nzW;
								if ((int1 & 32) == 0 || this.pzX * (this.pzX < 0.0F ? float1 : float4) + this.pzY * (this.pzY < 0.0F ? float2 : float5) + this.pzZ * (this.pzZ < 0.0F ? float3 : float6) >= -this.pzW) {
									boolean1 &= this.pzX * (this.pzX < 0.0F ? float4 : float1) + this.pzY * (this.pzY < 0.0F ? float5 : float2) + this.pzZ * (this.pzZ < 0.0F ? float6 : float3) >= -this.pzW;
									return boolean1 ? -2 : -1;
								}
							}
						}
					}
				}
			}

			return int3;
		}
	}
}
