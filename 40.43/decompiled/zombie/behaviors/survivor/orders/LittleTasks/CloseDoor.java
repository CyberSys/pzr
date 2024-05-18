package zombie.behaviors.survivor.orders.LittleTasks;

import zombie.behaviors.Behavior;
import zombie.behaviors.survivor.orders.Order;
import zombie.characters.IsoGameCharacter;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoThumpable;

public class CloseDoor extends Order {
   IsoDoor door = null;
   IsoThumpable thumpDoor = null;
   IsoGameCharacter chr;

   public CloseDoor(IsoGameCharacter var1, IsoDoor var2) {
      super(var1);
      this.door = var2;
      this.chr = var1;
   }

   public CloseDoor(IsoGameCharacter var1, IsoThumpable var2) {
      super(var1);
      this.thumpDoor = var2;
      this.chr = var1;
   }

   public boolean complete() {
      if (this.door == null && this.thumpDoor == null) {
         return true;
      } else if (this.door != null) {
         return !this.door.open;
      } else {
         return !this.thumpDoor.open;
      }
   }

   public Behavior.BehaviorResult process() {
      if (this.door != null && this.door.open) {
         this.door.ToggleDoor(this.chr);
      } else if (this.thumpDoor != null && this.thumpDoor.open) {
         this.thumpDoor.ToggleDoor(this.chr);
      }

      return Behavior.BehaviorResult.Succeeded;
   }

   public void update() {
   }
}
