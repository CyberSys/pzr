package zombie.iso;

import java.awt.Dimension;
import java.awt.Point;
import zombie.core.math.PZMath;


public final class Vector2 implements Cloneable {
	public float x;
	public float y;

	public Vector2() {
		this.x = 0.0F;
		this.y = 0.0F;
	}

	public Vector2(Vector2 vector2) {
		this.x = vector2.x;
		this.y = vector2.y;
	}

	public Vector2(float float1, float float2) {
		this.x = float1;
		this.y = float2;
	}

	public static Vector2 fromAwtPoint(Point point) {
		return new Vector2((float)point.x, (float)point.y);
	}

	public static Vector2 fromLengthDirection(float float1, float float2) {
		Vector2 vector2 = new Vector2();
		vector2.setLengthAndDirection(float2, float1);
		return vector2;
	}

	public static float dot(float float1, float float2, float float3, float float4) {
		return float1 * float3 + float2 * float4;
	}

	public static Vector2 addScaled(Vector2 vector2, Vector2 vector22, float float1, Vector2 vector23) {
		vector23.set(vector2.x + vector22.x * float1, vector2.y + vector22.y * float1);
		return vector23;
	}

	public void rotate(float float1) {
		double double1 = (double)this.x * Math.cos((double)float1) - (double)this.y * Math.sin((double)float1);
		double double2 = (double)this.x * Math.sin((double)float1) + (double)this.y * Math.cos((double)float1);
		this.x = (float)double1;
		this.y = (float)double2;
	}

	public Vector2 add(Vector2 vector2) {
		this.x += vector2.x;
		this.y += vector2.y;
		return this;
	}

	public Vector2 aimAt(Vector2 vector2) {
		this.setLengthAndDirection(this.angleTo(vector2), this.getLength());
		return this;
	}

	public float angleTo(Vector2 vector2) {
		return (float)Math.atan2((double)(vector2.y - this.y), (double)(vector2.x - this.x));
	}

	public float angleBetween(Vector2 vector2) {
		float float1 = this.dot(vector2) / (this.getLength() * vector2.getLength());
		if (float1 < -1.0F) {
			float1 = -1.0F;
		}

		if (float1 > 1.0F) {
			float1 = 1.0F;
		}

		return (float)Math.acos((double)float1);
	}

	public Vector2 clone() {
		return new Vector2(this);
	}

	public float distanceTo(Vector2 vector2) {
		return (float)Math.sqrt(Math.pow((double)(vector2.x - this.x), 2.0) + Math.pow((double)(vector2.y - this.y), 2.0));
	}

	public float dot(Vector2 vector2) {
		return this.x * vector2.x + this.y * vector2.y;
	}

	public boolean equals(Object object) {
		if (!(object instanceof Vector2)) {
			return false;
		} else {
			Vector2 vector2 = (Vector2)object;
			return vector2.x == this.x && vector2.y == this.y;
		}
	}

	public float getDirection() {
		float float1 = (float)Math.atan2((double)this.y, (double)this.x);
		float float2 = PZMath.wrap(float1, -3.1415927F, 3.1415927F);
		return float2;
	}

	public static float getDirection(float float1, float float2) {
		float float3 = (float)Math.atan2((double)float2, (double)float1);
		float float4 = PZMath.wrap(float3, -3.1415927F, 3.1415927F);
		return float4;
	}

	@Deprecated
	public float getDirectionNeg() {
		return (float)Math.atan2((double)this.x, (double)this.y);
	}

	public Vector2 setDirection(float float1) {
		this.setLengthAndDirection(float1, this.getLength());
		return this;
	}

	public float getLength() {
		return (float)Math.sqrt((double)(this.x * this.x + this.y * this.y));
	}

	public float getLengthSquared() {
		return this.x * this.x + this.y * this.y;
	}

	public Vector2 setLength(float float1) {
		this.normalize();
		this.x *= float1;
		this.y *= float1;
		return this;
	}

	public float normalize() {
		float float1 = this.getLength();
		if (float1 == 0.0F) {
			this.x = 0.0F;
			this.y = 0.0F;
		} else {
			this.x /= float1;
			this.y /= float1;
		}

		return float1;
	}

	public Vector2 set(Vector2 vector2) {
		this.x = vector2.x;
		this.y = vector2.y;
		return this;
	}

	public Vector2 set(float float1, float float2) {
		this.x = float1;
		this.y = float2;
		return this;
	}

	public Vector2 setLengthAndDirection(float float1, float float2) {
		this.x = (float)(Math.cos((double)float1) * (double)float2);
		this.y = (float)(Math.sin((double)float1) * (double)float2);
		return this;
	}

	public Dimension toAwtDimension() {
		return new Dimension((int)this.x, (int)this.y);
	}

	public Point toAwtPoint() {
		return new Point((int)this.x, (int)this.y);
	}

	public String toString() {
		return String.format("Vector2 (X: %f, Y: %f) (L: %f, D:%f)", this.x, this.y, this.getLength(), this.getDirection());
	}

	public float getX() {
		return this.x;
	}

	public void setX(float float1) {
		this.x = float1;
	}

	public float getY() {
		return this.y;
	}

	public void setY(float float1) {
		this.y = float1;
	}

	public void tangent() {
		double double1 = (double)this.x * Math.cos(Math.toRadians(90.0)) - (double)this.y * Math.sin(Math.toRadians(90.0));
		double double2 = (double)this.x * Math.sin(Math.toRadians(90.0)) + (double)this.y * Math.cos(Math.toRadians(90.0));
		this.x = (float)double1;
		this.y = (float)double2;
	}

	public void scale(float float1) {
		scale(this, float1);
	}

	public static Vector2 scale(Vector2 vector2, float float1) {
		vector2.x *= float1;
		vector2.y *= float1;
		return vector2;
	}
}
