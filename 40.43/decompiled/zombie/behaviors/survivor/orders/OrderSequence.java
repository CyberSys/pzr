package zombie.behaviors.survivor.orders;

import java.util.Stack;
import zombie.behaviors.Behavior;
import zombie.characters.IsoGameCharacter;
import zombie.ui.TextManager;
import zombie.ui.UIFont;

public class OrderSequence extends Order {
   public int ID = 0;
   public Stack Orders = new Stack();

   public OrderSequence(IsoGameCharacter var1) {
      super(var1);
   }

   public boolean complete() {
      return this.ID >= this.Orders.size();
   }

   public void update() {
      if (this.ID < this.Orders.size()) {
         ((Order)this.Orders.get(this.ID)).update();
      }

   }

   public void initOrder() {
   }

   public Behavior.BehaviorResult process() {
      if (this.ID >= this.Orders.size()) {
         return Behavior.BehaviorResult.Succeeded;
      } else {
         if (!((Order)this.Orders.get(this.ID)).bInit) {
            ((Order)this.Orders.get(this.ID)).initOrder();
            ((Order)this.Orders.get(this.ID)).bInit = true;
         }

         Behavior.BehaviorResult var1 = ((Order)this.Orders.get(this.ID)).process();
         if (((Order)this.Orders.get(this.ID)).complete()) {
            ++this.ID;
         }

         return this.ID >= this.Orders.size() ? Behavior.BehaviorResult.Succeeded : Behavior.BehaviorResult.Working;
      }
   }

   public int renderDebug(int var1) {
      byte var2 = 50;
      TextManager.instance.DrawString(UIFont.Small, (double)var2, (double)var1, "OrderSequence", 1.0D, 1.0D, 1.0D, 1.0D);
      var1 += 30;
      if (this.ID < this.Orders.size()) {
         ((Order)this.Orders.get(this.ID)).renderDebug(var1);
      }

      return var1;
   }

   public float getPriority(IsoGameCharacter var1) {
      if (!this.bInit) {
         return 10000.0F;
      } else {
         return this.Orders.size() <= this.ID ? -10000.0F : ((Order)this.Orders.get(this.ID)).getPriority(var1);
      }
   }
}
