package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.iso.IsoDirections;
import zombie.iso.Vector2;

public class StaggerBackState extends State {
   static StaggerBackState _instance = new StaggerBackState();
   Vector2 dirThisFrame = new Vector2();

   public static StaggerBackState instance() {
      return _instance;
   }

   public void enter(IsoGameCharacter var1) {
      if (var1 instanceof IsoZombie) {
         ((IsoZombie)var1).NetRemoteState = 8;
      }

      if (var1 instanceof IsoZombie) {
         if (((IsoZombie)var1).bCrawling) {
            var1.getStateMachine().Lock = false;
            if (var1.getStateMachine().getPrevious() == CrawlingZombieTurnState.instance()) {
               var1.getStateMachine().changeState((State)null);
            } else {
               var1.getStateMachine().changeState(var1.getStateMachine().getPrevious());
            }

            return;
         }

         var1.PlayAnim("ZombieStaggerBack");
      }

      var1.setStateEventDelayTimer(this.getMaxStaggerTime(var1));
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

      this.dirThisFrame.x = var1.getHitDir().x;
      this.dirThisFrame.y = var1.getHitDir().y;
      this.dirThisFrame.normalize();
      var1.setDir(IsoDirections.reverse(IsoDirections.fromAngle(this.dirThisFrame)));
      var1.setIgnoreMovementForDirection(true);
   }

   public void execute(IsoGameCharacter var1) {
      this.dirThisFrame.x = var1.getHitDir().x;
      this.dirThisFrame.y = var1.getHitDir().y;
      float var2 = var1.getStateEventDelayTimer() / this.getMaxStaggerTime(var1);
      Vector2 var10000 = this.dirThisFrame;
      var10000.x *= var2;
      var10000 = this.dirThisFrame;
      var10000.y *= var2;
      var1.Move(this.dirThisFrame);
      if (var1 instanceof IsoZombie) {
         ((IsoZombie)var1).NetRemoteState = 7;
         if (var1.getStateEventDelayTimer() <= 0.0F) {
            if (((IsoZombie)var1).target instanceof IsoGameCharacter && ((IsoGameCharacter)((IsoZombie)var1).target).getVehicle() != null) {
               var1.changeState(WalkTowardState.instance());
               return;
            }

            if (((IsoZombie)var1).target instanceof IsoPlayer && ((IsoPlayer)((IsoZombie)var1).target).GhostMode) {
               var1.setShootable(true);
               var1.setDefaultState();
               return;
            }

            var1.getStateMachine().changeState(LungeState.instance());
            ((IsoZombie)var1).LungeTimer = 90.0F;
            var1.setStateEventDelayTimer(40.0F);
            var1.setShootable(true);
            return;
         }
      } else if (var1.getStateEventDelayTimer() <= 0.0F) {
         var1.setShootable(true);
         var1.getStateMachine().changeState(var1.getDefaultState());
         return;
      }

      if (this.dirThisFrame.getLength() > 0.0F) {
         this.dirThisFrame.normalize();
         this.dirThisFrame.normalize();
         var1.setDir(IsoDirections.reverse(IsoDirections.fromAngle(this.dirThisFrame)));
      }

      if (var1 instanceof IsoZombie) {
         ((IsoZombie)var1).reqMovement.x = var1.angle.x;
         ((IsoZombie)var1).reqMovement.y = var1.angle.y;
      }

   }

   public void exit(IsoGameCharacter var1) {
      var1.setIgnoreMovementForDirection(false);
   }

   private float getMaxStaggerTime(IsoGameCharacter var1) {
      float var2 = 35.0F * var1.getHitForce() * var1.getStaggerTimeMod();
      if (var2 < 30.0F) {
         return 30.0F;
      } else {
         return var2 > 50.0F ? 50.0F : var2;
      }
   }
}
