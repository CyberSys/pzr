package zombie.ai.astar.heuristics;

import zombie.ai.astar.AStarHeuristic;
import zombie.ai.astar.Mover;
import zombie.ai.astar.TileBasedMap;

public class ManhattanHeuristic implements AStarHeuristic {
   private int minimumCost;

   public ManhattanHeuristic(int var1) {
      this.minimumCost = var1;
   }

   public float getCost(TileBasedMap var1, Mover var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      return (float)(this.minimumCost * (Math.abs(var3 - var6) + Math.abs(var4 - var7) + Math.abs(var5 - var8)));
   }
}
