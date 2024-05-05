package zombie.audio.parameters;

import zombie.audio.FMODLocalParameter;
import zombie.core.math.PZMath;
import zombie.network.GameClient;
import zombie.scripting.objects.VehicleScript;
import zombie.vehicles.BaseVehicle;


public class ParameterVehicleSkid extends FMODLocalParameter {
	private final BaseVehicle vehicle;
	private final BaseVehicle.WheelInfo[] wheelInfo;

	public ParameterVehicleSkid(BaseVehicle baseVehicle) {
		super("VehicleSkid");
		this.vehicle = baseVehicle;
		this.wheelInfo = baseVehicle.wheelInfo;
	}

	public float calculateCurrentValue() {
		float float1 = 1.0F;
		if (GameClient.bClient && !this.vehicle.isLocalPhysicSim()) {
			return float1;
		} else {
			VehicleScript vehicleScript = this.vehicle.getScript();
			if (vehicleScript == null) {
				return float1;
			} else {
				int int1 = 0;
				for (int int2 = vehicleScript.getWheelCount(); int1 < int2; ++int1) {
					float1 = PZMath.min(float1, this.wheelInfo[int1].skidInfo);
				}

				return (float)((int)(100.0F - PZMath.clamp(float1, 0.0F, 1.0F) * 100.0F)) / 100.0F;
			}
		}
	}
}
