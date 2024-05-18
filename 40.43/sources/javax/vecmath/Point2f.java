package javax.vecmath;

import java.io.Serializable;


public class Point2f extends Tuple2f implements Serializable {
	static final long serialVersionUID = -4801347926528714435L;

	public Point2f(float float1, float float2) {
		super(float1, float2);
	}

	public Point2f(float[] floatArray) {
		super(floatArray);
	}

	public Point2f(Point2f point2f) {
		super((Tuple2f)point2f);
	}

	public Point2f(Point2d point2d) {
		super((Tuple2d)point2d);
	}

	public Point2f(Tuple2d tuple2d) {
		super(tuple2d);
	}

	public Point2f(Tuple2f tuple2f) {
		super(tuple2f);
	}

	public Point2f() {
	}

	public final float distanceSquared(Point2f point2f) {
		float float1 = this.x - point2f.x;
		float float2 = this.y - point2f.y;
		return float1 * float1 + float2 * float2;
	}

	public final float distance(Point2f point2f) {
		float float1 = this.x - point2f.x;
		float float2 = this.y - point2f.y;
		return (float)Math.sqrt((double)(float1 * float1 + float2 * float2));
	}

	public final float distanceL1(Point2f point2f) {
		return Math.abs(this.x - point2f.x) + Math.abs(this.y - point2f.y);
	}

	public final float distanceLinf(Point2f point2f) {
		return Math.max(Math.abs(this.x - point2f.x), Math.abs(this.y - point2f.y));
	}
}
