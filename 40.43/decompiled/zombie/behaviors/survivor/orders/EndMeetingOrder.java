package zombie.behaviors.survivor.orders;

import zombie.characters.IsoGameCharacter;
import zombie.characters.SurvivorDesc;

public class EndMeetingOrder extends OrderSequence {
   IsoGameCharacter chr;

   public EndMeetingOrder(IsoGameCharacter var1) {
      super(var1);
      this.chr = var1;
   }

   public boolean complete() {
      return true;
   }

   public void initOrder() {
      for(int var1 = 0; var1 < this.chr.getDescriptor().getGroup().Members.size(); ++var1) {
         SurvivorDesc var2 = (SurvivorDesc)this.chr.getDescriptor().getGroup().Members.get(var1);
         if (var2 != this.chr.getDescriptor() && var2.getInstance().InRoomWith(this.chr) && var2.getInstance().getOrder() instanceof GuardOrder) {
            var2.getInstance().getOrders().pop();
         }
      }

   }
}
