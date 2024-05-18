package zombie.characters.personalities;

import zombie.behaviors.DecisionPath;
import zombie.behaviors.general.FollowBehaviour;
import zombie.behaviors.survivor.AttackBehavior;
import zombie.behaviors.survivor.FleeBehaviour;
import zombie.behaviors.survivor.ObeyOrders;
import zombie.behaviors.survivor.SatisfyIdleBehavior;
import zombie.characters.IsoSurvivor;
import zombie.characters.SurvivorPersonality;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.HandWeapon;

public class FriendlyArmed extends SurvivorPersonality {
   public int getZombieFleeAmount() {
      return 10;
   }

   public void CreateBehaviours(IsoSurvivor var1) {
      var1.getMasterBehaviorList().addChild(new ObeyOrders(var1));
      var1.behaviours.AddTrigger("IdleBoredom", 0.0F, 0.6F, 1.0E-6F, new SatisfyIdleBehavior());
      var1.getMasterBehaviorList().addChild(new FleeBehaviour());
      new FollowBehaviour();
      AttackBehavior var3 = new AttackBehavior();
      var1.getMasterBehaviorList().addChild(var3);
      var3.process((DecisionPath)null, var1);
      if (var1.getPrimaryHandItem() != null) {
         InventoryItem var4 = var1.getPrimaryHandItem();
         if (var4 instanceof HandWeapon) {
            var1.setUseHandWeapon((HandWeapon)var4);
         }
      }

      var1.getMasterBehaviorList().addChild(var1.behaviours);
   }

   public int getHuntZombieRange() {
      return 10;
   }
}
