package zombie.behaviors;

import zombie.characters.IsoGameCharacter;

public class SequenceBehavior extends Behavior {
   public int ID = 0;
   public boolean ProcessNextOnFail = false;

   public Behavior.BehaviorResult process(DecisionPath var1, IsoGameCharacter var2) {
      if (this.ID >= this.childNodes.size()) {
         return Behavior.BehaviorResult.Succeeded;
      } else {
         Behavior.BehaviorResult var3;
         do {
            do {
               if (this.ID >= this.childNodes.size()) {
                  return Behavior.BehaviorResult.Working;
               }

               var3 = this.processChild(var1, var2, this.ID);
               if (var3 == Behavior.BehaviorResult.Succeeded) {
                  ++this.ID;
               } else {
                  if (var3 != Behavior.BehaviorResult.Failed) {
                     return var3;
                  }

                  if (!this.ProcessNextOnFail) {
                     this.ID = 0;
                     return var3;
                  }

                  ++this.ID;
               }
            } while(this.ID != this.childNodes.size());
         } while(var3 != Behavior.BehaviorResult.Succeeded && (var3 != Behavior.BehaviorResult.Failed || !this.ProcessNextOnFail));

         this.ID = 0;
         return Behavior.BehaviorResult.Succeeded;
      }
   }

   public void reset() {
      this.ID = 0;

      for(int var1 = 0; var1 < this.childNodes.size(); ++var1) {
         ((Behavior)this.childNodes.get(var1)).reset();
      }

   }

   public boolean valid() {
      return true;
   }
}
