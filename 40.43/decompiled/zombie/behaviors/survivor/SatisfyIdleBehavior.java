package zombie.behaviors.survivor;

import zombie.behaviors.Behavior;
import zombie.behaviors.DecisionPath;
import zombie.behaviors.general.PathFindBehavior;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoUtils;
import zombie.ui.TextManager;
import zombie.ui.UIFont;

public class SatisfyIdleBehavior extends Behavior {
   public boolean Started = false;
   boolean OtherRoom = false;
   PathFindBehavior pathFind = new PathFindBehavior("Idle");
   IsoGridSquare sq = null;
   int timeout = 0;

   public Behavior.BehaviorResult process(DecisionPath var1, IsoGameCharacter var2) {
      Behavior.BehaviorResult var3 = Behavior.BehaviorResult.Working;
      return var3;
   }

   public void reset() {
      this.Started = false;
      this.sq = null;
      this.pathFind.reset();
   }

   public boolean valid() {
      return true;
   }

   private boolean InDistanceOfPlayer(IsoGameCharacter var1, int var2, int var3) {
      if (var1.getDescriptor().getGroup().Leader.getInstance().getCurrentSquare() == null) {
         return true;
      } else if (var1.getDescriptor().getGroup().Leader == var1.getDescriptor()) {
         return true;
      } else if (var1.getDescriptor().getGroup().Leader.getInstance().getCurrentSquare().getRoom() != null && var1.getCurrentSquare().getRoom() == null) {
         return false;
      } else if (var1.getDescriptor().getGroup().Leader.getInstance().getCurrentSquare().getRoom() == null && var1.getCurrentSquare().getRoom() != null) {
         return false;
      } else if (var1.getDescriptor().getGroup().Leader.getInstance().getCurrentSquare().getRoom() != null && var1.getCurrentSquare().getRoom() != null && var1.getCurrentSquare().getRoom().building == var1.getDescriptor().getGroup().Leader.getInstance().getCurrentSquare().getRoom().building && var1.getThreatLevel() == 0) {
         return true;
      } else {
         return IsoUtils.DistanceManhatten((float)var2, (float)var3, (float)((int)var1.getDescriptor().getGroup().Leader.getInstance().getX()), (float)((int)var1.getDescriptor().getGroup().Leader.getInstance().getY())) < var1.getPersonality().getPlayerDistanceComfort();
      }
   }

   public float getPriority(IsoGameCharacter var1) {
      float var2 = 1.0F;
      if (var1.getThreatLevel() > 0) {
         var2 -= 1000000.0F;
      }

      if (var1.getTimeSinceZombieAttack() < 30) {
         var2 = -1000000.0F;
      }

      return var2;
   }

   public int renderDebug(int var1) {
      byte var2 = 50;
      TextManager.instance.DrawString(UIFont.Small, (double)var2, (double)var1, "SatisfyIdleBehaviour", 1.0D, 1.0D, 1.0D, 1.0D);
      var1 += 30;
      return var1;
   }
}
