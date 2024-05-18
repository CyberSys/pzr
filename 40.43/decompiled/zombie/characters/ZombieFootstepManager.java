package zombie.characters;

public class ZombieFootstepManager extends BaseZombieSoundManager {
   public static final ZombieFootstepManager instance = new ZombieFootstepManager();

   public ZombieFootstepManager() {
      super(40, 100);
   }

   public void playSound(IsoZombie var1) {
      var1.getEmitter().playFootsteps("zombie_m");
   }

   public void postUpdate() {
   }
}
