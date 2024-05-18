package zombie.behaviors.survivor.orders;

import zombie.behaviors.Behavior;
import zombie.characters.IsoGameCharacter;

public abstract class Order {
   public IsoGameCharacter character = null;
   public String type = "Order";
   public String name = "unnamed";
   public boolean bInit = false;

   public Order(IsoGameCharacter var1) {
      this.character = var1;
   }

   public abstract Behavior.BehaviorResult process();

   public abstract boolean complete();

   public boolean ActedThisFrame() {
      return true;
   }

   public Behavior.BehaviorResult processNext() {
      for(int var1 = this.character.getOrders().size() - 1; var1 >= 0; --var1) {
         if (this.character.getOrders().get(var1) == this && var1 > 1) {
            return ((Order)this.character.getOrders().get(var1 - 1)).process();
         }
      }

      return Behavior.BehaviorResult.Succeeded;
   }

   public void updatenext() {
      for(int var1 = this.character.getOrders().size() - 1; var1 >= 0; --var1) {
         if (this.character.getOrders().get(var1) == this && var1 > 1) {
            ((Order)this.character.getOrders().get(var1 - 1)).update();
         }
      }

   }

   public abstract void update();

   public boolean isCancelledOnAttack() {
      return true;
   }

   public void initOrder() {
   }

   public float getPriority(IsoGameCharacter var1) {
      return 100000.0F;
   }

   public int renderDebug(int var1) {
      return var1;
   }

   public float getPathSpeed() {
      if (this.character.getDangerLevels() == 0.0F) {
         return 0.06F;
      } else {
         float var1 = 10.0F / this.character.getDangerLevels();
         if (var1 > 1.0F) {
            var1 = 1.0F;
         }

         if (var1 < 0.0F) {
            var1 = 0.0F;
         }

         return 0.06F + 0.02F * var1;
      }
   }

   public int getAttackIfEnemiesAroundBias() {
      return 0;
   }

   public boolean isCritical() {
      return false;
   }
}
