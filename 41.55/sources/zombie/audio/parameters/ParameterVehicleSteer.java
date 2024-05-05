package zombie.audio.parameters;

import zombie.audio.FMODLocalParameter;
import zombie.core.math.PZMath;
import zombie.scripting.objects.VehicleScript;
import zombie.vehicles.BaseVehicle;


public class ParameterVehicleSteer extends FMODLocalParameter {
	private final BaseVehicle vehicle;

	public ParameterVehicleSteer(BaseVehicle baseVehicle) {
		super("VehicleSteer");
		this.vehicle = baseVehicle;
	}

	public float calculateCurrentValue() {
		float float1 = 0.0F;
		if (!this.vehicle.isEngineRunning()) {
			return float1;
		} else {
			VehicleScript vehicleScript = this.vehicle.getScript();
			if (vehicleScript == null) {
				return float1;
			} else {
				BaseVehicle.WheelInfo[] wheelInfoArray = this.vehicle.wheelInfo;
				int int1 = 0;
				for (int int2 = vehicleScript.getWheelCount(); int1 < int2; ++int1) {
					float1 = PZMath.max(float1, Math.abs(wheelInfoArray[int1].steering));
				}

				return (float)((int)(PZMath.clamp(float1, 0.0F, 1.0F) * 100.0F)) / 100.0F;
			}
		}
	}
}
