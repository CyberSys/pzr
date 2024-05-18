package zombie.behaviors.general;

import zombie.behaviors.Behavior;
import zombie.behaviors.DecisionPath;
import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.inventory.InventoryItem;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoUtils;

public class FollowBehaviour extends Behavior {
   public int thinkTime = 30;
   public int thinkTimeMax = 30;
   public boolean stayInside = false;
   PathFindBehavior pathFind = new PathFindBehavior("FollowBehaviour");
   IsoGameCharacter Target = null;
   InventoryItem weapon = null;
   int timeout = 180;

   public Behavior.BehaviorResult process(DecisionPath var1, IsoGameCharacter var2) {
      var2.setFollowingTarget(this.Target);
      boolean var3 = false;
      --this.timeout;
      if (this.timeout <= 0) {
      }

      if (this.Target != null && this.Target.getHealth() <= 0.0F) {
         this.Target = null;
         this.weapon = null;
         this.timeout = 180;
         return Behavior.BehaviorResult.Succeeded;
      } else {
         --this.thinkTime;
         if (this.Target == null && this.thinkTime <= 0) {
            this.Target = var2.getCurrentSquare().FindFriend(var2, var2.getPersonality().getHuntZombieRange(), var2.getEnemyList());
            if (this.Target != null && this.Target.getCurrentSquare() != null) {
               var3 = true;
            }

            if (Rand.Next(2) != 0) {
               var2.setPathSpeed(0.08F);
            } else {
               var2.setPathSpeed(0.05F);
            }

            this.thinkTime = this.thinkTimeMax;
            this.pathFind.sx = var2.getCurrentSquare().getX();
            this.pathFind.sy = var2.getCurrentSquare().getY();
            this.pathFind.sz = var2.getCurrentSquare().getZ();
         }

         if (this.Target == null) {
            this.weapon = null;
            this.timeout = 180;
            return Behavior.BehaviorResult.Succeeded;
         } else {
            IsoGridSquare var4 = var2.getCurrentSquare();
            IsoGridSquare var5 = this.Target.getCurrentSquare();
            if (var4 != null && var5 != null) {
               float var6 = IsoUtils.DistanceManhatten((float)var4.getX(), (float)var4.getY(), (float)var5.getX(), (float)var5.getY());
               if (var5.getZ() == var4.getZ() && !(5.0F < var6)) {
                  this.timeout = 180;
                  return Behavior.BehaviorResult.Succeeded;
               }

               if (var3) {
                  this.pathFind.tx = var5.getX();
                  this.pathFind.ty = var5.getY();
                  this.pathFind.tz = var5.getZ();
                  var3 = false;
               }

               Behavior.BehaviorResult var7 = this.pathFind.process(var1, var2);
               if (var7 == Behavior.BehaviorResult.Failed) {
                  this.Target = null;
                  this.weapon = null;
                  this.thinkTime = this.thinkTimeMax;
                  return var7;
               }

               if (var7 == Behavior.BehaviorResult.Succeeded) {
                  this.Target = null;
                  this.weapon = null;
                  this.thinkTime = 0;
                  return Behavior.BehaviorResult.Succeeded;
               }
            }

            return Behavior.BehaviorResult.Working;
         }
      }
   }

   public void reset() {
      this.Target = null;
      this.weapon = null;
      this.timeout = 180;
      this.pathFind.reset();
   }

   public boolean valid() {
      return true;
   }
}
