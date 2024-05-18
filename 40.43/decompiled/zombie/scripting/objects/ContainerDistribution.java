package zombie.scripting.objects;

import java.util.ArrayList;
import zombie.core.Rand;
import zombie.inventory.ItemContainer;
import zombie.inventory.ItemContainerFiller;
import zombie.iso.IsoCell;
import zombie.iso.areas.IsoRoom;

public class ContainerDistribution extends BaseScriptObject {
   public String RoomDef;
   public ArrayList Containers = new ArrayList(1);
   public ArrayList Entries = new ArrayList(1);
   static ArrayList roomTemp = new ArrayList();

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
      if (var1.equals("Room")) {
         this.RoomDef = var2;
      } else if (var1.equals("Containers")) {
         String[] var3 = var2.split("/");
         String[] var4 = var3;
         int var5 = var3.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String var7 = var4[var6];
            this.Containers.add(var7.trim());
         }
      } else {
         boolean var8 = false;
         boolean var10 = false;
         int var9;
         int var11;
         if (var2.contains("-")) {
            String[] var12 = var2.split("-");
            var9 = Integer.parseInt(var12[0].trim());
            var11 = Integer.parseInt(var12[1].trim());
         } else {
            var9 = var11 = Integer.parseInt(var2.trim());
         }

         ContainerDistribution.Entry var13 = new ContainerDistribution.Entry(var1.trim(), var9, var11);
         this.Entries.add(var13);
      }

   }

   public boolean ContainerValid(String var1) {
      if (this.Containers.isEmpty()) {
         return true;
      } else {
         for(int var2 = 0; var2 < this.Containers.size(); ++var2) {
            if (((String)this.Containers.get(var2)).equals(var1)) {
               return true;
            }
         }

         return false;
      }
   }

   public void Process(IsoCell var1) {
      int var4;
      if (this.RoomDef != null) {
         ArrayList var2 = this.FindRooms(var1);
         if (var2.isEmpty()) {
            return;
         }

         IsoRoom var3 = null;

         for(var4 = 0; var4 < this.Entries.size(); ++var4) {
            ContainerDistribution.Entry var5 = (ContainerDistribution.Entry)this.Entries.get(var4);
            int var6 = Rand.Next(var5.minimum, var5.maximum);

            for(int var7 = 0; var7 < var6; ++var7) {
               var3 = (IsoRoom)var2.get(Rand.Next(var2.size()));
               if (var3 != null && !var3.Containers.isEmpty()) {
                  int var8 = Rand.Next(var3.Containers.size());
                  if (this.ContainerValid(((ItemContainer)var3.Containers.get(var8)).type)) {
                     ItemContainer var16 = (ItemContainer)var3.Containers.get(var8);
                     String var17 = var5.objectType;
                     if (!var17.contains(".")) {
                        var17 = this.module.name + "." + var17;
                     }

                     var16.AddItem(var17);
                  } else {
                     boolean var9 = false;

                     for(int var10 = 0; var10 < var3.Containers.size(); ++var10) {
                        if (this.ContainerValid(((ItemContainer)var3.Containers.get(var10)).type)) {
                           var9 = true;
                        }
                     }

                     if (var9) {
                        --var7;
                     }
                  }
               } else {
                  --var7;
               }
            }
         }
      } else {
         for(int var11 = 0; var11 < this.Entries.size(); ++var11) {
            ContainerDistribution.Entry var12 = (ContainerDistribution.Entry)this.Entries.get(var11);
            var4 = Rand.Next(var12.minimum, var12.maximum);

            for(int var13 = 0; var13 < var4; ++var13) {
               ItemContainer var14 = this.getRandomContainer();
               if (var14 != null) {
                  String var15 = var12.objectType;
                  if (!var15.contains(".")) {
                     var15 = this.module.name + "." + var15;
                  }

                  var14.AddItem(var15);
               }
            }
         }
      }

   }

   private ItemContainer getRandomContainer() {
      ArrayList var1 = new ArrayList();
      if (ItemContainerFiller.DistributionTarget.isEmpty()) {
         return null;
      } else {
         boolean var2 = false;
         int var3 = 2000;

         while(!var2) {
            --var3;
            if (var3 <= 0) {
               return null;
            }

            for(int var4 = 0; var4 < ItemContainerFiller.Containers.size(); ++var4) {
               for(int var5 = 0; var5 < this.Containers.size(); ++var5) {
                  if (((ItemContainer)ItemContainerFiller.Containers.get(var4)).type.equals(this.Containers.get(var5))) {
                     var1.add(ItemContainerFiller.Containers.get(var4));
                  }
               }
            }

            if (!var1.isEmpty()) {
               var2 = true;
            }
         }

         return (ItemContainer)var1.get(Rand.Next(var1.size()));
      }
   }

   private IsoRoom FindRoom(IsoCell var1) {
      roomTemp.clear();

      for(int var2 = 0; var2 < var1.getRoomList().size(); ++var2) {
         IsoRoom var3 = (IsoRoom)var1.getRoomList().get(var2);
         if (var3.RoomDef != null && var3.RoomDef.equals(this.RoomDef)) {
            roomTemp.add(var3);
         }
      }

      if (!roomTemp.isEmpty()) {
         return (IsoRoom)roomTemp.get(Rand.Next(roomTemp.size()));
      } else {
         return null;
      }
   }

   private ArrayList FindRooms(IsoCell var1) {
      roomTemp.clear();

      for(int var2 = 0; var2 < var1.getRoomList().size(); ++var2) {
         IsoRoom var3 = (IsoRoom)var1.getRoomList().get(var2);
         if (var3.RoomDef != null && var3.RoomDef.equals(this.RoomDef)) {
            roomTemp.add(var3);
         }
      }

      return roomTemp;
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
