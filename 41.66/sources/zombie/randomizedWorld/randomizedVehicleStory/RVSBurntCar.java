package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.core.Rand;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.Vector2;
import zombie.vehicles.BaseVehicle;


public final class RVSBurntCar extends RandomizedVehicleStoryBase {

	public RVSBurntCar() {
		this.name = "Burnt Car";
		this.minZoneWidth = 2;
		this.minZoneHeight = 5;
		this.setChance(13);
	}

	public void randomizeVehicleStory(IsoMetaGrid.Zone zone, IsoChunk chunk) {
		this.callVehicleStorySpawner(zone, chunk, 0.0F);
	}

	public boolean initVehicleStorySpawner(IsoMetaGrid.Zone zone, IsoChunk chunk, boolean boolean1) {
		VehicleStorySpawner vehicleStorySpawner = VehicleStorySpawner.getInstance();
		vehicleStorySpawner.clear();
		Vector2 vector2 = IsoDirections.N.ToVector();
		float float1 = 0.5235988F;
		if (boolean1) {
			float1 = 0.0F;
		}

		vector2.rotate(Rand.Next(-float1, float1));
		vehicleStorySpawner.addElement("vehicle1", 0.0F, 0.0F, vector2.getDirection(), 2.0F, 5.0F);
		vehicleStorySpawner.setParameter("zone", zone);
		return true;
	}

	public void spawnElement(VehicleStorySpawner vehicleStorySpawner, VehicleStorySpawner.Element element) {
		IsoGridSquare square = element.square;
		if (square != null) {
			float float1 = element.z;
			IsoMetaGrid.Zone zone = (IsoMetaGrid.Zone)vehicleStorySpawner.getParameter("zone", IsoMetaGrid.Zone.class);
			String string = element.id;
			byte byte1 = -1;
			switch (string.hashCode()) {
			case 2014205573: 
				if (string.equals("vehicle1")) {
					byte1 = 0;
				}

			
			default: 
				switch (byte1) {
				case 0: 
					BaseVehicle baseVehicle = this.addVehicle(zone, element.position.x, element.position.y, float1, element.direction, (String)null, "Base.CarNormal", (Integer)null, (String)null);
					if (baseVehicle != null) {
						baseVehicle = baseVehicle.setSmashed("right");
					}

				
				default: 
				
				}

			
			}
		}
	}
}
