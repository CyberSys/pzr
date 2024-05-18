package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;

public class ReanimatePlayerState extends State {
   private static ReanimatePlayerState _instance = new ReanimatePlayerState();

   public static ReanimatePlayerState instance() {
      return _instance;
   }

   public void enter(IsoGameCharacter var1) {
      var1.getStateMachine().Lock = true;
      var1.PlayAnimUnlooped("ZombieGetUp");
      var1.def.setFrameSpeedPerFrame(0.2F);
      if (var1 instanceof IsoPlayer) {
         ((IsoPlayer)var1).setForceOverrideAnim(true);
      }

      var1.setOnFloor(false);
   }

   public void execute(IsoGameCharacter var1) {
      if (var1.getSpriteDef().Finished) {
         var1.getStateMachine().Lock = false;
         if (var1 instanceof IsoPlayer) {
            ((IsoPlayer)var1).setForceOverrideAnim(false);
         }

         var1.setDefaultState();
      }

   }

   public void exit(IsoGameCharacter var1) {
   }
}
