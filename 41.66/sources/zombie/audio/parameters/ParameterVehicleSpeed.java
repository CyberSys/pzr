package zombie.audio.parameters;

import zombie.audio.FMODLocalParameter;
import zombie.vehicles.BaseVehicle;


public class ParameterVehicleSpeed extends FMODLocalParameter {
	private final BaseVehicle vehicle;

	public ParameterVehicleSpeed(BaseVehicle baseVehicle) {
		super("VehicleSpeed");
		this.vehicle = baseVehicle;
	}

	public float calculateCurrentValue() {
		return (float)Math.round(Math.abs(this.vehicle.getCurrentSpeedKmHour()));
	}
}
