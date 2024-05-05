package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.core.Rand;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.Vector2;
import zombie.vehicles.BaseVehicle;


public final class RVSCarCrash extends RandomizedVehicleStoryBase {

	public RVSCarCrash() {
		this.name = "Basic Car Crash";
		this.minZoneWidth = 5;
		this.minZoneHeight = 7;
		this.setChance(25);
	}

	public void randomizeVehicleStory(IsoMetaGrid.Zone zone, IsoChunk chunk) {
		this.callVehicleStorySpawner(zone, chunk, 0.0F);
	}

	public boolean initVehicleStorySpawner(IsoMetaGrid.Zone zone, IsoChunk chunk, boolean boolean1) {
		VehicleStorySpawner vehicleStorySpawner = VehicleStorySpawner.getInstance();
		vehicleStorySpawner.clear();
		float float1 = 0.5235988F;
		if (boolean1) {
			float1 = 0.0F;
		}

		Vector2 vector2 = IsoDirections.N.ToVector();
		vector2.rotate(Rand.Next(-float1, float1));
		vehicleStorySpawner.addElement("vehicle1", 0.0F, 1.0F, vector2.getDirection(), 2.0F, 5.0F);
		boolean boolean2 = Rand.NextBool(2);
		vector2 = boolean2 ? IsoDirections.E.ToVector() : IsoDirections.W.ToVector();
		vector2.rotate(Rand.Next(-float1, float1));
		vehicleStorySpawner.addElement("vehicle2", 0.0F, -2.5F, vector2.getDirection(), 2.0F, 5.0F);
		vehicleStorySpawner.setParameter("zone", zone);
		vehicleStorySpawner.setParameter("smashed", Rand.NextBool(3));
		vehicleStorySpawner.setParameter("east", boolean2);
		return true;
	}

	public void spawnElement(VehicleStorySpawner vehicleStorySpawner, VehicleStorySpawner.Element element) {
		IsoGridSquare square = element.square;
		if (square != null) {
			float float1 = element.z;
			IsoMetaGrid.Zone zone = (IsoMetaGrid.Zone)vehicleStorySpawner.getParameter("zone", IsoMetaGrid.Zone.class);
			boolean boolean1 = vehicleStorySpawner.getParameterBoolean("smashed");
			boolean boolean2 = vehicleStorySpawner.getParameterBoolean("east");
			String string = element.id;
			byte byte1 = -1;
			switch (string.hashCode()) {
			case 2014205573: 
				if (string.equals("vehicle1")) {
					byte1 = 0;
				}

				break;
			
			case 2014205574: 
				if (string.equals("vehicle2")) {
					byte1 = 1;
				}

			
			}

			switch (byte1) {
			case 0: 
			
			case 1: 
				BaseVehicle baseVehicle = this.addVehicle(zone, element.position.x, element.position.y, float1, element.direction, "bad", (String)null, (Integer)null, (String)null);
				if (baseVehicle != null) {
					if (boolean1) {
						String string2 = "Front";
						if ("vehicle2".equals(element.id)) {
							string2 = boolean2 ? "Right" : "Left";
						}

						baseVehicle = baseVehicle.setSmashed(string2);
						baseVehicle.setBloodIntensity(string2, 1.0F);
					}

					if ("vehicle1".equals(element.id) && Rand.Next(10) < 4) {
						this.addZombiesOnVehicle(Rand.Next(2, 5), (String)null, (Integer)null, baseVehicle);
					}
				}

			
			default: 
			
			}
		}
	}
}
