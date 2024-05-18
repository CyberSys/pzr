package zombie.behaviors.survivor.orders;

import java.util.Stack;
import zombie.behaviors.survivor.orders.LittleTasks.GotoRoomOrder;
import zombie.characters.IsoGameCharacter;
import zombie.characters.SurvivorDesc;
import zombie.iso.IsoGridSquare;

public class CallMeetingOrder extends OrderSequence {
   IsoGameCharacter chr;
   Stack characters = new Stack();
   int timeout = 500;

   public CallMeetingOrder(IsoGameCharacter var1) {
      super(var1);
      this.chr = var1;
   }

   public void update() {
      --this.timeout;
      super.update();
   }

   public boolean complete() {
      if (this.timeout < 0) {
         return true;
      } else {
         for(int var1 = 0; var1 < this.characters.size(); ++var1) {
            if (!((IsoGameCharacter)this.characters.get(var1)).isDead() && !((IsoGameCharacter)this.characters.get(var1)).InRoomWith(this.chr)) {
               return false;
            }
         }

         return true;
      }
   }

   public void initOrder() {
      for(int var1 = 0; var1 < this.chr.getDescriptor().getGroup().Members.size(); ++var1) {
         SurvivorDesc var2 = (SurvivorDesc)this.chr.getDescriptor().getGroup().Members.get(var1);
         if (var2 != this.chr.getDescriptor() && var2 != null && var2.getInstance().InBuildingWith(this.chr)) {
            IsoGridSquare var3 = this.chr.getCurrentSquare().getRoom().getFreeTile();
            var2.getInstance().GiveOrder(new GuardOrder(var2.getInstance()), false);
            var2.getInstance().GiveOrder(new GotoRoomOrder(var2.getInstance(), this.chr.getCurrentSquare().getRoom()), false);
            var2.getInstance().GiveOrder(new GotoOrder(var2.getInstance(), var3.getX(), var3.getY(), var3.getZ()), false);
            this.characters.add(var2.getInstance());
         }
      }

      this.Orders.add(new GuardOrder(this.chr));
   }
}
