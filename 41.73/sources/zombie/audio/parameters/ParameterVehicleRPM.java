package zombie.audio.parameters;

import zombie.audio.FMODLocalParameter;
import zombie.core.math.PZMath;
import zombie.vehicles.BaseVehicle;


public class ParameterVehicleRPM extends FMODLocalParameter {
	private final BaseVehicle vehicle;

	public ParameterVehicleRPM(BaseVehicle baseVehicle) {
		super("VehicleRPM");
		this.vehicle = baseVehicle;
	}

	public float calculateCurrentValue() {
		float float1 = PZMath.clamp((float)this.vehicle.getEngineSpeed(), 0.0F, 7000.0F);
		float float2 = this.vehicle.getScript().getEngineIdleSpeed();
		float float3 = float2 * 1.1F;
		float float4 = 800.0F;
		float float5 = 7000.0F;
		float float6;
		if (float1 < float3) {
			float6 = float1 / float3 * float4;
		} else {
			float6 = float4 + (float1 - float3) / (7000.0F - float3) * (float5 - float4);
		}

		return (float)((int)((float6 + 50.0F - 1.0F) / 50.0F)) * 50.0F;
	}
}
