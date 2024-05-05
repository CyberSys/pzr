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
		VehicleInterpolationData vehicleInterpolationData = null;
		VehicleInterpolationData vehicleInterpolationData2 = null;
		long long1 = GameTime.getServerTime() - this.serverDelay - (long)(this.physicsDelayMs * 1000000);
		if (this.dataList.size() == 2 && ((VehicleInterpolationData)this.dataList.getFirst()).time == ((VehicleInterpolationData)this.dataList.getLast()).time) {
			this.dataList.removeFirst();
			vehicleInterpolationData2 = (VehicleInterpolationData)this.dataList.getLast();
		} else {
			ListIterator listIterator;
			if (!this.buffering) {
				if (this.dataList.size() == 0) {
					this.buffering = true;
					return false;
				}
			} else {
				listIterator = this.dataList.listIterator();
				long long2 = 0L;
				long long3 = 0L;
				while (true) {
					if (!listIterator.hasNext()) {
						if (long2 != 0L && long3 - long2 >= (long)(this.physicsDelayMs * 1000000)) {
							this.buffering = false;
							break;
						}

						return false;
					}

					VehicleInterpolationData vehicleInterpolationData3 = (VehicleInterpolationData)listIterator.next();
					if (long2 == 0L || vehicleInterpolationData3.time < long2) {
						long2 = vehicleInterpolationData3.time;
					}

					if (vehicleInterpolationData3.time > long3) {
						long3 = vehicleInterpolationData3.time;
					}
				}
			}

			if (this.physicsDelayMs <= 0) {
				vehicleInterpolationData2 = (VehicleInterpolationData)this.dataList.getFirst();
			} else {
				listIterator = this.dataList.listIterator();
				VehicleInterpolationData vehicleInterpolationData4;
				while (listIterator.hasNext()) {
					vehicleInterpolationData4 = (VehicleInterpolationData)listIterator.next();
					if (vehicleInterpolationData4.time >= long1) {
						vehicleInterpolationData2 = vehicleInterpolationData4;
						if (!listIterator.hasPrevious()) {
							return false;
						}

						listIterator.previous();
						if (!listIterator.hasPrevious()) {
							return false;
						}

						vehicleInterpolationData = (VehicleInterpolationData)listIterator.previous();
						break;
					}
				}

				while (listIterator.hasPrevious()) {
					vehicleInterpolationData4 = (VehicleInterpolationData)listIterator.previous();
					pool.push(vehicleInterpolationData4);
					listIterator.remove();
				}
			}

			if (vehicleInterpolationData2 == null) {
				this.buffering = true;
				if (!this.dataList.isEmpty()) {
					pool.addAll(this.dataList);
					this.dataList.clear();
				}

				return false;
			}
		}

		int int1;
		if (vehicleInterpolationData == null) {
			byte byte1 = 0;
			int int2 = byte1 + 1;
			floatArray[byte1] = vehicleInterpolationData2.x;
			floatArray[int2++] = vehicleInterpolationData2.y;
			floatArray[int2++] = vehicleInterpolationData2.z;
			floatArray[int2++] = vehicleInterpolationData2.qx;
			floatArray[int2++] = vehicleInterpolationData2.qy;
			floatArray[int2++] = vehicleInterpolationData2.qz;
			floatArray[int2++] = vehicleInterpolationData2.qw;
			floatArray[int2++] = vehicleInterpolationData2.vx;
			floatArray[int2++] = vehicleInterpolationData2.vy;
			floatArray[int2++] = vehicleInterpolationData2.vz;
			floatArray[int2++] = (float)vehicleInterpolationData2.w_count;
			for (int1 = 0; int1 < vehicleInterpolationData2.w_count; ++int1) {
				floatArray[int2++] = vehicleInterpolationData2.w_st[int1];
				floatArray[int2++] = vehicleInterpolationData2.w_rt[int1];
				floatArray[int2++] = vehicleInterpolationData2.w_si[int1];
			}

			return true;
		} else {
			float float1 = (float)(long1 - vehicleInterpolationData.time) / (float)(vehicleInterpolationData2.time - vehicleInterpolationData.time);
			byte byte2 = 0;
			int1 = byte2 + 1;
			floatArray[byte2] = (vehicleInterpolationData2.x - vehicleInterpolationData.x) * float1 + vehicleInterpolationData.x;
			floatArray[int1++] = (vehicleInterpolationData2.y - vehicleInterpolationData.y) * float1 + vehicleInterpolationData.y;
			floatArray[int1++] = (vehicleInterpolationData2.z - vehicleInterpolationData.z) * float1 + vehicleInterpolationData.z;
			float float2 = vehicleInterpolationData2.qx * vehicleInterpolationData.qx + vehicleInterpolationData2.qy * vehicleInterpolationData.qy + vehicleInterpolationData2.qz * vehicleInterpolationData.qz + vehicleInterpolationData2.qw * vehicleInterpolationData.qw;
			if (float2 < 0.0F) {
				vehicleInterpolationData2.qx *= -1.0F;
				vehicleInterpolationData2.qy *= -1.0F;
				vehicleInterpolationData2.qz *= -1.0F;
				vehicleInterpolationData2.qw *= -1.0F;
			}

			floatArray[int1++] = vehicleInterpolationData.qx * (1.0F - float1) + vehicleInterpolationData2.qx * float1;
			floatArray[int1++] = vehicleInterpolationData.qy * (1.0F - float1) + vehicleInterpolationData2.qy * float1;
			floatArray[int1++] = vehicleInterpolationData.qz * (1.0F - float1) + vehicleInterpolationData2.qz * float1;
			floatArray[int1++] = vehicleInterpolationData.qw * (1.0F - float1) + vehicleInterpolationData2.qw * float1;
			floatArray[int1++] = (vehicleInterpolationData2.vx - vehicleInterpolationData.vx) * float1 + vehicleInterpolationData.vx;
			floatArray[int1++] = (vehicleInterpolationData2.vy - vehicleInterpolationData.vy) * float1 + vehicleInterpolationData.vy;
			floatArray[int1++] = (vehicleInterpolationData2.vz - vehicleInterpolationData.vz) * float1 + vehicleInterpolationData.vz;
			floatArray[int1++] = (float)vehicleInterpolationData2.w_count;
			for (int int3 = 0; int3 < vehicleInterpolationData2.w_count; ++int3) {
				floatArray[int1++] = (vehicleInterpolationData2.w_st[int3] - vehicleInterpolationData.w_st[int3]) * float1 + vehicleInterpolationData.w_st[int3];
				floatArray[int1++] = (vehicleInterpolationData2.w_rt[int3] - vehicleInterpolationData.w_rt[int3]) * float1 + vehicleInterpolationData.w_rt[int3];
				floatArray[int1++] = (vehicleInterpolationData2.w_si[int3] - vehicleInterpolationData.w_si[int3]) * float1 + vehicleInterpolationData.w_si[int3];
			}

			return true;
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
