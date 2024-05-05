package zombie.audio.parameters;

import zombie.audio.FMODLocalParameter;
import zombie.core.math.PZMath;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehiclePart;


public class ParameterVehicleEngineCondition extends FMODLocalParameter {
	private final BaseVehicle vehicle;

	public ParameterVehicleEngineCondition(BaseVehicle baseVehicle) {
		super("VehicleEngineCondition");
		this.vehicle = baseVehicle;
	}

	public float calculateCurrentValue() {
		VehiclePart vehiclePart = this.vehicle.getPartById("Engine");
		return vehiclePart == null ? 100.0F : (float)PZMath.clamp(vehiclePart.getCondition(), 0, 100);
	}
}
