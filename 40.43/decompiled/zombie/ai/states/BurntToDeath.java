package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.iso.IsoDirections;
import zombie.iso.objects.IsoDeadBody;
import zombie.network.GameServer;
import zombie.ui.TutorialManager;

public class BurntToDeath extends State {
   static BurntToDeath _instance = new BurntToDeath();

   public static BurntToDeath instance() {
      return _instance;
   }

   public void enter(IsoGameCharacter var1) {
      if (var1 instanceof IsoSurvivor) {
         ((IsoSurvivor)var1).getDescriptor().bDead = true;
      }

      if (!(var1 instanceof IsoZombie)) {
         var1.PlayAnimUnlooped("Die");
      } else {
         var1.PlayAnimUnlooped("ZombieDeath");
      }

      var1.def.AnimFrameIncrease = 0.25F;
      var1.getStateMachine().Lock = true;
      String var2 = var1.isFemale() ? "FemaleZombieDeath" : "MaleZombieDeath";
      var1.getEmitter().playVocals(var2);
      if (GameServer.bServer && var1 instanceof IsoZombie) {
         GameServer.sendZombieSound(IsoZombie.ZombieSound.Burned, (IsoZombie)var1);
      }

   }

   public void execute(IsoGameCharacter var1) {
      if ((int)var1.def.Frame == var1.sprite.CurrentAnim.Frames.size() - 1) {
         if (var1 == TutorialManager.instance.wife) {
            var1.dir = IsoDirections.S;
         }

         var1.RemoveAttachedAnims();
         if (GameServer.bServer && var1 instanceof IsoZombie) {
            GameServer.sendDeadZombie((IsoZombie)var1);
         }

         new IsoDeadBody(var1);
      }

   }

   public void exit(IsoGameCharacter var1) {
      var1.setIgnoreMovementForDirection(false);
   }
}
