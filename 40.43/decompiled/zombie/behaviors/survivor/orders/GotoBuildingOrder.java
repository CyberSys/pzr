package zombie.behaviors.survivor.orders;

import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.iso.IsoGridSquare;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.IsoRoomExit;

public class GotoBuildingOrder extends GotoOrder {
   IsoBuilding b;
   IsoGameCharacter chr;

   public GotoBuildingOrder(IsoGameCharacter var1, IsoBuilding var2) {
      super(var1);
      this.b = var2;
      this.chr = var1;
      IsoRoomExit var3 = (IsoRoomExit)var2.Exits.get(Rand.Next(var2.Exits.size()));
      if (var3.From == null) {
         var3 = var3.To;
      }

      IsoGridSquare var4 = var3.From.getFreeTile();
      this.init(var4.getX(), var4.getY(), var4.getZ());
   }

   public boolean complete() {
      return super.complete();
   }
}
