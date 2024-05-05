package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.characters.NetworkCharacter;
import zombie.iso.IsoChunk;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.vehicles.PathFindBehavior2;
import zombie.vehicles.PolygonalMap2;

public class WalkTowardNetworkState extends State {
   static WalkTowardNetworkState _instance = new WalkTowardNetworkState();

   public static WalkTowardNetworkState instance() {
      return _instance;
   }

   public void enter(IsoGameCharacter var1) {
   }

   public void execute(IsoGameCharacter var1) {
      IsoZombie var2 = (IsoZombie)var1;
      PathFindBehavior2 var3 = var2.getPathFindBehavior2();
      if (var2.networkAI.moveToTarget != null) {
         var2.vectorToTarget.x = var2.networkAI.moveToTarget.x - var2.x;
         var2.vectorToTarget.y = var2.networkAI.moveToTarget.y - var2.y;
      } else {
         var2.vectorToTarget.x = var2.networkAI.targetX - var2.x;
         var2.vectorToTarget.y = var2.networkAI.targetY - var2.y;
      }

      var3.walkingOnTheSpot.reset(var2.x, var2.y);
      if (var2.z != (float)var2.networkAI.targetZ || var2.networkAI.predictionType != NetworkCharacter.PredictionMoveTypes.Thump && var2.networkAI.predictionType != NetworkCharacter.PredictionMoveTypes.Climb) {
         if (var2.z == (float)var2.networkAI.targetZ && !PolygonalMap2.instance.lineClearCollide(var2.x, var2.y, var2.networkAI.targetX, var2.networkAI.targetY, var2.networkAI.targetZ, (IsoMovingObject)null)) {
            if (var2.networkAI.usePathFind) {
               var3.reset();
               var2.setPath2((PolygonalMap2.Path)null);
               var2.networkAI.usePathFind = false;
            }

            if (var2.networkAI.moveToTarget != null) {
               var3.moveToDir(var2.networkAI.moveToTarget, 1.0F);
               var2.setVariable("bMoving", IsoUtils.DistanceManhatten(var2.networkAI.moveToTarget.x, var2.networkAI.moveToTarget.y, var2.nx, var2.ny) > 0.5F);
            } else {
               var3.moveToPoint(var2.networkAI.targetX, var2.networkAI.targetY, 1.0F);
               var2.setVariable("bMoving", IsoUtils.DistanceManhatten(var2.networkAI.targetX, var2.networkAI.targetY, var2.nx, var2.ny) > 0.5F);
            }
         } else {
            if (!var2.networkAI.usePathFind) {
               var3.pathToLocationF(var2.networkAI.targetX, var2.networkAI.targetY, (float)var2.networkAI.targetZ);
               var3.walkingOnTheSpot.reset(var2.x, var2.y);
               var3.setTargetT(var2.networkAI.targetT);
               var2.networkAI.usePathFind = true;
            }

            PathFindBehavior2.BehaviorResult var4 = var3.update();
            if (var4 == PathFindBehavior2.BehaviorResult.Failed) {
               var2.setPathFindIndex(-1);
               return;
            }

            if (var4 == PathFindBehavior2.BehaviorResult.Succeeded) {
               int var5 = (int)var2.getPathFindBehavior2().getTargetX();
               int var6 = (int)var2.getPathFindBehavior2().getTargetY();
               IsoChunk var7 = GameServer.bServer ? ServerMap.instance.getChunk(var5 / 10, var6 / 10) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(var5, var6, 0);
               if (var7 == null) {
                  var2.setVariable("bMoving", true);
                  return;
               }

               var2.setPath2((PolygonalMap2.Path)null);
               var2.setVariable("bMoving", true);
               return;
            }
         }
      } else {
         if (var2.networkAI.usePathFind) {
            var3.reset();
            var2.setPath2((PolygonalMap2.Path)null);
            var2.networkAI.usePathFind = false;
         }

         if (var2.networkAI.moveToTarget != null) {
            var3.moveToDir(var2.networkAI.moveToTarget, 1.0F);
            var2.setVariable("bMoving", IsoUtils.DistanceManhatten(var2.networkAI.moveToTarget.x, var2.networkAI.moveToTarget.y, var2.nx, var2.ny) > 0.5F);
         } else {
            var3.moveToPoint(var2.networkAI.targetX, var2.networkAI.targetY, 1.0F);
            var2.setVariable("bMoving", IsoUtils.DistanceManhatten(var2.networkAI.targetX, var2.networkAI.targetY, var2.nx, var2.ny) > 0.5F);
         }
      }

      if (!((IsoZombie)var1).bCrawling) {
         var1.setOnFloor(false);
      }

      boolean var8 = var1.isCollidedWithVehicle();
      if (((IsoZombie)var1).target instanceof IsoGameCharacter && ((IsoGameCharacter)((IsoZombie)var1).target).getVehicle() != null && ((IsoGameCharacter)((IsoZombie)var1).target).getVehicle().isCharacterAdjacentTo(var1)) {
         var8 = false;
      }

      if (var1.isCollidedThisFrame() || var8) {
         var2.AllowRepathDelay = 0.0F;
         var2.pathToLocation(var1.getPathTargetX(), var1.getPathTargetY(), var1.getPathTargetZ());
         if (!"true".equals(var2.getVariableString("bPathfind"))) {
            var2.setVariable("bPathfind", true);
            var2.setVariable("bMoving", false);
         }

      }
   }

   public void exit(IsoGameCharacter var1) {
      IsoZombie var2 = (IsoZombie)var1;
      var2.setVariable("bMoving", false);
   }
}
