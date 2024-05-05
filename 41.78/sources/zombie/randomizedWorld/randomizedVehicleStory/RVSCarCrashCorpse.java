package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.core.Rand;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.Vector2;
import zombie.vehicles.BaseVehicle;


public final class RVSCarCrashCorpse extends RandomizedVehicleStoryBase {

	public RVSCarCrashCorpse() {
		this.name = "Basic Car Crash Corpse";
		this.minZoneWidth = 6;
		this.minZoneHeight = 11;
		this.setChance(10);
	}

	public void randomizeVehicleStory(IsoMetaGrid.Zone zone, IsoChunk chunk) {
		float float1 = 0.5235988F;
		this.callVehicleStorySpawner(zone, chunk, Rand.Next(-float1, float1));
	}

	public boolean initVehicleStorySpawner(IsoMetaGrid.Zone zone, IsoChunk chunk, boolean boolean1) {
		VehicleStorySpawner vehicleStorySpawner = VehicleStorySpawner.getInstance();
		vehicleStorySpawner.clear();
		Vector2 vector2 = IsoDirections.N.ToVector();
		float float1 = 2.5F;
		vehicleStorySpawner.addElement("vehicle1", 0.0F, float1, vector2.getDirection(), 2.0F, 5.0F);
		vehicleStorySpawner.addElement("corpse", 0.0F, float1 - (float)(boolean1 ? 7 : Rand.Next(4, 7)), vector2.getDirection() + 3.1415927F, 1.0F, 2.0F);
		vehicleStorySpawner.setParameter("zone", zone);
		return true;
	}

	public void spawnElement(VehicleStorySpawner vehicleStorySpawner, VehicleStorySpawner.Element element) {
		IsoGridSquare square = element.square;
		if (square != null) {
			float float1 = element.z;
			IsoMetaGrid.Zone zone = (IsoMetaGrid.Zone)vehicleStorySpawner.getParameter("zone", IsoMetaGrid.Zone.class);
			BaseVehicle baseVehicle = (BaseVehicle)vehicleStorySpawner.getParameter("vehicle1", BaseVehicle.class);
			String string = element.id;
			byte byte1 = -1;
			switch (string.hashCode()) {
			case -1354663044: 
				if (string.equals("corpse")) {
					byte1 = 0;
				}

				break;
			
			case 2014205573: 
				if (string.equals("vehicle1")) {
					byte1 = 1;
				}

			
			}

			switch (byte1) {
			case 0: 
				if (baseVehicle != null) {
					createRandomDeadBody(element.position.x, element.position.y, element.z, element.direction, false, 35, 30, (String)null);
					this.addTrailOfBlood(element.position.x, element.position.y, element.z, element.direction, 15);
				}

				break;
			
			case 1: 
				baseVehicle = this.addVehicle(zone, element.position.x, element.position.y, float1, element.direction, "bad", (String)null, (Integer)null, (String)null);
				if (baseVehicle != null) {
					baseVehicle = baseVehicle.setSmashed("Front");
					baseVehicle.setBloodIntensity("Front", 1.0F);
					vehicleStorySpawner.setParameter("vehicle1", baseVehicle);
				}

			
			}
		}
	}
}
