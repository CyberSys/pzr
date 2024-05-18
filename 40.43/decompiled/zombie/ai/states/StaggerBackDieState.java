package zombie.ai.states;

import zombie.GameTime;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.characters.Stats;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.iso.IsoDirections;
import zombie.iso.Vector2;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.network.GameServer;

public class StaggerBackDieState extends State {
   static StaggerBackDieState _instance = new StaggerBackDieState();
   Vector2 dirThisFrame = new Vector2();
   int AnimDelayRate = 10;
   float idealx = 0.0F;
   float idealy = 0.0F;
   float moveDir = 0.6F;

   public static StaggerBackDieState instance() {
      return _instance;
   }

   public void enter(IsoGameCharacter var1) {
      if (var1 instanceof IsoZombie) {
         ((IsoZombie)var1).NetRemoteState = 8;
         ((IsoZombie)var1).DoNetworkDirty();
      }

      if (GameServer.bServer && var1 instanceof IsoPlayer) {
         GameServer.SendDeath((IsoPlayer)var1);
      }

      this.idealx = 0.0F;
      this.idealy = 0.0F;
      if (var1 instanceof IsoSurvivor) {
         ((IsoSurvivor)var1).getDescriptor().bDead = true;
      }

      if (var1 instanceof IsoZombie) {
         if (!((IsoZombie)var1).isFakeDead()) {
            ++IsoZombie.ZombieDeaths;
            var1.PlayAnim("ZombieStaggerBack");
            var1.def.setFrameSpeedPerFrame(0.3F);
         } else {
            var1.PlayAnimUnlooped("ZombieDeath");
            var1.def.Frame = (float)(var1.sprite.CurrentAnim.Frames.size() - 1);
            var1.setReanimateTimer(0.1F);
         }
      } else {
         var1.PlayAnimUnlooped("ZombieDeath");
         var1.def.setFrameSpeedPerFrame(0.3F);
      }

      boolean var2 = !Core.bLastStand && var1 instanceof IsoZombie && (((IsoZombie)var1).isFakeDead() || var1.getHealth() < 1.0F && Rand.Next(10) == 0);
      var1.StateMachineParams.clear();
      var1.StateMachineParams.put(0, var2);
      var1.setStateEventDelayTimer(!var2 && !var1.isDead() ? this.getMaxStaggerTime(var1) : 0.0F);
      Vector2 var10000 = var1.getHitDir();
      var10000.x *= var1.getHitForce();
      var10000 = var1.getHitDir();
      var10000.y *= var1.getHitForce();
      var10000 = var1.getHitDir();
      var10000.x *= 0.08F;
      var10000 = var1.getHitDir();
      var10000.y *= 0.08F;
      if (var1.getHitDir().getLength() > 0.06F) {
         var1.getHitDir().setLength(0.06F);
      }

      if (var1 instanceof IsoZombie) {
         ((IsoZombie)var1).DoNetworkDirty();
      }

      this.dirThisFrame.x = var1.getHitDir().x;
      this.dirThisFrame.y = var1.getHitDir().y;
      if (this.dirThisFrame.getLength() > 0.0F) {
         this.dirThisFrame.normalize();
         var1.setDir(IsoDirections.reverse(IsoDirections.fromAngle(this.dirThisFrame)));
      }

      if (var1 instanceof IsoZombie) {
         ((IsoZombie)var1).reqMovement.x = var1.angle.x;
         ((IsoZombie)var1).reqMovement.y = var1.angle.y;
      }

      var1.setIgnoreMovementForDirection(true);
      var1.getStateMachine().Lock = true;
      var1.setReanimPhase(0);
      if (var1 instanceof IsoZombie) {
         var1.setReanimateTimer((float)(Rand.Next(60) + 30));
      }

      if (Rand.Next(5) == 0) {
         var1.setReanimateTimer((float)(Rand.Next(60) + 30));
      }

      if (var1 instanceof IsoZombie) {
         var1.setReanimAnimFrame(3);
         var1.setReanimAnimDelay(this.AnimDelayRate);
      }

      if (var1.getHealth() > 0.0F && !(var1 instanceof IsoPlayer)) {
         var1.setReanim(true);
         var1.setDieCount(var1.getDieCount() + 1);
      } else if (var1.getHealth() <= 0.0F) {
         var1.playDeadSound();
      }

      if (var1 instanceof IsoZombie) {
         ((IsoZombie)var1).DoNetworkDirty();
      }

   }

   public void execute(IsoGameCharacter var1) {
      if (var1 instanceof IsoZombie) {
         ((IsoZombie)var1).NetRemoteState = 8;
         ((IsoZombie)var1).DoNetworkDirty();
      }

      float var5;
      if (var1 instanceof IsoZombie && var1.getSprite().CurrentAnim.name.equals("ZombieDeadToCrawl") && var1.getSpriteDef().Frame > 12.0F) {
         if (var1.getSprite().CurrentAnim.name.equals("ZombieDeadToCrawl") && (int)var1.getSpriteDef().Frame >= 16 && (int)var1.getSpriteDef().Frame <= 21 && ((IsoZombie)var1).isFakeDead()) {
            ((IsoZombie)var1).setFakeDead(false);
            if (var1.getHealth() > 0.0F && ((IsoZombie)var1).isTargetInCone(1.5F, 0.9F) && ((IsoZombie)var1).target instanceof IsoGameCharacter) {
               IsoGameCharacter var2 = (IsoGameCharacter)((IsoZombie)var1).target;
               if (var2.getVehicle() == null || var2.getVehicle().couldCrawlerAttackPassenger(var2)) {
                  var2.getBodyDamage().AddRandomDamageFromZombie((IsoZombie)var1);
               }
            }

            var1.getStateMachine().changeState(WalkTowardState.instance());
         }

         if (this.idealx == 0.0F || this.idealy == 0.0F) {
            this.idealx = var1.getX() - (float)((int)var1.getX());
            this.idealy = var1.getY() - (float)((int)var1.getY());
            if (var1.getDir() == IsoDirections.N) {
               this.idealy -= this.moveDir;
            }

            if (var1.getDir() == IsoDirections.NE) {
               this.idealx += this.moveDir;
               this.idealy -= this.moveDir;
            }

            if (var1.getDir() == IsoDirections.NW) {
               this.idealx -= this.moveDir;
               this.idealy -= this.moveDir;
            }

            if (var1.getDir() == IsoDirections.S) {
               this.idealy += this.moveDir;
            }

            if (var1.getDir() == IsoDirections.SE) {
               this.idealx += this.moveDir;
               this.idealy += this.moveDir;
            }

            if (var1.getDir() == IsoDirections.SW) {
               this.idealx -= this.moveDir;
               this.idealy += this.moveDir;
            }

            if (var1.getDir() == IsoDirections.W) {
               this.idealx -= this.moveDir;
            }

            if (var1.getDir() == IsoDirections.E) {
               this.idealx += this.moveDir;
            }

            if (this.idealx > 0.99F) {
               this.idealx = 0.99F;
            }

            if (this.idealy > 0.99F) {
               this.idealy = 0.99F;
            }

            if (this.idealx < 0.0F) {
               this.idealx = 0.0F;
            }

            if (this.idealy < 0.0F) {
               this.idealy = 0.0F;
            }
         }

         var5 = var1.x - (float)((int)var1.x);
         float var3 = var1.y - (float)((int)var1.y);
         float var4;
         if (var5 != this.idealx) {
            var4 = (this.idealx - var5) / 4.0F;
            var5 += var4;
            var1.x = (float)((int)var1.x) + var5;
         }

         if (var3 != this.idealy) {
            var4 = (this.idealy - var3) / 4.0F;
            var3 += var4;
            var1.y = (float)((int)var1.y) + var3;
         }

         var1.nx = var1.x;
         var1.ny = var1.y;
         if (var1 instanceof IsoZombie) {
            ((IsoZombie)var1).DoNetworkDirty();
         }
      }

      if (var1.sprite != null && var1.sprite.CurrentAnim != null) {
         if (!var1.isIgnoreStaggerBack()) {
            if (var1.getReanimPhase() == 0 && var1.getStateEventDelayTimer() > 0.0F) {
               this.dirThisFrame.x = var1.getHitDir().x;
               this.dirThisFrame.y = var1.getHitDir().y;
               var1.setOnFloor(true);
               var5 = var1.getStateEventDelayTimer() / this.getMaxStaggerTime(var1);
               Vector2 var10000 = this.dirThisFrame;
               var10000.x *= var5;
               var10000 = this.dirThisFrame;
               var10000.y *= var5;
               if (!var1.isIgnoreStaggerBack()) {
                  var1.Move(this.dirThisFrame);
               }

               if (var1.getHitBy() != null) {
                  Stats var7 = var1.getHitBy().getStats();
                  var7.stress -= 0.0016F;
               }
            } else {
               this.dirThisFrame.set(0.0F, 0.0F);
            }

            if (this.dirThisFrame.getLength() > 0.0F) {
               this.dirThisFrame.normalize();
               var1.setDir(IsoDirections.reverse(IsoDirections.fromAngle(this.dirThisFrame)));
            }

            if (var1 instanceof IsoZombie) {
               ((IsoZombie)var1).reqMovement.x = var1.angle.x;
               ((IsoZombie)var1).reqMovement.y = var1.angle.y;
            }
         }

         if ((var1.isIgnoreStaggerBack() || var1.isUnderVehicle() || var1.getStateEventDelayTimer() <= 0.0F && !var1.sprite.CurrentAnim.name.equals("ZombieDeath") && !var1.sprite.CurrentAnim.name.equals("ZombieGetUp") && !var1.sprite.CurrentAnim.name.equals("Die") && !var1.sprite.CurrentAnim.name.equals("ZombieDeadToCrawl")) && var1 instanceof IsoZombie && !((IsoZombie)var1).bCrawling && !((IsoZombie)var1).isFakeDead()) {
            var1.PlayAnimUnlooped("ZombieDeath");
            var1.setOnFloor(true);
            if (var1.isReanim() && var1.getReanimPhase() > 0) {
               var1.setReanimPhase(0);
               var1.setReanimateTimer((float)(Rand.Next(60) + 30));
               var1.def.setFrameSpeedPerFrame(0.45F);
            }
         }

         if ((var1.sprite.CurrentAnim.name.equals("ZombieGetUp") || var1.sprite.CurrentAnim.name.equals("ZombieDeath") || var1.sprite.CurrentAnim.name.equals("Die") || var1.sprite.CurrentAnim.name.equals("ZombieDeadToCrawl")) && var1.def.Finished) {
            if (var1.isDead()) {
               var1.setReanim(false);
               if (GameServer.bServer && var1 instanceof IsoZombie) {
                  GameServer.sendDeadZombie((IsoZombie)var1);
               }

               IsoDeadBody var6 = new IsoDeadBody(var1);
               if (var1 instanceof IsoPlayer && var1.shouldBecomeZombieAfterDeath()) {
                  var6.reanimateLater();
               }
            }

            if (var1.getReanimPhase() == 0) {
               var1.setCollidable(false);
               var1.setReanimPhase(1);
               var1.def.Finished = true;
            }

            if (var1.isReanim()) {
               if (var1.getReanimPhase() == 1) {
                  if (var1.getHealth() <= 0.0F) {
                     var1.setHealth(0.5F);
                  }

                  var1.setReanimateTimer(var1.getReanimateTimer() - GameTime.getInstance().getMultiplier() / 1.6F);
                  if (var1.getReanimateTimer() <= 0.0F) {
                     if (var1.StateMachineParams.get(0) != null && var1.StateMachineParams.get(0).equals(true)) {
                        var1.def.Frame = 0.0F;
                        var1.def.Finished = false;
                        this.playSafeDead(var1);
                     } else if (!var1.isUnderVehicle()) {
                        var1.def.Frame = 0.0F;
                        var1.def.Finished = false;
                        var1.setReanimPhase(2);
                        var1.PlayAnimUnlooped("ZombieGetUp");
                        var1.def.setFrameSpeedPerFrame(0.2F);
                     }
                  }
               }

               if (var1.getReanimPhase() == 2) {
                  var1.setVisibleToNPCs(false);
                  var1.setOnFloor(true);
                  if ((int)var1.def.Frame >= var1.sprite.CurrentAnim.Frames.size() - 2) {
                     var1.getStateMachine().Lock = false;
                     var1.setReanimPhase(3);
                     var1.setVisibleToNPCs(true);
                     if (var1 instanceof IsoZombie && !((IsoZombie)var1).bCrawling) {
                        var1.setOnFloor(false);
                     }

                     var1.setCollidable(true);
                  }
               }

               if (var1.getReanimPhase() == 3) {
                  var1.def.setFrameSpeedPerFrame(0.23F);
                  if (var1 instanceof IsoZombie) {
                     IsoSpriteInstance var8 = var1.def;
                     var8.AnimFrameIncrease *= ((IsoZombie)var1).getSpeedMod();
                     if (((IsoZombie)var1).walkVariantUse == null) {
                        ((IsoZombie)var1).walkVariant = "ZombieWalk";
                        ((IsoZombie)var1).DoZombieStats();
                     }

                     var1.PlayAnim(((IsoZombie)var1).walkVariantUse);
                  }

                  var1.sprite.Animate = true;
                  if (var1 instanceof IsoZombie && !((IsoZombie)var1).isFakeDead()) {
                     ((IsoZombie)var1).AllowRepathDelay = 0.0F;
                     var1.setDefaultState();
                  } else {
                     var1.getStateMachine().changeState(WalkTowardState.instance());
                  }

                  var1.setReanim(false);
                  if (var1 instanceof IsoZombie && !((IsoZombie)var1).bCrawling) {
                     var1.setOnFloor(false);
                  }
               }
            } else {
               if (var1 instanceof IsoZombie && var1.getAttackedBy() != null && var1.upKillCount) {
                  var1.getAttackedBy().setZombieKills(var1.getAttackedBy().getZombieKills() + 1);
               } else if (var1 instanceof IsoSurvivor && var1.getAttackedBy() != null && var1.upKillCount) {
                  var1.getAttackedBy().setSurvivorKills(var1.getAttackedBy().getSurvivorKills() + 1);
               }

               if (GameServer.bServer && var1 instanceof IsoZombie && var1.getAttackedBy() instanceof IsoPlayer) {
                  var1.getAttackedBy().sendObjectChange("AddZombieKill");
               }
            }
         }

      }
   }

   private void playSafeDead(IsoGameCharacter var1) {
      var1.setVisibleToNPCs(false);
      var1.setCollidable(false);
      ((IsoZombie)var1).setFakeDead(true);
      ((IsoZombie)var1).setOnFloor(true);
      if (!var1.isUnderVehicle() && ((IsoZombie)var1).target != null && var1.getZ() == ((IsoZombie)var1).target.getZ() && ((IsoZombie)var1).target.DistTo(var1) < 2.5F) {
         var1.setIgnoreMovementForDirection(false);
         ((IsoZombie)var1).DirectionFromVector(((IsoZombie)var1).vectorToTarget);
         var1.setIgnoreMovementForDirection(true);
         ((IsoZombie)var1).bCrawling = true;
         var1.setVisibleToNPCs(true);
         var1.setCollidable(true);
         var1.PlayAnimUnlooped("ZombieDeadToCrawl");
         var1.def.setFrameSpeedPerFrame(0.27F);
         ((IsoZombie)var1).DoZombieStats();
         var1.getStateMachine().Lock = false;
         var1.setReanimPhase(2);
         String var2 = "MaleZombieAttack";
         if (var1.isFemale()) {
            var2 = "FemaleZombieAttack";
         }

         var1.getEmitter().playSound(var2);
         if (((IsoZombie)var1).target instanceof IsoPlayer) {
            IsoPlayer var3 = (IsoPlayer)((IsoZombie)var1).target;
            Stats var10000 = var3.getStats();
            var10000.Panic += var3.getBodyDamage().getPanicIncreaseValue() * 3.0F;
         }
      } else {
         var1.PlayAnimUnlooped("ZombieDeath");
         var1.def.Frame = (float)(var1.sprite.CurrentAnim.Frames.size() - 1);
         var1.def.Finished = true;
         var1.setReanimateTimer(0.1F);
      }

   }

   public void exit(IsoGameCharacter var1) {
      var1.setIgnoreMovementForDirection(false);
      var1.getStateMachine().Lock = false;
   }

   private float getMaxStaggerTime(IsoGameCharacter var1) {
      float var2 = 35.0F * var1.getHitForce() * var1.getStaggerTimeMod();
      if (var2 < 15.0F) {
         return 15.0F;
      } else {
         return var2 > 25.0F ? 25.0F : var2;
      }
   }
}
