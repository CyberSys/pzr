package zombie.randomizedWorld.randomizedVehicleStory;

import java.util.ArrayList;
import zombie.characters.IsoZombie;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.core.Rand;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.Vector2;
import zombie.vehicles.BaseVehicle;


public final class RVSAmbulanceCrash extends RandomizedVehicleStoryBase {

	public RVSAmbulanceCrash() {
		this.name = "Ambulance Crash";
		this.minZoneWidth = 5;
		this.minZoneHeight = 7;
		this.setChance(5);
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

				break;
			
			case 2014205574: 
				if (string.equals("vehicle2")) {
					byte1 = 1;
				}

			
			}

			BaseVehicle baseVehicle;
			switch (byte1) {
			case 0: 
				baseVehicle = this.addVehicle(zone, element.position.x, element.position.y, float1, element.direction, (String)null, "Base.VanAmbulance", (Integer)null, (String)null);
				if (baseVehicle != null) {
					this.addZombiesOnVehicle(Rand.Next(1, 3), "AmbulanceDriver", (Integer)null, baseVehicle);
					ArrayList arrayList = this.addZombiesOnVehicle(Rand.Next(1, 3), "HospitalPatient", (Integer)null, baseVehicle);
					for (int int1 = 0; int1 < arrayList.size(); ++int1) {
						for (int int2 = 0; int2 < 7; ++int2) {
							if (Rand.NextBool(2)) {
								((IsoZombie)arrayList.get(int1)).addVisualBandage(BodyPartType.getRandom(), true);
							}
						}
					}
				}

				break;
			
			case 1: 
				baseVehicle = this.addVehicle(zone, element.position.x, element.position.y, float1, element.direction, "bad", (String)null, (Integer)null, (String)null);
				if (baseVehicle == null) {
				}

			
			}
		}
	}
}
