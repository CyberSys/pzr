package zombie.core.utils;

import java.util.Arrays;
import zombie.debug.DebugLog;


public class ObjectCube implements Cloneable {
	private final int width;
	private final int height;
	private final int depth;
	private final Object[] value;

	public ObjectCube(int int1, int int2, int int3) {
		DebugLog.log("Created object cube of size " + int1 + "x" + int2 + "x" + int3 + " (" + int1 * int2 * int3 * 4 + " bytes)");
		this.width = int1;
		this.height = int2;
		this.depth = int3;
		this.value = new Object[int1 * int2 * int3];
	}

	public ObjectCube clone() throws CloneNotSupportedException {
		ObjectCube objectCube = new ObjectCube(this.width, this.height, this.depth);
		System.arraycopy(this.value, 0, objectCube.value, 0, this.value.length);
		return objectCube;
	}

	public void clear() {
		Arrays.fill(this.value, (Object)null);
	}

	public void fill(Object object) {
		Arrays.fill(this.value, object);
	}

	private int getIndex(int int1, int int2, int int3) {
		return int1 >= 0 && int2 >= 0 && int3 >= 0 && int1 < this.width && int2 < this.height && int3 < this.depth ? int1 + int2 * this.width + int3 * this.width * this.height : -1;
	}

	public Object getValue(int int1, int int2, int int3) {
		int int4 = this.getIndex(int1, int2, int3);
		return int4 == -1 ? null : this.value[int4];
	}

	public void setValue(int int1, int int2, int int3, Object object) {
		int int4 = this.getIndex(int1, int2, int3);
		if (int4 != -1) {
			this.value[int4] = object;
		}
	}

	public final int getWidth() {
		return this.width;
	}

	public final int getHeight() {
		return this.height;
	}

	public int getDepth() {
		return this.depth;
	}
}
