package zombie.vehicles;


public class VehicleInterpolationData {
	long time = 0L;
	float x = 0.0F;
	float y = 0.0F;
	float z = 0.0F;
	float qx = 0.0F;
	float qy = 0.0F;
	float qz = 0.0F;
	float qw = 0.0F;
	float vx = 0.0F;
	float vy = 0.0F;
	float vz = 0.0F;
	short w_count = 4;
	float[] w_st = new float[4];
	float[] w_rt = new float[4];
	float[] w_si = new float[4];

	VehicleInterpolationData() {
	}

	void setNumWheels(int int1) {
		this.w_count = (short)int1;
		if (int1 > this.w_st.length) {
			this.w_st = new float[int1];
			this.w_rt = new float[int1];
			this.w_si = new float[int1];
		}
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
		this.setNumWheels(vehicleInterpolationData.w_count);
		for (int int1 = 0; int1 < vehicleInterpolationData.w_count; ++int1) {
			this.w_st[int1] = vehicleInterpolationData.w_st[int1];
			this.w_rt[int1] = vehicleInterpolationData.w_rt[int1];
			this.w_si[int1] = vehicleInterpolationData.w_si[int1];
		}
	}
}
