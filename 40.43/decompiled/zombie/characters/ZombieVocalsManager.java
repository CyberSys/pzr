package zombie.characters;

public class ZombieVocalsManager extends BaseZombieSoundManager {
   public static final ZombieVocalsManager instance = new ZombieVocalsManager();

   public ZombieVocalsManager() {
      super(40, 1000);
   }

   public void playSound(IsoZombie var1) {
      String var2 = var1.bFemale ? "FemaleZombieIdle" : "MaleZombieIdle";
      var1.getEmitter().playVocals(var2);
   }

   public void postUpdate() {
   }
}
