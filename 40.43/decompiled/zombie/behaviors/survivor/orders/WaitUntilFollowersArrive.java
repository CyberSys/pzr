package zombie.behaviors.survivor.orders;

import zombie.behaviors.Behavior;
import zombie.behaviors.DecisionPath;
import zombie.behaviors.survivor.SatisfyIdleBehavior;
import zombie.characters.IsoGameCharacter;
import zombie.characters.SurvivorDesc;

public class WaitUntilFollowersArrive extends Order {
   SatisfyIdleBehavior idle = new SatisfyIdleBehavior();
   int timeout = 600;

   public WaitUntilFollowersArrive(IsoGameCharacter var1) {
      super(var1);
   }

   public Behavior.BehaviorResult process() {
      this.idle.process((DecisionPath)null, this.character);
      --this.timeout;
      return Behavior.BehaviorResult.Working;
   }

   public boolean complete() {
      if (this.timeout <= 0) {
         return true;
      } else {
         for(int var1 = 0; var1 < this.character.getDescriptor().getGroup().Members.size(); ++var1) {
            SurvivorDesc var2 = (SurvivorDesc)this.character.getDescriptor().getGroup().Members.get(var1);
            if (var2.getInstance() != null && var2.getInstance() != this.character && !var2.getInstance().isDead() && var2.getInstance().getOrder() instanceof FollowOrder && ((FollowOrder)var2.getInstance().getOrder()).target == this.character && !var2.getInstance().InBuildingWith(this.character)) {
               return false;
            }
         }

         return true;
      }
   }

   public void update() {
   }
}
