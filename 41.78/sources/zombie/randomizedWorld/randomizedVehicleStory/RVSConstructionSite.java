package zombie.randomizedWorld.randomizedVehicleStory;

import java.util.ArrayList;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoObject;
import zombie.iso.Vector2;
import zombie.vehicles.BaseVehicle;


public final class RVSConstructionSite extends RandomizedVehicleStoryBase {
	private ArrayList tools = null;

	public RVSConstructionSite() {
		this.name = "Construction Site";
		this.minZoneWidth = 6;
		this.minZoneHeight = 6;
		this.setChance(3);
		this.tools = new ArrayList();
		this.tools.add("Base.PickAxe");
		this.tools.add("Base.Shovel");
		this.tools.add("Base.Shovel2");
		this.tools.add("Base.Hammer");
		this.tools.add("Base.LeadPipe");
		this.tools.add("Base.PipeWrench");
		this.tools.add("Base.Sledgehammer");
		this.tools.add("Base.Sledgehammer2");
	}

	public void randomizeVehicleStory(IsoMetaGrid.Zone zone, IsoChunk chunk) {
		this.callVehicleStorySpawner(zone, chunk, 0.0F);
	}

	public boolean initVehicleStorySpawner(IsoMetaGrid.Zone zone, IsoChunk chunk, boolean boolean1) {
		VehicleStorySpawner vehicleStorySpawner = VehicleStorySpawner.getInstance();
		vehicleStorySpawner.clear();
		boolean boolean2 = Rand.NextBool(2);
		if (boolean1) {
			boolean2 = true;
		}

		int int1 = boolean2 ? 1 : -1;
		Vector2 vector2 = IsoDirections.N.ToVector();
		float float1 = 0.5235988F;
		if (boolean1) {
			float1 = 0.0F;
		}

		vector2.rotate(Rand.Next(-float1, float1));
		vehicleStorySpawner.addElement("vehicle1", (float)(-int1) * 2.0F, 0.0F, vector2.getDirection(), 2.0F, 5.0F);
		float float2 = 0.0F;
		vehicleStorySpawner.addElement("manhole", (float)int1 * 1.5F, 1.5F, float2, 3.0F, 3.0F);
		int int2 = Rand.Next(0, 3);
		for (int int3 = 0; int3 < int2; ++int3) {
			float2 = 0.0F;
			vehicleStorySpawner.addElement("tool", (float)int1 * Rand.Next(0.0F, 3.0F), -Rand.Next(0.7F, 2.3F), float2, 1.0F, 1.0F);
		}

		vehicleStorySpawner.setParameter("zone", zone);
		return true;
	}

	public void spawnElement(VehicleStorySpawner vehicleStorySpawner, VehicleStorySpawner.Element element) {
		IsoGridSquare square = element.square;
		if (square != null) {
			float float1 = PZMath.max(element.position.x - (float)square.x, 0.001F);
			float float2 = PZMath.max(element.position.y - (float)square.y, 0.001F);
			float float3 = 0.0F;
			float float4 = element.z;
			IsoMetaGrid.Zone zone = (IsoMetaGrid.Zone)vehicleStorySpawner.getParameter("zone", IsoMetaGrid.Zone.class);
			BaseVehicle baseVehicle = (BaseVehicle)vehicleStorySpawner.getParameter("vehicle1", BaseVehicle.class);
			String string = element.id;
			byte byte1 = -1;
			switch (string.hashCode()) {
			case 3565976: 
				if (string.equals("tool")) {
					byte1 = 1;
				}

				break;
			
			case 835476762: 
				if (string.equals("manhole")) {
					byte1 = 0;
				}

				break;
			
			case 2014205573: 
				if (string.equals("vehicle1")) {
					byte1 = 2;
				}

			
			}

			switch (byte1) {
			case 0: 
				square.AddTileObject(IsoObject.getNew(square, "street_decoration_01_15", (String)null, false));
				IsoGridSquare square2 = square.getAdjacentSquare(IsoDirections.E);
				if (square2 != null) {
					square2.AddTileObject(IsoObject.getNew(square2, "street_decoration_01_26", (String)null, false));
				}

				square2 = square.getAdjacentSquare(IsoDirections.W);
				if (square2 != null) {
					square2.AddTileObject(IsoObject.getNew(square2, "street_decoration_01_26", (String)null, false));
				}

				square2 = square.getAdjacentSquare(IsoDirections.S);
				if (square2 != null) {
					square2.AddTileObject(IsoObject.getNew(square2, "street_decoration_01_26", (String)null, false));
				}

				square2 = square.getAdjacentSquare(IsoDirections.N);
				if (square2 != null) {
					square2.AddTileObject(IsoObject.getNew(square2, "street_decoration_01_26", (String)null, false));
				}

				break;
			
			case 1: 
				String string2 = (String)this.tools.get(Rand.Next(this.tools.size()));
				square.AddWorldInventoryItem(string2, float1, float2, float3);
				break;
			
			case 2: 
				baseVehicle = this.addVehicle(zone, element.position.x, element.position.y, float4, element.direction, (String)null, "Base.PickUpTruck", (Integer)null, "ConstructionWorker");
				if (baseVehicle != null) {
					this.addZombiesOnVehicle(Rand.Next(2, 5), "ConstructionWorker", 0, baseVehicle);
					this.addZombiesOnVehicle(1, "Foreman", 0, baseVehicle);
					vehicleStorySpawner.setParameter("vehicle1", baseVehicle);
				}

			
			}
		}
	}
}
