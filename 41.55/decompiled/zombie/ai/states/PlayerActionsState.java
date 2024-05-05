package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.HandWeapon;

public final class PlayerActionsState extends State {
   private static final PlayerActionsState _instance = new PlayerActionsState();

   public static PlayerActionsState instance() {
      return _instance;
   }

   public void enter(IsoGameCharacter var1) {
      InventoryItem var2 = var1.getPrimaryHandItem();
      InventoryItem var3 = var1.getSecondaryHandItem();
      if (!(var2 instanceof HandWeapon) && !(var3 instanceof HandWeapon)) {
         var1.setHideWeaponModel(true);
      }

   }

   public void execute(IsoGameCharacter var1) {
   }

   public void exit(IsoGameCharacter var1) {
      var1.setHideWeaponModel(false);
   }

   public void animEvent(IsoGameCharacter var1, AnimEvent var2) {
   }
}
