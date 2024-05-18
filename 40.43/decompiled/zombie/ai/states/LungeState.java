package zombie.ai.states;

import zombie.GameTime;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.iso.IsoDirections;
import zombie.iso.Vector2;
import zombie.network.GameServer;
import zombie.vehicles.BaseVehicle;

public class LungeState extends State {
   static LungeState _instance = new LungeState();
   Vector2 temp = new Vector2();
   int turnTimer = 0;

   public static LungeState instance() {
      return _instance;
   }

   public void execute(IsoGameCharacter var1) {
      if (!((IsoZombie)var1).bCrawling) {
         var1.setOnFloor(false);
      }

      var1.setShootable(true);
      if (var1 instanceof IsoZombie) {
         IsoZombie var2 = (IsoZombie)var1;
         var2.DoNetworkDirty();
         float var3 = 0.0F;
         if (var2.target != null) {
            var3 = var2.getWidth() + var2.target.getWidth();
         } else {
            var3 = var2.getWidth() * 2.0F;
         }

         ((IsoZombie)var1).setIgnoreMovementForDirection(false);
         if (var2.target == null) {
            if (var2.LastTargetSeenX != -1) {
               var2.pathToLocation(var2.LastTargetSeenX, var2.LastTargetSeenY, var2.LastTargetSeenZ);
               if (var2.getCurrentState() == this) {
                  var2.setDefaultState();
               }
            } else {
               var2.AllowRepathDelay = 0.0F;
               var2.setDefaultState();
            }

            return;
         }

         if (var2.vectorToTarget.getLength() - var3 >= 0.8F && var2.LungeTimer <= 0.0F) {
            var2.AllowRepathDelay = 0.0F;
            if (var2.getStateMachine().getPrevious() == WalkTowardState.instance() || var2.getStateMachine().getPrevious() == PathFindState.instance()) {
               if (var2.target != null) {
                  var2.getStateMachine().setPrevious(ZombieStandState.instance());
                  var2.pathToCharacter((IsoGameCharacter)var2.target);
               } else {
                  var2.getStateMachine().RevertToPrevious();
               }

               return;
            }
         }

         if (var2.target instanceof IsoGameCharacter) {
            BaseVehicle var4 = ((IsoGameCharacter)var2.target).getVehicle();
            if (var4 != null && var4.isCharacterAdjacentTo(var2)) {
               var2.AttemptAttack();
               return;
            }
         }

         float var9 = var2.vectorToTarget.getLength();
         float var5 = var3;
         if (var2.bCrawling) {
            var5 = 0.9F;
         }

         if (var9 < var5) {
            var2.AttemptAttack();
            return;
         }

         if (var2.bLunger) {
            var2.walkVariantUse = "ZombieWalk3";
         }

         var2.LungeTimer -= GameTime.getInstance().getMultiplier() / 1.6F;
         if (var2.LungeTimer < 0.0F) {
            var2.LungeTimer = 0.0F;
         }

         this.temp.x = var2.vectorToTarget.x;
         this.temp.y = var2.vectorToTarget.y;
         var2.getZombieLungeSpeed(this.temp);
         var2.Move(this.temp);
         var2.updateFrameSpeed();
         this.temp.normalize();
         boolean var6 = false;
         if (var2.NetRemoteState != 4) {
            var2.NetRemoteState = 4;
            var6 = true;
         }

         if (var2.target != null && var2.target instanceof IsoPlayer && ((IsoPlayer)((IsoPlayer)var2.target)).playerMoveDir.getLength() > 0.0F) {
         }

         if (!var2.bCrawling) {
            var2.DirectionFromVector(this.temp);
            var2.getVectorFromDirection(var2.angle);
         } else {
            ++this.turnTimer;
            if (this.turnTimer > 3) {
               IsoDirections var7 = var1.dir;
               IsoDirections var8 = IsoDirections.fromAngle(this.temp);
               if (var7 != var8 && var1.getStateMachine().getCurrent() != CrawlingZombieTurnState.instance()) {
                  var1.StateMachineParams.clear();
                  var1.StateMachineParams.put(0, var8);
                  var1.getStateMachine().Lock = false;
                  var1.getStateMachine().changeState(CrawlingZombieTurnState.instance());
               }

               this.turnTimer = 0;
            }
         }

         if (GameServer.bServer && var6) {
            GameServer.sendZombie((IsoZombie)var1);
         }
      }

   }
}
