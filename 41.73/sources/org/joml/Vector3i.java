package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.NumberFormat;


public class Vector3i implements Externalizable,Vector3ic {
	private static final long serialVersionUID = 1L;
	public int x;
	public int y;
	public int z;

	public Vector3i() {
	}

	public Vector3i(int int1) {
		this.x = int1;
		this.y = int1;
		this.z = int1;
	}

	public Vector3i(int int1, int int2, int int3) {
		this.x = int1;
		this.y = int2;
		this.z = int3;
	}

	public Vector3i(Vector3ic vector3ic) {
		this.x = vector3ic.x();
		this.y = vector3ic.y();
		this.z = vector3ic.z();
	}

	public Vector3i(Vector2ic vector2ic, int int1) {
		this.x = vector2ic.x();
		this.y = vector2ic.y();
		this.z = int1;
	}

	public Vector3i(float float1, float float2, float float3, int int1) {
		this.x = Math.roundUsing(float1, int1);
		this.y = Math.roundUsing(float2, int1);
		this.z = Math.roundUsing(float3, int1);
	}

	public Vector3i(double double1, double double2, double double3, int int1) {
		this.x = Math.roundUsing(double1, int1);
		this.y = Math.roundUsing(double2, int1);
		this.z = Math.roundUsing(double3, int1);
	}

	public Vector3i(Vector2fc vector2fc, float float1, int int1) {
		this.x = Math.roundUsing(vector2fc.x(), int1);
		this.y = Math.roundUsing(vector2fc.y(), int1);
		this.z = Math.roundUsing(float1, int1);
	}

	public Vector3i(Vector3fc vector3fc, int int1) {
		this.x = Math.roundUsing(vector3fc.x(), int1);
		this.y = Math.roundUsing(vector3fc.y(), int1);
		this.z = Math.roundUsing(vector3fc.z(), int1);
	}

	public Vector3i(Vector2dc vector2dc, float float1, int int1) {
		this.x = Math.roundUsing(vector2dc.x(), int1);
		this.y = Math.roundUsing(vector2dc.y(), int1);
		this.z = Math.roundUsing(float1, int1);
	}

	public Vector3i(Vector3dc vector3dc, int int1) {
		this.x = Math.roundUsing(vector3dc.x(), int1);
		this.y = Math.roundUsing(vector3dc.y(), int1);
		this.z = Math.roundUsing(vector3dc.z(), int1);
	}

	public Vector3i(int[] intArray) {
		this.x = intArray[0];
		this.y = intArray[1];
		this.z = intArray[2];
	}

	public Vector3i(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, byteBuffer.position(), byteBuffer);
	}

	public Vector3i(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
	}

	public Vector3i(IntBuffer intBuffer) {
		MemUtil.INSTANCE.get(this, intBuffer.position(), intBuffer);
	}

	public Vector3i(int int1, IntBuffer intBuffer) {
		MemUtil.INSTANCE.get(this, int1, intBuffer);
	}

	public int x() {
		return this.x;
	}

	public int y() {
		return this.y;
	}

	public int z() {
		return this.z;
	}

	public Vector3i set(Vector3ic vector3ic) {
		this.x = vector3ic.x();
		this.y = vector3ic.y();
		this.z = vector3ic.z();
		return this;
	}

	public Vector3i set(Vector3dc vector3dc) {
		this.x = (int)vector3dc.x();
		this.y = (int)vector3dc.y();
		this.z = (int)vector3dc.z();
		return this;
	}

	public Vector3i set(Vector3dc vector3dc, int int1) {
		this.x = Math.roundUsing(vector3dc.x(), int1);
		this.y = Math.roundUsing(vector3dc.y(), int1);
		this.z = Math.roundUsing(vector3dc.z(), int1);
		return this;
	}

	public Vector3i set(Vector3fc vector3fc, int int1) {
		this.x = Math.roundUsing(vector3fc.x(), int1);
		this.y = Math.roundUsing(vector3fc.y(), int1);
		this.z = Math.roundUsing(vector3fc.z(), int1);
		return this;
	}

	public Vector3i set(Vector2ic vector2ic, int int1) {
		this.x = vector2ic.x();
		this.y = vector2ic.y();
		this.z = int1;
		return this;
	}

	public Vector3i set(int int1) {
		this.x = int1;
		this.y = int1;
		this.z = int1;
		return this;
	}

	public Vector3i set(int int1, int int2, int int3) {
		this.x = int1;
		this.y = int2;
		this.z = int3;
		return this;
	}

	public Vector3i set(int[] intArray) {
		this.x = intArray[0];
		this.y = intArray[1];
		this.z = intArray[2];
		return this;
	}

	public Vector3i set(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, byteBuffer.position(), byteBuffer);
		return this;
	}

	public Vector3i set(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
		return this;
	}

	public Vector3i set(IntBuffer intBuffer) {
		MemUtil.INSTANCE.get(this, intBuffer.position(), intBuffer);
		return this;
	}

	public Vector3i set(int int1, IntBuffer intBuffer) {
		MemUtil.INSTANCE.get(this, int1, intBuffer);
		return this;
	}

	public Vector3i setFromAddress(long long1) {
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
		
		case 2: 
			return this.z;
		
		default: 
			throw new IllegalArgumentException();
		
		}
	}

	public Vector3i setComponent(int int1, int int2) throws IllegalArgumentException {
		switch (int1) {
		case 0: 
			this.x = int2;
			break;
		
		case 1: 
			this.y = int2;
			break;
		
		case 2: 
			this.z = int2;
			break;
		
		default: 
			throw new IllegalArgumentException();
		
		}
		return this;
	}

	public IntBuffer get(IntBuffer intBuffer) {
		MemUtil.INSTANCE.put(this, intBuffer.position(), intBuffer);
		return intBuffer;
	}

	public IntBuffer get(int int1, IntBuffer intBuffer) {
		MemUtil.INSTANCE.put(this, int1, intBuffer);
		return intBuffer;
	}

	public ByteBuffer get(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put(this, byteBuffer.position(), byteBuffer);
		return byteBuffer;
	}

	public ByteBuffer get(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.put(this, int1, byteBuffer);
		return byteBuffer;
	}

	public Vector3ic getToAddress(long long1) {
		if (Options.NO_UNSAFE) {
			throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
		} else {
			MemUtil.MemUtilUnsafe.put(this, long1);
			return this;
		}
	}

	public Vector3i sub(Vector3ic vector3ic) {
		this.x -= vector3ic.x();
		this.y -= vector3ic.y();
		this.z -= vector3ic.z();
		return this;
	}

	public Vector3i sub(Vector3ic vector3ic, Vector3i vector3i) {
		vector3i.x = this.x - vector3ic.x();
		vector3i.y = this.y - vector3ic.y();
		vector3i.z = this.z - vector3ic.z();
		return vector3i;
	}

	public Vector3i sub(int int1, int int2, int int3) {
		this.x -= int1;
		this.y -= int2;
		this.z -= int3;
		return this;
	}

	public Vector3i sub(int int1, int int2, int int3, Vector3i vector3i) {
		vector3i.x = this.x - int1;
		vector3i.y = this.y - int2;
		vector3i.z = this.z - int3;
		return vector3i;
	}

	public Vector3i add(Vector3ic vector3ic) {
		this.x += vector3ic.x();
		this.y += vector3ic.y();
		this.z += vector3ic.z();
		return this;
	}

	public Vector3i add(Vector3ic vector3ic, Vector3i vector3i) {
		vector3i.x = this.x + vector3ic.x();
		vector3i.y = this.y + vector3ic.y();
		vector3i.z = this.z + vector3ic.z();
		return vector3i;
	}

	public Vector3i add(int int1, int int2, int int3) {
		this.x += int1;
		this.y += int2;
		this.z += int3;
		return this;
	}

	public Vector3i add(int int1, int int2, int int3, Vector3i vector3i) {
		vector3i.x = this.x + int1;
		vector3i.y = this.y + int2;
		vector3i.z = this.z + int3;
		return vector3i;
	}

	public Vector3i mul(int int1) {
		this.x *= int1;
		this.y *= int1;
		this.z *= int1;
		return this;
	}

	public Vector3i mul(int int1, Vector3i vector3i) {
		vector3i.x = this.x * int1;
		vector3i.y = this.y * int1;
		vector3i.z = this.z * int1;
		return vector3i;
	}

	public Vector3i mul(Vector3ic vector3ic) {
		this.x *= vector3ic.x();
		this.y *= vector3ic.y();
		this.z *= vector3ic.z();
		return this;
	}

	public Vector3i mul(Vector3ic vector3ic, Vector3i vector3i) {
		vector3i.x = this.x * vector3ic.x();
		vector3i.y = this.y * vector3ic.y();
		vector3i.z = this.z * vector3ic.z();
		return vector3i;
	}

	public Vector3i mul(int int1, int int2, int int3) {
		this.x *= int1;
		this.y *= int2;
		this.z *= int3;
		return this;
	}

	public Vector3i mul(int int1, int int2, int int3, Vector3i vector3i) {
		vector3i.x = this.x * int1;
		vector3i.y = this.y * int2;
		vector3i.z = this.z * int3;
		return vector3i;
	}

	public Vector3i div(float float1) {
		float float2 = 1.0F / float1;
		this.x = (int)((float)this.x * float2);
		this.y = (int)((float)this.y * float2);
		this.z = (int)((float)this.z * float2);
		return this;
	}

	public Vector3i div(float float1, Vector3i vector3i) {
		float float2 = 1.0F / float1;
		vector3i.x = (int)((float)this.x * float2);
		vector3i.y = (int)((float)this.y * float2);
		vector3i.z = (int)((float)this.z * float2);
		return vector3i;
	}

	public Vector3i div(int int1) {
		this.x /= int1;
		this.y /= int1;
		this.z /= int1;
		return this;
	}

	public Vector3i div(int int1, Vector3i vector3i) {
		vector3i.x = this.x / int1;
		vector3i.y = this.y / int1;
		vector3i.z = this.z / int1;
		return vector3i;
	}

	public long lengthSquared() {
		return (long)(this.x * this.x + this.y * this.y + this.z * this.z);
	}

	public static long lengthSquared(int int1, int int2, int int3) {
		return (long)(int1 * int1 + int2 * int2 + int3 * int3);
	}

	public double length() {
		return (double)Math.sqrt((float)(this.x * this.x + this.y * this.y + this.z * this.z));
	}

	public static double length(int int1, int int2, int int3) {
		return (double)Math.sqrt((float)(int1 * int1 + int2 * int2 + int3 * int3));
	}

	public double distance(Vector3ic vector3ic) {
		int int1 = this.x - vector3ic.x();
		int int2 = this.y - vector3ic.y();
		int int3 = this.z - vector3ic.z();
		return (double)Math.sqrt((float)(int1 * int1 + int2 * int2 + int3 * int3));
	}

	public double distance(int int1, int int2, int int3) {
		int int4 = this.x - int1;
		int int5 = this.y - int2;
		int int6 = this.z - int3;
		return (double)Math.sqrt((float)(int4 * int4 + int5 * int5 + int6 * int6));
	}

	public long gridDistance(Vector3ic vector3ic) {
		return (long)(Math.abs(vector3ic.x() - this.x()) + Math.abs(vector3ic.y() - this.y()) + Math.abs(vector3ic.z() - this.z()));
	}

	public long gridDistance(int int1, int int2, int int3) {
		return (long)(Math.abs(int1 - this.x()) + Math.abs(int2 - this.y()) + Math.abs(int3 - this.z()));
	}

	public long distanceSquared(Vector3ic vector3ic) {
		int int1 = this.x - vector3ic.x();
		int int2 = this.y - vector3ic.y();
		int int3 = this.z - vector3ic.z();
		return (long)(int1 * int1 + int2 * int2 + int3 * int3);
	}

	public long distanceSquared(int int1, int int2, int int3) {
		int int4 = this.x - int1;
		int int5 = this.y - int2;
		int int6 = this.z - int3;
		return (long)(int4 * int4 + int5 * int5 + int6 * int6);
	}

	public static double distance(int int1, int int2, int int3, int int4, int int5, int int6) {
		return (double)Math.sqrt((float)distanceSquared(int1, int2, int3, int4, int5, int6));
	}

	public static long distanceSquared(int int1, int int2, int int3, int int4, int int5, int int6) {
		int int7 = int1 - int4;
		int int8 = int2 - int5;
		int int9 = int3 - int6;
		return (long)(int7 * int7 + int8 * int8 + int9 * int9);
	}

	public Vector3i zero() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
		return this;
	}

	public String toString() {
		return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
	}

	public String toString(NumberFormat numberFormat) {
		String string = numberFormat.format((long)this.x);
		return "(" + string + " " + numberFormat.format((long)this.y) + " " + numberFormat.format((long)this.z) + ")";
	}

	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeInt(this.x);
		objectOutput.writeInt(this.y);
		objectOutput.writeInt(this.z);
	}

	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		this.x = objectInput.readInt();
		this.y = objectInput.readInt();
		this.z = objectInput.readInt();
	}

	public Vector3i negate() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
		return this;
	}

	public Vector3i negate(Vector3i vector3i) {
		vector3i.x = -this.x;
		vector3i.y = -this.y;
		vector3i.z = -this.z;
		return vector3i;
	}

	public Vector3i min(Vector3ic vector3ic) {
		this.x = this.x < vector3ic.x() ? this.x : vector3ic.x();
		this.y = this.y < vector3ic.y() ? this.y : vector3ic.y();
		this.z = this.z < vector3ic.z() ? this.z : vector3ic.z();
		return this;
	}

	public Vector3i min(Vector3ic vector3ic, Vector3i vector3i) {
		vector3i.x = this.x < vector3ic.x() ? this.x : vector3ic.x();
		vector3i.y = this.y < vector3ic.y() ? this.y : vector3ic.y();
		vector3i.z = this.z < vector3ic.z() ? this.z : vector3ic.z();
		return vector3i;
	}

	public Vector3i max(Vector3ic vector3ic) {
		this.x = this.x > vector3ic.x() ? this.x : vector3ic.x();
		this.y = this.y > vector3ic.y() ? this.y : vector3ic.y();
		this.z = this.z > vector3ic.z() ? this.z : vector3ic.z();
		return this;
	}

	public Vector3i max(Vector3ic vector3ic, Vector3i vector3i) {
		vector3i.x = this.x > vector3ic.x() ? this.x : vector3ic.x();
		vector3i.y = this.y > vector3ic.y() ? this.y : vector3ic.y();
		vector3i.z = this.z > vector3ic.z() ? this.z : vector3ic.z();
		return vector3i;
	}

	public int maxComponent() {
		float float1 = (float)Math.abs(this.x);
		float float2 = (float)Math.abs(this.y);
		float float3 = (float)Math.abs(this.z);
		if (float1 >= float2 && float1 >= float3) {
			return 0;
		} else {
			return float2 >= float3 ? 1 : 2;
		}
	}

	public int minComponent() {
		float float1 = (float)Math.abs(this.x);
		float float2 = (float)Math.abs(this.y);
		float float3 = (float)Math.abs(this.z);
		if (float1 < float2 && float1 < float3) {
			return 0;
		} else {
			return float2 < float3 ? 1 : 2;
		}
	}

	public Vector3i absolute() {
		this.x = Math.abs(this.x);
		this.y = Math.abs(this.y);
		this.z = Math.abs(this.z);
		return this;
	}

	public Vector3i absolute(Vector3i vector3i) {
		vector3i.x = Math.abs(this.x);
		vector3i.y = Math.abs(this.y);
		vector3i.z = Math.abs(this.z);
		return vector3i;
	}

	public int hashCode() {
		byte byte1 = 1;
		int int1 = 31 * byte1 + this.x;
		int1 = 31 * int1 + this.y;
		int1 = 31 * int1 + this.z;
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
			Vector3i vector3i = (Vector3i)object;
			if (this.x != vector3i.x) {
				return false;
			} else if (this.y != vector3i.y) {
				return false;
			} else {
				return this.z == vector3i.z;
			}
		}
	}

	public boolean equals(int int1, int int2, int int3) {
		if (this.x != int1) {
			return false;
		} else if (this.y != int2) {
			return false;
		} else {
			return this.z == int3;
		}
	}
}
