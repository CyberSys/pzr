package zombie.behaviors.survivor.orders.LittleTasks;

import zombie.behaviors.Behavior;
import zombie.behaviors.survivor.orders.Order;
import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.iso.Vector2;

public class StopAndFaceForOrder extends Order {
   IsoGameCharacter other;
   int ticks;
   int delayticks = 0;
   Vector2 vec = new Vector2();

   public StopAndFaceForOrder(IsoGameCharacter var1, IsoGameCharacter var2, int var3) {
      super(var1);
      this.other = var2;
      this.ticks = var3;
      this.delayticks = Rand.Next(35) + 10;
   }

   public Behavior.BehaviorResult process() {
      if (this.delayticks > 0) {
         return this.processNext();
      } else {
         this.vec.x = this.other.getX();
         this.vec.y = this.other.getY();
         Vector2 var10000 = this.vec;
         var10000.x -= this.character.getX();
         var10000 = this.vec;
         var10000.y -= this.character.getY();
         this.vec.normalize();
         this.character.DirectionFromVector(this.vec);
         return Behavior.BehaviorResult.Succeeded;
      }
   }

   public boolean complete() {
      return this.ticks <= 0;
   }

   public void update() {
      if (this.delayticks <= 0) {
         --this.ticks;
      } else {
         --this.delayticks;
         this.updatenext();
      }

   }

   public float getPriority(IsoGameCharacter var1) {
      return this.delayticks <= 0 ? 100000.0F : -100000.0F;
   }
}
