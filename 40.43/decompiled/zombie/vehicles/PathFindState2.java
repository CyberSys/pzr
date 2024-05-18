package zombie.vehicles;

import zombie.ai.State;
import zombie.ai.astar.AStarPathFinder;
import zombie.ai.states.WalkTowardState;
import zombie.behaviors.Behavior;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.iso.IsoChunk;
import zombie.iso.IsoWorld;
import zombie.network.GameServer;
import zombie.network.ServerMap;

public class PathFindState2 extends State {
   private static final PathFindState2 _instance = new PathFindState2();

   public static PathFindState2 instance() {
      return _instance;
   }

   public void enter(IsoGameCharacter var1) {
   }

   public void execute(IsoGameCharacter var1) {
      Behavior.BehaviorResult var2 = var1.getPathFindBehavior2().update();
      if (var2 == Behavior.BehaviorResult.Failed) {
         if (var1 instanceof IsoZombie) {
            ((IsoZombie)var1).AllowRepathDelay = 0.0F;
         }

         var1.setPathFindIndex(-1);
         var1.setDefaultState();
      } else if (var2 == Behavior.BehaviorResult.Succeeded) {
         if (var1 instanceof IsoZombie) {
            ((IsoZombie)var1).AllowRepathDelay = 0.0F;
         }

         int var3 = (int)var1.getPathFindBehavior2().getTargetX();
         int var4 = (int)var1.getPathFindBehavior2().getTargetY();
         IsoChunk var5 = GameServer.bServer ? ServerMap.instance.getChunk(var3 / 10, var4 / 10) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(var3, var4, 0);
         if (var5 == null) {
            var1.changeState(WalkTowardState.instance());
         } else {
            var1.setDefaultState();
            var1.setPath2((PolygonalMap2.Path)null);
         }
      }
   }

   public void exit(IsoGameCharacter var1) {
      if (var1.getFinder().progress == AStarPathFinder.PathFindProgress.notyetfound) {
         PolygonalMap2.instance.cancelRequest(var1);
         var1.getFinder().progress = AStarPathFinder.PathFindProgress.notrunning;
      }

      var1.setPath2((PolygonalMap2.Path)null);
   }
}
