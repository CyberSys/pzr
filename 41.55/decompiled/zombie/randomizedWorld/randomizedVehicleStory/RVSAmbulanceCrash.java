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

   public void randomizeVehicleStory(IsoMetaGrid.Zone var1, IsoChunk var2) {
      IsoDirections var3 = Rand.NextBool(2) ? IsoDirections.N : IsoDirections.S;
      boolean var4 = Rand.NextBool(2);
      IsoDirections var5 = var4 ? var3.RotLeft(2) : var3.RotRight(2);
      if (var3 == IsoDirections.S) {
         var5 = var4 ? var3.RotRight(2) : var3.RotLeft(2);
      }

      IsoGridSquare var6 = this.getCenterOfChunk(var1, var2);
      if (var6 != null) {
         byte var7 = 0;
         byte var8 = 0;
         if (this.horizontalZone) {
            var7 = 3;
            if (!var4) {
               var7 = -3;
            }
         } else {
            var8 = -3;
            if (var3 == IsoDirections.S) {
               var8 = 3;
            }
         }

         IsoGridSquare var9 = var6.getCell().getGridSquare(var6.x + var7, var6.y + var8, var6.z);
         if (var9 != null) {
            BaseVehicle var10 = this.addVehicle(var1, var6, var2, (String)null, "Base.VanAmbulance", var3);
            this.addVehicle(var1, var9, var2, "bad", (String)null, var5);
            this.addZombiesOnVehicle(Rand.Next(1, 3), "AmbulanceDriver", (Integer)null, var10);
            ArrayList var11 = this.addZombiesOnVehicle(Rand.Next(1, 3), "HospitalPatient", (Integer)null, var10);

            for(int var12 = 0; var12 < var11.size(); ++var12) {
               for(int var13 = 0; var13 < 7; ++var13) {
                  if (Rand.NextBool(2)) {
                     ((IsoZombie)var11.get(var12)).addVisualBandage(BodyPartType.getRandom(), true);
                  }
               }
            }

         }
      }
   }
}
