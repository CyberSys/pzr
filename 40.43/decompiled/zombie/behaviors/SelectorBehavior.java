package zombie.behaviors;

import zombie.characters.IsoGameCharacter;

public class SelectorBehavior extends Behavior {
   public int ID = 0;

   public Behavior.BehaviorResult process(DecisionPath var1, IsoGameCharacter var2) {
      if (this.ID >= this.childNodes.size()) {
         return Behavior.BehaviorResult.Failed;
      } else {
         Behavior.BehaviorResult var3 = this.processChild(var1, var2, this.ID);
         if (var3 == Behavior.BehaviorResult.Failed) {
            ++this.ID;
         }

         if (var3 == Behavior.BehaviorResult.Succeeded) {
            return var3;
         } else if (this.ID == this.childNodes.size() && var3 == Behavior.BehaviorResult.Failed) {
            this.ID = 0;
            return Behavior.BehaviorResult.Failed;
         } else {
            return Behavior.BehaviorResult.Working;
         }
      }
   }

   public void reset() {
   }

   public boolean valid() {
      return true;
   }
}
