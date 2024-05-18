package zombie.behaviors.survivor.orders;

import java.util.Stack;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoSurvivor;
import zombie.iso.IsoCell;
import zombie.iso.IsoWorld;
import zombie.iso.areas.BuildingScore;
import zombie.iso.areas.IsoBuilding;

public class LootMission extends OrderSequence {
   IsoBuilding b;
   IsoGameCharacter chr;

   public LootMission(IsoGameCharacter var1) {
      super(var1);
      this.chr = var1;
   }

   public void initOrder() {
      this.character = this.chr;
      Stack var1 = IsoWorld.instance.CurrentCell.getBestBuildings(IsoCell.BuildingSearchCriteria.General, 8);
      float var2 = -1.0F;
      IsoBuilding var3 = null;

      for(int var4 = 0; var4 < var1.size(); ++var4) {
         if (((BuildingScore)var1.get(var4)).building == ((IsoSurvivor)this.chr).getDescriptor().getGroup().Safehouse) {
            var1.remove(var4);
            --var4;
         } else {
            float var5 = ((BuildingScore)var1.get(var4)).building.getNeedsScore(((IsoSurvivor)this.chr).getDescriptor().getGroup());
            if (var5 > var2) {
               var2 = var5;
               var3 = ((BuildingScore)var1.get(var4)).building;
            }
         }
      }

      if (var3 != null && !var3.Exits.isEmpty()) {
         if (!this.character.IsInBuilding(((IsoSurvivor)this.chr).getDescriptor().getGroup().Safehouse)) {
            this.Orders.add(new GotoBuildingOrder(this.chr, ((IsoSurvivor)this.chr).getDescriptor().getGroup().Safehouse));
         }

         this.Orders.add(new CallMeetingOrder(this.chr));
         this.Orders.add(new ArrangeLootingTeamOrder(this.chr, "Base.ArrangeLooting"));
         this.Orders.add(new EndMeetingOrder(this.chr));
         this.Orders.add(new GotoBuildingOrder(this.chr, var3));
         this.Orders.add(new LootBuilding(this.chr, var3, LootBuilding.LootStyle.Extreme));
         var3 = ((IsoSurvivor)this.chr).getDescriptor().getGroup().Safehouse;
         this.Orders.add(new GotoSafehouseOrder(this.chr));
         this.Orders.add(new SecureSafehouse(this.chr));
         this.Orders.add(new ReturnToSafehouseConversation(this.chr, "Base.BackWithLoot"));
         this.Orders.add(new DumpLootOrder(this.chr, var3));
      }
   }
}
