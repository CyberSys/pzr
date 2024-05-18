package zombie.scripting.objects;

import java.util.ArrayList;
import java.util.Stack;
import zombie.core.Rand;
import zombie.inventory.ItemContainerFiller;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.scripting.ScriptManager;

public class ShelfDistribution extends BaseScriptObject {
   public String Zone;
   public int LootedValue = 0;
   public ArrayList Entries = new ArrayList(1);
   private float ItemDepth = 0.5F;

   public void Load(String var1, String[] var2) {
      String[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String var6 = var3[var5];
         var6 = var6.trim();
         String[] var7 = var6.split("=");
         if (var7.length == 2) {
            this.DoLine(var7[0].trim(), var7[1].trim());
         } else if (var7[0].trim().length() > 0) {
            this.Entries.add(new ShelfDistribution.Entry(var7[0].trim(), 1, 1));
         }
      }

   }

   private void DoLine(String var1, String var2) {
      if (var1.equals("Zone")) {
         this.Zone = var2;
      }

      if (var1.equals("LootedValue")) {
         this.LootedValue = Integer.parseInt(var2);
      }

      if (var1.equals("ItemDepth")) {
         this.ItemDepth = Float.parseFloat(var2);
      }

   }

   public void Process(IsoCell var1) {
      Stack var2 = ScriptManager.instance.getZones(this.Zone);

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         Zone var4 = (Zone)var2.get(var3);

         for(int var5 = var4.x; var5 < var4.x2; ++var5) {
            for(int var6 = var4.y; var6 < var4.y2; ++var6) {
               IsoGridSquare var7 = var1.getGridSquare(var5, var6, var4.z);
               if (var7 != null) {
                  float var8;
                  String var9;
                  if (var7.getProperties().Is(IsoFlagType.floorS) || var7.getProperties().Is(IsoFlagType.floorE)) {
                     var8 = 0.10000001F;
                     var9 = ((ShelfDistribution.Entry)this.Entries.get(Rand.Next(this.Entries.size()))).objectType;
                     if (!var9.contains(".")) {
                        var9 = this.module.name + "." + var9;
                     }

                     ItemContainerFiller.FillTable(this.LootedValue, var7, (String)null, var9, var8, this.ItemDepth);
                  }

                  if (var7.getProperties().Is(IsoFlagType.tableS) || var7.getProperties().Is(IsoFlagType.tableE)) {
                     var8 = 0.4F;
                     var9 = ((ShelfDistribution.Entry)this.Entries.get(Rand.Next(this.Entries.size()))).objectType;
                     if (!var9.contains(".")) {
                        var9 = this.module.name + "." + var9;
                     }

                     ItemContainerFiller.FillTable(this.LootedValue, var7, (String)null, var9, var8);
                  }

                  if (var7.getProperties().Is(IsoFlagType.shelfE) || var7.getProperties().Is(IsoFlagType.shelfS)) {
                     var8 = 0.65F;
                     var9 = ((ShelfDistribution.Entry)this.Entries.get(Rand.Next(this.Entries.size()))).objectType;
                     if (!var9.contains(".")) {
                        var9 = this.module.name + "." + var9;
                     }

                     ItemContainerFiller.FillTable(this.LootedValue, var7, (String)null, var9, var8, this.ItemDepth);
                  }
               }
            }
         }
      }

   }

   public class Entry {
      String objectType;
      int minimum;
      int maximum;

      public Entry(String var2, int var3, int var4) {
         this.objectType = var2;
         this.minimum = var3;
         this.maximum = var4;
      }
   }
}
