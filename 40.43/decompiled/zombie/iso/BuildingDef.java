package zombie.iso;

import gnu.trove.list.array.TShortArrayList;
import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaManager;
import zombie.core.Rand;
import zombie.core.stash.StashSystem;
import zombie.network.GameServer;
import zombie.network.ServerMap;

public class BuildingDef {
   public KahluaTable table = null;
   private int keySpawned = 0;
   public boolean seen = false;
   public boolean hasBeenVisited = false;
   public String stash = null;
   public int lootRespawnHour = -1;
   public TShortArrayList overlappedChunks;
   public boolean bAlarmed = false;
   private int keyId = -1;
   int ID = 0;
   public int x = 10000000;
   public int y = 10000000;
   public int x2 = -10000000;
   public int y2 = -10000000;
   public ArrayList rooms = new ArrayList();
   public final ArrayList emptyoutside = new ArrayList();
   static ArrayList squareChoices = new ArrayList();
   public IsoMetaGrid.Zone zone;

   public BuildingDef() {
      this.table = LuaManager.platform.newTable();
      this.setKeyId(Rand.Next(100000000));
   }

   public KahluaTable getTable() {
      return this.table;
   }

   public ArrayList getRooms() {
      return this.rooms;
   }

   public void setAllExplored(boolean var1) {
      for(int var2 = 0; var2 < this.rooms.size(); ++var2) {
         RoomDef var3 = (RoomDef)this.rooms.get(var2);
         var3.setExplored(var1);
      }

   }

   public boolean isAllExplored() {
      for(int var1 = 0; var1 < this.rooms.size(); ++var1) {
         if (!((RoomDef)this.rooms.get(var1)).bExplored) {
            return false;
         }
      }

      return true;
   }

   public RoomDef getFirstRoom() {
      return (RoomDef)this.rooms.get(0);
   }

   public int getChunkX() {
      return this.x / 10;
   }

   public int getChunkY() {
      return this.y / 10;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
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

   public int getID() {
      return this.ID;
   }

   public void CalculateBounds(ArrayList var1) {
      int var2;
      RoomDef var3;
      int var4;
      RoomDef.RoomRect var5;
      for(var2 = 0; var2 < this.rooms.size(); ++var2) {
         var3 = (RoomDef)this.rooms.get(var2);

         for(var4 = 0; var4 < var3.rects.size(); ++var4) {
            var5 = (RoomDef.RoomRect)var3.rects.get(var4);
            if (var5.x < this.x) {
               this.x = var5.x;
            }

            if (var5.y < this.y) {
               this.y = var5.y;
            }

            if (var5.x + var5.w > this.x2) {
               this.x2 = var5.x + var5.w;
            }

            if (var5.y + var5.h > this.y2) {
               this.y2 = var5.y + var5.h;
            }
         }
      }

      for(var2 = 0; var2 < this.emptyoutside.size(); ++var2) {
         var3 = (RoomDef)this.emptyoutside.get(var2);

         for(var4 = 0; var4 < var3.rects.size(); ++var4) {
            var5 = (RoomDef.RoomRect)var3.rects.get(var4);
            if (var5.x < this.x) {
               this.x = var5.x;
            }

            if (var5.y < this.y) {
               this.y = var5.y;
            }

            if (var5.x + var5.w > this.x2) {
               this.x2 = var5.x + var5.w;
            }

            if (var5.y + var5.h > this.y2) {
               this.y2 = var5.y + var5.h;
            }
         }
      }

      int var13 = this.x / 10;
      var4 = this.y / 10;
      int var14 = (this.x2 + 0) / 10;
      int var6 = (this.y2 + 0) / 10;
      this.overlappedChunks = new TShortArrayList((var14 - var13 + 1) * (var6 - var4 + 1) * 2);
      this.overlappedChunks.clear();
      var1.clear();
      var1.addAll(this.rooms);
      var1.addAll(this.emptyoutside);

      for(int var7 = 0; var7 < var1.size(); ++var7) {
         RoomDef var8 = (RoomDef)var1.get(var7);

         for(int var9 = 0; var9 < var8.rects.size(); ++var9) {
            RoomDef.RoomRect var10 = (RoomDef.RoomRect)var8.rects.get(var9);
            var13 = var10.x / 10;
            var4 = var10.y / 10;
            var14 = (var10.x + var10.w + 0) / 10;
            var6 = (var10.y + var10.h + 0) / 10;

            for(int var11 = var4; var11 <= var6; ++var11) {
               for(int var12 = var13; var12 <= var14; ++var12) {
                  if (!this.overlapsChunk(var12, var11)) {
                     this.overlappedChunks.add((short)var12);
                     this.overlappedChunks.add((short)var11);
                  }
               }
            }
         }
      }

   }

   public boolean overlapsChunk(int var1, int var2) {
      for(int var3 = 0; var3 < this.overlappedChunks.size(); var3 += 2) {
         if (var1 == this.overlappedChunks.get(var3) && var2 == this.overlappedChunks.get(var3 + 1)) {
            return true;
         }
      }

      return false;
   }

   public IsoGridSquare getFreeSquareInRoom() {
      squareChoices.clear();

      for(int var1 = 0; var1 < this.rooms.size(); ++var1) {
         RoomDef var2 = (RoomDef)this.rooms.get(var1);

         for(int var3 = 0; var3 < var2.rects.size(); ++var3) {
            RoomDef.RoomRect var4 = (RoomDef.RoomRect)var2.rects.get(var3);

            for(int var5 = var4.getX(); var5 < var4.getX2(); ++var5) {
               for(int var6 = var4.getY(); var6 < var4.getY2(); ++var6) {
                  IsoGridSquare var7 = IsoWorld.instance.CurrentCell.getGridSquare(var5, var6, var2.getZ());
                  if (var7 != null && var7.isFree(false)) {
                     squareChoices.add(var7);
                  }
               }
            }
         }
      }

      if (!squareChoices.isEmpty()) {
         return (IsoGridSquare)squareChoices.get(Rand.Next(squareChoices.size()));
      } else {
         return null;
      }
   }

   public boolean isFullyStreamedIn() {
      for(int var1 = 0; var1 < this.overlappedChunks.size(); var1 += 2) {
         short var2 = this.overlappedChunks.get(var1);
         short var3 = this.overlappedChunks.get(var1 + 1);
         IsoChunk var4 = GameServer.bServer ? ServerMap.instance.getChunk(var2, var3) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(var2 * 10, var3 * 10, 0);
         if (var4 == null) {
            return false;
         }
      }

      return true;
   }

   public IsoMetaGrid.Zone getZone() {
      return this.zone;
   }

   public int getKeyId() {
      return this.keyId;
   }

   public void setKeyId(int var1) {
      this.keyId = var1;
   }

   public int getKeySpawned() {
      return this.keySpawned;
   }

   public void setKeySpawned(int var1) {
      this.keySpawned = var1;
   }

   public boolean isHasBeenVisited() {
      return this.hasBeenVisited;
   }

   public void setHasBeenVisited(boolean var1) {
      if (var1 && !this.hasBeenVisited) {
         StashSystem.visitedBuilding(this);
      }

      this.hasBeenVisited = var1;
   }
}
