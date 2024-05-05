package zombie.vehicles;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import org.joml.Quaternionf;
import zombie.GameTime;
import zombie.core.physics.WorldSimulation;


public class VehicleInterpolation {
	int delay;
	int history;
	boolean buffering;
	private static final ArrayDeque pool = new ArrayDeque();
	private static final List outdated = new ArrayList();
	TreeSet buffer = new TreeSet();
	private static final Quaternionf tempQuaternionA = new Quaternionf();
	private static final Quaternionf tempQuaternionB = new Quaternionf();
	private static final VehicleInterpolationData temp = new VehicleInterpolationData();

	VehicleInterpolation() {
		this.reset();
		this.delay = 500;
		this.history = 800;
	}

	public void reset() {
		this.buffering = true;
		this.clear();
	}

	public void clear() {
		if (!this.buffer.isEmpty()) {
			pool.addAll(this.buffer);
			this.buffer.clear();
			outdated.clear();
		}
	}

	public void update(long long1) {
		temp.time = long1 - (long)this.delay;
		VehicleInterpolationData vehicleInterpolationData = (VehicleInterpolationData)this.buffer.floor(temp);
		Iterator iterator = this.buffer.iterator();
		while (iterator.hasNext()) {
			VehicleInterpolationData vehicleInterpolationData2 = (VehicleInterpolationData)iterator.next();
			if (long1 - vehicleInterpolationData2.time > (long)this.history && vehicleInterpolationData2 != vehicleInterpolationData) {
				outdated.add(vehicleInterpolationData2);
			}
		}

		List list = outdated;
		TreeSet treeSet = this.buffer;
		Objects.requireNonNull(treeSet);
		list.forEach(treeSet::remove);
		pool.addAll(outdated);
		outdated.clear();
		if (this.buffer.isEmpty()) {
			this.buffering = true;
		}
	}

	private void interpolationDataCurrentAdd(BaseVehicle baseVehicle) {
		VehicleInterpolationData vehicleInterpolationData = pool.isEmpty() ? new VehicleInterpolationData() : (VehicleInterpolationData)pool.pop();
		vehicleInterpolationData.time = GameTime.getServerTimeMills() - (long)this.delay;
		vehicleInterpolationData.x = baseVehicle.jniTransform.origin.x + WorldSimulation.instance.offsetX;
		vehicleInterpolationData.y = baseVehicle.jniTransform.origin.z + WorldSimulation.instance.offsetY;
		vehicleInterpolationData.z = baseVehicle.jniTransform.origin.y;
		Quaternionf quaternionf = baseVehicle.jniTransform.getRotation(new Quaternionf());
		vehicleInterpolationData.qx = quaternionf.x;
		vehicleInterpolationData.qy = quaternionf.y;
		vehicleInterpolationData.qz = quaternionf.z;
		vehicleInterpolationData.qw = quaternionf.w;
		vehicleInterpolationData.vx = baseVehicle.jniLinearVelocity.x;
		vehicleInterpolationData.vy = baseVehicle.jniLinearVelocity.y;
		vehicleInterpolationData.vz = baseVehicle.jniLinearVelocity.z;
		vehicleInterpolationData.engineSpeed = (float)baseVehicle.engineSpeed;
		vehicleInterpolationData.throttle = baseVehicle.throttle;
		vehicleInterpolationData.setNumWheels((short)baseVehicle.wheelInfo.length);
		for (int int1 = 0; int1 < vehicleInterpolationData.wheelsCount; ++int1) {
			vehicleInterpolationData.wheelSteering[int1] = baseVehicle.wheelInfo[int1].steering;
			vehicleInterpolationData.wheelRotation[int1] = baseVehicle.wheelInfo[int1].rotation;
			vehicleInterpolationData.wheelSkidInfo[int1] = baseVehicle.wheelInfo[int1].skidInfo;
			vehicleInterpolationData.wheelSuspensionLength[int1] = baseVehicle.wheelInfo[int1].suspensionLength;
		}

		this.buffer.add(vehicleInterpolationData);
	}

	public void interpolationDataAdd(BaseVehicle baseVehicle, VehicleInterpolationData vehicleInterpolationData) {
		VehicleInterpolationData vehicleInterpolationData2 = pool.isEmpty() ? new VehicleInterpolationData() : (VehicleInterpolationData)pool.pop();
		vehicleInterpolationData2.copy(vehicleInterpolationData);
		if (this.buffer.isEmpty()) {
			this.interpolationDataCurrentAdd(baseVehicle);
		}

		this.buffer.add(vehicleInterpolationData2);
		this.update(GameTime.getServerTimeMills());
	}

	public void interpolationDataAdd(ByteBuffer byteBuffer, long long1, float float1, float float2, float float3, long long2) {
		VehicleInterpolationData vehicleInterpolationData = pool.isEmpty() ? new VehicleInterpolationData() : (VehicleInterpolationData)pool.pop();
		vehicleInterpolationData.time = long1;
		vehicleInterpolationData.x = float1;
		vehicleInterpolationData.y = float2;
		vehicleInterpolationData.z = float3;
		vehicleInterpolationData.qx = byteBuffer.getFloat();
		vehicleInterpolationData.qy = byteBuffer.getFloat();
		vehicleInterpolationData.qz = byteBuffer.getFloat();
		vehicleInterpolationData.qw = byteBuffer.getFloat();
		vehicleInterpolationData.vx = byteBuffer.getFloat();
		vehicleInterpolationData.vy = byteBuffer.getFloat();
		vehicleInterpolationData.vz = byteBuffer.getFloat();
		vehicleInterpolationData.engineSpeed = byteBuffer.getFloat();
		vehicleInterpolationData.throttle = byteBuffer.getFloat();
		vehicleInterpolationData.setNumWheels(byteBuffer.getShort());
		for (int int1 = 0; int1 < vehicleInterpolationData.wheelsCount; ++int1) {
			vehicleInterpolationData.wheelSteering[int1] = byteBuffer.getFloat();
			vehicleInterpolationData.wheelRotation[int1] = byteBuffer.getFloat();
			vehicleInterpolationData.wheelSkidInfo[int1] = byteBuffer.getFloat();
			vehicleInterpolationData.wheelSuspensionLength[int1] = byteBuffer.getFloat();
		}

		this.buffer.add(vehicleInterpolationData);
		this.update(long2);
	}

	public boolean interpolationDataGet(float[] floatArray, float[] floatArray2) {
		long long1 = WorldSimulation.instance.time - (long)this.delay;
		return this.interpolationDataGet(floatArray, floatArray2, long1);
	}

	public boolean interpolationDataGet(float[] floatArray, float[] floatArray2, long long1) {
		temp.time = long1;
		VehicleInterpolationData vehicleInterpolationData = (VehicleInterpolationData)this.buffer.higher(temp);
		VehicleInterpolationData vehicleInterpolationData2 = (VehicleInterpolationData)this.buffer.floor(temp);
		if (this.buffering) {
			if (this.buffer.size() < 2 || vehicleInterpolationData == null || vehicleInterpolationData2 == null) {
				return false;
			}

			this.buffering = false;
		} else if (this.buffer.isEmpty()) {
			this.reset();
			return false;
		}

		byte byte1 = 0;
		int int1;
		if (vehicleInterpolationData == null) {
			if (vehicleInterpolationData2 == null) {
				this.reset();
				return false;
			} else {
				floatArray2[0] = vehicleInterpolationData2.engineSpeed;
				floatArray2[1] = vehicleInterpolationData2.throttle;
				int1 = byte1 + 1;
				floatArray[byte1] = vehicleInterpolationData2.x;
				floatArray[int1++] = vehicleInterpolationData2.y;
				floatArray[int1++] = vehicleInterpolationData2.z;
				floatArray[int1++] = vehicleInterpolationData2.qx;
				floatArray[int1++] = vehicleInterpolationData2.qy;
				floatArray[int1++] = vehicleInterpolationData2.qz;
				floatArray[int1++] = vehicleInterpolationData2.qw;
				floatArray[int1++] = vehicleInterpolationData2.vx;
				floatArray[int1++] = vehicleInterpolationData2.vy;
				floatArray[int1++] = vehicleInterpolationData2.vz;
				floatArray[int1++] = (float)vehicleInterpolationData2.wheelsCount;
				for (int int2 = 0; int2 < vehicleInterpolationData2.wheelsCount; ++int2) {
					floatArray[int1++] = vehicleInterpolationData2.wheelSteering[int2];
					floatArray[int1++] = vehicleInterpolationData2.wheelRotation[int2];
					floatArray[int1++] = vehicleInterpolationData2.wheelSkidInfo[int2];
					floatArray[int1++] = vehicleInterpolationData2.wheelSuspensionLength[int2];
				}

				this.reset();
				return true;
			}
		} else if (vehicleInterpolationData2 != null && Math.abs(vehicleInterpolationData.time - vehicleInterpolationData2.time) >= 10L) {
			float float1 = (float)(long1 - vehicleInterpolationData2.time) / (float)(vehicleInterpolationData.time - vehicleInterpolationData2.time);
			floatArray2[0] = (vehicleInterpolationData.engineSpeed - vehicleInterpolationData2.engineSpeed) * float1 + vehicleInterpolationData2.engineSpeed;
			floatArray2[1] = (vehicleInterpolationData.throttle - vehicleInterpolationData2.throttle) * float1 + vehicleInterpolationData2.throttle;
			int1 = byte1 + 1;
			floatArray[byte1] = (vehicleInterpolationData.x - vehicleInterpolationData2.x) * float1 + vehicleInterpolationData2.x;
			floatArray[int1++] = (vehicleInterpolationData.y - vehicleInterpolationData2.y) * float1 + vehicleInterpolationData2.y;
			floatArray[int1++] = (vehicleInterpolationData.z - vehicleInterpolationData2.z) * float1 + vehicleInterpolationData2.z;
			tempQuaternionA.set(vehicleInterpolationData2.qx, vehicleInterpolationData2.qy, vehicleInterpolationData2.qz, vehicleInterpolationData2.qw);
			tempQuaternionB.set(vehicleInterpolationData.qx, vehicleInterpolationData.qy, vehicleInterpolationData.qz, vehicleInterpolationData.qw);
			tempQuaternionA.nlerp(tempQuaternionB, float1);
			floatArray[int1++] = tempQuaternionA.x;
			floatArray[int1++] = tempQuaternionA.y;
			floatArray[int1++] = tempQuaternionA.z;
			floatArray[int1++] = tempQuaternionA.w;
			floatArray[int1++] = (vehicleInterpolationData.vx - vehicleInterpolationData2.vx) * float1 + vehicleInterpolationData2.vx;
			floatArray[int1++] = (vehicleInterpolationData.vy - vehicleInterpolationData2.vy) * float1 + vehicleInterpolationData2.vy;
			floatArray[int1++] = (vehicleInterpolationData.vz - vehicleInterpolationData2.vz) * float1 + vehicleInterpolationData2.vz;
			floatArray[int1++] = (float)vehicleInterpolationData.wheelsCount;
			for (int int3 = 0; int3 < vehicleInterpolationData.wheelsCount; ++int3) {
				floatArray[int1++] = (vehicleInterpolationData.wheelSteering[int3] - vehicleInterpolationData2.wheelSteering[int3]) * float1 + vehicleInterpolationData2.wheelSteering[int3];
				floatArray[int1++] = (vehicleInterpolationData.wheelRotation[int3] - vehicleInterpolationData2.wheelRotation[int3]) * float1 + vehicleInterpolationData2.wheelRotation[int3];
				floatArray[int1++] = (vehicleInterpolationData.wheelSkidInfo[int3] - vehicleInterpolationData2.wheelSkidInfo[int3]) * float1 + vehicleInterpolationData2.wheelSkidInfo[int3];
				floatArray[int1++] = (vehicleInterpolationData.wheelSuspensionLength[int3] - vehicleInterpolationData2.wheelSuspensionLength[int3]) * float1 + vehicleInterpolationData2.wheelSuspensionLength[int3];
			}

			return true;
		} else {
			return false;
		}
	}
}
