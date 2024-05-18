package zombie.ai.states;

import fmod.fmod.FMODSoundEmitter;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.ai.State;
import zombie.audio.BaseSoundEmitter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.iso.IsoDirections;
import zombie.iso.LosUtil;
import zombie.network.GameServer;

public class AttackState extends State {
   static AttackState _instance = new AttackState();
   private BaseSoundEmitter emitter;

   public static AttackState instance() {
      return _instance;
   }

   public void enter(IsoGameCharacter var1) {
      var1.setIgnoreMovementForDirection(true);
      if (var1 instanceof IsoZombie) {
         IsoZombie var2 = (IsoZombie)var1;
         if (!var2.bCrawling) {
            var2.PlayAnim("ZombieBite");
            var2.def.setFrameSpeedPerFrame(0.2F);
            var2.def.Frame = 4.0F;
         } else {
            var2.PlayAnim("Zombie_CrawlLunge");
            var2.def.setFrameSpeedPerFrame(Rand.Next(0.12F, 0.17F));
         }
      }

   }

   public void execute(IsoGameCharacter var1) {
      if (var1 instanceof IsoZombie) {
         IsoZombie var2 = (IsoZombie)var1;
         var2.setShootable(true);
         if (var2.target != null) {
            if (var2.target instanceof IsoGameCharacter && ((IsoGameCharacter)var2.target).getVehicle() != null) {
               var2.setDefaultState();
               return;
            }

            if (var2.target instanceof IsoPlayer && ((IsoPlayer)var2.target).GhostMode) {
               var2.setDefaultState();
               return;
            }

            if (!var2.bCrawling) {
               var2.setDir(IsoDirections.fromAngle(var2.vectorToTarget));
               var2.setOnFloor(false);
            } else if (var2.dir != IsoDirections.fromAngle(var2.vectorToTarget)) {
               var1.StateMachineParams.clear();
               var1.StateMachineParams.put(0, IsoDirections.fromAngle(var2.vectorToTarget));
               var1.getStateMachine().Lock = false;
               var1.getStateMachine().changeState(CrawlingZombieTurnState.instance());
            }

            if (Math.abs(var2.z - var2.target.z) >= 0.2F) {
               var2.target = null;
               var2.Wander();
               return;
            }
         }

         float var3 = var2.vectorToTarget.getLength();
         float var4 = var2.bCrawling ? 1.4F : 0.9F;
         if (var3 > var4) {
            var2.getStateMachine().Lock = false;
            var2.Wander();
            var2.Lunge();
            return;
         }

         if (var2.target == null) {
            var2.Wander();
            return;
         }

         IsoGameCharacter var5;
         if (var2.target != null && (var2.def.Frame > 7.0F || ((IsoGameCharacter)var2.target).getSlowFactor() > 0.0F)) {
            var5 = (IsoGameCharacter)var2.target;
            float var6 = var5.getSlowFactor();
            if (var5.getSlowFactor() <= 0.0F) {
               var5.setSlowTimer(30.0F);
            }

            var5.setSlowTimer(var5.getSlowTimer() + GameTime.instance.getMultiplier());
            if (var5.getSlowTimer() > 90.0F) {
               var5.setSlowTimer(60.0F);
            }

            var5.setSlowFactor(var5.getSlowFactor() + 0.03F);
            if (var5.getSlowFactor() >= 0.5F) {
               var5.setSlowFactor(0.5F);
            }

            if (GameServer.bServer && var6 != var5.getSlowFactor()) {
               GameServer.sendSlowFactor(var5);
            }
         }

         var2.target.setTimeSinceZombieAttack(0);
         var2.target.setLastTargettedBy(var2);
         --var2.AttackAnimTime;
         if (!var2.bCrawling) {
            var2.PlayAnim("ZombieBite");
            var2.def.setFrameSpeedPerFrame(0.2F);
         } else {
            var2.PlayAnim("Zombie_CrawlLunge");
            var2.def.setFrameSpeedPerFrame(Rand.Next(0.12F, 0.17F));
         }

         if (SandboxOptions.instance.Lore.Speed.getValue() == 1 || var2.speedType == 1) {
            var2.def.setFrameSpeedPerFrame(0.4F);
         }

         if (var2.inactive) {
            var2.def.setFrameSpeedPerFrame(0.08F);
         }

         var5 = (IsoGameCharacter)var2.target;
         if (var5 != null && var5.isDead()) {
            if (var5.getLeaveBodyTimedown() > 3600.0F) {
               var2.getStateMachine().changeState(ZombieStandState.instance());
               var2.target = null;
            } else {
               var5.setLeaveBodyTimedown(var5.getLeaveBodyTimedown() + GameTime.getInstance().getMultiplier() / 1.6F);
               if (!GameServer.bServer && !Core.SoundDisabled && Rand.Next(Rand.AdjustForFramerate(15)) == 0) {
                  if (this.emitter == null) {
                     this.emitter = new FMODSoundEmitter();
                  }

                  String var9 = var2.isFemale() ? "FemaleZombieEating" : "MaleZombieEating";
                  if (!this.emitter.isPlaying(var9)) {
                     this.emitter.playSound(var9);
                  }
               }
            }

            var2.TimeSinceSeenFlesh = 0.0F;
            return;
         }

         boolean var8 = GameServer.bServer && GameServer.bFastForward || !GameServer.bServer && IsoPlayer.allPlayersAsleep();
         if (!var8) {
            boolean var7 = var2.bCrawling && var2.def.Frame >= 10.0F && var2.def.Frame <= 16.0F || !var2.bCrawling && var2.def.Frame >= 15.0F && var2.def.Frame <= 21.0F;
            if (!var7) {
               var2.HurtPlayerTimer = 0;
               return;
            }

            if (var2.HurtPlayerTimer == 1) {
               return;
            }
         }

         if (Rand.Next(Rand.AdjustForFramerate(3)) == 0 && var2.target != null && Math.abs(var2.z - var2.target.z) < 0.2F) {
            LosUtil.TestResults var10 = LosUtil.lineClear(var2.getCell(), (int)var2.getX(), (int)var2.getY(), (int)var2.getZ(), (int)var2.target.getX(), (int)var2.target.getY(), (int)var2.target.getZ(), false);
            if (var10 != LosUtil.TestResults.Blocked && var10 != LosUtil.TestResults.ClearThroughClosedDoor && !var2.target.getSquare().isSomethingTo(var2.getCurrentSquare())) {
               var5.getBodyDamage().AddRandomDamageFromZombie(var2);
               var5.getBodyDamage().Update();
               if (var5.isDead()) {
                  if (var5.isFemale()) {
                     var2.getEmitter().playVocals("FemaleBeingEatenDeath");
                  } else {
                     var2.getEmitter().playVocals("MaleBeingEatenDeath");
                  }

                  var5.setHealth(0.0F);
               } else if (var5.isAsleep()) {
                  if (GameServer.bServer) {
                     var5.sendObjectChange("wakeUp");
                  } else {
                     var5.forceAwake();
                  }
               }

               var2.HurtPlayerTimer = 1;
            }
         }
      }

   }

   public void exit(IsoGameCharacter var1) {
      var1.setIgnoreMovementForDirection(false);
      var1.getStateMachine().Lock = false;
   }
}
