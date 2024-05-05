package zombie.randomizedWorld.randomizedVehicleStory;

import java.util.ArrayList;
import zombie.core.Rand;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.vehicles.BaseVehicle;

public final class RVSUtilityVehicle extends RandomizedVehicleStoryBase {
   private ArrayList tools = null;
   private ArrayList carpenterTools = null;

   public RVSUtilityVehicle() {
      this.name = "Utility Vehicle";
      this.minZoneWidth = 3;
      this.minZoneHeight = 3;
      this.setChance(7);
      this.tools = new ArrayList();
      this.tools.add("Base.PickAxe");
      this.tools.add("Base.Shovel");
      this.tools.add("Base.Shovel2");
      this.tools.add("Base.Hammer");
      this.tools.add("Base.LeadPipe");
      this.tools.add("Base.PipeWrench");
      this.tools.add("Base.Sledgehammer");
      this.tools.add("Base.Sledgehammer2");
      this.carpenterTools = new ArrayList();
      this.carpenterTools.add("Base.Hammer");
      this.carpenterTools.add("Base.NailsBox");
      this.carpenterTools.add("Base.Plank");
      this.carpenterTools.add("Base.Plank");
      this.carpenterTools.add("Base.Plank");
      this.carpenterTools.add("Base.Screwdriver");
      this.carpenterTools.add("Base.Saw");
      this.carpenterTools.add("Base.Saw");
      this.carpenterTools.add("Base.Woodglue");
   }

   public void randomizeVehicleStory(IsoMetaGrid.Zone var1, IsoChunk var2) {
      int var3 = Rand.Next(0, 7);
      switch(var3) {
      case 0:
         this.doUtilityVehicle(var1, var2, (String)null, "Base.PickUpTruck", "ConstructionWorker", 0, "ConstructionWorker", this.tools, Rand.Next(0, 3), true);
         break;
      case 1:
         this.doUtilityVehicle(var1, var2, "police", (String)null, "Police", (Integer)null, (String)null, (ArrayList)null, 0, false);
         break;
      case 2:
         this.doUtilityVehicle(var1, var2, "fire", (String)null, "Fireman", (Integer)null, (String)null, (ArrayList)null, 0, false);
         break;
      case 3:
         this.doUtilityVehicle(var1, var2, "ranger", (String)null, "Ranger", (Integer)null, (String)null, (ArrayList)null, 0, true);
         break;
      case 4:
         this.doUtilityVehicle(var1, var2, "mccoy", (String)null, "McCoys", 0, "Carpenter", this.carpenterTools, Rand.Next(2, 6), true);
         break;
      case 5:
         this.doUtilityVehicle(var1, var2, "postal", (String)null, "Postal", (Integer)null, (String)null, (ArrayList)null, 0, false);
         break;
      case 6:
         this.doUtilityVehicle(var1, var2, "fossoil", (String)null, "Fossoil", (Integer)null, (String)null, (ArrayList)null, 0, false);
      }

   }

   public void doUtilityVehicle(IsoMetaGrid.Zone var1, IsoChunk var2, String var3, String var4, String var5, Integer var6, String var7, ArrayList var8, int var9, boolean var10) {
      IsoGridSquare var11 = this.getCenterOfChunk(var1, var2);
      if (var11 != null) {
         IsoDirections var12 = Rand.NextBool(2) ? IsoDirections.N : IsoDirections.S;
         if (this.horizontalZone) {
            var12 = Rand.NextBool(2) ? IsoDirections.E : IsoDirections.W;
         }

         BaseVehicle var13 = this.addVehicle(var1, var11, var2, var3, var4, (Integer)null, var12, var7);
         if (var13 != null) {
            this.addZombiesOnVehicle(Rand.Next(2, 5), var5, var6, var13);
            if (var10 && Rand.NextBool(7)) {
               this.addTrailer(var13, var1, var2, var3, var7, Rand.NextBool(1) ? "Base.Trailer" : "Base.TrailerCover");
            }

            if (var8 != null) {
               for(int var14 = 0; var14 < var9; ++var14) {
                  IsoGridSquare var15 = IsoCell.getInstance().getGridSquare((double)(var11.x + Rand.Next(-4, 4)), (double)(var11.y + Rand.Next(-4, 4)), (double)var13.z);
                  if (var15 != null) {
                     var15.AddWorldInventoryItem((String)var8.get(Rand.Next(var8.size())), 0.3F, 0.3F, 0.0F);
                  }
               }
            }

         }
      }
   }
}
