package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.core.Rand;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.Vector2;
import zombie.vehicles.BaseVehicle;


public final class RVSPoliceBlockade extends RandomizedVehicleStoryBase {

	public RVSPoliceBlockade() {
		this.name = "Police Blockade";
		this.minZoneWidth = 8;
		this.minZoneHeight = 8;
		this.setChance(3);
		this.setMaximumDays(30);
	}

	public void randomizeVehicleStory(IsoMetaGrid.Zone zone, IsoChunk chunk) {
		this.callVehicleStorySpawner(zone, chunk, 0.0F);
	}

	public boolean initVehicleStorySpawner(IsoMetaGrid.Zone zone, IsoChunk chunk, boolean boolean1) {
		VehicleStorySpawner vehicleStorySpawner = VehicleStorySpawner.getInstance();
		vehicleStorySpawner.clear();
		float float1 = 0.17453292F;
		if (boolean1) {
			float1 = 0.0F;
		}

		float float2 = 1.5F;
		float float3 = 1.0F;
		if (this.zoneWidth >= 10) {
			float2 = 2.5F;
			float3 = 0.0F;
		}

		IsoDirections directions = Rand.NextBool(2) ? IsoDirections.W : IsoDirections.E;
		Vector2 vector2 = directions.ToVector();
		vector2.rotate(Rand.Next(-float1, float1));
		vehicleStorySpawner.addElement("vehicle1", -float2, float3, vector2.getDirection(), 2.0F, 5.0F);
		vector2 = directions.RotLeft(4).ToVector();
		vector2.rotate(Rand.Next(-float1, float1));
		vehicleStorySpawner.addElement("vehicle2", float2, -float3, vector2.getDirection(), 2.0F, 5.0F);
		String string = "Base.CarLightsPolice";
		if (Rand.NextBool(3)) {
			string = "Base.PickUpVanLightsPolice";
		}

		vehicleStorySpawner.setParameter("zone", zone);
		vehicleStorySpawner.setParameter("script", string);
		return true;
	}

	public void spawnElement(VehicleStorySpawner vehicleStorySpawner, VehicleStorySpawner.Element element) {
		IsoGridSquare square = element.square;
		if (square != null) {
			float float1 = element.z;
			IsoMetaGrid.Zone zone = (IsoMetaGrid.Zone)vehicleStorySpawner.getParameter("zone", IsoMetaGrid.Zone.class);
			String string = vehicleStorySpawner.getParameterString("script");
			String string2 = element.id;
			byte byte1 = -1;
			switch (string2.hashCode()) {
			case 2014205573: 
				if (string2.equals("vehicle1")) {
					byte1 = 0;
				}

				break;
			
			case 2014205574: 
				if (string2.equals("vehicle2")) {
					byte1 = 1;
				}

			
			}

			switch (byte1) {
			case 0: 
			
			case 1: 
				BaseVehicle baseVehicle = this.addVehicle(zone, element.position.x, element.position.y, float1, element.direction, (String)null, string, (Integer)null, (String)null);
				if (baseVehicle != null) {
					if (Rand.NextBool(3)) {
						baseVehicle.setHeadlightsOn(true);
						baseVehicle.setLightbarLightsMode(2);
					}

					this.addZombiesOnVehicle(Rand.Next(2, 4), "police", (Integer)null, baseVehicle);
				}

			
			default: 
			
			}
		}
	}
}
