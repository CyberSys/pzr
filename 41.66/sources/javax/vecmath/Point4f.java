package javax.vecmath;

import java.io.Serializable;


public class Point4f extends Tuple4f implements Serializable {
	static final long serialVersionUID = 4643134103185764459L;

	public Point4f(float float1, float float2, float float3, float float4) {
		super(float1, float2, float3, float4);
	}

	public Point4f(float[] floatArray) {
		super(floatArray);
	}

	public Point4f(Point4f point4f) {
		super((Tuple4f)point4f);
	}

	public Point4f(Point4d point4d) {
		super((Tuple4d)point4d);
	}

	public Point4f(Tuple4f tuple4f) {
		super(tuple4f);
	}

	public Point4f(Tuple4d tuple4d) {
		super(tuple4d);
	}

	public Point4f(Tuple3f tuple3f) {
		super(tuple3f.x, tuple3f.y, tuple3f.z, 1.0F);
	}

	public Point4f() {
	}

	public final void set(Tuple3f tuple3f) {
		this.x = tuple3f.x;
		this.y = tuple3f.y;
		this.z = tuple3f.z;
		this.w = 1.0F;
	}

	public final float distanceSquared(Point4f point4f) {
		float float1 = this.x - point4f.x;
		float float2 = this.y - point4f.y;
		float float3 = this.z - point4f.z;
		float float4 = this.w - point4f.w;
		return float1 * float1 + float2 * float2 + float3 * float3 + float4 * float4;
	}

	public final float distance(Point4f point4f) {
		float float1 = this.x - point4f.x;
		float float2 = this.y - point4f.y;
		float float3 = this.z - point4f.z;
		float float4 = this.w - point4f.w;
		return (float)Math.sqrt((double)(float1 * float1 + float2 * float2 + float3 * float3 + float4 * float4));
	}

	public final float distanceL1(Point4f point4f) {
		return Math.abs(this.x - point4f.x) + Math.abs(this.y - point4f.y) + Math.abs(this.z - point4f.z) + Math.abs(this.w - point4f.w);
	}

	public final float distanceLinf(Point4f point4f) {
		float float1 = Math.max(Math.abs(this.x - point4f.x), Math.abs(this.y - point4f.y));
		float float2 = Math.max(Math.abs(this.z - point4f.z), Math.abs(this.w - point4f.w));
		return Math.max(float1, float2);
	}

	public final void project(Point4f point4f) {
		float float1 = 1.0F / point4f.w;
		this.x = point4f.x * float1;
		this.y = point4f.y * float1;
		this.z = point4f.z * float1;
		this.w = 1.0F;
	}
}
