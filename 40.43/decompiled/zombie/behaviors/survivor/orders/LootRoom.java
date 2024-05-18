package zombie.behaviors.survivor.orders;

import zombie.characters.IsoGameCharacter;
import zombie.inventory.ItemContainer;
import zombie.iso.IsoGridSquare;
import zombie.iso.areas.IsoRoom;

class LootRoom extends OrderSequence {
   IsoGameCharacter chr;
   IsoRoom room;
   LootBuilding.LootStyle style;

   public LootRoom(IsoGameCharacter var1, IsoRoom var2, LootBuilding.LootStyle var3) {
      super(var1);
      this.style = var3;
      this.room = var2;
      this.chr = var1;

      for(int var4 = 0; var4 < var2.Containers.size(); ++var4) {
         ItemContainer var5 = (ItemContainer)var2.Containers.get(var4);
         IsoGridSquare var6 = var5.parent.square;
         this.Orders.add(new GotoNextTo(var1, var6.getX(), var6.getY(), var6.getZ()));
         this.Orders.add(new LootContainer(var1, var5, var3));
      }

   }

   public void init() {
   }
}
