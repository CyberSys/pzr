package zombie.characters.personalities;

import zombie.behaviors.survivor.MasterSurvivorBehavior;
import zombie.characters.IsoSurvivor;
import zombie.characters.SurvivorPersonality;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.HandWeapon;

public class GunNut extends SurvivorPersonality {
   public void CreateBehaviours(IsoSurvivor var1) {
      var1.setMasterProper(new MasterSurvivorBehavior(var1));
      var1.getMasterBehaviorList().addChild(var1.getMasterProper());
      if (var1.getPrimaryHandItem() != null) {
         InventoryItem var2 = var1.getPrimaryHandItem();
         if (var2 instanceof HandWeapon) {
            var1.setUseHandWeapon((HandWeapon)var2);
         }
      }

      var1.getMasterBehaviorList().addChild(var1.behaviours);
   }

   public int getHuntZombieRange() {
      return 10;
   }
}
