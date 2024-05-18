package zombie.behaviors;

import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;

public class RandomBehavior extends Behavior {
   public Behavior.BehaviorResult process(DecisionPath var1, IsoGameCharacter var2) {
      return this.processChild(var1, var2, Rand.Next(this.childNodes.size()));
   }

   public void reset() {
   }

   public boolean valid() {
      return true;
   }
}
