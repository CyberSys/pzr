package zombie.behaviors.survivor.orders;

import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.iso.IsoGridSquare;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.IsoRoomExit;

public class GotoSafehouseOrder extends GotoOrder {
   IsoBuilding b;
   IsoGameCharacter chr;

   public GotoSafehouseOrder(IsoGameCharacter var1) {
      super(var1);
      this.chr = var1;
   }

   public void initOrder() {
      this.b = this.chr.getDescriptor().getGroup().Safehouse;
      IsoRoomExit var1 = (IsoRoomExit)this.b.Exits.get(Rand.Next(this.b.Exits.size()));
      if (var1.From == null) {
         var1 = var1.To;
      }

      IsoGridSquare var2 = var1.From.getFreeTile();
      this.init(var2.getX(), var2.getY(), var2.getZ());
   }

   public boolean complete() {
      return super.complete();
   }
}
