package zombie.characters;

import zombie.iso.IsoObject;

public final class ZombieThumpManager extends BaseZombieSoundManager {
   public static final ZombieThumpManager instance = new ZombieThumpManager();

   public ZombieThumpManager() {
      super(40, 100);
   }

   public void playSound(IsoZombie var1) {
      if (var1.thumpFlag == 1) {
         var1.getEmitter().playSoundImpl("ZombieThumpGeneric", (IsoObject)null);
      } else if (var1.thumpFlag == 2) {
         var1.getEmitter().playSoundImpl("ZombieThumpGeneric", (IsoObject)null);
         var1.getEmitter().playSoundImpl("ZombieThumpWindow", (IsoObject)null);
      } else if (var1.thumpFlag == 3) {
         var1.getEmitter().playSoundImpl("ZombieThumpWindow", (IsoObject)null);
      } else if (var1.thumpFlag == 4) {
         var1.getEmitter().playSoundImpl("ZombieThumpMetal", (IsoObject)null);
      }

   }

   public void postUpdate() {
      for(int var1 = 0; var1 < this.characters.size(); ++var1) {
         ((IsoZombie)this.characters.get(var1)).setThumpFlag(0);
      }

   }
}
