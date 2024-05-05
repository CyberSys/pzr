package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.core.Rand;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.vehicles.BaseVehicle;


public final class RVSBanditRoad extends RandomizedVehicleStoryBase {

	public RVSBanditRoad() {
		this.name = "Bandits on Road";
		this.minZoneWidth = 5;
		this.minZoneHeight = 3;
		this.setMinimumDays(30);
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
				BaseVehicle baseVehicle = this.addVehicle(zone, square, chunk, "bad", (String)null, directions);
				BaseVehicle baseVehicle2 = this.addVehicle(zone, square2, chunk, "bad", (String)null, directions2);
				baseVehicle.setSmashed("Front");
				this.addZombiesOnVehicle(Rand.Next(3, 6), "Bandit", (Integer)null, baseVehicle);
				this.addZombiesOnVehicle(Rand.Next(3, 5), (String)null, (Integer)null, baseVehicle2);
				int int1 = Rand.Next(3, 6);
				for (int int2 = 0; int2 < int1; ++int2) {
					int int3 = Rand.Next((int)baseVehicle2.x - 3, (int)baseVehicle2.x + 3);
					int int4 = Rand.Next((int)baseVehicle2.y - 3, (int)baseVehicle2.y + 3);
					this.addTraitOfBlood(directions, 15, int3, int4, 0);
					createRandomDeadBody(int3, int4, 0, (IsoDirections)null, 6);
				}
			}
		}
	}
}
