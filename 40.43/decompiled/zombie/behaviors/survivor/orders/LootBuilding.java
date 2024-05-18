package zombie.behaviors.survivor.orders;

import java.util.Stack;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoSurvivor;
import zombie.characters.SurvivorDesc;
import zombie.core.Rand;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.IsoRoom;

public class LootBuilding extends OrderSequence {
   IsoGameCharacter chr;
   IsoBuilding building;
   LootBuilding.LootStyle style;

   public LootBuilding(IsoGameCharacter var1, IsoBuilding var2, LootBuilding.LootStyle var3) {
      super(var1);
      this.building = var2;
      this.style = var3;
      this.chr = var1;
      Stack var4 = new Stack();
      var4.addAll(var2.Rooms);

      for(int var5 = 0; var5 < var4.size(); ++var5) {
         if (((IsoRoom)var4.get(var5)).Containers.isEmpty()) {
            var4.remove(var5);
            --var5;
         }
      }

      while(!var4.isEmpty()) {
         IsoRoom var6 = (IsoRoom)var4.get(Rand.Next(var4.size()));
         this.Orders.add(new LootRoom(var1, var6, var3));
         var4.remove(var6);
      }

   }

   public boolean complete() {
      return this.chr.getInventoryWeight() + 10.0F >= (float)(this.chr.getMaxWeight() / 2) ? true : super.complete();
   }

   public void initOrder() {
      if (((IsoSurvivor)this.chr).getDescriptor().getGroup().Leader == this.chr.getDescriptor()) {
         for(int var1 = 0; var1 < this.chr.getDescriptor().getGroup().Members.size(); ++var1) {
            SurvivorDesc var2 = (SurvivorDesc)this.chr.getDescriptor().getGroup().Members.get(var1);
            if (var2.getInstance() == null) {
               return;
            }

            if (var2.getInstance().getOrder() instanceof FollowOrder) {
               var2.getInstance().GiveOrder(new LootBuilding(var2.getInstance(), this.building, this.style), false);
            }
         }
      }

   }

   public static enum LootStyle {
      Safehouse,
      Medium,
      Extreme;
   }
}
