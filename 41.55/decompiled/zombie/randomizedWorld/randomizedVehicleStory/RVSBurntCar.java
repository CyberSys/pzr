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

   public void randomizeVehicleStory(IsoMetaGrid.Zone var1, IsoChunk var2) {
      IsoGridSquare var3 = this.getCenterOfChunk(var1, var2);
      if (var3 != null) {
         BaseVehicle var4 = this.addVehicle(var1, var3, var2, (String)null, "Base.CarNormal", (Integer)null, IsoDirections.S, (String)null);
         var4 = var4.setSmashed("right");
      }
   }
}
