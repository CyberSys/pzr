package zombie.behaviors.survivor.orders;

import zombie.behaviors.Behavior;
import zombie.behaviors.DecisionPath;
import zombie.behaviors.general.PathFindBehavior;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoSurvivor;
import zombie.core.Rand;
import zombie.iso.IsoCamera;
import zombie.ui.TextManager;
import zombie.ui.UIFont;

public class FollowOrder extends Order {
   public int Range = 1;
   public int pathfindtimer = 60;
   public IsoGameCharacter target;
   public boolean bStrict = false;
   PathFindBehavior PathFind = new PathFindBehavior();
   float rangelast = 0.0F;
   public float lastDist = 0.0F;
   public float currentDist = 0.0F;

   public FollowOrder(IsoGameCharacter var1, IsoGameCharacter var2, int var3) {
      super(var1);
      this.target = var2;
      this.Range = var3;
      this.PathFind.reset();
      this.PathFind.name = "FollowOrder";
   }

   public FollowOrder(IsoGameCharacter var1) {
      super(var1);
   }

   public FollowOrder(IsoGameCharacter var1, IsoGameCharacter var2, int var3, boolean var4) {
      super(var1);
      this.target = var2;
      this.Range = var3;
      this.PathFind.reset();
      this.PathFind.name = "FollowOrder";
      this.bStrict = true;
   }

   private boolean InDistanceOfPlayer(IsoGameCharacter var1, int var2, int var3) {
      if (this.target.getCurrentSquare() == null) {
         return true;
      } else {
         this.target.ensureOnTile();
         var1.ensureOnTile();
         if (this.target.getCurrentSquare() != null && this.target.getCurrentSquare().getRoom() != null && var1.getCurrentSquare().getRoom() != this.target.getCurrentSquare().getRoom()) {
            return false;
         } else if (this.target.getCurrentSquare() != null && var1.getCurrentSquare().getRoom() != null && this.target.getCurrentSquare().getRoom() == null) {
            return false;
         } else {
            this.rangelast = this.currentDist;
            return this.rangelast < (float)this.Range;
         }
      }
   }

   public Behavior.BehaviorResult process() {
      if (this.target == null) {
         return Behavior.BehaviorResult.Failed;
      } else {
         boolean var1;
         if (this.character == IsoCamera.CamCharacter) {
            var1 = false;
         }

         this.lastDist = this.currentDist;
         this.currentDist = this.character.DistTo(this.target);
         --this.pathfindtimer;
         var1 = this.InDistanceOfPlayer(this.character, (int)this.character.getX(), (int)this.character.getY());
         if (!var1 && this.pathfindtimer < 0 && this.currentDist > (float)this.Range) {
            this.PathFind.reset();
            this.PathFind.sx = (int)this.character.getX();
            this.PathFind.sy = (int)this.character.getY();
            this.PathFind.sz = (int)this.character.getZ();
            this.PathFind.tx = (int)this.target.getX() + (Rand.Next(6) - 3);
            this.PathFind.ty = (int)this.target.getY() + (Rand.Next(6) - 3);
            this.PathFind.tz = (int)this.target.getZ();
            this.pathfindtimer = 120;
         }

         Behavior.BehaviorResult var2 = this.PathFind.process((DecisionPath)null, this.character);
         if (var2 != Behavior.BehaviorResult.Working) {
            this.pathfindtimer = -1;
         }

         return var2;
      }
   }

   public boolean isCancelledOnAttack() {
      return false;
   }

   public float getPriority(IsoGameCharacter var1) {
      float var2 = 0.0F;
      if (this.target == null) {
         return -1000000.0F;
      } else {
         float var3 = var1.DistTo(this.target);
         this.lastDist = this.currentDist;
         this.currentDist = var3;
         var3 -= (float)this.Range;
         if (var3 < 0.0F) {
            var3 = 0.0F;
         }

         var2 += var3 * 6.0F;
         var2 += (float)(var1.getThreatLevel() * 5);
         var2 += var1.getDescriptor().getLoyalty() * 5.0F;
         if (this.target != null && this.target.getLegsSprite().CurrentAnim.name.equals("Run")) {
            var2 *= 2.0F;
         }

         boolean var4 = this.InDistanceOfPlayer(var1, (int)var1.getX(), (int)var1.getY());
         if (var4) {
            var2 = 0.0F;
         } else {
            var2 *= 20000.0F;
         }

         return var1 instanceof IsoSurvivor && !((IsoSurvivor)var1).getVeryCloseEnemyList().isEmpty() ? 0.0F : var2;
      }
   }

   public float getPathSpeed() {
      return this.currentDist > (float)this.Range * 3.0F ? 0.08F : 0.06F;
   }

   public int renderDebug(int var1) {
      byte var2 = 50;
      TextManager.instance.DrawString(UIFont.Small, (double)var2, (double)var1, "FollowOrder", 1.0D, 1.0D, 1.0D, 1.0D);
      var1 += 30;
      return var1;
   }

   public boolean complete() {
      return this.target == null ? true : this.target.isDead();
   }

   public void update() {
   }
}
