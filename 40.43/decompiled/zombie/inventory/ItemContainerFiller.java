package zombie.inventory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import zombie.core.Rand;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.IsoRoom;

public class ItemContainerFiller {
   public static ArrayList DistributionTarget = new ArrayList();
   public static ArrayList Containers = new ArrayList();

   public static void DistributeGoodItems(IsoCell var0) {
      PlaceOnRandomFloor(var0, "foodItems", "TinnedSoup", 8);
      PlaceOnRandomFloor(var0, "foodItems", "Crisps", 8);
      PlaceOnRandomFloor(var0, "foodItems", "Crisps2", 8);
      PlaceOnRandomFloor(var0, "foodItems", "Crisps3", 8);
      PlaceOnRandomFloor(var0, "foodItems", "Pop", 8);
      PlaceOnRandomFloor(var0, "foodItems", "Pop2", 8);
      PlaceOnRandomFloor(var0, "foodItems", "Pop3", 8);

      int var1;
      ItemContainer var2;
      for(var1 = 0; var1 < 6; ++var1) {
         var2 = getRandomContainer("counter,wardrobe,crate");
         if (var2 != null) {
            var2.AddItem("Shotgun");
            var2.AddItem("ShotgunShells");
         }
      }

      for(var1 = 0; var1 < 15; ++var1) {
         var2 = getRandomContainer("counter,wardrobe,crate");
         if (var2 != null) {
            var2.AddItem("ShotgunShells");
         }
      }

      for(var1 = 0; var1 < 6; ++var1) {
         var2 = getRandomContainer("counter,wardrobe,crate");
         if (var2 != null) {
            var2.AddItem("Shotgun");
         }
      }

      for(var1 = 0; var1 < 8; ++var1) {
         var2 = getRandomContainer("counter,wardrobe,crate");
         if (var2 != null) {
            var2.AddItem("BaseballBat");
         }
      }

      for(var1 = 0; var1 < 30; ++var1) {
         var2 = getRandomContainer("counter,crate,sidetable");
         if (var2 != null) {
            var2.AddItem("Battery");
         }
      }

      for(var1 = 0; var1 < 6; ++var1) {
         var2 = getRandomContainer("crate");
         if (var2 != null) {
            var2.AddItem("PetrolCan");
         }
      }

      for(var1 = 0; var1 < 6; ++var1) {
         var2 = getRandomContainer("crate,counter");
         if (var2 != null) {
            var2.AddItem("Hammer");
         }
      }

      for(var1 = 0; var1 < 1; ++var1) {
         var2 = getRandomContainer("crate,counter");
         if (var2 != null) {
            var2.AddItem("Axe");
         }
      }

      for(var1 = 0; var1 < 4; ++var1) {
         var2 = getRandomContainer("crate,counter");
         if (var2 != null) {
            var2.AddItem("Axe");
         }
      }

      for(var1 = 0; var1 < 60; ++var1) {
         var2 = getRandomContainer("counter,crate,sidetable");
         if (var2 != null) {
            var2.AddItem("Nails");
         }
      }

      for(var1 = 0; var1 < 30; ++var1) {
         var2 = getRandomContainer("wardrobe");
         if (var2 != null) {
            var2.AddItem("Sheet");
         }
      }

      for(var1 = 0; var1 < 30; ++var1) {
         var2 = getRandomContainer("wardrobe");
         if (var2 != null) {
            var2.AddItem("Belt");
         }
      }

      for(var1 = 0; var1 < 30; ++var1) {
         var2 = getRandomContainer("wardrobe");
         if (var2 != null) {
            var2.AddItem("Socks");
         }
      }

      for(var1 = 0; var1 < 20; ++var1) {
         var2 = getRandomContainer("counter,crate,sidetable");
         if (var2 != null) {
            var2.AddItem("Lighter");
         }
      }

      for(var1 = 0; var1 < 30; ++var1) {
         var2 = getRandomContainer("counter,crate,sidetable,fridge");
         if (var2 != null) {
            var2.AddItem("WhiskeyFull");
         }
      }

      for(var1 = 0; var1 < 10; ++var1) {
         var2 = getRandomContainer("vendingsnacks");
         if (var2 != null) {
            var2.AddItem("Crisps");
            var2.AddItem("Crisps2");
            var2.AddItem("Crisps3");
         }
      }

      for(var1 = 0; var1 < 10; ++var1) {
         var2 = getRandomContainer("vendingpop");
         if (var2 != null) {
            var2.AddItem("Pop");
            var2.AddItem("Pop2");
            var2.AddItem("Pop3");
         }
      }

      for(var1 = 0; var1 < 10; ++var1) {
         var2 = getRandomContainer("counter,crate,sidetable,fridge");
         if (var2 != null) {
            var2.AddItem("Chocolate");
         }
      }

      for(var1 = 0; var1 < 5; ++var1) {
         var2 = getRandomContainer("counter,crate,sidetable,fridge");
         if (var2 != null) {
            var2.AddItem("Torch");
         }
      }

      for(var1 = 0; var1 < 10; ++var1) {
         var2 = getRandomContainer("fridge");
         if (var2 != null) {
            var2.AddItem("Bread");
         }
      }

      for(var1 = 0; var1 < 20; ++var1) {
         var2 = getRandomContainer("counter");
         if (var2 != null) {
            var2.AddItem("DishCloth");
         }
      }

      for(var1 = 0; var1 < 20; ++var1) {
         var2 = getRandomContainer("counter,sidetable");
         if (var2 != null) {
            var2.AddItem("Pen");
         }
      }

      for(var1 = 0; var1 < 20; ++var1) {
         var2 = getRandomContainer("counter,sidetable");
         if (var2 != null) {
            var2.AddItem("Pencil");
         }
      }

      for(var1 = 0; var1 < 20; ++var1) {
         var2 = getRandomContainer("fridge");
         if (var2 != null) {
            var2.AddItem("Carrots");
         }
      }

      for(var1 = 0; var1 < 20; ++var1) {
         var2 = getRandomContainer("fridge");
         if (var2 != null) {
            var2.AddItem("Steak");
         }
      }

      for(var1 = 0; var1 < 20; ++var1) {
         var2 = getRandomContainer("counter");
         if (var2 != null) {
            var2.AddItem("Carrots");
         }
      }

      for(var1 = 0; var1 < 20; ++var1) {
         var2 = getRandomContainer("counter");
         if (var2 != null) {
            var2.AddItem("Steak");
         }
      }

      for(var1 = 0; var1 < 30; ++var1) {
         var2 = getRandomContainer("medicine");
         if (var2 != null) {
            var2.AddItem("Pills");
         }
      }

      for(var1 = 0; var1 < 10; ++var1) {
         var2 = getRandomContainer("medicine");
         if (var2 != null) {
            var2.AddItem("PillsBeta");
         }
      }

      for(var1 = 0; var1 < 30; ++var1) {
         var2 = getRandomContainer("medicine");
         if (var2 != null) {
            var2.AddItem("PillsSleepingTablets");
         }
      }

      for(var1 = 0; var1 < 10; ++var1) {
         var2 = getRandomContainer("medicine");
         if (var2 != null) {
            var2.AddItem("PillsAntiDep");
         }
      }

      for(var1 = 0; var1 < 20; ++var1) {
         var2 = getRandomContainer("fridge");
         if (var2 != null) {
            var2.AddItem("Apple");
         }
      }

      for(var1 = 0; var1 < 6; ++var1) {
         var2 = getRandomContainer("counter");
         if (var2 != null) {
            var2.AddItem("TinOpener");
         }
      }

      for(var1 = 0; var1 < 30; ++var1) {
         var2 = getRandomContainer("crate");
         if (var2 != null) {
            var2.AddItem("Plank");
         }
      }

      for(var1 = 0; var1 < 3; ++var1) {
         var2 = getRandomContainer("counter,wardrobe,crate");
         if (var2 != null) {
            var2.AddItem("BaseballBat");
         }
      }

      for(var1 = 0; var1 < 12; ++var1) {
         var2 = getRandomContainer("counter,wardrobe,crate");
         if (var2 != null) {
            var2.AddItem("ShotgunShells");
         }
      }

   }

   public static void FillContainer(ItemContainer var0, String var1) {
      String var2 = var0.type;
      if (var2.equals("counter")) {
         DoCounter(var0, var1);
      }

      if (var2.equals("wardrobe")) {
         DoWardrobe(var0, var1);
      }

      if (var2.equals("medicine")) {
         DoMedicine(var0, var1);
      }

      if (var1.equals("rangerHut") && var0.type.equals("counter")) {
         var0.AddItem("Axe");
      }

      if (var1.equals("tutKitchen2") && var0.type.equals("fridge")) {
         var0.AddItem("Carrots");
         var0.AddItem("Apple");
      }

   }

   public static void FillRoom(IsoRoom var0) {
      if (var0.RoomDef.equals("shopBig")) {
         DoShopBig(var0);
      }

      if (var0.RoomDef.equals("bar")) {
         DoBar(var0);
      }

      Iterator var1 = var0.Containers.iterator();

      while(var1.hasNext()) {
         ItemContainer var2 = (ItemContainer)var1.next();
         FillContainer(var2, var0.RoomDef);
      }

   }

   public static void FillTable(IsoGridSquare var0, String var1) {
      boolean var2 = false;
      boolean var3 = false;
      if (var0.getProperties().Is(IsoFlagType.tableE)) {
         var2 = true;
      }

      if (var0.getProperties().Is(IsoFlagType.tableS)) {
         var3 = true;
      }

      int var4;
      float var5;
      float var6;
      float var7;
      if (var2) {
         for(var4 = 0; var4 < 3; ++var4) {
            var5 = 0.5F;
            var6 = 0.45F + (float)Rand.Next(10) / 200.0F;
            var7 = (float)var4 * 0.33F;
            if (Rand.Next(5) == 0 || var1.equals("shopGeneral") || var1.equals("tutKitchen1")) {
               AddShelfItem(var1, var0, var6, var7, var5);
            }
         }
      }

      if (var3) {
         for(var4 = 0; var4 < 3; ++var4) {
            var5 = 0.5F;
            var6 = 0.45F + (float)Rand.Next(10) / 200.0F;
            var7 = (float)var4 * 0.33F;
            if (Rand.Next(5) == 0 || var1.equals("shopGeneral") || var1.equals("tutKitchen1")) {
               AddShelfItem(var1, var0, var7, var6, var5);
            }
         }
      }

   }

   public static void FillTable(IsoGridSquare var0, String var1, String var2, float var3) {
      boolean var4 = false;
      boolean var5 = false;
      if (var0.getProperties().Is(IsoFlagType.tableE)) {
         var4 = true;
      }

      if (var0.getProperties().Is(IsoFlagType.tableS)) {
         var5 = true;
      }

      int var6;
      float var7;
      float var8;
      if (var4) {
         for(var6 = 0; var6 < 5; ++var6) {
            var7 = 0.8F;
            var8 = (float)var6 * 0.2F;
            AddShelfItem(var1, var0, var7, var8, var3, var2);
         }
      }

      if (var5) {
         for(var6 = 0; var6 < 5; ++var6) {
            var7 = 0.8F;
            var8 = (float)var6 * 0.2F;
            AddShelfItem(var1, var0, var8, var7, var3, var2);
         }
      }

   }

   public static void FillTable(int var0, IsoGridSquare var1, String var2, String var3, float var4) {
      boolean var5 = false;
      boolean var6 = false;
      if (var1.getProperties().Is(IsoFlagType.tableE)) {
         var5 = true;
      }

      if (var1.getProperties().Is(IsoFlagType.tableS)) {
         var6 = true;
      }

      int var7;
      float var8;
      float var9;
      if (var5) {
         for(var7 = 0; var7 < 5; ++var7) {
            if (Rand.Next(var0) == 0) {
               var8 = 0.8F;
               var9 = (float)var7 * 0.2F;
               AddShelfItem(var2, var1, var8, var9, var4, var3);
            }
         }
      }

      if (var6) {
         for(var7 = 0; var7 < 5; ++var7) {
            if (Rand.Next(var0) == 0) {
               var8 = 0.8F;
               var9 = (float)var7 * 0.2F;
               AddShelfItem(var2, var1, var9, var8, var4, var3);
            }
         }
      }

   }

   public static void FillTable(int var0, IsoGridSquare var1, String var2, String var3, float var4, float var5) {
      boolean var6 = false;
      boolean var7 = false;
      if (var1.getProperties().Is(IsoFlagType.tableE)) {
         var6 = true;
      }

      if (var1.getProperties().Is(IsoFlagType.tableS)) {
         var7 = true;
      }

      int var8;
      float var10;
      if (var6) {
         for(var8 = 0; var8 < 5; ++var8) {
            if (Rand.Next(var0) == 0) {
               var10 = (float)var8 * 0.2F;
               AddShelfItem(var2, var1, var5, var10, var4, var3);
            }
         }
      }

      if (var7) {
         for(var8 = 0; var8 < 5; ++var8) {
            if (Rand.Next(var0) == 0) {
               var10 = (float)var8 * 0.2F;
               AddShelfItem(var2, var1, var10, var5, var4, var3);
            }
         }
      }

   }

   public static void FillTable(IsoGridSquare var0, String var1, String var2, float var3, float var4) {
      boolean var5 = false;
      boolean var6 = false;
      if (var0.getProperties().Is(IsoFlagType.tableE)) {
         var5 = true;
      }

      if (var0.getProperties().Is(IsoFlagType.tableS)) {
         var6 = true;
      }

      int var7;
      float var9;
      if (var5) {
         for(var7 = 0; var7 < 5; ++var7) {
            if (Rand.Next(4) == 0) {
               var9 = (float)var7 * 0.2F;
               AddShelfItem(var1, var0, var4, var9, var3, var2);
            }
         }
      }

      if (var6) {
         for(var7 = 0; var7 < 5; ++var7) {
            if (Rand.Next(4) == 0) {
               var9 = (float)var7 * 0.2F;
               AddShelfItem(var1, var0, var9, var4, var3, var2);
            }
         }
      }

   }

   static void AddShelfItem(String var0, IsoGridSquare var1, float var2, float var3, float var4) {
      if (var0.equals("tutKitchen1") && var1.getX() == 40 && var1.getY() == 25 && var2 == 0.33F) {
         var1.AddWorldInventoryItem("Pot", var2, var3, var4);
      }

      if (var0.equals("kitchen")) {
         switch(Rand.Next(4)) {
         case 0:
            var1.AddWorldInventoryItem("WhiskeyHalf", var2, var3, var4);
            break;
         case 1:
            var1.AddWorldInventoryItem("WhiskeyFull", var2, var3, var4);
            break;
         case 2:
            var1.AddWorldInventoryItem("Bread", var2, var3, var4);
            break;
         case 3:
            var1.AddWorldInventoryItem("TinnedSoup", var2, var3, var4);
         }
      }

   }

   private static void AddShelfItem(String var0, IsoGridSquare var1, float var2, float var3, float var4, String var5) {
      var1.AddWorldInventoryItem(var5, var2, var3, var4);
   }

   private static void AddToRandomContainer(IsoRoom var0, String var1) {
      int var2 = Rand.Next(var0.Containers.size());
      ((ItemContainer)var0.Containers.get(var2)).AddItem(var1);
   }

   private static void AddToRandomContainer(IsoRoom var0, String var1, String var2) {
      Stack var3 = new Stack();
      Iterator var4 = var0.Containers.iterator();

      while(var4.hasNext()) {
         ItemContainer var5 = (ItemContainer)var4.next();
         if (var5.type.equals(var2)) {
            var3.add(var5);
         }
      }

      int var6 = Rand.Next(var3.size());
      ((ItemContainer)var3.get(var6)).AddItem(var1);
   }

   private static void DoBar(IsoRoom var0) {
      Iterator var1 = var0.TileList.iterator();

      while(true) {
         IsoGridSquare var2;
         boolean var3;
         int var4;
         IsoObject var5;
         String var6;
         label108:
         do {
            do {
               while(true) {
                  if (!var1.hasNext()) {
                     return;
                  }

                  var2 = (IsoGridSquare)var1.next();
                  if (!var2.getProperties().Is(IsoFlagType.shelfS)) {
                     break;
                  }

                  var3 = false;

                  for(var4 = 0; var4 < var2.getObjects().size(); ++var4) {
                     var5 = (IsoObject)var2.getObjects().get(var4);
                     if (var5.container != null && var5.container.type.equals("counter")) {
                        var3 = true;
                     }
                  }

                  if (!var3) {
                     var6 = "WhiskeyFull";
                     switch(Rand.Next(5)) {
                     case 0:
                        var6 = "WhiskeyFull";
                        break;
                     case 1:
                        var6 = "WhiskeyHalf";
                        break;
                     case 2:
                        var6 = "WhiskeyEmpty";
                        break;
                     case 3:
                        var6 = "WineEmpty";
                        break;
                     case 4:
                        var6 = "WineEmpty2";
                     }

                     FillTable(var2, var0.RoomDef, var6, 0.75F);
                     break;
                  }
               }

               if (!var2.getProperties().Is(IsoFlagType.tableS)) {
                  continue label108;
               }

               var3 = false;

               for(var4 = 0; var4 < var2.getObjects().size(); ++var4) {
                  var5 = (IsoObject)var2.getObjects().get(var4);
                  if (var5.container != null && var5.container.type.equals("counter")) {
                     var3 = true;
                  }
               }
            } while(var3);

            var6 = "WhiskeyFull";
            switch(Rand.Next(5)) {
            case 0:
               var6 = "WhiskeyFull";
               break;
            case 1:
               var6 = "WhiskeyHalf";
               break;
            case 2:
               var6 = "WhiskeyEmpty";
               break;
            case 3:
               var6 = "WineEmpty";
               break;
            case 4:
               var6 = "WineEmpty2";
            }

            FillTable(var2, var0.RoomDef, var6, 0.45F);
         } while(!var2.getProperties().Is(IsoFlagType.floorS));

         var3 = false;

         for(var4 = 0; var4 < var2.getObjects().size(); ++var4) {
            var5 = (IsoObject)var2.getObjects().get(var4);
            if (var5.container != null && var5.container.type.equals("counter")) {
               var3 = true;
            }
         }

         if (!var3) {
            var6 = "WhiskeyFull";
            switch(Rand.Next(5)) {
            case 0:
               var6 = "WhiskeyFull";
               break;
            case 1:
               var6 = "WhiskeyHalf";
               break;
            case 2:
               var6 = "WhiskeyEmpty";
               break;
            case 3:
               var6 = "WineEmpty";
               break;
            case 4:
               var6 = "WineEmpty2";
            }

            FillTable(var2, var0.RoomDef, var6, 0.15F);
         }
      }
   }

   private static void DoCounter(ItemContainer var0, String var1) {
      FillTable(var0.SourceGrid, var1);
      if (!var1.equals("tutKitchen2")) {
         int var3;
         if (var1.equals("shopGeneral")) {
            int var2 = Rand.Next(3) + 1;

            for(var3 = 0; var3 < var2; ++var3) {
               var0.AddItem("Bread");
            }

            var2 = Rand.Next(3) + 1;

            for(var3 = 0; var3 < var2; ++var3) {
               var0.AddItem("WhiskeyFull");
            }

            if (Rand.Next(10) == 0) {
               var0.AddItem("BaseballBat");
            }
         } else if (var1.equals("shed")) {
            byte var4 = 10;

            for(var3 = 0; var3 < var4; ++var3) {
               var0.AddItem("Plank");
            }

            var4 = 3;

            for(var3 = 0; var3 < var4; ++var3) {
               var0.AddItem("Nails");
            }
         }
      }

   }

   private static void DoMedicine(ItemContainer var0, String var1) {
      if (var1.equals("tutorialBathroom")) {
         var0.AddItem("Pills");
      }

   }

   private static void DoShopBig(IsoRoom var0) {
      Iterator var1 = var0.TileList.iterator();

      while(true) {
         IsoGridSquare var2;
         boolean var3;
         int var4;
         IsoObject var5;
         String var6;
         label80:
         do {
            do {
               if (!var1.hasNext()) {
                  return;
               }

               var2 = (IsoGridSquare)var1.next();
               if (!var2.getProperties().Is(IsoFlagType.tableS)) {
                  continue label80;
               }

               var3 = false;

               for(var4 = 0; var4 < var2.getObjects().size(); ++var4) {
                  var5 = (IsoObject)var2.getObjects().get(var4);
                  if (var5.container != null && var5.container.type.equals("counter")) {
                     var3 = true;
                  }
               }
            } while(var3);

            var6 = "None";
            switch(Rand.Next(9)) {
            case 0:
               var6 = "Bread";
               break;
            case 1:
               var6 = "TinnedSoup";
               break;
            case 2:
               var6 = "WhiskeyFull";
               break;
            case 3:
               var6 = "Pop";
               break;
            case 4:
               var6 = "Pop2";
               break;
            case 5:
               var6 = "Pop3";
               break;
            case 6:
               var6 = "Crisps";
               break;
            case 7:
               var6 = "Crisps2";
               break;
            case 8:
               var6 = "Crisps3";
            }

            FillTable(var2, var0.RoomDef, var6, 0.5F);
         } while(!var2.getProperties().Is(IsoFlagType.floorS));

         var3 = false;

         for(var4 = 0; var4 < var2.getObjects().size(); ++var4) {
            var5 = (IsoObject)var2.getObjects().get(var4);
            if (var5.container != null && var5.container.type.equals("counter")) {
               var3 = true;
            }
         }

         if (!var3) {
            var6 = "None";
            switch(Rand.Next(9)) {
            case 0:
               var6 = "Bread";
               break;
            case 1:
               var6 = "TinnedSoup";
               break;
            case 2:
               var6 = "WhiskeyFull";
               break;
            case 3:
               var6 = "Pop";
               break;
            case 4:
               var6 = "Pop2";
               break;
            case 5:
               var6 = "Pop3";
               break;
            case 6:
               var6 = "Crisps";
               break;
            case 7:
               var6 = "Crisps2";
               break;
            case 8:
               var6 = "Crisps3";
            }

            FillTable(var2, var0.RoomDef, var6, 0.15F);
         }
      }
   }

   private static void DoWardrobe(ItemContainer var0, String var1) {
      if (var1.equals("tutorialBedroom")) {
         var0.AddItem("Sheet");
         var0.AddItem("Pillow");
      }

   }

   private static ItemContainer getRandomContainer(String var0) {
      ArrayList var1 = new ArrayList();
      if (DistributionTarget.isEmpty()) {
         return null;
      } else {
         String[] var2 = var0.split(",");
         boolean var3 = false;

         while(!var3) {
            int var4 = Rand.Next(DistributionTarget.size());
            IsoBuilding var5 = (IsoBuilding)DistributionTarget.get(var4);

            for(int var6 = 0; var6 < var5.container.size(); ++var6) {
               for(int var7 = 0; var7 < var2.length; ++var7) {
                  if (((ItemContainer)var5.container.get(var6)).type.equals(var2[var7].trim())) {
                     var1.add(var5.container.get(var6));
                  }
               }
            }

            if (!var1.isEmpty()) {
               var3 = true;
            }
         }

         return (ItemContainer)var1.get(Rand.Next(var1.size()));
      }
   }

   private static void PlaceOnRandomFloor(IsoCell var0, String var1, String var2, int var3) {
      ArrayList var4 = new ArrayList();
      String[] var5 = var1.split(",");
      boolean var6 = false;

      int var7;
      IsoCell.Zone var8;
      while(!var6) {
         for(var7 = 0; var7 < var0.getZoneStack().size(); ++var7) {
            var8 = (IsoCell.Zone)var0.getZoneStack().get(var7);

            for(int var9 = 0; var9 < var5.length; ++var9) {
               if (var8.Name.equals(var5[var9].trim())) {
                  var4.add(var8);
               }
            }
         }

         if (!var4.isEmpty()) {
            var6 = true;
         }
      }

      for(var7 = 0; var7 < var3; ++var7) {
         var8 = (IsoCell.Zone)var4.get(Rand.Next(var4.size()));
         IsoGridSquare var10 = var0.getFreeTile(var8);
         if (var10 != null) {
            var10.AddWorldInventoryItem(var2, (float)(100 + Rand.Next(400)) / 1000.0F, (float)(100 + Rand.Next(400)) / 1000.0F, 0.0F);
         }
      }

   }
}
