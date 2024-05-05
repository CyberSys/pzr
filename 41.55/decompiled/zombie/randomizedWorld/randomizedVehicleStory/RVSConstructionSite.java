package zombie.randomizedWorld.randomizedVehicleStory;

import java.util.ArrayList;
import zombie.core.Rand;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoObject;
import zombie.vehicles.BaseVehicle;

public final class RVSConstructionSite extends RandomizedVehicleStoryBase {
   private ArrayList tools = null;

   public RVSConstructionSite() {
      this.name = "Construction Site";
      this.minZoneWidth = 7;
      this.minZoneHeight = 4;
      this.setChance(3);
      this.tools = new ArrayList();
      this.tools.add("Base.PickAxe");
      this.tools.add("Base.Shovel");
      this.tools.add("Base.Shovel2");
      this.tools.add("Base.Hammer");
      this.tools.add("Base.LeadPipe");
      this.tools.add("Base.PipeWrench");
      this.tools.add("Base.Sledgehammer");
      this.tools.add("Base.Sledgehammer2");
   }

   public void randomizeVehicleStory(IsoMetaGrid.Zone var1, IsoChunk var2) {
      IsoGridSquare var3 = IsoCell.getInstance().getGridSquare(this.minX, this.minY, var1.z);
      IsoGridSquare var4 = this.getCenterOfChunk(var1, var2);
      if (var3 != null && var4 != null) {
         IsoDirections var5 = Rand.NextBool(2) ? IsoDirections.N : IsoDirections.S;
         if (this.horizontalZone) {
            var5 = Rand.NextBool(2) ? IsoDirections.E : IsoDirections.W;
         }

         BaseVehicle var6 = this.addVehicle(var1, var3, var2, (String)null, "Base.PickUpTruck", (Integer)null, var5, "ConstructionWorker");
         this.addZombiesOnVehicle(Rand.Next(2, 5), "ConstructionWorker", 0, var6);
         this.addZombiesOnVehicle(1, "Foreman", 0, var6);
         var4.AddTileObject(IsoObject.getNew(var4, "street_decoration_01_15", (String)null, false));
         IsoGridSquare var7 = IsoCell.getInstance().getGridSquare((double)(var4.x + 1), (double)var4.y, (double)var6.z);
         if (var7 != null) {
            var7.AddTileObject(IsoObject.getNew(var7, "street_decoration_01_26", (String)null, false));
         }

         var7 = IsoCell.getInstance().getGridSquare((double)(var4.x - 1), (double)var4.y, (double)var6.z);
         if (var7 != null) {
            var7.AddTileObject(IsoObject.getNew(var7, "street_decoration_01_26", (String)null, false));
         }

         var7 = IsoCell.getInstance().getGridSquare((double)var4.x, (double)(var4.y + 1), (double)var6.z);
         if (var7 != null) {
            var7.AddTileObject(IsoObject.getNew(var7, "street_decoration_01_26", (String)null, false));
         }

         var7 = IsoCell.getInstance().getGridSquare((double)var4.x, (double)(var4.y - 1), (double)var6.z);
         if (var7 != null) {
            var7.AddTileObject(IsoObject.getNew(var7, "street_decoration_01_26", (String)null, false));
         }

         int var8 = Rand.Next(0, 3);

         for(int var9 = 0; var9 < var8; ++var9) {
            var7 = IsoCell.getInstance().getGridSquare((double)(var4.x + Rand.Next(-4, 4)), (double)(var4.y + Rand.Next(-4, 4)), (double)var6.z);
            if (var7 != null) {
               var7.AddWorldInventoryItem((String)this.tools.get(Rand.Next(this.tools.size())), 0.3F, 0.3F, 0.0F);
            }
         }

      }
   }
}
