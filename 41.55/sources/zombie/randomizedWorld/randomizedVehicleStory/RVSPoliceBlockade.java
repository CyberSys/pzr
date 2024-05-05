package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.core.Rand;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.vehicles.BaseVehicle;


public final class RVSPoliceBlockade extends RandomizedVehicleStoryBase {

	public RVSPoliceBlockade() {
		this.name = "Police Blockade";
		this.setChance(3);
		this.setMaximumDays(30);
	}

	public boolean isValid(IsoMetaGrid.Zone zone, IsoChunk chunk, boolean boolean1) {
		boolean boolean2 = super.isValid(zone, chunk, boolean1);
		if (!boolean2) {
			return false;
		} else {
			byte byte1 = 10;
			if (this.horizontalZone) {
				return chunk.wy * byte1 <= zone.y && (chunk.wy + 1) * byte1 >= zone.y + zone.h;
			} else {
				return chunk.wx * byte1 <= zone.x && (chunk.wx + 1) * byte1 >= zone.x + zone.w;
			}
		}
	}

	public void randomizeVehicleStory(IsoMetaGrid.Zone zone, IsoChunk chunk) {
		IsoDirections directions = Rand.NextBool(2) ? IsoDirections.N : IsoDirections.S;
		if (!this.horizontalZone) {
			directions = Rand.NextBool(2) ? IsoDirections.E : IsoDirections.W;
		}

		IsoDirections directions2 = directions.RotLeft(4);
		IsoGridSquare square = IsoCell.getInstance().getGridSquare(this.minX, this.minY, zone.z);
		if (square != null) {
			byte byte1 = 0;
			byte byte2 = 0;
			if (!this.horizontalZone) {
				byte1 = 5;
			} else {
				byte2 = 5;
			}

			IsoGridSquare square2 = square.getCell().getGridSquare(square.x + byte1, square.y + byte2, square.z);
			if (square2 != null) {
				String string = "Base.CarLightsPolice";
				if (Rand.NextBool(3)) {
					string = "Base.PickUpVanLightsPolice";
				}

				BaseVehicle baseVehicle = this.addVehicle(zone, square, chunk, (String)null, string, directions);
				if (Rand.NextBool(3)) {
					baseVehicle.setHeadlightsOn(true);
					baseVehicle.setLightbarLightsMode(2);
				}

				BaseVehicle baseVehicle2 = this.addVehicle(zone, square2, chunk, (String)null, string, directions2);
				if (Rand.NextBool(3)) {
					baseVehicle2.setHeadlightsOn(true);
					baseVehicle2.setLightbarLightsMode(2);
				}

				this.addZombiesOnVehicle(Rand.Next(2, 4), "police", (Integer)null, baseVehicle);
				this.addZombiesOnVehicle(Rand.Next(2, 4), "police", (Integer)null, baseVehicle2);
			}
		}
	}
}
