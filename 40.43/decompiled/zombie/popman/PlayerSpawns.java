package zombie.popman;

import java.util.ArrayList;
import zombie.core.PerformanceSettings;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.areas.IsoBuilding;

final class PlayerSpawns {
   private ArrayList playerSpawns = new ArrayList();

   public void addSpawn(int var1, int var2, int var3) {
      PlayerSpawns.PlayerSpawn var4 = new PlayerSpawns.PlayerSpawn(var1, var2, var3);
      if (var4.building != null) {
         this.playerSpawns.add(var4);
      }

   }

   public void update() {
      for(int var1 = 0; var1 < this.playerSpawns.size(); ++var1) {
         PlayerSpawns.PlayerSpawn var2 = (PlayerSpawns.PlayerSpawn)this.playerSpawns.get(var1);
         if (--var2.counter <= 0) {
            this.playerSpawns.remove(var1--);
         }
      }

   }

   public boolean allowZombie(IsoGridSquare var1) {
      for(int var2 = 0; var2 < this.playerSpawns.size(); ++var2) {
         PlayerSpawns.PlayerSpawn var3 = (PlayerSpawns.PlayerSpawn)this.playerSpawns.get(var2);
         if (!var3.allowZombie(var1)) {
            return false;
         }
      }

      return true;
   }

   private static class PlayerSpawn {
      public int x;
      public int y;
      public int counter;
      public IsoBuilding building;

      public PlayerSpawn(int var1, int var2, int var3) {
         this.x = var1;
         this.y = var2;
         this.counter = PerformanceSettings.LockFPS * 10;
         IsoGridSquare var4 = IsoWorld.instance.getCell().getGridSquare(var1, var2, var3);
         if (var4 != null) {
            this.building = var4.getBuilding();
         }

      }

      public boolean allowZombie(IsoGridSquare var1) {
         if (this.building == var1.getBuilding()) {
            return false;
         } else if (this.building == null) {
            return true;
         } else {
            return var1.getX() < this.building.def.getX() - 15 || var1.getX() >= this.building.def.getX2() + 15 || var1.getY() < this.building.def.getY() - 15 || var1.getY() >= this.building.def.getY2() + 15;
         }
      }
   }
}
