package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.core.Rand;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.Vector2;
import zombie.vehicles.BaseVehicle;


public final class RVSTrailerCrash extends RandomizedVehicleStoryBase {

	public RVSTrailerCrash() {
		this.name = "Trailer Crash";
		this.minZoneWidth = 5;
		this.minZoneHeight = 12;
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
		float float2 = 0.0F;
		float float3 = -1.5F;
		vehicleStorySpawner.addElement("vehicle1", float2, float3, vector2.getDirection(), 2.0F, 5.0F);
		byte byte1 = 4;
		vehicleStorySpawner.addElement("trailer", float2, float3 + 2.5F + 1.0F + (float)byte1 / 2.0F, vector2.getDirection(), 2.0F, (float)byte1);
		boolean boolean2 = Rand.NextBool(2);
		vector2 = boolean2 ? IsoDirections.E.ToVector() : IsoDirections.W.ToVector();
		vector2.rotate(Rand.Next(-float1, float1));
		float float4 = 0.0F;
		float float5 = float3 - 2.5F - 1.0F;
		vehicleStorySpawner.addElement("vehicle2", float4, float5, vector2.getDirection(), 2.0F, 5.0F);
		vehicleStorySpawner.setParameter("zone", zone);
		vehicleStorySpawner.setParameter("east", boolean2);
		return true;
	}

	public void spawnElement(VehicleStorySpawner vehicleStorySpawner, VehicleStorySpawner.Element element) {
		IsoGridSquare square = element.square;
		if (square != null) {
			float float1 = element.z;
			IsoMetaGrid.Zone zone = (IsoMetaGrid.Zone)vehicleStorySpawner.getParameter("zone", IsoMetaGrid.Zone.class);
			boolean boolean1 = vehicleStorySpawner.getParameterBoolean("east");
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
			String string2;
			switch (byte1) {
			case 0: 
				baseVehicle = this.addVehicle(zone, element.position.x, element.position.y, float1, element.direction, (String)null, "Base.PickUpVan", (Integer)null, (String)null);
				if (baseVehicle != null) {
					baseVehicle = baseVehicle.setSmashed("Front");
					baseVehicle.setBloodIntensity("Front", 1.0F);
					string2 = Rand.NextBool(2) ? "Base.Trailer" : "Base.TrailerCover";
					if (Rand.NextBool(6)) {
						string2 = "Base.TrailerAdvert";
					}

					BaseVehicle baseVehicle2 = this.addTrailer(baseVehicle, zone, square.getChunk(), (String)null, (String)null, string2);
					if (baseVehicle2 != null && Rand.NextBool(3)) {
						baseVehicle2.setAngles(baseVehicle2.getAngleX(), Rand.Next(90.0F, 110.0F), baseVehicle2.getAngleZ());
					}

					if (Rand.Next(10) < 4) {
						this.addZombiesOnVehicle(Rand.Next(2, 5), (String)null, (Integer)null, baseVehicle);
					}

					vehicleStorySpawner.setParameter("vehicle1", baseVehicle);
				}

				break;
			
			case 1: 
				baseVehicle = this.addVehicle(zone, element.position.x, element.position.y, float1, element.direction, "bad", (String)null, (Integer)null, (String)null);
				if (baseVehicle != null) {
					string2 = boolean1 ? "Right" : "Left";
					baseVehicle = baseVehicle.setSmashed(string2);
					baseVehicle.setBloodIntensity(string2, 1.0F);
					vehicleStorySpawner.setParameter("vehicle2", baseVehicle);
				}

			
			}
		}
	}
}
