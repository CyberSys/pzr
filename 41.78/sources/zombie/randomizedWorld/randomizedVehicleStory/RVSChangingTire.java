package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.inventory.InventoryItem;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.Vector2;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehiclePart;


public final class RVSChangingTire extends RandomizedVehicleStoryBase {

	public RVSChangingTire() {
		this.name = "Changing Tire";
		this.minZoneWidth = 5;
		this.minZoneHeight = 5;
		this.setChance(3);
	}

	public void randomizeVehicleStory(IsoMetaGrid.Zone zone, IsoChunk chunk) {
		float float1 = 0.5235988F;
		this.callVehicleStorySpawner(zone, chunk, Rand.Next(-float1, float1));
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
		vehicleStorySpawner.addElement("vehicle1", (float)int1 * -1.5F, 0.0F, vector2.getDirection(), 2.0F, 5.0F);
		vehicleStorySpawner.addElement("tire1", (float)int1 * 0.0F, 0.0F, 0.0F, 1.0F, 1.0F);
		vehicleStorySpawner.addElement("tool1", (float)int1 * 0.8F, -0.2F, 0.0F, 1.0F, 1.0F);
		vehicleStorySpawner.addElement("tool2", (float)int1 * 1.2F, 0.2F, 0.0F, 1.0F, 1.0F);
		vehicleStorySpawner.addElement("tire2", (float)int1 * 2.0F, 0.0F, 0.0F, 1.0F, 1.0F);
		vehicleStorySpawner.setParameter("zone", zone);
		vehicleStorySpawner.setParameter("removeRightWheel", boolean2);
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
			boolean boolean1 = vehicleStorySpawner.getParameterBoolean("removeRightWheel");
			BaseVehicle baseVehicle = (BaseVehicle)vehicleStorySpawner.getParameter("vehicle1", BaseVehicle.class);
			String string = element.id;
			byte byte1 = -1;
			switch (string.hashCode()) {
			case 110369225: 
				if (string.equals("tire1")) {
					byte1 = 0;
				}

				break;
			
			case 110369226: 
				if (string.equals("tire2")) {
					byte1 = 1;
				}

				break;
			
			case 110545305: 
				if (string.equals("tool1")) {
					byte1 = 2;
				}

				break;
			
			case 110545306: 
				if (string.equals("tool2")) {
					byte1 = 3;
				}

				break;
			
			case 2014205573: 
				if (string.equals("vehicle1")) {
					byte1 = 4;
				}

			
			}

			InventoryItem inventoryItem;
			switch (byte1) {
			case 0: 
				if (baseVehicle != null) {
					inventoryItem = square.AddWorldInventoryItem("Base.ModernTire" + baseVehicle.getScript().getMechanicType(), float1, float2, float3);
					if (inventoryItem != null) {
						inventoryItem.setItemCapacity((float)inventoryItem.getMaxCapacity());
					}

					this.addBloodSplat(square, Rand.Next(10, 20));
				}

				break;
			
			case 1: 
				if (baseVehicle != null) {
					inventoryItem = square.AddWorldInventoryItem("Base.OldTire" + baseVehicle.getScript().getMechanicType(), float1, float2, float3);
					if (inventoryItem != null) {
						inventoryItem.setCondition(0);
					}
				}

				break;
			
			case 2: 
				square.AddWorldInventoryItem("Base.LugWrench", float1, float2, float3);
				break;
			
			case 3: 
				square.AddWorldInventoryItem("Base.Jack", float1, float2, float3);
				break;
			
			case 4: 
				baseVehicle = this.addVehicle(zone, element.position.x, element.position.y, float4, element.direction, "good", (String)null, (Integer)null, (String)null);
				if (baseVehicle != null) {
					baseVehicle.setGeneralPartCondition(0.7F, 40.0F);
					baseVehicle.setRust(0.0F);
					VehiclePart vehiclePart = baseVehicle.getPartById(boolean1 ? "TireRearRight" : "TireRearLeft");
					baseVehicle.setTireRemoved(vehiclePart.getWheelIndex(), true);
					vehiclePart.setModelVisible("InflatedTirePlusWheel", false);
					vehiclePart.setInventoryItem((InventoryItem)null);
					this.addZombiesOnVehicle(2, (String)null, (Integer)null, baseVehicle);
					vehicleStorySpawner.setParameter("vehicle1", baseVehicle);
				}

			
			}
		}
	}
}
