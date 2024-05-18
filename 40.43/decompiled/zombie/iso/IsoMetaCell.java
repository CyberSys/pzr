package zombie.iso;

import java.util.ArrayList;
import zombie.Lua.LuaEventManager;

public class IsoMetaCell {
   public IsoMetaChunk[] ChunkMap = new IsoMetaChunk[900];
   private int wx = 0;
   private int wy = 0;
   public LotHeader info = null;
   public final ArrayList vehicleZones = new ArrayList();
   public ArrayList triggers = new ArrayList();

   public IsoMetaCell(int var1, int var2) {
      this.wx = var1;
      this.wy = var2;

      for(int var3 = 0; var3 < 900; ++var3) {
         this.ChunkMap[var3] = new IsoMetaChunk();
      }

   }

   public void addTrigger(BuildingDef var1, int var2, int var3, String var4) {
      this.triggers.add(new IsoMetaGrid.Trigger(var1, var2, var3, var4));
   }

   public void checkTriggers() {
      if (IsoCamera.CamCharacter != null) {
         int var1 = (int)IsoCamera.CamCharacter.getX();
         int var2 = (int)IsoCamera.CamCharacter.getY();

         for(int var3 = 0; var3 < this.triggers.size(); ++var3) {
            IsoMetaGrid.Trigger var4 = (IsoMetaGrid.Trigger)this.triggers.get(var3);
            if (var1 >= var4.def.x - var4.triggerRange && var1 <= var4.def.x2 + var4.triggerRange && var2 >= var4.def.y - var4.triggerRange && var2 <= var4.def.y2 + var4.triggerRange) {
               if (!var4.triggered) {
                  LuaEventManager.triggerEvent("OnTriggerNPCEvent", var4.type, var4.data, var4.def);
               }

               LuaEventManager.triggerEvent("OnMultiTriggerNPCEvent", var4.type, var4.data, var4.def);
               var4.triggered = true;
            }
         }

      }
   }

   public IsoMetaChunk getChunk(int var1, int var2) {
      return var2 < 30 && var1 < 30 && var1 >= 0 && var2 >= 0 ? this.ChunkMap[var2 * 30 + var1] : null;
   }

   public void addZone(IsoMetaGrid.Zone var1, int var2, int var3) {
      int var4 = (var1.x + var1.w) / 10;
      if ((var1.x + var1.w) % 10 == 0) {
         --var4;
      }

      int var5 = (var1.y + var1.h) / 10;
      if ((var1.y + var1.h) % 10 == 0) {
         --var5;
      }

      for(int var6 = var1.y / 10; var6 <= var5; ++var6) {
         for(int var7 = var1.x / 10; var7 <= var4; ++var7) {
            if (var7 >= var2 / 10 && var7 < (var2 + 300) / 10 && var6 >= var3 / 10 && var6 < (var3 + 300) / 10 && this.ChunkMap[var7 - var2 / 10 + (var6 - var3 / 10) * 30] != null) {
               this.ChunkMap[var7 - var2 / 10 + (var6 - var3 / 10) * 30].addZone(var1);
            }
         }
      }

   }

   public void removeZone(IsoMetaGrid.Zone var1) {
      int var2 = (var1.x + var1.w) / 10;
      if ((var1.x + var1.w) % 10 == 0) {
         --var2;
      }

      int var3 = (var1.y + var1.h) / 10;
      if ((var1.y + var1.h) % 10 == 0) {
         --var3;
      }

      int var4 = this.wx * 300;
      int var5 = this.wy * 300;

      for(int var6 = var1.y / 10; var6 <= var3; ++var6) {
         for(int var7 = var1.x / 10; var7 <= var2; ++var7) {
            if (var7 >= var4 / 10 && var7 < (var4 + 300) / 10 && var6 >= var5 / 10 && var6 < (var5 + 300) / 10 && this.ChunkMap[var7 - var4 / 10 + (var6 - var5 / 10) * 30] != null) {
               this.ChunkMap[var7 - var4 / 10 + (var6 - var5 / 10) * 30].removeZone(var1);
            }
         }
      }

   }

   public void addRoom(RoomDef var1, int var2, int var3) {
      int var4 = var1.x2 / 10;
      if (var1.x2 % 10 == 0) {
         --var4;
      }

      int var5 = var1.y2 / 10;
      if (var1.y2 % 10 == 0) {
         --var5;
      }

      for(int var6 = var1.y / 10; var6 <= var5; ++var6) {
         for(int var7 = var1.x / 10; var7 <= var4; ++var7) {
            if (var7 >= var2 / 10 && var7 < (var2 + 300) / 10 && var6 >= var3 / 10 && var6 < (var3 + 300) / 10 && this.ChunkMap[var7 - var2 / 10 + (var6 - var3 / 10) * 30] != null) {
               this.ChunkMap[var7 - var2 / 10 + (var6 - var3 / 10) * 30].addRoom(var1);
            }
         }
      }

   }

   public void getZonesIntersecting(int var1, int var2, int var3, int var4, int var5, ArrayList var6) {
      int var7 = (var1 + var4) / 10;
      if ((var1 + var4) % 10 == 0) {
         --var7;
      }

      int var8 = (var2 + var5) / 10;
      if ((var2 + var5) % 10 == 0) {
         --var8;
      }

      int var9 = this.wx * 300;
      int var10 = this.wy * 300;

      for(int var11 = var2 / 10; var11 <= var8; ++var11) {
         for(int var12 = var1 / 10; var12 <= var7; ++var12) {
            if (var12 >= var9 / 10 && var12 < (var9 + 300) / 10 && var11 >= var10 / 10 && var11 < (var10 + 300) / 10 && this.ChunkMap[var12 - var9 / 10 + (var11 - var10 / 10) * 30] != null) {
               this.ChunkMap[var12 - var9 / 10 + (var11 - var10 / 10) * 30].getZonesIntersecting(var1, var2, var3, var4, var5, var6);
            }
         }
      }

   }

   public void getRoomsIntersecting(int var1, int var2, int var3, int var4, ArrayList var5) {
      int var6 = (var1 + var3) / 10;
      if ((var1 + var3) % 10 == 0) {
         --var6;
      }

      int var7 = (var2 + var4) / 10;
      if ((var2 + var4) % 10 == 0) {
         --var7;
      }

      int var8 = this.wx * 300;
      int var9 = this.wy * 300;

      for(int var10 = var2 / 10; var10 <= var7; ++var10) {
         for(int var11 = var1 / 10; var11 <= var6; ++var11) {
            if (var11 >= var8 / 10 && var11 < (var8 + 300) / 10 && var10 >= var9 / 10 && var10 < (var9 + 300) / 10 && this.ChunkMap[var11 - var8 / 10 + (var10 - var9 / 10) * 30] != null) {
               this.ChunkMap[var11 - var8 / 10 + (var10 - var9 / 10) * 30].getRoomsIntersecting(var1, var2, var3, var4, var5);
            }
         }
      }

   }
}
