package zombie.behaviors;

import java.util.ArrayList;
import zombie.characters.IsoGameCharacter;

public abstract class Behavior {
   public Behavior.BehaviorResult last;
   protected final ArrayList childNodes;

   public Behavior() {
      this.last = Behavior.BehaviorResult.Working;
      this.childNodes = new ArrayList(3);
   }

   public float getPathSpeed() {
      return 0.06F;
   }

   public int renderDebug(int var1) {
      return var1;
   }

   public void update() {
   }

   public void onSwitch() {
   }

   public abstract Behavior.BehaviorResult process(DecisionPath var1, IsoGameCharacter var2);

   public abstract void reset();

   public abstract boolean valid();

   public void addChild(Behavior var1) {
      this.childNodes.add(var1);
   }

   public Behavior.BehaviorResult processChild(DecisionPath var1, IsoGameCharacter var2, int var3) {
      if (!((Behavior)this.childNodes.get(var3)).valid()) {
         return Behavior.BehaviorResult.Failed;
      } else {
         var1.DecisionPath.push(this);
         Behavior.BehaviorResult var4 = ((Behavior)this.childNodes.get(var3)).process(var1, var2);
         var1.DecisionPath.pop();
         return var4;
      }
   }

   public static enum BehaviorResult {
      Failed,
      Working,
      Succeeded;
   }
}
