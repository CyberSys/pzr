package zombie.vehicles;

import java.io.BufferedWriter;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.ListIterator;
import org.joml.Quaternionf;
import zombie.GameTime;


public class VehicleInterpolation {
	static final boolean PR = false;
	static final boolean DEBUG = false;
	BufferedWriter DebugInDataWriter;
	BufferedWriter DebugOutDataWriter;
	public int physicsDelayMs;
	public int physicsBufferMs;
	boolean buffering;
	long serverDelay;
	VehicleInterpolationData lastData;
	LinkedList dataList = new LinkedList();
	private static final ArrayDeque pool = new ArrayDeque();
	private float[] currentVehicleData = new float[23];
	private float[] tempVehicleData = new float[23];
	private boolean isSetCurrentVehicleData = false;
	private Quaternionf javaxQuat4f = new Quaternionf();

	VehicleInterpolation(int int1) {
		this.physicsDelayMs = int1;
		this.physicsBufferMs = int1;
		this.buffering = true;
	}

	protected void finalize() {
	}

	public void interpolationDataAdd(ByteBuffer byteBuffer) {
		VehicleInterpolationData vehicleInterpolationData = pool.isEmpty() ? new VehicleInterpolationData() : (VehicleInterpolationData)pool.pop();
		vehicleInterpolationData.time = byteBuffer.getLong();
		vehicleInterpolationData.x = byteBuffer.getFloat();
		vehicleInterpolationData.y = byteBuffer.getFloat();
		vehicleInterpolationData.z = byteBuffer.getFloat();
		vehicleInterpolationData.qx = byteBuffer.getFloat();
		vehicleInterpolationData.qy = byteBuffer.getFloat();
		vehicleInterpolationData.qz = byteBuffer.getFloat();
		vehicleInterpolationData.qw = byteBuffer.getFloat();
		vehicleInterpolationData.vx = byteBuffer.getFloat();
		vehicleInterpolationData.vy = byteBuffer.getFloat();
		vehicleInterpolationData.vz = byteBuffer.getFloat();
		vehicleInterpolationData.setNumWheels(byteBuffer.getShort());
		for (int int1 = 0; int1 < vehicleInterpolationData.w_count; ++int1) {
			vehicleInterpolationData.w_st[int1] = byteBuffer.getFloat();
			vehicleInterpolationData.w_rt[int1] = byteBuffer.getFloat();
			vehicleInterpolationData.w_si[int1] = byteBuffer.getFloat();
		}

		long long1 = GameTime.getServerTime() - vehicleInterpolationData.time;
		if (Math.abs(this.serverDelay - long1) > 2000000000L) {
			this.serverDelay = long1;
		}

		this.serverDelay = (long)((double)this.serverDelay + (double)(long1 - this.serverDelay) * 0.1);
		ListIterator listIterator = this.dataList.listIterator();
		long long2 = 0L;
		while (listIterator.hasNext()) {
			VehicleInterpolationData vehicleInterpolationData2 = (VehicleInterpolationData)listIterator.next();
			if (vehicleInterpolationData2.time > vehicleInterpolationData.time) {
				if (listIterator.hasPrevious()) {
					listIterator.previous();
					listIterator.add(vehicleInterpolationData);
				} else {
					this.dataList.addFirst(vehicleInterpolationData);
				}

				return;
			}

			if (vehicleInterpolationData.time - vehicleInterpolationData2.time > (long)((this.physicsBufferMs + this.physicsDelayMs) * 1000000)) {
				pool.push(vehicleInterpolationData2);
				listIterator.remove();
			} else if (vehicleInterpolationData2.time > long2) {
				long2 = vehicleInterpolationData2.time;
			}
		}

		if (long2 == 0L || vehicleInterpolationData.time - long2 > (long)((this.physicsBufferMs + this.physicsDelayMs) * 1000000)) {
			if (!this.dataList.isEmpty()) {
				pool.addAll(this.dataList);
				this.dataList.clear();
			}

			this.buffering = true;
		}

		this.dataList.addLast(vehicleInterpolationData);
	}

	public boolean interpolationDataGet(float[] floatArray) {
		VehicleInterpolationData vehicleInterpolationData;
		if (!this.buffering) {
			if (this.dataList.size() == 0) {
				this.buffering = true;
				return false;
			}
		} else {
			ListIterator listIterator = this.dataList.listIterator();
			long long1 = 0L;
			long long2 = 0L;
			while (true) {
				if (!listIterator.hasNext()) {
					if (long1 != 0L && long2 - long1 >= (long)(this.physicsDelayMs * 1000000)) {
						this.buffering = false;
						break;
					}

					return false;
				}

				vehicleInterpolationData = (VehicleInterpolationData)listIterator.next();
				if (long1 == 0L || vehicleInterpolationData.time < long1) {
					long1 = vehicleInterpolationData.time;
				}

				if (vehicleInterpolationData.time > long2) {
					long2 = vehicleInterpolationData.time;
				}
			}
		}

		VehicleInterpolationData vehicleInterpolationData2 = null;
		VehicleInterpolationData vehicleInterpolationData3 = null;
		long long3 = GameTime.getServerTime() - this.serverDelay - (long)(this.physicsDelayMs * 1000000);
		if (this.physicsDelayMs > 0) {
			ListIterator listIterator2 = this.dataList.listIterator();
			while (listIterator2.hasNext()) {
				vehicleInterpolationData = (VehicleInterpolationData)listIterator2.next();
				if (vehicleInterpolationData.time >= long3) {
					vehicleInterpolationData3 = vehicleInterpolationData;
					if (!listIterator2.hasPrevious()) {
						return false;
					}

					listIterator2.previous();
					if (!listIterator2.hasPrevious()) {
						return false;
					}

					vehicleInterpolationData2 = (VehicleInterpolationData)listIterator2.previous();
					break;
				}
			}

			while (listIterator2.hasPrevious()) {
				vehicleInterpolationData = (VehicleInterpolationData)listIterator2.previous();
				pool.push(vehicleInterpolationData);
				listIterator2.remove();
			}
		} else {
			vehicleInterpolationData3 = (VehicleInterpolationData)this.dataList.getFirst();
		}

		if (vehicleInterpolationData3 == null) {
			this.buffering = true;
			if (!this.dataList.isEmpty()) {
				pool.addAll(this.dataList);
				this.dataList.clear();
			}

			return false;
		} else {
			int int1;
			if (vehicleInterpolationData2 == null) {
				byte byte1 = 0;
				int int2 = byte1 + 1;
				floatArray[byte1] = vehicleInterpolationData3.x;
				floatArray[int2++] = vehicleInterpolationData3.y;
				floatArray[int2++] = vehicleInterpolationData3.z;
				floatArray[int2++] = vehicleInterpolationData3.qx;
				floatArray[int2++] = vehicleInterpolationData3.qy;
				floatArray[int2++] = vehicleInterpolationData3.qz;
				floatArray[int2++] = vehicleInterpolationData3.qw;
				floatArray[int2++] = vehicleInterpolationData3.vx;
				floatArray[int2++] = vehicleInterpolationData3.vy;
				floatArray[int2++] = vehicleInterpolationData3.vz;
				floatArray[int2++] = (float)vehicleInterpolationData3.w_count;
				for (int1 = 0; int1 < vehicleInterpolationData3.w_count; ++int1) {
					floatArray[int2++] = vehicleInterpolationData3.w_st[int1];
					floatArray[int2++] = vehicleInterpolationData3.w_rt[int1];
					floatArray[int2++] = vehicleInterpolationData3.w_si[int1];
				}

				return true;
			} else {
				float float1 = (float)(long3 - vehicleInterpolationData2.time) / (float)(vehicleInterpolationData3.time - vehicleInterpolationData2.time);
				byte byte2 = 0;
				int1 = byte2 + 1;
				floatArray[byte2] = (vehicleInterpolationData3.x - vehicleInterpolationData2.x) * float1 + vehicleInterpolationData2.x;
				floatArray[int1++] = (vehicleInterpolationData3.y - vehicleInterpolationData2.y) * float1 + vehicleInterpolationData2.y;
				floatArray[int1++] = (vehicleInterpolationData3.z - vehicleInterpolationData2.z) * float1 + vehicleInterpolationData2.z;
				float float2 = vehicleInterpolationData3.qx * vehicleInterpolationData2.qx + vehicleInterpolationData3.qy * vehicleInterpolationData2.qy + vehicleInterpolationData3.qz * vehicleInterpolationData2.qz + vehicleInterpolationData3.qw * vehicleInterpolationData2.qw;
				if (float2 < 0.0F) {
					vehicleInterpolationData3.qx *= -1.0F;
					vehicleInterpolationData3.qy *= -1.0F;
					vehicleInterpolationData3.qz *= -1.0F;
					vehicleInterpolationData3.qw *= -1.0F;
				}

				floatArray[int1++] = vehicleInterpolationData2.qx * (1.0F - float1) + vehicleInterpolationData3.qx * float1;
				floatArray[int1++] = vehicleInterpolationData2.qy * (1.0F - float1) + vehicleInterpolationData3.qy * float1;
				floatArray[int1++] = vehicleInterpolationData2.qz * (1.0F - float1) + vehicleInterpolationData3.qz * float1;
				floatArray[int1++] = vehicleInterpolationData2.qw * (1.0F - float1) + vehicleInterpolationData3.qw * float1;
				floatArray[int1++] = (vehicleInterpolationData3.vx - vehicleInterpolationData2.vx) * float1 + vehicleInterpolationData2.vx;
				floatArray[int1++] = (vehicleInterpolationData3.vy - vehicleInterpolationData2.vy) * float1 + vehicleInterpolationData2.vy;
				floatArray[int1++] = (vehicleInterpolationData3.vz - vehicleInterpolationData2.vz) * float1 + vehicleInterpolationData2.vz;
				floatArray[int1++] = (float)vehicleInterpolationData3.w_count;
				for (int int3 = 0; int3 < vehicleInterpolationData3.w_count; ++int3) {
					floatArray[int1++] = (vehicleInterpolationData3.w_st[int3] - vehicleInterpolationData2.w_st[int3]) * float1 + vehicleInterpolationData2.w_st[int3];
					floatArray[int1++] = (vehicleInterpolationData3.w_rt[int3] - vehicleInterpolationData2.w_rt[int3]) * float1 + vehicleInterpolationData2.w_rt[int3];
					floatArray[int1++] = (vehicleInterpolationData3.w_si[int3] - vehicleInterpolationData2.w_si[int3]) * float1 + vehicleInterpolationData2.w_si[int3];
				}

				return true;
			}
		}
	}

	public boolean interpolationDataGetPR(float[] floatArray) {
		return this.interpolationDataGet(floatArray);
	}

	public void setVehicleData(BaseVehicle baseVehicle) {
		if (!this.dataList.isEmpty()) {
			pool.addAll(this.dataList);
			this.dataList.clear();
		}
	}

	public void poolData() {
		if (!this.dataList.isEmpty()) {
			pool.addAll(this.dataList);
			this.dataList.clear();
		}
	}
}
