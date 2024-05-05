package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.core.Rand;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.vehicles.BaseVehicle;


public final class RVSCarCrashCorpse extends RandomizedVehicleStoryBase {

	public RVSCarCrashCorpse() {
		this.name = "Basic Car Crash Corpse";
		this.minZoneWidth = 5;
		this.minZoneHeight = 3;
		this.setChance(10);
	}

	public void randomizeVehicleStory(IsoMetaGrid.Zone zone, IsoChunk chunk) {
		IsoDirections directions = Rand.NextBool(2) ? IsoDirections.N : IsoDirections.S;
		if (this.horizontalZone) {
			directions = Rand.NextBool(2) ? IsoDirections.W : IsoDirections.E;
		}

		IsoGridSquare square = this.getCenterOfChunk(zone, chunk);
		if (square != null) {
			int int1 = 0;
			int int2 = 0;
			if (directions == IsoDirections.S) {
				int2 = Rand.Next(4, 7);
			}

			if (directions == IsoDirections.N) {
				int2 = Rand.Next(-7, -4);
			}

			if (directions == IsoDirections.W) {
				int1 = Rand.Next(-7, -4);
			}

			if (directions == IsoDirections.E) {
				int1 = Rand.Next(4, 7);
			}

			BaseVehicle baseVehicle = this.addVehicle(zone, square, chunk, "bad", (String)null, directions);
			baseVehicle.setSmashed("Front");
			baseVehicle.setBloodIntensity("Front", 1.0F);
			createRandomDeadBody(square.x + int1, square.y + int2, square.z, directions.RotLeft(4), 35, 30);
			this.addTraitOfBlood(directions, 15, square.x + int1, square.y + int2, square.z);
		}
	}
}
