package zombie.iso.areas;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import zombie.VirtualZombieManager;
import zombie.core.Rand;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoObject;
import zombie.iso.IsoRoomLight;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.MetaObject;
import zombie.iso.RoomDef;
import zombie.iso.objects.IsoLightSwitch;
import zombie.network.GameServer;

public class IsoRoom {
   public Vector Beds = new Vector();
   public Rectangle bounds;
   public IsoBuilding building = null;
   public ArrayList Containers = new ArrayList();
   public ArrayList Windows = new ArrayList();
   public Vector Exits = new Vector();
   public int layer;
   public String RoomDef = "none";
   public Vector TileList = new Vector();
   public int transparentWalls = 0;
   public boolean lit = false;
   public ArrayList lightSwitches = new ArrayList();
   public ArrayList lights = new ArrayList();
   public ArrayList roomLights = new ArrayList();
   public ArrayList WaterSources = new ArrayList();
   public int seen = 1000000000;
   public int visited = 1000000000;
   public RoomDef def;
   public ArrayList rects = new ArrayList(1);
   public ArrayList Squares = new ArrayList();
   private Vector tempRects = new Vector();

   public IsoBuilding getBuilding() {
      return this.building;
   }

   public String getName() {
      return this.RoomDef;
   }

   public IsoBuilding CreateBuilding(IsoCell var1) {
      IsoBuilding var2 = new IsoBuilding(var1);
      this.AddToBuilding(var2);
      return var2;
   }

   public boolean isInside(int var1, int var2, int var3) {
      for(int var4 = 0; var4 < this.rects.size(); ++var4) {
         int var5 = ((RoomDef.RoomRect)this.rects.get(var4)).x;
         int var6 = ((RoomDef.RoomRect)this.rects.get(var4)).y;
         int var7 = ((RoomDef.RoomRect)this.rects.get(var4)).getX2();
         int var8 = ((RoomDef.RoomRect)this.rects.get(var4)).getY2();
         if (var1 >= var5 && var2 >= var6 && var1 < var7 && var2 < var8 && var3 == this.layer) {
            return true;
         }
      }

      return false;
   }

   public IsoGridSquare getFreeTile() {
      boolean var1 = false;
      IsoGridSquare var2 = null;
      int var3 = 100;

      while(!var1 && var3 > 0) {
         --var3;
         var1 = true;
         if (this.TileList.isEmpty()) {
            return null;
         }

         var2 = (IsoGridSquare)this.TileList.get(Rand.Next(this.TileList.size()));

         for(int var4 = 0; var4 < this.Exits.size(); ++var4) {
            if (var2.getX() == ((IsoRoomExit)this.Exits.get(var4)).x && var2.getY() == ((IsoRoomExit)this.Exits.get(var4)).y) {
               var1 = false;
            }
         }

         if (var1 && !var2.isFree(true)) {
            var1 = false;
         }
      }

      return var3 < 0 ? null : var2;
   }

   void AddToBuilding(IsoBuilding var1) {
      this.building = var1;
      var1.AddRoom(this);
      Iterator var2 = this.Exits.iterator();

      while(var2.hasNext()) {
         IsoRoomExit var3 = (IsoRoomExit)var2.next();
         if (var3.To.From != null && var3.To.From.building == null) {
            var3.To.From.AddToBuilding(var1);
         }
      }

   }

   public ArrayList getWaterSources() {
      return this.WaterSources;
   }

   public void setWaterSources(ArrayList var1) {
      this.WaterSources = var1;
   }

   public boolean hasWater() {
      if (this.WaterSources.isEmpty()) {
         return false;
      } else {
         Iterator var1 = this.WaterSources.iterator();

         while(var1 != null && var1.hasNext()) {
            IsoObject var2 = (IsoObject)var1.next();
            if (var2.hasWater()) {
               return true;
            }
         }

         return false;
      }
   }

   public void useWater() {
      if (!this.WaterSources.isEmpty()) {
         Iterator var1 = this.WaterSources.iterator();

         while(var1 != null && var1.hasNext()) {
            IsoObject var2 = (IsoObject)var1.next();
            if (var2.hasWater()) {
               var2.useWater(1);
               break;
            }
         }

      }
   }

   public ArrayList getWindows() {
      return this.Windows;
   }

   public void addSquare(IsoGridSquare var1) {
      if (!this.Squares.contains(var1)) {
         this.Squares.add(var1);
      }
   }

   private void addExitTo(IsoGridSquare var1, IsoGridSquare var2) {
      IsoRoom var3 = null;
      IsoRoom var4 = null;
      if (var1 != null) {
         var3 = var1.getRoom();
      }

      if (var2 != null) {
         var4 = var2.getRoom();
      }

      if (var3 != null || var4 != null) {
         IsoRoom var5 = var3;
         if (var3 == null) {
            var5 = var4;
         }

         IsoRoomExit var6 = new IsoRoomExit(var5, var1.getX(), var1.getY(), var1.getZ());
         var6.type = IsoRoomExit.ExitType.Door;
         if (var5 == var3) {
            if (var4 != null) {
               IsoRoomExit var7 = var4.getExitAt(var2.getX(), var2.getY(), var2.getZ());
               if (var7 == null) {
                  var7 = new IsoRoomExit(var4, var2.getX(), var2.getY(), var2.getZ());
                  var4.Exits.add(var7);
               }

               var6.To = var7;
            } else {
               var3.building.Exits.add(var6);
               if (var2 != null) {
                  var6.To = new IsoRoomExit(var6, var2.getX(), var2.getY(), var2.getZ());
               }
            }

            var3.Exits.add(var6);
         } else {
            var4.building.Exits.add(var6);
            if (var2 != null) {
               var6.To = new IsoRoomExit(var6, var2.getX(), var2.getY(), var2.getZ());
            }

            var4.Exits.add(var6);
         }

      }
   }

   private IsoRoomExit getExitAt(int var1, int var2, int var3) {
      for(int var4 = 0; var4 < this.Exits.size(); ++var4) {
         IsoRoomExit var5 = (IsoRoomExit)this.Exits.get(var4);
         if (var5.x == var1 && var5.y == var2 && var5.layer == var3) {
            return var5;
         }
      }

      return null;
   }

   public void removeSquare(IsoGridSquare var1) {
      this.Squares.remove(var1);
      IsoRoomExit var2 = this.getExitAt(var1.getX(), var1.getY(), var1.getZ());
      if (var2 != null) {
         this.Exits.remove(var2);
         if (var2.To != null) {
            var2.From = null;
         }

         if (this.building.Exits.contains(var2)) {
            this.building.Exits.remove(var2);
         }
      }

      for(int var3 = 0; var3 < var1.getObjects().size(); ++var3) {
         IsoObject var4 = (IsoObject)var1.getObjects().get(var3);
         if (var4 instanceof IsoLightSwitch) {
            this.lightSwitches.remove(var4);
         }
      }

   }

   public void spawnZombies() {
      VirtualZombieManager.instance.addZombiesToMap(1, this.def, false);
   }

   public void onSee() {
      IsoWorld.instance.getCell().roomSpotted(this);
   }

   public Vector getTileList() {
      return this.TileList;
   }

   public ArrayList getSquares() {
      return this.Squares;
   }

   public ArrayList getContainer() {
      return this.Containers;
   }

   public IsoGridSquare getRandomSquare() {
      return this.Squares.isEmpty() ? null : (IsoGridSquare)this.Squares.get(Rand.Next(this.Squares.size()));
   }

   public IsoGridSquare getRandomFreeSquare() {
      int var1 = 100;
      IsoGridSquare var2 = null;
      if (GameServer.bServer) {
         while(var1 > 0) {
            var2 = IsoWorld.instance.CurrentCell.getGridSquare(this.def.getX() + Rand.Next(this.def.getW()), this.def.getY() + Rand.Next(this.def.getH()), this.def.level);
            if (var2 != null && var2.getRoom() == this && var2.isFree(true)) {
               return var2;
            }

            --var1;
         }

         return null;
      } else if (this.Squares.isEmpty()) {
         return null;
      } else {
         while(var1 > 0) {
            var2 = (IsoGridSquare)this.Squares.get(Rand.Next(this.Squares.size()));
            if (var2.isFree(true)) {
               return var2;
            }

            --var1;
         }

         return null;
      }
   }

   public boolean hasLightSwitches() {
      if (!this.lightSwitches.isEmpty()) {
         return true;
      } else {
         for(int var1 = 0; var1 < this.def.objects.size(); ++var1) {
            if (((MetaObject)this.def.objects.get(var1)).getType() == 7) {
               return true;
            }
         }

         return false;
      }
   }

   private void subdivide(int var1, int var2, int var3, int var4) {
      if (var3 <= 10 && var4 <= 10) {
         this.tempRects.add(new RoomDef.RoomRect(var1, var2, var3, var4));
      } else {
         if (var3 >= var4) {
            this.subdivide(var1, var2, var3 / 2, var4);
            this.subdivide(var1 + var3 / 2, var2, var3 - var3 / 2, var4);
         } else {
            this.subdivide(var1, var2, var3, var4 / 2);
            this.subdivide(var1, var2 + var4 / 2, var3, var4 - var4 / 2);
         }

      }
   }

   public void createLights1(boolean var1) {
      this.lights.clear();

      for(int var2 = 0; var2 < this.def.rects.size(); ++var2) {
         RoomDef.RoomRect var3 = (RoomDef.RoomRect)this.def.rects.get(var2);
         int var4 = var3.w + var3.h;
         IsoLightSource var5 = new IsoLightSource(var3.x + var3.w / 2, var3.y + var3.h / 2, this.def.level, 0.9F, 0.8F, 0.7F, (int)((float)var4 * 0.6F));
         var5.bActive = var1;
         var5.bWasActive = var5.bActive;
         var5.bHydroPowered = true;
         this.lights.add(var5);
      }

   }

   public void createLights2(boolean var1) {
      this.lights.clear();

      for(int var2 = 0; var2 < this.def.rects.size(); ++var2) {
         RoomDef.RoomRect var3 = (RoomDef.RoomRect)this.def.rects.get(var2);
         this.tempRects.clear();
         this.subdivide(var3.x, var3.y, var3.w, var3.h);
         Iterator var4 = this.tempRects.iterator();

         while(var4.hasNext()) {
            RoomDef.RoomRect var5 = (RoomDef.RoomRect)var4.next();
            int var10000 = var5.w + var5.h;
            byte var6 = 15;
            IsoLightSource var7 = new IsoLightSource(var5.x + var5.w / 2, var5.y + var5.h / 2, this.def.level, 0.9F, 0.8F, 0.7F, (int)((float)var6 * 0.6F));
            var7.bActive = true;
            var7.bWasActive = var7.bActive;
            var7.bHydroPowered = true;
            this.lights.add(var7);
         }
      }

   }

   public void createLights3(boolean var1) {
      this.lights.clear();
      int var2 = 10000000;
      int var3 = 1000000;
      int var4 = -1;
      int var5 = -1;

      int var6;
      for(var6 = 0; var6 < this.def.rects.size(); ++var6) {
         RoomDef.RoomRect var7 = (RoomDef.RoomRect)this.def.rects.get(var6);
         var2 = Math.min(var7.x, var2);
         var3 = Math.min(var7.y, var3);
         var4 = Math.max(var7.x + var7.w, var4);
         var5 = Math.max(var7.y + var7.h, var5);
      }

      for(var6 = (int)Math.floor((double)((float)var2 / 5.0F)); (double)var6 <= Math.ceil((double)((float)var4 / 5.0F)); ++var6) {
         for(int var23 = (int)Math.floor((double)((float)var3 / 5.0F)); (double)var23 <= Math.ceil((double)((float)var5 / 5.0F)); ++var23) {
            int var8 = var6 * 5;
            int var9 = var23 * 5;
            byte var10 = 5;
            byte var11 = 5;

            for(int var12 = 0; var12 < this.def.rects.size(); ++var12) {
               RoomDef.RoomRect var13 = (RoomDef.RoomRect)this.def.rects.get(var12);
               if (var13.x + var13.w > var8 && var13.x < var8 + var10 && var13.y + var13.h > var9 && var13.y < var9 + var11) {
                  int var14 = Math.max(var13.x, var8);
                  int var15 = Math.min(var13.x + var13.w, var8 + var10);
                  int var16 = Math.max(var13.y, var9);
                  int var17 = Math.min(var13.y + var13.h, var9 + var11);
                  int var18 = var14 + (var15 - var14) / 2;
                  int var19 = var16 + (var17 - var16) / 2;
                  boolean var20 = false;
                  Iterator var21 = this.lights.iterator();

                  IsoLightSource var22;
                  while(var21.hasNext()) {
                     var22 = (IsoLightSource)var21.next();
                     if (IsoUtils.DistanceTo2D((float)var18, (float)var19, (float)var22.x, (float)var22.y) < 5.0F) {
                        var20 = true;
                        break;
                     }
                  }

                  if (!var20) {
                     byte var24 = 10;
                     var22 = new IsoLightSource(var18, var19, this.def.level, 0.9F, 0.8F, 0.7F, (int)((float)var24 * 0.6F));
                     var22.bActive = true;
                     var22.bWasActive = var22.bActive;
                     var22.bHydroPowered = true;
                     this.lights.add(var22);
                  }
               }
            }
         }
      }

   }

   public void createLights(boolean var1) {
      if (this.roomLights.isEmpty()) {
         for(int var2 = 0; var2 < this.def.rects.size(); ++var2) {
            RoomDef.RoomRect var3 = (RoomDef.RoomRect)this.def.rects.get(var2);
            IsoRoomLight var4 = new IsoRoomLight(this, var3.x, var3.y, this.def.level, var3.w, var3.h);
            this.roomLights.add(var4);
         }

      }
   }
}
