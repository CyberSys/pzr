package zombie.behaviors.survivor.orders.Needs;

import java.util.Stack;
import zombie.behaviors.survivor.orders.OrderSequence;
import zombie.characters.IsoGameCharacter;

public class StopBleeding extends OrderSequence {
   public Stack Items = new Stack();

   public StopBleeding(IsoGameCharacter var1) {
      super(var1);
      this.type = "StopBleeding";
   }

   public void initOrder() {
      this.Items.add("RippedSheets");
   }

   public boolean isCritical() {
      return true;
   }
}
