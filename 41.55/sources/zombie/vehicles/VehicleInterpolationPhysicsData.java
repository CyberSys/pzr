package zombie.vehicles;


public class VehicleInterpolationPhysicsData {
	long time = 0L;
	float force;
	float[] data = new float[23];

	VehicleInterpolationPhysicsData() {
	}

	void copy(VehicleInterpolationPhysicsData vehicleInterpolationPhysicsData) {
		this.time = vehicleInterpolationPhysicsData.time;
		for (int int1 = 0; int1 < this.data.length; ++int1) {
			this.data[int1] = vehicleInterpolationPhysicsData.data[int1];
		}
	}
}
