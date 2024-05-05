package zombie.audio.parameters;

import zombie.audio.FMODLocalParameter;
import zombie.scripting.objects.VehicleScript;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehiclePart;


public class ParameterVehicleTireMissing extends FMODLocalParameter {
	private final BaseVehicle vehicle;

	public ParameterVehicleTireMissing(BaseVehicle baseVehicle) {
		super("VehicleTireMissing");
		this.vehicle = baseVehicle;
	}

	public float calculateCurrentValue() {
		boolean boolean1 = false;
		VehicleScript vehicleScript = this.vehicle.getScript();
		if (vehicleScript != null) {
			for (int int1 = 0; int1 < vehicleScript.getWheelCount(); ++int1) {
				VehicleScript.Wheel wheel = vehicleScript.getWheel(int1);
				VehiclePart vehiclePart = this.vehicle.getPartById("Tire" + wheel.getId());
				if (vehiclePart == null || vehiclePart.getInventoryItem() == null) {
					boolean1 = true;
					break;
				}
			}
		}

		return boolean1 ? 1.0F : 0.0F;
	}
}
