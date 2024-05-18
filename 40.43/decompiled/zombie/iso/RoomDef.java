package zombie.iso;

import java.util.ArrayList;
import zombie.core.Rand;

public class RoomDef {
   public boolean bExplored = false;
   public int IndoorZombies = 0;
   public boolean bLightsActive = false;
   public String name;
   public int level;
   public BuildingDef building;
   public int ID = -1;
   public ArrayList rects = new ArrayList(1);
   public ArrayList objects = new ArrayList(0);
   public int x = 100000;
   public int y = 100000;
   public int x2 = -10000;
   public int y2 = -10000;
   public int area;

   public boolean isInside(int var1, int var2, int var3) {
      int var4 = this.building.x;
      int var5 = this.building.y;

      for(int var6 = 0; var6 < this.rects.size(); ++var6) {
         int var7 = ((RoomDef.RoomRect)this.rects.get(var6)).x;
         int var8 = ((RoomDef.RoomRect)this.rects.get(var6)).y;
         int var9 = ((RoomDef.RoomRect)this.rects.get(var6)).getX2();
         int var10 = ((RoomDef.RoomRect)this.rects.get(var6)).getY2();
         if (var1 >= var7 && var2 >= var8 && var1 < var9 && var2 < var10 && var3 == this.level) {
            return true;
         }
      }

      return false;
   }

   public boolean intersects(int var1, int var2, int var3, int var4) {
      for(int var5 = 0; var5 < this.rects.size(); ++var5) {
         RoomDef.RoomRect var6 = (RoomDef.RoomRect)this.rects.get(var5);
         if (var1 + var3 > var6.getX() && var1 < var6.getX2() && var2 + var4 > var6.getY() && var2 < var6.getY2()) {
            return true;
         }
      }

      return false;
   }

   public ArrayList getObjects() {
      return this.objects;
   }

   public ArrayList getMetaObjects() {
      return this.objects;
   }

   public BuildingDef getBuilding() {
      return this.building;
   }

   public String getName() {
      return this.name;
   }

   public ArrayList getRects() {
      return this.rects;
   }

   public int getY() {
      return this.y;
   }

   public int getX() {
      return this.x;
   }

   public int getX2() {
      return this.x2;
   }

   public int getY2() {
      return this.y2;
   }

   public int getW() {
      return this.x2 - this.x;
   }

   public int getH() {
      return this.y2 - this.y;
   }

   public int getZ() {
      return this.level;
   }

   public void CalculateBounds() {
      for(int var1 = 0; var1 < this.rects.size(); ++var1) {
         RoomDef.RoomRect var2 = (RoomDef.RoomRect)this.rects.get(var1);
         if (var2.x < this.x) {
            this.x = var2.x;
         }

         if (var2.y < this.y) {
            this.y = var2.y;
         }

         if (var2.x + var2.w > this.x2) {
            this.x2 = var2.x + var2.w;
         }

         if (var2.y + var2.h > this.y2) {
            this.y2 = var2.y + var2.h;
         }

         this.area += var2.w * var2.h;
      }

   }

   public RoomDef(int var1, String var2) {
      this.ID = var1;
      this.name = var2;
   }

   public void setBuilding(BuildingDef var1) {
      this.building = var1;
   }

   public int getArea() {
      return this.area;
   }

   public void setExplored(boolean var1) {
      this.bExplored = var1;
   }

   public IsoGridSquare getFreeSquare() {
      ArrayList var1 = new ArrayList();

      for(int var2 = 0; var2 < this.rects.size(); ++var2) {
         RoomDef.RoomRect var3 = (RoomDef.RoomRect)this.rects.get(var2);

         for(int var4 = var3.getX(); var4 < var3.getX2(); ++var4) {
            for(int var5 = var3.getY(); var5 < var3.getY2(); ++var5) {
               IsoGridSquare var6 = IsoWorld.instance.CurrentCell.getGridSquare(var4, var5, this.getZ());
               if (var6 != null && var6.isFree(false)) {
                  var1.add(var6);
               }
            }
         }
      }

      if (!var1.isEmpty()) {
         return (IsoGridSquare)var1.get(Rand.Next(var1.size()));
      } else {
         return null;
      }
   }

   public boolean isEmptyOutside() {
      return "emptyoutside".equalsIgnoreCase(this.name);
   }

   public static class RoomRect {
      public int x;
      public int y;
      public int w;
      public int h;

      public int getX() {
         return this.x;
      }

      public int getY() {
         return this.y;
      }

      public int getX2() {
         return this.x + this.w;
      }

      public int getY2() {
         return this.y + this.h;
      }

      public int getW() {
         return this.w;
      }

      public int getH() {
         return this.h;
      }

      public RoomRect(int var1, int var2, int var3, int var4) {
         this.x = var1;
         this.y = var2;
         this.w = var3;
         this.h = var4;
      }
   }
}
