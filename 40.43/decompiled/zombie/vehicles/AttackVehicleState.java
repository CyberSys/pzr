package zombie.vehicles;

import fmod.fmod.FMODSoundEmitter;
import org.joml.Vector3f;
import zombie.GameTime;
import zombie.ai.State;
import zombie.ai.states.ZombieStandState;
import zombie.audio.BaseSoundEmitter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.network.GameServer;

public class AttackVehicleState extends State {
   private static final AttackVehicleState _instance = new AttackVehicleState();
   private BaseSoundEmitter emitter;

   public static AttackVehicleState instance() {
      return _instance;
   }

   public void enter(IsoGameCharacter var1) {
      ((IsoZombie)var1).thumpFrame = -1;
      var1.setIgnoreMovementForDirection(true);
   }

   public void execute(IsoGameCharacter var1) {
      IsoZombie var2 = (IsoZombie)var1;
      if (!(var2.target instanceof IsoGameCharacter)) {
         var1.setDefaultState();
      } else {
         IsoGameCharacter var3 = (IsoGameCharacter)var2.target;
         if (var3.isDead()) {
            if (var3.getLeaveBodyTimedown() > 3600.0F) {
               var2.getStateMachine().changeState(ZombieStandState.instance());
               var2.target = null;
            } else {
               var3.setLeaveBodyTimedown(var3.getLeaveBodyTimedown() + GameTime.getInstance().getMultiplier() / 1.6F);
               if (!GameServer.bServer && !Core.SoundDisabled && Rand.Next(Rand.AdjustForFramerate(15)) == 0) {
                  if (this.emitter == null) {
                     this.emitter = new FMODSoundEmitter();
                  }

                  String var13 = var2.isFemale() ? "FemaleZombieEating" : "MaleZombieEating";
                  if (!this.emitter.isPlaying(var13)) {
                     this.emitter.playSound(var13);
                  }
               }
            }

            var2.TimeSinceSeenFlesh = 0.0F;
         } else {
            BaseVehicle var4 = var3.getVehicle();
            if (var4 != null && var4.isCharacterAdjacentTo(var1)) {
               Vector3f var5 = var4.chooseBestAttackPosition(var3, var1);
               if (var5 == null || !(Math.abs(var5.x - var1.x) > 0.1F) && !(Math.abs(var5.y - var1.y) > 0.1F)) {
                  boolean var6 = false;
                  VehicleWindow var7 = null;
                  int var8 = var4.getSeat(var3);
                  String var9 = var4.getPassengerArea(var8);
                  VehiclePart var10 = null;
                  if (var4.isInArea(var9, var1)) {
                     var10 = var4.getPassengerDoor(var8);
                     if (var10 != null && var10.getDoor() != null) {
                        if (var10.getInventoryItem() != null && !var10.getDoor().isOpen()) {
                           var7 = var10.findWindow();
                           if (var7 != null) {
                              if (!var7.isHittable()) {
                                 var7 = null;
                              }

                              var6 = var7 == null;
                           } else {
                              var6 = false;
                           }
                        } else {
                           var6 = true;
                        }
                     }
                  } else {
                     var10 = var4.getNearestBodyworkPart(var1);
                     if (var10 != null) {
                        var7 = var10.findWindow();
                        if (var7 != null && !var7.isHittable()) {
                           var7 = null;
                        }
                     }
                  }

                  var1.setIgnoreMovementForDirection(false);
                  var1.faceThisObject(var3);
                  var1.setIgnoreMovementForDirection(true);
                  boolean var11 = GameServer.bServer && GameServer.bFastForward || !GameServer.bServer && IsoPlayer.allPlayersAsleep();
                  if (var6) {
                     var2.PlayAnim("ZombieBite");
                     var2.def.setFrameSpeedPerFrame(0.2F);
                     if (!var11) {
                        boolean var12 = var2.def.Frame >= 15.0F && var2.def.Frame <= 21.0F;
                        if (!var12) {
                           var2.HurtPlayerTimer = 0;
                           return;
                        }

                        if (var2.HurtPlayerTimer == 1) {
                           return;
                        }
                     }

                     var3.getBodyDamage().AddRandomDamageFromZombie(var2);
                     var3.getBodyDamage().Update();
                     if (var3.isDead()) {
                        if (var3.isFemale()) {
                           var2.getEmitter().playVocals("FemaleBeingEatenDeath");
                        } else {
                           var2.getEmitter().playVocals("MaleBeingEatenDeath");
                        }

                        var3.setHealth(0.0F);
                     } else if (var3.isAsleep()) {
                        if (GameServer.bServer) {
                           var3.sendObjectChange("wakeUp");
                           var3.setAsleep(false);
                        } else {
                           var3.forceAwake();
                        }
                     }

                     var2.HurtPlayerTimer = 1;
                  } else {
                     var1.PlayAnim("ZombieDoor");
                     var1.def.setFrameSpeedPerFrame(0.15F);
                     int var14 = (int)var1.def.getFrame();
                     if (var7 != null) {
                        if (var11 || var2.thumpFrame < 5 && var14 >= 5) {
                           var7.damage(var2.strength);
                           if (!GameServer.bServer) {
                              var1.getEmitter().playSound("ZombieThumpWindow", var4);
                           }

                           var2.thumpFlag = 3;
                        }
                     } else if (var11 || var2.thumpFrame < 5 && var14 >= 5) {
                        if (!GameServer.bServer) {
                           var1.getEmitter().playSound("ZombieThumpVehicle", var4);
                        }

                        var2.thumpFlag = 1;
                     }

                     if (var10 != null && Rand.Next(100) < 5) {
                        var10.setCondition(var10.getCondition() - var2.strength);
                     }

                     var2.thumpFrame = var14;
                     if (var3.isAsleep()) {
                        if (GameServer.bServer) {
                           var3.sendObjectChange("wakeUp");
                           var3.setAsleep(false);
                        } else {
                           var3.forceAwake();
                        }
                     }
                  }

               } else if (!(Math.abs(var4.getCurrentSpeedKmHour()) > 0.1F) || !var4.isCharacterAdjacentTo(var1) && !(var4.DistToSquared(var1) < 16.0F)) {
                  if (var2.AllowRepathDelay <= 0.0F) {
                     var1.pathToCharacter(var3);
                     var2.AllowRepathDelay = 6.25F;
                  }

               }
            } else {
               var1.setDefaultState();
            }
         }
      }
   }

   public void exit(IsoGameCharacter var1) {
      var1.setIgnoreMovementForDirection(false);
   }
}
