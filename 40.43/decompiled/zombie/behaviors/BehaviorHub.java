package zombie.behaviors;

import java.util.HashMap;
import java.util.Iterator;
import zombie.characters.IsoGameCharacter;

public class BehaviorHub extends Behavior {
   public HashMap TriggerMap = new HashMap();

   public BehaviorHub.BehaviorTrigger AddTrigger(String var1, float var2, float var3, float var4, Behavior var5) {
      BehaviorHub.BehaviorTrigger var6 = new BehaviorHub.BehaviorTrigger();
      var6.Name = var1;
      var6.Value = var2;
      var6.TriggerValue = var3;
      var6.TriggerBehavior = var5;
      var6.Decay = var4;
      var6.LastValue = var2;
      this.TriggerMap.put(var1, var6);
      return var6;
   }

   public void ChangeTriggerValue(String var1, float var2) {
      ((BehaviorHub.BehaviorTrigger)this.TriggerMap.get(var1)).LastValue = ((BehaviorHub.BehaviorTrigger)this.TriggerMap.get(var1)).Value;
      BehaviorHub.BehaviorTrigger var10000 = (BehaviorHub.BehaviorTrigger)this.TriggerMap.get(var1);
      var10000.Value += var2;
   }

   public Behavior.BehaviorResult process(DecisionPath var1, IsoGameCharacter var2) {
      var1.DecisionPath.push(this);
      Iterator var3 = this.TriggerMap.values().iterator();

      BehaviorHub.BehaviorTrigger var4;
      do {
         if (!var3.hasNext()) {
            var1.DecisionPath.pop();
            return Behavior.BehaviorResult.Succeeded;
         }

         var4 = (BehaviorHub.BehaviorTrigger)var3.next();
         if (var4.Value > 1.0F) {
            var4.Value = 1.0F;
         }

         var4.Value -= var4.Decay;
         if (var4.Value < 0.0F) {
            var4.Value = 0.0F;
         }
      } while(!(var4.Value >= var4.TriggerValue));

      if (var4.LastValue < var4.TriggerValue) {
         var4.TriggerBehavior.reset();
      }

      Behavior.BehaviorResult var5 = var4.TriggerBehavior.process(var1, var2);
      var1.DecisionPath.pop();
      if (var5 == Behavior.BehaviorResult.Failed) {
         return Behavior.BehaviorResult.Succeeded;
      } else if (var5 == Behavior.BehaviorResult.Succeeded) {
         return Behavior.BehaviorResult.Succeeded;
      } else {
         return Behavior.BehaviorResult.Working;
      }
   }

   public void reset() {
      Iterator var1 = this.TriggerMap.values().iterator();

      while(var1.hasNext()) {
         BehaviorHub.BehaviorTrigger var2 = (BehaviorHub.BehaviorTrigger)var1.next();
         var2.TriggerBehavior.reset();
      }

   }

   public void SetTriggerValue(String var1, float var2) {
      if (this.TriggerMap.containsKey(var1)) {
         ((BehaviorHub.BehaviorTrigger)this.TriggerMap.get(var1)).LastValue = ((BehaviorHub.BehaviorTrigger)this.TriggerMap.get(var1)).Value;
         ((BehaviorHub.BehaviorTrigger)this.TriggerMap.get(var1)).Value = var2;
      }
   }

   public boolean valid() {
      return true;
   }

   public class BehaviorTrigger {
      public float Decay;
      public String Name;
      public Behavior TriggerBehavior;
      public float TriggerValue;
      public float Value;
      private float LastValue;
   }
}
