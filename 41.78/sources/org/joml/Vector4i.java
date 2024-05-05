package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.NumberFormat;


public class Vector4i implements Externalizable,Vector4ic {
	private static final long serialVersionUID = 1L;
	public int x;
	public int y;
	public int z;
	public int w;

	public Vector4i() {
		this.w = 1;
	}

	public Vector4i(Vector4ic vector4ic) {
		this.x = vector4ic.x();
		this.y = vector4ic.y();
		this.z = vector4ic.z();
		this.w = vector4ic.w();
	}

	public Vector4i(Vector3ic vector3ic, int int1) {
		this.x = vector3ic.x();
		this.y = vector3ic.y();
		this.z = vector3ic.z();
		this.w = int1;
	}

	public Vector4i(Vector2ic vector2ic, int int1, int int2) {
		this.x = vector2ic.x();
		this.y = vector2ic.y();
		this.z = int1;
		this.w = int2;
	}

	public Vector4i(Vector3fc vector3fc, float float1, int int1) {
		this.x = Math.roundUsing(vector3fc.x(), int1);
		this.y = Math.roundUsing(vector3fc.y(), int1);
		this.z = Math.roundUsing(vector3fc.z(), int1);
		float1 = (float)Math.roundUsing(float1, int1);
	}

	public Vector4i(Vector4fc vector4fc, int int1) {
		this.x = Math.roundUsing(vector4fc.x(), int1);
		this.y = Math.roundUsing(vector4fc.y(), int1);
		this.z = Math.roundUsing(vector4fc.z(), int1);
		this.w = Math.roundUsing(vector4fc.w(), int1);
	}

	public Vector4i(Vector4dc vector4dc, int int1) {
		this.x = Math.roundUsing(vector4dc.x(), int1);
		this.y = Math.roundUsing(vector4dc.y(), int1);
		this.z = Math.roundUsing(vector4dc.z(), int1);
		this.w = Math.roundUsing(vector4dc.w(), int1);
	}

	public Vector4i(int int1) {
		this.x = int1;
		this.y = int1;
		this.z = int1;
		this.w = int1;
	}

	public Vector4i(int int1, int int2, int int3, int int4) {
		this.x = int1;
		this.y = int2;
		this.z = int3;
		this.w = int4;
	}

	public Vector4i(int[] intArray) {
		this.x = intArray[0];
		this.y = intArray[1];
		this.z = intArray[2];
		this.w = intArray[3];
	}

	public Vector4i(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, byteBuffer.position(), byteBuffer);
	}

	public Vector4i(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
	}

	public Vector4i(IntBuffer intBuffer) {
		MemUtil.INSTANCE.get(this, intBuffer.position(), intBuffer);
	}

	public Vector4i(int int1, IntBuffer intBuffer) {
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

	public int w() {
		return this.w;
	}

	public Vector4i set(Vector4ic vector4ic) {
		this.x = vector4ic.x();
		this.y = vector4ic.y();
		this.z = vector4ic.z();
		this.w = vector4ic.w();
		return this;
	}

	public Vector4i set(Vector4dc vector4dc) {
		this.x = (int)vector4dc.x();
		this.y = (int)vector4dc.y();
		this.z = (int)vector4dc.z();
		this.w = (int)vector4dc.w();
		return this;
	}

	public Vector4i set(Vector4dc vector4dc, int int1) {
		this.x = Math.roundUsing(vector4dc.x(), int1);
		this.y = Math.roundUsing(vector4dc.y(), int1);
		this.z = Math.roundUsing(vector4dc.z(), int1);
		this.w = Math.roundUsing(vector4dc.w(), int1);
		return this;
	}

	public Vector4i set(Vector4fc vector4fc, int int1) {
		this.x = Math.roundUsing(vector4fc.x(), int1);
		this.y = Math.roundUsing(vector4fc.y(), int1);
		this.z = Math.roundUsing(vector4fc.z(), int1);
		this.w = Math.roundUsing(vector4fc.w(), int1);
		return this;
	}

	public Vector4i set(Vector3ic vector3ic, int int1) {
		this.x = vector3ic.x();
		this.y = vector3ic.y();
		this.z = vector3ic.z();
		this.w = int1;
		return this;
	}

	public Vector4i set(Vector2ic vector2ic, int int1, int int2) {
		this.x = vector2ic.x();
		this.y = vector2ic.y();
		this.z = int1;
		this.w = int2;
		return this;
	}

	public Vector4i set(int int1) {
		this.x = int1;
		this.y = int1;
		this.z = int1;
		this.w = int1;
		return this;
	}

	public Vector4i set(int int1, int int2, int int3, int int4) {
		this.x = int1;
		this.y = int2;
		this.z = int3;
		this.w = int4;
		return this;
	}

	public Vector4i set(int[] intArray) {
		this.x = intArray[0];
		this.y = intArray[1];
		this.z = intArray[2];
		this.w = intArray[2];
		return this;
	}

	public Vector4i set(ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, byteBuffer.position(), byteBuffer);
		return this;
	}

	public Vector4i set(int int1, ByteBuffer byteBuffer) {
		MemUtil.INSTANCE.get(this, int1, byteBuffer);
		return this;
	}

	public Vector4i set(IntBuffer intBuffer) {
		MemUtil.INSTANCE.get(this, intBuffer.position(), intBuffer);
		return this;
	}

	public Vector4i set(int int1, IntBuffer intBuffer) {
		MemUtil.INSTANCE.get(this, int1, intBuffer);
		return this;
	}

	public Vector4i setFromAddress(long long1) {
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
		
		case 3: 
			return this.w;
		
		default: 
			throw new IllegalArgumentException();
		
		}
	}

	public int maxComponent() {
		int int1 = Math.abs(this.x);
		int int2 = Math.abs(this.y);
		int int3 = Math.abs(this.z);
		int int4 = Math.abs(this.w);
		if (int1 >= int2 && int1 >= int3 && int1 >= int4) {
			return 0;
		} else if (int2 >= int3 && int2 >= int4) {
			return 1;
		} else {
			return int3 >= int4 ? 2 : 3;
		}
	}

	public int minComponent() {
		int int1 = Math.abs(this.x);
		int int2 = Math.abs(this.y);
		int int3 = Math.abs(this.z);
		int int4 = Math.abs(this.w);
		if (int1 < int2 && int1 < int3 && int1 < int4) {
			return 0;
		} else if (int2 < int3 && int2 < int4) {
			return 1;
		} else {
			return int3 < int4 ? 2 : 3;
		}
	}

	public Vector4i setComponent(int int1, int int2) throws IllegalArgumentException {
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
		
		case 3: 
			this.w = int2;
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

	public Vector4ic getToAddress(long long1) {
		if (Options.NO_UNSAFE) {
			throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
		} else {
			MemUtil.MemUtilUnsafe.put(this, long1);
			return this;
		}
	}

	public Vector4i sub(Vector4ic vector4ic) {
		this.x -= vector4ic.x();
		this.y -= vector4ic.y();
		this.z -= vector4ic.z();
		this.w -= vector4ic.w();
		return this;
	}

	public Vector4i sub(int int1, int int2, int int3, int int4) {
		this.x -= int1;
		this.y -= int2;
		this.z -= int3;
		this.w -= int4;
		return this;
	}

	public Vector4i sub(Vector4ic vector4ic, Vector4i vector4i) {
		vector4i.x = this.x - vector4ic.x();
		vector4i.y = this.y - vector4ic.y();
		vector4i.z = this.z - vector4ic.z();
		vector4i.w = this.w - vector4ic.w();
		return vector4i;
	}

	public Vector4i sub(int int1, int int2, int int3, int int4, Vector4i vector4i) {
		vector4i.x = this.x - int1;
		vector4i.y = this.y - int2;
		vector4i.z = this.z - int3;
		vector4i.w = this.w - int4;
		return vector4i;
	}

	public Vector4i add(Vector4ic vector4ic) {
		this.x += vector4ic.x();
		this.y += vector4ic.y();
		this.z += vector4ic.z();
		this.w += vector4ic.w();
		return this;
	}

	public Vector4i add(Vector4ic vector4ic, Vector4i vector4i) {
		vector4i.x = this.x + vector4ic.x();
		vector4i.y = this.y + vector4ic.y();
		vector4i.z = this.z + vector4ic.z();
		vector4i.w = this.w + vector4ic.w();
		return vector4i;
	}

	public Vector4i add(int int1, int int2, int int3, int int4) {
		this.x += int1;
		this.y += int2;
		this.z += int3;
		this.w += int4;
		return this;
	}

	public Vector4i add(int int1, int int2, int int3, int int4, Vector4i vector4i) {
		vector4i.x = this.x + int1;
		vector4i.y = this.y + int2;
		vector4i.z = this.z + int3;
		vector4i.w = this.w + int4;
		return vector4i;
	}

	public Vector4i mul(Vector4ic vector4ic) {
		this.x *= vector4ic.x();
		this.y *= vector4ic.y();
		this.z *= vector4ic.z();
		this.w *= vector4ic.w();
		return this;
	}

	public Vector4i mul(Vector4ic vector4ic, Vector4i vector4i) {
		vector4i.x = this.x * vector4ic.x();
		vector4i.y = this.y * vector4ic.y();
		vector4i.z = this.z * vector4ic.z();
		vector4i.w = this.w * vector4ic.w();
		return vector4i;
	}

	public Vector4i div(Vector4ic vector4ic) {
		this.x /= vector4ic.x();
		this.y /= vector4ic.y();
		this.z /= vector4ic.z();
		this.w /= vector4ic.w();
		return this;
	}

	public Vector4i div(Vector4ic vector4ic, Vector4i vector4i) {
		vector4i.x = this.x / vector4ic.x();
		vector4i.y = this.y / vector4ic.y();
		vector4i.z = this.z / vector4ic.z();
		vector4i.w = this.w / vector4ic.w();
		return vector4i;
	}

	public Vector4i mul(int int1) {
		this.x *= int1;
		this.y *= int1;
		this.z *= int1;
		this.w *= int1;
		return this;
	}

	public Vector4i mul(int int1, Vector4i vector4i) {
		vector4i.x = this.x * int1;
		vector4i.y = this.y * int1;
		vector4i.z = this.z * int1;
		vector4i.w = this.w * int1;
		return vector4i;
	}

	public Vector4i div(float float1) {
		float float2 = 1.0F / float1;
		this.x = (int)((float)this.x * float2);
		this.y = (int)((float)this.y * float2);
		this.z = (int)((float)this.z * float2);
		this.w = (int)((float)this.w * float2);
		return this;
	}

	public Vector4i div(float float1, Vector4i vector4i) {
		float float2 = 1.0F / float1;
		vector4i.x = (int)((float)this.x * float2);
		vector4i.y = (int)((float)this.y * float2);
		vector4i.z = (int)((float)this.z * float2);
		vector4i.w = (int)((float)this.w * float2);
		return vector4i;
	}

	public Vector4i div(int int1) {
		this.x /= int1;
		this.y /= int1;
		this.z /= int1;
		this.w /= int1;
		return this;
	}

	public Vector4i div(int int1, Vector4i vector4i) {
		vector4i.x = this.x / int1;
		vector4i.y = this.y / int1;
		vector4i.z = this.z / int1;
		vector4i.w = this.w / int1;
		return vector4i;
	}

	public long lengthSquared() {
		return (long)(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
	}

	public static long lengthSquared(int int1, int int2, int int3, int int4) {
		return (long)(int1 * int1 + int2 * int2 + int3 * int3 + int4 * int4);
	}

	public double length() {
		return (double)Math.sqrt((float)(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w));
	}

	public static double length(int int1, int int2, int int3, int int4) {
		return (double)Math.sqrt((float)(int1 * int1 + int2 * int2 + int3 * int3 + int4 * int4));
	}

	public double distance(Vector4ic vector4ic) {
		int int1 = this.x - vector4ic.x();
		int int2 = this.y - vector4ic.y();
		int int3 = this.z - vector4ic.z();
		int int4 = this.w - vector4ic.w();
		return (double)Math.sqrt(Math.fma((float)int1, (float)int1, Math.fma((float)int2, (float)int2, Math.fma((float)int3, (float)int3, (float)(int4 * int4)))));
	}

	public double distance(int int1, int int2, int int3, int int4) {
		int int5 = this.x - int1;
		int int6 = this.y - int2;
		int int7 = this.z - int3;
		int int8 = this.w - int4;
		return (double)Math.sqrt(Math.fma((float)int5, (float)int5, Math.fma((float)int6, (float)int6, Math.fma((float)int7, (float)int7, (float)(int8 * int8)))));
	}

	public long gridDistance(Vector4ic vector4ic) {
		return (long)(Math.abs(vector4ic.x() - this.x()) + Math.abs(vector4ic.y() - this.y()) + Math.abs(vector4ic.z() - this.z()) + Math.abs(vector4ic.w() - this.w()));
	}

	public long gridDistance(int int1, int int2, int int3, int int4) {
		return (long)(Math.abs(int1 - this.x()) + Math.abs(int2 - this.y()) + Math.abs(int3 - this.z()) + Math.abs(int4 - this.w()));
	}

	public int distanceSquared(Vector4ic vector4ic) {
		int int1 = this.x - vector4ic.x();
		int int2 = this.y - vector4ic.y();
		int int3 = this.z - vector4ic.z();
		int int4 = this.w - vector4ic.w();
		return int1 * int1 + int2 * int2 + int3 * int3 + int4 * int4;
	}

	public int distanceSquared(int int1, int int2, int int3, int int4) {
		int int5 = this.x - int1;
		int int6 = this.y - int2;
		int int7 = this.z - int3;
		int int8 = this.w - int4;
		return int5 * int5 + int6 * int6 + int7 * int7 + int8 * int8;
	}

	public static double distance(int int1, int int2, int int3, int int4, int int5, int int6, int int7, int int8) {
		int int9 = int1 - int5;
		int int10 = int2 - int6;
		int int11 = int3 - int7;
		int int12 = int4 - int8;
		return (double)Math.sqrt((float)(int9 * int9 + int10 * int10 + int11 * int11 + int12 * int12));
	}

	public static long distanceSquared(int int1, int int2, int int3, int int4, int int5, int int6, int int7, int int8) {
		int int9 = int1 - int5;
		int int10 = int2 - int6;
		int int11 = int3 - int7;
		int int12 = int4 - int8;
		return (long)(int9 * int9 + int10 * int10 + int11 * int11 + int12 * int12);
	}

	public int dot(Vector4ic vector4ic) {
		return this.x * vector4ic.x() + this.y * vector4ic.y() + this.z * vector4ic.z() + this.w * vector4ic.w();
	}

	public Vector4i zero() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
		this.w = 0;
		return this;
	}

	public Vector4i negate() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
		this.w = -this.w;
		return this;
	}

	public Vector4i negate(Vector4i vector4i) {
		vector4i.x = -this.x;
		vector4i.y = -this.y;
		vector4i.z = -this.z;
		vector4i.w = -this.w;
		return vector4i;
	}

	public String toString() {
		return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
	}

	public String toString(NumberFormat numberFormat) {
		String string = numberFormat.format((long)this.x);
		return "(" + string + " " + numberFormat.format((long)this.y) + " " + numberFormat.format((long)this.z) + " " + numberFormat.format((long)this.w) + ")";
	}

	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeInt(this.x);
		objectOutput.writeInt(this.y);
		objectOutput.writeInt(this.z);
		objectOutput.writeInt(this.w);
	}

	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		this.x = objectInput.readInt();
		this.y = objectInput.readInt();
		this.z = objectInput.readInt();
		this.w = objectInput.readInt();
	}

	public Vector4i min(Vector4ic vector4ic) {
		this.x = this.x < vector4ic.x() ? this.x : vector4ic.x();
		this.y = this.y < vector4ic.y() ? this.y : vector4ic.y();
		this.z = this.z < vector4ic.z() ? this.z : vector4ic.z();
		this.w = this.w < vector4ic.w() ? this.w : vector4ic.w();
		return this;
	}

	public Vector4i min(Vector4ic vector4ic, Vector4i vector4i) {
		vector4i.x = this.x < vector4ic.x() ? this.x : vector4ic.x();
		vector4i.y = this.y < vector4ic.y() ? this.y : vector4ic.y();
		vector4i.z = this.z < vector4ic.z() ? this.z : vector4ic.z();
		vector4i.w = this.w < vector4ic.w() ? this.w : vector4ic.w();
		return vector4i;
	}

	public Vector4i max(Vector4ic vector4ic) {
		this.x = this.x > vector4ic.x() ? this.x : vector4ic.x();
		this.y = this.y > vector4ic.y() ? this.y : vector4ic.y();
		this.z = this.z > vector4ic.z() ? this.z : vector4ic.z();
		this.w = this.w > vector4ic.w() ? this.w : vector4ic.w();
		return this;
	}

	public Vector4i max(Vector4ic vector4ic, Vector4i vector4i) {
		vector4i.x = this.x > vector4ic.x() ? this.x : vector4ic.x();
		vector4i.y = this.y > vector4ic.y() ? this.y : vector4ic.y();
		vector4i.z = this.z > vector4ic.z() ? this.z : vector4ic.z();
		vector4i.w = this.w > vector4ic.w() ? this.w : vector4ic.w();
		return vector4i;
	}

	public Vector4i absolute() {
		this.x = Math.abs(this.x);
		this.y = Math.abs(this.y);
		this.z = Math.abs(this.z);
		this.w = Math.abs(this.w);
		return this;
	}

	public Vector4i absolute(Vector4i vector4i) {
		vector4i.x = Math.abs(this.x);
		vector4i.y = Math.abs(this.y);
		vector4i.z = Math.abs(this.z);
		vector4i.w = Math.abs(this.w);
		return vector4i;
	}

	public int hashCode() {
		byte byte1 = 1;
		int int1 = 31 * byte1 + this.x;
		int1 = 31 * int1 + this.y;
		int1 = 31 * int1 + this.z;
		int1 = 31 * int1 + this.w;
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
			Vector4i vector4i = (Vector4i)object;
			if (this.x != vector4i.x) {
				return false;
			} else if (this.y != vector4i.y) {
				return false;
			} else if (this.z != vector4i.z) {
				return false;
			} else {
				return this.w == vector4i.w;
			}
		}
	}

	public boolean equals(int int1, int int2, int int3, int int4) {
		if (this.x != int1) {
			return false;
		} else if (this.y != int2) {
			return false;
		} else if (this.z != int3) {
			return false;
		} else {
			return this.w == int4;
		}
	}
}
