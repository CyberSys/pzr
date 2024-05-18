package zombie.characters.personalities;

import zombie.behaviors.survivor.MasterSurvivorBehavior;
import zombie.characters.IsoSurvivor;
import zombie.characters.SurvivorPersonality;

public class Cowardly extends SurvivorPersonality {
   public void CreateBehaviours(IsoSurvivor var1) {
      var1.setMasterProper(new MasterSurvivorBehavior(var1));
      var1.getMasterBehaviorList().addChild(var1.getMasterProper());
      var1.getMasterBehaviorList().addChild(var1.behaviours);
   }

   public int getHuntZombieRange() {
      return 10;
   }

   public int getZombieFleeAmount() {
      return 1;
   }
}
