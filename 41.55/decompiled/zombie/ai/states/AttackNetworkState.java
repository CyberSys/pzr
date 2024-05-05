package zombie.ai.states;

import java.util.HashMap;
import zombie.GameTime;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.skills.PerkFactory;
import zombie.core.Rand;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.iso.IsoMovingObject;
import zombie.iso.LosUtil;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.util.StringUtils;

public class AttackNetworkState extends State {
   private static final AttackNetworkState s_instance = new AttackNetworkState();
   private static final String frontStr = "FRONT";
   private static final String backStr = "BEHIND";
   private static final String rightStr = "LEFT";
   private static final String leftStr = "RIGHT";

   public static AttackNetworkState instance() {
      return s_instance;
   }

   public void enter(IsoGameCharacter var1) {
      IsoZombie var2 = (IsoZombie)var1;
      HashMap var3 = var1.getStateMachineParams(this);
      var3.clear();
      var3.put(0, Boolean.FALSE);
      var1.setVariable("AttackOutcome", "start");
      var1.clearVariable("AttackDidDamage");
      var1.clearVariable("ZombieBiteDone");
      var1.setIgnoreMovementForDirection(true);
      var2.setTargetSeenTime(1.0F);
      if (!var2.bCrawling) {
         var2.setVariable("AttackType", "bite");
      }

      if (!var2.attackNetworkEvents.isEmpty()) {
         var2.currentAttackNetworkEvent = (IsoZombie.AttackNetworkEvent)var2.attackNetworkEvents.pop();
      }

   }

   public void execute(IsoGameCharacter var1) {
      IsoZombie var2 = (IsoZombie)var1;
      HashMap var3 = var1.getStateMachineParams(this);
      IsoGameCharacter var4 = (IsoGameCharacter)var2.target;
      if (var4 == null || !"Chainsaw".equals(var4.getVariableString("ZombieHitReaction"))) {
         String var5 = var1.getVariableString("AttackOutcome");
         if ("success".equals(var5) && !var1.getVariableBoolean("bAttack") && (var4 == null || !var4.isGodMod()) && !var1.getVariableBoolean("AttackDidDamage") && var1.getVariableString("ZombieBiteDone") != "true") {
            var1.setVariable("AttackOutcome", "interrupted");
         }

         if (var4 == null || var4.isDead()) {
            var2.setTargetSeenTime(10.0F);
         }

         if (var4 != null && var3.get(0) == Boolean.FALSE && !"started".equals(var5) && !StringUtils.isNullOrEmpty(var1.getVariableString("PlayerHitReaction"))) {
            var3.put(0, Boolean.TRUE);
            var4.testDefense(var2);
         }

         var2.setShootable(true);
         if (var2.target != null && !var2.bCrawling) {
            if (!"fail".equals(var5) && !"interrupted".equals(var5)) {
               var2.faceThisObject(var2.target);
            }

            var2.setOnFloor(false);
         }

         boolean var6 = var2.speedType == 1;
         if (var2.target != null && var6 && ("start".equals(var5) || "success".equals(var5))) {
            IsoGameCharacter var7 = (IsoGameCharacter)var2.target;
            float var8 = var7.getSlowFactor();
            if (var7.getSlowFactor() <= 0.0F) {
               var7.setSlowTimer(30.0F);
            }

            var7.setSlowTimer(var7.getSlowTimer() + GameTime.instance.getMultiplier());
            if (var7.getSlowTimer() > 60.0F) {
               var7.setSlowTimer(60.0F);
            }

            var7.setSlowFactor(var7.getSlowFactor() + 0.03F);
            if (var7.getSlowFactor() >= 0.5F) {
               var7.setSlowFactor(0.5F);
            }

            if (GameServer.bServer && var8 != var7.getSlowFactor()) {
               GameServer.sendSlowFactor(var7);
            }
         }

         if (var2.target != null) {
            var2.target.setTimeSinceZombieAttack(0);
            var2.target.setLastTargettedBy(var2);
         }

         if (!var2.bCrawling) {
            var2.setVariable("AttackType", "bite");
         }

      }
   }

   public void exit(IsoGameCharacter var1) {
      IsoZombie var2 = (IsoZombie)var1;
      if (var2.currentAttackNetworkEvent != null) {
         var2.currentAttackNetworkEvent = null;
      }

      var1.clearVariable("AttackOutcome");
      var1.clearVariable("AttackType");
      var1.clearVariable("PlayerHitReaction");
      var1.setIgnoreMovementForDirection(false);
      var1.setStateMachineLocked(false);
      if (var2.target != null && var2.target.isOnFloor()) {
         var2.setEatBodyTarget(var2.target, true);
         var2.setTarget((IsoMovingObject)null);
      }

      var2.AllowRepathDelay = 0.0F;
   }

   public void animEvent(IsoGameCharacter var1, AnimEvent var2) {
      IsoZombie var3 = (IsoZombie)var1;
      IsoGameCharacter var4;
      if (!var3.networkAI.isLocalControl()) {
         if (var3.currentAttackNetworkEvent != null) {
            if (var2.m_EventName.equalsIgnoreCase("SetAttackOutcome")) {
               switch(var3.currentAttackNetworkEvent.outcome) {
               case 1:
                  var3.setVariable("AttackOutcome", "success");
                  break;
               case 2:
                  var3.setVariable("AttackOutcome", "fail");
                  break;
               case 3:
                  var3.setVariable("AttackOutcome", "interrupted");
                  break;
               default:
                  var3.setVariable("AttackOutcome", "fail");
               }
            }

            if (var2.m_EventName.equalsIgnoreCase("AttackCollisionCheck")) {
               if (var3.target == null) {
                  return;
               }

               var4 = (IsoGameCharacter)var3.target;
               var4.setAttackingZombie(var3);
               var4.setHitReaction(var3.currentAttackNetworkEvent.targetHitReaction);
               this.triggerPlayerReaction(var3.currentAttackNetworkEvent.targetHitReaction, var1);
            }

            if (var2.m_EventName.equalsIgnoreCase("EatBody")) {
               var1.setVariable("EatingStarted", true);
               ((IsoZombie)var1).setEatBodyTarget(((IsoZombie)var1).target, true);
               ((IsoZombie)var1).setTarget((IsoMovingObject)null);
            }
         }

      } else {
         if (var2.m_EventName.equalsIgnoreCase("SetAttackOutcome")) {
            if (var3.getVariableBoolean("bAttack")) {
               var3.setVariable("AttackOutcome", "success");
            } else {
               var3.setVariable("AttackOutcome", "fail");
            }
         }

         if (var2.m_EventName.equalsIgnoreCase("AttackCollisionCheck")) {
            var4 = (IsoGameCharacter)var3.target;
            if (var4 == null) {
               return;
            }

            var4.setHitFromBehind(var3.isBehind(var4));
            String var5 = var4.testDotSide(var3);
            boolean var6 = var5.equals("FRONT");
            if (var6 && !StringUtils.isNullOrEmpty(var4.getVariableString("AttackType"))) {
               return;
            }

            if (var4 != null && "KnifeDeath".equals(var4.getVariableString("ZombieHitReaction"))) {
               int var7 = var4.getPerkLevel(PerkFactory.Perks.SmallBlade) + 1;
               int var8 = Math.max(0, 9 - var7 * 2);
               if (Rand.NextBool(var8)) {
                  return;
               }
            }

            this.triggerPlayerReaction(var1.getVariableString("PlayerHitReaction"), var1);
            if (var3.networkAI.isLocalControl()) {
               GameClient.sendZombieAttackTarget(var3, (IsoPlayer)var4, var3.getVariableString("AttackOutcome"), var4.getHitReaction());
            }
         }

         if (var2.m_EventName.equalsIgnoreCase("EatBody")) {
            var1.setVariable("EatingStarted", true);
            ((IsoZombie)var1).setEatBodyTarget(((IsoZombie)var1).target, true);
            ((IsoZombie)var1).setTarget((IsoMovingObject)null);
         }

      }
   }

   public boolean isAttacking(IsoGameCharacter var1) {
      return true;
   }

   private void triggerPlayerReaction(String var1, IsoGameCharacter var2) {
      IsoZombie var3 = (IsoZombie)var2;
      IsoGameCharacter var4 = (IsoGameCharacter)var3.target;
      if (var4 != null) {
         if (!(var3.DistTo(var4) > 1.0F) || var3.bCrawling) {
            if (!var3.isFakeDead() && !var3.bCrawling || !(var3.DistTo(var4) > 1.3F)) {
               if ((!var4.isDead() || var4.getHitReaction().equals("EndDeath")) && !var4.isOnFloor()) {
                  var4.setHitFromBehind(var3.isBehind(var4));
                  String var5 = var4.testDotSide(var3);
                  boolean var6 = var5.equals("FRONT");
                  boolean var7 = var5.equals("BEHIND");
                  if (var5.equals("RIGHT")) {
                     var1 = var1 + "LEFT";
                  }

                  if (var5.equals("LEFT")) {
                     var1 = var1 + "RIGHT";
                  }

                  if (!((IsoPlayer)var4).bDoShove || !var6) {
                     if (!((IsoPlayer)var4).bDoShove || var6 || var7 || Rand.Next(100) <= 75) {
                        if (!(Math.abs(var3.z - var4.z) >= 0.2F)) {
                           LosUtil.TestResults var8 = LosUtil.lineClear(var3.getCell(), (int)var3.getX(), (int)var3.getY(), (int)var3.getZ(), (int)var4.getX(), (int)var4.getY(), (int)var4.getZ(), false);
                           if (var8 != LosUtil.TestResults.Blocked && var8 != LosUtil.TestResults.ClearThroughClosedDoor) {
                              if (!var4.getSquare().isSomethingTo(var3.getCurrentSquare())) {
                                 var4.setAttackingZombie(var3);
                                 boolean var9 = var4.getBodyDamage().AddRandomDamageFromZombie(var3, var1);
                                 var2.setVariable("AttackDidDamage", var9);
                                 var4.getBodyDamage().Update();
                                 if (var4.isDead()) {
                                    if (var4.isFemale()) {
                                       var3.getEmitter().playVocals("FemaleBeingEatenDeath");
                                    } else {
                                       var3.getEmitter().playVocals("MaleBeingEatenDeath");
                                    }

                                    var4.setHealth(0.0F);
                                    var3.setEatBodyTarget(var4, true);
                                    var3.setTarget((IsoMovingObject)null);
                                 } else if (var4.isAsleep()) {
                                    if (GameServer.bServer) {
                                       var4.sendObjectChange("wakeUp");
                                    } else {
                                       var4.forceAwake();
                                    }
                                 }

                                 var4.reportEvent("washit");
                              }
                           }
                        }
                     }
                  }
               } else {
                  var3.setEatBodyTarget(var4, true);
               }
            }
         }
      }
   }
}
