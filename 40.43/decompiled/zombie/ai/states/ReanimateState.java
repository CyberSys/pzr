package zombie.ai.states;

import zombie.GameTime;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.iso.IsoCamera;

public class ReanimateState extends State {
   static ReanimateState _instance = new ReanimateState();
   int AnimDelayRate = 10;

   public static ReanimateState instance() {
      return _instance;
   }

   public void enter(IsoGameCharacter var1) {
      if (var1 instanceof IsoSurvivor) {
         ((IsoSurvivor)var1).getDescriptor().bDead = true;
      }

      var1.PlayAnim("ZombieDeath");
      var1.def.Frame = 0.0F;
      var1.def.Looped = false;
      var1.setDefaultState(this);
      var1.getStateMachine().Lock = true;
      var1.setReanimPhase(0);
      var1.setReanimateTimer((float)(Rand.Next(250) + 1200));
      var1.getBodyDamage().setOverallBodyHealth(0.0F);
      if (Rand.Next(4) == 0) {
         var1.setReanimateTimer(var1.getReanimateTimer() * 3.0F);
      }

      var1.setCollidable(false);
      if (var1 instanceof IsoPlayer) {
         ((IsoPlayer)var1).removeSaveFile();
      }

      var1.setReanimAnimFrame(3);
      var1.setReanimAnimDelay(this.AnimDelayRate);
   }

   public void execute(IsoGameCharacter var1) {
      if (var1.getReanimPhase() == 0 && (int)var1.def.Frame == var1.sprite.CurrentAnim.Frames.size() - 1) {
         var1.setReanimPhase(1);
         var1.sprite.Animate = false;
         var1.setCollidable(false);
      }

      if (var1.getReanimPhase() == 1) {
         var1.setReanimateTimer(var1.getReanimateTimer() - GameTime.getInstance().getMultiplier() / 1.6F);
         if (var1.getReanimateTimer() <= 0.0F && var1.getLeaveBodyTimedown() > 3600.0F) {
            var1.getCurrentSquare().getCell().getRemoveList().add(var1);
            var1.getCurrentSquare().getMovingObjects().remove(var1);
            IsoZombie var2 = new IsoZombie(var1.getCell(), var1.getDescriptor());
            var2.setCurrent(var1.getCurrentSquare());
            var2.getCurrentSquare().getMovingObjects().add(var2);
            var2.setX(var1.getX());
            var2.setY(var1.getY());
            var2.setZ(var1.getZ());
            var2.setInventory(var1.getInventory());
            var1.getCell().getZombieList().add(var2);
            var2.setDir(var1.getDir());
            var2.setPathSpeed(var2.getPathSpeed() * 1.2F);
            var2.wanderSpeed = var2.getPathSpeed() * 0.5F * var2.getSpeedMod();
            var2.setHealth(var2.getHealth() * 5.0F);
            var2.PlayAnim("ZombieDeath");
            var2.def.Frame = (float)(var2.sprite.CurrentAnim.Frames.size() - 1);
            var2.def.Looped = false;
            if (IsoCamera.CamCharacter == var1) {
               IsoCamera.SetCharacterToFollow(var2);
            }

            var2.def.Finished = false;
            var2.PlayAnimUnlooped("ZombieGetUp");
            var2.def.setFrameSpeedPerFrame(0.2F);
            var2.getStateMachine().setCurrent(this);
            var2.setReanimPhase(2);
            var2.setShootable(true);
            var2.getStateMachine().Lock = true;
         }
      }

      if (var1.getReanimPhase() == 2 && (int)var1.def.Frame >= var1.sprite.CurrentAnim.Frames.size() - 2) {
         var1.getStateMachine().Lock = false;
         var1.setReanimPhase(3);
         var1.setVisibleToNPCs(true);
         var1.setShootable(true);
      }

      if (var1.getReanimPhase() == 3) {
         var1.getStateMachine().Lock = false;
         var1.getStateMachine().setCurrent(ZombieStandState._instance);
      }

   }
}
