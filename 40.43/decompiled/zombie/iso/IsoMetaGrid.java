package zombie.iso;

import java.awt.Rectangle;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.MapGroups;
import zombie.SandboxOptions;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaManager;
import zombie.characters.Faction;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.logger.ExceptionLogger;
import zombie.core.network.ByteBufferWriter;
import zombie.core.stash.StashSystem;
import zombie.debug.DebugLog;
import zombie.gameStates.ChooseGameInfo;
import zombie.iso.areas.NonPvpZone;
import zombie.iso.areas.SafeHouse;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.util.SharedStrings;

public final class IsoMetaGrid {
   public int minX = 10000000;
   public int minY = 10000000;
   public int maxX = -10000000;
   public int maxY = -10000000;
   private final ArrayList tempRooms = new ArrayList();
   static Rectangle a = new Rectangle();
   static Rectangle b = new Rectangle();
   public ArrayList Zones = new ArrayList();
   public ArrayList Buildings = new ArrayList();
   private final ArrayList tempZones1 = new ArrayList();
   private final ArrayList tempZones2 = new ArrayList();
   public ArrayList VehiclesZones = new ArrayList();
   public IsoMetaCell[][] Grid;
   private int width;
   private int height;
   private SharedStrings sharedStrings = new SharedStrings();
   private final IsoMetaGrid.MetaGridLoaderThread[] threads = new IsoMetaGrid.MetaGridLoaderThread[8];
   private long createStartTime;
   private static final int NUM_LOADER_THREADS = 8;
   ArrayList HighZombieList = new ArrayList();
   static ArrayList roomChoices = new ArrayList(50);

   public int getMinX() {
      return this.minX;
   }

   public int getMinY() {
      return this.minY;
   }

   public int getMaxX() {
      return this.maxX;
   }

   public int getMaxY() {
      return this.maxY;
   }

   public IsoMetaGrid.Zone getZoneAt(int var1, int var2, int var3) {
      IsoMetaChunk var4 = this.getChunkDataFromTile(var1, var2);
      return var4 != null ? var4.getZoneAt(var1, var2, var3) : null;
   }

   public ArrayList getZonesAt(int var1, int var2, int var3) {
      ArrayList var4 = new ArrayList();
      IsoMetaChunk var5 = this.getChunkDataFromTile(var1, var2);
      return var5 != null ? var5.getZonesAt(var1, var2, var3, var4) : var4;
   }

   public ArrayList getZonesIntersecting(int var1, int var2, int var3, int var4, int var5) {
      ArrayList var6 = new ArrayList();
      return this.getZonesIntersecting(var1, var2, var3, var4, var5, var6);
   }

   public ArrayList getZonesIntersecting(int var1, int var2, int var3, int var4, int var5, ArrayList var6) {
      for(int var7 = var2 / 300; var7 <= (var2 + this.height) / 300; ++var7) {
         for(int var8 = var1 / 300; var8 <= (var1 + this.width) / 300; ++var8) {
            if (var8 >= this.minX && var8 <= this.maxX && var7 >= this.minY && var7 <= this.maxY && this.Grid[var8 - this.minX][var7 - this.minY] != null) {
               this.Grid[var8 - this.minX][var7 - this.minY].getZonesIntersecting(var1, var2, var3, var4, var5, var6);
            }
         }
      }

      return var6;
   }

   public RoomDef getRoomAt(int var1, int var2, int var3) {
      IsoMetaChunk var4 = this.getChunkDataFromTile(var1, var2);
      return var4 != null ? var4.getRoomAt(var1, var2, var3) : null;
   }

   public RoomDef getEmptyOutsideAt(int var1, int var2, int var3) {
      IsoMetaChunk var4 = this.getChunkDataFromTile(var1, var2);
      return var4 != null ? var4.getEmptyOutsideAt(var1, var2, var3) : null;
   }

   public int countRoomsIntersecting(int var1, int var2, int var3, int var4) {
      this.tempRooms.clear();

      for(int var5 = var2 / 300; var5 <= (var2 + this.height) / 300; ++var5) {
         for(int var6 = var1 / 300; var6 <= (var1 + this.width) / 300; ++var6) {
            if (var6 >= this.minX && var6 <= this.maxX && var5 >= this.minY && var5 <= this.maxY) {
               IsoMetaCell var7 = this.Grid[var6 - this.minX][var5 - this.minY];
               if (var7 != null) {
                  var7.getRoomsIntersecting(var1, var2, var3, var4, this.tempRooms);
               }
            }
         }
      }

      return this.tempRooms.size();
   }

   public int countNearbyBuildingsRooms(IsoPlayer var1) {
      int var2 = (int)var1.getX() - 20;
      int var3 = (int)var1.getY() - 20;
      byte var4 = 40;
      byte var5 = 40;
      int var6 = this.countRoomsIntersecting(var2, var3, var4, var5);
      return var6;
   }

   private boolean isInside(IsoMetaGrid.Zone var1, BuildingDef var2) {
      a.x = var1.x;
      a.y = var1.y;
      a.width = var1.w;
      a.height = var1.h;
      b.x = var2.x;
      b.y = var2.y;
      b.width = var2.getW();
      b.height = var2.getH();
      return a.contains(b);
   }

   private boolean isAdjacent(IsoMetaGrid.Zone var1, IsoMetaGrid.Zone var2) {
      if (var1 == var2) {
         return false;
      } else {
         a.x = var1.x;
         a.y = var1.y;
         a.width = var1.w;
         a.height = var1.h;
         b.x = var2.x;
         b.y = var2.y;
         b.width = var2.w;
         b.height = var2.h;
         --a.x;
         --a.y;
         Rectangle var10000 = a;
         var10000.width += 2;
         var10000 = a;
         var10000.height += 2;
         --b.x;
         --b.y;
         var10000 = b;
         var10000.width += 2;
         var10000 = b;
         var10000.height += 2;
         return a.intersects(b);
      }
   }

   public IsoMetaGrid.Zone registerZone(String var1, String var2, int var3, int var4, int var5, int var6, int var7) {
      var1 = this.sharedStrings.get(var1);
      var2 = this.sharedStrings.get(var2);
      IsoMetaGrid.Zone var8 = new IsoMetaGrid.Zone(var1, var2, var3, var4, var5, var6, var7);
      if (var3 >= this.minX * 300 - 100 && var4 >= this.minY * 300 - 100 && var3 + var6 <= (this.maxX + 1) * 300 + 100 && var4 + var7 <= (this.maxY + 1) * 300 + 100 && var5 >= 0 && var5 < 8 && var6 <= 600 && var7 <= 600) {
         this.addZone(var8);
         return var8;
      } else {
         DebugLog.log("ERROR: not adding suspicious zone \"" + var1 + "\" \"" + var2 + "\" " + var3 + "," + var4 + "," + var5 + " " + var6 + "x" + var7);
         return var8;
      }
   }

   public IsoMetaGrid.Zone registerZoneNoOverlap(String var1, String var2, int var3, int var4, int var5, int var6, int var7) {
      if (var3 >= this.minX * 300 - 100 && var4 >= this.minY * 300 - 100 && var3 + var6 <= (this.maxX + 1) * 300 + 100 && var4 + var7 <= (this.maxY + 1) * 300 + 100 && var5 >= 0 && var5 < 8 && var6 <= 600 && var7 <= 600) {
         this.tempZones1.clear();
         ArrayList var8 = this.getZonesIntersecting(var3, var4, var5, var6, var7, this.tempZones1);
         ArrayList var9 = this.tempZones2;

         for(int var10 = 0; var10 < var8.size(); ++var10) {
            IsoMetaGrid.Zone var11 = (IsoMetaGrid.Zone)var8.get(var10);
            if (var11.difference(var3, var4, var5, var6, var7, var9)) {
               if (Core.bDebug) {
                  if (var9.isEmpty()) {
                     DebugLog.log("zone: removed overlapped zone x,y,z=" + var11.x + "," + var11.y + "," + var11.z);
                  } else {
                     DebugLog.log("zone: cut zone into " + var9.size() + " pieces x,y,z=" + var11.x + "," + var11.y + "," + var11.z);
                  }
               }

               this.removeZone(var11);

               for(int var12 = 0; var12 < var9.size(); ++var12) {
                  this.addZone((IsoMetaGrid.Zone)var9.get(var12));
               }
            }
         }

         return this.registerZone(var1, var2, var3, var4, var5, var6, var7);
      } else {
         DebugLog.log("ERROR: not adding suspicious zone \"" + var1 + "\" \"" + var2 + "\" " + var3 + "," + var4 + "," + var5 + " " + var6 + "x" + var7);
         return null;
      }
   }

   private void addZone(IsoMetaGrid.Zone var1) {
      this.Zones.add(var1);

      for(int var2 = var1.y / 300; var2 <= (var1.y + var1.h) / 300; ++var2) {
         for(int var3 = var1.x / 300; var3 <= (var1.x + var1.w) / 300; ++var3) {
            if (var3 >= this.minX && var3 <= this.maxX && var2 >= this.minY && var2 <= this.maxY && this.Grid[var3 - this.minX][var2 - this.minY] != null) {
               this.Grid[var3 - this.minX][var2 - this.minY].addZone(var1, var3 * 300, var2 * 300);
            }
         }
      }

   }

   public void removeZone(IsoMetaGrid.Zone var1) {
      this.Zones.remove(var1);

      for(int var2 = var1.y / 300; var2 <= (var1.y + var1.h) / 300; ++var2) {
         for(int var3 = var1.x / 300; var3 <= (var1.x + var1.w) / 300; ++var3) {
            if (var3 >= this.minX && var3 <= this.maxX && var2 >= this.minY && var2 <= this.maxY && this.Grid[var3 - this.minX][var2 - this.minY] != null) {
               this.Grid[var3 - this.minX][var2 - this.minY].removeZone(var1);
            }
         }
      }

   }

   public void removeZonesForCell(int var1, int var2) {
      IsoMetaCell var3 = this.getCellData(var1, var2);
      if (var3 != null) {
         ArrayList var4 = this.tempZones1;
         var4.clear();

         int var5;
         for(var5 = 0; var5 < 900; ++var5) {
            var3.ChunkMap[var5].getZonesIntersecting(var1 * 300, var2 * 300, 0, 300, 300, var4);
         }

         for(var5 = 0; var5 < var4.size(); ++var5) {
            IsoMetaGrid.Zone var6 = (IsoMetaGrid.Zone)var4.get(var5);
            ArrayList var7 = this.tempZones2;
            if (var6.difference(var1 * 300, var2 * 300, 0, 300, 300, var7)) {
               if (Core.bDebug) {
                  if (var7.isEmpty()) {
                     DebugLog.log("zone: removed overlapped zone x,y,z=" + var6.x + "," + var6.y + "," + var6.z);
                  } else {
                     DebugLog.log("zone: cut zone into " + var7.size() + " pieces x,y,z=" + var6.x + "," + var6.y + "," + var6.z);
                  }
               }

               this.removeZone(var6);

               for(int var8 = 0; var8 < var7.size(); ++var8) {
                  this.addZone((IsoMetaGrid.Zone)var7.get(var8));
               }
            }
         }

         if (!var3.vehicleZones.isEmpty()) {
            var3.vehicleZones.clear();
         }

      }
   }

   public void removeZonesForLotDirectory(String var1) {
      if (!this.Zones.isEmpty()) {
         File var2 = new File(ZomboidFileSystem.instance.getString("media/maps/" + var1 + "/"));
         if (var2.isDirectory()) {
            ChooseGameInfo.Map var3 = ChooseGameInfo.getMapDetails(var1);
            if (var3 != null) {
               String[] var4 = var2.list();
               if (var4 != null) {
                  for(int var5 = 0; var5 < var4.length; ++var5) {
                     String var6 = var4[var5];
                     if (var6.endsWith(".lotheader")) {
                        String[] var7 = var6.split("_");
                        var7[1] = var7[1].replace(".lotheader", "");
                        int var8 = Integer.parseInt(var7[0].trim());
                        int var9 = Integer.parseInt(var7[1].trim());
                        this.removeZonesForCell(var8, var9);
                     }
                  }

               }
            }
         }
      }
   }

   public void processZones() {
      int var1 = 0;

      for(int var2 = this.minX; var2 <= this.maxX; ++var2) {
         for(int var3 = this.minY; var3 <= this.maxY; ++var3) {
            if (this.Grid[var2 - this.minX][var3 - this.minY] != null) {
               for(int var4 = 0; var4 < 30; ++var4) {
                  for(int var5 = 0; var5 < 30; ++var5) {
                     var1 = Math.max(var1, this.Grid[var2 - this.minX][var3 - this.minY].getChunk(var5, var4).numZones());
                  }
               }
            }
         }
      }

      DebugLog.log("Max #ZONES on one chunk is " + var1);
   }

   public IsoMetaGrid.Zone registerVehiclesZone(String var1, String var2, int var3, int var4, int var5, int var6, int var7, KahluaTable var8) {
      if (!var2.equals("Vehicle") && !var2.equals("ParkingStall")) {
         return null;
      } else {
         var1 = this.sharedStrings.get(var1);
         var2 = this.sharedStrings.get(var2);
         IsoMetaGrid.VehicleZone var9 = new IsoMetaGrid.VehicleZone(var1, var2, var3, var4, var5, var6, var7, var8);
         this.VehiclesZones.add(var9);
         int var10 = (int)Math.ceil((double)((float)(var9.x + var9.w) / 300.0F));
         int var11 = (int)Math.ceil((double)((float)(var9.y + var9.h) / 300.0F));

         for(int var12 = var9.y / 300; var12 < var11; ++var12) {
            for(int var13 = var9.x / 300; var13 < var10; ++var13) {
               if (var13 >= this.minX && var13 <= this.maxX && var12 >= this.minY && var12 <= this.maxY && this.Grid[var13 - this.minX][var12 - this.minY] != null) {
                  this.Grid[var13 - this.minX][var12 - this.minY].vehicleZones.add(var9);
               }
            }
         }

         return var9;
      }
   }

   public void checkVehiclesZones() {
      int var4 = 0;

      while(var4 < this.VehiclesZones.size()) {
         boolean var1 = true;

         for(int var5 = 0; var5 < var4; ++var5) {
            IsoMetaGrid.Zone var2 = (IsoMetaGrid.Zone)this.VehiclesZones.get(var4);
            IsoMetaGrid.Zone var3 = (IsoMetaGrid.Zone)this.VehiclesZones.get(var5);
            if (var2.getX() == var3.getX() && var2.getY() == var3.getY() && var2.h == var3.h && var2.w == var3.w) {
               var1 = false;
               DebugLog.log("checkVehiclesZones: ERROR! Zone '" + var2.name + "':'" + var2.type + "' (" + var2.x + ", " + var2.y + ") dublicate with Zone '" + var3.name + "':'" + var3.type + "' (" + var3.x + ", " + var3.y + ")");
               break;
            }
         }

         if (var1) {
            ++var4;
         } else {
            this.VehiclesZones.remove(var4);
         }
      }

   }

   public void save(ByteBuffer var1) {
      this.savePart(var1, 0);
      this.savePart(var1, 1);
   }

   public void savePart(ByteBuffer var1, int var2) {
      int var3;
      if (var2 == 0) {
         var1.put((byte)77);
         var1.put((byte)69);
         var1.put((byte)84);
         var1.put((byte)65);
         var1.putInt(143);
         var1.putInt(this.Grid.length);
         var1.putInt(this.Grid[0].length);

         for(var3 = 0; var3 < this.Grid.length; ++var3) {
            for(int var4 = 0; var4 < this.Grid[0].length; ++var4) {
               IsoMetaCell var5 = this.Grid[var3][var4];
               int var6 = 0;
               if (var5.info != null) {
                  var6 = var5.info.Rooms.values().size();
               }

               var1.putInt(var6);
               Iterator var7;
               if (var5.info != null) {
                  var7 = var5.info.Rooms.entrySet().iterator();

                  while(var7.hasNext()) {
                     Entry var8 = (Entry)var7.next();
                     var1.putInt((Integer)var8.getKey());
                     var1.put((byte)(((RoomDef)var8.getValue()).bExplored ? 1 : 0));
                     var1.put((byte)(((RoomDef)var8.getValue()).bLightsActive ? 1 : 0));
                  }
               }

               if (var5.info != null) {
                  var1.putInt(var5.info.Buildings.size());
               } else {
                  var1.putInt(0);
               }

               if (var5.info != null) {
                  var7 = var5.info.Buildings.iterator();

                  while(var7.hasNext()) {
                     BuildingDef var9 = (BuildingDef)var7.next();
                     var1.put((byte)(var9.bAlarmed ? 1 : 0));
                     var1.putInt(var9.getKeyId());
                     var1.put((byte)(var9.seen ? 1 : 0));
                     var1.put((byte)(var9.isHasBeenVisited() ? 1 : 0));
                     var1.putInt(var9.lootRespawnHour);
                  }
               }
            }
         }

      } else {
         var1.putInt(SafeHouse.getSafehouseList().size());

         for(var3 = 0; var3 < SafeHouse.getSafehouseList().size(); ++var3) {
            ((SafeHouse)SafeHouse.getSafehouseList().get(var3)).save(var1);
         }

         var1.putInt(NonPvpZone.getAllZones().size());

         for(var3 = 0; var3 < NonPvpZone.getAllZones().size(); ++var3) {
            ((NonPvpZone)NonPvpZone.getAllZones().get(var3)).save(var1);
         }

         var1.putInt(Faction.getFactions().size());

         for(var3 = 0; var3 < Faction.getFactions().size(); ++var3) {
            ((Faction)Faction.getFactions().get(var3)).save(var1);
         }

         if (!GameClient.bClient) {
            StashSystem.save(var1);
         }

      }
   }

   public void load(ByteBuffer var1) {
      var1.mark();
      byte var3 = var1.get();
      byte var4 = var1.get();
      byte var5 = var1.get();
      byte var6 = var1.get();
      int var2;
      if (var3 == 77 && var4 == 69 && var5 == 84 && var6 == 65) {
         var2 = var1.getInt();
      } else {
         var2 = 33;
         var1.reset();
      }

      int var7 = var1.getInt();
      int var8 = var1.getInt();
      if (var7 != this.Grid.length || var8 != this.Grid[0].length) {
         DebugLog.log("map_meta.bin world size (" + var7 + "x" + var8 + ") does not match the current map size (" + this.Grid.length + "x" + this.Grid[0].length + ")");
         var7 = Math.min(var7, this.Grid.length);
         var8 = Math.min(var8, this.Grid[0].length);
      }

      int var9 = 0;
      int var10 = 0;

      int var11;
      int var12;
      IsoMetaCell var13;
      int var14;
      int var15;
      for(var11 = 0; var11 < var7; ++var11) {
         for(var12 = 0; var12 < var8; ++var12) {
            var13 = this.Grid[var11][var12];
            var14 = var1.getInt();

            int var16;
            boolean var17;
            for(var15 = 0; var15 < var14; ++var15) {
               var16 = var1.getInt();
               var17 = var1.get() == 1;
               boolean var18 = false;
               if (var2 >= 34) {
                  var18 = var1.get() == 1;
               } else {
                  var18 = Rand.Next(2) == 0;
               }

               if (var13.info != null) {
                  RoomDef var19 = (RoomDef)var13.info.Rooms.get(var16);
                  if (var19 != null) {
                     var19.setExplored(var17);
                     var19.bLightsActive = var18;
                  } else {
                     DebugLog.log("ERROR: invalid room ID #" + var16 + " in cell " + var11 + "," + var12 + " while reading map_meta.bin");
                  }
               }
            }

            var15 = var1.getInt();
            var9 += var15;

            for(var16 = 0; var16 < var15; ++var16) {
               var17 = var1.get() == 1;
               int var26 = var2 >= 57 ? var1.getInt() : -1;
               boolean var27 = var2 >= 74 ? var1.get() == 1 : false;
               boolean var20 = var2 >= 107 ? var1.get() == 1 : false;
               if (var2 >= 111 && var2 < 121) {
                  var1.getInt();
               } else {
                  boolean var10000 = false;
               }

               int var22 = var2 >= 125 ? var1.getInt() : 0;
               if (var13.info != null && var16 < var13.info.Buildings.size()) {
                  BuildingDef var23 = (BuildingDef)var13.info.Buildings.get(var16);
                  if (var17) {
                     ++var10;
                  }

                  var23.bAlarmed = var17;
                  var23.setKeyId(var26);
                  if (var2 >= 74) {
                     var23.seen = var27;
                  }

                  var23.hasBeenVisited = var20;
                  var23.lootRespawnHour = var22;
               }
            }
         }
      }

      if (var2 <= 112) {
         this.Zones = new ArrayList();

         for(var11 = 0; var11 < this.height; ++var11) {
            for(var12 = 0; var12 < this.width; ++var12) {
               var13 = this.Grid[var12][var11];
               if (var13 != null) {
                  for(var14 = 0; var14 < 30; ++var14) {
                     for(var15 = 0; var15 < 30; ++var15) {
                        var13.ChunkMap[var15 + var14 * 30].clearZones();
                     }
                  }
               }
            }
         }

         this.loadZone(var1, var2);
      }

      if (var2 >= 66) {
         SafeHouse.clearSafehouseList();
         var11 = var1.getInt();

         for(var12 = 0; var12 < var11; ++var12) {
            SafeHouse.load(var1, var2);
         }
      }

      if (var2 >= 101) {
         NonPvpZone.nonPvpZoneList = new ArrayList();
         var11 = var1.getInt();

         for(var12 = 0; var12 < var11; ++var12) {
            NonPvpZone var24 = new NonPvpZone();
            var24.load(var1, var2);
            NonPvpZone.getAllZones().add(var24);
         }
      }

      if (var2 >= 102) {
         Faction.factions = new ArrayList();
         var11 = var1.getInt();

         for(var12 = 0; var12 < var11; ++var12) {
            Faction var25 = new Faction();
            var25.load(var1, var2);
            Faction.getFactions().add(var25);
         }
      }

      if (var2 >= 107 && !GameClient.bClient) {
         StashSystem.load(var1, var2);
      }

   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public IsoMetaCell getCellData(int var1, int var2) {
      return var1 - this.minX >= 0 && var2 - this.minY >= 0 && var1 - this.minX < this.width && var2 - this.minY < this.height ? this.Grid[var1 - this.minX][var2 - this.minY] : null;
   }

   public IsoMetaCell getCellDataAbs(int var1, int var2) {
      return this.Grid[var1][var2];
   }

   public IsoMetaCell getCurrentCellData() {
      int var1 = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].WorldX;
      int var2 = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].WorldY;
      float var3 = (float)var1;
      float var4 = (float)var2;
      var3 /= 30.0F;
      var4 /= 30.0F;
      if (var3 < 0.0F) {
         var3 = (float)((int)var3 - 1);
      }

      if (var4 < 0.0F) {
         var4 = (float)((int)var4 - 1);
      }

      var1 = (int)var3;
      var2 = (int)var4;
      return this.getCellData(var1, var2);
   }

   public IsoMetaCell getMetaGridFromTile(int var1, int var2) {
      int var3 = var1 / 300;
      int var4 = var2 / 300;
      return this.getCellData(var3, var4);
   }

   public IsoMetaChunk getCurrentChunkData() {
      int var1 = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].WorldX;
      int var2 = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].WorldY;
      float var3 = (float)var1;
      float var4 = (float)var2;
      var3 /= 30.0F;
      var4 /= 30.0F;
      if (var3 < 0.0F) {
         var3 = (float)((int)var3 - 1);
      }

      if (var4 < 0.0F) {
         var4 = (float)((int)var4 - 1);
      }

      var1 = (int)var3;
      var2 = (int)var4;
      return this.getCellData(var1, var2).getChunk(IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].WorldX - var1 * 30, IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].WorldY - var2 * 30);
   }

   public IsoMetaChunk getChunkData(int var1, int var2) {
      float var5 = (float)var1;
      float var6 = (float)var2;
      var5 /= 30.0F;
      var6 /= 30.0F;
      if (var5 < 0.0F) {
         var5 = (float)((int)var5 - 1);
      }

      if (var6 < 0.0F) {
         var6 = (float)((int)var6 - 1);
      }

      int var3 = (int)var5;
      int var4 = (int)var6;
      IsoMetaCell var7 = this.getCellData(var3, var4);
      return var7 == null ? null : var7.getChunk(var1 - var3 * 30, var2 - var4 * 30);
   }

   public IsoMetaChunk getChunkDataFromTile(int var1, int var2) {
      int var3 = var1 / 10;
      int var4 = var2 / 10;
      var3 -= this.minX * 30;
      var4 -= this.minY * 30;
      int var5 = var3 / 30;
      int var6 = var4 / 30;
      var3 += this.minX * 30;
      var4 += this.minY * 30;
      var5 += this.minX;
      var6 += this.minY;
      IsoMetaCell var7 = this.getCellData(var5, var6);
      return var7 == null ? null : var7.getChunk(var3 - var5 * 30, var4 - var6 * 30);
   }

   public boolean isValidSquare(int var1, int var2) {
      if (var1 < this.minX * 300) {
         return false;
      } else if (var1 >= (this.maxX + 1) * 300) {
         return false;
      } else if (var2 < this.minY * 300) {
         return false;
      } else {
         return var2 < (this.maxY + 1) * 300;
      }
   }

   public boolean isValidChunk(int var1, int var2) {
      var1 *= 10;
      var2 *= 10;
      if (var1 < this.minX * 300) {
         return false;
      } else if (var1 >= (this.maxX + 1) * 300) {
         return false;
      } else if (var2 < this.minY * 300) {
         return false;
      } else if (var2 >= (this.maxY + 1) * 300) {
         return false;
      } else {
         return this.Grid[var1 / 300 - this.minX][var2 / 300 - this.minY].info != null;
      }
   }

   public void Create() {
      this.CreateStep1();
      this.CreateStep2();
   }

   public void CreateStep1() {
      this.minX = 10000000;
      this.minY = 10000000;
      this.maxX = -10000000;
      this.maxY = -10000000;
      IsoLot.InfoHeaders.clear();
      IsoLot.InfoHeaderNames.clear();
      IsoLot.InfoFileNames.clear();
      long var1 = System.currentTimeMillis();
      DebugLog.log("IsoMetaGrid.Create: begin scanning directories");
      ArrayList var4 = this.getLotDirectories();
      DebugLog.log("Looking in these map folders:");
      Iterator var5 = var4.iterator();

      String var6;
      while(var5.hasNext()) {
         var6 = (String)var5.next();
         var6 = ZomboidFileSystem.instance.getString("media/maps/" + var6 + "/");
         DebugLog.log("    " + (new File(var6)).getAbsolutePath());
      }

      DebugLog.log("<End of map-folders list>");
      var5 = var4.iterator();

      while(true) {
         File var3;
         do {
            if (!var5.hasNext()) {
               if (this.maxX >= this.minX && this.maxY >= this.minY) {
                  this.Grid = new IsoMetaCell[this.maxX - this.minX + 1][this.maxY - this.minY + 1];
                  this.width = this.maxX - this.minX + 1;
                  this.height = this.maxY - this.minY + 1;
                  long var14 = System.currentTimeMillis() - var1;
                  DebugLog.log("IsoMetaGrid.Create: finished scanning directories in " + (float)var14 / 1000.0F + " seconds");
                  DebugLog.log("IsoMetaGrid.Create: begin loading");
                  this.createStartTime = System.currentTimeMillis();

                  for(int var15 = 0; var15 < 8; ++var15) {
                     IsoMetaGrid.MetaGridLoaderThread var16 = new IsoMetaGrid.MetaGridLoaderThread(this.minY + var15);
                     var16.setDaemon(true);
                     var16.setName("MetaGridLoaderThread" + var15);
                     var16.start();
                     this.threads[var15] = var16;
                  }

                  return;
               }

               throw new IllegalStateException("Failed to find any .lotheader files");
            }

            var6 = (String)var5.next();
            var3 = new File(ZomboidFileSystem.instance.getString("media/maps/" + var6 + "/"));
         } while(!var3.isDirectory());

         ChooseGameInfo.Map var7 = ChooseGameInfo.getMapDetails(var6);
         String[] var8 = var3.list();

         for(int var9 = 0; var9 < var8.length; ++var9) {
            if (!IsoLot.InfoFileNames.containsKey(var8[var9])) {
               if (var8[var9].endsWith(".lotheader")) {
                  String[] var10 = var8[var9].split("_");
                  var10[1] = var10[1].replace(".lotheader", "");
                  int var11 = Integer.parseInt(var10[0].trim());
                  int var12 = Integer.parseInt(var10[1].trim());
                  if (var11 < this.minX) {
                     this.minX = var11;
                  }

                  if (var12 < this.minY) {
                     this.minY = var12;
                  }

                  if (var11 > this.maxX) {
                     this.maxX = var11;
                  }

                  if (var12 > this.maxY) {
                     this.maxY = var12;
                  }

                  IsoLot.InfoFileNames.put(var8[var9], var3.getAbsolutePath() + File.separator + var8[var9]);
                  LotHeader var13 = new LotHeader();
                  var13.bFixed2x = var7.isFixed2x();
                  IsoLot.InfoHeaders.put(var8[var9], var13);
                  IsoLot.InfoHeaderNames.add(var8[var9]);
               } else if (var8[var9].endsWith(".lotpack")) {
                  IsoLot.InfoFileNames.put(var8[var9], var3.getAbsolutePath() + File.separator + var8[var9]);
               } else if (var8[var9].startsWith("chunkdata_")) {
                  IsoLot.InfoFileNames.put(var8[var9], var3.getAbsolutePath() + File.separator + var8[var9]);
               }
            }
         }
      }
   }

   public void CreateStep2() {
      boolean var1 = true;

      while(true) {
         int var2;
         while(var1) {
            var1 = false;

            for(var2 = 0; var2 < 8; ++var2) {
               if (this.threads[var2].isAlive()) {
                  var1 = true;

                  try {
                     Thread.sleep(100L);
                  } catch (InterruptedException var5) {
                  }
                  break;
               }
            }
         }

         for(var2 = 0; var2 < 8; ++var2) {
            this.threads[var2].postLoad();
            this.threads[var2] = null;
         }

         for(var2 = 0; var2 < this.Buildings.size(); ++var2) {
            BuildingDef var3 = (BuildingDef)this.Buildings.get(var2);
            if (!Core.GameMode.equals("LastStand") && var3.rooms.size() > 2) {
               int var4 = 11;
               if (SandboxOptions.instance.getElecShutModifier() > -1 && GameTime.instance.NightsSurvived < SandboxOptions.instance.getElecShutModifier()) {
                  var4 = 9;
               }

               if (SandboxOptions.instance.Alarm.getValue() == 1) {
                  var4 = -1;
               } else if (SandboxOptions.instance.Alarm.getValue() == 2) {
                  var4 += 5;
               } else if (SandboxOptions.instance.Alarm.getValue() == 3) {
                  var4 += 3;
               } else if (SandboxOptions.instance.Alarm.getValue() == 5) {
                  var4 -= 3;
               } else if (SandboxOptions.instance.Alarm.getValue() == 6) {
                  var4 -= 5;
               }

               if (var4 > -1) {
                  var3.bAlarmed = Rand.Next(var4) == 0;
               }
            }
         }

         long var6 = System.currentTimeMillis() - this.createStartTime;
         DebugLog.log("IsoMetaGrid.Create: finished loading in " + (float)var6 / 1000.0F + " seconds");
         return;
      }
   }

   public void Dispose() {
      this.Grid = (IsoMetaCell[][])null;
   }

   public Vector2 getRandomIndoorCoord() {
      return null;
   }

   public RoomDef getRandomRoomBetweenRange(float var1, float var2, float var3, float var4) {
      RoomDef var5 = null;
      float var6 = 0.0F;
      roomChoices.clear();
      LotHeader var7 = null;

      for(int var8 = 0; var8 < IsoLot.InfoHeaderNames.size(); ++var8) {
         var7 = (LotHeader)IsoLot.InfoHeaders.get(IsoLot.InfoHeaderNames.get(var8));
         if (!var7.RoomList.isEmpty()) {
            for(int var9 = 0; var9 < var7.RoomList.size(); ++var9) {
               var5 = (RoomDef)var7.RoomList.get(var9);
               var6 = IsoUtils.DistanceManhatten(var1, var2, (float)var5.x, (float)var5.y);
               if (var6 > var3 && var6 < var4) {
                  roomChoices.add(var5);
               }
            }
         }
      }

      if (!roomChoices.isEmpty()) {
         return (RoomDef)roomChoices.get(Rand.Next(roomChoices.size()));
      } else {
         return null;
      }
   }

   public RoomDef getRandomRoomNotInRange(float var1, float var2, int var3) {
      RoomDef var4 = null;

      do {
         LotHeader var5 = null;

         do {
            var5 = (LotHeader)IsoLot.InfoHeaders.get(IsoLot.InfoHeaderNames.get(Rand.Next(IsoLot.InfoHeaderNames.size())));
         } while(var5.RoomList.isEmpty());

         var4 = (RoomDef)var5.RoomList.get(Rand.Next(var5.RoomList.size()));
      } while(var4 == null || IsoUtils.DistanceManhatten(var1, var2, (float)var4.x, (float)var4.y) < (float)var3);

      return var4;
   }

   public void save() {
      try {
         if (SliceY.SliceBuffer == null) {
            SliceY.SliceBuffer = ByteBuffer.allocate(10000000);
         }

         SliceY.SliceBuffer.rewind();
         File var1 = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_meta.bin");
         FileOutputStream var2 = new FileOutputStream(var1);
         BufferedOutputStream var3 = new BufferedOutputStream(var2);
         this.save(SliceY.SliceBuffer);
         var3.write(SliceY.SliceBuffer.array(), 0, SliceY.SliceBuffer.position());
         var3.flush();
         var3.close();
         if (SliceY.SliceBuffer == null) {
            SliceY.SliceBuffer = ByteBuffer.allocate(10000000);
         }

         SliceY.SliceBuffer.rewind();
         File var4 = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_zone.bin");
         FileOutputStream var5 = new FileOutputStream(var4);
         BufferedOutputStream var6 = new BufferedOutputStream(var5);
         this.saveZone(SliceY.SliceBuffer);
         var6.write(SliceY.SliceBuffer.array(), 0, SliceY.SliceBuffer.position());
         var6.flush();
         var6.close();
      } catch (Exception var7) {
         var7.printStackTrace();
      }

   }

   public void loadZone(ByteBuffer var1, int var2) {
      if (var2 == -1) {
         byte var3 = var1.get();
         byte var4 = var1.get();
         byte var5 = var1.get();
         byte var6 = var1.get();
         if (var3 != 90 || var4 != 79 || var5 != 78 || var6 != 69) {
            DebugLog.log("ERROR: expected 'ZONE' at start of map_zone.bin");
            return;
         }

         var2 = var1.getInt();
      }

      int var17 = this.Zones.size();
      if (!GameServer.bServer && var2 >= 34 || GameServer.bServer && var2 >= 36) {
         this.Zones.clear();

         int var7;
         int var8;
         int var18;
         int var19;
         for(var18 = 0; var18 < this.height; ++var18) {
            for(var19 = 0; var19 < this.width; ++var19) {
               IsoMetaCell var20 = this.Grid[var19][var18];
               if (var20 != null) {
                  for(var7 = 0; var7 < 30; ++var7) {
                     for(var8 = 0; var8 < 30; ++var8) {
                        var20.ChunkMap[var8 + var7 * 30].clearZones();
                     }
                  }
               }
            }
         }

         int var10;
         int var11;
         int var14;
         String var24;
         if (var2 >= 141) {
            var18 = var1.getInt();
            HashMap var21 = new HashMap();

            int var23;
            for(var23 = 0; var23 < var18; ++var23) {
               var24 = GameWindow.ReadStringUTF(var1);
               var21.put(var23, var24);
            }

            var23 = var1.getInt();
            DebugLog.log("loading " + var23 + " zones from map_zone.bin");

            for(var7 = 0; var7 < var23; ++var7) {
               String var25 = (String)var21.get(Integer.valueOf(var1.getShort()));
               String var26 = (String)var21.get(Integer.valueOf(var1.getShort()));
               var10 = var1.getInt();
               var11 = var1.getInt();
               byte var27 = var1.get();
               int var13 = var1.getInt();
               var14 = var1.getInt();
               int var28 = var1.getInt();
               IsoMetaGrid.Zone var30 = this.registerZone(var25, var26, var10, var11, var27, var13, var14);
               var30.hourLastSeen = var28;
               var30.haveConstruction = var1.get() == 1;
               var30.lastActionTimestamp = var1.getInt();
               var30.setOriginalName((String)var21.get(Integer.valueOf(var1.getShort())));
            }

            return;
         }

         var18 = var1.getInt();
         DebugLog.log("loading " + var18 + " zones from map_zone.bin");
         if (var2 <= 112 && var18 > var17 * 2) {
            DebugLog.log("ERROR: seems like too many zones in map_zone.bin");
            return;
         }

         for(var19 = 0; var19 < var18; ++var19) {
            String var22 = GameWindow.ReadString(var1);
            var24 = GameWindow.ReadString(var1);
            var8 = var1.getInt();
            int var9 = var1.getInt();
            var10 = var1.getInt();
            var11 = var1.getInt();
            int var12 = var1.getInt();
            if (var2 < 121) {
               var1.getInt();
            } else {
               boolean var10000 = false;
            }

            var14 = var2 < 68 ? var1.getShort() : var1.getInt();
            IsoMetaGrid.Zone var15 = this.registerZone(var22, var24, var8, var9, var10, var11, var12);
            var15.hourLastSeen = var14;
            if (var2 >= 35) {
               boolean var16 = var1.get() == 1;
               var15.haveConstruction = var16;
            }

            if (var2 >= 41) {
               var15.lastActionTimestamp = var1.getInt();
            }

            if (var2 >= 98) {
               var15.setOriginalName(GameWindow.ReadString(var1));
            }

            if (var2 >= 110 && var2 < 121) {
               int var29 = var1.getInt();
            }
         }
      }

   }

   public void saveZone(ByteBuffer var1) {
      var1.put((byte)90);
      var1.put((byte)79);
      var1.put((byte)78);
      var1.put((byte)69);
      var1.putInt(143);
      HashSet var2 = new HashSet();

      for(int var3 = 0; var3 < this.Zones.size(); ++var3) {
         IsoMetaGrid.Zone var4 = (IsoMetaGrid.Zone)this.Zones.get(var3);
         var2.add(var4.getName());
         var2.add(var4.getOriginalName());
         var2.add(var4.getType());
      }

      ArrayList var7 = new ArrayList(var2);
      HashMap var8 = new HashMap();

      int var5;
      for(var5 = 0; var5 < var7.size(); ++var5) {
         var8.put(var7.get(var5), var5);
      }

      if (var7.size() > 32767) {
         throw new IllegalStateException("IsoMetaGrid.saveZone() string table is too large");
      } else {
         var1.putInt(var7.size());

         for(var5 = 0; var5 < var7.size(); ++var5) {
            GameWindow.WriteString(var1, (String)var7.get(var5));
         }

         var1.putInt(this.Zones.size());

         for(var5 = 0; var5 < this.Zones.size(); ++var5) {
            IsoMetaGrid.Zone var6 = (IsoMetaGrid.Zone)this.Zones.get(var5);
            var1.putShort(((Integer)var8.get(var6.getName())).shortValue());
            var1.putShort(((Integer)var8.get(var6.getType())).shortValue());
            var1.putInt(var6.x);
            var1.putInt(var6.y);
            var1.put((byte)var6.z);
            var1.putInt(var6.w);
            var1.putInt(var6.h);
            var1.putInt(var6.hourLastSeen);
            var1.put((byte)(var6.haveConstruction ? 1 : 0));
            var1.putInt(var6.lastActionTimestamp);
            var1.putShort(((Integer)var8.get(var6.getOriginalName())).shortValue());
         }

         var2.clear();
         var7.clear();
         var8.clear();
      }
   }

   private void getLotDirectories(String var1, ArrayList var2) {
      if (!var2.contains(var1)) {
         ChooseGameInfo.Map var3 = ChooseGameInfo.getMapDetails(var1);
         if (var3 != null) {
            var2.add(var1);
            Iterator var4 = var3.getLotDirectories().iterator();

            while(var4.hasNext()) {
               String var5 = (String)var4.next();
               this.getLotDirectories(var5, var2);
            }

         }
      }
   }

   public ArrayList getLotDirectories() {
      if (GameClient.bClient) {
         Core.GameMap = GameClient.GameMap;
      }

      if (GameServer.bServer) {
         Core.GameMap = GameServer.GameMap;
      }

      if (Core.GameMap.equals("DEFAULT")) {
         MapGroups var1 = new MapGroups();
         var1.createGroups();
         if (var1.getNumberOfGroups() != 1) {
            throw new RuntimeException("GameMap is DEFAULT but there are multiple worlds to choose from");
         }

         var1.setWorld(0);
      }

      ArrayList var5 = new ArrayList();
      if (Core.GameMap.contains(";")) {
         String[] var2 = Core.GameMap.split(";");

         for(int var3 = 0; var3 < var2.length; ++var3) {
            String var4 = var2[var3].trim();
            if (!var4.isEmpty() && !var5.contains(var4)) {
               var5.add(var4);
            }
         }
      } else {
         this.getLotDirectories(Core.GameMap, var5);
      }

      return var5;
   }

   private final class MetaGridLoaderThread extends Thread {
      int wY;
      final SharedStrings sharedStrings = new SharedStrings();
      final ArrayList Buildings = new ArrayList();
      final ArrayList tempRooms = new ArrayList();

      MetaGridLoaderThread(int var2) {
         this.wY = var2;
      }

      public void run() {
         try {
            this.runInner();
         } catch (Exception var2) {
            ExceptionLogger.logException(var2);
         }

      }

      void runInner() {
         for(int var1 = this.wY; var1 <= IsoMetaGrid.this.maxY; var1 += 8) {
            for(int var2 = IsoMetaGrid.this.minX; var2 <= IsoMetaGrid.this.maxX; ++var2) {
               this.loadCell(var2, var1);
            }
         }

      }

      void loadCell(int var1, int var2) {
         IsoMetaCell var3 = new IsoMetaCell(var1, var2);
         IsoMetaGrid.this.Grid[var1 - IsoMetaGrid.this.minX][var2 - IsoMetaGrid.this.minY] = var3;
         String var4 = var1 + "_" + var2 + ".lotheader";
         if (IsoLot.InfoFileNames.containsKey(var4)) {
            LotHeader var5 = (LotHeader)IsoLot.InfoHeaders.get(var4);
            if (var5 != null) {
               File var6 = new File((String)IsoLot.InfoFileNames.get(var4));
               if (var6.exists()) {
                  var3.info = var5;

                  try {
                     RandomAccessFile var7 = new RandomAccessFile(var6.getAbsolutePath(), "r");
                     Throwable var8 = null;

                     try {
                        var5.version = IsoLot.readInt(var7);
                        int var9 = IsoLot.readInt(var7);

                        int var10;
                        for(var10 = 0; var10 < var9; ++var10) {
                           String var11 = IsoLot.readString(var7);
                           var5.tilesUsed.add(this.sharedStrings.get(var11.trim()));
                        }

                        var7.read();
                        var5.width = IsoLot.readInt(var7);
                        var5.height = IsoLot.readInt(var7);
                        var5.levels = IsoLot.readInt(var7);
                        var10 = IsoLot.readInt(var7);

                        int var14;
                        int var15;
                        int var31;
                        for(var31 = 0; var31 < var10; ++var31) {
                           String var12 = IsoLot.readString(var7);
                           RoomDef var13 = new RoomDef(var31, this.sharedStrings.get(var12));
                           var13.level = IsoLot.readInt(var7);
                           var14 = IsoLot.readInt(var7);

                           for(var15 = 0; var15 < var14; ++var15) {
                              RoomDef.RoomRect var16 = new RoomDef.RoomRect(IsoLot.readInt(var7) + var1 * 300, IsoLot.readInt(var7) + var2 * 300, IsoLot.readInt(var7), IsoLot.readInt(var7));
                              var13.rects.add(var16);
                           }

                           var13.CalculateBounds();
                           var5.Rooms.put(new Integer(var13.ID), var13);
                           var5.RoomList.add(var13);
                           var3.addRoom(var13, var1 * 300, var2 * 300);
                           var15 = IsoLot.readInt(var7);

                           for(int var35 = 0; var35 < var15; ++var35) {
                              int var17 = IsoLot.readInt(var7);
                              int var18 = IsoLot.readInt(var7);
                              int var19 = IsoLot.readInt(var7);
                              var13.objects.add(new MetaObject(var17, var18 + var1 * 300 - var13.x, var19 + var2 * 300 - var13.y, var13));
                           }

                           var13.bLightsActive = Rand.Next(2) == 0;
                        }

                        var31 = IsoLot.readInt(var7);

                        int var32;
                        for(var32 = 0; var32 < var31; ++var32) {
                           BuildingDef var33 = new BuildingDef();
                           var14 = IsoLot.readInt(var7);
                           var33.ID = var32;

                           for(var15 = 0; var15 < var14; ++var15) {
                              RoomDef var37 = (RoomDef)var5.Rooms.get(IsoLot.readInt(var7));
                              var37.building = var33;
                              if (var37.isEmptyOutside()) {
                                 var33.emptyoutside.add(var37);
                              } else {
                                 var33.rooms.add(var37);
                              }
                           }

                           var33.CalculateBounds(this.tempRooms);
                           var5.Buildings.add(var33);
                           this.Buildings.add(var33);
                        }

                        for(var32 = 0; var32 < 30; ++var32) {
                           for(int var34 = 0; var34 < 30; ++var34) {
                              var14 = var7.read();
                              IsoMetaChunk var36 = var3.getChunk(var32, var34);
                              var36.setZombieIntensity(var14);
                           }
                        }
                     } catch (Throwable var28) {
                        var8 = var28;
                        throw var28;
                     } finally {
                        if (var7 != null) {
                           if (var8 != null) {
                              try {
                                 var7.close();
                              } catch (Throwable var27) {
                                 var8.addSuppressed(var27);
                              }
                           } else {
                              var7.close();
                           }
                        }

                     }
                  } catch (Exception var30) {
                     DebugLog.log("ERROR loading " + var6.getAbsolutePath());
                     ExceptionLogger.logException(var30);
                  }

               }
            }
         }
      }

      void postLoad() {
         IsoMetaGrid.this.Buildings.addAll(this.Buildings);
         this.Buildings.clear();
         this.sharedStrings.clear();
         this.tempRooms.clear();
      }
   }

   public static class Trigger {
      public BuildingDef def;
      public int triggerRange;
      public int zombieExclusionRange;
      public String type;
      public boolean triggered = false;
      public KahluaTable data;

      public KahluaTable getModData() {
         return this.data;
      }

      public Trigger(BuildingDef var1, int var2, int var3, String var4) {
         this.def = var1;
         this.triggerRange = var2;
         this.zombieExclusionRange = var3;
         this.type = var4;
         this.data = LuaManager.platform.newTable();
      }
   }

   public static final class VehicleZone extends IsoMetaGrid.Zone {
      public IsoDirections dir;
      public short flags;
      public static final short VZF_FaceDirection = 1;

      public VehicleZone(String var1, String var2, int var3, int var4, int var5, int var6, int var7, KahluaTable var8) {
         super(var1, var2, var3, var4, var5, var6, var7);
         this.dir = IsoDirections.Max;
         this.flags = 0;
         if (var8 != null) {
            Object var9 = var8.rawget("Direction");
            if (var9 instanceof String) {
               this.dir = IsoDirections.valueOf((String)var9);
            }

            var9 = var8.rawget("FaceDirection");
            if (var9 == Boolean.TRUE) {
               this.flags = (short)(this.flags | 1);
            }
         }

      }

      public boolean isFaceDirection() {
         return (this.flags & 1) != 0;
      }
   }

   public static class Zone {
      public int hourLastSeen = 0;
      public int lastActionTimestamp = 0;
      public boolean haveConstruction = false;
      public String name;
      public String type;
      public int x;
      public int y;
      public int z;
      public int w;
      public int h;
      private String originalName;

      public float getHoursSinceLastSeen() {
         return (float)GameTime.instance.getWorldAgeHours() - (float)this.hourLastSeen;
      }

      public void setHourSeenToCurrent() {
         this.hourLastSeen = (int)GameTime.instance.getWorldAgeHours();
      }

      public Zone(String var1, String var2, int var3, int var4, int var5, int var6, int var7) {
         this.h = var7;
         this.originalName = var1;
         this.name = var1;
         this.type = var2;
         this.x = var3;
         this.y = var4;
         this.z = var5;
         this.w = var6;
      }

      public void setHaveConstruction(boolean var1) {
         this.haveConstruction = var1;
         if (GameClient.bClient) {
            ByteBufferWriter var2 = GameClient.connection.startPacket();
            PacketTypes.doPacket((short)92, var2);
            var2.putInt(this.x);
            var2.putInt(this.y);
            var2.putInt(this.z);
            GameClient.connection.endPacketImmediate();
         }

      }

      public boolean haveCons() {
         return this.haveConstruction;
      }

      public int getZombieDensity() {
         IsoMetaChunk var1 = IsoWorld.instance.MetaGrid.getChunkDataFromTile(this.x, this.y);
         return var1 != null ? var1.getUnadjustedZombieIntensity() : 0;
      }

      public boolean intersects(int var1, int var2, int var3, int var4, int var5) {
         if (this.z != var3) {
            return false;
         } else if (var1 + var4 > this.x && var1 < this.x + this.w) {
            return var2 + var5 > this.y && var2 < this.y + this.h;
         } else {
            return false;
         }
      }

      public boolean difference(int var1, int var2, int var3, int var4, int var5, ArrayList var6) {
         var6.clear();
         if (this.intersects(var1, var2, var3, var4, var5)) {
            int var7;
            int var8;
            if (this.x < var1) {
               var7 = Math.max(var2, this.y);
               var8 = Math.min(var2 + var5, this.y + this.h);
               var6.add(new IsoMetaGrid.Zone(this.name, this.type, this.x, var7, var3, var1 - this.x, var8 - var7));
            }

            if (var1 + var4 < this.x + this.w) {
               var7 = Math.max(var2, this.y);
               var8 = Math.min(var2 + var5, this.y + this.h);
               var6.add(new IsoMetaGrid.Zone(this.name, this.type, var1 + var4, var7, var3, this.x + this.w - (var1 + var4), var8 - var7));
            }

            if (this.y < var2) {
               var6.add(new IsoMetaGrid.Zone(this.name, this.type, this.x, this.y, var3, this.w, var2 - this.y));
            }

            if (var2 + var5 < this.y + this.h) {
               var6.add(new IsoMetaGrid.Zone(this.name, this.type, this.x, var2 + var5, var3, this.w, this.y + this.h - (var2 + var5)));
            }

            return true;
         } else {
            return false;
         }
      }

      public IsoGridSquare getRandomSquareInZone() {
         return IsoWorld.instance.getCell().getGridSquare(Rand.Next(this.x, this.x + this.w), Rand.Next(this.y, this.y + this.h), this.z);
      }

      public IsoGridSquare getRandomUnseenSquareInZone() {
         return null;
      }

      public void addSquare(IsoGridSquare var1) {
      }

      public ArrayList getSquares() {
         return null;
      }

      public void removeSquare(IsoGridSquare var1) {
      }

      public String getName() {
         return this.name;
      }

      public String getType() {
         return this.type;
      }

      public void setType(String var1) {
         this.type = var1;
      }

      public void setName(String var1) {
         this.name = var1;
      }

      public int getLastActionTimestamp() {
         return this.lastActionTimestamp;
      }

      public void setLastActionTimestamp(int var1) {
         this.lastActionTimestamp = var1;
      }

      public int getX() {
         return this.x;
      }

      public int getY() {
         return this.y;
      }

      public int getHeight() {
         return this.h;
      }

      public int getWidth() {
         return this.w;
      }

      public void sendToServer() {
         if (GameClient.bClient) {
            GameClient.registerZone(this, true);
         }

      }

      public String getOriginalName() {
         return this.originalName;
      }

      public void setOriginalName(String var1) {
         this.originalName = var1;
      }
   }
}
