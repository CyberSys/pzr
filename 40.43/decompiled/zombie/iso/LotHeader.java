package zombie.iso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.IsoRoom;

public class LotHeader {
   protected ArrayList tilesUsed = new ArrayList();
   protected ArrayList zones = new ArrayList();
   public int width = 0;
   public int height = 0;
   public int levels = 0;
   public int version = 0;
   public HashMap Rooms = new HashMap();
   public ArrayList RoomList = new ArrayList();
   public ArrayList Buildings = new ArrayList();
   public HashMap isoRooms = new HashMap();
   public HashMap isoBuildings = new HashMap();
   public boolean bFixed2x;

   public int getHeight() {
      return this.height;
   }

   public int getWidth() {
      return this.width;
   }

   public int getLevels() {
      return this.levels;
   }

   public IsoRoom getRoom(int var1) {
      boolean var2;
      if (var1 != 0) {
         var2 = false;
      }

      if (!this.Rooms.containsKey(var1)) {
         var2 = false;
      }

      RoomDef var5 = (RoomDef)this.Rooms.get(var1);
      IsoRoom var3;
      if (!this.isoRooms.containsKey(var1)) {
         var3 = new IsoRoom();
         var3.rects.addAll(var5.rects);
         var3.RoomDef = var5.name;
         var3.def = var5;
         var3.layer = var5.level;
         IsoWorld.instance.CurrentCell.getRoomList().add(var3);
         if (var5.building == null) {
            var5.building = new BuildingDef();
            var5.building.ID = this.Buildings.size();
            var5.building.rooms.add(var5);
            var5.building.CalculateBounds(new ArrayList());
            this.Buildings.add(var5.building);
         }

         int var4 = var5.building.ID;
         this.isoRooms.put(var1, var3);
         if (!this.isoBuildings.containsKey(var4)) {
            var3.building = new IsoBuilding();
            var3.building.def = var5.building;
            this.isoBuildings.put(var4, var3.building);
            var3.building.CreateFrom(var5.building, this);
         } else {
            var3.building = (IsoBuilding)this.isoBuildings.get(var4);
         }

         return var3;
      } else {
         var3 = (IsoRoom)this.isoRooms.get(var1);
         return var3;
      }
   }

   /** @deprecated */
   @Deprecated
   public int getRoomAt(int var1, int var2, int var3) {
      Iterator var4 = this.Rooms.entrySet().iterator();

      while(var4.hasNext()) {
         Entry var5 = (Entry)var4.next();
         RoomDef var6 = (RoomDef)var5.getValue();

         for(int var7 = 0; var7 < var6.rects.size(); ++var7) {
            RoomDef.RoomRect var8 = (RoomDef.RoomRect)var6.rects.get(var7);
            if (var8.x <= var1 && var8.y <= var2 && var6.level == var3 && var8.getX2() > var1 && var8.getY2() > var2) {
               return (Integer)var5.getKey();
            }
         }
      }

      return -1;
   }
}
