package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.vehicles.BaseVehicle;


public final class RVSBurntCar extends RandomizedVehicleStoryBase {

	public RVSBurntCar() {
		this.name = "Burnt Car";
		this.minZoneWidth = 5;
		this.minZoneHeight = 3;
		this.setChance(13);
	}

	public void randomizeVehicleStory(IsoMetaGrid.Zone zone, IsoChunk chunk) {
		IsoGridSquare square = this.getCenterOfChunk(zone, chunk);
		if (square != null) {
			BaseVehicle baseVehicle = this.addVehicle(zone, square, chunk, (String)null, "Base.CarNormal", (Integer)null, IsoDirections.S, (String)null);
			baseVehicle = baseVehicle.setSmashed("right");
		}
	}
}
