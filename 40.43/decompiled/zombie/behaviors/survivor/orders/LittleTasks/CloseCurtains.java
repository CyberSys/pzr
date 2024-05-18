package zombie.behaviors.survivor.orders.LittleTasks;

import zombie.behaviors.Behavior;
import zombie.behaviors.survivor.orders.Order;
import zombie.characters.IsoGameCharacter;
import zombie.iso.objects.IsoCurtain;

public class CloseCurtains extends Order {
   IsoCurtain door = null;
   IsoGameCharacter chr;

   public CloseCurtains(IsoGameCharacter var1, IsoCurtain var2) {
      super(var1);
      this.door = var2;
      this.chr = var1;
   }

   public boolean complete() {
      if (this.door == null) {
         return true;
      } else {
         return !this.door.open;
      }
   }

   public Behavior.BehaviorResult process() {
      if (this.door != null && this.door.open) {
         this.door.ToggleDoor(this.chr);
      }

      return Behavior.BehaviorResult.Succeeded;
   }

   public void update() {
   }
}
