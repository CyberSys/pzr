package zombie.vehicles;


public class VehicleInterpolationData implements Comparable {
	protected long time;
	protected float x;
	protected float y;
	protected float z;
	protected float qx;
	protected float qy;
	protected float qz;
	protected float qw;
	protected float vx;
	protected float vy;
	protected float vz;
	protected float engineSpeed;
	protected float throttle;
	protected short wheelsCount = 4;
	protected float[] wheelSteering = new float[4];
	protected float[] wheelRotation = new float[4];
	protected float[] wheelSkidInfo = new float[4];
	protected float[] wheelSuspensionLength = new float[4];

	protected void setNumWheels(short short1) {
		if (short1 > this.wheelsCount) {
			this.wheelSteering = new float[short1];
			this.wheelRotation = new float[short1];
			this.wheelSkidInfo = new float[short1];
			this.wheelSuspensionLength = new float[short1];
		}

		this.wheelsCount = short1;
	}

	void copy(VehicleInterpolationData vehicleInterpolationData) {
		this.time = vehicleInterpolationData.time;
		this.x = vehicleInterpolationData.x;
		this.y = vehicleInterpolationData.y;
		this.z = vehicleInterpolationData.z;
		this.qx = vehicleInterpolationData.qx;
		this.qy = vehicleInterpolationData.qy;
		this.qz = vehicleInterpolationData.qz;
		this.qw = vehicleInterpolationData.qw;
		this.vx = vehicleInterpolationData.vx;
		this.vy = vehicleInterpolationData.vy;
		this.vz = vehicleInterpolationData.vz;
		this.engineSpeed = vehicleInterpolationData.engineSpeed;
		this.throttle = vehicleInterpolationData.throttle;
		this.setNumWheels(vehicleInterpolationData.wheelsCount);
		for (int int1 = 0; int1 < vehicleInterpolationData.wheelsCount; ++int1) {
			this.wheelSteering[int1] = vehicleInterpolationData.wheelSteering[int1];
			this.wheelRotation[int1] = vehicleInterpolationData.wheelRotation[int1];
			this.wheelSkidInfo[int1] = vehicleInterpolationData.wheelSkidInfo[int1];
			this.wheelSuspensionLength[int1] = vehicleInterpolationData.wheelSuspensionLength[int1];
		}
	}

	public int compareTo(VehicleInterpolationData vehicleInterpolationData) {
		return Long.compare(this.time, vehicleInterpolationData.time);
	}
}
