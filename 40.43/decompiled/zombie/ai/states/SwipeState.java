package zombie.ai.states;

import java.util.Iterator;
import java.util.Stack;
import zombie.GameTime;
import zombie.SoundManager;
import zombie.Lua.LuaEventManager;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.characters.Stats;
import zombie.characters.Moodles.MoodleType;
import zombie.characters.skills.PerkFactory;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.input.Mouse;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoObjectPicker;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.Vector2;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoTree;
import zombie.iso.objects.IsoWindow;

public class SwipeState extends State {
   static SwipeState _instance = new SwipeState();
   Stack HitList = new Stack();
   Stack RemoveList = new Stack();

   public static SwipeState instance() {
      return _instance;
   }

   public void WeaponLowerCondition(HandWeapon var1, IsoGameCharacter var2) {
      if (var1.getUses() > 1) {
         var1.Use();
         InventoryItem var3 = InventoryItemFactory.CreateItem(var1.getModule() + "." + var1.getType());
         ((HandWeapon)var3).setCondition(var1.getCondition() - 1);
         var1.getContainer().AddItem(var3);
         var2.setPrimaryHandItem(var3);
         if (var3.getCondition() <= 0) {
            HandWeapon var4 = (HandWeapon)var2.getInventory().getBestWeapon(var2.getDescriptor());
            if (var4 != null) {
               var2.setPrimaryHandItem(var4);
            }
         }
      } else {
         var1.setCondition(var1.getCondition() - 1);
         if (var1.getCondition() <= 0) {
            HandWeapon var5 = (HandWeapon)var2.getInventory().getBestWeapon(var2.getDescriptor());
            if (var5 != null) {
               var2.setPrimaryHandItem(var5);
            }
         }
      }

   }

   public void enter(IsoGameCharacter var1) {
      HandWeapon var2 = var1.getUseHandWeapon();
      LuaEventManager.triggerEvent("OnWeaponSwing", var1, var2);
      if (var2.isRanged()) {
         float var3 = 1.0F - GameTime.getInstance().Ambient / 0.8F;
         IsoWorld.instance.CurrentCell.getLamppostPositions().add(new IsoLightSource((int)var1.x, (int)var1.y, (int)var1.z, 0.8F * var3, 0.8F * var3, 0.6F * var3, 18, 6));
      }

   }

   public void execute(IsoGameCharacter var1) {
      var1.StopAllActionQueue();
      HandWeapon var2 = var1.getUseHandWeapon();
      if (var2.getCondition() <= 0) {
         var2 = null;
      }

      this.RemoveList.clear();
      if (var2 != null && var1.getAttackDelay() == var1.getAttackDelayUse()) {
         LuaEventManager.triggerEvent("OnWeaponSwingHitPoint", var1, var2);
         if (!PerkFactory.newMode) {
            var1.getXp().AddXP(PerkFactory.Perks.Fitness, 1.0F);
         }

         this.HitList.clear();
         if (var2.isUseEndurance()) {
         }

         if (var2.getPhysicsObject() != null) {
            var1.Throw(var2);
         }

         boolean var3 = false;
         int var4 = 0;
         int var5;
         float var17;
         if (var1 instanceof IsoPlayer && var2.isAimedFirearm()) {
            var3 = true;
            float var12 = (float)Mouse.getX();
            float var13 = (float)Mouse.getY();
            Vector2 var15 = new Vector2(0.0F, 0.0F);
            var17 = (float)Rand.Next((int)(IsoPlayer.instance.AimRadius * 1000.0F)) / 1000.0F;
            if (Core.bDoubleSize) {
               var17 *= 2.0F;
            }

            int var18 = 1 * var1.getHitChancesMod();
            if (IsoPlayer.instance.EffectiveAimDistance < 1.0F) {
               var18 = 20;
            }

            for(int var19 = 0; var19 < var18; ++var19) {
               var15.x = (float)Rand.Next(2000) / 1000.0F - 1.0F;
               var15.y = (float)Rand.Next(2000) / 1000.0F - 1.0F;
               var15.setLength(var17);
               IsoMovingObject var22 = IsoObjectPicker.Instance.PickTarget((int)(var15.x + var12), (int)(var15.y + var13));
               if (var22 != null && (!(IsoPlayer.instance.EffectiveAimDistance > 1.0F) || IsoObjectPicker.Instance.IsHeadShot(var22, (int)(var15.x + var12), (int)(var15.y + var13)))) {
                  var3 = false;
                  var1.getXp().AddXP(PerkFactory.Perks.Aiming, 3.0F);
                  if (var22 != null) {
                     ++var4;
                     this.HitList.add(var22);
                     break;
                  }
               }

               if (var22 != null && var19 == var18 - 1) {
                  ++var4;
                  this.HitList.add(var22);
                  break;
               }
            }
         } else {
            this.CheckObjectHit(var1, var2);
            if (var4 < var2.getMaxHitCount()) {
               for(var5 = 0; var5 < var1.getCell().getObjectList().size(); ++var5) {
                  IsoMovingObject var6 = (IsoMovingObject)var1.getCell().getObjectList().get(var5);
                  if (var6.isShootable() && var1.IsAttackRange(var6.getX(), var6.getY(), var6.getZ())) {
                     float var7 = 1.0F;
                     if ((float)Rand.Next(100) <= var7 * var2.getToHitModifier() * 140.0F || var1.isAttackWasSuperAttack()) {
                        Vector2 var8 = new Vector2(var1.getX(), var1.getY());
                        Vector2 var9 = new Vector2(var6.getX(), var6.getY());
                        var9.x -= var8.x;
                        var9.y -= var8.y;
                        Vector2 var10 = var1.getAngle();
                        var1.DirectionFromVector(var10);
                        var9.normalize();
                        float var11 = var9.dot(var10);
                        if (!var2.isRanged() && var1.getDescriptor() != null && !(var6 instanceof IsoZombie)) {
                           if (!var1.getDescriptor().InGroupWith(var6) && var1 instanceof IsoSurvivor && var6 instanceof IsoGameCharacter && !var1.getEnemyList().contains((IsoGameCharacter)var6)) {
                           }

                           this.RemoveList.add(var6);
                        }

                        if (var11 > 1.0F) {
                           var11 = 1.0F;
                        }

                        if (var11 < -1.0F) {
                           var11 = -1.0F;
                        }

                        if (var11 >= var2.getMinAngle() && var11 <= var2.getMaxAngle()) {
                           this.HitList.add(var6);
                           var6.setHitFromAngle(var11);
                           ++var4;
                        }

                        if (var4 >= var2.getMaxHitCount()) {
                           break;
                        }
                     }
                  }
               }
            }

            if (this.RemoveList.size() != this.HitList.size()) {
               this.HitList.removeAll(this.RemoveList);
               var4 = this.HitList.size();
            }

            if (var1 instanceof IsoPlayer && ((IsoPlayer)((IsoPlayer)var1)).isFakeAttack()) {
               this.HitList.clear();
               var4 = 0;
               ((IsoPlayer)((IsoPlayer)var1)).setFakeAttack(false);
               ((IsoPlayer)((IsoPlayer)var1)).getFakeAttackTarget().AttackObject(var1);
            }
         }

         var1.setLastHitCount(var4);
         if (!PerkFactory.newMode) {
            if (var1.getStats().endurance > var1.getStats().endurancewarn && !var2.isRanged()) {
               var1.getXp().AddXP(PerkFactory.Perks.Fitness, 1.0F);
            }

            if (!var2.isRanged() && var4 > 1) {
               var1.getXp().AddXP(PerkFactory.Perks.Strength, (float)(var4 / 2));
            }

            if (var4 > 0) {
               var1.getXp().AddXP(var2, 1);
            }
         }

         if (var4 > 0 && var2.getImpactSound() != null && var1 instanceof IsoPlayer) {
            SoundManager.instance.PlaySound(var2.getImpactSound(), false, 0.2F);
         }

         if (!var2.isMultipleHitConditionAffected() && Rand.Next(var2.getConditionLowerChance()) == 0) {
            this.WeaponLowerCondition(var2, var1);
         }

         var5 = 1;
         Iterator var14 = this.HitList.iterator();

         while(var14.hasNext()) {
            IsoMovingObject var16 = (IsoMovingObject)var14.next();
            if (LosUtil.lineClear(var1.getCell(), (int)var1.getX(), (int)var1.getY(), (int)var1.getZ(), (int)var16.getX(), (int)var16.getY(), (int)var16.getZ(), false) != LosUtil.TestResults.Blocked) {
               var17 = var2.getMinDamage();
               float var20 = var2.getMaxDamage() - var2.getMinDamage();
               if (var20 == 0.0F) {
                  var17 += 0.0F;
               } else {
                  var17 += (float)Rand.Next((int)(var20 * 1000.0F)) / 1000.0F;
               }

               if (!var2.isRanged()) {
                  var17 *= var2.getDamageMod(var1) * var1.getHittingMod();
               }

               float var21 = var17 / (float)var5;
               if (var1.isAttackWasSuperAttack()) {
                  var21 *= 10.0F;
               }

               var5 += 2;
               if (var2.isUseEndurance() && var2.isShareEndurance()) {
                  switch(var1.getMoodles().getMoodleLevel(MoodleType.Endurance)) {
                  case 0:
                  default:
                     break;
                  case 1:
                     var21 *= 0.7F;
                     break;
                  case 2:
                     var21 *= 0.5F;
                     break;
                  case 3:
                     var21 *= 0.35F;
                     break;
                  case 4:
                     var21 *= 0.2F;
                  }
               }

               if (var2.isMultipleHitConditionAffected() && Rand.Next(var2.getConditionLowerChance()) == 0) {
                  this.WeaponLowerCondition(var2, var1);
               }

               var16.Hit(var2, var1, var21, var3, 1.0F);
               if (var16 instanceof IsoGameCharacter && ((IsoGameCharacter)var16).getHealth() <= 0.0F) {
                  Stats var10000 = var1.getStats();
                  var10000.stress -= 0.02F;
                  if (var1 instanceof IsoSurvivor) {
                     ((IsoSurvivor)var1).Killed((IsoGameCharacter)var16);
                  }
               }
            }
         }

         if (var1 instanceof IsoPlayer && var2.isAimedFirearm()) {
            ((IsoPlayer)var1).AimRadius += ((IsoPlayer)var1).getRadiusKickback(var2);
         }
      }

      if (var1.getAttackDelay() <= -5.0F || var1.def.Frame >= (float)(var1.sprite.CurrentAnim.Frames.size() - 1)) {
         var1.setAttackDelay(-1.0F);
         var1.getStateMachine().RevertToPrevious();
         var1.getStateMachine().Lock = false;
         var1.PlayAnim("Idle");
      }

   }

   private void CheckObjectHit(IsoGameCharacter var1, HandWeapon var2) {
      IsoDirections var3 = IsoDirections.fromAngle(var1.getAngle());
      int var4 = 0;
      int var5 = 0;
      if (var3 == IsoDirections.NE || var3 == IsoDirections.N || var3 == IsoDirections.NW) {
         --var5;
      }

      if (var3 == IsoDirections.SE || var3 == IsoDirections.S || var3 == IsoDirections.SW) {
         ++var5;
      }

      if (var3 == IsoDirections.NW || var3 == IsoDirections.W || var3 == IsoDirections.SW) {
         --var4;
      }

      if (var3 == IsoDirections.NE || var3 == IsoDirections.E || var3 == IsoDirections.SE) {
         ++var4;
      }

      boolean var6 = false;
      IsoGridSquare var7 = var1.getCurrentSquare().getCell().getGridSquare(var1.getCurrentSquare().getX() + var4, var1.getCurrentSquare().getY() + var5, var1.getCurrentSquare().getZ());
      int var8;
      IsoObject var9;
      if (var7 != null) {
         for(var8 = 0; var8 < var7.getSpecialObjects().size(); ++var8) {
            var9 = (IsoObject)var7.getSpecialObjects().get(var8);
            if (var9 instanceof IsoBarricade) {
               ((IsoBarricade)var9).WeaponHit(var1, var2);
            }

            if (var9 instanceof IsoWindow) {
               ((IsoWindow)var9).WeaponHit(var1, var2);
            }

            if (var9 instanceof IsoThumpable) {
               ((IsoThumpable)var9).WeaponHit(var1, var2);
            }
         }

         for(var8 = 0; var8 < var7.getObjects().size(); ++var8) {
            var9 = (IsoObject)var7.getObjects().get(var8);
            if (var9 instanceof IsoTree) {
               ((IsoTree)var9).WeaponHit(var1, var2);
            }
         }
      }

      if (var3 == IsoDirections.NE || var3 == IsoDirections.N || var3 == IsoDirections.NW) {
         for(var8 = 0; var8 < var1.getCurrentSquare().getSpecialObjects().size(); ++var8) {
            var9 = (IsoObject)var1.getCurrentSquare().getSpecialObjects().get(var8);
            if (var9 instanceof IsoDoor && ((IsoDoor)var9).north && !((IsoDoor)var9).open) {
               ((IsoDoor)var9).WeaponHit(var1, var2);
            }

            if (var9 instanceof IsoThumpable && ((IsoThumpable)var9).north) {
               ((IsoThumpable)var9).WeaponHit(var1, var2);
            }

            if (var9 instanceof IsoWindow && ((IsoWindow)var9).north) {
               ((IsoWindow)var9).WeaponHit(var1, var2);
            }

            if (var9 instanceof IsoThumpable && ((IsoThumpable)var9).north) {
               ((IsoThumpable)var9).WeaponHit(var1, var2);
            }
         }
      }

      IsoObject var10;
      IsoGridSquare var11;
      int var12;
      if (var3 == IsoDirections.SE || var3 == IsoDirections.S || var3 == IsoDirections.SW) {
         var11 = var1.getCell().getGridSquare(var1.getCurrentSquare().getX(), var1.getCurrentSquare().getY() + 1, var1.getCurrentSquare().getZ());
         if (var11 != null) {
            for(var12 = 0; var12 < var11.getSpecialObjects().size(); ++var12) {
               var10 = (IsoObject)var11.getSpecialObjects().get(var12);
               if (var10 instanceof IsoDoor && ((IsoDoor)var10).north) {
                  ((IsoDoor)var10).WeaponHit(var1, var2);
               }

               if (var10 instanceof IsoWindow && ((IsoWindow)var10).north) {
                  ((IsoWindow)var10).WeaponHit(var1, var2);
               }

               if (var10 instanceof IsoThumpable && ((IsoThumpable)var10).north) {
                  ((IsoThumpable)var10).WeaponHit(var1, var2);
               }
            }
         }
      }

      if (var3 == IsoDirections.SE || var3 == IsoDirections.E || var3 == IsoDirections.NE) {
         var11 = var1.getCell().getGridSquare(var1.getCurrentSquare().getX() + 1, var1.getCurrentSquare().getY(), var1.getCurrentSquare().getZ());
         if (var11 != null) {
            for(var12 = 0; var12 < var11.getSpecialObjects().size(); ++var12) {
               var10 = (IsoObject)var11.getSpecialObjects().get(var12);
               if (var10 instanceof IsoDoor && !((IsoDoor)var10).north) {
                  ((IsoDoor)var10).WeaponHit(var1, var2);
               }

               if (var10 instanceof IsoWindow && !((IsoWindow)var10).north) {
                  ((IsoWindow)var10).WeaponHit(var1, var2);
               }

               if (var10 instanceof IsoThumpable && !((IsoThumpable)var10).north) {
                  ((IsoThumpable)var10).WeaponHit(var1, var2);
               }
            }
         }
      }

      if (var3 == IsoDirections.NW || var3 == IsoDirections.W || var3 == IsoDirections.SW) {
         for(var8 = 0; var8 < var1.getCurrentSquare().getSpecialObjects().size(); ++var8) {
            var9 = (IsoObject)var1.getCurrentSquare().getSpecialObjects().get(var8);
            if (var9 instanceof IsoDoor && !((IsoDoor)var9).north) {
               ((IsoDoor)var9).WeaponHit(var1, var2);
            }

            if (var9 instanceof IsoWindow && !((IsoWindow)var9).north) {
               ((IsoWindow)var9).WeaponHit(var1, var2);
            }

            if (var9 instanceof IsoThumpable && !((IsoThumpable)var9).north) {
               ((IsoThumpable)var9).WeaponHit(var1, var2);
            }
         }
      }

   }
}
