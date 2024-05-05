package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.core.Rand;
import zombie.inventory.InventoryItem;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehiclePart;


public final class RVSChangingTire extends RandomizedVehicleStoryBase {

	public RVSChangingTire() {
		this.name = "Changing Tire";
		this.minZoneWidth = 4;
		this.minZoneHeight = 4;
		this.setChance(3);
	}

	public void randomizeVehicleStory(IsoMetaGrid.Zone zone, IsoChunk chunk) {
		IsoGridSquare square = IsoCell.getInstance().getGridSquare(this.minX, this.minY, zone.z);
		if (square != null) {
			IsoDirections directions = Rand.NextBool(2) ? IsoDirections.N : IsoDirections.S;
			byte byte1 = 2;
			byte byte2 = 0;
			if (this.horizontalZone) {
				directions = Rand.NextBool(2) ? IsoDirections.E : IsoDirections.W;
				byte1 = 0;
				byte2 = 2;
			}

			BaseVehicle baseVehicle = this.addVehicle(zone, square, chunk, "good", (String)null, directions);
			baseVehicle.setGeneralPartCondition(0.7F, 40.0F);
			baseVehicle.setRust(0.0F);
			VehiclePart vehiclePart = baseVehicle.getPartById("TireRearLeft");
			if (directions == IsoDirections.E || directions == IsoDirections.N) {
				vehiclePart = baseVehicle.getPartById("TireRearRight");
			}

			baseVehicle.setTireRemoved(vehiclePart.getWheelIndex(), true);
			vehiclePart.setModelVisible("InflatedTirePlusWheel", false);
			vehiclePart.setInventoryItem((InventoryItem)null);
			this.addZombiesOnVehicle(2, (String)null, (Integer)null, baseVehicle);
			IsoGridSquare square2 = IsoCell.getInstance().getGridSquare((double)(square.x + byte1), (double)(square.y + byte2), (double)baseVehicle.z);
			if (square2 != null) {
				square2.AddWorldInventoryItem("Base.LugWrench", 0.0F, 0.0F, 0.0F);
				square2.AddWorldInventoryItem("Base.Jack", 0.3F, 0.3F, 0.0F);
				this.addBloodSplat(square2, Rand.Next(10, 20));
				byte1 = 3;
				byte2 = 0;
				if (this.horizontalZone) {
					byte1 = 0;
					byte2 = 3;
				}

				square2 = IsoCell.getInstance().getGridSquare((double)(square.x + byte1), (double)(square.y + byte2), (double)baseVehicle.z);
				this.addBloodSplat(square2, Rand.Next(10, 20));
				InventoryItem inventoryItem = square2.AddWorldInventoryItem("Base.OldTire" + baseVehicle.getScript().getMechanicType(), 0.0F, 0.0F, 0.0F);
				inventoryItem.setCondition(0);
				byte1 = 1;
				byte2 = 0;
				if (this.horizontalZone) {
					byte1 = 0;
					byte2 = 1;
				}

				square2 = IsoCell.getInstance().getGridSquare((double)(square.x + byte1), (double)(square.y + byte2), (double)baseVehicle.z);
				this.addBloodSplat(square2, Rand.Next(10, 20));
				InventoryItem inventoryItem2 = square2.AddWorldInventoryItem("Base.ModernTire" + baseVehicle.getScript().getMechanicType(), 0.5F, 0.5F, 0.0F);
				inventoryItem2.setItemCapacity((float)inventoryItem2.getMaxCapacity());
			}
		}
	}
}
