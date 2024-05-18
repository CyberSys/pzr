package zombie.behaviors.survivor;

import zombie.behaviors.Behavior;
import zombie.behaviors.DecisionPath;
import zombie.behaviors.survivor.orders.Order;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoCamera;

public class ObeyOrders extends Behavior {
   IsoGameCharacter character;
   public boolean Started = false;

   public ObeyOrders(IsoGameCharacter var1) {
      this.character = var1;
   }

   public Behavior.BehaviorResult process(DecisionPath var1, IsoGameCharacter var2) {
      if (var2 == IsoCamera.CamCharacter) {
         boolean var3 = false;
      }

      Behavior.BehaviorResult var4;
      if (var2.getPersonalNeed() != null) {
         if (!var2.getPersonalNeed().bInit) {
            var2.getPersonalNeed().initOrder();
            var2.getPersonalNeed().bInit = true;
         }

         var4 = var2.getPersonalNeed().process();
         if (var2.getPersonalNeed().complete()) {
            var2.getPersonalNeeds().pop();
            var2.setPersonalNeed((Order)null);
         } else if (var2.getPersonalNeed().ActedThisFrame()) {
            return var4;
         }
      }

      if (var2.getOrder() == null) {
         return Behavior.BehaviorResult.Succeeded;
      } else {
         if (!var2.getOrder().bInit) {
            var2.getOrder().initOrder();
            var2.getOrder().bInit = true;
         }

         var4 = var2.getOrder().process();
         if (var2.getOrder().complete()) {
            var2.getOrders().pop();
         }

         return var4;
      }
   }

   public void update() {
      if (this.character.getOrder() != null) {
         this.character.getOrder().update();
      }

   }

   public void reset() {
   }

   public float getPathSpeed() {
      if (this.character.getOrder() != null) {
         return this.character.getOrder().getPathSpeed();
      } else if (this.character.getDangerLevels() == 0.0F) {
         return 0.05F;
      } else {
         float var1 = 10.0F / this.character.getDangerLevels();
         if (var1 > 1.0F) {
            var1 = 1.0F;
         }

         if (var1 < 0.0F) {
            var1 = 0.0F;
         }

         return 0.05F + 0.02F * var1;
      }
   }

   public boolean valid() {
      return true;
   }

   public int renderDebug(int var1) {
      return this.character.getOrder() == null ? var1 : this.character.getOrder().renderDebug(var1);
   }
}
