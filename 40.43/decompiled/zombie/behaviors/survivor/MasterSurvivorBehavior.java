package zombie.behaviors.survivor;

import zombie.ai.astar.Path;
import zombie.behaviors.Behavior;
import zombie.behaviors.DecisionPath;
import zombie.behaviors.survivor.orders.Order;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoSurvivor;
import zombie.characters.Stats;
import zombie.iso.IsoCamera;

public class MasterSurvivorBehavior extends Behavior {
   IsoSurvivor survivor;
   public Behavior toProcess = null;
   int sinceLastChanged = 0;
   int sinceLastChangedMax = 120;
   FleeBehaviour flee;
   AttackBehavior attack;
   SatisfyIdleBehavior idle;
   ObeyOrders orders;
   int timeTillProcessChange = 120;
   int timeTillPathSpeedChange = 120;
   public static float FleeMultiplier = 0.05F;
   public static float AttackMultiplier = 15.0F;

   public MasterSurvivorBehavior(IsoSurvivor var1) {
      this.survivor = var1;
      this.flee = new FleeBehaviour();
      this.attack = new AttackBehavior();
      this.idle = new SatisfyIdleBehavior();
      this.orders = new ObeyOrders(var1);
   }

   public Behavior.BehaviorResult process(DecisionPath var1, IsoGameCharacter var2) {
      var2.setPath((Path)null);
      boolean var3;
      if (var2 == IsoCamera.CamCharacter) {
         var3 = false;
      }

      if (this.toProcess instanceof ObeyOrders && var2.getDangerLevels() > 5.0F) {
         this.timeTillProcessChange = -1;
      }

      Behavior.BehaviorResult var7;
      if (var2.getPersonalNeed() != null && var2.getPersonalNeed().isCritical() && !(this.toProcess instanceof FleeBehaviour)) {
         if (!var2.getPersonalNeed().bInit) {
            var2.getPersonalNeed().initOrder();
            var2.getPersonalNeed().bInit = true;
         }

         var7 = var2.getPersonalNeed().process();
         if (!var2.getPersonalNeed().complete()) {
            return var7;
         }

         var2.getPersonalNeeds().pop();
         var2.setPersonalNeed((Order)null);
      }

      this.flee.update();
      this.attack.update();
      this.idle.update();
      this.orders.update();
      --this.timeTillProcessChange;
      --this.timeTillPathSpeedChange;
      if (var2.getStats().endurance < var2.getStats().endurancewarn && var2.getStats().endurancelast >= var2.getStats().endurancewarn) {
         this.timeTillPathSpeedChange = -1;
      }

      if (var2.getStats().endurance < var2.getStats().endurancedanger && var2.getStats().endurancelast >= var2.getStats().endurancedanger) {
         this.timeTillPathSpeedChange = -1;
      }

      if (this.timeTillPathSpeedChange <= 0) {
         if (var2 == IsoCamera.CamCharacter) {
            var3 = false;
         }

         this.timeTillPathSpeedChange = 100;
         if (this.toProcess != null) {
            var2.setPathSpeed(this.toProcess.getPathSpeed());
         } else {
            var2.setPathSpeed(0.05F);
         }

         if (var2.getStats().endurance < var2.getStats().endurancewarn) {
            var2.setPathSpeed(0.05F);
            this.timeTillPathSpeedChange = 200;
         }

         if (var2.getStats().endurance < var2.getStats().endurancedanger) {
            var2.setPathSpeed(0.04F);
            this.timeTillPathSpeedChange = 200;
         }
      }

      Stats var10000;
      if (var2.getPathSpeed() > 0.06F) {
         var10000 = var2.getStats();
         var10000.endurance -= 0.005F;
      }

      if (var2.getPathSpeed() <= 0.06F) {
         var10000 = var2.getStats();
         var10000.endurance += 5.0E-4F;
      }

      if (var2.getPathSpeed() <= 0.04F) {
         var10000 = var2.getStats();
         var10000.endurance += 0.001F;
      }

      if (this.attack == this.toProcess && ((IsoSurvivor)var2).getVeryCloseEnemyList().size() > 3) {
         this.timeTillProcessChange = -1;
      }

      if (this.orders == this.toProcess && ((IsoSurvivor)var2).getVeryCloseEnemyList().size() > 3) {
         this.timeTillProcessChange = -1;
      }

      if (this.timeTillProcessChange <= 0 || this.toProcess == null) {
         if (var2 == IsoCamera.CamCharacter) {
            var3 = false;
         }

         float var8 = -100000.0F;
         if (var2.getOrder() != null) {
            var8 = var2.getOrder().getPriority(var2) * 5.0F;
         }

         if (!((IsoSurvivor)var2).getVeryCloseEnemyList().isEmpty()) {
            this.timeTillProcessChange = -1;
            var8 = -10000.0F;
         }

         float var4 = 0.0F;
         var4 = this.attack.getPriority(var2);
         float var5 = this.flee.getPriority(var2);
         float var6 = this.idle.getPriority(var2);
         if (var2.getThreatLevel() > 0 && !var2.getLocalRelevantEnemyList().isEmpty() && var2.getCurrentSquare().getRoom() != null && var4 > 0.0F) {
            var4 += 1000000.0F;
         }

         if (var2.getThreatLevel() < 10 && var2.getVeryCloseEnemyList().size() > 0 && var4 > 0.0F) {
            var4 += 1000000.0F;
         }

         if (var6 > var4 && var6 > var8 && var6 > var5) {
            if (this.toProcess != this.idle) {
               this.idle.onSwitch();
            }

            this.toProcess = this.idle;
            this.timeTillProcessChange = 90;
         } else {
            this.idle.reset();
         }

         if (var4 > var5 && var4 > var6 && var4 > var8) {
            if (this.toProcess != this.attack) {
               this.attack.onSwitch();
            }

            this.toProcess = this.attack;
            this.timeTillProcessChange = 100;
         }

         if (var5 > var4 && var5 > var6 && var5 > var8) {
            if (this.toProcess != this.flee) {
               this.flee.onSwitch();
            }

            this.toProcess = this.flee;
            this.timeTillProcessChange = 620;
         }

         if (var8 > var4 && var8 > var6 && var8 > var5) {
            if (this.toProcess != this.orders) {
               this.orders.onSwitch();
            }

            this.toProcess = this.orders;
            this.timeTillProcessChange = 220;
         }
      }

      if (this.toProcess != null) {
         var7 = this.toProcess.process((DecisionPath)null, var2);
         this.toProcess.last = var7;
         return var7;
      } else {
         return Behavior.BehaviorResult.Succeeded;
      }
   }

   public void reset() {
   }

   public boolean valid() {
      return true;
   }

   public int renderDebug(int var1) {
      boolean var2 = true;
      var1 += 20;
      if (this.toProcess != null) {
      }

      return var1;
   }
}
