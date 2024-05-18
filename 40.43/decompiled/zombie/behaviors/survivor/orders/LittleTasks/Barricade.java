package zombie.behaviors.survivor.orders.LittleTasks;

import zombie.behaviors.Behavior;
import zombie.behaviors.survivor.orders.Order;
import zombie.characters.IsoGameCharacter;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoDoor;

public class Barricade extends Order {
   IsoDoor door = null;
   IsoGameCharacter chr;
   int level = 2;

   public Barricade(IsoGameCharacter var1, IsoDoor var2) {
      super(var1);
      this.door = var2;
      this.chr = var1;
   }

   public boolean complete() {
      IsoBarricade var1 = this.door.getBarricadeForCharacter(this.chr);
      return var1 != null && var1.getNumPlanks() >= this.level;
   }

   public Behavior.BehaviorResult process() {
      IsoBarricade var1 = this.door.getBarricadeForCharacter(this.chr);
      if (this.chr.getCharacterActions().isEmpty() && var1 != null && var1.getNumPlanks() < this.level) {
      }

      return var1 != null && var1.getNumPlanks() >= this.level ? Behavior.BehaviorResult.Succeeded : Behavior.BehaviorResult.Working;
   }

   public void update() {
   }
}
