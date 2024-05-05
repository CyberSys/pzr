package zombie.randomizedWorld.randomizedVehicleStory;

import java.util.ArrayList;
import zombie.characters.IsoZombie;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.core.Rand;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.vehicles.BaseVehicle;


public final class RVSAmbulanceCrash extends RandomizedVehicleStoryBase {

	public RVSAmbulanceCrash() {
		this.name = "Ambulance Crash";
		this.setChance(5);
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
				BaseVehicle baseVehicle = this.addVehicle(zone, square, chunk, (String)null, "Base.VanAmbulance", directions);
				this.addVehicle(zone, square2, chunk, "bad", (String)null, directions2);
				this.addZombiesOnVehicle(Rand.Next(1, 3), "AmbulanceDriver", (Integer)null, baseVehicle);
				ArrayList arrayList = this.addZombiesOnVehicle(Rand.Next(1, 3), "HospitalPatient", (Integer)null, baseVehicle);
				for (int int1 = 0; int1 < arrayList.size(); ++int1) {
					for (int int2 = 0; int2 < 7; ++int2) {
						if (Rand.NextBool(2)) {
							((IsoZombie)arrayList.get(int1)).addVisualBandage(BodyPartType.getRandom(), true);
						}
					}
				}
			}
		}
	}
}
