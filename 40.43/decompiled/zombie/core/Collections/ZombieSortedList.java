package zombie.core.Collections;

import java.util.ArrayList;
import java.util.Collections;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.iso.IsoWorld;
import zombie.network.ServerOptions;

public class ZombieSortedList {
   private IsoPlayer player;
   private ArrayList zombies;

   public ZombieSortedList(IsoPlayer var1, int var2) {
      this.player = var1;
      this.zombies = new ArrayList();
   }

   private boolean contain(IsoZombie var1) {
      for(int var2 = 0; var2 < this.zombies.size(); ++var2) {
         if (((ZombieSortedList.ZombieWeight)this.zombies.get(var2)).zombie.equals(var1)) {
            return true;
         }
      }

      return false;
   }

   public void update() {
      this.zombies.clear();
      int var1 = ServerOptions.instance.ZombieUpdateMaxHighPriority.getValue();
      double var2 = ServerOptions.instance.ZombieUpdateRadiusHighPriority.getValue();
      var2 *= var2;
      ArrayList var4 = IsoWorld.instance.CurrentCell.getZombieList();

      int var5;
      for(var5 = 0; var5 < var4.size(); ++var5) {
         IsoZombie var6 = (IsoZombie)var4.get(var5);
         double var7 = (double)(this.player.x - var6.x);
         double var9 = (double)(this.player.y - var6.y);
         var7 *= var7;
         var9 *= var9;
         float var11 = (float)(var7 + var9);
         if ((double)var11 < var2 && !this.zombies.contains(var6)) {
            this.zombies.add(new ZombieSortedList.ZombieWeight(var6, var11));
         }
      }

      Collections.sort(this.zombies);
      if (var1 < this.zombies.size()) {
         for(var5 = this.zombies.size() - 1; var5 >= var1; --var5) {
            this.zombies.remove(var5);
         }
      }

   }

   public IsoZombie getZombie(int var1) {
      if (this.zombies.size() != 0 && var1 <= this.zombies.size() - 1) {
         ZombieSortedList.ZombieWeight var2 = (ZombieSortedList.ZombieWeight)this.zombies.get(this.zombies.size() - 1 - var1);
         var2.weight = 0.0F;
         return var2.zombie;
      } else {
         return null;
      }
   }

   class ZombieWeight implements Comparable {
      public IsoZombie zombie;
      public float weight;

      public ZombieWeight(IsoZombie var2, float var3) {
         this.zombie = var2;
         this.weight = var3;
      }

      public int compareTo(ZombieSortedList.ZombieWeight var1) {
         if (this.weight == var1.weight) {
            return 0;
         } else {
            return this.weight < var1.weight ? -1 : 1;
         }
      }

      public boolean equals(Object var1) {
         return this.zombie.equals(var1);
      }
   }
}
