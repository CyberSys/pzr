package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.core.Rand;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.vehicles.BaseVehicle;


public final class RVSCarCrash extends RandomizedVehicleStoryBase {

	public RVSCarCrash() {
		this.name = "Basic Car Crash";
		this.minZoneWidth = 5;
		this.minZoneHeight = 3;
		this.setChance(25);
	}

	public void randomizeVehicleStory(IsoMetaGrid.Zone zone, IsoChunk chunk) {
		IsoDirections directions = Rand.NextBool(2) ? IsoDirections.N : IsoDirections.S;
		boolean boolean1 = Rand.NextBool(2);
		IsoDirections directions2 = boolean1 ? directions.RotLeft(2) : directions.RotRight(2);
		if (directions == IsoDirections.S) {
			directions2 = boolean1 ? directions.RotRight(2) : directions.RotLeft(2);
		}

		IsoGridSquare square = this.getCenterOfChunk(zone, chunk);
		if (square != null) {
			byte byte1 = 0;
			byte byte2 = 0;
			if (this.horizontalZone) {
				byte1 = 3;
				if (!boolean1) {
					byte1 = -3;
				}
			} else {
				byte2 = -3;
				if (directions == IsoDirections.S) {
					byte2 = 3;
				}
			}

			IsoGridSquare square2 = square.getCell().getGridSquare(square.x + byte1, square.y + byte2, square.z);
			if (square2 != null) {
				BaseVehicle baseVehicle = this.addVehicle(zone, square, chunk, "bad", (String)null, directions);
				BaseVehicle baseVehicle2 = this.addVehicle(zone, square2, chunk, "bad", (String)null, directions2);
				if (Rand.NextBool(3)) {
					BaseVehicle[] baseVehicleArray = this.addSmashedOverlay(baseVehicle, baseVehicle2, byte1, byte2, this.horizontalZone, true);
					baseVehicle = baseVehicleArray[0];
				}

				if (Rand.Next(10) < 4) {
					this.addZombiesOnVehicle(Rand.Next(2, 5), (String)null, (Integer)null, baseVehicle);
				}
			}
		}
	}
}
