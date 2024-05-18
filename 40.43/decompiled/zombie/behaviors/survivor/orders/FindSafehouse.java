package zombie.behaviors.survivor.orders;

import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoSurvivor;

public class FindSafehouse extends OrderSequence {
   public FindSafehouse(IsoGameCharacter var1) {
      super(var1);
      IsoSurvivor var2 = (IsoSurvivor)var1;
      this.Orders.add(new GotoBuildingOrder(var2, var2.getDescriptor().getGroup().Safehouse));
      this.Orders.add(new SecureSafehouse(var2));
      this.Orders.add(new LootBuilding(var1, var2.getDescriptor().getGroup().Safehouse, LootBuilding.LootStyle.Safehouse));
   }
}
