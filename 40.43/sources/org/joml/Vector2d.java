package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;


public class Vector2d implements Externalizable,Vector2dc {
	private static final long serialVersionUID = 1L;
	public double x;
	public double y;

	public Vector2d() {
	}

	public Vector2d(double double1) {
		this(double1, double1);
	}

	public Vector2d(double double1, double double2) {
		this.x = double1;
		this.y = double2;
	}

	public Vector2d(Vector2dc vector2dc) {
		this.x = vector2dc.x();
		this.y = vector2dc.y();
	}

	public Vector2d(Vector2fc vector2fc) {
		this.x = (double)vector2fc.x();
		this.y = (double)vector2fc.y();
	}

	public Vector2d(ByteBuffer byteBuffer) {
		this(byteBuffer.position(), byteBuffer);
	}

	public Vector2d(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
	}

	public Vector2d(DoubleBuffer doubleBuffer) {
		this(doubleBuffer.position(), doubleBuffer);
	}

	public Vector2d(int int1, DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.get(this, int1, doubleBuffer);
	}

	public double x() {
		return this.x;
	}

	public double y() {
		return this.y;
	}

	public Vector2d set(double double1) {
		return this.set(double1, double1);
	}

	public Vector2d set(double double1, double double2) {
		this.x = double1;
		this.y = double2;
		return this;
	}

	public Vector2d set(Vector2dc vector2dc) {
		this.x = vector2dc.x();
		this.y = vector2dc.y();
		return this;
	}

	public Vector2d set(Vector2fc vector2fc) {
		this.x = (double)vector2fc.x();
		this.y = (double)vector2fc.y();
		return this;
	}

	public Vector2d set(ByteBuffer byteBuffer) {
		return this.set(byteBuffer.position(), byteBuffer);
	}

	public Vector2d set(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
		return this;
	}

	public Vector2d set(DoubleBuffer doubleBuffer) {
		return this.set(doubleBuffer.position(), doubleBuffer);
	}

	public Vector2d set(int int1, DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.get(this, int1, doubleBuffer);
		return this;
	}

	public Vector2d setComponent(int int1, double double1) throws IllegalArgumentException {
		switch (int1) {
		case 0: 
			this.x = double1;
			break;
		
		case 1: 
			this.y = double1;
			break;
		
		default: 
			throw new IllegalArgumentException();
		
		}
		return this;
	}

	public ByteBuffer get(ByteBuffer byteBuffer) {
		return this.get(byteBuffer.position(), byteBuffer);
	}

	public ByteBuffer get(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put(this, int1, byteBuffer);
		return byteBuffer;
	}

	public DoubleBuffer get(DoubleBuffer doubleBuffer) {
		return this.get(doubleBuffer.position(), doubleBuffer);
	}

	public DoubleBuffer get(int int1, DoubleBuffer doubleBuffer) {
		MemUtil.INSTANCE.put(this, int1, doubleBuffer);
		return doubleBuffer;
	}

	public Vector2d perpendicular() {
		return this.set(this.y, this.x * -1.0);
	}

	public Vector2d sub(Vector2dc vector2dc) {
		this.x -= vector2dc.x();
		this.y -= vector2dc.y();
		return this;
	}

	public Vector2d sub(double double1, double double2) {
		this.x -= double1;
		this.y -= double2;
		return this;
	}

	public Vector2d sub(double double1, double double2, Vector2d vector2d) {
		vector2d.x = this.x - double1;
		vector2d.y = this.y - double2;
		return vector2d;
	}

	public Vector2d sub(Vector2fc vector2fc) {
		this.x -= (double)vector2fc.x();
		this.y -= (double)vector2fc.y();
		return this;
	}

	public Vector2d sub(Vector2dc vector2dc, Vector2d vector2d) {
		vector2d.x = this.x - vector2dc.x();
		vector2d.y = this.y - vector2dc.y();
		return vector2d;
	}

	public Vector2d sub(Vector2fc vector2fc, Vector2d vector2d) {
		vector2d.x = this.x + (double)vector2fc.x();
		vector2d.y = this.y + (double)vector2fc.y();
		return vector2d;
	}

	public Vector2d mul(double double1) {
		this.x *= double1;
		this.y *= double1;
		return this;
	}

	public Vector2d mul(double double1, Vector2d vector2d) {
		vector2d.x = this.x * double1;
		vector2d.y = this.y * double1;
		return vector2d;
	}

	public Vector2d mul(double double1, double double2) {
		this.x *= double1;
		this.y *= double2;
		return this;
	}

	public Vector2d mul(double double1, double double2, Vector2d vector2d) {
		vector2d.x = this.x * double1;
		vector2d.y = this.y * double2;
		return vector2d;
	}

	public Vector2d mul(Vector2dc vector2dc) {
		this.x *= vector2dc.x();
		this.y *= vector2dc.y();
		return this;
	}

	public Vector2d mul(Vector2dc vector2dc, Vector2d vector2d) {
		vector2d.x = this.x * vector2dc.x();
		vector2d.y = this.y * vector2dc.y();
		return vector2d;
	}

	public double dot(Vector2dc vector2dc) {
		return this.x * vector2dc.x() + this.y * vector2dc.y();
	}

	public double angle(Vector2dc vector2dc) {
		double double1 = this.x * vector2dc.x() + this.y * vector2dc.y();
		double double2 = this.x * vector2dc.y() - this.y * vector2dc.x();
		return Math.atan2(double2, double1);
	}

	public double length() {
		return Math.sqrt(this.x * this.x + this.y * this.y);
	}

	public double distance(Vector2dc vector2dc) {
		return this.distance(vector2dc.x(), vector2dc.y());
	}

	public double distance(Vector2fc vector2fc) {
		return this.distance((double)vector2fc.x(), (double)vector2fc.y());
	}

	public double distance(double double1, double double2) {
		double double3 = this.x - double1;
		double double4 = this.y - double2;
		return Math.sqrt(double3 * double3 + double4 * double4);
	}

	public Vector2d normalize() {
		double double1 = 1.0 / Math.sqrt(this.x * this.x + this.y * this.y);
		this.x *= double1;
		this.y *= double1;
		return this;
	}

	public Vector2d normalize(Vector2d vector2d) {
		double double1 = 1.0 / Math.sqrt(this.x * this.x + this.y * this.y);
		vector2d.x = this.x * double1;
		vector2d.y = this.y * double1;
		return vector2d;
	}

	public Vector2d add(Vector2dc vector2dc) {
		this.x += vector2dc.x();
		this.y += vector2dc.y();
		return this;
	}

	public Vector2d add(double double1, double double2) {
		this.x += double1;
		this.y += double2;
		return this;
	}

	public Vector2d add(double double1, double double2, Vector2d vector2d) {
		vector2d.x = this.x + double1;
		vector2d.y = this.y + double2;
		return vector2d;
	}

	public Vector2d add(Vector2fc vector2fc) {
		this.x += (double)vector2fc.x();
		this.y += (double)vector2fc.y();
		return this;
	}

	public Vector2d add(Vector2dc vector2dc, Vector2d vector2d) {
		vector2d.x = this.x + vector2dc.x();
		vector2d.y = this.y + vector2dc.y();
		return vector2d;
	}

	public Vector2d add(Vector2fc vector2fc, Vector2d vector2d) {
		vector2d.x = this.x + (double)vector2fc.x();
		vector2d.y = this.y + (double)vector2fc.y();
		return vector2d;
	}

	public Vector2d zero() {
		this.x = 0.0;
		this.y = 0.0;
		return this;
	}

	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeDouble(this.x);
		objectOutput.writeDouble(this.y);
	}

	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		this.x = objectInput.readDouble();
		this.y = objectInput.readDouble();
	}

	public Vector2d negate() {
		this.x = -this.x;
		this.y = -this.y;
		return this;
	}

	public Vector2d negate(Vector2d vector2d) {
		vector2d.x = -this.x;
		vector2d.y = -this.y;
		return vector2d;
	}

	public Vector2d lerp(Vector2dc vector2dc, double double1) {
		return this.lerp(vector2dc, double1, this);
	}

	public Vector2d lerp(Vector2dc vector2dc, double double1, Vector2d vector2d) {
		vector2d.x = this.x + (vector2dc.x() - this.x) * double1;
		vector2d.y = this.y + (vector2dc.y() - this.y) * double1;
		return vector2d;
	}

	public int hashCode() {
		byte byte1 = 1;
		long long1 = Double.doubleToLongBits(this.x);
		int int1 = 31 * byte1 + (int)(long1 ^ long1 >>> 32);
		long1 = Double.doubleToLongBits(this.y);
		int1 = 31 * int1 + (int)(long1 ^ long1 >>> 32);
		return int1;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object == null) {
			return false;
		} else if (this.getClass() != object.getClass()) {
			return false;
		} else {
			Vector2d vector2d = (Vector2d)object;
			if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(vector2d.x)) {
				return false;
			} else {
				return Double.doubleToLongBits(this.y) == Double.doubleToLongBits(vector2d.y);
			}
		}
	}

	public String toString() {
		DecimalFormat decimalFormat = new DecimalFormat(" 0.000E0;-");
		return this.toString(decimalFormat).replaceAll("E(\\d+)", "E+$1");
	}

	public String toString(NumberFormat numberFormat) {
		return "(" + numberFormat.format(this.x) + " " + numberFormat.format(this.y) + ")";
	}

	public Vector2d fma(Vector2dc vector2dc, Vector2dc vector2dc2) {
		this.x += vector2dc.x() * vector2dc2.x();
		this.y += vector2dc.y() * vector2dc2.y();
		return this;
	}

	public Vector2d fma(double double1, Vector2dc vector2dc) {
		this.x += double1 * vector2dc.x();
		this.y += double1 * vector2dc.y();
		return this;
	}

	public Vector2d fma(Vector2dc vector2dc, Vector2dc vector2dc2, Vector2d vector2d) {
		vector2d.x = this.x + vector2dc.x() * vector2dc2.x();
		vector2d.y = this.y + vector2dc.y() * vector2dc2.y();
		return vector2d;
	}

	public Vector2d fma(double double1, Vector2dc vector2dc, Vector2d vector2d) {
		vector2d.x = this.x + double1 * vector2dc.x();
		vector2d.y = this.y + double1 * vector2dc.y();
		return vector2d;
	}

	public Vector2dc toImmutable() {
		return (Vector2dc)(!Options.DEBUG ? this : new Vector2d.Proxy(this));
	}

	private final class Proxy implements Vector2dc {
		private final Vector2dc delegate;

		Proxy(Vector2dc vector2dc) {
			this.delegate = vector2dc;
		}

		public double x() {
			return this.delegate.x();
		}

		public double y() {
			return this.delegate.y();
		}

		public ByteBuffer get(ByteBuffer byteBuffer) {
			return this.delegate.get(byteBuffer);
		}

		public ByteBuffer get(int int1, ByteBuffer byteBuffer) {
			return this.delegate.get(int1, byteBuffer);
		}

		public DoubleBuffer get(DoubleBuffer doubleBuffer) {
			return this.delegate.get(doubleBuffer);
		}

		public DoubleBuffer get(int int1, DoubleBuffer doubleBuffer) {
			return this.delegate.get(int1, doubleBuffer);
		}

		public Vector2d sub(double double1, double double2, Vector2d vector2d) {
			return this.delegate.sub(double1, double2, vector2d);
		}

		public Vector2d sub(Vector2dc vector2dc, Vector2d vector2d) {
			return this.delegate.sub(vector2dc, vector2d);
		}

		public Vector2d sub(Vector2fc vector2fc, Vector2d vector2d) {
			return this.delegate.sub(vector2fc, vector2d);
		}

		public Vector2d mul(double double1, Vector2d vector2d) {
			return this.delegate.mul(double1, vector2d);
		}

		public Vector2d mul(double double1, double double2, Vector2d vector2d) {
			return this.delegate.mul(double1, double2, vector2d);
		}

		public Vector2d mul(Vector2dc vector2dc, Vector2d vector2d) {
			return this.delegate.mul(vector2dc, vector2d);
		}

		public double dot(Vector2dc vector2dc) {
			return this.delegate.dot(vector2dc);
		}

		public double angle(Vector2dc vector2dc) {
			return this.delegate.angle(vector2dc);
		}

		public double length() {
			return this.delegate.length();
		}

		public double distance(Vector2dc vector2dc) {
			return this.delegate.distance(vector2dc);
		}

		public double distance(Vector2fc vector2fc) {
			return this.delegate.distance(vector2fc);
		}

		public double distance(double double1, double double2) {
			return this.delegate.distance(double1, double2);
		}

		public Vector2d normalize(Vector2d vector2d) {
			return this.delegate.normalize(vector2d);
		}

		public Vector2d add(double double1, double double2, Vector2d vector2d) {
			return this.delegate.add(double1, double2, vector2d);
		}

		public Vector2d add(Vector2dc vector2dc, Vector2d vector2d) {
			return this.delegate.add(vector2dc, vector2d);
		}

		public Vector2d add(Vector2fc vector2fc, Vector2d vector2d) {
			return this.delegate.add(vector2fc, vector2d);
		}

		public Vector2d negate(Vector2d vector2d) {
			return this.delegate.negate(vector2d);
		}

		public Vector2d lerp(Vector2dc vector2dc, double double1, Vector2d vector2d) {
			return this.delegate.lerp(vector2dc, double1, vector2d);
		}

		public Vector2d fma(Vector2dc vector2dc, Vector2dc vector2dc2, Vector2d vector2d) {
			return this.delegate.fma(vector2dc, vector2dc2, vector2d);
		}

		public Vector2d fma(double double1, Vector2dc vector2dc, Vector2d vector2d) {
			return this.delegate.fma(double1, vector2dc, vector2d);
		}
	}
}
