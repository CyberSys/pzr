package zombie.behaviors.survivor.orders;

import zombie.behaviors.Behavior;
import zombie.behaviors.DecisionPath;
import zombie.behaviors.survivor.SatisfyIdleBehavior;
import zombie.characters.IsoGameCharacter;

public class IdleOrder extends Order {
   SatisfyIdleBehavior idle = new SatisfyIdleBehavior();

   public IdleOrder(IsoGameCharacter var1) {
      super(var1);
   }

   public Behavior.BehaviorResult process() {
      this.idle.process((DecisionPath)null, this.character);
      return Behavior.BehaviorResult.Working;
   }

   public boolean complete() {
      return false;
   }

   public void update() {
   }

   public float getPriority(IsoGameCharacter var1) {
      return 200.0F;
   }
}
