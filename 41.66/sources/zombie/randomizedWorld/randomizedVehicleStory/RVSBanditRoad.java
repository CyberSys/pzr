package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.core.Rand;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.Vector2;
import zombie.vehicles.BaseVehicle;


public final class RVSBanditRoad extends RandomizedVehicleStoryBase {

	public RVSBanditRoad() {
		this.name = "Bandits on Road";
		this.minZoneWidth = 7;
		this.minZoneHeight = 9;
		this.setMinimumDays(30);
		this.setChance(3);
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
		vehicleStorySpawner.addElement("vehicle1", 0.0F, 2.0F, vector2.getDirection(), 2.0F, 5.0F);
		boolean boolean2 = Rand.NextBool(2);
		vector2 = boolean2 ? IsoDirections.E.ToVector() : IsoDirections.W.ToVector();
		vector2.rotate(Rand.Next(-float1, float1));
		float float2 = 0.0F;
		float float3 = -1.5F;
		vehicleStorySpawner.addElement("vehicle2", float2, float3, vector2.getDirection(), 2.0F, 5.0F);
		int int1 = Rand.Next(3, 6);
		for (int int2 = 0; int2 < int1; ++int2) {
			float float4 = Rand.Next(float2 - 3.0F, float2 + 3.0F);
			float float5 = Rand.Next(float3 - 3.0F, float3 + 3.0F);
			vehicleStorySpawner.addElement("corpse", float4, float5, Rand.Next(0.0F, 6.2831855F), 1.0F, 2.0F);
		}

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
			case -1354663044: 
				if (string.equals("corpse")) {
					byte1 = 0;
				}

				break;
			
			case 2014205573: 
				if (string.equals("vehicle1")) {
					byte1 = 1;
				}

				break;
			
			case 2014205574: 
				if (string.equals("vehicle2")) {
					byte1 = 2;
				}

			
			}

			BaseVehicle baseVehicle;
			switch (byte1) {
			case 0: 
				baseVehicle = (BaseVehicle)vehicleStorySpawner.getParameter("vehicle1", BaseVehicle.class);
				if (baseVehicle != null) {
					createRandomDeadBody(element.position.x, element.position.y, element.z, element.direction, false, 6, 0, (String)null);
					this.addTrailOfBlood(element.position.x, element.position.y, element.z, Vector2.getDirection(element.position.x - baseVehicle.x, element.position.y - baseVehicle.y), 15);
				}

				break;
			
			case 1: 
				baseVehicle = this.addVehicle(zone, element.position.x, element.position.y, float1, element.direction, "bad", (String)null, (Integer)null, (String)null);
				if (baseVehicle != null) {
					baseVehicle = baseVehicle.setSmashed("Front");
					this.addZombiesOnVehicle(Rand.Next(3, 6), "Bandit", (Integer)null, baseVehicle);
					vehicleStorySpawner.setParameter("vehicle1", baseVehicle);
				}

				break;
			
			case 2: 
				baseVehicle = this.addVehicle(zone, element.position.x, element.position.y, float1, element.direction, "bad", (String)null, (Integer)null, (String)null);
				if (baseVehicle != null) {
					this.addZombiesOnVehicle(Rand.Next(3, 5), (String)null, (Integer)null, baseVehicle);
					vehicleStorySpawner.setParameter("vehicle2", baseVehicle);
				}

			
			}
		}
	}
}
