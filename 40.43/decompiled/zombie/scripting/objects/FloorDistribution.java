package zombie.scripting.objects;

import java.util.ArrayList;
import java.util.Stack;
import zombie.core.Rand;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.scripting.ScriptManager;

public class FloorDistribution extends BaseScriptObject {
   public String Zone;
   public ArrayList Entries = new ArrayList(1);

   public void Load(String var1, String[] var2) {
      String[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String var6 = var3[var5];
         var6 = var6.trim();
         String[] var7 = var6.split("=");
         if (var7.length == 2) {
            this.DoLine(var7[0].trim(), var7[1].trim());
         }
      }

   }

   private void DoLine(String var1, String var2) {
      if (var1.equals("Zone")) {
         this.Zone = var2;
      } else {
         boolean var3 = false;
         boolean var4 = false;
         int var6;
         int var7;
         if (var2.contains("-")) {
            String[] var5 = var2.split("-");
            var6 = Integer.parseInt(var5[0].trim());
            var7 = Integer.parseInt(var5[1].trim());
         } else {
            var6 = var7 = Integer.parseInt(var2.trim());
         }

         FloorDistribution.Entry var8 = new FloorDistribution.Entry(var1.trim(), var6, var7);
         this.Entries.add(var8);
      }

   }

   public void Process(IsoCell var1) {
      Stack var2 = ScriptManager.instance.getZones(this.Zone);
      if (!var2.isEmpty()) {
         for(int var3 = 0; var3 < this.Entries.size(); ++var3) {
            int var4 = Rand.Next(((FloorDistribution.Entry)this.Entries.get(var3)).minimum, ((FloorDistribution.Entry)this.Entries.get(var3)).maximum);

            for(int var5 = 0; var5 < var4; ++var5) {
               Zone var6 = (Zone)var2.get(Rand.Next(var2.size()));
               IsoGridSquare var7 = var1.getFreeTile(new IsoCell.Zone(var6.name, var6.x, var6.y, var6.x2 - var6.x, var6.y2 - var6.y, var6.z));
               if (var7 != null) {
                  String var8 = ((FloorDistribution.Entry)this.Entries.get(var3)).objectType;
                  if (!var8.contains(".")) {
                     var8 = this.module.name + "." + var8;
                  }

                  var7.AddWorldInventoryItem(var8, (float)(100 + Rand.Next(400)) / 1000.0F, (float)(100 + Rand.Next(400)) / 1000.0F, 0.0F);
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
