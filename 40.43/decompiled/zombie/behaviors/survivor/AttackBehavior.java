package zombie.behaviors.survivor;

import zombie.behaviors.Behavior;
import zombie.behaviors.DecisionPath;
import zombie.behaviors.general.PathFindBehavior;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemType;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoUtils;
import zombie.iso.LosUtil;
import zombie.iso.Vector2;
import zombie.ui.TextManager;
import zombie.ui.UIFont;

public class AttackBehavior extends Behavior {
   public boolean HasRangeRequirement = false;
   public IsoGameCharacter RangeTest = null;
   public int TestRangeMax = 10000;
   public int thinkTime = 10;
   public int thinkTimeMax = 3;
   public boolean stayInside = false;
   PathFindBehavior pathFind = new PathFindBehavior("Attack");
   IsoGameCharacter Target = null;
   InventoryItem weapon = null;
   int timeout = 180;
   IsoGridSquare backuppoint = null;
   boolean backingup = false;
   public boolean bWaitForThem = false;
   int nextbackuptest = 0;
   private int failedTimeout = 60;
   Vector2 a = new Vector2();

   public Behavior.BehaviorResult process(DecisionPath var1, IsoGameCharacter var2) {
      if (this.Target != null && !((IsoSurvivor)var2).getLocalRelevantEnemyList().contains(this.Target)) {
         this.Target = null;
         this.pathFind.reset();
         this.backuppoint = null;
         this.Target = null;
         this.weapon = null;
         this.bWaitForThem = false;
      }

      if (this.backuppoint != null) {
         if (!this.backingup || this.pathFind.sx == 0) {
            this.pathFind.reset();
            this.pathFind.sx = var2.getCurrentSquare().getX();
            this.pathFind.sy = var2.getCurrentSquare().getY();
            this.pathFind.sz = var2.getCurrentSquare().getZ();
            this.pathFind.tx = this.backuppoint.getX();
            this.pathFind.ty = this.backuppoint.getY();
            this.pathFind.tz = this.backuppoint.getZ();
         }

         Behavior.BehaviorResult var3 = this.pathFind.process(var1, var2);
         if (var3 == Behavior.BehaviorResult.Working) {
            this.backingup = true;
            return Behavior.BehaviorResult.Working;
         }

         this.pathFind.reset();
         this.backuppoint = null;
         this.Target = null;
         this.weapon = null;
         if (var3 == Behavior.BehaviorResult.Succeeded) {
            this.bWaitForThem = true;
         }

         this.nextbackuptest = 60;
         this.backingup = false;
      }

      boolean var9 = false;
      if (var2.getLastTargettedBy() != null) {
         if (this.Target != var2.getLastTargettedBy()) {
            var9 = true;
         }

         this.Target = var2.getLastTargettedBy();
         this.pathFind.sx = var2.getCurrentSquare().getX();
         this.pathFind.sy = var2.getCurrentSquare().getY();
         this.pathFind.sz = var2.getCurrentSquare().getZ();
      }

      if (this.Target != null) {
      }

      if (this.Target == null || !(this.Target.getHealth() <= 0.0F) && !(this.Target.getBodyDamage().getHealth() <= 0.0F)) {
         HandWeapon var4;
         if (var2.getPrimaryHandItem() != null && var2.getPrimaryHandItem().getCat() == ItemType.Weapon) {
            var4 = (HandWeapon)var2.getPrimaryHandItem();
            if (!var2.HasItem(var4.getAmmoType()) && var4.getAmmoType() != null) {
               var2.setPrimaryHandItem((InventoryItem)null);
            }
         }

         if (var2.getPrimaryHandItem() == null || var2.getPrimaryHandItem().getCat() != ItemType.Weapon) {
            if (!var2.getInventory().HasType(ItemType.Weapon)) {
               this.timeout = 180;
               return Behavior.BehaviorResult.Succeeded;
            }

            var4 = (HandWeapon)var2.getInventory().getBestWeapon(var2.getDescriptor());
            if (var4 != null && (var2.HasItem(var4.getAmmoType()) || var4.getAmmoType() == null)) {
               var2.setPrimaryHandItem(var4);
               if (var2.getPrimaryHandItem() == var2.getSecondaryHandItem()) {
                  var2.setSecondaryHandItem((InventoryItem)null);
               }
            }
         }

         this.weapon = var2.getPrimaryHandItem();
         --this.thinkTime;
         if (this.weapon != null && this.weapon.getCondition() <= 0) {
            this.weapon = null;
         }

         if (this.Target == null) {
            if (this.HasRangeRequirement) {
               this.Target = var2.getCurrentSquare().FindEnemy(var2, var2.getPersonality().getHuntZombieRange(), var2.getLocalRelevantEnemyList(), this.RangeTest, this.TestRangeMax);
            } else {
               this.Target = var2.getCurrentSquare().FindEnemy(var2, var2.getPersonality().getHuntZombieRange(), var2.getLocalRelevantEnemyList());
            }

            if (this.Target != null && this.Target.getCurrentSquare() != null) {
               var9 = true;
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
         } else if (this.weapon == null) {
            return Behavior.BehaviorResult.Succeeded;
         } else {
            IsoGridSquare var10 = var2.getCurrentSquare();
            IsoGridSquare var5 = this.Target.getCurrentSquare();
            if (this.weapon instanceof HandWeapon && var10 != null && var5 != null) {
               HandWeapon var6 = (HandWeapon)this.weapon;
               float var7 = IsoUtils.DistanceTo(var2.getX(), var2.getY(), this.Target.getX(), this.Target.getY());
               if (var5.getZ() == var10.getZ() && !(var6.getMaxRange(var2) * 0.9F < var7) && LosUtil.lineClear(var10.getCell(), var10.getX(), var10.getY(), var10.getZ(), var5.getX(), var5.getY(), var5.getZ(), false) == LosUtil.TestResults.Clear) {
                  this.a.x = this.Target.getX();
                  this.a.y = this.Target.getY();
                  Vector2 var10000 = this.a;
                  var10000.x -= var2.getX();
                  var10000 = this.a;
                  var10000.y -= var2.getY();
                  if (this.a.getLength() > 0.0F) {
                     this.a.normalize();
                     var2.DirectionFromVector(this.a);
                     var2.getAngle().x = this.a.x;
                     var2.getAngle().y = this.a.y;
                     boolean var11 = ((IsoSurvivor)var2).AttemptAttack(0.2F);
                     if (!var11) {
                        this.Target = null;
                        this.weapon = null;
                        return Behavior.BehaviorResult.Failed;
                     }

                     var2.PlayShootAnim();
                  }

                  this.bWaitForThem = false;
                  this.timeout = 30;
                  return Behavior.BehaviorResult.Succeeded;
               }

               if (var9) {
                  this.pathFind.tx = var5.getX();
                  this.pathFind.ty = var5.getY();
                  this.pathFind.tz = var5.getZ();
               }

               Behavior.BehaviorResult var8 = this.pathFind.process(var1, var2);
               if (var8 == Behavior.BehaviorResult.Failed) {
                  this.Target = null;
                  this.weapon = null;
                  this.thinkTime = this.thinkTimeMax;
                  return Behavior.BehaviorResult.Succeeded;
               }

               if (var8 == Behavior.BehaviorResult.Succeeded) {
                  var7 = IsoUtils.DistanceTo(var2.getX(), var2.getY(), this.Target.getX(), this.Target.getY());
                  if (var7 > ((HandWeapon)this.weapon).getMaxRange(var2)) {
                     this.pathFind.tx = var5.getX();
                     this.pathFind.ty = var5.getY();
                     this.pathFind.tz = var5.getZ();
                  }

                  return Behavior.BehaviorResult.Working;
               }
            }

            return Behavior.BehaviorResult.Working;
         }
      } else {
         this.Target = null;
         this.weapon = null;
         var2.getStats().idleboredom = 1.0F;
         this.timeout = 180;
         return Behavior.BehaviorResult.Succeeded;
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

   float getPriority(IsoGameCharacter var1) {
      float var2 = 0.0F;
      if (!var1.IsArmed()) {
         return -1.0E7F;
      } else if (var1.getLocalRelevantEnemyList().isEmpty()) {
         return -1.0E7F;
      } else {
         if (IsoPlayer.DemoMode) {
            var2 += 1000.0F;
         }

         if (var1.getLocalRelevantEnemyList().size() < 5) {
            var2 += (float)(var1.getLocalRelevantEnemyList().size() * 5);
         }

         if (var1.getLocalRelevantEnemyList().size() > 10) {
            var2 -= (float)(var1.getLocalRelevantEnemyList().size() * 5);
         }

         if (var1.getLocalRelevantEnemyList().size() > 20) {
            var2 -= (float)(var1.getLocalRelevantEnemyList().size() * 10);
         }

         if (var1.getDangerLevels() > 300.0F) {
            var2 -= 10000.0F;
         }

         var2 += (float)(var1.getLocalNeutralList().size() * 10);
         var2 += var1.getDescriptor().getBravery() * 50.0F;
         var2 *= MasterSurvivorBehavior.AttackMultiplier;
         if (var1.getTimeSinceZombieAttack() < 30) {
            var2 += 1000.0F;
            var2 *= 100.0F;
         }

         if (this.Target == null && var2 > 0.0F) {
            var2 /= 100.0F;
         }

         if (var1.getStats().endurance < var1.getStats().endurancedanger) {
            var2 -= -10000.0F;
         }

         return var1.getLocalRelevantEnemyList().isEmpty() ? -1000000.0F : var2;
      }
   }

   public int renderDebug(int var1) {
      byte var2 = 50;
      TextManager.instance.DrawString(UIFont.Small, (double)var2, (double)var1, "AttackBehaviour", 1.0D, 1.0D, 1.0D, 1.0D);
      var1 += 30;
      return var1;
   }
}
