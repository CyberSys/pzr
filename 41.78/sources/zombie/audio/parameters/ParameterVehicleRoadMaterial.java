package zombie.audio.parameters;

import zombie.audio.FMODLocalParameter;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.vehicles.BaseVehicle;


public final class ParameterVehicleRoadMaterial extends FMODLocalParameter {
	private final BaseVehicle vehicle;

	public ParameterVehicleRoadMaterial(BaseVehicle baseVehicle) {
		super("VehicleRoadMaterial");
		this.vehicle = baseVehicle;
	}

	public float calculateCurrentValue() {
		if (!this.vehicle.isEngineRunning()) {
			return Float.isNaN(this.getCurrentValue()) ? 0.0F : this.getCurrentValue();
		} else {
			return (float)this.getMaterial().label;
		}
	}

	private ParameterVehicleRoadMaterial.Material getMaterial() {
		IsoGridSquare square = this.vehicle.getCurrentSquare();
		if (square == null) {
			return ParameterVehicleRoadMaterial.Material.Concrete;
		} else {
			IsoObject object = this.vehicle.getCurrentSquare().getFloor();
			if (object != null && object.getSprite() != null && object.getSprite().getName() != null) {
				String string = object.getSprite().getName();
				if (!string.endsWith("blends_natural_01_5") && !string.endsWith("blends_natural_01_6") && !string.endsWith("blends_natural_01_7") && !string.endsWith("blends_natural_01_0")) {
					if (!string.endsWith("blends_natural_01_64") && !string.endsWith("blends_natural_01_69") && !string.endsWith("blends_natural_01_70") && !string.endsWith("blends_natural_01_71")) {
						if (string.startsWith("blends_natural_01")) {
							return ParameterVehicleRoadMaterial.Material.Grass;
						} else if (!string.endsWith("blends_street_01_48") && !string.endsWith("blends_street_01_53") && !string.endsWith("blends_street_01_54") && !string.endsWith("blends_street_01_55")) {
							if (string.startsWith("floors_interior_tilesandwood_01_")) {
								int int1 = Integer.parseInt(string.replaceFirst("floors_interior_tilesandwood_01_", ""));
								return int1 > 40 && int1 < 48 ? ParameterVehicleRoadMaterial.Material.Wood : ParameterVehicleRoadMaterial.Material.Concrete;
							} else if (string.startsWith("carpentry_02_")) {
								return ParameterVehicleRoadMaterial.Material.Wood;
							} else if (string.contains("interior_carpet_")) {
								return ParameterVehicleRoadMaterial.Material.Carpet;
							} else {
								float float1 = square.getPuddlesInGround();
								return (double)float1 > 0.1 ? ParameterVehicleRoadMaterial.Material.Puddle : ParameterVehicleRoadMaterial.Material.Concrete;
							}
						} else {
							return ParameterVehicleRoadMaterial.Material.Gravel;
						}
					} else {
						return ParameterVehicleRoadMaterial.Material.Dirt;
					}
				} else {
					return ParameterVehicleRoadMaterial.Material.Sand;
				}
			} else {
				return ParameterVehicleRoadMaterial.Material.Concrete;
			}
		}
	}

	static enum Material {

		Concrete,
		Grass,
		Gravel,
		Puddle,
		Snow,
		Wood,
		Carpet,
		Dirt,
		Sand,
		label;

		private Material(int int1) {
			this.label = int1;
		}
		private static ParameterVehicleRoadMaterial.Material[] $values() {
			return new ParameterVehicleRoadMaterial.Material[]{Concrete, Grass, Gravel, Puddle, Snow, Wood, Carpet, Dirt, Sand};
		}
	}
}
