package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.NumberFormat;


public class Vector2i implements Externalizable,Vector2ic {
	private static final long serialVersionUID = 1L;
	public int x;
	public int y;

	public Vector2i() {
	}

	public Vector2i(int int1) {
		this.x = int1;
		this.y = int1;
	}

	public Vector2i(int int1, int int2) {
		this.x = int1;
		this.y = int2;
	}

	public Vector2i(float float1, float float2, int int1) {
		this.x = Math.roundUsing(float1, int1);
		this.y = Math.roundUsing(float2, int1);
	}

	public Vector2i(double double1, double double2, int int1) {
		this.x = Math.roundUsing(double1, int1);
		this.y = Math.roundUsing(double2, int1);
	}

	public Vector2i(Vector2ic vector2ic) {
		this.x = vector2ic.x();
		this.y = vector2ic.y();
	}

	public Vector2i(Vector2fc vector2fc, int int1) {
		this.x = Math.roundUsing(vector2fc.x(), int1);
		this.y = Math.roundUsing(vector2fc.y(), int1);
	}

	public Vector2i(Vector2dc vector2dc, int int1) {
		this.x = Math.roundUsing(vector2dc.x(), int1);
		this.y = Math.roundUsing(vector2dc.y(), int1);
	}

	public Vector2i(int[] intArray) {
		this.x = intArray[0];
		this.y = intArray[1];
	}

	public Vector2i(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, byteBuffer.position(), byteBuffer);
	}

	public Vector2i(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
	}

	public Vector2i(IntBuffer intBuffer) {
		MemUtil.INSTANCE.get(this, intBuffer.position(), intBuffer);
	}

	public Vector2i(int int1, IntBuffer intBuffer) {
		MemUtil.INSTANCE.get(this, int1, intBuffer);
	}

	public int x() {
		return this.x;
	}

	public int y() {
		return this.y;
	}

	public Vector2i set(int int1) {
		this.x = int1;
		this.y = int1;
		return this;
	}

	public Vector2i set(int int1, int int2) {
		this.x = int1;
		this.y = int2;
		return this;
	}

	public Vector2i set(Vector2ic vector2ic) {
		this.x = vector2ic.x();
		this.y = vector2ic.y();
		return this;
	}

	public Vector2i set(Vector2dc vector2dc) {
		this.x = (int)vector2dc.x();
		this.y = (int)vector2dc.y();
		return this;
	}

	public Vector2i set(Vector2dc vector2dc, int int1) {
		this.x = Math.roundUsing(vector2dc.x(), int1);
		this.y = Math.roundUsing(vector2dc.y(), int1);
		return this;
	}

	public Vector2i set(Vector2fc vector2fc, int int1) {
		this.x = Math.roundUsing(vector2fc.x(), int1);
		this.y = Math.roundUsing(vector2fc.y(), int1);
		return this;
	}

	public Vector2i set(int[] intArray) {
		this.x = intArray[0];
		this.y = intArray[1];
		return this;
	}

	public Vector2i set(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, byteBuffer.position(), byteBuffer);
		return this;
	}

	public Vector2i set(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
		return this;
	}

	public Vector2i set(IntBuffer intBuffer) {
		MemUtil.INSTANCE.get(this, intBuffer.position(), intBuffer);
		return this;
	}

	public Vector2i set(int int1, IntBuffer intBuffer) {
		MemUtil.INSTANCE.get(this, int1, intBuffer);
		return this;
	}

	public Vector2i setFromAddress(long long1) {
		if (Options.NO_UNSAFE) {
			throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
		} else {
			MemUtil.MemUtilUnsafe.get(this, long1);
			return this;
		}
	}

	public int get(int int1) throws IllegalArgumentException {
		switch (int1) {
		case 0: 
			return this.x;
		
		case 1: 
			return this.y;
		
		default: 
			throw new IllegalArgumentException();
		
		}
	}

	public Vector2i setComponent(int int1, int int2) throws IllegalArgumentException {
		switch (int1) {
		case 0: 
			this.x = int2;
			break;
		
		case 1: 
			this.y = int2;
			break;
		
		default: 
			throw new IllegalArgumentException();
		
		}
		return this;
	}

	public ByteBuffer get(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put(this, byteBuffer.position(), byteBuffer);
		return byteBuffer;
	}

	public ByteBuffer get(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put(this, int1, byteBuffer);
		return byteBuffer;
	}

	public IntBuffer get(IntBuffer intBuffer) {
		MemUtil.INSTANCE.put(this, intBuffer.position(), intBuffer);
		return intBuffer;
	}

	public IntBuffer get(int int1, IntBuffer intBuffer) {
		MemUtil.INSTANCE.put(this, int1, intBuffer);
		return intBuffer;
	}

	public Vector2ic getToAddress(long long1) {
		if (Options.NO_UNSAFE) {
			throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
		} else {
			MemUtil.MemUtilUnsafe.put(this, long1);
			return this;
		}
	}

	public Vector2i sub(Vector2ic vector2ic) {
		this.x -= vector2ic.x();
		this.y -= vector2ic.y();
		return this;
	}

	public Vector2i sub(Vector2ic vector2ic, Vector2i vector2i) {
		vector2i.x = this.x - vector2ic.x();
		vector2i.y = this.y - vector2ic.y();
		return vector2i;
	}

	public Vector2i sub(int int1, int int2) {
		this.x -= int1;
		this.y -= int2;
		return this;
	}

	public Vector2i sub(int int1, int int2, Vector2i vector2i) {
		vector2i.x = this.x - int1;
		vector2i.y = this.y - int2;
		return vector2i;
	}

	public long lengthSquared() {
		return (long)(this.x * this.x + this.y * this.y);
	}

	public static long lengthSquared(int int1, int int2) {
		return (long)(int1 * int1 + int2 * int2);
	}

	public double length() {
		return (double)Math.sqrt((float)(this.x * this.x + this.y * this.y));
	}

	public static double length(int int1, int int2) {
		return (double)Math.sqrt((float)(int1 * int1 + int2 * int2));
	}

	public double distance(Vector2ic vector2ic) {
		int int1 = this.x - vector2ic.x();
		int int2 = this.y - vector2ic.y();
		return (double)Math.sqrt((float)(int1 * int1 + int2 * int2));
	}

	public double distance(int int1, int int2) {
		int int3 = this.x - int1;
		int int4 = this.y - int2;
		return (double)Math.sqrt((float)(int3 * int3 + int4 * int4));
	}

	public long distanceSquared(Vector2ic vector2ic) {
		int int1 = this.x - vector2ic.x();
		int int2 = this.y - vector2ic.y();
		return (long)(int1 * int1 + int2 * int2);
	}

	public long distanceSquared(int int1, int int2) {
		int int3 = this.x - int1;
		int int4 = this.y - int2;
		return (long)(int3 * int3 + int4 * int4);
	}

	public long gridDistance(Vector2ic vector2ic) {
		return (long)(Math.abs(vector2ic.x() - this.x()) + Math.abs(vector2ic.y() - this.y()));
	}

	public long gridDistance(int int1, int int2) {
		return (long)(Math.abs(int1 - this.x()) + Math.abs(int2 - this.y()));
	}

	public static double distance(int int1, int int2, int int3, int int4) {
		int int5 = int1 - int3;
		int int6 = int2 - int4;
		return (double)Math.sqrt((float)(int5 * int5 + int6 * int6));
	}

	public static long distanceSquared(int int1, int int2, int int3, int int4) {
		int int5 = int1 - int3;
		int int6 = int2 - int4;
		return (long)(int5 * int5 + int6 * int6);
	}

	public Vector2i add(Vector2ic vector2ic) {
		this.x += vector2ic.x();
		this.y += vector2ic.y();
		return this;
	}

	public Vector2i add(Vector2ic vector2ic, Vector2i vector2i) {
		vector2i.x = this.x + vector2ic.x();
		vector2i.y = this.y + vector2ic.y();
		return vector2i;
	}

	public Vector2i add(int int1, int int2) {
		this.x += int1;
		this.y += int2;
		return this;
	}

	public Vector2i add(int int1, int int2, Vector2i vector2i) {
		vector2i.x = this.x + int1;
		vector2i.y = this.y + int2;
		return vector2i;
	}

	public Vector2i mul(int int1) {
		this.x *= int1;
		this.y *= int1;
		return this;
	}

	public Vector2i mul(int int1, Vector2i vector2i) {
		vector2i.x = this.x * int1;
		vector2i.y = this.y * int1;
		return vector2i;
	}

	public Vector2i mul(Vector2ic vector2ic) {
		this.x *= vector2ic.x();
		this.y *= vector2ic.y();
		return this;
	}

	public Vector2i mul(Vector2ic vector2ic, Vector2i vector2i) {
		vector2i.x = this.x * vector2ic.x();
		vector2i.y = this.y * vector2ic.y();
		return vector2i;
	}

	public Vector2i mul(int int1, int int2) {
		this.x *= int1;
		this.y *= int2;
		return this;
	}

	public Vector2i mul(int int1, int int2, Vector2i vector2i) {
		vector2i.x = this.x * int1;
		vector2i.y = this.y * int2;
		return vector2i;
	}

	public Vector2i div(float float1) {
		float float2 = 1.0F / float1;
		this.x = (int)((float)this.x * float2);
		this.y = (int)((float)this.y * float2);
		return this;
	}

	public Vector2i div(float float1, Vector2i vector2i) {
		float float2 = 1.0F / float1;
		vector2i.x = (int)((float)this.x * float2);
		vector2i.y = (int)((float)this.y * float2);
		return vector2i;
	}

	public Vector2i div(int int1) {
		this.x /= int1;
		this.y /= int1;
		return this;
	}

	public Vector2i div(int int1, Vector2i vector2i) {
		vector2i.x = this.x / int1;
		vector2i.y = this.y / int1;
		return vector2i;
	}

	public Vector2i zero() {
		this.x = 0;
		this.y = 0;
		return this;
	}

	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeInt(this.x);
		objectOutput.writeInt(this.y);
	}

	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		this.x = objectInput.readInt();
		this.y = objectInput.readInt();
	}

	public Vector2i negate() {
		this.x = -this.x;
		this.y = -this.y;
		return this;
	}

	public Vector2i negate(Vector2i vector2i) {
		vector2i.x = -this.x;
		vector2i.y = -this.y;
		return vector2i;
	}

	public Vector2i min(Vector2ic vector2ic) {
		this.x = this.x < vector2ic.x() ? this.x : vector2ic.x();
		this.y = this.y < vector2ic.y() ? this.y : vector2ic.y();
		return this;
	}

	public Vector2i min(Vector2ic vector2ic, Vector2i vector2i) {
		vector2i.x = this.x < vector2ic.x() ? this.x : vector2ic.x();
		vector2i.y = this.y < vector2ic.y() ? this.y : vector2ic.y();
		return vector2i;
	}

	public Vector2i max(Vector2ic vector2ic) {
		this.x = this.x > vector2ic.x() ? this.x : vector2ic.x();
		this.y = this.y > vector2ic.y() ? this.y : vector2ic.y();
		return this;
	}

	public Vector2i max(Vector2ic vector2ic, Vector2i vector2i) {
		vector2i.x = this.x > vector2ic.x() ? this.x : vector2ic.x();
		vector2i.y = this.y > vector2ic.y() ? this.y : vector2ic.y();
		return vector2i;
	}

	public int maxComponent() {
		int int1 = Math.abs(this.x);
		int int2 = Math.abs(this.y);
		return int1 >= int2 ? 0 : 1;
	}

	public int minComponent() {
		int int1 = Math.abs(this.x);
		int int2 = Math.abs(this.y);
		return int1 < int2 ? 0 : 1;
	}

	public Vector2i absolute() {
		this.x = Math.abs(this.x);
		this.y = Math.abs(this.y);
		return this;
	}

	public Vector2i absolute(Vector2i vector2i) {
		vector2i.x = Math.abs(this.x);
		vector2i.y = Math.abs(this.y);
		return vector2i;
	}

	public int hashCode() {
		byte byte1 = 1;
		int int1 = 31 * byte1 + this.x;
		int1 = 31 * int1 + this.y;
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
			Vector2i vector2i = (Vector2i)object;
			if (this.x != vector2i.x) {
				return false;
			} else {
				return this.y == vector2i.y;
			}
		}
	}

	public boolean equals(int int1, int int2) {
		if (this.x != int1) {
			return false;
		} else {
			return this.y == int2;
		}
	}

	public String toString() {
		return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
	}

	public String toString(NumberFormat numberFormat) {
		String string = numberFormat.format((long)this.x);
		return "(" + string + " " + numberFormat.format((long)this.y) + ")";
	}
}
