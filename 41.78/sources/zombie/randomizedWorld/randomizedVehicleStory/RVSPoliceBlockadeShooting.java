package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.core.Rand;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoObject;
import zombie.iso.Vector2;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehiclePart;


public final class RVSPoliceBlockadeShooting extends RandomizedVehicleStoryBase {

	public RVSPoliceBlockadeShooting() {
		this.name = "Police Blockade Shooting";
		this.minZoneWidth = 8;
		this.minZoneHeight = 8;
		this.setChance(1);
		this.setMaximumDays(30);
	}

	public boolean isValid(IsoMetaGrid.Zone zone, IsoChunk chunk, boolean boolean1) {
		boolean boolean2 = super.isValid(zone, chunk, boolean1);
		return !boolean2 ? false : zone.isRectangle();
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

		boolean boolean2 = Rand.NextBool(2);
		if (boolean1) {
			boolean2 = true;
		}

		IsoDirections directions = Rand.NextBool(2) ? IsoDirections.W : IsoDirections.E;
		Vector2 vector2 = directions.ToVector();
		vector2.rotate(Rand.Next(-float1, float1));
		vehicleStorySpawner.addElement("vehicle1", -float2, float3, vector2.getDirection(), 2.0F, 5.0F);
		vector2 = directions.RotLeft(4).ToVector();
		vector2.rotate(Rand.Next(-float1, float1));
		vehicleStorySpawner.addElement("vehicle2", float2, -float3, vector2.getDirection(), 2.0F, 5.0F);
		vehicleStorySpawner.addElement("barricade", 0.0F, boolean2 ? -float3 - 2.5F : float3 + 2.5F, IsoDirections.N.ToVector().getDirection(), (float)this.zoneWidth, 1.0F);
		int int1 = Rand.Next(7, 15);
		for (int int2 = 0; int2 < int1; ++int2) {
			vehicleStorySpawner.addElement("corpse", Rand.Next((float)(-this.zoneWidth) / 2.0F + 1.0F, (float)this.zoneWidth / 2.0F - 1.0F), boolean2 ? (float)Rand.Next(-7, -4) - float3 : (float)Rand.Next(5, 8) + float3, IsoDirections.getRandom().ToVector().getDirection(), 1.0F, 2.0F);
		}

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
			case -1354663044: 
				if (string2.equals("corpse")) {
					byte1 = 1;
				}

				break;
			
			case 1971942889: 
				if (string2.equals("barricade")) {
					byte1 = 0;
				}

				break;
			
			case 2014205573: 
				if (string2.equals("vehicle1")) {
					byte1 = 2;
				}

				break;
			
			case 2014205574: 
				if (string2.equals("vehicle2")) {
					byte1 = 3;
				}

			
			}

			BaseVehicle baseVehicle;
			switch (byte1) {
			case 0: 
				int int1;
				IsoGridSquare square2;
				int int2;
				int int3;
				int int4;
				if (this.horizontalZone) {
					int2 = (int)(element.position.y - element.width / 2.0F);
					int3 = (int)(element.position.y + element.width / 2.0F) - 1;
					int4 = (int)element.position.x;
					for (int1 = int2; int1 <= int3; ++int1) {
						square2 = IsoCell.getInstance().getGridSquare(int4, int1, zone.z);
						if (square2 != null) {
							if (int1 != int2 && int1 != int3) {
								square2.AddTileObject(IsoObject.getNew(square2, "construction_01_9", (String)null, false));
							} else {
								square2.AddTileObject(IsoObject.getNew(square2, "street_decoration_01_26", (String)null, false));
							}
						}
					}

					return;
				} else {
					int2 = (int)(element.position.x - element.width / 2.0F);
					int3 = (int)(element.position.x + element.width / 2.0F) - 1;
					int4 = (int)element.position.y;
					for (int1 = int2; int1 <= int3; ++int1) {
						square2 = IsoCell.getInstance().getGridSquare(int1, int4, zone.z);
						if (square2 != null) {
							if (int1 != int2 && int1 != int3) {
								square2.AddTileObject(IsoObject.getNew(square2, "construction_01_8", (String)null, false));
							} else {
								square2.AddTileObject(IsoObject.getNew(square2, "street_decoration_01_26", (String)null, false));
							}
						}
					}

					return;
				}

			
			case 1: 
				baseVehicle = (BaseVehicle)vehicleStorySpawner.getParameter("vehicle1", BaseVehicle.class);
				if (baseVehicle != null) {
					createRandomDeadBody(element.position.x, element.position.y, (float)zone.z, element.direction, false, 10, 10, (String)null);
					IsoDirections directions = this.horizontalZone ? (element.position.x < baseVehicle.x ? IsoDirections.W : IsoDirections.E) : (element.position.y < baseVehicle.y ? IsoDirections.N : IsoDirections.S);
					float float2 = directions.ToVector().getDirection();
					this.addTrailOfBlood(element.position.x, element.position.y, element.z, float2, 5);
				}

				break;
			
			case 2: 
			
			case 3: 
				baseVehicle = this.addVehicle(zone, element.position.x, element.position.y, float1, element.direction, (String)null, string, (Integer)null, (String)null);
				if (baseVehicle != null) {
					vehicleStorySpawner.setParameter(element.id, baseVehicle);
					if (Rand.NextBool(3)) {
						baseVehicle.setHeadlightsOn(true);
						baseVehicle.setLightbarLightsMode(2);
						VehiclePart vehiclePart = baseVehicle.getBattery();
						if (vehiclePart != null) {
							vehiclePart.setLastUpdated(0.0F);
						}
					}

					String string3 = "PoliceRiot";
					Integer integer = 0;
					this.addZombiesOnVehicle(Rand.Next(2, 4), string3, integer, baseVehicle);
				}

			
			}
		}
	}
}
