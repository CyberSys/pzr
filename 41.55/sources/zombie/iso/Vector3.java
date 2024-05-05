package zombie.iso;

import java.awt.Dimension;
import java.awt.Point;


public final class Vector3 implements Cloneable {
	public float x;
	public float y;
	public float z;

	public Vector3() {
		this.x = 0.0F;
		this.y = 0.0F;
		this.z = 0.0F;
	}

	public Vector3(Vector3 vector3) {
		this.x = vector3.x;
		this.y = vector3.y;
		this.z = vector3.z;
	}

	public Vector3(float float1, float float2, float float3) {
		this.x = float1;
		this.y = float2;
		this.z = float3;
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

	public void rotate(float float1) {
		double double1 = (double)this.x * Math.cos((double)float1) - (double)this.y * Math.sin((double)float1);
		double double2 = (double)this.x * Math.sin((double)float1) + (double)this.y * Math.cos((double)float1);
		this.x = (float)double1;
		this.y = (float)double2;
	}

	public void rotatey(float float1) {
		double double1 = (double)this.x * Math.cos((double)float1) - (double)this.z * Math.sin((double)float1);
		double double2 = (double)this.x * Math.sin((double)float1) + (double)this.z * Math.cos((double)float1);
		this.x = (float)double1;
		this.z = (float)double2;
	}

	public Vector2 add(Vector2 vector2) {
		return new Vector2(this.x + vector2.x, this.y + vector2.y);
	}

	public Vector3 addToThis(Vector2 vector2) {
		this.x += vector2.x;
		this.y += vector2.y;
		return this;
	}

	public Vector3 addToThis(Vector3 vector3) {
		this.x += vector3.x;
		this.y += vector3.y;
		this.z += vector3.z;
		return this;
	}

	public Vector3 div(float float1) {
		this.x /= float1;
		this.y /= float1;
		this.z /= float1;
		return this;
	}

	public Vector3 aimAt(Vector2 vector2) {
		this.setLengthAndDirection(this.angleTo(vector2), this.getLength());
		return this;
	}

	public float angleTo(Vector2 vector2) {
		return (float)Math.atan2((double)(vector2.y - this.y), (double)(vector2.x - this.x));
	}

	public Vector3 clone() {
		return new Vector3(this);
	}

	public float distanceTo(Vector2 vector2) {
		return (float)Math.sqrt(Math.pow((double)(vector2.x - this.x), 2.0) + Math.pow((double)(vector2.y - this.y), 2.0));
	}

	public float dot(Vector2 vector2) {
		return this.x * vector2.x + this.y * vector2.y;
	}

	public float dot3d(Vector3 vector3) {
		return this.x * vector3.x + this.y * vector3.y + this.z * vector3.z;
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
		return (float)Math.atan2((double)this.x, (double)this.y);
	}

	public Vector3 setDirection(float float1) {
		this.setLengthAndDirection(float1, this.getLength());
		return this;
	}

	public float getLength() {
		float float1 = this.getLengthSq();
		return (float)Math.sqrt((double)float1);
	}

	public float getLengthSq() {
		return this.x * this.x + this.y * this.y + this.z * this.z;
	}

	public Vector3 setLength(float float1) {
		this.normalize();
		this.x *= float1;
		this.y *= float1;
		this.z *= float1;
		return this;
	}

	public void normalize() {
		float float1 = this.getLength();
		if (float1 == 0.0F) {
			this.x = 0.0F;
			this.y = 0.0F;
			this.z = 0.0F;
		} else {
			this.x /= float1;
			this.y /= float1;
			this.z /= float1;
		}

		float1 = this.getLength();
	}

	public Vector3 set(Vector3 vector3) {
		this.x = vector3.x;
		this.y = vector3.y;
		this.z = vector3.z;
		return this;
	}

	public Vector3 set(float float1, float float2, float float3) {
		this.x = float1;
		this.y = float2;
		this.z = float3;
		return this;
	}

	public Vector3 setLengthAndDirection(float float1, float float2) {
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

	public Vector3 sub(Vector3 vector3, Vector3 vector32) {
		return sub(this, vector3, vector32);
	}

	public static Vector3 sub(Vector3 vector3, Vector3 vector32, Vector3 vector33) {
		vector33.set(vector3.x - vector32.x, vector3.y - vector32.y, vector3.z - vector32.z);
		return vector33;
	}
}
