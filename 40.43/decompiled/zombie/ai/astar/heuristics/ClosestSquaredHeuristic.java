package zombie.ai.astar.heuristics;

import zombie.ai.astar.AStarHeuristic;
import zombie.ai.astar.Mover;
import zombie.ai.astar.TileBasedMap;

public class ClosestSquaredHeuristic implements AStarHeuristic {
   public float getCost(TileBasedMap var1, Mover var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      float var9 = (float)(var6 - var3);
      float var10 = (float)(var7 - var4);
      float var11 = (float)(var8 - var5);
      return var9 * var9 + var10 * var10 + var11 * var11;
   }
}
