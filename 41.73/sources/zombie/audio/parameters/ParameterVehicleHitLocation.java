package zombie.audio.parameters;

import org.joml.Vector3f;
import zombie.audio.FMODLocalParameter;
import zombie.scripting.objects.VehicleScript;
import zombie.vehicles.BaseVehicle;


public class ParameterVehicleHitLocation extends FMODLocalParameter {
	private ParameterVehicleHitLocation.HitLocation location;

	public ParameterVehicleHitLocation() {
		super("VehicleHitLocation");
		this.location = ParameterVehicleHitLocation.HitLocation.Front;
	}

	public float calculateCurrentValue() {
		return (float)this.location.label;
	}

	public static ParameterVehicleHitLocation.HitLocation calculateLocation(BaseVehicle baseVehicle, float float1, float float2, float float3) {
		VehicleScript vehicleScript = baseVehicle.getScript();
		if (vehicleScript == null) {
			return ParameterVehicleHitLocation.HitLocation.Front;
		} else {
			Vector3f vector3f = baseVehicle.getLocalPos(float1, float2, float3, (Vector3f)((BaseVehicle.Vector3fObjectPool)BaseVehicle.TL_vector3f_pool.get()).alloc());
			Vector3f vector3f2 = vehicleScript.getExtents();
			Vector3f vector3f3 = vehicleScript.getCenterOfMassOffset();
			float float4 = vector3f3.z - vector3f2.z / 2.0F;
			float float5 = vector3f3.z + vector3f2.z / 2.0F;
			float4 *= 0.9F;
			float5 *= 0.9F;
			ParameterVehicleHitLocation.HitLocation hitLocation;
			if (vector3f.z >= float4 && vector3f.z <= float5) {
				hitLocation = ParameterVehicleHitLocation.HitLocation.Side;
			} else if (vector3f.z > 0.0F) {
				hitLocation = ParameterVehicleHitLocation.HitLocation.Front;
			} else {
				hitLocation = ParameterVehicleHitLocation.HitLocation.Rear;
			}

			((BaseVehicle.Vector3fObjectPool)BaseVehicle.TL_vector3f_pool.get()).release(vector3f);
			return hitLocation;
		}
	}

	public void setLocation(ParameterVehicleHitLocation.HitLocation hitLocation) {
		this.location = hitLocation;
	}

	public static enum HitLocation {

		Front,
		Rear,
		Side,
		label;

		private HitLocation(int int1) {
			this.label = int1;
		}
		private static ParameterVehicleHitLocation.HitLocation[] $values() {
			return new ParameterVehicleHitLocation.HitLocation[]{Front, Rear, Side};
		}
	}
}
