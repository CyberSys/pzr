package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.core.Rand;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.vehicles.BaseVehicle;


public final class RVSTrailerCrash extends RandomizedVehicleStoryBase {

	public RVSTrailerCrash() {
		this.name = "Trailer Crash";
		this.minZoneWidth = 5;
		this.minZoneHeight = 3;
		this.setChance(3);
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
				BaseVehicle baseVehicle = this.addVehicle(zone, square, chunk, (String)null, "Base.PickUpVan", directions);
				BaseVehicle baseVehicle2 = this.addVehicle(zone, square2, chunk, "bad", (String)null, directions2);
				if (baseVehicle != null) {
					if (Rand.Next(10) < 4) {
						this.addZombiesOnVehicle(Rand.Next(2, 5), (String)null, (Integer)null, baseVehicle);
					}

					String string = Rand.NextBool(2) ? "Base.Trailer" : "Base.TrailerCover";
					if (Rand.NextBool(6)) {
						string = "Base.TrailerAdvert";
					}

					BaseVehicle[] baseVehicleArray = this.addSmashedOverlay(baseVehicle, baseVehicle2, byte1, byte2, this.horizontalZone, true);
					baseVehicle = baseVehicleArray[0];
					BaseVehicle baseVehicle3 = this.addTrailer(baseVehicle, zone, chunk, (String)null, (String)null, string);
					if (baseVehicle3 != null && Rand.NextBool(3)) {
						baseVehicle3.setAngles(baseVehicle3.getAngleX(), Rand.Next(90.0F, 110.0F), baseVehicle3.getAngleZ());
					}
				}
			}
		}
	}
}
