package zombie.behaviors.survivor.orders.LittleTasks;

import zombie.behaviors.Behavior;
import zombie.behaviors.survivor.orders.Order;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.objects.IsoWindow;

public class HangSheet extends Order {
   IsoWindow door = null;
   IsoGameCharacter chr;
   IsoGridSquare sq;

   public HangSheet(IsoGameCharacter var1, IsoWindow var2, IsoGridSquare var3) {
      super(var1);
      this.door = var2;
      this.sq = var3;
      this.chr = var1;
   }

   public boolean complete() {
      return true;
   }

   public Behavior.BehaviorResult process() {
      if (!this.character.getInventory().contains("Sheet")) {
         return Behavior.BehaviorResult.Succeeded;
      } else if (this.door.HasCurtains() != null) {
         return Behavior.BehaviorResult.Succeeded;
      } else {
         int var1 = IsoCell.getSheetCurtains();
         IsoDirections var2 = IsoDirections.N;
         if (this.door.north && this.sq == this.door.square) {
            var2 = IsoDirections.N;
         }

         if (this.door.north && this.sq != this.door.square) {
            var2 = IsoDirections.S;
         }

         if (!this.door.north && this.sq == this.door.square) {
            var2 = IsoDirections.W;
         }

         if (!this.door.north && this.sq != this.door.square) {
            var2 = IsoDirections.E;
         }

         var1 = 16;
         if (var2 == IsoDirections.E) {
            ++var1;
         }

         if (var2 == IsoDirections.S) {
            var1 += 3;
         }

         if (var2 == IsoDirections.N) {
            var1 += 2;
         }

         var1 += 4;
         IsoCurtain var3 = new IsoCurtain(this.door.getCell(), this.sq, "TileObjects3_" + var1, this.door.north);
         this.sq.AddSpecialTileObject(var3);
         if (var3.open) {
            var3.ToggleDoorSilent();
         }

         this.character.getInventory().RemoveOneOf("Sheet");
         return Behavior.BehaviorResult.Succeeded;
      }
   }

   public void update() {
   }
}
