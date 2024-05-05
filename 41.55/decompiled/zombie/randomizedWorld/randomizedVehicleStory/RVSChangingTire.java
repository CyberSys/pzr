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

   public void randomizeVehicleStory(IsoMetaGrid.Zone var1, IsoChunk var2) {
      IsoGridSquare var3 = IsoCell.getInstance().getGridSquare(this.minX, this.minY, var1.z);
      if (var3 != null) {
         IsoDirections var4 = Rand.NextBool(2) ? IsoDirections.N : IsoDirections.S;
         byte var5 = 2;
         byte var6 = 0;
         if (this.horizontalZone) {
            var4 = Rand.NextBool(2) ? IsoDirections.E : IsoDirections.W;
            var5 = 0;
            var6 = 2;
         }

         BaseVehicle var7 = this.addVehicle(var1, var3, var2, "good", (String)null, var4);
         var7.setGeneralPartCondition(0.7F, 40.0F);
         var7.setRust(0.0F);
         VehiclePart var8 = var7.getPartById("TireRearLeft");
         if (var4 == IsoDirections.E || var4 == IsoDirections.N) {
            var8 = var7.getPartById("TireRearRight");
         }

         var7.setTireRemoved(var8.getWheelIndex(), true);
         var8.setModelVisible("InflatedTirePlusWheel", false);
         var8.setInventoryItem((InventoryItem)null);
         this.addZombiesOnVehicle(2, (String)null, (Integer)null, var7);
         IsoGridSquare var9 = IsoCell.getInstance().getGridSquare((double)(var3.x + var5), (double)(var3.y + var6), (double)var7.z);
         if (var9 != null) {
            var9.AddWorldInventoryItem("Base.LugWrench", 0.0F, 0.0F, 0.0F);
            var9.AddWorldInventoryItem("Base.Jack", 0.3F, 0.3F, 0.0F);
            this.addBloodSplat(var9, Rand.Next(10, 20));
            var5 = 3;
            var6 = 0;
            if (this.horizontalZone) {
               var5 = 0;
               var6 = 3;
            }

            var9 = IsoCell.getInstance().getGridSquare((double)(var3.x + var5), (double)(var3.y + var6), (double)var7.z);
            this.addBloodSplat(var9, Rand.Next(10, 20));
            InventoryItem var10 = var9.AddWorldInventoryItem("Base.OldTire" + var7.getScript().getMechanicType(), 0.0F, 0.0F, 0.0F);
            var10.setCondition(0);
            var5 = 1;
            var6 = 0;
            if (this.horizontalZone) {
               var5 = 0;
               var6 = 1;
            }

            var9 = IsoCell.getInstance().getGridSquare((double)(var3.x + var5), (double)(var3.y + var6), (double)var7.z);
            this.addBloodSplat(var9, Rand.Next(10, 20));
            InventoryItem var11 = var9.AddWorldInventoryItem("Base.ModernTire" + var7.getScript().getMechanicType(), 0.5F, 0.5F, 0.0F);
            var11.setItemCapacity((float)var11.getMaxCapacity());
         }

      }
   }
}
