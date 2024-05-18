package org.joml;


public class RayAabIntersection {
	private float originX;
	private float originY;
	private float originZ;
	private float dirX;
	private float dirY;
	private float dirZ;
	private float c_xy;
	private float c_yx;
	private float c_zy;
	private float c_yz;
	private float c_xz;
	private float c_zx;
	private float s_xy;
	private float s_yx;
	private float s_zy;
	private float s_yz;
	private float s_xz;
	private float s_zx;
	private byte classification;

	public RayAabIntersection() {
	}

	public RayAabIntersection(float float1, float float2, float float3, float float4, float float5, float float6) {
		this.set(float1, float2, float3, float4, float5, float6);
	}

	public void set(float float1, float float2, float float3, float float4, float float5, float float6) {
		this.originX = float1;
		this.originY = float2;
		this.originZ = float3;
		this.dirX = float4;
		this.dirY = float5;
		this.dirZ = float6;
		this.precomputeSlope();
	}

	private static int signum(float float1) {
		return float1 != 0.0F && !Float.isNaN(float1) ? (1 - Float.floatToIntBits(float1) >>> 31 << 1) - 1 : 0;
	}

	private void precomputeSlope() {
		float float1 = 1.0F / this.dirX;
		float float2 = 1.0F / this.dirY;
		float float3 = 1.0F / this.dirZ;
		this.s_yx = this.dirX * float2;
		this.s_xy = this.dirY * float1;
		this.s_zy = this.dirY * float3;
		this.s_yz = this.dirZ * float2;
		this.s_xz = this.dirZ * float1;
		this.s_zx = this.dirX * float3;
		this.c_xy = this.originY - this.s_xy * this.originX;
		this.c_yx = this.originX - this.s_yx * this.originY;
		this.c_zy = this.originY - this.s_zy * this.originZ;
		this.c_yz = this.originZ - this.s_yz * this.originY;
		this.c_xz = this.originZ - this.s_xz * this.originX;
		this.c_zx = this.originX - this.s_zx * this.originZ;
		int int1 = signum(this.dirX);
		int int2 = signum(this.dirY);
		int int3 = signum(this.dirZ);
		this.classification = (byte)(int3 + 1 << 4 | int2 + 1 << 2 | int1 + 1);
	}

	public boolean test(float float1, float float2, float float3, float float4, float float5, float float6) {
		switch (this.classification) {
		case 0: 
			return this.MMM(float1, float2, float3, float4, float5, float6);
		
		case 1: 
			return this.OMM(float1, float2, float3, float4, float5, float6);
		
		case 2: 
			return this.PMM(float1, float2, float3, float4, float5, float6);
		
		case 3: 
			return false;
		
		case 4: 
			return this.MOM(float1, float2, float3, float4, float5, float6);
		
		case 5: 
			return this.OOM(float1, float2, float3, float4, float5);
		
		case 6: 
			return this.POM(float1, float2, float3, float4, float5, float6);
		
		case 7: 
			return false;
		
		case 8: 
			return this.MPM(float1, float2, float3, float4, float5, float6);
		
		case 9: 
			return this.OPM(float1, float2, float3, float4, float5, float6);
		
		case 10: 
			return this.PPM(float1, float2, float3, float4, float5, float6);
		
		case 11: 
		
		case 12: 
		
		case 13: 
		
		case 14: 
		
		case 15: 
			return false;
		
		case 16: 
			return this.MMO(float1, float2, float3, float4, float5, float6);
		
		case 17: 
			return this.OMO(float1, float2, float3, float4, float6);
		
		case 18: 
			return this.PMO(float1, float2, float3, float4, float5, float6);
		
		case 19: 
			return false;
		
		case 20: 
			return this.MOO(float1, float2, float3, float5, float6);
		
		case 21: 
			return false;
		
		case 22: 
			return this.POO(float2, float3, float4, float5, float6);
		
		case 23: 
			return false;
		
		case 24: 
			return this.MPO(float1, float2, float3, float4, float5, float6);
		
		case 25: 
			return this.OPO(float1, float3, float4, float5, float6);
		
		case 26: 
			return this.PPO(float1, float2, float3, float4, float5, float6);
		
		case 27: 
		
		case 28: 
		
		case 29: 
		
		case 30: 
		
		case 31: 
			return false;
		
		case 32: 
			return this.MMP(float1, float2, float3, float4, float5, float6);
		
		case 33: 
			return this.OMP(float1, float2, float3, float4, float5, float6);
		
		case 34: 
			return this.PMP(float1, float2, float3, float4, float5, float6);
		
		case 35: 
			return false;
		
		case 36: 
			return this.MOP(float1, float2, float3, float4, float5, float6);
		
		case 37: 
			return this.OOP(float1, float2, float4, float5, float6);
		
		case 38: 
			return this.POP(float1, float2, float3, float4, float5, float6);
		
		case 39: 
			return false;
		
		case 40: 
			return this.MPP(float1, float2, float3, float4, float5, float6);
		
		case 41: 
			return this.OPP(float1, float2, float3, float4, float5, float6);
		
		case 42: 
			return this.PPP(float1, float2, float3, float4, float5, float6);
		
		default: 
			return false;
		
		}
	}

	private boolean MMM(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.originX >= float1 && this.originY >= float2 && this.originZ >= float3 && this.s_xy * float1 - float5 + this.c_xy <= 0.0F && this.s_yx * float2 - float4 + this.c_yx <= 0.0F && this.s_zy * float3 - float5 + this.c_zy <= 0.0F && this.s_yz * float2 - float6 + this.c_yz <= 0.0F && this.s_xz * float1 - float6 + this.c_xz <= 0.0F && this.s_zx * float3 - float4 + this.c_zx <= 0.0F;
	}

	private boolean OMM(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.originX >= float1 && this.originX <= float4 && this.originY >= float2 && this.originZ >= float3 && this.s_zy * float3 - float5 + this.c_zy <= 0.0F && this.s_yz * float2 - float6 + this.c_yz <= 0.0F;
	}

	private boolean PMM(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.originX <= float4 && this.originY >= float2 && this.originZ >= float3 && this.s_xy * float4 - float5 + this.c_xy <= 0.0F && this.s_yx * float2 - float1 + this.c_yx >= 0.0F && this.s_zy * float3 - float5 + this.c_zy <= 0.0F && this.s_yz * float2 - float6 + this.c_yz <= 0.0F && this.s_xz * float4 - float6 + this.c_xz <= 0.0F && this.s_zx * float3 - float1 + this.c_zx >= 0.0F;
	}

	private boolean MOM(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.originY >= float2 && this.originY <= float5 && this.originX >= float1 && this.originZ >= float3 && this.s_xz * float1 - float6 + this.c_xz <= 0.0F && this.s_zx * float3 - float4 + this.c_zx <= 0.0F;
	}

	private boolean OOM(float float1, float float2, float float3, float float4, float float5) {
		return this.originZ >= float3 && this.originX >= float1 && this.originX <= float4 && this.originY >= float2 && this.originY <= float5;
	}

	private boolean POM(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.originY >= float2 && this.originY <= float5 && this.originX <= float4 && this.originZ >= float3 && this.s_xz * float4 - float6 + this.c_xz <= 0.0F && this.s_zx * float3 - float1 + this.c_zx >= 0.0F;
	}

	private boolean MPM(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.originX >= float1 && this.originY <= float5 && this.originZ >= float3 && this.s_xy * float1 - float2 + this.c_xy >= 0.0F && this.s_yx * float5 - float4 + this.c_yx <= 0.0F && this.s_zy * float3 - float2 + this.c_zy >= 0.0F && this.s_yz * float5 - float6 + this.c_yz <= 0.0F && this.s_xz * float1 - float6 + this.c_xz <= 0.0F && this.s_zx * float3 - float4 + this.c_zx <= 0.0F;
	}

	private boolean OPM(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.originX >= float1 && this.originX <= float4 && this.originY <= float5 && this.originZ >= float3 && this.s_zy * float3 - float2 + this.c_zy >= 0.0F && this.s_yz * float5 - float6 + this.c_yz <= 0.0F;
	}

	private boolean PPM(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.originX <= float4 && this.originY <= float5 && this.originZ >= float3 && this.s_xy * float4 - float2 + this.c_xy >= 0.0F && this.s_yx * float5 - float1 + this.c_yx >= 0.0F && this.s_zy * float3 - float2 + this.c_zy >= 0.0F && this.s_yz * float5 - float6 + this.c_yz <= 0.0F && this.s_xz * float4 - float6 + this.c_xz <= 0.0F && this.s_zx * float3 - float1 + this.c_zx >= 0.0F;
	}

	private boolean MMO(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.originZ >= float3 && this.originZ <= float6 && this.originX >= float1 && this.originY >= float2 && this.s_xy * float1 - float5 + this.c_xy <= 0.0F && this.s_yx * float2 - float4 + this.c_yx <= 0.0F;
	}

	private boolean OMO(float float1, float float2, float float3, float float4, float float5) {
		return this.originY >= float2 && this.originX >= float1 && this.originX <= float4 && this.originZ >= float3 && this.originZ <= float5;
	}

	private boolean PMO(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.originZ >= float3 && this.originZ <= float6 && this.originX <= float4 && this.originY >= float2 && this.s_xy * float4 - float5 + this.c_xy <= 0.0F && this.s_yx * float2 - float1 + this.c_yx >= 0.0F;
	}

	private boolean MOO(float float1, float float2, float float3, float float4, float float5) {
		return this.originX >= float1 && this.originY >= float2 && this.originY <= float4 && this.originZ >= float3 && this.originZ <= float5;
	}

	private boolean POO(float float1, float float2, float float3, float float4, float float5) {
		return this.originX <= float3 && this.originY >= float1 && this.originY <= float4 && this.originZ >= float2 && this.originZ <= float5;
	}

	private boolean MPO(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.originZ >= float3 && this.originZ <= float6 && this.originX >= float1 && this.originY <= float5 && this.s_xy * float1 - float2 + this.c_xy >= 0.0F && this.s_yx * float5 - float4 + this.c_yx <= 0.0F;
	}

	private boolean OPO(float float1, float float2, float float3, float float4, float float5) {
		return this.originY <= float4 && this.originX >= float1 && this.originX <= float3 && this.originZ >= float2 && this.originZ <= float5;
	}

	private boolean PPO(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.originZ >= float3 && this.originZ <= float6 && this.originX <= float4 && this.originY <= float5 && this.s_xy * float4 - float2 + this.c_xy >= 0.0F && this.s_yx * float5 - float1 + this.c_yx >= 0.0F;
	}

	private boolean MMP(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.originX >= float1 && this.originY >= float2 && this.originZ <= float6 && this.s_xy * float1 - float5 + this.c_xy <= 0.0F && this.s_yx * float2 - float4 + this.c_yx <= 0.0F && this.s_zy * float6 - float5 + this.c_zy <= 0.0F && this.s_yz * float2 - float3 + this.c_yz >= 0.0F && this.s_xz * float1 - float3 + this.c_xz >= 0.0F && this.s_zx * float6 - float4 + this.c_zx <= 0.0F;
	}

	private boolean OMP(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.originX >= float1 && this.originX <= float4 && this.originY >= float2 && this.originZ <= float6 && this.s_zy * float6 - float5 + this.c_zy <= 0.0F && this.s_yz * float2 - float3 + this.c_yz >= 0.0F;
	}

	private boolean PMP(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.originX <= float4 && this.originY >= float2 && this.originZ <= float6 && this.s_xy * float4 - float5 + this.c_xy <= 0.0F && this.s_yx * float2 - float1 + this.c_yx >= 0.0F && this.s_zy * float6 - float5 + this.c_zy <= 0.0F && this.s_yz * float2 - float3 + this.c_yz >= 0.0F && this.s_xz * float4 - float3 + this.c_xz >= 0.0F && this.s_zx * float6 - float1 + this.c_zx >= 0.0F;
	}

	private boolean MOP(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.originY >= float2 && this.originY <= float5 && this.originX >= float1 && this.originZ <= float6 && this.s_xz * float1 - float3 + this.c_xz >= 0.0F && this.s_zx * float6 - float4 + this.c_zx <= 0.0F;
	}

	private boolean OOP(float float1, float float2, float float3, float float4, float float5) {
		return this.originZ <= float5 && this.originX >= float1 && this.originX <= float3 && this.originY >= float2 && this.originY <= float4;
	}

	private boolean POP(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.originY >= float2 && this.originY <= float5 && this.originX <= float4 && this.originZ <= float6 && this.s_xz * float4 - float3 + this.c_xz >= 0.0F && this.s_zx * float6 - float1 + this.c_zx <= 0.0F;
	}

	private boolean MPP(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.originX >= float1 && this.originY <= float5 && this.originZ <= float6 && this.s_xy * float1 - float2 + this.c_xy >= 0.0F && this.s_yx * float5 - float4 + this.c_yx <= 0.0F && this.s_zy * float6 - float2 + this.c_zy >= 0.0F && this.s_yz * float5 - float3 + this.c_yz >= 0.0F && this.s_xz * float1 - float3 + this.c_xz >= 0.0F && this.s_zx * float6 - float4 + this.c_zx <= 0.0F;
	}

	private boolean OPP(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.originX >= float1 && this.originX <= float4 && this.originY <= float5 && this.originZ <= float6 && this.s_zy * float6 - float2 + this.c_zy <= 0.0F && this.s_yz * float5 - float3 + this.c_yz <= 0.0F;
	}

	private boolean PPP(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.originX <= float4 && this.originY <= float5 && this.originZ <= float6 && this.s_xy * float4 - float2 + this.c_xy >= 0.0F && this.s_yx * float5 - float1 + this.c_yx >= 0.0F && this.s_zy * float6 - float2 + this.c_zy >= 0.0F && this.s_yz * float5 - float3 + this.c_yz >= 0.0F && this.s_xz * float4 - float3 + this.c_xz >= 0.0F && this.s_zx * float6 - float1 + this.c_zx >= 0.0F;
	}
}
