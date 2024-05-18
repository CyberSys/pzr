package zombie.characters;

import zombie.GameTime;
import zombie.WorldSoundManager;
import zombie.Lua.LuaHookManager;
import zombie.ai.states.SwipeStatePlayer;
import zombie.characters.Moodles.MoodleType;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoCell;
import zombie.iso.IsoMovingObject;
import zombie.iso.Vector2;
import zombie.ui.UIManager;

public class IsoLivingCharacter extends IsoGameCharacter {
   public float useChargeDelta = 0.0F;
   public HandWeapon bareHands = (HandWeapon)InventoryItemFactory.CreateItem("BareHands");
   public boolean bDoShove = false;
   private boolean bAimAtFloor = false;
   public boolean bCollidedWithPushable = false;
   public IsoGameCharacter targetOnGround;

   public IsoLivingCharacter(IsoCell var1, float var2, float var3, float var4) {
      super(var1, var2, var3, var4);
   }

   public boolean isAimAtFloor() {
      return this.bAimAtFloor;
   }

   public void setAimAtFloor(boolean var1) {
      this.bAimAtFloor = var1;
   }

   public boolean isCollidedWithPushableThisFrame() {
      return this.bCollidedWithPushable;
   }

   public boolean AttemptAttack(float var1) {
      HandWeapon var2 = null;
      if (this.leftHandItem instanceof HandWeapon) {
         var2 = (HandWeapon)this.leftHandItem;
      } else {
         var2 = this.bareHands;
      }

      return var2 != this.bareHands && this instanceof IsoPlayer && LuaHookManager.TriggerHook("Attack", this, var1) ? false : this.DoAttack(var1);
   }

   public boolean DoAttack(float var1) {
      if (var1 < 0.35F) {
         var1 = 0.35F;
      }

      if (this instanceof IsoPlayer) {
         ((IsoPlayer)((IsoPlayer)this)).FakeAttack = false;
      }

      if (!(this.Health <= 0.0F) && !(this.BodyDamage.getHealth() < 0.0F)) {
         if (this.leftHandItem != null && this.AttackDelay <= 0.0F) {
            InventoryItem var2 = this.leftHandItem;
            if (var2 instanceof HandWeapon) {
               this.useHandWeapon = (HandWeapon)var2;
               if (this.useHandWeapon.getCondition() <= 0) {
                  return false;
               }

               int var3 = this.Moodles.getMoodleLevel(MoodleType.Endurance);
               if (this.useHandWeapon.isCantAttackWithLowestEndurance() && var3 == 4) {
                  return false;
               }

               int var4 = 0;
               if (this instanceof IsoSurvivor && this.useHandWeapon.isRanged() && var4 < this.useHandWeapon.getMaxHitCount()) {
                  for(int var5 = 0; var5 < this.getCell().getObjectList().size(); ++var5) {
                     IsoMovingObject var6 = (IsoMovingObject)this.getCell().getObjectList().get(var5);
                     if (var6 != this && var6.isShootable() && this.IsAttackRange(var6.getX(), var6.getY(), var6.getZ()) && !this.useHandWeapon.isDirectional()) {
                        float var7 = 1.0F;
                        if (var7 > 0.0F) {
                           Vector2 var8 = new Vector2(this.getX(), this.getY());
                           Vector2 var9 = new Vector2(var6.getX(), var6.getY());
                           var9.x -= var8.x;
                           var9.y -= var8.y;
                           boolean var10 = false;
                           if (var9.x == 0.0F && var9.y == 0.0F) {
                              var10 = true;
                           }

                           Vector2 var11 = this.angle;
                           this.DirectionFromVector(var11);
                           var9.normalize();
                           float var12 = var9.dot(var11);
                           if (var10) {
                              var12 = 1.0F;
                           }

                           if (var12 > 1.0F) {
                              var12 = 1.0F;
                           }

                           if (var12 < -1.0F) {
                              var12 = -1.0F;
                           }

                           if (var12 >= this.useHandWeapon.getMinAngle() && var12 <= this.useHandWeapon.getMaxAngle()) {
                              if (this.descriptor != null && !(var6 instanceof IsoZombie)) {
                                 if (this.descriptor.InGroupWith(var6)) {
                                 }

                                 return false;
                              }

                              ++var4;
                           }

                           if (var4 >= this.useHandWeapon.getMaxHitCount()) {
                              break;
                           }
                        }
                     }
                  }
               }

               if (UIManager.getPicked() != null) {
                  this.attackTargetSquare = UIManager.getPicked().square;
                  if (UIManager.getPicked().tile instanceof IsoMovingObject) {
                     this.attackTargetSquare = ((IsoMovingObject)UIManager.getPicked().tile).getCurrentSquare();
                  }
               }

               if (this.useHandWeapon.getAmmoType() != null && !this.inventory.contains(this.useHandWeapon.getAmmoType())) {
                  return false;
               }

               if (this.useHandWeapon.getOtherHandRequire() == null || this.rightHandItem != null && this.rightHandItem.getType().equals(this.useHandWeapon.getOtherHandRequire())) {
                  float var13 = this.useHandWeapon.getSwingTime();
                  if (this.useHandWeapon.isUseEndurance()) {
                     var13 *= 1.0F - this.stats.endurance;
                  }

                  if (var13 < this.useHandWeapon.getMinimumSwingTime()) {
                     var13 = this.useHandWeapon.getMinimumSwingTime();
                  }

                  this.getEmitter().playSound(this.useHandWeapon.getSwingSound(), this);
                  WorldSoundManager.instance.addSound(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), this.useHandWeapon.getSoundRadius(), this.useHandWeapon.getSoundVolume());
                  var13 *= this.useHandWeapon.getSpeedMod(this);
                  var13 *= 1.0F / GameTime.instance.getMultiplier();
                  this.AttackDelayMax = this.AttackDelay = (float)((int)var13);
                  this.AttackDelayUse = (float)((int)(this.AttackDelayMax * this.useHandWeapon.getDoSwingBeforeImpact()));
                  this.AttackDelayUse = this.AttackDelayMax - this.AttackDelayUse;
                  this.AttackWasSuperAttack = this.superAttack;
                  this.stateMachine.changeState(SwipeStatePlayer.instance());
                  if (this.useHandWeapon.getAmmoType() != null) {
                     if (this instanceof IsoPlayer) {
                        IsoPlayer.instance.inventory.RemoveOneOf(this.useHandWeapon.getAmmoType());
                     } else {
                        this.inventory.RemoveOneOf(this.useHandWeapon.getAmmoType());
                     }
                  }

                  if (this.useHandWeapon.isUseSelf() && this.leftHandItem != null) {
                     this.leftHandItem.Use();
                  }

                  if (this.useHandWeapon.isOtherHandUse() && this.rightHandItem != null) {
                     this.rightHandItem.Use();
                  }

                  return true;
               }

               return false;
            }
         }

         return false;
      } else {
         return false;
      }
   }
}
