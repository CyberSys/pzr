package zombie.behaviors.survivor.orders.LittleTasks;

import zombie.behaviors.survivor.orders.GotoOrder;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoGridSquare;
import zombie.iso.areas.IsoRoom;
import zombie.iso.areas.IsoRoomExit;

public class GotoRoomOrder extends GotoOrder {
   IsoRoom b;
   IsoGameCharacter chr;

   public GotoRoomOrder(IsoGameCharacter var1, IsoRoom var2) {
      super(var1);
      this.b = var2;
      this.chr = var1;
      if (!var2.Exits.isEmpty()) {
         IsoRoomExit var3 = (IsoRoomExit)var2.Exits.get(0);
         if (var3.From == null) {
            var3 = var3.To;
         }

         IsoGridSquare var4 = var3.From.getFreeTile();
         this.init(var4.getX(), var4.getY(), var4.getZ());
      }
   }

   public boolean complete() {
      return super.complete();
   }
}
