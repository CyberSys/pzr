package javax.vecmath;

import java.io.Serializable;


public class Point3f extends Tuple3f implements Serializable {
	static final long serialVersionUID = -8689337816398030143L;

	public Point3f(float float1, float float2, float float3) {
		super(float1, float2, float3);
	}

	public Point3f(float[] floatArray) {
		super(floatArray);
	}

	public Point3f(Point3f point3f) {
		super((Tuple3f)point3f);
	}

	public Point3f(Point3d point3d) {
		super((Tuple3d)point3d);
	}

	public Point3f(Tuple3f tuple3f) {
		super(tuple3f);
	}

	public Point3f(Tuple3d tuple3d) {
		super(tuple3d);
	}

	public Point3f() {
	}

	public final float distanceSquared(Point3f point3f) {
		float float1 = this.x - point3f.x;
		float float2 = this.y - point3f.y;
		float float3 = this.z - point3f.z;
		return float1 * float1 + float2 * float2 + float3 * float3;
	}

	public final float distance(Point3f point3f) {
		float float1 = this.x - point3f.x;
		float float2 = this.y - point3f.y;
		float float3 = this.z - point3f.z;
		return (float)Math.sqrt((double)(float1 * float1 + float2 * float2 + float3 * float3));
	}

	public final float distanceL1(Point3f point3f) {
		return Math.abs(this.x - point3f.x) + Math.abs(this.y - point3f.y) + Math.abs(this.z - point3f.z);
	}

	public final float distanceLinf(Point3f point3f) {
		float float1 = Math.max(Math.abs(this.x - point3f.x), Math.abs(this.y - point3f.y));
		return Math.max(float1, Math.abs(this.z - point3f.z));
	}

	public final void project(Point4f point4f) {
		float float1 = 1.0F / point4f.w;
		this.x = point4f.x * float1;
		this.y = point4f.y * float1;
		this.z = point4f.z * float1;
	}
}
