package zombie.audio.parameters;

import zombie.audio.FMODLocalParameter;
import zombie.vehicles.BaseVehicle;


public class ParameterVehicleGear extends FMODLocalParameter {
	private final BaseVehicle vehicle;

	public ParameterVehicleGear(BaseVehicle baseVehicle) {
		super("VehicleGear");
		this.vehicle = baseVehicle;
	}

	public float calculateCurrentValue() {
		return (float)(this.vehicle.getTransmissionNumber() + 1);
	}
}
