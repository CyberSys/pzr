package zombie.behaviors.survivor.orders.LittleTasks;

import zombie.behaviors.survivor.orders.GotoNextTo;
import zombie.behaviors.survivor.orders.GotoOrder;
import zombie.behaviors.survivor.orders.ObtainItem;
import zombie.behaviors.survivor.orders.OrderSequence;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoGridSquare;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.objects.IsoWindow;

public class AquireSheetAndBlockWindow extends OrderSequence {
   IsoWindow w;
   IsoCurtain c;

   public AquireSheetAndBlockWindow(IsoGameCharacter var1, IsoWindow var2, IsoCurtain var3) {
      super(var1);
      this.c = var3;
      this.w = var2;
   }

   public boolean complete() {
      return this.ID < this.Orders.size() && !(this.Orders.get(this.ID) instanceof ObtainItem) && this.c == null && !this.character.getInventory().contains("Sheet") ? true : super.complete();
   }

   public void initOrder() {
      if (this.c != null && this.c.open) {
         if (this.c.square.isFree(false)) {
            this.Orders.add(new GotoOrder(this.character, this.c.square.getX(), this.c.square.getY(), this.c.square.getZ()));
         } else {
            this.Orders.add(new GotoNextTo(this.character, this.c.square.getX(), this.c.square.getY(), this.c.square.getZ()));
         }

         this.Orders.add(new CloseCurtains(this.character, this.c));
      } else if (this.c == null) {
         this.Orders.add(new ObtainItem(this.character, "Sheet", 10000));
         IsoGridSquare var1 = this.w.getInsideSquare();
         if (var1.isFree(false)) {
            this.Orders.add(new GotoOrder(this.character, var1.getX(), var1.getY(), var1.getZ()));
         } else {
            this.Orders.add(new GotoNextTo(this.character, var1.getX(), var1.getY(), var1.getZ()));
         }

         this.Orders.add(new HangSheet(this.character, this.w, var1));
      }

   }
}
