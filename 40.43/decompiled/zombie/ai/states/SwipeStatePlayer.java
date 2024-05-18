package zombie.ai.states;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Stack;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.WorldSoundManager;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaHookManager;
import zombie.ai.State;
import zombie.characters.Faction;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoLivingCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.characters.Stats;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.characters.Moodles.MoodleType;
import zombie.characters.skills.PerkFactory;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.network.ByteBufferWriter;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.Vector2;
import zombie.iso.areas.NonPvpZone;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoTree;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoZombieGiblets;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.ServerOptions;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehiclePart;
import zombie.vehicles.VehicleWindow;

public class SwipeStatePlayer extends State {
   static SwipeStatePlayer _instance = new SwipeStatePlayer();
   static ArrayList HitList = new ArrayList();
   private static ArrayList HitList2 = new ArrayList();
   private static Vector2 tempVector2_1 = new Vector2();
   private static Vector2 tempVector2_2 = new Vector2();
   private ArrayList dotList = new ArrayList();
   private boolean bHitOnlyTree;
   static IsoPlayer testPlayer;
   private static SwipeStatePlayer.CustomComparator Comparator = new SwipeStatePlayer.CustomComparator();
   static Stack RemoveList = new Stack();

   public static SwipeStatePlayer instance() {
      return _instance;
   }

   public static void WeaponLowerCondition(HandWeapon var0, IsoGameCharacter var1) {
      if (var0.getUses() > 1) {
         var0.Use();
         InventoryItem var2 = InventoryItemFactory.CreateItem(var0.getModule() + "." + var0.getType());
         ((HandWeapon)var2).setCondition(var0.getCondition() - 1);
         var0.getContainer().AddItem(var2);
         var1.setPrimaryHandItem(var2);
      } else {
         var0.setCondition(var0.getCondition() - 1);
      }

   }

   public void enter(IsoGameCharacter var1) {
      testPlayer = (IsoPlayer)var1;
      var1.AttackDelayLast = -10000.0F;
      HandWeapon var2 = var1.getUseHandWeapon();
      if (var1.getSprite().CurrentAnim.name.contains("Attack_") && var1.def.Frame < (float)(var1.sprite.CurrentAnim.Frames.size() - 1) && var1.def.Frame != 0.0F) {
         var1.getStateMachine().RevertToPrevious();
      } else {
         LuaEventManager.triggerEvent("OnWeaponSwing", var1, var2);
         if (LuaHookManager.TriggerHook("WeaponSwing", var1, var2)) {
            var1.getStateMachine().RevertToPrevious();
         }

         var1.StopAllActionQueue();
         if (var1 instanceof IsoPlayer && ((IsoPlayer)var1).isLocalPlayer()) {
            IsoWorld.instance.CurrentCell.setDrag((KahluaTable)null, ((IsoPlayer)var1).PlayerIndex);
         }

         ((IsoLivingCharacter)var1).bDoShove = false;
         ((IsoLivingCharacter)var1).setAimAtFloor(false);
         ((IsoLivingCharacter)var1).targetOnGround = null;
         boolean var3 = false;
         if (var1 instanceof IsoPlayer && (var2 == ((IsoPlayer)var1).bareHands || var1.isForceShove())) {
            ((IsoLivingCharacter)var1).bDoShove = true;
            ((IsoLivingCharacter)var1).setAimAtFloor(false);
            var3 = false;
            if (var1.isForceShove()) {
               var2 = ((IsoLivingCharacter)var1).bareHands;
            }
         }

         float var4 = Float.MAX_VALUE;

         for(int var5 = 0; var5 < var1.getCell().getObjectList().size(); ++var5) {
            IsoMovingObject var6 = (IsoMovingObject)var1.getCell().getObjectList().get(var5);
            if (var6.isShootable() && var6 instanceof IsoZombie && (double)Math.abs(var6.getZ() - var1.getZ()) < 0.5D) {
               float var7 = IsoUtils.DistanceTo(var6.x, var6.y, var1.x, var1.y);
               float var8 = var2.getMaxRange(var1);
               if (((IsoZombie)var6).bCrawling) {
                  var8 = (float)((double)var8 + 0.5D);
               }

               if (var7 < var4 && var7 < var8) {
                  Vector2 var9 = tempVector2_1.set(var1.getX(), var1.getY());
                  Vector2 var10 = tempVector2_2.set(var6.getX(), var6.getY());
                  var10.x -= var9.x;
                  var10.y -= var9.y;
                  Vector2 var11 = var1.getAngle();
                  var10.normalize();
                  var11.normalize();
                  float var12 = var10.dot(var11);
                  if (var12 > ((IsoLivingCharacter)var1).bareHands.getMinAngle() && !var6.isOnFloor()) {
                     var4 = var7;
                  }

                  if (var12 > var2.getMinAngle() && var1.IsAttackRange(var2, var6.getX(), var6.getY(), var6.getZ()) && !var2.isDirectional()) {
                     if (!var6.isOnFloor()) {
                        ((IsoLivingCharacter)var1).setAimAtFloor(false);
                        ((IsoLivingCharacter)var1).targetOnGround = null;
                        var3 = true;
                     } else if (!var3 && var1.IsAttackRange(var2, var6.getX(), var6.getY(), var6.getZ()) && !var2.isDirectional()) {
                        ((IsoLivingCharacter)var1).setAimAtFloor(true);
                        ((IsoLivingCharacter)var1).targetOnGround = (IsoGameCharacter)var6;
                     }
                  }
               }
            }
         }

         boolean var13 = false;
         float var14;
         if (var4 < var2.getMinRange() && var2.CloseKillMove == null) {
            ((IsoLivingCharacter)var1).bDoShove = true;
            ((IsoLivingCharacter)var1).setAimAtFloor(false);
            if (((IsoLivingCharacter)var1).bareHands.getSwingAnim() != null) {
               var1.PlayAnimUnlooped("Attack_" + ((IsoLivingCharacter)var1).bareHands.getSwingAnim());
               var14 = 0.016666668F * (float)var1.getSprite().CurrentAnim.Frames.size() / var1.getAttackDelayMax();
               var1.def.setFrameSpeedPerFrame(var14 * 2.0F);
               ((IsoLivingCharacter)var1).useChargeDelta = 3.0F;
            }
         } else if (var2.getSwingAnim() != null) {
            var1.def.Finished = false;
            if (((IsoLivingCharacter)var1).isAimAtFloor()) {
               if (((IsoLivingCharacter)var1).bDoShove) {
                  var1.PlayAnimUnlooped("Attack_Floor_Stamp");
                  var14 = 0.013888889F * (float)var1.getSprite().CurrentAnim.Frames.size() / var1.getAttackDelayMax();
                  if (var1.isForceShove()) {
                     var14 = 0.17F;
                  }

                  var1.def.setFrameSpeedPerFrame(var14 * 2.0F);
                  var1.setAttackDelayUse(0.35F * (float)(var1.getSprite().CurrentAnim.Frames.size() - 1));
               } else {
                  var1.PlayAnimUnlooped("Attack_Floor_" + var2.getSwingAnim());
                  var14 = 0.016666668F * (float)var1.getSprite().CurrentAnim.Frames.size() / var1.getAttackDelayMax();
                  var1.def.setFrameSpeedPerFrame(var14 * 2.0F);
                  var1.setAttackDelayUse(0.35F * (float)(var1.getSprite().CurrentAnim.Frames.size() - 1));
               }
            } else {
               if (var2.CloseKillMove != null && var4 < var2.getMinRange()) {
                  var1.PlayAnimUnlooped("Attack_" + var2.CloseKillMove);
                  var1.setAttackDelayUse(0.4F * (float)(var1.getSprite().CurrentAnim.Frames.size() - 1));
                  var14 = 0.016666668F * (float)var1.getSprite().CurrentAnim.Frames.size() / var1.getAttackDelayMax();
                  var1.def.setFrameSpeedPerFrame(var14);
                  var13 = true;
               } else {
                  var1.setAttackDelayUse(0.3F * (float)(var1.getSprite().CurrentAnim.Frames.size() - 1));
                  if (!var1.isForceShove()) {
                     var1.PlayAnimUnlooped("Attack_" + var2.getSwingAnim());
                  } else if (var1.isForceShove() && (var4 < 1.4F || var4 == Float.MAX_VALUE && var1.getClickSound() == null)) {
                     var1.PlayAnimUnlooped("Attack_" + var2.getSwingAnim());
                  }

                  var14 = 0.016666668F * (float)var1.getSprite().CurrentAnim.Frames.size() / var1.getAttackDelayMax();
                  if (var1.isForceShove()) {
                     var14 = 0.12F;
                  }

                  var1.def.setFrameSpeedPerFrame(var14 * 2.0F);
               }

               if (var2.isRanged()) {
                  var1.setAttackDelayUse(0.05F * (float)(var1.getSprite().CurrentAnim.Frames.size() - 1));
               }
            }
         }

         var1.sprite.CurrentAnim.FinishUnloopedOnFrame = 0;
         if (!((IsoLivingCharacter)var1).bDoShove) {
            if (var2.isRanged()) {
               var14 = GameTime.getInstance().getNight() * 0.8F;
               IsoWorld.instance.CurrentCell.getLamppostPositions().add(new IsoLightSource((int)var1.x, (int)var1.y, (int)var1.z, 0.8F * var14, 0.8F * var14, 0.6F * var14, 18, 6));
            }

            if (var2.getPhysicsObject() == null) {
               var1.getEmitter().playSound(var2.getSwingSound());
               if (!var13) {
                  WorldSoundManager.instance.addSound(var1, (int)var1.getX(), (int)var1.getY(), (int)var1.getZ(), Math.max(10, var2.getSoundRadius()), var2.getSoundVolume());
               } else {
                  WorldSoundManager.instance.addSound(var1, (int)var1.getX(), (int)var1.getY(), (int)var1.getZ(), Math.max(10, var2.getSoundRadius()) / 4, var2.getSoundVolume() / 4);
               }
            }
         }

         if (var1.getAttackDelayUse() < 1.0F) {
            var1.setAttackDelayUse(1.0F);
         }

         if (GameClient.bClient && var1 == IsoPlayer.instance) {
            GameClient.instance.sendPlayer((IsoPlayer)var1);
         }

      }
   }

   public void execute(IsoGameCharacter var1) {
      var1.StopAllActionQueue();
      HandWeapon var2 = var1.getUseHandWeapon();
      if (((IsoLivingCharacter)var1).bDoShove || var1.isForceShove()) {
         var2 = ((IsoLivingCharacter)var1).bareHands;
      }

      if (var2 == null) {
         var1.PlayAnim("Idle");
         var1.getStateMachine().RevertToPrevious();
      } else {
         RemoveList.clear();
         if (var2 != null && var1.def.Frame >= var1.getAttackDelayUse() && var1.AttackDelayLast < var1.getAttackDelayUse()) {
            this.ConnectSwing(var1, var2);
         }

         if (var1.def.Finished || var1.AttackDelayLast > var1.def.Frame) {
            this.changeWeapon(var2, var1);
            var1.setAttackDelay(-1.0F);
            var1.getStateMachine().RevertToPrevious();
            var1.getStateMachine().Lock = false;
            ((IsoPlayer)var1).NetRemoteState = 0;
            if (var1.sprite.CurrentAnim.name.contains("Shove") || var1.isForceShove()) {
               var1.PlayAnim("Idle");
            }

            var1.setForceShove(false);
            var1.setClickSound((String)null);
         }

         var1.AttackDelayLast = var1.def.Frame;
      }
   }

   public void ConnectSwing(IsoGameCharacter var1, HandWeapon var2) {
      if (GameServer.bServer) {
         DebugLog.log(DebugType.Network, "Player swing connects.");
      }

      int var3 = var2.getMaxHitCount();
      if (var2 == ((IsoPlayer)var1).bareHands && !(((IsoPlayer)var1).getPrimaryHandItem() instanceof HandWeapon)) {
         var3 = 1;
      }

      if (var2 == ((IsoPlayer)var1).bareHands && ((IsoPlayer)var1).targetOnGround != null) {
         var3 = 1;
      }

      LuaEventManager.triggerEvent("OnWeaponSwingHitPoint", var1, var2);
      if (!PerkFactory.newMode) {
         var1.getXp().AddXP(PerkFactory.Perks.Fitness, 1.0F);
      }

      HitList.clear();
      if (var2.getPhysicsObject() != null) {
         var1.Throw(var2);
      }

      boolean var4 = false;
      int var5 = 0;
      boolean var6 = false;
      boolean var7 = false;
      var6 = this.CheckObjectHit(var1, var2);
      int var8;
      Vector2 var12;
      Vector2 var13;
      float var15;
      float var16;
      int var43;
      Vector2 var44;
      if (var5 < var3) {
         IsoMovingObject var9;
         Faction var10;
         Faction var11;
         if (((IsoLivingCharacter)var1).bDoShove) {
            if (!((IsoLivingCharacter)var1).isAimAtFloor()) {
               var4 = true;
            }

            var8 = 0;

            label749:
            while(true) {
               if (var8 >= var1.getCell().getObjectList().size()) {
                  Collections.sort(HitList, Comparator);

                  while(true) {
                     if (HitList.size() <= var3) {
                        break label749;
                     }

                     HitList.remove(HitList.size() - 1);
                  }
               }

               var9 = (IsoMovingObject)var1.getCell().getObjectList().get(var8);
               if (var9 != var1 && (!GameClient.bClient || !(var9 instanceof IsoPlayer) || ((IsoPlayer)var9).accessLevel.equals("") && ServerOptions.instance.PVP.getValue() && (!ServerOptions.instance.SafetySystem.getValue() || !var1.isSafety() || !((IsoGameCharacter)var9).isSafety())) && (GameClient.bClient || !(var9 instanceof IsoPlayer) || IsoPlayer.getCoopPVP()) && (!(var9 instanceof IsoGameCharacter) || !((IsoGameCharacter)var9).godMod) && (!GameClient.bClient || !(var9 instanceof IsoPlayer) || NonPvpZone.getNonPvpZone((int)var9.getX(), (int)var9.getY()) == null) && (!GameClient.bClient || !(var9 instanceof IsoPlayer) || !(var1 instanceof IsoPlayer) || NonPvpZone.getNonPvpZone((int)var1.getX(), (int)var1.getY()) == null)) {
                  label889: {
                     if (GameClient.bClient && var9 instanceof IsoPlayer && var1 instanceof IsoPlayer && !((IsoPlayer)var1).factionPvp) {
                        var10 = Faction.getPlayerFaction((IsoPlayer)var1);
                        var11 = Faction.getPlayerFaction((IsoPlayer)var9);
                        if (var10 != null && var11 != null && var10 == var11) {
                           break label889;
                        }
                     }

                     if ((var9 == ((IsoLivingCharacter)var1).targetOnGround || var9.isShootable() && !var9.isOnFloor() && !((IsoLivingCharacter)var1).isAimAtFloor() || var9.isShootable() && var9.isOnFloor() && ((IsoLivingCharacter)var1).isAimAtFloor()) && (var1.IsAttackRange(var2, var9.getX(), var9.getY(), var9.getZ()) && !var2.isDirectional() || var9 instanceof BaseVehicle && var1.DistToSquared((float)((int)var9.x), (float)((int)var9.y)) <= 16.0F)) {
                        LosUtil.TestResults var42 = LosUtil.lineClear(var1.getCell(), (int)var1.getX(), (int)var1.getY(), (int)var1.getZ(), (int)var9.getX(), (int)var9.getY(), (int)var9.getZ(), false);
                        if (var42 != LosUtil.TestResults.Blocked && var42 != LosUtil.TestResults.ClearThroughClosedDoor && (var9.getCurrentSquare() == null || var1.getCurrentSquare() == null || var9.getCurrentSquare() == var1.getCurrentSquare() || !var9.getCurrentSquare().isWindowBlockedTo(var1.getCurrentSquare()))) {
                           var44 = tempVector2_1.set(var1.getX(), var1.getY());
                           var12 = tempVector2_2.set(var9.getX(), var9.getY());
                           var12.x -= var44.x;
                           var12.y -= var44.y;
                           var13 = var1.getAngle();
                           var1.DirectionFromVector(var13);
                           var12.normalize();
                           float var49 = var12.dot(var13);
                           if (!var2.isRanged() && var1.getDescriptor() != null && !(var9 instanceof IsoZombie) && (var1.getDescriptor().InGroupWith(var9) || var1 instanceof IsoSurvivor && var9 instanceof IsoGameCharacter && !var1.getEnemyList().contains((IsoGameCharacter)var9))) {
                              RemoveList.add(var9);
                           } else {
                              if (var49 > 1.0F) {
                                 var49 = 1.0F;
                              }

                              if (var49 < -1.0F) {
                                 var49 = -1.0F;
                              }

                              if (var49 >= var2.getMinAngle() && var49 <= var2.getMaxAngle() || ((IsoLivingCharacter)var1).targetOnGround == var9) {
                                 HitList.add(var9);
                                 var9.setHitFromAngle(var49);
                                 ++var5;
                              }
                           }
                        }
                     }
                  }
               }

               ++var8;
            }
         } else {
            for(var8 = 0; var8 < var1.getCell().getObjectList().size(); ++var8) {
               var9 = (IsoMovingObject)var1.getCell().getObjectList().get(var8);
               if (var9 != var1 && (!GameClient.bClient || !(var9 instanceof IsoPlayer) || ((IsoPlayer)var9).accessLevel.equals("") && ServerOptions.instance.PVP.getValue() && (!ServerOptions.instance.SafetySystem.getValue() || !var1.isSafety() || !((IsoGameCharacter)var9).isSafety())) && (GameClient.bClient || !(var9 instanceof IsoPlayer) || IsoPlayer.getCoopPVP())) {
                  if (var9 instanceof IsoPlayer) {
                     if (GameClient.bClient && var9 instanceof IsoPlayer && NonPvpZone.getNonPvpZone((int)var9.getX(), (int)var9.getY()) != null || GameClient.bClient && var9 instanceof IsoPlayer && var1 instanceof IsoPlayer && NonPvpZone.getNonPvpZone((int)var1.getX(), (int)var1.getY()) != null) {
                        continue;
                     }

                     if (GameClient.bClient && !var2.isRanged() && var9 instanceof IsoPlayer && var1 instanceof IsoPlayer && !((IsoPlayer)var1).factionPvp) {
                        var10 = Faction.getPlayerFaction((IsoPlayer)var1);
                        var11 = Faction.getPlayerFaction((IsoPlayer)var9);
                        if (var10 != null && var11 != null && var10 == var11) {
                           continue;
                        }
                     }
                  }

                  if ((!(var9 instanceof IsoGameCharacter) || !((IsoGameCharacter)var9).godMod) && (var9 == ((IsoLivingCharacter)var1).targetOnGround || var9.isShootable() && !var9.isOnFloor() && !((IsoLivingCharacter)var1).isAimAtFloor() || var9.isShootable() && var9.isOnFloor() && ((IsoLivingCharacter)var1).isAimAtFloor())) {
                     VehiclePart var39 = null;
                     if (var9 instanceof BaseVehicle) {
                        var39 = ((BaseVehicle)var9).getNearestBodyworkPart(var1);
                     }

                     if (var39 != null || var1.IsAttackRange(var2, var9.getX(), var9.getY(), var9.getZ()) && !var2.isDirectional()) {
                        LosUtil.TestResults var41 = LosUtil.lineClear(var1.getCell(), (int)var1.getX(), (int)var1.getY(), (int)var1.getZ(), (int)var9.getX(), (int)var9.getY(), (int)var9.getZ(), false);
                        if (var41 != LosUtil.TestResults.Blocked && var41 != LosUtil.TestResults.ClearThroughClosedDoor) {
                           var12 = tempVector2_1.set(var1.getX(), var1.getY());
                           var13 = tempVector2_2.set(var9.getX(), var9.getY());
                           var13.x -= var12.x;
                           var13.y -= var12.y;
                           Vector2 var14 = var1.getAngle();
                           var1.DirectionFromVector(var14);
                           var13.normalize();
                           var15 = var13.dot(var14);
                           if (var15 > 1.0F) {
                              var15 = 1.0F;
                           }

                           if (var15 < -1.0F) {
                              var15 = -1.0F;
                           }

                           var16 = var2.getMinAngle();
                           if (var2.isRanged()) {
                              var16 -= var2.getAimingPerkMinAngleModifier() * (float)(var1.getPerkLevel(PerkFactory.Perks.Aiming) / 2);
                           }

                           if (var15 >= var16 && var15 <= var2.getMaxAngle()) {
                              HitList.add(var9);
                              var9.setHitFromAngle(var15);
                              ++var5;
                           }
                        }
                     }
                  }
               }
            }
         }

         Collections.sort(HitList, Comparator);
         HitList2.clear();
         if (var2.isPiercingBullets()) {
            double var35 = 0.0D;

            for(var43 = 0; var43 < HitList.size(); ++var43) {
               IsoMovingObject var45 = (IsoMovingObject)HitList.get(var43);
               double var47 = (double)(var1.getX() - var45.getX());
               double var50 = (double)(-(var1.getY() - var45.getY()));
               double var52 = Math.atan2(var50, var47);
               if (var52 < 0.0D) {
                  var52 = Math.abs(var52);
               } else {
                  var52 = 6.283185307179586D - var52;
               }

               if (var43 == 0) {
                  var35 = Math.toDegrees(var52);
                  HitList2.add(var45);
               } else {
                  double var18 = Math.toDegrees(var52);
                  if (Math.abs(var35 - var18) < 1.0D) {
                     HitList2.add(var45);
                     break;
                  }
               }
            }

            HitList.clear();
            HitList.addAll(HitList2);
         } else {
            while(HitList.size() > var3) {
               HitList.remove(HitList.size() - 1);
            }
         }
      }

      Stats var10000;
      if (var2.isUseEndurance()) {
         float var37 = 0.0F;
         if (var2.isTwoHandWeapon() && (var1.getPrimaryHandItem() != var2 || var1.getSecondaryHandItem() != var2)) {
            var37 = var2.getWeight() / 1.5F / 10.0F;
         }

         float var36 = 0.0F;
         if (var5 <= 0 && !var1.isForceShove()) {
            var36 = (var2.getWeight() * 0.28F * var2.getFatigueMod(var1) * var1.getFatigueMod() * var2.getEnduranceMod() * 0.3F + var37) * 0.04F;
            float var46 = 1.0F;
            if (var1.HasTrait("Asthmatic")) {
               var46 = 1.3F;
            }

            var10000 = var1.getStats();
            var10000.endurance -= var36 * var46;
         }
      }

      if (var1 instanceof IsoPlayer && ((IsoPlayer)((IsoPlayer)var1)).isFakeAttack()) {
         HitList.clear();
         var5 = 0;
         ((IsoPlayer)((IsoPlayer)var1)).setFakeAttack(false);
         ((IsoPlayer)((IsoPlayer)var1)).getFakeAttackTarget().AttackObject(var1);
      }

      var1.setLastHitCount(HitList.size());
      if (!PerkFactory.newMode) {
         if (var1.getStats().endurance > var1.getStats().endurancewarn && !var2.isRanged()) {
            var1.getXp().AddXP(PerkFactory.Perks.Fitness, 1.0F);
         }

         if (!var2.isRanged() && var5 > 1) {
            var1.getXp().AddXP(PerkFactory.Perks.Strength, (float)(var5 / 2));
         }

         if (var5 > 0) {
            var1.getXp().AddXP(var2, var5);
         }
      }

      if (!var2.isMultipleHitConditionAffected()) {
         if (Rand.Next(var2.getConditionLowerChance() + var1.getMaintenanceMod() * 2) == 0) {
            WeaponLowerCondition(var2, var1);
         } else if (!var2.isRanged() && !var2.getName().contains("Bare Hands")) {
            if (var1.haveBladeWeapon()) {
               var1.getXp().AddXP(PerkFactory.Perks.BladeMaintenance, 1.0F);
            } else {
               var1.getXp().AddXP(PerkFactory.Perks.BluntMaintenance, 1.0F);
            }
         }

         var7 = true;
      }

      var8 = 1;
      this.dotList.clear();
      if (HitList.isEmpty() && var1.getClickSound() != null) {
         var1.getEmitter().playSound(var1.getClickSound());
         var1.setRecoilDelay(10.0F);
      }

      for(int var38 = 0; var38 < HitList.size(); ++var38) {
         IsoMovingObject var48 = (IsoMovingObject)HitList.get(var38);
         var44 = tempVector2_1.set(var1.getX(), var1.getY());
         var12 = tempVector2_2.set(var48.getX(), var48.getY());
         var12.x -= var44.x;
         var12.y -= var44.y;
         var13 = tempVector2_1.set(var1.getAngle().getX(), var1.getAngle().getY());
         var13.tangent();
         var12.normalize();
         boolean var51 = true;
         var15 = var13.dot(var12);

         float var17;
         for(int var54 = 0; var54 < this.dotList.size(); ++var54) {
            var17 = (Float)this.dotList.get(var54);
            if ((double)Math.abs(var15 - var17) < 1.0E-4D) {
               var51 = false;
            }
         }

         var16 = var2.getMinDamage();
         var17 = var2.getMaxDamage();
         if (!var51) {
            var16 /= 5.0F;
            var17 /= 5.0F;
         }

         if (var38 == 0) {
            this.dotList.add(var15);
         }

         int var53 = var2.getHitChance();
         var53 = (int)((float)var53 + var2.getAimingPerkHitChanceModifier() * (float)(var1.getPerkLevel(PerkFactory.Perks.Aiming) / 2));
         float var19 = IsoUtils.DistanceTo(var48.getX(), var48.getY(), var1.getX(), var1.getY());
         if (var2.getMinRangeRanged() > 0.0F) {
            if (var19 < var2.getMinRangeRanged()) {
               var53 -= 50;
            }
         } else if ((double)var19 < 1.5D && var2.isRanged()) {
            var53 += 15;
         }

         if (var2.isRanged() && var1.getBeenMovingFor() > (float)(var2.getAimingTime() + var1.getPerkLevel(PerkFactory.Perks.Aiming))) {
            var53 = (int)((float)var53 - (var1.getBeenMovingFor() - (float)(var2.getAimingTime() + var1.getPerkLevel(PerkFactory.Perks.Aiming))));
         }

         if (var1.HasTrait("Marksman")) {
            var53 += 20;
         }

         float var20 = 0.0F;

         for(int var21 = BodyPartType.ToIndex(BodyPartType.Hand_L); var21 <= BodyPartType.ToIndex(BodyPartType.UpperArm_R); ++var21) {
            var20 += ((BodyPart)var1.getBodyDamage().getBodyParts().get(var21)).getPain();
         }

         if (var20 > 0.0F) {
            var53 = (int)((float)var53 - var20 / 10.0F);
         }

         if (var53 <= 10) {
            var53 = 10;
         }

         if (var53 > 100 || !var2.isRanged()) {
            var53 = 100;
         }

         boolean var55 = Rand.Next(100) <= var53;
         if (var48 instanceof IsoZombie && ((IsoZombie)var48).getStateMachine().getCurrent() == StaggerBackDieState.instance() && ((IsoZombie)var48).getReanimPhase() == 1) {
            ((IsoZombie)var48).setReanimateTimer((float)(Rand.Next(60) + 30));
         }

         if (var48 instanceof IsoZombie && ((IsoZombie)var48).getStateMachine().getCurrent() == StaggerBackDieState.instance() && ((IsoZombie)var48).getReanimPhase() == 2 && !((IsoZombie)var48).isFakeDead()) {
            float var22 = 15.0F - var48.def.Frame;
            if (var22 < 2.0F) {
               var22 = 2.0F;
            }

            ((IsoZombie)var48).setReanimPhase(1);
            ((IsoZombie)var48).PlayAnimUnlooped("ZombieDeath");
            var48.def.Frame = var22;
            ((IsoZombie)var48).setReanimateTimer((float)(Rand.Next(60) + 30));
         }

         boolean var56 = false;
         if (!var2.isTwoHandWeapon() || var2.isTwoHandWeapon() && var1.getPrimaryHandItem() == var2 && var1.getSecondaryHandItem() == var2) {
            var56 = true;
         }

         float var24 = var17 - var16;
         float var23;
         if (var24 == 0.0F) {
            var23 = var16 + 0.0F;
         } else {
            var23 = var16 + (float)Rand.Next((int)(var24 * 1000.0F)) / 1000.0F;
         }

         if (!var2.isRanged()) {
            var23 *= var2.getDamageMod(var1) * var1.getHittingMod();
         }

         if (!var56 && !var2.isRanged() && var17 > var16) {
            var23 -= var16;
         }

         if (var20 > 0.0F) {
            var23 /= var20 / 10.0F;
         }

         if (var1.HasTrait("Underweight")) {
            var23 *= 0.8F;
         }

         if (var1.HasTrait("Very Underweight")) {
            var23 *= 0.6F;
         }

         if (var1.HasTrait("Emaciated")) {
            var23 *= 0.4F;
         }

         float var25 = var23 / ((float)var8 / 2.0F);
         if (var1.isAttackWasSuperAttack()) {
            var25 *= 10.0F;
         }

         switch(var1.getMoodles().getMoodleLevel(MoodleType.Endurance)) {
         case 0:
         default:
            break;
         case 1:
            var25 *= 0.5F;
            break;
         case 2:
            var25 *= 0.2F;
            break;
         case 3:
            var25 *= 0.1F;
            break;
         case 4:
            var25 *= 0.05F;
         }

         ++var8;
         if (var2.isUseEndurance() && var2.isShareEndurance()) {
         }

         if (var2.isMultipleHitConditionAffected()) {
            if (Rand.Next(var2.getConditionLowerChance() + var1.getMaintenanceMod() * 2) == 0) {
               WeaponLowerCondition(var2, var1);
            } else if (!var2.getName().contains("Bare Hands")) {
               if (var1.haveBladeWeapon()) {
                  var1.getXp().AddXP(PerkFactory.Perks.BladeMaintenance, 1.0F);
               } else {
                  var1.getXp().AddXP(PerkFactory.Perks.BluntMaintenance, 1.0F);
               }
            }

            var7 = true;
         }

         Vector2 var26 = tempVector2_1.set(var1.getX(), var1.getY());
         Vector2 var27 = tempVector2_2.set(var48.getX(), var48.getY());
         var27.x -= var26.x;
         var27.y -= var26.y;
         float var28 = var27.getLength();
         float var29 = 1.0F;
         if (!var2.isRangeFalloff()) {
            var29 = var28 / var2.getMaxRange(var1);
         }

         if (var29 < 0.3F) {
            var29 = 1.0F;
         }

         if (var2.getZombieHitSound() != null) {
         }

         if (var2.isRanged() && var1.getPerkLevel(PerkFactory.Perks.Aiming) < 6 && var1.getMoodles().getMoodleLevel(MoodleType.Panic) > 2) {
            var25 -= (float)var1.getMoodles().getMoodleLevel(MoodleType.Panic) * 0.2F;
         }

         if (!var2.isRanged() && var1.getMoodles().getMoodleLevel(MoodleType.Panic) > 1) {
            var25 -= (float)var1.getMoodles().getMoodleLevel(MoodleType.Panic) * 0.1F;
         }

         if (var1.getMoodles().getMoodleLevel(MoodleType.Stress) > 1) {
            var25 -= (float)var1.getMoodles().getMoodleLevel(MoodleType.Stress) * 0.1F;
         }

         if (var25 < 0.0F) {
            var25 = 0.1F;
         }

         var1.knockbackAttackMod = 1.0F;
         if (var2.CloseKillMove != null && var28 < var2.getMinRange() && var1.sprite.CurrentAnim.name.contains(var2.CloseKillMove)) {
            var29 *= 1000.0F;
            var1.knockbackAttackMod = 0.0F;
            WorldSoundManager.instance.addSound(var1, (int)var1.x, (int)var1.y, (int)var1.z, 4, 4);
            var48.setCloseKilled(true);
         } else {
            var48.setCloseKilled(false);
            if (var2.getImpactSound() != null && (!((IsoLivingCharacter)var1).bDoShove || var1.sprite.CurrentAnim.name.contains("Attack_Floor_Stamp"))) {
               var1.getEmitter().playSound(var2.getImpactSound());
            }

            WorldSoundManager.instance.addSound(var1, (int)var1.x, (int)var1.y, (int)var1.z, 8, 8);
            if (Rand.Next(3) == 0) {
               WorldSoundManager.instance.addSound(var1, (int)var1.x, (int)var1.y, (int)var1.z, 10, 10);
            } else if (Rand.Next(7) == 0) {
               WorldSoundManager.instance.addSound(var1, (int)var1.x, (int)var1.y, (int)var1.z, 16, 16);
            }
         }

         if (GameClient.bClient) {
            if (var55) {
               ByteBufferWriter var57 = GameClient.connection.startPacket();
               PacketTypes.doPacket((short)26, var57);
               var57.putByte((byte)((IsoPlayer)var1).PlayerIndex);
               if (var48 instanceof IsoZombie) {
                  var57.putInt(1);
                  var57.putShort(((IsoZombie)var48).OnlineID);
               } else if (var48 instanceof IsoPlayer) {
                  var57.putInt(2);
                  var57.putShort((short)((IsoPlayer)var48).OnlineID);
                  var1.setSafetyCooldown(var1.getSafetyCooldown() + (float)ServerOptions.instance.SafetyCooldownTimer.getValue());
               } else if (var48 instanceof BaseVehicle) {
                  var57.putInt(3);
                  var57.putShort(((BaseVehicle)var48).VehicleID);
               }

               var57.putUTF(var2.getFullType());
               var57.putFloat(var25);
               var57.putBoolean(var4);
               var57.putBoolean(var48.isCloseKilled());
               var57.putFloat(var29);
               var57.putFloat(var48.getX());
               var57.putFloat(var48.getY());
               var57.putFloat(var48.getZ());
               var57.putFloat(var1.getX());
               var57.putFloat(var1.getY());
               var57.putFloat(var1.getZ());
               var57.putFloat(var48.getHitForce());
               var57.putFloat(var48.getHitDir().x);
               var57.putFloat(var48.getHitDir().y);
               var57.putFloat(((IsoPlayer)var1).useChargeDelta);
               GameClient.connection.endPacket();
            }
         } else if (var55) {
            if (!(var48 instanceof BaseVehicle) && var48.getSquare() != null && var1.getSquare() != null && !var48.getSquare().isWindowBlockedTo(var1.getSquare()) && !var1.getSquare().isWindowBlockedTo(var48.getSquare())) {
               var48.Hit(var2, var1, var25, var4, var29);
               if (var48 instanceof IsoGameCharacter) {
                  if (((IsoGameCharacter)var48).getHealth() <= 0.0F) {
                     var10000 = var1.getStats();
                     var10000.stress -= 0.02F;
                     if (var1 instanceof IsoSurvivor) {
                        ((IsoSurvivor)var1).Killed((IsoGameCharacter)var48);
                     }
                  } else if (!((IsoLivingCharacter)var1).bDoShove) {
                     splash(var48, var2, var1);
                  }
               }
            } else if (var48 instanceof BaseVehicle) {
               BaseVehicle var30 = (BaseVehicle)var48;
               var30.getNearestBodyworkPart(var1);
               VehiclePart var31 = var30.getNearestBodyworkPart(var1);
               if (var31 != null) {
                  VehicleWindow var32 = var31.getWindow();

                  for(int var33 = 0; var33 < var31.getChildCount(); ++var33) {
                     VehiclePart var34 = var31.getChild(var33);
                     if (var34.getWindow() != null) {
                        var32 = var34.getWindow();
                        break;
                     }
                  }

                  if (var32 != null) {
                     var32.damage((int)var25 * 10);
                  } else {
                     var31.setCondition(var31.getCondition() - (int)var25 * 10);
                  }
               }
            }
         }
      }

      var1.AttackDelayLast = var1.def.Frame;
      if (var5 > 0 && Rand.Next(4) == 0 && !var2.isRanged() && !var2.getName().contains("Bare Hands")) {
         if (var1.haveBladeWeapon()) {
            var1.getXp().AddXP(PerkFactory.Perks.BladeGuard, 1.0F);
         } else {
            var1.getXp().AddXP(PerkFactory.Perks.BluntGuard, 1.0F);
         }
      }

      if (!var7 && var6) {
         boolean var40 = this.bHitOnlyTree && var2.getScriptItem().Categories.contains("Axe");
         var43 = var40 ? 2 : 1;
         if (Rand.Next(var2.getConditionLowerChance() * var43 + var1.getMaintenanceMod() * 2) == 0) {
            WeaponLowerCondition(var2, var1);
         } else if (Rand.Next(2) == 0 && !var2.getName().contains("Bare Hands")) {
            if (var1.haveBladeWeapon()) {
               var1.getXp().AddXP(PerkFactory.Perks.BladeMaintenance, 1.0F);
            } else {
               var1.getXp().AddXP(PerkFactory.Perks.BluntMaintenance, 1.0F);
            }
         }
      }

   }

   private static void splash(IsoMovingObject var0, HandWeapon var1, IsoGameCharacter var2) {
      IsoGameCharacter var3 = (IsoGameCharacter)var0;
      if (SandboxOptions.instance.ClothingDegradation.getValue() > 1) {
         float var4 = 0.01F;
         float var5 = 0.1F;
         if (SandboxOptions.instance.ClothingDegradation.getValue() == 2) {
            var4 = 0.001F;
            var5 = 0.01F;
         }

         if (SandboxOptions.instance.ClothingDegradation.getValue() == 3) {
            var4 = 0.1F;
            var5 = 0.3F;
         }

         Clothing var10000;
         if (var2.getClothingItem_Torso() != null && var2.getClothingItem_Torso() instanceof Clothing) {
            var10000 = (Clothing)var2.getClothingItem_Torso();
            var10000.bloodLevel += Rand.Next(var4, var5);
         }

         if (var2.getClothingItem_Legs() != null && var2.getClothingItem_Legs() instanceof Clothing) {
            var10000 = (Clothing)var2.getClothingItem_Legs();
            var10000.bloodLevel += Rand.Next(var4, var5);
         }
      }

      if (var1 != null && SandboxOptions.instance.BloodLevel.getValue() > 1) {
         int var8 = var1.getSplatNumber();
         if (var8 < 1) {
            var8 = 1;
         }

         if (Core.bLastStand) {
            var8 *= 3;
         }

         switch(SandboxOptions.instance.BloodLevel.getValue()) {
         case 2:
            var8 /= 2;
         case 3:
         default:
            break;
         case 4:
            var8 *= 2;
            break;
         case 5:
            var8 *= 5;
         }

         for(int var9 = 0; var9 < var8; ++var9) {
            var3.splatBlood(3, 0.3F);
         }
      }

      byte var11 = 3;
      byte var10 = 7;
      switch(SandboxOptions.instance.BloodLevel.getValue()) {
      case 1:
         var10 = 0;
         break;
      case 2:
         var10 = 4;
         var11 = 5;
      case 3:
      default:
         break;
      case 4:
         var10 = 10;
         var11 = 2;
         break;
      case 5:
         var10 = 15;
         var11 = 0;
      }

      if (SandboxOptions.instance.BloodLevel.getValue() > 1) {
         var3.splatBloodFloorBig(0.3F);
      }

      float var6 = 0.6F;
      if (var3 instanceof IsoZombie && ((IsoZombie)var3).bCrawling || var3.legsSprite != null && var3.legsSprite.CurrentAnim != null && "ZombieDeath".equals(var3.legsSprite.CurrentAnim.name)) {
         var6 = 0.3F;
      }

      for(int var7 = 0; var7 < var10; ++var7) {
         if (Rand.Next(var11) == 0) {
            new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, var3.getCell(), var3.getX(), var3.getY(), var3.getZ() + var6, var3.getHitDir().x * Rand.Next(1.5F, 5.0F), var3.getHitDir().y * Rand.Next(1.5F, 5.0F));
         }
      }

   }

   private IsoMovingObject PickNPCTarget(IsoSurvivor var1, HandWeapon var2) {
      for(int var3 = 0; var3 < var1.getLocalEnemyList().size(); ++var3) {
         IsoMovingObject var4 = (IsoMovingObject)var1.getLocalEnemyList().get(var3);
         if ((var4.isShootable() && !var4.isOnFloor() && !var1.isAimAtFloor() || var4.isShootable() && var4.isOnFloor() && var1.isAimAtFloor()) && var1.IsAttackRange(var2, var4.getX(), var4.getY(), var4.getZ())) {
            Vector2 var5 = new Vector2(var1.getX(), var1.getY());
            Vector2 var6 = new Vector2(var4.getX(), var4.getY());
            var6.x -= var5.x;
            var6.y -= var5.y;
            Vector2 var7 = var1.getAngle();
            var1.DirectionFromVector(var7);
            var6.normalize();
            float var8 = var6.dot(var7);
            if (var8 > 1.0F) {
               var8 = 1.0F;
            }

            if (var8 < -1.0F) {
               var8 = -1.0F;
            }

            if (var8 >= var2.getMinAngle() && var8 <= var2.getMaxAngle()) {
               return var4;
            }
         }
      }

      return null;
   }

   private boolean CheckObjectHit(IsoGameCharacter var1, HandWeapon var2) {
      boolean var3 = false;
      int var4 = 0;
      int var5 = 0;
      IsoDirections var6 = IsoDirections.fromAngle(var1.getAngle());
      int var7 = 0;
      int var8 = 0;
      if (var6 == IsoDirections.NE || var6 == IsoDirections.N || var6 == IsoDirections.NW) {
         --var8;
      }

      if (var6 == IsoDirections.SE || var6 == IsoDirections.S || var6 == IsoDirections.SW) {
         ++var8;
      }

      if (var6 == IsoDirections.NW || var6 == IsoDirections.W || var6 == IsoDirections.SW) {
         --var7;
      }

      if (var6 == IsoDirections.NE || var6 == IsoDirections.E || var6 == IsoDirections.SE) {
         ++var7;
      }

      boolean var9 = false;
      IsoGridSquare var10 = var1.getCurrentSquare().getCell().getGridSquare(var1.getCurrentSquare().getX() + var7, var1.getCurrentSquare().getY() + var8, var1.getCurrentSquare().getZ());
      int var11;
      IsoObject var12;
      if (var10 != null) {
         for(var11 = 0; var11 < var10.getSpecialObjects().size(); ++var11) {
            var12 = (IsoObject)var10.getSpecialObjects().get(var11);
            if (var12 instanceof IsoBarricade) {
               ((IsoBarricade)var12).WeaponHit(var1, var2);
               var3 = true;
               ++var4;
            }

            if (var12 instanceof IsoWindow && !((IsoWindow)var12).isSmashed()) {
               ((IsoWindow)var12).WeaponHit(var1, var2);
               var3 = true;
               ++var4;
            }

            if (var12 instanceof IsoThumpable) {
               ((IsoThumpable)var12).WeaponHit(var1, var2);
               var3 = true;
               ++var4;
            }

            if (var12.getSpecialObjectIndex() == -1) {
               --var11;
            }
         }

         for(var11 = 0; var11 < var10.getObjects().size(); ++var11) {
            var12 = (IsoObject)var10.getObjects().get(var11);
            if (var12 instanceof IsoTree) {
               ((IsoTree)var12).WeaponHit(var1, var2);
               var3 = true;
               ++var4;
               ++var5;
            }

            if (var12.getObjectIndex() == -1) {
               --var11;
            }
         }
      }

      if (var6 == IsoDirections.NE || var6 == IsoDirections.N || var6 == IsoDirections.NW) {
         for(var11 = 0; var11 < var1.getCurrentSquare().getSpecialObjects().size(); ++var11) {
            var12 = (IsoObject)var1.getCurrentSquare().getSpecialObjects().get(var11);
            if (var12 instanceof IsoDoor && ((IsoDoor)var12).north && !((IsoDoor)var12).open) {
               ((IsoDoor)var12).WeaponHit(var1, var2);
               var3 = true;
               ++var4;
            }

            if (var12 instanceof IsoWindow && ((IsoWindow)var12).north && !((IsoWindow)var12).isSmashed()) {
               ((IsoWindow)var12).WeaponHit(var1, var2);
               var3 = true;
               ++var4;
            }

            if (var12 instanceof IsoThumpable && ((IsoThumpable)var12).north) {
               ((IsoThumpable)var12).WeaponHit(var1, var2);
               var3 = true;
               ++var4;
            }

            if (var12.getSpecialObjectIndex() == -1) {
               --var11;
            }
         }
      }

      IsoObject var13;
      int var14;
      IsoGridSquare var15;
      if (var6 == IsoDirections.SE || var6 == IsoDirections.S || var6 == IsoDirections.SW) {
         var15 = var1.getCell().getGridSquare(var1.getCurrentSquare().getX(), var1.getCurrentSquare().getY() + 1, var1.getCurrentSquare().getZ());
         if (var15 != null) {
            for(var14 = 0; var14 < var15.getSpecialObjects().size(); ++var14) {
               var13 = (IsoObject)var15.getSpecialObjects().get(var14);
               if (var13 instanceof IsoDoor && ((IsoDoor)var13).north) {
                  ((IsoDoor)var13).WeaponHit(var1, var2);
                  var3 = true;
                  ++var4;
               }

               if (var13 instanceof IsoWindow && ((IsoWindow)var13).north && !((IsoWindow)var13).isSmashed()) {
                  ((IsoWindow)var13).WeaponHit(var1, var2);
                  var3 = true;
                  ++var4;
               }

               if (var13 instanceof IsoThumpable && ((IsoThumpable)var13).north) {
                  ((IsoThumpable)var13).WeaponHit(var1, var2);
                  var3 = true;
                  ++var4;
               }

               if (var13.getSpecialObjectIndex() == -1) {
                  --var14;
               }
            }
         }
      }

      if (var6 == IsoDirections.SE || var6 == IsoDirections.E || var6 == IsoDirections.NE) {
         var15 = var1.getCell().getGridSquare(var1.getCurrentSquare().getX() + 1, var1.getCurrentSquare().getY(), var1.getCurrentSquare().getZ());
         if (var15 != null) {
            for(var14 = 0; var14 < var15.getSpecialObjects().size(); ++var14) {
               var13 = (IsoObject)var15.getSpecialObjects().get(var14);
               if (var13 instanceof IsoDoor && !((IsoDoor)var13).north) {
                  ((IsoDoor)var13).WeaponHit(var1, var2);
                  var3 = true;
                  ++var4;
               }

               if (var13 instanceof IsoWindow && !((IsoWindow)var13).north && !((IsoWindow)var13).isSmashed()) {
                  ((IsoWindow)var13).WeaponHit(var1, var2);
                  var3 = true;
                  ++var4;
               }

               if (var13 instanceof IsoThumpable && !((IsoThumpable)var13).north) {
                  ((IsoThumpable)var13).WeaponHit(var1, var2);
                  var3 = true;
                  ++var4;
               }

               if (var13.getSpecialObjectIndex() == -1) {
                  --var14;
               }
            }
         }
      }

      if (var6 == IsoDirections.NW || var6 == IsoDirections.W || var6 == IsoDirections.SW) {
         for(var11 = 0; var11 < var1.getCurrentSquare().getSpecialObjects().size(); ++var11) {
            var12 = (IsoObject)var1.getCurrentSquare().getSpecialObjects().get(var11);
            if (var12 instanceof IsoDoor && !((IsoDoor)var12).north) {
               ((IsoDoor)var12).WeaponHit(var1, var2);
               var3 = true;
               ++var4;
            }

            if (var12 instanceof IsoWindow && !((IsoWindow)var12).north && !((IsoWindow)var12).isSmashed()) {
               ((IsoWindow)var12).WeaponHit(var1, var2);
               var3 = true;
               ++var4;
            }

            if (var12 instanceof IsoThumpable && !((IsoThumpable)var12).north) {
               ((IsoThumpable)var12).WeaponHit(var1, var2);
               var3 = true;
               ++var4;
            }

            if (var12.getSpecialObjectIndex() == -1) {
               --var11;
            }
         }
      }

      this.bHitOnlyTree = var3 && var4 == var5;
      return var3;
   }

   public void changeWeapon(HandWeapon var1, IsoGameCharacter var2) {
      if (var1 != null && var1.isUseSelf()) {
         var2.getInventory().setDrawDirty(true);
         Iterator var3 = var2.getInventory().getItems().iterator();

         while(var3.hasNext()) {
            InventoryItem var4 = (InventoryItem)var3.next();
            if (var4 != var1 && var4 instanceof HandWeapon && var4.getType() == var1.getType() && var4.getCondition() > 0) {
               if (var2.getPrimaryHandItem() == var1 && var2.getSecondaryHandItem() == var1) {
                  var2.setPrimaryHandItem(var4);
                  var2.setSecondaryHandItem(var4);
               } else if (var2.getPrimaryHandItem() == var1) {
                  var2.setPrimaryHandItem(var4);
               } else if (var2.getSecondaryHandItem() == var1) {
                  var2.setSecondaryHandItem(var4);
               }

               return;
            }
         }
      }

      if (var1 == null || var1.getCondition() <= 0 || var1.isUseSelf()) {
         HandWeapon var5 = (HandWeapon)var2.getInventory().getBestWeapon(var2.getDescriptor());
         var2.setPrimaryHandItem((InventoryItem)null);
         if (var2.getSecondaryHandItem() == var1) {
            var2.setSecondaryHandItem((InventoryItem)null);
         }

         if (var5 != null && var5 != var2.getPrimaryHandItem() && var5.getCondition() > 0) {
            var2.setPrimaryHandItem(var5);
            if (var5.isTwoHandWeapon() && var2.getSecondaryHandItem() == null) {
               var2.setSecondaryHandItem(var5);
            }
         }
      }

   }

   public static class CustomComparator implements Comparator {
      public int compare(IsoMovingObject var1, IsoMovingObject var2) {
         float var3 = var1.DistToProper(SwipeStatePlayer.testPlayer);
         float var4 = var2.DistToProper(SwipeStatePlayer.testPlayer);
         if (var3 > var4) {
            return 1;
         } else {
            return var4 > var3 ? -1 : 0;
         }
      }
   }
}
