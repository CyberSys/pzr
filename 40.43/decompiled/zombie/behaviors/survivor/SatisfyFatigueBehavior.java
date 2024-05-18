package zombie.behaviors.survivor;

import java.util.Iterator;
import zombie.behaviors.Behavior;
import zombie.behaviors.DecisionPath;
import zombie.behaviors.general.PathFindBehavior;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoGridSquare;
import zombie.iso.areas.IsoRoom;

public class SatisfyFatigueBehavior extends Behavior {
   PathFindBehavior pathFind = new PathFindBehavior("Fatigue");

   public Behavior.BehaviorResult process(DecisionPath var1, IsoGameCharacter var2) {
      IsoGridSquare var3 = null;
      Behavior.BehaviorResult var4 = Behavior.BehaviorResult.Failed;
      if (var2.getCurrentSquare().getRoom() != null) {
         if (var2.getCurrentSquare().getRoom().Beds.size() > 0) {
            var3 = (IsoGridSquare)var2.getCurrentSquare().getRoom().Beds.get(0);
            if (var3.getMovingObjects().size() > 0 && var3.getMovingObjects().get(0) != var2) {
               var3 = null;
            }
         } else {
            Iterator var5 = var2.getCurrentSquare().getRoom().building.Rooms.iterator();

            while(var5.hasNext()) {
               IsoRoom var6 = (IsoRoom)var5.next();
               if (var6.Beds.size() > 0) {
                  var3 = (IsoGridSquare)var6.Beds.get(0);
                  if (var3.getMovingObjects().size() > 0 && var3.getMovingObjects().get(0) != var2) {
                     var3 = null;
                  }
               }

               if (var3 != null) {
                  break;
               }
            }
         }

         if (var3 != null) {
            if (!this.pathFind.running(var2)) {
               this.pathFind.sx = (int)var2.getX();
               this.pathFind.sy = (int)var2.getY();
               this.pathFind.sz = (int)var2.getZ();
            }

            this.pathFind.tx = var3.getX();
            this.pathFind.ty = var3.getY();
            this.pathFind.tz = var3.getZ();
            var4 = this.pathFind.process(var1, var2);
            if (var4 == Behavior.BehaviorResult.Succeeded) {
               var2.setAsleep(true);
            }
         }
      }

      return var4;
   }

   public void reset() {
   }

   public boolean valid() {
      return true;
   }
}
