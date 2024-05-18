package zombie.behaviors;

import zombie.characters.IsoGameCharacter;

public class RootBehavior extends Behavior {
   public Behavior.BehaviorResult process(DecisionPath var1, IsoGameCharacter var2) {
      if (this.childNodes.size() == 0) {
         return Behavior.BehaviorResult.Working;
      } else {
         for(int var3 = 0; var3 < this.childNodes.size(); ++var3) {
            this.processChild(var1, var2, var3);
         }

         return Behavior.BehaviorResult.Working;
      }
   }

   public void reset() {
   }

   public boolean valid() {
      return true;
   }
}
